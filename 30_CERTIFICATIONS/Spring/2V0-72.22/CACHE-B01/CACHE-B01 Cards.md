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
caffeine_versions:
  - 2.9.3
tags:
  - spring
  - certification
  - caching
  - caffeine
  - redis
---

# CACHE-B01 — Spring Cache, Caffeine and Redis

> [!summary]
> 20 нормализованных карточек по Spring Cache abstraction, cache keys, eviction, stampede, Caffeine local cache, Redis shared cache, TTL, serialization и согласованности L1/L2. Каждая карточка отделяет Spring contract от provider-specific behavior.

---

## CACHE-B01-C001 — Is Spring Cache a cache storage implementation?

### Russian Translation

Является ли Spring Cache реализацией хранилища кеша?

> [!answer]- Answer
> No. Spring Cache is an abstraction and AOP integration layer over cache providers selected through `CacheManager`.

### Explanation

Spring определяет **когда** выполнить lookup, put или eviction. `CacheManager` находит cache region, а конкретный provider определяет storage, expiration, capacity, serialization, distribution и failure behavior.

```text
@Cacheable
   ↓
CacheInterceptor
   ↓
CacheManager
   ↓
Caffeine / Redis / another provider
```

### Mini Example

```java
@EnableCaching
@Configuration
class CacheConfiguration {

    @Bean
    CacheManager cacheManager() {
        return new CaffeineCacheManager("products");
    }
}
```

### Exam Trap

`@EnableCaching` включает proxy infrastructure, но не создаёт production-grade distributed storage, TTL policy или invalidation protocol автоматически.

### Memory Hook

**Spring decides when; provider decides where and how.**

---

## CACHE-B01-C002 — What does `@Cacheable` do on a cache hit?

### Russian Translation

Что делает `@Cacheable` при cache hit?

> [!answer]- Answer
> It returns the cached value and skips the underlying target method invocation.

### Explanation

Interceptor до target invocation вычисляет cache name и key, затем выполняет lookup. При hit cached value становится method result. При miss вызывается target method, после чего result может быть сохранён.

### Mini Example

```java
@Cacheable(cacheNames = "productById", key = "#id")
public ProductDto findProduct(long id) {
    databaseCalls.incrementAndGet();
    return repository.findDtoById(id).orElseThrow();
}
```

Два последовательных proxy-вызова с `id=42` обычно дают один database call.

### Exam Trap

Cache hit не выполняет method body. Поэтому side effects, metrics counter или authorization, расположенные **внутри** cached method, также будут пропущены.

### Production Transfer

Размещай security и обязательные side effects вне cached computation либо обеспечивай правильный advisor order.

### Memory Hook

**Hit returns early; miss invokes and stores.**

---

## CACHE-B01-C003 — What is the difference between `condition` and `unless`?

### Russian Translation

Чем отличаются `condition` и `unless`?

> [!answer]- Answer
> `condition` is evaluated before invocation and decides whether caching participates; `unless` is evaluated after invocation and can veto storing the result.

### Explanation

`condition` имеет доступ к method arguments и context, но ещё не знает result. `unless` выполняется после target method и поэтому может использовать `#result`.

### Mini Example

```java
@Cacheable(
    cacheNames = "search",
    key = "#query",
    condition = "#query.length() >= 3",
    unless = "#result == null || #result.isEmpty()"
)
public List<ProductDto> search(String query) {
    return repository.search(query);
}
```

### Exam Trap

`#result` недоступен в pre-invocation `condition`. Кроме того, `unless=true` не отменяет уже выполненный expensive method — оно только запрещает cache put.

### Production Transfer

Не кешируй transient empty result без осознанного negative-caching policy, иначе краткий downstream failure может превратиться в длительную ложную пустоту.

### Memory Hook

**Condition before; unless after.**

---

## CACHE-B01-C004 — What does `@CachePut` guarantee about method execution?

### Russian Translation

Что `@CachePut` гарантирует относительно выполнения method?

> [!answer]- Answer
> The method is always invoked, and its result is then written to the cache according to the configured key and conditions.

### Explanation

`@CachePut` предназначен для синхронизации cache после authoritative update или explicit refresh. В отличие от `@Cacheable`, он не использует hit для пропуска method body.

### Mini Example

```java
@Transactional
@CachePut(cacheNames = "productById", key = "#result.id")
public ProductDto rename(long id, String name) {
    Product product = repository.findById(id).orElseThrow();
    product.rename(name);
    return mapper.toDto(product);
}
```

