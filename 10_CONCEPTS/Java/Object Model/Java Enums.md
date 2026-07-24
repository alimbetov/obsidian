---
type: atomic-concept
concept_id: JAVA-B03-C09
domain: java
route: JAVA-B03
subdomain: enums
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

# Java Enums

> [!summary]
> Enum constants are the fixed instances of an enum class and may carry state, behavior and constant-specific class bodies.

## Constants and construction

```java
enum Phase {
    NEW(0), RUNNING(1), DONE(2);
    private final int code;
    Phase(int code) { this.code = code; }
    int code() { return code; }
}
```

Enum constructors are private in effect and cannot be declared public or protected. Constants are initialized in declaration order before ordinary static fields that follow them.

## Enum API

Every enum has compiler-generated `values()` and `valueOf(String)` methods. `values()` returns a new array. `name()` returns the declared identifier; `toString()` may be overridden. `ordinal()` is declaration position and should not be persisted as a stable business identifier.

## Constant-specific behavior

A constant may provide a class body that overrides an enum method.

```java
enum Operation {
    PLUS { int apply(int a, int b) { return a + b; } },
    MINUS { int apply(int a, int b) { return a - b; } };
    abstract int apply(int a, int b);
}
```

## Switching on enum values

A switch expression can be exhaustive by listing every enum constant. In Java 17, direct enum case labels use unqualified constant names. Java 21 permits qualified enum constants in broader pattern-switch contexts.

## Active recall

1. Can an enum constructor be public?
2. Does `values()` expose the internal backing array?
3. Why is `ordinal()` unsuitable as a durable external identifier?

## Route navigation

- **Previous:** [[10_CONCEPTS/Java/Object Model/Java Records]]
- **Next:** [[10_CONCEPTS/Java/Object Model/Java Sealed Types]]
- **Roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- **Cards:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03A Object Lifecycle Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03B Initialization and Scope Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03C Inheritance and Interfaces Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03D Records Sealed and Patterns Cards]]
- **Drills:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- **Lab:** [[50_LABS/Java/JAVA-B03/README]]
- **Sources:** [[98_SOURCES/Java SE 17 1Z0-829 Sources]], [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
- **Dashboard:** [[00_HOME/Java Learning Dashboard]]
