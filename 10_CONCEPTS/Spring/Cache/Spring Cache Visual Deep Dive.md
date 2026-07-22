---
type: concept
domain: spring
subdomain: caching
difficulty: advanced
status: learning
interview: true
certification:
  - spring-2V0-72.22
spring_versions:
  - 5.3.39
spring_data_redis_versions:
  - 2.7.18
java_versions:
  - 8
  - 21
production_relevance: critical
prerequisites:
  - "[[Spring Cache with Caffeine and Redis]]"
  - "[[Spring AOP Visual Deep Dive]]"
related:
  - "[[10_CONCEPTS/Spring/Transactions/Spring Transaction Management Deep Dive|Spring Transaction Management Deep Dive]]"
  - "[[Transactional Outbox and Commit Boundaries]]"
tags:
  - spring
  - cache
  - caffeine
  - redis
  - diagrams
  - visual-learning
---

# Spring Cache Visual Deep Dive

> [!summary]
> Spring Cache — это proxy-based orchestration, а не само хранилище. Для правильного проектирования нужно одновременно видеть четыре плоскости: **interception path**, **key identity**, **provider topology** и **consistency/invalidation timeline**.

# 1. Полная модель cache invocation

```mermaid
sequenceDiagram
    participant C as Caller
    participant P as Cache proxy
    participant I as CacheInterceptor
    participant K as KeyGenerator
    participant M as CacheManager
    participant Cache as Cache provider
    participant T as Target method

    C->>P: findProduct(42)
    P->>I: invoke
    I->>K: generate key
    K-->>I: key=42
    I->>M: resolve cache region
    M-->>I: productById
    I->>Cache: get(42)
    alt cache hit
        Cache-->>I: ProductDto
        I-->>P: cached value
        P-->>C: ProductDto
    else cache miss
        Cache-->>I: null
        I->>T: invoke findProduct(42)
        T-->>I: ProductDto
        I->>Cache: put(42, ProductDto)
        I-->>P: ProductDto
        P-->>C: ProductDto
    end
```

Главный вывод: cache hit не просто ускоряет method — он **полностью пропускает target invocation**.

# 2. Spring и provider отвечают за разные вещи

```mermaid
flowchart LR
    Spring["Spring Cache abstraction"] --> S1["Annotation parsing"]
    Spring --> S2["Proxy interception"]
    Spring --> S3["Cache name and key"]
    Spring --> S4["condition and unless"]
    Spring --> S5["put and evict orchestration"]

    Provider["Cache provider"] --> P1["Storage"]
    Provider --> P2["Eviction and expiration"]
    Provider --> P3["Concurrency semantics"]
    Provider --> P4["Serialization"]
    Provider --> P5["Local or distributed topology"]
```

```text
Spring decides when.
Provider decides where and how.
```

# 3. `@Cacheable`: hit и miss как две разные программы

```java
@Cacheable(
        cacheNames = "productById",
        key = "#tenantId + ':' + #productId",
        unless = "#result == null"
)
public ProductDto findById(String tenantId, Long productId) {
    return repository.findRequired(tenantId, productId);
}
```

## Miss path

```mermaid
flowchart TD
    A["Call findById"] --> B["Calculate tenantId:productId"]
    B --> C{"Entry exists?"}
    C -->|No| D["Invoke repository"]
    D --> E["Build ProductDto"]
    E --> F{"unless is true?"}
    F -->|No| G["Store entry"]
    F -->|Yes| H["Return without caching"]
    G --> I["Return result"]
    H --> I
```

## Hit path

```mermaid
flowchart LR
    A["Call findById"] --> B["Calculate key"]
    B --> C["Cache hit"]
    C --> D["Return cached ProductDto"]
    D --> E["Repository is not invoked"]
```

> [!warning]
> Side effects, audit inserts и metrics внутри cached target method выполняются только на misses.

# 4. `condition` и `unless`

