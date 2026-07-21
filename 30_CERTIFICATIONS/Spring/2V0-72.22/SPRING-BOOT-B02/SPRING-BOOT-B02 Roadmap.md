---
type: certification-roadmap
certification: spring-2V0-72.22
domain: spring
subdomain: spring-boot-configuration
batch: SPRING-BOOT-B02
status: published
published_cards: 35
visual_diagrams: 30
production_cases: 12
pre_test_questions: 10
post_test_questions: 15
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
  - roadmap
---

# SPRING-BOOT-B02 — Configuration Properties and Externalized Configuration

> [!summary]
> Route goal: trace raw sources into the ordered Environment, explain Config Data/profile behavior, bind a coherent namespace into typed validated objects and diagnose wrong values from origin through conversion.

# Route navigation

- **Registry:** [[00_HOME/Knowledge Route Registry]]
- **Master:** [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring 99 Percent Master Roadmap]]
- **Domain map:** [[01_MAPS/Spring Map]]
- **Previous:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B01/SPRING-BOOT-B01 Roadmap]]
- **Next:** `SPRING-MVC-B01 — DispatcherServlet and Controller Pipeline`
- **Canvas:** [[01_MAPS/Spring Boot Configuration Map.canvas]]

# Objective traceability

| Objective | Interpretation | Evidence |
|---|---|---|
| `SPRING-1.3.1` | external properties control configuration | canonical, cards, cases, lab |
| `SPRING-1.3.2` | profiles and profile-specific configuration | visual, cards, Config Data test |
| `SPRING-6.2.1` | options for defining/loading properties | full route and source index |

Machine manifest:

```text
.github/objectives/spring-2V0-72.22.json
```

# Learning outcomes

After completion the learner can:

1. Reconstruct `PropertySource` precedence.
2. Predict a winning value across files, environment and command line.
3. Explain Config Data timing and imports.
4. Distinguish replace/additional locations.
5. Explain active/default profiles, groups and document activation.
6. Choose `@Value` versus `@ConfigurationProperties`.
7. Bind nested collections, maps, `Duration` and `DataSize`.
8. Explain Boot 2.5 constructor binding and current delta.
9. Make invalid configuration fail startup through validation.
10. Diagnose origin, conversion, validation and secret exposure.

# Corrected learning cycle

```mermaid
flowchart LR
    PRE["10-question pre-test"] --> THEORY["Canonical mechanism"]
    THEORY --> VISUAL["30 visual models"]
    VISUAL --> CARDS["35 stable card IDs"]
    CARDS --> CASES["12 incidents"]
    CASES --> LAB["7 executable tests"]
    LAB --> POST["15-question post-test"]
    POST --> PROGRESS["Per-card progress registry"]
```

# Artifacts

| Role | Artifact |
|---|---|
| Canonical | [[10_CONCEPTS/Spring/Boot/Spring Boot Externalized Configuration and Type-safe Binding]] |
| Visual | [[10_CONCEPTS/Spring/Boot/Spring Boot Configuration Visual Deep Dive]] |
| Cards | [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Cards]] |
| Assessment | [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Assessment]] |
| Cases | [[40_PRODUCTION_CASES/Spring/Spring Boot Configuration Production Cases]] |
| Lab | [[50_LABS/Spring/SPRING-BOOT-B02/README]] |
| Canvas | [[01_MAPS/Spring Boot Configuration Map.canvas]] |
| Sources | [[98_SOURCES/Spring Boot Externalized Configuration Sources]] |
| Progress | [[70_PROGRESS/README]] |

# Coverage

## Environment and precedence

- `Environment` and ordered `PropertySource`;
- command line, system properties and environment variables;
- packaged versus external configuration;
- canonical key and winning source;
- test overrides and context-cache impact.

## Config Data and profiles

- Boot 2.5 Config Data pipeline;
- default search locations;
- `spring.config.name`;
- `spring.config.location`;
- `spring.config.additional-location`;
- `spring.config.import`;
- `optional:`;
- config trees;
- multi-document resources;
- active/default profiles;
- profile-specific files;
- groups, include and document activation.

## Type-safe binding

- `@Value` versus `@ConfigurationProperties`;
- registration mechanisms;
- relaxed binding;
- nested objects;
- collections/maps;
- conversion;
- `Duration` and `DataSize`;
- Boot 2.5 constructor binding;
- current inference/record delta;
- validation;
- custom converters;
- programmatic `Binder`;
- metadata and unknown-property controls.

## Security and diagnostics

- secret delivery versus secret exposure;
- Actuator/logging boundaries;
- origin and source inspection;
- active-document evidence;
- conversion/validation distinction;
- deployment and test overrides.

# Progress compatibility

Every card has stable ID:

```text
SPRING-BOOT-B02-C001 ... SPRING-BOOT-B02-C035
```

Record outcomes through:

```bash
python .github/scripts/card_progress.py record \
  --card-id SPRING-BOOT-B02-C001 \
  --outcome correct-confident \
  --confidence 4
```

# Quality gate

- [x] Official objective IDs assigned.
- [x] Learning outcomes defined.
- [x] Pre-test and post-test.
- [x] Canonical and visual routes.
- [x] 35 normalized cards.
- [x] 12 production incidents.
- [x] Java 8 / Boot 2.5 source lab.
- [x] Version baseline/current delta.
- [x] Canvas and primary sources.
- [ ] Maven tests passed in GitHub Actions.
- [ ] Delayed learner review data collected.
- [ ] Mixed Spring timed mock coverage added.

# Next route

```text
SPRING-MVC-B01 — DispatcherServlet and Controller Pipeline
```
