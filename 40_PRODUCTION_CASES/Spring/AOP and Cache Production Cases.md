---
type: production-case-set
domain: spring
subdomain:
  - aop
  - caching
status: active
case_count: 12
tags:
  - spring
  - aop
  - cache
  - caffeine
  - redis
  - production
---

# Spring AOP and Cache Production Cases

> [!summary]
> Кейсы построены от наблюдаемого production symptom к proxy path, cache topology, root cause, доказательству и исправлению.

---

# Case 1. `REQUIRES_NEW` не создаёт отдельные транзакции

## Симптом

Batch из 100 платежей должен сохранять каждый payment в отдельной transaction. Ошибка на payment 87 откатывает все предыдущие 86.

## Код

```java
@Service
class PaymentBatchService {

    public void process(List<PaymentCommand> commands) {
        for (PaymentCommand command : commands) {
            processOne(command);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOne(PaymentCommand command) {
        repository.save(command);
    }
}
```

## Причина

```text
proxy.process()
    ↓
target.process()
    ↓
this.processOne()
    ↓
transaction interceptor not entered
```

## Доказательство

- runtime bean является proxy;
- log transaction manager показывает transaction только вокруг `process()` или вообще не показывает вокруг `processOne()`;
- external вызов `processOne()` работает иначе, чем внутренний.

## Исправление

```java
@Service
class SinglePaymentService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOne(PaymentCommand command) {
        repository.save(command);
    }
}
```

Batch service вызывает отдельный proxied collaborator.

## Lesson

> Annotation на method не гарантирует boundary. Boundary появляется при вызове через proxy.

---

# Case 2. `@Async` отправляет email в request thread

## Симптом

HTTP request иногда занимает 8 секунд. Thread dump показывает SMTP call в `http-nio-*` thread, хотя method помечен `@Async`.

## Код

```java
public Receipt pay(PaymentCommand command) {
    Receipt receipt = paymentProcessor.pay(command);
    sendReceipt(receipt);
    return receipt;
}

@Async("mailExecutor")
public void sendReceipt(Receipt receipt) {
    mailClient.send(receipt);
}
```

## Причина

Self-invocation bypasses async interceptor.

## Исправление

```java
@Service
class ReceiptNotificationService {

    @Async("mailExecutor")
    public void sendReceipt(Receipt receipt) {
        mailClient.send(receipt);
    }
}
```

## Дополнительные проверки

- queue capacity;
- rejection policy;
- exception handler;
- MDC/security context propagation;
- graceful shutdown.

---

# Case 3. Final method не попадает в audit aspect

## Симптом

Audit records присутствуют для большинства credit operations, но отсутствуют для `recalculateScore()`.

## Код

```java
@AuditedOperation("CREDIT_SCORE")
public final Score recalculateScore(Long clientId) {
    return model.calculate(clientId);
}
```

## Причина

Bean использует CGLIB proxy. Final method нельзя override, поэтому interceptor не получает invocation.

## Исправления

- убрать `final`, если это допустимо;
- вынести operation в interface-based collaborator;
- применить explicit decorator;
- использовать weaving model, если она действительно нужна.

## Диагностика

```java
AopUtils.isCglibProxy(bean);
AopUtils.getTargetClass(bean);
```

---

# Case 4. Method security обойдена internal call

## Симптом

External вызов `approveCredit()` требует роль `CREDIT_MANAGER`, но scheduled job вызывает ту же business operation без security check.

## Код

```java
public void autoApprove(Long id) {
    approveCredit(id);
}

@PreAuthorize("hasRole('CREDIT_MANAGER')")
public void approveCredit(Long id) {
    repository.approve(id);
}
```

## Причина

Scheduled method вызывает secured method на `this`.

## Исправление

Разделить orchestration и secured operation. Для machine identity определить отдельную authorization policy, а не обходить method security случайным internal call.

---

# Case 5. Custom aspect проглотил exception и transaction commit-нулся

## Симптом

Business method бросил exception, но caller получил `null`, а часть database changes оказалась committed.

## Ошибочный advice

```java
@Around("@annotation(AuditedOperation)")
public Object audit(ProceedingJoinPoint pjp) {
    try {
        return pjp.proceed();
    } catch (Throwable error) {
        log.error("operation failed", error);
        return null;
    }
}
```

## Причина

Aspect изменил control-flow contract. Внешний transaction interceptor мог увидеть normal return.

## Исправление

```java
catch (Throwable error) {
    auditFailure(error);
    throw error;
}
```

Если нужен fallback, он должен быть частью явного application contract и тестироваться вместе с rollback semantics.

---

# Case 6. Product cache не даёт hits из-за self-invocation

## Симптом

`@Cacheable` method вызывается сотни раз, repository metrics не уменьшаются.

## Код

```java
public ProductPage page(Long id) {
    ProductDto product = findProduct(id);
    return pageMapper.toPage(product);
}

@Cacheable(cacheNames = "productById", key = "#id")
public ProductDto findProduct(Long id) {
    return repository.load(id);
}
```

## Причина

Internal call bypasses cache proxy.

## Исправление

Создать отдельный `ProductQueryService` и вызывать его извне.

## Проверка

