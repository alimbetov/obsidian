---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain:
  - spring-data-jpa
  - jpa
batch_id: DATA-B01
status: published
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
> 36 карточек по persistence context, entity lifecycle, dirty checking, flush, locking, repository proxies, queries, Specifications, projections, pagination, N+1 и fetch plans.

---

## DATA-B01-C001 — What is a JPA persistence context?

### Russian Translation

Что такое persistence context в JPA?

> [!answer]- Answer
> A persistence context is the set of entity instances managed by an `EntityManager`, with one canonical managed instance for each persistent identity.

### Explanation

It acts as an identity map and unit of work. Managed changes are tracked and synchronized with the database during flush.

### Exam Trap

A persistence context is not the same as Redis, a second-level cache or the database transaction itself.

### Memory Hook

**Identity map + unit of work.**

---

## DATA-B01-C002 — What are the main JPA entity lifecycle states?

### Russian Translation

Каковы основные состояния JPA entity?

> [!answer]- Answer
> New, managed, detached and removed.

### Explanation

New has not been associated with a persistence context; managed is tracked; detached has identity but is no longer tracked; removed is scheduled for deletion.

### Exam Trap

Having a non-null ID does not automatically make an object managed.

---

## DATA-B01-C003 — What happens to a new entity passed to `persist()`?

### Russian Translation

Что происходит с новой entity при вызове `persist()`?

> [!answer]- Answer
> The same object instance becomes managed and is scheduled for insertion at or before flush/commit.

### Exam Trap

`persist()` does not always execute INSERT immediately.

---

## DATA-B01-C004 — Does `merge()` make the argument instance managed?

### Russian Translation

Делает ли `merge()` переданный object managed?

> [!answer]- Answer
> No. It copies the argument state into a managed instance and returns that managed copy; the original object remains detached.

### Mini Example

```java
Customer managed = entityManager.merge(detached);
managed != detached;
```

### Memory Hook

**Persist manages original; merge returns copy.**

---

## DATA-B01-C005 — What is dirty checking?

### Russian Translation

Что такое dirty checking?

> [!answer]- Answer
> It is the detection of changes to managed entity state so the provider can generate SQL updates during flush.

### Production Transfer

A managed entity normally does not need an explicit repository `save()` after each setter call.

---

## DATA-B01-C006 — What is the difference between flush and commit?

### Russian Translation

Чем flush отличается от commit?

> [!answer]- Answer
> Flush synchronizes persistence-context changes to SQL inside the current transaction; commit finalizes the transaction.

### Exam Trap

A flushed INSERT can still be rolled back.

### Memory Hook

**Flush sends SQL; commit finalizes TX.**

---

## DATA-B01-C007 — Can AUTO flush happen before transaction commit?

### Russian Translation

Может ли AUTO flush произойти до commit?

> [!answer]- Answer
> Yes. A provider may flush before a query whose result must reflect pending entity changes, as well as before commit.

### Exam Trap

Do not assume that no SQL executes until the annotated method returns.

---

## DATA-B01-C008 — What does `EntityManager.clear()` do?

### Russian Translation

Что делает `EntityManager.clear()`?

> [!answer]- Answer
> It detaches all managed entities from the persistence context.

### Production Transfer

In batch processing, use `flush()` before `clear()` if queued changes must be preserved.

---

## DATA-B01-C009 — What does `refresh(entity)` do?

### Russian Translation

Что делает `refresh(entity)`?

> [!answer]- Answer
> It reloads a managed entity from the database and overwrites its current in-memory state.

### Exam Trap

Calling refresh can discard unflushed local changes.

---

## DATA-B01-C010 — What does a JPA cascade control?

### Russian Translation

Что контролирует JPA cascade?

> [!answer]- Answer
> It controls whether an EntityManager lifecycle operation such as persist, merge or remove is propagated across an association.

### Exam Trap

JPA cascade is not the same mechanism as database `ON DELETE CASCADE`.

---

## DATA-B01-C011 — How do `orphanRemoval` and `CascadeType.REMOVE` differ?

### Russian Translation

Чем отличаются `orphanRemoval` и `CascadeType.REMOVE`?

> [!answer]- Answer
> `orphanRemoval` deletes a child removed from its owning parent relationship; cascade REMOVE propagates deletion when the parent itself is removed.

### Memory Hook

**Unlink child vs delete parent.**