### Exam Trap

Не смешивай `@Cacheable` и `@CachePut` на одном method без взаимоисключающих conditions: одна annotation хочет пропустить invocation, другая требует его выполнить.

### Production Transfer

Проверь, что write method и read method используют одинаковый key contract. `#id` против `#result.id` должны давать один и тот же serialized key.

### Memory Hook

**CachePut always calls, then updates.**

---

## CACHE-B01-C005 — What does `@CacheEvict` do by default when the method throws?

### Russian Translation

Что по умолчанию делает `@CacheEvict`, если method бросил exception?

> [!answer]- Answer
> With the default after-invocation behavior, eviction is not performed when the method fails.

### Explanation

По умолчанию eviction происходит после нормального завершения method. Это полезно для command: если authoritative update упал, прежнее cached value может оставаться корректным.

### Mini Example

```java
@CacheEvict(cacheNames = "productById", key = "#id")
public void delete(long id) {
    repository.deleteById(id);
}
```

Если `deleteById` выбросил exception, entry обычно не удаляется.

### Exam Trap

`beforeInvocation=true` удаляет entry **до** method call и поэтому eviction сохранится даже при последующем exception. Это меняет failure semantics.

### Production Transfer

Используй `beforeInvocation=true`, только когда stale value опаснее временного miss и повторная загрузка безопасна даже после failed command.

### Memory Hook

**Default evict after success; beforeInvocation evicts regardless.**

---

## CACHE-B01-C006 — Why is a cache key part of the data contract?

### Russian Translation

Почему cache key является частью data contract?

> [!answer]- Answer
> It defines the identity and isolation of cached values; an incomplete, ambiguous or unstable key can return data for the wrong request.

### Explanation

Cache key должен включать все аргументы, влияющие на result: tenant, locale, authorization scope, filters, version и entity identity. Key также должен быть стабилен между write, read и eviction paths.

### Mini Example

Плохо:

```java
@Cacheable(cacheNames = "accounts", key = "#accountId")
AccountDto find(String tenantId, long accountId) { ... }
```

Хорошо:

```java
@Cacheable(cacheNames = "accounts", key = "#tenantId + ':' + #accountId")
AccountDto find(String tenantId, long accountId) { ... }
```

### Exam Trap

Совпадение Java `equals/hashCode` недостаточно для Redis, если serializer или string representation меняется между deployments.

### Production Transfer

Key contract должен быть versioned и документирован так же, как external API schema.

### Memory Hook

**Wrong key means wrong identity.**

---

## CACHE-B01-C007 — Why can `@Cacheable` fail during self-invocation?

### Russian Translation

Почему `@Cacheable` может не сработать при self-invocation?

> [!answer]- Answer
> The internal `this.method()` call bypasses the caching proxy and its interceptor.

### Explanation

Caching — proxy-based concern. После входа в target object внутренний call остаётся обычным Java invocation и не выполняет cache lookup/put.

### Mini Example

```java
@Service
class CatalogService {

    public ProductDto warm(long id) {
        return find(id); // bypasses cache proxy
    }

    @Cacheable(cacheNames = "productById", key = "#id")
    public ProductDto find(long id) {
        return repository.findDtoById(id).orElseThrow();
    }
}
```

### Exam Trap

Переход с JDK proxy на CGLIB не исправляет self-invocation. Проблема в caller path, а не в proxy implementation.

### Production Transfer

Вынеси cached query в отдельный `ProductQueryService` и вызывай его через injected proxy.

### Memory Hook

**No proxy crossing — no cache interception.**

---

## CACHE-B01-C008 — What problem does `sync=true` address?

### Russian Translation

Какую проблему решает `sync=true`?

> [!answer]- Answer
> It asks the cache provider to coordinate concurrent loading of the same missing key so that fewer callers execute the expensive method simultaneously.

### Explanation

Spring использует synchronized cache-loading contract провайдера. Это полезно для same-key contention внутри области координации конкретного cache implementation.

### Mini Example

```java
@Cacheable(cacheNames = "riskProfile", key = "#clientId", sync = true)
public RiskProfile load(long clientId) {
    return remoteRiskClient.fetch(clientId);
}
```

При одновременном miss по одному key Caffeine может выполнить один mapping computation для локального cache.

### Exam Trap

