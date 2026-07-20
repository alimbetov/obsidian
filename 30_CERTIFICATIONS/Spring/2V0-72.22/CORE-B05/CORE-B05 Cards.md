---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain: configuration-profiles
batch_id: CORE-B05
status: published
card_count: 24
language:
  question: en
  translation: ru
  explanation: ru
prerequisites:
  - "[[10_CONCEPTS/Spring/Core/Configuration Profiles and Externalized Properties]]"
related:
  - "[[01_MAPS/Spring Configuration and Profiles Map.canvas]]"
  - "[[40_PRODUCTION_CASES/Spring/Configuration and Profiles Production Cases]]"
  - "[[50_LABS/Spring/Core-B05/README]]"
tags:
  - spring
  - certification
  - flashcards
  - configuration
  - profiles
---

# CORE-B05 — Configuration and Profiles Cards

> [!summary]
> Партия тренирует различие между формированием bean graph и разрешением runtime values: full/lite configuration, `proxyBeanMethods`, imports, profiles, Environment, PropertySource, placeholders и typed configuration.

## Как проходить

1. Прочитать English Question.
2. Ответить без открытия Answer.
3. Назвать mechanism и boundary.
4. Сверить Russian Translation.
5. Разобрать Exam Trap.
6. Применить правило к production configuration incident.

---

## CORE-B05-C093

### Question

> [!question]
> What is the main semantic benefit of full `@Configuration` mode?

### Russian Translation

> Каково главное семантическое преимущество полного режима `@Configuration`?

> [!answer]- Answer
> Calls from one `@Bean` method to another can be intercepted and redirected to the container-managed bean according to its scope.

### Explanation

Full configuration is enhanced so an inter-bean method call is not necessarily an ordinary Java factory invocation. It can delegate to the BeanFactory and preserve singleton, prototype, lifecycle, and post-processing semantics.

### Exam Trap

> [!warning]
> Full mode does not mean every method is transactional or proxied for arbitrary application behavior. The special semantics concern configuration-class bean methods.

### Memory Hook

> Full mode turns a bean-method call into a managed lookup.

---

## CORE-B05-C094

### Question

> [!question]
> What happens when one `@Bean` method calls another in lite mode?

### Russian Translation

> Что происходит, когда один `@Bean`-метод вызывает другой в lite mode?

> [!answer]- Answer
> It is an ordinary Java method call and may create a new unmanaged object instead of returning the bean stored in the container.

### Explanation

Lite mode applies when `@Bean` methods are declared outside a full configuration class or when proxying is disabled. The method is still a bean factory when the container invokes it, but a direct internal call has normal Java semantics.

### Exam Trap

> [!warning]
> The presence of `@Bean` on a method does not globally replace every direct invocation with `getBean()`.

### Mini Example

```java
@Component
class LiteFactory {
    @Bean Repository repository() { return new Repository(); }
    @Bean Service service() { return new Service(repository()); }
}
```

### Memory Hook

> Lite call means Java call.

---

## CORE-B05-C095

### Question

> [!question]
> Does `proxyBeanMethods=false` change a bean definition from singleton to prototype?

### Russian Translation

> Меняет ли `proxyBeanMethods=false` scope bean с singleton на prototype?

> [!answer]- Answer
> No. It changes inter-bean method-call interception, not the scope of bean definitions registered in the container.

### Explanation

The container still applies the declared scope when resolving a bean. The risk is that a direct method call inside the configuration class bypasses the container and creates a regular object.

### Exam Trap

> [!warning]
> Do not confuse method-call semantics with bean scope metadata.

### Memory Hook

> Proxy flag changes calls, not scopes.

---

## CORE-B05-C096

### Question

> [!question]
> What dependency style is safest when `proxyBeanMethods=false` is used?

### Russian Translation

> Какой стиль объявления dependencies наиболее безопасен при `proxyBeanMethods=false`?

> [!answer]- Answer
> Declare dependencies as `@Bean` method parameters and let the container resolve them.

### Explanation

Parameter injection makes the dependency explicit and independent of configuration-class interception.

### Mini Example

```java
@Bean
Service service(Repository repository) {
    return new Service(repository);
}
```

### Exam Trap

> [!warning]
> Calling `repository()` directly is not equivalent to receiving `Repository repository` as a method parameter in lite mode.

