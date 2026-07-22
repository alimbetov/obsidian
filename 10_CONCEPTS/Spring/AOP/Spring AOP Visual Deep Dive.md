---
type: concept
domain: spring
subdomain: aop
difficulty: advanced
status: learning
interview: true
certification:
  - spring-2V0-72.22
spring_versions:
  - 5.3.39
java_versions:
  - 8
  - 21
production_relevance: critical
prerequisites:
  - "[[Spring AOP Proxy Mechanics]]"
related:
  - "[[Spring Cache Visual Deep Dive]]"
  - "[[10_CONCEPTS/Spring/Transactions/Spring Transaction Management Deep Dive|Spring Transaction Management Deep Dive]]"
tags:
  - spring
  - aop
  - proxy
  - diagrams
  - visual-learning
---

# Spring AOP Visual Deep Dive

> [!summary]
> Эта заметка объясняет Spring AOP через визуальные модели и последовательности вызовов. Главный вопрос не «есть ли annotation», а **через какой object reference прошёл вызов и какие interceptors реально были включены в runtime chain**.

# 1. Annotation не выполняет поведение сама

`@Transactional`, `@Async`, `@Cacheable` и method-security annotations являются metadata. Поведение появляется, когда Spring infrastructure:

1. обнаруживает metadata;
2. создаёт advisor/interceptor;
3. оборачивает bean в proxy;
4. получает внешний method invocation через этот proxy.

```mermaid
flowchart LR
    A["Annotation metadata"] --> B["Infrastructure processor"]
    B --> C["Advisor and interceptor"]
    C --> D["Spring proxy"]
    D --> E["Intercepted external call"]
    E --> F["Target method"]
```

## Что происходит при создании bean

```mermaid
sequenceDiagram
    participant BF as BeanFactory
    participant BPP as AutoProxyCreator
    participant T as Target bean
    participant P as Proxy
    participant C as Caller

    BF->>T: instantiate and initialize
    BF->>BPP: postProcessAfterInitialization(target)
    BPP->>BPP: find eligible advisors
    alt advisors found
        BPP-->>BF: return proxy around target
        BF-->>C: inject proxy reference
    else no advisors
        BPP-->>BF: return target unchanged
        BF-->>C: inject target reference
    end
    C->>P: businessMethod()
    P->>T: invoke through interceptor chain
```

> [!important]
> Spring container обычно хранит и инъектирует proxy reference. Target остаётся внутренним объектом, к которому proxy делегирует вызов.

# 2. Proxy и target — разные роли

```mermaid
flowchart TB
    Caller["Caller bean"] --> Ref["Injected service reference"]
    Ref --> Proxy["Spring proxy"]
    Proxy --> Chain["Interceptor chain"]
    Chain --> Target["Target service instance"]
    Target --> Repo["Repository or gateway"]
```

## Практический пример

```java
@Service
class PaymentService {

    @Transactional
    public PaymentResult pay(PaymentCommand command) {
        return execute(command);
    }
}
```

Фактически caller обычно получает не голый `PaymentService`, а proxy:

```text
paymentController.paymentService
    ↓
PaymentService$$SpringCGLIB$$...
    ↓
TransactionInterceptor
    ↓
PaymentService target
```

# 3. JDK proxy и CGLIB визуально

## JDK Dynamic Proxy

```mermaid
classDiagram
    class PaymentService {
        <<interface>>
        +pay(command)
    }
    class JdkProxy {
        +pay(command)
    }
    class PaymentServiceImpl {
        +pay(command)
        +internalOnlyMethod()
    }

    PaymentService <|.. JdkProxy
    PaymentService <|.. PaymentServiceImpl
    JdkProxy --> PaymentServiceImpl : delegates
```

JDK proxy публикует interface contract. Method, отсутствующий в interface, нельзя вызвать через reference типа interface.

```java
PaymentService service = context.getBean(PaymentService.class);
service.pay(command);
```

## CGLIB proxy

