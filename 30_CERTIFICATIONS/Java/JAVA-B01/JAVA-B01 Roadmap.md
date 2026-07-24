---
type: learning-route
route: JAVA-B01
domain: java
subdomain: values-text-date-time
status: implementation-ready
evidence_status: source-and-drill-backed
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
certifications:
  - java-1Z0-829
  - java-1Z0-830
objectives:
  - JAVA21-1.1
  - JAVA21-1.2
  - JAVA21-1.3
base_card_target: 75
drill_card_target: 15
published_base_cards: 75
published_drills: 15
tags:
  - java
  - java-17
  - java-21
  - ocp
  - values
  - strings
  - text-blocks
  - date-time
---

# JAVA-B01 — Values, Strings, Text Blocks and Date-Time

> [!summary]
> `JAVA-B01` is the first executable certification domain. It establishes the value, conversion, text and temporal rules reused by later compile/output questions. Java 17 and Java 21 share the core behavior covered here; the exact exam lane is still recorded on every question.

# Why this route is first

Later domains assume that the learner can already reason about:

```text
literal type
numeric promotion
cast legality
operator precedence
short-circuit evaluation
String identity versus equality
StringBuilder mutation and aliasing
text-block indentation and escapes
temporal immutability
Period versus Duration
Instant versus local date-time
zone gaps and overlaps
formatter symbols and resolver behavior
```

Without these rules, questions about overload resolution, collections, streams, exceptions and concurrency become guesswork.

# Objective mapping

| Objective | Scope | Route evidence |
|---|---|---|
| `JAVA21-1.1` | primitives, wrappers, Math, promotions, casts and boolean expressions | values cards, drills and `ValuesProof` |
| `JAVA21-1.2` | String, StringBuilder and text blocks | text cards, drills and `TextProof` |
| `JAVA21-1.3` | date/time, period, duration, instant, zones and DST | date-time cards, drills and `DateTimeProof` |

# Route artifacts

| Role | Artifact |
|---|---|
| Canonical knowledge | [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]] |
| Values cards | [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01A Values Cards]] |
| Text cards | [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01B Text Cards]] |
| Date-time cards | [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01C Date-Time Cards]] |
| Compile/output drills | [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills]] |
| Executable lab | [[50_LABS/Java/JAVA-B01/README]] |
| Java 21 source index | [[98_SOURCES/Java SE 21 1Z0-830 Sources]] |
| Java 17 source index | [[98_SOURCES/Java SE 17 1Z0-829 Sources]] |
| Java 17/21 delta | [[30_CERTIFICATIONS/Java/Java 17 and 21 Exam Delta Matrix]] |

# Version contract

```text
1Z0-829 → compile and API reasoning against Java SE 17
1Z0-830 → compile and API reasoning against Java SE 21
```

For this route, the tested contracts are intentionally shared unless a card says otherwise:

- primitive conversions and promotions are unchanged;
- `String`, `StringBuilder` and final text-block behavior are shared;
- the core `java.time` contracts used here are shared;
- runtime examples must still be executed in both JDK lanes to prevent accidental API leakage.

# Knowledge slices

## JAVA-B01A — Values and expressions

```text
primitive domains and literal types
binary, octal, decimal and hexadecimal literals
underscores in numeric literals
widening and narrowing primitive conversion
unary and binary numeric promotion
compound assignment conversion
integer division and remainder
overflow and floating-point special values
operator precedence and short-circuiting
boxing, unboxing, wrapper caches and equality
parsing and valueOf
Math rounding, exact arithmetic and edge cases
```

Target: **25 cards**.

## JAVA-B01B — Strings, builders and text blocks

```text
String immutability and pooling
compile-time constants and interning
concatenation evaluation order
index-based methods and boundary behavior
equality, comparison and searching
strip, trim, blank and empty semantics
literal replacement versus regex replacement
split behavior and trailing empty elements
formatted strings
text-block opening, indentation and closing delimiter rules
text-block escapes: \s and line continuation
StringBuilder mutation, capacity-independent semantics and aliasing
```

Target: **25 cards**.

## JAVA-B01C — Date-time and zones

```text
immutable temporal objects
LocalDate, LocalTime and LocalDateTime
Instant and epoch-based reasoning
Period versus Duration
supported and unsupported temporal units
month-end adjustment
ZoneId, ZoneOffset, ZonedDateTime and OffsetDateTime
DST gaps and overlaps
same-instant versus same-local transformations
DateTimeFormatter patterns and resolver behavior
locale-sensitive formatting
TemporalAdjusters
```

Target: **25 cards**.

# Drill model

The 15 drills are not definition recall. Every drill requires at least one of:

```text
compile / does not compile
exact output
exception type
identity versus equality
mutation trace
DST transition reasoning
formatter parse result
```

Allocation:

| Drill family | Count |
|---|---:|
| Values and promotions | 4 |
| String and text blocks | 5 |
| Date-time and zones | 6 |
| **Total** | **15** |

# Evidence policy

Each statement must resolve to one of these evidence classes:

```text
language conversion or expression rule → JLS 21 / JLS 17
String or wrapper API result           → Java SE API
text-block lexical rule                → JLS text-block sections
java.time behavior                     → Java SE API
DST transition                         → ZoneRules plus explicit ZoneId
exact execution result                 → JAVA-B01 lab source
```

Environment-dependent defaults are forbidden in deterministic questions:

```text
no system default locale
no system default time zone
no current clock
no unspecified charset
```

Use explicit `Locale`, `ZoneId`, `Clock` or fixed values.

# Study sequence

```mermaid
flowchart LR
    A[Primitive values] --> B[Promotions and operators]
    B --> C[Wrappers and Math]
    C --> D[String identity and methods]
    D --> E[StringBuilder mutation]
    E --> F[Text blocks]
    F --> G[Local temporal types]
    G --> H[Period and Duration]
    H --> I[Instant and zones]
    I --> J[DST gaps and overlaps]
    J --> K[Format and parse]
    K --> L[Mixed drills]
```

# Completion gate

```text
[x] JAVA21-1.1 mapped to canonical, cards, drills and lab source
[x] JAVA21-1.2 mapped to canonical, cards, drills and lab source
[x] JAVA21-1.3 mapped to canonical, cards, drills and lab source
[x] 75 stable base-card IDs
[x] 15 stable drill IDs
[x] deterministic zones and locales
[x] explicit Java 17/21 lane contract
[x] no JDBC counted in this route
[ ] execute the lab on JDK 17 and JDK 21 in CI
[ ] attach CI run IDs
[ ] add delayed-review performance data
```

# Route status

The knowledge and question assets are complete for the initial B01 quota. The route is **not yet marked `lab-proven`** because CI execution on both JDK lanes has not been attached.

# Next route

```text
JAVA-B02 — Control Flow and Java 21 Pattern Switch
```

B02 may assume all B01 conversion, operator and text-output rules.