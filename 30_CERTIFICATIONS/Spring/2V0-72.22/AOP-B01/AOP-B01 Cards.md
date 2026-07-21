---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain: aop
batch_id: AOP-B01
status: published
card_count: 24
first_card: AOP-B01-C001
last_card: AOP-B01-C024
spring_versions:
  - 5.3.39
tags:
  - spring
  - certification
  - aop
  - proxy
---

# AOP-B01 — Spring AOP and Proxy Mechanics

> [!summary]
> 24 нормализованные карточки по AOP terminology, JDK/CGLIB proxies, self-invocation, pointcuts, advice ordering и proxy-based annotations. Каждая карточка содержит механизм, конкретную exam trap и production-oriented example.

---

## AOP-B01-C001 — What problem does Spring AOP primarily solve?

### Russian Translation

Какую основную проблему решает Spring AOP?

> [!answer]- Answer
> It modularizes cross-cutting concerns such as transactions, security, caching, metrics and audit without duplicating infrastructure logic in every business method.

### Explanation

Cross-cutting concern требуется во многих несвязанных use cases. Вместо ручного `begin/commit`, security check и metrics timer в каждом service method Spring строит proxy и выполняет reusable interceptor chain вокруг выбранного вызова.

```text
caller
  → security advice
  → transaction advice
  → metrics advice
  → business method
```

### Mini Example

```java
@Service
class PaymentService {

    @Transactional
    @Timed("payment.authorize")
    public Receipt authorize(Command command) {
        return gateway.authorize(command);
    }
}
```

Бизнес-метод не содержит ручного transaction или metrics lifecycle.

### Exam Trap

AOP не исправляет плохое domain decomposition и не заменяет обычную композицию объектов. Оно предназначено для поведения, которое действительно пересекает несколько модулей.

### Memory Hook

**Cross-cutting concern — one reusable boundary.**

---

## AOP-B01-C002 — What is a join point in Spring AOP?

### Russian Translation

Что такое join point в Spring AOP?

> [!answer]- Answer
> A join point is a method execution on a Spring-managed object that can be intercepted by Spring AOP advice.

### Explanation

Spring AOP использует proxy-based join-point model. Практически важный join point — вызов метода через опубликованный Spring proxy. Full AspectJ поддерживает более широкий набор join points, например constructor execution и field access.

### Mini Example

```java
paymentService.authorize(command);
```

Если `paymentService` является proxy и method соответствует pointcut, этот method execution становится join point для interceptor chain.

### Exam Trap

Не утверждай, что обычный Spring AOP автоматически перехватывает произвольный field access, constructor call или объект, созданный через `new`.

### Memory Hook

**Spring AOP join point = proxied method execution.**

---

## AOP-B01-C003 — What is a pointcut?

### Russian Translation

Что такое pointcut?

> [!answer]- Answer
> A pointcut is a predicate that selects which join points should receive advice.

### Explanation

Pointcut отвечает на вопрос **где** должен применяться interceptor. Он может выбирать методы по package, type, method signature, annotation или комбинации выражений. Само выполняемое поведение находится в advice.

### Mini Example

```java
@Pointcut("execution(* kz.bank.payment..*Service.*(..))")
void paymentOperations() {
}

@Before("paymentOperations()")
public void recordInvocation() {
    audit.increment();
}
```

### Exam Trap

Pointcut не является кодом, который выполняется вокруг метода. Pointcut выбирает; advice действует.

### Production Transfer

Слишком широкий `execution(* *(..))` может включить infrastructure beans, увеличить latency и создать рекурсивное логирование.

### Memory Hook

**Pointcut chooses the boundary.**

---

## AOP-B01-C004 — What is advice?

### Russian Translation

Что такое advice?

> [!answer]- Answer
> Advice is the action executed before, after or around a join point selected by a pointcut.

### Explanation

Основные типы:

- `@Before` — до target invocation;
- `@AfterReturning` — после успешного возврата;
- `@AfterThrowing` — после exception;
- `@After` — finally-like cleanup;
- `@Around` — полностью контролирует продолжение вызова.

### Mini Example

