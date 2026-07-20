---
type: sources
domain: spring
subdomain:
  - aop
  - caching
status: active
spring_versions:
  - 5.3.39
  - current
spring_data_redis_versions:
  - 2.7.18
  - current
java_versions:
  - 8
  - 21
tags:
  - spring
  - aop
  - caching
  - caffeine
  - redis
  - sources
---

# Spring AOP and Cache Sources

## Spring AOP

1. Spring Framework 5.3 Core reference — AOP concepts, `@AspectJ` support, advice, pointcuts, proxying mechanisms and self-invocation  
   https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/core.html#aop

2. Current Spring Framework reference — proxying mechanisms  
   https://docs.spring.io/spring-framework/reference/core/aop/proxying.html

3. Spring Framework 5.3 `EnableAspectJAutoProxy` API  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/context/annotation/EnableAspectJAutoProxy.html

4. Spring Framework 5.3 `AopContext` API  
   https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/aop/framework/AopContext.html

5. Current Spring Framework reference — programmatic proxies with `ProxyFactory`  
   https://docs.spring.io/spring-framework/reference/core/aop-api/prog.html

## Spring Cache abstraction

6. Spring Framework cache abstraction  
   https://docs.spring.io/spring-framework/reference/integration/cache.html

7. Annotation-driven caching — `@Cacheable`, `@CachePut`, `@CacheEvict`, conditions, keys and synchronized loading  
   https://docs.spring.io/spring-framework/reference/integration/cache/annotations.html

8. Spring Framework 5.3 integration reference — proxy mode and self-invocation limitation for caching  
   https://docs.spring.io/spring-framework/docs/5.3.39/reference/html/integration.html#cache

9. Spring Framework 5.3 `CacheManager` API  
   https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/cache/CacheManager.html

## Caffeine

10. Caffeine official repository  
    https://github.com/ben-manes/caffeine

11. Caffeine official wiki — eviction, expiration, refresh, statistics and listeners  
    https://github.com/ben-manes/caffeine/wiki

12. Spring `CaffeineCacheManager` API  
    https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/cache/caffeine/CaffeineCacheManager.html

## Redis cache

13. Spring Data Redis 2.7.18 cache package  
    https://docs.spring.io/spring-data-redis/docs/2.7.18/api/org/springframework/data/redis/cache/package-summary.html

14. Spring Data Redis 2.7.18 `RedisCacheManager` API  
    https://docs.spring.io/spring-data-redis/docs/2.7.18/api/org/springframework/data/redis/cache/RedisCacheManager.html

15. Spring Data Redis 2.7.18 `RedisCacheConfiguration` API  
    https://docs.spring.io/spring-data-redis/docs/2.7.18/api/org/springframework/data/redis/cache/RedisCacheConfiguration.html

16. Current Redis Cache reference — TTL, prefixes, serializers, writer locking and batch strategies  
    https://docs.spring.io/spring-data/redis/reference/redis/redis-cache.html

17. Spring Data Redis serializer package  
    https://docs.spring.io/spring-data/redis/reference/api/java/org/springframework/data/redis/serializer/package-summary.html

## Version policy

- Certification explanations target Spring Framework `5.3.39` semantics unless explicitly marked otherwise.
- Current documentation is used for concepts that remain stable and for identifying newer capabilities.
- Caffeine `2.9.3` is used in the Java 8 lab. Caffeine 3.x targets newer Java baselines and is not substituted silently.
- Spring Data Redis `2.7.18` is used in the Java 8 lab to remain aligned with the Spring 5.3 generation.
- Redis TTL, serialization, writer and batch settings must be verified against the exact Spring Data Redis version deployed.
- A cache abstraction guarantee is not automatically a distributed consistency guarantee.