```mermaid
flowchart TD
    A["Method invocation"] --> B{"condition before invocation"}
    B -->|false| C["Execute method without cache operation"]
    B -->|true| D["Check cache"]
    D -->|hit| E["Return cached value"]
    D -->|miss| F["Execute target"]
    F --> G{"unless after invocation"}
    G -->|true| H["Return result, do not store"]
    G -->|false| I["Store and return"]
```

## Пример

```java
@Cacheable(
        cacheNames = "search",
        key = "#request.cacheKey()",
        condition = "#request.cacheable",
        unless = "#result == null || #result.items.isEmpty()"
)
public SearchResult search(SearchRequest request) {
    return gateway.search(request);
}
```

- `condition` не может использовать `#result`;
- `unless` может проверять result;
- `condition=false` не означает «не выполнять method»;
- `unless=true` не означает «вернуть null».

# 5. Cache key — модель identity и isolation

## Ошибочный key

```java
@Cacheable(cacheNames = "customer", key = "#customerId")
public CustomerDto find(String tenantId, Long customerId) {
    return repository.find(tenantId, customerId);
}
```

```mermaid
sequenceDiagram
    participant TA as Tenant A
    participant C as Shared cache
    participant TB as Tenant B

    TA->>C: get key 42
    C-->>TA: miss
    TA->>C: put key 42 = Tenant A customer
    TB->>C: get key 42
    C-->>TB: Tenant A customer
```

## Правильная identity

```text
customer:v3:{tenantId}:{customerId}
```

```mermaid
flowchart LR
    Input["Method inputs"] --> Tenant["Tenant boundary"]
    Input --> Entity["Entity identity"]
    Input --> Variant["Locale, currency, permissions or query variant"]
    Tenant --> Key["Stable cache key"]
    Entity --> Key
    Variant --> Key
```

## Проверочный список key design

- включает ли key tenant/environment boundary;
- зависит ли result от locale, currency, role или feature flag;
- стабилен ли `toString()` аргумента;
- не содержит ли mutable object;
- одинаково ли формируется key на всех nodes и versions;
- нужна ли schema generation в prefix.

# 6. `@CachePut` и `@CacheEvict`

## `@CachePut`

```mermaid
sequenceDiagram
    participant C as Caller
    participant P as Cache proxy
    participant T as Update method
    participant DB as Database
    participant Cache as Cache

    C->>P: updateProduct(command)
    P->>T: always invoke
    T->>DB: update row
    DB-->>T: saved entity
    T-->>P: ProductDto
    P->>Cache: put(result.id, result)
    P-->>C: ProductDto
```

## `@CacheEvict` after invocation

```mermaid
flowchart TD
    A["Invoke delete method"] --> B{"Method succeeds?"}
    B -->|Yes| C["Evict entry"]
    B -->|No| D["Do not evict by default"]
```

## `beforeInvocation=true`

```mermaid
flowchart TD
    A["Call method"] --> B["Evict first"]
    B --> C["Invoke target"]
    C --> D{"Target fails?"}
    D -->|Yes| E["Entry is already gone"]
    D -->|No| F["Operation completes"]
```

# 7. Transaction и cache timing

## Опасный порядок: cache обновился до commit

```mermaid
sequenceDiagram
    participant C as Caller
    participant TX as Transaction
    participant DB as Database
    participant Cache as Cache

    C->>TX: update product
    TX->>DB: UPDATE row
    TX->>Cache: put new value
    TX->>TX: commit
    TX-->>C: commit fails
    Note over DB,Cache: DB rolls back, cache may contain value that never committed
```

## Transaction-aware timing

```mermaid
sequenceDiagram
    participant C as Caller
    participant TX as Transaction
    participant DB as Database
    participant Sync as TransactionSynchronization
    participant Cache as Cache

    C->>TX: update product
    TX->>DB: UPDATE row
    TX->>Sync: register deferred cache put or evict
    TX->>TX: commit
    alt commit succeeds
        TX->>Sync: afterCommit
        Sync->>Cache: apply cache operation
    else commit fails
        TX->>Sync: rollback completion
        Note right of Cache: no cache update
    end
```

