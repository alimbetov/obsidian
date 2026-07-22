---
type: concept
domain: spring
subdomain: testing
difficulty: advanced
status: learning
interview: true
certification:
  - spring-2V0-72.22
spring_versions:
  - 5.3.39
spring_boot_versions:
  - 2.7.18
java_versions:
  - 8
production_relevance: critical
prerequisites:
  - "[[Spring TestContext and Test Slices]]"
  - "[[Spring Data JPA Testing with Testcontainers]]"
  - "[[Spring Data JPA Visual Deep Dive]]"
tags:
  - spring
  - testing
  - testcontext
  - testcontainers
  - diagrams
  - visual-learning
---

# Spring Testing Visual Deep Dive

> [!summary]
> Тест должен доказывать конкретный риск на правильной границе. Главные различия: **unit vs Spring context**, **slice vs full context**, **test-managed vs service transaction**, **flush vs commit**, **H2 vs production database**.

# 1. Выбор границы теста

```mermaid
flowchart TD
    R["What risk must be detected?"] --> U{"Pure business logic?"}
    U -->|Yes| UT["JUnit unit test"]
    U -->|No| J{"JPA mapping or repository query?"}
    J -->|Yes| DJ["@DataJpaTest"]
    J -->|No| S{"Service, proxy or transaction boundary?"}
    S -->|Yes| SB["@SpringBootTest"]
    S -->|No| P{"PostgreSQL-specific behavior?"}
    P -->|Yes| TC["Testcontainers PostgreSQL"]
    P -->|No| W["Choose MVC, WebFlux or configuration slice"]
```

Минимальная граница уменьшает noise, но слишком узкая граница не видит production interaction.

# 2. TestContext lifecycle

```mermaid
sequenceDiagram
    participant J as JUnit Jupiter
    participant E as SpringExtension
    participant M as TestContextManager
    participant C as ApplicationContext
    participant L as TestExecutionListeners
    participant T as Test instance

    J->>E: execute test class
    E->>M: create TestContext
    M->>C: load or reuse context
    M->>L: beforeTestClass
    M->>T: dependency injection
    M->>L: beforeTestMethod
    J->>T: invoke test
    M->>L: afterTestMethod
    M->>L: afterTestClass
```

# 3. Context cache

```mermaid
flowchart LR
    Config1["Configuration key A"] --> Cache["Spring context cache"]
    Config2["Configuration key A"] --> Cache
    Cache --> Reuse["Reuse same ApplicationContext"]
    Config3["Different profile, property or mock"] --> New["Create another context"]
```

Context cache key может меняться из-за:

- profiles;
- properties;
- mock/spy definitions;
- context customizers;
- configuration classes;
- parent context.

# 4. Почему `@DirtiesContext` дорого

```mermaid
flowchart LR
    Test["Test uses @DirtiesContext"] --> Evict["Remove context from cache"]
    Evict --> Next["Next test rebuilds context"]
    Next --> Slow["Longer suite and more flakiness surface"]
```

`@DirtiesContext` — инструмент для реально изменённого application context, а не database cleanup.

# 5. `@DataJpaTest` slice

```mermaid
flowchart TB
    Slice["@DataJpaTest"] --> Entities["Entities"]
    Slice --> Repositories["Spring Data repositories"]
    Slice --> JPA["EntityManager and Hibernate"]
    Slice --> DataSource["Embedded or configured test database"]
    Slice -. usually excludes .-> Services["Regular service beans"]
    Slice -. usually excludes .-> Web["Web layer"]
```

Slice test доказывает repository/JPA boundary, но не full service topology.

# 6. Test-managed transaction

```mermaid
sequenceDiagram
    participant L as TransactionalTestExecutionListener
    participant DB as Database
    participant T as Test method

    L->>DB: begin test transaction
    L->>T: invoke @BeforeEach and test
    T->>DB: repository operations
    T-->>L: test completes
    L->>DB: rollback by default
```

`@Transactional` на test method меняет topology: service может присоединиться к уже существующей test transaction.

# 7. Test transaction vs service transaction

## Test-level transaction

```mermaid
flowchart LR
    Test["Transactional test"] --> TX["Test-managed TX-1"]
    TX --> Service["Service REQUIRED joins TX-1"]
    Service --> Repo["Repository joins TX-1"]
```

## Production-like caller

```mermaid
flowchart LR
    Test["Non-transactional test"] --> Proxy["Service transaction proxy"]
    Proxy --> TX["Service creates TX-1"]
    TX --> Repo["Repository participates"]
```