### Memory Hook

> Parameters ask the container; calls ask Java.

---

## CORE-B05-C097

### Question

> [!question]
> Can `@Bean` methods be declared on a class that is not annotated with `@Configuration`?

### Russian Translation

> Можно ли объявлять `@Bean`-методы в class без `@Configuration`?

> [!answer]- Answer
> Yes. They are processed in lite mode, so direct inter-bean method calls are not intercepted.

### Explanation

A component or another registered class may contain bean methods. The critical distinction is not whether definitions are registered, but whether cross-method references have full configuration semantics.

### Exam Trap

> [!warning]
> “Supported” does not mean “identical semantics to full mode.”

### Memory Hook

> Bean methods can be lite; only full configuration protects cross-calls.

---

## CORE-B05-C098

### Question

> [!question]
> What is the basic purpose of `@Import`?

### Russian Translation

> Каково основное назначение `@Import`?

> [!answer]- Answer
> It composes configuration by registering definitions from imported configuration classes, components, selectors, or registrars.

### Explanation

`@Import` is an explicit composition mechanism. It makes module inclusion visible without requiring broad package scanning.

### Exam Trap

> [!warning]
> Importing a class does not necessarily instantiate its beans immediately. Registration and instantiation are different phases.

### Memory Hook

> Import adds definitions; lifecycle creates objects later.

---

## CORE-B05-C099

### Question

> [!question]
> Which types can `@Import` reference in Spring 5.3?

### Russian Translation

> Какие типы может импортировать `@Import` в Spring 5.3?

> [!answer]- Answer
> Configuration classes, regular component classes, `ImportSelector`, `DeferredImportSelector`, and `ImportBeanDefinitionRegistrar` implementations.

### Explanation

Simple classes support explicit composition; selectors choose imports programmatically; registrars create definitions directly.

### Exam Trap

> [!warning]
> `@Import` is not limited to classes annotated with `@Configuration`.

### Memory Hook

> Class, selector, registrar.

---

## CORE-B05-C100

### Question

> [!question]
> When is `DeferredImportSelector` processed relative to regular imports?

### Russian Translation

> Когда обрабатывается `DeferredImportSelector` относительно обычных imports?

> [!answer]- Answer
> Its selection is deferred until regular configuration classes have been processed.

### Explanation

Deferred selection is useful for infrastructure that must make decisions after ordinary user configuration is known, such as auto-configuration-style mechanisms.

### Exam Trap

> [!warning]
> “Deferred” does not mean runtime after the context has started. It is still part of configuration parsing.

### Memory Hook

> Deferred means later in parsing, not later in runtime.

---

## CORE-B05-C101

### Question

> [!question]
> What is the key difference between component scanning and explicit imports?

### Russian Translation

> В чём ключевое различие между component scanning и явным import?

> [!answer]- Answer
> Scanning discovers matching components by package rules, while imports explicitly compose known configuration or component modules.

### Explanation

Scanning favors convention and broad discovery. Imports favor explicit module boundaries and focused test contexts.

### Exam Trap

> [!warning]
> Neither mechanism is universally superior. The choice is about graph visibility and module boundaries.

### Memory Hook

> Scan discovers; import declares.

---

## CORE-B05-C102

### Question

> [!question]
> What does `@Profile` primarily control?

### Russian Translation

> Чем в первую очередь управляет `@Profile`?

> [!answer]- Answer
> It conditionally includes bean definitions in the application context based on active or default profiles.

### Explanation

Profile changes the structure of the bean graph. It is not merely a named property file.

### Exam Trap

> [!warning]
> Do not define profiles only as “different property values.” Profile-specific configuration files are a separate Boot feature.

### Memory Hook

> Profile chooses definitions.

---

## CORE-B05-C103

### Question

> [!question]
> When must active profiles be configured programmatically for an `AnnotationConfigApplicationContext`?

### Russian Translation

> Когда нужно программно установить active profiles для `AnnotationConfigApplicationContext`?

> [!answer]- Answer
> Before `refresh()`, so profile conditions are evaluated while bean definitions are being registered.

### Explanation

After refresh, the graph already exists. Changing the Environment profile list does not rebuild existing singleton definitions and instances.

### Exam Trap

> [!warning]
> Setting a profile after context startup is not a dynamic feature toggle.

### Memory Hook

