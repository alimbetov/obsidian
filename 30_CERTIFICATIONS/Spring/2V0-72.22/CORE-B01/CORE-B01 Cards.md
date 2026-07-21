---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain: core
batch_id: CORE-B01
status: published
normalization_status: complete
card_count: 20
first_card: CORE-B01-C001
last_card: CORE-B01-C020
tags:
  - spring
  - certification
  - core
  - ioc
  - dependency-injection
---

# CORE-B01 — IoC and Bean Registration Foundations

> [!summary]
> 20 normalized cards по IoC, DI, beans, BeanDefinition, BeanFactory/ApplicationContext, component scanning, Java configuration и injection styles.

## Route navigation

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Core Card Roadmap]]
- [[10_CONCEPTS/Spring/Core/Spring Core Foundations]]
- [[10_CONCEPTS/Spring/Core/Spring Core Visual Deep Dive]]
- [[01_MAPS/Spring Core Foundation Map.canvas]]

---

## CORE-B01-C001 — What does Inversion of Control mean in Spring?

### Russian Translation

Что означает Inversion of Control в Spring?

> [!answer]- Answer
> The container, rather than application objects, controls object creation, configuration, wiring and lifecycle.

### Explanation

Application classes describe dependencies and behavior, while container metadata determines how the object graph is assembled.

### Exam Trap

IoC is broader than looking up dependencies; Dependency Injection is its primary Spring implementation style.

---

## CORE-B01-C002 — What is Dependency Injection?

### Russian Translation

Что такое Dependency Injection?

> [!answer]- Answer
> Dependencies are supplied to an object from the outside instead of being created or located by that object.

### Explanation

Constructor, setter or field injection lets the container provide collaborators selected from registered bean definitions.

### Exam Trap

DI is a mechanism; IoC is the broader transfer-of-control principle.

---

## CORE-B01-C003 — What is a Spring bean?

### Russian Translation

Что такое Spring bean?

> [!answer]- Answer
> An object instantiated, configured and managed by the Spring IoC container.

### Explanation

A bean is an ordinary Java object whose metadata, dependencies, lifecycle and post-processing are container-controlled.

### Exam Trap

An object created with `new` inside application code is not automatically a Spring bean.

---

## CORE-B01-C004 — What is a BeanDefinition?

### Russian Translation

Что такое BeanDefinition?

> [!answer]- Answer
> Metadata describing how the container should create and manage a bean.

### Explanation

It can include class or factory method, scope, constructor arguments, dependencies, lifecycle callbacks and configuration flags.

### Exam Trap

A BeanDefinition is the recipe, not the resulting bean instance.

---

## CORE-B01-C005 — What is the main responsibility of BeanFactory?

### Russian Translation

Какова основная ответственность BeanFactory?

> [!answer]- Answer
> It provides the fundamental container contract for bean creation, configuration, dependency resolution and lookup.

### Explanation

Higher-level contexts build on this core engine rather than replacing its responsibilities.

### Exam Trap

Do not attribute all ApplicationContext services to the minimal BeanFactory contract.

---

## CORE-B01-C006 — How is ApplicationContext related to BeanFactory?

### Russian Translation

Как ApplicationContext связан с BeanFactory?

> [!answer]- Answer
> ApplicationContext extends the BeanFactory contract and adds application-level services.

### Explanation

It adds events, message resolution, resource loading, `Environment` support and infrastructure integration.

### Exam Trap

They are not unrelated competing container APIs.

---

## CORE-B01-C007 — Which container is normally preferred in Spring applications?

### Russian Translation

Какой container обычно предпочитают в Spring-приложениях?

> [!answer]- Answer
> `ApplicationContext`.

### Explanation

It includes BeanFactory capabilities plus services normally required by real applications.

### Exam Trap

BeanFactory remains the foundational contract even when applications use ApplicationContext directly.

---

## CORE-B01-C008 — Which metadata styles can define Spring beans?

### Russian Translation

Какие стили configuration metadata могут определять Spring beans?

> [!answer]- Answer
> Annotated components, Java `@Bean` methods, XML and programmatic registration.

### Explanation

Different input formats are normalized into the container's BeanDefinition model.

### Exam Trap

Annotation configuration did not eliminate XML or programmatic registration.

---

## CORE-B01-C009 — What does `@Component` indicate?

### Russian Translation

Что обозначает `@Component`?

> [!answer]- Answer
> The class is a candidate for component scanning and registration as a Spring bean.

### Explanation

