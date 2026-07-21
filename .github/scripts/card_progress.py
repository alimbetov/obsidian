#!/usr/bin/env python3
"""Per-card progress registry for Markdown certification batches.

The content model may keep many cards in one Markdown file. This script extracts
stable card IDs, validates a sparse learner progress registry, schedules reviews,
and generates a static queue that works without Obsidian community plugins.
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from dataclasses import dataclass
from datetime import date, datetime, timedelta, timezone
from pathlib import Path
from typing import Dict, Iterable, List, Optional, Sequence, Tuple

CARD_HEADING_RE = re.compile(
    r"^#{1,2}\s+(?P<id>[A-Z0-9]+(?:-[A-Z0-9]+)*-B\d+-C\d+)\s*(?:—|-)?\s*(?P<title>.*)$",
    re.MULTILINE,
)
BATCH_RE = re.compile(r"^(?:batch|batch_id):\s*(?P<batch>[^\s]+)\s*$", re.MULTILINE)
OUTCOMES = {
    "correct-confident": 5,
    "correct-guessed": 3,
    "wrong-attention": 2,
    "wrong-confusion": 1,
    "wrong-concept": 0,
}
VALID_STATES = {"new", "learning", "review", "relearning", "suspended"}
EXCLUDED_DIRS = {".git", ".audit", ".obsidian", "node_modules", "target"}


@dataclass(frozen=True)
class Card:
    card_id: str
    title: str
    source_path: str
    batch_id: Optional[str]
    domain: str


def utc_now() -> datetime:
    return datetime.now(timezone.utc).replace(microsecond=0)


def today() -> date:
    return datetime.now(timezone.utc).date()


def default_progress() -> dict:
    return {
        "state": "new",
        "attempts": 0,
        "correct_attempts": 0,
        "repetitions": 0,
        "lapses": 0,
        "ease_factor": 2.5,
        "interval_days": 0,
        "confidence": 0,
        "last_outcome": None,
        "last_answered": None,
        "next_review": None,
        "suspended": False,
        "history": [],
    }


def infer_domain(path: Path) -> str:
    parts = path.parts
    if "Spring" in parts:
        return "spring"
    if "Java" in parts:
        return "java"
    if "Databases" in parts:
        return "databases"
    return parts[0].lower() if parts else "unknown"


def included(path: Path, root: Path) -> bool:
    try:
        parts = path.relative_to(root).parts
    except ValueError:
        return False
    return path.is_file() and path.suffix.lower() == ".md" and not any(part in EXCLUDED_DIRS for part in parts)


def scan_catalog(root: Path) -> Dict[str, Card]:
    cards: Dict[str, Card] = {}
    duplicates: Dict[str, List[str]] = {}

    for path in sorted(root.rglob("*.md")):
        if not included(path, root):
            continue
        text = path.read_text(encoding="utf-8")
        batch_match = BATCH_RE.search(text)
        batch_id = batch_match.group("batch") if batch_match else None
        relative = path.relative_to(root).as_posix()
        for match in CARD_HEADING_RE.finditer(text):
            card_id = match.group("id")
            title = match.group("title").strip() or card_id
            card = Card(card_id, title, relative, batch_id, infer_domain(path.relative_to(root)))
            if card_id in cards:
                duplicates.setdefault(card_id, [cards[card_id].source_path]).append(relative)
            else:
                cards[card_id] = card

    if duplicates:
        details = "; ".join(f"{card_id}: {paths}" for card_id, paths in sorted(duplicates.items()))
        raise ValueError(f"Duplicate card IDs: {details}")
    return cards


def load_progress(path: Path) -> dict:
    if not path.exists():
        raise FileNotFoundError(f"Progress file does not exist: {path}")
    data = json.loads(path.read_text(encoding="utf-8"))
    if data.get("schema_version") != 1:
        raise ValueError("Unsupported progress schema_version")
    if not isinstance(data.get("learner_id"), str) or not data["learner_id"].strip():
        raise ValueError("learner_id must be a non-empty string")
    if not isinstance(data.get("cards"), dict):
        raise ValueError("cards must be an object")
    return data


def write_progress(path: Path, data: dict) -> None:
    data["updated_at"] = utc_now().isoformat().replace("+00:00", "Z")
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def validate_record(card_id: str, record: dict) -> List[str]:
    errors: List[str] = []
    required = set(default_progress())
    missing = sorted(required - set(record))
    extra = sorted(set(record) - required)
    if missing:
        errors.append(f"{card_id}: missing fields {missing}")
    if extra:
        errors.append(f"{card_id}: unknown fields {extra}")
    if record.get("state") not in VALID_STATES:
        errors.append(f"{card_id}: invalid state {record.get('state')!r}")
    for name in ("attempts", "correct_attempts", "repetitions", "lapses", "interval_days", "confidence"):
        value = record.get(name)
        if not isinstance(value, int) or value < 0:
            errors.append(f"{card_id}: {name} must be a non-negative integer")
    if isinstance(record.get("confidence"), int) and record["confidence"] > 5:
        errors.append(f"{card_id}: confidence must be <= 5")
    ease = record.get("ease_factor")
    if not isinstance(ease, (int, float)) or not 1.3 <= float(ease) <= 3.5:
        errors.append(f"{card_id}: ease_factor must be between 1.3 and 3.5")
    outcome = record.get("last_outcome")
    if outcome is not None and outcome not in OUTCOMES:
        errors.append(f"{card_id}: invalid last_outcome {outcome!r}")
    if not isinstance(record.get("suspended"), bool):
        errors.append(f"{card_id}: suspended must be boolean")
    if not isinstance(record.get("history"), list):
        errors.append(f"{card_id}: history must be a list")
    if record.get("correct_attempts", 0) > record.get("attempts", 0):
        errors.append(f"{card_id}: correct_attempts cannot exceed attempts")
    return errors


def validate_progress(progress: dict, catalog: Dict[str, Card]) -> List[str]:
    errors: List[str] = []
    for card_id, record in sorted(progress["cards"].items()):
        if card_id not in catalog:
            errors.append(f"Progress references unknown card: {card_id}")
        if not isinstance(record, dict):
            errors.append(f"{card_id}: progress record must be an object")
            continue
        errors.extend(validate_record(card_id, record))
    return errors


def schedule(record: dict, outcome: str, answered_on: date) -> dict:
    if outcome not in OUTCOMES:
        raise ValueError(f"Unsupported outcome: {outcome}")

    quality = OUTCOMES[outcome]
    previous_interval = int(record.get("interval_days", 0))
    repetitions = int(record.get("repetitions", 0))
    ease = float(record.get("ease_factor", 2.5))

    ease = max(1.3, min(3.5, ease + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02))))

    if quality < 3:
        repetitions = 0
        interval = 1
        state = "relearning"
    else:
        repetitions += 1
        if repetitions == 1:
            interval = 1
            state = "learning"
        elif repetitions == 2:
            interval = 6
            state = "review"
        else:
            interval = max(1, int(round(max(previous_interval, 1) * ease)))
            state = "review"

        if outcome == "correct-guessed":
            interval = min(interval, 3 if repetitions <= 2 else 7)
            state = "learning" if repetitions <= 2 else "review"

    return {
        "quality": quality,
        "ease_factor": round(ease, 2),
        "repetitions": repetitions,
        "interval_days": interval,
        "state": state,
        "next_review": (answered_on + timedelta(days=interval)).isoformat(),
    }


def record_answer(
    progress: dict,
    catalog: Dict[str, Card],
    card_id: str,
    outcome: str,
    confidence: int,
    elapsed_seconds: Optional[int],
    note: Optional[str],
    answered_at: datetime,
) -> dict:
    if card_id not in catalog:
        raise KeyError(f"Unknown card ID: {card_id}")
    if outcome not in OUTCOMES:
        raise ValueError(f"Unknown outcome: {outcome}")
    if not 0 <= confidence <= 5:
        raise ValueError("confidence must be between 0 and 5")
    if elapsed_seconds is not None and elapsed_seconds < 0:
        raise ValueError("elapsed_seconds must be non-negative")

    record = progress["cards"].setdefault(card_id, default_progress())
    before = int(record["interval_days"])
    result = schedule(record, outcome, answered_at.date())

    record["attempts"] += 1
    if outcome.startswith("correct-"):
        record["correct_attempts"] += 1
    else:
        record["lapses"] += 1
    record["repetitions"] = result["repetitions"]
    record["ease_factor"] = result["ease_factor"]
    record["interval_days"] = result["interval_days"]
    record["state"] = result["state"]
    record["confidence"] = confidence
    record["last_outcome"] = outcome
    record["last_answered"] = answered_at.date().isoformat()
    record["next_review"] = result["next_review"]
    record["suspended"] = False
    record["history"].append({
        "answered_at": answered_at.astimezone(timezone.utc).replace(microsecond=0).isoformat().replace("+00:00", "Z"),
        "outcome": outcome,
        "confidence": confidence,
        "elapsed_seconds": elapsed_seconds,
        "interval_before": before,
        "interval_after": result["interval_days"],
        "note": note,
    })
    return record


def due_cards(catalog: Dict[str, Card], progress: dict, on_date: date) -> List[Tuple[int, Card, dict]]:
    rows: List[Tuple[int, Card, dict]] = []
    for card_id, card in catalog.items():
        record = progress["cards"].get(card_id, default_progress())
        if record.get("suspended") or record.get("state") == "suspended":
            continue
        next_review = record.get("next_review")
        if record.get("state") == "new" or next_review is None:
            priority = 2
        else:
            review_date = date.fromisoformat(next_review)
            if review_date > on_date:
                continue
            overdue = (on_date - review_date).days
            priority = 0 if overdue > 0 else 1
        rows.append((priority, card, record))
    rows.sort(key=lambda row: (
        row[0],
        row[2].get("next_review") or "9999-12-31",
        row[2].get("confidence", 0),
        row[1].card_id,
    ))
    return rows


def catalog_json(catalog: Dict[str, Card]) -> dict:
    return {
        "schema_version": 1,
        "generated_at": utc_now().isoformat().replace("+00:00", "Z"),
        "card_count": len(catalog),
        "cards": [
            {
                "card_id": card.card_id,
                "title": card.title,
                "source_path": card.source_path,
                "batch_id": card.batch_id,
                "domain": card.domain,
            }
            for card in sorted(catalog.values(), key=lambda item: item.card_id)
        ],
    }


def write_queue(path: Path, catalog: Dict[str, Card], progress: dict, on_date: date, limit: int = 100) -> None:
    due = due_cards(catalog, progress, on_date)
    reviewed = sum(1 for card_id in catalog if progress["cards"].get(card_id, {}).get("attempts", 0) > 0)
    lines = [
        "# Card Review Queue",
        "",
        f"Generated: **{utc_now().isoformat().replace('+00:00', 'Z')}**",
        "",
        "## Summary",
        "",
        f"- Catalog cards: **{len(catalog)}**",
        f"- Cards with attempts: **{reviewed}**",
        f"- Due or new cards: **{len(due)}**",
        "",
        "## Queue",
        "",
        "| Priority | Card | State | Confidence | Next review | Source |",
        "|---|---|---|---:|---|---|",
    ]
    for priority, card, record in due[:limit]:
        label = {0: "overdue", 1: "due", 2: "new"}[priority]
        source = card.source_path.removesuffix(".md")
        lines.append(
            f"| {label} | `{card.card_id}` — {card.title} | {record['state']} | "
            f"{record['confidence']} | {record.get('next_review') or ''} | [[{source}]] |"
        )
    if not due:
        lines.append("| — | No cards are due | — | — | — | — |")
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text("\n".join(lines) + "\n", encoding="utf-8")


def command_audit(args: argparse.Namespace) -> int:
    root = Path(args.root).resolve()
    catalog = scan_catalog(root)
    progress = load_progress(Path(args.progress))
    errors = validate_progress(progress, catalog)

    catalog_output = Path(args.catalog_output)
    catalog_output.parent.mkdir(parents=True, exist_ok=True)
    catalog_output.write_text(json.dumps(catalog_json(catalog), ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    write_queue(Path(args.queue_output), catalog, progress, date.fromisoformat(args.on_date) if args.on_date else today())

    summary = {
        "catalog_cards": len(catalog),
        "progress_records": len(progress["cards"]),
        "cards_with_attempts": sum(1 for record in progress["cards"].values() if record.get("attempts", 0) > 0),
        "errors": len(errors),
    }
    print(json.dumps(summary, ensure_ascii=False, indent=2))
    for error in errors:
        print(f"ERROR: {error}", file=sys.stderr)
    return 1 if errors else 0


def command_sync(args: argparse.Namespace) -> int:
    root = Path(args.root).resolve()
    catalog = scan_catalog(root)
    progress_path = Path(args.progress)
    progress = load_progress(progress_path)
    errors = validate_progress(progress, catalog)
    if errors:
        raise ValueError("Cannot sync invalid progress: " + "; ".join(errors))
    added = 0
    for card_id in sorted(catalog):
        if card_id not in progress["cards"]:
            progress["cards"][card_id] = default_progress()
            added += 1
    write_progress(progress_path, progress)
    print(json.dumps({"catalog_cards": len(catalog), "added": added, "total_records": len(progress["cards"])}, indent=2))
    return 0


def command_record(args: argparse.Namespace) -> int:
    root = Path(args.root).resolve()
    catalog = scan_catalog(root)
    progress_path = Path(args.progress)
    progress = load_progress(progress_path)
    record = record_answer(
        progress,
        catalog,
        args.card_id,
        args.outcome,
        args.confidence,
        args.elapsed_seconds,
        args.note,
        datetime.fromisoformat(args.answered_at.replace("Z", "+00:00")) if args.answered_at else utc_now(),
    )
    write_progress(progress_path, progress)
    print(json.dumps({"card_id": args.card_id, "progress": record}, ensure_ascii=False, indent=2))
    return 0


def command_due(args: argparse.Namespace) -> int:
    root = Path(args.root).resolve()
    catalog = scan_catalog(root)
    progress = load_progress(Path(args.progress))
    errors = validate_progress(progress, catalog)
    if errors:
        raise ValueError("Invalid progress: " + "; ".join(errors))
    due = due_cards(catalog, progress, date.fromisoformat(args.on_date) if args.on_date else today())
    for priority, card, record in due[: args.limit]:
        label = {0: "OVERDUE", 1: "DUE", 2: "NEW"}[priority]
        print(f"{label}\t{card.card_id}\t{record['state']}\t{record.get('next_review') or '-'}\t{card.title}")
    return 0


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description=__doc__)
    subparsers = parser.add_subparsers(dest="command", required=True)

    def common(command: argparse.ArgumentParser) -> None:
        command.add_argument("--root", default=".")
        command.add_argument("--progress", default="70_PROGRESS/card-progress.json")

    audit = subparsers.add_parser("audit")
    common(audit)
    audit.add_argument("--catalog-output", default=".audit/card-catalog.json")
    audit.add_argument("--queue-output", default=".audit/card-review-queue.md")
    audit.add_argument("--on-date")
    audit.set_defaults(func=command_audit)

    sync = subparsers.add_parser("sync")
    common(sync)
    sync.set_defaults(func=command_sync)

    record = subparsers.add_parser("record")
    common(record)
    record.add_argument("--card-id", required=True)
    record.add_argument("--outcome", required=True, choices=sorted(OUTCOMES))
    record.add_argument("--confidence", required=True, type=int)
    record.add_argument("--elapsed-seconds", type=int)
    record.add_argument("--note")
    record.add_argument("--answered-at", help="ISO-8601 datetime; defaults to current UTC")
    record.set_defaults(func=command_record)

    due = subparsers.add_parser("due")
    common(due)
    due.add_argument("--limit", type=int, default=30)
    due.add_argument("--on-date")
    due.set_defaults(func=command_due)
    return parser


def main() -> int:
    parser = build_parser()
    args = parser.parse_args()
    try:
        return args.func(args)
    except (ValueError, KeyError, FileNotFoundError, json.JSONDecodeError) as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
