#!/usr/bin/env python3
"""Calculate certification material readiness from objective and artifact evidence.

Readiness is intentionally conservative:
- 25% objective traceability from `.audit/objective-traceability.json`;
- 75% vertical-slice artifact/card completeness from the readiness manifest.

Low readiness is reported, not treated as a CI error. Invalid or dishonest
metadata remains a blocking error.
"""

from __future__ import annotations

import json
from collections import Counter
from pathlib import Path
from typing import Dict, List

ROOT = Path(__file__).resolve().parents[2]
MANIFEST = ROOT / ".github/certification-readiness.json"
OBJECTIVE_REPORT = ROOT / ".audit/objective-traceability.json"
OUTPUT_DIR = ROOT / ".audit"
OBJECTIVE_WEIGHT = 0.25
VERTICAL_WEIGHT = 0.75
STATUS_CAP = {
    "published": 100.0,
    "partial": 85.0,
    "planned": 0.0,
}


def exists(path: str | None) -> bool:
    return bool(path) and (ROOT / path).is_file()


def add_finding(findings: List[dict], severity: str, category: str, path: str, message: str) -> None:
    findings.append({
        "severity": severity,
        "category": category,
        "path": path,
        "message": message,
    })


def domain_score(domain: dict) -> tuple[float, dict]:
    required_roles = domain.get("required_roles", [])
    artifacts: Dict[str, List[str]] = domain.get("artifacts", {})

    role_results = {}
    for role in required_roles:
        declared = [path for path in artifacts.get(role, []) if isinstance(path, str)]
        present = [path for path in declared if exists(path)]
        role_results[role] = {
            "declared": len(declared),
            "present": len(present),
            "complete": bool(declared) and len(present) == len(declared),
        }

    complete_roles = sum(1 for result in role_results.values() if result["complete"])
    artifact_score = 0.0 if not required_roles else 70.0 * complete_roles / len(required_roles)

    target = int(domain.get("base_card_target", 0) or 0)
    mapped = int(domain.get("base_cards_mapped", 0) or 0)
    card_ratio = 1.0 if target == 0 else min(mapped / target, 1.0)
    card_score = 30.0 * card_ratio

    raw_score = artifact_score + card_score
    cap = STATUS_CAP.get(domain.get("status", "planned"), 0.0)
    score = min(raw_score, cap)

    return round(score, 2), {
        "artifact_score": round(artifact_score, 2),
        "card_score": round(card_score, 2),
        "status_cap": cap,
        "card_target": target,
        "cards_mapped": mapped,
        "roles": role_results,
    }


def load_objective_scores(findings: List[dict]) -> Dict[str, float]:
    if not OBJECTIVE_REPORT.is_file():
        add_finding(
            findings,
            "error",
            "objective-report",
            str(OBJECTIVE_REPORT.relative_to(ROOT)),
            "Objective traceability audit must run before readiness calculation",
        )
        return {}
    try:
        report = json.loads(OBJECTIVE_REPORT.read_text(encoding="utf-8"))
    except json.JSONDecodeError as exc:
        add_finding(findings, "error", "objective-report-json", str(OBJECTIVE_REPORT.relative_to(ROOT)), str(exc))
        return {}

    if report.get("summary", {}).get("errors", 0):
        add_finding(
            findings,
            "error",
            "objective-report",
            str(OBJECTIVE_REPORT.relative_to(ROOT)),
            "Objective traceability report contains errors",
        )

    scores: Dict[str, float] = {}
    for track in report.get("tracks", []):
        track_id = track.get("track_id")
        score = track.get("traceability_score")
        if isinstance(track_id, str) and isinstance(score, (int, float)):
            scores[track_id] = float(score)
    return scores


