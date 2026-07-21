---
type: certification-roadmap
certification: spring-2V0-72.22
domain: spring
subdomain: spring-core
status: active
base_cards_available: 174
published_in_vault: 140
visual_enrichment: complete
batches:
  - CORE-B01
  - CORE-B02
  - CORE-B03
  - CORE-B04
  - CORE-B05
  - CORE-B06
tags:
  - spring
  - certification
  - roadmap
  - navigation
---

# Spring Core Card Roadmap

> [!summary]
> Шесть вертикальных Spring Core modules связывают container metadata, dependency resolution, lifecycle, extension points, configuration и advanced scopes. Route index ниже является основной точкой входа для concepts, Canvas, cards, cases, labs и sources.

# Route navigation

- **Registry:** [[00_HOME/Knowledge Route Registry]]
- **Domain map:** [[01_MAPS/Spring Map]]
- **Visual deep dive:** [[10_CONCEPTS/Spring/Core/Spring Core Visual Deep Dive]]
- **Visual atlas:** [[01_MAPS/Spring Core Visual Atlas.canvas]]
- **Review:** [[00_HOME/Review Dashboard]]
- **Previous foundation:** Java object model — planned.
- **Next route:** [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring AOP and Cache Roadmap]]

# Progress

```text
CORE-B01  20 cards  PUBLISHED
CORE-B02  24 cards  PUBLISHED
CORE-B03  24 cards  PUBLISHED
CORE-B04  24 cards  PUBLISHED
CORE-B05  24 cards  PUBLISHED
CORE-B06  24 cards  PUBLISHED
--------------------------------
TOTAL    140 cards
```

```mermaid
flowchart LR
    B1["CORE-B01 Container foundation"] --> B2["CORE-B02 Candidate resolution"]
    B2 --> B3["CORE-B03 Bean lifecycle"]
    B3 --> B4["CORE-B04 Extension points"]
    B4 --> B5["CORE-B05 Configuration"]
    B5 --> B6["CORE-B06 Advanced Core"]
    B6 --> AOP["AOP and proxies"]
```

# Visual route

- [[10_CONCEPTS/Spring/Core/Spring Core Visual Deep Dive]]
- [[01_MAPS/Spring Core Visual Atlas.canvas]]
- [[01_MAPS/Spring Core Foundation Map.canvas]]
- [[01_MAPS/Spring Dependency Resolution Map.canvas]]
- [[01_MAPS/Spring Bean Lifecycle Map.canvas]]
- [[01_MAPS/Spring Container Extension Points Map.canvas]]
- [[01_MAPS/Spring Configuration and Profiles Map.canvas]]
- [[01_MAPS/Spring Advanced Core Map.canvas]]

# CORE-B01 — IoC, registration and injection

| Role | Artifact |
|---|---|
| Concept | [[10_CONCEPTS/Spring/Core/Spring Core Foundations]] |
| Canvas | [[01_MAPS/Spring Core Foundation Map.canvas]] |
| Cards | [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B01/CORE-B01 Cards]] |

Coverage:

- IoC and container ownership;
- `BeanDefinition`;
- component scanning and Java configuration;
- stereotype annotations;
- constructor, setter and field injection.

# CORE-B02 — Candidate resolution and optionality

| Role | Artifact |
|---|---|
| Concept | [[10_CONCEPTS/Spring/Core/Dependency Resolution and Optional Injection]] |
| Canvas | [[01_MAPS/Spring Dependency Resolution Map.canvas]] |
| Cards | [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B02/CORE-B02 Cards]] |
| Cases | [[40_PRODUCTION_CASES/Spring/Dependency Resolution Production Cases]] |
| Lab | [[50_LABS/Spring/Core-B02/README]] |
| Sources | [[98_SOURCES/Spring Dependency Resolution Sources]] |

Coverage:

- candidates by type;
- qualifiers and `@Primary`;
- collection ordering;
- generics as qualifiers;
- `Optional`, `ObjectProvider` and lazy lookup.

# CORE-B03 — Bean lifecycle

