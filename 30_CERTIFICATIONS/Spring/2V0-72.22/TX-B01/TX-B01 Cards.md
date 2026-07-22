---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain: transactions
batch_id: TX-B01
status: published
normalization_status: complete
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
> 32 normalized cards по transaction interceptor, logical/physical transactions, propagation, isolation, rollback rules, programmatic management, events, cache ordering, async и outbox.

## Route navigation

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Transaction Management Roadmap]]
- [[10_CONCEPTS/Spring/Transactions/Spring Transaction Management Deep Dive]]
- [[10_CONCEPTS/Spring/Transactions/Spring Transaction Management Visual Deep Dive]]
- [[10_CONCEPTS/Spring/Transactions/Transactional Outbox and Commit Boundaries]]
- [[40_PRODUCTION_CASES/Spring/Transaction Management Production Cases]]
- [[50_LABS/Spring/TX-B01/README]]
- [[98_SOURCES/Spring Transaction Management Sources]]

---

## TX-B01-C001 — How are declarative Spring transactions commonly applied?

### Russian Translation

Как обычно применяются декларативные транзакции Spring?

> [!answer]- Answer
> Through an AOP interceptor around calls that cross a Spring-managed proxy boundary.

### Explanation

`@Transactional` is metadata. The interceptor reads it, selects a `PlatformTransactionManager`, begins or joins a transaction, invokes the target and then commits or rolls back.

### Exam Trap

The annotation itself is not a JVM instruction that starts a transaction without proxy/interceptor infrastructure.

---

## TX-B01-C002 — What is the difference between a logical and a physical transaction?

### Russian Translation

Чем отличается logical transaction от physical transaction?

> [!answer]- Answer
> A logical transaction is the transactional scope associated with an intercepted method; a physical transaction is the actual resource transaction, such as a JDBC connection transaction.

### Explanation

Several `REQUIRED` method scopes can share one physical transaction and one final commit or rollback.

### Exam Trap

Do not count every `@Transactional` method as a separate database transaction.

---

## TX-B01-C003 — What are the default `@Transactional` settings?

### Russian Translation

Каковы настройки `@Transactional` по умолчанию?

> [!answer]- Answer
> `REQUIRED`, `ISOLATION_DEFAULT`, read-write, manager-default timeout, rollback for `RuntimeException` and `Error`, and no rollback for checked exceptions by default.

### Explanation

Defaults define the logical transaction request; the selected manager and database still determine physical resource behavior.

### Exam Trap

A checked exception does not automatically roll back the transaction.

---

## TX-B01-C004 — What does `PROPAGATION_REQUIRED` do?

### Russian Translation

Что делает `PROPAGATION_REQUIRED`?

> [!answer]- Answer
> It joins an existing transaction or creates a new physical transaction when none exists.

### Explanation

Each intercepted method gets a logical scope, but participating scopes share the same physical transaction.

### Exam Trap

`REQUIRED` does not mean “always create a new transaction.”

---

## TX-B01-C005 — Why can an inner `REQUIRED` method affect the outer commit?

### Russian Translation

Почему внутренний `REQUIRED` method может повлиять на commit внешнего method?

> [!answer]- Answer
> Because both logical scopes can share one physical transaction, and the inner scope can mark that shared transaction rollback-only.

### Explanation

Once the physical transaction is doomed, catching the original exception does not restore it to a committable state.

### Exam Trap

A normal return from the outer method does not guarantee the shared transaction can commit.

---

## TX-B01-C006 — When is `UnexpectedRollbackException` commonly thrown?

### Russian Translation

Когда обычно возникает `UnexpectedRollbackException`?

> [!answer]- Answer
> When an inner participating scope marks the shared transaction rollback-only, but the outer scope attempts to commit.

### Explanation

The exception prevents the caller from believing a commit succeeded when the physical transaction actually rolled back.

### Exam Trap

It is usually reported at the outer commit boundary, not necessarily where rollback-only was first set.

---

## TX-B01-C007 — What does `PROPAGATION_REQUIRES_NEW` do?

### Russian Translation

Что делает `PROPAGATION_REQUIRES_NEW`?

