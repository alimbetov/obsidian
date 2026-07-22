# Java Backend Knowledge System

Структурированная Obsidian-база для Java backend: mechanism-oriented explanations, visual models, per-card progress, objective traceability, active recall, production incidents, executable labs и certification readiness.

# Главные входы

1. [[00_HOME/Certification 99 Percent Readiness Dashboard]] — Spring, Java и Concurrency readiness.
2. [[00_HOME/Card Review Dashboard]] — per-card review queue и spaced repetition.
3. [[00_HOME/Knowledge Route Registry]] — все published routes.
4. [[00_HOME/Java Backend Knowledge System]] — учебный вход.
5. [[30_CERTIFICATIONS/Certification MOC]] — certification navigation.
6. [[01_MAPS/Java Backend Map.canvas]] — общая spatial map.

# Corrected learning model

```text
Official objective
    ↓
Pre-test
    ↓
Canonical mechanism
    ↓
Visual deep dive / Canvas
    ↓
Stable card IDs + per-card progress
    ↓
Production cases
    ↓
Executable lab
    ↓
Post-test
    ↓
Timed mock + delayed review
```

System components:

```text
70_PROGRESS/card-progress.json
.github/scripts/card_progress.py
.github/objectives/*.json
.github/objective-overrides/*.json
.github/scripts/audit_objective_traceability.py
.github/scripts/audit_certification_readiness.py
```

# 99 percent targets

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

# Per-card progress

Cards may remain in batch files, but learning state is stored by stable `card_id`.

```bash
python .github/scripts/card_progress.py audit
python .github/scripts/card_progress.py sync
python .github/scripts/card_progress.py due --limit 30
python .github/scripts/card_progress.py record \
  --card-id SPRING-MVC-B01-C001 \
  --outcome correct-confident \
  --confidence 4
```

Details:

- [[70_PROGRESS/README]]
- [[00_HOME/Card Review Dashboard]]

# Objective traceability

Traceability manifests map official/capability objectives to:

```text
canonical notes
visual models
card IDs
production cases
labs
sources
mocks
```

Current manifests:

- Spring `2V0-72.22`: 57 detailed objectives;
- Java `1Z0-829`: 11 capability domains;
- Java Concurrency: 8 advanced objectives.

Readiness formula:

```text
25% objective traceability
75% vertical-slice artifact/card completeness
```

# Visual layer

```text
Spring Core                         26 diagrams
AOP                                 20 diagrams
Cache                               27 diagrams
Transactions                        20 diagrams
Spring Data JPA                     31 diagrams
Spring Testing                      24 diagrams
Spring Boot Auto-configuration      31 diagrams
Spring Boot Configuration           30 diagrams
Spring MVC DispatcherServlet        30 diagrams
Java Concurrency                    35 diagrams
DB-B01                              62 diagrams
Standard example                     1 diagram
Canvas atlases                       8 maps
------------------------------------------------
Visual elements                    346
```

Visual entry points:

- [[01_MAPS/Spring Core Visual Atlas.canvas]]
- [[01_MAPS/Spring AOP and Cache Visual Atlas.canvas]]
- [[01_MAPS/Spring Boot Auto-configuration Map.canvas]]
- [[01_MAPS/Spring Boot Configuration Map.canvas]]
- [[01_MAPS/Spring MVC DispatcherServlet Map.canvas]]
- [[01_MAPS/Java Concurrency Visual Atlas.canvas]]
- [[01_MAPS/Database Indexes and Query Plans Map.canvas]]
- [[01_MAPS/Certification 99 Percent Map.canvas]]

# Published Spring routes

## Spring Core

```text
CORE-B01  20 normalized
CORE-B02  24
CORE-B03  24
CORE-B04  24 normalized
CORE-B05  24
CORE-B06  24
----------------------
TOTAL    140 cards
```

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Core Card Roadmap]]
- [[10_CONCEPTS/Spring/Core/Spring Core Visual Deep Dive]]

## AOP and Cache

```text
AOP-B01    24 normalized
CACHE-B01  20 normalized
```

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring AOP and Cache Roadmap]]

## Transaction Management

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Transaction Management Roadmap]]
- `TX-B01`: 32 normalized cards.

