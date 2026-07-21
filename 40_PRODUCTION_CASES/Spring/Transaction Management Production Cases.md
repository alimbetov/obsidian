---
type: production-case-set
domain: spring
subdomain: transactions
status: active
case_count: 15
tags:
  - spring
  - transaction
  - propagation
  - isolation
  - rollback
  - outbox
  - production
---

# Spring Transaction Management Production Cases

> [!summary]
> 15 кейсов от production symptom к logical/physical transaction timeline, root cause, доказательству и исправлению.

---

# Case 1. `UnexpectedRollbackException` после пойманного exception

## Симптом

Service ловит ошибку платежа и успешно доходит до `return`, но caller получает:

```text
UnexpectedRollbackException:
Transaction rolled back because it has been marked as rollback-only
```

## Код

```java
@Transactional
public OrderResult createOrder(CreateOrderCommand command) {
    orderRepository.insert(command.order());

    try {
        paymentService.reserve(command.payment());
    } catch (PaymentRejectedException error) {
        orderRepository.markPaymentPending(command.orderId());
    }

    return OrderResult.accepted(command.orderId());
}
```

```java
@Transactional
public void reserve(Payment payment) {
    paymentRepository.insertAttempt(payment);
    throw new PaymentRejectedException();
}
```

## Timeline

```text
TX-1 begins
outer logical scope enters
inner REQUIRED logical scope joins TX-1
inner throws RuntimeException
TX-1 marked rollback-only
outer catches exception
outer requests commit
manager rolls TX-1 back
UnexpectedRollbackException returned
```

## Root cause

Two logical scopes share one physical transaction.

## Доказательство

- включить transaction debug logging;
- вывести transaction active/rollback state;
- убрать catch и сравнить outcome;
- временно заменить inner propagation на `REQUIRES_NEW` в lab, не как автоматический production fix.

## Исправления

- дать exception выйти наружу, если операция должна rollback полностью;
- выразить payment rejection как expected result без runtime exception;
- сохранить failure status в отдельной `REQUIRES_NEW` transaction, если это независимый record;
- применить `NESTED`, если нужен savepoint и manager/database поддерживают его.

---

# Case 2. `REQUIRES_NEW` исчерпал connection pool

## Симптом

Под нагрузкой все request threads зависают. Hikari metrics:

```text
active = 30
idle = 0
pending = 30
pool size = 30
```

## Код

