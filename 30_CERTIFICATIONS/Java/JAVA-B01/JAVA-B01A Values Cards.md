---
type: certification-batch
domain: java
subdomain: values-and-expressions
batch: JAVA-VALUES-B01
status: published
card_count: 25
java_versions:
  - 17
  - 21
objectives:
  - JAVA21-1.1
tags:
  - java
  - primitives
  - wrappers
  - operators
  - active-recall
---

# JAVA-B01A — Values and Expressions Cards

## Route navigation

- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]]
- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills]]
- [[50_LABS/Java/JAVA-B01/README]]

---

## JAVA-VALUES-B01-C001 — What are the default types of integer and floating-point literals?

### Russian Translation

Какие типы по умолчанию имеют целочисленные литералы и литералы с плавающей точкой?

> [!answer]- Answer
> An ordinary integer literal is `int` when representable; an ordinary decimal floating literal is `double`. Use `L` for `long` and `F` for `float`.

### Explanation

The assignment target does not retroactively change an oversized decimal integer literal into `long`.

### Exam Trap

`long x = 3_000_000_000;` does not compile; the literal itself is invalid as an `int`. Add `L`.

---

## JAVA-VALUES-B01-C002 — Where may underscores appear in numeric literals?

### Russian Translation

Где разрешены символы подчёркивания в числовых литералах?

> [!answer]- Answer
> They may separate digits, but not start or end the literal, touch a decimal point, touch a suffix, or appear between a radix prefix and its first digit.

### Explanation

`1_000`, `0xCA_FE` and `3.14_15` are valid.

### Exam Trap

`1_.0`, `10_L`, `_100` and `0x_FF` do not compile.

---

## JAVA-VALUES-B01-C003 — How are binary, octal and hexadecimal integer literals written?

### Russian Translation

Как записываются двоичные, восьмеричные и шестнадцатеричные целые литералы?

> [!answer]- Answer
> Binary uses `0b`, octal uses a leading `0`, and hexadecimal uses `0x` or `0X`.

### Explanation

`0b1010`, `012` and `0xA` all represent decimal 10.

### Exam Trap

A leading zero is not decorative: `010` is octal 8, not decimal 10.

---

## JAVA-VALUES-B01-C004 — Is `char` a numeric primitive?

### Russian Translation

Является ли `char` числовым примитивом?

> [!answer]- Answer
> `char` is an unsigned 16-bit integral primitive and participates in numeric promotion.

### Explanation

`'A' + 1` has type `int` and value 66.

### Exam Trap

`char` represents one UTF-16 code unit, not necessarily one complete Unicode code point.

---

## JAVA-VALUES-B01-C005 — Can `boolean` be converted to or from an integer?

### Russian Translation

Можно ли преобразовать `boolean` в целое число или обратно?

> [!answer]- Answer
> No. Java has no numeric/boolean conversion, even with an explicit cast.

### Explanation

Only `true` and `false` are boolean values.

### Exam Trap

Patterns familiar from C, such as `if (1)`, do not compile in Java.

---

## JAVA-VALUES-B01-C006 — Which primitive conversions are widening?

### Russian Translation

Какие преобразования примитивов являются расширяющими?

> [!answer]- Answer
> The main chains are `byte→short→int→long→float→double` and `char→int→long→float→double`.

### Explanation

Widening is normally implicit, although widening to floating point can lose precision.

### Exam Trap

`byte` and `short` do not widen to `char`; `char` does not widen to `short`.

---

## JAVA-VALUES-B01-C007 — What does a narrowing integral cast do?

### Russian Translation

Что делает сужающее целочисленное преобразование?

> [!answer]- Answer
> It retains the low-order bits required by the target type; it does not clamp the value.

### Explanation

`(byte)130` produces `-126`.

### Exam Trap

The cast can change both magnitude and sign.

---

## JAVA-VALUES-B01-C008 — When can an `int` constant be assigned to `byte`, `short` or `char` without a cast?

