---
type: concept
domain: spring
subdomain:
  - spring-data-jpa
  - repositories
  - querying
difficulty: advanced
status: learning
confidence: 0
interview: true
certification:
  - spring-2V0-72.22
spring_versions:
  - 5.3.39
spring_data_jpa_versions:
  - 2.7.18
hibernate_versions:
  - 5.6.15.Final
java_versions:
  - 8
  - 21
production_relevance: critical
prerequisites:
  - "[[Spring Data JPA Persistence Context and Entity Lifecycle]]"
  - "[[Spring Transaction Management Deep Dive]]"
related:
  - "[[Spring Cache with Caffeine and Redis]]"
  - "[[Transactional Outbox and Commit Boundaries]]"
tags:
  - spring-data
  - jpa
  - repository
  - specification
  - projection
  - pagination
  - n-plus-one
  - entity-graph
---

# Spring Data Repositories, Queries and Fetching

> [!summary] За 30 секунд
> Spring Data JPA создаёт proxy для repository interface и делегирует стандартные CRUD operations базовой реализации `SimpleJpaRepository`, а query methods строит из имени, `@Query`, named query, Specification или custom fragment. Repository не заменяет JPA: persistence context, dirty checking, flush и transaction semantics остаются JPA/Hibernate. Главная production-задача — выбрать правильную форму query и fetch plan: entity, projection, fetch join, `@EntityGraph`, page/slice или Specification.

# 1. Repository proxy — что реально создаёт Spring

```java
public interface CustomerRepository
        extends JpaRepository<Customer, Long>,
                JpaSpecificationExecutor<Customer> {
}
```

Spring не генерирует source code implementation. Во время startup infrastructure строит proxy:

```text
Caller
  ↓
Repository proxy
  ↓
Repository method metadata
  ├── base CRUD method → SimpleJpaRepository
  ├── derived query → query parser/executor
  ├── @Query → declared query executor
  ├── Specification → Criteria API
  └── custom fragment → user implementation
  ↓
EntityManager
  ↓
Persistence Context / Database
```

## Что даёт repository abstraction

- меньше boilerplate;
- единый CRUD contract;
- query derivation;
- pagination/sorting;
- projections;
- specifications;
- locking metadata;
- auditing integration;
- custom fragments.

## Чего repository не отменяет

- entity lifecycle;
- dirty checking;
- transaction boundaries;
- flush timing;
- lazy loading;
- SQL plans;
- indexes;
- N+1;
- locking/deadlocks;
- database constraints.

> Repository упрощает entry point, но не делает data access бесплатным или магическим.

---

# 2. Иерархия repository interfaces

Упрощённо:

```text
Repository<T, ID>
    ↓
CrudRepository<T, ID>
    ↓
PagingAndSortingRepository<T, ID>
    ↓
JpaRepository<T, ID>
```

`JpaRepository` добавляет JPA-specific operations, например:

- `flush()`;
- `saveAndFlush()`;
- batch delete methods;
- reference access (`getReferenceById` в новых APIs / `getOne` в старых versions).

Не расширяй `JpaRepository` автоматически, если module хочет минимальный contract. Можно определить собственный base repository:

```java
@NoRepositoryBean
public interface DomainRepository<T, ID>
        extends Repository<T, ID> {

    Optional<T> findById(ID id);

    <S extends T> S save(S entity);
}
```

`@NoRepositoryBean` запрещает Spring создавать instance для промежуточного generic interface.

---

# 3. Transaction semantics repository methods

Inherited CRUD methods получают transaction metadata от `SimpleJpaRepository`:

```text
read methods   → readOnly transaction
write methods  → ordinary @Transactional
```

Но declared query methods сами по себе не обязаны получать transaction annotation.

```java
interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByStatus(Status status);
}
```

Рекомендуемая архитектура:

```java
@Service
class CustomerQueryService {

    @Transactional(readOnly = true)
    public CustomerDetailsDto details(Long id) {
        ...
    }
}
```

Service-level boundary охватывает:

- несколько repositories;
- lazy/fetch plan;
- mapping;
- business checks;
- consistency unit.

> Repository transaction — low-level default. Application service transaction — unit-of-work contract.

---

# 4. Query derivation

Spring Data парсит method name:

```java
List<Customer> findByStatusAndCreatedAtAfterOrderByCreatedAtDesc(
        Status status,
        Instant createdAfter
);
```

