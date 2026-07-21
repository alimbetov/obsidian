---
type: concept
domain: spring
subdomain: transactions
difficulty: advanced
status: learning
interview: true
certification:
  - spring-2V0-72.22
spring_versions:
  - 5.3.39
java_versions:
  - 8
  - 21
production_relevance: critical
prerequisites:
  - "[[Spring AOP Visual Deep Dive]]"
  - "[[Spring Transaction Management Deep Dive]]"
related:
  - "[[Transactional Outbox and Commit Boundaries]]"
  - "[[Spring Cache Visual Deep Dive]]"
tags:
  - spring
  - transactions
  - propagation
  - diagrams
  - visual-learning
---

# Spring Transaction Management Visual Deep Dive

> [!summary]
> Главная ошибка при изучении Spring transactions — смешение **method scope**, **logical transaction scope**, **physical database transaction**, **connection** и **commit boundary**. Эта заметка разделяет их визуально.

# 1. Runtime pipeline

```mermaid
sequenceDiagram
    participant C as Caller
    participant P as Spring proxy
    participant TI as TransactionInterceptor
    participant TM as PlatformTransactionManager
    participant R as JDBC or JPA resource
    participant T as Target service

    C->>P: placeOrder(command)
    P->>TI: invoke
    TI->>TM: getTransaction(attributes)
    TM->>R: bind connection or EntityManager
    TI->>T: invoke target
    T-->>TI: result or exception
    alt success
        TI->>TM: commit
        TM->>R: physical commit
    else rollback rule matches
        TI->>TM: rollback
        TM->>R: physical rollback
    end
    TI-->>P: return or throw
    P-->>C: return or throw
```

`@Transactional` metadata начинает работать только при вызове через transaction proxy.

# 2. Logical scope и physical transaction

```mermaid
flowchart TB
    Physical["Physical DB transaction TX-1"] --> Outer["Logical scope: OrderService.create"]
    Physical --> Payment["Logical scope: PaymentService.reserve"]
    Physical --> Stock["Logical scope: StockService.reserve"]
```

Три annotated methods могут участвовать в одной physical transaction.

```text
logical scope   → правила конкретного intercepted method
physical TX     → реальный commit/rollback database resource
```

# 3. `REQUIRED`: присоединение к существующей transaction

```mermaid
sequenceDiagram
    participant C as Caller
    participant O as OrderService proxy
    participant P as PaymentService proxy
    participant DB as Database TX-1

    C->>O: createOrder()
    O->>DB: begin TX-1
    O->>P: reservePayment()
    P->>DB: join TX-1
    P->>DB: insert reservation
    P-->>O: success
    O->>DB: insert order
    O->>DB: commit TX-1
    O-->>C: result
```

`REQUIRED` не означает «всегда создать новую transaction». Оно означает «использовать существующую либо создать, если её нет».

# 4. `UnexpectedRollbackException`

```mermaid
sequenceDiagram
    participant C as Caller
    participant O as Outer REQUIRED
    participant I as Inner REQUIRED
    participant TX as Physical TX-1

    C->>O: execute()
    O->>TX: begin
    O->>I: innerWork()
    I->>TX: join
    I-->>O: throws RuntimeException
    I->>TX: mark rollback-only
    O->>O: catch exception
    O->>TX: request commit
    TX->>TX: rollback because rollback-only
    TX-->>O: UnexpectedRollbackException
    O-->>C: UnexpectedRollbackException
```

## Почему catch не спасает

Outer method поймал Java exception, но physical transaction уже помечена rollback-only внутренним logical scope.

# 5. `REQUIRES_NEW`

```mermaid
sequenceDiagram
    participant C as Caller
    participant O as Outer service
    participant I as Audit service
    participant TX1 as TX-1
    participant TX2 as TX-2

    C->>O: process()
    O->>TX1: begin
    O->>I: writeAudit()
    I->>TX1: suspend
    I->>TX2: begin independent transaction
    I->>TX2: insert audit
    I->>TX2: commit
    I->>TX1: resume
    O->>TX1: business failure
    O->>TX1: rollback
```

Audit row может остаться committed, хотя outer business transaction откатилась.

# 6. Connection-pool pressure при `REQUIRES_NEW`

```mermaid
flowchart TB
    T1["Thread 1 holds connection for TX-1"] --> W1["Needs second connection for TX-2"]
    T2["Thread 2 holds connection for TX-1"] --> W2["Needs second connection for TX-2"]
    T3["Thread 3 holds connection for TX-1"] --> W3["Needs second connection for TX-2"]
    Pool["Pool exhausted"] --> W1
    Pool --> W2
    Pool --> W3
```

Если каждый request удерживает outer connection и ждёт ещё одну, pool должен выдержать nested demand. Иначе возможен взаимный starvation.

# 7. `NESTED`: savepoint, а не independent transaction

