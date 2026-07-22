---
type: lab
domain: spring
subdomain: aop
status: active
spring_version: 5.3.39
java_version: 8
tags:
  - spring
  - aop
  - proxy
  - lab
---

# Spring AOP-B01 Lab

## Цель

Увидеть реальные proxy boundaries вместо запоминания аннотаций:

- JDK dynamic proxy;
- CGLIB proxy;
- ordered aspect chain;
- self-invocation;
- `AopContext` re-entry;
- final/private method boundaries;
- настоящий transaction interceptor;
- `@Async` executor boundary;
- runtime proxy/advisor diagnostics.

## Запуск

```bash
cd 50_LABS/Spring/AOP-B01
mvn clean compile exec:java
```

Требования:

- JDK 8+;
- Maven;
- доступ к Maven Central для первого dependency resolution.

## Experiment 1. Programmatic JDK proxy

Target реализует `PaymentPort`.

Ожидаемая форма:

```text
jdk proxy class = com.sun.proxy.$Proxy...
is JDK proxy = true
JDK advice before pay
target processes P-JDK
JDK advice after pay
```

JDK proxy публикует interface contract.

## Experiment 2. Programmatic CGLIB proxy

`ProxyFactory.setProxyTargetClass(true)` принудительно создаёт class-based proxy.

Ожидаемо:

```text
cglib proxy class = lab.PaymentTarget$$EnhancerBySpringCGLIB...
is CGLIB proxy = true
CGLIB advice before pay
target processes P-CGLIB
CGLIB advice after pay
```

## Experiment 3. Ordered aspect chain

`TracingAspect` имеет order 10, `AuditAspect` — order 20.

Ожидаемый nesting:

```text
TRACE enter
  AUDIT enter
    target observed
  AUDIT success
  AUDIT exit
TRACE exit
```

Вход идёт от более высокого precedence к внутреннему advice, выход разворачивается обратно.

## Experiment 4. Self-invocation

```java
public void outerCallsObserved() {
    observed("self");
}
```

Target вызовет annotated method напрямую. `TRACE` и `AUDIT` для внутреннего method не появятся.

Контрольный вопрос:

> Изменится ли результат, если заменить JDK proxy на CGLIB?

Ответ: нет. Проблема — caller path, а не proxy implementation.

## Experiment 5. `AopContext.currentProxy()`

При `exposeProxy=true` target может получить current proxy и повторно войти через него.

```java
((ObservedService) AopContext.currentProxy())
        .observed("AopContext");
```

Advice выполняется, но business code становится связанным со Spring AOP. Предпочтительное архитектурное исправление — отдельный collaborator.

## Experiment 6. Final и private methods

### Final method

```java
@Observed
public final void finalObserved()
```

CGLIB не может override method, поэтому aspect не выполнится.

### Private method

```java
@Observed
private void privateObserved()
```

Private method вызывается внутри target и также не может быть override/перехвачен proxy.

## Experiment 7. Настоящий transaction interceptor

Lab включает `@EnableTransactionManagement` и logging implementation `PlatformTransactionManager`.

External call:

```text
TX begin
  target saves TX-EXTERNAL
TX commit
```

Self-invocation:

```java
public void selfInvoke(String id) {
    processOne(id);
}
```

Счётчик показывает:

```text
transactions started by self invocation = 0
```

Batch facade вызывает отдельный proxied collaborator три раза:

```text
transactions started by batch = 3
```

Это прямое наблюдение разницы между `this-call` и collaborator call.

## Experiment 8. `@Async`

External call проходит через async interceptor:

```text
send ASYNC-EXTERNAL on notification-1
```

Self-invocation выполняется на main thread:

```text
send ASYNC-SELF on main
```

## Runtime diagnostics

Lab выводит:

```java
AopUtils.isAopProxy(bean)
AopUtils.isJdkDynamicProxy(bean)
AopUtils.isCglibProxy(bean)
AopUtils.getTargetClass(bean)
```

Если proxy реализует `Advised`, выводится полный advisor list.

## Управляемые эксперименты

1. Установить `proxyTargetClass=false` и добавить interface к service.
2. Поменять `@Order` аспектов.
3. Удалить `joinPoint.proceed()` из AuditAspect.
4. Проглотить exception и проверить transaction outcome.
5. Убрать `exposeProxy=true` и вызвать `AopContext.currentProxy()`.
6. Перенести `@Transactional` на private method.
7. Создать service через `new` и сравнить runtime behavior.
8. Изменить executor queue capacity и rejection policy.

## Проверка исходного кода

Source предназначен для Java 8 и Spring Framework 5.3.39. Полная проверка выполняется приведённой Maven-командой.

## Related

- [[10_CONCEPTS/Spring/AOP/Spring AOP Proxy Mechanics]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/AOP-B01/AOP-B01 Cards]]
- [[40_PRODUCTION_CASES/Spring/AOP and Cache Production Cases]]
