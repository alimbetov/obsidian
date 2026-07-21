---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain:
  - testing
  - test-context
  - data-jpa-testing
batch_id: TEST-B01
status: published
card_count: 36
first_card: TEST-B01-C001
last_card: TEST-B01-C036
spring_versions:
  - 5.3.39
spring_boot_versions:
  - 2.7.18
tags:
  - spring
  - certification
  - testing
  - junit
  - testcontainers
---

# TEST-B01 — Spring Testing

> [!summary]
> 36 карточек по TestContext Framework, test slices, transactional tests, `@DataJpaTest`, `flush/clear`, commit boundaries, context caching, Testcontainers и SQL regression tests.

---

## TEST-B01-C001 — What is the purpose of the Spring TestContext Framework?

### Russian Translation

Каково назначение Spring TestContext Framework?

> [!answer]- Answer
> It integrates a testing framework with Spring context loading, dependency injection, test execution listeners, transaction management and context caching.

### Explanation

JUnit executes the test, while SpringExtension/TestContextManager prepare the test instance and delegate lifecycle phases to listeners.

### Exam Trap

It is not only an annotation collection and it does not require a full Boot application context.

### Memory Hook

`JUnit runs; TestContext integrates Spring.`

---

## TEST-B01-C002 — Which component coordinates Spring test lifecycle callbacks?

### Russian Translation

Какой компонент координирует lifecycle callbacks Spring test?

> [!answer]- Answer
> `TestContextManager`.

### Explanation

It manages a `TestContext` for a test class and invokes registered `TestExecutionListener` implementations at defined phases.

### Exam Trap

`ApplicationContext` contains application beans; it does not itself coordinate JUnit lifecycle callbacks.

---

## TEST-B01-C003 — What does a `TestExecutionListener` do?

### Russian Translation

Что делает `TestExecutionListener`?

> [!answer]- Answer
> It reacts to test execution phases to provide infrastructure such as dependency injection, transactions, SQL scripts and context dirtiness handling.

### Memory Hook

`Listener instruments the test lifecycle.`

---

## TEST-B01-C004 — What is the main benefit of Spring's test context cache?

### Russian Translation

Каково основное преимущество test context cache Spring?

> [!answer]- Answer
> Compatible test classes can reuse the same loaded `ApplicationContext`, reducing suite startup cost.

### Exam Trap

The cache is based on merged context configuration, not only on the test class name.

---

## TEST-B01-C005 — Which changes commonly create a different context-cache key?

### Russian Translation

Какие изменения обычно создают другой ключ context cache?

> [!answer]- Answer
> Different configuration classes, profiles, test properties, context customizers, mock beans, resource locations or parent contexts.

### Production Transfer

Dozens of unique inline properties can turn one reusable context into dozens of startups.

---

## TEST-B01-C006 — What does `@DirtiesContext` mean?

### Russian Translation

Что означает `@DirtiesContext`?

> [!answer]- Answer
> The test has modified context-level state, so the context should be removed from the cache and rebuilt for later tests.

### Exam Trap

It is not the preferred mechanism for cleaning database rows.

---

## TEST-B01-C007 — When should a plain unit test be preferred over `@SpringBootTest`?

### Russian Translation

Когда plain unit test предпочтительнее `@SpringBootTest`?

> [!answer]- Answer
> When the behavior is pure Java logic and does not depend on Spring configuration, proxies or infrastructure.

### Mini Example

A price calculation rule should normally be instantiated directly and tested without a container.

---

## TEST-B01-C008 — What does `@SpringJUnitConfig` combine?

### Russian Translation

Что объединяет `@SpringJUnitConfig`?

> [!answer]- Answer
> Spring's JUnit Jupiter extension and context configuration support.

### Explanation

It is useful for focused Framework-level integration tests without Boot auto-configuration.

---

## TEST-B01-C009 — What is the primary purpose of `@SpringBootTest`?

### Russian Translation

Каково основное назначение `@SpringBootTest`?

> [!answer]- Answer
> To load a broad Spring Boot application context and verify application wiring and integration behavior.

### Exam Trap

It does not automatically mean a real web server or production database is used.

---

## TEST-B01-C010 — What is a Spring Boot test slice?

### Russian Translation

Что такое Spring Boot test slice?

> [!answer]- Answer
> A focused test context that includes auto-configuration and components relevant to one infrastructure layer.

### Memory Hook

`Slice narrows the graph, not the assertion quality.`

---

## TEST-B01-C011 — What does `@DataJpaTest` configure by default?

### Russian Translation