```mermaid
classDiagram
    class PaymentService {
        +pay(command)
        +final settle()
        -privateAudit()
    }
    class PaymentServiceCglibProxy {
        +pay(command)
    }

    PaymentService <|-- PaymentServiceCglibProxy
```

CGLIB proxy создаёт subclass и перехватывает overridable methods.

## Матрица перехватываемости

| Method | JDK proxy | CGLIB proxy | Причина |
|---|---:|---:|---|
| Public method из interface | Да | Да | Доступен proxy boundary |
| Public implementation-only method | Нет через interface reference | Да | JDK публикует interface contract |
| Final method | Не относится к interface limitation | Нет | Subclass не может override |
| Private method | Нет | Нет | Не является proxy-visible invocation |
| Self-invoked public method | Нет advice | Нет advice | Вызов не пересекает proxy |

# 4. External invocation против self-invocation

## Внешний вызов

```mermaid
sequenceDiagram
    participant C as Controller
    participant P as Service proxy
    participant TX as TransactionInterceptor
    participant T as Service target

    C->>P: processBatch()
    P->>TX: invoke
    TX->>TX: begin transaction
    TX->>T: processBatch()
    T-->>TX: result
    TX->>TX: commit
    TX-->>P: result
    P-->>C: result
```

## Внутренний вызов через `this`

```java
@Service
class BatchService {

    public void processBatch(List<Item> items) {
        for (Item item : items) {
            processOne(item);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOne(Item item) {
        repository.save(item);
    }
}
```

```mermaid
sequenceDiagram
    participant C as Caller
    participant P as BatchService proxy
    participant T as BatchService target

    C->>P: processBatch(items)
    P->>T: processBatch(items)
    loop each item
        T->>T: this.processOne(item)
        Note right of T: Proxy is not crossed
    end
    T-->>P: done
    P-->>C: done
```

Результат: `REQUIRES_NEW` не создаётся для каждого item.

## Правильная декомпозиция

```mermaid
flowchart LR
    Batch["BatchService proxy"] --> Single["SingleItemService proxy"]
    Single --> TX["TransactionInterceptor"]
    TX --> Target["SingleItemService target"]
```

```java
@Service
class BatchService {
    private final SingleItemService singleItemService;

    public void processBatch(List<Item> items) {
        for (Item item : items) {
            singleItemService.processOne(item);
        }
    }
}

@Service
class SingleItemService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOne(Item item) {
        repository.save(item);
    }
}
```

# 5. Advisor chain — это вложенные вызовы

Пусть порядок:

```text
Security  order 10
Tracing   order 20
Transaction order 30
Target
```

```mermaid
sequenceDiagram
    participant C as Caller
    participant S as Security
    participant TR as Tracing
    participant TX as Transaction
    participant T as Target

    C->>S: invoke
    S->>S: authorize
    S->>TR: proceed
    TR->>TR: start span
    TR->>TX: proceed
    TX->>TX: begin
    TX->>T: invoke
    T-->>TX: result
    TX->>TX: commit
    TX-->>TR: result
    TR->>TR: finish span
    TR-->>S: result
    S-->>C: result
```

## Почему порядок меняет семантику

```mermaid
flowchart TD
    A{"Security outside transaction?"}
    A -->|Yes| B["Unauthorized call rejected before DB transaction"]
    A -->|No| C["Transaction may open before authorization failure"]
    C --> D["Extra connection usage and different audit timing"]
```

Порядок влияет на:

- момент открытия transaction;
- какие exceptions увидит outer interceptor;
- попадёт ли retry внутрь или снаружи transaction;
- будет ли tracing охватывать commit;
- когда будет записан audit.

# 6. Around advice и `proceed()`

```java
@Around("@annotation(Audited)")
public Object audit(ProceedingJoinPoint pjp) throws Throwable {
    auditStart(pjp);
    try {
        Object result = pjp.proceed();
        auditSuccess(pjp, result);
        return result;
    } catch (Throwable error) {
        auditFailure(pjp, error);
        throw error;
    }
}
```

## Нормальный путь

```mermaid
flowchart LR
    A["around enter"] --> B["proceed"]
    B --> C["inner interceptors"]
    C --> D["target"]
    D --> E["return or throw"]
    E --> F["around exit"]
```

