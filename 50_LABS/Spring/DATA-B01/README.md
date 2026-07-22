---
type: lab
domain: spring
subdomain:
  - spring-data-jpa
  - jpa
status: active
java_version: 8
spring_version: 5.3.39
spring_data_jpa_version: 2.7.18
hibernate_version: 5.6.15.Final
h2_version: 2.1.214
tags:
  - spring
  - jpa
  - hibernate
  - repository
  - lab
---

# Spring DATA-B01 Lab — Persistence Context, Queries and Fetching

## Цель

Увидеть не только repository API, а реальные механизмы под ним:

- Spring Data repository proxy;
- persistence-context identity map;
- managed, detached и merged entity;
- dirty checking без вызова `save()`;
- flush-time validation/constraint failure;
- `LazyInitializationException` после завершения transaction;
- N+1 через Hibernate Statistics;
- fetch join и `@EntityGraph`;
- interface projection;
- динамический `Specification`;
- `Page` против `Slice`;
- pessimistic lock;
- stale persistence context после bulk JPQL update;
- optimistic-lock conflict через два независимых `EntityManager`.

## Версии

```text
Java                8 baseline
Spring Framework    5.3.39
Spring Data JPA     2.7.18
Hibernate ORM       5.6.15.Final
JPA API             2.2 / javax.persistence
H2                   2.1.214
```

Этот lab намеренно использует `javax.persistence`, потому что Spring Framework 5.3 / Spring Data JPA 2.7 / Hibernate 5.6 принадлежат pre-Jakarta namespace generation.

---

# 1. Запуск

```bash
cd 50_LABS/Spring/DATA-B01
mvn clean compile exec:java
```

Требования:

- JDK 8+;
- Maven;
- доступ к Maven Central при первом dependency resolution.

Lab создаёт H2 database в памяти:

```text
jdbc:h2:mem:datajpalab
```

Schema создаётся Hibernate через:

```text
hibernate.hbm2ddl.auto=create-drop
```

Для production schema migration нужен Liquibase/Flyway, а не `create-drop`.

---

# 2. Домен лаборатории

```text
Author 1 ───── * Book
```

`Author` содержит:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
Long id;

@Version
long version;

@OneToMany(
    mappedBy = "author",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.LAZY
)
List<Book> books;
```

`Book` содержит lazy many-to-one:

```java
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "author_id", nullable = false)
Author author;
```

Seed создаёт трёх authors и шесть books.

---

# 3. Repository proxy

Lab выводит runtime class repository и результат:

```java
AopUtils.isAopProxy(repository)
```

Ожидаемая форма:

```text
class        = jdk.proxy...$Proxy...
is AOP proxy = true
```

Repository interface не имеет написанной вручную реализации. Spring Data создаёт proxy, выбирает base implementation и query execution strategy для каждого method.

---

# 4. Persistence-context identity map

В одной transaction выполняются два вызова:

```java
Author first = entityManager.find(Author.class, id);
Author second = entityManager.find(Author.class, id);
```

Ожидаемо:

```text
same Java instance = true
managed            = true
SQL statements     = 1
```

Модель:

```text
first find
    ↓
SELECT + register Author#id

second find
    ↓
identity map hit
    ↓
same managed object
```

Это не обычная optional cache optimization. Identity map обеспечивает одну canonical managed instance для одной entity identity внутри persistence context.

---

# 5. Dirty checking без `save()`

```java
@Transactional
public void rename(Long id) {
    Author author = entityManager.find(Author.class, id);
    author.setName("new-name");
}
```

Lab намеренно не вызывает:

```java
repository.save(author);
```

При flush Hibernate сравнивает managed state с snapshot и генерирует `UPDATE`.

Ожидаемо:

```text
repository.save called = false
database value = ...-DIRTY
```

Главный вывод:

> Изменение managed entity сохраняется dirty checking. `save()` не является кнопкой «разрешить UPDATE» для уже managed object.

---

# 6. Detach и merge

Sequence:

```text
find managed entity
    ↓
detach
    ↓
modify detached object
    ↓
flush: no UPDATE for detached object
    ↓
merge(detached)
    ↓
