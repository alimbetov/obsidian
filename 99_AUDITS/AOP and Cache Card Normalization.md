---
type: quality-audit
domain: spring
subdomain:
  - aop
  - caching
status: completed
normalized_at: 2026-07-21
batches:
  - AOP-B01
  - CACHE-B01
tags:
  - audit
  - cards
  - pedagogy
  - aop
  - cache
---

# AOP and Cache Card Normalization

> [!summary]
> `AOP-B01` и `CACHE-B01` приведены к единому pedagogical contract. Нормализация не сводилась к добавлению пустых headings: для каждой карточки добавлены mechanism explanation, конкретная exam trap и, где distinction зависит от execution path, реальный code/production example.

# 1. Scope

Нормализованы:

```text
AOP-B01    24 / 24 cards
CACHE-B01  20 / 20 cards
---------------------------
TOTAL      44 / 44 cards
```

Каждая карточка теперь содержит:

```text
Question
Russian Translation
Answer
Explanation
Exam Trap
```

Дополнительные секции используются по смыслу:

```text
Mini Example
Production Transfer
Memory Hook
```

# 2. Нормализация AOP-B01

## 2.1 Единая execution model

Карточки используют одну базовую модель:

```text
caller
  → Spring proxy
  → matching advisors/interceptors
  → target method
  → reverse unwind
```

Эта модель применяется к:

- transaction interception;
- caching;
- async submission;
- method security;
- custom around advice;
- metrics and audit.

## 2.2 Усиленные distinctions

### Join point, pointcut, advice и advisor

```text
join point → потенциально перехватываемый method execution
pointcut   → predicate, выбирающий execution
advice     → выполняемое cross-cutting behavior
advisor    → pointcut + advice
```

### JDK proxy и CGLIB

```text
JDK proxy   → implements interfaces
CGLIB proxy → subclasses target class
```

Обе модели сохраняют общий self-invocation limitation:

```text
this.method()
  → direct target call
  → proxy is not crossed
  → proxy advice is absent
```

### Interceptability

Явно показаны ограничения:

- `private` method не является proxy-visible boundary;
- `final` method нельзя override в CGLIB subclass;
- объект, созданный через `new`, не проходит Spring post-processing;
- annotation сама по себе не создаёт runtime behavior.

## 2.3 Реальные AOP-примеры

Добавлены примеры:

1. reusable security/transaction/metrics boundary;
2. `ProceedingJoinPoint.proceed()` и пропуск inner interceptors;
3. JDK proxy через interface contract;
4. CGLIB class-based proxy;
5. `final` method с неработающим transaction advice;
6. `private` method с `Propagation.REQUIRES_NEW`;
7. same-class `REQUIRES_NEW` failure;
8. refactoring в отдельный `AuditService`;
9. `AopContext.currentProxy()` requirements;
10. advisor entry/exit order;
11. exception swallowing, создающее false success;
12. runtime diagnostics через `AopUtils`;
13. advisor-chain inspection через `Advised#getAdvisors()`;
14. self-invoked `@Async`, работающий на caller thread;
15. async transaction boundary на worker thread;
16. internal method-security bypass.

## 2.4 Исправление, найденное при review

Первый draft содержал несуществующий параметр:

```text
@Transactional(requiresNew = true)
```

Он заменён на корректный Spring API:

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
```

При этом example намеренно оставляет method `private`, чтобы показать: корректная annotation syntax всё равно не делает private method перехватываемым.

# 3. Нормализация CACHE-B01

## 3.1 Разделение Spring contract и provider behavior

Каждая карточка различает:

```text
Spring Cache abstraction
  → interception, cache operation, key evaluation

CacheManager
  → cache-region selection

Provider
  → locality, storage, capacity, expiration,
     serialization, locking and failure behavior
