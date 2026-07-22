#!/usr/bin/env python3
"""Create merged objective manifests from base files and route-level overrides."""

from __future__ import annotations

import argparse
import copy
import json
import shutil
import sys
from pathlib import Path
from typing import Dict, List


def load_json(path: Path) -> dict:
    return json.loads(path.read_text(encoding="utf-8"))


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", default=".")
    parser.add_argument("--base-dir", default=".github/objectives")
    parser.add_argument("--override-dir", default=".github/objective-overrides")
    parser.add_argument("--output-dir", default=".audit/objectives")
    args = parser.parse_args()

    root = Path(args.root).resolve()
    base_dir = root / args.base_dir
    override_dir = root / args.override_dir
    output_dir = root / args.output_dir

    if output_dir.exists():
        shutil.rmtree(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    manifests: Dict[str, dict] = {}
    source_paths: Dict[str, Path] = {}
    for path in sorted(base_dir.glob("*.json")):
        data = load_json(path)
        track_id = data.get("track_id")
        if not isinstance(track_id, str) or not track_id:
            print(f"ERROR: {path} has no track_id", file=sys.stderr)
            return 2
        if track_id in manifests:
            print(f"ERROR: duplicate base track_id {track_id}", file=sys.stderr)
            return 2
        manifests[track_id] = copy.deepcopy(data)
        source_paths[track_id] = path

    errors: List[str] = []
    applied = 0
    for path in sorted(override_dir.glob("*.json")) if override_dir.exists() else []:
        patch = load_json(path)
        if patch.get("schema_version") != 1:
            errors.append(f"{path}: unsupported schema_version")
            continue
        track_id = patch.get("track_id")
        if track_id not in manifests:
            errors.append(f"{path}: unknown track_id {track_id!r}")
            continue
        objectives = manifests[track_id].get("objectives", [])
        by_id = {objective.get("id"): objective for objective in objectives if isinstance(objective, dict)}
        for update in patch.get("objective_updates", []):
            if not isinstance(update, dict) or not isinstance(update.get("id"), str):
                errors.append(f"{path}: invalid objective update")
                continue
            objective_id = update["id"]
            if objective_id not in by_id:
                errors.append(f"{path}: unknown objective ID {objective_id}")
                continue
            target = by_id[objective_id]
            for key, value in update.items():
                if key != "id":
                    target[key] = copy.deepcopy(value)
            target["override_source"] = path.relative_to(root).as_posix()
            applied += 1

    if errors:
        for error in errors:
            print(f"ERROR: {error}", file=sys.stderr)
        return 1

    for track_id, data in manifests.items():
        source_name = source_paths[track_id].name
        (output_dir / source_name).write_text(
            json.dumps(data, ensure_ascii=False, indent=2) + "\n",
            encoding="utf-8",
        )

    print(json.dumps({
        "tracks": len(manifests),
        "objective_updates_applied": applied,
        "output_dir": output_dir.relative_to(root).as_posix(),
    }, ensure_ascii=False, indent=2))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