Для проверки реальной service transaction boundary test-level transaction часто нужно убрать.

# 8. `flush()` и false positive

```mermaid
sequenceDiagram
    participant T as Test
    participant PC as Persistence Context
    participant DB as Database

    T->>PC: persist invalid entity
    PC-->>T: object accepted
    T->>T: assertion passes
    Note over T,DB: SQL may not have executed yet
```

## Stronger test

```mermaid
sequenceDiagram
    participant T as Test
    participant PC as Persistence Context
    participant DB as Database

    T->>PC: persist invalid entity
    T->>PC: flush
    PC->>DB: INSERT
    DB-->>PC: constraint violation
    PC-->>T: exception observed in test
```

# 9. `clear()` и database round-trip

```mermaid
flowchart LR
    Save["save entity"] --> Flush["flush SQL"]
    Flush --> Clear["clear persistence context"]
    Clear --> Reload["find entity again"]
    Reload --> DB["actual database SELECT"]
```

Без `clear()`, repository read может вернуть ту же managed instance и не доказать mapping/database state.

# 10. Commit boundary

```mermaid
sequenceDiagram
    participant T as Test
    participant TT as TestTransaction
    participant DB as Database

    T->>DB: persist data
    T->>TT: flagForCommit
    T->>TT: end
    TT->>DB: commit
    T->>DB: verify state outside original transaction
```

Commit-time constraints, deferred checks и after-commit listeners требуют реального commit, а не только flush.

# 11. `REQUIRES_NEW` inside test

```mermaid
sequenceDiagram
    participant TestTX as Test TX-1
    participant Service as Service
    participant Inner as REQUIRES_NEW TX-2
    participant DB as Database

    TestTX->>Service: call
    Service->>Inner: start TX-2
    Inner->>DB: commit data
    Inner-->>Service: done
    TestTX->>DB: rollback TX-1
    Note over Inner,DB: TX-2 data remains committed
```

Default test rollback не удаляет independent committed transaction.

# 12. Preemptive timeout and thread boundary

```mermaid
sequenceDiagram
    participant Main as Test framework thread
    participant TX as Test-managed transaction
    participant Worker as Timeout worker thread
    participant DB as Database

    Main->>TX: begin and bind to thread
    Main->>Worker: execute test body elsewhere
    Worker->>DB: write without test-bound TX
    DB-->>Worker: commit through application path
    Main->>TX: rollback original thread transaction
    Note over Worker,DB: worker write may remain
```

Thread-bound transaction context не переносится автоматически.

# 13. H2 versus PostgreSQL

```mermaid
flowchart TD
    Query["Repository or native query"] --> H2["H2 test"]
    Query --> PG["PostgreSQL container"]
    H2 --> JPA["Good for basic JPA mechanics"]
    H2 --> Gap["May differ in syntax, types, locking and planner"]
    PG --> Real["Production dialect, constraints, MVCC and plans"]
```

H2 не является эмулятором PostgreSQL во всех аспектах.

# 14. Testcontainers lifecycle

```mermaid
sequenceDiagram
    participant J as JUnit
    participant TC as PostgreSQLContainer
    participant DP as DynamicPropertySource
    participant C as Spring context
    participant T as Tests

    J->>TC: start container
    TC-->>DP: JDBC URL, username, password
    DP->>C: register datasource properties
    C->>C: bootstrap JPA and repositories
    C->>T: run tests
    J->>TC: stop according to lifecycle
```

Static container обычно разделяется между methods одного test class; instance container может стартовать для каждого test instance.

# 15. Container and context lifecycle mismatch

```mermaid
flowchart LR
    Container["Container stops"] --> Context["Cached Spring context remains"]
    Context --> DataSource["Datasource points to dead mapped port"]
    DataSource --> Failure["Later test fails unexpectedly"]
```

Container ownership должен быть согласован с context cache lifecycle.

# 16. N+1 regression test

```mermaid
sequenceDiagram
    participant T as Test
    participant H as Hibernate Statistics
    participant R as Repository
    participant DB as Database

    T->>H: reset statement count
    T->>R: load authors
    R->>DB: root SELECT
    T->>T: traverse books
    T->>H: read prepareStatementCount
    H-->>T: 4 statements
    T->>T: assert expected upper bound
```

Query-count assertion превращает N+1 из ручной проверки logs в regression test.

# 17. Optimistic-lock test with two contexts

