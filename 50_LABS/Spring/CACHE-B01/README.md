---
type: lab
domain: spring
subdomain: caching
status: active
spring_version: 5.3.39
spring_data_redis_version: 2.7.18
caffeine_version: 2.9.3
java_version: 8
tags:
  - spring
  - cache
  - caffeine
  - redis
  - lab
---

# Spring CACHE-B01 Lab

## Цель

На одном `ProductCatalog` увидеть разницу между:

- Spring Cache abstraction;
- Caffeine local cache;
- Redis shared cache;
- external proxy call и self-invocation;
- `@Cacheable`, `@CachePut`, `@CacheEvict`;
- local statistics;
- Redis TTL, prefix и JSON serialization.

## Версии

```text
Java                  8
Spring Framework      5.3.39
Spring Data Redis     2.7.18
Caffeine              2.9.3
Lettuce                6.2.7.RELEASE
```

Caffeine 2.9.3 выбрана намеренно: lab сохраняет Java 8 baseline.

---

# 1. Caffeine mode

Не требует внешних сервисов.

```bash
cd 50_LABS/Spring/CACHE-B01
mvn clean compile exec:java
```

## Experiment 1. `@Cacheable` hit

```java
ProductDto first = catalog.find(1L);
ProductDto second = catalog.find(1L);
```

Ожидаемо:

```text
repository load for product 1
repository loads after two external reads = 1
```

Первый вызов — miss, второй — hit.

## Experiment 2. Self-invocation

```java
public void findTwiceInternally(Long id) {
    find(id);
    find(id);
}
```

Ожидаемо:

```text
repository loads caused by internal pair = 2
```

Оба вызова выполняются внутри target и обходят cache proxy.

## Experiment 3. `@CachePut`

```java
@CachePut(cacheNames = "productById", key = "#result.id")
public ProductDto update(ProductDto product)
```

После update следующий `find(1L)` должен вернуть новое cached value без repository load.

## Experiment 4. `@CacheEvict`

```java
@CacheEvict(cacheNames = "productById", key = "#id")
public void evict(Long id)
```

После eviction следующий read снова вызывает repository.

## Experiment 5. Caffeine statistics

Lab выводит:

```text
estimated size
hit count
miss count
hit rate
eviction count
```

Configuration:

```java
Caffeine.newBuilder()
        .maximumSize(1_000)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .recordStats()
```

---

# 2. Redis mode

## Поднять Redis

```bash
docker compose up -d redis
```

Проверить:

```bash
docker compose ps
redis-cli -h localhost -p 6379 ping
```

Ожидается:

```text
PONG
```

## Запустить Redis experiment

Linux/macOS:

```bash
RUN_REDIS=true mvn clean compile exec:java
```

PowerShell:

```powershell
$env:RUN_REDIS="true"
mvn clean compile exec:java
```

## Redis configuration

```text
host                 localhost
port                 6379
cache name           productById
TTL                  5 minutes
prefix               lab:v1:productById::
key serializer       StringRedisSerializer
value serializer     GenericJackson2JsonRedisSerializer
null caching         disabled
transaction aware    enabled
```

## Expected read behavior

```text
first find(42)  → repository load + Redis put
second find(42) → Redis hit
repository loads = 1
```

## Посмотреть keys

```bash
docker compose exec redis redis-cli KEYS 'lab:v1:*'
```

Для production large keyspace не использовать `KEYS` как обычный operational scan. Здесь keyspace учебный и маленький.

## Посмотреть TTL

Сначала получить key:

```bash
docker compose exec redis redis-cli KEYS 'lab:v1:productById::*'
```

Затем:

```bash
docker compose exec redis redis-cli TTL '<key>'
```

## Очистить lab data

```bash
docker compose exec redis redis-cli FLUSHDB
```

Только для локального отдельного Redis lab.

---

# 3. Реальные упражнения

## Exercise A. Tenant-safe key

Изменить service method:

```java
find(String tenantId, Long id)
```

и key:

```java
key = "#tenantId + ':' + #id"
```

Проверить одинаковый product ID в двух tenants.

## Exercise B. Negative caching

Вернуть `null` для unknown ID и сравнить:

- `unless = "#result == null"`;
- разрешённое null caching;
- explicit `NotFound` marker;
- короткий negative TTL.

## Exercise C. Cache stampede

1. Добавить `Thread.sleep(500)` в repository.
2. Запустить 20 threads с одним ID.
3. Сравнить `sync=true` и `sync=false`.
4. Повторить на двух JVM и увидеть, что local coordination не является distributed single-flight.

## Exercise D. Caffeine eviction

Изменить:

```java
maximumSize(2)
```

Загрузить IDs `1`, `2`, `42`, затем посмотреть eviction count.

## Exercise E. Redis serialization migration

1. Записать `ProductDto` v1.
2. Изменить field type/name.
3. Запустить новую версию.
4. Спроектировать versioned prefix `lab:v2:`.

## Exercise F. Redis failure

1. Запустить Redis mode.
2. Остановить Redis во время работы.
3. Наблюдать exception.
4. Спроектировать policy:
   - fail request;
   - bypass to repository;
   - serve stale local value;
   - circuit breaker.

## Exercise G. L1 Caffeine + L2 Redis

Реализовать sequence:

```text
L1 → L2 → repository
```

Затем обязательно добавить cross-node invalidation experiment. Простое двойное `put` не решает consistency.

---

# 4. Production checks

Перед внедрением кеша ответить:

1. Source of truth?
2. Cache identity/key?
3. Tenant/environment isolation?
4. Positive и negative TTL?
5. Update — put или evict?
6. Что происходит при cache outage?
7. Как предотвращается stampede?
8. Каков serialization contract?
9. Как инвалидируется Caffeine на других nodes?
10. Какие metrics докажут пользу?

---

# 5. Ограничения лаборатории

- Compose использует один standalone Redis.
- Нет Redis Cluster/Sentinel.
- Нет real database transaction.
- `transactionAware()` не демонстрирует distributed atomicity.
- Нет production-grade L1/L2 invalidation.
- Docker image tag удобен для lab; production deployment должен pin-ить проверенную версию.

## Related

- [[10_CONCEPTS/Spring/Cache/Spring Cache with Caffeine and Redis]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CACHE-B01/CACHE-B01 Cards]]
- [[40_PRODUCTION_CASES/Spring/AOP and Cache Production Cases]]
- [[98_SOURCES/Spring AOP and Cache Sources]]
