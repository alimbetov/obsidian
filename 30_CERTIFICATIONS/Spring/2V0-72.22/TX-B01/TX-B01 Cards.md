---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain: transactions
batch_id: TX-B01
status: published
card_count: 32
first_card: TX-B01-C001
last_card: TX-B01-C032
spring_versions:
  - 5.3.39
tags:
  - spring
  - certification
  - transaction
  - propagation
  - isolation
---

# TX-B01 — Spring Transaction Management Deep Dive

> [!summary]
> 32 карточки по transaction interceptor, logical/physical transactions, propagation, isolation, rollback rules, programmatic management, events, cache ordering, async и outbox.

---

## TX-B01-C001 — How are declarative Spring transactions commonly applied?

### Russian Translation

Как обычно применяются декларативные транзакции Spring?

> [!answer]- Answer
> Through an AOP interceptor around calls that cross a Spring-managed proxy boundary.

### Explanation

`@Transactional` is metadata. A transaction interceptor reads that metadata, obtains a transaction from a selected `PlatformTransactionManager`, invokes the target and then commits or rolls back.

### Exam Trap

The annotation alone is not a JVM instruction to start a transaction.

### Memory Hook

**Caller → proxy → interceptor → manager → resource → target.**

---

## TX-B01-C002 — What is the difference between a logical and a physical transaction?

### Russian Translation

Чем отличается logical transaction от physical transaction?

> [!answer]- Answer
> A logical transaction is the transactional scope associated with an intercepted method; a physical transaction is the actual resource transaction, such as a JDBC connection transaction.

### Explanation

Several `REQUIRED` logical scopes can map to one physical transaction.

### Exam Trap

Do not count every `@Transactional` method as a separate database transaction.

---

## TX-B01-C003 — What are the default `@Transactional` settings?

### Russian Translation

Каковы настройки `@Transactional` по умолчанию?

> [!answer]- Answer
> `REQUIRED`, `ISOLATION_DEFAULT`, read-write, manager-default timeout, rollback for `RuntimeException` and `Error`, and no rollback for checked exceptions by default.

### Exam Trap

A checked exception does not automatically roll back the transaction.

---

## TX-B01-C004 — What does `PROPAGATION_REQUIRED` do?

### Russian Translation

Что делает `PROPAGATION_REQUIRED`?

> [!answer]- Answer
> It joins an existing transaction or creates a new physical transaction when none exists.

### Explanation

Each intercepted method still has a logical scope even when several scopes share one physical transaction.

### Memory Hook

**REQUIRED: join or create.**

---

## TX-B01-C005 — Why can an inner `REQUIRED` method affect the outer commit?

### Russian Translation

Почему внутренний `REQUIRED` method может повлиять на commit внешнего method?

> [!answer]- Answer
> Because both logical scopes can share one physical transaction, and the inner scope can mark that shared transaction rollback-only.

### Production Transfer

Catching the inner exception does not necessarily restore the transaction to a committable state.

---

## TX-B01-C006 — When is `UnexpectedRollbackException` commonly thrown?

### Russian Translation

Когда обычно возникает `UnexpectedRollbackException`?

> [!answer]- Answer
> When an inner participating scope marks the shared transaction rollback-only, but the outer scope attempts to commit because it did not decide to roll back itself.

### Explanation

The exception prevents the outer caller from falsely believing that a commit occurred.

### Memory Hook

**Outer asked to commit; physical transaction was already doomed.**

---

## TX-B01-C007 — What does `PROPAGATION_REQUIRES_NEW` do?

### Russian Translation

Что делает `PROPAGATION_REQUIRES_NEW`?

> [!answer]- Answer
> It suspends any existing transaction and starts an independent physical transaction.

### Explanation

The inner transaction can commit or roll back independently from the outer one.

### Exam Trap

`REQUIRES_NEW` is not a savepoint.

---

## TX-B01-C008 — What resource risk does `REQUIRES_NEW` introduce?

### Russian Translation

Какой ресурсный риск создаёт `REQUIRES_NEW`?

> [!answer]- Answer
> The outer transaction can keep its connection while the inner transaction needs another connection, which can exhaust the pool under concurrency.

### Production Transfer

A loop of `REQUIRES_NEW` calls requires connection-pool and latency analysis.

---

## TX-B01-C009 — What does `PROPAGATION_NESTED` normally use for JDBC transactions?

### Russian Translation

Что обычно использует `PROPAGATION_NESTED` для JDBC transactions?

> [!answer]- Answer
> A savepoint inside the existing physical transaction.

### Explanation

The inner scope can roll back to the savepoint while the outer physical transaction continues.

### Exam Trap

An outer rollback still removes work that was successful inside the nested scope.

---

## TX-B01-C010 — How does `NESTED` differ from `REQUIRES_NEW`?

### Russian Translation

Чем `NESTED` отличается от `REQUIRES_NEW`?

