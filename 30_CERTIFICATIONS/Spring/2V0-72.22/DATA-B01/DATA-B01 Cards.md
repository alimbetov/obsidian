---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain:
  - spring-data-jpa
  - jpa
batch_id: DATA-B01
status: published
normalization_status: complete
card_count: 36
first_card: DATA-B01-C001
last_card: DATA-B01-C036
spring_versions:
  - 5.3.39
spring_data_jpa_versions:
  - 2.7.18
hibernate_versions:
  - 5.6.15.Final
tags:
  - spring
  - certification
  - jpa
  - repository
  - persistence-context
---

# DATA-B01 — Spring Data and JPA

> [!summary]
> 36 normalized cards по persistence context, entity lifecycle, dirty checking, flush, locking, repository proxies, queries, Specifications, projections, pagination, N+1 и fetch plans.

## Route navigation

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Data JPA Roadmap]]
- [[10_CONCEPTS/Spring/Data/Spring Data JPA Persistence Context and Entity Lifecycle]]
- [[10_CONCEPTS/Spring/Data/Spring Data Repositories Queries and Fetching]]
- [[10_CONCEPTS/Spring/Data/Spring Data JPA Visual Deep Dive]]
- [[40_PRODUCTION_CASES/Spring/Spring Data JPA Production Cases]]
- [[50_LABS/Spring/DATA-B01/README]]
- [[98_SOURCES/Spring Data JPA Sources]]

---

## DATA-B01-C001 — What is a JPA persistence context?

### Russian Translation

Что такое persistence context в JPA?

> [!answer]- Answer
> A persistence context is the set of entity instances managed by an `EntityManager`, with one canonical managed instance for each persistent identity.

### Explanation

It acts as an identity map and unit of work. Managed changes are tracked and synchronized with the database during flush.

### Exam Trap

A persistence context is not Redis, a second-level cache or the database transaction itself.

---

## DATA-B01-C002 — What are the main JPA entity lifecycle states?

### Russian Translation

Каковы основные состояния JPA entity?

> [!answer]- Answer
> New, managed, detached and removed.

### Explanation

New has not entered a persistence context; managed is tracked; detached has identity but is no longer tracked; removed is scheduled for deletion.

### Exam Trap

A non-null ID does not automatically make an object managed.

---

## DATA-B01-C003 — What happens to a new entity passed to `persist()`?

### Russian Translation

Что происходит с новой entity при вызове `persist()`?

> [!answer]- Answer
> The same object instance becomes managed and is scheduled for insertion at or before flush/commit.

### Explanation

`persist()` changes the entity's lifecycle state immediately, while SQL timing depends on flush mode and identifier strategy.

### Exam Trap

`persist()` does not guarantee an immediate `INSERT` statement.

---

## DATA-B01-C004 — Does `merge()` make the argument instance managed?

### Russian Translation

Делает ли `merge()` переданный object managed?

> [!answer]- Answer
> No. It copies the argument state into a managed instance and returns that managed copy; the original remains detached.

### Explanation

Subsequent managed changes must be applied to the returned instance, not assumed to affect the detached argument.

### Exam Trap

Ignoring the return value of `merge()` commonly leads to updates being applied to the wrong Java instance.

### Mini Example

```java
Customer managed = entityManager.merge(detached);
assert managed != detached;
```

---

## DATA-B01-C005 — What is dirty checking?

### Russian Translation

Что такое dirty checking?

> [!answer]- Answer
> It is detection of changes to managed entity state so the provider can generate SQL updates during flush.

### Explanation

The provider compares current managed state with snapshots or enhanced tracking metadata and queues updates for changed persistent attributes.

### Exam Trap

Dirty checking applies to managed entities; changing a detached entity does not automatically schedule an update.

### Production Transfer

A managed entity normally does not need an explicit repository `save()` after every setter call.

---

## DATA-B01-C006 — What is the difference between flush and commit?

