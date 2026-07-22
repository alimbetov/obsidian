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
normalization_status: complete
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
> 36 normalized cards по TestContext Framework, test slices, transactional tests, `@DataJpaTest`, `flush/clear`, commit boundaries, context caching, Testcontainers и SQL regression tests.

## Route navigation

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Testing Roadmap]]
- [[10_CONCEPTS/Spring/Testing/Spring TestContext and Test Slices]]
- [[10_CONCEPTS/Spring/Testing/Spring Data JPA Testing with Testcontainers]]
- [[10_CONCEPTS/Spring/Testing/Spring Testing Visual Deep Dive]]
- [[40_PRODUCTION_CASES/Spring/Spring Testing Production Cases]]
- [[50_LABS/Spring/TEST-B01/README]]
- [[98_SOURCES/Spring Testing Sources]]

---

## TEST-B01-C001 — What is the purpose of the Spring TestContext Framework?

### Russian Translation

Каково назначение Spring TestContext Framework?

> [!answer]- Answer
> It integrates a testing framework with Spring context loading, dependency injection, test execution listeners, transaction management and context caching.

### Explanation

JUnit executes tests, while Spring prepares test instances and infrastructure around the test lifecycle.

### Exam Trap

It is not only an annotation collection and does not require a full Boot context.

---

## TEST-B01-C002 — Which component coordinates Spring test lifecycle callbacks?

### Russian Translation

Какой компонент координирует lifecycle callbacks Spring test?

> [!answer]- Answer
> `TestContextManager`.

### Explanation

It manages the `TestContext` and delegates phases to registered `TestExecutionListener` implementations.

### Exam Trap

`ApplicationContext` contains beans but does not coordinate JUnit lifecycle callbacks.

---

## TEST-B01-C003 — What does a `TestExecutionListener` do?

### Russian Translation

Что делает `TestExecutionListener`?

> [!answer]- Answer
> It reacts to test phases to provide infrastructure such as dependency injection, transactions, SQL scripts and context dirtiness handling.

### Explanation

Listeners insert Spring behavior before and after test-class, test-instance and test-method events.

### Exam Trap

A listener is test infrastructure, not an application event listener.

---

## TEST-B01-C004 — What is the main benefit of Spring's test context cache?

### Russian Translation

Каково основное преимущество test context cache Spring?

> [!answer]- Answer
> Compatible test classes can reuse the same loaded `ApplicationContext`, reducing suite startup cost.

### Explanation

Context construction is expensive, so reuse can dominate total test-suite performance.

### Exam Trap

The cache key is based on merged configuration, not only on the test class name.

---

## TEST-B01-C005 — Which changes commonly create a different context-cache key?

### Russian Translation

Какие изменения обычно создают другой ключ context cache?

> [!answer]- Answer
> Different configuration classes, profiles, test properties, context customizers, mock beans, resource locations or parent contexts.

### Explanation

Each unique configuration fingerprint can force another context startup and reduce cache reuse.

### Exam Trap

Many small `@MockBean` or inline-property variations can fragment the cache unexpectedly.

---

## TEST-B01-C006 — What does `@DirtiesContext` mean?

### Russian Translation

Что означает `@DirtiesContext`?

> [!answer]- Answer
> The test modified context-level state, so the context should be removed from the cache and rebuilt for later tests.

### Explanation

It protects later tests from a mutated singleton graph or altered application context.

### Exam Trap

It is not the preferred mechanism for cleaning ordinary database rows.

---

## TEST-B01-C007 — When should a plain unit test be preferred over `@SpringBootTest`?

### Russian Translation

Когда plain unit test предпочтительнее `@SpringBootTest`?

> [!answer]- Answer
> When behavior is pure Java logic and does not depend on Spring configuration, proxies or infrastructure.

### Explanation

A plain test is faster and isolates domain behavior from container wiring.

### Exam Trap

Loading a context does not automatically make a test more realistic or valuable.

---

## TEST-B01-C008 — What does `@SpringJUnitConfig` combine?

### Russian Translation

Что объединяет `@SpringJUnitConfig`?

> [!answer]- Answer
> Spring's JUnit Jupiter extension and context configuration support.

### Explanation

It is a compact way to write focused Framework-level integration tests without Boot auto-configuration.

### Exam Trap

It is not equivalent to `@SpringBootTest`.

---

