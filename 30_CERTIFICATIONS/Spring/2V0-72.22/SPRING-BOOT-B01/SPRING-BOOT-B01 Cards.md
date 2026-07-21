---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain: spring-boot
batch: SPRING-BOOT-B01
status: published
card_count: 30
exam_baseline:
  - spring-boot-2.5
current_delta:
  - spring-boot-current
tags:
  - spring-boot
  - auto-configuration
  - active-recall
---

# SPRING-BOOT-B01 — Bootstrap and Auto-configuration Cards

## Route navigation

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B01/SPRING-BOOT-B01 Roadmap]]
- [[10_CONCEPTS/Spring/Boot/Spring Boot Bootstrap and Auto-configuration]]
- [[10_CONCEPTS/Spring/Boot/Spring Boot Auto-configuration Visual Deep Dive]]
- [[40_PRODUCTION_CASES/Spring/Spring Boot Auto-configuration Production Cases]]
- [[50_LABS/Spring/SPRING-BOOT-B01/README]]
- [[98_SOURCES/Spring Boot Auto-configuration Sources]]

---

## SPRING-BOOT-B01-C001 — What does `@SpringBootApplication` combine?

### Russian Translation

Какие основные аннотации объединяет `@SpringBootApplication`?

> [!answer]- Answer
> `@SpringBootConfiguration`, `@EnableAutoConfiguration` and `@ComponentScan`.

### Explanation

The three responsibilities are different: primary Boot configuration, auto-configuration import and application component scanning.

### Exam Trap

Do not answer only `@Configuration`; that omits scanning and auto-configuration.

---

## SPRING-BOOT-B01-C002 — Is component scanning the mechanism that discovers Boot auto-configurations?

### Russian Translation

Обнаруживает ли component scanning auto-configuration classes Spring Boot?

> [!answer]- Answer
> No. Application components are found by component scanning; Boot auto-configurations are selected through `@EnableAutoConfiguration` infrastructure.

### Explanation

An auto-configuration library should not depend on the consumer scanning its package.

### Exam Trap

A class annotated with `@Configuration` is not automatically imported merely because it exists in a dependency JAR.

---

## SPRING-BOOT-B01-C003 — Why should the main application class usually be in a root package?

### Russian Translation

Почему main application class обычно размещают в корневом package приложения?

> [!answer]- Answer
> So component scanning and several default package-based searches cover the intended application packages without scanning unrelated namespaces.

### Explanation

`@ComponentScan` starts from the package of the application class unless configured otherwise.

### Exam Trap

The Java default package is not a convenient universal root; it can cause broad and unpredictable scanning.

---

## SPRING-BOOT-B01-C004 — What is the stable bootstrap order to remember?

### Russian Translation

Какой стабильный порядок bootstrap нужно помнить?

> [!answer]- Answer
> Prepare the environment, create the application context, load configuration sources, refresh the context, invoke runners and publish readiness.

### Explanation

Exact internal listeners and event timing are version-sensitive, but this phase model remains useful.

### Exam Trap

Do not start with bean construction; environment and context preparation happen first.

---

## SPRING-BOOT-B01-C005 — What can influence the default `WebApplicationType`?

### Russian Translation

Что влияет на выбор `WebApplicationType` по умолчанию?

> [!answer]- Answer
> The classpath and any explicit application-type configuration.

### Explanation

Boot chooses a generic, servlet or reactive context strategy.

### Exam Trap

Having servlet and reactive dependencies does not create two web applications automatically.

---

## SPRING-BOOT-B01-C006 — What does Spring Boot auto-configuration attempt to do?

### Russian Translation

Что пытается сделать auto-configuration Spring Boot?

> [!answer]- Answer
> Contribute sensible default bean definitions when classpath, properties, application type and existing-bean conditions support them.

### Explanation

Auto-configuration is conditional configuration, not runtime code generation.

### Exam Trap

It does not guarantee that every library on the classpath receives a fully working bean.

---

## SPRING-BOOT-B01-C007 — How do you opt in to auto-configuration?

### Russian Translation

Как включается auto-configuration?

> [!answer]- Answer
> With `@EnableAutoConfiguration`, commonly through `@SpringBootApplication`.

### Explanation

Only the primary configuration normally needs the annotation.

### Exam Trap

Adding a starter dependency alone does not opt in if auto-configuration is not enabled.

---

## SPRING-BOOT-B01-C008 — What is the role of `AutoConfigurationImportSelector` conceptually?

### Russian Translation

Какова концептуальная роль `AutoConfigurationImportSelector`?

> [!answer]- Answer
> It loads auto-configuration candidates, applies exclusions and filters, and returns selected configuration imports.

### Explanation

Condition evaluation and configuration parsing then determine which definitions are contributed.

### Exam Trap

It does not instantiate every auto-configured bean directly.

---

## SPRING-BOOT-B01-C009 — Why is deferred import useful for auto-configuration?

