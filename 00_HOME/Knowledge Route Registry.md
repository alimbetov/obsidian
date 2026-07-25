---
type: route-registry
domain: knowledge-system
status: active
verified_at: 2026-07-25
tags:
  - navigation
  - roadmap
  - knowledge-graph
  - certification
---

# Knowledge Route Registry

> [!summary]
> Единая точка навигации по published learning routes. Каждый route должен иметь понятный вход для начинающего, canonical hub, focused concepts, stable cards, practice evidence, sources и progress path.

## Global entry points

- [[00_HOME/Java Beginner Learning Path]]
- [[00_HOME/Java Learning Cockpit]]
- [[00_HOME/Java Learning Dashboard]]
- [[00_HOME/Certification 99 Percent Readiness Dashboard]]
- [[00_HOME/Card Review Dashboard]]
- [[30_CERTIFICATIONS/Certification MOC]]
- [[01_MAPS/Java Certification Routes.canvas]]
- [[01_MAPS/Certification 99 Percent Map.canvas]]
- [[90_TEMPLATES/Cross-Linking Standard]]
- [[90_TEMPLATES/Student-Friendly Atomic Lesson Standard]]
- [[70_PROGRESS/README]]

## Published route contract

```text
Beginner path / Dashboard / MOC
    ↓
Route roadmap
    ↓
Beginner bridge or simple model
    ↓
Canonical hub
    ↓
Atomic concept notes
    ↓
Cards and drills
    ↓
Production cases / lab
    ↓
Sources and objective evidence
    ↓
Progress review
```

## Java certification program

### Master tracks

| Track | Roadmap | Status |
|---|---|---|
| Student-friendly Java | [[00_HOME/Java Beginner Learning Path]] | B01–B04 entry layer published |
| Java 11/17/21 platform | [[00_HOME/Java 11 17 21 Complete Knowledge Program]] | active |
| Oracle Java 17 + 21 | [[00_HOME/Oracle Java 17 and 21 Certification Program]] | active |
| Java `1Z0-829` | [[30_CERTIFICATIONS/Java/1Z0-829/Java SE 17 99 Percent Master Roadmap]] | B01–B04 lab-proven |
| Java `1Z0-830` | [[30_CERTIFICATIONS/Java/1Z0-830/Java SE 21 99 Percent Master Roadmap]] | B01–B04 lab-proven |
| Java Concurrency | [[30_CERTIFICATIONS/Java/Concurrency/Java Concurrency 99 Percent Roadmap]] | foundation published |

### JAVA-LTS-B01 — Java 11, 17 and 21 Evolution

| Role | Artifact |
|---|---|
| Roadmap | [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Roadmap]] |
| Canonical | [[10_CONCEPTS/Java/Versions/Java 11 17 21 LTS Evolution]] |
| Cards | [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Cards]] |
| Cases | [[40_PRODUCTION_CASES/Java/Java 11 17 21 Migration Cases]] |
| Lab | [[50_LABS/Java/JAVA-LTS-B01/README]] |
| Sources | [[98_SOURCES/Java 11 17 21 Official Sources]] |

Status: complete vertical slice with JDK 11/17/21 matrix.

### JAVA-B01 — Values, Text and Date-Time

| Role | Artifact |
|---|---|
| Beginner bridge | [[10_CONCEPTS/Java/Foundations/Java B01 Beginner Bridge]] |
| Roadmap | [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]] |
| Canonical hub | [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]] |
| Atomic concepts | 9 linked notes |
| Cards | 75 across values, text and date-time batches |
| Drills | [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills|15 drills]] |
| Lab | [[50_LABS/Java/JAVA-B01/README]] |
| Sources | [[98_SOURCES/Java SE 17 1Z0-829 Sources]], [[98_SOURCES/Java SE 21 1Z0-830 Sources]] |

Status: `lab-proven`, JDK 17/21 PASS.

### JAVA-B02 — Control Flow and Pattern Switch

| Role | Artifact |
|---|---|
| Beginner bridge | [[10_CONCEPTS/Java/Foundations/Java B02 Beginner Bridge]] |
| Roadmap | [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]] |
| Canonical hub | [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]] |
| Atomic concepts | 8 linked notes |
| Cards | 60 across flow, switch and pattern-switch batches |
| Drills | [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills|20 drills]] |
| Lab | [[50_LABS/Java/JAVA-B02/README]] |
| Negative evidence | 11 expected compile failures |
| Sources | [[98_SOURCES/Java SE 17 1Z0-829 Sources]], [[98_SOURCES/Java SE 21 1Z0-830 Sources]] |

Status: `lab-proven`, JDK 17/21 PASS.

