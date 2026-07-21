#!/usr/bin/env python3
"""Run the cross-link audit against base routes plus route override files."""

from __future__ import annotations

import copy
import json
import subprocess
import sys
from pathlib import Path
from typing import Dict, List

ROOT = Path(__file__).resolve().parents[2]
BASE_PATH = ROOT / ".github/knowledge-routes.json"
OVERRIDE_DIR = ROOT / ".github/knowledge-route-overrides"
AUDIT_SCRIPT = ROOT / ".github/scripts/audit_cross_links.py"


def load(path: Path) -> dict:
    return json.loads(path.read_text(encoding="utf-8"))


def main() -> int:
    original_text = BASE_PATH.read_text(encoding="utf-8")
    base = json.loads(original_text)
    routes: Dict[str, dict] = {
        route["id"]: copy.deepcopy(route)
        for route in base.get("routes", [])
        if isinstance(route, dict) and isinstance(route.get("id"), str)
    }
    order: List[str] = list(routes)
    errors: List[str] = []

    for path in sorted(OVERRIDE_DIR.glob("*.json")) if OVERRIDE_DIR.exists() else []:
        patch = load(path)
        if patch.get("schema_version") != 1:
            errors.append(f"{path}: unsupported schema_version")
            continue
        for route in patch.get("routes", []):
            if not isinstance(route, dict) or not isinstance(route.get("id"), str):
                errors.append(f"{path}: invalid route entry")
                continue
            route_id = route["id"]
            if route_id not in routes:
                order.append(route_id)
            routes[route_id] = copy.deepcopy(route)

    if errors:
        for error in errors:
            print(f"ERROR: {error}", file=sys.stderr)
        return 2

    merged = copy.deepcopy(base)
    merged["routes"] = [routes[route_id] for route_id in order]

    try:
        BASE_PATH.write_text(json.dumps(merged, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        result = subprocess.run([sys.executable, str(AUDIT_SCRIPT)], cwd=ROOT, check=False)
        return result.returncode
    finally:
        BASE_PATH.write_text(original_text, encoding="utf-8")


if __name__ == "__main__":
    raise SystemExit(main())
