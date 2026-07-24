---
type: atomic-concept
domain: java
route: JAVA-B01
concept_id: JAVA-B01-N06
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
prerequisites:
  - JAVA-B01-N05
previous_note: Java StringBuilder Mutation
next_note: Java Local Date-Time Types
tags:
  - java
  - atomic-note
  - text-blocks
  - indentation
  - escapes
---

# Java Text Blocks

> [!summary]
> A text block is an ordinary `String` whose value is determined by delimiter placement, incidental indentation and source-level escapes.

## Opening delimiter

The opening delimiter is three quotes followed only by optional whitespace and a line terminator.

```java
String ok = """
        text
        """;
```

Content cannot begin on the same source line as the opening delimiter.

## Incidental indentation

The compiler removes incidental indentation using:

- the least-indented nonblank content line;
- the position of the closing delimiter.

```java
String text = """
        one
          two
        """;
```

Result:

```text
one\n
  two\n
```

Moving the closing delimiter can change retained indentation.

## Final newline

With the closing delimiter on its own line, the preceding line terminator is normally part of the result.

```java
String text = """
        one
        """;

text.equals("one\n"); // true
```

## Text-block escapes

`\s` creates or preserves a space:

```java
String text = """
        a\s
        b
        """;
```

A backslash at the physical end of a source line suppresses the generated newline:

```java
String text = """
        one\
        two
        """;

// "onetwo\n"
```

Ordinary escapes remain available:

```text
\n  \t  \"  \\
```

## A text block has no automatic semantics

```java
String json = """
        {"name":"Java"}
        """;
```

The result is a `String`. It is not parsed as JSON and has no template processing.

## Reliable output protocol

1. Mark the opening and closing delimiter columns.
2. Find incidental indentation.
3. Remove it from every content line.
4. Apply line-continuation and `\s`.
5. Apply ordinary escapes.
6. Decide whether the final newline remains.
7. Compare the exact resulting string.

## Exam traps

- content on the opening delimiter line is invalid.
- closing delimiter position affects indentation.
- visible source indentation is not always retained.
- a final newline is commonly present.
- line continuation joins physical lines.
- a text block is still just `String`.

## Route navigation

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Route roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]]
- **Previous concept:** [[10_CONCEPTS/Java/Core/Java StringBuilder Mutation]]
- **Next concept:** [[10_CONCEPTS/Java/Core/Java Local Date-Time Types]]
- **Practice cards:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01B Text Cards]]
- **Compile/output drills:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills]]
- **Executable lab:** [[50_LABS/Java/JAVA-B01/README]]
- **Primary sources:** [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
