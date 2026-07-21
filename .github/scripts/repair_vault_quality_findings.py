#!/usr/bin/env python3
"""Repair structural links and a small set of diagram semantics found by the vault audit."""

from pathlib import Path

TEXT_REPLACEMENTS = {
    "10_CONCEPTS/Spring/AOP/Spring AOP Proxy Mechanics.md": {
        '  - "[[Transaction Propagation]]"\n': '  - "[[10_CONCEPTS/Spring/Transactions/Spring Transaction Management Deep Dive|Transaction Propagation]]"\n',
        '  - "[[Spring Async]]"\n': '  - "[[10_CONCEPTS/Spring/AOP/Spring AOP Proxy Mechanics#18. @Async and thread boundaries|Spring Async]]"\n',
    },
    "10_CONCEPTS/Spring/Core/Bean Lifecycle from Definition to Destruction.md": {
        '  - "[[BeanPostProcessor]]"\n': '  - "[[10_CONCEPTS/Spring/Core/Container Extension Points|BeanPostProcessor]]"\n',
        '  - "[[Spring AOP]]"\n': '  - "[[10_CONCEPTS/Spring/AOP/Spring AOP Proxy Mechanics|Spring AOP]]"\n',
    },
    "01_MAPS/Spring Testing Map.canvas": {
        '# @DataJpaTest\\n\\nEntities, repositories, JPA infrastructure, embedded DB and rollback. Regular services are excluded.':
        '# @DataJpaTest\\n\\nEntities, repositories and JPA infrastructure. Tests roll back by default; an embedded DB is used only when Boot replacement applies. Regular services are excluded.',
    },
    "10_CONCEPTS/Java/Concurrency/Atomic CAS and Counters.md": {
        "    C3 --> S[sum]\n```\n\n> [!tip] Memory Hook":
        "    C3 --> S[sum]\n```\n\n> [!note] Diagram precision\n> Cells illustrate striped contention reduction. A Java thread is not permanently assigned to one fixed cell; the implementation chooses and may retry/update different cells under contention.\n\n> [!tip] Memory Hook",
    },
    "01_MAPS/Java Map.md": {
        "### Foundations\n\n- [[10_CONCEPTS/Java/Concurrency/Visibility Atomicity Ordering]]":
        "### Foundations\n\n- [[10_CONCEPTS/Java/Concurrency/Threads]]\n- [[10_CONCEPTS/Java/Concurrency/Visibility Atomicity Ordering]]",
        "### Task execution\n\n- [[10_CONCEPTS/Java/Concurrency/ExecutorService]]":
        "### Task execution\n\n- [[10_CONCEPTS/Java/Concurrency/ExecutorService]]\n- [[10_CONCEPTS/Java/Concurrency/Future]]\n- [[10_CONCEPTS/Java/Concurrency/ForkJoinPool]]",
        "- [[10_CONCEPTS/Java/Concurrency/Virtual Threads]]\n":
        "- [[10_CONCEPTS/Java/Concurrency/Virtual Threads]]\n- [[10_CONCEPTS/Java/JVM/Memory Leaks]]\n",
    },
}

REDIRECT_FILE = Path("98_SOURCES/Spring AOP Cache Redis and Caffeine Sources.md")
REDIRECT_CONTENT = """---
type: source-index-redirect
domain: spring
subdomain: aop-cache
status: deprecated
canonical: \"[[98_SOURCES/Spring AOP and Cache Sources]]\"
tags:
  - spring
  - aop
  - cache
  - sources
  - redirect
---

# Spring AOP, Cache, Redis and Caffeine Sources

> [!important]
> Этот файл сохранён как compatibility redirect. Канонический version-pinned source index находится в [[98_SOURCES/Spring AOP and Cache Sources|Spring AOP and Cache Sources]].

Причина консолидации:

- один источник истины для Spring Framework `5.3.39`;
- один version policy для Spring Data Redis `2.7.18`;
- явное разделение stable contracts и current documentation;
- отсутствие расхождений между двумя почти одинаковыми списками ссылок.
"""


def replace_exact(path: Path, replacements: dict[str, str]) -> bool:
    text = path.read_text(encoding="utf-8")
    original = text
    for old, new in replacements.items():
        if old not in text:
            raise SystemExit(f"Expected fragment not found in {path}: {old[:120]!r}")
        text = text.replace(old, new, 1)
    if text != original:
        path.write_text(text, encoding="utf-8")
        return True
    return False


def main() -> None:
    changed = []
    for file_name, replacements in TEXT_REPLACEMENTS.items():
        path = Path(file_name)
        if replace_exact(path, replacements):
            changed.append(file_name)

    if REDIRECT_FILE.read_text(encoding="utf-8") != REDIRECT_CONTENT:
        REDIRECT_FILE.write_text(REDIRECT_CONTENT, encoding="utf-8")
        changed.append(REDIRECT_FILE.as_posix())

    for file_name in changed:
        print(f"repaired {file_name}")
    print(f"repaired files: {len(changed)}")


if __name__ == "__main__":
    main()