### Russian Translation

Чем flush отличается от commit?

> [!answer]- Answer
> Flush synchronizes persistence-context changes to SQL inside the current transaction; commit finalizes the transaction.

### Explanation

Flush can expose constraint failures and acquire locks before commit, but the transaction can still roll back afterward.

### Exam Trap

A flushed `INSERT` is not yet durable and can still be rolled back.

---

## DATA-B01-C007 — Can AUTO flush happen before transaction commit?

### Russian Translation

Может ли AUTO flush произойти до commit?

> [!answer]- Answer
> Yes. A provider may flush before a query whose result must reflect pending entity changes, as well as before commit.

### Explanation

AUTO mode preserves query consistency by synchronizing relevant pending changes before executing overlapping queries.

### Exam Trap

Do not assume that no SQL runs until the annotated method returns.

---

## DATA-B01-C008 — What does `EntityManager.clear()` do?

### Russian Translation

Что делает `EntityManager.clear()`?

> [!answer]- Answer
> It detaches all managed entities from the persistence context.

### Explanation

After `clear()`, changes to those Java objects are no longer tracked unless they are merged or loaded again.

### Exam Trap

Calling `clear()` before flushing can discard pending managed changes from the unit of work.

### Production Transfer

In batch processing, use `flush()` before `clear()` when queued changes must be preserved.

---

## DATA-B01-C009 — What does `refresh(entity)` do?

### Russian Translation

Что делает `refresh(entity)`?

> [!answer]- Answer
> It reloads a managed entity from the database and overwrites its current in-memory state.

### Explanation

Refresh is useful when database-side changes must replace the managed snapshot, but it deliberately discards local unflushed state.

### Exam Trap

`refresh()` is not a merge and cannot be used on an arbitrary detached object.

---

## DATA-B01-C010 — What does a JPA cascade control?

### Russian Translation

Что контролирует JPA cascade?

> [!answer]- Answer
> It controls whether an EntityManager lifecycle operation such as persist, merge or remove is propagated across an association.

### Explanation

Cascade settings describe object-graph operation propagation inside JPA, independently for persist, merge, remove, refresh and detach.

### Exam Trap

JPA cascade is not the same mechanism as database `ON DELETE CASCADE`.

---

## DATA-B01-C011 — How do `orphanRemoval` and `CascadeType.REMOVE` differ?

### Russian Translation

Чем отличаются `orphanRemoval` и `CascadeType.REMOVE`?

> [!answer]- Answer
> `orphanRemoval` deletes a child removed from its owning parent relationship; cascade REMOVE propagates deletion when the parent itself is removed.

### Explanation

One reacts to unlinking a dependent child, while the other reacts to a remove operation on the parent entity.

### Exam Trap

Removing a child from a collection does not imply deletion unless orphan removal or explicit removal semantics apply.

---

## DATA-B01-C012 — Which side owns a bidirectional association?

### Russian Translation

Какая сторона владеет bidirectional association?

> [!answer]- Answer
> The owning side defines the foreign-key mapping, typically with `@JoinColumn`; `mappedBy` identifies the inverse side.

### Explanation

JPA uses the owning side to decide which relationship state is written to the database.

### Exam Trap

Updating only the inverse side may leave the database relationship unchanged even if the Java graph looks correct.

### Production Transfer

Helper methods should update both Java sides of a bidirectional relationship.

---

## DATA-B01-C013 — Does `FetchType.EAGER` guarantee one SQL query?

### Russian Translation

Гарантирует ли `FetchType.EAGER` один SQL query?

> [!answer]- Answer
> No. The provider may use joins or additional selects, and EAGER can still produce N+1 behavior.

### Explanation

EAGER specifies when data must be available, not the exact SQL fetch strategy used to obtain it.

### Exam Trap

A fetch requirement is not a promise of a single join query.

---

## DATA-B01-C014 — What commonly causes `LazyInitializationException`?

