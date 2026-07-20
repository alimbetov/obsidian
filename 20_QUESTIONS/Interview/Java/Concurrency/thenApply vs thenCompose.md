---
type: interview-question
domain: java
subdomain: concurrency
difficulty: middle
status: learning
confidence: 0
interview: true
topic: "[[CompletableFuture]]"
java_versions: [8, 11, 17, 21]
tags:
  - java
  - concurrency
  - interview
---

# thenApply vs thenCompose

> [!question]
> Когда использовать `thenApply`, а когда `thenCompose`?

> [!answer]- Короткий ответ
> `thenApply` используется для синхронного преобразования `T -> R`. `thenCompose` — когда функция возвращает следующий `CompletableFuture<R>` и вложенную future нужно flatten-ить.

## Типовая схема

```text
thenApply:
CompletableFuture<T> + (T -> R)
= CompletableFuture<R>

thenCompose:
CompletableFuture<T> + (T -> CompletableFuture<R>)
= CompletableFuture<R>
```

## Пример

```java
customerFuture.thenApply(Customer::name);
```

```java
customerFuture.thenCompose(customer -> loadLimitAsync(customer.id()));
```

## Exam Trap

Использование `thenApply` с async function создаёт:

```text
CompletableFuture<CompletableFuture<Limit>>
```

## Memory Hook

> **Apply maps. Compose flatMaps.**
