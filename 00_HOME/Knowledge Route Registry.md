---
type: route-registry
domain: knowledge-system
status: active
tags:
  - navigation
  - roadmap
  - knowledge-graph
---

# Knowledge Route Registry

> [!summary]
> Единая точка навигации по опубликованным learning routes. Каждый route связывает domain map, roadmap, canonical theory, visual models, active recall, production cases, executable lab и primary sources.

# How to use the registry

```mermaid
flowchart LR
    README["Repository README"] --> REG["Knowledge Route Registry"]
    REG --> MOC["Domain MOC"]
    MOC --> ROAD["Route roadmap"]
    ROAD --> CONCEPT["Canonical concepts"]
    CONCEPT --> VISUAL["Visual models"]
    VISUAL --> CARDS["Cards / recall"]
    CARDS --> CASES["Production cases"]
    CASES --> LAB["Executable lab"]
    LAB --> SOURCES["Primary sources"]
```

Cross-linking rules: [[90_TEMPLATES/Cross-Linking Standard]].

# Java routes

## JAVA-CONCURRENCY — Java Concurrency

| Role | Artifact |
|---|---|
| Domain map | [[01_MAPS/Java Map]] |
| Learning path | [[10_CONCEPTS/Java/Concurrency/Concurrency Learning Path]] |
| Visual deep dive | [[10_CONCEPTS/Java/Concurrency/Java Concurrency Visual Deep Dive]] |
| Visual atlas | [[01_MAPS/Java Concurrency Visual Atlas.canvas]] |
| Foundation canvas | [[01_MAPS/Java Concurrency Map.canvas]] |
| Advanced canvas | [[01_MAPS/Java Advanced Concurrency Map.canvas]] |
| Recall | [[20_QUESTIONS/Interview/Java/Concurrency/Advanced Concurrency Recall]] |
| Lab | [[50_LABS/Java/Concurrency/README]] |
| Sources | [[98_SOURCES/Java Concurrency Sources]] |
| Advanced sources | [[98_SOURCES/Advanced Concurrency Sources]] |

Canonical concepts:

- [[10_CONCEPTS/Java/Concurrency/Threads]];
- [[10_CONCEPTS/Java/Concurrency/Visibility Atomicity Ordering]];
- [[10_CONCEPTS/Java/Concurrency/Race Condition]];
- [[10_CONCEPTS/Java/Concurrency/Java Memory Model]];
- [[10_CONCEPTS/Java/Concurrency/Happens-Before]];
- [[10_CONCEPTS/Java/Concurrency/volatile]];
- [[10_CONCEPTS/Java/Concurrency/synchronized]];
- [[10_CONCEPTS/Java/Concurrency/ReentrantLock]];
- [[10_CONCEPTS/Java/Concurrency/Atomic CAS and Counters]];
- [[10_CONCEPTS/Java/Concurrency/Deadlock Livelock and Lock Ordering]];
- [[10_CONCEPTS/Java/Concurrency/Concurrent Collections and Backpressure]];
- [[10_CONCEPTS/Java/Concurrency/ExecutorService]];
- [[10_CONCEPTS/Java/Concurrency/Future]];
- [[10_CONCEPTS/Java/Concurrency/ForkJoinPool]];
- [[10_CONCEPTS/Java/Concurrency/CompletableFuture]];
- [[10_CONCEPTS/Java/Concurrency/ThreadLocal]];
- [[10_CONCEPTS/Java/Concurrency/Virtual Threads]].

Next adjacent domains:

- Java Language — planned;
- Java Collections — planned;
- JVM Internals — planned.

# Spring routes

## SPRING-CORE — Spring Core

| Role | Artifact |
|---|---|
| Domain map | [[01_MAPS/Spring Map]] |
| Roadmap | [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Core Card Roadmap]] |
| Visual deep dive | [[10_CONCEPTS/Spring/Core/Spring Core Visual Deep Dive]] |
| Visual atlas | [[01_MAPS/Spring Core Visual Atlas.canvas]] |
| Review dashboard | [[00_HOME/Review Dashboard]] |

### CORE-B01 — IoC, registration and injection

- Concept: [[10_CONCEPTS/Spring/Core/Spring Core Foundations]];
- Canvas: [[01_MAPS/Spring Core Foundation Map.canvas]];
- Cards: [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B01/CORE-B01 Cards]].

### CORE-B02 — Candidate resolution

- Concept: [[10_CONCEPTS/Spring/Core/Dependency Resolution and Optional Injection]];
- Canvas: [[01_MAPS/Spring Dependency Resolution Map.canvas]];
- Cards: [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B02/CORE-B02 Cards]];
- Cases: [[40_PRODUCTION_CASES/Spring/Dependency Resolution Production Cases]];
- Lab: [[50_LABS/Spring/Core-B02/README]];
- Sources: [[98_SOURCES/Spring Dependency Resolution Sources]].