---

## DATA-B01-C012 — Which side owns a bidirectional association?

### Russian Translation

Какая сторона владеет bidirectional association?

> [!answer]- Answer
> The owning side is the side that defines the foreign-key mapping, typically the side with `@JoinColumn`; `mappedBy` identifies the inverse side.

### Production Transfer

Helper methods should update both Java sides of the relationship.

---

## DATA-B01-C013 — Does `FetchType.EAGER` guarantee one SQL query?

### Russian Translation

Гарантирует ли `FetchType.EAGER` один SQL query?

> [!answer]- Answer
> No. The provider may use joins or additional selects, and EAGER can still produce N+1 behavior.

### Exam Trap

EAGER is a fetch requirement, not a specific SQL strategy.

---

## DATA-B01-C014 — What commonly causes `LazyInitializationException`?

### Russian Translation

Что обычно вызывает `LazyInitializationException`?

> [!answer]- Answer
> Accessing an uninitialized lazy association after the owning persistence context has closed.

### Best Fix

Fetch and map the required data inside the application transaction using a projection, fetch join or entity graph.

---

## DATA-B01-C015 — What trade-off does Open Session in View introduce?

### Russian Translation

Какой trade-off создаёт Open Session in View?

> [!answer]- Answer
> It permits lazy loading later in the web request but can move SQL outside explicit service transaction boundaries and hide N+1 queries.

### Exam Trap

OSIV is not a substitute for an intentional query and DTO boundary.

---

## DATA-B01-C016 — How does `@Version` support optimistic locking?

### Russian Translation

Как `@Version` поддерживает optimistic locking?

> [!answer]- Answer
> Updates include the previously read version in the WHERE clause and increment it; zero updated rows indicates a stale writer.

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

### Production Transfer

Keep locked transactions short and acquire rows in a stable order.

---

## DATA-B01-C018 — Why is entity `hashCode()` based only on a generated ID risky?

### Russian Translation

Почему `hashCode()` entity только по generated ID опасен?

> [!answer]- Answer
> The ID can change from null to generated after insertion, changing the hash code while the object is already stored in a hash-based collection.

### Exam Trap

Do not include mutable collections and associations in entity equality.

---

## DATA-B01-C019 — What object usually backs a Spring Data JPA repository interface?

### Russian Translation

Какой object обычно стоит за Spring Data JPA repository interface?

> [!answer]- Answer
> A Spring-created repository proxy that routes methods to base CRUD implementation, query execution infrastructure or custom fragments.

### Memory Hook

**Repository interface → runtime proxy.**

---

## DATA-B01-C020 — What implementation commonly handles inherited JPA CRUD methods?

### Russian Translation

Какая implementation обычно обрабатывает inherited JPA CRUD methods?

> [!answer]- Answer
> `SimpleJpaRepository`.

### Explanation

Its inherited read methods normally use read-only transaction configuration, while write methods use ordinary transactional defaults.

---

## DATA-B01-C021 — Should application transaction boundaries normally be defined only by repository defaults?

### Russian Translation

Следует ли полагаться только на transaction defaults repository methods?

> [!answer]- Answer
> Usually no. A service/facade should define the unit-of-work boundary that coordinates repositories, mapping and business rules.

### Production Transfer

One application use case often spans several repository calls.

---

## DATA-B01-C022 — When is query derivation a good choice?

### Russian Translation

Когда query derivation является хорошим выбором?

> [!answer]- Answer
> For short, stable and readable predicates that can be clearly expressed in a repository method name.

### Exam Trap

A very long derived method name is not more maintainable than an explicit query or Specification.

---

## DATA-B01-C023 — What is the difference between JPQL and native SQL in `@Query`?

### Russian Translation

Чем JPQL отличается от native SQL в `@Query`?

> [!answer]- Answer
> JPQL queries the entity model and is translated by the provider; native SQL targets database tables and vendor SQL directly.

### Production Transfer

Native SQL gives more control but requires explicit portability, mapping and count-query decisions.

---

## DATA-B01-C024 — Why is `@Modifying` required for update/delete `@Query` methods?

### Russian Translation

Почему `@Modifying` нужен для update/delete `@Query` methods?

> [!answer]- Answer
> It tells Spring Data that the declared query performs DML rather than returning a result set.

### Exam Trap

`@Modifying` does not automatically add a transaction to every declared method.

---

