#!/usr/bin/env python3
from pathlib import Path

path = Path("30_CERTIFICATIONS/Spring/2V0-72.22/AOP-B01/AOP-B01 Cards.md")
text = path.read_text(encoding="utf-8")
old = "@Transactional(requiresNew = true) // conceptually ineffective here"
new = "@Transactional(propagation = Propagation.REQUIRES_NEW) // ineffective on a private method"
if old not in text:
    raise SystemExit("Expected invalid example was not found")
path.write_text(text.replace(old, new, 1), encoding="utf-8")
print("repaired AOP-B01 private-method transaction example")
