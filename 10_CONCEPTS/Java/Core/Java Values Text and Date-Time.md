---
type: canonical-concept
domain: java
subdomain: values-text-date-time
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
objectives:
  - JAVA21-1.1
  - JAVA21-1.2
  - JAVA21-1.3
tags:
  - java
  - primitives
  - strings
  - text-blocks
  - java-time
  - dst
---

# Java Values, Text and Date-Time

> [!summary]
> This note is the canonical mechanism reference for `JAVA-B01`. It focuses on rules that determine compilation, exact output or thrown exceptions. The default reasoning baseline is Java 21 for `1Z0-830`; the covered rules also apply to the Java 17 lane unless explicitly stated otherwise.

# 1. Values and primitive types

## 1.1 Primitive domains

| Type | Width | Integral/floating | Notes |
|---|---:|---|---|
| `byte` | 8 | integral | signed, `-128..127` |
| `short` | 16 | integral | signed |
| `int` | 32 | integral | default integral literal type |
| `long` | 64 | integral | suffix `L` or `l`; prefer `L` |
| `char` | 16 | integral | unsigned UTF-16 code unit |
| `float` | 32 | floating | suffix `F` or `f` required for ordinary decimal literals |
| `double` | 64 | floating | default floating literal type |
| `boolean` | JVM-dependent representation | neither numeric nor integral | only `true` and `false` |

`boolean` does not convert to or from numeric primitives. `char` participates in numeric promotion even though it commonly represents text.

## 1.2 Literal rules

```java
int decimal = 1_000_000;
int binary  = 0b1010;
int octal   = 012;
int hex     = 0xCAFE;
long large  = 3_000_000_000L;
float rate  = 2.5F;
double pi   = 3.14159;
```

Underscores may separate digits but cannot appear:

- at the beginning or end of a literal;
- immediately before or after a decimal point;
- immediately before a type suffix;
- between a radix prefix and the first digit.

```java
int a = 1_000;       // valid
// int b = _1000;    // invalid
// double c = 1_.0;  // invalid
// long d = 10_L;    // invalid
```

A decimal integer literal that does not fit in `int` requires `L`; the compiler does not first infer `long` from the assignment target.

```java
// long x = 3_000_000_000;  // does not compile
long y = 3_000_000_000L;    // compiles
```

## 1.3 Widening and narrowing

Widening primitive conversion is generally implicit:

```text
byte → short → int → long → float → double
char → int → long → float → double
```

`byte` and `short` do not widen to `char`, and `char` does not widen to `short`.

Narrowing requires an explicit cast unless a constant-expression assignment rule applies:

```java
int i = 130;
byte b = (byte) i;   // -126

final int CONSTANT = 100;
byte ok = CONSTANT;  // constant value fits

int variable = 100;
// byte no = variable; // does not compile
```

A narrowing cast preserves low-order bits; it does not clamp to the target range.

## 1.4 Unary and binary numeric promotion

Unary numeric promotion converts `byte`, `short` and `char` operands to `int` for unary numeric operators.

```java
byte b = 1;
// byte c = +b;   // result is int
int c = +b;
```

For most binary numeric operators:

```text
if either operand is double → both become double
else if either is float     → both become float
else if either is long      → both become long
else                        → both become int
```

```java
short a = 2;
short b = 3;
int sum = a + b;
// short s = a + b; // does not compile
```

## 1.5 Compound assignment

Compound assignment includes an implicit cast back to the left-hand type:

```java
byte b = 120;
b += 10;                 // equivalent to b = (byte) (b + 10)
System.out.println(b);    // -126
```

This is not equivalent to ordinary assignment:

```java
byte b = 1;
// b = b + 1; // does not compile: b + 1 is int
b += 1;       // compiles
```

The left-hand expression is evaluated only once. This matters for arrays and side effects.

## 1.6 Integer division and remainder

Integral division truncates toward zero:

```java
System.out.println(7 / 3);    // 2
System.out.println(-7 / 3);   // -2
```

The remainder has the sign of the dividend:

```java
System.out.println(-7 % 3);   // -1
System.out.println(7 % -3);   // 1
```

For nonzero divisor `b`, Java satisfies:

```text
(a / b) * b + (a % b) == a
```

Integer division by zero throws `ArithmeticException`; floating-point division by zero produces infinity or NaN.

## 1.7 Overflow and exact arithmetic

