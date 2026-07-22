#!/usr/bin/env python3
"""Cross-link graph audit for published Obsidian learning routes.

The structural vault audit validates that individual wikilinks resolve. This audit
validates graph quality:

- repository entry points reach the central route registry;
- the registry reaches every published route hub;
- route indexes collectively link every declared artifact;
- published artifacts have inbound links;
- advanced Markdown artifacts are not navigation sinks;
- global orphan notes are reported as debt;
- Canvas file nodes participate in the same directed graph.
"""

from __future__ import annotations

import json
import re
import sys
from collections import Counter, defaultdict, deque
from pathlib import Path
from typing import Dict, Iterable, List, Sequence, Set, Tuple

ROOT = Path(__file__).resolve().parents[2]
MANIFEST_PATH = ROOT / ".github/knowledge-routes.json"
OUTPUT_DIR = ROOT / ".audit"
EXCLUDED_DIRS = {".git", ".obsidian", ".audit", "node_modules", "target"}
WIKILINK_RE = re.compile(r"\[\[([^\]]+)\]\]")
URL_PREFIXES = ("http://", "https://", "mailto:", "obsidian://")
CONTENT_PREFIXES = (
    "00_HOME/",
    "01_MAPS/",
    "10_CONCEPTS/",
    "20_QUESTIONS/",
    "30_CERTIFICATIONS/",
    "40_PRODUCTION_CASES/",
    "50_LABS/",
    "98_SOURCES/",
    "99_AUDITS/",
)
ORPHAN_EXEMPT_PREFIXES = ("90_TEMPLATES/", ".github/")
ORPHAN_EXEMPT_FILES = {"README.md", "CONTRIBUTING.md"}


def relative(path: Path) -> str:
    return path.relative_to(ROOT).as_posix()


def included(path: Path) -> bool:
    return path.is_file() and not any(part in EXCLUDED_DIRS for part in path.relative_to(ROOT).parts)


def all_files() -> List[Path]:
    return sorted(path for path in ROOT.rglob("*") if included(path))


def clean_target(raw: str) -> str:
    value = raw.split("|", 1)[0].strip()
    value = value.split("#", 1)[0].strip()
    value = value.split("^", 1)[0].strip()
    return value.replace("\\", "/")


def build_index(files: Sequence[Path]) -> Tuple[Set[str], Dict[str, List[str]]]:
    exact = {relative(path) for path in files}
    by_name: Dict[str, List[str]] = defaultdict(list)
    for path in files:
        if path.suffix.lower() not in {".md", ".canvas"}:
            continue
        rel = relative(path)
        by_name[path.name].append(rel)
        by_name[path.stem].append(rel)
    return exact, by_name


def resolve(target: str, exact: Set[str], by_name: Dict[str, List[str]]) -> Tuple[str, List[str]]:
    if not target or target.startswith(URL_PREFIXES):
        return "ignored", []

    candidates = [target]
    if not Path(target).suffix:
        candidates.extend((target + ".md", target + ".canvas"))

    for candidate in candidates:
        normalized = candidate.lstrip("./")
        if normalized in exact:
            return "resolved", [normalized]

    name = Path(target).name
    matches = set(by_name.get(name, [])) | set(by_name.get(Path(name).stem, []))
    if len(matches) == 1:
        return "resolved", sorted(matches)
    if len(matches) > 1:
        return "ambiguous", sorted(matches)
    return "missing", []


def add_edge(outbound: Dict[str, Set[str]], inbound: Dict[str, Set[str]], source: str, target: str) -> None:
    if source == target:
        return
    outbound[source].add(target)
    inbound[target].add(source)


def build_graph(files: Sequence[Path], exact: Set[str], by_name: Dict[str, List[str]]):
    outbound: Dict[str, Set[str]] = defaultdict(set)
    inbound: Dict[str, Set[str]] = defaultdict(set)
    link_findings: List[Dict[str, object]] = []

    for path in files:
        source = relative(path)
        suffix = path.suffix.lower()
        if suffix == ".md":
            text = path.read_text(encoding="utf-8")
            for line_no, line in enumerate(text.splitlines(), start=1):
                for match in WIKILINK_RE.finditer(line):
                    raw = match.group(1).strip()
                    target = clean_target(raw)
                    status, matches = resolve(target, exact, by_name)
                    if status == "resolved":
                        add_edge(outbound, inbound, source, matches[0])
                    elif status == "ambiguous":
                        link_findings.append({
                            "severity": "warning",
                            "category": "ambiguous-link",
                            "path": source,
                            "line": line_no,
                            "message": f"[[{raw}]] matches {', '.join(matches[:8])}",
                        })
                    elif status == "missing":
                        link_findings.append({
                            "severity": "error",
                            "category": "broken-link",
                            "path": source,
                            "line": line_no,
                            "message": f"Broken target [[{raw}]]",
                        })
        elif suffix == ".canvas":
            try:
                data = json.loads(path.read_text(encoding="utf-8"))
            except (json.JSONDecodeError, UnicodeDecodeError):
                continue
            for node in data.get("nodes", []):
                if not isinstance(node, dict) or node.get("type") != "file":
                    continue
                target = node.get("file")
                if isinstance(target, str):
                    normalized = target.lstrip("./")
                    if normalized in exact:
                        add_edge(outbound, inbound, source, normalized)

    return outbound, inbound, link_findings


