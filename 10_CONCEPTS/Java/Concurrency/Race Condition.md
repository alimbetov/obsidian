---
type: concept
domain: java
subdomain: concurrency
difficulty: intermediate
status: learning
confidence: 0
interview: true
certification:
  - java
java_versions: [8, 11, 17, 21]
production_relevance: high
prerequisites:
  - "[[Visibility Atomicity Ordering]]"
related:
  - "[[synchronized]]"
  - "[[ReentrantLock]]"
  - "[[Java Memory Model]]"
tags:
  - java
  - concurrency
---

# Race Condition

> [!summary] За 30 секунд
> Race condition возникает, когда корректность результата зависит от непредсказуемого взаимного порядка действий нескольких потоков.

## Не путать с data race

- **Race condition** — более широкая логическая проблема: результат зависит от timing.
- **Data race** в терминах Java Memory Model — конфликтующие обращения к одной переменной без happens-before, где хотя бы одно обращение является записью.

Можно иметь логическую race condition даже при использовании потокобезопасных отдельных операций.

## Пример: check-then-act

```java
if (!users.containsKey(id)) {
    users.put(id, loadUser(id));
}
```

Даже если `users` — `ConcurrentHashMap`, составная логика не атомарна:

```mermaid
sequenceDiagram
    participant A as Thread A
    participant B as Thread B
    participant M as ConcurrentHashMap
    A->>M: containsKey(id) = false
    B->>M: containsKey(id) = false
    A->>M: put(loadUser())
    B->>M: put(loadUser())
    Note over A,B: Двойная загрузка, хотя отдельные методы thread-safe
```

Правильный вариант:

```java
User user = users.computeIfAbsent(id, this::loadUser);
```

## Другие формы race condition

- read-modify-write: `counter++`;
- check-then-act: проверил, затем выполнил;
- lazy initialization без safe publication;
- два перевода средств с одновременной проверкой баланса;
- запуск cleanup одновременно с использованием ресурса.

## Как распознать

Спроси:

1. Какие данные разделяются?
2. Какие действия должны восприниматься как единая операция?
3. Может ли другой поток вмешаться между проверкой и действием?
4. На каком объекте или механизме строится coordination?
5. Есть ли единый инвариант, который должен сохраняться?

## Исправление начинается с инварианта

Плохая стратегия:

> «Добавим `synchronized` в случайный метод».

Хорошая стратегия:

> «Баланс не может стать отрицательным; проверка и списание должны быть одной критической секцией».

## Memory Hook

> Race condition — это не «два потока работают одновременно». Это **неправильная зависимость корректности от того, кому повезло выполнить шаг первым**.

## Sources

- [[98_SOURCES/Java Concurrency Sources|Primary Java Concurrency Sources]]