> [!answer]- Answer
> It suspends any existing transaction and starts an independent physical transaction.

### Explanation

The inner transaction has its own connection/resource transaction and can commit or roll back independently.

### Exam Trap

`REQUIRES_NEW` is not a savepoint inside the outer transaction.

---

## TX-B01-C008 — What resource risk does `REQUIRES_NEW` introduce?

### Russian Translation

Какой ресурсный риск создаёт `REQUIRES_NEW`?

> [!answer]- Answer
> The outer transaction can retain its connection while the inner transaction needs another one, exhausting the pool under concurrency.

### Explanation

Nested service calls can multiply simultaneous connection demand beyond request-thread count.

### Exam Trap

Logical independence does not make the extra physical resource free.

---

## TX-B01-C009 — What does `PROPAGATION_NESTED` normally use for JDBC transactions?

### Russian Translation

Что обычно использует `PROPAGATION_NESTED` для JDBC transactions?

> [!answer]- Answer
> A savepoint inside the existing physical transaction.

### Explanation

The nested scope can roll back to the savepoint while the outer physical transaction continues.

### Exam Trap

An outer rollback still removes work that succeeded before or inside the savepoint.

---

## TX-B01-C010 — How does `NESTED` differ from `REQUIRES_NEW`?

### Russian Translation

Чем `NESTED` отличается от `REQUIRES_NEW`?

> [!answer]- Answer
> `NESTED` normally uses one physical transaction with savepoints; `REQUIRES_NEW` uses a separate independently committed physical transaction.

### Explanation

The distinction determines connection usage, commit independence and what an outer rollback can undo.

### Exam Trap

A nested rollback is partial only within the still-active outer transaction.

---

## TX-B01-C011 — What does `PROPAGATION_SUPPORTS` do?

### Russian Translation

Что делает `PROPAGATION_SUPPORTS`?

> [!answer]- Answer
> It participates in an existing transaction but runs without a physical transaction when none exists.

### Explanation

The same method can therefore execute with different resource synchronization and consistency depending on its caller.

### Exam Trap

`SUPPORTS` does not guarantee a transaction exists.

---

## TX-B01-C012 — What does `PROPAGATION_MANDATORY` enforce?

### Russian Translation

Что гарантирует `PROPAGATION_MANDATORY`?

> [!answer]- Answer
> It requires an existing transaction and throws an exception when called without one.

### Explanation

It protects operations that are valid only as part of a larger atomic use case.

### Exam Trap

`MANDATORY` does not create a transaction when none exists.

---

## TX-B01-C013 — What does `PROPAGATION_NOT_SUPPORTED` do?

### Russian Translation

Что делает `PROPAGATION_NOT_SUPPORTED`?

> [!answer]- Answer
> It suspends an existing transaction and executes the method without a transaction.

### Explanation

The surrounding transaction resumes after the non-transactional method completes.

### Exam Trap

Suspension removes atomicity between that method and the surrounding database work.

---

## TX-B01-C014 — What does `PROPAGATION_NEVER` enforce?

### Russian Translation

Что гарантирует `PROPAGATION_NEVER`?

> [!answer]- Answer
> It executes only when no transaction exists and throws an exception when called inside one.

### Explanation

`NEVER` is a defensive contract for operations that must not participate in transactional context.

### Exam Trap

Do not confuse `NEVER` with `NOT_SUPPORTED`: one rejects a transaction, the other suspends it.

---

## TX-B01-C015 — Why does self-invocation break propagation changes?

### Russian Translation

Почему self-invocation ломает изменение propagation?

> [!answer]- Answer
> An internal `this.method()` call does not cross the transaction proxy, so a second transaction interceptor invocation does not occur.

### Explanation

The call reaches the target method directly inside the same object, bypassing metadata interception for the inner method.

### Exam Trap

Switching from JDK proxy to CGLIB does not solve ordinary self-invocation.

---

## TX-B01-C016 — Which exceptions cause rollback by default?

### Russian Translation

Какие exceptions вызывают rollback по умолчанию?

> [!answer]- Answer
> `RuntimeException` and `Error` cause rollback by default; checked exceptions do not.

### Explanation

Rollback rules are evaluated by the transaction interceptor when the method exits exceptionally.

