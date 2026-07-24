---
type: canonical-hub
domain: java
route: JAVA-B01
subdomain: values-text-date-time
status: published
evidence_status: lab-proven
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
objectives:
  - JAVA21-1.1
  - JAVA21-1.2
  - JAVA21-1.3
atomic_note_count: 9
tags:
  - java
  - canonical-hub
  - primitives
  - strings
  - text-blocks
  - java-time
  - dst
---

# Java Values, Text and Date-Time

> [!summary]
> Canonical hub for `JAVA-B01`. The former monolithic chapter is split into nine atomic notes so each mechanism has its own backlinks, review path and previous/next navigation. Java 17 and Java 21 share the rules covered by this route unless an individual note states otherwise.

## Start here

| Step | Atomic concept | Main outcome |
|---:|---|---|
| 1 | [[10_CONCEPTS/Java/Core/Java Primitive Values and Literals]] | identify primitive domains and literal types |
| 2 | [[10_CONCEPTS/Java/Core/Java Numeric Promotion and Casting]] | solve expression typing, casts and overflow |
| 3 | [[10_CONCEPTS/Java/Core/Java Wrappers Boxing and Math]] | separate boxing, value equality and reference identity |
| 4 | [[10_CONCEPTS/Java/Core/Java String Identity and Operations]] | reason about pooling, indexes, regex and exact output |
| 5 | [[10_CONCEPTS/Java/Core/Java StringBuilder Mutation]] | trace mutation, chaining and aliases |
| 6 | [[10_CONCEPTS/Java/Core/Java Text Blocks]] | compute indentation, escapes and final newlines |
| 7 | [[10_CONCEPTS/Java/Core/Java Local Date-Time Types]] | classify local temporal values and supported units |
| 8 | [[10_CONCEPTS/Java/Core/Java Period Duration and Instant]] | distinguish calendar arithmetic from elapsed time |
| 9 | [[10_CONCEPTS/Java/Core/Java Zones DST and Formatting]] | resolve zones, DST, parsing and formatting |

## Learning path

```mermaid
flowchart LR
    A[Primitive values] --> B[Promotion and casting]
    B --> C[Wrappers and Math]
    C --> D[String identity]
    D --> E[StringBuilder mutation]
    E --> F[Text blocks]
    F --> G[Local date-time]
    G --> H[Period Duration Instant]
    H --> I[Zones DST Formatting]
    I --> J[Cards and drills]
    J --> K[Executable lab]
```

## Choose a study mode

### Learn the route

1. Read the nine atomic notes in order.
2. After each note, answer its **Active recall** section without reopening the explanation.
3. Complete the matching card batch.
4. Predict drill output before revealing the answer.
5. Run the lab only after making a written prediction.

### Review a weak concept

| Confusion | Open |
|---|---|
| literal type or underscore | [[10_CONCEPTS/Java/Core/Java Primitive Values and Literals]] |
| `byte + byte`, cast or overflow | [[10_CONCEPTS/Java/Core/Java Numeric Promotion and Casting]] |
| wrapper `==` or null unboxing | [[10_CONCEPTS/Java/Core/Java Wrappers Boxing and Math]] |
| String pool, substring or split | [[10_CONCEPTS/Java/Core/Java String Identity and Operations]] |
| builder aliases and mutation | [[10_CONCEPTS/Java/Core/Java StringBuilder Mutation]] |
| text-block indentation | [[10_CONCEPTS/Java/Core/Java Text Blocks]] |
| local temporal types | [[10_CONCEPTS/Java/Core/Java Local Date-Time Types]] |
| Period versus Duration | [[10_CONCEPTS/Java/Core/Java Period Duration and Instant]] |
| DST, formatter or locale | [[10_CONCEPTS/Java/Core/Java Zones DST and Formatting]] |

### Exam drill mode

- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01A Values Cards|25 values cards]]
- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01B Text Cards|25 text cards]]
- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01C Date-Time Cards|25 date-time cards]]
- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills|15 compile/output drills]]

### Evidence mode

- [[50_LABS/Java/JAVA-B01/README|JDK 17/21 executable proof]]
- CI evidence: JDK 17 and JDK 21 lanes pass.
- Deterministic questions use explicit zones, locales and fixed values.

## Exam reasoning protocol

1. Fix the Java version.
2. Determine literal and expression types.
3. Apply promotion before assignment conversion.
4. Separate reference identity from value equality.
5. Trace mutable aliases.
6. Visualize text-block indentation and final newline.
7. Classify temporal values as local, offset-based, zoned or instant.
8. Distinguish `Period` from `Duration`.
9. Resolve DST gaps and overlaps using an explicit region zone.
10. Check formatter symbol case, locale and resolver style.

## Route artifacts

| Role | Artifact |
|---|---|
| Roadmap | [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]] |
| Values cards | [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01A Values Cards]] |
| Text cards | [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01B Text Cards]] |
| Date-time cards | [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01C Date-Time Cards]] |
| Drills | [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills]] |
| Lab | [[50_LABS/Java/JAVA-B01/README]] |
| Java 17 sources | [[98_SOURCES/Java SE 17 1Z0-829 Sources]] |
| Java 21 sources | [[98_SOURCES/Java SE 21 1Z0-830 Sources]] |
| Version delta | [[30_CERTIFICATIONS/Java/Java 17 and 21 Exam Delta Matrix]] |
| Java dashboard | [[00_HOME/Java Learning Dashboard]] |
| Visual map | [[01_MAPS/Java Certification Routes.canvas]] |

## Previous and next

- **Program entry:** [[00_HOME/Oracle Java 17 and 21 Certification Program]]
- **Next route:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]
