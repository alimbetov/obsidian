#!/usr/bin/env python3
"""Repository-wide quality audit for the Obsidian learning vault.

Checks:
- Markdown fence balance and Mermaid extraction;
- Obsidian wikilink resolution across all repository file types;
- Canvas JSON, node/edge references, file nodes, disconnected and overlapping nodes;
- certification batch counts, IDs and mandatory card sections;
- lightweight pedagogical metrics for concepts, production cases and labs.

Mermaid syntax is validated by mermaid-cli in the companion GitHub Actions workflow.
"""

from __future__ import annotations

import argparse
import json
import re
import shutil
from collections import Counter, defaultdict
from pathlib import Path
from typing import Dict, Iterable, List, Optional, Sequence, Set, Tuple

EXCLUDED_DIRS = {".git", ".obsidian", ".audit", "node_modules", "target"}
FENCE_RE = re.compile(r"^\s*(`{3,}|~{3,})([^`]*)$")
WIKILINK_RE = re.compile(r"\[\[([^\]]+)\]\]")
CARD_HEADING_RE = re.compile(r"^(#{1,2})\s+([A-Z]+-B\d+-C\d+)\b.*$", re.MULTILINE)
FRONTMATTER_CARD_COUNT_RE = re.compile(r"^card_count:\s*(\d+)\s*$", re.MULTILINE)
FRONTMATTER_BATCH_RE = re.compile(r"^batch_id:\s*([^\s]+)\s*$", re.MULTILINE)
URL_PREFIXES = ("http://", "https://", "mailto:", "obsidian://")
TEMPLATE_PLACEHOLDERS = {"Previous card", "Next card", "Related concept", "Production case", "Lab"}


def finding(severity: str, category: str, path: str, message: str, line: Optional[int] = None) -> Dict[str, object]:
    return {
        "severity": severity,
        "category": category,
        "path": path,
        "message": message,
        "line": line,
    }


def included(path: Path, root: Path) -> bool:
    return path.is_file() and not any(part in EXCLUDED_DIRS for part in path.relative_to(root).parts)


def all_files(root: Path) -> List[Path]:
    return sorted(path for path in root.rglob("*") if included(path, root))


def rel(root: Path, path: Path) -> str:
    return path.relative_to(root).as_posix()


def safe_mermaid_name(path: str, index: int) -> str:
    value = re.sub(r"[^A-Za-z0-9_.-]+", "__", path)
    return f"{value}__{index:03d}.mmd"


def clean_link(raw: str) -> str:
    value = raw.split("|", 1)[0].strip()
    value = value.split("#", 1)[0].strip()
    value = value.split("^", 1)[0].strip()
    return value.replace("\\", "/")


def build_file_index(root: Path, files: Sequence[Path]) -> Tuple[Set[str], Dict[str, List[str]]]:
    exact = {rel(root, path) for path in files}
    by_name: Dict[str, List[str]] = defaultdict(list)
    for path in files:
        if path.suffix.lower() not in {".md", ".canvas"}:
            continue
        relative = rel(root, path)
        by_name[path.name].append(relative)
        by_name[path.stem].append(relative)
    return exact, by_name


def resolve_link(target: str, exact: Set[str], by_name: Dict[str, List[str]]) -> Tuple[str, List[str]]:
    if not target or target.startswith(URL_PREFIXES):
        return "ignored", []

    candidates = [target]
    if not Path(target).suffix:
        candidates.extend((target + ".md", target + ".canvas"))

    for candidate in candidates:
        normalized = candidate.lstrip("./")
        if normalized in exact:
            return "resolved", [normalized]

    key = Path(target).name
    matches = set(by_name.get(key, [])) | set(by_name.get(Path(key).stem, []))
    if len(matches) == 1:
        return "resolved", sorted(matches)
    if len(matches) > 1:
        return "ambiguous", sorted(matches)
    return "missing", []


