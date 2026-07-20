---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain: caching
batch_id: CACHE-B01
status: published
card_count: 20
first_card: CACHE-B01-C001
last_card: CACHE-B01-C020
spring_versions:
  - 5.3.39
spring_data_redis_versions:
  - 2.7.18
tags:
  - spring
  - certification
  - caching
  - caffeine
  - redis
---

# CACHE-B01 — Spring Cache, Caffeine and Redis

> [!summary]
> 20 карточек по cache abstraction, keys, eviction, stampede, Caffeine local cache, Redis shared cache, TTL, serialization и L1/L2 consistency.

---

## CACHE-B01-C001 — Is Spring Cache a cache storage implementation?

### Russian Translation

Является ли Spring Cache реализацией хранилища кеша?

> [!answer]- Answer
> No. It is an abstraction and AOP integration over cache providers selected through `CacheManager`.

### Exam Trap

`@EnableCaching` does not create a production-grade storage by itself.

### Memory Hook

**Spring decides when; provider decides where and how.**

---

## CACHE-B01-C002 — What does `@Cacheable` do on a cache hit?

### Russian Translation

Что делает `@Cacheable` при cache hit?

> [!answer]- Answer
> It returns the cached value and skips the underlying target method invocation.

### Explanation

The cache interceptor calculates the key and queries the selected cache before invoking the target.

---

## CACHE-B01-C003 — What is the difference between `condition` and `unless`?

### Russian Translation

Чем отличаются `condition` и `unless`?

> [!answer]- Answer
> `condition` decides before invocation whether caching should apply; `unless` can inspect `#result` after invocation and veto storing it.

### Exam Trap

`#result` is not available to a pre-invocation condition.

---

## CACHE-B01-C004 — What does `@CachePut` guarantee about method execution?

### Russian Translation

Что `@CachePut` гарантирует относительно выполнения method?

> [!answer]- Answer
> The method is always invoked and its result is then placed in the cache.

### Exam Trap

`@CachePut` is not a read optimization and should not normally be mixed with `@Cacheable` on the same method.

---

## CACHE-B01-C005 — What does `@CacheEvict` do by default when the method throws?

### Russian Translation

Что по умолчанию делает `@CacheEvict`, если method бросил exception?

> [!answer]- Answer
> With the default after-invocation behavior, eviction is not performed when the method fails.

### Follow-up

`beforeInvocation=true` performs eviction before the method and therefore even if the method later fails.

---

## CACHE-B01-C006 — Why is a cache key part of the data contract?

### Russian Translation

Почему cache key является частью data contract?

> [!answer]- Answer
> It determines identity and isolation of cached values; an incomplete or unstable key can return the wrong data.

### Production Transfer

A multi-tenant cache key must usually include tenant identity, not only entity ID.

---

## CACHE-B01-C007 — Why can `@Cacheable` fail during self-invocation?

### Russian Translation

Почему `@Cacheable` может не сработать при self-invocation?

> [!answer]- Answer
> The internal `this.method()` call bypasses the caching proxy and interceptor.

### Best Fix

Move the cached query to a separate Spring collaborator.

---

## CACHE-B01-C008 — What problem does `sync=true` address?

### Russian Translation

Какую проблему решает `sync=true`?

> [!answer]- Answer
> It asks the cache provider to coordinate concurrent loading of the same key so multiple callers do not all invoke the expensive method on a miss.

### Exam Trap

It is not automatically a distributed lock across all application nodes.

---

## CACHE-B01-C009 — What is cache stampede?

### Russian Translation

Что такое cache stampede?

> [!answer]- Answer
> Many concurrent requests observe the same missing or expired key and simultaneously reload it from the backing system.

### Mitigations

Single-flight loading, refresh-ahead, TTL jitter, stale-while-revalidate, distributed locking or rate limiting.

---

## CACHE-B01-C010 — What is the primary locality property of Caffeine?

### Russian Translation

Каково главное свойство локальности Caffeine?

> [!answer]- Answer
> Each JVM instance has its own in-memory cache; entries are not automatically shared with other application nodes.

### Memory Hook