## Spring Data and JPA

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Data JPA Roadmap]]
- `DATA-B01`: 36 normalized cards.

## Spring Testing

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Testing Roadmap]]
- `TEST-B01`: 36 normalized cards.

## SPRING-BOOT-B01 — Bootstrap and Auto-configuration

```text
31 diagrams
30 cards
15 production cases
6 ApplicationContextRunner tests
```

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B01/SPRING-BOOT-B01 Roadmap]]
- [[10_CONCEPTS/Spring/Boot/Spring Boot Bootstrap and Auto-configuration]]
- [[50_LABS/Spring/SPRING-BOOT-B01/README]]

## SPRING-BOOT-B02 — Externalized Configuration

```text
Canonical sections       44
Visual diagrams          30
Cards                    35
Pre-test questions       10
Post-test questions      15
Production cases         12
Executable tests          7
Canvas                    1
Source index              1
```

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Roadmap]]
- [[10_CONCEPTS/Spring/Boot/Spring Boot Externalized Configuration and Type-safe Binding]]
- [[10_CONCEPTS/Spring/Boot/Spring Boot Configuration Visual Deep Dive]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Cards]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Assessment]]
- [[40_PRODUCTION_CASES/Spring/Spring Boot Configuration Production Cases]]
- [[50_LABS/Spring/SPRING-BOOT-B02/README]]
- [[01_MAPS/Spring Boot Configuration Map.canvas]]
- [[98_SOURCES/Spring Boot Externalized Configuration Sources]]

## SPRING-MVC-B01 — DispatcherServlet and Controller Pipeline

```text
Canonical sections       50
Visual diagrams          30
Cards                    35
Pre-test questions       10
Post-test questions      15
Production cases         12
Executable tests         11
Canvas                    1
Source index              1
```

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Roadmap]]
- [[10_CONCEPTS/Spring/MVC/DispatcherServlet and Annotated Controller Pipeline]]
- [[10_CONCEPTS/Spring/MVC/Spring MVC DispatcherServlet Visual Deep Dive]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Cards]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Assessment]]
- [[40_PRODUCTION_CASES/Spring/Spring MVC DispatcherServlet Production Cases]]
- [[50_LABS/Spring/SPRING-MVC-B01/README]]
- [[01_MAPS/Spring MVC DispatcherServlet Map.canvas]]
- [[98_SOURCES/Spring MVC DispatcherServlet Sources]]

# Published Spring total

```text
Spring Core               140
AOP and Cache               44
Transaction Management      32
Spring Data and JPA          36
Spring Testing               36
Spring Boot B01              30
Spring Boot B02              35
Spring MVC B01               35
-------------------------------
TOTAL                       388 cards
```

# Java Concurrency foundation

- [[30_CERTIFICATIONS/Java/Concurrency/Java Concurrency 99 Percent Roadmap]]
- [[10_CONCEPTS/Java/Concurrency/Concurrency Learning Path]]
- [[10_CONCEPTS/Java/Concurrency/Java Concurrency Visual Deep Dive]]
- [[50_LABS/Java/Concurrency/README]]

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

- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Roadmap]]
- 2 canonical notes;
- 62 diagrams;
- 30 cards;
- 14 production cases;
- 10 PostgreSQL experiments.

# Running Spring route labs

```bash
mvn clean test -f 50_LABS/Spring/SPRING-BOOT-B01/pom.xml
mvn clean test -f 50_LABS/Spring/SPRING-BOOT-B02/pom.xml
mvn clean test -f 50_LABS/Spring/SPRING-MVC-B01/pom.xml
```

# Quality controls

```text
structural Markdown/card audit
cross-link graph with route overrides
per-card progress tests and catalog audit
objective traceability audit
combined readiness calculation
Boot B01/B02 Maven tests
MVC B01 DispatcherServlet pipeline tests
Canvas validation
full Mermaid rendering
strict published-card contract
```

# Current delivery sequence

```text
SPRING-MVC-B02
SPRING-SEC-B01
SPRING-ACT-B01
SPRING-JDBC-B01
SPRING-WEBTEST-B01
SPRING-SPEL-B01
Spring drill bank and mocks
JAVA-B01 … JAVA-B11
Java drill bank and mocks
Concurrency consolidation and mini-mocks
```
