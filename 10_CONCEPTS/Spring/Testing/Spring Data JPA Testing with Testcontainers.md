---
type: concept
domain: spring
subdomain:
  - testing
  - spring-data-jpa
  - testcontainers
difficulty: advanced
status: learning
confidence: 0
interview: true
certification:
  - spring-2V0-72.22
spring_versions:
  - 5.3.39
spring_boot_versions:
  - 2.7.18
spring_data_jpa_versions:
  - 2.7.18
hibernate_versions:
  - 5.6.15.Final
testcontainers_versions:
  - 1.19.8
java_versions:
  - 8
production_relevance: critical
prerequisites:
  - "[[Spring TestContext and Test Slices]]"
  - "[[Spring Data JPA Persistence Context and Entity Lifecycle]]"
  - "[[Spring Data Repositories Queries and Fetching]]"
related:
  - "[[Spring Transaction Management Deep Dive]]"
  - "[[Spring Data JPA Production Cases]]"
tags:
  - spring
  - testing
  - jpa
  - hibernate
  - testcontainers
  - postgresql
---

# Spring Data JPA Testing with Testcontainers

> [!summary] За 30 секунд
> Хороший JPA test должен явно доказать нужный слой: mapping и repository contract — через `@DataJpaTest`; service transaction — через Spring integration test; реальный dialect, constraints, locking и execution semantics — через production-like database в Testcontainers. Для ORM tests обязательны `flush()` и часто `clear()`. Rollback по умолчанию полезен для isolation, но скрывает after-commit и commit-time behavior, поэтому такие сценарии проверяются отдельными commit tests.

# 1. Testing matrix

| Что проверяем | H2 slice | PostgreSQL Testcontainers | Full service context |
|---|---:|---:|---:|
| entity mapping | да | да | косвенно |
| derived query | да | да | косвенно |
| PostgreSQL native SQL | нет | да | при PostgreSQL |
| unique/not-null basic constraints | да | да | да |
| deferrable constraint | нет/иначе | да | да |
| MVCC/locking | не production-like | да | да |
| repository proxy | да | да | да |
| service propagation | нет | нет, если service не загружен | да |
| after-commit event | нет при default rollback | возможно | да |
| N+1 statement count | да | да | да |

Главный принцип:

```text
H2 teaches JPA mechanics.
PostgreSQL proves PostgreSQL behavior.
Full context proves application orchestration.
```

# 2. Почему H2 недостаточно

H2 полезен:

- быстрый startup;
- deterministic in-memory state;
- basic mapping;
- simple JPQL;
- dirty checking;
- cascade/orphan removal;
- repository proxy;
- first-level cache.

Но H2 не является доказательством:

- PostgreSQL `ILIKE`;
- JSONB/operator behavior;
- partial indexes;
- `SKIP LOCKED` details;
- MVCC snapshots;
- sequence behavior;
- deferrable constraints;
- planner/execution plan;
- lock timeout/deadlock behavior;
- collation/case sensitivity;
- exact SQL dialect.

`MODE=PostgreSQL` улучшает compatibility syntax, но не превращает H2 в PostgreSQL.

# 3. Testcontainers model

```text
JUnit lifecycle
    ↓
Testcontainers extension
    ↓
Docker-compatible runtime
    ↓
postgres:15-alpine
    ↓
random host port
    ↓
DynamicPropertySource
    ↓
Spring DataSource / Hibernate
```

Пример:

```java
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostgreSqlOrderRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("orders")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

## Почему нельзя hard-code localhost:5432

Testcontainers использует mapped port:

```text
container port 5432
    ↓
random host port 32791
```

Использовать нужно `getJdbcUrl()` или `getHost()` + `getMappedPort()`.

# 4. `@AutoConfigureTestDatabase(replace = NONE)`

Без этой настройки Boot slice может заменить configured datasource embedded database.

```java
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
```

Ментальная модель:

```text
Testcontainers properties configured
        ↓
Boot test database replacement?
        ├── ANY/AUTO → possible embedded replacement
        └── NONE     → keep container datasource
