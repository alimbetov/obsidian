---
type: learning-route
route: JAVA-LTS-B01
domain: java
subdomain: java-versions
status: published
java_versions:
  - 11
  - 17
  - 21
card_count: 30
production_cases: 10
lab_matrix:
  - 11
  - 17
  - 21
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
> This route establishes the cumulative Java LTS model before the detailed language, API, JVM and certification routes. It teaches release boundaries, feature status, safe migration and the distinction between Java 17 exam semantics and Java 21 production semantics.

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
| Cards | [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Cards]] |
| Production cases | [[40_PRODUCTION_CASES/Java/Java 11 17 21 Migration Cases]] |
| Lab | [[50_LABS/Java/JAVA-LTS-B01/README]] |
| Sources | [[98_SOURCES/Java 11 17 21 Official Sources]] |
| Domain map | [[01_MAPS/Java Map]] |

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
30 base comparison cards
10 production migration cases
3 JDK compile/run lanes
future target: 20 version drills
future target: 3 timed migration mini-mocks
```

# Runtime evidence

The lab verifies:

```text
JDK 11-compatible API usage
JDK 17 records, sealed classes, text blocks and switch expressions
JDK 21 virtual threads, record patterns, pattern switch and sequenced collections
minimum-version compile boundaries
```

# Quality checklist

- [x] Canonical comparison note.
- [x] Official source index.
- [x] Stable card IDs.
- [x] Production migration cases.
- [x] Multi-JDK lab source.
- [ ] JDK 11/17/21 CI matrix passing.
- [ ] Version-coverage audit passing.
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

# Related certification

- [[30_CERTIFICATIONS/Java/1Z0-829/Java SE 17 99 Percent Master Roadmap]]
- [[98_SOURCES/Java SE 17 1Z0-829 Sources]]
