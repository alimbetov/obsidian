---
type: source-index
domain: java
subdomain: java-se-17-certification
status: active
verified_at: 2026-07-23
certification:
  - java-1Z0-829
java_version: 17
java_11_baseline: required
java_21_delta: required
tags:
  - java
  - java-11
  - java-17
  - java-21
  - ocp
  - sources
  - jls
---

# Java SE 17 1Z0-829 Sources

> [!summary]
> Primary official source index for the exact Java 17 certification baseline. Use [[98_SOURCES/Java 11 17 21 Official Sources]] for cumulative Java 11 compatibility and Java 21 production deltas. Detailed exam logistics and objective wording must be re-verified in Oracle's registration portal before final exam freeze.

# Version boundary

```text
Java 11  compatibility and migration context
Java 17  exact language/API exam baseline
Java 21  production delta and version traps
```

Rules:

1. Compile/no-compile answers use Java 17 JLS rules.
2. API answers use Java SE 17 API contracts.
3. Java 21-only syntax and APIs are invalid unless the question explicitly compares versions.
4. Java 11 history is used to explain migration and minimum-version boundaries.
5. Preview/incubator features are classified by their exact release state.

# Oracle learning path

- https://learn.oracle.com/ols/learning-path/become-a-java-se-17-developer/88323/99487

Oracle describes the Java SE 17 Developer path as covering:

```text
object-oriented programming
Java syntax and language constructs
Collections and Streams
I/O and Concurrency
deployment
JDK 17 features
```

The repository decomposes those capabilities into 11 domain routes in [[30_CERTIFICATIONS/Java/1Z0-829/Java SE 17 99 Percent Master Roadmap]].

# Java Language Specification — Java SE 17

- https://docs.oracle.com/javase/specs/jls/se17/html/index.html

Use as the source of truth for:

```text
lexical structure and types
conversions and numeric promotion
expressions and operators
statements and control flow
classes, interfaces and inheritance
method invocation and overload resolution
exceptions
arrays
generics
lambda expressions
modules
records
sealed classes
pattern matching available in Java 17
```

High-value chapters:

- Chapter 4 — Types, Values and Variables;
- Chapter 5 — Conversions and Contexts;
- Chapter 8 — Classes;
- Chapter 9 — Interfaces;
- Chapter 11 — Exceptions;
- Chapter 14 — Blocks, Statements and Patterns;
- Chapter 15 — Expressions;
- Chapter 17 — Threads and Locks;
- Chapter 18 — Type Inference.

# Java SE 17 API documentation

- https://docs.oracle.com/en/java/javase/17/docs/api/index.html

Use for exact API contracts:

```text
java.lang
java.time
java.util and java.util.function
java.util.stream
java.util.concurrent
java.io
java.nio.file
java.sql
java.text
java.util.ResourceBundle
```

For exam drills, always distinguish:

```text
language rule → JLS
API behavior   → Java API documentation
command/tool   → JDK tool documentation
```

# JDK 17 tools

- https://docs.oracle.com/en/java/javase/17/docs/specs/man/javac.html
- https://docs.oracle.com/en/java/javase/17/docs/specs/man/java.html
- https://docs.oracle.com/en/java/javase/17/docs/specs/man/jar.html
- https://docs.oracle.com/en/java/javase/17/docs/specs/man/jdeps.html
- https://docs.oracle.com/en/java/javase/17/docs/specs/man/jlink.html

Use for:

- compilation and class/module paths;
- launching applications and modules;
- JAR creation;
- dependency analysis;
- custom runtime images.

# Java Virtual Machine Specification

- https://docs.oracle.com/javase/specs/jvms/se17/html/index.html

Use only where an exam question depends on JVM-level terminology. Most language questions should be answered from the JLS, not inferred from a particular JVM implementation.

# Java Memory Model

- https://docs.oracle.com/javase/specs/jls/se17/html/jls-17.html

Use for:

- happens-before;
- synchronization order;
- volatile rules;
- monitor unlock/lock;
- thread start/join;
- final-field semantics;
- correctly synchronized programs.

Related route:

- [[30_CERTIFICATIONS/Java/Concurrency/Java Concurrency 99 Percent Roadmap]]
- [[10_CONCEPTS/Java/Concurrency/Java Memory Model]]
- [[10_CONCEPTS/Java/Concurrency/Happens-Before]]

# Source policy for cards

Every code question should identify its evidence class:

```text
compile/no-compile  → JLS rule
exact API outcome   → API contract
module/tool command → tool documentation
concurrency order   → JLS Chapter 17
```

Do not use implementation accidents from one IDE/JVM as the canonical answer.

# Cross-version source policy

For a route that compares releases, use:

- [[98_SOURCES/Java 11 17 21 Official Sources]];
- Java 11, 17 and 21 JLS/JVMS/API documentation;
- OpenJDK JEP catalogs for exact release and feature status;
- Oracle migration guides for operational compatibility.

Java 21-only items such as virtual threads and sequenced collections belong in production-delta or version-trap sections, not Java 17 exam answers.

# Related materials

- [[00_HOME/Java 11 17 21 Complete Knowledge Program]]
- [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Roadmap]]
- [[00_HOME/Certification 99 Percent Readiness Dashboard]]
- [[30_CERTIFICATIONS/Java/1Z0-829/Java SE 17 99 Percent Master Roadmap]]
- [[01_MAPS/Java Map]]
