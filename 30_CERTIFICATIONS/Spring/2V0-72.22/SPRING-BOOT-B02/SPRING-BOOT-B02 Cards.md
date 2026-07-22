---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain: spring-boot-configuration
batch_id: SPRING-BOOT-B02
status: published
normalization_status: complete
card_count: 35
first_card: SPRING-BOOT-B02-C001
last_card: SPRING-BOOT-B02-C035
objectives:
  - SPRING-1.3.1
  - SPRING-1.3.2
  - SPRING-6.2.1
exam_baseline:
  - spring-boot-2.5.15
current_delta:
  - spring-boot-current
tags:
  - spring-boot
  - externalized-configuration
  - configuration-properties
---

# SPRING-BOOT-B02 — Configuration Properties and Externalized Configuration Cards

## Route navigation

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Roadmap]]
- [[10_CONCEPTS/Spring/Boot/Spring Boot Externalized Configuration and Type-safe Binding]]
- [[10_CONCEPTS/Spring/Boot/Spring Boot Configuration Visual Deep Dive]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Assessment]]
- [[40_PRODUCTION_CASES/Spring/Spring Boot Configuration Production Cases]]
- [[50_LABS/Spring/SPRING-BOOT-B02/README]]
- [[98_SOURCES/Spring Boot Externalized Configuration Sources]]

---

## SPRING-BOOT-B02-C001 — What is Spring Boot externalized configuration?

### Russian Translation

Что такое externalized configuration в Spring Boot?

> [!answer]- Answer
> It is the use of external property sources to configure application behavior without recompiling application code.

### Explanation

Boot prepares an ordered `Environment` from files, environment variables, system properties, command-line arguments, tests and other sources.

### Exam Trap

Externalized configuration is not limited to `application.properties`.

---

## SPRING-BOOT-B02-C002 — What does the Spring `Environment` represent?

### Russian Translation

Что представляет Spring `Environment`?

> [!answer]- Answer
> It provides property lookup plus active and default profile information over an ordered set of `PropertySource` objects.

### Explanation

The environment is the resolved configuration view used by conditions, binding and application beans.

### Exam Trap

It is not one map loaded from one file.

---

## SPRING-BOOT-B02-C003 — How is a value chosen when several property sources contain the same key?

### Russian Translation

Как выбирается значение, если одинаковый key есть в нескольких property sources?

> [!answer]- Answer
> The value from the source with higher precedence wins.

### Explanation

Diagnosis requires inspecting all sources and their order, not only the source where the key was first defined.

### Exam Trap

The value in packaged YAML can be overridden by deployment or command-line configuration.

---

## SPRING-BOOT-B02-C004 — Do command-line properties commonly override configuration files?

### Russian Translation

Переопределяют ли command-line properties конфигурационные файлы?

> [!answer]- Answer
> Yes, command-line option properties normally have high precedence and commonly override file values.

### Explanation

`--client.timeout=3s` becomes an environment property before binding.

### Exam Trap

A positional argument without `--key=value` is not automatically a property.

---

## SPRING-BOOT-B02-C005 — How are environment-variable names adapted for configuration binding?

### Russian Translation

Как имена environment variables адаптируются для configuration binding?

> [!answer]- Answer
> Uppercase underscore-separated names are normalized toward canonical property names, for example `CLIENT_TIMEOUT` to `client.timeout`.

### Explanation

Relaxed naming lets platform-friendly environment variables bind to Java properties.

### Exam Trap

Relaxed binding does not mean every arbitrary spelling is equivalent in every lookup API.

---

## SPRING-BOOT-B02-C006 — Do YAML and properties files create different runtime configuration models?

### Russian Translation

Создают ли YAML и properties разные runtime models?

> [!answer]- Answer
> No. Both are converted into property keys and values in the environment.

### Explanation

YAML provides hierarchical authoring, but `client.retry.max-attempts` remains a flat canonical key.

### Exam Trap

YAML nesting is not a nested Java object until binding occurs.

---

## SPRING-BOOT-B02-C007 — What is Config Data in Spring Boot 2.5?