Conceptually:

```text
find By status
     And createdAt After
     Order By createdAt Desc
```

## Когда query derivation хороша

- query короткая;
- intent читается;
- 1–3 predicates;
- нет сложных joins/subqueries;
- naming остаётся стабильным.

## Когда method name становится запахом

```java
findByStatusAndTypeAndRegionCodeAndCreatedAtBetweenAndNameContainingIgnoreCase...
```

Проблемы:

- трудно читать;
- трудно переиспользовать predicates;
- optional filters создают explosion методов;
- rename property ломает API;
- query plan неочевиден.

Для dynamic query используй Specification, Criteria, Querydsl или custom repository implementation.

---

# 5. Reserved keywords и property traversal

```java
List<Customer> findByAddressCityName(String cityName);
```

Spring Data строит property path:

```text
Customer.address.city.name
```

Если имена неоднозначны, underscore может явно разделить traversal:

```java
findByAddress_City_Name(...)
```

Риск:

- deep traversal создаёт joins;
- association fetch не определяется автоматически;
- query может быть корректна функционально, но дорога.

Derived name отвечает на вопрос **where**, но не всегда отвечает **как загружать graph**.

---

# 6. `@Query`: JPQL против native SQL

## JPQL

```java
@Query("""
    select c
    from Customer c
    where c.status = :status
      and lower(c.name) like lower(concat('%', :name, '%'))
    order by c.createdAt desc
""")
List<Customer> search(
        @Param("status") Status status,
        @Param("name") String name
);
```

Для Java 8 lab используй обычную string concatenation, но conceptually query остаётся JPQL.

JPQL работает с:

- entity names;
- entity fields;
- associations;
- provider-generated SQL.

## Native query

```java
@Query(
    value = "select * from customer where metadata @> cast(:json as jsonb)",
    nativeQuery = true
)
List<Customer> findByJson(@Param("json") String json);
```

Использовать, когда нужны:

- database-specific functions;
- CTE/window functions;
- PostgreSQL JSONB/arrays;
- optimizer hints;
- exact SQL control.

Trade-off:

- меньше portability;
- count query для pagination нужно задавать отдельно;
- entity/projection mapping сложнее;
- schema changes теснее связаны с code.

---

# 7. Parameter binding

Предпочтительно named parameters:

```java
@Query("select c from Customer c where c.email = :email")
Optional<Customer> findExact(@Param("email") String email);
```

В новых compiler configurations parameter names могут быть доступны через `-parameters`, но explicit `@Param` полезен для version-stable readability.

Не конкатенируй user input в JPQL/native SQL:

```java
// dangerous
String sql = "select ... where name = '" + name + "'";
```

Используй bind parameters для correctness, security и plan reuse.

---

# 8. `@Modifying`

Spring Data должен знать, что `@Query` выполняет update/delete:

```java
@Modifying
@Transactional
@Query("update Customer c set c.status = :status where c.id = :id")
int updateStatus(@Param("id") Long id,
                 @Param("status") Status status);
```

Без `@Modifying` infrastructure будет трактовать query как select.

## Return value

Часто полезно возвращать affected row count:

```java
int updated = repository.updateStatus(id, ACTIVE);
if (updated != 1) {
    throw new ConcurrentModificationException(...);
}
```

## Persistence context trap

Bulk update обходит managed entities:

```java
Customer managed = repository.findById(id).orElseThrow(...);
repository.updateStatus(id, INACTIVE);

managed.getStatus(); // may still be ACTIVE
```

Options:

```java
@Modifying(
    flushAutomatically = true,
    clearAutomatically = true
)
```

или explicit `flush`, `clear`, `refresh`.

---

# 9. Specifications — dynamic query without method explosion

Repository:

```java
public interface CustomerRepository
        extends JpaRepository<Customer, Long>,
                JpaSpecificationExecutor<Customer> {
}
```

Specification:

```java
public static Specification<Customer> hasStatus(Status status) {
    return (root, query, cb) ->
            status == null
                    ? cb.conjunction()
                    : cb.equal(root.get("status"), status);
}
```

```java
public static Specification<Customer> nameContains(String text) {
    return (root, query, cb) -> {
        if (text == null || text.trim().isEmpty()) {
            return cb.conjunction();
        }
        return cb.like(
                cb.lower(root.get("name")),
                "%" + text.toLowerCase() + "%"
        );
    };
}
```