```

# 5. Static container lifecycle

```java
@Container
static PostgreSQLContainer<?> postgres = ...;
```

Static container обычно живёт на test class:

```text
start once before all methods
run all methods
stop after class
```

Instance field container:

```text
start before each test
stop after each test
```

Для database suite static container обычно быстрее, но данные между methods нужно очищать.

# 6. Schema management

Варианты:

1. Hibernate `create-drop` — быстро для lab.
2. Flyway/Liquibase — production-like migrations.
3. SQL scripts — controlled fixture schema.

Для реального migration proof предпочтительнее:

```text
container starts empty
    ↓
Flyway/Liquibase applies production migrations
    ↓
repository tests run
```

Иначе test может пройти на schema, сгенерированной Hibernate, но production migration останется broken.

# 7. Fixture strategies

## Builder

```java
PurchaseOrder order = new PurchaseOrderBuilder()
        .number("ORD-100")
        .status("NEW")
        .withLine("SKU-1", 2)
        .build();
```

Подходит для domain-readable setup.

## `@Sql`

```java
@Sql("/sql/orders.sql")
@Test
void findsExpiredOrders() {
}
```

Подходит для complex relational state.

## Repository setup

```java
repository.saveAll(...);
repository.flush();
entityManager.clear();
```

Подходит для ordinary fixtures, но setup сам зависит от mapping/repository behavior.

# 8. Database round-trip test

```java
PurchaseOrder order = repository.save(
        new PurchaseOrder("ORD-1", "NEW")
);
repository.flush();
entityManager.clear();

PurchaseOrder reloaded = repository.findById(order.getId()).orElseThrow();

assertThat(reloaded.getOrderNumber()).isEqualTo("ORD-1");
assertThat(reloaded.getStatus()).isEqualTo("NEW");
```

Что доказано:

- INSERT выполнен;
- mapping columns корректен;
- row можно прочитать;
- assertion не обслужена first-level cache.

# 9. Constraint test

```java
repository.save(new PurchaseOrder("ORD-DUP", "NEW"));
repository.flush();

repository.save(new PurchaseOrder("ORD-DUP", "NEW"));

assertThatThrownBy(repository::flush)
        .isInstanceOf(DataIntegrityViolationException.class);
```

Почему два flush могут быть полезны:

- первый гарантирует существование первой row;
- второй локализует failure второй operation.

# 10. Commit-time failure

Некоторые ошибки появляются только при commit:

- deferred constraints;
- optimistic conflict;
- transaction synchronization;
- trigger logic;
- database validation.

Test-managed rollback не доказывает commit.

```java
repository.save(...);
TestTransaction.flagForCommit();

assertThatThrownBy(TestTransaction::end)
        .isInstanceOf(RuntimeException.class);
```

Затем test должен восстановить isolation или использовать отдельный context/schema.

# 11. `@Commit` против explicit transaction ending

## `@Commit`

Хорош для сценария, где verification выполняется в `@AfterTransaction`.

```java
@Test
@Commit
void commitsOrder() {
    repository.save(new PurchaseOrder("ORD-1", "NEW"));
}

@AfterTransaction
void verifyCommittedState() {
    assertThat(repository.findByOrderNumber("ORD-1")).isPresent();
}
```

## `TestTransaction.end()`

Хорош, когда commit и verification нужны внутри одного test method.

# 12. Service transaction test

Repository slice не проверяет service propagation.

```java
@SpringBootTest
class OrderServiceTransactionTest {

    @Autowired
    OrderService service;

    @Autowired
    OrderRepository repository;

    @Test
    void rollsBackOrderAndLinesTogether() {
        assertThatThrownBy(() -> service.createAndFail("ORD-1"))
                .isInstanceOf(IllegalStateException.class);

        assertThat(repository.findByOrderNumber("ORD-1")).isEmpty();
    }
}
```

Важно: test method здесь лучше не делать `@Transactional`, если требуется увидеть production transaction outcome. Иначе service может присоединиться к test transaction, и test будет проверять другую topology.

# 13. Testing propagation

Для propagation tests нужно считать физические commits или проверять committed rows вне outer transaction.

Пример `REQUIRES_NEW` audit:

```text
outer service transaction starts
    ↓
inner audit REQUIRES_NEW commits
    ↓