## TEST-B01-C009 — What is the primary purpose of `@SpringBootTest`?

### Russian Translation

Каково основное назначение `@SpringBootTest`?

> [!answer]- Answer
> To load a broad Spring Boot application context and verify application wiring and integration behavior.

### Explanation

It tests the composed Boot application graph rather than a narrow slice.

### Exam Trap

It does not automatically start a real server or connect to a production-like database.

---

## TEST-B01-C010 — What is a Spring Boot test slice?

### Russian Translation

Что такое Spring Boot test slice?

> [!answer]- Answer
> A focused test context including auto-configuration and components relevant to one infrastructure layer.

### Explanation

A slice reduces graph size while preserving the framework behavior of the selected layer.

### Exam Trap

A slice narrows loaded components; it does not improve assertions by itself.

---

## TEST-B01-C011 — What does `@DataJpaTest` configure by default?

### Russian Translation

Что по умолчанию конфигурирует `@DataJpaTest`?

> [!answer]- Answer
> JPA entities, Spring Data repositories, JPA infrastructure, an embedded database when available, `TestEntityManager`, and transactional rollback semantics.

### Explanation

It creates a focused persistence-layer context rather than the complete service/application graph.

### Exam Trap

Regular service components are not generally included automatically.

---

## TEST-B01-C012 — Are `@DataJpaTest` methods transactional by default?

### Russian Translation

Являются ли методы `@DataJpaTest` transactional по умолчанию?

> [!answer]- Answer
> Yes. They normally run in a test-managed transaction rolled back after each test.

### Explanation

Rollback improves isolation and speed but can hide commit-time or after-commit behavior.

### Exam Trap

Transactional rollback does not prove what happens after a real application commit.

---

## TEST-B01-C013 — Why can a JPA test pass without actually executing an INSERT?

### Russian Translation

Почему JPA test может пройти без фактического выполнения INSERT?

> [!answer]- Answer
> Hibernate may defer SQL until flush or commit, and the test can finish before the deferred operation is forced.

### Explanation

Write-behind means in-memory persistence-context state can look correct even when the database has not accepted the row.

### Exam Trap

An assertion against the same managed entity does not prove SQL or constraints executed.

---

## TEST-B01-C014 — Why should a persistence test often call `clear()` before reloading?

### Russian Translation

Почему persistence test часто должен вызывать `clear()` перед повторной загрузкой?

> [!answer]- Answer
> To ensure the assertion reads from the database instead of returning the same managed entity from the first-level cache.

### Explanation

`flush()` proves SQL execution; `clear()` removes identity-map reuse so a subsequent find proves a database round trip.

### Exam Trap

Reloading without clearing may return the same Java instance.

---

## TEST-B01-C015 — What is `TestEntityManager`?

### Russian Translation

Что такое `TestEntityManager`?

> [!answer]- Answer
> A test-focused facade around JPA `EntityManager` providing convenient persist, flush, clear and find operations.

### Explanation

It shortens common persistence-test operations while preserving underlying JPA semantics.

### Exam Trap

It does not replace understanding entity lifecycle and persistence-context behavior.

---

## TEST-B01-C016 — What is a test-managed transaction?

### Russian Translation

Что такое test-managed transaction?

> [!answer]- Answer
> A transaction controlled by Spring's `TransactionalTestExecutionListener` or `TestTransaction` around a test method.

### Explanation

Application `REQUIRED` transactions commonly join it, but other propagation modes can create a different topology.

### Exam Trap

A test transaction is infrastructure around the test, not necessarily the exact production caller topology.

---

## TEST-B01-C017 — What is the default outcome of a transactional Spring integration test?

### Russian Translation

Каков результат transactional Spring integration test по умолчанию?

> [!answer]- Answer
> Rollback after the test method completes.

### Explanation

The default protects data isolation without requiring explicit cleanup for every test.

### Exam Trap

Rollback by default does not verify commit callbacks, triggers or durable side effects.

---

## TEST-B01-C018 — What does `@Commit` do on a transactional test?

### Russian Translation

Что делает `@Commit` на transactional test?

> [!answer]- Answer
> It instructs the test transaction to commit after the test instead of rolling back.

### Explanation

It is useful for targeted commit-boundary tests but requires deliberate cleanup or isolated infrastructure.

### Exam Trap

Using `@Commit` broadly can make tests order-dependent through persistent data.

---

