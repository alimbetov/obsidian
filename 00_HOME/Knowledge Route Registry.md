---
type: route-registry
domain: knowledge-system
status: active
tags:
  - navigation
  - roadmap
  - knowledge-graph
  - certification
---

# Knowledge Route Registry

> [!summary]
> Единая точка навигации по published learning routes. Каждый новый route должен иметь objective evidence, stable card IDs, progress compatibility, cross-links и executable proof.

# Global entry points

- [[00_HOME/Certification 99 Percent Readiness Dashboard]]
- [[00_HOME/Java 11 17 21 Complete Knowledge Program]]
- [[00_HOME/Card Review Dashboard]]
- [[30_CERTIFICATIONS/Certification MOC]]
- [[00_HOME/Review Dashboard]]
- [[01_MAPS/Java Backend Map.canvas]]
- [[90_TEMPLATES/Cross-Linking Standard]]
- [[70_PROGRESS/README]]

```mermaid
flowchart LR
    OBJ["Objective"] --> PRE["Pre-test"]
    PRE --> ROAD["Route roadmap"]
    ROAD --> CONCEPT["Canonical + visual"]
    CONCEPT --> CARDS["Stable card IDs"]
    CARDS --> CASES["Cases"]
    CASES --> LAB["Lab"]
    LAB --> POST["Post-test"]
    POST --> PROGRESS["Per-card progress"]
```

# Certification and platform tracks

| Track | Master roadmap | Target |
|---|---|---:|
| Spring 2V0-72.22 | [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring 99 Percent Master Roadmap]] | 99% |
| Java 11/17/21 platform | [[00_HOME/Java 11 17 21 Complete Knowledge Program]] | 99% |
| Java 1Z0-829 | [[30_CERTIFICATIONS/Java/1Z0-829/Java SE 17 99 Percent Master Roadmap]] | 99% |
| Java Concurrency | [[30_CERTIFICATIONS/Java/Concurrency/Java Concurrency 99 Percent Roadmap]] | 99% |

# Java routes

## JAVA-LTS-B01 — Java 11, 17 and 21 Evolution and Migration

| Role | Artifact |
|---|---|
| Program | [[00_HOME/Java 11 17 21 Complete Knowledge Program]] |
| Roadmap | [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Roadmap]] |
| Canonical | [[10_CONCEPTS/Java/Versions/Java 11 17 21 LTS Evolution]] |
| Cards | [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Cards]] |
| Cases | [[40_PRODUCTION_CASES/Java/Java 11 17 21 Migration Cases]] |
| Lab | [[50_LABS/Java/JAVA-LTS-B01/README]] |
| Sources | [[98_SOURCES/Java 11 17 21 Official Sources]] |
| Domain map | [[01_MAPS/Java Map]] |
| Progress | [[70_PROGRESS/README]] |

Machine controls:

```text
.github/java-version-coverage.json
.github/scripts/audit_java_version_coverage.py
JDK 11 / 17 / 21 GitHub Actions matrix
```

## JAVA-CONCURRENCY

| Role | Artifact |
|---|---|
| Domain map | [[01_MAPS/Java Map]] |
| Learning path | [[10_CONCEPTS/Java/Concurrency/Concurrency Learning Path]] |
| 99% roadmap | [[30_CERTIFICATIONS/Java/Concurrency/Java Concurrency 99 Percent Roadmap]] |
| Visual | [[10_CONCEPTS/Java/Concurrency/Java Concurrency Visual Deep Dive]] |
| Recall | [[20_QUESTIONS/Interview/Java/Concurrency/Advanced Concurrency Recall]] |
| Lab | [[50_LABS/Java/Concurrency/README]] |
| Sources | [[98_SOURCES/Java Concurrency Sources]] |

## JAVA-1Z0-829

- [[30_CERTIFICATIONS/Java/1Z0-829/Java SE 17 99 Percent Master Roadmap]]
- [[98_SOURCES/Java SE 17 1Z0-829 Sources]]
- Exact exam baseline: Java 17.
- Required context: Java 11 compatibility baseline and Java 21 production delta.
- Status: `JAVA-LTS-B01` published; ten Java 17 domain routes remain planned.

# Spring routes

## SPRING-CORE

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Core Card Roadmap]]
- [[10_CONCEPTS/Spring/Core/Spring Core Visual Deep Dive]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B01/CORE-B01 Cards]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B04/CORE-B04 Cards]]

## SPRING-AOP-CACHE

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring AOP and Cache Roadmap]]
- [[10_CONCEPTS/Spring/AOP/Spring AOP Proxy Mechanics]]
- [[10_CONCEPTS/Spring/Cache/Spring Cache with Caffeine and Redis]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/AOP-B01/AOP-B01 Cards]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CACHE-B01/CACHE-B01 Cards]]

## SPRING-TX

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Transaction Management Roadmap]]
- [[10_CONCEPTS/Spring/Transactions/Spring Transaction Management Deep Dive]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/TX-B01/TX-B01 Cards]]
- [[50_LABS/Spring/TX-B01/README]]