## Забытый `proceed()`

```mermaid
flowchart LR
    A["around enter"] --> B["returns fallback"]
    B --> C["Target never runs"]
    C --> D["Business operation silently suppressed"]
```

## Двойной `proceed()`

```mermaid
flowchart TD
    A["around advice"] --> B["proceed first time"]
    B --> C["target executes once"]
    C --> D["proceed second time"]
    D --> E["target executes again"]
    E --> F["duplicate side effects"]
```

# 7. Exception propagation и rollback

## Корректный advice

```java
catch (Throwable error) {
    recordFailure(error);
    throw error;
}
```

## Опасный advice

```java
catch (Throwable error) {
    log.warn("ignored", error);
    return PaymentResult.failed();
}
```

```mermaid
sequenceDiagram
    participant C as Caller
    participant A as Around advice
    participant TX as TransactionInterceptor
    participant T as Target

    C->>A: invoke
    A->>TX: proceed
    TX->>T: business method
    T-->>TX: throws RuntimeException
    TX->>TX: mark rollback
    TX-->>A: rethrow
    A-->>C: returns fallback instead
    Note over C,A: Caller sees normal return, original failure is hidden
```

В зависимости от расположения advice относительно transaction interceptor swallowing может изменить rollback semantics или скрыть `UnexpectedRollbackException` context.

# 8. `@Async` — это proxy + executor + новый thread

```mermaid
sequenceDiagram
    participant C as Caller thread
    participant P as Async proxy
    participant E as TaskExecutor
    participant W as Worker thread
    participant T as Target

    C->>P: sendNotification()
    P->>E: submit task
    P-->>C: return Future or void
    E->>W: execute task
    W->>T: sendNotification()
    T-->>W: complete
```

## Self-invocation ломает async dispatch

```mermaid
sequenceDiagram
    participant C as Caller
    participant P as Service proxy
    participant T as Service target

    C->>P: process()
    P->>T: process()
    T->>T: this.sendAsync()
    Note right of T: Runs on current thread
```

## Transaction context не переносится автоматически

```mermaid
flowchart LR
    CT["Caller thread transaction"] -->|thread-local state| C["Caller thread"]
    C --> E["Executor queue"]
    E --> W["Worker thread"]
    W --> NT["No caller transaction"]
```

Async method должен открыть собственную transaction, если она нужна.

# 9. Method security и internal call

```java
@Service
class AccountService {

    public void closeDormantAccount(Long id) {
        closeAccount(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void closeAccount(Long id) {
        repository.close(id);
    }
}
```

```mermaid
flowchart TD
    External["External caller"] --> Proxy["Security proxy"]
    Proxy --> Check["Authorization check"]
    Check --> Target["closeAccount"]

    Internal["closeDormantAccount target method"] --> Direct["this.closeAccount"]
    Direct --> Bypass["Security interceptor bypassed"]
```

> [!warning]
> Method-security annotation не является compile-time guard. Она работает только при поддерживаемом intercepted invocation path.

# 10. Final, private и manual `new`

```mermaid
flowchart TD
    M["Annotated method does not run through advice"] --> Q1{"Bean created by Spring?"}
    Q1 -->|No| N["Manual new: no proxy and no BeanPostProcessors"]
    Q1 -->|Yes| Q2{"Call crosses proxy?"}
    Q2 -->|No| S["Self-invocation or direct target reference"]
    Q2 -->|Yes| Q3{"Method proxy-visible?"}
    Q3 -->|No| V["private, final or interface mismatch"]
    Q3 -->|Yes| Q4{"Advisor matches method?"}
    Q4 -->|No| P["Pointcut or annotation mismatch"]
    Q4 -->|Yes| O["Inspect advisor order and infrastructure"]
```

# 11. Runtime diagnostics