Integral overflow wraps in two's-complement arithmetic:

```java
int max = Integer.MAX_VALUE;
System.out.println(max + 1);  // -2147483648
```

Use exact Math methods when overflow must be detected:

```java
Math.addExact(Integer.MAX_VALUE, 1); // ArithmeticException
Math.multiplyExact(100_000, 100_000); // ArithmeticException for int overload
```

## 1.8 Floating-point special values

```java
double positiveInfinity = 1.0 / 0.0;
double negativeInfinity = -1.0 / 0.0;
double nan = 0.0 / 0.0;
```

`NaN` is unequal to every value, including itself:

```java
double x = Double.NaN;
System.out.println(x == x);          // false
System.out.println(Double.isNaN(x)); // true
```

Positive and negative zero compare equal with `==`, but some API methods preserve the distinction:

```java
System.out.println(0.0 == -0.0);              // true
System.out.println(Double.compare(0.0, -0.0)); // 1
```

## 1.9 Boolean operators and short-circuiting

`&&` and `||` short-circuit. `&` and `|` on booleans evaluate both operands.

```java
int x = 0;
boolean a = false && ++x > 0; // x remains 0
boolean b = false &  ++x > 0; // x becomes 1
```

`^` is exclusive OR for booleans: true when operands differ.

## 1.10 Operator precedence

High-value order, from stronger to weaker:

```text
postfix ++ --
unary ++ -- + - ! ~ cast
* / %
+ -
shifts
relational < <= > >= instanceof
equality == !=
bitwise & ^ |
logical && ||
conditional ?:
assignment = += -= ...
```

Do not use precedence as a memorization substitute when code can be parenthesized. Exam questions, however, often omit parentheses deliberately.

## 1.11 Conditional operator typing

The conditional operator may apply numeric promotion or constant narrowing rules. Always determine the types of the second and third operands before considering the assignment target.

```java
byte b = 10;
int i = true ? b : 20;   // expression can be narrowed by constant rule in some combinations
```

For unrelated reference types, the result type is a common compatible type determined by the language rules, not simply `Object` in every case.

# 2. Wrappers and Math

## 2.1 Boxing and unboxing

Boxing converts a primitive to its wrapper. Unboxing converts a wrapper to its primitive.

```java
Integer boxed = 10;
int primitive = boxed;
```

Unboxing `null` throws `NullPointerException`:

```java
Integer boxed = null;
// int value = boxed; // compiles, throws at runtime
```

Mixed arithmetic normally unboxes wrappers first.

## 2.2 Identity versus equality

`==` compares wrapper references when both operands are references. `equals` compares wrapper value and wrapper type.

```java
Integer a = 100;
Integer b = 100;
System.out.println(a == b);      // commonly true by required Integer cache range
System.out.println(a.equals(b)); // true

Integer c = 1000;
Integer d = 1000;
System.out.println(c == d);      // do not rely on identity; normally false
```

The required `Integer` cache includes `-128..127`. Use `equals` for value comparison.

Different wrapper classes are not equal even when numerically similar:

```java
Integer i = 1;
Long l = 1L;
System.out.println(i.equals(l)); // false
```

## 2.3 Parsing and factory methods

```java
int a = Integer.parseInt("42");      // primitive int
Integer b = Integer.valueOf("42");   // Integer
int c = Integer.parseInt("101", 2);  // 5
```

Invalid numeric text throws `NumberFormatException`.

## 2.4 Math methods

```java
Math.abs(-5)          // 5
Math.max(3, 7)        // 7
Math.min(3, 7)        // 3
Math.floor(2.9)       // 2.0
Math.ceil(2.1)        // 3.0
Math.round(2.5f)      // int 3
Math.round(2.5d)      // long 3
Math.pow(2, 3)        // double 8.0
Math.sqrt(9)          // double 3.0
```

A classic edge case:

```java
Math.abs(Integer.MIN_VALUE) == Integer.MIN_VALUE
```

There is no positive `int` counterpart for the minimum value.

# 3. String

## 3.1 Immutability

A `String` operation returns a value; it does not mutate the existing instance.

```java
String s = "java";
s.toUpperCase();
System.out.println(s); // java
s = s.toUpperCase();
System.out.println(s); // JAVA
```

## 3.2 Pooling and compile-time constants

String literals are interned. Compile-time constant concatenation is folded and can refer to the same pooled instance.

