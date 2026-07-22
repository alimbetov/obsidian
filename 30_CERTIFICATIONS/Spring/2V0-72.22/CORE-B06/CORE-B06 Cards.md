---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain: advanced-core
batch_id: CORE-B06
status: published
card_count: 24
card_range: CORE-B06-C117..CORE-B06-C140
language:
  question: en
  translation: ru
  explanation: ru
spring_versions:
  - 5.3.39
tags:
  - spring
  - certification
  - advanced-core
---

# CORE-B06 — Advanced Core Cards

> [!summary]
> Партия проверяет scopes, scoped proxies, runtime lookup, FactoryBean, lazy initialization, circular dependencies, context hierarchy, Resource, MessageSource и lifecycle ownership.

---

# CORE-B06-C117

## Question

> [!question]
> What does the singleton scope mean in the Spring container?

## Russian Translation

Что означает singleton scope в Spring container?

> [!answer]- Answer
> One shared instance per bean definition per Spring container.

## Explanation

Spring singleton не является JVM-global singleton. Два разных `ApplicationContext` создадут два экземпляра одного и того же bean definition.

## Exam Trap

`singleton` не означает one instance per classloader или one instance per application process.

## Memory Hook

> **Per bean, per container.**

---

# CORE-B06-C118

## Question

> [!question]
> Does singleton scope make a bean thread-safe?

## Russian Translation

Делает ли singleton scope bean потокобезопасным?

> [!answer]- Answer
> No. Scope controls identity and lifetime, not synchronization or immutability.

## Explanation

Один mutable bean может одновременно обслуживаться множеством request threads. Thread safety обеспечивается stateless design, immutability или отдельным concurrency protocol.

## Exam Trap

Один instance не означает один thread.

## Memory Hook

> **Identity is not synchronization.**

---

# CORE-B06-C119

## Question

> [!question]
> What does prototype scope guarantee?

## Russian Translation

Что гарантирует prototype scope?

> [!answer]- Answer
> A new bean instance is created for each container lookup or dependency resolution request.

## Explanation

Каждый `getBean()` или новый injection resolution создаёт новый prototype object. Но прямое injection в singleton происходит только один раз при создании singleton.

## Exam Trap

Prototype не означает новый object при каждом вызове business method.

## Memory Hook

> **Prototype is new per lookup, not per method call.**

---

# CORE-B06-C120

## Question

> [!question]
> Does the container automatically invoke destruction callbacks for prototype beans?

## Russian Translation

Вызывает ли container автоматически destruction callbacks для prototype beans?

> [!answer]- Answer
> No. After creation and initialization, destruction ownership is normally transferred to the caller.

## Explanation

Container выполняет initialization callbacks prototype bean, но не отслеживает его до shutdown как singleton. Caller или отдельный lifecycle manager должен закрыть ресурсы.

## Exam Trap

`@PreDestroy` на prototype не гарантирует вызов при `context.close()`.

## Memory Hook

> **Container creates; caller cleans.**

---

# CORE-B06-C121

## Question

> [!question]
> Why does injecting a prototype bean directly into a singleton not provide a fresh instance for every use?

## Russian Translation

Почему прямое внедрение prototype bean в singleton не даёт новый экземпляр при каждом использовании?

> [!answer]- Answer
> Because the dependency is resolved once when the singleton is created.

## Explanation

Singleton construction является одним injection event. Полученный prototype object сохраняется в field и затем переиспользуется.

## Mini Example

```java
@Component
class Service {
    Service(PrototypeHelper helper) {
        this.helper = helper;
    }
}
```

## Exam Trap

Scope dependency не превращает field в dynamic lookup.

## Memory Hook

> **Injection time decides the captured instance.**

---

# CORE-B06-C122

## Question

> [!question]
> How can a singleton obtain a fresh prototype bean for each operation?

## Russian Translation

Как singleton может получать новый prototype bean для каждой операции?

> [!answer]- Answer
> Use `ObjectProvider<T>`, `ObjectFactory<T>`, method injection, or an explicit factory and perform lookup at operation time.

