---
type: atomic-concept
domain: java
route: JAVA-B02
concept_id: JAVA-B02-N01
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
prerequisites:
  - JAVA-B01
next_note: Java Loops Transfers and Labels
tags:
  - java
  - atomic-note
  - conditions
  - definite-assignment
---

# Java Conditions and Definite Assignment

> [!summary]
> Conditions require boolean results, while local-variable reads require the compiler to prove assignment on every reachable path.

## Boolean-only conditions

`if`, `while`, `do-while` and the middle expression of a classic `for` require `boolean` or unboxable `Boolean`.

```java
Boolean enabled = null;
if (enabled) { } // compiles, then throws NullPointerException
```

There is no truthy/falsy conversion:

```java
// if (1) { }
// if (new Object()) { }
```

## Short-circuit boundary

```java
left && right
left || right
```

The right operand may be skipped.

```java
boolean result = false && dangerous(); // dangerous() not called
```

Boolean `&` and `|` evaluate both sides:

```java
boolean result = false & dangerous();  // dangerous() runs
```

## Dangling `else`

Without braces, `else` belongs to the nearest unmatched `if`.

```java
if (outer)
    if (inner)
        actionA();
    else
        actionB();
```

The `else` belongs to `if (inner)`.

## Local variables and definite assignment

Local variables have no default values.

```java
int value;
if (condition) {
    value = 10;
}
System.out.println(value); // does not compile
```

An exhaustive branch can establish assignment:

```java
int value;
if (condition) {
    value = 10;
} else {
    value = 20;
}
System.out.println(value);
```

## Blank finals

A blank final local may be assigned exactly once on every path.

```java
final int value;
if (condition) {
    value = 10;
} else {
    value = 20;
}
```

## Pattern variables and flow

```java
if (value instanceof String text && text.length() > 3) {
    System.out.println(text);
}
```

The right operand executes only after the match succeeds, so `text` is available.

## Exam traps

- a null `Boolean` compiles as a condition and fails during unboxing.
- indentation does not bind `else`; grammar does.
- field default values do not apply to locals.
- assignment on one branch is insufficient.
- `&&` can extend pattern-variable scope into the right operand.
- `||` generally cannot.

## Route navigation

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Route roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]]
- **Next concept:** [[10_CONCEPTS/Java/Core/Java Loops Transfers and Labels]]
- **Practice cards:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02A Control Flow Cards]]
- **Compile/output drills:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills]]
- **Executable lab:** [[50_LABS/Java/JAVA-B02/README]]
- **Primary sources:** [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