### Russian Translation

Почему deferred import полезен для auto-configuration?

> [!answer]- Answer
> It allows user configuration to be parsed before auto-configuration selection is finalized, supporting back-off behavior based on visible definitions.

### Explanation

User intent should normally win over defaults.

### Exam Trap

Do not interpret deferred import as lazy bean initialization.

---

## SPRING-BOOT-B01-C010 — Where are Boot 2.x custom auto-configuration candidates commonly registered?

### Russian Translation

Где обычно регистрируются custom auto-configurations в Spring Boot 2.x?

> [!answer]- Answer
> Under the `EnableAutoConfiguration` key in `META-INF/spring.factories`.

### Explanation

This is an exam-version-sensitive registration mechanism.

### Exam Trap

`AutoConfiguration.imports` is the current mechanism, not the complete answer for a Boot 2.5 question.

---

## SPRING-BOOT-B01-C011 — Where are current Boot auto-configurations registered?

### Russian Translation

Где регистрируются auto-configurations в современных версиях Spring Boot?

> [!answer]- Answer
> In `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.

### Explanation

The file lists candidate auto-configuration classes, normally one per line.

### Exam Trap

Do not project this registration file backward into all Boot 2.x questions.

---

## SPRING-BOOT-B01-C012 — What does `@ConditionalOnClass` check?

### Russian Translation

Что проверяет `@ConditionalOnClass`?

> [!answer]- Answer
> Whether specified classes are available to the application classloader.

### Explanation

It is commonly used to activate support only when an optional technology is present.

### Exam Trap

It does not check whether a Spring bean of that class already exists.

---

## SPRING-BOOT-B01-C013 — What does `@ConditionalOnBean` check?

### Russian Translation

Что проверяет `@ConditionalOnBean`?

> [!answer]- Answer
> Whether matching bean definitions/beans are available according to the condition's search rules.

### Explanation

This condition expresses dependency on configured infrastructure, not merely classpath presence.

### Exam Trap

Do not confuse it with `@ConditionalOnClass`.

---

## SPRING-BOOT-B01-C014 — What is the main purpose of `@ConditionalOnMissingBean`?

### Russian Translation

Какова основная цель `@ConditionalOnMissingBean`?

> [!answer]- Answer
> To register a default only when the application has not already provided a matching bean.

### Explanation

It is the core back-off mechanism for non-invasive auto-configuration.

### Exam Trap

It does not mean the default bean has lower runtime priority after both beans are created; normally the default is not registered.

---

## SPRING-BOOT-B01-C015 — What does `@ConditionalOnProperty` use as evidence?

### Russian Translation

Какое условие использует `@ConditionalOnProperty`?

> [!answer]- Answer
> A property in the prepared Spring `Environment`, interpreted with options such as prefix, name, `havingValue` and `matchIfMissing`.

### Explanation

It is evaluated while configuration is being processed, not on every method call.

### Exam Trap

Missing property does not always mean no match; `matchIfMissing` may change the result.

---

## SPRING-BOOT-B01-C016 — What does `@ConditionalOnWebApplication` distinguish?

### Russian Translation

Что различает `@ConditionalOnWebApplication`?

> [!answer]- Answer
> Whether the context represents a web application, optionally servlet or reactive.

### Explanation

It prevents web-only infrastructure from appearing in non-web contexts.

### Exam Trap

It is not the same as checking for one controller bean.

---

## SPRING-BOOT-B01-C017 — Can matching conditions still lead to startup failure?

### Russian Translation

Может ли приложение упасть после успешного совпадения conditions?

> [!answer]- Answer
> Yes. Conditions can allow definition registration, while property binding, construction, validation or later bean creation can still fail.

### Explanation

Condition match and successful context refresh are separate stages.

### Exam Trap

A positive match does not prove a usable bean instance exists.

---

## SPRING-BOOT-B01-C018 — What does auto-configuration back-off mean?

### Russian Translation

Что означает back-off auto-configuration?

> [!answer]- Answer
> Boot refrains from registering a default because user configuration or another condition already satisfies the role.

### Explanation

A user-defined `DataSource` commonly makes default data-source creation back off.

### Exam Trap

Back-off is not an error and does not mean the auto-configuration class was undiscovered.

---

## SPRING-BOOT-B01-C019 — How can a specific auto-configuration be excluded?

### Russian Translation

Как исключить конкретную auto-configuration?

> [!answer]- Answer
> With annotation `exclude`, `excludeName`, or the `spring.autoconfigure.exclude` property.

### Explanation

Class-based exclusion requires the class to be referenceable; name-based exclusion works without loading it.

### Exam Trap

Defining one replacement bean is not identical to excluding the entire auto-configuration.

---

## SPRING-BOOT-B01-C020 — What does `--debug` provide for auto-configuration diagnosis?

### Russian Translation

Что даёт `--debug` для диагностики auto-configuration?

> [!answer]- Answer
> Debug logging including the condition evaluation report, showing positive matches, negative matches, exclusions and unconditional classes.

### Explanation

It helps identify why a candidate matched or did not match.

### Exam Trap

Debug mode does not change a failed condition into a matching condition.

---

## SPRING-BOOT-B01-C021 — Does `@AutoConfigureAfter` force bean instance creation order?

### Russian Translation

Гарантирует ли `@AutoConfigureAfter` порядок создания bean instances?

> [!answer]- Answer
> No. It orders auto-configuration definition contribution; bean creation order is determined by dependencies and explicit lifecycle relationships.

### Explanation

Configuration ordering and runtime dependency ordering are distinct.

### Exam Trap

Do not use auto-configuration ordering as a substitute for constructor dependencies.

---

## SPRING-BOOT-B01-C022 — What is a Spring Boot starter?

### Russian Translation

Что такое Spring Boot starter?

> [!answer]- Answer
> An opinionated dependency descriptor that brings a coherent set of dependencies designed to work with Boot dependency management and auto-configuration.

### Explanation

The starter changes the classpath; auto-configuration code reacts to that classpath and other conditions.

### Exam Trap

A starter is not necessarily the class that registers application beans.

---

## SPRING-BOOT-B01-C023 — What does Boot dependency management do?

### Russian Translation

Что делает dependency management Spring Boot?

> [!answer]- Answer
> It supplies compatible dependency versions for declared or transitive dependencies.

### Explanation

A parent POM or imported BOM can manage versions.

### Exam Trap

Dependency management does not add a library that was never declared or brought transitively.

---

## SPRING-BOOT-B01-C024 — Why should auto-configuration use `@ConditionalOnMissingBean` for defaults?

### Russian Translation

Почему default beans в auto-configuration используют `@ConditionalOnMissingBean`?

> [!answer]- Answer
> To let application configuration replace the default without disabling the whole auto-configuration.

### Explanation

This preserves opinionated defaults and user control simultaneously.

### Exam Trap

Using only `@Primary` would still create both beans and may retain unwanted side effects.

---

## SPRING-BOOT-B01-C025 — Why should an auto-configuration package not be component-scanned accidentally?

### Russian Translation

Почему package auto-configuration не должен случайно попадать в component scan?

> [!answer]- Answer
> Because direct scanning can bypass intended candidate-registration and condition boundaries.

### Explanation

Auto-configurations should be loaded through their dedicated registration mechanism and explicit imports.

### Exam Trap

Putting auto-configuration under the consumer application's root package can make behavior appear independent of `spring.factories` or imports metadata.

---

## SPRING-BOOT-B01-C026 — What is `ApplicationContextRunner` designed for?

### Russian Translation

Для чего предназначен `ApplicationContextRunner`?

> [!answer]- Answer
> To create small, controlled application contexts and assert auto-configuration behavior under different properties, beans and classpath conditions.

### Explanation

It is especially useful for custom auto-configuration tests.

### Exam Trap

It is not a full end-to-end server test by default.

---

## SPRING-BOOT-B01-C027 — How can an auto-configuration test simulate a missing optional library?

### Russian Translation

Как тест auto-configuration может симулировать отсутствие optional library?

> [!answer]- Answer
> By running the context with a filtered classloader that hides the relevant classes.

### Explanation

This directly tests `@ConditionalOnClass` behavior.

### Exam Trap

Not registering a bean does not simulate a missing classpath dependency.

---

## SPRING-BOOT-B01-C028 — What is the difference between `CommandLineRunner` and `ApplicationRunner`?

### Russian Translation

Чем `CommandLineRunner` отличается от `ApplicationRunner`?

> [!answer]- Answer
> `CommandLineRunner` receives raw `String...` arguments; `ApplicationRunner` receives parsed `ApplicationArguments`.

### Explanation

Both run after context refresh and can be ordered.

### Exam Trap

They do not run before the application context exists.

---

## SPRING-BOOT-B01-C029 — What is the trade-off of global lazy initialization?

### Russian Translation

Каков trade-off global lazy initialization?

> [!answer]- Answer
> It can reduce startup work, but moves bean creation failures and latency to first use.

### Explanation

It changes when cost and failure appear; it does not repair invalid configuration.

### Exam Trap

A successful lazy startup does not prove every bean can be created.

---

## SPRING-BOOT-B01-C030 — What is the correct diagnostic order for an expected auto-configured bean that is missing?

### Russian Translation

Каков правильный порядок диагностики отсутствующего auto-configured bean?

> [!answer]- Answer
> Check candidate registration/version, exclusions, condition report, property values, application type, visible user beans and finally later bean-creation failures.

### Explanation

The sequence separates discovery, matching, definition registration and instantiation.

### Exam Trap

Do not immediately add `@ComponentScan` to the library package; that may bypass and conceal the real auto-configuration problem.
