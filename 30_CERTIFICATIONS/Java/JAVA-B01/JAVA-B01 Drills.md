---
type: certification-drill-batch
domain: java
subdomain: values-text-date-time
batch: JAVA-B01-DRILLS
status: published
drill_count: 15
java_versions:
  - 17
  - 21
objectives:
  - JAVA21-1.1
  - JAVA21-1.2
  - JAVA21-1.3
tags:
  - java
  - compile-output
  - drills
  - ocp
---

# JAVA-B01 — Compile and Output Drills

> [!instruction]
> For every question, first decide **compile / no compile**. If it compiles, predict exact output or the first uncaught exception. Use Java 17 or Java 21 as stated; every current drill has the same result on both lanes.

## Navigation

- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]]
- [[50_LABS/Java/JAVA-B01/README]]

---

## JAVA-B01-D001 — Compound assignment overflow

```java
byte b = 120;
b += 10;
System.out.println(b);
```

> [!answer]- Answer
> Compiles and prints `-126`.

### Mechanism

`b + 10` is `int`; compound assignment narrows the result back to `byte`, retaining low-order bits.

### Error taxonomy

`promotion → implicit compound cast → overflow wrap`

---

## JAVA-B01-D002 — Character promotion

```java
char c = 'A';
System.out.println(c + 1);
```

> [!answer]- Answer
> Compiles and prints `66`.

### Mechanism

`char` undergoes binary numeric promotion to `int`.

### Error taxonomy

`char treated as text instead of integral value`

---

## JAVA-B01-D003 — Wrapper identity

```java
Integer a = 127;
Integer b = 127;
Integer c = 128;
Integer d = 128;
System.out.println((a == b) + ":" + (c == d) + ":" + c.equals(d));
```

> [!answer]- Answer
> Compiles and normally prints `true:false:true`; the first result is required by the standard cache range, while identity outside that required range must not be used as a value contract.

### Mechanism

`==` compares wrapper references; `equals` compares Integer values.

### Error taxonomy

`identity mistaken for equality`

---

## JAVA-B01-D004 — NaN comparison

```java
double x = 0.0 / 0.0;
System.out.println((x == x) + ":" + Double.isNaN(x));
```

> [!answer]- Answer
> Compiles and prints `false:true`.

### Mechanism

NaN is unequal to every value, including itself.

### Error taxonomy

`ordinary equality applied to NaN`

---

## JAVA-B01-D005 — Concatenation order

```java
System.out.println(1 + 2 + "3" + 4 + 5);
```

> [!answer]- Answer
> Compiles and prints `3345`.

### Mechanism

`1 + 2` is numeric addition. After the String operand, remaining `+` operations concatenate left to right.

### Error taxonomy

`operator mode transition missed`

---

## JAVA-B01-D006 — Compile-time and runtime concatenation

```java
String part = "ja";
final String constant = "ja";
String a = part + "va";
String b = constant + "va";
System.out.println((a == "java") + ":" + (b == "java"));
```

> [!answer]- Answer
> Compiles and prints `false:true`.

### Mechanism

`part` causes runtime concatenation; `constant` is a constant variable, so the second expression is folded to the pooled literal.

### Error taxonomy

`final confused with compile-time constant`

---

## JAVA-B01-D007 — Split and trailing empty values

```java
String value = "a,b,";
System.out.println(value.split(",").length + ":" + value.split(",", -1).length);
```

> [!answer]- Answer
> Compiles and prints `2:3`.

### Mechanism

The one-argument overload removes trailing empty strings; a negative limit preserves them.

### Error taxonomy

`regex split limit ignored`

---

## JAVA-B01-D008 — Text-block indentation

```java
String value = """
        A
          B
        """;
System.out.print(value
        .replace(" ", ".")
        .replace("\n", "\\n"));
```

> [!answer]- Answer
> Compiles and prints `A\n..B\n` as visible characters.

### Mechanism

Eight incidental spaces are removed; the two additional spaces before `B` remain. The text block includes a final newline.

### Error taxonomy

`closing-delimiter indentation or final newline miscounted`

---

## JAVA-B01-D009 — StringBuilder alias and reverse