### Russian Translation

Когда константу `int` можно присвоить `byte`, `short` или `char` без явного приведения?

> [!answer]- Answer
> When it is a compile-time constant expression and its value fits the target range.

### Explanation

`final int N = 100; byte b = N;` compiles because `N` is a constant variable.

### Exam Trap

A merely unchanged local variable is not automatically a compile-time constant.

---

## JAVA-VALUES-B01-C009 — What type results from unary promotion of `byte`, `short` or `char`?

### Russian Translation

Какой тип получается после унарного числового продвижения `byte`, `short` или `char`?

> [!answer]- Answer
> They are promoted to `int`.

### Explanation

Unary `+`, unary `-` and bitwise complement `~` therefore produce `int` for these operands.

### Exam Trap

`byte b = 1; byte x = -b;` does not compile without a cast.

---

## JAVA-VALUES-B01-C010 — What is the binary numeric promotion order?

### Russian Translation

Каков порядок бинарного числового продвижения?

> [!answer]- Answer
> `double`, then `float`, then `long`, otherwise both operands become `int`.

### Explanation

Two `short` operands still produce an `int` result for arithmetic.

### Exam Trap

The smaller assignment target does not stop promotion.

---

## JAVA-VALUES-B01-C011 — Why does compound assignment compile where ordinary assignment fails?

### Russian Translation

Почему составное присваивание компилируется там, где обычное присваивание не компилируется?

> [!answer]- Answer
> A compound assignment includes an implicit cast back to the left-hand type.

### Explanation

`b += 1` behaves approximately like `b = (byte)(b + 1)` when `b` is `byte`.

### Exam Trap

The left-hand expression is evaluated once, so the equivalence is not exact when side effects are present.

---

## JAVA-VALUES-B01-C012 — How does integral division round?

### Russian Translation

Как округляется результат целочисленного деления?

> [!answer]- Answer
> It truncates toward zero.

### Explanation

`7 / 3` is 2 and `-7 / 3` is -2.

### Exam Trap

It does not use mathematical floor for negative values.

---

## JAVA-VALUES-B01-C013 — What sign does the remainder operator produce?

### Russian Translation

Какой знак имеет результат оператора остатка `%`?

> [!answer]- Answer
> The remainder has the sign of the dividend.

### Explanation

`-7 % 3` is -1, while `7 % -3` is 1.

### Exam Trap

Do not apply a language-independent modulo rule without checking Java semantics.

---

## JAVA-VALUES-B01-C014 — What happens on integral overflow?

### Russian Translation

Что происходит при переполнении целого типа?

> [!answer]- Answer
> The result wraps according to the fixed-width two's-complement representation; ordinary arithmetic does not throw.

### Explanation

`Integer.MAX_VALUE + 1` becomes `Integer.MIN_VALUE`.

### Exam Trap

Use `Math.addExact`, `subtractExact` or `multiplyExact` when overflow must throw.

---

## JAVA-VALUES-B01-C015 — How does division by zero differ for integral and floating operands?

### Russian Translation

Чем различается деление на ноль для целых и вещественных операндов?

> [!answer]- Answer
> Integral division by zero throws `ArithmeticException`; floating-point division yields infinity or NaN.

### Explanation

`1.0 / 0.0` is positive infinity and `0.0 / 0.0` is NaN.

### Exam Trap

A literal expression such as `1 / 0` can fail during compile-time constant evaluation.

---

## JAVA-VALUES-B01-C016 — How should NaN be tested?

### Russian Translation

Как правильно проверять значение NaN?

> [!answer]- Answer
> Use `Double.isNaN` or `Float.isNaN`.

### Explanation

NaN compares unequal to every value, including itself.

### Exam Trap

`x == Double.NaN` is always false.

---

## JAVA-VALUES-B01-C017 — What is the difference between `&&` and `&` for booleans?

### Russian Translation

В чём разница между `&&` и `&` для логических значений?