### Russian Translation

Что такое Config Data в Spring Boot 2.5?

> [!answer]- Answer
> The early configuration-loading system that discovers and loads application files, imports, profile documents and config trees into the environment.

### Explanation

It runs before ordinary bean creation so values can affect profiles and auto-configuration conditions.

### Exam Trap

Config Data is not the same as adding an `@PropertySource` during ordinary configuration parsing.

---

## SPRING-BOOT-B02-C008 — What does `spring.config.name` change?

### Russian Translation

Что изменяет `spring.config.name`?

> [!answer]- Answer
> It changes the base name Boot searches for instead of the default `application` name.

### Explanation

For example, `myservice` makes Boot look for `myservice.properties` or YAML equivalents.

### Exam Trap

It changes the base name, not the property prefix used by `@ConfigurationProperties`.

---

## SPRING-BOOT-B02-C009 — What does `spring.config.location` do?

### Russian Translation

Что делает `spring.config.location`?

> [!answer]- Answer
> It replaces the default Config Data search locations with the supplied locations.

### Explanation

The caller must include every required location because standard defaults are no longer implicitly retained.

### Exam Trap

It is not additive; that is the role of `spring.config.additional-location`.

---

## SPRING-BOOT-B02-C010 — What does `spring.config.additional-location` do?

### Russian Translation

Что делает `spring.config.additional-location`?

> [!answer]- Answer
> It adds locations while retaining Boot's default search locations.

### Explanation

Packaged defaults can remain fallback values while external deployment locations override them.

### Exam Trap

Do not confuse extending the search path with replacing it.

---

## SPRING-BOOT-B02-C011 — What does `spring.config.import` do?

### Russian Translation

Что делает `spring.config.import`?

> [!answer]- Answer
> It imports additional Config Data locations from a configuration document.

### Explanation

Imported files or config trees participate in document activation and property ordering before context refresh.

### Exam Trap

It is not a Java `@Import` and does not import configuration classes.

---

## SPRING-BOOT-B02-C012 — What does the `optional:` prefix mean for a Config Data location?

### Russian Translation

Что означает prefix `optional:` у Config Data location?

> [!answer]- Answer
> Startup continues when the location is missing instead of failing with a location-not-found error.

### Explanation

It changes missing-resource handling, not conversion or validation behavior for values that are present.

### Exam Trap

Do not mark required credentials optional merely to make startup succeed.

---

## SPRING-BOOT-B02-C013 — What is a Config Data config tree?

### Russian Translation

Что такое config tree в Config Data?

> [!answer]- Answer
> A directory where filenames become property keys and file contents become property values.

### Explanation

It is commonly used for mounted container or Kubernetes secrets.

### Exam Trap

A config tree improves delivery structure but does not automatically secure logs or Actuator endpoints.

---

## SPRING-BOOT-B02-C014 — How do multi-document configuration files work?

### Russian Translation

Как работают multi-document configuration files?

> [!answer]- Answer
> One resource contains several ordered logical documents, and only documents whose activation conditions match contribute values.

### Explanation

Later active documents can override values from earlier documents.

### Exam Trap

A document separator does not activate a profile by itself.

---

## SPRING-BOOT-B02-C015 — What is a profile-specific configuration file?

### Russian Translation

Что такое profile-specific configuration file?

> [!answer]- Answer
> A file such as `application-prod.properties` that contributes values when the named profile is active.

### Explanation

It augments and can override non-profile configuration according to Config Data ordering.

### Exam Trap

The presence of the file does not activate the profile.

---

## SPRING-BOOT-B02-C016 — What is the difference between active and default profiles?

### Russian Translation

Чем отличаются active и default profiles?

> [!answer]- Answer
> Active profiles are explicitly selected; default profiles apply only when no active profile has been chosen.

### Explanation

The environment exposes both sets and uses them during document activation.

### Exam Trap

A default profile is not appended when explicit active profiles already exist.

---

## SPRING-BOOT-B02-C017 — What is a profile group?

### Russian Translation

Что такое profile group?

> [!answer]- Answer
> A named profile that activates a configured group of member profiles.

