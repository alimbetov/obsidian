---
type: atomic-concept
domain: java
route: JAVA-B01
concept_id: JAVA-B01-N09
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
prerequisites:
  - JAVA-B01-N08
previous_note: Java Period Duration and Instant
tags:
  - java
  - atomic-note
  - zoneid
  - dst
  - datetimeformatter
---

# Java Zones DST and Formatting

> [!summary]
> Region zones map local fields to instants using transition rules. Exact-output questions must use explicit zones, locales and resolver styles.

## Offset versus region zone

`ZoneOffset` is fixed:

```java
ZoneOffset.of("+05:00");
```

A region `ZoneId` has historical and future rules:

```java
ZoneId.of("Asia/Almaty");
ZoneId.of("Europe/Berlin");
```

A fixed offset does not model DST.

## `OffsetDateTime` and `ZonedDateTime`

| Type | Has offset | Has region rules |
|---|---|---|
| `OffsetDateTime` | yes | no |
| `ZonedDateTime` | yes | yes, for a region `ZoneId` |

Use `ZonedDateTime` when future calendar behavior must follow named-region rules.

## Same instant versus same local fields

```java
ZonedDateTime almaty = ZonedDateTime.of(
        2026, 7, 24, 12, 0, 0, 0,
        ZoneId.of("Asia/Almaty"));

ZonedDateTime sameInstant =
        almaty.withZoneSameInstant(ZoneId.of("UTC"));

ZonedDateTime sameLocal =
        almaty.withZoneSameLocal(ZoneId.of("UTC"));
```

- `withZoneSameInstant` preserves the timeline point.
- `withZoneSameLocal` preserves local fields and changes the timeline point.

## DST gap

During a spring-forward gap, some local times do not exist.

```java
ZonedDateTime gap = ZonedDateTime.of(
        2026, 3, 29, 2, 30, 0, 0,
        ZoneId.of("Europe/Berlin"));
```

The invalid local `02:30` is normally shifted forward to `03:30+02:00`.

## DST overlap

During a fall-back overlap, one local time occurs twice.

```java
ZonedDateTime overlap = ZonedDateTime.of(
        2026, 10, 25, 2, 30, 0, 0,
        ZoneId.of("Europe/Berlin"));
```

The earlier offset is normally selected. Use:

```java
overlap.withLaterOffsetAtOverlap();
overlap.withEarlierOffsetAtOverlap();
```

## Formatter patterns

| Symbol | Meaning |
|---|---|
| `u` | proleptic year |
| `y` | year-of-era |
| `M` | month |
| `m` | minute |
| `d` | day-of-month |
| `D` | day-of-year |
| `H` | hour `0..23` |
| `h` | clock-hour `1..12` |
| `s` | second |
| `S` | fraction |
| `a` | AM/PM |
| `E` | day-of-week text |
| `X` | offset |
| `V` | zone ID |

`MM` is month; `mm` is minute. Prefer `uuuu` for ISO-style strict parsing.

## Resolver styles

`DateTimeFormatter.ofPattern` uses SMART by default:

```java
DateTimeFormatter smart =
        DateTimeFormatter.ofPattern("uuuu-MM-dd");

LocalDate.parse("2025-02-30", smart); // 2025-02-28
```

Strict validation:

```java
DateTimeFormatter strict =
        smart.withResolverStyle(ResolverStyle.STRICT);

LocalDate.parse("2025-02-30", strict); // DateTimeParseException
```

## Locale and adjusters

Use an explicit locale:

```java
DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.ENGLISH);
```

Useful adjusters:

```java
TemporalAdjusters.firstDayOfMonth();
TemporalAdjusters.lastDayOfMonth();
TemporalAdjusters.next(DayOfWeek.MONDAY);
TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY);
```

`next` always moves. `nextOrSame` may retain the current date.

## Exam protocol

1. Classify the value as local, offset-based, zoned or instant.
2. Identify the exact region zone or offset.
3. Inspect gap or overlap rules.
4. Distinguish calendar and elapsed arithmetic.
5. Check pattern-letter case.
6. Check resolver style.
7. Use an explicit locale.

## Exam traps

- default system zone and locale make output environment-dependent.
- `withZoneSameLocal` does not preserve the instant.
- gap times are adjusted.
- overlap times are ambiguous.
- `MM` and `mm` mean different fields.
- SMART parsing may normalize an invalid date.
- `next` and `nextOrSame` differ on an already matching day.

## Route navigation

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Route roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]]
- **Previous concept:** [[10_CONCEPTS/Java/Core/Java Period Duration and Instant]]
- **Practice cards:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01C Date-Time Cards]]
- **Compile/output drills:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills]]
- **Executable lab:** [[50_LABS/Java/JAVA-B01/README]]
- **Primary sources:** [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
