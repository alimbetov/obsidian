---
type: lab
domain: spring
subdomain: transactions
status: active
spring_version: 5.3.39
java_version: 8
database: H2 2.1.214
tags:
  - spring
  - transaction
  - propagation
  - rollback
  - outbox
  - lab
---

# Spring TX-B01 Lab

## Цель

Увидеть реальные transaction outcomes в database, а не только прочитать определения:

- proxy boundary;
- logical scopes и physical transactions;
- `REQUIRED` и `UnexpectedRollbackException`;
- независимый commit `REQUIRES_NEW`;
- `NESTED` savepoint;
- default checked-exception commit;
- `rollbackFor`;
- `TransactionTemplate`;
- `TransactionSynchronization.afterCommit()`;
- `@TransactionalEventListener(AFTER_COMMIT)`;
- thread-bound transaction;
- isolation introspection;
- atomic business-row + outbox-row write;
- physical begin/commit/rollback counters.

## Версии

```text
Java              8+
Spring Framework  5.3.39
H2                 2.1.214
```

## Запуск

```bash
cd 50_LABS/Spring/TX-B01
mvn clean compile exec:java
```

Требуются Maven и доступ к Maven Central при первом dependency resolution.

---

# 1. Proxy diagnostics

Lab сначала выводит:

```text
requiredOuterService: proxy=true, cglib=true, target=RequiredOuterService
requiresNewOuterService: proxy=true, cglib=true, target=RequiresNewOuterService
nestedImportService: proxy=true, cglib=true, target=NestedImportService
```

Точное generated class name не фиксируется. Важно:

```text
Spring-managed bean
    ↓
transaction advisor
    ↓
CGLIB proxy
```

---

# 2. `REQUIRED` и `UnexpectedRollbackException`

Outer method:

```java
@Transactional
public void createOrderAndCatchPaymentFailure(String orderNo) {
    jdbc.update("insert into orders(order_no) values (?)", orderNo);

    try {
        inner.reserveThenFail(orderNo);
    } catch (PaymentRejected error) {
        System.out.println("outer caught");
    }
}
```

Inner method:

```java
@Transactional
public void reserveThenFail(String orderNo) {
    jdbc.update("insert into payments(order_no) values (?)", orderNo);
    throw new PaymentRejected("payment rejected");
}
```

Expected logical sequence:

```text
outer REQUIRED logical scope
    ↓
physical TX-1 begins
    ↓
inner REQUIRED logical scope joins TX-1
    ↓
inner RuntimeException marks TX-1 rollback-only
    ↓
outer catches exception and returns normally
    ↓
outer requests commit
    ↓
TX-1 rolls back
    ↓
caller receives UnexpectedRollbackException
```

Expected database result:

```text
orders committed   = 0
payments committed = 0
```

## Experiment

Replace inner propagation with `REQUIRES_NEW` and predict:

- what commits;
- what exception outer receives;
- how many physical transactions begin.

Do not treat this change as an automatic production fix; first decide whether payment attempt is truly independent.

---

# 3. `REQUIRES_NEW` independent commit

Outer:

```java
@Transactional
public void createOrderThenFail(String orderNo) {
    jdbc.update("insert into orders(order_no) values (?)", orderNo);
    auditService.record("created " + orderNo);
    throw new BusinessFailure("outer business failure");
}
```

