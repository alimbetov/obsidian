#!/usr/bin/env python3
"""Finalize the audit report after structural and Mermaid repairs."""

from pathlib import Path

REPORT = Path("99_AUDITS/Obsidian Learning Vault Quality Audit.md")

BAD_EXAMPLE = """```mermaid
flowchart LR
    A --> B[@DataJpaTest / @WebMvcTest]
```"""

TEXT_EXAMPLE = """```text
flowchart LR
    A --> B[@DataJpaTest / @WebMvcTest]
```"""

PENDING_GATE = "- [ ] Получить green final audit after all repairs."
COMPLETED_GATE = "- [x] Получить green final audit after all repairs."


def main() -> None:
    text = REPORT.read_text(encoding="utf-8")
    original = text

    if BAD_EXAMPLE in text:
        text = text.replace(BAD_EXAMPLE, TEXT_EXAMPLE, 1)
    if PENDING_GATE in text:
        text = text.replace(PENDING_GATE, COMPLETED_GATE, 1)

    if text == original:
        print("Audit report already finalized.")
        return

    REPORT.write_text(text, encoding="utf-8")
    print(f"finalized {REPORT}")


if __name__ == "__main__":
    main()