state copied into managed copy
```

Lab проверяет:

```text
original managed      = false
merge result managed  = true
same object           = false
```

Критическое правило:

```java
Author managed = entityManager.merge(detached);
```

Нужно продолжать работать с `managed`, а не предполагать, что исходный `detached` object снова стал managed.

---

# 7. Что делает repository `save()` с detached entity

Spring Data JPA выбирает между `persist()` и `merge()` по результату определения new/existing state.

Lab передаёт detached entity в:

```java
Author returned = repository.save(detached);
```

Ожидаемо:

```text
returned == argument = false
argument managed     = false
returned managed     = true
```

Это практическая причина не игнорировать return value `save()` для detached/existing entity.

---

# 8. `LazyInitializationException`

Repository возвращает `Author`, после чего repository transaction завершается. Затем main method обращается к lazy collection:

```java
Author detached = repository.findById(id).orElseThrow(...);
detached.getBooks().size();
```

Ожидаемо:

```text
expected = LazyInitializationException
reason   = detached entity has no open persistence context
```

Правильные решения выбираются по use case:

- fetch join;
- `@EntityGraph`;
- DTO projection;
- mapping внутри transaction;
- explicit query boundary.

Плохое универсальное решение:

```text
сделать все associations EAGER
```

Это часто переносит проблему в over-fetching и N+1 на других запросах.

---

# 9. Failure появляется на flush, а не на setter

Lab добавляет `Book` с `null` title в managed aggregate:

```java
author.addBook(new Book(null));
```

Java object уже изменён, но failure появляется при:

```java
entityManager.flush();
```

Ожидаемая форма:

```text
invalid child added in memory
failure surfaced at flush = PropertyValueException
```

Точный exception может зависеть от provider и того, где обнаружено ограничение: в Hibernate metadata validation или database constraint.

Главный вывод:

```text
setter success
≠
flush success
≠
commit success
```

---

# 10. N+1 через реальные statement counters

Hibernate Statistics включены:

```text
hibernate.generate_statistics=true
```

Baseline:

```java
List<Author> authors = repository.findAll();
for (Author author : authors) {
    author.getBooks().size();
}
```

При трёх authors ожидаемая форма:

```text
1 query for authors
3 queries for books
-------------------
4 SQL statements
```

Это N+1:

```text
1 root query + N association queries
```

Lab не полагается только на визуальную оценку log: он выводит `Statistics#getPrepareStatementCount()`.

---

# 11. Fetch join

Repository method:

```java
@Query(
    "select distinct a " +
    "from Author a " +
    "left join fetch a.books " +
    "order by a.id"
)
List<Author> findAllWithBooks();
```

Ожидаемо:

```text
SQL statements = 1
```

`distinct` нужен на уровне root entity result, потому что relational join создаёт строку на сочетание author/book.

## Ограничение

Collection fetch join и pagination плохо сочетаются: database rows относятся к joined result, а page должен относиться к root entities. Для pageable aggregate query обычно нужны:

1. page IDs;
2. отдельный fetch query по IDs;
3. batch fetching;
4. тщательно спроектированная projection.

---

# 12. `@EntityGraph`

Repository method:

```java
@EntityGraph(attributePaths = "books")
List<Author> findAllByOrderByIdAsc();
```

Entity graph отделяет fetch plan от JPQL predicate.

Полезная ментальная модель:

```text
query predicate says WHAT rows
entity graph says WHICH associations
```

Lab снова выводит количество SQL statements после доступа к books.

---

# 13. Interface projection

```java
public interface AuthorSummary {
    Long getId();
    String getName();
}
```

Repository:

```java
List<AuthorSummary>
findByNameContainingIgnoreCaseOrderByIdAsc(String name);
```

Lab показывает runtime projection class и значения.

Projection полезна, когда read use case требует только часть columns и не должен возвращать managed aggregate.

Необходимо помнить:

- interface projection обычно создаётся proxy;
- nested projections могут потребовать joins;
- projection не является универсальным решением сложного reporting SQL;
- entity lifecycle и dirty checking к projection не применяются как к managed entity.

---

# 14. Dynamic query через `Specification`

Lab собирает predicate только для заданных filters:

```java
Specification<Author> spec = Specification.where(null);

if (name != null) {
    spec = spec.and(nameContains(name));
}

if (minId != null) {
    spec = spec.and(idGreaterThanOrEqualTo(minId));
}
```

Это лучше combinatorial explosion:

```text
findByName
findByNameAndStatus
findByNameAndStatusAndRegion
findByStatusAndRegion
...
```

Однако specification должна оставаться composable predicate abstraction, а не превращаться в скрытый монолит, который одновременно управляет joins, projections, grouping, pagination и provider hints.

---

# 15. `Page` против `Slice`

Lab запускает два метода с одинаковым filter/page size:

```java
Page<Author> findPageByNameContainingIgnoreCase(...);
Slice<Author> findSliceByNameContainingIgnoreCase(...);
```

`Page` должен знать:

```text
totalElements
totalPages
```

поэтому часто выполняет дополнительный count query.

`Slice` должен знать только:

```text
есть ли следующая порция
```

и обычно запрашивает `pageSize + 1` rows без total count.

Lab выводит:

```text
page SQL statements
slice SQL statements
```

На больших таблицах count query может стать самым дорогим запросом endpoint.

---