### JAVA-B03 — Object Model, Records and Record Patterns

| Role | Artifact |
|---|---|
| Beginner bridge | [[10_CONCEPTS/Java/Foundations/Java B03 Beginner Bridge]] |
| Roadmap | [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]] |
| Canonical hub | [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]] |
| Atomic concepts | 12 linked notes |
| Canvas | [[01_MAPS/Java Object Model and Record Patterns Map.canvas]] |
| Cards | 115 across lifecycle, initialization, inheritance and type batches |
| Drills | [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills|35 drills]] |
| Lab | [[50_LABS/Java/JAVA-B03/README]] |
| Negative evidence | 17 expected compile failures |
| Sources | [[98_SOURCES/Java SE 17 1Z0-829 Sources]], [[98_SOURCES/Java SE 21 1Z0-830 Sources]] |

Status: `lab-proven`, JDK 17/21 PASS.

### JAVA-B04 — Exceptions and Resource Safety

| Role | Artifact |
|---|---|
| Roadmap / beginner entry | [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Roadmap]] |
| Canonical hub | [[10_CONCEPTS/Java/Exceptions/Java Exceptions and Resource Safety]] |
| Atomic concepts | 6 student-friendly notes |
| Cards | [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Cards|36 cards]] |
| Drills | [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Drills|20 drills]] |
| Production cases | [[40_PRODUCTION_CASES/Java/Java Exceptions Production Cases|8 cases]] |
| Lab | [[50_LABS/Java/JAVA-B04/README]] |
| Runtime proof | catch/finally, cause, close order, suppressed, initializer cleanup |
| Negative evidence | 6 expected compile failures |
| Sources | [[98_SOURCES/Java SE 17 1Z0-829 Sources]], [[98_SOURCES/Java SE 21 1Z0-830 Sources]] |

Status: `lab-proven`; JDK 17/21 PASS in GitHub Actions run `30148997725`.

### Current Java inventory

```text
published exam routes            4
lab-proven exam routes           4
atomic concept notes            35
beginner foundation/bridges       4
base cards                      286
drills                           90
Java production cases             8
positive proof classes           10
expected compile-fail cases      34
```

### Next Java routes

| Order | Route | Status |
|---:|---|---|
| 1 | `JAVA-B05` — Collections, Generics, Sequenced Collections | next |
| 2 | `JAVA-B06` — Lambdas and Streams | planned |
| 3 | `JAVA-B07` — Modules and Deployment | planned |
| 4 | `JAVA-B08` — Concurrency and Virtual Threads | planned; theory exists |
| 5 | `JAVA-B09` — I/O, NIO.2 and Serialization | planned |
| 6 | `JAVA-B10` — JDBC for 1Z0-829 | planned |
| 7 | `JAVA-B11` — Localization | planned |
| 8 | `JAVA-SUP-B01` — Logging, Annotations and supplementary Generics | planned |

## Java Concurrency

- [[10_CONCEPTS/Java/Concurrency/Concurrency Learning Path]]
- [[10_CONCEPTS/Java/Concurrency/Java Concurrency Visual Deep Dive]]
- [[20_QUESTIONS/Interview/Java/Concurrency/Advanced Concurrency Recall]]
- [[50_LABS/Java/Concurrency/README]]
- [[98_SOURCES/Java Concurrency Sources]]

## Spring published route hubs

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Core Card Roadmap]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring AOP and Cache Roadmap]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Transaction Management Roadmap]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Data JPA Roadmap]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Testing Roadmap]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B01/SPRING-BOOT-B01 Roadmap]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Roadmap]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Roadmap]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Roadmap]]

The aggregate vault workflow currently exposes an unrelated `SPRING-MVC-B02` regression separately from passing Java route workflows.

## Database route

### DB-B01 — Indexes and Query Plans

- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Roadmap]]
- [[10_CONCEPTS/Databases/PostgreSQL Index Mechanics]]
- [[10_CONCEPTS/Databases/PostgreSQL EXPLAIN and Query Plan Analysis]]
- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Cards]]
- [[40_PRODUCTION_CASES/Databases/Indexes and Query Plans Production Cases]]
- [[50_LABS/Databases/DB-B01/README]]

## Registry quality checklist

```text
[x] beginner path points to B01-B04
[x] published Java routes listed explicitly
[x] canonical hubs link atomic concepts
[x] atomic concepts link practice and evidence
[x] stable card IDs remain progress-compatible
[x] objective and route overrides added for B04
[x] dedicated JDK 17/21 proof workflow passed
[ ] all Java certification domains complete
[ ] learner progress registry initialized and used
[ ] timed mock bank complete
```