### Explanation

It models deployment bundles such as database, messaging and observability profiles under one logical name.

### Exam Trap

A group is not a property-source precedence mechanism by itself.

---

## SPRING-BOOT-B02-C018 — What does `spring.config.activate.on-profile` do?

### Russian Translation

Что делает `spring.config.activate.on-profile`?

> [!answer]- Answer
> It activates a Config Data document only when its profile expression matches.

### Explanation

The activation condition belongs to the document, allowing one file to hold defaults and profile-specific overrides.

### Exam Trap

It does not globally set the active profile list.

---

## SPRING-BOOT-B02-C019 — When is `@Value` appropriate?

### Russian Translation

Когда уместен `@Value`?

> [!answer]- Answer
> For a small number of unrelated values, placeholders or expressions local to one injection point.

### Explanation

It keeps a simple dependency visible but becomes hard to manage when a large namespace is scattered across many beans.

### Exam Trap

`@Value` is not the preferred replacement for a coherent validated configuration object.

---

## SPRING-BOOT-B02-C020 — What is the main purpose of `@ConfigurationProperties`?

### Russian Translation

Какова основная цель `@ConfigurationProperties`?

> [!answer]- Answer
> To bind a coherent external configuration namespace into a type-safe object.

### Explanation

It supports relaxed names, conversion, nested structures, collections, validation and metadata.

### Exam Trap

Annotating a class does not automatically register it as a bean.

---

## SPRING-BOOT-B02-C021 — What is relaxed binding?

### Russian Translation

Что такое relaxed binding?

> [!answer]- Answer
> The ability to bind common external naming forms such as kebab case, camel case and environment-variable form to the same Java property.

### Explanation

Canonical documentation should still use lowercase kebab-case names.

### Exam Trap

Relaxed binding is not permission to use inconsistent undocumented names everywhere.

---

## SPRING-BOOT-B02-C022 — How can a configuration-properties type be registered?

### Russian Translation

Как зарегистрировать configuration-properties type?

> [!answer]- Answer
> Through `@EnableConfigurationProperties`, `@ConfigurationPropertiesScan`, or an explicit bean declaration.

### Explanation

The Binder operates on a registered bean or explicit programmatic bind target.

### Exam Trap

`@ConfigurationProperties` alone is binding metadata, not universal component scanning.

---

## SPRING-BOOT-B02-C023 — Why use nested configuration-properties objects?

### Russian Translation

Зачем использовать nested configuration-properties objects?

> [!answer]- Answer
> To represent hierarchical namespaces with explicit ownership, types and validation boundaries.

### Explanation

A `client.retry.*` subtree maps naturally to a nested `Retry` object.

### Exam Trap

Nested YAML does not create nested Java objects unless the target type supports the structure.

---

## SPRING-BOOT-B02-C024 — Can `@ConfigurationProperties` bind collections and maps?

### Russian Translation

Может ли `@ConfigurationProperties` связывать collections и maps?

> [!answer]- Answer
> Yes. Indexed properties can bind lists or sets, and named child keys can bind maps.

### Explanation

This is useful for servers, routing tables, headers and feature-specific subconfiguration.

### Exam Trap

Collection replacement or merging across several property sources must be verified for the intended source combination.

---

## SPRING-BOOT-B02-C025 — What does the Binder's conversion layer do?

### Russian Translation

Что делает conversion layer Binder?

> [!answer]- Answer
> It converts resolved source values into target Java types such as numbers, enums, URI, Duration, DataSize and custom domain types.

### Explanation

Property resolution can succeed while conversion fails later.

### Exam Trap

A key existing in the environment does not prove it can bind to the target type.

---

## SPRING-BOOT-B02-C026 — How should a `Duration` value normally be written externally?

### Russian Translation

Как обычно следует задавать `Duration` во внешней конфигурации?

> [!answer]- Answer
> With an explicit unit such as `500ms`, `5s` or `2m`.

### Explanation

Explicit units avoid ambiguity around default-unit behavior.

### Exam Trap

A bare number may use a default unit that differs from the reader's assumption.

---

