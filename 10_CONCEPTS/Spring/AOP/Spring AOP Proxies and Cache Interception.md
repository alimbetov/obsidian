---
type: concept
domain: spring
subdomain: aop
status: learning
confidence: 0
spring_versions:
  - 5.3.39
java_versions:
  - 8
related:
  - "[[01_MAPS/Spring AOP and Cache Map.canvas]]"
  - "[[30_CERTIFICATIONS/Spring/2V0-72.22/AOP-B01/AOP-B01 Cards]]"
  - "[[40_PRODUCTION_CASES/Spring/AOP and Cache Production Cases]]"
  - "[[50_LABS/Spring/AOP-B01/README]]"
tags:
  - spring
  - aop
  - proxy
  - caching
  - redis
  - caffeine
---

# Spring AOP, Proxies and Cache Interception

> [!summary] За 30 секунд
> Spring AOP обычно работает через proxy. Вызов должен пересечь proxy boundary, чтобы advice выполнился. JDK proxy реализует interfaces, CGLIB создаёт subclass. Self-invocation, private methods и часть final-method scenarios обходят interception. `@Transactional`, `@Async`, `@Cacheable` и method security используют ту же базовую модель: external call → proxy → interceptor chain → target. Caffeine хранит данные локально в JVM; Redis хранит их вне процесса и разделяется между instances.

## 1. Базовая модель

```text
caller
  ↓
proxy
  ↓
advisor matching
  ↓
interceptor chain
  ↓
target method
  ↓
interceptors unwind
  ↓
caller
```

Главная проверка при любой проблеме Spring AOP:

```text
Did the invocation cross the proxy?
```

Если ответ `нет`, annotation может быть корректной, bean может быть Spring-managed, но advice всё равно не выполнится.

## 2. Термины без путаницы

| Термин | Практический смысл |
|---|---|
| Join point | точка выполнения; в Spring AOP обычно method execution |
| Pointcut | правило выбора методов |
| Advice | код, выполняемый до/после/вокруг target method |
| Advisor | pointcut + advice |
| Interceptor | runtime-реализация around behavior |
| Proxy | объект, который получает внешний вызов и запускает chain |
| Target | исходный business object |

## 3. Реальный пример: аудит оплаты

```java
public interface PaymentService {
    Receipt pay(PaymentCommand command);
}

@Service
class DefaultPaymentService implements PaymentService {
    @Override
    public Receipt pay(PaymentCommand command) {
        return gateway.charge(command);
    }
}
```

Aspect:

```java
@Aspect
@Component
class PaymentAuditAspect {

    @Around("execution(* com.acme.payment.PaymentService.pay(..))")
    public Object audit(ProceedingJoinPoint pjp) throws Throwable {
        long started = System.nanoTime();
        try {
            Object result = pjp.proceed();
            auditRepository.success(pjp.getArgs(), System.nanoTime() - started);
            return result;
        } catch (Throwable ex) {
            auditRepository.failure(pjp.getArgs(), ex);
            throw ex;
        }
    }
}
```

Runtime:

```text
REST controller
  ↓
PaymentService proxy
  ↓
PaymentAuditAspect
  ↓
DefaultPaymentService.pay
  ↓
Gateway
```

## 4. JDK dynamic proxy

JDK proxy используется вокруг interfaces.

```text
PaymentService interface
        ↑
JDK proxy implements interface
        ↓
DefaultPaymentService target
```

Проверка:

```java
Object bean = context.getBean(PaymentService.class);
System.out.println(Proxy.isProxyClass(bean.getClass()));
System.out.println(AopUtils.isJdkDynamicProxy(bean));
```

### Ограничение типа

```java
PaymentService service = context.getBean(PaymentService.class); // OK
DefaultPaymentService impl = context.getBean(DefaultPaymentService.class); // may fail
```

JDK proxy не является subclass concrete implementation.

## 5. CGLIB proxy

CGLIB создаёт subclass target class.

```text
DefaultPaymentService
        ↑
Generated subclass proxy
```

Проверка:

```java
AopUtils.isCglibProxy(bean)
```

CGLIB полезен, когда interface отсутствует или нужен proxy по class type. Но subclass proxy не может корректно override final methods, а private methods вообще не участвуют в polymorphic interception.

## 6. Proxy selection

Практическая модель:

```text
interfaces available + proxyTargetClass=false
    → usually JDK proxy

proxyTargetClass=true
    → class-based proxy

no useful interface
    → class-based proxy
```

Не строй архитектуру на предположении о конкретном generated class. Инъецируй business interface, где это естественно.

## 7. Self-invocation

```java
@Service
class OrderService {

    public void place(Order order) {
        reserve(order); // direct call on this
    }

    @Transactional
    public void reserve(Order order) {
        repository.save(order);
    }
}
```