Composition:

```java
Specification<Customer> spec = Specification
        .where(hasStatus(filter.getStatus()))
        .and(nameContains(filter.getName()))
        .and(createdAfter(filter.getCreatedAfter()));

Page<Customer> page = repository.findAll(spec, pageable);
```

## Почему Specification полезна

- optional filters;
- reusable predicates;
- composition `and/or/not`;
- type-safe metamodel возможен;
- pagination;
- separation filter semantics from controller.

## Чего Specification не гарантирует

- хороший index usage;
- оптимальный join plan;
- отсутствие duplicate rows;
- правильный fetch graph;
- дешёвый count query.

Dynamic query всё равно нужно проверять через SQL и execution plan.

---

# 10. Specification join и duplicate rows

```java
public static Specification<Customer> hasOrderStatus(OrderStatus status) {
    return (root, query, cb) -> {
        Join<Customer, Order> orders = root.join("orders");
        query.distinct(true);
        return cb.equal(orders.get("status"), status);
    };
}
```

Без `distinct(true)` один Customer может появиться несколько раз — по одной строке на matching Order.

Но SQL `DISTINCT` может быть дорогим. Иногда лучше:

- `exists` subquery;
- отдельный ID query;
- projection;
- two-step fetch;
- database-specific optimization.

---

# 11. Query by Example

QBE подходит для простого probe-based matching:

```java
Customer probe = new Customer();
probe.setStatus(ACTIVE);
probe.setRegion("ALMATY");

ExampleMatcher matcher = ExampleMatcher.matchingAll()
        .withIgnoreNullValues()
        .withIgnoreCase("region");

repository.findAll(Example.of(probe, matcher));
```

Хорошо:

- simple equality/string matching;
- admin filter prototypes;
- минимальный boilerplate.

Слабо:

- ranges;
- grouped OR logic;
- joins;
- subqueries;
- complex business predicates.

Не используй QBE как универсальную замену Specification.

---

# 12. Projections

## Interface projection

```java
public interface CustomerSummary {
    Long getId();
    String getName();
    Status getStatus();
}
```

```java
List<CustomerSummary> findByStatus(Status status);
```

Spring создаёт projection proxy.

Плюсы:

- меньше columns;
- удобный read model;
- no entity mutation;
- API intent виден в return type.

## DTO projection

```java
public final class CustomerSummaryDto {
    private final Long id;
    private final String name;

    public CustomerSummaryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
```

JPQL:

```java
@Query("select new com.example.CustomerSummaryDto(c.id, c.name) "
     + "from Customer c where c.status = :status")
List<CustomerSummaryDto> summaries(Status status);
```

## Closed projection

Все getters соответствуют entity properties. Spring Data может оптимизировать selected columns.

## Open projection

```java
public interface CustomerDisplay {
    String getName();

    @Value("#{target.name + ' [' + target.status + ']'}")
    String getDisplayName();
}
```

Open projection через SpEL может потребовать полный entity state и хуже оптимизироваться.

> Для критичных queries предпочитай явный DTO contract и проверенный SQL.

---

# 13. Dynamic projections

```java
<T> List<T> findByStatus(Status status, Class<T> type);
```

Usage:

```java
repository.findByStatus(ACTIVE, CustomerSummary.class);
repository.findByStatus(ACTIVE, Customer.class);
```

Полезно, когда один predicate нужен для разных read models.

Риск: слишком polymorphic repository API усложняет понимание SQL и тестирование.

---

# 14. Pagination: Page, Slice и List

## `Page<T>`

```java
Page<Customer> page = repository.findAll(pageable);
```

Обычно выполняет:

```text
content query
    +
count query
```

Page знает:

- total elements;
- total pages;
- current page.

## `Slice<T>`

```java
Slice<Customer> slice = repository.findByStatus(status, pageable);
```

Знает только, есть ли следующий slice. Обычно избегает полного count.

## Когда count дорог

- сложные joins;
- billions of rows;
- dynamic filters;
- distributed/partitioned data;
- high-traffic scrolling.

Используй Slice или keyset pagination, если total count не нужен бизнесу.

---

# 15. Offset pagination и keyset pagination

## Offset

```sql
select ...
order by created_at desc
limit 50 offset 500000
```

Проблемы:

