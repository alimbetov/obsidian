---
type: lab-index
domain: java
subdomain: concurrency
status: active
java_versions: [8, 11, 17, 21]
tags:
  - java
  - concurrency
  - lab
---

# Java Concurrency Labs

> [!summary] Назначение
> Лаборатории превращают абстрактные гарантии Java Memory Model в наблюдаемые эксперименты. Некоторые broken examples недетерминированы: отсутствие ошибки в одном запуске не доказывает корректность.

## Правило эксперимента

Для каждого примера:

1. Предскажи результат до запуска.
2. Объясни, какая операция должна быть atomic или visible.
3. Запусти несколько раз.
4. Исправь минимальным подходящим механизмом.
5. Объясни, какую гарантию добавило исправление.

## Java 8-compatible labs

Компиляция:

```bash
mkdir -p out/java8
javac --release 8 -d out/java8 java8/*.java
```

Запуск:

```bash
java -cp out/java8 RaceConditionDemo
java -cp out/java8 SynchronizedCounterDemo
java -cp out/java8 VolatileFlagDemo
java -cp out/java8 VolatileCounterTrap
java -cp out/java8 ExecutorLifecycleDemo
java -cp out/java8 CompletableFuturePipelineDemo
```

## Java 21 labs

```bash
mkdir -p out/java21
javac --release 21 -d out/java21 java21/*.java
java -cp out/java21 VirtualThreadDemo
```

## Матрица наблюдений

| Lab | Что наблюдаем | Связанная тема |
|---|---|---|
| RaceConditionDemo | lost updates | [[Race Condition]] |
| SynchronizedCounterDemo | atomic critical section | [[synchronized]] |
| VolatileFlagDemo | visibility flag | [[volatile]] |
| VolatileCounterTrap | volatile не даёт compound atomicity | [[Visibility Atomicity Ordering]] |
| ExecutorLifecycleDemo | Future result и graceful shutdown | [[ExecutorService]] |
| CompletableFuturePipelineDemo | composition и combination | [[CompletableFuture]] |
| VirtualThreadDemo | thread-per-task для blocking work | [[Virtual Threads]] |

## Важное ограничение

> [!warning]
> Нельзя доказывать корректность конкурентного кода только результатами тестового запуска. Лаборатория помогает увидеть симптом, но доказательство строится через happens-before, atomicity и invariant.
