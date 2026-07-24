---
type: atomic-concept
domain: java
route: JAVA-B02
concept_id: JAVA-B02-N07
status: published
verified_at: 2026-07-24
java_versions:
  - 21
prerequisites:
  - JAVA-B02-N06
previous_note: Java Pattern Matching for instanceof
next_note: Java Switch Dominance and Exhaustiveness
tags:
  - java
  - atomic-note
  - java-21
  - pattern-switch
  - guards
  - null
---

# Java 21 Pattern Switch

> [!summary]
> Java 21 finalizes pattern matching for switch: reference selectors, type patterns, explicit null handling and `when` guards.

## Version boundary

Java 17 has final switch expressions and final `instanceof` patterns. Pattern switch in Java 17 is preview-only and excluded from ordinary 1Z0-829 assumptions.

Java 21 finalizes the feature.

## Type-pattern labels

```java
static String describe(Object value) {
    return switch (value) {
        case Integer i -> "int " + i;
        case String s -> "text " + s;
        default -> "other";
    };
}
```

A type pattern:

```text
tests runtime type compatibility
binds a local variable
does not match null
```

Labels are considered in source order, subject to dominance rules.

## `when` guards

```java
return switch (value) {
    case String s when s.isBlank() -> "blank";
    case String s -> "text";
    default -> "other";
};
```

Only pattern labels may have guards. The pattern variable is in scope in the guard and selected rule.

## Null handling

```java
return switch (value) {
    case null -> "missing";
    case String s -> s;
    default -> value.toString();
};
```

Without `case null`, a null selector throws `NullPointerException`.

`default` alone does not match null.

Combined form:

```java
case null, default -> "rest";
```

It handles null and every unmatched non-null value.

## Broader reference selectors

Java 21 pattern switch can use any reference selector type, enabling switches over `Object`, domain interfaces and sealed hierarchies.

## Qualified enum constants

Java 21 permits:

```java
case Color.RED -> ...
```

This is useful when the selector type is broader than one enum class. The same syntax is a version trap for a direct Java 17 enum switch.

## Enhanced switch statements

A statement is enhanced when it:

- uses a pattern label;
- uses `case null`;
- switches on a non-legacy reference selector.

Enhanced switch statements must be exhaustive.

## Exam traps

- final syntax uses `when`, not older preview guard syntax.
- type patterns do not match null.
- `default` does not handle null.
- arbitrary guards do not generally make dominance disappear.
- enhanced switch statements must be exhaustive.
- qualified enum constants are version-sensitive.

## Route navigation

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Route roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]]
- **Previous concept:** [[10_CONCEPTS/Java/Core/Java Pattern Matching for instanceof]]
- **Next concept:** [[10_CONCEPTS/Java/Core/Java Switch Dominance and Exhaustiveness]]
- **Practice cards:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02C Pattern Switch Cards]]
- **Compile/output drills:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills]]
- **Executable lab:** [[50_LABS/Java/JAVA-B02/README]]
- **Primary sources:** [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