```java
@Transactional
public void process(Payment payment) {
    paymentRepository.lock(payment.id());
    auditService.record(payment);
}
```

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void record(Payment payment) {
    auditRepository.insert(payment);
}
```

## Timeline

```text
30 threads each hold outer connection
30 threads request second connection for REQUIRES_NEW
pool has no free connection
all outer transactions wait while holding their connections
```

## Root cause

Independent inner physical transactions need additional resources.

## Исправления

- increase pool only after workload calculation;
- remove unnecessary `REQUIRES_NEW`;
- move audit to outbox/async pipeline;
- shorten outer transaction;
- bound concurrent requests;
- isolate audit datasource/pool if independence is required.

## Lesson

> Propagation is also resource-allocation policy.

---

# Case 3. `NESTED` behaves like unsupported operation

## Симптом

Developer expects row-level savepoint rollback, but startup or invocation throws nested-transaction-not-supported exception.

## Root cause

Selected transaction manager or resource does not support savepoint-based nested transactions in this configuration.

## Checks

- actual `PlatformTransactionManager` class;
- JDBC savepoint support;
- `nestedTransactionAllowed` configuration where relevant;
- whether JPA and direct JDBC use the same underlying connection;
- database driver behavior.

## Incorrect assumption

```text
Propagation.NESTED always works because the enum exists
```

## Fix

- use `DataSourceTransactionManager` with supported JDBC savepoints where appropriate;
- redesign batch with explicit per-item `REQUIRES_NEW` and pool analysis;
- validate all rows before one atomic write;
- stage data and apply valid rows separately.

---

# Case 4. Checked exception committed partial data

## Симптом

Import failed with `IOException`, but `IMPORT_STARTED` row remained committed.

## Code

```java
@Transactional
public void importFile(Path file) throws IOException {
    importRepository.markStarted(file.toString());
    parser.parse(file); // IOException
    importRepository.markCompleted(file.toString());
}
```

## Root cause

Checked exception does not trigger rollback by default.

## Fix

```java
@Transactional(rollbackFor = IOException.class)
public void importFile(Path file) throws IOException {
    // ...
}
```

Alternative: translate infrastructure checked exception into a domain runtime exception at the correct boundary.

## Test

Assert database state after an actual committed integration-test boundary, not only that exception was thrown.

---

# Case 5. Exception swallowed, transaction committed

## Симптом

Service logs database constraint error but returns `SUCCESS`; some previous writes commit.

## Code

```java
@Transactional
public Result execute(Command command) {
    try {
        repository.insertHeader(command);
        repository.insertDetails(command);
        return Result.success();
    } catch (RuntimeException error) {
        log.error("failed", error);
        return Result.success();
    }
}
```

## Root cause

Transaction interceptor sees normal return. Depending on resource behavior, transaction may commit or may fail later because it was marked rollback-only.

## Fix

- rethrow exception;
- return failure only after explicitly marking rollback-only;
- separate expected validation from exceptional infrastructure failure;
- do not use catch as a transaction policy substitute.

---

# Case 6. Inner `SERIALIZABLE` method still runs at `READ_COMMITTED`

## Symptom

Developer adds:

```java
@Transactional(isolation = Isolation.SERIALIZABLE)
public void allocateLimit() {}
```

but concurrent anomaly remains.

## Caller

```java
@Transactional(isolation = Isolation.READ_COMMITTED)
public void approve() {
    limitService.allocateLimit();
}
```

## Root cause

Inner `REQUIRED` method joins the already-created physical transaction. Local isolation does not restart it.

## Proof

Inspect current isolation through `TransactionSynchronizationManager` and database session metadata.

## Fixes

- define isolation at outer transaction boundary;
- use `REQUIRES_NEW` only if independent transaction semantics are correct;
- enable validation of existing transaction characteristics;
- solve lost update with versioning/atomic update/lock instead of blindly raising isolation.

---

# Case 7. `readOnly=true` did not prevent UPDATE

## Symptom

Method annotated `readOnly=true` successfully changes data in one environment but fails in another.

## Root cause

`readOnly` is a hint/contract whose enforcement depends on transaction manager, provider, driver and database.

## Bad assumption

```text
readOnly=true is a language-level write guard
```

## Fix

- design query services without mutation methods;
- use database permissions/read-only replica where hard enforcement is required;
- test actual provider behavior;
- enable strict mismatch validation for nested scopes where supported.

---

# Case 8. `@Async` method sees no caller transaction

## Symptom

Async listener cannot access lazy relation and writes are not rolled back with caller operation.

## Code

```java
@Transactional
public void createOrder(Order order) {
    repository.save(order);
    notificationService.send(order);
}
```

```java
@Async
public void send(Order order) {
    order.getItems().size();
    auditRepository.insert(...);
}
```

## Root cause

Async method runs on another thread. Imperative transaction and persistence context are thread-bound.

## Fix

```java
@Async
@Transactional
public void send(long orderId) {
    Order order = repository.getRequired(orderId);
    // own worker transaction
}
```

Pass immutable IDs/DTOs, not attached entities.

## Important

Worker transaction is independent from caller transaction. If caller rolls back after scheduling, worker may observe missing data or execute too early. Use after-commit event or outbox based on required durability.

---

# Case 9. Two databases partially committed

## Symptom

Core account row commits, reporting database insert fails. Developer expected one `@Transactional` to rollback both.

## Code

```java
@Transactional("coreTransactionManager")
public void createCustomer(Customer customer) {
    coreRepository.insert(customer);
    reportingRepository.insert(customer);
}
```

## Root cause

`reportingRepository` uses another datasource/transaction manager. One local manager does not control both resources atomically.

## Fix options

- JTA/XA after operational evaluation;
- outbox/eventual projection;
- one database owns source of truth;
- retryable reporting projection;
- reconciliation job.

## Lesson

> Multiple local transactions do not become distributed atomicity by annotation proximity.

---

# Case 10. Cache contains data that DB rolled back

## Symptom

API returns a new product price from Redis, but database still contains the old price after transaction failure.

## Failure sequence

```text
DB update executed
cache put performed
DB commit fails
cache has uncommitted value
```

## Root causes

- cache interceptor order;
- cache manager not transaction-aware;
- manual cache put inside transaction;
- external Redis cannot participate atomically in local DB transaction.

## Fixes

- evict after successful commit;
- defer cache operation through transaction synchronization;
- prefer invalidation over speculative cache put;
- use versioned values;
- publish durable invalidation through outbox for distributed L1 caches.

---

# Case 11. `AFTER_COMMIT` listener lost Kafka event

## Symptom

Order is committed, but `OrderCreated` message is absent. Logs stop immediately after database commit due to process crash.

## Code

```java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void publish(OrderCreated event) {
    kafkaTemplate.send("orders", event);
}
```

## Root cause

After-commit callback is not durable publication intent. Database commit cannot be undone after listener failure.

## Fix

Write business state and outbox row in one transaction. Relay publishes and retries later.

## Use listener when

- side effect is best-effort;
- loss is acceptable;
- operation is local and recoverable.

---

# Case 12. Outbox event published twice

## Symptom

Consumer creates duplicate invoice for one order.

## Relay timeline

```text
relay publishes event
broker acknowledges
process crashes before markPublished
row retried
same event published again
```

## Root cause

At-least-once delivery window.

## Fix

Consumer inbox/idempotency:

```sql
primary key (consumer_name, message_id)
```

and business unique constraints where appropriate.

## Lesson

> Outbox guarantees durable intent, not duplicate-free delivery by magic.

---

# Case 13. HTTP call inside transaction saturated DB pool

## Symptom

Fraud service latency rises from 100 ms to 5 s. Database pool pending count rises even though SQL latency is low.

## Code

```java
@Transactional
public void approve(long id) {
    Loan loan = repository.getForUpdate(id);
    FraudResult fraud = fraudClient.check(loan);
    repository.apply(fraud);
}
```

## Root cause

Connection and row locks remain held during remote network wait.

## Fix

State machine:

```text
TX-1 mark FRAUD_CHECK_PENDING + outbox
remote check outside DB transaction
TX-2 validate version + apply result
```

For synchronous design, move remote call outside transaction and use optimistic version validation before update.

---

# Case 14. Transactional test passed but production failed at commit

## Symptom

Integration test passes, but production gets unique constraint violation at transaction commit.

## Cause

Test never flushed or observed real commit because framework rolled the test transaction back after method completion.

## Fixes

- explicit flush;
- explicit commit with test transaction utilities;
- use `TransactionTemplate` in test;
- assert after transaction completion;
- add real concurrent/constraint integration test.

## Additional trap

Preemptive test timeout can execute code on another thread outside the test-managed transaction.

---

# Case 15. Opposite account lock order caused deadlock

## Symptom

Database reports deadlock between two transfers:

```text
transfer A → B
transfer B → A
```

## Code

```java
@Transactional
public void transfer(long sourceId, long targetId, Money amount) {
    Account source = repository.getForUpdate(sourceId);
    Account target = repository.getForUpdate(targetId);
    // mutate
}
```

Two transactions lock rows in opposite order.

## Fix

Canonical lock order:

```java
long first = Math.min(sourceId, targetId);
long second = Math.max(sourceId, targetId);

Account a = repository.getForUpdate(first);
Account b = repository.getForUpdate(second);
```

Then map back to source/target roles.

Also:

- keep transaction short;
- retry deadlock victim transaction with bounded backoff;
- avoid remote calls while locks are held;
- collect deadlock graphs and SQL statements.

---

# Production checklist

Before approving a transaction design:

1. Where does caller cross proxy?
2. Which transaction manager is selected?
3. How many logical scopes exist?
4. How many physical transactions exist?
5. Which propagation branch applies?
6. Which exceptions roll back?
7. Can rollback-only be hidden by catch?
8. Does isolation apply to a new or joined transaction?
9. Are locks acquired in stable order?
10. How long is connection held?
11. Is remote I/O inside transaction?
12. What happens at cache update time?
13. What happens after process crash?
14. Is event delivery durable?
15. Are retries and consumers idempotent?

# Related materials

- [[10_CONCEPTS/Spring/Transactions/Spring Transaction Management Deep Dive]]
- [[10_CONCEPTS/Spring/Transactions/Transactional Outbox and Commit Boundaries]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/TX-B01/TX-B01 Cards]]
- [[50_LABS/Spring/TX-B01/README]]
