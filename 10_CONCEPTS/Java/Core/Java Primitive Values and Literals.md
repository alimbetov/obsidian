---
type: atomic-concept
domain: java
route: JAVA-B01
concept_id: JAVA-B01-N01
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
next_note: Java Numeric Promotion and Casting
tags:
  - java
  - atomic-note
  - primitives
  - literals
---

# Java Primitive Values and Literals

> [!summary]
> Primitive questions are solved from literal and conversion rules first. The assignment target does not rewrite an invalid literal into another type.

## Learning outcomes

- identify all eight primitive types;
- determine default literal types;
- read binary, octal, decimal and hexadecimal literals;
- validate underscore placement;
- distinguish numeric `char` from non-numeric `boolean`.

## Primitive domains

| Type | Width | Category | Exam-relevant property |
|---|---:|---|---|
| `byte` | 8 | integral | signed, `-128..127` |
| `short` | 16 | integral | signed |
| `int` | 32 | integral | default ordinary integer literal type |
| `long` | 64 | integral | use `L` for an explicit long literal |
| `char` | 16 | integral | unsigned UTF-16 code unit |
| `float` | 32 | floating | ordinary decimal literal needs `F` |
| `double` | 64 | floating | default floating literal type |
| `boolean` | logical | non-numeric | only `true` and `false` |

`boolean` never converts to or from an integer. `char` participates in numeric promotion.

## Literal forms

```java
int decimal = 1_000_000;
int binary  = 0b1010;
int octal   = 012;
int hex     = 0xCAFE;
long large  = 3_000_000_000L;
float rate  = 2.5F;
double pi   = 3.14159;
```

A leading zero makes an integral literal octal:

```java
int value = 010;   // decimal 8
```

## Underscore rules

Underscores may separate digits. They cannot:

- start or end the literal;
- touch a decimal point;
- touch a suffix;
- appear between a radix prefix and its first digit.

```java
int ok1 = 1_000;
int ok2 = 0xCA_FE;
double ok3 = 3.14_15;

// int a = _100;
// double b = 1_.0;
// long c = 10_L;
// int d = 0x_FF;
```

## Target type does not repair a literal

```java
// long x = 3_000_000_000;  // invalid int literal
long y = 3_000_000_000L;
```

The compiler validates the literal before assignment conversion.

## `char` and Unicode

```java
char c = 'A';
int codeUnit = c;       // 65
int promoted = c + 1;   // 66
```

A `char` stores one UTF-16 code unit, not necessarily one complete Unicode code point.

## Exam traps

- `010` is octal.
- `2.5` is `double`; assigning it to `float` needs `F` or a cast.
- lowercase `l` is legal but visually ambiguous; prefer `L`.
- an invalid decimal integer literal does not become `long` because the target is `long`.
- `boolean` is not a numeric primitive.

## Active recall

1. Why does `long x = 3_000_000_000;` fail?
2. What decimal value is stored by `int x = 012;`?
3. Why does `'A' + 1` have type `int`?
4. Which underscore placements are illegal?

## Route navigation

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Route roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]]
- **Next concept:** [[10_CONCEPTS/Java/Core/Java Numeric Promotion and Casting]]
- **Practice cards:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01A Values Cards]]
- **Compile/output drills:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills]]
- **Executable lab:** [[50_LABS/Java/JAVA-B01/README]]
- **Primary sources:** [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