- repository call counter;
- `AopUtils.isAopProxy(productQueryService)`;
- hit/miss metrics;
- identical key across calls.

---

# Case 7. Tenant B получил кешированные данные Tenant A

## Симптом

Редкий security incident: customer с ID 42 в tenant B получил profile tenant A.

## Ошибочный key

```java
@Cacheable(cacheNames = "customer", key = "#customerId")
public CustomerDto find(String tenantId, Long customerId) {
}
```

## Причина

Cache identity не включает tenant boundary.

## Исправление

```java
key = "#tenantId + ':' + #customerId"
```

Дополнительно:

- prefix environment/service;
- integration tests с одинаковыми IDs в разных tenants;
- не помещать sensitive data в plaintext key без необходимости.

---

# Case 8. Caffeine node A обновлён, node B отдаёт старое значение

## Архитектура

```text
load balancer
  ├─ node A + Caffeine A
  └─ node B + Caffeine B
```

## Симптом

После обновления продукта часть requests видит новую цену, часть старую до истечения TTL.

## Причина

Caffeine caches локальны и не имеют автоматической cross-node invalidation.

## Варианты решения

1. Redis shared cache.
2. Invalidation event через Kafka/RabbitMQ/Redis PubSub.
3. Очень короткий local TTL.
4. Versioned data/generation number.
5. Sticky traffic, если bounded staleness допустима — но это не полноценная invalidation strategy.

## Lesson

> Local cache consistency ограничена одним JVM, если вы явно не построили distributed invalidation.

---

# Case 9. Redis outage перегрузил database

## Симптом

Redis недоступен 40 секунд. Все application nodes переключаются на direct DB reads. Database CPU достигает 100%, затем падает основной API.

## Failure chain

```text
Redis failure
    ↓
cache bypass on every node
    ↓
full request traffic reaches DB
    ↓
connection pool saturation
    ↓
timeouts and retries
    ↓
retry amplification
```

## Исправления

- circuit breaker вокруг cache operations;
- bounded concurrency к DB fallback;
- rate limiting;
- stale local cache;
- fallback only for critical requests;
- disable aggressive retries;
- capacity test for cache-loss mode;
- runbook и alert по Redis latency/error rate.

## Decision

Cache должен быть явно classified:

```text
optimization dependency
or
critical serving dependency
```

---

# Case 10. Массовый cache stampede после deployment

## Симптом

После rolling restart Caffeine caches пусты. 20 nodes одновременно загружают popular catalog и создают тысячи одинаковых DB queries.

## Причина

Cold local caches и одинаковый request distribution.

## Mitigations

- warm-up popular keys;
- `sync=true` для same-node coalescing;
- Redis L2;
- staggered rollout;
- TTL jitter;
- refresh-ahead;
- bounded loader executor;
- protect DB with bulkhead.

## Important boundary

`sync=true` на каждом node не объединяет loads между 20 JVM автоматически.

---

# Case 11. Redis value перестал десериализоваться после deployment

## Симптом

После изменения DTO новая версия application получает errors чтения старых cache entries.

## Причины

- class name изменился;
- field type изменился;
- polymorphic JSON metadata несовместима;
- JDK serialization зависит от class evolution;
- two application versions работают одновременно при rolling deployment.

## Исправления

- stable cache DTO;
- schema/version prefix;
- backward-compatible reader;
- dual-read during migration;
- planned cache namespace rollover;
- controlled eviction old namespace.

## Lesson

> Redis cache value — distributed serialized contract, а не private Java field.

---

# Case 12. L1 Caffeine продолжает отдавать stale value после Redis eviction

## Architecture

```text
L1 Caffeine per node
        ↓ miss
L2 Redis shared
        ↓ miss
Database
```

## Симптом

Admin update evicts Redis, но некоторые nodes ещё 5 минут отдают старое значение из Caffeine.

## Причина

Eviction была выполнена только в L2.

## Correct protocol options

- publish invalidation event `{cacheName, key, version}`;
- every node evicts L1;
- short L1 TTL plus Redis canonical version;
- store data version and reject older L1 entry;
- generation token in L1 key;
- accept bounded staleness explicitly.

## Race to consider

```text
node B reads old DB/Redis value
admin publishes invalidation
node B writes old value into L1 after event
```

Нужны version/order semantics, а не только «отправили event».

---

# Unified diagnostic table

| Symptom | First proof |
|---|---|
| transaction annotation ignored | proxy path and self-invocation |
| async still on request thread | thread name + proxy crossing |
| audit misses one method | final/private visibility + pointcut |
| cache has no hits | key, manager, self-invocation, metrics |
| values differ by node | local vs shared topology |
| Redis cache errors after release | serializer and schema version |
| DB overload after Redis outage | fallback concurrency and retry amplification |
| stale L1 after update | cross-node invalidation protocol |

## Related materials

- [[10_CONCEPTS/Spring/AOP/Spring AOP Proxy Mechanics]]
- [[10_CONCEPTS/Spring/Cache/Spring Cache with Caffeine and Redis]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/AOP-B01/AOP-B01 Cards]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CACHE-B01/CACHE-B01 Cards]]
- [[50_LABS/Spring/AOP-B01/README]]
- [[50_LABS/Spring/CACHE-B01/README]]