Что по умолчанию конфигурирует `@DataJpaTest`?

> [!answer]- Answer
> JPA entities, Spring Data repositories, JPA infrastructure, an embedded database when available, `TestEntityManager`, and transactional rollback semantics.

### Exam Trap

Regular service components are not generally included by the slice.

---

## TEST-B01-C012 — Are `@DataJpaTest` methods transactional by default?

### Russian Translation

Являются ли методы `@DataJpaTest` transactional по умолчанию?

> [!answer]- Answer
> Yes. They normally run in a test-managed transaction that is rolled back after each test.

---

## TEST-B01-C013 — Why can a JPA test pass without actually executing an INSERT?

### Russian Translation

Почему JPA test может пройти без фактического выполнения INSERT?

> [!answer]- Answer
> Hibernate may defer SQL until flush or commit, and the test can finish before the deferred operation is forced.

### Correct Practice

Call `flush()` when the assertion depends on SQL execution or a database constraint.

---

## TEST-B01-C014 — Why should a persistence test often call `clear()` before reloading?

### Russian Translation

Почему persistence test часто должен вызывать `clear()` перед повторной загрузкой?

> [!answer]- Answer
> To ensure the assertion reads from the database instead of returning the same managed entity from the first-level cache.

### Memory Hook

`Flush proves SQL; clear proves reload.`

---

## TEST-B01-C015 — What is `TestEntityManager`?

### Russian Translation

Что такое `TestEntityManager`?

> [!answer]- Answer
> A test-focused facade around JPA `EntityManager` that provides convenient persist, flush, clear and find operations.

### Exam Trap

It does not replace understanding of persistence-context semantics.

---

## TEST-B01-C016 — What is a test-managed transaction?

### Russian Translation

Что такое test-managed transaction?

> [!answer]- Answer
> A transaction controlled by Spring's `TransactionalTestExecutionListener` or `TestTransaction` around a test method.

### Explanation

Application `REQUIRED` transactions commonly participate in it, but other propagation modes can change the topology.

---

## TEST-B01-C017 — What is the default outcome of a transactional Spring integration test?

### Russian Translation

Каков результат transactional Spring integration test по умолчанию?

> [!answer]- Answer
> Rollback after the test method completes.

### Exam Trap

Default rollback does not prove after-commit behavior.

---

## TEST-B01-C018 — What does `@Commit` do on a transactional test?

### Russian Translation

Что делает `@Commit` на transactional test?

> [!answer]- Answer
> It instructs the test transaction to commit after the test instead of rolling back.

### Production Transfer

Use it narrowly and ensure committed data is cleaned or isolated.

---

## TEST-B01-C019 — What is the purpose of `TestTransaction`?

### Russian Translation

Для чего используется `TestTransaction`?

> [!answer]- Answer
> To inspect, start, end and choose commit or rollback for the current test-managed transaction programmatically.

### Mini Example

`flagForCommit()` followed by `end()` can expose commit-time failures inside a test method.

---

## TEST-B01-C020 — When do `@BeforeTransaction` and `@AfterTransaction` run?

### Russian Translation

Когда выполняются `@BeforeTransaction` и `@AfterTransaction`?

> [!answer]- Answer
> Outside the test-managed transaction, immediately before it begins and after it ends.

### Exam Trap

`@BeforeEach` and `@AfterEach` run inside the test transaction for a transactional test method.

---

## TEST-B01-C021 — Why is `assertTimeoutPreemptively` dangerous with transactional tests?

### Russian Translation

Почему `assertTimeoutPreemptively` опасен с transactional tests?

> [!answer]- Answer
> It may execute test code in another thread that does not inherit Spring's thread-bound test transaction, so writes can commit unexpectedly.

### Memory Hook

`New thread, no test transaction.`

---

## TEST-B01-C022 — Does a test-level `rollbackFor` control test-managed rollback rules?

### Russian Translation

Управляет ли test-level `rollbackFor` правилами test-managed rollback?

> [!answer]- Answer
> No. Test-managed transaction outcome should be controlled with `@Commit`, `@Rollback` or `TestTransaction`.

### Exam Trap

Do not assume all production `@Transactional` attributes have identical test-managed meaning.

---

## TEST-B01-C023 — Why can a transactional test hide production propagation behavior?

### Russian Translation

Почему transactional test может скрыть production propagation behavior?

> [!answer]- Answer
> The application service may join the surrounding test transaction, creating a different transaction topology than a real non-transactional caller.

### Production Transfer

Propagation tests often should call the service without a test-level transaction.

---

## TEST-B01-C024 — What does `@RecordApplicationEvents` prove?