`sync=true` не является универсальным distributed lock между всеми JVM. Эффект зависит от provider implementation; для Redis и multi-node topology нужно проверять точный runtime contract.

### Production Transfer

В Spring 5.3 synchronous caching имеет ограничения на сочетание cache operations и result veto. Не проектируй сложную policy, не проверив документацию версии.

### Memory Hook

**Sync coordinates a miss; it does not make a cluster lock.**

---

## CACHE-B01-C009 — What is cache stampede?

### Russian Translation

Что такое cache stampede?

> [!answer]- Answer
> It occurs when many concurrent requests observe the same missing or expired key and simultaneously reload it from the backing system.

### Explanation

Один hot key после expiration может превратить cache в synchronized traffic amplifier:

```text
100 requests
   ↓ same expired key
100 database/HTTP loads
```

### Mini Example

```java
@Cacheable(cacheNames = "exchangeRates", key = "'daily'")
public Rates loadRates() {
    return slowExternalProvider.fetch();
}
```

Если entry истекает ровно в начале traffic peak, все nodes могут начать reload одновременно.

### Exam Trap

Большой TTL уменьшает частоту stampede, но увеличивает staleness. Это не полноценное решение.

### Production Transfer

Используй комбинацию single-flight, refresh-ahead, TTL jitter, stale-while-revalidate, bounded concurrency и downstream rate limits.

### Memory Hook

**One expired hot key can unleash the whole crowd.**

---

## CACHE-B01-C010 — What is the primary locality property of Caffeine?

### Russian Translation

Каково главное свойство локальности Caffeine?

> [!answer]- Answer
> Each JVM instance owns an independent in-memory cache; entries and evictions are not automatically shared with other application nodes.

### Explanation

Caffeine обеспечивает очень низкую latency и не требует network hop, но cache state живёт внутри process memory.

```text
node A: product 42 = v2
node B: product 42 = v1
```

Такое расхождение возможно без invalidation protocol.

### Mini Example

```java
CaffeineCacheManager manager = new CaffeineCacheManager("productById");
manager.setCaffeine(
    Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(Duration.ofMinutes(2))
);
```

### Exam Trap

Caffeine не становится distributed cache из-за того, что приложение запущено в Kubernetes. У каждого pod собственная копия.

### Production Transfer

При rolling deployment каждая новая JVM начинает с cold cache; учитывай warm-up и database load.

### Memory Hook

**Caffeine is local speed.**

---

## CACHE-B01-C011 — What is the difference between `maximumSize` and `maximumWeight` in Caffeine?

### Russian Translation

Чем отличаются `maximumSize` и `maximumWeight` в Caffeine?

> [!answer]- Answer
> `maximumSize` limits entry count, while `maximumWeight` combines entry weights from a custom `Weigher` and limits their estimated total.

### Explanation

Entry count подходит для примерно одинаковых values. Weight useful, когда один cached object занимает 2 KB, а другой — 5 MB. Weight является application estimate, а не точным измерением JVM heap bytes.

### Mini Example

```java
Caffeine.newBuilder()
    .maximumWeight(100 * 1024 * 1024L)
    .weigher((String key, byte[] value) -> value.length)
    .build();
```

### Exam Trap

`maximumWeight` без осмысленного `Weigher` не моделирует memory cost. Также size/weight eviction не гарантирует strict business priority для каждой entry.

### Production Transfer

Добавь eviction metrics и heap monitoring; неверная weight function может создать либо низкий hit rate, либо memory pressure.

### Memory Hook

**Size counts entries; weight estimates cost.**

---

## CACHE-B01-C012 — How do `expireAfterWrite` and `expireAfterAccess` differ?

### Russian Translation

Чем отличаются `expireAfterWrite` и `expireAfterAccess`?

> [!answer]- Answer
> `expireAfterWrite` measures age from creation or replacement; `expireAfterAccess` extends an entry's lifetime from its latest qualifying access.

### Explanation

`expireAfterWrite` задаёт верхнюю границу freshness относительно write time. `expireAfterAccess` сохраняет hot entries и удаляет idle entries.

### Mini Example

```java
Caffeine.newBuilder()
    .expireAfterWrite(Duration.ofMinutes(5))
    .maximumSize(10_000);
```

```java
Caffeine.newBuilder()
    .expireAfterAccess(Duration.ofMinutes(5))
    .maximumSize(10_000);
```

### Exam Trap

Expiration не гарантирует отдельный timer callback ровно в указанную миллисекунду. Physical cleanup зависит от provider maintenance, хотя expired entry уже не должна считаться valid hit.

