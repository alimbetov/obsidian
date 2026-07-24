---
type: atomic-concept
domain: java
route: JAVA-B02
concept_id: JAVA-B02-N05
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
prerequisites:
  - JAVA-B02-N04
previous_note: Java Classic Switch
next_note: Java Pattern Matching for instanceof
tags:
  - java
  - atomic-note
  - switch-expression
  - yield
  - exhaustiveness
---

# Java Switch Expressions

> [!summary]
> A switch expression must produce a value or complete abruptly on every selected path, and it must be exhaustive.

## Arrow expression arms

```java
int size = switch (token) {
    case "S" -> 1;
    case "M" -> 2;
    case "L" -> 3;
    default -> throw new IllegalArgumentException(token);
};
```

The whole construct has a type and produces a value.

## Block arms and `yield`

```java
int size = switch (token) {
    case "S" -> {
        log(token);
        yield 1;
    }
    default -> 0;
};
```

`yield` returns a value from the switch expression. `break value` is not final Java syntax.

A selected block arm cannot complete normally without yielding a value.

## Exhaustiveness

Every selector value must be handled.

For an enum, complete constant coverage can prove exhaustiveness:

```java
enum State { NEW, RUNNING, DONE }

int code = switch (state) {
    case NEW -> 0;
    case RUNNING -> 1;
    case DONE -> 2;
};
```

No `default` is required.

## Why broad `default` can be undesirable

For enum and sealed hierarchies, explicit exhaustive coverage lets recompilation reveal a newly introduced alternative. A broad `default` can hide that change.

## Target typing and arm types

The result type is derived from arm expressions and assignment or invocation context. Do not assume every mixed arm set simply becomes `Object`.

```java
var value = switch (n) {
    case 1 -> 10;
    default -> 20L;
};
```

Normal conversion and numeric-promotion rules still apply.

## Abrupt completion

An arm may throw instead of producing a value:

```java
String result = switch (token) {
    case "OK" -> "accepted";
    default -> throw new IllegalArgumentException(token);
};
```

## Compile-fail boundaries

The B02 lab verifies failures for:

- missing `default` or complete coverage;
- block arm without `yield`;
- duplicate case labels;
- unsupported selector types.

## Exam traps

- switch expressions must be exhaustive.
- `yield` is required for value-producing block arms.
- `break value` is obsolete preview-era syntax.
- an arm that throws is valid.
- enum completeness can replace `default`.
- arm types follow normal conversion rules.

## Route navigation

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Route roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]]
- **Previous concept:** [[10_CONCEPTS/Java/Core/Java Classic Switch]]
- **Next concept:** [[10_CONCEPTS/Java/Core/Java Pattern Matching for instanceof]]
- **Practice cards:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02B Switch Cards]]
- **Compile/output drills:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills]]
- **Executable lab:** [[50_LABS/Java/JAVA-B02/README]]
- **Primary sources:** [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