### Russian Translation

Что доказывает `@RecordApplicationEvents`?

> [!answer]- Answer
> It proves that application events were published in the test `ApplicationContext`.

### Exam Trap

It does not prove durable broker delivery.

---

## TEST-B01-C025 — What does `@AutoConfigureTestDatabase(replace = NONE)` protect?

### Russian Translation

Что защищает `@AutoConfigureTestDatabase(replace = NONE)`?

> [!answer]- Answer
> It prevents the configured datasource, such as a Testcontainers PostgreSQL datasource, from being replaced with an embedded test database.

---

## TEST-B01-C026 — Why should Testcontainers connection details be obtained dynamically?

### Russian Translation

Почему connection details Testcontainers нужно получать динамически?

> [!answer]- Answer
> Containers commonly use randomized host ports and may run on a non-localhost Docker host.

### Mini Example

Use `postgres.getJdbcUrl()` instead of hard-coding `jdbc:postgresql://localhost:5432/...`.

---

## TEST-B01-C027 — What does `@DynamicPropertySource` do?

### Russian Translation

Что делает `@DynamicPropertySource`?

> [!answer]- Answer
> It registers dynamic properties, such as container JDBC URL and credentials, before the Spring test context is created.

---

## TEST-B01-C028 — What is the lifecycle difference between static and instance `@Container` fields?

### Russian Translation

Чем отличается lifecycle static и instance `@Container` fields?

> [!answer]- Answer
> A static container is typically shared across all methods in the test class, while an instance container is started for each test instance/method lifecycle.

### Production Transfer

Static containers are faster but require explicit data isolation.

---

## TEST-B01-C029 — Why is H2 not sufficient for PostgreSQL-specific repository tests?

### Russian Translation

Почему H2 недостаточно для PostgreSQL-specific repository tests?

> [!answer]- Answer
> H2 has different SQL grammar, type system, MVCC, locking, planner, sequences and constraint behavior.

### Memory Hook

`Compatibility mode is not database identity.`

---

## TEST-B01-C030 — How should an optimistic-lock test create a real conflict?

### Russian Translation

Как optimistic-lock test должен создавать настоящий conflict?

> [!answer]- Answer
> Load the same row in two independent persistence contexts/transactions, commit one update, then commit the stale update.

### Exam Trap

Two references from one `EntityManager` are normally the same managed instance.

---

## TEST-B01-C031 — What infrastructure is required for a meaningful pessimistic-lock test?

### Russian Translation

Какая infrastructure нужна для meaningful pessimistic-lock test?

> [!answer]- Answer
> At least two transactions/connections, coordination between threads, a bounded timeout and the real target database.

---

## TEST-B01-C032 — How can N+1 be protected by an automated regression test?

### Russian Translation

Как защититься от N+1 автоматическим regression test?

> [!answer]- Answer
> Execute the use case, initialize the required associations and assert Hibernate/JDBC statement count or a bounded query-count invariant.

### Exam Trap

Checking that the returned data is correct does not detect N+1.

---

## TEST-B01-C033 — Why can exact SQL-count assertions become brittle?

### Russian Translation

Почему exact SQL-count assertions могут стать brittle?

> [!answer]- Answer
> Legitimate provider, batching or fetch-plan changes can alter statement count without violating the business performance invariant.

### Correct Practice

Use exact counts for focused query contracts and upper bounds for broader service behavior.

---

## TEST-B01-C034 — What is the strongest way to test database migrations?

### Russian Translation

Как наиболее надёжно тестировать database migrations?

> [!answer]- Answer
> Start an empty real database container, apply the production Flyway/Liquibase migrations, and then run schema and repository assertions.

### Exam Trap

Hibernate `create-drop` does not test production migration files.

---

## TEST-B01-C035 — Why can a manually stopped singleton container break later tests?

### Russian Translation

Почему вручную остановленный singleton container может сломать последующие tests?

> [!answer]- Answer
> Spring may reuse a cached `ApplicationContext` whose datasource still points to the stopped container.

### Memory Hook

`Container lifecycle must outlive cached context.`

---

## TEST-B01-C036 — What is the recommended layered strategy for Spring Data JPA testing?

### Russian Translation

Какова рекомендуемая layered strategy для Spring Data JPA testing?

> [!answer]- Answer
> Use unit tests for domain rules, `@DataJpaTest` for repository/mapping behavior, full-context tests for service transactions, and Testcontainers for real database semantics.

### Memory Hook

```text
Unit → logic
Slice → layer
Full context → wiring
Container → real dependency
```