## Explanation

`ObjectProvider.getObject()` обращается к container в момент вызова. Для prototype scope каждый lookup создаёт новый instance.

## Mini Example

```java
ReportBuilder builder = builders.getObject();
```

## Exam Trap

`Optional<PrototypeBean>` решает absence, но не repeated lookup.

## Memory Hook

> **Need fresh? Ask the container now.**

---

# CORE-B06-C123

## Question

> [!question]
> What problem does a scoped proxy solve?

## Russian Translation

Какую проблему решает scoped proxy?

> [!answer]- Answer
> It lets a longer-lived bean hold a stable reference that resolves the correct shorter-lived target at method-invocation time.

## Explanation

Singleton может хранить request/session proxy. Каждый вызов proxy делегируется target текущего request/session.

## Exam Trap

Proxy не меняет scope target. Он меняет момент и способ target resolution.

## Memory Hook

> **Stable handle, contextual target.**

---

# CORE-B06-C124

## Question

> [!question]
> What is the difference between `ScopedProxyMode.INTERFACES` and `ScopedProxyMode.TARGET_CLASS`?

## Russian Translation

Чем отличаются `INTERFACES` и `TARGET_CLASS` для scoped proxy?

> [!answer]- Answer
> `INTERFACES` creates a JDK interface-based proxy; `TARGET_CLASS` creates a class-based proxy.

## Explanation

Interface proxy требует, чтобы consumer зависел от интерфейса. Target-class proxy позволяет injection по concrete class, но подчиняется class-proxy ограничениям.

## Exam Trap

Class-based proxy не означает, что private/final behavior автоматически перехватывается.

## Memory Hook

> **Interface contract or target-class shape.**

---

# CORE-B06-C125

## Question

> [!question]
> Are request and session scopes available in a plain non-web ApplicationContext?

## Russian Translation

Доступны ли request и session scopes в обычном non-web ApplicationContext?

> [!answer]- Answer
> Not automatically. They require web-aware context and scope infrastructure.

## Explanation

`AnnotationConfigApplicationContext` без web infrastructure не имеет active HTTP request/session context.

## Exam Trap

Наличие `@RequestScope` annotation в classpath не создаёт request lifecycle.

## Memory Hook

> **Web scope needs a web context.**

---

# CORE-B06-C126

## Question

> [!question]
> What does `ObjectProvider.getIfAvailable()` solve, and what does it not solve?

## Russian Translation

Что решает `getIfAvailable()` и чего он не решает?

> [!answer]- Answer
> It handles zero candidates by returning `null` or a supplied default; it does not automatically resolve ambiguity among multiple candidates.

## Explanation

Optionality и candidate selection — разные стадии. Multiple candidates всё ещё требуют qualifier, primary или unique policy.

## Exam Trap

Optional lookup не означает arbitrary first candidate.

## Memory Hook

> **Available handles zero, not many.**

---

# CORE-B06-C127

## Question

> [!question]
> What does a normal lookup of a `FactoryBean` name return?

## Russian Translation

Что возвращает обычный lookup имени `FactoryBean`?

> [!answer]- Answer
> The object produced by `FactoryBean.getObject()`.

## Explanation

Container transparently exposes product under the factory bean name.

## Mini Example

```java
Client client = context.getBean("client", Client.class);
```

## Exam Trap

Normal lookup does not return the `FactoryBean` implementation itself.

## Memory Hook

> **Name gives product.**

---

# CORE-B06-C128

## Question

> [!question]
> How do you retrieve the `FactoryBean` instance itself?

## Russian Translation

Как получить сам экземпляр `FactoryBean`?

> [!answer]- Answer
> Prefix the bean name with `&`.

## Mini Example

```java
ClientFactoryBean factory =
        context.getBean("&client", ClientFactoryBean.class);
```

## Explanation

`&` — dereference prefix `BeanFactory`, а не часть declared bean name.

## Exam Trap

`getBean(ClientFactoryBean.class)` может быть неоднозначен при нескольких factories; `&name` выражает точную identity.

