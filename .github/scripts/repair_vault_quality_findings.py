#!/usr/bin/env python3
"""Repair the four remaining broken Spring aliases found by the vault audit."""

from pathlib import Path

REPLACEMENTS = {
    "10_CONCEPTS/Spring/AOP/Spring AOP Proxy Mechanics.md": {
        "[[Transaction Propagation]]": "[[10_CONCEPTS/Spring/Transactions/Spring Transaction Management Deep Dive|Transaction Propagation]]",
        "[[Spring Async]]": "[[10_CONCEPTS/Spring/AOP/Spring AOP Proxy Mechanics#18. @Async and thread boundaries|Spring Async]]",
    },
    "10_CONCEPTS/Spring/Core/Bean Lifecycle from Definition to Destruction.md": {
        "[[BeanPostProcessor]]": "[[10_CONCEPTS/Spring/Core/Container Extension Points|BeanPostProcessor]]",
        "[[Spring AOP]]": "[[10_CONCEPTS/Spring/AOP/Spring AOP Proxy Mechanics|Spring AOP]]",
    },
}


def main() -> None:
    changed = []
    for file_name, replacements in REPLACEMENTS.items():
        path = Path(file_name)
        text = path.read_text(encoding="utf-8")
        original = text
        for old, new in replacements.items():
            if new in text:
                continue
            if old not in text:
                raise SystemExit(f"Expected alias not found in {file_name}: {old}")
            text = text.replace(old, new)
        if text != original:
            path.write_text(text, encoding="utf-8")
            changed.append(file_name)

    for file_name in changed:
        print(f"repaired {file_name}")
    print(f"repaired files: {len(changed)}")


if __name__ == "__main__":
    main()