# 16. Pessimistic lock

Repository:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select a from Author a where a.id = :id")
Optional<Author> findLockedById(Long id);
```

Lab выполняет method внутри transaction и выводит:

```text
lock mode = PESSIMISTIC_WRITE
```

Фактический SQL и blocking semantics определяет database dialect. H2 демонстрирует API path, но production lock behavior необходимо проверять на PostgreSQL/Oracle.

---

# 17. Bulk JPQL update и stale managed state

Bulk query:

```java
@Modifying(
    flushAutomatically = true,
    clearAutomatically = false
)
@Query("update Author a set a.name = :name where a.id = :id")
int renameWithoutClearing(Long id, String name);
```

Sequence:

```text
load Author as managed object
    ↓
execute bulk UPDATE directly in DB
    ↓
managed object is not synchronized automatically
```

Ожидаемо:

```text
managed object = old value
database row   = new value
managed is stale = true
```

После:

```java
entityManager.clear();
entityManager.find(...);
```

видно новое значение.

Варианты:

- `clearAutomatically=true`;
- explicit `clear()`;
- `refresh()` выбранных entities;
- выполнять bulk DML в отдельной короткой transaction;
- не смешивать bulk DML и дальнейшую domain logic над уже managed entities.

---

# 18. Optimistic locking

`Author` содержит:

```java
@Version
long version;
```

Lab открывает два независимых `EntityManager`:

```text
TX1 loads version V
TX2 loads version V
TX2 updates and commits version V+1
TX1 tries UPDATE ... WHERE version=V
0 rows updated
TX1 rollback with optimistic-lock failure
```

Это предотвращает silent lost update.

Production code должен определить policy:

- показать conflict пользователю;
- перечитать state и повторить command;
- merge business changes;
- ограниченно retry только безопасные idempotent operations.

---

# 19. Управляемые упражнения

## Exercise A. `persist()` против `merge()`

Реализовать entity с application-assigned ID и `Persistable<ID>`. Сравнить, как Spring Data определяет new state.

## Exercise B. `saveAndFlush()`

Заменить `save()` на `saveAndFlush()` и определить:

- когда SQL выполняется раньше;
- почему transaction всё ещё может откатиться;
- когда early constraint detection действительно полезна.

## Exercise C. `clearAutomatically`

Установить:

```java
@Modifying(clearAutomatically = true)
```

и проверить, что ранее managed entity становится detached.

## Exercise D. Batch processing

Создать 10 000 entities и сравнить:

```java
for (...) {
    persist(...);
}
```

с:

```java
for (...) {
    persist(...);
    if (i % batchSize == 0) {
        flush();
        clear();
    }
}
```

Измерить memory и statement batching.

## Exercise E. PostgreSQL Testcontainers

Перенести locking experiments на PostgreSQL и проверить:

- `PESSIMISTIC_WRITE`;
- lock timeout;
- deadlock;
- `SKIP LOCKED`;
- MVCC visibility;
- real execution plans.

## Exercise F. Pagination under concurrent writes

Сравнить offset pagination и keyset pagination, когда между запросами вставляются новые rows.

## Exercise G. N+1 regression test

Создать integration test, который сбрасывает Hibernate Statistics, вызывает query service и утверждает верхнюю границу prepared statements.

---

# 20. Production checklist

Перед выпуском query/data-access flow ответить:

1. Где находится transaction boundary?
2. Какие entities managed в этот момент?
3. Когда произойдёт flush?
4. Может ли constraint failure появиться только на commit?
5. Какие associations реально нужны use case?
6. Есть ли N+1?
7. Нужны entity, projection или dedicated query DTO?
8. Нужен `Page`, `Slice` или keyset/window?
9. Какая стратегия lost-update protection?
10. Может ли bulk DML оставить stale context?
11. Какова стоимость count query?
12. Как query ведёт себя на production database, а не только H2?

---

# 21. Ограничения лаборатории

- H2 не воспроизводит PostgreSQL/Oracle MVCC и locking полностью.
- Lab не использует Spring Boot auto-configuration: infrastructure видна явно.
- Second-level cache отключён.
- Open EntityManager in View отсутствует намеренно.
- Нет Testcontainers.
- Нет production schema migration.
- Полный Maven runtime должен быть выполнен в окружении с Maven и dependency resolution.

## Related

- [[10_CONCEPTS/Spring/Data/Spring Data JPA Persistence Context and Entity Lifecycle]]
- [[10_CONCEPTS/Spring/Data/Spring Data Repositories Queries and Fetching]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/DATA-B01/DATA-B01 Cards]]
- [[40_PRODUCTION_CASES/Spring/Spring Data JPA Production Cases]]
- [[98_SOURCES/Spring Data JPA Sources]]