### CORE-B03 — Bean lifecycle

- Concept: [[10_CONCEPTS/Spring/Core/Bean Lifecycle from Definition to Destruction]];
- Canvas: [[01_MAPS/Spring Bean Lifecycle Map.canvas]];
- Cards: [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B03/CORE-B03 Cards]];
- Cases: [[40_PRODUCTION_CASES/Spring/Bean Lifecycle Production Cases]];
- Lab: [[50_LABS/Spring/Core-B03/README]];
- Sources: [[98_SOURCES/Spring Bean Lifecycle Sources]].

### CORE-B04 — Container extension points

- Concept: [[10_CONCEPTS/Spring/Core/Container Extension Points]];
- Canvas: [[01_MAPS/Spring Container Extension Points Map.canvas]];
- Cards: [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B04/CORE-B04 Cards]];
- Cases: [[40_PRODUCTION_CASES/Spring/Container Extension Point Production Cases]];
- Lab: [[50_LABS/Spring/Core-B04/README]];
- Sources: [[98_SOURCES/Spring Container Extension Point Sources]].

### CORE-B05 — Configuration and profiles

- Concept: [[10_CONCEPTS/Spring/Core/Configuration Profiles and Externalized Properties]];
- Canvas: [[01_MAPS/Spring Configuration and Profiles Map.canvas]];
- Cards: [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B05/CORE-B05 Cards]];
- Cases: [[40_PRODUCTION_CASES/Spring/Configuration and Profiles Production Cases]];
- Lab: [[50_LABS/Spring/Core-B05/README]];
- Sources: [[98_SOURCES/Spring Configuration and Profiles Sources]].

### CORE-B06 — Scopes, FactoryBean and hierarchy

- Concept: [[10_CONCEPTS/Spring/Core/Advanced Core Scopes FactoryBean and Context Hierarchy]];
- Canvas: [[01_MAPS/Spring Advanced Core Map.canvas]];
- Cards: [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B06/CORE-B06 Cards]];
- Cases: [[40_PRODUCTION_CASES/Spring/Advanced Core Production Cases]];
- Lab: [[50_LABS/Spring/Core-B06/README]];
- Sources: [[98_SOURCES/Spring Advanced Core Sources]].

## SPRING-AOP-CACHE — AOP, Caffeine and Redis

| Role | Artifact |
|---|---|
| Roadmap | [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring AOP and Cache Roadmap]] |
| AOP canonical | [[10_CONCEPTS/Spring/AOP/Spring AOP Proxy Mechanics]] |
| AOP visual | [[10_CONCEPTS/Spring/AOP/Spring AOP Visual Deep Dive]] |
| Cache canonical | [[10_CONCEPTS/Spring/Cache/Spring Cache with Caffeine and Redis]] |
| Cache visual | [[10_CONCEPTS/Spring/Cache/Spring Cache Visual Deep Dive]] |
| Shared bridge | [[10_CONCEPTS/Spring/AOP/Spring AOP Proxies and Cache Interception]] |
| Visual atlas | [[01_MAPS/Spring AOP and Cache Visual Atlas.canvas]] |
| Runtime canvas | [[01_MAPS/Spring AOP and Caching Map.canvas]] |
| AOP cards | [[30_CERTIFICATIONS/Spring/2V0-72.22/AOP-B01/AOP-B01 Cards]] |
| Cache cards | [[30_CERTIFICATIONS/Spring/2V0-72.22/CACHE-B01/CACHE-B01 Cards]] |
| Cases | [[40_PRODUCTION_CASES/Spring/AOP and Cache Production Cases]] |
| AOP lab | [[50_LABS/Spring/AOP-B01/README]] |
| Cache lab | [[50_LABS/Spring/CACHE-B01/README]] |
| Sources | [[98_SOURCES/Spring AOP and Cache Sources]] |

Previous: [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Core Card Roadmap]].

Next: [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Transaction Management Roadmap]].

## SPRING-TX — Transaction Management

| Role | Artifact |
|---|---|
| Roadmap | [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Transaction Management Roadmap]] |
| Canonical | [[10_CONCEPTS/Spring/Transactions/Spring Transaction Management Deep Dive]] |
| Visual deep dive | [[10_CONCEPTS/Spring/Transactions/Spring Transaction Management Visual Deep Dive]] |
| Outbox | [[10_CONCEPTS/Spring/Transactions/Transactional Outbox and Commit Boundaries]] |
| Canvas | [[01_MAPS/Spring Transaction Management Map.canvas]] |
| Cards | [[30_CERTIFICATIONS/Spring/2V0-72.22/TX-B01/TX-B01 Cards]] |
| Cases | [[40_PRODUCTION_CASES/Spring/Transaction Management Production Cases]] |
| Lab | [[50_LABS/Spring/TX-B01/README]] |
| Sources | [[98_SOURCES/Spring Transaction Management Sources]] |