- DB должна пропустить много rows;
- concurrent inserts создают duplicates/skips;
- cost растёт с глубиной page.

## Keyset / seek

```sql
select ...
where (created_at, id) < (:lastCreatedAt, :lastId)
order by created_at desc, id desc
limit 50
```

Плюсы:

- stable continuation;
- index-friendly;
- cost не растёт линейно с page number.

Требования:

- deterministic unique sort;
- cursor values;
- нельзя легко прыгнуть на arbitrary page 10000.

В Spring Data 2.7 основная portable модель — custom query/Specification; новые Scroll APIs появились позднее, поэтому version-sensitive behavior нужно отмечать отдельно.

---

# 16. Stable sorting

Плохо:

```java
PageRequest.of(page, size, Sort.by("createdAt").descending())
```

Если многие rows имеют одинаковый `createdAt`, порядок нестабилен.

Лучше:

```java
Sort sort = Sort.by(
        Sort.Order.desc("createdAt"),
        Sort.Order.desc("id")
);
```

Unique tie-breaker предотвращает jumping rows между pages.

---

# 17. N+1 problem

Entity:

```java
@Entity
class Customer {
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Order> orders;
}
```

Query:

```java
List<Customer> customers = repository.findAll();

for (Customer customer : customers) {
    System.out.println(customer.getOrders().size());
}
```

SQL:

```text
1 query for customers
N queries for orders
--------------------
N + 1 queries
```

## Почему functional tests могут не заметить

- test dataset маленький;
- first-level cache скрывает повторный load;
- logs выключены;
- latency local H2 низкая;
- production network DB latency выше;
- serialization accesses associations later.

## Как доказать

- Hibernate statistics;
- datasource proxy / p6spy;
- SQL count assertions;
- APM spans;
- repository integration test with realistic rows.

---

# 18. Fetch join

```java
@Query("select distinct c from Customer c "
     + "left join fetch c.orders "
     + "where c.id = :id")
Optional<Customer> findDetailedById(Long id);
```

Один query загружает Customer + orders.

## `distinct`

SQL join возвращает несколько rows для одного Customer. JPQL `distinct` помогает deduplicate root entities.

## Ограничения

- multiple collections create cartesian product;
- collection fetch join + pagination problematic;
- large collection duplicates root columns;
- fetch join меняет query shape;
- count query нельзя механически копировать.

---

# 19. `@EntityGraph`

```java
@EntityGraph(attributePaths = {"orders"})
@Query("select c from Customer c where c.id = :id")
Optional<Customer> findGraphById(@Param("id") Long id);
```

Преимущество: fetch plan отделён от JPQL predicate.

## FETCH graph

Attributes в graph рассматриваются как eager для query, неуказанные могут трактоваться lazy согласно graph semantics.

## LOAD graph

Attributes в graph eager, остальные следуют mapping fetch type.

Use cases:

- несколько fetch plans для одного predicate;
- repository readability;
- ad-hoc nested paths;
- замена global EAGER.

Ограничения:

- provider всё равно выбирает SQL strategy;
- deep graph может быть дорогим;
- pagination with collections остаётся сложной;
- graph не решает плохую domain model.

---

# 20. Fetch join + pagination trap

```java
@Query("select c from Customer c left join fetch c.orders")
Page<Customer> findAllDetailed(Pageable pageable);
```

Проблема: SQL rows соответствуют `(customer, order)`, а page должен считаться по customers.

Provider может:

- применить pagination in memory;
- вернуть неполные collections;
- выдать warning;
- сгенерировать дорогой query.

Production pattern:

```text
1. page root IDs
2. fetch graph by IDs
3. preserve requested order
```

или:

- projection query;
- batch fetching;
- separate collection endpoint;
- aggregate summary instead of full collection.

---

# 21. Batch fetching

Hibernate-specific optimization:

```java
@BatchSize(size = 50)
@OneToMany(mappedBy = "customer")
private List<Order> orders;
```

Вместо N individual selects provider может загрузить associations группами:

```text
where customer_id in (?, ?, ..., ?)
```

Это уменьшает N+1, но:

- остаётся provider-specific;
- не всегда лучше fetch join;
- требует измерения;
- не определяет API read model.

---

# 22. `getReferenceById` и lazy reference

```java
Customer reference = repository.getReferenceById(customerId);
order.setCustomer(reference);
```

