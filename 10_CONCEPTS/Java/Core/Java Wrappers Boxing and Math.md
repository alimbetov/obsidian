---
type: atomic-concept
domain: java
route: JAVA-B01
concept_id: JAVA-B01-N03
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
prerequisites:
  - JAVA-B01-N02
previous_note: Java Numeric Promotion and Casting
next_note: Java String Identity and Operations
tags:
  - java
  - atomic-note
  - wrappers
  - boxing
  - math
---

# Java Wrappers Boxing and Math

> [!summary]
> Wrapper questions require separating primitive value semantics from reference identity and remembering that arithmetic usually unboxes first.

## Boxing and unboxing

```java
Integer boxed = 10;
int value = boxed;
```

Unboxing `null` compiles and fails at runtime:

```java
Integer boxed = null;
int value = boxed;   // NullPointerException
```

Mixed arithmetic normally unboxes wrappers:

```java
Integer a = 2;
int result = a + 3;
```

## Identity versus equality

When both operands are references, `==` checks identity.

```java
Integer a = 100;
Integer b = 100;
System.out.println(a == b);      // true: required cache range
System.out.println(a.equals(b)); // true
```

The required `Integer` cache range is `-128..127`.

Do not rely on identity outside that range:

```java
Integer c = 1000;
Integer d = 1000;
System.out.println(c == d);      // normally false
```

Wrapper `equals` checks wrapper type:

```java
Integer i = 1;
Long l = 1L;
System.out.println(i.equals(l)); // false
```

## Parsing and factories

```java
int primitive = Integer.parseInt("42");
Integer boxed = Integer.valueOf("42");
int binary = Integer.parseInt("101", 2); // 5
```

Invalid numeric text throws `NumberFormatException`.

## Useful `Math` methods

```java
Math.abs(-5);          // 5
Math.max(3, 7);        // 7
Math.min(3, 7);        // 3
Math.floor(2.9);       // 2.0
Math.ceil(2.1);        // 3.0
Math.round(2.5f);      // int 3
Math.round(2.5d);      // long 3
Math.pow(2, 3);        // double 8.0
Math.sqrt(9);          // double 3.0
```

Exact methods throw on overflow:

```java
Math.addExact(Integer.MAX_VALUE, 1);
Math.multiplyExact(100_000, 100_000);
```

## Minimum-value edge case

```java
Math.abs(Integer.MIN_VALUE) == Integer.MIN_VALUE
```

There is no positive `int` representation of `2147483648`.

## Exam traps

- `Integer == Integer` is identity, not numeric comparison.
- cache behavior is guaranteed only for the required range.
- `Integer.equals(Long)` is false.
- unboxing `null` fails at runtime.
- `parseInt` returns `int`; `valueOf` returns a wrapper.
- `Math.round(float)` returns `int`; `Math.round(double)` returns `long`.
- `Math.abs(MIN_VALUE)` remains negative.

## Active recall

1. Why can two `Integer` objects containing `100` be identical?
2. What happens when a null wrapper participates in arithmetic?
3. Why is `Integer.valueOf("1").equals(Long.valueOf("1"))` false?
4. Which Math methods detect overflow?

## Route navigation

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Route roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]]
- **Previous concept:** [[10_CONCEPTS/Java/Core/Java Numeric Promotion and Casting]]
- **Next concept:** [[10_CONCEPTS/Java/Core/Java String Identity and Operations]]
- **Practice cards:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01A Values Cards]]
- **Compile/output drills:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills]]
- **Executable lab:** [[50_LABS/Java/JAVA-B01/README]]
- **Primary sources:** [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
