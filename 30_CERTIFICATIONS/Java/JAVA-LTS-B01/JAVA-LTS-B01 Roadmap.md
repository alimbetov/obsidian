---
type: learning-route
route: JAVA-LTS-B01
domain: java
subdomain: java-versions
status: published
evidence_status: lab-proven
verified_at: 2026-07-23
java_versions:
  - 11
  - 17
  - 21
published_cards: 30
visual_diagrams: 15
pre_test_questions: 10
post_test_questions: 15
production_cases: 10
lab_matrix:
  - 11
  - 17
  - 21
canvas: Java 11 17 21 LTS Map
tags:
  - java
  - java-11
  - java-17
  - java-21
  - migration
  - roadmap
---

# JAVA-LTS-B01 — Java 11, 17 and 21 Evolution and Migration

> [!summary]
> This route establishes the cumulative Java LTS model before the detailed language, API, JVM and certification routes. It teaches release boundaries, feature status, safe migration and the distinction between Java 17 exam semantics and Java 21 production semantics. The route is runtime-proven independently on JDK 11, 17 and 21.

# Learning outcomes

After completing the route, the learner can:

1. Explain the separate roles of Java 11, 17 and 21.
2. Classify a feature as permanent, preview, incubator, experimental, deprecated or removed.
3. Identify which release first supports a language/API feature.
4. Prevent Java 21 behavior from leaking into Java 17 exam answers.
5. Use `--release` correctly for older targets.
6. Plan 8→11, 11→17 and 17→21 migrations.
7. Diagnose removed modules, strong encapsulation and reflective-access failures.
8. Explain virtual-thread benefits and resource limits.
9. Select a production target based on framework, operations and migration evidence.
10. Compile and run version-specific examples on JDK 11, 17 and 21.

# Route artifacts

| Role | Artifact |
|---|---|
| Program | [[00_HOME/Java 11 17 21 Complete Knowledge Program]] |
| Canonical | [[10_CONCEPTS/Java/Versions/Java 11 17 21 LTS Evolution]] |
| Visual deep dive | [[10_CONCEPTS/Java/Versions/Java 11 17 21 Visual Deep Dive]] |
| Cards | [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Cards]] |
| Assessment | [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Assessment]] |
| Production cases | [[40_PRODUCTION_CASES/Java/Java 11 17 21 Migration Cases]] |
| Lab | [[50_LABS/Java/JAVA-LTS-B01/README]] |
| Canvas | [[01_MAPS/Java 11 17 21 LTS Map.canvas]] |
| Sources | [[98_SOURCES/Java 11 17 21 Official Sources]] |
| Domain map | [[01_MAPS/Java Map]] |
| Progress | [[70_PROGRESS/README]] |

# Scope

## Java 11

```text
HTTP Client
single-file source launch
var lambda parameters
String/Files API additions
Flight Recorder
TLS 1.3
Nest-Based Access Control
Epsilon and experimental ZGC
removed Java EE/CORBA modules
removed deployment stack
8→11 migration
```

## Java 17

```text
switch expressions
text blocks
records
pattern matching for instanceof
sealed classes
helpful NullPointerExceptions
Stream.toList
strong encapsulation
serialization filters
removed/deprecated runtime components
11→17 migration
```

## Java 21

```text
virtual threads
record patterns
pattern matching for switch
sequenced collections
Generational ZGC
KEM API
UTF-8 default inherited from JDK 18
preview/incubator feature boundaries
17→21 migration
```

# Assessment model

```text
10-question pre-test
30 base comparison cards
15-question post-test
10 production migration cases
15 visual models
3 JDK compile/run lanes
future target: 20 version drills
future target: 3 timed migration mini-mocks
```

# Runtime evidence

GitHub Actions run `30027027138` executed three independent jobs:

```text
Java LTS 11 proof  PASS
Java LTS 17 proof  PASS
Java LTS 21 proof  PASS
```

The matrix proves:

```text
JDK 11-compatible API usage
JDK 17 records, sealed classes, text blocks and switch expressions
JDK 21 virtual threads, record patterns, pattern switch and sequenced collections
minimum-version compile boundaries
actual execution on matching runtimes
```

Lane contract:

```text
JDK 11 → Java11Baseline
JDK 17 → Java11Baseline + Java17Features
JDK 21 → Java11Baseline + Java17Features + Java21Features
```

# Current platform contribution

`JAVA-LTS-D18 — Migration and compatibility` is classified as `lab-proven` for shared knowledge and all three version layers.

```text
Overall Java 11/17/21 platform evidence  15.35%
Shared-domain evidence                   10.83%
Java 11 layer                            19.17%
Java 17 layer                            15.00%
Java 21 layer                            16.39%
```

This low overall percentage is intentional: the migration route is complete, but most of the remaining 17 shared domains still require full vertical slices.

# Quality checklist

- [x] Canonical comparison note.
- [x] Visual deep dive with 15 diagrams.
- [x] Pre-test and post-test.
- [x] Official source index.
- [x] 30 stable card IDs.
- [x] 10 production migration cases.
- [x] Multi-JDK lab source.
- [x] Canvas route map.
- [x] JDK 11/17/21 GitHub Actions matrix passing.
- [x] Version-coverage audit passing.
- [ ] Cross-version drill bank.
- [ ] Migration mini-mocks.

# Next routes

```text
JAVA-B01 Data, Text and Date-Time
JAVA-B02 Control Flow and Pattern Matching
JAVA-B03 Object Model
JAVA-B04 Exceptions and Resources
JAVA-B05 Collections and Generics
JAVA-B06 Lambdas and Streams
JAVA-B07 Modules and Deployment
JAVA-B08 Concurrency
JAVA-B09 I/O and NIO.2
JAVA-B10 JDBC
JAVA-B11 Localization
JVM/GC/JIT/diagnostics routes
```

Each next route must include:

```text
shared Java mechanism
Java 11 compatibility baseline
Java 17 exact exam semantics
Java 21 production delta
version-aware cards and drills
multi-JDK evidence where applicable
```

# Related certification

- [[30_CERTIFICATIONS/Java/1Z0-829/Java SE 17 99 Percent Master Roadmap]]
- [[98_SOURCES/Java SE 17 1Z0-829 Sources]]
