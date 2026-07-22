---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain: container-extension-points
batch_id: CORE-B04
status: published
normalization_status: complete
card_count: 24
first_card: CORE-B04-C069
last_card: CORE-B04-C092
tags:
  - spring
  - certification
  - extension-points
  - lifecycle
---

# CORE-B04 — Container Extension Point Cards

> [!summary]
> 24 normalized cards по metadata phase, instance phase, processor ordering, instantiation hooks, early references и destruction processing.

## Route navigation

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Core Card Roadmap]]
- [[10_CONCEPTS/Spring/Core/Container Extension Points]]
- [[10_CONCEPTS/Spring/Core/Spring Core Visual Deep Dive]]
- [[01_MAPS/Spring Container Extension Points Map.canvas]]
- [[40_PRODUCTION_CASES/Spring/Container Extension Point Production Cases]]
- [[50_LABS/Spring/Core-B04/README]]
- [[98_SOURCES/Spring Container Extension Point Sources]]

---

## CORE-B04-C069 — What is the fundamental difference between BeanFactoryPostProcessor and BeanPostProcessor?

### Russian Translation

В чём фундаментальное различие BeanFactoryPostProcessor и BeanPostProcessor?

> [!answer]- Answer
> A BeanFactoryPostProcessor works with bean configuration metadata before ordinary beans are instantiated. A BeanPostProcessor works with bean instances during creation.

### Explanation

The first edits BeanDefinition recipes; the second can inspect, modify or replace object references.

### Exam Trap

BeanFactoryPostProcessor does not post-process business instances of BeanFactory.

---

## CORE-B04-C070 — What additional capability does BeanDefinitionRegistryPostProcessor provide?

### Russian Translation

Какую дополнительную возможность даёт BeanDefinitionRegistryPostProcessor?

> [!answer]- Answer
> It can register, remove or modify BeanDefinitions through BeanDefinitionRegistry before regular BeanFactoryPostProcessor processing.

### Explanation

It changes the catalog of definitions, not merely properties of definitions that already exist.

### Exam Trap

Ordinary BeanFactoryPostProcessor does not provide the registry-specific callback.

---

## CORE-B04-C071 — Which callback runs first in a BeanDefinitionRegistryPostProcessor?

### Russian Translation

Какой callback BeanDefinitionRegistryPostProcessor выполняется первым?

> [!answer]- Answer
> `postProcessBeanDefinitionRegistry()` runs before the inherited `postProcessBeanFactory()`.

### Explanation

Registry mutation completes before later metadata refinement against the BeanFactory.

### Exam Trap

The interface has two phases, not only the registry callback.

---

## CORE-B04-C072 — Why should a BeanFactoryPostProcessor normally avoid calling `getBean()`?

### Russian Translation

Почему BeanFactoryPostProcessor обычно не должен вызывать `getBean()`?

> [!answer]- Answer
> Because it can instantiate application beans prematurely, before normal BeanPostProcessor and auto-proxy infrastructure is fully active.

### Explanation

The processor belongs to the metadata phase; forcing instance creation breaks lifecycle ordering.

### Exam Trap

Technical ability to call `getBean()` does not make the lifecycle interaction correct.

---

## CORE-B04-C073 — Why is a BeanFactoryPostProcessor-returning `@Bean` method commonly declared `static`?

### Russian Translation

Почему `@Bean`-метод, возвращающий BeanFactoryPostProcessor, обычно объявляют `static`?

> [!answer]- Answer
> A static factory method can be invoked without prematurely instantiating its declaring `@Configuration` class and dependencies.

### Explanation

BFPP infrastructure is needed before ordinary configuration instances should enter the normal bean lifecycle.

### Exam Trap

A non-static method may force early configuration-class creation and make that class ineligible for complete post-processing.

---

## CORE-B04-C074 — What kind of state should a BeanFactoryPostProcessor modify?

### Russian Translation

Какое состояние должен изменять BeanFactoryPostProcessor?

> [!answer]- Answer
> BeanDefinition metadata such as property values, scope, lazy-init, qualifiers, class or factory metadata.

### Explanation

It changes the blueprint before ordinary construction begins.

### Exam Trap

Changing fields on an already-created business object is not BeanFactory post-processing.

---