## TEST-B01-C019 — What is the purpose of `TestTransaction`?

### Russian Translation

Для чего используется `TestTransaction`?

> [!answer]- Answer
> To inspect, start, end and choose commit or rollback for the current test-managed transaction programmatically.

### Explanation

It allows a test method to force commit-time behavior and then begin a new verification transaction.

### Exam Trap

Calling repository `flush()` is not the same as ending and committing the test transaction.

---

## TEST-B01-C020 — When do `@BeforeTransaction` and `@AfterTransaction` run?

### Russian Translation

Когда выполняются `@BeforeTransaction` и `@AfterTransaction`?

> [!answer]- Answer
> Outside the test-managed transaction, immediately before it begins and after it ends.

### Explanation

They are intended for setup or verification that must not participate in the test transaction.

### Exam Trap

`@BeforeEach` and `@AfterEach` normally run inside the test transaction for a transactional test method.

---

## TEST-B01-C021 — Why is `assertTimeoutPreemptively` dangerous with transactional tests?

### Russian Translation

Почему `assertTimeoutPreemptively` опасен с transactional tests?

> [!answer]- Answer
> It may execute code in another thread that does not inherit Spring's thread-bound test transaction, so writes can commit unexpectedly.

### Explanation

Spring binds imperative transaction state to the original test thread.

### Exam Trap

A timeout helper that starts another thread can invalidate rollback assumptions.

---

## TEST-B01-C022 — Does a test-level `rollbackFor` control test-managed rollback rules?

### Russian Translation

Управляет ли test-level `rollbackFor` правилами test-managed rollback?

> [!answer]- Answer
> No. Test-managed outcome should be controlled with `@Commit`, `@Rollback` or `TestTransaction`.

### Explanation

Not every production `@Transactional` attribute has the same meaning for the TestContext transaction wrapper.

### Exam Trap

Do not assume production rollback metadata is the supported control surface for test-managed completion.

---

## TEST-B01-C023 — Why can a transactional test hide production propagation behavior?

### Russian Translation

Почему transactional test может скрыть production propagation behavior?

> [!answer]- Answer
> The application service may join the surrounding test transaction, creating a different topology than a real non-transactional caller.

### Explanation

The test wrapper can eliminate the “no existing transaction” condition that production propagation depends on.

### Exam Trap

Propagation tests often need to call the service without test-level `@Transactional`.

---

## TEST-B01-C024 — What does `@RecordApplicationEvents` prove?

### Russian Translation

Что доказывает `@RecordApplicationEvents`?

> [!answer]- Answer
> It proves that application events were published in the test `ApplicationContext`.

### Explanation

The feature records in-process Spring application events for assertions.

### Exam Trap

It does not prove broker publication, external delivery or consumer processing.

---

## TEST-B01-C025 — What does `@AutoConfigureTestDatabase(replace = NONE)` protect?

### Russian Translation

Что защищает `@AutoConfigureTestDatabase(replace = NONE)`?

> [!answer]- Answer
> It prevents a configured datasource, such as Testcontainers PostgreSQL, from being replaced with an embedded test database.

### Explanation

The annotation preserves the intended database dialect and behavior in the test slice.

### Exam Trap

Creating a container is insufficient if the slice silently replaces its datasource.

---

## TEST-B01-C026 — Why should Testcontainers connection details be obtained dynamically?

### Russian Translation

Почему connection details Testcontainers нужно получать динамически?

> [!answer]- Answer
> Containers commonly use randomized host ports and may run on a non-local Docker host.

### Explanation

The container object is the source of truth for JDBC URL, username and password after startup.

### Exam Trap

Hard-coded `localhost:5432` can pass locally and fail in CI or remote Docker environments.

---

## TEST-B01-C027 — What does `@DynamicPropertySource` do?

### Russian Translation

Что делает `@DynamicPropertySource`?

> [!answer]- Answer
> It registers dynamic properties, such as container JDBC URL and credentials, before the Spring test context is created.

### Explanation

The values participate in environment preparation and therefore affect bean definitions and auto-configuration.

### Exam Trap

Registering properties after context creation is too late for datasource auto-configuration.

---

## TEST-B01-C028 — What is the lifecycle difference between static and instance `@Container` fields?

### Russian Translation

Чем отличается lifecycle static и instance `@Container` fields?

> [!answer]- Answer
> A static container is typically shared across all methods in the test class, while an instance container follows each test-instance lifecycle.