Audit:

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void record(String message) {
    jdbc.update("insert into audit_log(message) values (?)", message);
}
```

Expected:

```text
outer TX-1 begins
TX-1 suspended
inner TX-2 begins
TX-2 commits audit
TX-1 resumes
outer throws
TX-1 rolls back
```

Database:

```text
orders committed = 0
audit committed  = 1
```

## Production question

What happens with 50 concurrent outer transactions and a connection pool of 50 when every thread requests one `REQUIRES_NEW` connection?

---

# 4. `NESTED` savepoint

Input:

```text
GOOD-1
BAD-2
GOOD-3
```

Worker:

```java
@Transactional(propagation = Propagation.NESTED)
public void importOne(String value) {
    jdbc.update("insert into imported_row(row_value) values (?)", value);

    if (value.startsWith("BAD")) {
        throw new InvalidRow(value);
    }
}
```

Expected:

```text
GOOD-1 inserted
savepoint for BAD-2
BAD-2 inserted then rolled back to savepoint
GOOD-3 inserted
outer transaction commits
```

Database:

```text
committed rows = [GOOD-1, GOOD-3]
```

## Experiments

1. Replace `NESTED` with `REQUIRED`.
2. Predict whether catching `InvalidRow` saves the outer transaction.
3. Replace `NESTED` with `REQUIRES_NEW`.
4. Compare physical transaction counters.
5. Throw after all rows and observe that successful nested work also disappears with outer rollback.

---

# 5. Checked exception rules

Default method:

```java
@Transactional
public void defaultCheckedException(String marker) throws IOException {
    jdbc.update("insert into tx_marker(marker) values (?)", marker);
    throw new IOException("checked failure");
}
```

Expected:

```text
IOException reaches caller
marker commits
```

Rollback rule:

```java
@Transactional(rollbackFor = IOException.class)
public void rollbackForCheckedException(String marker) throws IOException {
    jdbc.update("insert into tx_marker(marker) values (?)", marker);
    throw new IOException("checked failure with rollback rule");
}
```

Expected:

```text
IOException reaches caller
marker rolls back
```

Output:

```text
DEFAULT-CHECKED committed = true
ROLLBACK-CHECKED committed = false
```

---

# 6. `TransactionTemplate`

```java
template.execute(status -> {
    jdbc.update("insert into tx_marker(marker) values (?)", marker);

    if (rollback) {
        status.setRollbackOnly();
    }

    return null;
});
```

Expected:

```text
TEMPLATE-COMMIT   exists
TEMPLATE-ROLLBACK does not exist
```

This demonstrates an explicit code-block boundary without relying on another proxy call.

## Experiment

Use two sequential `TransactionTemplate.execute()` calls:

```text
TX-1 commits step A
TX-2 fails step B
```

Discuss whether compensation is required.

---

# 7. Synchronization callbacks

Inside a transaction:

```java
TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                System.out.println("direct afterCommit callback");
            }
        }
);
```

Expected:

```text
TX commit
direct afterCommit callback
```

Important:

```text
afterCommit failure cannot undo the database commit
```

Use this for non-durable local completion logic or as part of an explicit consistency design.

---

# 8. `@TransactionalEventListener`

Publisher inside transaction:

```java
publisher.publishEvent(new MarkerCommittedEvent(marker));
```

Listener:

```java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void onMarkerCommitted(MarkerCommittedEvent event) {
    // local callback
}
```

Expected:

```text
events after commit = 1
```

## Experiments

1. Throw after publishing event but before commit.
2. Change phase to `AFTER_ROLLBACK`.
3. Publish outside a transaction.
4. Add `fallbackExecution=true` and compare behavior.
5. Simulate process failure after DB commit and explain why this is not durable broker delivery.

---

# 9. Thread boundary

Caller transaction:

```text
caller thread active = true
```

Worker executor thread:

```text
worker thread active = false
```

Imperative transaction is not copied to a new thread.

## Production consequence

Passing a JPA entity to `@Async` does not transfer its persistence context. Pass an ID/immutable command and load data inside the worker’s own transaction.

---

# 10. Isolation introspection

```java
@Transactional(isolation = Isolation.SERIALIZABLE)
public void serializableOperation() {
    Integer isolation = TransactionSynchronizationManager
            .getCurrentTransactionIsolationLevel();
}
```

The lab prints Spring’s isolation code for the active transaction.

## Deeper experiment

Create an outer `READ_COMMITTED` method that calls an inner `REQUIRED + SERIALIZABLE` method while `setValidateExistingTransaction(true)` is enabled. Observe mismatch validation.

---

# 11. Transactional Outbox atomicity

Writer:

```java
@Transactional
public void createOrder(String orderNo, boolean fail) {
    insertOrder(orderNo);
    insertOutboxEvent(orderNo);

    if (fail) {
        throw new BusinessFailure();
    }
}
```

Expected:

```text
ORD-OUTBOX-OK:
  order exists  = true
  outbox exists = true

ORD-OUTBOX-FAIL:
  order exists  = false
  outbox exists = false
```

The atomic guarantee is:

```text
business state and publication intent commit together
```

It is not:

```text
database and broker commit as one physical transaction
```

---

# 12. Simplified relay

The lab relay:

```text
reads PENDING rows
prints simulated publish
uses a short TransactionTemplate
marks row PUBLISHED
```

It intentionally remains simple.

A production relay also needs:

- safe multi-worker claiming;
- retry backoff and jitter;
- uncertain broker acknowledgment handling;
- lease recovery;
- dead-letter state;
- aggregate ordering;
- cleanup/partitioning;
- metrics for backlog and oldest pending age;
- idempotent consumers.

---

# 13. Physical transaction counters

`CountingTransactionManager` subclasses `DataSourceTransactionManager` and counts:

```text
doBegin
doCommit
doRollback
```

This makes the difference observable:

```text
number of @Transactional methods
≠
number of physical transactions
```

For example, an inner `REQUIRED` method does not call `doBegin()` for another physical transaction, while `REQUIRES_NEW` does.

---

# 14. Suggested extensions

## A. Propagation matrix lab

Add methods for:

```text
SUPPORTS
MANDATORY
NOT_SUPPORTED
NEVER
```

Print:

```java
TransactionSynchronizationManager.isActualTransactionActive()
```

Call each method both inside and outside an outer transaction.

## B. Connection pool exhaustion

Replace direct H2 datasource with HikariCP:

```text
maximumPoolSize = concurrent outer threads
```

Run concurrent outer `REQUIRES_NEW` calls and observe pending connections. Use strict timeouts to prevent a permanent test hang.

## C. Concurrent lost update

Use two threads and latches:

```text
both read same balance/version
both attempt update
```

Compare:

- naive read-modify-write;
- optimistic version column;
- `SELECT ... FOR UPDATE`;
- atomic conditional update.

## D. Cache ordering

Inside transaction, manually put a new value into cache, then force DB rollback. Observe stale cache. Move invalidation to after commit and compare.

## E. Outbox duplicate

Simulate:

```text
publish succeeds
markPublished is skipped
relay runs again
```

Add consumer inbox table with a unique `(consumer, event_id)` key.

---

# 15. Validation status

- Maven project structure is present.
- Source targets Java 8 and Spring Framework 5.3.39.
- H2 provides observable commit/rollback state without an external database.
- Full Maven execution must be performed in an environment with Maven and dependency resolution.
- PostgreSQL-specific isolation, locking and `SKIP LOCKED` behavior must be tested against PostgreSQL rather than inferred from H2.

# Related

- [[10_CONCEPTS/Spring/Transactions/Spring Transaction Management Deep Dive]]
- [[10_CONCEPTS/Spring/Transactions/Transactional Outbox and Commit Boundaries]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/TX-B01/TX-B01 Cards]]
- [[40_PRODUCTION_CASES/Spring/Transaction Management Production Cases]]
- [[98_SOURCES/Spring Transaction Management Sources]]
