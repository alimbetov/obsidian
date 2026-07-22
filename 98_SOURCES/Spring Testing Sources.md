---
type: source-index
domain: spring
subdomain:
  - testing
status: active
source_policy: primary-official
versions:
  spring: 5.3.39
  spring_boot: 2.7.18
  testcontainers: 1.19.8-baseline
---

# Spring Testing Sources

> [!summary]
> Primary official sources for TEST-B01. The learning baseline is Spring Framework 5.3.39, Spring Boot 2.7.18 and Java 8. Current documentation is used only when the contract is explicitly marked version-stable or when comparing generations.

# Spring Framework 5.3

## Testing reference

- [Spring Framework 5.3 Testing](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/testing.html)

Use for:

- TestContext Framework;
- `TestContextManager`;
- `TestExecutionListener`;
- context loading and caching;
- transactional test execution;
- `@Commit` and `@Rollback`;
- `@BeforeTransaction` and `@AfterTransaction`;
- `TestTransaction`;
- manual flush false-positive warning;
- preemptive timeout/thread-bound transaction warning;
- `@Sql`;
- `@RecordApplicationEvents`.

## Current transaction-test reference

- [Spring TestContext Transaction Management](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/tx.html)

Use as a current comparison source. Verify package/API differences before applying examples to Spring 5.3.

## Test context cache

- [Context Caching](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/testing.html#testcontext-ctx-management-caching)

Use for:

- cache-key inputs;
- static cache behavior;
- `@DirtiesContext`;
- cache statistics logging.

# Spring Boot 2.7.18

## Reference documentation

- [Spring Boot 2.7.18 Reference](https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/)

Use for:

- Boot testing support;
- test slices;
- auto-configured JPA tests;
- test database replacement;
- `TestEntityManager`;
- test auto-configuration appendix.

## Test auto-configuration appendix

- [Spring Boot 2.7 Test Auto-configuration](https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/test-auto-configuration.html)

Use for the exact auto-configurations imported by:

- `@DataJpaTest`;
- `@JdbcTest`;
- `@WebMvcTest`;
- other slices.

## Boot 2.7 API

- [Spring Boot 2.7.18 API](https://docs.spring.io/spring-boot/docs/2.7.x/api/)

Relevant APIs:

- `org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest`;
- `TestEntityManager`;
- `AutoConfigureTestDatabase`;
- `SpringBootTest`;
- `MockBean`.

# Spring Data JPA

- [Spring Data JPA 2.7 Reference](https://docs.spring.io/spring-data/jpa/docs/2.7.18/reference/html/)
- [Transactionality](https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html)
- [Repository query methods](https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html)

Use for:

- inherited repository transaction metadata;
- query behavior;
- repository proxy semantics;
- persistence-context interaction.

# Hibernate ORM 5.6

- [Hibernate ORM 5.6 User Guide](https://docs.hibernate.org/orm/5.6/userguide/html_single/Hibernate_User_Guide.html)

Use for:

- persistence context;
- flush;
- dirty checking;
- statistics;
- optimistic/pessimistic locking;
- fetch plans;
- batching.

# Testcontainers

## Main documentation

- [Testcontainers for Java](https://java.testcontainers.org/)

## JUnit Jupiter integration

- [JUnit 5 integration](https://java.testcontainers.org/test_framework_integration/junit_5/)

Use for:

- `@Testcontainers`;
- `@Container`;
- static vs instance container lifecycle.

## PostgreSQL module

- [PostgreSQL module](https://java.testcontainers.org/modules/databases/postgres/)

Use for:

- `PostgreSQLContainer`;
- JDBC URL and credentials;
- PostgreSQL image selection.

## Database containers

- [Database containers](https://java.testcontainers.org/modules/databases/)

Important distinction from the official documentation:

```text
H2 is faster.
A real database container provides actual database compatibility.
```

## Runtime requirements

- [Supported container runtimes](https://java.testcontainers.org/supported_docker_environment/)

Use for CI/local Docker requirements.

## Reusable containers

- [Reusable containers — experimental](https://java.testcontainers.org/features/reuse/)

Do not enable reuse by default in CI. It is opt-in and has different cleanup semantics.

# JUnit Jupiter

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

Use for:

- extension model;
- lifecycle;
- test instances;
- timeouts;
- parallel execution;
- assertions.

When using preemptive timeout APIs, combine the JUnit execution model with Spring's warning that test-managed transaction state is thread-bound.

# Version boundary

```text
Spring Boot 2.7 / Spring 5.3
    → javax.persistence
    → Java 8 baseline

Spring Boot 3+
    → jakarta.persistence
    → newer Java baseline
```

Do not copy current Boot 3/4 package names into the TEST-B01 Java 8 lab.

# Evidence policy

A test claim is considered proven only at the correct layer:

| Claim | Required evidence |
|---|---|
| Java branch works | unit test |
| repository mapping works | JPA slice + flush/clear |
| service rollback works | proxy-backed integration test |
| PostgreSQL query works | PostgreSQL test |
| migration works | migration tool on empty/upgrade schema |
| N+1 absent | SQL/statement count |
| optimistic conflict works | two persistence contexts |
| after-commit callback works | real commit |
| broker delivery survives crash | durable integration/system test |
