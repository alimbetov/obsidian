---
type: interview-question
domain: java
subdomain: concurrency
difficulty: senior
status: learning
confidence: 0
interview: true
topic: "[[Virtual Threads]]"
java_versions:
  - 21
tags:
  - java
  - concurrency
  - interview
---

# Are virtual threads faster?

> [!question]
> Делают ли virtual threads выполнение кода быстрее?

> [!answer]- Короткий ответ
> Они не ускоряют отдельное CPU-вычисление. Они повышают scalability workloads с большим количеством blocking I/O, потому что ожидание не требует удерживать отдельный дорогой platform thread на каждую задачу.

## Хороший ответ уровня Senior

Virtual threads улучшают **throughput under blocking concurrency**, а не скорость процессора. Реальное выполнение Java-кода всё равно ограничено carrier threads и CPU cores. Кроме того, capacity БД, connection pool и downstream services не увеличивается автоматически.

## Пример выбора

| Workload | Ожидаемая польза |
|---|---|
| 50 000 HTTP calls с ожиданием | высокая |
| 50 000 sleeping tasks | высокая масштабируемость |
| 50 000 SHA-256 computations | CPU остаётся bottleneck |
| 100 000 DB calls при pool size 30 | DB concurrency около 30 |

## Follow-up

- Нужно ли создавать pool virtual threads?
- Что ограничивать вместо thread count?
- Что такое pinning в Java 21?
- Как ThreadLocal влияет на memory footprint?

## Memory Hook

> **Cheaper waiting, not faster computing.**