### Exam Trap

A checked exception must be configured with `rollbackFor` when rollback is required.

---

## TX-B01-C017 — What can happen if a transactional method catches and swallows a runtime exception?

### Russian Translation

Что может произойти, если transactional method поймал и проглотил runtime exception?

> [!answer]- Answer
> The interceptor may observe a normal return and attempt commit, unless the transaction was already marked rollback-only or code marks it explicitly.

### Explanation

Exception handling changes what the interceptor sees at the proxy boundary and therefore changes rollback behavior.

### Exam Trap

Logging an exception is not equivalent to rethrowing it or marking rollback-only.

---

## TX-B01-C018 — What does `rollbackFor` configure?

### Russian Translation

Что настраивает `rollbackFor`?

> [!answer]- Answer
> It adds exception types that must cause rollback, including checked exceptions.

### Explanation

Type-based rules extend the default rollback classifier for the annotated transaction attribute.

### Exam Trap

Overly broad patterns can match unintended exception names; prefer explicit classes.

---

## TX-B01-C019 — What does `readOnly=true` guarantee?

### Russian Translation

Что гарантирует `readOnly=true`?

> [!answer]- Answer
> It expresses a read-only hint or contract to the transaction manager and resource; it is not a universal compile-time prohibition of writes.

### Explanation

Actual effects depend on the manager, JDBC driver, JPA provider, database and routing configuration.

### Exam Trap

Do not assume every provider rejects writes merely because `readOnly=true` is present.

---

## TX-B01-C020 — When does a method-level isolation setting actually take effect?

### Russian Translation

Когда isolation, заданный на method, реально начинает действовать?

> [!answer]- Answer
> When the method starts a new physical transaction, normally with `REQUIRED` when none exists or with `REQUIRES_NEW`.

### Explanation

A participating logical scope cannot normally replace characteristics of an already-started physical transaction.

### Exam Trap

An inner `REQUIRED` method usually inherits the outer transaction's isolation.

---

## TX-B01-C021 — What does `ISOLATION_DEFAULT` mean?

### Russian Translation

Что означает `ISOLATION_DEFAULT`?

> [!answer]- Answer
> Use the default isolation behavior of the selected transaction manager and underlying database or resource.

### Explanation

Spring delegates the effective isolation choice instead of forcing a specific JDBC constant.

### Exam Trap

Default isolation differs across databases and deployment configuration.

---

## TX-B01-C022 — Does `SERIALIZABLE` remove every need for optimistic or pessimistic locking?

### Russian Translation

Устраняет ли `SERIALIZABLE` любую необходимость в optimistic/pessimistic locking?

> [!answer]- Answer
> No. Database semantics, retry behavior, contention and domain-specific lost-update protection still require explicit design.

### Explanation

Higher isolation can reject or serialize conflicting transactions, but business conflict detection and retry policy remain application concerns.

### Exam Trap

Increasing isolation globally is not automatically safer or cheaper than a targeted version check or conditional update.

---

## TX-B01-C023 — What is `TransactionTemplate` used for?

### Russian Translation

Для чего используется `TransactionTemplate`?

> [!answer]- Answer
> It provides programmatic transaction demarcation around an explicit code block.

### Explanation

It is useful when transaction boundaries must be visible in control flow or when orchestration mixes transactional and non-transactional phases.

### Exam Trap

Programmatic management does not remove the need to choose the correct transaction manager and propagation behavior.

---

## TX-B01-C024 — What is a `PlatformTransactionManager` responsible for?

### Russian Translation

За что отвечает `PlatformTransactionManager`?

> [!answer]- Answer
> It obtains transaction status according to a definition and commits or rolls back the selected resource transaction.

### Explanation

Concrete managers adapt JDBC, JPA, JTA or another resource model to Spring's common transaction API.

### Exam Trap

The manager does not make unrelated resources atomic unless it actually coordinates them.

---

## TX-B01-C025 — Does selecting one transaction manager make writes through another manager atomic?

### Russian Translation

Делает ли выбор одного transaction manager операции через другой manager атомарными?

> [!answer]- Answer
> No. Multiple local transaction managers do not become one distributed transaction automatically.