## DATA-B01-C025 — Why can bulk JPQL update leave stale entities?

### Russian Translation

Почему bulk JPQL update может оставить stale entities?

> [!answer]- Answer
> Bulk DML updates database rows directly and does not synchronize every managed entity already present in the persistence context.

### Best Fix

Use controlled flush/clear/refresh or `@Modifying(clearAutomatically = true)` according to the use case.

---

## DATA-B01-C026 — What is a Spring Data JPA `Specification`?

### Russian Translation

Что такое Spring Data JPA `Specification`?

> [!answer]- Answer
> A composable predicate over an entity expressed through the JPA Criteria API and executed through `JpaSpecificationExecutor`.

### Production Transfer

Specifications are useful for optional filters but do not guarantee an efficient execution plan.

---

## DATA-B01-C027 — What problem do projections solve?

### Russian Translation

Какую проблему решают projections?

> [!answer]- Answer
> They return a dedicated read model containing selected attributes instead of materializing and exposing an entire entity aggregate.

### Exam Trap

Nested projection properties can still require joins and materialize more data than expected.

---

## DATA-B01-C028 — How do `Page` and `Slice` differ?

### Russian Translation

Чем отличаются `Page` и `Slice`?

> [!answer]- Answer
> `Page` normally includes total-count information and therefore often runs a count query; `Slice` only determines whether another chunk exists.

### Production Transfer

Use Slice when total pages are not a business requirement and count is expensive.

---

## DATA-B01-C029 — Why should pagination sorting include a unique tie-breaker?

### Russian Translation

Почему pagination sort должен включать unique tie-breaker?

> [!answer]- Answer
> Rows with equal primary sort values otherwise have no deterministic order and can move between pages.

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

### Production Transfer

Prove N+1 with query counts or statistics, not by inspecting annotations alone.

---

## DATA-B01-C031 — What does a fetch join do?

### Russian Translation

Что делает fetch join?

> [!answer]- Answer
> It instructs a query to fetch a selected association as part of that query result, usually reducing later lazy selects.

### Exam Trap

Fetching multiple collections can create a cartesian product and duplicate root rows.

---

## DATA-B01-C032 — What does Spring Data `@EntityGraph` configure?

### Russian Translation

Что конфигурирует Spring Data `@EntityGraph`?

> [!answer]- Answer
> It applies a named or ad-hoc JPA entity graph as a fetch plan to a repository query method.

### Production Transfer

It separates the predicate from the association fetch plan, but the provider still chooses the SQL strategy.

---

## DATA-B01-C033 — Why is a collection fetch join problematic with `Page`?

### Russian Translation

Почему collection fetch join проблематичен вместе с `Page`?

> [!answer]- Answer
> SQL rows represent parent-child combinations while pagination must count and limit parent entities, which can cause incorrect or in-memory pagination and expensive count queries.

### Best Fix

Page root IDs first, then fetch the graph by those IDs, or use a projection.

---

## DATA-B01-C034 — What does `getReferenceById()` commonly return?

### Russian Translation

Что обычно возвращает `getReferenceById()`?

> [!answer]- Answer
> A lazy entity reference/proxy that may avoid an immediate SELECT until state is accessed.

### Exam Trap

A reference is not proof that the row exists; access can fail later.

---

## DATA-B01-C035 — What is the difference between JPA auditing and a domain audit log?

### Russian Translation

Чем JPA auditing отличается от domain audit log?

> [!answer]- Answer
> JPA auditing records technical creator/modifier timestamps and actors, while a domain audit log records business events and decisions.

### Exam Trap

`@LastModifiedDate` is not an immutable history of all changes.

---

## DATA-B01-C036 — When should a query return an entity instead of a projection?

### Russian Translation

Когда query должен возвращать entity, а не projection?

> [!answer]- Answer
> When the use case needs a managed aggregate for mutation, invariants and dirty checking; read-only endpoints often benefit from projections or DTOs.

### Memory Hook

**Entity for behavior; projection for view.**

## Related materials

- [[10_CONCEPTS/Spring/Data/Spring Data JPA Persistence Context and Entity Lifecycle]]
- [[10_CONCEPTS/Spring/Data/Spring Data Repositories Queries and Fetching]]
- [[40_PRODUCTION_CASES/Spring/Spring Data JPA Production Cases]]
- [[50_LABS/Spring/DATA-B01/README]]