Внутренний вызов:

```text
proxy.place()
  ↓
target.place()
  ↓
this.reserve()
  ↓
no second proxy crossing
  ↓
transaction advice not entered
```

### Исправление 1: выделить collaborator

```java
@Service
class ReservationService {
    @Transactional
    public void reserve(Order order) {
        repository.save(order);
    }
}
```

```java
@Service
class OrderService {
    private final ReservationService reservations;

    public void place(Order order) {
        reservations.reserve(order);
    }
}
```

### Исправление 2: orchestration boundary

Transaction boundary должен совпадать с business use case, а не с случайным internal method.

## 8. Private и final methods

### Private

```java
@Cacheable("users")
private User loadUser(long id) { ... }
```

Proxy не может получить внешний polymorphic call к private method. Annotation не создаёт магию.

### Final

Class-based proxy не может override final method.

```java
@Transactional
public final void settle() { ... }
```

Это плохая interception boundary.

### Практическое правило

```text
public externally invoked method
    → valid proxy boundary

private/internal/final-only method
    → suspicious boundary
```

## 9. Interceptor chain

Один method может одновременно иметь:

- security;
- transaction;
- caching;
- metrics;
- retry;
- custom audit.

```text
Security
  ↓
Metrics
  ↓
Transaction
  ↓
Cache
  ↓
Target
```

Порядок влияет на semantics.

### Пример

Если cache interceptor находится снаружи transaction interceptor:

```text
cache hit
  → target skipped
  → transaction not opened
```

Это обычно желаемо для read cache.

Если cache put выполняется до успешного commit, можно опубликовать данные, которые transaction позже откатит. Поэтому cache invalidation/update нужно связывать с transaction semantics осознанно.

## 10. `@Transactional` как AOP-кейс

```java
@Transactional
public Receipt transfer(TransferCommand command) {
    debit(command);
    credit(command);
    return receipt(command);
}
```

Runtime:

```text
proxy
  ↓
TransactionInterceptor
  ↓
begin
  ↓
target.transfer
  ↓
commit or rollback
```

### Частые production failures

- self-invocation;
- bean создан через `new`;
- method не public для используемого proxy mode;
- checked exception не соответствует rollback rule;
- transaction manager не тот;
- method выполняется в другом thread после `@Async` boundary.

## 11. `@Async` как AOP-кейс

```java
@Async("notificationExecutor")
public CompletableFuture<Void> sendReceipt(String email) {
    mailClient.send(email);
    return CompletableFuture.completedFuture(null);
}
```

```text
caller thread
  ↓
async proxy
  ↓
submit task
  ↓
worker thread
  ↓
target method
```

Self-invocation снова обходит proxy. Transaction context и ThreadLocal context автоматически не переносятся как единый контракт.

## 12. Spring Cache abstraction

Spring Cache abstraction отделяет annotation semantics от конкретного storage.

```text
@Cacheable
  ↓
CacheInterceptor
  ↓
CacheManager
  ↓
Cache implementation
  ├── Caffeine
  └── Redis
```

Основные annotations:

| Annotation | Поведение |
|---|---|
| `@Cacheable` | сначала lookup; при hit method пропускается |
| `@CachePut` | method всегда выполняется; результат записывается |
| `@CacheEvict` | удаляет одну entry или весь cache |
| `@Caching` | группирует несколько операций |
| `@CacheConfig` | общие параметры на class level |

## 13. Реальный пример: справочник продуктов

```java
@Service
@CacheConfig(cacheNames = "products")
class ProductCatalogService {

    @Cacheable(key = "#id", unless = "#result == null", sync = true)
    public ProductDto findById(long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    @CachePut(key = "#result.id")
    @Transactional
    public ProductDto update(UpdateProductCommand command) {
        Product product = repository.getReferenceById(command.id());
        product.rename(command.name());
        return mapper.toDto(product);
    }

    @CacheEvict(key = "#id")
    @Transactional
    public void delete(long id) {
        repository.deleteById(id);
    }
}
```

## 14. Cache key design

Плохой key:

```java
@Cacheable(cacheNames = "offers", key = "#clientId")
public Offers findOffers(long clientId, String segment, Locale locale)
```

Здесь result зависит не только от clientId.

Лучше:

```java
@Cacheable(
    cacheNames = "offers",
    key = "T(String).format('%s:%s:%s', #clientId, #segment, #locale.language)"
)
```

Или отдельный immutable key object/custom `KeyGenerator`.

### Key должен включать все параметры, влияющие на result

```text
same key
  → must mean same semantic result
```

## 15. `condition` и `unless`

```java
@Cacheable(
    cacheNames = "documents",
    key = "#id",
    condition = "#id > 0",
    unless = "#result == null || #result.sensitive"
)
```

