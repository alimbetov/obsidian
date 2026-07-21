#!/usr/bin/env python3
from pathlib import Path

path = Path("99_AUDITS/Obsidian Learning Vault Quality Audit.md")
text = path.read_text(encoding="utf-8")
old = "После нормализации AOP/CACHE остаются 98 cards с missing mandatory sections."
new = "После нормализации AOP/CACHE остаются 100 cards с missing mandatory sections."
if old not in text:
    raise SystemExit("Expected stale remaining-card count was not found")
path.write_text(text.replace(old, new, 1), encoding="utf-8")
print("corrected remaining card count from 98 to 100")
