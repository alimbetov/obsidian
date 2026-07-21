#!/usr/bin/env python3
"""Static quality audit for the Obsidian learning vault.

The script intentionally uses only the Python standard library so it can run in
CI and on a developer workstation. Mermaid syntax is rendered in a separate CI
step; this script extracts every Mermaid block and validates repository-level
structure around it.
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from collections import Counter, defaultdict
from dataclasses import asdict, dataclass
from pathlib import Path
from typing import Dict, Iterable, List, Optional, Sequence, Set, Tuple

EXCLUDED_DIRS = {".git", ".obsidian", ".audit", "node_modules", "target"}
FENCE_RE = re.compile(r"^\s*(`{3,}|~{3,})([^`]*)$")
WIKILINK_RE = re.compile(r"\[\[([^\]]+)\]\]")
CARD_HEADING_RE = re.compile(r"^##\s+([A-Z]+-B\d+-C\d+)\b")
FRONTMATTER_CARD_COUNT_RE = re.compile(r"^card_count:\s*(\d+)\s*$", re.MULTILINE)
FRONTMATTER_BATCH_RE = re.compile(r"^batch_id:\s*([^\s]+)\s*$", re.MULTILINE)
URL_PREFIXES = ("http://", "https://", "mailto:", "obsidian://")


@dataclass
class Finding:
    severity: str
    category: str
    path: str
    message: str
    line: Optional[int] = None


@dataclass
class MarkdownMetrics:
    path: str
    lines: int
    nonblank_lines: int
    h2_sections: int
    code_blocks: int
    mermaid_blocks: int
    wikilinks: int


def iter_files(root: Path, suffixes: Sequence[str]) -> Iterable[Path]:
    for path in root.rglob("*"):
        if not path.is_file() or path.suffix.lower() not in suffixes:
            continue
        if any(part in EXCLUDED_DIRS for part in path.relative_to(root).parts):
            continue
        yield path


def rel(root: Path, path: Path) -> str:
    return path.relative_to(root).as_posix()


def safe_name(path: str, index: int) -> str:
    normalized = re.sub(r"[^A-Za-z0-9_.-]+", "__", path)
    return f"{normalized}__{index:03d}.mmd"


def parse_markdown(
    root: Path,
    path: Path,
    mermaid_dir: Path,
    findings: List[Finding],
) -> Tuple[MarkdownMetrics, List[Tuple[str, int]], List[str]]:
    relative = rel(root, path)
    text = path.read_text(encoding="utf-8")
    lines = text.splitlines()
    links: List[Tuple[str, int]] = []
    mermaid_sources: List[str] = []
    code_blocks = 0
    open_fence: Optional[Tuple[str, int, str, int]] = None
    current_mermaid: List[str] = []

    for line_no, line in enumerate(lines, start=1):
        for match in WIKILINK_RE.finditer(line):
            links.append((match.group(1).strip(), line_no))

        fence = FENCE_RE.match(line)
        if not fence:
            if open_fence and open_fence[2] == "mermaid":
                current_mermaid.append(line)
            continue

        marker = fence.group(1)
        info = fence.group(2).strip().split(maxsplit=1)[0].lower() if fence.group(2).strip() else ""

        if open_fence is None:
            open_fence = (marker[0], len(marker), info, line_no)
            code_blocks += 1
            if info == "mermaid":
                current_mermaid = []
            continue

        open_char, open_len, open_info, open_line = open_fence
        if marker[0] == open_char and len(marker) >= open_len:
            if open_info == "mermaid":
                source = "\n".join(current_mermaid).strip() + "\n"
                mermaid_sources.append(source)
                target = mermaid_dir / safe_name(relative, len(mermaid_sources))
                target.write_text(source, encoding="utf-8")
            open_fence = None
            current_mermaid = []
        elif open_info == "mermaid":
            current_mermaid.append(line)

    if open_fence is not None:
        findings.append(
            Finding(
                "error",
                "markdown-fence",
                relative,
                f"Code fence opened on line {open_fence[3]} is not closed",
                open_fence[3],
            )
        )

    metrics = MarkdownMetrics(
        path=relative,
        lines=len(lines),
        nonblank_lines=sum(1 for line in lines if line.strip()),
        h2_sections=sum(1 for line in lines if line.startswith("## ")),
        code_blocks=code_blocks,
        mermaid_blocks=len(mermaid_sources),
        wikilinks=len(links),
    )
    return metrics, links, mermaid_sources


def index_files(root: Path, files: Sequence[Path]) -> Tuple[Set[str], Dict[str, List[str]]]:
    exact: Set[str] = set()
    by_stem: Dict[str, List[str]] = defaultdict(list)
    for path in files:
        relative = rel(root, path)
        exact.add(relative)
        by_stem[path.stem].append(relative)
        by_stem[path.name].append(relative)
    return exact, by_stem


def clean_wikilink(raw: str) -> str:
    target = raw.split("|", 1)[0].strip()
    target = target.split("#", 1)[0].strip()
    target = target.split("^", 1)[0].strip()
    return target.replace("\\", "/")


def resolve_wikilink(target: str, exact: Set[str], by_stem: Dict[str, List[str]]) -> Tuple[str, List[str]]:
    if not target or target.startswith(URL_PREFIXES):
        return "ignored", []

    candidates = [target]
    suffix = Path(target).suffix.lower()
    if not suffix:
        candidates.extend([target + ".md", target + ".canvas"])

    for candidate in candidates:
        normalized = candidate.lstrip("./")
        if normalized in exact:
            return "resolved", [normalized]

    basename = Path(target).name
    matches: Set[str] = set()
    for key in (basename, Path(basename).stem):
        matches.update(by_stem.get(key, []))

    if len(matches) == 1:
        return "resolved", sorted(matches)
    if len(matches) > 1:
        return "ambiguous", sorted(matches)
    return "missing", []


def audit_canvas(root: Path, path: Path, exact: Set[str], findings: List[Finding]) -> Dict[str, int]:
    relative = rel(root, path)
    try:
        payload = json.loads(path.read_text(encoding="utf-8"))
    except (json.JSONDecodeError, UnicodeDecodeError) as exc:
        findings.append(Finding("error", "canvas-json", relative, f"Invalid Canvas JSON: {exc}"))
        return {"nodes": 0, "edges": 0, "file_nodes": 0}

    nodes = payload.get("nodes")
    edges = payload.get("edges")
    if not isinstance(nodes, list) or not isinstance(edges, list):
        findings.append(Finding("error", "canvas-schema", relative, "Canvas must contain list fields 'nodes' and 'edges'"))
        return {"nodes": 0, "edges": 0, "file_nodes": 0}

    node_ids = [node.get("id") for node in nodes if isinstance(node, dict)]
    edge_ids = [edge.get("id") for edge in edges if isinstance(edge, dict)]
    for duplicate in sorted(key for key, count in Counter(node_ids).items() if key and count > 1):
        findings.append(Finding("error", "canvas-node-id", relative, f"Duplicate node id: {duplicate}"))
    for duplicate in sorted(key for key, count in Counter(edge_ids).items() if key and count > 1):
        findings.append(Finding("error", "canvas-edge-id", relative, f"Duplicate edge id: {duplicate}"))

    valid_node_ids = {node_id for node_id in node_ids if isinstance(node_id, str)}
    for edge in edges:
        if not isinstance(edge, dict):
            findings.append(Finding("error", "canvas-schema", relative, "Canvas edge is not an object"))
            continue
        for field in ("fromNode", "toNode"):
            node_id = edge.get(field)
            if node_id not in valid_node_ids:
                findings.append(
                    Finding(
                        "error",
                        "canvas-edge-reference",
                        relative,
                        f"Edge {edge.get('id', '<no-id>')} references missing {field}={node_id!r}",
                    )
                )

    file_nodes = 0
    for node in nodes:
        if not isinstance(node, dict):
            findings.append(Finding("error", "canvas-schema", relative, "Canvas node is not an object"))
            continue
        if node.get("type") != "file":
            continue
        file_nodes += 1
        file_value = node.get("file")
        if not isinstance(file_value, str) or not file_value.strip():
            findings.append(Finding("error", "canvas-file-node", relative, f"File node {node.get('id')} has no file path"))
            continue
        normalized = file_value.lstrip("./")
        if normalized not in exact:
            findings.append(
                Finding(
                    "error",
                    "canvas-file-node",
                    relative,
                    f"File node {node.get('id')} points to missing path: {file_value}",
                )
            )

    if not nodes:
        findings.append(Finding("warning", "canvas-content", relative, "Canvas has no nodes"))
    if nodes and not edges:
        findings.append(Finding("warning", "canvas-content", relative, "Canvas has nodes but no edges"))

    return {"nodes": len(nodes), "edges": len(edges), "file_nodes": file_nodes}


def audit_card_file(path: Path, relative: str, text: str, findings: List[Finding], global_ids: List[Tuple[str, str]]) -> None:
    declared_match = FRONTMATTER_CARD_COUNT_RE.search(text)
    batch_match = FRONTMATTER_BATCH_RE.search(text)
    card_ids = [match.group(1) for line in text.splitlines() if (match := CARD_HEADING_RE.match(line))]
    for card_id in card_ids:
        global_ids.append((card_id, relative))

    if declared_match:
        declared = int(declared_match.group(1))
        actual = len(set(card_ids))
        if declared != actual:
            findings.append(
                Finding(
                    "error",
                    "card-count",
                    relative,
                    f"Frontmatter card_count={declared}, but {actual} unique card headings were found",
                )
            )
    elif "Cards.md" in relative:
        findings.append(Finding("warning", "card-count", relative, "Card batch has no frontmatter card_count"))

    if batch_match and card_ids:
        batch = batch_match.group(1)
        foreign = [card_id for card_id in card_ids if not card_id.startswith(batch + "-")]
        if foreign:
            findings.append(Finding("error", "card-id", relative, f"Card IDs do not match batch_id {batch}: {foreign[:5]}"))

    if card_ids:
        sections = re.split(r"(?m)^##\s+(?=[A-Z]+-B\d+-C\d+\b)", text)[1:]
        required = {
            "Russian Translation": re.compile(r"(?m)^###\s+Russian Translation\s*$"),
            "Answer": re.compile(r"(?m)^(?:###\s+Answer\s*$|>\s*\[!answer\])", re.IGNORECASE),
            "Explanation": re.compile(r"(?m)^###\s+Explanation\s*$"),
            "Exam Trap": re.compile(r"(?m)^###\s+Exam Trap\s*$"),
        }
        for index, section in enumerate(sections):
            card_id_match = re.match(r"([^\n]+)", section)
            card_id = card_ids[index] if index < len(card_ids) else (card_id_match.group(1) if card_id_match else "unknown")
            missing = [name for name, pattern in required.items() if not pattern.search(section)]
            if missing:
                findings.append(Finding("warning", "card-completeness", relative, f"{card_id} misses sections: {', '.join(missing)}"))


def add_pedagogical_findings(metrics: MarkdownMetrics, findings: List[Finding]) -> None:
    path = metrics.path
    if path.startswith("10_CONCEPTS/"):
        if metrics.nonblank_lines < 120:
            findings.append(Finding("warning", "concept-depth", path, f"Concept note has only {metrics.nonblank_lines} nonblank lines"))
        if metrics.h2_sections < 4:
            findings.append(Finding("warning", "concept-structure", path, f"Concept note has only {metrics.h2_sections} H2 sections"))
        if metrics.code_blocks == 0:
            findings.append(Finding("warning", "concept-examples", path, "Concept note contains no fenced code/example block"))
        if metrics.mermaid_blocks == 0:
            findings.append(Finding("info", "concept-diagram", path, "Concept note contains no inline Mermaid diagram; verify that a linked Canvas covers the mechanism"))

    if path.startswith("40_PRODUCTION_CASES/"):
        if metrics.nonblank_lines < 80:
            findings.append(Finding("warning", "production-depth", path, f"Production case file has only {metrics.nonblank_lines} nonblank lines"))

    if path.startswith("50_LABS/") and path.endswith("README.md"):
        text = Path(path).name  # marker only; detailed README checks are performed by content below
        del text


def build_report(
    root: Path,
    markdown_metrics: List[MarkdownMetrics],
    canvas_metrics: Dict[str, Dict[str, int]],
    findings: List[Finding],
    mermaid_count: int,
) -> Dict[str, object]:
    severity_counts = Counter(item.severity for item in findings)
    category_counts = Counter(item.category for item in findings)
    concepts = [metric for metric in markdown_metrics if metric.path.startswith("10_CONCEPTS/")]
    cards = [metric for metric in markdown_metrics if "/Cards.md" in metric.path]
    cases = [metric for metric in markdown_metrics if metric.path.startswith("40_PRODUCTION_CASES/")]
    labs = [metric for metric in markdown_metrics if metric.path.startswith("50_LABS/")]

    return {
        "summary": {
            "markdown_files": len(markdown_metrics),
            "canvas_files": len(canvas_metrics),
            "concept_files": len(concepts),
            "card_files": len(cards),
            "production_case_files": len(cases),
            "lab_files": len(labs),
            "mermaid_blocks": mermaid_count,
            "findings": dict(severity_counts),
        },
        "category_counts": dict(sorted(category_counts.items())),
        "findings": [asdict(item) for item in findings],
        "markdown_metrics": [asdict(metric) for metric in markdown_metrics],
        "canvas_metrics": canvas_metrics,
    }


def write_markdown_report(report: Dict[str, object], output: Path) -> None:
    summary = report["summary"]
    findings = report["findings"]
    metrics = report["markdown_metrics"]

    lines = [
        "# Obsidian Vault Quality Audit",
        "",
        "## Inventory",
        "",
        f"- Markdown files: **{summary['markdown_files']}**",
        f"- Canvas files: **{summary['canvas_files']}**",
        f"- Concept files: **{summary['concept_files']}**",
        f"- Card batch files: **{summary['card_files']}**",
        f"- Production-case files: **{summary['production_case_files']}**",
        f"- Lab files: **{summary['lab_files']}**",
        f"- Extracted Mermaid blocks: **{summary['mermaid_blocks']}**",
        "",
        "## Finding counts",
        "",
        f"- Errors: **{summary['findings'].get('error', 0)}**",
        f"- Warnings: **{summary['findings'].get('warning', 0)}**",
        f"- Informational: **{summary['findings'].get('info', 0)}**",
        "",
        "## Findings",
        "",
        "| Severity | Category | File | Line | Finding |",
        "|---|---|---|---:|---|",
    ]
    for item in findings:
        message = str(item["message"]).replace("|", "\\|").replace("\n", " ")
        lines.append(
            f"| {item['severity']} | {item['category']} | `{item['path']}` | {item.get('line') or ''} | {message} |"
        )

    lines.extend(
        [
            "",
            "## Concept-note metrics",
            "",
            "| File | Nonblank lines | H2 sections | Code blocks | Mermaid | Wikilinks |",
            "|---|---:|---:|---:|---:|---:|",
        ]
    )
    for metric in metrics:
        if not metric["path"].startswith("10_CONCEPTS/"):
            continue
        lines.append(
            f"| `{metric['path']}` | {metric['nonblank_lines']} | {metric['h2_sections']} | "
            f"{metric['code_blocks']} | {metric['mermaid_blocks']} | {metric['wikilinks']} |"
        )

    lines.extend(
        [
            "",
            "## Interpretation",
            "",
            "- Structural errors should be fixed before merging.",
            "- Warnings require manual review; some are intentional design choices.",
            "- Mermaid render results are appended by the GitHub Actions workflow.",
            "- Line count is not a quality score; it is used only to find suspiciously thin modules.",
            "",
        ]
    )
    output.write_text("\n".join(lines), encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", default=".")
    parser.add_argument("--output-dir", default=".audit")
    args = parser.parse_args()

    root = Path(args.root).resolve()
    output_dir = (root / args.output_dir).resolve()
    mermaid_dir = output_dir / "mermaid"
    mermaid_dir.mkdir(parents=True, exist_ok=True)

    findings: List[Finding] = []
    markdown_files = sorted(iter_files(root, (".md",)))
    canvas_files = sorted(iter_files(root, (".canvas",)))
    all_linkable_files = markdown_files + canvas_files
    exact, by_stem = index_files(root, all_linkable_files)

    markdown_metrics: List[MarkdownMetrics] = []
    all_links: List[Tuple[str, int, str]] = []
    global_card_ids: List[Tuple[str, str]] = []
    mermaid_count = 0

    for path in markdown_files:
        relative = rel(root, path)
        try:
            text = path.read_text(encoding="utf-8")
        except UnicodeDecodeError as exc:
            findings.append(Finding("error", "encoding", relative, f"File is not valid UTF-8: {exc}"))
            continue

        if not text.strip():
            findings.append(Finding("error", "empty-file", relative, "Markdown file is empty"))

        metrics, links, mermaid_sources = parse_markdown(root, path, mermaid_dir, findings)
        markdown_metrics.append(metrics)
        mermaid_count += len(mermaid_sources)
        all_links.extend((target, line, relative) for target, line in links)
        add_pedagogical_findings(metrics, findings)
        if "/Cards.md" in relative or "batch_id:" in text:
            audit_card_file(path, relative, text, findings, global_card_ids)

        if relative.startswith("50_LABS/") and relative.endswith("README.md"):
            lower = text.lower()
            if not any(token in lower for token in ("mvn ", "javac ", "gradle ", "docker compose")):
                findings.append(Finding("warning", "lab-run-command", relative, "Lab README has no obvious run command"))
            if not any(token in lower for token in ("expected", "ожида", "trace", "результат")):
                findings.append(Finding("warning", "lab-expected-result", relative, "Lab README has no expected-result section"))

    for raw_target, line_no, source in all_links:
        target = clean_wikilink(raw_target)
        status, matches = resolve_wikilink(target, exact, by_stem)
        if status == "missing":
            findings.append(Finding("error", "wikilink", source, f"Broken wikilink target: [[{raw_target}]]", line_no))
        elif status == "ambiguous":
            findings.append(
                Finding(
                    "warning",
                    "wikilink-ambiguous",
                    source,
                    f"Ambiguous wikilink [[{raw_target}]] matches: {', '.join(matches[:8])}",
                    line_no,
                )
            )

    card_id_map: Dict[str, List[str]] = defaultdict(list)
    for card_id, source in global_card_ids:
        card_id_map[card_id].append(source)
    for card_id, sources in sorted(card_id_map.items()):
        if len(sources) > 1:
            findings.append(Finding("error", "card-id-duplicate", sources[0], f"Duplicate card ID {card_id} in: {', '.join(sources)}"))

    canvas_metrics: Dict[str, Dict[str, int]] = {}
    for path in canvas_files:
        canvas_metrics[rel(root, path)] = audit_canvas(root, path, exact, findings)

    findings.sort(key=lambda item: ({"error": 0, "warning": 1, "info": 2}.get(item.severity, 9), item.category, item.path, item.line or 0))
    report = build_report(root, markdown_metrics, canvas_metrics, findings, mermaid_count)
    output_dir.mkdir(parents=True, exist_ok=True)
    (output_dir / "vault-audit.json").write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")
    write_markdown_report(report, output_dir / "vault-audit.md")

    summary = report["summary"]
    print(json.dumps(summary, ensure_ascii=False, indent=2))
    print(f"Reports: {output_dir / 'vault-audit.md'} and {output_dir / 'vault-audit.json'}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