def main() -> int:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    findings: List[dict] = []

    if not MANIFEST.is_file():
        add_finding(findings, "error", "manifest", str(MANIFEST.relative_to(ROOT)), "Readiness manifest is missing")
        data = {"tracks": []}
    else:
        try:
            data = json.loads(MANIFEST.read_text(encoding="utf-8"))
        except json.JSONDecodeError as exc:
            add_finding(findings, "error", "manifest-json", str(MANIFEST.relative_to(ROOT)), str(exc))
            data = {"tracks": []}

    objective_scores = load_objective_scores(findings)

    dashboard = data.get("dashboard")
    if not exists(dashboard):
        add_finding(findings, "error", "dashboard", str(dashboard), "Readiness dashboard does not exist")

    track_reports = []
    for track in data.get("tracks", []):
        track_id = track.get("id", "UNKNOWN")
        roadmap = track.get("roadmap")
        if not exists(roadmap):
            add_finding(findings, "error", "roadmap", str(roadmap), f"{track_id}: master roadmap does not exist")

        if track_id not in objective_scores:
            add_finding(findings, "error", "objective-track", track_id, "No objective traceability score exists for track")

        domains = track.get("domains", [])
        total_weight = sum(int(domain.get("weight", 0) or 0) for domain in domains)
        if total_weight != 100:
            add_finding(findings, "error", "weights", track_id, f"Domain weights must total 100, found {total_weight}")

        vertical_score = 0.0
        domain_reports = []
        for domain in domains:
            domain_id = domain.get("id", "UNKNOWN")
            weight = int(domain.get("weight", 0) or 0)
            status = domain.get("status", "planned")
            if status not in STATUS_CAP:
                add_finding(findings, "error", "status", domain_id, f"Unknown status: {status}")

            mapped = int(domain.get("base_cards_mapped", 0) or 0)
            target = int(domain.get("base_card_target", 0) or 0)
            if mapped < 0 or target < 0 or mapped > target:
                add_finding(findings, "error", "card-count", domain_id, f"Invalid mapped/target card count: {mapped}/{target}")

            domain_roadmap = domain.get("roadmap")
            if domain_roadmap and not exists(domain_roadmap):
                add_finding(findings, "error", "domain-roadmap", str(domain_roadmap), f"{domain_id}: domain roadmap missing")

            for role, paths in domain.get("artifacts", {}).items():
                for path in paths:
                    if not exists(path):
                        add_finding(findings, "error", "artifact", path, f"{domain_id}: declared {role} artifact missing")

            score, details = domain_score(domain)
            if status == "published":
                for role in domain.get("required_roles", []):
                    role_details = details["roles"].get(role, {})
                    if not role_details.get("complete", False):
                        add_finding(findings, "error", "published-role", domain_id, f"Published domain missing complete role: {role}")

            vertical_score += score * weight / 100.0
            domain_reports.append({
                "id": domain_id,
                "status": status,
                "weight": weight,
                "score": score,
                **details,
            })

        objective_score = objective_scores.get(track_id, 0.0)
        readiness = round(OBJECTIVE_WEIGHT * objective_score + VERTICAL_WEIGHT * vertical_score, 2)
        target_readiness = float(track.get("target_material_readiness", 99))
        track_reports.append({
            "id": track_id,
            "title": track.get("title", track_id),
            "roadmap": roadmap,
            "material_readiness": readiness,
            "objective_traceability_score": round(objective_score, 2),
            "vertical_slice_score": round(vertical_score, 2),
            "formula": {
                "objective_weight": OBJECTIVE_WEIGHT,
                "vertical_weight": VERTICAL_WEIGHT,
            },
            "target_material_readiness": target_readiness,
            "remaining_to_target": round(max(target_readiness - readiness, 0.0), 2),
            "targets": track.get("targets", {}),
            "domains": domain_reports,
        })

    findings.sort(key=lambda item: (
        {"error": 0, "warning": 1, "info": 2}.get(item["severity"], 9),
        item["category"],
        item["path"],
    ))
    counts = Counter(item["severity"] for item in findings)

    report = {
        "summary": {
            "tracks": len(track_reports),
            "errors": counts.get("error", 0),
            "warnings": counts.get("warning", 0),
        },
        "tracks": track_reports,
        "findings": findings,
    }

    (OUTPUT_DIR / "certification-readiness.json").write_text(
        json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8"
    )

    lines = [
        "# Certification Material Readiness",
        "",
        "> Formula: 25% objective traceability + 75% vertical-slice artifact/card completeness.",
        "",
        "## Track summary",
        "",
        "| Track | Readiness | Objective traceability | Vertical slices | Target | Remaining |",
        "|---|---:|---:|---:|---:|---:|",
    ]
    for track in track_reports:
        lines.append(
            f"| {track['title']} | {track['material_readiness']:.2f}% | "
            f"{track['objective_traceability_score']:.2f}% | {track['vertical_slice_score']:.2f}% | "
            f"{track['target_material_readiness']:.2f}% | {track['remaining_to_target']:.2f}% |"
        )

    for track in track_reports:
        lines.extend([
            "",
            f"## {track['title']}",
            "",
            "| Domain | Status | Weight | Score | Cards | Artifact roles |",
            "|---|---|---:|---:|---:|---|",
        ])
        for domain in track["domains"]:
            completed_roles = sum(1 for role in domain["roles"].values() if role["complete"])
            total_roles = len(domain["roles"])
            lines.append(
                f"| {domain['id']} | {domain['status']} | {domain['weight']} | "
                f"{domain['score']:.2f}% | {domain['cards_mapped']}/{domain['card_target']} | "
                f"{completed_roles}/{total_roles} |"
            )

    lines.extend([
        "",
        "## Integrity findings",
        "",
        f"- Errors: **{counts.get('error', 0)}**",
        f"- Warnings: **{counts.get('warning', 0)}**",
        "",
        "| Severity | Category | Path | Finding |",
        "|---|---|---|---|",
    ])
    for item in findings:
        message = str(item["message"]).replace("|", "\\|")
        lines.append(f"| {item['severity']} | {item['category']} | `{item['path']}` | {message} |")

    lines.extend([
        "",
        "> Low readiness is not a CI failure. Invalid or dishonest readiness metadata is a CI failure.",
        "",
    ])
    (OUTPUT_DIR / "certification-readiness.md").write_text("\n".join(lines), encoding="utf-8")

    print(json.dumps(report["summary"], ensure_ascii=False, indent=2))
    for track in track_reports:
        print(
            f"{track['id']}: {track['material_readiness']:.2f}% "
            f"(objectives {track['objective_traceability_score']:.2f}%, "
            f"vertical {track['vertical_slice_score']:.2f}%)"
        )

    return 1 if counts.get("error", 0) else 0


if __name__ == "__main__":
    raise SystemExit(main())