> [!important]
> Transaction-aware cache timing уменьшает окно inconsistency, но не создаёт XA atomicity между database и Redis.

# 8. Caffeine: локальная topology

```mermaid
flowchart TB
    LB["Load balancer"] --> A["Node A"]
    LB --> B["Node B"]
    LB --> C["Node C"]

    A --> CA["Caffeine A"]
    B --> CB["Caffeine B"]
    C --> CC["Caffeine C"]

    A --> DB["Database"]
    B --> DB
    C --> DB
```

Каждая JVM имеет отдельный state:

```text
Node A cache key 42 = version 7
Node B cache key 42 = version 6
Node C cache key 42 = absent
```

## Почему это важно

- update на node A не инвалидирует B и C;
- restart очищает local cache;
- deployment создаёт cold nodes;
- sticky sessions могут маскировать inconsistency;
- hit ratio каждого node отличается.

# 9. Caffeine eviction и expiration

```mermaid
flowchart TD
    Entry["Cache entry"] --> Size{"maximumSize or maximumWeight exceeded?"}
    Entry --> WriteTTL{"expireAfterWrite reached?"}
    Entry --> AccessTTL{"expireAfterAccess reached?"}
    Size -->|Yes| Evict["Evict candidate"]
    WriteTTL -->|Yes| Expire["Expire entry"]
    AccessTTL -->|Yes| Expire
```

## `maximumSize` против `maximumWeight`

```text
maximumSize   → ограничивает количество entries
maximumWeight → ограничивает сумму custom weights
```

Большой PDF preview и маленький status DTO не должны обязательно считаться одинаково.

# 10. Redis: shared topology

```mermaid
flowchart TB
    LB["Load balancer"] --> A["Node A"]
    LB --> B["Node B"]
    LB --> C["Node C"]

    A --> R["Redis shared cache"]
    B --> R
    C --> R
    A --> DB["Database"]
    B --> DB
    C --> DB
```

Shared state решает проблему независимых local entries, но добавляет:

- network latency;
- serialization boundary;
- Redis availability dependency;
- hot keys;
- connection-pool limits;
- cross-version compatibility.

# 11. Serialization как wire contract

```mermaid
sequenceDiagram
    participant V1 as Application version 1
    participant R as Redis
    participant V2 as Application version 2

    V1->>R: write ProductCacheValue v1
    V2->>R: read same bytes
    alt compatible schema
        R-->>V2: decoded value
    else incompatible class or field contract
        R-->>V2: deserialization failure
    end
```

## Практический envelope

```java
final class ProductCacheValue {
    private int schemaVersion;
    private long id;
    private String name;
    private long sourceVersion;
}
```

## Versioned keyspace

```text
catalog:prod:product:v3::42
```

При несовместимом rollout можно сменить generation и прогреть новый keyspace без чтения старого format.

# 12. TTL не заменяет invalidation

```mermaid
flowchart LR
    Update["Business update"] --> Stale["Old cache entry remains"]
    Stale --> Window["Staleness window until TTL"]
    Window --> Expire["Entry expires"]
    Expire --> Reload["Next request reloads"]
```

TTL отвечает на вопрос:

```text
Как долго entry может жить без явного изменения?
```

Invalidation отвечает:

```text
Что делать, когда business state уже изменился?
```

Для balance, permissions и limits допустимое staleness window может быть близко к нулю.

# 13. Cache stampede

```mermaid
sequenceDiagram
    participant R1 as Request 1
    participant R2 as Request 2
    participant R3 as Request 3
    participant C as Cache
    participant DB as Database

    R1->>C: get hotKey
    R2->>C: get hotKey
    R3->>C: get hotKey
    C-->>R1: miss
    C-->>R2: miss
    C-->>R3: miss
    R1->>DB: load hotKey
    R2->>DB: load hotKey
    R3->>DB: load hotKey
```

## Защита

```mermaid
flowchart TD
    Miss["Hot key miss"] --> Single["Single-flight per key"]
    Miss --> Jitter["TTL jitter"]
    Miss --> Refresh["Refresh ahead"]
    Miss --> Stale["Stale-while-revalidate"]
    Miss --> Lock["Distributed lock when justified"]
    Miss --> Limit["Rate limit or bulkhead"]
```