### Explanation

Static reuse improves speed but increases the need for explicit database isolation.

### Exam Trap

A shared container does not imply shared test data is safe.

---

## TEST-B01-C029 — Why is H2 not sufficient for PostgreSQL-specific repository tests?

### Russian Translation

Почему H2 недостаточно для PostgreSQL-specific repository tests?

> [!answer]- Answer
> H2 differs in SQL grammar, type system, MVCC, locking, planner, sequences and constraint behavior.

### Explanation

Compatibility mode can imitate syntax but cannot reproduce all semantics of the production database.

### Exam Trap

An H2 pass is not evidence that native SQL, locking or migration behavior works on PostgreSQL.

---

## TEST-B01-C030 — How should an optimistic-lock test create a real conflict?

### Russian Translation

Как optimistic-lock test должен создавать настоящий conflict?

> [!answer]- Answer
> Load the same row in two independent persistence contexts or transactions, commit one update, then commit the stale update.

### Explanation

A real conflict requires two independently versioned snapshots.

### Exam Trap

Two references loaded by one `EntityManager` are usually the same managed instance.

---

## TEST-B01-C031 — What infrastructure is required for a meaningful pessimistic-lock test?

### Russian Translation

Какая infrastructure нужна для meaningful pessimistic-lock test?

> [!answer]- Answer
> At least two transactions or connections, coordination between threads, a bounded timeout and the real target database.

### Explanation

The test must create genuine concurrent lock ownership and a measurable competing operation.

### Exam Trap

Sequential calls in one transaction do not test lock contention.

---

## TEST-B01-C032 — How can N+1 be protected by an automated regression test?

### Russian Translation

Как защититься от N+1 автоматическим regression test?

> [!answer]- Answer
> Execute the use case, initialize required associations and assert a Hibernate/JDBC statement-count or bounded query-count invariant.

### Explanation

Correct returned data does not reveal whether it required one query or hundreds.

### Exam Trap

Inspecting fetch annotations alone is not a performance regression test.

---

## TEST-B01-C033 — Why can exact SQL-count assertions become brittle?

### Russian Translation

Почему exact SQL-count assertions могут стать brittle?

> [!answer]- Answer
> Legitimate provider, batching or fetch-plan changes can alter statement count without violating the intended performance invariant.

### Explanation

Exact counts fit narrow repository contracts; service-level tests often need an upper bound or relative invariant.

### Exam Trap

Avoid replacing all query-count assertions with vague timing tests, which are even less deterministic.

---

## TEST-B01-C034 — What is the strongest way to test database migrations?

### Russian Translation

Как наиболее надёжно тестировать database migrations?

> [!answer]- Answer
> Start an empty real database container, apply production Flyway or Liquibase migrations, and then run schema and repository assertions.

### Explanation

This exercises the same migration scripts, dialect and object-creation order used in deployment.

### Exam Trap

Hibernate `create-drop` does not test production migration files.

---

## TEST-B01-C035 — Why can a manually stopped singleton container break later tests?

### Russian Translation

Почему вручную остановленный singleton container может сломать последующие tests?

> [!answer]- Answer
> Spring may reuse a cached `ApplicationContext` whose datasource still points to the stopped container.

### Explanation

Container lifetime and context-cache lifetime must be compatible.

### Exam Trap

Stopping shared infrastructure in one test class can invalidate other cached contexts.

---

## TEST-B01-C036 — What is the recommended layered strategy for Spring Data JPA testing?

### Russian Translation

Какова рекомендуемая layered strategy для Spring Data JPA testing?

> [!answer]- Answer
> Use unit tests for domain rules, `@DataJpaTest` for repository/mapping behavior, full-context tests for service transactions, and Testcontainers for real database semantics.

### Explanation

Each layer proves a different risk with the smallest context capable of producing trustworthy evidence.

### Exam Trap

One broad `@SpringBootTest` suite cannot efficiently replace focused unit, slice and real-database tests.

---

# Review matrix

| Risk | Best proof |
|---|---|
| pure domain rule | plain unit test |
| repository mapping/query | `@DataJpaTest` + flush/clear |
| service transaction | full context without test wrapper when topology matters |
| PostgreSQL semantics | Testcontainers PostgreSQL |
| N+1 | statement-count invariant |
| commit callback | explicit commit boundary |