## CORE-B04-C075 — At what points does a basic BeanPostProcessor receive callbacks?

### Russian Translation

В какие моменты базовый BeanPostProcessor получает callbacks?

> [!answer]- Answer
> Before initialization callbacks and after initialization callbacks for each eligible bean instance.

### Explanation

The target is already instantiated and populated; initialization mechanisms run between the two callbacks.

### Exam Trap

`postProcessBeforeInitialization` does not run before the constructor.

---

## CORE-B04-C076 — May `postProcessAfterInitialization()` return a different object reference?

### Russian Translation

Может ли `postProcessAfterInitialization()` вернуть другой object reference?

> [!answer]- Answer
> Yes. It may return a wrapper, decorator or proxy that becomes the exposed bean reference.

### Explanation

This replacement capability underpins many proxy-based Spring features.

### Exam Trap

Consumers are not guaranteed to receive the same object identity created by the constructor.

---

## CORE-B04-C077 — Is a BeanPostProcessor automatically applied to beans in parent and child contexts?

### Russian Translation

Применяется ли BeanPostProcessor автоматически к beans parent и child contexts?

> [!answer]- Answer
> No. BeanPostProcessors are scoped to the container in which they are registered.

### Explanation

Context hierarchy affects bean lookup, not automatic cross-context processor application.

### Exam Trap

Bean visibility and processor scope are separate rules.

---

## CORE-B04-C078 — Why should an `@Bean` method creating a BeanPostProcessor expose a processor-compatible return type?

### Russian Translation

Почему `@Bean`-метод BeanPostProcessor должен иметь совместимый declared return type?

> [!answer]- Answer
> So the ApplicationContext can detect its post-processor nature early enough from factory-method metadata.

### Explanation

Declaring `Object` can hide the infrastructure type until full instantiation, while the processor is needed before regular beans.

### Exam Trap

A runtime object implementing BeanPostProcessor is not sufficient if its declared factory metadata prevents early detection.

---

## CORE-B04-C079 — How are programmatically registered BeanPostProcessors ordered?

### Russian Translation

Как упорядочиваются programmatically registered BeanPostProcessors?

> [!answer]- Answer
> They execute in registration order; `PriorityOrdered` and `Ordered` contracts are ignored for that explicit sequence.

### Explanation

`addBeanPostProcessor()` establishes call order directly.

### Exam Trap

`@Order` does not reorder processors added manually.

---

## CORE-B04-C080 — How are auto-detected processors generally grouped for ordering?

### Russian Translation

Как в общем случае группируются auto-detected processors по order?

> [!answer]- Answer
> `PriorityOrdered` processors first, then `Ordered`, then unordered processors.

### Explanation

Order values are interpreted within the appropriate auto-detected category.

### Exam Trap

Do not apply this grouping rule to explicitly programmatic registration.

---

## CORE-B04-C081 — Why can a business bean injected directly into a BeanPostProcessor miss auto-proxying?

### Russian Translation

Почему business bean, напрямую внедрённый в BeanPostProcessor, может остаться без auto-proxy?

> [!answer]- Answer
> BeanPostProcessors and their direct dependencies are created early, before all processors including auto-proxy creators are available.

### Explanation

The dependency can be instantiated before the complete processing chain is registered.

### Exam Trap

The problem is early creation timing, not necessarily a missing annotation on the service.

---

## CORE-B04-C082 — What can `postProcessBeforeInstantiation()` do?

### Russian Translation

Что может сделать `postProcessBeforeInstantiation()`?

> [!answer]- Answer
> It may return a substitute object before normal target instantiation, short-circuiting the standard creation path.

### Explanation

This is an `InstantiationAwareBeanPostProcessor` hook operating before the constructor path completes normally.

### Exam Trap

Do not confuse it with `postProcessBeforeInitialization()`, where the target already exists.

---

## CORE-B04-C083 — What does returning `false` from `postProcessAfterInstantiation()` mean?

### Russian Translation

Что означает возврат `false` из `postProcessAfterInstantiation()`?

> [!answer]- Answer
> It tells Spring to skip normal property population for that bean.

### Explanation

The object exists, but dependency/property population does not continue through the ordinary path.

### Exam Trap

Returning false does not destroy the bean or undo constructor invocation.

