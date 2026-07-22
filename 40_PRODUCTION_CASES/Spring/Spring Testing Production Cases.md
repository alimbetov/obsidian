---
type: production-case-set
domain: spring
subdomain:
  - testing
  - test-context
  - testcontainers
status: active
case_count: 16
tags:
  - spring
  - testing
  - production
  - junit
  - testcontainers
---

# Spring Testing Production Cases

> [!summary]
> 16 кейсов построены от наблюдаемого симптома к test topology, скрытому false positive, evidence и корректной стратегии тестирования.

---

# Case 1. Repository test зелёный, production падает на unique constraint

## Симптом

Test сохраняет две entity с одинаковым business key и проходит, а production получает constraint violation.

## Код

```java
repository.save(new PurchaseOrder("ORD-1"));
repository.save(new PurchaseOrder("ORD-1"));
```

## Root cause

Hibernate отложил INSERT до flush. Test method завершился, и test transaction была rolled back без forced SQL evidence.

## Как доказать

```java
assertThatThrownBy(repository::flush)
        .isInstanceOf(DataIntegrityViolationException.class);
```

## Исправление

Force flush exactly at the operation whose database outcome matters.

## Production lesson

Assertion над Java object не является assertion над database constraint.

---

# Case 2. Test читает изменённое значение, но column mapping неверен

## Симптом

После `save()` test получает ожидаемое поле, хотя production после reload видит `null` или старое значение.

## Root cause

Повторный query вернул managed entity из first-level cache.

## Evidence

```java
repository.flush();
entityManager.clear();
PurchaseOrder reloaded = repository.findById(id).orElseThrow();
```

## Исправление

Persistence round-trip tests должны использовать `flush + clear + reload`.

---

# Case 3. `AFTER_COMMIT` listener никогда не выполняется в test

## Симптом

`@TransactionalEventListener(AFTER_COMMIT)` работает в production, но test не видит event.

## Root cause

Transactional test автоматически rolled back. Commit phase не наступила.

## Исправление

```java
TestTransaction.flagForCommit();
TestTransaction.end();
```

или точечный `@Commit` и verification в `@AfterTransaction`.

## Boundary

Даже successful after-commit callback не доказывает durable Kafka/RabbitMQ delivery.

---

# Case 4. Transaction propagation test проходит только с `@Transactional` на test

## Симптом

Test показывает rollback всех операций, но production сохраняет inner `REQUIRES_NEW` audit row.

## Root cause

Test-level transaction изменила caller topology. Application methods выполняются внутри уже существующей transaction.

## Исправление

Для propagation proof вызывать service из non-transactional test и проверять committed rows отдельными queries.

---

# Case 5. Test rollback не очистил данные

## Симптом

После transactional test в database осталась row.

## Root cause

Application вызвал `REQUIRES_NEW`, worker thread или preemptive timeout path. Эта операция не участвовала в test-managed transaction.

## Evidence

- transaction logs;
- thread names;
- physical transaction counters;
- query после `@AfterTransaction`.

## Исправление

Не предполагать, что rollback test transaction охватывает все resource operations.

---

# Case 6. `assertTimeoutPreemptively` оставляет committed rows

## Симптом

Test framework сообщает timeout и rollback, но данные остаются.

## Timeline

```text
test thread: Spring transaction active
    ↓
JUnit worker thread executes lambda
    ↓
no thread-bound test transaction
    ↓
repository opens/commits its own transaction
```

## Исправление

Использовать non-preemptive timeout либо transaction/database-specific timeout.

---

# Case 7. H2 test зелёный, PostgreSQL native query падает

## Симптом

Repository test проходит на H2, production получает syntax/type error.

## Typical causes

- `ILIKE`;
- JSONB operators;
- arrays;
- UUID casting;
- interval syntax;
- reserved identifiers;
- collation;
- native pagination.

## Исправление

Database-specific queries запускать на PostgreSQL Testcontainers.

## Lesson

`MODE=PostgreSQL` — compatibility aid, not PostgreSQL proof.

---

# Case 8. Testcontainers test подключился к H2

## Симптом

Container стартует, но Hibernate log показывает H2 dialect.

## Root cause

`@DataJpaTest` заменил datasource embedded database.

## Исправление

```java
@AutoConfigureTestDatabase(replace = Replace.NONE)
```

и dynamic datasource properties.

---

# Case 9. Container test работает локально, но падает в CI

## Симптом

Connection refused или test подключается не к тому host/port.

## Root cause

Hard-coded `localhost:5432` вместо container-provided connection details.

## Исправление

Использовать `getJdbcUrl()`, `getHost()` и mapped ports.

---

# Case 10. Suite иногда падает после предыдущего container test

## Симптом

DataSource указывает на уже остановленный container.

## Root cause

Container остановлен вручную, но Spring context остался в TestContext cache и был reused.

## Исправление

Согласовать container ownership с context cache. Не смешивать несовместимые lifecycle patterns.

---

# Case 11. Tests проходят по отдельности, но падают вместе

## Симптом

Duplicate keys, unexpected counts или stale fixture state.

## Root cause

Static shared container + committed tests + отсутствующий cleanup.

## Исправления

- unique fixture identifiers;
- transaction rollback where valid;
- truncate/schema reset for commit tests;
- separate database/schema per class;
- random-order suite run.

---

# Case 12. Test suite внезапно стала в три раза медленнее

## Симптом

Много повторных application startups.

## Root cause

Context cache fragmentation:

- уникальные `@MockBean` sets;
- разные inline properties;
- разные profiles;
- чрезмерный `@DirtiesContext`.

## Evidence

Включить logging TestContext cache statistics и сравнить merged configurations.

## Исправление

Стандартизировать test configurations и уменьшить число уникальных context shapes.

---

# Case 13. `@DirtiesContext` используется после каждого database test

## Симптом

Tests изолированы, но suite очень медленный.

## Root cause

Context rebuild используется как database cleanup.

## Исправление

- rollback;
- truncate;
- schema reset;
- cleanup SQL;
- unique fixtures.

`@DirtiesContext` оставить для реального context-level mutation.

---

# Case 14. N+1 вернулся после harmless refactoring

## Симптом

Functional tests зелёные, endpoint latency выросла линейно от количества rows.

## Root cause

Tests проверяли result values, но не query count.

## Исправление

```java
statistics.clear();
service.loadOrders();
assertThat(statistics.getPrepareStatementCount())
        .isLessThanOrEqualTo(2);
```

## Lesson

Performance-sensitive fetch plan — часть observable contract.

---

# Case 15. Optimistic-lock test никогда не получает conflict

## Симптом

Два «конкурентных» изменения всегда успешно сохраняются.

## Root cause

Обе references загружены одним `EntityManager` и указывают на одну managed instance.

## Исправление

Использовать два independent persistence contexts и две physical transactions.

---

# Case 16. Hibernate schema tests проходят, Flyway deployment падает

## Симптом

CI repository tests зелёные, deployment migration не применяется.

## Root cause

Tests используют `ddl-auto=create-drop`, поэтому production migration files вообще не проверяются.

## Исправление

```text
empty PostgreSQL container
    ↓
apply Flyway/Liquibase migrations
    ↓
verify schema version
    ↓
run repository tests
```

## Production lesson

Generated schema test и migration test доказывают разные вещи.
