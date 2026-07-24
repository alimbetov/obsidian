---
type: moc
domain: certification
status: active
verified_at: 2026-07-24
tags:
  - certification
  - navigation
  - readiness
---

# Certification MOC

## Primary entry points

- [[00_HOME/Java Learning Dashboard]]
- [[00_HOME/Certification 99 Percent Readiness Dashboard]]
- [[00_HOME/Oracle Java 17 and 21 Certification Program]]
- [[00_HOME/Java 11 17 21 Complete Knowledge Program]]
- [[00_HOME/Card Review Dashboard]]
- [[00_HOME/Knowledge Route Registry]]
- [[01_MAPS/Java Certification Routes.canvas]]
- [[01_MAPS/Certification 99 Percent Map.canvas]]

## Learning-system contract

```mermaid
flowchart LR
    OBJ[Official objective] --> ROAD[Route roadmap]
    ROAD --> HUB[Canonical hub]
    HUB --> NOTE[Atomic concepts]
    NOTE --> CARD[Stable cards]
    CARD --> DRILL[Drills and cases]
    DRILL --> LAB[Executable evidence]
    LAB --> REVIEW[Per-card progress]
    REVIEW --> MOCK[Timed mock]
```

Machine controls:

```text
70_PROGRESS/card-progress.json
.github/scripts/card_progress.py
.github/objectives/*.json
.github/objective-overrides/*.json
.github/scripts/audit_objective_traceability.py
.github/scripts/audit_certification_readiness.py
.github/java-version-coverage.json
.github/scripts/audit_java_version_coverage.py
```

## Master tracks

| Track | Roadmap | Current delivery |
|---|---|---|
| Spring 2V0-72.22 | [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring 99 Percent Master Roadmap]] | multiple published routes |
| Java 11/17/21 platform | [[00_HOME/Java 11 17 21 Complete Knowledge Program]] | `JAVA-LTS-B01` published |
| Oracle Java 17 + 21 | [[00_HOME/Oracle Java 17 and 21 Certification Program]] | B01 and B02 lab-proven |
| Java `1Z0-829` | [[30_CERTIFICATIONS/Java/1Z0-829/Java SE 17 99 Percent Master Roadmap]] | B01 and B02 published |
| Java `1Z0-830` | [[30_CERTIFICATIONS/Java/1Z0-830/Java SE 21 99 Percent Master Roadmap]] | B01 and B02 published |
| Java Concurrency | [[30_CERTIFICATIONS/Java/Concurrency/Java Concurrency 99 Percent Roadmap]] | foundation published |

# Java certification navigation

## Version model

```text
Java 11  compatibility and migration baseline
Java 17  exact 1Z0-829 compile/API baseline
Java 21  exact 1Z0-830 compile/API baseline
```

Shared mechanisms are reused, but questions and proofs remain version-bound.

- [[30_CERTIFICATIONS/Java/Java 17 and 21 Exam Delta Matrix]]
- [[98_SOURCES/Java SE 17 1Z0-829 Sources]]
- [[98_SOURCES/Java SE 21 1Z0-830 Sources]]

## JAVA-B01 — Values, Text and Date-Time

Status: **lab-proven**.

```text
9 atomic concepts
75 base cards
15 drills
3 proof classes
JDK 17 / JDK 21 PASS
```

- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]]
- [[50_LABS/Java/JAVA-B01/README]]

Atomic start: [[10_CONCEPTS/Java/Core/Java Primitive Values and Literals]].

## JAVA-B02 — Control Flow and Java 21 Pattern Switch

Status: **lab-proven**.

```text
8 atomic concepts
60 base cards
20 drills
2 positive proof classes
11 expected compile failures
JDK 17 / JDK 21 PASS
```

- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]
- [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]]
- [[50_LABS/Java/JAVA-B02/README]]

Atomic start: [[10_CONCEPTS/Java/Core/Java Conditions and Definite Assignment]].

## Java route sequence

| Order | Route | Java 17 | Java 21 | Status |
|---:|---|---|---|---|
| 1 | JAVA-B01 Values/Text/Date-Time | included | included | lab-proven |
| 2 | JAVA-B02 Control Flow | included | pattern-switch delta | lab-proven |
| 3 | JAVA-B03 Object Model | records/sealed | record patterns | next |
| 4 | JAVA-B05 Collections/Generics | included | sequenced collections | planned |
| 5 | JAVA-B06 Lambdas/Streams | included | included | planned |
| 6 | JAVA-B04 Exceptions/Resources | included | included | planned |
| 7 | JAVA-B07 Modules/Deployment | included | included | planned |
| 8 | JAVA-B08 Concurrency | included | virtual threads | planned |
| 9 | JAVA-B09 I/O/NIO.2 | included | included | planned |
| 10 | JAVA-B10 JDBC | direct objective | supplementary backend only | planned |
| 11 | JAVA-B11 Localization | included | included | planned |
| 12 | JAVA-SUP-B01 | supporting | logging/annotations/generics | planned |

## Java progress inventory

```text
published exam routes          2
atomic notes                  17
base cards                   135
drills                        35
positive proof classes         5
expected compile failures     11
```

Use [[00_HOME/Java Learning Dashboard]] for the operational learning flow.

# Java Concurrency

- [[30_CERTIFICATIONS/Java/Concurrency/Java Concurrency 99 Percent Roadmap]]
- [[10_CONCEPTS/Java/Concurrency/Concurrency Learning Path]]
- [[10_CONCEPTS/Java/Concurrency/Java Concurrency Visual Deep Dive]]
- [[20_QUESTIONS/Interview/Java/Concurrency/Advanced Concurrency Recall]]
- [[50_LABS/Java/Concurrency/README]]

# Spring navigation

## Published batches

| Area | Route hub |
|---|---|
| Spring Core | [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Core Card Roadmap]] |
| AOP and Cache | [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring AOP and Cache Roadmap]] |
| Transactions | [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Transaction Management Roadmap]] |
| Data JPA | [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Data JPA Roadmap]] |
| Testing | [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Testing Roadmap]] |
| Spring Boot B01 | [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B01/SPRING-BOOT-B01 Roadmap]] |
| Spring Boot B02 | [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Roadmap]] |
| Spring MVC B01 | [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Roadmap]] |
| Spring MVC B02 | [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Roadmap]] |

The aggregate workflow currently has an unrelated `SPRING-MVC-B02` regression; Java B01/B02 route-specific checks pass separately.

# Database navigation

- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Roadmap]]
- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Cards]]
- [[50_LABS/Databases/DB-B01/README]]

# Card contract

Every published card contains:

```text
Question
Russian Translation
Answer
Explanation
Exam Trap
```

Learning state is stored per stable `card_id`, not in batch frontmatter.

# Recommended user process

1. Open the relevant dashboard.
2. Select the exact exam and Java version.
3. Follow route roadmap → canonical hub → atomic notes.
4. Answer active recall without notes.
5. Complete stable cards and record confidence.
6. Attempt drills before execution.
7. Predict and run the lab.
8. Enter timed mocks only after delayed review.

# Next implementation route

```text
JAVA-B03 — Object Model, Records, Sealed Types and Record Patterns
```
