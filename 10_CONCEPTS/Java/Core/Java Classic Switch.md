---
type: atomic-concept
domain: java
route: JAVA-B02
concept_id: JAVA-B02-N04
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
prerequisites:
  - JAVA-B02-N03
previous_note: Java Reachability Rules
next_note: Java Switch Expressions
tags:
  - java
  - atomic-note
  - switch
  - fall-through
  - case-labels
---

# Java Classic Switch

> [!summary]
> A classic switch selects an entry label. Colon groups may fall through; arrow rules do not.

## Legacy selector types

```text
byte, short, char, int
Byte, Short, Character, Integer
String
enum
```

Primitive `boolean`, `long`, `float` and `double` are not legacy selector types.

## Case labels are compile-time constants

```java
final int ONE = 1;
switch (value) {
    case ONE:
        break;
}
```

A merely final runtime value is not enough:

```java
final int one = Integer.parseInt("1");
// case one:  // not a constant expression
```

Duplicate values are rejected even when spelled differently:

```java
case 1:
case 0b1: // duplicate
```

## Colon fall-through

```java
switch (n) {
    case 1:
        print("A");
    case 2:
        print("B");
    default:
        print("C");
}
```

For `n == 2`, output is `BC`.

The label selects an entry point. Execution continues until `break`, `return`, `throw`, or the end of the switch.

## Arrow rules

```java
switch (n) {
    case 1, 2 -> print("small");
    default -> print("other");
}
```

Arrow rules do not fall through. A rule body may be an expression statement, block or throw statement.

## Null selector behavior

For legacy reference selectors such as `String` and enum, a null selector normally throws `NullPointerException`. A `default` label does not make legacy null safe.

## Enum constants and version boundary

In a direct enum switch under Java 17, labels are unqualified:

```java
case RED -> ...
```

Java 21 permits qualified enum constants in broader scenarios. Treat this as a version-sensitive compile rule.

## Trace protocol

1. Validate selector type.
2. Compute constant labels.
3. Reject duplicates.
4. Evaluate the selector.
5. Find the matching label or `default`.
6. Trace fall-through or arrow completion.
7. Apply transfer statements.

## Exam traps

- `long` is not a classic primitive selector.
- a final local is not automatically a compile-time constant.
- colon groups fall through without `break`.
- arrow rules never fall through.
- different literal spellings can duplicate a case value.
- null reference selectors fail before matching `default`.

## Route navigation

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Route roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]]
- **Previous concept:** [[10_CONCEPTS/Java/Core/Java Reachability Rules]]
- **Next concept:** [[10_CONCEPTS/Java/Core/Java Switch Expressions]]
- **Practice cards:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02B Switch Cards]]
- **Compile/output drills:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills]]
- **Executable lab:** [[50_LABS/Java/JAVA-B02/README]]
- **Primary sources:** [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