outer fails and rolls back
    ↓
order absent, audit present
```

Test должен вызывать external proxied beans. Direct self-invocation даст ложный сценарий.

# 14. Optimistic locking test

Нужны два independent persistence contexts:

```java
EntityManager first = emf.createEntityManager();
EntityManager second = emf.createEntityManager();

first.getTransaction().begin();
PurchaseOrder a = first.find(PurchaseOrder.class, id);

second.getTransaction().begin();
PurchaseOrder b = second.find(PurchaseOrder.class, id);
b.setStatus("PAID");
second.getTransaction().commit();

a.setStatus("CANCELLED");
assertThatThrownBy(() -> first.getTransaction().commit())
        .isInstanceOf(RollbackException.class);
```

Один shared `EntityManager` не создаст настоящий stale-version conflict.

# 15. Pessimistic locking test

Нужны:

- две connections;
- два threads/transactions;
- coordination primitives;
- bounded timeout;
- real database.

```text
TX-1 locks row
    ↓ latch
TX-2 attempts PESSIMISTIC_WRITE
    ↓
assert blocks or times out according to DB policy
```

H2 result нельзя переносить на PostgreSQL/Oracle без проверки.

# 16. N+1 regression test

```java
Statistics statistics = entityManagerFactory
        .unwrap(SessionFactory.class)
        .getStatistics();

statistics.clear();

List<PurchaseOrder> orders = repository.findAll();
orders.forEach(order -> order.getLines().size());

assertThat(statistics.getPrepareStatementCount())
        .isLessThanOrEqualTo(2);
```

Лучше проверять invariant:

```text
statement count does not grow with number of root rows
```

Чрезмерно жёсткое `equals(1)` может стать brittle при legitimate provider changes. Для focused query contract exact count допустим; для broader service лучше верхняя граница.

# 17. Fetch plan regression

```java
@EntityGraph(attributePaths = "lines")
List<PurchaseOrder> findAllByStatus(String status);
```

Test:

```java
statistics.clear();
List<PurchaseOrder> result = repository.findAllByStatus("NEW");
result.forEach(order -> order.getLines().size());

assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
```

Также проверить отсутствие duplicate root rows и корректность pagination.

# 18. Page/count-query test

```java
Page<PurchaseOrder> page = repository.findByStatus(
        "NEW",
        PageRequest.of(0, 20)
);
```

Проверять:

- content order deterministic;
- total count корректен;
- count query не содержит invalid fetch join;
- empty page behavior;
- last page;
- concurrent insert implications, если relevant.

# 19. Slice boundary test

Полезно доказать, что обычный service не загружен:

```java
@DataJpaTest
class RepositorySliceBoundaryTest {

    @Autowired
    ApplicationContext context;

    @Test
    void doesNotLoadOrderService() {
        assertThat(context.getBeansOfType(OrderService.class)).isEmpty();
    }
}
```

Это защищает slice от случайного расширения.

# 20. Context cache and Testcontainers

Контейнер и Spring context имеют разные lifecycles.

Проблемная схема:

```text
static container stopped manually
    ↓
Spring context remains cached
    ↓
next test reuses DataSource pointing to dead container
```

Не останавливать managed container вручную, пока cached context может быть reused.

# 21. Singleton container pattern

Можно запустить один container на несколько classes, но нужно контролировать:

- startup/stop ownership;
- context cache;
- schema reset;
- parallel tests;
- port/property publication;
- Ryuk cleanup.

Нельзя смешивать static initializer singleton pattern и `@Testcontainers` lifecycle без понимания ownership — container может остановиться раньше cached context.

# 22. Parallel execution

Shared database создаёт риски:

- одинаковые unique keys;
- cross-test cleanup;
- lock interference;
- sequence assumptions;
- transaction isolation noise.

Стратегии:

- unique fixture IDs;
- schema per test class;
- database per container/test class;
- truncate with locking discipline;
- disable parallelism for concurrency-sensitive suite.

# 23. Cleanup strategies

| Strategy | Плюсы | Риски |
|---|---|---|
| rollback | быстро | не покрывает commit |
| truncate tables | real commit tests | FK order, locks |
| recreate schema | strong isolation | дороже |
| new container | maximal isolation | slowest |
| unique data only | simple | state accumulation |

# 24. Testing migrations

```text
empty PostgreSQL container
    ↓