**Caffeine is local speed.**

---

## CACHE-B01-C011 — What is the difference between `maximumSize` and `maximumWeight` in Caffeine?

### Russian Translation

Чем отличаются `maximumSize` и `maximumWeight` в Caffeine?

> [!answer]- Answer
> `maximumSize` limits the number of entries, while `maximumWeight` uses a custom weigher to limit an estimated total weight.

### Production Transfer

Weight is useful when cached objects vary greatly in memory or payload size.

---

## CACHE-B01-C012 — How do `expireAfterWrite` and `expireAfterAccess` differ?

### Russian Translation

Чем отличаются `expireAfterWrite` и `expireAfterAccess`?

> [!answer]- Answer
> `expireAfterWrite` measures from creation/update; `expireAfterAccess` measures from the latest access.

### Exam Trap

Expiration policy belongs to the provider and is not expressed merely by `@Cacheable`.

---

## CACHE-B01-C013 — What is the primary distribution property of Redis cache?

### Russian Translation

Каково главное распределённое свойство Redis cache?

> [!answer]- Answer
> Multiple application instances can use the same external cache store and therefore observe the same shared entries and evictions.

### Memory Hook

**Redis is shared cache state.**

---

## CACHE-B01-C014 — Why should Redis cache values use an explicit serialization contract?

### Russian Translation

Почему Redis cache values должны иметь явный serialization contract?

> [!answer]- Answer
> Values cross process and deployment boundaries, so type evolution, readability, security and backward compatibility must be controlled.

### Production Transfer

Prefer stable cache DTOs over persistence entities with lazy proxies.

---

## CACHE-B01-C015 — What does Redis cache TTL control?

### Russian Translation

Что контролирует TTL в Redis cache?

> [!answer]- Answer
> It controls how long a written cache entry remains valid before expiration according to the provider's TTL semantics.

### Exam Trap

TTL is not a substitute for an invalidation strategy after business updates.

---

## CACHE-B01-C016 — Why use a Redis key prefix?

### Russian Translation

Зачем нужен prefix для Redis cache keys?

> [!answer]- Answer
> It separates services, environments, bounded contexts, schema versions and cache regions in one Redis keyspace.

### Example

```text
bank:prod:catalog:v2:productById::42
```

---

## CACHE-B01-C017 — What does a transaction-aware Redis cache manager not guarantee?

### Russian Translation

Чего не гарантирует transaction-aware Redis cache manager?

> [!answer]- Answer
> It does not create a distributed atomic transaction between the database and Redis.

### Explanation

It can align cache put/evict timing with Spring transaction completion, but it is not an XA consistency guarantee.

---

## CACHE-B01-C018 — What is the main risk of a two-level Caffeine plus Redis cache?

### Russian Translation

Каков главный риск двухуровневого кеша Caffeine + Redis?

> [!answer]- Answer
> Cross-node L1 caches can keep stale values after Redis or the database has been updated unless an invalidation protocol exists.

### Mitigations

Short L1 TTL, invalidation events, versioned entries or generation numbers.

---

## CACHE-B01-C019 — What can happen when Redis fails and every node bypasses cache?

### Russian Translation

Что может произойти, если Redis упал и все nodes начали обходить кеш?

> [!answer]- Answer
> The database can receive the entire uncached traffic load and become the next failure point.

### Production Transfer

Cache outage policy needs bulkheads, rate limits, fallback capacity and stale-data decisions.

---

## CACHE-B01-C020 — What should be checked first when a cache appears ineffective?

### Russian Translation

Что проверять первым, если кеш кажется неработающим?

> [!answer]- Answer
> Verify proxy crossing, actual `CacheManager`, cache name, calculated key, conditions, TTL, eviction, provider locality and hit/miss metrics.

### Memory Hook

**Proxy → manager → cache → key → policy → metrics.**

## Related materials

- [[10_CONCEPTS/Spring/Cache/Spring Cache with Caffeine and Redis]]
- [[50_LABS/Spring/CACHE-B01/README]]
- [[40_PRODUCTION_CASES/Spring/AOP and Cache Production Cases]]
