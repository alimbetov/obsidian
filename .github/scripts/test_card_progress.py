#!/usr/bin/env python3

import importlib.util
import sys
import tempfile
import unittest
from datetime import date, datetime, timezone
from pathlib import Path

SCRIPT = Path(__file__).with_name("card_progress.py")
SPEC = importlib.util.spec_from_file_location("card_progress", SCRIPT)
card_progress = importlib.util.module_from_spec(SPEC)
assert SPEC.loader is not None
sys.modules[SPEC.name] = card_progress
SPEC.loader.exec_module(card_progress)


class CardProgressTests(unittest.TestCase):

    def test_default_progress_is_new(self):
        record = card_progress.default_progress()
        self.assertEqual("new", record["state"])
        self.assertEqual(0, record["attempts"])
        self.assertEqual([], record["history"])

    def test_confident_success_uses_one_then_six_days(self):
        record = card_progress.default_progress()
        first = card_progress.schedule(record, "correct-confident", date(2026, 7, 22))
        self.assertEqual(1, first["interval_days"])
        self.assertEqual("2026-07-23", first["next_review"])

        record.update(first)
        second = card_progress.schedule(record, "correct-confident", date(2026, 7, 23))
        self.assertEqual(6, second["interval_days"])
        self.assertEqual("review", second["state"])

    def test_wrong_concept_resets_repetitions(self):
        record = card_progress.default_progress()
        record.update({"repetitions": 4, "interval_days": 20, "state": "review"})
        result = card_progress.schedule(record, "wrong-concept", date(2026, 7, 22))
        self.assertEqual(0, result["repetitions"])
        self.assertEqual(1, result["interval_days"])
        self.assertEqual("relearning", result["state"])

    def test_guessed_answer_does_not_get_long_interval(self):
        record = card_progress.default_progress()
        record.update({"repetitions": 2, "interval_days": 6, "state": "review"})
        result = card_progress.schedule(record, "correct-guessed", date(2026, 7, 22))
        self.assertLessEqual(result["interval_days"], 7)

    def test_record_answer_appends_history(self):
        card = card_progress.Card(
            "TEST-B01-C001",
            "Example",
            "cards.md",
            "TEST-B01",
            "spring",
        )
        catalog = {card.card_id: card}
        progress = {"schema_version": 1, "learner_id": "test", "updated_at": None, "cards": {}}
        answered = datetime(2026, 7, 22, 12, 0, tzinfo=timezone.utc)

        record = card_progress.record_answer(
            progress,
            catalog,
            card.card_id,
            "correct-confident",
            4,
            42,
            "Explained mechanism",
            answered,
        )

        self.assertEqual(1, record["attempts"])
        self.assertEqual(1, record["correct_attempts"])
        self.assertEqual(1, len(record["history"]))
        self.assertEqual("2026-07-23", record["next_review"])

    def test_catalog_detects_duplicate_ids(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            (root / "a.md").write_text("## TEST-B01-C001 — A\n", encoding="utf-8")
            (root / "b.md").write_text("## TEST-B01-C001 — B\n", encoding="utf-8")
            with self.assertRaises(ValueError):
                card_progress.scan_catalog(root)

    def test_progress_unknown_card_is_error(self):
        progress = {
            "schema_version": 1,
            "learner_id": "test",
            "updated_at": None,
            "cards": {"UNKNOWN-B01-C001": card_progress.default_progress()},
        }
        errors = card_progress.validate_progress(progress, {})
        self.assertTrue(any("unknown card" in error.lower() for error in errors))

    def test_due_queue_includes_new_and_due_not_future(self):
        catalog = {
            "TEST-B01-C001": card_progress.Card("TEST-B01-C001", "New", "a.md", "TEST-B01", "spring"),
            "TEST-B01-C002": card_progress.Card("TEST-B01-C002", "Due", "a.md", "TEST-B01", "spring"),
            "TEST-B01-C003": card_progress.Card("TEST-B01-C003", "Future", "a.md", "TEST-B01", "spring"),
        }
        due_record = card_progress.default_progress()
        due_record.update({"state": "review", "next_review": "2026-07-21"})
        future_record = card_progress.default_progress()
        future_record.update({"state": "review", "next_review": "2026-07-30"})
        progress = {
            "schema_version": 1,
            "learner_id": "test",
            "updated_at": None,
            "cards": {
                "TEST-B01-C002": due_record,
                "TEST-B01-C003": future_record,
            },
        }
        rows = card_progress.due_cards(catalog, progress, date(2026, 7, 22))
        ids = [row[1].card_id for row in rows]
        self.assertEqual(["TEST-B01-C002", "TEST-B01-C001"], ids)


if __name__ == "__main__":
    unittest.main()