```java
String a = "java";
String b = "ja" + "va";
System.out.println(a == b); // true
```

Runtime concatenation generally creates a distinct string object:

```java
String part = "ja";
String c = part + "va";
System.out.println(a == c);       // false
System.out.println(a.equals(c));  // true
```

`intern()` returns the canonical pooled representation.

## 3.3 Concatenation evaluation order

String concatenation proceeds left to right once a `String` operand is involved:

```java
System.out.println(1 + 2 + "3"); // 33
System.out.println("1" + 2 + 3); // 123
```

Parentheses can restore numeric addition:

```java
System.out.println("1" + (2 + 3)); // 15
```

## 3.4 Index contracts

Indexes are zero-based. End indexes are usually exclusive.

```java
String s = "certify";
s.length();          // 7
s.charAt(0);         // 'c'
s.substring(1, 4);  // "ert"
s.indexOf('t');      // 3
s.lastIndexOf('i');  // 5
```

`charAt(length())` throws `StringIndexOutOfBoundsException`.

`substring(begin, end)` requires:

```text
0 <= begin <= end <= length
```

## 3.5 Equality and ordering

```java
"Java".equals("java")            // false
"Java".equalsIgnoreCase("java")  // true
"abc".compareTo("abd")           // negative
```

`compareTo` promises negative, zero or positive—not specifically `-1`, `0`, `1`.

## 3.6 Blank, empty, trim and strip

```java
"".isEmpty()      // true
"   ".isEmpty()   // false
"   ".isBlank()   // true
```

`trim()` removes characters with code point at most U+0020 from the ends. `strip()` uses Unicode-aware whitespace classification.

Related methods:

```java
strip()
stripLeading()
stripTrailing()
```

## 3.7 Replacement and splitting

`replace` treats arguments literally:

```java
"a.b".replace(".", "-") // "a-b"
```

`replaceAll` and `replaceFirst` use regular expressions:

```java
"a.b".replaceAll(".", "-") // "---" because . matches any character
```

`split` uses a regex. With the one-argument overload, trailing empty strings are discarded:

```java
"a,b,".split(",").length     // 2
"a,b,".split(",", -1).length // 3
```

## 3.8 Formatting

```java
String result = "%s:%02d".formatted("A", 7); // A:07
String same = String.format("%s:%02d", "A", 7);
```

Formatting may be locale-sensitive. Use an explicit locale for deterministic decimal, grouping or date output.

# 4. StringBuilder

## 4.1 Mutation and chaining

`StringBuilder` is mutable; most mutator methods return the same builder for chaining.

```java
StringBuilder b = new StringBuilder("ab");
b.append("cd").insert(1, "X");
System.out.println(b); // aXbcd
```

## 4.2 Key index methods

```java
append(value)
insert(offset, value)
delete(start, end)
deleteCharAt(index)
replace(start, end, text)
reverse()
setCharAt(index, char)
setLength(newLength)
substring(start, end) // returns String
```

End indexes for `delete`, `replace` and `substring` are exclusive.

If `setLength` increases the length, null characters (`\u0000`) fill the new positions.

## 4.3 Aliasing

Two variables can refer to the same mutable builder:

```java
StringBuilder a = new StringBuilder("x");
StringBuilder b = a;
b.append("y");
System.out.println(a); // xy
```

## 4.4 Equality

`StringBuilder` does not override `Object.equals` for content equality.

```java
StringBuilder a = new StringBuilder("java");
StringBuilder b = new StringBuilder("java");
System.out.println(a.equals(b));                  // false
System.out.println(a.toString().equals(b.toString())); // true
```

# 5. Text blocks

## 5.1 A text block is a String

```java
String json = """
        {
          "name": "Java"
        }
        """;
```

The result is an ordinary `String`. No JSON parsing or templating occurs automatically.

## 5.2 Opening delimiter

The opening delimiter consists of three double quotes followed only by optional whitespace and then a line terminator. Content starts on the next line.

```java
String ok = """
        text
        """;
```

Putting content immediately after the opening delimiter is invalid.

## 5.3 Incidental indentation

The compiler removes incidental indentation based on the least-indented nonblank line and the closing delimiter position. The closing delimiter is therefore semantically significant.

```java
String a = """
        one
          two
        """;
```

Result:

```text
one\n
  two\n
```

Moving the closing delimiter left or right can change retained indentation.

## 5.4 Text-block escapes

`\s` preserves a space at a line end or creates an explicit space:

```java
String s = """
        a\s
        b
        """;
```

A backslash at the physical end of a source line suppresses the resulting newline:

```java
String s = """
        one\
        two
        """;
// "onetwo\n"
```

Ordinary escapes such as `\n`, `\t`, `\"` and `\\` remain available.

# 6. Date-time model

## 6.1 Immutable value types

Core `java.time` classes are immutable. Operations return new values.

```java
LocalDate d = LocalDate.of(2026, 1, 31);
d.plusDays(1);
System.out.println(d); // 2026-01-31

d = d.plusDays(1);
System.out.println(d); // 2026-02-01
```

## 6.2 Local types

| Type | Contains | Does not contain |
|---|---|---|
| `LocalDate` | date | time, offset, zone |
| `LocalTime` | time | date, offset, zone |
| `LocalDateTime` | date and time | offset, zone |

These types are not global timeline points until combined with an offset or zone.

Invalid calendar values throw `DateTimeException`:

```java
// LocalDate.of(2026, 2, 30); // DateTimeException
```

## 6.3 Month-end adjustment

Adding months resolves to a valid date in the target month:

```java
LocalDate.of(2025, 1, 31).plusMonths(1) // 2025-02-28
LocalDate.of(2024, 1, 31).plusMonths(1) // 2024-02-29
```

This is not the same as adding a fixed number of days.

## 6.4 Period

`Period` is date-based and stores years, months and days.

```java
Period p = Period.of(1, 14, 3);
System.out.println(p);             // P1Y14M3D
System.out.println(p.normalized()); // P2Y2M3D
```

Normalization combines years and months but does not convert days into months.

Common factories:

```java
Period.ofDays(10)
Period.ofWeeks(2)   // represented as 14 days
Period.ofMonths(3)
Period.ofYears(1)
```

## 6.5 Duration

`Duration` is time-based and stores seconds plus nanoseconds.

```java
Duration.ofHours(2)
Duration.ofMinutes(90)
Duration.between(startInstant, endInstant)
```

Duration works naturally with `Instant`, `LocalTime`, `LocalDateTime`, `OffsetDateTime` and `ZonedDateTime`; a date-only type does not support seconds.

```java
LocalDate d = LocalDate.of(2026, 1, 1);
// d.plus(Duration.ofHours(1)); // UnsupportedTemporalTypeException
```

Likewise, adding a year-based `Period` to `LocalTime` is unsupported.

## 6.6 Period versus Duration across DST

For a zoned date-time, one calendar day and 24 hours can produce different local times around DST:

```java
ZoneId zone = ZoneId.of("Europe/Berlin");
ZonedDateTime start = ZonedDateTime.of(2026, 3, 28, 12, 0, 0, 0, zone);

start.plus(Period.ofDays(1));   // 2026-03-29T12:00+02:00
start.plus(Duration.ofHours(24)); // 2026-03-29T13:00+02:00
```

`Period` follows the local calendar; `Duration` follows elapsed timeline time.

# 7. Instant, offsets and zones

## 7.1 Instant

`Instant` is a timeline point relative to UTC epoch.

```java
Instant epoch = Instant.EPOCH; // 1970-01-01T00:00:00Z
Instant x = Instant.parse("2026-07-24T03:00:00Z");
```

An `Instant` has no region-zone rules and no local calendar representation by itself.

## 7.2 ZoneOffset and ZoneId

`ZoneOffset` is a fixed offset such as `+05:00`.

`ZoneId` can be:

- a region identifier such as `Asia/Almaty`;
- a fixed offset identifier.

Region zones carry historical and future transition rules. A fixed offset does not model DST.

## 7.3 ZonedDateTime and OffsetDateTime

| Type | Offset | Region rules |
|---|---|---|
| `OffsetDateTime` | yes | no |
| `ZonedDateTime` | yes | yes, when backed by region `ZoneId` |

`ZonedDateTime` is appropriate when future calendar behavior must follow a named region's rules.

## 7.4 Same instant versus same local fields

```java
ZonedDateTime almaty = ZonedDateTime.of(
        2026, 7, 24, 12, 0, 0, 0,
        ZoneId.of("Asia/Almaty"));

ZonedDateTime utcSameInstant = almaty.withZoneSameInstant(ZoneId.of("UTC"));
ZonedDateTime utcSameLocal   = almaty.withZoneSameLocal(ZoneId.of("UTC"));
```