```java
@Around("@annotation(Monitored)")
public Object measure(ProceedingJoinPoint pjp) throws Throwable {
    long started = System.nanoTime();
    try {
        return pjp.proceed();
    } finally {
        timer.record(System.nanoTime() - started);
    }
}
```

### Exam Trap

`@After` не означает «только после успеха». Оно ближе к `finally`; для успешного результата используется `@AfterReturning`.

### Memory Hook

**Pointcut chooses; advice acts.**

---

## AOP-B01-C005 — What is an advisor?

### Russian Translation

Что такое advisor?

> [!answer]- Answer
> An advisor combines advice with the pointcut that determines where the advice applies.

### Explanation

На runtime Spring строит interceptor chain из подходящих advisors. Один aspect может создать несколько advisors, потому что в нём могут быть разные advice methods и pointcuts.

### Mini Example

```text
TransactionAttributeSourceAdvisor
  = transaction pointcut
  + TransactionInterceptor
```

### Exam Trap

`aspect`, `advisor` и `advice` — не синонимы. Aspect является модулем; advisor представляет одну пару selection + behavior.

### Production Transfer

При дублированной auto-configuration один bean может получить неожиданную цепочку из нескольких похожих advisors. Это видно через `Advised#getAdvisors()`.

### Memory Hook

**Advisor = where + what.**

---

## AOP-B01-C006 — How does an around advice continue to the target method?

### Russian Translation

Как around advice продолжает вызов target method?

> [!answer]- Answer
> It calls `ProceedingJoinPoint.proceed()`.

### Explanation

`@Around` advice находится внутри invocation chain. `proceed()` передаёт управление следующему interceptor или target method. Advice может вызвать его один раз, не вызвать вообще либо, в специальных retry designs, вызвать повторно.

### Mini Example

```java
@Around("@annotation(IdempotentCommand)")
public Object guard(ProceedingJoinPoint pjp) throws Throwable {
    if (duplicateDetector.isDuplicate(pjp.getArgs())) {
        return duplicateDetector.previousResult(pjp.getArgs());
    }
    return pjp.proceed();
}
```

### Exam Trap

Забытый `proceed()` не просто отключает target method: он также пропускает все внутренние interceptors, включая transaction и security, расположенные глубже в chain.

### Production Transfer

Если endpoint возвращает `null`, но service logs отсутствуют, проверь around advice, которое завершает вызов без `proceed()`.

### Memory Hook

**Around owns continuation.**

---

## AOP-B01-C007 — When does Spring use a JDK dynamic proxy?

### Russian Translation

Когда Spring использует JDK dynamic proxy?

> [!answer]- Answer
> By default, when the target exposes at least one interface and class-based proxying is not forced.

### Explanation

JDK proxy реализует интерфейсы target object. Caller взаимодействует с proxy через interface contract, а invocation handler передаёт вызов Spring interceptor chain и затем target.

### Mini Example

```java
interface PaymentPort {
    Receipt authorize(Command command);
}

@Service
class PaymentService implements PaymentPort {
    public Receipt authorize(Command command) {
        return gateway.authorize(command);
    }
}
```

Инъекция через `PaymentPort` естественно совместима с JDK proxy.

### Exam Trap

Implementation-only method, отсутствующий в proxy interfaces, нельзя безопасно вызывать через JDK proxy contract.

### Production Transfer

Инъекция конкретного класса может неожиданно сломаться после включения JDK proxying. Предпочитай stable interface boundary, когда это часть design.

### Memory Hook

**JDK proxy implements interfaces.**

---

## AOP-B01-C008 — When does Spring use CGLIB proxying?

### Russian Translation

Когда Spring использует CGLIB proxy?

> [!answer]- Answer
> When the target has no interfaces or class-based proxying is explicitly forced.

### Explanation

CGLIB создаёт runtime subclass target class и переопределяет interceptable methods. Поэтому caller может использовать concrete class type, но остаются ограничения Java inheritance model.

### Mini Example

```java
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Configuration
class AopConfiguration {
}
```

### Exam Trap

CGLIB не означает «перехватывается абсолютно любой method». `final`, `private` и недоступные для override методы остаются вне subclass interception.

### Production Transfer

Class-based proxy может повлиять на runtime class checks и serialization assumptions. Для диагностики используй `AopUtils.getTargetClass(bean)`.