## Memory Hook

> **Ampersand gives factory.**

---

# CORE-B06-C129

## Question

> [!question]
> Is `FactoryBean` the same mechanism as an `@Bean` factory method?

## Russian Translation

Является ли `FactoryBean` тем же механизмом, что и factory method с `@Bean`?

> [!answer]- Answer
> No. An `@Bean` method is configuration metadata; a `FactoryBean` is itself a managed bean whose normal lookup exposes a product.

## Explanation

Оба создают objects, но имеют разные identity и lookup semantics.

## Exam Trap

Сходство слова factory не делает contracts одинаковыми.

## Memory Hook

> **Factory method creates a bean; FactoryBean is a bean that creates a product.**

---

# CORE-B06-C130

## Question

> [!question]
> What does `FactoryBean.isSingleton()` describe?

## Russian Translation

Что описывает `FactoryBean.isSingleton()`?

> [!answer]- Answer
> Whether the product returned by that factory should be treated as a shared singleton product.

## Explanation

Это не scope самого FactoryBean definition. Factory bean identity и product identity — две разные оси.

## Exam Trap

`isSingleton()` не заменяет `@Scope` factory bean.

## Memory Hook

> **Factory scope and product identity are separate.**

---

# CORE-B06-C131

## Question

> [!question]
> Does `@Lazy` change a bean's scope?

## Russian Translation

Изменяет ли `@Lazy` scope bean?

> [!answer]- Answer
> No. It changes when the bean is created, not how many scoped instances exist.

## Explanation

Lazy singleton остаётся singleton; его creation откладывается до first demand.

## Exam Trap

Lazy singleton is not prototype.

## Memory Hook

> **Later creation, same scope.**

---

# CORE-B06-C132

## Question

> [!question]
> Can a lazy bean still be created during application startup?

## Russian Translation

Может ли lazy bean всё равно создаться во время startup?

> [!answer]- Answer
> Yes. An eager singleton that directly depends on it forces its creation to satisfy that dependency.

## Explanation

`@Lazy` на target definition откладывает independent pre-instantiation, но eager dependency resolution всё равно требует target.

## Exam Trap

Lazy does not override dependency necessity.

## Memory Hook

> **An eager consumer can wake a lazy target.**

---

# CORE-B06-C133

## Question

> [!question]
> Why is a constructor-based circular dependency fundamentally unresolvable by normal construction?

## Russian Translation

Почему constructor circular dependency принципиально не разрешается обычным созданием?

> [!answer]- Answer
> Each constructor requires the other fully constructed object before either object can be created first.

## Explanation

`A(B)` требует B, а `B(A)` требует A. Нет точки, где можно создать первый complete instance.

## Exam Trap

Changing bean order does not solve the cycle.

## Memory Hook

> **No first constructor exists.**

---

# CORE-B06-C134

## Question

> [!question]
> Why can some setter or field circular references appear resolvable in the core container?

## Russian Translation

Почему некоторые setter/field cycles могут выглядеть разрешимыми в core container?

> [!answer]- Answer
> The container may expose an early singleton reference after instantiation and use it while populating the other bean.

## Explanation

Early reference ещё не означает fully initialized object. Proxy consistency and lifecycle order become complex.

## Exam Trap

Resolvable does not mean recommended, complete, or accepted by every Spring Boot version policy.

## Memory Hook

> **Early reference is not finished bean.**

---

# CORE-B06-C135

## Question

> [!question]
> What is the preferred response to a circular dependency in application design?

## Russian Translation

Как предпочтительно исправлять circular dependency в application design?

> [!answer]- Answer
> Redesign the responsibility graph: extract orchestration, introduce events, narrow interfaces, or pass runtime data explicitly.

## Explanation

`@Lazy` или provider может быть корректен только если delayed lookup отражает реальную lifecycle semantics. Использование их только для сокрытия cycle оставляет coupling.

## Exam Trap

Technical startup success is not architectural resolution.

## Memory Hook

> **Break the responsibility cycle, not only the creation cycle.**

