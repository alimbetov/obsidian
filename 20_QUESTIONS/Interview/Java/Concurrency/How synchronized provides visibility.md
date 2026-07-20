---
type: interview-question
domain: java
subdomain: concurrency
difficulty: middle
status: learning
confidence: 0
interview: true
topic: "[[synchronized]]"
related:
  - "[[Happens-Before]]"
java_versions: [8, 11, 17, 21]
tags:
  - java
  - concurrency
  - interview
---

# How does synchronized provide visibility?

> [!question]
> Почему `synchronized` решает не только mutual exclusion, но и visibility?

> [!answer]- Короткий ответ
> Unlock monitor одним потоком happens-before последующему lock того же monitor другим потоком. Поэтому записи, сделанные до unlock, должны быть видимы после later lock.

## Критическое условие

Оба потока должны использовать **тот же monitor**.

```java
synchronized (lock) {
    value = 42;
}

synchronized (lock) {
    System.out.println(value);
}
```

Не работает как единый protocol:

```java
synchronized (writerLock) {
    value = 42;
}

synchronized (readerLock) {
    System.out.println(value);
}
```

## Что проверяет интервьюер

- monitor rule;
- значение «same monitor»;
- разделение mutual exclusion и publication;
- единый synchronization protocol для всех accesses.

## Memory Hook

> **Unlock publishes; later lock receives — but only through the same door.**