```mermaid
sequenceDiagram
    participant A as EntityManager A
    participant DB as Row version=1
    participant B as EntityManager B

    A->>DB: load version 1
    B->>DB: load version 1
    A->>DB: update to version 2 and commit
    B->>DB: update where version=1
    DB-->>B: conflict
```

Один EntityManager не создаёт реальную competing transaction model.

# 18. Pessimistic-lock test

```mermaid
sequenceDiagram
    participant T1 as Thread and TX-1
    participant DB as PostgreSQL
    participant T2 as Thread and TX-2

    T1->>DB: SELECT FOR UPDATE
    DB-->>T1: lock acquired
    T2->>DB: SELECT FOR UPDATE
    Note right of T2: blocks
    T1->>DB: commit
    DB-->>T2: lock acquired or timeout result
```

Такой test требует двух threads, двух transactions и real database semantics.

# 19. Migration test

```mermaid
flowchart LR
    Empty["Empty PostgreSQL schema"] --> Migration["Run Flyway or Liquibase"]
    Migration --> Boot["Start application mappings"]
    Boot --> Smoke["Execute repository smoke tests"]
```

`ddl-auto=create-drop` может скрыть broken production migrations.

# 20. Diagnostic decision tree

```mermaid
flowchart TD
    A["Test is green but production fails"] --> B{"Was SQL forced with flush?"}
    B -->|No| B1["Add flush for DB constraint proof"]
    B -->|Yes| C{"Was persistence context cleared?"}
    C -->|No| C1["Reload outside first-level cache"]
    C -->|Yes| D{"Was commit boundary tested?"}
    D -->|No| D1["Use TestTransaction or non-rollback test"]
    D -->|Yes| E{"Correct database engine?"}
    E -->|No| E1["Use PostgreSQL Testcontainers"]
    E -->|Yes| F{"Same transaction topology as production?"}
    F -->|No| F1["Remove test-level transaction or model propagation explicitly"]
    F -->|Yes| G["Inspect concurrency, migrations and context lifecycle"]
```

# 21. Layered suite

```mermaid
flowchart TB
    Unit["Many fast unit tests"] --> Slice["Focused slice tests"]
    Slice --> Full["Service and full-context tests"]
    Full --> PG["PostgreSQL integration tests"]
    PG --> E2E["Few end-to-end tests"]
```

Каждый слой ловит другой класс дефектов. Нельзя заменить все слои только `@SpringBootTest` или только mocks.

# 22. Production case: order persistence

## Risks

- unique constraint appears only on flush;
- service rollback is hidden by test transaction;
- after-commit event never runs;
- H2 accepts query rejected by PostgreSQL;
- N+1 returns after mapping change.

```mermaid
flowchart LR
    Unit["Unit: domain rules"] --> Slice["DataJpaTest: mapping, query, flush and clear"]
    Slice --> Service["SpringBootTest: service proxy and rollback"]
    Service --> Commit["TestTransaction: commit callbacks"]
    Commit --> PG["Testcontainers: PostgreSQL dialect and locking"]
    PG --> Metrics["SQL-count regression"]
```

# 23. Interview explanation

```text
1. Choose the smallest test boundary that proves the risk.
2. @DataJpaTest is a JPA slice, not a full application test.
3. Transactional tests roll back by default and can change service propagation topology.
4. flush proves SQL execution; clear proves database reload; commit proves commit-time behavior.
5. REQUIRES_NEW may survive test rollback.
6. Thread-bound test transactions do not follow preemptive timeout worker threads.
7. H2 proves basic mechanics; PostgreSQL Testcontainers proves production database behavior.
8. Context and container lifecycles must be aligned.
```

# 24. Exercises

1. Write a false-positive constraint test without flush, then fix it.
2. Prove database round-trip with clear and reload.
3. Compare service rollback with and without test-level transaction.
4. Commit through TestTransaction and verify after-commit listener.
5. Show committed `REQUIRES_NEW` data surviving outer test rollback.
6. Run native `ILIKE` only against PostgreSQL container.
7. Add SQL-count assertion for N+1.
8. Reproduce optimistic and pessimistic locking with independent transactions.

## Related materials

- [[Spring TestContext and Test Slices]]
- [[Spring Data JPA Testing with Testcontainers]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/TEST-B01/TEST-B01 Cards]]
- [[40_PRODUCTION_CASES/Spring/Spring Testing Production Cases]]
- [[50_LABS/Spring/TEST-B01/README]]