> [!answer]- Answer
> `NESTED` normally uses one physical transaction with savepoints; `REQUIRES_NEW` uses a separate independently committed physical transaction.

### Memory Hook

**Nested saves a point; requires-new starts another transaction.**

---

## TX-B01-C011 — What does `PROPAGATION_SUPPORTS` do?

### Russian Translation

Что делает `PROPAGATION_SUPPORTS`?

> [!answer]- Answer
> It participates in an existing transaction but runs without a physical transaction when none exists.

### Exam Trap

The same method can behave differently depending on its caller, especially with lazy loading and synchronization callbacks.

---

## TX-B01-C012 — What does `PROPAGATION_MANDATORY` enforce?

### Russian Translation

Что гарантирует `PROPAGATION_MANDATORY`?

> [!answer]- Answer
> It requires an existing transaction and throws an exception when called without one.

### Production Transfer

Useful for a ledger writer that must always be part of a service-level atomic operation.

---

## TX-B01-C013 — What does `PROPAGATION_NOT_SUPPORTED` do?

### Russian Translation

Что делает `PROPAGATION_NOT_SUPPORTED`?

> [!answer]- Answer
> It suspends an existing transaction and executes the method without a transaction.

### Exam Trap

Suspending the transaction also removes atomicity between that method and the surrounding database work.

---

## TX-B01-C014 — What does `PROPAGATION_NEVER` enforce?

### Russian Translation

Что гарантирует `PROPAGATION_NEVER`?

> [!answer]- Answer
> It executes only when no transaction exists and throws an exception when called inside one.

### Memory Hook

**MANDATORY requires; NEVER forbids.**

---

## TX-B01-C015 — Why does self-invocation break propagation changes?

### Russian Translation

Почему self-invocation ломает изменение propagation?

> [!answer]- Answer
> An internal `this.method()` call does not cross the transaction proxy, so a second transaction interceptor invocation does not occur.

### Best Fix

Move the operation to a separate Spring collaborator.

### Exam Trap

Changing from JDK proxy to CGLIB does not solve self-invocation.

---

## TX-B01-C016 — Which exceptions cause rollback by default?

### Russian Translation

Какие exceptions вызывают rollback по умолчанию?

> [!answer]- Answer
> `RuntimeException` and `Error` cause rollback by default; checked exceptions do not.

### Mini Example

```java
@Transactional(rollbackFor = IOException.class)
public void importFile(Path path) throws IOException {
    // ...
}
```

---

## TX-B01-C017 — What can happen if a transactional method catches and swallows a runtime exception?

### Russian Translation

Что может произойти, если transactional method поймал и проглотил runtime exception?

> [!answer]- Answer
> The interceptor may observe a normal return and attempt commit, unless the transaction was already marked rollback-only or the code explicitly marks it.

### Production Transfer

Exception handling is part of transaction semantics, not merely logging style.

---

## TX-B01-C018 — What does `rollbackFor` configure?

### Russian Translation

Что настраивает `rollbackFor`?

> [!answer]- Answer
> It adds exception types that must cause rollback, including checked exceptions.

### Exam Trap

Prefer type-safe exception classes over overly broad name patterns.

---

## TX-B01-C019 — What does `readOnly=true` guarantee?

### Russian Translation

Что гарантирует `readOnly=true`?

> [!answer]- Answer
> It expresses a read-only hint/contract to the transaction manager and resource; it is not a universal compile-time prohibition of writes.

### Explanation

Actual effects depend on JDBC driver, JPA provider, database and custom routing infrastructure.

---

## TX-B01-C020 — When does a method-level isolation setting actually take effect?

### Russian Translation

Когда isolation, заданный на method, реально начинает действовать?

> [!answer]- Answer
> When the method starts a new physical transaction, normally with `REQUIRED` when none exists or with `REQUIRES_NEW`.

### Exam Trap

An inner `REQUIRED` method that joins an outer transaction normally inherits the outer physical transaction’s characteristics.

---

## TX-B01-C021 — What does `ISOLATION_DEFAULT` mean?

### Russian Translation

Что означает `ISOLATION_DEFAULT`?

> [!answer]- Answer
> Use the default isolation behavior of the selected transaction manager and underlying database/resource.

### Exam Trap

Do not assume the same default isolation across all databases.

---

## TX-B01-C022 — Does `SERIALIZABLE` remove every need for optimistic or pessimistic locking?

### Russian Translation

Устраняет ли `SERIALIZABLE` любую необходимость в optimistic/pessimistic locking?

> [!answer]- Answer
> No. Database semantics, retry behavior, contention and domain-specific lost-update protection still require explicit design.

### Production Transfer

Atomic conditional updates and version checks are often clearer than globally increasing isolation.

---

## TX-B01-C023 — What is `TransactionTemplate` used for?

### Russian Translation

Для чего используется `TransactionTemplate`?

> [!answer]- Answer
> It provides programmatic transaction demarcation around an explicit code block.

