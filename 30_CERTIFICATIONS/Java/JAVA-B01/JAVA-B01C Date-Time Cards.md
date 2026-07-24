---
type: certification-batch
domain: java
subdomain: date-time-zones
batch: JAVA-B01C
status: published
card_count: 25
java_versions:
  - 17
  - 21
objectives:
  - JAVA21-1.3
tags:
  - java
  - java-time
  - zoneid
  - dst
  - active-recall
---

# JAVA-B01C — Date-Time and Zones Cards

## Route navigation

- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]]
- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills]]
- [[50_LABS/Java/JAVA-B01/README]]

---

## JAVA-B01C-C001 — Are `java.time` value types mutable?

### Russian Translation

Являются ли типы `java.time` изменяемыми?

> [!answer]- Answer
> Core types such as `LocalDate`, `Instant`, `Period`, `Duration` and `ZonedDateTime` are immutable.

### Explanation

Methods such as `plusDays` return a new value and leave the original unchanged.

### Exam Trap

Ignoring a returned temporal value has the same practical effect as ignoring a returned String.

---

## JAVA-B01C-C002 — What does `LocalDate` contain?

### Russian Translation

Какие данные содержит `LocalDate`?

> [!answer]- Answer
> A calendar date without time-of-day, offset or time zone.

### Explanation

It cannot by itself identify a unique instant on the global timeline.

### Exam Trap

Do not assume the system default zone is silently attached.

---

## JAVA-B01C-C003 — What happens when an invalid date is constructed?

### Russian Translation

Что происходит при создании несуществующей даты?

> [!answer]- Answer
> Factory methods such as `LocalDate.of` throw `DateTimeException`.

### Explanation

`LocalDate.of(2025, 2, 29)` is invalid because 2025 is not a leap year.

### Exam Trap

Construction does not automatically roll excess days into the next month.

---

## JAVA-B01C-C004 — How does `plusMonths` handle the end of a month?

### Russian Translation

Как `plusMonths` обрабатывает конец месяца?

> [!answer]- Answer
> It resolves to the last valid day when the original day does not exist in the target month.

### Explanation

2025-01-31 plus one month becomes 2025-02-28.

### Exam Trap

Adding one month is not equivalent to adding 30 or 31 days.

---

## JAVA-B01C-C005 — What does a `Period` represent?

### Russian Translation

Что представляет `Period`?

> [!answer]- Answer
> A date-based amount in years, months and days.

### Explanation

It follows calendar arithmetic rather than a fixed number of seconds.

### Exam Trap

`Period.ofWeeks(2)` is stored as 14 days, not as a separate weeks field.

---

## JAVA-B01C-C006 — What does a `Duration` represent?

### Russian Translation

Что представляет `Duration`?

> [!answer]- Answer
> A time-based amount stored as seconds plus nanoseconds.

### Explanation

It models elapsed timeline time and works naturally with instants and date-time types supporting seconds.

### Exam Trap

A date-only `LocalDate` cannot add a duration measured in hours.

---

## JAVA-B01C-C007 — What does `Period.normalized()` change?

### Russian Translation

Что изменяет `Period.normalized()`?

> [!answer]- Answer
> It combines years and months into a normalized year/month pair while leaving days separate.

### Explanation

`P1Y14M3D` normalizes to `P2Y2M3D`.

### Exam Trap

Days are not converted to months because month length is calendar-dependent.

---

## JAVA-B01C-C008 — Can a `Duration` be added to `LocalDate`?

### Russian Translation

Можно ли прибавить `Duration` к `LocalDate`?

> [!answer]- Answer
> The call compiles through the temporal API but throws `UnsupportedTemporalTypeException` at runtime.

### Explanation

`LocalDate` supports date-based units, not seconds or nanoseconds.

### Exam Trap

Distinguish method availability from support for a particular temporal unit.

---

## JAVA-B01C-C009 — What sign does `Duration.between(start, end)` use?

### Russian Translation

Как определяется знак результата `Duration.between(start, end)`?

> [!answer]- Answer
> It is positive when `end` is after `start`, negative when it is before, and zero when equal.

### Explanation

Argument order is semantically significant.

### Exam Trap

Do not take an absolute value unless the requirement explicitly asks for magnitude only.

---

## JAVA-B01C-C010 — What is `Instant.EPOCH`?

### Russian Translation

Что такое `Instant.EPOCH`?

> [!answer]- Answer
> `1970-01-01T00:00:00Z`, the Java epoch reference point in UTC.

### Explanation

An Instant is a timeline point, not a local date-time.

### Exam Trap

The `Z` suffix means UTC offset zero.

---

## JAVA-B01C-C011 — Does `LocalDateTime` identify a unique instant?

### Russian Translation

Определяет ли `LocalDateTime` уникальный момент времени?

> [!answer]- Answer
> No. It lacks an offset or zone, so the same local fields can map to different instants or no valid instant during transitions.

### Explanation

Attach a `ZoneId` or `ZoneOffset` to resolve it.

### Exam Trap

A local timestamp from a log is ambiguous unless its zone/offset contract is known.

---

## JAVA-B01C-C012 — How do `ZoneOffset` and region `ZoneId` differ?

### Russian Translation

Чем отличаются `ZoneOffset` и региональный `ZoneId`?

> [!answer]- Answer
> A `ZoneOffset` is fixed; a region `ZoneId` carries historical and future transition rules.

### Explanation

`+01:00` cannot by itself model seasonal changes, while `Europe/Berlin` can.

### Exam Trap

Current offset equality does not make a fixed offset and a region zone semantically interchangeable.

---

## JAVA-B01C-C013 — What extra information does `ZonedDateTime` have over `OffsetDateTime`?