```java
StringBuilder a = new StringBuilder("ab");
StringBuilder b = a;
b.reverse();
System.out.println(a + ":" + b + ":" + (a == b));
```

> [!answer]- Answer
> Compiles and prints `ba:ba:true`.

### Mechanism

Both variables alias the same mutable builder.

### Error taxonomy

`mutable alias treated as copied value`

---

## JAVA-B01-D010 — Month arithmetic is path-dependent

```java
LocalDate d = LocalDate.of(2025, 1, 31);
System.out.println(d.plusMonths(1).plusMonths(1));
System.out.println(d.plusMonths(2));
```

> [!answer]- Answer
> Compiles and prints:
>
> ```text
> 2025-03-28
> 2025-03-31
> ```

### Mechanism

The first addition adjusts January 31 to February 28; the second starts from that adjusted date. Adding two months directly resolves in March.

### Error taxonomy

`calendar operation assumed associative`

---

## JAVA-B01-D011 — Unsupported temporal amount

```java
LocalDate d = LocalDate.of(2026, 7, 24);
System.out.println(d.plus(Duration.ofHours(1)));
```

> [!answer]- Answer
> Compiles, then throws `UnsupportedTemporalTypeException`.

### Mechanism

The generic `plus(TemporalAmount)` method exists, but `LocalDate` does not support second-based duration units.

### Error taxonomy

`method availability confused with supported unit`

---

## JAVA-B01-D012 — Calendar day versus 24 hours across DST

```java
ZoneId zone = ZoneId.of("Europe/Berlin");
ZonedDateTime start = ZonedDateTime.of(2026, 3, 28, 12, 0, 0, 0, zone);
System.out.println(start.plus(Period.ofDays(1)).toLocalTime());
System.out.println(start.plus(Duration.ofHours(24)).toLocalTime());
```

> [!answer]- Answer
> Compiles and prints:
>
> ```text
> 12:00
> 13:00
> ```

### Mechanism

A calendar day preserves local noon across the spring transition; 24 elapsed hours lands one clock-hour later.

### Error taxonomy

`Period and Duration treated as equivalent`

---

## JAVA-B01-D013 — DST gap resolution

```java
ZoneId zone = ZoneId.of("Europe/Berlin");
ZonedDateTime zdt = ZonedDateTime.of(2026, 3, 29, 2, 30, 0, 0, zone);
System.out.println(zdt);
```

> [!answer]- Answer
> Compiles and prints `2026-03-29T03:30+02:00[Europe/Berlin]`.

### Mechanism

The requested 02:30 is in a one-hour gap and is shifted forward to 03:30.

### Error taxonomy

`nonexistent local time accepted literally`

---

## JAVA-B01-D014 — DST overlap selection

```java
ZoneId zone = ZoneId.of("Europe/Berlin");
ZonedDateTime first = ZonedDateTime.of(2026, 10, 25, 2, 30, 0, 0, zone);
ZonedDateTime second = first.withLaterOffsetAtOverlap();
System.out.println(first.getOffset() + ":" + second.getOffset());
```

> [!answer]- Answer
> Compiles and prints `+02:00:+01:00`.

### Mechanism

The default uses the earlier overlap offset; the method selects the later occurrence.

### Error taxonomy

`overlap assumed to have one instant`

---

## JAVA-B01-D015 — SMART versus STRICT parsing

```java
DateTimeFormatter smart = DateTimeFormatter.ofPattern("uuuu-MM-dd");
DateTimeFormatter strict = smart.withResolverStyle(ResolverStyle.STRICT);
System.out.println(LocalDate.parse("2025-02-30", smart));
System.out.println(LocalDate.parse("2025-02-30", strict));
```

> [!answer]- Answer
> The code compiles. It first prints `2025-02-28`, then the second parse throws `DateTimeParseException`.

### Mechanism

`ofPattern` uses SMART resolution by default; STRICT rejects the invalid date.

### Error taxonomy

`pattern validity confused with resolver strictness`

# Drill completion record

```text
D001-D004  values
D005-D009  strings/builders/text blocks
D010-D015  date-time/zones
```

A drill is mastered only after the learner can state both the result and the governing rule without executing it.