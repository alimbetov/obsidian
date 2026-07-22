#!/usr/bin/env python3
"""Validate certification objective -> learning evidence traceability.

Objective manifests are deliberately paraphrased taxonomies backed by official
source references. Each objective records concrete repository evidence: notes,
diagrams, cards, cases, labs, sources and mocks. The audit rejects missing paths,
unknown card IDs, impossible status claims and duplicate objective IDs.
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from collections import Counter
from pathlib import Path
from typing import Dict, Iterable, List, Sequence, Set

CARD_HEADING_RE = re.compile(
    r"^#{1,2}\s+(?P<id>[A-Z0-9]+(?:-[A-Z0-9]+)*-B\d+-C\d+)\b",
    re.MULTILINE,
)
EXCLUDED_DIRS = {".git", ".audit", ".obsidian", "node_modules", "target"}
STATUS_SCORE = {
    "unmapped": 0.0,
    "theory-only": 0.25,
    "theory-visual": 0.40,
    "cards-ready": 0.60,
    "lab-proven": 0.80,
    "mock-covered": 0.95,
    "complete": 1.00,
}
STATUS_REQUIREMENTS = {
    "unmapped": set(),
    "theory-only": {"canonical"},
    "theory-visual": {"canonical", "visual"},
    "cards-ready": {"canonical", "cards"},
    "lab-proven": {"canonical", "cards", "labs"},
    "mock-covered": {"canonical", "cards", "mocks"},
    "complete": {"canonical", "cards", "sources"},
}
PATH_ROLES = {"canonical", "visual", "cases", "labs", "sources", "mocks", "roadmaps", "canvas"}
CARD_ROLES = {"cards", "card_prefixes"}


def included(path: Path, root: Path) -> bool:
    try:
        parts = path.relative_to(root).parts
    except ValueError:
        return False
    return path.is_file() and not any(part in EXCLUDED_DIRS for part in parts)


def scan_card_ids(root: Path) -> Set[str]:
    ids: Set[str] = set()
    duplicates: Set[str] = set()
    for path in root.rglob("*.md"):
        if not included(path, root):
            continue
        text = path.read_text(encoding="utf-8")
        for match in CARD_HEADING_RE.finditer(text):
            card_id = match.group("id")
            if card_id in ids:
                duplicates.add(card_id)
            ids.add(card_id)
    if duplicates:
        raise ValueError(f"Duplicate card IDs: {sorted(duplicates)}")
    return ids


def load_manifest(path: Path) -> dict:
    data = json.loads(path.read_text(encoding="utf-8"))
    if data.get("schema_version") != 1:
        raise ValueError(f"{path}: unsupported schema_version")
    if not isinstance(data.get("track_id"), str) or not data["track_id"]:
        raise ValueError(f"{path}: track_id is required")
    if not isinstance(data.get("objectives"), list):
        raise ValueError(f"{path}: objectives must be a list")
    return data


def path_exists(root: Path, relative: str) -> bool:
    return (root / relative).is_file()


def validate_objective(root: Path, objective: dict, card_ids: Set[str]) -> List[str]:
    errors: List[str] = []
    objective_id = objective.get("id", "<missing-id>")
    status = objective.get("status")
    if status not in STATUS_SCORE:
        errors.append(f"{objective_id}: invalid status {status!r}")
        return errors
    weight = objective.get("weight", 1)
    if not isinstance(weight, (int, float)) or weight <= 0:
        errors.append(f"{objective_id}: weight must be positive")
    evidence = objective.get("evidence", {})
    if not isinstance(evidence, dict):
        errors.append(f"{objective_id}: evidence must be an object")
        return errors

    unknown_roles = sorted(set(evidence) - PATH_ROLES - CARD_ROLES)
    if unknown_roles:
        errors.append(f"{objective_id}: unknown evidence roles {unknown_roles}")

    present_roles: Set[str] = set()
    for role in PATH_ROLES:
        values = evidence.get(role, [])
        if values is None:
            values = []
        if not isinstance(values, list) or not all(isinstance(value, str) for value in values):
            errors.append(f"{objective_id}: evidence.{role} must be a string list")
            continue
        if values:
            present_roles.add(role)
        for value in values:
            if not path_exists(root, value):
                errors.append(f"{objective_id}: missing {role} path {value}")

    exact_cards = evidence.get("cards", []) or []
    prefixes = evidence.get("card_prefixes", []) or []
    if not isinstance(exact_cards, list) or not all(isinstance(value, str) for value in exact_cards):
        errors.append(f"{objective_id}: evidence.cards must be a string list")
        exact_cards = []
    if not isinstance(prefixes, list) or not all(isinstance(value, str) for value in prefixes):
        errors.append(f"{objective_id}: evidence.card_prefixes must be a string list")
        prefixes = []

    matched_cards: Set[str] = set()
    for card_id in exact_cards:
        if card_id not in card_ids:
            errors.append(f"{objective_id}: unknown card ID {card_id}")
        else:
            matched_cards.add(card_id)
    for prefix in prefixes:
        matches = {card_id for card_id in card_ids if card_id.startswith(prefix)}
        if not matches:
            errors.append(f"{objective_id}: card prefix matches no cards: {prefix}")
        matched_cards.update(matches)
    if matched_cards:
        present_roles.add("cards")

    missing_requirements = STATUS_REQUIREMENTS[status] - present_roles
    if missing_requirements:
        errors.append(
            f"{objective_id}: status {status} requires evidence roles {sorted(missing_requirements)}"
        )

    if status == "complete" and not ({"visual", "cases", "labs"} & present_roles):
        errors.append(f"{objective_id}: complete objective requires visual, case or lab transfer evidence")

    objective["_matched_cards"] = len(matched_cards)
    objective["_present_roles"] = sorted(present_roles)
    return errors


def audit_manifest(root: Path, path: Path, card_ids: Set[str]) -> dict:
    data = load_manifest(path)
    errors: List[str] = []
    objective_ids: Set[str] = set()
    duplicate_ids: Set[str] = set()
    weighted_score = 0.0
    total_weight = 0.0
    status_counts = Counter()
    objective_reports = []

    for objective in data["objectives"]:
        if not isinstance(objective, dict):
            errors.append("objective entry must be an object")
            continue
        objective_id = objective.get("id")
        if not isinstance(objective_id, str) or not objective_id:
            errors.append("objective without a valid id")
            continue
        if objective_id in objective_ids:
            duplicate_ids.add(objective_id)
        objective_ids.add(objective_id)
        errors.extend(validate_objective(root, objective, card_ids))
        status = objective.get("status", "unmapped")
        weight = float(objective.get("weight", 1)) if isinstance(objective.get("weight", 1), (int, float)) else 1.0
        total_weight += weight
        weighted_score += STATUS_SCORE.get(status, 0.0) * weight
        status_counts[status] += 1
        objective_reports.append({
            "id": objective_id,
            "title": objective.get("title", objective_id),
            "section": objective.get("section"),
            "status": status,
            "weight": weight,
            "matched_cards": objective.get("_matched_cards", 0),
            "evidence_roles": objective.get("_present_roles", []),
        })

    if duplicate_ids:
        errors.append(f"duplicate objective IDs: {sorted(duplicate_ids)}")

    score = 0.0 if total_weight == 0 else round(100.0 * weighted_score / total_weight, 2)
    return {
        "manifest": path.relative_to(root).as_posix(),
        "track_id": data["track_id"],
        "title": data.get("title", data["track_id"]),
        "source": data.get("source"),
        "verified_at": data.get("verified_at"),
        "objective_count": len(objective_reports),
        "traceability_score": score,
        "status_counts": dict(status_counts),
        "errors": errors,
        "objectives": objective_reports,
    }


def write_markdown(path: Path, reports: Sequence[dict]) -> None:
    lines = [
        "# Objective Traceability Audit",
        "",
        "| Track | Objectives | Traceability score | Errors |",
        "|---|---:|---:|---:|",
    ]
    for report in reports:
        lines.append(
            f"| {report['title']} | {report['objective_count']} | "
            f"{report['traceability_score']:.2f}% | {len(report['errors'])} |"
        )

    for report in reports:
        lines.extend([
            "",
            f"## {report['title']}",
            "",
            f"- Manifest: `{report['manifest']}`",
            f"- Source: {report.get('source') or 'not declared'}",
            f"- Verified: {report.get('verified_at') or 'not declared'}",
            f"- Traceability score: **{report['traceability_score']:.2f}%**",
            "",
            "| Objective | Status | Cards | Evidence roles |",
            "|---|---|---:|---|",
        ])
        for objective in report["objectives"]:
            roles = ", ".join(objective["evidence_roles"])
            lines.append(
                f"| `{objective['id']}` — {objective['title']} | {objective['status']} | "
                f"{objective['matched_cards']} | {roles} |"
            )
        if report["errors"]:
            lines.extend(["", "### Errors", ""])
            lines.extend(f"- {error}" for error in report["errors"])

    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text("\n".join(lines) + "\n", encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", default=".")
    parser.add_argument("--manifest-dir", default=".github/objectives")
    parser.add_argument("--json-output", default=".audit/objective-traceability.json")
    parser.add_argument("--markdown-output", default=".audit/objective-traceability.md")
    args = parser.parse_args()

    root = Path(args.root).resolve()
    manifest_dir = root / args.manifest_dir
    card_ids = scan_card_ids(root)
    manifest_paths = sorted(manifest_dir.glob("*.json"))
    if not manifest_paths:
        print(f"ERROR: no objective manifests in {manifest_dir}", file=sys.stderr)
        return 2

    reports = []
    for path in manifest_paths:
        try:
            reports.append(audit_manifest(root, path, card_ids))
        except (ValueError, json.JSONDecodeError) as exc:
            reports.append({
                "manifest": path.relative_to(root).as_posix(),
                "track_id": path.stem,
                "title": path.stem,
                "source": None,
                "verified_at": None,
                "objective_count": 0,
                "traceability_score": 0.0,
                "status_counts": {},
                "errors": [str(exc)],
                "objectives": [],
            })

    total_errors = sum(len(report["errors"]) for report in reports)
    output = {
        "summary": {
            "tracks": len(reports),
            "objectives": sum(report["objective_count"] for report in reports),
            "errors": total_errors,
        },
        "tracks": reports,
    }
    json_path = root / args.json_output
    json_path.parent.mkdir(parents=True, exist_ok=True)
    json_path.write_text(json.dumps(output, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    write_markdown(root / args.markdown_output, reports)

    print(json.dumps(output["summary"], ensure_ascii=False, indent=2))
    for report in reports:
        print(f"{report['track_id']}: {report['traceability_score']:.2f}% ({len(report['errors'])} errors)")
    return 1 if total_errors else 0


if __name__ == "__main__":
    raise SystemExit(main())