```mermaid
sequenceDiagram
    participant O as Outer transaction
    participant DB as Physical TX-1
    participant N as Nested scope

    O->>DB: begin TX-1
    O->>DB: insert GOOD-1
    O->>N: process BAD-2
    N->>DB: create savepoint SP-1
    N->>DB: insert BAD-2
    N-->>O: failure
    O->>DB: rollback to SP-1
    O->>DB: insert GOOD-3
    O->>DB: commit TX-1
```

```text
REQUIRES_NEW → independent physical transaction
NESTED       → savepoint inside one physical transaction
```

Поддержка зависит от transaction manager и resource capabilities.

# 8. Propagation decision model

```mermaid
flowchart TD
    A["Method invoked"] --> B{"Existing transaction?"}
    B -->|No| C{"Propagation"}
    B -->|Yes| D{"Propagation"}

    C -->|REQUIRED| C1["Create transaction"]
    C -->|REQUIRES_NEW| C2["Create transaction"]
    C -->|NESTED| C3["Usually create transaction"]
    C -->|SUPPORTS| C4["Run without transaction"]
    C -->|MANDATORY| C5["Throw exception"]
    C -->|NOT_SUPPORTED| C6["Run without transaction"]
    C -->|NEVER| C7["Run without transaction"]

    D -->|REQUIRED| D1["Join existing"]
    D -->|REQUIRES_NEW| D2["Suspend and create new"]
    D -->|NESTED| D3["Create savepoint"]
    D -->|SUPPORTS| D4["Participate"]
    D -->|MANDATORY| D5["Participate"]
    D -->|NOT_SUPPORTED| D6["Suspend and run without"]
    D -->|NEVER| D7["Throw exception"]
```

# 9. Rollback rules

```mermaid
flowchart TD
    E["Method throws"] --> T{"Exception type"}
    T -->|RuntimeException| R["Rollback by default"]
    T -->|Error| R
    T -->|Checked exception| C["Commit by default"]
    C --> O{"rollbackFor configured?"}
    O -->|Yes| R
    O -->|No| Commit["Commit if no other rollback-only marker"]
```

## Checked exception trap

```java
@Transactional(rollbackFor = IOException.class)
public void importFile(Path file) throws IOException {
    repository.markStarted(file);
    parser.parse(file);
}
```

Без `rollbackFor`, `IOException` по умолчанию не требует rollback.

# 10. Isolation и competing transactions

## Lost update without protection

```mermaid
sequenceDiagram
    participant A as Transaction A
    participant DB as Account row balance=100
    participant B as Transaction B

    A->>DB: read balance 100
    B->>DB: read balance 100
    A->>DB: write balance 80
    B->>DB: write balance 70
    Note over A,B: A update is lost
```

## Optimistic version check

```mermaid
sequenceDiagram
    participant A as Transaction A
    participant DB as Row version=5
    participant B as Transaction B

    A->>DB: read version 5
    B->>DB: read version 5
    A->>DB: update where version=5, set version=6
    DB-->>A: 1 row updated
    B->>DB: update where version=5
    DB-->>B: 0 rows updated
    B-->>B: optimistic-lock conflict
```

Isolation level и locking strategy решают разные классы anomalies. Высокая isolation не заменяет осознанную lost-update protection во всех сценариях.

# 11. `readOnly=true`

```mermaid
flowchart LR
    Annotation["@Transactional readOnly=true"] --> TM["Transaction manager hint"]
    TM --> Driver["JDBC driver behavior"]
    TM --> ORM["ORM flush behavior"]
    Driver --> DB["Database enforcement may vary"]
    ORM --> DB
```

`readOnly=true` не является универсальным Java-level запретом на `INSERT` или `UPDATE`.

# 12. TransactionSynchronization lifecycle

```mermaid
sequenceDiagram
    participant S as Service
    participant TX as Transaction manager
    participant Sync as Synchronization callback
    participant DB as Database

    S->>TX: begin
    S->>DB: write rows
    S->>Sync: register callback
    TX->>Sync: beforeCommit
    TX->>Sync: beforeCompletion
    TX->>DB: physical commit
    TX->>Sync: afterCommit
    TX->>Sync: afterCompletion(COMMITTED)
```

`afterCommit` выполняется после успешного commit, но failure во внешней системе после этого уже не может откатить database transaction.

# 13. Cache invalidation after commit

```mermaid
sequenceDiagram
    participant S as Update service
    participant DB as Database
    participant TX as Transaction
    participant Cache as Cache

    S->>TX: begin
    S->>DB: update product
    S->>TX: register afterCommit eviction
    TX->>DB: commit
    alt commit succeeds
        TX->>Cache: evict product key
    else rollback
        Note right of Cache: cache operation skipped
    end
```

Это уменьшает риск «cache содержит state, которого нет в DB», но не даёт atomic guarantee между DB и external cache.

# 14. Async/thread boundary