### Production Transfer

Для business freshness чаще легче обосновать `expireAfterWrite`; access-based expiry может удерживать популярное, но давно устаревшее value.

### Memory Hook

**Write expiry limits age; access expiry rewards activity.**

---

## CACHE-B01-C013 — What is the primary distribution property of Redis cache?

### Russian Translation

Каково главное распределённое свойство Redis cache?

> [!answer]- Answer
> Multiple application instances can use the same external cache store and observe shared entries and evictions.

### Explanation

Redis переносит cache state из JVM в отдельный network service. Это упрощает cross-node sharing, но добавляет network latency, serialization, connection pools и отдельный failure domain.

### Mini Example

```text
node A ─┐
node B ─┼─→ Redis: productById::42
node C ─┘
```

### Exam Trap

Shared Redis не гарантирует, что cache всегда согласован с database. DB update и Redis mutation остаются двумя отдельными systems.

### Production Transfer

Настрой timeout, connection pool, metrics, keyspace isolation и explicit outage policy.

### Memory Hook

**Redis shares state, not truth.**

---

## CACHE-B01-C014 — Why should Redis cache values use an explicit serialization contract?

### Russian Translation

Почему Redis cache values должны иметь явный serialization contract?

> [!answer]- Answer
> Values cross process and deployment boundaries, so type evolution, compatibility, readability and security must be controlled explicitly.

### Explanation

Serialized value может быть записан version N и прочитан version N+1 другим node. Persistence entities, lazy proxies и implementation-specific class metadata создают хрупкий wire format.

### Mini Example

```java
record-like Java 8 DTO concept:

final class ProductCacheValue {
    private final int schemaVersion;
    private final long id;
    private final String name;
}
```

Key prefix:

```text
catalog:prod:product:v2::42
```

### Exam Trap

Java native serialization или serializer с embedded class names может превратить harmless refactoring в cache deserialization failure.

### Production Transfer

Используй stable cache DTO, explicit serializer, schema version и controlled rollout/eviction strategy.

### Memory Hook

**Redis value is a wire contract.**

---

## CACHE-B01-C015 — What does Redis cache TTL control?

### Russian Translation

Что контролирует TTL в Redis cache?

> [!answer]- Answer
> It controls how long an entry remains eligible before expiration according to the configured Redis cache policy.

### Explanation

В Spring Data Redis 2.7 `RedisCacheConfiguration#entryTtl` обычно задаёт fixed TTL для cache region. TTL ограничивает staleness window и memory retention, но не понимает business events.

### Mini Example

```java
RedisCacheConfiguration products = RedisCacheConfiguration
    .defaultCacheConfig()
    .entryTtl(Duration.ofMinutes(10))
    .disableCachingNullValues();
```

### Exam Trap

TTL не заменяет invalidation после update. Даже TTL 30 seconds может быть недопустим для account balance или access rights.

### Production Transfer

Выбирай TTL по допустимой stale-data duration, reload cost и outage behavior, а не по произвольному общему значению для всех caches.

### Memory Hook

**TTL bounds age; invalidation reacts to change.**

---

## CACHE-B01-C016 — Why use a Redis key prefix?

### Russian Translation

Зачем нужен prefix для Redis cache keys?

> [!answer]- Answer
> It separates services, environments, bounded contexts, cache regions and schema generations in a shared Redis keyspace.

### Explanation

Без namespace два приложения могут создать одинаковый raw key с разной semantic или serializer. Version prefix также позволяет безопасно переключить format без массовой синхронной миграции old entries.

### Mini Example

```text
bank:prod:catalog:v2:productById::42
bank:test:catalog:v2:productById::42
```

### Exam Trap

Prefix не заменяет authorization/tenant component внутри business key. Namespace отделяет systems, но не обязательно пользователей одного system.

### Production Transfer

Включай service, environment, cache/schema version и region. Избегай key, содержащих secrets или персональные данные в открытом виде.

### Memory Hook

**Prefix defines the keyspace boundary.**

---

## CACHE-B01-C017 — What does a transaction-aware Redis cache manager not guarantee?

### Russian Translation

Чего не гарантирует transaction-aware Redis cache manager?

> [!answer]- Answer
> It does not create an atomic distributed transaction between the relational database and Redis.

### Explanation