### Explanation

Each manager controls its own resource boundary unless a distributed transaction coordinator or redesign is used.

### Exam Trap

Two `@Transactional` methods using different managers are not one atomic unit merely because they run on the same thread.

---

## TX-B01-C026 — What is `TransactionSynchronization` used for?

### Russian Translation

Для чего используется `TransactionSynchronization`?

> [!answer]- Answer
> It registers callbacks for transaction lifecycle phases such as before commit, after commit and after completion.

### Explanation

Callbacks coordinate side effects with transaction phases but do not change the already-completed durability boundary.

### Exam Trap

An exception in `afterCommit()` cannot undo a database commit that already happened.

---

## TX-B01-C027 — What is the default phase of `@TransactionalEventListener`?

### Russian Translation

Какова default phase у `@TransactionalEventListener`?

> [!answer]- Answer
> `AFTER_COMMIT`.

### Explanation

Other phases include `BEFORE_COMMIT`, `AFTER_ROLLBACK` and `AFTER_COMPLETION`, allowing event handling to align with transaction outcome.

### Exam Trap

Without an active transaction, the listener normally does not run unless fallback execution is enabled.

---

## TX-B01-C028 — Is `@TransactionalEventListener(AFTER_COMMIT)` a durable messaging guarantee?

### Russian Translation

Является ли `@TransactionalEventListener(AFTER_COMMIT)` гарантией надёжной доставки сообщения?

> [!answer]- Answer
> No. The database can commit and the process can crash before the listener completes its external side effect.

### Explanation

The callback aligns timing with commit but does not persist publication intent for recovery after process failure.

### Exam Trap

After-commit timing is not equivalent to Transactional Outbox durability.

---

## TX-B01-C029 — Does an imperative transaction automatically cross an `@Async` boundary?

### Russian Translation

Переходит ли imperative transaction автоматически через границу `@Async`?

> [!answer]- Answer
> No. Imperative transactions are commonly thread-bound, while the async method runs on another thread.

### Explanation

The worker thread receives no inherited JDBC/JPA transaction context unless it starts its own transaction.

### Exam Trap

Passing an entity or request object to another thread does not transfer the transaction.

---

## TX-B01-C030 — Why is a long remote call inside a database transaction dangerous?

### Russian Translation

Почему длинный remote call внутри DB transaction опасен?

> [!answer]- Answer
> It keeps connections and locks open during network waiting, increasing pool pressure, lock contention and timeout coupling.

### Explanation

The database resource remains occupied while the application waits on an independent failure domain.

### Exam Trap

A transaction timeout does not make a slow remote dependency operationally cheap.

---

## TX-B01-C031 — What atomic guarantee does Transactional Outbox provide?

### Russian Translation

Какую атомарную гарантию даёт Transactional Outbox?

> [!answer]- Answer
> The business change and durable publication intent are committed in the same local database transaction.

### Explanation

A separate relay can retry publication because the intent survives process crashes together with the business state.

### Exam Trap

Outbox does not make the database and broker one physical transaction.

---

## TX-B01-C032 — Why must outbox consumers be idempotent?

### Russian Translation

Почему consumers outbox-событий должны быть idempotent?

> [!answer]- Answer
> A crash can occur after broker acceptance but before the outbox row is marked published, causing the same message to be sent again.

### Explanation

The relay normally provides at-least-once delivery, so consumers must detect or safely repeat duplicate effects.

### Exam Trap

Outbox removes the dual-write gap but does not guarantee exactly-once business processing by itself.

---

# Review matrix

| Confusion pair | Correct distinction |
|---|---|
| logical vs physical transaction | method scope vs actual resource transaction |
| `REQUIRED` vs `REQUIRES_NEW` | join/create vs suspend/create independent |
| `REQUIRES_NEW` vs `NESTED` | independent transaction vs savepoint |
| `MANDATORY` vs `NEVER` | transaction required vs forbidden |
| checked vs runtime exception | checked commits by default, runtime rolls back |
| read-only vs no writes | hint/contract vs universal enforcement |
| after-commit vs durable delivery | phase callback vs persisted intent |
| async vs same transaction | worker thread gets separate/no transaction |