Previous: [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring AOP and Cache Roadmap]].

Next: [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Data JPA Roadmap]].

## SPRING-DATA-JPA — Spring Data and JPA

| Role | Artifact |
|---|---|
| Roadmap | [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Data JPA Roadmap]] |
| Persistence canonical | [[10_CONCEPTS/Spring/Data/Spring Data JPA Persistence Context and Entity Lifecycle]] |
| Repository canonical | [[10_CONCEPTS/Spring/Data/Spring Data Repositories Queries and Fetching]] |
| Visual deep dive | [[10_CONCEPTS/Spring/Data/Spring Data JPA Visual Deep Dive]] |
| Canvas | [[01_MAPS/Spring Data JPA Map.canvas]] |
| Cards | [[30_CERTIFICATIONS/Spring/2V0-72.22/DATA-B01/DATA-B01 Cards]] |
| Cases | [[40_PRODUCTION_CASES/Spring/Spring Data JPA Production Cases]] |
| Lab | [[50_LABS/Spring/DATA-B01/README]] |
| Sources | [[98_SOURCES/Spring Data JPA Sources]] |

Previous: [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Transaction Management Roadmap]].

Next: [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Testing Roadmap]].

## SPRING-TEST — Spring Testing

| Role | Artifact |
|---|---|
| Roadmap | [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Testing Roadmap]] |
| TestContext canonical | [[10_CONCEPTS/Spring/Testing/Spring TestContext and Test Slices]] |
| JPA/Testcontainers canonical | [[10_CONCEPTS/Spring/Testing/Spring Data JPA Testing with Testcontainers]] |
| Visual deep dive | [[10_CONCEPTS/Spring/Testing/Spring Testing Visual Deep Dive]] |
| Canvas | [[01_MAPS/Spring Testing Map.canvas]] |
| Cards | [[30_CERTIFICATIONS/Spring/2V0-72.22/TEST-B01/TEST-B01 Cards]] |
| Cases | [[40_PRODUCTION_CASES/Spring/Spring Testing Production Cases]] |
| Lab | [[50_LABS/Spring/TEST-B01/README]] |
| Sources | [[98_SOURCES/Spring Testing Sources]] |

Previous: [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Data JPA Roadmap]].

Next Spring route: Spring Boot Internals and Auto-configuration — planned.

# Database routes

## DB-B01 — Indexes and Query Plans

| Role | Artifact |
|---|---|
| Domain map | [[01_MAPS/Databases Map]] |
| Roadmap | [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Roadmap]] |
| Index canonical | [[10_CONCEPTS/Databases/PostgreSQL Index Mechanics]] |
| Plan canonical | [[10_CONCEPTS/Databases/PostgreSQL EXPLAIN and Query Plan Analysis]] |
| Canvas | [[01_MAPS/Database Indexes and Query Plans Map.canvas]] |
| Cards | [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Cards]] |
| Cases | [[40_PRODUCTION_CASES/Databases/Indexes and Query Plans Production Cases]] |
| Lab | [[50_LABS/Databases/DB-B01/README]] |
| Sources | [[98_SOURCES/PostgreSQL Indexes and Query Plans Sources]] |

Previous bridge: [[10_CONCEPTS/Spring/Data/Spring Data JPA Visual Deep Dive]].

Next: DB-B02 — Transactions, MVCC and Locks — planned.

# Planned routes

| ID | Route | Status |
|---|---|---|
| DB-B02 | Transactions, MVCC and Locks | planned |
| SPRING-BOOT-B01 | Boot internals and auto-configuration | planned |
| JAVA-LANG-B01 | Language and object model | planned |
| JAVA-COLLECTIONS-B01 | Collections and HashMap internals | planned |
| JVM-B01 | Runtime memory, class loading, JIT and GC | planned |
| MSG-B01 | Kafka delivery, ordering and consumer groups | planned |
| DS-B01 | Resilience, consistency and distributed coordination | planned |

# Registry quality checklist

```text
[ ] Every published route is reachable from README
[ ] Every route is reachable from a domain MOC
[ ] Roadmap lists canonical/visual/cards/cases/lab/sources
[ ] Every published artifact has at least one inbound link
[ ] Canvas is linked from Markdown
[ ] Previous and next links reflect current status
[ ] No broken or ambiguous strict-route link
```

## Related quality materials

- [[90_TEMPLATES/Cross-Linking Standard]]
- [[90_TEMPLATES/Pedagogical Visual Standard]]
- [[99_AUDITS/Obsidian Learning Vault Quality Audit]]
