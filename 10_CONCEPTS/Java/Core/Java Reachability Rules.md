---
type: atomic-concept
domain: java
route: JAVA-B02
concept_id: JAVA-B02-N03
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
prerequisites:
  - JAVA-B02-N02
previous_note: Java Loops Transfers and Labels
next_note: Java Classic Switch
tags:
  - java
  - atomic-note
  - reachability
  - unreachable-code
---

# Java Reachability Rules

> [!summary]
> Reachability is a compile-time structural analysis. It is related to, but distinct from, definite assignment and constant folding.

## Unreachable statements

A statement that cannot be reached under Java's structural rules is rejected.

```java
while (false) {
    System.out.println("never"); // compile-time error
}
```

A statement after an unconditional `return` is unreachable.

## Special treatment of `if`

Java permits:

```java
if (false) {
    debugOnly();
}
```

This supports conditional-compilation idioms. The special rule does not make `while (false)` legal.

## Infinite loops

```java
while (true) {
}
```

A following statement is unreachable unless the compiler sees a reachable transfer that can exit the loop.

```java
while (true) {
    if (condition) {
        break;
    }
}
System.out.println("reachable");
```

## Reachability versus definite assignment

```text
reachability        → can control arrive here?
definite assignment → is the local assigned on every arriving path?
```

A reachable statement may still fail definite-assignment analysis.

## Abrupt completion

The following complete abruptly:

```text
break
continue
return
throw
```

Abrupt completion changes which later statements and branches are reachable.

## Constant expressions

Reachability follows specific language rules, not arbitrary execution of the program by the compiler. Do not assume every logically impossible branch is classified as unreachable.

## Compile-fail evidence

The B02 lab includes controlled negative sources for:

- `while (false)`;
- invalid `continue` target;
- non-exhaustive switch expressions;
- missing `yield`;
- duplicate case labels.

## Exam traps

- `if (false)` is allowed while `while (false)` is rejected.
- an infinite loop can make following code unreachable.
- a reachable `break` can restore reachability after an infinite loop.
- reachability does not guarantee definite assignment.
- analysis follows language rules, not informal logical deduction.

## Route navigation

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Route roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]]
- **Previous concept:** [[10_CONCEPTS/Java/Core/Java Loops Transfers and Labels]]
- **Next concept:** [[10_CONCEPTS/Java/Core/Java Classic Switch]]
- **Practice cards:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02A Control Flow Cards]]
- **Compile/output drills:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills]]
- **Executable lab:** [[50_LABS/Java/JAVA-B02/README]]
- **Primary sources:** [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