# 14. `sync=true`: граница локальной координации

```mermaid
flowchart TB
    A1["Node A request 1"] --> CA["Caffeine A single-flight"]
    A2["Node A request 2"] --> CA
    B1["Node B request 1"] --> CB["Caffeine B single-flight"]
    B2["Node B request 2"] --> CB
    CA --> DB["Backend"]
    CB --> DB
```

`sync=true` может объединить concurrent loads внутри конкретного provider/cache instance, но два nodes всё ещё способны одновременно загрузить один key.

# 15. L1 Caffeine + L2 Redis

## Read path

```mermaid
sequenceDiagram
    participant C as Caller
    participant L1 as Caffeine L1
    participant L2 as Redis L2
    participant DB as Database

    C->>L1: get key
    alt L1 hit
        L1-->>C: value
    else L1 miss
        L1->>L2: get key
        alt L2 hit
            L2-->>L1: value
            L1->>L1: populate
            L1-->>C: value
        else L2 miss
            L2->>DB: load
            DB-->>L2: value
            L2->>L2: store
            L2-->>L1: value
            L1->>L1: store
            L1-->>C: value
        end
    end
```

## Update и invalidation

```mermaid
sequenceDiagram
    participant U as Update service
    participant DB as Database
    participant L2 as Redis
    participant Bus as Invalidation channel
    participant A as Node A L1
    participant B as Node B L1
    participant C as Node C L1

    U->>DB: commit new version
    U->>L2: evict or update key
    U->>Bus: publish invalidation event
    Bus->>A: evict key
    Bus->>B: evict key
    Bus->>C: evict key
```

## Основной риск

Redis entry можно обновить, но remote L1 caches продолжат отдавать старые values, пока не истечёт L1 TTL или не придёт invalidation event.

# 16. Почему список cacheNames не создаёт L1/L2 автоматически

```java
@Cacheable(cacheNames = {"productsL1", "productsL2"}, key = "#id")
public ProductDto find(Long id) {
    return repository.find(id);
}
```

Эта запись не определяет полноценный multi-tier protocol:

- порядок lookup;
- promotion L2 → L1;
- partial hit behavior;
- invalidation всех L1 nodes;
- разные TTL;
- failure policy;
- version comparison.

Multi-level cache требует явного orchestration либо специализированного composite implementation.

# 17. Redis outage как каскадный failure

```mermaid
flowchart TD
    R["Redis outage"] --> B["All nodes bypass cache"]
    B --> D["Database traffic spikes"]
    D --> P["DB pool saturation"]
    P --> T["Request latency and timeouts"]
    T --> Retry["Clients retry"]
    Retry --> D
```

## Failure policy должна ответить

- fail-open или fail-closed;
- можно ли вернуть stale value;
- сколько direct DB traffic выдержит система;
- нужен ли rate limit;
- какие operations critical;
- как отключить cache retries;
- как контролировать connection storms после recovery.

# 18. Negative caching

```mermaid
flowchart TD
    A["Lookup entity 999"] --> B{"Found?"}
    B -->|No| C["Store short-lived NOT_FOUND marker"]
    C --> D["Repeated requests avoid DB"]
    D --> E["Short TTL expires"]
```

Negative caching полезен против repeated misses, но требует осторожности:

- object может быть создан сразу после miss;
- слишком длинный TTL скрывает новое состояние;
- authorization failure нельзя путать с absence;
- null serialization policy должна быть явной.

# 19. Metrics и observability

```mermaid
flowchart LR
    Metrics["Cache metrics"] --> Hit["hit rate"]
    Metrics --> Miss["miss rate"]
    Metrics --> Load["load latency"]
    Metrics --> Evict["eviction count"]
    Metrics --> Size["estimated size"]
    Metrics --> Errors["provider errors"]
    Metrics --> Stale["stale-version detection"]
```