### Mini Example

```java
return transactionTemplate.execute(status -> {
    Result result = repository.save(command);
    if (!result.valid()) {
        status.setRollbackOnly();
    }
    return result;
});
```

### Use Case

A short transaction inside a larger non-transactional orchestration method.

---

## TX-B01-C024 — What is a `PlatformTransactionManager` responsible for?

### Russian Translation

За что отвечает `PlatformTransactionManager`?

> [!answer]- Answer
> It obtains transaction status according to a definition and commits or rolls back the selected resource transaction.

### Explanation

Concrete managers adapt JDBC, JPA, JTA or other resources to the common Spring model.

---

## TX-B01-C025 — Does selecting one transaction manager make writes through another manager atomic?

### Russian Translation

Делает ли выбор одного transaction manager операции через другой manager атомарными?

> [!answer]- Answer
> No. Multiple local transaction managers do not become one distributed transaction automatically.

### Production Transfer

Use JTA/XA only when justified, or redesign with outbox, saga or ownership boundaries.

---

## TX-B01-C026 — What is `TransactionSynchronization` used for?

### Russian Translation

Для чего используется `TransactionSynchronization`?

> [!answer]- Answer
> It registers callbacks for transaction lifecycle phases such as before commit, after commit and after completion.

### Exam Trap

An exception in `afterCommit()` cannot undo a database commit that already happened.

---

## TX-B01-C027 — What is the default phase of `@TransactionalEventListener`?

### Russian Translation

Какова default phase у `@TransactionalEventListener`?

> [!answer]- Answer
> `AFTER_COMMIT`.

### Other Phases

`BEFORE_COMMIT`, `AFTER_ROLLBACK` and `AFTER_COMPLETION`.

### Exam Trap

Without an active transaction, the listener normally does not run unless fallback execution is enabled.

---

## TX-B01-C028 — Is `@TransactionalEventListener(AFTER_COMMIT)` a durable messaging guarantee?

### Russian Translation

Является ли `@TransactionalEventListener(AFTER_COMMIT)` гарантией надёжной доставки сообщения?

> [!answer]- Answer
> No. The database can commit and the process can crash before the listener completes its external side effect.

### Best Fit

Use Transactional Outbox for business-critical durable publication.

---

## TX-B01-C029 — Does an imperative transaction automatically cross an `@Async` boundary?

### Russian Translation

Переходит ли imperative transaction автоматически через границу `@Async`?

> [!answer]- Answer
> No. Imperative transactions are commonly thread-bound, while the async method runs on another thread.

### Production Transfer

The async method needs its own transaction if it performs transactional work.

---

## TX-B01-C030 — Why is a long remote call inside a database transaction dangerous?

### Russian Translation

Почему длинный remote call внутри DB transaction опасен?

> [!answer]- Answer
> It keeps connections and locks open during network waiting, increasing pool pressure, lock contention and timeout coupling.

### Better Design

Use a state machine, short transactions, version checks and outbox-driven orchestration where appropriate.

---

## TX-B01-C031 — What atomic guarantee does Transactional Outbox provide?

### Russian Translation

Какую атомарную гарантию даёт Transactional Outbox?

> [!answer]- Answer
> The business change and durable publication intent are committed in the same local database transaction.

### Exam Trap

It does not make the database and broker one physical transaction.

---

## TX-B01-C032 — Why must outbox consumers be idempotent?

### Russian Translation

Почему consumers outbox-событий должны быть idempotent?

> [!answer]- Answer
> A crash can happen after broker acceptance but before the outbox row is marked published, causing the same message to be sent again.

### Memory Hook

**Atomic intent, at-least-once delivery, idempotent consumer.**

---

# Review matrix

| Confusion pair | Correct distinction |
|---|---|
| logical vs physical transaction | method scope vs actual resource commit |
| `REQUIRED` vs `REQUIRES_NEW` | join/create vs suspend/create independent |
| `REQUIRES_NEW` vs `NESTED` | independent transaction vs savepoint |
| `MANDATORY` vs `NEVER` | transaction required vs forbidden |
| caught exception vs successful commit | caught exception can still leave rollback-only |
| checked vs runtime exception | checked commits by default, runtime rolls back |
| read-only vs no writes | hint/contract vs universal enforcement |
| after-commit vs durable delivery | phase callback vs persisted publication intent |
| cache-aware timing vs XA | deferred cache action vs cross-resource atomicity |
| async vs same transaction | worker thread gets separate/no transaction |

# Related materials

- [[10_CONCEPTS/Spring/Transactions/Spring Transaction Management Deep Dive]]
- [[10_CONCEPTS/Spring/Transactions/Transactional Outbox and Commit Boundaries]]
- [[40_PRODUCTION_CASES/Spring/Transaction Management Production Cases]]
- [[50_LABS/Spring/TX-B01/README]]
- [[01_MAPS/Spring Transaction Management Map.canvas]]