def route_nodes(route: Dict[str, object]) -> Set[str]:
    values: Set[str] = set()
    for key in ("hub",):
        value = route.get(key)
        if isinstance(value, str):
            values.add(value)
    for key in ("entry_points", "indexes", "artifacts"):
        for value in route.get(key, []):
            if isinstance(value, str):
                values.add(value)
    return values


def reachable(outbound: Dict[str, Set[str]], start: str, target: str, allowed: Set[str] | None = None) -> bool:
    if start == target:
        return True
    queue = deque([start])
    seen = {start}
    while queue:
        current = queue.popleft()
        for nxt in outbound.get(current, set()):
            if allowed is not None and nxt not in allowed:
                continue
            if nxt == target:
                return True
            if nxt not in seen:
                seen.add(nxt)
                queue.append(nxt)
    return False


def finding(severity: str, category: str, path: str, message: str) -> Dict[str, object]:
    return {
        "severity": severity,
        "category": category,
        "path": path,
        "message": message,
    }


def main() -> int:
    if not MANIFEST_PATH.exists():
        print(f"Missing manifest: {MANIFEST_PATH}", file=sys.stderr)
        return 2

    manifest = json.loads(MANIFEST_PATH.read_text(encoding="utf-8"))
    files = all_files()
    exact, by_name = build_index(files)
    outbound, inbound, findings = build_graph(files, exact, by_name)

    registry = manifest["registry"]
    strict_nodes: Set[str] = {registry}
    route_reports: List[Dict[str, object]] = []

    if registry not in exact:
        findings.append(finding("error", "registry", registry, "Central route registry does not exist"))

    for entry in manifest.get("global_entry_points", []):
        if entry not in exact:
            findings.append(finding("error", "entry-point", entry, "Global entry point does not exist"))
        elif registry not in outbound.get(entry, set()):
            findings.append(finding("error", "entry-point", entry, f"Must link directly to [[{registry}]]"))

    for route in manifest.get("routes", []):
        route_id = route["id"]
        status = route.get("status", "draft")
        hub = route["hub"]
        entries = list(route.get("entry_points", []))
        indexes = list(route.get("indexes", []))
        artifacts = list(route.get("artifacts", []))
        nodes = route_nodes(route)
        strict_nodes.update(nodes)

        missing = sorted(node for node in nodes if node not in exact)
        for node in missing:
            findings.append(finding("error", "route-file", node, f"{route_id}: declared route artifact does not exist"))

        if hub in exact and registry in exact and hub not in outbound.get(registry, set()):
            findings.append(finding("error", "registry-edge", registry, f"Registry must link route hub {hub} ({route_id})"))

        for entry in entries:
            if entry not in exact:
                continue
            direct_targets = outbound.get(entry, set())
            if hub not in direct_targets and registry not in direct_targets:
                findings.append(finding(
                    "error" if status == "published" else "warning",
                    "route-entry",
                    entry,
                    f"{route_id}: entry point must link the route hub or central registry",
                ))

        index_set = {index for index in indexes if index in exact}
        for artifact in artifacts:
            if artifact not in exact:
                continue
            linked_from_indexes = sorted(index for index in index_set if artifact in outbound.get(index, set()))
            if not linked_from_indexes:
                findings.append(finding(
                    "error" if status == "published" else "warning",
                    "route-coverage",
                    artifact,
                    f"{route_id}: no declared route index links this artifact",
                ))
            if not inbound.get(artifact):
                findings.append(finding(
                    "error" if status == "published" else "warning",
                    "orphan-published",
                    artifact,
                    f"{route_id}: artifact has no inbound links",
                ))
            if artifact.endswith(".md") and not outbound.get(artifact):
                findings.append(finding(
                    "warning",
                    "navigation-sink",
                    artifact,
                    f"{route_id}: Markdown artifact has no outbound wikilinks",
                ))

        route_allowed = nodes | {registry}
        registry_reaches_hub = registry in exact and hub in exact and reachable(outbound, registry, hub)
        route_reports.append({
            "id": route_id,
            "status": status,
            "hub": hub,
            "artifacts": len(artifacts),
            "missing_files": len(missing),
            "registry_reaches_hub": registry_reaches_hub,
            "indexed_artifacts": sum(
                1 for artifact in artifacts
                if artifact in exact and any(artifact in outbound.get(index, set()) for index in index_set)
            ),
            "route_nodes": len(route_allowed),
        })

    markdown_canvas = [
        relative(path)
        for path in files
        if path.suffix.lower() in {".md", ".canvas"}
        and relative(path).startswith(CONTENT_PREFIXES)
        and not relative(path).startswith(ORPHAN_EXEMPT_PREFIXES)
        and relative(path) not in ORPHAN_EXEMPT_FILES
    ]
    global_orphans = sorted(node for node in markdown_canvas if not inbound.get(node))
    global_sinks = sorted(node for node in markdown_canvas if node.endswith(".md") and not outbound.get(node))

    for node in global_orphans:
        severity = "error" if node in strict_nodes else "warning"
        findings.append(finding(severity, "orphan-global", node, "No inbound Markdown or Canvas link"))

    strict_ambiguous = []
    for item in findings:
        if item.get("category") == "ambiguous-link" and item.get("path") in strict_nodes:
            strict_ambiguous.append(item)
            item["severity"] = "error"
            item["category"] = "ambiguous-strict-route-link"

    findings.sort(key=lambda item: (
        {"error": 0, "warning": 1, "info": 2}.get(str(item["severity"]), 9),
        str(item["category"]),
        str(item["path"]),
    ))
    severity_counts = Counter(str(item["severity"]) for item in findings)

    graph_nodes = set(outbound) | set(inbound)
    edge_count = sum(len(targets) for targets in outbound.values())
    report = {
        "summary": {
            "graph_nodes": len(graph_nodes),
            "graph_edges": edge_count,
            "published_routes": sum(1 for route in manifest.get("routes", []) if route.get("status") == "published"),
            "strict_route_nodes": len(strict_nodes),
            "global_orphans": len(global_orphans),
            "global_sinks": len(global_sinks),
            "findings": dict(severity_counts),
        },
        "routes": route_reports,
        "findings": findings,
        "global_orphans": global_orphans,
        "global_sinks": global_sinks,
    }

    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    (OUTPUT_DIR / "cross-link-report.json").write_text(
        json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8"
    )

    lines = [
        "# Cross-Link Graph Audit",
        "",
        "## Summary",
        "",
        f"- Graph nodes: **{report['summary']['graph_nodes']}**",
        f"- Directed edges: **{report['summary']['graph_edges']}**",
        f"- Published routes: **{report['summary']['published_routes']}**",
        f"- Strict route nodes: **{report['summary']['strict_route_nodes']}**",
        f"- Global orphan artifacts: **{report['summary']['global_orphans']}**",
        f"- Global Markdown sinks: **{report['summary']['global_sinks']}**",
        f"- Errors: **{severity_counts.get('error', 0)}**",
        f"- Warnings: **{severity_counts.get('warning', 0)}**",
        "",
        "## Published route coverage",
        "",
        "| Route | Hub | Artifacts | Indexed | Missing | Registry reaches hub |",
        "|---|---|---:|---:|---:|---|",
    ]
    for route in route_reports:
        lines.append(
            f"| {route['id']} | `{route['hub']}` | {route['artifacts']} | "
            f"{route['indexed_artifacts']} | {route['missing_files']} | "
            f"{'yes' if route['registry_reaches_hub'] else 'no'} |"
        )

    lines.extend([
        "",
        "## Findings",
        "",
        "| Severity | Category | File | Finding |",
        "|---|---|---|---|",
    ])
    for item in findings:
        message = str(item["message"]).replace("|", "\\|").replace("\n", " ")
        lines.append(f"| {item['severity']} | {item['category']} | `{item['path']}` | {message} |")

    lines.extend([
        "",
        "## Interpretation",
        "",
        "- Errors block a published route from being considered navigationally complete.",
        "- Warnings identify draft/future orphan debt or Markdown sinks requiring review.",
        "- Route coverage is based on `.github/knowledge-routes.json`.",
        "- Canvas file nodes count as directed navigation edges.",
        "",
    ])
    (OUTPUT_DIR / "cross-link-report.md").write_text("\n".join(lines), encoding="utf-8")

    print(json.dumps(report["summary"], ensure_ascii=False, indent=2))
    return 1 if severity_counts.get("error", 0) else 0


if __name__ == "__main__":
    raise SystemExit(main())
