---
type: certification-batch
domain: java
subdomain: switch-statements-expressions
batch: JAVA-SWITCH-B02
status: published
card_count: 20
java_versions:
  - 17
  - 21
objectives:
  - JAVA21-2.1
tags:
  - java
  - switch
  - yield
  - exhaustiveness
  - active-recall
---

# JAVA-B02B — Classic Switch and Switch Expressions Cards

## Route navigation

- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]
- [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]]
- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills]]
- [[50_LABS/Java/JAVA-B02/README]]

---

## JAVA-SWITCH-B02-C001 — Which selector types are supported by the legacy form of `switch`?

### Russian Translation

Какие типы селектора поддерживает классическая форма `switch`?

> [!answer]- Answer
> `byte`, `short`, `char`, `int`, their wrappers, `String`, and enum types are the legacy selector types.

### Explanation

Integral selectors may undergo unboxing and certain narrowing compatibility checks for case constants.

### Exam Trap

`boolean`, `long`, `float`, and `double` are not legacy switch selector types.

---

## JAVA-SWITCH-B02-C002 — What must a traditional `case` constant be?

### Russian Translation

Чем должна быть традиционная константа `case`?

> [!answer]- Answer
> It must be a compile-time constant expression compatible with the selector type, or an applicable enum constant.

### Explanation

`final` is not enough by itself; the initializer must make the variable a constant variable.

### Exam Trap

A `final Integer` or a value returned by a method is not a case constant.

---

## JAVA-SWITCH-B02-C003 — Can two `case` labels have the same value after constant folding?

### Russian Translation

Могут ли две метки `case` иметь одинаковое значение после свёртки констант?

> [!answer]- Answer
> No. Duplicate case constants are a compile-time error even if written differently.

### Explanation

`case 1 + 1` and `case 2` denote the same constant value.

### Exam Trap

Different source text does not make duplicate constants distinct.

---

## JAVA-SWITCH-B02-C004 — What is fall-through in a colon-style switch statement?

### Russian Translation

Что такое fall-through в `switch` с двоеточиями?

> [!answer]- Answer
> After a matching label, statements continue through subsequent labeled groups until control exits or the switch ends.

### Explanation

A case label selects an entry point; it does not create an automatic boundary.

### Exam Trap

Forgetting `break` may be intentional or may change exact output.

---

## JAVA-SWITCH-B02-C005 — Do arrow-style switch rules fall through?

### Russian Translation

Есть ли fall-through у правил `switch` со стрелкой?

> [!answer]- Answer
> No. Exactly one matching rule is selected and its right-hand side completes independently.

### Explanation

Arrow rules avoid the accidental fall-through of colon-style statement groups.

### Exam Trap

You cannot place ordinary statements between arrow rules to share execution by fall-through.

---

## JAVA-SWITCH-B02-C006 — What does `break` do inside a switch statement?

### Russian Translation

Что делает `break` внутри оператора `switch`?

> [!answer]- Answer
> An unlabeled `break` exits the innermost switch statement.

### Explanation

This is commonly used to end a colon-style statement group.

### Exam Trap

A switch expression does not use `break value`; value production uses an expression or `yield`.

---

## JAVA-SWITCH-B02-C007 — What forms may appear to the right of an arrow in a switch rule?

### Russian Translation

Какие формы допустимы справа от стрелки в правиле `switch`?

> [!answer]- Answer
> A switch expression rule may use an expression, block, or `throw`; a switch statement rule uses a statement expression, block, or `throw`.

### Explanation

A block is required when multiple statements must run before producing a value.

### Exam Trap

A bare local-variable declaration is not a statement expression for an arrow rule; put it in a block.

---

## JAVA-SWITCH-B02-C008 — When is `yield` required?

### Russian Translation

Когда требуется `yield`?

> [!answer]- Answer
> A block used as a value-producing arm of a switch expression uses `yield` to provide that arm's result.

### Explanation

`yield` exits the enclosing switch expression, not an arbitrary loop or method.

### Exam Trap

`return` returns from the method and cannot replace `yield` when the method must continue with the switch result.

---

## JAVA-SWITCH-B02-C009 — Is `break value;` valid in the final switch-expression syntax?

### Russian Translation

Допустим ли `break value;` в финальном синтаксисе switch expression?

> [!answer]- Answer
> No. The final language uses `yield value;` inside block or colon-style arms.

### Explanation

Early preview designs used different syntax, but Java 17 and Java 21 use `yield`.

### Exam Trap

Exam questions may test obsolete preview syntax as a compile error.

---

## JAVA-SWITCH-B02-C010 — Must a switch expression be exhaustive?

### Russian Translation

Должен ли switch expression быть исчерпывающим?