| Role | Artifact |
|---|---|
| Concept | [[10_CONCEPTS/Spring/Core/Bean Lifecycle from Definition to Destruction]] |
| Canvas | [[01_MAPS/Spring Bean Lifecycle Map.canvas]] |
| Cards | [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B03/CORE-B03 Cards]] |
| Cases | [[40_PRODUCTION_CASES/Spring/Bean Lifecycle Production Cases]] |
| Lab | [[50_LABS/Spring/Core-B03/README]] |
| Sources | [[98_SOURCES/Spring Bean Lifecycle Sources]] |

Coverage:

- instantiation and dependency population;
- aware callbacks;
- before/after initialization processors;
- init and destroy callbacks;
- proxy publication.

# CORE-B04 — Container extension points

| Role | Artifact |
|---|---|
| Concept | [[10_CONCEPTS/Spring/Core/Container Extension Points]] |
| Canvas | [[01_MAPS/Spring Container Extension Points Map.canvas]] |
| Cards | [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B04/CORE-B04 Cards]] |
| Cases | [[40_PRODUCTION_CASES/Spring/Container Extension Point Production Cases]] |
| Lab | [[50_LABS/Spring/Core-B04/README]] |
| Sources | [[98_SOURCES/Spring Container Extension Point Sources]] |

Coverage:

- `BeanDefinitionRegistryPostProcessor`;
- `BeanFactoryPostProcessor`;
- `BeanPostProcessor`;
- ordering;
- instantiation-aware hooks;
- early references and auto-proxy infrastructure.

# CORE-B05 — Configuration, profiles and properties

| Role | Artifact |
|---|---|
| Concept | [[10_CONCEPTS/Spring/Core/Configuration Profiles and Externalized Properties]] |
| Canvas | [[01_MAPS/Spring Configuration and Profiles Map.canvas]] |
| Cards | [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B05/CORE-B05 Cards]] |
| Cases | [[40_PRODUCTION_CASES/Spring/Configuration and Profiles Production Cases]] |
| Lab | [[50_LABS/Spring/Core-B05/README]] |
| Sources | [[98_SOURCES/Spring Configuration and Profiles Sources]] |

Coverage:

- full versus lite configuration;
- imports;
- profiles and conditions;
- `Environment`;
- property-source precedence;
- placeholders and type-safe binding.

# CORE-B06 — Scopes, FactoryBean and hierarchy

| Role | Artifact |
|---|---|
| Concept | [[10_CONCEPTS/Spring/Core/Advanced Core Scopes FactoryBean and Context Hierarchy]] |
| Canvas | [[01_MAPS/Spring Advanced Core Map.canvas]] |
| Cards | [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B06/CORE-B06 Cards]] |
| Cases | [[40_PRODUCTION_CASES/Spring/Advanced Core Production Cases]] |
| Lab | [[50_LABS/Spring/Core-B06/README]] |
| Sources | [[98_SOURCES/Spring Advanced Core Sources]] |

Coverage:

- singleton/prototype identity and ownership;
- request/session/custom scopes;
- scoped proxies;
- `FactoryBean` product versus factory;
- `&beanName`;
- lazy initialization;
- circular dependencies and early references;
- parent/child context lookup and shadowing;
- `Resource`, `MessageSource` and lifecycle ownership.

# Completion definition

Spring Core route is complete when the learner can:

1. trace metadata into `BeanDefinition` and a ready bean;
2. explain candidate resolution without guessing;
3. identify the exact lifecycle/extension phase;
4. reason about identity, lookup timing and ownership;
5. explain why a proxy or scoped target is returned;
6. diagnose startup failure through evidence;
7. predict and run at least one lab trace per batch.

# Quality status

- [x] 140 cards linked from one roadmap.
- [x] Six canonical vertical slices.
- [x] Seven connected visual entry points.
- [x] Production cases linked for CORE-B02–B06.
- [x] Labs linked for CORE-B02–B06.
- [x] Primary source indexes linked for CORE-B02–B06.
- [x] Central registry and domain MOC backlinks.
- [x] Machine-readable route manifest.
- [ ] CORE-B01 dedicated production case/lab/source vertical slice.
- [ ] Four remaining Core cards normalized by mandatory-section audit.
- [ ] Full Maven runtime pass for all labs.