def extract_markdown(
    root: Path,
    path: Path,
    mermaid_dir: Path,
    findings: List[Dict[str, object]],
) -> Tuple[Dict[str, object], List[Tuple[str, int]]]:
    relative = rel(root, path)
    text = path.read_text(encoding="utf-8")
    lines = text.splitlines()
    links: List[Tuple[str, int]] = []
    mermaid_count = 0
    code_blocks = 0
    open_fence: Optional[Tuple[str, int, str, int]] = None
    mermaid_lines: List[str] = []

    for line_no, line in enumerate(lines, start=1):
        links.extend((match.group(1).strip(), line_no) for match in WIKILINK_RE.finditer(line))
        match = FENCE_RE.match(line)
        if not match:
            if open_fence and open_fence[2] == "mermaid":
                mermaid_lines.append(line)
            continue

        marker = match.group(1)
        info = match.group(2).strip().split(maxsplit=1)[0].lower() if match.group(2).strip() else ""
        if open_fence is None:
            open_fence = (marker[0], len(marker), info, line_no)
            code_blocks += 1
            mermaid_lines = []
            continue

        char, length, language, opened_at = open_fence
        if marker[0] == char and len(marker) >= length:
            if language == "mermaid":
                mermaid_count += 1
                source = "\n".join(mermaid_lines).strip() + "\n"
                (mermaid_dir / safe_mermaid_name(relative, mermaid_count)).write_text(source, encoding="utf-8")
            open_fence = None
            mermaid_lines = []
        elif language == "mermaid":
            mermaid_lines.append(line)

    if open_fence:
        findings.append(finding("error", "markdown-fence", relative, f"Unclosed code fence opened at line {open_fence[3]}", open_fence[3]))

    metrics = {
        "path": relative,
        "lines": len(lines),
        "nonblank_lines": sum(1 for line in lines if line.strip()),
        "h2_sections": sum(1 for line in lines if line.startswith("## ")),
        "code_blocks": code_blocks,
        "mermaid_blocks": mermaid_count,
        "wikilinks": len(links),
    }
    return metrics, links


def audit_cards(relative: str, text: str, findings: List[Dict[str, object]]) -> Optional[Dict[str, object]]:
    matches = list(CARD_HEADING_RE.finditer(text))
    declared_match = FRONTMATTER_CARD_COUNT_RE.search(text)
    batch_match = FRONTMATTER_BATCH_RE.search(text)
    if not matches and not declared_match and "Cards.md" not in relative:
        return None

    card_ids = [match.group(2) for match in matches]
    declared = int(declared_match.group(1)) if declared_match else None
    batch = batch_match.group(1) if batch_match else None
    unique_ids = set(card_ids)

    if declared is None:
        findings.append(finding("warning", "card-count", relative, "Card batch has no frontmatter card_count"))
    elif declared != len(unique_ids):
        findings.append(finding("error", "card-count", relative, f"card_count={declared}, but {len(unique_ids)} unique card headings were found"))

    if batch:
        foreign = [card_id for card_id in card_ids if not card_id.startswith(batch + "-")]
        if foreign:
            findings.append(finding("error", "card-id", relative, f"Card IDs do not match batch_id {batch}: {foreign[:5]}"))

    duplicates = [card_id for card_id, count in Counter(card_ids).items() if count > 1]
    if duplicates:
        findings.append(finding("error", "card-id", relative, f"Duplicate card headings: {duplicates}"))

    required_patterns = {
        "Russian Translation": re.compile(r"(?m)^#{2,3}\s+Russian Translation\s*$"),
        "Answer": re.compile(r"(?m)^>\s*\[!answer\]", re.IGNORECASE),
        "Explanation": re.compile(r"(?m)^#{2,3}\s+Explanation\s*$"),
        "Exam Trap": re.compile(r"(?m)^#{2,3}\s+Exam Trap\s*$"),
    }
    missing_details: List[Dict[str, object]] = []
    for index, match in enumerate(matches):
        end = matches[index + 1].start() if index + 1 < len(matches) else len(text)
        body = text[match.end():end]
        missing = [name for name, pattern in required_patterns.items() if not pattern.search(body)]
        if missing:
            missing_details.append({"card_id": match.group(2), "missing": missing})

    if missing_details:
        examples = "; ".join(f"{item['card_id']}: {', '.join(item['missing'])}" for item in missing_details[:6])
        findings.append(
            finding(
                "warning",
                "card-completeness",
                relative,
                f"{len(missing_details)}/{len(card_ids)} cards miss mandatory sections. Examples: {examples}",
            )
        )

    heading_levels = sorted({len(match.group(1)) for match in matches})
    if heading_levels and heading_levels != [2]:
        findings.append(finding("warning", "card-heading-style", relative, f"Card headings use levels {heading_levels}; the dominant vault format is H2"))

    return {
        "path": relative,
        "batch_id": batch,
        "declared_count": declared,
        "actual_count": len(unique_ids),
        "heading_levels": heading_levels,
        "cards_missing_required_sections": len(missing_details),
        "missing_details": missing_details,
    }