### Russian Translation

Что обычно вызывает `LazyInitializationException`?

> [!answer]- Answer
> Accessing an uninitialized lazy association after the owning persistence context has closed.

### Explanation

The proxy or persistent collection needs an active provider session to load data that was not fetched earlier.

### Exam Trap

Changing every association to EAGER hides the boundary problem and can create over-fetching or N+1 queries.

### Best Fix

Fetch and map required data inside the application transaction using a projection, fetch join or entity graph.

---

## DATA-B01-C015 — What trade-off does Open Session in View introduce?

### Russian Translation

Какой trade-off создаёт Open Session in View?

> [!answer]- Answer
> It permits lazy loading later in the web request but can move SQL outside explicit service transaction boundaries and hide N+1 queries.

### Explanation

OSIV extends persistence-context availability into the presentation layer, reducing immediate lazy failures while weakening query ownership.

### Exam Trap

OSIV is not a substitute for an intentional query and DTO boundary.

---

## DATA-B01-C016 — How does `@Version` support optimistic locking?

### Russian Translation

Как `@Version` поддерживает optimistic locking?

> [!answer]- Answer
> Updates include the previously read version in the `WHERE` clause and increment it; zero updated rows indicates a stale writer.

### Explanation

The version column detects concurrent modification without holding a database lock for the entire user interaction.

### Exam Trap

Optimistic locking detects a conflict; it does not automatically merge two competing business changes.

### Mini Example

```sql
update customer
set name=?, version=version+1
where id=? and version=?
```

---

## DATA-B01-C017 — What is the main cost of pessimistic locking?

### Russian Translation

Какова основная цена pessimistic locking?

> [!answer]- Answer
> It blocks competing transactions and can cause lock waits, deadlocks, timeouts and reduced throughput.

### Explanation

The database coordinates access immediately, trading conflict prevention for resource retention and contention risk.

### Exam Trap

A pessimistic lock does not make a long transaction harmless; longer lock duration increases operational risk.

### Production Transfer

Keep locked transactions short and acquire rows in a stable order.

---

## DATA-B01-C018 — Why is entity `hashCode()` based only on a generated ID risky?

### Russian Translation

Почему `hashCode()` entity только по generated ID опасен?

> [!answer]- Answer
> The ID can change from null to generated after insertion, changing the hash code while the object is already stored in a hash-based collection.

### Explanation

Hash-based collections assume the key's hash code remains stable while the element is contained.

### Exam Trap

Do not include mutable collections and associations in entity equality because their state also changes over time.

---

## DATA-B01-C019 — What object usually backs a Spring Data JPA repository interface?

### Russian Translation

Какой object обычно стоит за Spring Data JPA repository interface?

> [!answer]- Answer
> A Spring-created repository proxy that routes methods to base CRUD implementation, query execution infrastructure or custom fragments.

### Explanation

The interface is metadata for runtime composition; Spring selects an implementation path per method.

### Exam Trap

A repository interface is not instantiated directly by the JVM and does not require a handwritten implementation for every method.

---

## DATA-B01-C020 — What implementation commonly handles inherited JPA CRUD methods?

### Russian Translation

Какая implementation обычно обрабатывает inherited JPA CRUD methods?

> [!answer]- Answer
> `SimpleJpaRepository`.

### Explanation

Repository proxies delegate inherited CRUD behavior to this base implementation while derived queries and fragments use other paths.

### Exam Trap

The proxy itself and `SimpleJpaRepository` are related but not the same object role.

---

## DATA-B01-C021 — Should transaction boundaries normally be defined only by repository defaults?

### Russian Translation

Следует ли полагаться только на transaction defaults repository methods?

> [!answer]- Answer
> Usually no. A service or facade should define the unit-of-work boundary coordinating repositories, mapping and business rules.

### Explanation

Repository defaults apply method by method, while an application use case often spans several calls that must share one transaction.

### Exam Trap