- `condition` проверяется до method invocation;
- `unless` может использовать `#result` после invocation.

## 16. `sync=true` и cache stampede

Без синхронизации:

```text
100 requests miss same key
  ↓
100 database calls
```

С `sync=true` при поддержке provider:

```text
first caller computes
other callers wait
  ↓
one populated value
```

Это локальная защита в пределах semantics конкретного cache provider. Для нескольких JVM локальная Caffeine-синхронизация не координирует разные nodes.

## 17. Self-invocation в caching

```java
public ProductDto warm(long id) {
    return findById(id);
}

@Cacheable("products")
public ProductDto findById(long id) { ... }
```

`warm()` вызывает `findById()` напрямую на target, поэтому caching advice не выполняется.

Исправление — внешний collaborator или явный cache warming component, вызывающий proxied service.

## 18. Caffeine — локальный L1 cache

Caffeine находится внутри JVM процесса.

```text
instance A → its own Caffeine cache
instance B → its own Caffeine cache
instance C → its own Caffeine cache
```

Пример configuration:

```java
@Bean
CaffeineCacheManager caffeineCacheManager() {
    CaffeineCacheManager manager = new CaffeineCacheManager("products", "branches");
    manager.setCaffeine(
        Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .recordStats()
    );
    return manager;
}
```

### Сильные стороны

- минимальная latency;
- нет network hop;
- высокая throughput;
- size-based eviction;
- expiration/refresh;
- полезен для hot immutable/reference data.

### Ограничения

- cache не общий между nodes;
- invalidation нужно доставлять каждому instance;
- memory расходуется в каждом JVM;
- restart очищает cache;
- stale windows различаются между nodes.

## 19. Redis — распределённый L2 cache

```text
service instance A ─┐
service instance B ─┼→ Redis
service instance C ─┘
```

Пример:

```java
@Bean
RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
    RedisCacheConfiguration defaults = RedisCacheConfiguration
            .defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues()
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair
                    .fromSerializer(new StringRedisSerializer())
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair
                    .fromSerializer(new GenericJackson2JsonRedisSerializer())
            );

    return RedisCacheManager.builder(factory)
            .cacheDefaults(defaults)
            .withCacheConfiguration(
                "products",
                defaults.entryTtl(Duration.ofMinutes(5))
            )
            .transactionAware()
            .build();
}
```

### Сильные стороны

- общий cache для всех instances;
- survives application restart;
- central TTL/invalidation;
- подходит для shared read models и expensive remote results.

### Ограничения

- network latency;
- serialization compatibility;
- Redis availability становится частью request path;
- hot keys;
- memory eviction policy;
- cache key namespace/versioning;
- cluster/network operational complexity.

## 20. Serialization contract

Не сохраняй случайный JPA entity graph как долгоживущий Redis contract.

Плохо:

```java
@Cacheable("customers")
public CustomerEntity findCustomer(long id)
```

Лучше:

```java
@Cacheable("customers-v2")
public CustomerCacheValue findCustomer(long id)
```

```java
record CustomerCacheValue(
    long id,
    String name,
    String status,
    long version
) {}
```

Cache value — отдельный versioned data contract.

## 21. TTL design

TTL выбирается не по принципу «пусть будет 10 минут».

Учитывай:

- допустимый stale window;
- частоту изменения данных;
- стоимость reload;
- нагрузку при массовом expiry;
- regulatory/security sensitivity;
- negative caching;
- jitter.

```text
TTL = business staleness budget
      ∩ operational load budget
```

## 22. Eviction после записи

Cache-aside pattern:

```text
read:
cache → miss → database → cache put

write:
database commit → cache evict
```

Опасность:

```text
evict before commit
  ↓
concurrent read loads old DB value
  ↓
old value repopulates cache
  ↓
transaction commits new DB value
  ↓
cache remains stale
```

Поэтому invalidation часто нужно выполнять after successful commit.

Практические варианты:

- transaction-aware cache manager;
- transaction synchronization;
- domain event after commit;
- outbox/event-driven invalidation;
- versioned keys.

## 23. Caffeine + Redis two-level cache

```text
request
  ↓
L1 Caffeine
  ├── hit → return
  └── miss
        ↓
      L2 Redis
        ├── hit → populate L1 → return
        └── miss
              ↓
            DB/API
              ↓
            populate Redis
              ↓
            populate Caffeine
```

### Почему нельзя просто указать два cache names

```java
@Cacheable(cacheNames = {"productsL1", "productsL2"})
```

Это не полноценный read-through two-level algorithm. Поведение нескольких caches зависит от interceptor/cache implementation semantics и может не дать ожидаемого promotion/invalidation contract.

Лучше создать явный `TwoLevelCache`/`CacheResolver` или отдельный repository decorator.