run Flyway/Liquibase
    ↓
verify schema version
    ↓
run repository/service tests
```

Дополнительные tests:

- migration from previous released schema;
- checksum mismatch;
- non-null column backfill;
- index creation;
- rollback/forward-only policy;
- zero-downtime compatibility.

# 25. Native query test

```java
@Query(
    value = "select * from purchase_orders where order_number ilike concat('%', :fragment, '%')",
    nativeQuery = true
)
List<PurchaseOrder> searchIgnoreCase(String fragment);
```

Этот query должен проверяться на PostgreSQL, потому что `ILIKE` — dialect feature.

# 26. JSON/array/database-specific types

Для PostgreSQL-specific mappings проверять:

- JSONB serialization;
- enum mapping;
- arrays;
- UUID;
- timestamp/time zone;
- numeric precision;
- generated columns;
- custom types.

Embedded database здесь даёт слишком слабое доказательство.

# 27. Testing transaction-bound events

`@TransactionalEventListener(AFTER_COMMIT)` не сработает при default rollback.

```java
service.create(...);
TestTransaction.flagForCommit();
TestTransaction.end();

assertThat(listener.events()).hasSize(1);
```

Но это всё ещё in-process callback test, а не durable broker-delivery test.

# 28. Outbox test

Atomicity:

```text
business row + outbox row commit together
or both roll back
```

Relay test:

- claim pending rows;
- publish fake/embedded broker result;
- mark published;
- simulate uncertain acknowledgment;
- verify duplicate publish;
- verify consumer idempotency.

# 29. Common false positives

1. Assertion читает managed entity вместо database.
2. SQL не был flushed.
3. Test rollback скрывает commit callback.
4. H2 принимает query, PostgreSQL отклоняет.
5. Mock repository скрывает transaction behavior.
6. Shared test transaction меняет propagation topology.
7. One EntityManager не создаёт concurrency conflict.
8. Test verifies event publication, not delivery.
9. `@DirtiesContext` маскирует state leak.
10. Test order случайно создаёт нужную fixture.

# 30. Recommended suite

```text
src/test/java
├── unit/
│   └── domain rules
├── slice/
│   ├── repository mapping
│   ├── query derivation
│   ├── projections
│   └── SQL-count regression
├── integration/
│   ├── service transaction
│   ├── events
│   └── cache/outbox boundaries
└── container/
    ├── PostgreSQL native queries
    ├── locks/MVCC
    ├── migrations
    └── dialect-specific types
```

# 31. Production checklist

- [ ] Repository query tested with meaningful data shape.
- [ ] Constraint failure forced with flush/commit.
- [ ] Reload performed after clear.
- [ ] Native query tested on actual engine.
- [ ] N+1 guarded by statement-count test.
- [ ] Pagination has deterministic unique sort.
- [ ] Optimistic conflict uses two persistence contexts.
- [ ] Pessimistic test has bounded timeout.
- [ ] Migration tested from empty schema.
- [ ] Commit callbacks tested with real commit.
- [ ] Test data isolation works under random order.
- [ ] Container ownership compatible with context cache.

# 32. Senior interview answer

> For Spring Data JPA I use a layered test strategy. `@DataJpaTest` is the default for mapping, repository methods, dirty checking, projections and fetch plans. I force `flush()` to surface deferred SQL errors and clear the persistence context before reload assertions. I use a full Spring context when I need service transaction or proxy behavior. Database-specific queries, migrations, MVCC and locking run against PostgreSQL Testcontainers with datasource replacement disabled and properties registered dynamically. I keep default rollback tests separate from commit tests, because rollback isolation does not prove commit-time constraints, after-commit events or durable state.

# 33. Memory hooks

```text
H2 proves JPA mechanics.
Container proves database behavior.
Flush proves SQL.
Clear proves reload.
Two EntityManagers prove concurrency.
Rollback proves isolated logic.
Commit proves commit behavior.
Statistics prove query count.
Migration tool proves schema.
```
