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
> 24 карточки по AOP terminology, JDK/CGLIB proxy, self-invocation, pointcuts, advice ordering и реальным proxy-based annotations.

---

## AOP-B01-C001 — What problem does Spring AOP primarily solve?

### Russian Translation

Какую основную проблему решает Spring AOP?

> [!answer]- Answer
> It modularizes cross-cutting concerns such as transactions, security, caching, metrics and audit without duplicating that infrastructure logic in every business method.

### Explanation

AOP surrounds selected method executions with reusable advice while business code remains focused on domain behavior.

### Exam Trap

AOP does not automatically improve business-domain decomposition and is not a replacement for ordinary object-oriented design.

### Memory Hook

**Cross-cutting concern, one reusable boundary.**

---

## AOP-B01-C002 — What is a join point in Spring AOP?

### Russian Translation

Что такое join point в Spring AOP?

> [!answer]- Answer
> A join point is a method execution on a Spring-managed object that can be intercepted by AOP advice.

### Explanation

Spring AOP is proxy-based and focuses on method execution join points, unlike full AspectJ which supports a wider join-point model.

### Exam Trap

Do not claim that Spring AOP intercepts arbitrary field access or constructor execution by default.

---

## AOP-B01-C003 — What is a pointcut?

### Russian Translation

Что такое pointcut?

> [!answer]- Answer
> A pointcut is a predicate that selects which join points should receive advice.

### Mini Example

```java
@Pointcut("execution(* kz.bank.payment..*Service.*(..))")
void paymentOperations() {}
```

### Exam Trap

The pointcut selects methods; it is not the code executed around them.

---

## AOP-B01-C004 — What is advice?

### Russian Translation

Что такое advice?

> [!answer]- Answer
> Advice is the action executed before, after or around a selected join point.

### Explanation

Common advice types are before, after returning, after throwing, after/finally and around.

### Memory Hook

**Pointcut chooses; advice acts.**

---

## AOP-B01-C005 — What is an advisor?

### Russian Translation

Что такое advisor?

> [!answer]- Answer
> An advisor combines a pointcut with advice and can participate in the interceptor chain for a method invocation.

### Exam Trap

An aspect may contribute multiple advisors; advisor and aspect are not synonyms.

---

## AOP-B01-C006 — How does an around advice continue to the target method?

### Russian Translation

Как around advice продолжает вызов target method?

> [!answer]- Answer
> It invokes `ProceedingJoinPoint.proceed()`.

### Explanation

If `proceed()` is not called, the target method and inner interceptors are skipped.

### Production Transfer

A forgotten `proceed()` can make a business method silently stop executing.

---

## AOP-B01-C007 — When does Spring use a JDK dynamic proxy?

### Russian Translation

Когда Spring использует JDK dynamic proxy?

> [!answer]- Answer
> By default, when the target exposes at least one interface and class-based proxying is not forced.

### Explanation

The proxy implements the target interfaces and delegates through an invocation handler/interceptor chain.

### Exam Trap

Implementation-only methods are not exposed through the JDK interface proxy.

---

## AOP-B01-C008 — When does Spring use CGLIB proxying?

### Russian Translation

Когда Spring использует CGLIB proxy?

> [!answer]- Answer
> When the target has no interfaces or class-based proxying is explicitly forced.

### Explanation

CGLIB creates a runtime-generated subclass of the target class.

### Memory Hook

**JDK implements; CGLIB extends.**

---

## AOP-B01-C009 — Why can a final method not be advised by a CGLIB proxy?

### Russian Translation

Почему final method нельзя перехватить CGLIB proxy?

> [!answer]- Answer
> Because a subclass proxy cannot override a final method.

### Exam Trap

Adding an AOP annotation to a final method does not remove the Java inheritance restriction.

---

## AOP-B01-C010 — Can a private method be advised by Spring proxy AOP?

### Russian Translation

Можно ли применить Spring proxy advice к private method?

> [!answer]- Answer
> No. A private method is not exposed through a JDK interface and cannot be overridden by a CGLIB subclass.

### Production Transfer

Moving `@Transactional` from a public service method to a private helper can remove the transaction boundary.

---

## AOP-B01-C011 — What is self-invocation?

### Russian Translation

Что такое self-invocation?

> [!answer]- Answer
> It is a method on the target object calling another method on the same target through `this` rather than through the published proxy.

### Explanation

The second call does not cross the proxy, so proxy advice is not applied.

### Memory Hook

**No proxy crossing — no proxy advice.**

---

## AOP-B01-C012 — Why may `@Transactional(REQUIRES_NEW)` fail during a same-class call?

### Russian Translation

Почему `@Transactional(REQUIRES_NEW)` может не сработать при вызове внутри того же класса?

> [!answer]- Answer
> Because the internal `this.method()` call bypasses the transaction proxy and its interceptor.

### Best Fix

Move the transactional operation to a separate Spring bean and call that collaborator.