### Memory Hook

**CGLIB proxy extends the target class.**

---

## AOP-B01-C009 — Why can a final method not be advised by a CGLIB proxy?

### Russian Translation

Почему final method нельзя перехватить CGLIB proxy?

> [!answer]- Answer
> Because a subclass proxy cannot override a final method.

### Explanation

CGLIB interception основана на override. Java запрещает override `final` method, поэтому вызов идёт непосредственно к inherited implementation без proxy override point.

### Mini Example

```java
@Service
class SettlementService {

    @Transactional
    public final void settle() {
        // transaction advice cannot intercept this CGLIB method
    }
}
```

### Exam Trap

Наличие `@Transactional` или другой annotation не отменяет правила Java language. Annotation сама по себе не создаёт interception.

### Production Transfer

Если класс не реализует interface и transaction не появляется, проверь `final` class/method до анализа transaction manager.

### Memory Hook

**No override — no CGLIB interception.**

---

## AOP-B01-C010 — Can a private method be advised by Spring proxy AOP?

### Russian Translation

Можно ли применить Spring proxy advice к private method?

> [!answer]- Answer
> No. A private method is not exposed through a JDK interface and cannot be overridden by a CGLIB subclass.

### Explanation

Proxy должен получить внешний method invocation. `private` method является внутренней implementation detail target class. Даже вызов из public method остаётся прямым вызовом внутри target object.

### Mini Example

```java
public void process() {
    persistAudit();
}

@Transactional(requiresNew = true) // conceptually ineffective here
private void persistAudit() {
}
```

### Exam Trap

Перемещение annotation с public service boundary на private helper обычно **удаляет**, а не усиливает proxy behavior.

### Production Transfer

Вынеси отдельную transaction boundary в другой Spring bean с public method.

### Memory Hook

**Private means target-internal, not proxy-visible.**

---

## AOP-B01-C011 — What is self-invocation?

### Russian Translation

Что такое self-invocation?

> [!answer]- Answer
> It is a method on the target object calling another method on the same target through `this`, bypassing the published proxy.

### Explanation

Первый внешний вызов может пройти через proxy. Но после попадания в target expression `this.otherMethod()` обращается к тому же объекту напрямую. Новый обход proxy не происходит.

### Mini Example

```java
@Service
class ReportService {

    public void rebuild() {
        loadAndCache(); // equivalent to this.loadAndCache()
    }

    @Cacheable("reports")
    public Report loadAndCache() {
        return repository.load();
    }
}
```

`rebuild()` не активирует cache interceptor для внутреннего вызова.

### Exam Trap

Self-invocation — это проблема **пути вызова**, а не типа proxy. Замена JDK proxy на CGLIB не делает `this.method()` внешним вызовом.

### Memory Hook

**No proxy crossing — no proxy advice.**

---

## AOP-B01-C012 — Why may `@Transactional(REQUIRES_NEW)` fail during a same-class call?

### Russian Translation

Почему `@Transactional(REQUIRES_NEW)` может не сработать при вызове внутри того же класса?

> [!answer]- Answer
> Because the internal `this.method()` call bypasses the transaction proxy and therefore never reaches `TransactionInterceptor`.

### Explanation

`REQUIRES_NEW` реализуется interceptor, который приостанавливает текущую transaction и начинает независимую physical transaction. Если interceptor не вызван, обычный Java method call продолжает существующий execution context.

### Mini Example

```java
@Service
class OrderService {

    @Transactional
    public void place(Order order) {
        repository.save(order);
        saveAudit(order.id()); // no new transaction
        throw new IllegalStateException("rollback order");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAudit(long orderId) {
        auditRepository.save(new Audit(orderId));
    }
}
```

Обе записи могут откатиться вместе.

### Exam Trap

`REQUIRES_NEW` — не intrinsic property method. Это инструкция transaction interceptor, применимая только при прохождении через proxy.

### Production Transfer

Вынеси audit operation в `AuditService` и вызывай его как injected collaborator.

### Memory Hook

**Propagation exists at the intercepted boundary.**

---

## AOP-B01-C013 — What is the preferred fix for self-invocation?

### Russian Translation

