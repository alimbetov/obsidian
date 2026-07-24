---
type: atomic-concept
domain: java
route: JAVA-B01
concept_id: JAVA-B01-N02
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
prerequisites:
  - JAVA-B01-N01
previous_note: Java Primitive Values and Literals
next_note: Java Wrappers Boxing and Math
tags:
  - java
  - atomic-note
  - promotion
  - casting
  - operators
---

# Java Numeric Promotion and Casting

> [!summary]
> Java evaluates an expression using promotion rules before assignment conversion. Most compile traps come from skipping that order.

## Widening conversion

```text
byte → short → int → long → float → double
char → int → long → float → double
```

`byte` and `short` do not widen to `char`. `char` does not widen to `short`.

## Narrowing conversion

```java
int i = 130;
byte b = (byte) i;   // -126
```

A cast preserves low-order bits. It does not clamp the value.

A constant expression may be narrowed when it fits:

```java
final int CONSTANT = 100;
byte ok = CONSTANT;

int variable = 100;
// byte no = variable;
```

## Unary numeric promotion

Unary numeric operators promote `byte`, `short` and `char` to `int`.

```java
byte b = 1;
int x = +b;
// byte y = +b;
```

## Binary numeric promotion

```text
double present → double
else float      → float
else long       → long
else            → int
```

```java
short a = 2;
short b = 3;
int sum = a + b;
// short no = a + b;
```

## Compound assignment

Compound assignment includes an implicit cast back to the left-hand type:

```java
byte b = 120;
b += 10;
System.out.println(b);   // -126
```

Conceptually:

```java
b = (byte) (b + 10);
```

The left-hand expression is evaluated once.

## Division, remainder and overflow

Integral division truncates toward zero:

```java
7 / 3    // 2
-7 / 3   // -2
```

The remainder has the sign of the dividend:

```java
-7 % 3   // -1
7 % -3   // 1
```

Integral overflow wraps:

```java
int x = Integer.MAX_VALUE + 1;  // Integer.MIN_VALUE
```

Use exact arithmetic when overflow must be detected:

```java
Math.addExact(Integer.MAX_VALUE, 1); // ArithmeticException
```

## Floating-point special values

```java
double inf = 1.0 / 0.0;
double nan = 0.0 / 0.0;

nan == nan;                 // false
Double.isNaN(nan);          // true
0.0 == -0.0;                // true
Double.compare(0.0, -0.0);  // 1
```

## Boolean operators and precedence

`&&` and `||` short-circuit. Boolean `&` and `|` evaluate both operands.

```java
int x = 0;
boolean a = false && ++x > 0; // x = 0
boolean b = false &  ++x > 0; // x = 1
```

High-value precedence:

```text
postfix
unary and cast
* / %
+ -
shifts
relational and instanceof
equality
bitwise
&&
||
?:
assignment
```

## Exam algorithm

1. Determine literal and operand types.
2. Apply unary or binary promotion.
3. Determine the expression type.
4. Apply assignment conversion.
5. Apply explicit casts.
6. Trace overflow or exceptions.

## Exam traps

- `byte + byte` produces `int`.
- a cast defines retained bits; it does not prevent overflow.
- `b += 1` can compile when `b = b + 1` does not.
- integer division by zero throws; floating-point division does not.
- `NaN` is unequal to itself.

## Route navigation

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Route roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]]
- **Previous concept:** [[10_CONCEPTS/Java/Core/Java Primitive Values and Literals]]
- **Next concept:** [[10_CONCEPTS/Java/Core/Java Wrappers Boxing and Math]]
- **Practice cards:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01A Values Cards]]
- **Compile/output drills:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills]]
- **Executable lab:** [[50_LABS/Java/JAVA-B01/README]]
- **Primary sources:** [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