```java
Object bean = context.getBean(PaymentService.class);

System.out.println(AopUtils.isAopProxy(bean));
System.out.println(AopUtils.isJdkDynamicProxy(bean));
System.out.println(AopUtils.isCglibProxy(bean));
System.out.println(AopUtils.getTargetClass(bean));

if (bean instanceof Advised) {
    for (Advisor advisor : ((Advised) bean).getAdvisors()) {
        System.out.println(advisor);
    }
}
```

## Диагностическая последовательность

```mermaid
flowchart TD
    A["Advice did not run"] --> B{"Spring owns object?"}
    B -->|No| B1["Replace manual new with injected bean"]
    B -->|Yes| C{"Bean is proxied?"}
    C -->|No| C1["Check infrastructure and advisor eligibility"]
    C -->|Yes| D{"Call crosses proxy?"}
    D -->|No| D1["Refactor self-invocation to collaborator"]
    D -->|Yes| E{"Method visible to proxy?"}
    E -->|No| E1["Check interface, final, private"]
    E -->|Yes| F{"Pointcut matches exact runtime method?"}
    F -->|No| F1["Correct pointcut or annotation placement"]
    F -->|Yes| G["Inspect advisors, order and exceptions"]
```

# 12. Полный production case: payment execution

## Требование

При выполнении платежа нужно:

1. проверить permission;
2. открыть transaction;
3. записать business rows;
4. отправить audit;
5. измерить latency;
6. не допустить duplicate retry side effects.

## Возможная цепочка

```mermaid
sequenceDiagram
    participant API as REST Controller
    participant SEC as Security interceptor
    participant TRACE as Trace interceptor
    participant RETRY as Retry interceptor
    participant TX as Transaction interceptor
    participant SVC as PaymentService
    participant DB as Database

    API->>SEC: pay(command)
    SEC->>SEC: authorize
    SEC->>TRACE: proceed
    TRACE->>TRACE: start span
    TRACE->>RETRY: proceed
    RETRY->>TX: attempt 1
    TX->>TX: begin
    TX->>SVC: pay(command)
    SVC->>DB: insert payment
    DB-->>SVC: transient failure
    SVC-->>TX: exception
    TX->>TX: rollback
    TX-->>RETRY: exception
    RETRY->>TX: attempt 2
    TX->>TX: begin new transaction
    TX->>SVC: pay(command)
    SVC->>DB: insert with idempotency key
    DB-->>SVC: success
    SVC-->>TX: result
    TX->>TX: commit
    TX-->>RETRY: result
    RETRY-->>TRACE: result
    TRACE->>TRACE: finish span
    TRACE-->>SEC: result
    SEC-->>API: response
```

## Что здесь нужно проверить

- Retry должен находиться **снаружи** transaction, если каждая попытка требует новой transaction.
- Idempotency должна защищать от повторного side effect.
- Security обычно выгодно выполнять до открытия transaction.
- Trace может охватывать все retry attempts либо каждую попытку отдельно — это архитектурное решение.

# 13. Как объяснять AOP на собеседовании

Хороший ответ строится по схеме:

```text
1. Spring создаёт proxy вокруг bean.
2. Caller должен вызвать method через proxy reference.
3. Proxy строит interceptor chain из подходящих advisors.
4. Chain выполняется до/после target method.
5. Self-invocation, private/final methods и manual new обходят или блокируют interception.
6. Диагностика начинается с ownership, proxy type, caller path и advisors.
```

# 14. Практические упражнения

1. Нарисовать sequence для `@Transactional + @Retryable` в двух вариантах order.
2. Сделать service с self-invoked `@Async` и вывести thread names.
3. Сравнить JDK и CGLIB через `AopUtils`.
4. Добавить around advice без `proceed()` и доказать, что target не выполнился.
5. Вывести `Advised#getAdvisors()` для transaction, cache и custom aspect.
6. Перенести secured method в collaborator и доказать восстановление authorization check.

## Related materials

- [[Spring AOP Proxy Mechanics]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/AOP-B01/AOP-B01 Cards]]
- [[50_LABS/Spring/AOP-B01/README]]
- [[40_PRODUCTION_CASES/Spring/AOP and Cache Production Cases]]
- [[Spring Cache Visual Deep Dive]]