def boxes_overlap(a: Dict[str, object], b: Dict[str, object]) -> float:
    try:
        ax1, ay1 = float(a["x"]), float(a["y"])
        ax2, ay2 = ax1 + float(a["width"]), ay1 + float(a["height"])
        bx1, by1 = float(b["x"]), float(b["y"])
        bx2, by2 = bx1 + float(b["width"]), by1 + float(b["height"])
    except (KeyError, TypeError, ValueError):
        return 0.0
    width = max(0.0, min(ax2, bx2) - max(ax1, bx1))
    height = max(0.0, min(ay2, by2) - max(ay1, by1))
    intersection = width * height
    smaller = min((ax2 - ax1) * (ay2 - ay1), (bx2 - bx1) * (by2 - by1))
    return intersection / smaller if smaller > 0 else 0.0


def audit_canvas(root: Path, path: Path, exact: Set[str], findings: List[Dict[str, object]]) -> Dict[str, object]:
    relative = rel(root, path)
    try:
        data = json.loads(path.read_text(encoding="utf-8"))
    except (json.JSONDecodeError, UnicodeDecodeError) as exc:
        findings.append(finding("error", "canvas-json", relative, f"Invalid Canvas JSON: {exc}"))
        return {"path": relative, "nodes": 0, "edges": 0, "file_nodes": 0}

    nodes = data.get("nodes")
    edges = data.get("edges")
    if not isinstance(nodes, list) or not isinstance(edges, list):
        findings.append(finding("error", "canvas-schema", relative, "Canvas must contain list fields nodes and edges"))
        return {"path": relative, "nodes": 0, "edges": 0, "file_nodes": 0}

    node_ids = [node.get("id") for node in nodes if isinstance(node, dict)]
    edge_ids = [edge.get("id") for edge in edges if isinstance(edge, dict)]
    for value, count in Counter(node_ids).items():
        if value and count > 1:
            findings.append(finding("error", "canvas-node-id", relative, f"Duplicate node id: {value}"))
    for value, count in Counter(edge_ids).items():
        if value and count > 1:
            findings.append(finding("error", "canvas-edge-id", relative, f"Duplicate edge id: {value}"))

    valid_ids = {value for value in node_ids if isinstance(value, str)}
    degree = Counter()
    for edge in edges:
        if not isinstance(edge, dict):
            findings.append(finding("error", "canvas-schema", relative, "Canvas edge is not an object"))
            continue
        for field in ("fromNode", "toNode"):
            value = edge.get(field)
            if value not in valid_ids:
                findings.append(finding("error", "canvas-edge-reference", relative, f"Edge {edge.get('id')} references missing {field}={value!r}"))
            elif isinstance(value, str):
                degree[value] += 1

    file_nodes = 0
    ordinary_nodes: List[Dict[str, object]] = []
    for node in nodes:
        if not isinstance(node, dict):
            findings.append(finding("error", "canvas-schema", relative, "Canvas node is not an object"))
            continue
        node_id = node.get("id")
        for field in ("width", "height"):
            value = node.get(field)
            if not isinstance(value, (int, float)) or value <= 0:
                findings.append(finding("error", "canvas-geometry", relative, f"Node {node_id} has invalid {field}={value!r}"))
        if node.get("type") == "file":
            file_nodes += 1
            target = node.get("file")
            if not isinstance(target, str) or target.lstrip("./") not in exact:
                findings.append(finding("error", "canvas-file-node", relative, f"File node {node_id} points to missing path: {target}"))
        if node.get("type") != "group":
            ordinary_nodes.append(node)
            if isinstance(node_id, str) and degree[node_id] == 0 and len(nodes) > 1:
                findings.append(finding("warning", "canvas-disconnected", relative, f"Node {node_id} has no edges"))

    overlap_pairs = []
    for index, left in enumerate(ordinary_nodes):
        for right in ordinary_nodes[index + 1:]:
            ratio = boxes_overlap(left, right)
            if ratio >= 0.50:
                overlap_pairs.append((left.get("id"), right.get("id"), round(ratio, 2)))
    if overlap_pairs:
        sample = ", ".join(f"{a}/{b} ({ratio})" for a, b, ratio in overlap_pairs[:6])
        findings.append(finding("warning", "canvas-overlap", relative, f"Substantial node overlap detected: {sample}"))

    if not nodes:
        findings.append(finding("warning", "canvas-content", relative, "Canvas has no nodes"))
    if nodes and not edges:
        findings.append(finding("warning", "canvas-content", relative, "Canvas has nodes but no edges"))

    return {
        "path": relative,
        "nodes": len(nodes),
        "edges": len(edges),
        "file_nodes": file_nodes,
        "disconnected_nodes": sum(1 for node in ordinary_nodes if isinstance(node.get("id"), str) and degree[node["id"]] == 0),
        "overlap_pairs": len(overlap_pairs),
    }


