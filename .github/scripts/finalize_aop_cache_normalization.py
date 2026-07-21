#!/usr/bin/env python3
from pathlib import Path


def replace_exact(path: Path, old: str, new: str) -> None:
    text = path.read_text(encoding="utf-8")
    if old not in text:
        raise SystemExit(f"Expected fragment not found in {path}: {old!r}")
    path.write_text(text.replace(old, new, 1), encoding="utf-8")


cache = Path("30_CERTIFICATIONS/Spring/2V0-72.22/CACHE-B01/CACHE-B01 Cards.md")
replace_exact(
    cache,
    "record-like Java 8 DTO concept:\n\nfinal class ProductCacheValue {",
    "// Java 8 cache DTO with an explicit schema version\nfinal class ProductCacheValue {",
)

audit = Path("99_AUDITS/Obsidian Learning Vault Quality Audit.md")
replace_exact(
    audit,
    "> Репозиторий уже является сильной обучающей системой по Spring и Java Concurrency, но пока неравномерен как полный Java Backend vault. Наиболее зрелая часть — Spring Core → AOP/Cache → Transactions → Data/JPA → Testing. Основные риски: неоднородный стандарт карточек, ранее невалидные Mermaid labels, отсутствовавшие foundational notes и пока скелетные маршруты Databases, Messaging и Distributed Systems.\n",
    "> Репозиторий уже является сильной обучающей системой по Spring и Java Concurrency, но пока неравномерен как полный Java Backend vault. Наиболее зрелая часть — Spring Core → AOP/Cache → Transactions → Data/JPA → Testing. Основные риски: неоднородный стандарт карточек, ранее невалидные Mermaid labels, отсутствовавшие foundational notes и пока скелетные маршруты Databases, Messaging и Distributed Systems.\n\n> [!success] Normalization update — 2026-07-21\n> `AOP-B01` и `CACHE-B01` нормализованы: 44/44 карточки имеют Question, Russian Translation, Answer, Explanation и Exam Trap. Подробности: [[99_AUDITS/AOP and Cache Card Normalization]].\n",
)
replace_exact(audit, "| AOP-B01 | 20 / 24 |", "| AOP-B01 | 0 / 24 — normalized |")
replace_exact(audit, "| CACHE-B01 | 20 / 20 |", "| CACHE-B01 | 0 / 20 — normalized |")
replace_exact(
    audit,
    "Это не означает, что 138 cards неверны. Это означает, что они не соответствуют собственному pedagogical contract.",
    "После нормализации AOP/CACHE остаются 98 cards с missing mandatory sections. Это не означает, что они технически неверны; они ещё не соответствуют собственному pedagogical contract.",
)
replace_exact(
    audit,
    "```text\nAOP-B01\nCACHE-B01\nTX-B01\nDATA-B01\nTEST-B01\nCORE-B01 / CORE-B04\n```",
    "```text\nTX-B01\nDATA-B01\nTEST-B01\nCORE-B01 / CORE-B04\n```",
)
replace_exact(
    audit,
    "- [ ] Нормализовать mandatory sections в AOP/CACHE/TX/DATA/TEST cards.",
    "- [x] Нормализовать mandatory sections в AOP-B01 и CACHE-B01.\n- [ ] Нормализовать mandatory sections в TX/DATA/TEST cards.",
)

print("finalized AOP/CACHE normalization content and audit status")
