# Java Backend Knowledge System

Структурированная Obsidian-база для Java backend: mechanism-oriented explanations, visual models, active recall, production incidents, executable labs и измеряемая certification readiness.

# Главные входы

1. [[00_HOME/Certification 99 Percent Readiness Dashboard]] — Spring, Java и Concurrency readiness.
2. [[00_HOME/Java Backend Knowledge System]] — основной учебный вход.
3. [[00_HOME/Knowledge Route Registry]] — реестр опубликованных routes.
4. [[00_HOME/Review Dashboard]] — ежедневное повторение.
5. [[30_CERTIFICATIONS/Certification MOC]] — certification navigation.
6. [[01_MAPS/Java Backend Map.canvas]] — общая spatial map.

# 99 percent certification program

```text
Spring 2V0-72.22
  600 base cards
  150 drill cards
  6 full timed mocks

Java 1Z0-829
  720 base cards
  180 drill cards
  6 full timed mocks

Java Concurrency
  140 base cards
  40 drill cards
  20 production cases
  25 labs
  6 timed mini-mocks
```

Master roadmaps:

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring 99 Percent Master Roadmap]]
- [[30_CERTIFICATIONS/Java/1Z0-829/Java SE 17 99 Percent Master Roadmap]]
- [[30_CERTIFICATIONS/Java/Concurrency/Java Concurrency 99 Percent Roadmap]]

Readiness is machine-calculated by:

```text
.github/certification-readiness.json
.github/scripts/audit_certification_readiness.py
```

Low readiness is not a CI failure. Invalid weights, missing declared artifacts or a falsely published incomplete route are CI failures.

# Knowledge model

```text
Official objective
    ↓
Canonical concept
    ↓
Visual deep dive / Canvas
    ↓
Base cards
    ↓
Exam drills
    ↓
Production cases
    ↓
Executable lab
    ↓
Timed mock and review outcome
```

Cross-linking: [[90_TEMPLATES/Cross-Linking Standard]].

Visual standard: [[90_TEMPLATES/Pedagogical Visual Standard]].

# Current visual layer

```text
Spring Core                         26 diagrams
AOP                                 20 diagrams
Cache                               27 diagrams
Transactions                        20 diagrams
Spring Data JPA                     31 diagrams
Spring Testing                      24 diagrams
Spring Boot Auto-configuration      31 diagrams
Java Concurrency                    35 diagrams
DB-B01                              62 diagrams
Standard example                     1 diagram
Canvas atlases                       6 maps
------------------------------------------------
Visual elements                    284
```

Visual entry points:

- [[01_MAPS/Spring Visual Learning Atlas.canvas]]
- [[01_MAPS/Spring Core Visual Atlas.canvas]]
- [[01_MAPS/Spring AOP and Cache Visual Atlas.canvas]]
- [[01_MAPS/Spring Boot Auto-configuration Map.canvas]]
- [[01_MAPS/Java Concurrency Visual Atlas.canvas]]
- [[01_MAPS/Database Indexes and Query Plans Map.canvas]]

# Published Spring routes

## Spring Core

```text
CORE-B01  20
CORE-B02  24
CORE-B03  24
CORE-B04  24
CORE-B05  24
CORE-B06  24
----------------
TOTAL    140 cards
```

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Core Card Roadmap]]
- [[10_CONCEPTS/Spring/Core/Spring Core Visual Deep Dive]]
- [[01_MAPS/Spring Core Visual Atlas.canvas]]

## AOP and Cache

```text
AOP-B01    24 normalized cards
CACHE-B01  20 normalized cards
```

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring AOP and Cache Roadmap]]
- [[10_CONCEPTS/Spring/AOP/Spring AOP Visual Deep Dive]]
- [[10_CONCEPTS/Spring/Cache/Spring Cache Visual Deep Dive]]

## Transaction Management

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Transaction Management Roadmap]]
- `TX-B01`: 32 cards.
- Transaction proxy, propagation, rollback, callbacks and Outbox.

## Spring Data and JPA

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Data JPA Roadmap]]
- `DATA-B01`: 36 cards.
- Persistence context, repositories, fetching, pagination and locking.

## Spring Testing

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Testing Roadmap]]
- `TEST-B01`: 36 cards.
- TestContext, slices, transaction proof and Testcontainers.

## SPRING-BOOT-B01 — Bootstrap and Auto-configuration