## SPRING-BOOT-B02-C027 — What is `DataSize` binding used for?

### Russian Translation

Для чего используется binding в `DataSize`?

> [!answer]- Answer
> To convert values such as `10MB` or `256KB` into a typed data-size value.

### Explanation

Typed units are safer than hand-parsing integer byte counts.

### Exam Trap

Do not confuse `MB` configuration semantics with an arbitrary business unit.

---

## SPRING-BOOT-B02-C028 — How is immutable constructor binding commonly expressed in Boot 2.5?

### Russian Translation

Как обычно выражается immutable constructor binding в Boot 2.5?

> [!answer]- Answer
> With a constructor-bound `@ConfigurationProperties` type, commonly marked explicitly with `@ConstructorBinding` and registered through configuration-properties infrastructure.

### Explanation

All required values are supplied at creation, allowing final fields and immutable state.

### Exam Trap

Do not answer a Boot 2.5 question only with current single-constructor inference rules.

---

## SPRING-BOOT-B02-C029 — What changed for constructor binding in current Boot versions?

### Russian Translation

Что изменилось для constructor binding в современных Boot versions?

> [!answer]- Answer
> A single parameterized constructor can commonly be inferred, and records are natural immutable configuration-properties carriers.

### Explanation

The conceptual model remains typed immutable binding, while annotation requirements evolved.

### Exam Trap

Current behavior must not be projected backward into every Boot 2.5 exam question.

---

## SPRING-BOOT-B02-C030 — What does configuration-properties validation provide?

### Russian Translation

Что даёт validation configuration properties?

> [!answer]- Answer
> It checks the bound typed object against constraints and fails startup when required configuration is invalid.

### Explanation

Configuration becomes an executable contract rather than an unchecked collection of strings.

### Exam Trap

Validation requires a registered properties bean and an available validation provider.

---

## SPRING-BOOT-B02-C031 — When is a custom configuration-properties converter appropriate?

### Russian Translation

Когда уместен custom configuration-properties converter?

> [!answer]- Answer
> When a deterministic domain-specific string representation must be converted into a custom type.

### Explanation

The converter should be side-effect free because binding occurs during startup.

### Exam Trap

Do not perform remote calls or database access inside a binding converter.

---

## SPRING-BOOT-B02-C032 — What is the purpose of programmatic `Binder` usage?

### Russian Translation

Для чего используется programmatic `Binder`?

> [!answer]- Answer
> To bind an environment namespace explicitly into a target type from infrastructure or framework code.

### Explanation

Ordinary application configuration normally benefits from declarative properties-bean registration.

### Exam Trap

Programmatic binding does not bypass source precedence, conversion or validation rules.

---

## SPRING-BOOT-B02-C033 — What does the configuration metadata processor generate?

### Russian Translation

Что генерирует configuration metadata processor?

> [!answer]- Answer
> Metadata describing property names, types, defaults, descriptions and deprecation information for tooling.

### Explanation

IDE completion and documentation improve, but runtime validation remains a separate concern.

### Exam Trap

Metadata generation does not prove a supplied value is valid or consumed.

---

## SPRING-BOOT-B02-C034 — Can an unknown or misspelled property remain unnoticed?

### Russian Translation

Может ли unknown или misspelled property остаться незамеченным?

> [!answer]- Answer
> Yes. If no strict binding or validation consumes it, the application may start while the key has no effect.

### Explanation

Metadata, controlled tests and strict configuration contracts help detect unused or misspelled keys.

### Exam Trap

Successful startup does not prove every supplied property was bound.

---

## SPRING-BOOT-B02-C035 — What is the correct diagnostic sequence for a wrong configuration value?

### Russian Translation

Каков правильный порядок диагностики неправильного configuration value?

> [!answer]- Answer
> Confirm the canonical key, list all contributing sources, identify the winning source, verify active profiles/documents, then inspect conversion, binding and validation.

### Explanation

This separates source discovery and precedence from later typed-binding failures.

### Exam Trap

Do not conclude that Boot ignored YAML before checking environment variables, command-line arguments and test/deployment overrides.
