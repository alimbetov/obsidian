---
type: source-index
domain: spring
subdomain:
  - spring-data-jpa
  - jpa
  - hibernate
status: active
baseline:
  spring: 5.3.39
  spring_data_jpa: 2.7.18
  hibernate: 5.6.15.Final
tags:
  - sources
  - spring
  - jpa
  - hibernate
---

# Spring Data JPA Sources

## Version policy

Канонический executable baseline текущего vault:

```text
Java                8
Spring Framework    5.3.39
Spring Data JPA     2.7.18
Hibernate ORM       5.6.15.Final
JPA namespace       javax.persistence
```

Современная документация Spring Data JPA и Hibernate используется для проверки устойчивых концептов, но API, package namespace и новые return types нельзя автоматически переносить в baseline.

```text
Spring Data JPA 2.7 / Hibernate 5.6 → javax.persistence
Spring Data JPA 3+  / Hibernate 6+ → jakarta.persistence
```

---

# 1. Spring Data JPA 2.7.x

## Reference documentation

- Spring Data JPA 2.7 reference:
  https://docs.spring.io/spring-data/jpa/docs/2.7.x/reference/html/

Используется для:

- repository abstraction;
- query methods;
- transactionality;
- `Specification`;
- projections;
- locking;
- `@EntityGraph`;
- `@Modifying`;
- entity new-state detection.

## API index 2.7.18

- Package `org.springframework.data.jpa.repository`:
  https://docs.spring.io/spring-data/jpa/docs/2.7.18/api/org/springframework/data/jpa/repository/package-summary.html

- `SimpleJpaRepository`:
  https://docs.spring.io/spring-data/jpa/docs/2.7.18/api/org/springframework/data/jpa/repository/support/SimpleJpaRepository.html

- `EntityGraph`:
  https://docs.spring.io/spring-data/jpa/docs/2.7.18/api/org/springframework/data/jpa/repository/EntityGraph.html

- `JpaSpecificationExecutor`:
  https://docs.spring.io/spring-data/jpa/docs/2.7.18/api/org/springframework/data/jpa/repository/JpaSpecificationExecutor.html

- `Modifying`:
  https://docs.spring.io/spring-data/jpa/docs/2.7.18/api/org/springframework/data/jpa/repository/Modifying.html

- `Lock`:
  https://docs.spring.io/spring-data/jpa/docs/2.7.18/api/org/springframework/data/jpa/repository/Lock.html

## Transactionality

- Current transactionality chapter:
  https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html

Устойчивые правила:

- inherited CRUD methods use transaction configuration from `SimpleJpaRepository`;
- query methods declared by the user are not automatically transactional solely because they are query methods;
- service/facade transaction should define a multi-repository unit of work;
- changing managed entity state does not require `save()` from pure JPA perspective.

## Persisting entities

- Persisting entities/current new-state detection:
  https://docs.spring.io/spring-data/jpa/reference/jpa/entity-persistence.html

Используется для различия:

```text
new entity      → EntityManager.persist(...)
existing entity → EntityManager.merge(...)
```

и для `Persistable.isNew()`.

---

# 2. Query methods and repositories

## Query method fundamentals

- Defining query methods:
  https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html

Проверяемые темы:

- `CREATE`, `USE_DECLARED_QUERY`, `CREATE_IF_NOT_FOUND`;
- subject/predicate parsing;
- property traversal;
- underscore disambiguation;
- `Top`, `First`, `Distinct`;
- `Page`, `Slice`, `Sort`;
- stream resource ownership.

## JPA query methods

- JPA query methods:
  https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html

Проверяемые темы:

- `@Query`;
- JPQL vs native SQL;
- named parameters;
- `@Modifying`;
- entity graphs;
- locking;
- pagination/count-query considerations.

## Query keywords

- Repository query keywords:
  https://docs.spring.io/spring-data/jpa/reference/repositories/query-keywords-reference.html

Используется как справочник, а не как рекомендация строить длинные method names.

---

# 3. Specifications and dynamic queries

- Specifications:
  https://docs.spring.io/spring-data/jpa/reference/jpa/specifications.html

Основной контракт:

```java
Predicate toPredicate(
    Root<T> root,
    CriteriaQuery<?> query,
    CriteriaBuilder builder
)
```

`Specification` моделирует composable predicate. Для сложных reporting queries, grouping, window functions или provider-specific SQL может быть лучше custom repository/query implementation.

---

# 4. Projections

- Projections:
  https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html

Проверяемые темы:

- closed interface projection;
- open projection and SpEL cost;
- class/DTO projection;
- constructor expression;
- dynamic projection;
- nested-property joins;
- projection proxy semantics.

Version-sensitive note:

- modern docs use Java records as examples;
- Java 8 baseline lab uses ordinary interfaces/classes.

---

# 5. Pagination and large result traversal

- Paging, sorting and limiting:
  https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html#repositories.special-parameters

Stable contrast:

```text
Page  → content + total count metadata
Slice → content + hasNext, usually no total count
```

Version-sensitive additions such as `Window`, `ScrollPosition` and dedicated `Limit` belong to newer Spring Data generations and are not assumed available in 2.7.18.

---

# 6. Hibernate 5.6 persistence context

- Hibernate ORM 5.6 User Guide:
  https://docs.hibernate.org/orm/5.6/userguide/html_single/Hibernate_User_Guide.html

Core sections:

- persistence context;
- entity states;
- modifying managed state;
- detach/merge;
- flushing;
- batching;
- fetching;
- locking;
- statistics.

Stable lifecycle states:

```text
transient
managed/persistent
detached
removed
```

## Dirty checking and write-behind

Hibernate defines persistence context as a transactional write-behind cache. Managed state changes are translated to `INSERT`, `UPDATE` and `DELETE` during flush.

Important distinction:

```text
flush  → synchronize persistence context to database transaction
commit → make database transaction durable/visible according to DB semantics
```

## Statistics

Hibernate Statistics API:

- `SessionFactory#getStatistics()`;
- `Statistics#getPrepareStatementCount()`;
- entity/collection fetch counters.

Lab uses statement count to demonstrate N+1 rather than relying only on log inspection.

---

# 7. Jakarta Persistence/JPA specification boundary

For normative provider-independent semantics, consult the matching JPA 2.2 specification/API generation.

- JPA 2.2 API:
  https://javadoc.io/doc/javax.persistence/javax.persistence-api/2.2/index.html

Relevant APIs:

- `EntityManager`;
- `PersistenceContext` semantics;
- `FlushModeType`;
- `LockModeType`;
- `@Version`;
- `EntityGraph`;
- Criteria API.

Provider-specific SQL timing, batching, bytecode enhancement and locking SQL remain Hibernate/database concerns.

---

# 8. Production database verification

H2 is suitable for compact lifecycle demonstrations but must not be treated as proof of PostgreSQL/Oracle behavior.

Production integration tests should cover:

- actual dialect SQL;
- execution plans;
- MVCC visibility;
- lock waits and timeouts;
- deadlocks;
- unique/foreign-key constraint timing;
- pagination under concurrent writes;
- `SKIP LOCKED` or database-specific features;
- sequence/identity allocation;
- batching behavior.

## Related vault materials

- [[10_CONCEPTS/Spring/Data/Spring Data JPA Persistence Context and Entity Lifecycle]]
- [[10_CONCEPTS/Spring/Data/Spring Data Repositories Queries and Fetching]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/DATA-B01/DATA-B01 Cards]]
- [[40_PRODUCTION_CASES/Spring/Spring Data JPA Production Cases]]
- [[50_LABS/Spring/DATA-B01/README]]