Transaction-aware decorator может отложить cache put/evict до successful Spring transaction completion. Но после database commit процесс может завершиться до Redis command, Redis может быть unavailable, либо network response потеряется.

### Mini Example

```text
DB commit succeeds
      ↓
process crashes before Redis eviction
      ↓
stale cache remains
```

### Exam Trap

«После commit» не означает «атомарно вместе с commit». Это ordering improvement, а не XA consistency guarantee.

### Production Transfer

Для критичной cross-system invalidation используй outbox/CDC, idempotent invalidation event и reconciliation.

### Memory Hook

**Transaction-aware timing is not distributed atomicity.**

---

## CACHE-B01-C018 — What is the main risk of a two-level Caffeine plus Redis cache?

### Russian Translation

Каков главный риск двухуровневого кеша Caffeine + Redis?

> [!answer]- Answer
> Independent per-node L1 caches can retain stale values after Redis or the database has been updated unless an explicit invalidation or version protocol exists.

### Explanation

Типичный read path:

```text
request → Caffeine L1 → Redis L2 → database
```

Redis eviction не очищает автоматически Caffeine во всех JVM. Один node может продолжить отдавать stale L1 value.

### Mini Example

```text
node A updates DB and evicts Redis
node B still has product 42 in Caffeine for 2 minutes
```

### Exam Trap

Указание двух cache names в `@Cacheable` не создаёт автоматически корректную L1/L2 hierarchy с promotion и invalidation semantics.

### Production Transfer

Используй short L1 TTL, pub/sub or event-bus invalidation, versioned values/generation token и metrics для stale window.

### Memory Hook

**Two levels double speed opportunities and consistency obligations.**

---

## CACHE-B01-C019 — What can happen when Redis fails and every node bypasses cache?

### Russian Translation

Что может произойти, если Redis упал и все nodes начали обходить кеш?

> [!answer]- Answer
> The backing database or downstream service can receive the full uncached traffic load and become the next failure point.

### Explanation

Cache является load-bearing component. «Fail open to database» может сохранить функциональность при малой нагрузке, но вызвать connection-pool exhaustion и cascading failure во время peak traffic.

### Mini Example

```text
normal: 20 000 req/s → 95% cache hit → 1 000 DB req/s
outage: 20 000 req/s → 0% cache hit  → 20 000 DB req/s
```

### Exam Trap

Fallback на database не является безопасным только потому, что данные там authoritative. Capacity и latency limits могут быть намного ниже request rate.

### Production Transfer

Определи bulkhead, request coalescing, rate limit, stale-data mode, circuit breaker и maximum fallback concurrency.

### Memory Hook

**Cache outage can become database outage.**

---

## CACHE-B01-C020 — What should be checked first when a cache appears ineffective?

### Russian Translation

Что проверять первым, если кеш кажется неработающим?

> [!answer]- Answer
> Verify proxy crossing, the actual `CacheManager`, cache name, calculated key, conditions, TTL/eviction policy, provider locality and hit/miss metrics.

### Explanation

Рациональная последовательность:

```text
1. Spring-owned proxied bean?
2. External caller crosses proxy?
3. Expected CacheManager selected?
4. Correct cache region exists?
5. Same key on read/write/evict?
6. condition/unless permit storage?
7. Entry expires or is evicted too early?
8. Are observations from the same JVM/provider?
9. What do hit/miss/load metrics show?
```

### Mini Example

```java
Cache cache = cacheManager.getCache("productById");
log.info("cacheType={}", cache.getNativeCache().getClass());
log.info("key42={}", cache.get(42L));
```

Для Caffeine дополнительно используй provider statistics; для Redis — command latency, hits/misses, key TTL и serializer diagnostics.

### Exam Trap

Повторный database query не всегда означает cache miss: key может отличаться по type (`42` vs `"42"`), caller может обходить proxy, а наблюдение может выполняться на другой JVM с локальным Caffeine.

### Production Transfer

Диагностируй runtime evidence, а не только annotations в source code.

### Memory Hook

**Proxy → manager → region → key → policy → locality → metrics.**

## Related materials

- [[10_CONCEPTS/Spring/Cache/Spring Cache with Caffeine and Redis]]
- [[10_CONCEPTS/Spring/AOP/Spring AOP Proxies and Cache Interception]]
- [[50_LABS/Spring/CACHE-B01/README]]
- [[40_PRODUCTION_CASES/Spring/AOP and Cache Production Cases]]
- [[98_SOURCES/Spring AOP and Cache Sources]]
