---
type: interview-question
domain: java
subdomain: concurrency
difficulty: middle
status: learning
confidence: 0
interview: true
topic: "[[ExecutorService]]"
related:
  - "[[Future]]"
java_versions: [8, 11, 17, 21]
tags:
  - java
  - concurrency
  - interview
---

# execute vs submit

> [!question]
> В чём практическая разница между `Executor.execute()` и `ExecutorService.submit()`?

> [!answer]- Короткий ответ
> `execute` принимает `Runnable` и не возвращает handle результата. `submit` возвращает `Future`, поддерживает `Callable`, а exception задачи сохраняется в Future и проявляется через `get()`.

## Сравнение

| Свойство | execute | submit |
|---|---|---|
| Runnable | да | да |
| Callable | нет | да |
| Future | нет | да |
| Return value | нет | да |
| Exception observation | через thread/executor handling | через `Future.get()` |

## Production trap

```java
executor.submit(() -> {
    throw new IllegalStateException("boom");
});
```

Если Future проигнорирован, ошибка может остаться незамеченной приложением.

## Follow-up

- Что бросит `Future.get()`?
- Чем `get()` отличается от `join()` у CompletableFuture?
- Как централизованно логировать task failures?
- Почему graceful shutdown является частью корректности?