Several successful repository calls do not become one atomic use case unless an outer transaction coordinates them.

---

## DATA-B01-C022 — When is query derivation a good choice?

### Russian Translation

Когда query derivation является хорошим выбором?

> [!answer]- Answer
> For short, stable and readable predicates that can be clearly expressed in a repository method name.

### Explanation

Derived queries reduce boilerplate when the method name remains an accurate and maintainable representation of the predicate.

### Exam Trap

A very long derived method name is not more maintainable than an explicit query or Specification.

---

## DATA-B01-C023 — What is the difference between JPQL and native SQL in `@Query`?

### Russian Translation

Чем JPQL отличается от native SQL в `@Query`?

> [!answer]- Answer
> JPQL queries the entity model and is translated by the provider; native SQL targets database tables and vendor SQL directly.

### Explanation

JPQL improves object-model portability, while native SQL exposes database-specific syntax and result mapping responsibilities.

### Exam Trap

A native query may require a separate count query and explicit mapping for pagination or projections.

---

## DATA-B01-C024 — Why is `@Modifying` required for update/delete `@Query` methods?

### Russian Translation

Почему `@Modifying` нужен для update/delete `@Query` methods?

> [!answer]- Answer
> It tells Spring Data that the declared query performs DML rather than returning a result set.

### Explanation

Spring Data changes the execution path so the query returns an update count and can apply flush/clear options.

### Exam Trap

`@Modifying` does not automatically add a transaction to every declared method.

---

## DATA-B01-C025 — Why can bulk JPQL update leave stale entities?

### Russian Translation

Почему bulk JPQL update может оставить stale entities?

> [!answer]- Answer
> Bulk DML updates database rows directly and does not synchronize every managed entity already present in the persistence context.

### Explanation

The identity map can continue returning old in-memory state after the database has changed underneath it.

### Exam Trap

A successful update count does not prove managed instances were refreshed.

### Best Fix

Use controlled flush/clear/refresh or `@Modifying(clearAutomatically = true)` according to the use case.

---

## DATA-B01-C026 — What is a Spring Data JPA `Specification`?

### Russian Translation

Что такое Spring Data JPA `Specification`?

> [!answer]- Answer
> A composable predicate over an entity expressed through the JPA Criteria API and executed through `JpaSpecificationExecutor`.

### Explanation

Specifications allow optional filters to be combined while keeping predicate construction separate from repository method-name parsing.

### Exam Trap

A Specification improves composition, not automatically SQL selectivity, indexing or join efficiency.

---

## DATA-B01-C027 — What problem do projections solve?

### Russian Translation

Какую проблему решают projections?

> [!answer]- Answer
> They return a dedicated read model containing selected attributes instead of materializing and exposing an entire entity aggregate.

### Explanation

A projection can reduce transferred columns, detach API shape from entity shape and avoid accidental lazy traversal.

### Exam Trap

Nested projection properties can still require joins and materialize more data than expected.

---

## DATA-B01-C028 — How do `Page` and `Slice` differ?

### Russian Translation

Чем отличаются `Page` и `Slice`?

> [!answer]- Answer
> `Page` normally includes total-count information and therefore often runs a count query; `Slice` only determines whether another chunk exists.

### Explanation

`Slice` retrieves one extra element to determine `hasNext`, avoiding a global total when the business flow does not require it.

### Exam Trap

Returning `Page` can make an otherwise fast content query expensive because of the additional count query.

---

## DATA-B01-C029 — Why should pagination sorting include a unique tie-breaker?

### Russian Translation

Почему pagination sort должен включать unique tie-breaker?

> [!answer]- Answer
> Rows with equal primary sort values otherwise have no deterministic order and can move between pages.

### Explanation

A unique final key creates a total ordering, which is required for stable offset or keyset pagination.

### Exam Trap

Sorting only by a timestamp is not deterministic when several rows share the same timestamp.

### Mini Example