### Exam Trap

Changing JDK proxy to CGLIB does not fix self-invocation.

---

## AOP-B01-C013 — What is the preferred fix for self-invocation?

### Russian Translation

Какое исправление self-invocation предпочтительно?

> [!answer]- Answer
> Refactor the advised operation into a separate collaborator so the call naturally crosses a Spring proxy.

### Explanation

Self-injection and `AopContext.currentProxy()` work in specific configurations but couple business code to proxy infrastructure.

---

## AOP-B01-C014 — What is required for `AopContext.currentProxy()`?

### Russian Translation

Что требуется для `AopContext.currentProxy()`?

> [!answer]- Answer
> Proxy exposure must be enabled, and the call must occur inside an active AOP invocation.

### Mini Example

```java
@EnableAspectJAutoProxy(exposeProxy = true)
```

### Exam Trap

Spring does not expose the current proxy by default.

---

## AOP-B01-C015 — What happens when a service is instantiated with `new`?

### Russian Translation

Что происходит, если service создаётся через `new`?

> [!answer]- Answer
> The object is not processed as a Spring bean, so dependency injection, BeanPostProcessors and proxy-based annotations are absent.

### Production Transfer

This is a first diagnostic check when `@Transactional`, `@Async` or `@Cacheable` does not work.

---

## AOP-B01-C016 — How is advice ordering commonly expressed?

### Russian Translation

Как обычно задаётся порядок advice?

> [!answer]- Answer
> With `@Order` or the `Ordered` contract on aspects/advisors.

### Explanation

Lower order values normally have higher precedence on entry; nested exit occurs in reverse order.

### Exam Trap

Order affects semantics, not only log appearance.

---

## AOP-B01-C017 — Why might security advice be placed outside transaction advice?

### Russian Translation

Почему security advice может размещаться снаружи transaction advice?

> [!answer]- Answer
> So an unauthorized invocation is rejected before opening a database transaction.

### Production Transfer

Advisor order can change resource usage and rollback/audit behavior.

---

## AOP-B01-C018 — What is the risk of swallowing an exception in around advice?

### Russian Translation

Чем опасно проглатывание exception в around advice?

> [!answer]- Answer
> Outer interceptors and the caller may observe a normal return, changing rollback behavior and hiding the original failure.

### Best Practice

Record the failure and rethrow unless the aspect intentionally defines a fallback contract.

---

## AOP-B01-C019 — How can you detect the runtime proxy type?

### Russian Translation

Как определить runtime proxy type?

> [!answer]- Answer
> Use `AopUtils.isAopProxy`, `isJdkDynamicProxy`, `isCglibProxy` and `getTargetClass`.

### Mini Example

```java
AopUtils.isJdkDynamicProxy(bean);
AopUtils.isCglibProxy(bean);
```

---

## AOP-B01-C020 — How can you inspect the advisor chain?

### Russian Translation

Как посмотреть цепочку advisors?

> [!answer]- Answer
> If the proxy implements `Advised`, call `getAdvisors()`.

### Production Transfer

This helps diagnose transaction, cache, security and custom aspect ordering or duplicate proxies.

---

## AOP-B01-C021 — Why can `@Async` run synchronously during self-invocation?

### Russian Translation

Почему `@Async` может выполниться синхронно при self-invocation?

> [!answer]- Answer
> The internal call bypasses the async interceptor, so the target method runs on the current thread instead of being submitted to the executor.

### Exam Trap

The annotation is not a JVM instruction to create a thread.

---

## AOP-B01-C022 — Does an async method continue the caller transaction automatically?

### Russian Translation

Продолжает ли async method транзакцию caller автоматически?

> [!answer]- Answer
> No. The async task runs on another thread and needs its own explicit transaction boundary if required.

### Production Transfer

Passing a lazy JPA entity into an async task can fail after the caller transaction is closed.

---

## AOP-B01-C023 — Why can method security be bypassed by an internal call?

### Russian Translation

Почему method security может быть обойдена внутренним вызовом?

> [!answer]- Answer
> Because a `this.securedMethod()` call does not cross the method-security proxy/interceptor.

### Security Rule

Do not treat the presence of an annotation as a compile-time access guard.

---

## AOP-B01-C024 — What is the fastest diagnostic sequence when advice does not run?

### Russian Translation

Какова быстрая последовательность диагностики, если advice не выполняется?

> [!answer]- Answer
> Verify Spring ownership, runtime proxy type, caller path, self-invocation, method visibility/finality, pointcut match, advisor registration and advisor order.

### Memory Hook

**Bean → proxy → caller path → method boundary → advisor chain.**

## Related materials

- [[10_CONCEPTS/Spring/AOP/Spring AOP Proxy Mechanics]]
- [[50_LABS/Spring/AOP-B01/README]]
- [[40_PRODUCTION_CASES/Spring/AOP and Cache Production Cases]]
