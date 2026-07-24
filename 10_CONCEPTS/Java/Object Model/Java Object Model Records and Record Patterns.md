---
type: canonical-hub
domain: java
route: JAVA-B03
subdomain: object-model-record-patterns
status: published
evidence_status: implementation-ready
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
atomic_note_count: 12
objectives:
  - JAVA-B03
  - JAVA21-3.1
  - JAVA21-3.2
  - JAVA21-3.3
  - JAVA21-3.4
  - JAVA21-3.5
  - JAVA21-3.6
  - JAVA21-3.7
tags:
  - java
  - object-model
  - records
  - sealed-types
  - record-patterns
---

# Java Object Model, Records and Record Patterns

> [!summary]
> Canonical hub for `JAVA-B03`. The route separates shared Java 17/21 object-model rules from the final Java 21 record-pattern layer while preserving one ordered learning path.

## Ordered concept path

1. [[10_CONCEPTS/Java/Object Model/Java Object Creation Reachability and Lifecycle]]
2. [[10_CONCEPTS/Java/Object Model/Java Nested Local and Anonymous Classes]]
3. [[10_CONCEPTS/Java/Object Model/Java Fields Initializers and Constructor Order]]
4. [[10_CONCEPTS/Java/Object Model/Java Overloading Varargs and Method Selection]]
5. [[10_CONCEPTS/Java/Object Model/Java Scope Encapsulation Immutability and var]]
6. [[10_CONCEPTS/Java/Object Model/Java Inheritance Overriding Hiding and Polymorphism]]
7. [[10_CONCEPTS/Java/Object Model/Java Abstract Classes and Interfaces]]
8. [[10_CONCEPTS/Java/Object Model/Java Records]]
9. [[10_CONCEPTS/Java/Object Model/Java Enums]]
10. [[10_CONCEPTS/Java/Object Model/Java Sealed Types]]
11. [[10_CONCEPTS/Java/Object Model/Java Record Patterns]]
12. [[10_CONCEPTS/Java/Object Model/Java Nested Patterns and Exhaustiveness]]

## Version contract

```text
Java 17
  records and sealed types are final
  pattern matching for instanceof is final
  record patterns are not ordinary Java 17 syntax

Java 21
  all shared object-model rules
  final record patterns and nested patterns
  sealed-hierarchy interaction with pattern switch
```

## Study modes

### Learn

Read one atomic concept, predict its active-recall answers, then answer the corresponding card batch.

### Repair

Open the atomic concept linked from the failed card category. Do not reread the full route unless the failure crosses several mechanisms.

### Prove

Predict `JAVA-B03` lab output and compile failures before running the workflow.

## Route artifacts

- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03A Object Lifecycle Cards]]
- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03B Initialization and Scope Cards]]
- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03C Inheritance and Interfaces Cards]]
- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03D Records Sealed and Patterns Cards]]
- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- [[50_LABS/Java/JAVA-B03/README]]
- [[98_SOURCES/Java SE 17 1Z0-829 Sources]]
- [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
- [[00_HOME/Java Learning Dashboard]]
