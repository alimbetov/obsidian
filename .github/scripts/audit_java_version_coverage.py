#!/usr/bin/env python3
"""Audit the Java 11/17/21 cumulative knowledge program.

The audit validates the domain/version contract and computes honest coverage
scores. Low coverage is reported but does not fail CI. Invalid metadata,
missing required domains or missing declared evidence fail CI.
"""

from __future__ import annotations

import json
from collections import Counter
from pathlib import Path
from typing import Any

ROOT = Path(__file__).resolve().parents[2]
MANIFEST = ROOT / ".github/java-version-coverage.json"
OUTPUT_DIR = ROOT / ".audit"
REQUIRED_VERSIONS = ("11", "17", "21")


def add_finding(findings: list[dict[str, Any]], severity: str, category: str, path: str, message: str) -> None:
    findings.append({
        "severity": severity,
        "category": category,
        "path": path,
        "message": message,
    })


def file_exists(path: str | None) -> bool:
    return bool(path) and (ROOT / path).is_file()


def main() -> int:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    findings: list[dict[str, Any]] = []

    try:
        manifest = json.loads(MANIFEST.read_text(encoding="utf-8"))
    except FileNotFoundError:
        manifest = {}
        add_finding(findings, "error", "manifest", str(MANIFEST.relative_to(ROOT)), "Manifest is missing")
    except json.JSONDecodeError as exc:
        manifest = {}
        add_finding(findings, "error", "manifest-json", str(MANIFEST.relative_to(ROOT)), str(exc))

    for field in ("roadmap", "sources"):
        path = manifest.get(field)
        if not file_exists(path):
            add_finding(findings, "error", field, str(path), f"Declared {field} file is missing")

    versions = manifest.get("versions", {})
    if set(versions) != set(REQUIRED_VERSIONS):
        add_finding(
            findings,
            "error",
            "versions",
            ".github/java-version-coverage.json",
            f"Versions must be exactly {list(REQUIRED_VERSIONS)}, found {sorted(versions)}",
        )

    status_scores = manifest.get("status_scores", {})
    valid_statuses = set(status_scores)
    if not valid_statuses:
        add_finding(findings, "error", "status-scores", ".github/java-version-coverage.json", "No status scores declared")

    required_ids = manifest.get("required_domain_ids", [])
    domains = manifest.get("domains", [])
    actual_ids = [domain.get("id") for domain in domains]

    duplicates = [domain_id for domain_id, count in Counter(actual_ids).items() if domain_id and count > 1]
    if duplicates:
        add_finding(findings, "error", "domain-id", ".github/java-version-coverage.json", f"Duplicate domain IDs: {duplicates}")

    missing_ids = sorted(set(required_ids) - set(actual_ids))
    extra_ids = sorted(set(actual_ids) - set(required_ids))
    if missing_ids:
        add_finding(findings, "error", "domain-id", ".github/java-version-coverage.json", f"Missing required domains: {missing_ids}")
    if extra_ids:
        add_finding(findings, "error", "domain-id", ".github/java-version-coverage.json", f"Unexpected domains: {extra_ids}")

    shared_scores: list[float] = []
    version_scores: dict[str, list[float]] = {version: [] for version in REQUIRED_VERSIONS}
    domain_reports: list[dict[str, Any]] = []

    for domain in domains:
        domain_id = str(domain.get("id", "UNKNOWN"))
        title = str(domain.get("title", domain_id))
        shared_status = domain.get("shared_status")
        if shared_status not in valid_statuses:
            add_finding(findings, "error", "status", domain_id, f"Invalid shared_status: {shared_status}")
            shared_score = 0.0
        else:
            shared_score = float(status_scores[shared_status])
        shared_scores.append(shared_score)

        per_version: dict[str, dict[str, Any]] = {}
        statuses = domain.get("version_status", {})
        topics = domain.get("version_topics", {})
        for version in REQUIRED_VERSIONS:
            status = statuses.get(version)
            topic_list = topics.get(version)
            if status not in valid_statuses:
                add_finding(findings, "error", "status", domain_id, f"Java {version} has invalid status: {status}")
                score = 0.0
            else:
                score = float(status_scores[status])
            if not isinstance(topic_list, list) or not topic_list or not all(isinstance(item, str) and item.strip() for item in topic_list):
                add_finding(findings, "error", "version-topics", domain_id, f"Java {version} must have non-empty topic strings")
            version_scores[version].append(score)
            per_version[version] = {
                "status": status,
                "score": score,
                "topic_count": len(topic_list) if isinstance(topic_list, list) else 0,
            }

        evidence = domain.get("evidence", [])
        missing_evidence = []
        for path in evidence:
            if not file_exists(path):
                missing_evidence.append(path)
                add_finding(findings, "error", "evidence", path, f"{domain_id}: declared evidence file is missing")

        if shared_status in {"cards-ready", "lab-proven", "mock-covered", "complete"} and not evidence:
            add_finding(findings, "error", "evidence", domain_id, f"Status {shared_status} requires declared evidence")

        domain_reports.append({
            "id": domain_id,
            "title": title,
            "shared_status": shared_status,
            "shared_score": shared_score,
            "versions": per_version,
            "evidence_count": len(evidence),
            "missing_evidence": missing_evidence,
        })

    def average(values: list[float]) -> float:
        return round(sum(values) / len(values), 2) if values else 0.0

    shared_coverage = average(shared_scores)
    per_version_coverage = {version: average(scores) for version, scores in version_scores.items()}
    overall = round(
        0.25 * shared_coverage
        + 0.25 * per_version_coverage["11"]
        + 0.25 * per_version_coverage["17"]
        + 0.25 * per_version_coverage["21"],
        2,
    )

    findings.sort(key=lambda item: (
        {"error": 0, "warning": 1, "info": 2}.get(item["severity"], 9),
        item["category"],
        item["path"],
    ))
    counts = Counter(item["severity"] for item in findings)

    report = {
        "summary": {
            "program_id": manifest.get("program_id"),
            "domains": len(domains),
            "overall_coverage": overall,
            "shared_coverage": shared_coverage,
            "java_11_coverage": per_version_coverage["11"],
            "java_17_coverage": per_version_coverage["17"],
            "java_21_coverage": per_version_coverage["21"],
            "errors": counts.get("error", 0),
            "warnings": counts.get("warning", 0),
        },
        "domains": domain_reports,
        "findings": findings,
    }

    (OUTPUT_DIR / "java-version-coverage.json").write_text(
        json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8"
    )

    lines = [
        "# Java 11, 17 and 21 Knowledge Coverage",
        "",
        "## Summary",
        "",
        f"- Domains: **{len(domains)}**",
        f"- Overall coverage: **{overall:.2f}%**",
        f"- Shared-domain coverage: **{shared_coverage:.2f}%**",
        f"- Java 11 coverage: **{per_version_coverage['11']:.2f}%**",
        f"- Java 17 coverage: **{per_version_coverage['17']:.2f}%**",
        f"- Java 21 coverage: **{per_version_coverage['21']:.2f}%**",
        f"- Integrity errors: **{counts.get('error', 0)}**",
        "",
        "## Domain matrix",
        "",
        "| Domain | Shared | Java 11 | Java 17 | Java 21 | Evidence |",
        "|---|---:|---:|---:|---:|---:|",
    ]
    for domain in domain_reports:
        lines.append(
            f"| {domain['id']} — {domain['title']} | {domain['shared_score']:.0f}% | "
            f"{domain['versions']['11']['score']:.0f}% | {domain['versions']['17']['score']:.0f}% | "
            f"{domain['versions']['21']['score']:.0f}% | {domain['evidence_count']} |"
        )

    lines.extend([
        "",
        "## Integrity findings",
        "",
        "| Severity | Category | Path | Finding |",
        "|---|---|---|---|",
    ])
    for item in findings:
        message = str(item["message"]).replace("|", "\\|")
        lines.append(f"| {item['severity']} | {item['category']} | `{item['path']}` | {message} |")

    lines.extend([
        "",
        "> Coverage measures repository evidence. It is not a probability of passing an exam or completing a production migration.",
        "",
    ])
    (OUTPUT_DIR / "java-version-coverage.md").write_text("\n".join(lines), encoding="utf-8")

    print(json.dumps(report["summary"], ensure_ascii=False, indent=2))
    return 1 if counts.get("error", 0) else 0


if __name__ == "__main__":
    raise SystemExit(main())