The annotation marks discoverability; an active scan must still cover the class package before a BeanDefinition is registered.

### Exam Trap

`@Component` does not start component scanning by itself.

---

## CORE-B01-C010 — Which annotations are common specializations of `@Component`?

### Russian Translation

Какие аннотации являются распространёнными специализациями `@Component`?

> [!answer]- Answer
> `@Service`, `@Repository` and `@Controller`.

### Explanation

They retain component-detection semantics while expressing architectural intent; some also participate in extra infrastructure behavior.

### Exam Trap

They are not entirely separate bean-registration mechanisms.

---

## CORE-B01-C011 — What does component scanning do?

### Russian Translation

Что делает component scanning?

> [!answer]- Answer
> It searches configured packages for candidate component classes and registers BeanDefinitions for them.

### Explanation

Configured base packages define the discovery boundary.

### Exam Trap

An annotated class outside the scan tree is not automatically discovered.

---

## CORE-B01-C012 — What is the typical default bean name for `PaymentService`?

### Russian Translation

Каково типичное default bean name для class `PaymentService`?

> [!answer]- Answer
> `paymentService`.

### Explanation

Spring commonly derives the name from the short class name using decapitalization rules.

### Exam Trap

The default is not normally the fully qualified class name.

---

## CORE-B01-C013 — What does `@Bean` indicate?

### Russian Translation

Что обозначает `@Bean`?

> [!answer]- Answer
> The annotated method creates an object to be registered and managed as a Spring bean.

### Explanation

The method acts as an explicit factory, and its return value is registered under the configured or inferred bean name.

### Exam Trap

`@Bean` is a method-level factory annotation, not a class stereotype.

---

## CORE-B01-C014 — When is `@Bean` especially useful?

### Russian Translation

Когда `@Bean` особенно полезен?

> [!answer]- Answer
> When registering third-party classes or when construction requires explicit factory logic.

### Explanation

The application owns the factory method even when it cannot modify the produced class.

### Exam Trap

`@Bean` is not limited to third-party classes; it is a general Java configuration mechanism.

---

## CORE-B01-C015 — What is the key difference between `@Component` and `@Bean`?

### Russian Translation

Каково ключевое различие между `@Component` и `@Bean`?

> [!answer]- Answer
> `@Component` marks a class for scanning; `@Bean` marks a factory method whose return value becomes a bean.

### Explanation

Both produce managed beans, but discovery and construction control differ.

### Exam Trap

The difference is not whether the result is managed; both registration paths create beans.

---

## CORE-B01-C016 — What does `@Configuration` indicate?

### Russian Translation

Что обозначает `@Configuration`?

> [!answer]- Answer
> The class is a source of bean definitions, typically through `@Bean` methods, with full configuration semantics when processed accordingly.

### Explanation

In full mode Spring enhances the class so inter-bean method calls can return container-managed singletons.

### Exam Trap

A class containing `@Bean` methods does not always have identical full-mode interception semantics.

---

## CORE-B01-C017 — Which injection style is normally preferred for required dependencies?

### Russian Translation

Какой стиль injection обычно предпочитают для required dependencies?

> [!answer]- Answer
> Constructor injection.

### Explanation

Required dependencies become explicit, fields can be final and the object cannot be created in an incomplete state.

### Exam Trap

A question about supported injection styles is different from a question about recommended design.

---

## CORE-B01-C018 — Is `@Autowired` required on the only constructor of a Spring bean?

### Russian Translation

Нужен ли `@Autowired` на единственном constructor Spring bean?

> [!answer]- Answer
> Usually no.

### Explanation

When a class has one constructor, Spring can select it implicitly for dependency injection.

### Exam Trap

With multiple constructors, candidate-selection rules become important.

---

## CORE-B01-C019 — When is setter injection appropriate?

### Russian Translation

Когда setter injection уместен?

> [!answer]- Answer
> For optional or reconfigurable dependencies when the object remains valid without them.

### Explanation

Setter injection permits post-construction configuration, so required invariants must be protected elsewhere.

### Exam Trap

Do not label a dependency optional if the object is invalid without it.

---

## CORE-B01-C020 — What is a major drawback of field injection?

### Russian Translation

Каков основной недостаток field injection?

> [!answer]- Answer
> It hides required dependencies, complicates plain unit testing and prevents final dependency fields.

### Explanation

The class contract becomes less explicit and the object is difficult to instantiate without reflection or a container.

### Exam Trap

Field injection is supported; the issue is design quality, not basic capability.