## 24. Реальный TwoLevelCache

```java
final class TwoLevelProductCache {

    private final Cache<Long, ProductDto> local;
    private final RedisTemplate<String, ProductDto> redis;
    private final ProductRepository repository;

    ProductDto get(long id) {
        ProductDto l1 = local.getIfPresent(id);
        if (l1 != null) {
            return l1;
        }

        String key = "product:v2:" + id;
        ProductDto l2 = redis.opsForValue().get(key);
        if (l2 != null) {
            local.put(id, l2);
            return l2;
        }

        ProductDto loaded = repository.findDtoById(id);
        redis.opsForValue().set(key, loaded, Duration.ofMinutes(5));
        local.put(id, loaded);
        return loaded;
    }

    void evict(long id) {
        local.invalidate(id);
        redis.delete("product:v2:" + id);
    }
}
```

Production version также должна решить:

- concurrent loads;
- failures Redis;
- serialization;
- invalidation broadcast;
- metrics;
- negative caching;
- timeout budget.

## 25. L1 invalidation между nodes

Если node A обновил product:

```text
node A evicts local + Redis
node B local Caffeine may still contain old value
```

Варианты:

- short L1 TTL;
- Redis Pub/Sub invalidation;
- Kafka event;
- version check;
- no L1 for highly volatile data.

## 26. Cache penetration, breakdown, avalanche

### Penetration

Много запросов несуществующих keys постоянно доходят до DB.

Решения:

- negative caching;
- input validation;
- Bloom filter для специфичных workloads;
- rate limiting.

### Breakdown / hot-key stampede

Один hot key истёк, множество callers одновременно reload.

Решения:

- `sync=true` локально;
- distributed lock с осторожностью;
- refresh-ahead;
- stale-while-revalidate;
- request coalescing.

### Avalanche

Много keys истекают одновременно.

Решения:

- TTL jitter;
- staggered preload;
- capacity planning;
- graceful fallback.

## 27. Redis failure policy

Нужно решить заранее:

```text
Redis unavailable
  ↓
fail request?
load DB?
serve stale local value?
open circuit?
```

Cache обычно является optimization, но не всегда. Например, rate limit state или security session может быть correctness-critical.

Для read cache распространён policy:

```text
Redis timeout
  → record metric
  → fallback to source
  → avoid cascading retry storm
```

## 28. Metrics

Минимальный набор:

- hit/miss ratio;
- load latency;
- load failure count;
- eviction count;
- cache size/weight;
- Redis command latency;
- serialization errors;
- stale-serving count;
- invalidation lag;
- hot keys.

Caffeine может собирать local statistics через `recordStats()`. Для Redis нужна observability на уровне client, commands и application cache abstraction.

## 29. Proxy diagnostics

```java
Object bean = context.getBean(ProductCatalogService.class);

System.out.println(AopUtils.isAopProxy(bean));
System.out.println(AopUtils.isJdkDynamicProxy(bean));
System.out.println(AopUtils.isCglibProxy(bean));
System.out.println(AopUtils.getTargetClass(bean));
```

Если bean реализует `Advised`:

```java
Advised advised = (Advised) bean;
for (Advisor advisor : advised.getAdvisors()) {
    System.out.println(advisor);
}
```

## 30. Production checklist

Перед использованием AOP annotation ответь:

1. Кто вызывает method?
2. Пересекает ли вызов proxy?
3. Какой proxy type создан?
4. Метод public и overridable?
5. Каков порядок advisors?
6. Где transaction boundary?
7. Что произойдёт при async thread switch?
8. Cache key включает все semantic inputs?
9. Где хранится cache — local или shared?
10. Кто инвалидирует данные после commit?
11. Какова допустимая stale window?
12. Как система работает при Redis outage?

## 31. Senior interview answer

> Spring AOP — proxy-based method interception. Pointcut selects methods, advisor combines pointcut and advice, and interceptor chain surrounds target invocation. JDK proxies implement interfaces; CGLIB proxies subclass target classes. Only calls crossing the proxy are intercepted, so self-invocation and non-interceptable methods are common failure modes. Transactions, async execution, caching and security all inherit these proxy semantics. For caching, Caffeine is a fast per-JVM L1, while Redis is a shared distributed L2. Correct design requires explicit keys, TTL, serialization, after-commit invalidation, stampede protection, failure policy and observability.

## 32. Memory hooks

```text
No proxy crossing — no advice.
```

```text
JDK wraps the interface.
CGLIB subclasses the class.
```

```text
Cacheable may skip.
CachePut always runs.
CacheEvict removes.
```

```text
Caffeine is near.
Redis is shared.
```

```text
TTL is a business staleness budget.
```

## Sources

- [[98_SOURCES/Spring AOP Cache Redis and Caffeine Sources]]
