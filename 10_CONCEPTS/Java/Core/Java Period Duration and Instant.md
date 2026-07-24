---
type: atomic-concept
domain: java
route: JAVA-B01
concept_id: JAVA-B01-N08
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
prerequisites:
  - JAVA-B01-N07
previous_note: Java Local Date-Time Types
next_note: Java Zones DST and Formatting
tags:
  - java
  - atomic-note
  - period
  - duration
  - instant
---

# Java Period Duration and Instant

> [!summary]
> `Period` follows calendar units, `Duration` follows elapsed time, and `Instant` identifies a UTC timeline point.

## `Period`

`Period` stores years, months and days.

```java
Period p = Period.of(1, 14, 3);
System.out.println(p);              // P1Y14M3D
System.out.println(p.normalized()); // P2Y2M3D
```

Normalization combines years and months. It does not convert days into months.

```java
Period.ofDays(10);
Period.ofWeeks(2);   // represented as 14 days
Period.ofMonths(3);
Period.ofYears(1);
```

## `Duration`

`Duration` stores seconds plus nanoseconds.

```java
Duration.ofHours(2);
Duration.ofMinutes(90);
Duration.between(startInstant, endInstant);
```

It works naturally with `Instant`, `LocalTime`, `LocalDateTime`, `OffsetDateTime` and `ZonedDateTime`.

A date-only type does not support seconds.

## `Instant`

`Instant` is a global timeline point relative to the UTC epoch.

```java
Instant epoch = Instant.EPOCH;
Instant value = Instant.parse("2026-07-24T03:00:00Z");
```

It contains no region-zone rules and no human local calendar fields by itself.

## Calendar day versus 24 hours

Around DST, these operations can differ:

```java
ZoneId zone = ZoneId.of("Europe/Berlin");
ZonedDateTime start =
        ZonedDateTime.of(2026, 3, 28, 12, 0, 0, 0, zone);

start.plus(Period.ofDays(1));      // local 12:00 next day
start.plus(Duration.ofHours(24)); // local 13:00 next day
```

`Period` follows the local calendar. `Duration` follows elapsed timeline time.

## Choosing the correct model

```text
calendar components → Period
elapsed seconds      → Duration
global timestamp     → Instant
```

`Period.between` compares dates. `Duration.between` requires compatible values that support seconds.

## Exam traps

- `Period.ofWeeks(2)` stores 14 days.
- `normalized()` does not normalize days into months.
- `Duration` cannot be added to date-only values.
- an `Instant` has no named zone.
- one calendar day is not always 24 elapsed hours.
- local date-time arithmetic and instant arithmetic answer different questions.

## Active recall

1. What does `Period.normalized()` change?
2. Why can one day differ from 24 hours?
3. Which type represents a UTC timeline point?
4. Why is `Duration` invalid for a `LocalDate`?

## Route navigation

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Route roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]]
- **Previous concept:** [[10_CONCEPTS/Java/Core/Java Local Date-Time Types]]
- **Next concept:** [[10_CONCEPTS/Java/Core/Java Zones DST and Formatting]]
- **Practice cards:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01C Date-Time Cards]]
- **Compile/output drills:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills]]
- **Executable lab:** [[50_LABS/Java/JAVA-B01/README]]
- **Primary sources:** [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