> Profiles decide before refresh.

---

## CORE-B05-C104

### Question

> [!question]
> What is the role of the default profile?

### Russian Translation

> Какова роль default profile?

> [!answer]- Answer
> It is used for profile conditions when no explicit active profiles are set.

### Explanation

The default profile is part of bean-definition profile matching. Its name can be changed through the Environment.

### Exam Trap

> [!warning]
> A default profile is not the same as a default value in `${property:default}`.

### Memory Hook

> Default profile selects definitions; default placeholder selects a value.

---

## CORE-B05-C105

### Question

> [!question]
> Why are profiles usually a poor replacement for runtime feature flags?

### Russian Translation

> Почему profiles обычно плохо заменяют runtime feature flags?

> [!answer]- Answer
> Profiles are evaluated during context construction and usually require a rebuilt or restarted context, while feature flags may change per request, tenant, percentage, or time.

### Explanation

Profiles are good for startup graph variants. Dynamic rollout belongs in runtime decision infrastructure.

### Exam Trap

> [!warning]
> A profile name such as `new-feature` does not automatically provide dynamic rollout semantics.

### Memory Hook

> Profile is startup structure; flag is runtime choice.

---

## CORE-B05-C106

### Question

> [!question]
> What two responsibilities are exposed by Spring's `Environment` abstraction?

### Russian Translation

> Какие две ответственности предоставляет abstraction `Environment`?

> [!answer]- Answer
> Profile management and property resolution through an ordered set of property sources.

### Explanation

Environment answers both “which profiles are active?” and “what value does this key resolve to?”.

### Exam Trap

> [!warning]
> Environment is not only a wrapper around operating-system environment variables.

### Memory Hook

> Environment = profiles + properties.

---

## CORE-B05-C107

### Question

> [!question]
> What is a `PropertySource`?

### Russian Translation

> Что такое `PropertySource`?

> [!answer]- Answer
> It is an abstraction over a named source of key-value properties that participates in ordered property lookup.

### Explanation

The Environment checks property sources according to precedence. A key may exist in several sources, and the first winning source determines the resolved value.

### Exam Trap

> [!warning]
> A property file is one possible source, not the definition of the abstraction itself.

### Memory Hook

> PropertySource is one layer in the lookup chain.

---

## CORE-B05-C108

### Question

> [!question]
> Is there one universal Spring property-source precedence list for all applications and versions?

### Russian Translation

> Существует ли один универсальный список precedence property sources для всех Spring-приложений и версий?

> [!answer]- Answer
> No. The exact chain depends on plain Framework versus Boot, the Boot version, tests, programmatic sources, command-line inputs, and deployment configuration.

### Explanation

Spring Boot 2.4 introduced Config Data processing, and testing frameworks add high-precedence sources. Incident analysis must inspect the actual Environment and version.

### Exam Trap

> [!warning]
> Memorizing one old Boot precedence list and applying it to every version is unsafe.

### Memory Hook

> Precedence is versioned and contextual.

---

## CORE-B05-C109

### Question

> [!question]
> What does `@PropertySource` add to a Spring application?

### Russian Translation

> Что добавляет `@PropertySource` в Spring application?

> [!answer]- Answer
> It adds a specified property resource as a PropertySource in the Environment.

### Explanation

It is a Framework mechanism for adding sources. It does not reproduce the complete Spring Boot Config Data pipeline.

### Exam Trap

> [!warning]
> Do not assume `@PropertySource` automatically supports every YAML and profile-specific Boot behavior.

### Memory Hook

> PropertySource adds a source; Boot Config Data builds a broader pipeline.

---

## CORE-B05-C110

### Question

> [!question]
> What is the difference between `${...}` and `#{...}` in `@Value` expressions?

### Russian Translation

> В чём различие между `${...}` и `#{...}` в `@Value`?

> [!answer]- Answer
> `${...}` is a property placeholder resolved from property sources; `#{...}` is a Spring Expression Language expression.

### Explanation

A placeholder retrieves configuration data. SpEL evaluates an expression that may reference beans, operators, methods, or literals.

### Exam Trap

> [!warning]
> They may appear in the same annotation but are processed by different mechanisms.

### Memory Hook

> Dollar looks up; hash evaluates.

---

## CORE-B05-C111

### Question

> [!question]
> What does the syntax `${payment.timeout:1000}` mean?