def pedagogical_checks(path: Path, root: Path, metrics: Dict[str, object], findings: List[Dict[str, object]]) -> None:
    relative = rel(root, path)
    text = path.read_text(encoding="utf-8")
    lower = text.lower()

    if relative.startswith("10_CONCEPTS/"):
        if metrics["nonblank_lines"] < 100:
            findings.append(finding("warning", "concept-depth", relative, f"Only {metrics['nonblank_lines']} nonblank lines; manually verify explanatory depth"))
        if metrics["h2_sections"] < 4:
            findings.append(finding("warning", "concept-structure", relative, f"Only {metrics['h2_sections']} H2 sections"))
        if metrics["code_blocks"] == 0:
            findings.append(finding("warning", "concept-examples", relative, "No fenced code or trace example"))
        if metrics["mermaid_blocks"] == 0:
            findings.append(finding("info", "concept-diagram", relative, "No inline Mermaid diagram; verify coverage by a linked Canvas"))
        if "98_sources/" not in lower and "## sources" not in lower:
            findings.append(finding("warning", "concept-sources", relative, "No obvious link to a primary-source index"))

    if relative.startswith("40_PRODUCTION_CASES/"):
        if metrics["nonblank_lines"] < 80:
            findings.append(finding("warning", "production-depth", relative, f"Only {metrics['nonblank_lines']} nonblank lines"))
        if not any(token in lower for token in ("root cause", "корневая причина", "причина")):
            findings.append(finding("warning", "production-root-cause", relative, "No explicit root-cause section was detected"))
        if not any(token in lower for token in ("fix", "repair", "исправ", "решение")):
            findings.append(finding("warning", "production-fix", relative, "No explicit repair/fix section was detected"))

    if relative.startswith("50_LABS/") and relative.endswith("README.md"):
        if not any(token in lower for token in ("mvn ", "javac ", "gradle ", "docker compose")):
            findings.append(finding("warning", "lab-run-command", relative, "No obvious run command"))
        if not any(token in lower for token in ("expected", "ожида", "trace", "результат")):
            findings.append(finding("warning", "lab-expected-result", relative, "No expected-result or trace section"))


