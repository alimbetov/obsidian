---
type: certification-batch
domain: java
subdomain: pattern-switch-java-21
batch: JAVA-PATTERN-B02
status: published
card_count: 20
java_versions:
  - 17
  - 21
objectives:
  - JAVA21-2.1
tags:
  - java
  - java-21
  - pattern-switch
  - dominance
  - exhaustiveness
  - active-recall
---

# JAVA-B02C — Java 21 Pattern Switch Cards

## Route navigation

- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]
- [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]]
- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills]]
- [[50_LABS/Java/JAVA-B02/README]]

---

## JAVA-PATTERN-B02-C001 — What is the status of pattern matching for `switch` in Java 21 and Java 17?

### Russian Translation

Каков статус pattern matching for `switch` в Java 21 и Java 17?

> [!answer]- Answer
> It is final in Java 21. Java 17 contains an earlier preview form and it is not part of the normal Java 17 exam baseline.

### Explanation

Java 21 code should use the final JEP 441 syntax and semantics.

### Exam Trap

Do not compile Java 21 pattern-switch examples in the ordinary Java 17 lane.

---

## JAVA-PATTERN-B02-C002 — What selector types can Java 21 pattern switch use?

### Russian Translation

Какие типы селектора поддерживает pattern switch в Java 21?

> [!answer]- Answer
> It can switch over any reference type, in addition to the supported integral legacy primitives; primitive `boolean`, `long`, `float`, and `double` remain unsupported.

### Explanation

A `Long` reference can be switched with a `case Long value` pattern even though primitive `long` cannot be the selector.

### Exam Trap

Do not confuse a wrapper reference type with its unsupported primitive selector type.

---

## JAVA-PATTERN-B02-C003 — What does a type pattern in a case label do?

### Russian Translation

Что делает type pattern в метке case?

> [!answer]- Answer
> It tests whether the selector value has a compatible runtime type and, on success, binds a pattern variable.

### Explanation

`case String s -> s.length()` combines the type test, cast, and local binding.

### Exam Trap

A type pattern does not match `null`.

---

## JAVA-PATTERN-B02-C004 — How is a matching label selected when several patterns could apply?

### Russian Translation

Как выбирается метка, если подходят несколько patterns?

> [!answer]- Answer
> The first applicable case label in source order is selected.

### Explanation

The compiler therefore rejects labels that can never be selected because an earlier label dominates them.

### Exam Trap

Pattern switch does not search for a 'most specific' label after ignoring source order.

---

## JAVA-PATTERN-B02-C005 — How should subtype and supertype patterns be ordered?

### Russian Translation

В каком порядке нужно располагать patterns подтипа и супертипа?

> [!answer]- Answer
> Place the more specific subtype pattern before the broader supertype pattern.

### Explanation

`case String s` may precede `case CharSequence cs`; the reverse order makes the String case dominated.

### Exam Trap

An early `case Object o` usually blocks all later non-null type patterns.

---

## JAVA-PATTERN-B02-C006 — What is a guarded pattern case in Java 21?

### Russian Translation

Что такое guarded pattern case в Java 21?

> [!answer]- Answer
> It is a pattern label followed by `when` and a boolean guard, such as `case String s when s.isBlank() ->`.

### Explanation

The guard is evaluated only after the selector matches the pattern.

### Exam Trap

Final Java 21 syntax uses `when`, not the older preview `&&` guarded-pattern syntax.

---

## JAVA-PATTERN-B02-C007 — Which case labels may have a `when` guard?

### Russian Translation

Какие метки case могут иметь guard `when`?

> [!answer]- Answer
> Only pattern case labels may have guards.

### Explanation

A constant label such as `case "YES" when condition` is illegal.

### Exam Trap

Move constant-specific logic to another case or use a type pattern with a guard.

---

## JAVA-PATTERN-B02-C008 — Where is a pattern variable in scope for a guarded arrow rule?

### Russian Translation

Где доступна pattern variable в guarded arrow rule?

> [!answer]- Answer
> It is in scope in the guard and in the expression, block, or throw statement to the right of the arrow.

### Explanation

The variable exists only on the path where the pattern matched.

### Exam Trap

It is not available in later unrelated case arms or after the switch.

---

## JAVA-PATTERN-B02-C009 — How do guards affect dominance between pattern labels?

### Russian Translation

Как guards влияют на dominance между pattern labels?

> [!answer]- Answer
> A guarded pattern dominates another pattern only when its underlying pattern dominates and its guard is the constant expression `true`; arbitrary guards are not logically analyzed.

### Explanation

This permits ordered refinements such as guarded String cases followed by an unguarded String fallback.

### Exam Trap

The compiler does not prove that `s.length() > 0` covers another arbitrary guard.

---

## JAVA-PATTERN-B02-C010 — Can a pattern case dominate a constant case?

