---
type: atomic-concept
domain: java
route: JAVA-B01
concept_id: JAVA-B01-N07
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
prerequisites:
  - JAVA-B01-N06
previous_note: Java Text Blocks
next_note: Java Period Duration and Instant
tags:
  - java
  - atomic-note
  - java-time
  - localdate
  - localdatetime
---

# Java Local Date-Time Types

> [!summary]
> Local temporal types model calendar fields without an offset or region zone. They are immutable values, not global timeline points.

## Core local types

| Type | Contains | Does not contain |
|---|---|---|
| `LocalDate` | date | time, offset, zone |
| `LocalTime` | time | date, offset, zone |
| `LocalDateTime` | date and time | offset, zone |

A local value becomes a timeline point only after applying an offset or zone.

## Immutability

```java
LocalDate date = LocalDate.of(2026, 1, 31);
date.plusDays(1);
System.out.println(date); // 2026-01-31

date = date.plusDays(1);
System.out.println(date); // 2026-02-01
```

## Construction and validation

```java
LocalDate date = LocalDate.of(2026, 7, 24);
LocalTime time = LocalTime.of(14, 30);
LocalDateTime dateTime = LocalDateTime.of(date, time);
```

Invalid calendar values throw `DateTimeException`.

```java
LocalDate.of(2026, 2, 30); // DateTimeException
```

## Month-end adjustment

Adding months resolves to a valid date in the target month:

```java
LocalDate.of(2025, 1, 31).plusMonths(1); // 2025-02-28
LocalDate.of(2024, 1, 31).plusMonths(1); // 2024-02-29
```

Calendar month arithmetic is not equivalent to adding a fixed number of days.

## Supported-unit boundary

A temporal type supports only units meaningful for its model:

```java
LocalDate date = LocalDate.of(2026, 1, 1);
date.plusDays(1);               // valid
date.plus(Duration.ofHours(1)); // UnsupportedTemporalTypeException
```

Similarly, a year- or month-based `Period` cannot naturally be applied to `LocalTime`.

## Comparison

Local values compare their local fields:

```java
date1.isBefore(date2);
time1.isAfter(time2);
dateTime1.compareTo(dateTime2);
```

This is not instant comparison because no offset exists.

## Exam traps

- ignoring a returned value loses an update.
- invalid dates fail immediately.
- `LocalDateTime` is not an instant.
- adding months adjusts month-end.
- unsupported units fail at runtime.
- local comparisons do not involve time-zone transitions.

## Active recall

1. Why is `LocalDateTime` not globally unique?
2. What happens to January 31 plus one month?
3. Why does `LocalDate.plus(Duration.ofHours(1))` fail?
4. How does immutability affect chained calculations?

## Route navigation

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Route roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]]
- **Previous concept:** [[10_CONCEPTS/Java/Core/Java Text Blocks]]
- **Next concept:** [[10_CONCEPTS/Java/Core/Java Period Duration and Instant]]
- **Practice cards:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01C Date-Time Cards]]
- **Compile/output drills:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills]]
- **Executable lab:** [[50_LABS/Java/JAVA-B01/README]]
- **Primary sources:** [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
