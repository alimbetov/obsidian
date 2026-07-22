---
type: interview-question
domain: java
subdomain: concurrency
difficulty: middle
status: learning
confidence: 0
interview: true
topic: "[[volatile]]"
related:
  - "[[Visibility Atomicity Ordering]]"
  - "[[Race Condition]]"
java_versions: [8, 11, 17, 21]
tags:
  - java
  - concurrency
  - interview
---

# Why does volatile not make increment atomic?

> [!question]
> Почему `volatile int counter` не делает `counter++` потокобезопасным?

> [!answer]- Короткий ответ
> Потому что `counter++` — составная read-modify-write операция. `volatile` делает отдельные read/write видимыми и упорядоченными, но другой поток может вмешаться между чтением и записью.

## Развёрнутый ответ

```java
volatile int counter;

void increment() {
    counter++;
}
```

Логически:

```text
1. read counter
2. add 1
3. write counter
```

Два потока могут прочитать `10`, оба вычислить `11` и оба записать `11`. Одно обновление потеряно.

## Хорошее исправление

Для простого счётчика:

```java
AtomicInteger counter = new AtomicInteger();
counter.incrementAndGet();
```

Для более сложного invariant:

```java
synchronized void update() {
    // несколько связанных действий
}
```

## Что проверяет интервьюер

- различие visibility и atomicity;
- понимание compound operations;
- выбор atomic class против lock;
- способность не повторять миф «volatile делает всё thread-safe».

## Follow-up

1. Является ли volatile read атомарным?
2. Когда `AtomicInteger` недостаточен?
3. Что такое CAS retry loop?
4. Может ли `LongAdder` дать точное мгновенное значение при конкурентных updates?

## Memory Hook

> **Visible steps are still separate steps.**
