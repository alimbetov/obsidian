---
type: lab
domain: java
subdomain: java-versions
route: JAVA-LTS-B01
status: active
java_versions:
  - 11
  - 17
  - 21
tags:
  - java
  - java-11
  - java-17
  - java-21
  - migration
  - lab
---

# JAVA-LTS-B01 Lab — Compile and Run on Java 11, 17 and 21

## Purpose

Prove version boundaries with actual compilers and runtimes:

```text
Java 11 lane
  Java 11 String/API additions
  Predicate.not
  standard HTTP Client

Java 17 lane
  Java 11 baseline
  records
  sealed types
  pattern instanceof
  switch expressions
  text blocks

Java 21 lane
  Java 11 and 17 baselines
  virtual threads
  record patterns
  pattern switch
  sequenced collections
```

# Structure

```text
JAVA-LTS-B01/
├── README.md
├── run.sh
└── src/main/java/lab/lts
    ├── Java11Baseline.java
    ├── Java17Features.java
    └── Java21Features.java
```

# Run locally

Use a matching JDK for each lane:

```bash
bash 50_LABS/Java/JAVA-LTS-B01/run.sh 11
bash 50_LABS/Java/JAVA-LTS-B01/run.sh 17
bash 50_LABS/Java/JAVA-LTS-B01/run.sh 21
```

The script always compiles the Java 11 baseline with:

```bash
javac --release 11
```

The Java 17 and Java 21 source files are included only when the running CI lane supports them.

# Expected results

## JDK 11

```text
Java11Baseline compiles and runs
Java17Features is not compiled
Java21Features is not compiled
JAVA-LTS-B01 PASS on JDK 11
```

## JDK 17

```text
Java11Baseline compiles and runs
Java17Features compiles and runs
Java21Features is not compiled
JAVA-LTS-B01 PASS on JDK 17
```

## JDK 21

```text
all three source sets compile and run
virtual-thread task returns the expected value
sequenced collection first/last operations work
record-pattern switch returns the expected area
JAVA-LTS-B01 PASS on JDK 21
```

# Negative experiments

## Compile Java 17 code with release 11

```bash
javac --release 11 \
  50_LABS/Java/JAVA-LTS-B01/src/main/java/lab/lts/Java17Features.java
```

Expected: compilation failure because records, sealed types, text blocks and switch expressions are not part of the Java 11 source/API target.

## Compile Java 21 code with release 17

```bash
javac --release 17 \
  50_LABS/Java/JAVA-LTS-B01/src/main/java/lab/lts/Java21Features.java
```

Expected: compilation failure because virtual-thread APIs, record patterns, pattern switch and sequenced collection methods are unavailable in the Java 17 target.

# What this lab proves

```text
minimum language version
minimum API version
--release boundary
Java 17 exam versus Java 21 production separation
actual target-runtime execution
```

# What this lab does not prove

```text
full application migration
framework compatibility
agent/profiler compatibility
TLS and locale behavior
GC performance
production resource limits
```

Those require dedicated migration and JVM labs.

# Evidence worksheet

| Lane | Java 11 source | Java 17 source | Java 21 source | Result |
|---|---|---|---|---|
| JDK 11 | expected PASS | not compiled | not compiled | |
| JDK 17 | expected PASS | expected PASS | not compiled | |
| JDK 21 | expected PASS | expected PASS | expected PASS | |

# Route navigation

- [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Roadmap]]
- [[10_CONCEPTS/Java/Versions/Java 11 17 21 LTS Evolution]]
- [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Cards]]
- [[40_PRODUCTION_CASES/Java/Java 11 17 21 Migration Cases]]
- [[98_SOURCES/Java 11 17 21 Official Sources]]