### Russian Translation

> Что означает syntax `${payment.timeout:1000}`?

> [!answer]- Answer
> Resolve `payment.timeout`; if it is absent, use `1000` as the placeholder default and convert it to the target type.

### Explanation

The default is part of placeholder syntax. It is not a new PropertySource and does not validate whether the default is safe for every environment.

### Exam Trap

> [!warning]
> A default value can hide a missing production setting and should not be used for mandatory secrets or critical endpoints.

### Memory Hook

> Colon supplies placeholder fallback.

---

## CORE-B05-C112

### Question

> [!question]
> Why is a `PropertySourcesPlaceholderConfigurer` factory method commonly declared `static`?

### Russian Translation

> Почему factory method для `PropertySourcesPlaceholderConfigurer` обычно объявляют `static`?

> [!answer]- Answer
> Because it is a BeanFactoryPostProcessor and must be created early without prematurely instantiating its containing configuration class.

### Explanation

This follows the same registration discipline as other BeanFactoryPostProcessor beans.

### Exam Trap

> [!warning]
> The reason is lifecycle timing, not performance of static method invocation.

### Memory Hook

> Early metadata processor, static factory method.

---

## CORE-B05-C113

### Question

> [!question]
> When is `@Value` an acceptable configuration mechanism?

### Russian Translation

> Когда `@Value` является приемлемым механизмом configuration?

> [!answer]- Answer
> For a small number of local scalar values where a separate cohesive configuration object would add little value.

### Explanation

`@Value` is concise, but scattered keys become hard to validate, discover, refactor, and test when configuration grows.

### Exam Trap

> [!warning]
> “Supported” does not mean ideal for a large settings group.

### Memory Hook

> One local scalar: Value. Cohesive group: typed object.

---

## CORE-B05-C114

### Question

> [!question]
> What is the main advantage of Spring Boot `@ConfigurationProperties` over many `@Value` fields?

### Russian Translation

> Каково главное преимущество Spring Boot `@ConfigurationProperties` перед множеством `@Value` fields?

> [!answer]- Answer
> It binds a related namespace into a typed, cohesive, convertible, and validatable configuration object.

### Explanation

Typed binding creates an explicit contract and can provide relaxed binding, metadata, nested structures, and validation.

### Exam Trap

> [!warning]
> `@ConfigurationProperties` is a Spring Boot binding facility, not merely another spelling of Framework `@Value`.

### Memory Hook

> Bind a namespace into a contract.

---

## CORE-B05-C115

### Question

> [!question]
> Which mechanism should usually be used when the implementation is the same but only its endpoint and timeout differ by environment?

### Russian Translation

> Какой mechanism обычно использовать, если implementation одинаковая, но endpoint и timeout отличаются между environments?

> [!answer]- Answer
> Externalized properties, preferably bound to a typed configuration object, rather than separate profile-specific implementation classes.

### Explanation

Profiles should not duplicate classes solely to carry different values. Keep the graph stable and configure the instance.

### Exam Trap

> [!warning]
> Different values do not automatically require different bean definitions.

### Memory Hook

> Same code, different values: properties.

---

## CORE-B05-C116

### Question

> [!question]
> What does `@ActiveProfiles` do in a Spring integration test?

### Russian Translation

> Что делает `@ActiveProfiles` в Spring integration test?

> [!answer]- Answer
> It declares which bean-definition profiles are active while the test ApplicationContext is built.

### Explanation

It affects profile conditions and therefore the test bean graph. It does not itself supply arbitrary property values.

### Exam Trap

> [!warning]
> `@ActiveProfiles("test")` and `@TestPropertySource` solve different problems.

### Memory Hook

> ActiveProfiles selects graph; TestPropertySource supplies values.

---

# Batch Review Drill

Перед завершением партии объясни без заметки:

1. Почему direct `@Bean` call безопасен в full mode и опасен в lite mode?
2. Что меняет и чего не меняет `proxyBeanMethods=false`?
3. Когда `@Import` предпочтительнее scanning?
4. Почему profile не является feature flag?
5. Как определить победивший PropertySource?
6. Почему `@Value` и `@ConfigurationProperties` не эквивалентны?
7. Почему active profile и test properties — разные dimensions?

# Sources

- [[98_SOURCES/Spring Configuration and Profiles Sources]]