## SPRING-DATA-JPA

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Data JPA Roadmap]]
- [[10_CONCEPTS/Spring/Data/Spring Data JPA Persistence Context and Entity Lifecycle]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/DATA-B01/DATA-B01 Cards]]
- [[50_LABS/Spring/DATA-B01/README]]

## SPRING-TEST

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Testing Roadmap]]
- [[10_CONCEPTS/Spring/Testing/Spring TestContext and Test Slices]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/TEST-B01/TEST-B01 Cards]]
- [[50_LABS/Spring/TEST-B01/README]]

## SPRING-BOOT-B01 — Bootstrap and Auto-configuration

| Role | Artifact |
|---|---|
| Roadmap | [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B01/SPRING-BOOT-B01 Roadmap]] |
| Canonical | [[10_CONCEPTS/Spring/Boot/Spring Boot Bootstrap and Auto-configuration]] |
| Visual | [[10_CONCEPTS/Spring/Boot/Spring Boot Auto-configuration Visual Deep Dive]] |
| Cards | [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B01/SPRING-BOOT-B01 Cards]] |
| Cases | [[40_PRODUCTION_CASES/Spring/Spring Boot Auto-configuration Production Cases]] |
| Lab | [[50_LABS/Spring/SPRING-BOOT-B01/README]] |
| Canvas | [[01_MAPS/Spring Boot Auto-configuration Map.canvas]] |
| Sources | [[98_SOURCES/Spring Boot Auto-configuration Sources]] |

## SPRING-BOOT-B02 — Externalized Configuration

| Role | Artifact |
|---|---|
| Roadmap | [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Roadmap]] |
| Canonical | [[10_CONCEPTS/Spring/Boot/Spring Boot Externalized Configuration and Type-safe Binding]] |
| Visual | [[10_CONCEPTS/Spring/Boot/Spring Boot Configuration Visual Deep Dive]] |
| Cards | [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Cards]] |
| Assessment | [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Assessment]] |
| Cases | [[40_PRODUCTION_CASES/Spring/Spring Boot Configuration Production Cases]] |
| Lab | [[50_LABS/Spring/SPRING-BOOT-B02/README]] |
| Canvas | [[01_MAPS/Spring Boot Configuration Map.canvas]] |
| Sources | [[98_SOURCES/Spring Boot Externalized Configuration Sources]] |

## SPRING-MVC-B01 — DispatcherServlet and Controller Pipeline

| Role | Artifact |
|---|---|
| Roadmap | [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Roadmap]] |
| Canonical | [[10_CONCEPTS/Spring/MVC/DispatcherServlet and Annotated Controller Pipeline]] |
| Visual | [[10_CONCEPTS/Spring/MVC/Spring MVC DispatcherServlet Visual Deep Dive]] |
| Cards | [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Cards]] |
| Assessment | [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Assessment]] |
| Cases | [[40_PRODUCTION_CASES/Spring/Spring MVC DispatcherServlet Production Cases]] |
| Lab | [[50_LABS/Spring/SPRING-MVC-B01/README]] |
| Canvas | [[01_MAPS/Spring MVC DispatcherServlet Map.canvas]] |
| Sources | [[98_SOURCES/Spring MVC DispatcherServlet Sources]] |

Next Spring route: `SPRING-MVC-B02 — REST Endpoints and HTTP Clients`.

# Database routes

## DB-B01 — Indexes and Query Plans

- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Roadmap]]
- [[10_CONCEPTS/Databases/PostgreSQL Index Mechanics]]
- [[10_CONCEPTS/Databases/PostgreSQL EXPLAIN and Query Plan Analysis]]
- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Cards]]
- [[40_PRODUCTION_CASES/Databases/Indexes and Query Plans Production Cases]]
- [[50_LABS/Databases/DB-B01/README]]

# Planned routes

| ID | Route | Status |
|---|---|---|
| JAVA-B01…B11 | Java 17 exam domains with Java 11/21 deltas | next Java program |
| JAVA-JVM-B01 | Class loading, bytecode and runtime areas | planned |
| JAVA-GC-B01 | G1, ZGC and Generational ZGC | planned |
| JAVA-DIAG-B01 | JFR, jcmd, dumps and profiling | planned |
| SPRING-MVC-B02 | REST and HTTP clients | next Spring route |
| SPRING-SEC-B01 | Security | planned |
| SPRING-ACT-B01 | Actuator | planned |
| SPRING-JDBC-B01 | JdbcTemplate | planned |
| SPRING-WEBTEST-B01 | MockMvc | planned |
| SPRING-SPEL-B01 | SpEL | planned |
| DB-B02 | MVCC and Locks | planned |

# Registry quality checklist

```text
[x] registry links every published route hub
[x] route lists canonical/cards/cases/lab/sources
[x] card IDs are unique and progress-compatible
[x] machine coverage manifest exists
[x] Java LTS runtime lanes declared
[ ] all Java 11/17/21 domains complete
[ ] no broken or ambiguous strict-route link — enforced by CI
```