def write_markdown_report(report: Dict[str, object], target: Path) -> None:
    summary = report["summary"]
    lines = [
        "# Obsidian Vault Quality Audit",
        "",
        "## Inventory",
        "",
        f"- Repository files: **{summary['repository_files']}**",
        f"- Markdown files: **{summary['markdown_files']}**",
        f"- Canvas files: **{summary['canvas_files']}**",
        f"- Concept notes: **{summary['concept_files']}**",
        f"- Card batches: **{summary['card_batches']}**",
        f"- Production-case files: **{summary['production_case_files']}**",
        f"- Lab files: **{summary['lab_files']}**",
        f"- Extracted Mermaid blocks: **{summary['mermaid_blocks']}**",
        "",
        "## Findings",
        "",
        f"- Errors: **{summary['findings'].get('error', 0)}**",
        f"- Warnings: **{summary['findings'].get('warning', 0)}**",
        f"- Informational: **{summary['findings'].get('info', 0)}**",
        "",
        "| Severity | Category | File | Line | Finding |",
        "|---|---|---|---:|---|",
    ]
    for item in report["findings"]:
        message = str(item["message"]).replace("|", "\\|").replace("\n", " ")
        lines.append(f"| {item['severity']} | {item['category']} | `{item['path']}` | {item.get('line') or ''} | {message} |")

    lines.extend(["", "## Certification batch quality", "", "| Batch file | Declared | Actual | Cards missing mandatory sections | Heading levels |", "|---|---:|---:|---:|---|"])
    for item in report["card_batches"]:
        lines.append(f"| `{item['path']}` | {item['declared_count'] or ''} | {item['actual_count']} | {item['cards_missing_required_sections']} | {item['heading_levels']} |")

    lines.extend(["", "## Concept-note metrics", "", "| File | Nonblank lines | H2 | Code blocks | Mermaid | Links |", "|---|---:|---:|---:|---:|---:|"])
    for item in report["markdown_metrics"]:
        if item["path"].startswith("10_CONCEPTS/"):
            lines.append(f"| `{item['path']}` | {item['nonblank_lines']} | {item['h2_sections']} | {item['code_blocks']} | {item['mermaid_blocks']} | {item['wikilinks']} |")

    lines.extend(["", "## Canvas metrics", "", "| Canvas | Nodes | Edges | File nodes | Disconnected | Overlap pairs |", "|---|---:|---:|---:|---:|---:|"])
    for item in report["canvas_metrics"]:
        lines.append(f"| `{item['path']}` | {item['nodes']} | {item['edges']} | {item['file_nodes']} | {item['disconnected_nodes']} | {item['overlap_pairs']} |")

    lines.extend([
        "",
        "## Interpretation",
        "",
        "- Errors are structural defects or broken navigation and should block merge.",
        "- Warnings are manual-review items; they are not automatically proof of incorrect content.",
        "- Mermaid renderer results are appended by CI after every block is parsed by mermaid-cli.",
        "- Length metrics identify suspiciously thin notes but do not substitute for technical review.",
        "",
    ])
    target.write_text("\n".join(lines), encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", default=".")
    parser.add_argument("--output-dir", default=".audit")
    args = parser.parse_args()

    root = Path(args.root).resolve()
    output = (root / args.output_dir).resolve()
    if output.exists():
        shutil.rmtree(output)
    mermaid_dir = output / "mermaid"
    mermaid_dir.mkdir(parents=True, exist_ok=True)

    files = all_files(root)
    markdown_files = [path for path in files if path.suffix.lower() == ".md"]
    canvas_files = [path for path in files if path.suffix.lower() == ".canvas"]
    exact, by_name = build_file_index(root, files)

    findings: List[Dict[str, object]] = []
    markdown_metrics: List[Dict[str, object]] = []
    card_batches: List[Dict[str, object]] = []
    links: List[Tuple[str, int, str]] = []
    global_card_ids: Dict[str, List[str]] = defaultdict(list)

    for path in markdown_files:
        relative = rel(root, path)
        try:
            text = path.read_text(encoding="utf-8")
        except UnicodeDecodeError as exc:
            findings.append(finding("error", "encoding", relative, f"Not valid UTF-8: {exc}"))
            continue
        if not text.strip():
            findings.append(finding("error", "empty-file", relative, "Markdown file is empty"))

        metrics, found_links = extract_markdown(root, path, mermaid_dir, findings)
        markdown_metrics.append(metrics)
        links.extend((raw, line, relative) for raw, line in found_links)
        pedagogical_checks(path, root, metrics, findings)

        batch = audit_cards(relative, text, findings)
        if batch:
            card_batches.append(batch)
            for detail in batch.get("missing_details", []):
                pass
            for match in CARD_HEADING_RE.finditer(text):
                global_card_ids[match.group(2)].append(relative)

    for raw, line, source in links:
        target = clean_link(raw)
        status, matches = resolve_link(target, exact, by_name)
        if status == "missing":
            if source.startswith("90_TEMPLATES/") and target in TEMPLATE_PLACEHOLDERS:
                findings.append(finding("info", "wikilink-template", source, f"Template placeholder: [[{raw}]]", line))
            else:
                findings.append(finding("error", "wikilink", source, f"Broken wikilink target: [[{raw}]]", line))
        elif status == "ambiguous":
            findings.append(finding("warning", "wikilink-ambiguous", source, f"Ambiguous wikilink [[{raw}]] matches: {', '.join(matches[:8])}", line))

    for card_id, sources in global_card_ids.items():
        if len(sources) > 1:
            findings.append(finding("error", "card-id-duplicate", sources[0], f"Duplicate card ID {card_id} in: {', '.join(sources)}"))

    canvas_metrics = [audit_canvas(root, path, exact, findings) for path in canvas_files]
    findings.sort(key=lambda item: ({"error": 0, "warning": 1, "info": 2}.get(str(item["severity"]), 9), str(item["category"]), str(item["path"]), int(item.get("line") or 0)))

    severity_counts = Counter(str(item["severity"]) for item in findings)
    report = {
        "summary": {
            "repository_files": len(files),
            "markdown_files": len(markdown_files),
            "canvas_files": len(canvas_files),
            "concept_files": sum(1 for item in markdown_metrics if str(item["path"]).startswith("10_CONCEPTS/")),
            "card_batches": len(card_batches),
            "production_case_files": sum(1 for item in markdown_metrics if str(item["path"]).startswith("40_PRODUCTION_CASES/")),
            "lab_files": sum(1 for path in files if rel(root, path).startswith("50_LABS/")),
            "mermaid_blocks": sum(int(item["mermaid_blocks"]) for item in markdown_metrics),
            "findings": dict(severity_counts),
        },
        "category_counts": dict(Counter(str(item["category"]) for item in findings)),
        "findings": findings,
        "markdown_metrics": markdown_metrics,
        "card_batches": card_batches,
        "canvas_metrics": canvas_metrics,
    }

    output.mkdir(parents=True, exist_ok=True)
    (output / "vault-audit.json").write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")
    write_markdown_report(report, output / "vault-audit.md")
    print(json.dumps(report["summary"], ensure_ascii=False, indent=2))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
