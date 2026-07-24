---
type: atomic-concept
domain: java
route: JAVA-B02
concept_id: JAVA-B02-N08
status: published
verified_at: 2026-07-24
java_versions:
  - 21
prerequisites:
  - JAVA-B02-N07
previous_note: Java 21 Pattern Switch
tags:
  - java
  - atomic-note
  - dominance
  - exhaustiveness
  - sealed-types
---

# Java Switch Dominance and Exhaustiveness

> [!summary]
> Dominance prevents unreachable later labels. Exhaustiveness proves that every selector value reaches a rule or completes abruptly.

## Source-order selection

Several patterns can match one value. The first applicable non-dominated label is selected.

Valid order:

```java
switch (value) {
    case String s -> useString(s);
    case CharSequence cs -> useSequence(cs);
    default -> useOther(value);
}
```

Invalid order:

```java
switch (value) {
    case CharSequence cs -> useSequence(cs);
    case String s -> useString(s); // dominated
    default -> useOther(value);
}
```

Every `String` is already a `CharSequence`.

## Constants versus broad patterns

Place specific constants before compatible broad patterns:

```java
switch (value) {
    case "YES" -> accept();
    case String s -> inspect(s);
    default -> reject();
}
```

Reversing the first two labels makes the constant unreachable.

## Guards and dominance

An arbitrary guard is not generally solved logically by the compiler. Safe method:

1. compare underlying pattern types;
2. consider source order;
3. apply guard-specific rules;
4. never assume arbitrary business predicates are proven mutually exclusive.

A guard that is the constant expression `true` is treated specially.

## Match-all labels

Match-all forms include:

```text
default
case null, default
an unconditional type pattern for the selector type
```

At most one match-all label is allowed.

```java
case Object o -> ...
default -> ...       // second match-all: invalid
```

## Exhaustive enhanced statements

```java
static void print(Object value) {
    switch (value) {
        case String s -> System.out.println(s);
    }
}
```

This does not compile because the enhanced statement is not exhaustive.

## Sealed hierarchy coverage

```java
sealed interface Shape permits Circle, Rectangle {}
record Circle(double radius) implements Shape {}
record Rectangle(double width, double height) implements Shape {}

double area(Shape shape) {
    return switch (shape) {
        case Circle c -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.width() * r.height();
    };
}
```

The compiler uses permitted direct subclasses to prove coverage.

A broad `default` can hide a newly added permitted alternative.

## Pattern-variable scope and fall-through

For arrow rules, the variable is scoped to the guard and selected rule.

Colon fall-through cannot enter a group that assumes a pattern variable was bound, because execution might arrive from another label.

## Compile-fail evidence

The B02 lab verifies:

- dominated subtype patterns;
- duplicate match-all labels;
- non-exhaustive enhanced statements;
- invalid constant guards;
- controlled version traps.

## Exam algorithm

1. Fix Java version.
2. Classify statement or expression.
3. List the selector domain.
4. Validate label compatibility.
5. Reject duplicate constants.
6. Check dominance in source order.
7. Identify match-all labels.
8. Prove exhaustiveness.
9. Resolve null separately.
10. Trace the selected rule.

## Exam traps

- broader type patterns dominate later subtypes.
- a broad pattern may dominate a compatible constant.
- two match-all labels are illegal.
- sealed coverage can replace `default`.
- broad `default` may hide hierarchy evolution.
- enhanced statements require exhaustiveness.

## Route navigation

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Route roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]]
- **Previous concept:** [[10_CONCEPTS/Java/Core/Java 21 Pattern Switch]]
- **Practice cards:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02C Pattern Switch Cards]]
- **Compile/output drills:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills]]
- **Executable lab:** [[50_LABS/Java/JAVA-B02/README]]
- **Primary sources:** [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
