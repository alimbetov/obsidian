---
type: sources
domain: spring
subdomain: aop-cache
status: active
tags:
  - spring
  - aop
  - cache
  - redis
  - caffeine
---

# Spring AOP, Cache, Redis and Caffeine Sources

## Spring Framework — AOP

- Spring Framework Reference — AOP concepts.
  - https://docs.spring.io/spring-framework/reference/core/aop/introduction-defn.html
- Spring Framework Reference — proxying mechanisms.
  - https://docs.spring.io/spring-framework/reference/core/aop/proxying.html
- Spring Framework Reference — declaring aspects and pointcuts.
  - https://docs.spring.io/spring-framework/reference/core/aop/ataspectj.html
- `AopUtils` API.
  - https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/aop/support/AopUtils.html
- `Advised` API.
  - https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/aop/framework/Advised.html

## Spring Framework — Cache abstraction

- Cache abstraction overview.
  - https://docs.spring.io/spring-framework/reference/integration/cache.html
- Declarative annotation-based caching.
  - https://docs.spring.io/spring-framework/reference/integration/cache/annotations.html
- `@Cacheable` API.
  - https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/cache/annotation/Cacheable.html
- `CacheManager` API.
  - https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/cache/CacheManager.html
- `CaffeineCacheManager` API.
  - https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/cache/caffeine/CaffeineCacheManager.html

## Spring Data Redis

- Redis Cache reference.
  - https://docs.spring.io/spring-data/redis/reference/redis/redis-cache.html
- `RedisCacheManager` API.
  - https://docs.spring.io/spring-data/data-redis/docs/current/api/org/springframework/data/redis/cache/RedisCacheManager.html
- `RedisCacheConfiguration` API.
  - https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/cache/RedisCacheConfiguration.html
- Redis serialization reference.
  - https://docs.spring.io/spring-data/redis/reference/redis/template.html

## Caffeine

- Official project repository.
  - https://github.com/ben-manes/caffeine
- Official user guide.
  - https://github.com/ben-manes/caffeine/wiki
- Eviction.
  - https://github.com/ben-manes/caffeine/wiki/Eviction
- Refresh.
  - https://github.com/ben-manes/caffeine/wiki/Refresh
- Statistics.
  - https://github.com/ben-manes/caffeine/wiki/Statistics

## Contract rules used in the vault

1. Spring AOP material distinguishes public proxy contracts from version-sensitive internals.
2. Self-invocation claims are grounded in Spring proxy-mode behavior.
3. Cache annotations are described independently of storage provider.
4. Redis defaults and TTL/serialization behavior are version-sensitive and must identify the Spring Data Redis line.
5. Caffeine is treated as a per-JVM in-memory cache, not a distributed cache.
6. Two-level caching is presented as an explicit architecture, not as an automatic consequence of listing multiple cache names.
7. Experiments demonstrate runtime symptoms; official documentation defines the contract.