Provider может создать proxy/reference без immediate SELECT.

Хорошо, когда:

- нужно установить FK;
- existence проверяется constraint/transaction;
- state customer не используется.

Риск:

```java
reference.getName(); // may trigger SELECT / EntityNotFoundException
```

Не путай reference с guaranteed existing fully loaded entity.

---

# 23. Locking repository methods

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select c from Customer c where c.id = :id")
Optional<Customer> findLockedById(@Param("id") Long id);
```

Lock metadata применяется к query execution.

Важно:

- должна существовать transaction;
- database/provider определяет actual SQL;
- timeout hints могут быть non-portable;
- lock order должен быть stable;
- lock держится до transaction completion.

Optimistic locking обычно реализуется `@Version`, а не `@Lock` на каждом query.

---

# 24. Auditing

```java
@EnableJpaAuditing
```

Entity:

```java
@CreatedDate
private Instant createdAt;

@LastModifiedDate
private Instant modifiedAt;

@CreatedBy
private String createdBy;
```

Нужны:

- auditing entity listener;
- `AuditorAware<T>` для current actor;
- clock strategy;
- security/background-job semantics.

Auditing отвечает на «кто/когда изменил row», но не заменяет domain audit log или immutable event history.

---

# 25. Custom repository fragments

Interface:

```java
public interface CustomerSearchRepository {
    List<CustomerSearchRow> search(CustomerFilter filter);
}
```

Implementation:

```java
public class CustomerSearchRepositoryImpl
        implements CustomerSearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CustomerSearchRow> search(CustomerFilter filter) {
        // Criteria, Querydsl or optimized native SQL
    }
}
```

Aggregate repository:

```java
public interface CustomerRepository
        extends JpaRepository<Customer, Long>,
                JpaSpecificationExecutor<Customer>,
                CustomerSearchRepository {
}
```

Use fragment when:

- Specification становится неудобной;
- нужен complex DTO query;
- custom batching;
- native SQL;
- query plan требует explicit control.

---

# 26. Count query optimization

```java
@Query(
    value = "select c from Customer c join c.orders o where o.status = :status",
    countQuery = "select count(distinct c.id) from Customer c join c.orders o where o.status = :status"
)
Page<Customer> findByOrderStatus(OrderStatus status, Pageable pageable);
```

Без explicit count query Spring Data пытается derive one, но сложные queries могут:

- считать лишние joins;
- неправильно учитывать distinct/group by;
- быть очень дорогими;
- не парситься.

Проверяй content query и count query отдельно.

---

# 27. Native projection and mapping

Для simple scalar projection:

```java
public interface RegionCount {
    String getRegion();
    long getTotal();
}
```

```java
@Query(
    value = "select region as region, count(*) as total "
          + "from customer group by region",
    nativeQuery = true
)
List<RegionCount> countByRegion();
```

Aliases должны совпадать с projection property names.

Для сложных mappings:

- `@SqlResultSetMapping`;
- constructor mapping;
- custom repository + JDBC;
- jOOQ/Querydsl depending architecture.

Не заставляй JPA entity mapping обслуживать analytics query, если result — не aggregate.

---

# 28. Repository exception translation

Spring data-access infrastructure преобразует provider/database exceptions в hierarchy `DataAccessException`.

Преимущества:

- единый unchecked exception model;
- меньше зависимости от Hibernate exceptions;
- consistent transaction rollback;
- easier retry classification.

Но translation не отменяет знание root cause:

```text
DataIntegrityViolationException
    may wrap
ConstraintViolationException
    may wrap
