---
type: production-case-set
domain: spring
subdomain:
  - spring-data-jpa
  - jpa
status: active
case_count: 16
tags:
  - spring
  - jpa
  - hibernate
  - production
  - n-plus-one
  - locking
  - pagination
---

# Spring Data JPA Production Cases

> [!summary]
> 16 кейсов построены от наблюдаемого симптома к persistence-context state, SQL evidence, root cause и исправлению.

---

# Case 1. `LazyInitializationException` во время JSON serialization

## Симптом

Controller возвращает `Customer`, а serializer падает на `customer.orders` после выхода из service.

## Timeline

```text
service transaction opens
    ↓
Customer loaded, orders remain lazy
    ↓
service returns entity
    ↓
transaction/persistence context closes
    ↓
Jackson accesses orders
    ↓
LazyInitializationException
```

## Причина

Fetch plan и DTO mapping вынесены за пределы unit of work.

## Исправление

```java
@Transactional(readOnly = true)
public CustomerDetailsDto details(Long id) {
    Customer customer = repository.findGraphById(id)
            .orElseThrow(...);
    return mapper.toDto(customer);
}
```

Допустимы projection, fetch join или `@EntityGraph`.

## Не исправление

- сделать все associations EAGER;
- ловить exception;
- включить OSIV без анализа SQL.

---

# Case 2. После изменения managed entity `save()` не вызывался, но UPDATE произошёл

## Симптом

Разработчик удивлён, что код сохраняет name:

```java
@Transactional
public void rename(Long id) {
    Customer customer = repository.findById(id).orElseThrow(...);
    customer.setName("New");
}
```

## Причина

Entity managed. Dirty checking обнаруживает change при flush.

## Доказательство

```java
entityManager.contains(customer) == true
```

и SQL log показывает UPDATE при flush/commit.

## Lesson

`save()` не является обязательным «подтверждением setter» для managed entity.

---

# Case 3. `merge()` выполнен, но последующее изменение потерялось

## Код

```java
Customer managed = entityManager.merge(detached);
detached.setName("Second change");
```

## Симптом

В DB сохранилось state на момент merge, но не `Second change`.

## Причина

Исходный `detached` object не стал managed. JPA отслеживает returned `managed` copy.

## Исправление

```java
Customer managed = entityManager.merge(detached);
managed.setName("Second change");
```

---

# Case 4. Unique constraint exception приходит после выхода из service body

## Симптом

Внутри method все строки выполнились, а exception появляется на transaction commit.

## Причина

INSERT был queued в persistence context и выполняется при flush/commit.

## Исправление, если failure нужен раньше

```java
repository.save(customer);
repository.flush();
```

## Boundary

Flush обнаружит database error раньше, но не commit-ит transaction.

---

# Case 5. Batch import потребляет несколько гигабайт heap

## Код

```java
@Transactional
public void importRows(List<Row> rows) {
    for (Row row : rows) {
        entityManager.persist(map(row));
    }
}
```

## Причина

Persistence context удерживает entities и dirty-check snapshots до конца transaction.

## Исправление

```java
if ((index + 1) % 100 == 0) {
    entityManager.flush();
    entityManager.clear();
}
```

Дополнительно: JDBC batching, chunk transactions, bounded input buffer.

---

# Case 6. Bulk update изменил DB, но service читает старое значение

## Код

```java
Customer customer = repository.findById(id).orElseThrow(...);
repository.bulkDeactivate(id);
return customer.getStatus();
```

## Симптом

DB status = INACTIVE, returned object status = ACTIVE.

## Причина

Bulk JPQL DML обходит managed instance.

## Исправление

- `@Modifying(flushAutomatically = true, clearAutomatically = true)`;
- `entityManager.refresh(customer)`;
- разделить bulk operation и managed graph use.

---

# Case 7. N+1 обнаружился только в production

## Симптом

Endpoint на 100 customers выполняет 101 query и занимает секунды.

## Код

```java
List<Customer> customers = repository.findAll();
customers.forEach(c -> c.getOrders().size());
```

## Почему test прошёл

- 2 rows в H2;
- SQL logs выключены;
- local latency низкая;
- first-level cache скрывает часть повторов.

## Исправление

- projection;
- fetch join;
- `@EntityGraph`;
- batch fetching;
- dedicated aggregate query.

## Доказательство

Hibernate statistics или datasource proxy query count.

---

# Case 8. `FetchType.EAGER` не устранил N+1

## Симптом

