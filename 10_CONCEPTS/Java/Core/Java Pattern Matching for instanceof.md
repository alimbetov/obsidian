---
type: atomic-concept
domain: java
route: JAVA-B02
concept_id: JAVA-B02-N06
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
prerequisites:
  - JAVA-B02-N01
previous_note: Java Switch Expressions
next_note: Java 21 Pattern Switch
tags:
  - java
  - atomic-note
  - instanceof
  - type-patterns
  - flow-scoping
---

# Java Pattern Matching for instanceof

> [!summary]
> Java 17 finalizes type patterns for `instanceof`. The pattern variable exists only where flow analysis proves that the match succeeded.

## Basic type pattern

```java
if (value instanceof String text) {
    System.out.println(text.length());
}
```

The test performs runtime type compatibility and binds `text`. The pattern does not match null.

## Flow scoping with `&&`

```java
if (value instanceof String text && text.length() > 3) {
    use(text);
}
```

The right operand runs only when the match succeeds, so `text` is definitely available.

## Why `||` differs

```java
if (value instanceof String text || text.isEmpty()) {
    // does not compile
}
```

The right operand may run after the pattern test is false, when `text` is not initialized.

## Negated early exit

```java
if (!(value instanceof String text)) {
    return;
}
System.out.println(text.length());
```

After the non-matching path exits, the compiler knows the remaining path matched.

## Scope boundary

The pattern variable is a flow-scoped local, not a method-wide variable.

```java
if (value instanceof String text) {
    use(text);
}
// text is not available here without additional flow proof
```

## Relationship to Java 21 switch

The same type-pattern concept is used by pattern switch, but switch adds:

- ordered labels;
- dominance;
- exhaustiveness;
- `case null`;
- `when` guards.

Master `instanceof` flow scoping before pattern switch.

## Exam traps

- null never matches a type pattern.
- `&&` can extend scope into the right operand.
- `||` generally cannot.
- negated early exit can extend scope afterward.
- the variable exists only in flow-proven regions.
- Java 17 pattern switch is preview-only and excluded from normal 1Z0-829 questions.

## Route navigation

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Route roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]]
- **Previous concept:** [[10_CONCEPTS/Java/Core/Java Switch Expressions]]
- **Next concept:** [[10_CONCEPTS/Java/Core/Java 21 Pattern Switch]]
- **Practice cards:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02A Control Flow Cards]]
- **Compile/output drills:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills]]
- **Executable lab:** [[50_LABS/Java/JAVA-B02/README]]
- **Primary sources:** [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