Высокий hit rate сам по себе не доказывает корректность. Cache может быстро возвращать устаревшие или tenant-wrong values.

# 20. Диагностическое дерево

```mermaid
flowchart TD
    A["Cache seems ineffective or incorrect"] --> B{"Proxy crossing exists?"}
    B -->|No| B1["Check self-invocation and manual new"]
    B -->|Yes| C{"Correct CacheManager selected?"}
    C -->|No| C1["Check qualifiers, primary and resolver"]
    C -->|Yes| D{"Expected cache name exists?"}
    D -->|No| D1["Check configuration and dynamic creation"]
    D -->|Yes| E{"Calculated key stable and complete?"}
    E -->|No| E1["Fix identity, tenant and variants"]
    E -->|Yes| F{"Entry expires or is evicted too early?"}
    F -->|Yes| F1["Inspect TTL, size, weight and invalidations"]
    F -->|No| G{"Provider errors or topology mismatch?"}
    G -->|Yes| G1["Inspect Redis connectivity or local-node isolation"]
    G -->|No| H["Inspect hit, miss, load and stale-version metrics"]
```

# 21. Полный production case: product catalogue

## Требования

- 20 application nodes;
- product reads — 50 000 RPS;
- updates — 20 RPS;
- допустимая staleness — 5 seconds;
- Redis доступен, но возможны короткие outages;
- product DTO меняется при deployments.

## Возможная topology

```mermaid
flowchart TB
    Client["Clients"] --> LB["Load balancer"]
    LB --> A["Node A + Caffeine L1"]
    LB --> B["Node B + Caffeine L1"]
    LB --> N["Node N + Caffeine L1"]
    A --> R["Redis L2"]
    B --> R
    N --> R
    R --> DB["PostgreSQL"]
    Update["Product update service"] --> DB
    Update --> R
    Update --> Bus["Invalidation topic"]
    Bus --> A
    Bus --> B
    Bus --> N
```

## Политика

```text
L1 TTL            3 seconds
L2 TTL           10 minutes + jitter
Key generation    catalog:prod:product:v4:{tenant}:{id}
Value             ProductCacheValue schemaVersion=4
Update             DB commit → L2 evict → invalidation event
Redis outage       bounded DB fallback + rate limit + stale L1 where allowed
```

## Что проверять в production

- L1 hit ratio по node;
- Redis hit ratio;
- DB load during Redis outage;
- invalidation delivery lag;
- stale sourceVersion count;
- deserialization errors during rollout;
- hot-key load latency;
- cache key cardinality.

# 22. Как объяснять caching на собеседовании

```text
1. Spring Cache — proxy abstraction, provider — storage semantics.
2. @Cacheable на hit пропускает target method.
3. Key определяет identity и isolation.
4. Caffeine локален одной JVM; Redis разделяется между nodes.
5. TTL ограничивает возраст, но не заменяет invalidation.
6. sync=true не является distributed lock по умолчанию.
7. L1/L2 требует явного promotion и invalidation protocol.
8. Cache outage может перегрузить database.
9. Serialization — межверсионный wire contract.
10. Диагностика начинается с proxy, manager, cache, key, policy и metrics.
```

# 23. Практические упражнения

1. Доказать self-invocation для `@Cacheable` счётчиком repository calls.
2. Создать tenant collision с key только по `customerId`.
3. Запустить два JVM с Caffeine и показать независимые values.
4. Изменить Redis DTO между двумя versions и воспроизвести deserialization problem.
5. Запустить 20 threads на expired hot key и измерить stampede.
6. Сравнить `expireAfterWrite` и `expireAfterAccess`.
7. Смоделировать Redis outage и измерить рост DB load.
8. Реализовать L1 invalidation event и проверить три local nodes.

## Related materials

- [[Spring Cache with Caffeine and Redis]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CACHE-B01/CACHE-B01 Cards]]
- [[50_LABS/Spring/CACHE-B01/README]]
- [[40_PRODUCTION_CASES/Spring/AOP and Cache Production Cases]]
- [[Spring AOP Visual Deep Dive]]
