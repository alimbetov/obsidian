---
type: moc
domain: spring
subdomain: aop-cache
status: active
tags:
  - spring
  - aop
  - cache
  - route
---

# Spring AOP, Proxies and Cache Route

> [!summary]
> Интеграционная точка входа. Каноническая proxy-механика хранится в [[10_CONCEPTS/Spring/AOP/Spring AOP Proxy Mechanics]], а cache semantics и provider architecture — в [[10_CONCEPTS/Spring/Cache/Spring Cache with Caffeine and Redis]].

## Главная причинная цепочка

```text
external caller
    ↓
Spring proxy
    ↓
advisor / interceptor chain
    ↓
target method
```

`@Transactional`, `@Async`, method security и Spring Cache используют эту общую proxy-модель. Поэтому self-invocation, объект, созданный через `new`, private/final boundaries и неверный runtime proxy являются общими диагностическими точками.

## Маршрут изучения

1. [[10_CONCEPTS/Spring/AOP/Spring AOP Proxy Mechanics]]
2. [[30_CERTIFICATIONS/Spring/2V0-72.22/AOP-B01/AOP-B01 Cards]]
3. [[50_LABS/Spring/AOP-B01/README]]
4. [[10_CONCEPTS/Spring/Cache/Spring Cache with Caffeine and Redis]]
5. [[30_CERTIFICATIONS/Spring/2V0-72.22/CACHE-B01/CACHE-B01 Cards]]
6. [[50_LABS/Spring/CACHE-B01/README]]
7. [[40_PRODUCTION_CASES/Spring/AOP and Cache Production Cases]]
8. [[01_MAPS/Spring AOP and Caching Map.canvas]]

## Что доказать практикой

### Proxy mechanics

- JDK proxy публикует interface contract;
- CGLIB создаёт subclass proxy;
- self-invocation не входит во второй interceptor chain;
- advisor order меняет transaction/security/cache semantics;
- `AopUtils` и `Advised` показывают фактическую runtime-конфигурацию.

### Cache abstraction

- `@Cacheable` пропускает target на hit;
- `@CachePut` всегда выполняет method;
- `@CacheEvict` управляет invalidation;
- cache key является частью security и data identity;
- `sync=true` ограничен provider scope.

### Caffeine

- cache локален одному JVM;
- maximum size/weight ограничивает memory;
- expiration и refresh имеют разные semantics;
- statistics доказывают или опровергают пользу cache;
- cross-node invalidation отсутствует без отдельного протокола.

### Redis

- cache общий для нескольких nodes;
- TTL, prefix и serializers должны задаваться явно;
- value является distributed serialization contract;
- transaction-aware cache manager не создаёт XA atomicity с database;
- outage Redis может перегрузить source of truth.

### L1 + L2

```text
Caffeine L1
    ↓ miss
Redis L2
    ↓ miss
Database / remote API
```

Нужно явно спроектировать:

- promotion L2 → L1;
- write/eviction order;
- cross-node L1 invalidation;
- stale window;
- Redis failure policy;
- cache stampede protection;
- metrics.

## Быстрый production checklist

1. Вызов пересекает proxy?
2. Какой proxy type создан?
3. Какие advisors установлены и в каком порядке?
4. Cache key включает tenant, locale, version и остальные semantic inputs?
5. Cache local или shared?
6. Каков TTL и почему именно такой?
7. Когда происходит invalidation относительно database commit?
8. Что происходит при Redis outage?
9. Как защищён source of truth от stampede?
10. Какие метрики показывают hits, misses, loads, evictions и stale data?

## Sources

- [[98_SOURCES/Spring AOP and Cache Sources]]
- [[98_SOURCES/Spring AOP Cache Redis and Caffeine Sources]]
