---
type: atomic-concept
concept_id: JAVA-B03-C10
domain: java
route: JAVA-B03
subdomain: sealed-types
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
certifications:
  - java-1Z0-829
  - java-1Z0-830
prerequisites:
  - JAVA-B01
  - JAVA-B02
tags:
  - java
  - object-model
  - active-recall
---

# Java Sealed Types

> [!summary]
> Sealed classes and interfaces close an inheritance boundary so permitted direct subtypes are explicit and can support exhaustive reasoning.

## Declaration

```java
sealed interface Shape permits Circle, Rectangle {}
record Circle(double radius) implements Shape {}
record Rectangle(double width, double height) implements Shape {}
```

A permitted direct subtype must be declared `final`, `sealed` or `non-sealed` unless it is a record, which is already final.

## `permits`

The permits clause lists direct subtypes. It may be inferred when all direct subtypes are declared in the same compilation unit. Unauthorized direct extension or implementation is a compile-time error.

Permitted subtypes must satisfy locality constraints: in a named module they belong to the same module; in the unnamed-module/package model they must be in the same package.

## `non-sealed`

`non-sealed` reopens the hierarchy below one permitted subtype. It is legal only for a direct subtype of a sealed type.

```java
sealed class Node permits OpenNode, Leaf {}
non-sealed class OpenNode extends Node {}
final class Leaf extends Node {}
```

Subclasses below `OpenNode` are no longer restricted by the original sealed declaration.

## Exhaustive reasoning

The compiler can use the closed direct subtype set when checking enhanced switch exhaustiveness. Avoid a broad `default` when the goal is to receive a compilation failure after adding a new permitted subtype.

## Active recall

1. Which three modifiers may a permitted direct subtype use?
2. What does `non-sealed` do to the hierarchy below it?
3. Why can sealed types improve switch maintenance?

## Route navigation

- **Previous:** [[10_CONCEPTS/Java/Object Model/Java Enums]]
- **Next:** [[10_CONCEPTS/Java/Object Model/Java Record Patterns]]
- **Roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- **Cards:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03A Object Lifecycle Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03B Initialization and Scope Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03C Inheritance and Interfaces Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03D Records Sealed and Patterns Cards]]
- **Drills:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- **Lab:** [[50_LABS/Java/JAVA-B03/README]]
- **Sources:** [[98_SOURCES/Java SE 17 1Z0-829 Sources]], [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
- **Dashboard:** [[00_HOME/Java Learning Dashboard]]