```text
ORDER BY created_at DESC, id DESC
```

---

## DATA-B01-C030 — What is the N+1 query problem?

### Russian Translation

Что такое N+1 query problem?

> [!answer]- Answer
> One query loads N parent rows and subsequent lazy access triggers up to N additional queries for related data.

### Explanation

The failure is caused by per-row relationship loading after the initial parent query, often during mapping or serialization.

### Exam Trap

The presence of LAZY or EAGER alone does not prove N+1; statement counts and execution traces do.

---

## DATA-B01-C031 — What does a fetch join do?

### Russian Translation

Что делает fetch join?

> [!answer]- Answer
> It instructs a query to fetch a selected association as part of that query result, usually reducing later lazy selects.

### Explanation

The query controls the fetch plan for one use case without globally changing the mapping default.

### Exam Trap

Fetching multiple collections can create a cartesian product and duplicate root rows.

---

## DATA-B01-C032 — What does Spring Data `@EntityGraph` configure?

### Russian Translation

Что конфигурирует Spring Data `@EntityGraph`?

> [!answer]- Answer
> It applies a named or ad-hoc JPA entity graph as a fetch plan to a repository query method.

### Explanation

The predicate and fetch plan remain separate, allowing the same query condition to load different graph shapes.

### Exam Trap

An entity graph does not guarantee one SQL statement; the provider still chooses the physical fetch strategy.

---

## DATA-B01-C033 — Why is a collection fetch join problematic with `Page`?

### Russian Translation

Почему collection fetch join проблематичен вместе с `Page`?

> [!answer]- Answer
> SQL rows represent parent-child combinations while pagination must count and limit parent entities, which can cause incorrect or in-memory pagination and expensive count queries.

### Explanation

Limiting joined rows is not equivalent to limiting distinct parent entities when one parent has many children.

### Exam Trap

Adding `DISTINCT` does not automatically make collection fetch pagination efficient or correct at the SQL limit stage.

### Best Fix

Page root IDs first, then fetch the graph by those IDs, or use a projection.

---

## DATA-B01-C034 — What does `getReferenceById()` commonly return?

### Russian Translation

Что обычно возвращает `getReferenceById()`?

> [!answer]- Answer
> A lazy entity reference or proxy that may avoid an immediate `SELECT` until state is accessed.

### Explanation

The reference can be useful for setting foreign-key relationships when only identity is required.

### Exam Trap

A reference is not proof that the row exists; state access can fail later.

---

## DATA-B01-C035 — What is the difference between JPA auditing and a domain audit log?

### Russian Translation

Чем JPA auditing отличается от domain audit log?

> [!answer]- Answer
> JPA auditing records technical creator/modifier timestamps and actors, while a domain audit log records business events and decisions.

### Explanation

Technical metadata answers who/when changed the current row; a domain history explains what business action occurred and preserves prior events.

### Exam Trap

`@LastModifiedDate` is not an immutable history of all changes.

---

## DATA-B01-C036 — When should a query return an entity instead of a projection?

### Russian Translation

Когда query должен возвращать entity, а не projection?

> [!answer]- Answer
> When the use case needs a managed aggregate for mutation, invariants and dirty checking; read-only endpoints often benefit from projections or DTOs.

### Explanation

Choose entity materialization for behavior-rich transactional work and a dedicated read model for query-oriented output.

### Exam Trap

Returning entities from every read endpoint couples API serialization to persistence state and lazy-loading behavior.

---

# Review matrix

| Confusion pair | Correct distinction |
|---|---|
| persistence context vs transaction | identity/unit of work vs atomic resource boundary |
| `persist` vs `merge` | manage original vs return managed copy |
| flush vs commit | synchronize SQL vs finalize transaction |
| EAGER vs fetch join | mapping requirement vs query-specific strategy |
| Page vs Slice | total count vs next-chunk detection |
| entity vs projection | managed behavior vs read model |