```text
Canonical note        1
Visual deep dive      1
Mermaid diagrams     31
Cards                30
Production cases     15
Boot 2.5 lab          1
Canvas                1
Source index          1
```

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B01/SPRING-BOOT-B01 Roadmap]]
- [[10_CONCEPTS/Spring/Boot/Spring Boot Bootstrap and Auto-configuration]]
- [[10_CONCEPTS/Spring/Boot/Spring Boot Auto-configuration Visual Deep Dive]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B01/SPRING-BOOT-B01 Cards]]
- [[40_PRODUCTION_CASES/Spring/Spring Boot Auto-configuration Production Cases]]
- [[50_LABS/Spring/SPRING-BOOT-B01/README]]
- [[01_MAPS/Spring Boot Auto-configuration Map.canvas]]
- [[98_SOURCES/Spring Boot Auto-configuration Sources]]

Coverage:

```text
@SpringBootApplication
SpringApplication bootstrap
Environment and WebApplicationType
@EnableAutoConfiguration
AutoConfigurationImportSelector
conditions, exclusions and back-off
Condition Evaluation Report
starters and dependency management
Boot 2.x spring.factories
current AutoConfiguration.imports delta
ApplicationContextRunner
failure analyzers and runners
```

# Published Spring total

```text
Spring Core               140
AOP and Cache               44
Transaction Management      32
Spring Data and JPA          36
Spring Testing               36
Spring Boot B01              30
-------------------------------
TOTAL                       318 cards
```

# Java Concurrency foundation

- [[30_CERTIFICATIONS/Java/Concurrency/Java Concurrency 99 Percent Roadmap]]
- [[10_CONCEPTS/Java/Concurrency/Concurrency Learning Path]]
- [[10_CONCEPTS/Java/Concurrency/Java Concurrency Visual Deep Dive]]
- [[01_MAPS/Java Concurrency Visual Atlas.canvas]]
- [[20_QUESTIONS/Interview/Java/Concurrency/Advanced Concurrency Recall]]
- [[50_LABS/Java/Concurrency/README]]

Current conceptual coverage:

```text
thread lifecycle
JMM and happens-before
volatile, monitors and locks
CAS and atomics
executors and backpressure
Future and CompletableFuture
ForkJoinPool
ThreadLocal
virtual threads
concurrent collections
liveness diagnostics
```

Still required for 99%:

```text
140-card dedicated bank
40 drills
20 consolidated production cases
25 controlled labs
6 timed mini-mocks
```

# Java 1Z0-829 route

Master roadmap: [[30_CERTIFICATIONS/Java/1Z0-829/Java SE 17 99 Percent Master Roadmap]].

Planned domains:

```text
JAVA-B01 Data/Text/Date-Time
JAVA-B02 Control Flow
JAVA-B03 Object Model
JAVA-B04 Exceptions
JAVA-B05 Collections and Generics
JAVA-B06 Lambdas and Streams
JAVA-B07 Modules and Deployment
JAVA-B08 Concurrency Exam Objectives
JAVA-B09 I/O and NIO.2
JAVA-B10 JDBC
JAVA-B11 Localization
```

# Database route

## DB-B01 — Indexes and Query Plans

```text
2 canonical notes
62 diagrams
30 cards
14 production cases
10 PostgreSQL experiments
1 Canvas
```

- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Roadmap]]
- [[10_CONCEPTS/Databases/PostgreSQL Index Mechanics]]
- [[10_CONCEPTS/Databases/PostgreSQL EXPLAIN and Query Plan Analysis]]
- [[50_LABS/Databases/DB-B01/README]]

# Running the new Boot lab

```bash
cd 50_LABS/Spring/SPRING-BOOT-B01
mvn clean test
```

The GitHub Actions quality workflow executes this lab automatically.

# Quality controls

```text
.github/scripts/audit_vault_v2.py
.github/scripts/audit_cross_links.py
.github/scripts/audit_certification_readiness.py
.github/workflows/vault-quality-audit.yml
```

The gate checks:

- Markdown structure;
- wikilinks and route graph;
- Canvas JSON/references/geometry;
- real Mermaid rendering;
- strict published-card contracts;
- readiness-model integrity;
- SPRING-BOOT-B01 Maven tests.

# Current delivery sequence

```text
SPRING-BOOT-B02
SPRING-MVC-B01
SPRING-MVC-B02
SPRING-SEC-B01
SPRING-ACT-B01
SPRING-JDBC-B01
SPRING-WEBTEST-B01
SPRING-SPEL-B01
Spring legacy-card normalization
Spring drill bank and mocks
JAVA-B01 … JAVA-B11
Java drill bank and mocks
Concurrency consolidation and mini-mocks
```