Какое исправление self-invocation предпочтительно?

> [!answer]- Answer
> Refactor the advised operation into a separate Spring collaborator so the call naturally crosses its proxy.

### Explanation

Отдельный collaborator делает boundary видимой в object model. Это улучшает testability и не связывает business code с `AopContext`, bean lookup или self-injection.

### Mini Example

```java
@Service
class OrderService {
    private final AuditService auditService;

    OrderService(AuditService auditService) {
        this.auditService = auditService;
    }

    public void place(Order order) {
        auditService.record(order.id());
    }
}

@Service
class AuditService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(long orderId) {
        auditRepository.save(new Audit(orderId));
    }
}
```

### Exam Trap

Self-injection может заставить вызов пройти через proxy, но создаёт циклическую зависимость и скрывает architectural boundary.

### Production Transfer

Название collaborator должно отражать отдельную consistency/lifecycle responsibility, а не быть искусственным `Helper` только ради annotation.

### Memory Hook

**Make the boundary an object boundary.**

---

## AOP-B01-C014 — What is required for `AopContext.currentProxy()`?

### Russian Translation

Что требуется для `AopContext.currentProxy()`?

> [!answer]- Answer
> Proxy exposure must be enabled, and the call must occur inside an active AOP invocation on the current thread.

### Explanation

Spring хранит current proxy в thread-bound context только когда `exposeProxy=true`. Вызов вне interceptor path или на другом thread приведёт к ошибке отсутствия current proxy.

### Mini Example

```java
@EnableAspectJAutoProxy(exposeProxy = true)
@Configuration
class AopConfiguration {
}

((ReportService) AopContext.currentProxy()).loadAndCache();
```

### Exam Trap

Current proxy не публикуется по умолчанию. Кроме того, этот подход жёстко связывает domain/service code со Spring AOP API.

### Production Transfer

Используй как редкое tactical workaround или diagnostic technique; основной design — separate collaborator.

### Memory Hook

**Expose explicitly, available only during proxied invocation.**

---

## AOP-B01-C015 — What happens when a service is instantiated with `new`?

### Russian Translation

Что происходит, если service создаётся через `new`?

> [!answer]- Answer
> The object is outside Spring ownership, so dependency injection, BeanPostProcessors and proxy-based annotations are absent.

### Explanation

Spring создаёт proxy во время bean post-processing. Объект, созданный application code напрямую, не проходит registration, dependency population и proxy wrapping.

### Mini Example

```java
PaymentService service = new PaymentService(repository);
service.pay(); // @Transactional is only metadata here
```

### Exam Trap

Annotation не является JVM instruction. Без container processing `@Transactional`, `@Async`, `@Cacheable` и method security не получают runtime behavior.

### Production Transfer

Первый диагностический вопрос: экземпляр получен из `ApplicationContext` или создан вручную?

### Memory Hook

**No Spring ownership — no Spring proxy behavior.**

---

## AOP-B01-C016 — How is advice ordering commonly expressed?

### Russian Translation

Как обычно задаётся порядок advice?

> [!answer]- Answer
> With `@Order` or the `Ordered` contract on aspects and advisors.

### Explanation

Меньшее order value обычно означает более высокий precedence при входе. Interceptors образуют вложенную цепочку, поэтому выход и exception unwinding происходят в обратном порядке.

### Mini Example

```text
enter Security(order=0)
  enter Transaction(order=100)
    target
  exit Transaction
exit Security
```

### Exam Trap

Order влияет не только на порядок logs. Он определяет, будет ли audit выполняться внутри transaction, будет ли unauthorized call открывать connection и какой advice увидит exception.

### Production Transfer

Не полагайся на incidental registration order. Для критичной семантики задавай order явно и проверяй integration test.

### Memory Hook

**Enter by precedence; unwind in reverse.**

---

## AOP-B01-C017 — Why might security advice be placed outside transaction advice?

### Russian Translation

Почему security advice может размещаться снаружи transaction advice?

> [!answer]- Answer
> So an unauthorized invocation is rejected before a database transaction and its resources are opened.

### Explanation

Если security interceptor внешний, он может завершить вызов до получения database connection. Если transaction внешний, security check выполняется уже внутри transaction boundary.