```mermaid
sequenceDiagram
    participant C as Caller thread with TX-1
    participant E as Executor
    participant W as Worker thread
    participant DB as Database

    C->>DB: work inside TX-1
    C->>E: submit async task
    E->>W: run task
    Note right of W: TX-1 thread-local context is absent
    W->>DB: operation without TX-1
```

Async method должна открыть собственную transaction, если её работа должна быть transactional.

# 15. Multiple datasources и dual write

```mermaid
sequenceDiagram
    participant S as Service
    participant A as Database A
    participant B as Database B

    S->>A: commit business row
    A-->>S: success
    S->>B: commit reporting row
    B-->>S: failure
    Note over A,B: partial commit
```

Один local `PlatformTransactionManager` не превращает две независимые databases в одну atomic transaction.

# 16. Transactional Outbox

```mermaid
sequenceDiagram
    participant S as OrderService
    participant DB as Database transaction
    participant Relay as Outbox relay
    participant Broker as Kafka or RabbitMQ

    S->>DB: insert order
    S->>DB: insert outbox event
    S->>DB: commit both atomically
    Relay->>DB: claim pending event
    Relay->>Broker: publish event
    Broker-->>Relay: acknowledgement
    Relay->>DB: mark published
```

Outbox закрывает atomicity gap между business state и durable publication intent, но delivery обычно остаётся at-least-once.

# 17. Remote call inside transaction

```mermaid
sequenceDiagram
    participant S as Service
    participant DB as DB connection
    participant HTTP as Remote service

    S->>DB: begin and update
    S->>HTTP: blocking request
    Note over S,DB: connection remains held
    HTTP-->>S: slow response or timeout
    S->>DB: commit or rollback
```

Долгий remote call увеличивает transaction duration, lock time и pool occupancy.

# 18. Diagnostic decision tree

```mermaid
flowchart TD
    A["Transaction behavior is unexpected"] --> B{"Call crossed Spring proxy?"}
    B -->|No| B1["Check self-invocation, private/final, manual new"]
    B -->|Yes| C{"Correct transaction manager selected?"}
    C -->|No| C1["Check qualifier and datasource ownership"]
    C -->|Yes| D{"Existing transaction already present?"}
    D -->|Yes| D1["Inspect propagation and logical scopes"]
    D -->|No| D2["Inspect whether new transaction was created"]
    D1 --> E{"rollback-only set?"}
    D2 --> F{"Exception matches rollback rule?"}
    E -->|Yes| E1["Explain UnexpectedRollbackException path"]
    F -->|No| F1["Check checked exception and rollbackFor"]
    F -->|Yes| G["Inspect commit-time failure and resource state"]
    E -->|No| G
```

# 19. Production case: order, audit and event

## Requirement

- order и outbox event должны быть atomic;
- audit должен пережить rollback основной операции;
- notification отправляется asynchronously;
- cache очищается только после commit.

```mermaid
sequenceDiagram
    participant API as Controller
    participant O as OrderService TX-1
    participant A as AuditService TX-2
    participant DB as Database
    participant Cache as Cache
    participant Relay as Outbox relay
    participant N as Async notification

    API->>O: createOrder()
    O->>DB: insert order
    O->>DB: insert outbox row
    O->>A: write audit REQUIRES_NEW
    A->>DB: commit audit in TX-2
    O->>DB: commit order and outbox in TX-1
    O->>Cache: afterCommit eviction
    Relay->>DB: read outbox
    Relay->>N: publish notification command
```

## Failure questions

- Что останется, если TX-1 откатится после committed audit?
- Что произойдёт, если relay опубликовал event, но не отметил row?
- Может ли consumer обработать duplicate?
- Кто инвалидирует local L1 caches?

# 20. Interview explanation

```text
1. @Transactional работает через proxy и TransactionInterceptor.
2. Logical scopes могут делить одну physical transaction.
3. REQUIRED присоединяется, REQUIRES_NEW создаёт independent transaction, NESTED использует savepoint.
4. rollback-only объясняет UnexpectedRollbackException.
5. Checked exceptions не откатывают transaction по умолчанию.
6. Async thread не наследует caller transaction.
7. Один local manager не обеспечивает atomicity нескольких resources.
8. Synchronization callbacks и outbox решают разные commit-boundary задачи.
```

# 21. Exercises

1. Воспроизвести `UnexpectedRollbackException` и нарисовать logical scopes.
2. Измерить pool usage при nested `REQUIRES_NEW`.
3. Проверить `NESTED` на JDBC manager и savepoints.
4. Сравнить checked exception с `rollbackFor` и без него.
5. Доказать отсутствие caller transaction в worker thread.
6. Реализовать after-commit cache eviction.
7. Реализовать outbox relay с duplicate-safe consumer.

## Related materials

- [[Spring Transaction Management Deep Dive]]
- [[Transactional Outbox and Commit Boundaries]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/TX-B01/TX-B01 Cards]]
- [[40_PRODUCTION_CASES/Spring/Transaction Management Production Cases]]
- [[50_LABS/Spring/TX-B01/README]]