SQLState 23505 unique violation
```

Для production diagnostics логируй SQLState/constraint name, но не раскрывай sensitive SQL клиенту.

---

# 29. Repository return types

Хорошие contracts:

```java
Optional<Customer> findByEmail(String email);
List<Customer> findByStatus(Status status);
Page<Customer> findByRegion(String region, Pageable pageable);
Slice<Customer> findByStatus(Status status, Pageable pageable);
Stream<Customer> streamByStatus(Status status);
```

## Stream

Repository stream обычно требует открытой transaction/resource:

```java
@Transactional(readOnly = true)
public void export() {
    try (Stream<Customer> stream = repository.streamByStatus(ACTIVE)) {
        stream.forEach(exporter::write);
    }
}
```

Нужно закрывать stream, чтобы освободить DB resources.

---

# 30. `Optional` semantics

`Optional<Customer>` означает «0 или 1 row».

Если query возвращает несколько rows, это data-integrity/query-contract error, а не повод выбрать первый.

```java
Optional<Customer> findByEmail(String email);
```

Database должен поддержать contract unique constraint:

```sql
unique(email)
```

> Repository cardinality должна поддерживаться schema constraint, когда это business invariant.

---

# 31. Entity vs projection decision

Используй entity, когда:

- выполняется aggregate mutation;
- нужны invariants и domain behavior;
- persistence context tracking полезен;
- graph ограничен.

Используй projection/DTO, когда:

- read-only endpoint;
- нужен subset columns;
- joins across aggregates;
- analytics/reporting;
- pagination over flat rows;
- entity graph слишком большой;
- JSON shape отличается от domain model.

Decision:

```text
Need to mutate aggregate?
    ├─ yes → managed entity + transaction
    └─ no  → can projection express read model?
                ├─ yes → projection/DTO query
                └─ no  → entity with explicit fetch plan
```

---

# 32. Common production anti-patterns

## 1. Repository directly from controller

Transaction/fetch/mapping boundary becomes accidental.

## 2. `findAll()` on unbounded table

Memory and DB failure.

## 3. Global EAGER to fix LazyInitializationException

Creates over-fetching and N+1 variants.

## 4. `save()` after every setter

Misunderstands unit of work.

## 5. Page + collection fetch join

Incorrect/expensive pagination.

## 6. Dynamic query method explosion

Use Specification/custom query.

## 7. Entity returned as API DTO

Leaks lazy graph and schema.

## 8. Bulk update while managed entities remain stale

Clear/refresh explicitly.

## 9. Optional filters expressed as `(:param is null or column=:param)` everywhere

May degrade plans; compare generated SQL and indexes.

## 10. Count query ignored

Endpoint content query fast, count query 20 seconds.

---

# 33. Diagnostic workflow

Когда endpoint медленный:

1. Какой repository method вызван?
2. Query derived, JPQL, Specification или native?
3. Какой SQL сгенерирован?
4. Сколько SQL statements?
5. Есть N+1?
6. Какой fetch plan?
7. Есть pagination? Какой count query?
8. Sort deterministic и indexed?
9. Projection выбирает нужные columns?
10. Specification создаёт unnecessary joins/distinct?
11. Persistence context слишком большой?
12. Bulk query оставляет stale managed state?
13. DB execution plan использует index?
14. Lock mode блокирует rows?
15. OSIV выполняет SQL после service?
16. Serialization инициирует lazy loads?

---

# 34. Senior interview answer

> Spring Data JPA создаёт repository proxy и маршрутизирует methods либо в `SimpleJpaRepository`, либо в derived/declared query executor, Specification или custom fragment. Он уменьшает boilerplate, но не меняет JPA semantics: entities живут в persistence context, managed changes сохраняются dirty checking при flush, а transactions задают unit of work. Для чтения я выбираю query form по use case: короткие стабильные predicates — derived query, optional filters — Specification, сложный read model — projection/custom query, graph loading — fetch join или `@EntityGraph`. N+1 диагностирую по statement count, а не по annotation. Pagination требует stable sort и отдельной оценки count query; collection fetch join с Page обычно заменяю two-step ID paging или projection. Bulk DML обходит managed state, поэтому явно управляю flush/clear. Repository boundary проектирую вместе с schema constraints, indexes, transaction и observability.

# Memory hooks

```text
Repository is a proxy, not a replacement for JPA.
Derived query defines predicate, not fetch plan.
Specification composes WHERE; SQL plan still matters.
Projection is a read model, entity is a managed aggregate.
Page usually means content + count.
Slice avoids total count.
Fetch join fixes a query, EAGER changes every query.
EntityGraph defines per-method fetch plan.
Bulk DML bypasses persistence context.
N+1 is proven by query count.
```

# Related materials

- [[10_CONCEPTS/Spring/Data/Spring Data JPA Persistence Context and Entity Lifecycle]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/DATA-B01/DATA-B01 Cards]]
- [[40_PRODUCTION_CASES/Spring/Spring Data JPA Production Cases]]
- [[50_LABS/Spring/DATA-B01/README]]
- [[98_SOURCES/Spring Data JPA Sources]]