### Mini Example

```text
preferred for many commands:
Security → Transaction → Service

less efficient:
Transaction → Security → Service
```

### Exam Trap

Нет универсального порядка для всех systems. Например, audit requirement может требовать фиксации denied attempt независимо от business transaction.

### Production Transfer

Проверяй одновременно resource usage, rollback behavior и audit semantics.

### Memory Hook

**Order is architecture, not decoration.**

---

## AOP-B01-C018 — What is the risk of swallowing an exception in around advice?

### Russian Translation

Чем опасно проглатывание exception в around advice?

> [!answer]- Answer
> The caller and outer interceptors may observe a normal return, hiding the failure and changing rollback, retry and monitoring behavior.

### Explanation

Exception является частью control flow interceptor chain. Если around advice превращает failure в `null`, transaction interceptor может не получить ожидаемый throwable, retry layer не повторит operation, а metrics зарегистрирует success.

### Mini Example

```java
@Around("@annotation(Monitored)")
public Object unsafe(ProceedingJoinPoint pjp) {
    try {
        return pjp.proceed();
    } catch (Throwable error) {
        log.error("failed", error);
        return null; // changes the contract
    }
}
```

### Exam Trap

Логирование exception не равнозначно корректной обработке. После log exception нужно rethrow, если advice явно не реализует documented fallback contract.

### Production Transfer

При неожиданном commit после business error проверь outer around advice и exception translation.

### Memory Hook

**Swallowed failure becomes false success.**

---

## AOP-B01-C019 — How can you detect the runtime proxy type?

### Russian Translation

Как определить runtime proxy type?

> [!answer]- Answer
> Use `AopUtils.isAopProxy`, `AopUtils.isJdkDynamicProxy`, `AopUtils.isCglibProxy` and `AopUtils.getTargetClass`.

### Explanation

Runtime `bean.getClass()` может показывать generated proxy class. `AopUtils` отвечает на Spring-specific diagnostic questions без привязки к generated class name.

### Mini Example

```java
log.info("aop={}", AopUtils.isAopProxy(bean));
log.info("jdk={}", AopUtils.isJdkDynamicProxy(bean));
log.info("cglib={}", AopUtils.isCglibProxy(bean));
log.info("target={}", AopUtils.getTargetClass(bean));
```

### Exam Trap

`bean instanceof TargetInterface` не доказывает отсутствие proxy: JDK proxy как раз реализует target interfaces.

### Production Transfer

Снимай proxy diagnostics из actual injected bean, а не из нового экземпляра target class.

### Memory Hook

**Inspect the runtime bean, recover the target class.**

---

## AOP-B01-C020 — How can you inspect the advisor chain?

### Russian Translation

Как посмотреть цепочку advisors?

> [!answer]- Answer
> If the proxy implements `Advised`, cast it and call `getAdvisors()`.

### Explanation

Advisor list показывает зарегистрированные pointcut/advice pairs и их runtime order. Это прямое доказательство наличия transaction, cache, security или custom interceptor.

### Mini Example

```java
if (bean instanceof Advised) {
    for (Advisor advisor : ((Advised) bean).getAdvisors()) {
        log.info("advisor={}", advisor);
    }
}
```

### Exam Trap

Наличие annotation на method не доказывает, что соответствующий advisor зарегистрирован или что pointcut сопоставился с фактическим method.

### Production Transfer

Используй chain inspection при duplicate proxies, неожиданном order или отсутствии ожидаемого interceptor.

### Memory Hook

**Annotation is intent; advisor chain is runtime evidence.**

---

## AOP-B01-C021 — Why can `@Async` run synchronously during self-invocation?

### Russian Translation

Почему `@Async` может выполниться синхронно при self-invocation?

> [!answer]- Answer
> The internal call bypasses the async interceptor, so the target method executes on the current caller thread.

### Explanation

Async interceptor должен перехватить invocation и submit task в configured executor. Прямой `this.method()` не достигает interceptor и остаётся обычным Java call.

### Mini Example

```java
@Service
class NotificationService {

    public void register(User user) {
        sendWelcome(user); // still runs on request thread
    }

    @Async
    public void sendWelcome(User user) {
        mailClient.send(user.email());
    }
}
```

