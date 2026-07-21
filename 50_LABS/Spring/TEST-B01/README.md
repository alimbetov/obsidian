---
type: lab
domain: spring
subdomain:
  - testing
  - data-jpa-testing
  - testcontainers
status: ready-for-runtime-validation
java_versions:
  - 8
spring_boot_versions:
  - 2.7.18
testcontainers_versions:
  - 1.19.8
tags:
  - spring
  - lab
  - junit
  - datajpatest
  - postgresql
---

# TEST-B01 — Spring Testing Lab

Этот project показывает четыре разных testing boundaries:

```text
@DataJpaTest + H2
    → mapping, repository, dirty checking, constraints, SQL counts

@SpringBootTest without test transaction
    → application transaction proxy and rollback outcome

@SpringBootTest + TestTransaction
    → explicit test commit/rollback boundary

@DataJpaTest + PostgreSQL Testcontainers
    → real dialect, native ILIKE and PostgreSQL constraint behavior
```

# Versions

```text
Java                  8 baseline
Spring Boot           2.7.18
Spring Framework      5.3.39 through Boot dependency management
Spring Data JPA       2.7.18 through Boot dependency management
Hibernate             5.6.x through Boot dependency management
JUnit Jupiter         Boot-managed
Testcontainers        1.19.8
PostgreSQL image      postgres:15-alpine
```

Boot 2.7 uses `javax.persistence`. Boot 3+ uses `jakarta.persistence`; do not mechanically mix imports between generations.

# Project structure

```text
50_LABS/Spring/TEST-B01/
├── pom.xml
├── README.md
└── src
    ├── main/java/lab
    │   ├── TestingLabApplication.java
    │   ├── PurchaseOrder.java
    │   ├── PurchaseOrderLine.java
    │   ├── PurchaseOrderRepository.java
    │   └── PurchaseOrderService.java
    └── test/java/lab
        ├── PurchaseOrderRepositorySliceTest.java
        ├── PurchaseOrderServiceTransactionTest.java
        ├── PurchaseOrderCommitBoundaryTest.java
        └── PostgreSqlPurchaseOrderRepositoryTest.java
```

# Run all tests

Requirements:

- JDK 8+;
- Maven 3.8+;
- Docker-compatible runtime for Testcontainers tests.

```bash
cd 50_LABS/Spring/TEST-B01
mvn clean test
```

# Run only H2 and full-context tests

These tests do not need Docker:

```bash
mvn -Dtest=PurchaseOrderRepositorySliceTest test
mvn -Dtest=PurchaseOrderServiceTransactionTest test
mvn -Dtest=PurchaseOrderCommitBoundaryTest test
```

# Run PostgreSQL Testcontainers test

```bash
mvn -Dtest=PostgreSqlPurchaseOrderRepositoryTest test
```

Testcontainers will:

1. discover Docker;
2. pull `postgres:15-alpine` if missing;
3. start PostgreSQL on a randomized host port;
4. publish JDBC properties through `@DynamicPropertySource`;
5. stop and remove the container after the class.

# Experiment 1 — slice boundary

Test:

```java
sliceLoadsRepositoryButNotRegularService()
```

Expected invariant:

```text
PurchaseOrderRepository bean exists
PurchaseOrderService bean does not exist
```

Why:

`@DataJpaTest` intentionally loads the JPA slice, not the normal service graph.

Controlled change:

```java
@DataJpaTest
@Import(PurchaseOrderService.class)
```

Now the service is included explicitly. This demonstrates that a slice can be extended narrowly without switching immediately to `@SpringBootTest`.

# Experiment 2 — database round trip

Test:

```java
flushAndClearProveDatabaseRoundTrip()
```

Sequence:

```text
repository.save
    ↓
repository.flush
    ↓
TestEntityManager.clear
    ↓
repository.findById
    ↓
new Java object reconstructed from DB row
```

Assertions:

```text
reloaded != saved
order number survives mapping
two order lines survive cascade mapping
```

Remove `clear()` and observe that the test can return the same managed instance.

# Experiment 3 — deferred constraint evidence

Test:

```java
duplicateBusinessKeyFailsWhenSqlIsFlushed()
```

Sequence:

```text
first unique row inserted
    ↓
flush
    ↓
second duplicate scheduled
    ↓
flush
    ↓
DataIntegrityViolationException
```

The explicit flush ties the exception to the repository operation rather than test cleanup.

# Experiment 4 — dirty checking

Test:

```java
dirtyCheckingPersistsManagedChangesWithoutRepositorySave()
```

The test intentionally does not call `repository.save(managed)` after changing status.

```text
managed entity changed
    ↓
flush
    ↓
Hibernate dirty checking emits UPDATE
    ↓
clear and reload
```

# Experiment 5 — N+1 regression

Test:

```java
entityGraphPreventsNPlusOneRegression()
```

Fixture:

```text
3 purchase orders
2 lines per order
```

Plain LAZY traversal:

```text
1 order query
3 line queries
--------------
4 statements
```

Entity graph:

```text
1 joined/fetch-plan query
```

The test uses Hibernate `Statistics`, not visual log inspection.

Controlled change:

Remove `@EntityGraph(attributePaths = "lines")` from:

```java
findAllByStatusOrderByIdAsc
```

Expected failure:

```text
graphStatements expected 1 but becomes 4
```

# Experiment 6 — Page count query

Test:

```java
pageExecutesContentAndCountQueries()
```

With three matching rows and page size two:

```text
content query
count query
-----------
2 statements
```

This demonstrates why `Page` can be more expensive than `Slice`.

# Experiment 7 — real service transaction

Test class:

```java
PurchaseOrderServiceTransactionTest
```

The test methods are not annotated `@Transactional`. Therefore the call topology resembles a normal application caller:

```text
JUnit test
    ↓
Spring proxy
    ↓
PurchaseOrderService transaction
    ↓
repository
```

Success case:

```text
service.create
    ↓
commit
    ↓
row exists
```

Failure case:

```text
service.createAndFail
    ↓
INSERT scheduled/executed
    ↓
IllegalStateException
    ↓
rollback
    ↓
row absent
```

Add `@Transactional` to the test class and reason about how the service would join the surrounding test transaction.

# Experiment 8 — explicit test transaction boundary

Test class:

```java
PurchaseOrderCommitBoundaryTest
```

Commit path:

```java
TestTransaction.flagForCommit();
TestTransaction.end();
```

Rollback path:

```java
TestTransaction.flagForRollback();
TestTransaction.end();
```

Both tests call `TestTransaction.start()` again so the TestContext listener finishes with an active test transaction that can be rolled back normally.

`@DirtiesContext(AFTER_EACH_TEST_METHOD)` is used here deliberately because the test demonstrates committed state. In normal suites prefer a cheaper database cleanup strategy.

# Experiment 9 — PostgreSQL native query

Repository query:

```sql
select *
from purchase_orders
where order_number ilike concat('%', :fragment, '%')
order by id
```

Test:

```java
nativeIlikeQueryUsesRealPostgreSqlDialect()
```

This test is intentionally not run on H2. It proves PostgreSQL `ILIKE` behavior.

# Experiment 10 — PostgreSQL unique constraint

Test:

```java
uniqueConstraintIsEnforcedByPostgreSql()
```

The datasource is protected from embedded replacement by:

```java
@AutoConfigureTestDatabase(replace = Replace.NONE)
```

Remove this annotation and inspect which database and dialect actually start.

# Exercises

## Exercise 1 — false positive without flush

Remove the final `repository.flush()` from the duplicate-key test. Determine whether the exception still appears during the assertion, transaction completion or not at all.

## Exercise 2 — first-level cache trap

Remove `entityManager.clear()` from the round-trip test and assert:

```java
assertThat(reloaded).isSameAs(saved);
```

Explain why this is not database proof.

## Exercise 3 — Slice vs full context

Try to autowire `PurchaseOrderService` into `PurchaseOrderRepositorySliceTest` without `@Import`. Explain the failure.

## Exercise 4 — H2 vs PostgreSQL

Move the `ILIKE` method into an H2-only test. Compare behavior and explain why a compatibility success would still not prove production semantics.

## Exercise 5 — Test transaction changes topology

Annotate `PurchaseOrderServiceTransactionTest` with `@Transactional`. Draw:

```text
test-managed transaction
    ↓
service REQUIRED
    ↓
repository
```

Then remove test transaction and draw production topology.

## Exercise 6 — N+1 upper bound

Replace exact statement count with:

```java
assertThat(statementCount).isLessThanOrEqualTo(2L);
```

Discuss when exact and bounded assertions are appropriate.

## Exercise 7 — Migration test

Add Flyway, disable Hibernate schema generation and let the PostgreSQL test apply a real `V1__create_orders.sql` migration.

## Exercise 8 — Optimistic conflict

Add a test with two independent `EntityManager` instances that update the same `@Version` row.

## Exercise 9 — Pessimistic lock

Use two threads and two transactions. TX-1 calls `findLockedById`; TX-2 attempts the same lock. Add a bounded lock timeout and run only on PostgreSQL.

## Exercise 10 — after-commit event

Add `@TransactionalEventListener(AFTER_COMMIT)` and prove:

```text
default rollback → no callback
explicit commit  → callback
```

# Validation status

Completed in the repository:

- Maven structure created;
- Java 8-compatible source style reviewed;
- repository slice and full-context boundaries reviewed;
- `PostgreSQLContainer` generic construction corrected;
- datasource replacement disabled for PostgreSQL test;
- TestContext and Testcontainers source contracts documented.

Not executed in the current environment:

```text
mvn clean test
Docker/Testcontainers runtime
```

The current execution environment does not provide Maven dependency resolution or a Docker runtime. Therefore this lab is **structure-reviewed**, not claimed as runtime-PASS.
