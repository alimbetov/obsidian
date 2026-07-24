---
type: certification-batch
domain: java
subdomain: strings-builders-text-blocks
batch: JAVA-B01B
status: published
card_count: 25
java_versions:
  - 17
  - 21
objectives:
  - JAVA21-1.2
tags:
  - java
  - string
  - stringbuilder
  - text-blocks
  - active-recall
---

# JAVA-B01B — Strings, Builders and Text Blocks Cards

## Route navigation

- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]]
- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills]]
- [[50_LABS/Java/JAVA-B01/README]]

---

## JAVA-B01B-C001 — What does String immutability mean operationally?

### Russian Translation

Что практически означает неизменяемость `String`?

> [!answer]- Answer
> A String method cannot change the existing object; it returns the same or another String value.

### Explanation

Ignoring the return value of `toUpperCase`, `replace`, `substring` or `strip` leaves the variable unchanged.

### Exam Trap

A variable may be reassigned, but the original String object is still immutable.

---

## JAVA-B01B-C002 — Which Strings are placed in the string pool?

### Russian Translation

Какие строки помещаются в string pool?

> [!answer]- Answer
> String literals and interned strings use the canonical pool; compile-time constant concatenations are folded to pooled literals.

### Explanation

`"ja" + "va"` is a compile-time constant expression.

### Exam Trap

A concatenation involving a nonconstant variable normally occurs at runtime and is not identical to the pooled literal.

---

## JAVA-B01B-C003 — What does `new String("java")` guarantee?

### Russian Translation

Что гарантирует выражение `new String("java")`?

> [!answer]- Answer
> It creates a distinct String object even though the literal `"java"` is also present in the pool.

### Explanation

The new object is equal by content but not identical to the literal.

### Exam Trap

Calling `intern()` on the new object returns the pooled canonical reference.

---

## JAVA-B01B-C004 — How is mixed numeric and String concatenation evaluated?

### Russian Translation

Как вычисляется выражение, смешивающее числа и конкатенацию строк?

> [!answer]- Answer
> Evaluation is left to right; once a String operand participates, subsequent `+` operations concatenate.

### Explanation

`1 + 2 + "3"` produces `"33"`; `"1" + 2 + 3` produces `"123"`.

### Exam Trap

Parentheses can force arithmetic before concatenation.

---

## JAVA-B01B-C005 — When is String concatenation a compile-time constant?

### Russian Translation

Когда конкатенация строк является compile-time constant?

> [!answer]- Answer
> When every operand is a compile-time constant expression of primitive or String type.

### Explanation

A `final String` initialized with a constant expression can participate.

### Exam Trap

`final` alone is insufficient if the initializer is computed at runtime.

---

## JAVA-B01B-C006 — What index is valid for `charAt`?

### Russian Translation

Какие индексы допустимы для `charAt`?

> [!answer]- Answer
> From zero through `length() - 1`.

### Explanation

`charAt(length())` throws `StringIndexOutOfBoundsException`.

### Exam Trap

The end index convention used by `substring` does not apply to `charAt`.

---

## JAVA-B01B-C007 — What are the bounds for `substring(begin, end)`?

### Russian Translation

Каковы допустимые границы `substring(begin, end)`?

> [!answer]- Answer
> `0 <= begin <= end <= length`, and the end index is exclusive.

### Explanation

Equal begin and end produce the empty string.

### Exam Trap

Reversing the indexes or exceeding length throws `StringIndexOutOfBoundsException`.

---

## JAVA-B01B-C008 — What does `indexOf` return when no match exists?

### Russian Translation

Что возвращает `indexOf`, если совпадение не найдено?

> [!answer]- Answer
> `-1`.

### Explanation

Overloads search characters, substrings and optionally start from a specified index.

### Exam Trap

A return value of zero means a match at the first character, not “not found.”

---

## JAVA-B01B-C009 — How do `equals`, `equalsIgnoreCase` and `==` differ for Strings?

### Russian Translation

Чем отличаются `equals`, `equalsIgnoreCase` и `==` для строк?

> [!answer]- Answer
> `equals` compares exact content, `equalsIgnoreCase` compares content ignoring case, and `==` compares reference identity.

### Explanation

Pooling can make an identity comparison accidentally appear to be a content comparison.

### Exam Trap

Always trace how each reference was produced before predicting `==`.

---

## JAVA-B01B-C010 — What does `compareTo` guarantee?

### Russian Translation

Что гарантирует метод `compareTo`?

> [!answer]- Answer
> A negative, zero or positive result according to lexicographic order.

### Explanation

The magnitude is not part of the contract.

### Exam Trap

Do not expect only `-1`, `0` or `1`.

---

## JAVA-B01B-C011 — How do `isEmpty` and `isBlank` differ?

### Russian Translation

Чем отличаются `isEmpty` и `isBlank`?

> [!answer]- Answer
> `isEmpty` checks length zero; `isBlank` accepts an empty string or one containing only Unicode whitespace.

### Explanation

`"   ".isEmpty()` is false, while `"   ".isBlank()` is true.

### Exam Trap

Whitespace content still contributes to length.

---

## JAVA-B01B-C012 — How do `trim` and `strip` differ?

### Russian Translation

Чем отличаются `trim` и `strip`?

> [!answer]- Answer
> `trim` removes leading/trailing characters at or below U+0020; `strip` uses Unicode whitespace semantics.

