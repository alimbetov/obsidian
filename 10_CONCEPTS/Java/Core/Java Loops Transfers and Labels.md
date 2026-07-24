---
type: atomic-concept
domain: java
route: JAVA-B02
concept_id: JAVA-B02-N02
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
prerequisites:
  - JAVA-B02-N01
previous_note: Java Conditions and Definite Assignment
next_note: Java Reachability Rules
tags:
  - java
  - atomic-note
  - loops
  - break
  - continue
  - labels
---

# Java Loops Transfers and Labels

> [!summary]
> Loop questions are solved by tracing exact execution points and transfer targets, not by reading indentation informally.

## `while`

```text
condition → body → condition → ...
```

The body may execute zero times.

## `do-while`

```text
body → condition → body → ...
```

The body executes at least once. The trailing semicolon is required.

```java
do {
    work();
} while (condition);
```

## Classic `for`

```text
initializer → condition → body → update → condition → ...
```

All three header components are optional:

```java
for (;;) {
    // infinite unless control exits
}
```

`continue` transfers to the update expressions before the next condition test.

## Enhanced `for`

```java
for (Element element : source) {
    ...
}
```

The source must be an array or `Iterable`.

Reassigning the loop variable does not replace an array element:

```java
int[] values = {1, 2, 3};
for (int value : values) {
    value++;
}
// array unchanged
```

Mutating an object referenced by the loop variable can remain visible.

## `break`

Unlabeled `break` exits the innermost loop or switch statement.

A labeled `break` can exit an enclosing labeled statement:

```java
search: {
    if (found()) {
        break search;
    }
    continueWork();
}
```

## `continue`

Unlabeled `continue` targets the innermost loop.

A labeled `continue` must target an enclosing loop:

```java
outer:
for (int row = 0; row < 3; row++) {
    for (int col = 0; col < 3; col++) {
        if (skipRow(row, col)) {
            continue outer;
        }
    }
}
```

A labeled block is a valid `break` target but not a valid `continue` target.

## `return`

`return` completes the method invocation. It is not a multi-level `break`. A `finally` block may still execute before completion.

## Trace protocol

1. Execute the initializer once.
2. Test the condition.
3. Execute the body.
4. Resolve `break`, `continue`, `return` or `throw`.
5. For classic `for`, run updates after ordinary completion or `continue`.
6. Repeat.

## Exam traps

- `do-while` needs a trailing semicolon.
- `continue` in a classic `for` still runs updates.
- enhanced-loop reassignment does not update the source element.
- labeled `continue` cannot target a plain block.
- unlabeled transfer targets the innermost valid construct.
- `return` exits the method, not just the loop.

## Route navigation

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Route roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]]
- **Previous concept:** [[10_CONCEPTS/Java/Core/Java Conditions and Definite Assignment]]
- **Next concept:** [[10_CONCEPTS/Java/Core/Java Reachability Rules]]
- **Practice cards:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02A Control Flow Cards]]
- **Compile/output drills:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills]]
- **Executable lab:** [[50_LABS/Java/JAVA-B02/README]]
- **Primary sources:** [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
