---
type: moc
domain: spring
status: active
tags:
  - map
  - spring
---

# Spring Map

## Сертификационный маршрут

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Certification Card System]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Core Card Roadmap]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B01/CORE-B01 Cards|CORE-B01 — 20 cards]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B02/CORE-B02 Cards|CORE-B02 — 24 cards]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B03/CORE-B03 Cards|CORE-B03 — 24 cards]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B04/CORE-B04 Cards|CORE-B04 — 24 cards]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B05/CORE-B05 Cards|CORE-B05 — 24 cards]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B06/CORE-B06 Cards|CORE-B06 — 24 cards]]
- [[00_HOME/Review Dashboard]]

```mermaid
flowchart LR
    A[Concept module] --> B[Question EN]
    B --> C[Translation RU]
    C --> D[Direct answer]
    D --> E[Mechanism]
    E --> F[Exam trap]
    F --> G[Memory hook]
    G --> H[Production transfer]
    H --> I[Executable experiment]
    I --> J[Review outcome]
```

# Spring Core — completed route

## CORE-B01: container and registration

- [[10_CONCEPTS/Spring/Core/Spring Core Foundations]]
- [[01_MAPS/Spring Core Foundation Map.canvas]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B01/CORE-B01 Cards]]

IoC, BeanDefinition, registration, stereotypes, Java configuration and injection styles.

## CORE-B02: dependency resolution

- [[10_CONCEPTS/Spring/Core/Dependency Resolution and Optional Injection]]
- [[01_MAPS/Spring Dependency Resolution Map.canvas]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B02/CORE-B02 Cards]]
- [[40_PRODUCTION_CASES/Spring/Dependency Resolution Production Cases]]
- [[50_LABS/Spring/Core-B02/README]]

Candidates, primary, qualifiers, collections, optionality, providers and generics.

## CORE-B03: bean lifecycle

- [[10_CONCEPTS/Spring/Core/Bean Lifecycle from Definition to Destruction]]
- [[01_MAPS/Spring Bean Lifecycle Map.canvas]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B03/CORE-B03 Cards]]
- [[40_PRODUCTION_CASES/Spring/Bean Lifecycle Production Cases]]
- [[50_LABS/Spring/Core-B03/README]]

Instantiation, population, aware callbacks, initialization, proxy publication and destruction.

## CORE-B04: container extension points

- [[10_CONCEPTS/Spring/Core/Container Extension Points]]
- [[01_MAPS/Spring Container Extension Points Map.canvas]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B04/CORE-B04 Cards]]
- [[40_PRODUCTION_CASES/Spring/Container Extension Point Production Cases]]
- [[50_LABS/Spring/Core-B04/README]]

Registry/metadata/instance phases, post-processors, ordering, early references and proxy infrastructure.

## CORE-B05: configuration and profiles

- [[10_CONCEPTS/Spring/Core/Configuration Profiles and Externalized Properties]]
- [[01_MAPS/Spring Configuration and Profiles Map.canvas]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B05/CORE-B05 Cards]]
- [[40_PRODUCTION_CASES/Spring/Configuration and Profiles Production Cases]]
- [[50_LABS/Spring/Core-B05/README]]

Full/lite configuration, imports, profiles, Environment, PropertySources, placeholders and typed configuration.

## CORE-B06: advanced core

- [[10_CONCEPTS/Spring/Core/Advanced Core Scopes FactoryBean and Context Hierarchy]]
- [[01_MAPS/Spring Advanced Core Map.canvas]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B06/CORE-B06 Cards]]
- [[40_PRODUCTION_CASES/Spring/Advanced Core Production Cases]]
- [[50_LABS/Spring/Core-B06/README]]
- [[98_SOURCES/Spring Advanced Core Sources]]

Покрытие:

- singleton/prototype identity and ownership;
- request/session/custom scopes;
- scoped proxy and `ObjectProvider`;
- `FactoryBean` and `&beanName`;
- lazy creation;
- circular dependencies and early references;
- parent/child context visibility;
- `Resource` and `MessageSource`.

## Spring Core completion

```text
CORE-B01  20
CORE-B02  24
CORE-B03  24
CORE-B04  24
CORE-B05  24
CORE-B06  24
TOTAL    140 cards
```

# Next route — AOP and proxies

- join point, pointcut, advice and advisor;
- JDK dynamic proxy;
- CGLIB;
- proxy selection rules;
- self-invocation;
- final/private method limitations;
- interceptor chain;
- aspect ordering;
- custom pointcuts;
- proxy diagnostics;
- relationship to transactions, async, caching and security.

# Transactions

- `@Transactional`;
- propagation;
- isolation;
- rollback rules;
- read-only;
- transaction managers;
- programmatic transactions.

# Data access

- Spring JDBC;
- Spring Data repositories;
- JPA lifecycle;
- query derivation;
- specifications;
- pagination and projections.

# Web and Boot

- Spring MVC and WebFlux;
- validation and exception handling;
- auto-configuration;
- configuration properties;
- actuator;
- caching;
- testing;
- security.