### Explanation

`stripLeading` and `stripTrailing` remove one side only.

### Exam Trap

For pure ASCII spaces they often look identical, hiding the broader `strip` contract.

---

## JAVA-B01B-C013 — Does `String.replace` use regular expressions?

### Russian Translation

Использует ли `String.replace` регулярные выражения?

> [!answer]- Answer
> No. `replace` performs literal character or character-sequence replacement.

### Explanation

`replaceAll` and `replaceFirst` interpret the first argument as a regex.

### Exam Trap

`"a.b".replaceAll(".", "-")` replaces every character because regex `.` matches any character.

---

## JAVA-B01B-C014 — Why can `split` return fewer elements than visible separators suggest?

### Russian Translation

Почему `split` может вернуть меньше элементов, чем подсказывает количество разделителей?

> [!answer]- Answer
> The one-argument overload discards trailing empty strings.

### Explanation

Use a negative limit to preserve all trailing empty elements.

### Exam Trap

The delimiter is a regex, so metacharacters such as `.` or `|` require escaping.

---

## JAVA-B01B-C015 — What is returned by `String.formatted`?

### Russian Translation

Что возвращает `String.formatted`?

> [!answer]- Answer
> A new String formatted using the receiver as the format string.

### Explanation

`"%s:%02d".formatted("A", 7)` yields `A:07`.

### Exam Trap

Locale-sensitive output should not rely on the process default in deterministic questions.

---

## JAVA-B01B-C016 — What is the first content character of a legal text block?

### Russian Translation

Где начинается содержимое корректного text block?

> [!answer]- Answer
> After the mandatory line terminator following the opening `"""` delimiter.

### Explanation

Only optional whitespace may appear between the opening delimiter and that line terminator.

### Exam Trap

`String s = """text""";` is not valid text-block syntax.

---

## JAVA-B01B-C017 — How is incidental indentation removed from a text block?

### Russian Translation

Как удаляется incidental indentation в text block?

> [!answer]- Answer
> The compiler derives a common incidental indentation from nonblank lines and the closing delimiter position, then removes it.

### Explanation

Relative indentation beyond the common amount remains.

### Exam Trap

Moving only the closing delimiter can change the resulting String.

---

## JAVA-B01B-C018 — Does a text block normally end with a newline?

### Russian Translation

Заканчивается ли text block переводом строки?

> [!answer]- Answer
> Usually yes when the closing delimiter is on a following line after the final content line.

### Explanation

Delimiter placement and line continuation determine whether the final newline is present.

### Exam Trap

Count the final `\n` explicitly in exact-output questions.

---

## JAVA-B01B-C019 — What does `\s` mean in a String or text block?

### Russian Translation

Что означает escape-последовательность `\s` в строке или text block?

> [!answer]- Answer
> It represents one space character.

### Explanation

It is useful for preserving visible trailing whitespace in a text block.

### Exam Trap

It is a Java escape, not the regex whitespace token in this context.

---

## JAVA-B01B-C020 — What does a backslash at the physical end of a text-block line do?

### Russian Translation

Что делает обратный слеш в физическом конце строки text block?

> [!answer]- Answer
> It suppresses the line terminator that would otherwise appear in the resulting String.

### Explanation

Two source lines can therefore become adjacent in the runtime value.

### Exam Trap

This is distinct from writing the two-character sequence `\` plus `n`.

---

## JAVA-B01B-C021 — Are text blocks templates or structured JSON/XML values?

### Russian Translation

Являются ли text blocks шаблонами либо объектами JSON/XML?

> [!answer]- Answer
> No. A text block is an ordinary String literal with multiline lexical processing.

### Explanation

Interpolation, parsing and validation require separate APIs.

### Exam Trap

Do not attribute string-template behavior to final Java 17/21 text blocks.

---

## JAVA-B01B-C022 — Which StringBuilder methods mutate the same object?

### Russian Translation

Какие методы `StringBuilder` изменяют тот же объект?

> [!answer]- Answer
> Methods such as `append`, `insert`, `delete`, `replace`, `reverse`, `setCharAt` and `setLength` mutate the builder.

### Explanation

Most return the same builder for chaining.

### Exam Trap

`substring` returns a String; it does not produce a new builder.

---

## JAVA-B01B-C023 — What happens when two variables alias one StringBuilder?

### Russian Translation

Что происходит, когда две переменные ссылаются на один `StringBuilder`?

> [!answer]- Answer
> Mutation through either reference is visible through the other because both identify the same object.

### Explanation

Assignment copies the reference, not the builder content.

### Exam Trap

Do not reason about mutable builders as if they were immutable Strings.

---

## JAVA-B01B-C024 — What happens when `StringBuilder.setLength` increases the length?

### Russian Translation

Что происходит, когда `StringBuilder.setLength` увеличивает длину?

> [!answer]- Answer
> The builder grows and new positions contain the null character `\u0000`.

### Explanation

Reducing length truncates content.

### Exam Trap

The new positions are not ordinary space characters.

---

## JAVA-B01B-C025 — Does StringBuilder provide content-based `equals`?

### Russian Translation

Реализует ли `StringBuilder` сравнение содержимого через `equals`?

> [!answer]- Answer
> No. Its inherited `equals` is identity-based.

### Explanation

Convert to String or compare character sequences explicitly for content equality.

### Exam Trap

Two builders displaying identical text can still return false from `equals`.