---

## CORE-B04-C084 — What is the purpose of `postProcessProperties()`?

### Russian Translation

Для чего нужен `postProcessProperties()`?

> [!answer]- Answer
> It lets an InstantiationAwareBeanPostProcessor inspect or modify property values and perform custom injection before normal property population.

### Explanation

Annotation-driven field and method injection is implemented through this class of infrastructure.

### Exam Trap

This callback belongs to population, not after-initialization proxying.

---

## CORE-B04-C085 — What is SmartInstantiationAwareBeanPostProcessor primarily intended for?

### Russian Translation

Для чего прежде всего предназначен SmartInstantiationAwareBeanPostProcessor?

> [!answer]- Answer
> Framework infrastructure for bean type prediction, constructor selection and early bean references.

### Explanation

It exposes advanced decisions needed by container, AOP and injection machinery.

### Exam Trap

“Smart” does not mean it is the default application-level processor for every customization.

---

## CORE-B04-C086 — What does `determineCandidateConstructors()` influence?

### Russian Translation

На что влияет `determineCandidateConstructors()`?

> [!answer]- Answer
> It tells the container which constructors should be considered for autowiring.

### Explanation

Constructor annotation infrastructure can participate in selection before instantiation.

### Exam Trap

The callback proposes candidates; it does not construct the object itself.

---

## CORE-B04-C087 — What does `predictBeanType()` provide?

### Russian Translation

Что предоставляет `predictBeanType()`?

> [!answer]- Answer
> An early prediction of the eventual bean type for type matching and infrastructure decisions.

### Explanation

Prediction is useful when the final exposed object may be a proxy of another runtime class.

### Exam Trap

A predicted type does not mean the bean has already been instantiated.

---

## CORE-B04-C088 — What is `getEarlyBeanReference()` used for?

### Russian Translation

Для чего используется `getEarlyBeanReference()`?

> [!answer]- Answer
> It can expose an early, often proxy-compatible reference during certain circular-dependency resolution scenarios.

### Explanation

Infrastructure tries to keep the early reference consistent with the later published reference.

### Exam Trap

Early references are not a universal safe solution for all circular dependencies.

---

## CORE-B04-C089 — What does DestructionAwareBeanPostProcessor add to the lifecycle?

### Russian Translation

Что добавляет DestructionAwareBeanPostProcessor в lifecycle?

> [!answer]- Answer
> A callback before bean destruction callbacks, allowing processor-specific cleanup or deregistration.

### Explanation

It participates only where the container owns destruction lifecycle.

### Exam Trap

It does not make prototype destruction fully container-managed.

---

## CORE-B04-C090 — What is the purpose of `requiresDestruction(Object bean)`?

### Russian Translation

Для чего нужен `requiresDestruction(Object bean)`?

> [!answer]- Answer
> It lets a DestructionAwareBeanPostProcessor indicate whether a particular bean needs its destruction callback.

### Explanation

The method filters cleanup work to relevant beans.

### Exam Trap

It does not perform cleanup itself; it decides whether the callback is needed.

---

## CORE-B04-C091 — Why is AutowiredAnnotationBeanPostProcessor an important example?

### Russian Translation

Почему AutowiredAnnotationBeanPostProcessor — важный пример?

> [!answer]- Answer
> It shows that annotation-driven injection is implemented by infrastructure analyzing metadata and participating in constructor and property injection phases.

### Explanation

The annotation is passive metadata; the processor turns it into lifecycle behavior.

### Exam Trap

The JVM does not know Spring semantics of `@Autowired`.

---

## CORE-B04-C092 — Which extension point fits each major customization task?

### Russian Translation

Какие extension points выбрать для добавления definitions, изменения definitions, wrapping beans, custom injection и cleanup?

> [!answer]- Answer
> Add definitions: BeanDefinitionRegistryPostProcessor. Edit definitions: BeanFactoryPostProcessor. Wrap initialized beans: BeanPostProcessor. Customize population: InstantiationAwareBeanPostProcessor. Cleanup: DestructionAwareBeanPostProcessor.

### Explanation

Choose by lifecycle phase and by whether the target is metadata or an object instance.

### Exam Trap

Do not choose an interface only by a similar name; identify the phase first.