### Russian Translation

Какая дополнительная информация есть у `ZonedDateTime` по сравнению с `OffsetDateTime`?

> [!answer]- Answer
> It can retain a region zone and therefore its transition rules; `OffsetDateTime` retains only a fixed offset.

### Explanation

Both can identify an instant when their date, time and offset are known.

### Exam Trap

Future scheduling by civil rules usually requires a region zone, not only today's offset.

---

## JAVA-B01C-C014 — What does `withZoneSameInstant` preserve?

### Russian Translation

Что сохраняет `withZoneSameInstant`?

> [!answer]- Answer
> The global timeline instant.

### Explanation

Local date/time fields change to represent that same instant in the target zone.

### Exam Trap

Do not expect the displayed clock time to remain unchanged.

---

## JAVA-B01C-C015 — What does `withZoneSameLocal` preserve?

### Russian Translation

Что сохраняет `withZoneSameLocal`?

> [!answer]- Answer
> The local date and time fields as far as possible.

### Explanation

The resulting global instant normally changes because the target zone applies a different offset.

### Exam Trap

The method name does not promise same instant.

---

## JAVA-B01C-C016 — What happens when a local time falls inside a DST gap?

### Russian Translation

Что происходит, когда локальное время попадает в DST gap?

> [!answer]- Answer
> `ZonedDateTime` normally moves the local time forward by the length of the gap.

### Explanation

In a one-hour spring transition, 02:30 can resolve to 03:30 with the later offset.

### Exam Trap

The requested local fields may not survive construction because that wall-clock time never occurred.

---

## JAVA-B01C-C017 — Which offset is chosen by default during a DST overlap?

### Russian Translation

Какой offset выбирается по умолчанию во время DST overlap?

> [!answer]- Answer
> The earlier offset is normally selected.

### Explanation

The repeated local time maps to two valid instants.

### Exam Trap

“Earlier offset” refers to overlap resolution, not necessarily the numerically smaller offset value.

---

## JAVA-B01C-C018 — How is the later occurrence in an overlap selected?

### Russian Translation

Как выбрать более позднее повторение времени в overlap?

> [!answer]- Answer
> Call `withLaterOffsetAtOverlap()` on the zoned date-time.

### Explanation

`withEarlierOffsetAtOverlap()` selects the other occurrence explicitly.

### Exam Trap

Outside an overlap these methods do not invent a transition.

---

## JAVA-B01C-C019 — Why can adding one day differ from adding 24 hours?

### Russian Translation

Почему прибавление одного дня может отличаться от прибавления 24 часов?

> [!answer]- Answer
> `Period.ofDays(1)` follows the local calendar, while `Duration.ofHours(24)` follows elapsed time; DST can change the offset between endpoints.

### Explanation

Around spring-forward, the local clock result can differ by one hour.

### Exam Trap

On ordinary dates the two results may look identical and hide the distinction.

---

## JAVA-B01C-C020 — Are DateTimeFormatter pattern letters case-sensitive?

### Russian Translation

Чувствительны ли pattern letters `DateTimeFormatter` к регистру?

> [!answer]- Answer
> Yes. For example, `M` is month while `m` is minute; `H` and `h` represent different hour systems.

### Explanation

Pattern meaning is defined per symbol and count.

### Exam Trap

A visually minor case change can produce a valid but semantically wrong formatter.

---

## JAVA-B01C-C021 — What is the difference between `u` and `y` in a date pattern?

### Russian Translation

Чем отличаются `u` и `y` в шаблоне даты?

> [!answer]- Answer
> `u` is proleptic year; `y` is year-of-era.

### Explanation

`uuuu` is usually safer for strict ISO-style parsing without an era field.

### Exam Trap

Using `yyyy` with strict resolver settings can require era semantics that the input does not provide.

---

## JAVA-B01C-C022 — What resolver style does `DateTimeFormatter.ofPattern` use by default?

### Russian Translation

Какой resolver style используется по умолчанию в `DateTimeFormatter.ofPattern`?

> [!answer]- Answer
> `ResolverStyle.SMART`.

### Explanation

SMART can adjust certain invalid day-of-month combinations to a valid date.

### Exam Trap

Pattern syntax validity does not imply strict calendar validation.

---

## JAVA-B01C-C023 — How is strict date parsing requested?

### Russian Translation

Как включить строгий разбор даты?

> [!answer]- Answer
> Use a formatter with `withResolverStyle(ResolverStyle.STRICT)` and an appropriate pattern, commonly `uuuu-MM-dd`.

### Explanation

Invalid calendar dates then throw `DateTimeParseException`.

### Exam Trap

Strict `yyyy` parsing can fail for era-related reasons; understand the selected year symbol.

---

## JAVA-B01C-C024 — Why should exact-output formatters specify a Locale?

### Russian Translation

Почему форматтеры для exact-output должны явно задавать `Locale`?

> [!answer]- Answer
> Textual month/day names and localized styles depend on locale, so the process default makes output environment-dependent.

### Explanation

Use `Locale.ENGLISH`, `Locale.forLanguageTag(...)` or another explicit locale.

### Exam Trap

Numeric-looking patterns can still contain locale-sensitive text or decimal conventions through other formatting APIs.

---

## JAVA-B01C-C025 — How do `next` and `nextOrSame` TemporalAdjusters differ?

### Russian Translation

Чем отличаются `TemporalAdjusters.next` и `nextOrSame`?

> [!answer]- Answer
> `next` always moves to a later matching weekday; `nextOrSame` retains the current date when it already matches.

### Explanation

The distinction matters at boundary dates and in recurring scheduling logic.

### Exam Trap

Do not assume `next(MONDAY)` returns today when today is Monday.
