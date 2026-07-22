---
type: interview-question
domain: java
subdomain: concurrency
difficulty: senior
status: learning
confidence: 0
interview: true
topic: "[[Happens-Before]]"
related:
  - "[[Java Memory Model]]"
java_versions: [8, 11, 17, 21]
tags:
  - java
  - concurrency
  - interview
---

# What does happens-before actually guarantee?

> [!question]
> Что на самом деле гарантирует happens-before и почему это не просто хронологический порядок?

> [!answer]- Короткий ответ
> Если A happens-before B, эффекты A должны быть видимы B, а A считается упорядоченным перед B в допустимом наблюдении. Физическое выполнение «раньше по времени» без synchronization edge такой гарантии не создаёт.

## Развёрнутый ответ

Happens-before строится через правила Java Memory Model:

- program order;
- monitor unlock → later lock того же monitor;
- volatile write → subsequent volatile read той же variable;
- actions before `start()` → actions started thread;
- thread actions → successful return from `join()`;
- transitivity.

## Мини-пример

```java
int payload;
volatile boolean ready;

// writer
payload = 42;
ready = true;

// reader
if (ready) {
    System.out.println(payload);
}
```

Цепочка:

```text
payload write
  -> program order
volatile ready write
  -> volatile rule
volatile ready read
  -> program order
payload read
```

По transitivity reader получает доказательство visibility для `payload`.

## Неправильный ответ

> «Happens-before означает, что A завершилось раньше B по часам».

Почему неверно: wall-clock order не создаёт memory visibility contract.

## Что проверяет интервьюер

- способность рассуждать через formal edge;
- различие elapsed time и memory ordering;
- знание базовых rules;
- понимание transitivity.

## Follow-up

- Создаёт ли `Thread.sleep()` happens-before?
- Что гарантирует `Future.get()` относительно task completion?
- Почему разные lock objects не помогают?
