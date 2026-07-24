---
type: atomic-concept
concept_id: JAVA-B03-C12
domain: java
route: JAVA-B03
subdomain: nested-patterns
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

# Java Nested Patterns and Exhaustiveness

> [!summary]
> Nested record patterns compose structural deconstruction with sealed-hierarchy exhaustiveness, guards, dominance and source-order selection.

## Nested deconstruction

```java
record Point(int x, int y) {}
record Box(Point point, String label) {}

case Box(Point(int x, int y), String label) -> ...
```

The outer record pattern must match before nested component patterns are evaluated. Pattern variables are scoped to the selected rule and its guard.

## Guards

```java
case Box(Point(int x, int y), var label) when x == y -> ...
case Box(Point(int x, int y), var label) -> ...
```

Place the guarded special case before the unguarded pattern. An unguarded pattern with the same structure can dominate a later guarded form.

## Sealed recursive models

```java
sealed interface Expr permits Num, Add {}
record Num(int value) implements Expr {}
record Add(Expr left, Expr right) implements Expr {}
```

A switch covering every permitted subtype can omit `default`:

```java
int eval(Expr expr) {
    return switch (expr) {
        case Num(int value) -> value;
        case Add(var left, var right) -> eval(left) + eval(right);
    };
}
```

More specific nested patterns may precede a broader deconstruction:

```java
case Add(Num(int a), Num(int b)) -> a + b;
case Add(var left, var right) -> eval(left) + eval(right);
```

Reversing them makes the specific pattern unreachable.

## Evolution trap

Adding a new permitted subtype can make a previously exhaustive switch fail compilation after recompilation. This is desirable feedback. A broad `default` may hide the missing semantic branch.

## Active recall

1. Which pattern should appear first: a specific nested pattern or a broad deconstruction?
2. How does a sealed hierarchy support exhaustive switch checking?
3. Why can `default` weaken maintenance feedback?

## Route navigation

- **Previous:** [[10_CONCEPTS/Java/Object Model/Java Record Patterns]]
- **Next:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- **Roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- **Cards:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03A Object Lifecycle Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03B Initialization and Scope Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03C Inheritance and Interfaces Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03D Records Sealed and Patterns Cards]]
- **Drills:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- **Lab:** [[50_LABS/Java/JAVA-B03/README]]
- **Sources:** [[98_SOURCES/Java SE 17 1Z0-829 Sources]], [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
- **Dashboard:** [[00_HOME/Java Learning Dashboard]]