---

# CORE-B06-C136

## Question

> [!question]
> How does bean visibility work between parent and child ApplicationContexts?

## Russian Translation

Как работает visibility beans между parent и child contexts?

> [!answer]- Answer
> A child can resolve beans from its parent; a parent cannot resolve beans defined only in a child.

## Explanation

Lookup is local-first in child, then delegated upward to parent.

## Exam Trap

Context hierarchy is not bidirectional discovery.

## Memory Hook

> **Child looks up; parent never looks down.**

---

# CORE-B06-C137

## Question

> [!question]
> What happens when a child context defines a bean with the same name as a parent bean?

## Russian Translation

Что происходит, если child context определяет bean с тем же именем, что и parent?

> [!answer]- Answer
> Child-local lookup resolves the child bean, shadowing the parent bean for that child.

## Explanation

Parent bean всё ещё существует и остаётся доступным parent consumers. Shadowing изменяет child resolution only.

## Exam Trap

Child override does not mutate parent context.

## Memory Hook

> **Local child definition wins locally.**

---

# CORE-B06-C138

## Question

> [!question]
> Why should application code not assume that every Spring `Resource` is a file?

## Russian Translation

Почему нельзя считать, что каждый Spring `Resource` является файлом?

> [!answer]- Answer
> A Resource may represent classpath data inside a JAR, a URL, byte array, servlet resource, or file-system path.

## Explanation

`getFile()` не работает для всех resource types. Stream-based access переносимее.

## Mini Example

```java
try (InputStream in = resource.getInputStream()) {
    // read
}
```

## Exam Trap

Classpath resource inside executable JAR is not necessarily a `java.io.File`.

## Memory Hook

> **Resource is a handle, not a File promise.**

---

# CORE-B06-C139

## Question

> [!question]
> What role does `MessageSource` play in an ApplicationContext?

## Russian Translation

Какую роль играет `MessageSource` в ApplicationContext?

> [!answer]- Answer
> It resolves message codes with arguments and locale, supporting internationalized presentation messages.

## Explanation

`ApplicationContext` implements `MessageSource`. A bean named `messageSource` supplies local resolution; otherwise parent delegation may apply.

## Exam Trap

MessageSource should not replace stable domain error codes or structured API errors.

## Memory Hook

> **Code + arguments + locale → message.**

---

# CORE-B06-C140

## Question

> [!question]
> Which four questions should be answered when selecting an advanced Spring bean mechanism?

## Russian Translation

На какие четыре вопроса нужно ответить при выборе advanced Spring bean mechanism?

> [!answer]- Answer
> How many instances exist, when the target is resolved, how long it lives, and who destroys it.

## Explanation

Эти вопросы разделяют scope, provider/proxy, lazy timing и lifecycle ownership.

```text
Identity
Resolution
Lifetime
Ownership
```

## Exam Trap

Выбор annotation без ownership model приводит к leaks, stale state и hidden lookup behavior.

## Memory Hook

> **IRLO: Identity, Resolution, Lifetime, Ownership.**

---

## Batch Review Checklist

- [ ] Объяснить singleton как per-container/per-bean.
- [ ] Отличить prototype lookup от method invocation.
- [ ] Объяснить destruction ownership prototype.
- [ ] Нарисовать scoped proxy resolution.
- [ ] Сравнить provider и lazy proxy.
- [ ] Получить product и FactoryBean через name/`&name`.
- [ ] Объяснить, почему lazy bean может создаться eager.
- [ ] Сравнить constructor и setter cycles.
- [ ] Нарисовать parent/child visibility.
- [ ] Объяснить Resource без предположения File.
- [ ] Отделить localized message от domain error code.

## Related Concept

- [[10_CONCEPTS/Spring/Core/Advanced Core Scopes FactoryBean and Context Hierarchy]]

## Practice

- [[01_MAPS/Spring Advanced Core Map.canvas]]
- [[40_PRODUCTION_CASES/Spring/Advanced Core Production Cases]]
- [[50_LABS/Spring/Core-B06/README]]