```

## 3.2 Усиленные distinctions

### `@Cacheable`, `@CachePut`, `@CacheEvict`

```text
@Cacheable → hit skips method; miss invokes and may store
@CachePut  → always invokes, then writes result
@CacheEvict default → evicts after successful invocation
beforeInvocation=true → evicts before method, even if it fails
```

### `condition` и `unless`

```text
condition → before invocation, no #result
unless    → after invocation, may inspect #result
```

### Caffeine и Redis

```text
Caffeine → per-JVM local state and low latency
Redis    → shared external state with network/serialization cost
```

### TTL и invalidation

```text
TTL          → bounds entry age
invalidation → reacts to business change
```

TTL не считается заменой update-driven invalidation.

### Transaction-aware timing

```text
DB commit
  → deferred cache operation
```

не объявляется distributed atomic transaction:

```text
DB commit succeeds
process/Redis fails before eviction
stale cache remains
```

## 3.3 Реальные Cache-примеры

Добавлены примеры:

1. `CacheInterceptor → CacheManager → provider`;
2. cache hit, пропускающий method body;
3. conditional caching и empty-result veto;
4. `@CachePut` после authoritative update;
5. default versus before-invocation eviction;
6. multi-tenant cache-key collision;
7. self-invocation bypass;
8. `sync=true` same-key miss coordination;
9. hot-key cache stampede;
10. Caffeine per-node stale copies;
11. `maximumSize` versus `maximumWeight`;
12. `expireAfterWrite` versus `expireAfterAccess`;
13. Redis shared topology;
14. versioned Redis cache DTO;
15. `RedisCacheConfiguration#entryTtl`;
16. environment/service/schema key prefix;
17. failed Redis eviction after DB commit;
18. Caffeine L1 + Redis L2 cross-node staleness;
19. Redis outage increasing database load from 1,000 to 20,000 requests per second;
20. runtime diagnostic sequence from proxy to hit/miss metrics.

# 4. Technical precision rules

Нормализация придерживается baseline:

```text
Java                 8
Spring Framework     5.3.39
Spring Data Redis    2.7.18
Caffeine             2.9.3
```

Применены правила:

- `sync=true` не называется универсальным cluster-wide lock;
- Redis transaction-aware cache не называется XA transaction;
- Caffeine expiration не описывается как обязательный timer callback ровно в deadline;
- Redis value рассматривается как wire/schema contract;
- L1/L2 hierarchy не считается автоматическим результатом нескольких cache names;
- cache outage анализируется как capacity/failure-domain problem;
- annotations не рассматриваются отдельно от proxy path.

# 5. Validation gate

Проверяется repository-wide auditor:

```text
.github/scripts/audit_vault_v2.py
.github/workflows/vault-quality-audit.yml
```

Обязательные assertions для двух batches:

- frontmatter `card_count` совпадает с количеством IDs;
- IDs уникальны и соответствуют `batch_id`;
- все cards имеют mandatory sections;
- code fences закрыты;
- wikilinks разрешаются;
- Mermaid blocks рендерятся;
- source index связан с cards и canonical notes.

# 6. Remaining card-normalization backlog

После AOP/Cache остаются:

```text
TX-B01
DATA-B01
TEST-B01
CORE-B01 — 2 cards
CORE-B04 — 2 cards
```

Однако согласованный следующий implementation route после текущего rational stage:

```text
DB-B01 — Indexes and Query Plans
```

Перед началом DB-B01 следует сохранить card-normalization standard как Definition of Done для всех новых batches.

# Related materials

- [[99_AUDITS/Obsidian Learning Vault Quality Audit]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring AOP and Cache Roadmap]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/AOP-B01/AOP-B01 Cards]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CACHE-B01/CACHE-B01 Cards]]
- [[10_CONCEPTS/Spring/AOP/Spring AOP Proxy Mechanics]]
- [[10_CONCEPTS/Spring/Cache/Spring Cache with Caffeine and Redis]]
- [[98_SOURCES/Spring AOP and Cache Sources]]