### Russian Translation

Может ли pattern case доминировать над constant case?

> [!answer]- Answer
> Yes. A compatible type pattern placed first can make a later constant label unreachable.

### Explanation

`case String s ->` before `case "hello" ->` is dominated and does not compile.

### Exam Trap

Place specific constants before a broad matching type pattern.

---

## JAVA-PATTERN-B02-C011 — How can Java 21 switch handle a `null` selector?

### Russian Translation

Как Java 21 switch может обработать `null` selector?

> [!answer]- Answer
> Add an explicit `case null` label.

### Explanation

If the selector is null and no null label exists, the switch throws `NullPointerException`.

### Exam Trap

Type patterns and `default` do not implicitly match null.

---

## JAVA-PATTERN-B02-C012 — Does `default` match `null` in a pattern switch?

### Russian Translation

Совпадает ли `default` с `null` в pattern switch?

> [!answer]- Answer
> No. `default` is considered only for a non-null value that matched no other label.

### Explanation

Historical null hostility is preserved unless `case null` is written.

### Exam Trap

A switch with `default` but no `case null` still throws `NullPointerException` for null.

---

## JAVA-PATTERN-B02-C013 — What does `case null, default` mean?

### Russian Translation

Что означает `case null, default`?

> [!answer]- Answer
> It is one combined match-all label for null and for every remaining non-null value.

### Explanation

It is useful when both categories should share one action.

### Exam Trap

A switch block cannot also contain a separate `default` label.

---

## JAVA-PATTERN-B02-C014 — Which Java 21 switch statements must be exhaustive?

### Russian Translation

Какие операторы switch в Java 21 должны быть исчерпывающими?

> [!answer]- Answer
> Enhanced switch statements must be exhaustive: those using patterns, `case null`, or a non-legacy selector type.

### Explanation

Legacy switch statements remain allowed to omit cases for backward compatibility.

### Exam Trap

A pattern switch statement over `Object` cannot silently omit all other object types.

---

## JAVA-PATTERN-B02-C015 — How can sealed hierarchies make a pattern switch exhaustive?

### Russian Translation

Как sealed hierarchy делает pattern switch исчерпывающим?

> [!answer]- Answer
> If all permitted direct alternatives are covered by applicable case patterns, the compiler can prove exhaustiveness without `default`.

### Explanation

This gives compile-time feedback when the sealed hierarchy changes and the switch is recompiled.

### Exam Trap

Coverage is based on the sealed type structure, not on a runtime registry of subclasses.

---

## JAVA-PATTERN-B02-C016 — How many match-all labels may a switch block contain?

### Russian Translation

Сколько match-all labels может содержать switch block?

> [!answer]- Answer
> At most one.

### Explanation

`default`, `case null, default`, and an unconditional pattern for the selector type can each act as match-all labels.

### Exam Trap

Combining `case Object o` with `default` for an `Object` selector is a compile-time error.

---

## JAVA-PATTERN-B02-C017 — What is the scope of a pattern variable in an arrow arm?

### Russian Translation

Какова область видимости pattern variable в arrow arm?

> [!answer]- Answer
> It includes only the selected rule's right-hand expression, block, or throw statement, plus any guard.

### Explanation

Each arm can declare its own pattern variable, even with the same spelling.

### Exam Trap

The variable is not definitely matched outside that arm.

---

## JAVA-PATTERN-B02-C018 — Can a colon-style pattern case fall through into a group that uses its pattern variable?

### Russian Translation

Может ли colon-style pattern case провалиться в группу, использующую pattern variable?

> [!answer]- Answer
> No. Falling through a label that declares a pattern variable is forbidden.

### Explanation

The compiler prevents execution from entering a scope where the variable was never initialized by a match.

### Exam Trap

Traditional constant-label fall-through rules cannot be copied blindly to pattern labels.

---

## JAVA-PATTERN-B02-C019 — What changed for qualified enum constants in Java 21?

### Russian Translation

Что изменилось для квалифицированных enum-констант в Java 21?

> [!answer]- Answer
> Java 21 allows qualified enum constants in case labels when they are compatible with the selector.

### Explanation

This is useful when switching over a broader type that can contain constants from different enum classes.

### Exam Trap

Code using `case Color.RED` may compile on Java 21 but fail under the Java 17 baseline.

---

## JAVA-PATTERN-B02-C020 — Why is omitting a match-all label often preferable for a provably exhaustive enum or sealed switch?

### Russian Translation

Почему для доказуемо исчерпывающего enum или sealed switch часто лучше не писать match-all label?

> [!answer]- Answer
> The compiler can then report newly added alternatives at recompilation instead of routing them silently to `default`.

### Explanation

The compiler still provides a runtime failure path for incompatible separate evolution.

### Exam Trap

A broad default can reduce future compile-time safety.

---