> [!answer]- Answer
> Yes. Every possible selector value must be covered by case elements or a match-all label.

### Explanation

For an enum, listing every known constant can satisfy exhaustiveness without `default`.

### Exam Trap

A switch expression that handles only some values does not compile.

---

## JAVA-SWITCH-B02-C011 — Must a legacy switch statement be exhaustive?

### Russian Translation

Должен ли классический оператор switch быть исчерпывающим?

> [!answer]- Answer
> No. A legacy switch statement may have no matching label and then complete without executing a case body.

### Explanation

Java 21 preserves this behavior for backward compatibility.

### Exam Trap

Enhanced switch statements that use patterns, `case null`, or non-legacy selector types are different: they must be exhaustive.

---

## JAVA-SWITCH-B02-C012 — How can multiple constants share one arrow rule?

### Russian Translation

Как несколько констант могут использовать одно правило со стрелкой?

> [!answer]- Answer
> List them with commas in one label, such as `case 1, 2, 3 ->`.

### Explanation

The rule executes when any listed constant matches.

### Exam Trap

A colon-style group may stack labels, but comma-separated labels and fall-through are distinct mechanisms.

---

## JAVA-SWITCH-B02-C013 — Does the textual position of `default` control when it is chosen?

### Russian Translation

Определяет ли позиция `default` в тексте момент его выбора?

> [!answer]- Answer
> No. `default` is selected only when no applicable non-default label matches, regardless of where it appears.

### Explanation

Execution order after selection still follows colon fall-through rules when colon groups are used.

### Exam Trap

Placing `default` first does not make it match every value.

---

## JAVA-SWITCH-B02-C014 — What happens when a traditional reference selector is `null`?

### Russian Translation

Что происходит, если традиционный ссылочный селектор равен `null`?

> [!answer]- Answer
> Without an explicit Java 21 `case null`, switch evaluation throws `NullPointerException` before `default` can match.

### Explanation

This preserves the historical null-hostile behavior of switch.

### Exam Trap

`default` is not a null handler.

---

## JAVA-SWITCH-B02-C015 — Can an enum switch expression omit `default`?

### Russian Translation

Может ли switch expression по enum не иметь `default`?

> [!answer]- Answer
> Yes, if all enum constants known at compile time are covered.

### Explanation

Omitting a match-all label lets recompilation detect a newly added enum constant.

### Exam Trap

Adding `default` can hide an enum-evolution omission until runtime.

---

## JAVA-SWITCH-B02-C016 — What is the role of target typing in a switch expression?

### Russian Translation

Какую роль играет target typing для switch expression?

> [!answer]- Answer
> In an assignment, invocation, or return context, compatible arm expressions can be typed using the target type.

### Explanation

A switch expression can be a poly expression when it appears in an assignment or invocation context.

### Exam Trap

Do not determine the result type from only the first arm.

---

## JAVA-SWITCH-B02-C017 — Can a switch expression arm complete normally without producing a value?

### Russian Translation

Может ли ветвь switch expression завершиться нормально без выдачи значения?

> [!answer]- Answer
> No. A selected arm must produce a value with an expression or `yield`, or complete abruptly such as by throwing.

### Explanation

A value-producing block cannot simply reach its closing brace.

### Exam Trap

A missing `yield` in a normally completing block is a compile-time error.

---

## JAVA-SWITCH-B02-C018 — How does a colon-style switch expression produce values?

### Russian Translation

Как switch expression с двоеточиями выдаёт значения?

> [!answer]- Answer
> Its statement groups use `yield` on every reachable path that completes the selected computation.

### Explanation

Colon labels may still share statements through fall-through before a `yield` is reached.

### Exam Trap

Using `break` instead of `yield` leaves the expression without a result.

---

## JAVA-SWITCH-B02-C019 — Can a switch rule throw instead of yielding a value?

### Russian Translation

Может ли правило switch выбросить исключение вместо выдачи значения?

> [!answer]- Answer
> Yes. A `throw` rule completes abruptly and therefore satisfies the selected arm's control-flow requirement.

### Explanation

Other arms can still produce normal values.

### Exam Trap

The thrown expression must be a throwable object; `throw` is not a result value.

---

## JAVA-SWITCH-B02-C020 — What is the Java 17 rule for qualified enum constants in case labels?

### Russian Translation

Каково правило Java 17 для квалифицированных enum-констант в метках case?

> [!answer]- Answer
> When the selector type is that enum, Java 17 requires the unqualified enum constant name.

### Explanation

`case RED` is valid for `switch (Color c)`; `case Color.RED` is rejected in Java 17.

### Exam Trap

Java 21 relaxed this rule, so the exact Java version matters.

---