### Exam Trap

`@Async` не является байткод-инструкцией «создай thread». Оно работает только через configured method interceptor.

### Production Transfer

Сравни thread name внутри caller и async method; затем проверь proxy crossing и executor bean.

### Memory Hook

**No async interceptor — no task submission.**

---

## AOP-B01-C022 — Does an async method continue the caller transaction automatically?

### Russian Translation

Продолжает ли async method транзакцию caller автоматически?

> [!answer]- Answer
> No. The async task runs on another thread and does not inherit the caller's thread-bound transaction context.

### Explanation

Spring transaction resources обычно привязаны к текущему thread. Executor worker начинает отдельный execution context. Если async operation требует transaction, она должна открыть собственную boundary.

### Mini Example

```java
@Async
@Transactional
public void buildProjection(long orderId) {
    Order order = repository.findById(orderId).orElseThrow();
    projectionRepository.save(map(order));
}
```

Передача ID безопаснее передачи managed entity с lazy associations.

### Exam Trap

`@Async` и `@Transactional` на одном method не означают продолжение caller transaction. Это новая transaction на worker thread, если proxy order и invocation path корректны.

### Production Transfer

Не передавай lazy JPA entity за thread boundary; передавай immutable DTO или identifier и перечитывай данные внутри worker transaction.

### Memory Hook

**New thread — new transaction context.**

---

## AOP-B01-C023 — Why can method security be bypassed by an internal call?

### Russian Translation

Почему method security может быть обойдена внутренним вызовом?

> [!answer]- Answer
> Because `this.securedMethod()` does not cross the method-security proxy and therefore does not execute its authorization interceptor.

### Explanation

Method security является runtime interceptor, а не compile-time access modifier. Если public unsecured method внутри target вызывает secured method напрямую, authorization point отсутствует.

### Mini Example

```java
public void exportAll() {
    exportRestricted(); // direct target call
}

@PreAuthorize("hasRole('ADMIN')")
public void exportRestricted() {
    exporter.run();
}
```

### Exam Trap

Наличие `@PreAuthorize` в source code не гарантирует проверку при любом пути вызова.

### Production Transfer

Размести security на externally callable boundary и вынеси privileged operation в отдельный secured collaborator.

### Memory Hook

**Security annotation needs a secured proxy path.**

---

## AOP-B01-C024 — What is the fastest diagnostic sequence when advice does not run?

### Russian Translation

Какова быстрая последовательность диагностики, если advice не выполняется?

> [!answer]- Answer
> Verify Spring ownership, runtime proxy type, caller path, method interceptability, pointcut match, advisor registration and advisor order.

### Explanation

Диагностика должна идти от наиболее фундаментального условия к более тонкому:

```text
1. Is this object a Spring bean?
2. Is the injected object an AOP proxy?
3. Does the caller cross that proxy?
4. Is the method public/non-final and proxy-visible?
5. Does the pointcut match the runtime method?
6. Is the expected advisor registered?
7. Is another advisor changing the outcome?
```

### Mini Example

```java
log.info("beanClass={}", bean.getClass());
log.info("targetClass={}", AopUtils.getTargetClass(bean));

if (bean instanceof Advised) {
    Arrays.stream(((Advised) bean).getAdvisors())
            .forEach(advisor -> log.info("advisor={}", advisor));
}
```

### Exam Trap

Не начинай с замены transaction manager или proxy technology, пока не доказаны ownership и caller path. Большинство «annotation не работает» связано с `new`, self-invocation или неперехватываемым method.

### Production Transfer

Добавь небольшой integration test, который вызывает bean через context и проверяет observable effect advice, а не только наличие annotation через reflection.

### Memory Hook

**Bean → proxy → path → method → pointcut → advisor → order.**

## Related materials

- [[10_CONCEPTS/Spring/AOP/Spring AOP Proxy Mechanics]]
- [[10_CONCEPTS/Spring/AOP/Spring AOP Proxies and Cache Interception]]
- [[50_LABS/Spring/AOP-B01/README]]
- [[40_PRODUCTION_CASES/Spring/AOP and Cache Production Cases]]
- [[98_SOURCES/Spring AOP and Cache Sources]]