Association помечена EAGER, но provider выполняет один query parents и N secondary selects.

## Причина

EAGER задаёт требование загрузки, но не SQL strategy.

## Исправление

Определить fetch plan на query method, а не глобально менять mapping.

---

# Case 9. `Page<Customer>` с collection fetch join возвращает странные pages

## Query

```java
@Query("select c from Customer c left join fetch c.orders")
Page<Customer> findDetailed(Pageable pageable);
```

## Симптом

- fewer roots than page size;
- duplicates;
- in-memory pagination warning;
- дорогой count.

## Причина

SQL page применяется к joined rows, а API page — к root entities.

## Исправление

```text
1. page root IDs
2. fetch graph by IDs
3. restore requested ordering
```

или projection/summary query.

---

# Case 10. Count query медленнее content query в 20 раз

## Симптом

Первые 20 rows приходят за 50 ms, весь endpoint отвечает 8 seconds.

## Причина

`Page` запускает сложный count query с joins/distinct.

## Диагностика

Логировать content и count SQL отдельно; смотреть оба execution plans.

## Исправления

- `Slice`;
- simplified explicit `countQuery`;
- precomputed totals;
- keyset pagination;
- remove unnecessary joins from count.

---

# Case 11. Offset page пропускает/дублирует записи при новых inserts

## Sort

```text
ORDER BY created_at DESC
```

## Причина

Sort не unique, data изменяется между page requests.

## Исправление

```text
ORDER BY created_at DESC, id DESC
```

Для deep scrolling — keyset cursor `(createdAt, id)`.

---

# Case 12. Optimistic locking конфликт перезаписывается бесконечным retry

## Симптом

Два users редактируют credit application. Второй получает `OptimisticLockException`; generic retry повторяет update и перезаписывает решение первого.

## Причина

Retry не учитывает business semantics stale command.

## Исправление

- rollback;
- reread current state;
- compare user intent;
- show conflict or recompute command;
- retry только idempotent/revalidatable operation.

## Lesson

`@Version` обнаруживает conflict; business layer решает, можно ли retry.

---

# Case 13. Pessimistic locks вызвали deadlock

## Timeline

```text
T1 locks Account 10
T2 locks Account 20
T1 waits Account 20
T2 waits Account 10
```

## Причина

Rows блокируются в разном порядке.

## Исправление

- sort IDs;
- lock lower ID first;
- keep transaction short;
- configure lock timeout;
- retry deadlock victim with bounded attempts.

---

# Case 14. Repository projection неожиданно загружает большой nested graph

## Projection

```java
interface CustomerView {
    String getName();
    AddressView getAddress();
}
```

## Симптом

SQL содержит join и много address columns.

## Причина

Nested property traversal materializes joined property; projection optimization mainly applies to selected top-level properties.

## Исправление

Использовать explicit flat DTO query для critical endpoint.

---

# Case 15. `getReferenceById()` падает позже, чем ожидалось

## Код

```java
Customer customer = repository.getReferenceById(id);
order.setCustomer(customer);
log.info(customer.getName());
```

## Симптом

`EntityNotFoundException` появляется на `getName()`, а не на `getReferenceById()`.

## Причина

Method может вернуть lazy reference без immediate SELECT.

## Исправление

- `findById()` если existence/state нужны сейчас;
- reference only для FK assignment;
- rely on FK constraint intentionally.

---

# Case 16. Transactional test зелёный, production endpoint делает N+1 и lazy queries

## Симптом

Test method помечен `@Transactional`; mapper работает. В production mapper вызывается после service transaction и падает/делает SQL через OSIV.

## Причина

Test transaction lifetime не совпадает с production boundary.

## Исправление

- integration test через real service/controller boundary;
- disable OSIV in selected tests;
- assert query count;
- map DTO inside service transaction;
- force `flush()`/`clear()` before assertions when needed.

# Production review checklist

```text
Entity state?
Persistence context active?
Flush happened?
Transaction committed?
How many SQL statements?
Fetch plan explicit?
Page count query measured?
Sort stable?
Bulk DML cleared context?
Lock mode and order?
Projection or entity appropriate?
```

# Related

- [[10_CONCEPTS/Spring/Data/Spring Data JPA Persistence Context and Entity Lifecycle]]
- [[10_CONCEPTS/Spring/Data/Spring Data Repositories Queries and Fetching]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/DATA-B01/DATA-B01 Cards]]
- [[50_LABS/Spring/DATA-B01/README]]
