---
type: source-index
domain: java
subdomain: java-se-21-certification
status: active
verified_at: 2026-07-24
certification:
  - java-1Z0-830
java_version: 21
java_17_baseline: required
tags:
  - java
  - java-17
  - java-21
  - ocp
  - 1Z0-830
  - sources
  - jls
---

# Java SE 21 1Z0-830 Sources

> [!summary]
> Primary source index for the Java SE 21 Developer Professional track. Exact compile/no-compile answers use Java 21 JLS rules; API questions use Java SE 21 API contracts. Java 17 material is reused only when its behavior remains valid in Java 21.

# Oracle certification and learning path

- Exam page: https://education.oracle.com/java-se-21-developer-professional/pexam_1Z0-830
- Learning path: https://learn.oracle.com/ols/learning-path/become-a-java-se-21-developer/117252/138845
- Complete course: https://learn.oracle.com/ols/course/java-se-21-programming-complete/117252/138847
- Oracle practice exam: https://learn.oracle.com/ols/module/practice-exam-java-se-21-developer-professional/117252/148778

Re-verify exam logistics and topic wording before the final exam freeze because Oracle may update certification pages.

# Java Language Specification — Java SE 21

- https://docs.oracle.com/javase/specs/jls/se21/html/index.html

Use as the source of truth for:

```text
primitive and reference types
conversions and numeric promotion
operators and expressions
control flow
classes, records, enums and interfaces
inheritance and overload resolution
exceptions
arrays and generics
lambda expressions
pattern matching for instanceof and switch
record patterns
modules
threads and locks
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

# Java SE 21 API documentation

- https://docs.oracle.com/en/java/javase/21/docs/api/index.html

Exam-critical packages:

```text
java.lang
java.math
java.time
java.util
java.util.function
java.util.stream
java.util.concurrent
java.io
java.nio.file
java.text
java.util.ResourceBundle
```

Java 21-specific API boundaries:

```text
java.util.SequencedCollection
java.util.SequencedSet
java.util.SequencedMap
Thread.ofVirtual
Thread.startVirtualThread
Executors.newVirtualThreadPerTaskExecutor
```

# JDK 21 tool specifications

- https://docs.oracle.com/en/java/javase/21/docs/specs/man/javac.html
- https://docs.oracle.com/en/java/javase/21/docs/specs/man/java.html
- https://docs.oracle.com/en/java/javase/21/docs/specs/man/jar.html
- https://docs.oracle.com/en/java/javase/21/docs/specs/man/jdeps.html
- https://docs.oracle.com/en/java/javase/21/docs/specs/man/jlink.html
- https://docs.oracle.com/en/java/javase/21/docs/specs/man/jshell.html

# Java 21 feature sources

- JEP 431 — Sequenced Collections: https://openjdk.org/jeps/431
- JEP 440 — Record Patterns: https://openjdk.org/jeps/440
- JEP 441 — Pattern Matching for switch: https://openjdk.org/jeps/441
- JEP 444 — Virtual Threads: https://openjdk.org/jeps/444

The exam route treats these as final Java 21 features. Preview features such as Structured Concurrency and Scoped Values must be labeled preview and must not be mixed into normal Java 21 compile assumptions unless preview is explicitly enabled.

# Exact exam-domain model

```text
1  date/time/text/numeric/boolean values
2  program flow
3  object-oriented concepts
4  exceptions
5  arrays and collections
6  streams and lambda expressions
7  packaging and deployment
8  concurrent code execution
9  I/O and NIO.2
10 localization
11 supplementary: logging, standard annotations, generics
```

The 1Z0-830 main objective list does not include JDBC. JDBC remains part of the 1Z0-829 route and broader Java backend knowledge program.

# Evidence policy

Every card and question must identify the evidence class:

```text
compile/no-compile       → JLS 21
exact API result         → Java SE 21 API
module/tool command      → JDK 21 tool specification
feature/version status   → JEP + JLS/API
concurrency ordering     → JLS Chapter 17
```

# Related materials

- [[00_HOME/Oracle Java 17 and 21 Certification Program]]
- [[30_CERTIFICATIONS/Java/1Z0-830/Java SE 21 99 Percent Master Roadmap]]
- [[30_CERTIFICATIONS/Java/Java 17 and 21 Exam Delta Matrix]]
- [[00_HOME/Java 11 17 21 Complete Knowledge Program]]
- [[98_SOURCES/Java 11 17 21 Official Sources]]
