---
type: certification-audit
domain: knowledge-system
status: active
verified_at: 2026-07-23
tracks:
  - spring-2V0-72.22
  - java-1Z0-829
  - java-concurrency
tags:
  - certification
  - readiness
  - audit
  - methodology
---

# Certification Coverage Assessment — 2026-07-23

> [!summary]
> The repository has a strong Spring learning architecture and several complete vertical slices, but the knowledge base is not yet sufficient to claim exam readiness. Spring material readiness is **76.30%**; Java 1Z0-829 is **4.50%**; Java Concurrency is **45.70%** by the machine model. Candidate readiness remains unmeasured because full timed mock evidence does not exist.

# Evidence snapshot

Source: the latest `vault-quality-audit` artifact generated from the merged knowledge-base state.

```text
Repository files          271
Markdown files            163
Canvas files               22
Concept notes              52
Card batches               15
Stable card IDs           418
Production-case files      14
Lab files                  79
Mermaid blocks            443
Published routes           10
Objective records          76
```

Integrity results before this audit update:

```text
structural errors            0
objective errors             0
readiness errors             0
card completeness errors     0
cross-link warnings         15
Mermaid failures             1
```

The single Mermaid failure was the MVC `PathVariable` diagram with unquoted `/catalog/{id}` syntax. It has been corrected in [[10_CONCEPTS/Spring/MVC/Spring MVC DispatcherServlet Visual Deep Dive]].

# Current official exam status

## Spring Professional Develop 2V0-72.22

Verified on 2026-07-23 from Broadcom:

```text
status          available
questions       60
duration        130 minutes
format          single and multiple choice
passing score   300 scaled
price           USD 250
credential      Spring Certified Professional [v2]
```

Official pages:

- https://www.broadcom.com/support/education/software/certification/exams/spring-pro-develop-exam
- https://www.broadcom.com/support/education/vmware/certification/spring-certified-pro

## Java SE 17 Developer 1Z0-829

Verified on 2026-07-23 from Oracle Learning:

```text
status          active learning and exam path
exam            Java SE 17 Developer 1Z0-829
exam duration   1 hour 30 minutes
```

Oracle describes the preparation scope as object-oriented programming, Java syntax and constructs, Collections, Streams, I/O, Concurrency, deployment, and JDK 17 features.

Official pages:

- https://learn.oracle.com/ols/learning-path/become-a-java-se-17-developer/117252/118129
- https://education.oracle.com/java-se-21-developer/pexam_1Z0-829

# Machine readiness scores

Formula:

```text
25% objective traceability
75% vertical-slice artifact and card completeness
```

| Track | Overall | Objective traceability | Vertical slices | Target |
|---|---:|---:|---:|---:|
| Spring 2V0-72.22 | **76.30%** | 62.11% | 81.03% | 99% |
| Java 1Z0-829 | **4.50%** | 4.00% | 4.67% | 99% |
| Java Concurrency | **45.70%** | 40.00% | 47.60% | 99% |

These values measure repository evidence, not the learner's probability of passing.

# Spring 2V0-72.22 assessment

## Strongly covered

```text
Spring Core and dependency resolution
bean lifecycle and extension points
AOP and proxy boundaries
cache interception
transaction management
Spring Data JPA and persistence context
Spring TestContext and database testing
Spring Boot bootstrap and auto-configuration
externalized configuration and ConfigurationProperties
DispatcherServlet and annotated controller pipeline
```

Published Spring objective state:

```text
complete        19 / 57
lab-proven      11 / 57
cards-ready     12 / 57
theory-visual    1 / 57
unmapped        14 / 57
```

## Critical unmapped objectives

```text
SPRING-1.3.3   Spring Expression Language
SPRING-2.1.1   configure and use JdbcTemplate
SPRING-2.1.2   result-set callbacks
SPRING-2.1.3   translated data-access exceptions
SPRING-3.2.1   REST endpoints for multiple HTTP verbs
SPRING-3.2.2   RestTemplate client operations
SPRING-4.2.3   MockMvc testing as an explicit objective
SPRING-5.1     security concepts
SPRING-5.2     authentication and authorization
SPRING-5.3     method security
SPRING-6.3.1   Actuator endpoint configuration
SPRING-6.3.2   Actuator endpoint security
SPRING-6.3.3   custom metrics
SPRING-6.3.4   custom health indicators
```

## Exam verdict

```text
Material foundation      strong
Official coverage        incomplete
Timed mock bank          absent
Candidate evidence       absent
Registration verdict     not yet recommended
```

Spring becomes registration-ready after:

1. `SPRING-MVC-B02` — REST verbs, response contracts and `RestTemplate`.
2. `SPRING-SEC-B01` — authentication, authorization and method security.
3. `SPRING-ACT-B01` — Actuator, health and metrics.
4. `SPRING-JDBC-B01` — `JdbcTemplate` and exception translation.
5. `SPRING-WEBTEST-B01` — explicit `MockMvc` objective coverage.
6. `SPRING-SPEL-B01` — SpEL.
7. Mixed exam-drill bank.
8. Six full 60-question / 130-minute mocks.
9. Last three full mocks at or above 90%, with no domain below 85%.

# Java 1Z0-829 assessment

## Current state

Only the Concurrency domain has substantial canonical and visual material. Ten of eleven capability domains remain unmapped in the exam route.

```text
unmapped domains       10 / 11
theory-visual domains   1 / 11
base exam cards          0 / 720
exam drills              0 / 180
full timed mocks         0 / 6
```

## Required routes

```text
JAVA-B01 Data, Text and Date-Time
JAVA-B02 Control Flow
JAVA-B03 Object Model
JAVA-B04 Exceptions and Resources
JAVA-B05 Collections and Generics
JAVA-B06 Lambdas and Streams
JAVA-B07 Modules and Deployment
JAVA-B08 Concurrency Exam Objectives
JAVA-B09 I/O, NIO.2 and Serialization
JAVA-B10 JDBC
JAVA-B11 Localization
```

## Exam verdict

```text
Conceptual foundation   insufficient outside Concurrency
Objective coverage      very low
Exam drills             absent
Registration verdict    not recommended
```

# Java Concurrency assessment

The conceptual route is strong, but every one of the eight objectives is still classified as `theory-visual` because no dedicated card bank is mapped.

```text
objectives              8
theory-visual           8
mapped exam cards       0
production-case target  incomplete
controlled-lab target   incomplete
mini-mocks              0 / 6
```

Required to reach 99%:

```text
140 base cards
40 execution/diagnostic drills
20 consolidated production incidents
25 controlled or stress experiments
6 timed 30-question mini-mocks
```

# Methodological quality assessment

## Strengths

- Objective-linked vertical-slice model.
- Stable `card_id` and per-card progress registry.
- Normalized card contract with explanation and exam trap.
- Strong visual learning layer.
- Failure-first production cases.
- Executable Boot and MVC tests.
- Version separation between exam baseline and current production APIs.
- Automated structure, graph, objective, readiness, card and Mermaid audits.

## Remaining quality debt

### Navigation

```text
global orphan notes     1 before this update
navigation sinks       14
cross-link warnings    15
```

Most sinks are source indexes without backlinks. They are usable evidence leaves, but adding a `Related route` footer would improve navigation and remove ambiguity.

### Thin or under-structured notes

The structural audit reports:

```text
concept depth warnings       2
concept structure warnings  10
concept source warnings     11
lab expected-result warning  1
concept diagram info         8
```

These are not factual errors, but they identify notes that need manual pedagogical review.

### Assessment layer

The largest remaining methodology gap is not theory. It is mixed discrimination under time pressure:

```text
multiple-select drills
cross-domain scenarios
exact configuration-result questions
full timed mocks
weak-domain statistics
candidate-readiness evidence
```

# Priority order

```text
P0  keep main CI green and render all 443 Mermaid blocks
P1  SPRING-MVC-B02
P2  SPRING-SEC-B01
P3  SPRING-ACT-B01
P4  SPRING-JDBC-B01
P5  SPRING-WEBTEST-B01
P6  SPRING-SPEL-B01
P7  Spring mixed drills and full mocks
P8  JAVA-B01 through JAVA-B11
P9  Concurrency card/case/lab/mock consolidation
```

# Decision summary

| Track | Current conclusion |
|---|---|
| Spring 2V0-72.22 | Strong preparation base, but **not yet complete enough to recommend exam registration**. |
| Java 1Z0-829 | **Not ready**; most exam domains are absent. |
| Java Concurrency | Strong conceptual reference, but **not yet a complete assessment route**. |

# Related materials

- [[00_HOME/Certification 99 Percent Readiness Dashboard]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring 99 Percent Master Roadmap]]
- [[30_CERTIFICATIONS/Java/1Z0-829/Java SE 17 99 Percent Master Roadmap]]
- [[30_CERTIFICATIONS/Java/Concurrency/Java Concurrency 99 Percent Roadmap]]
- [[99_AUDITS/Pedagogical Visual Enrichment Pass]]