> [!answer]- Answer
> `&&` short-circuits; `&` evaluates both operands.

### Explanation

The same distinction applies to `||` versus `|`.

### Exam Trap

A side effect or exception in the right operand may occur with `&` but not with a short-circuited `&&`.

---

## JAVA-VALUES-B01-C018 — What does boolean XOR (`^`) mean?

### Russian Translation

Что означает логическое исключающее ИЛИ (`^`)?

> [!answer]- Answer
> It is true exactly when the two boolean operands differ.

### Explanation

`true ^ false` is true; `true ^ true` is false.

### Exam Trap

`^` never short-circuits.

---

## JAVA-VALUES-B01-C019 — When does `==` compare wrapper values and when does it compare references?

### Russian Translation

Когда `==` сравнивает значения wrapper-типов, а когда ссылки?

> [!answer]- Answer
> With two wrapper references, `==` compares identity. If one operand is primitive, unboxing can make it a primitive comparison.

### Explanation

`Integer a = 1000, b = 1000; a == b` normally compares distinct references.

### Exam Trap

Do not infer value equality from the small-wrapper cache.

---

## JAVA-VALUES-B01-C020 — What wrapper cache range is required for `Integer.valueOf` and autoboxing?

### Russian Translation

Какой диапазон кеша обязателен для `Integer.valueOf` и autoboxing?

> [!answer]- Answer
> At least `-128` through `127`.

### Explanation

Values in that range normally reuse cached instances.

### Exam Trap

Use `equals`, not `==`, even when a demonstration happens to return true.

---

## JAVA-VALUES-B01-C021 — What happens when `null` is unboxed?

### Russian Translation

Что происходит при unboxing значения `null`?

> [!answer]- Answer
> The code compiles but throws `NullPointerException` at runtime.

### Explanation

Arithmetic, comparison with a primitive and method argument conversion can all trigger unboxing.

### Exam Trap

`Integer x = null; int y = x;` is not a compile error.

---

## JAVA-VALUES-B01-C022 — How do `parseInt` and `valueOf` differ?

### Russian Translation

Чем отличаются `parseInt` и `valueOf`?

> [!answer]- Answer
> `parseInt` returns primitive `int`; `valueOf` returns `Integer` and may use the cache.

### Explanation

Both throw `NumberFormatException` for invalid text.

### Exam Trap

Radix overloads interpret digits in the supplied base; the source string does not use Java literal prefixes automatically.

---

## JAVA-VALUES-B01-C023 — What are the return types of `Math.round(float)` and `Math.round(double)`?

### Russian Translation

Какие типы возвращают `Math.round(float)` и `Math.round(double)`?

> [!answer]- Answer
> The `float` overload returns `int`; the `double` overload returns `long`.

### Explanation

`floor` and `ceil` return `double`.

### Exam Trap

Do not assign `Math.round(double)` to `int` without considering narrowing.

---

## JAVA-VALUES-B01-C024 — Why can `Math.abs(Integer.MIN_VALUE)` remain negative?

### Russian Translation

Почему `Math.abs(Integer.MIN_VALUE)` может остаться отрицательным?

> [!answer]- Answer
> The positive magnitude is one greater than `Integer.MAX_VALUE`, so it is not representable as `int`.

### Explanation

The fixed-width result wraps back to `Integer.MIN_VALUE`.

### Exam Trap

The same asymmetry exists for signed integral minimum values.

---

## JAVA-VALUES-B01-C025 — What do the `Math.*Exact` methods add to ordinary arithmetic?

### Russian Translation

Что добавляют методы `Math.*Exact` по сравнению с обычной арифметикой?

> [!answer]- Answer
> They throw `ArithmeticException` when the mathematically correct integral result cannot fit the return type.

### Explanation

Examples include `addExact`, `subtractExact`, `multiplyExact`, `incrementExact`, `decrementExact`, `negateExact` and `toIntExact`.

### Exam Trap

They detect overflow; they do not change promotion rules or accept arbitrary-precision results.