- `withZoneSameInstant` preserves the timeline point and changes local fields;
- `withZoneSameLocal` preserves local fields and changes the timeline point.

# 8. DST gaps and overlaps

## 8.1 Gap

During a spring-forward gap, a range of local times does not exist. Constructing a `ZonedDateTime` from such a local time normally shifts it forward by the gap length.

For `Europe/Berlin` on 2026-03-29:

```java
ZonedDateTime gap = ZonedDateTime.of(
        2026, 3, 29, 2, 30, 0, 0,
        ZoneId.of("Europe/Berlin"));
```

The local time `02:30` is in the gap and resolves to `03:30+02:00`.

## 8.2 Overlap

During a fall-back overlap, the same local time occurs twice. The default resolution normally chooses the earlier offset.

For `Europe/Berlin` on 2026-10-25:

```java
ZonedDateTime overlap = ZonedDateTime.of(
        2026, 10, 25, 2, 30, 0, 0,
        ZoneId.of("Europe/Berlin"));
```

Default result uses `+02:00`. Select the later occurrence with:

```java
overlap.withLaterOffsetAtOverlap(); // +01:00
```

Use `withEarlierOffsetAtOverlap()` to select the earlier offset explicitly.

# 9. Formatting and parsing

## 9.1 Predefined formatters

```java
DateTimeFormatter.ISO_LOCAL_DATE
DateTimeFormatter.ISO_LOCAL_DATE_TIME
DateTimeFormatter.ISO_OFFSET_DATE_TIME
DateTimeFormatter.ISO_ZONED_DATE_TIME
DateTimeFormatter.ISO_INSTANT
```

## 9.2 Pattern letters are case-sensitive

High-value symbols:

| Symbol | Meaning |
|---|---|
| `u` | proleptic year |
| `y` | year-of-era |
| `M` | month-of-year |
| `m` | minute-of-hour |
| `d` | day-of-month |
| `D` | day-of-year |
| `H` | hour 0-23 |
| `h` | clock-hour 1-12 |
| `s` | second |
| `S` | fraction-of-second |
| `a` | AM/PM |
| `E` | day-of-week text |
| `X` | offset-X and Z |
| `z` | zone name text |
| `V` | zone-id |

`MM` is month; `mm` is minute.

For ISO-style year parsing, prefer `uuuu` unless era-based behavior is intended.

## 9.3 Resolver styles

`DateTimeFormatter.ofPattern(...)` uses SMART resolver style by default.

```java
DateTimeFormatter smart = DateTimeFormatter.ofPattern("uuuu-MM-dd");
LocalDate.parse("2025-02-30", smart); // resolves to 2025-02-28 under SMART
```

Strict validation:

```java
DateTimeFormatter strict = smart.withResolverStyle(ResolverStyle.STRICT);
// LocalDate.parse("2025-02-30", strict); // DateTimeParseException
```

LENIENT performs broader arithmetic normalization.

## 9.4 Locale

Textual month/day names and localized formats require deterministic locale selection:

```java
DateTimeFormatter f = DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.ENGLISH);
```

Never assume the environment's default locale in an exact-output question.

# 10. TemporalAdjusters

Useful adjusters:

```java
TemporalAdjusters.firstDayOfMonth()
TemporalAdjusters.lastDayOfMonth()
TemporalAdjusters.firstDayOfNextMonth()
TemporalAdjusters.next(DayOfWeek.MONDAY)
TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)
TemporalAdjusters.previous(DayOfWeek.FRIDAY)
```

`next` always moves forward; `nextOrSame` can retain the current date.

# 11. Exam reasoning protocol

For every compile/output question:

1. Fix the Java version.
2. Determine each literal and expression type.
3. Apply promotion before assignment conversion.
4. Separate reference identity from value equality.
5. Trace mutable aliases.
6. For text blocks, visualize source indentation and final newline.
7. Classify temporal values as local, offset-based, zoned or instant.
8. Distinguish calendar arithmetic (`Period`) from elapsed time (`Duration`).
9. Use explicit zone transition rules for gaps and overlaps.
10. Check formatter symbol case and resolver style.

# Related

- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01A Values Cards]]
- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01B Text Cards]]
- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01C Date-Time Cards]]
- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills]]
- [[50_LABS/Java/JAVA-B01/README]]
- [[98_SOURCES/Java SE 21 1Z0-830 Sources]]