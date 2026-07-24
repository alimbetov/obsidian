---
type: certification-batch
domain: java
subdomain: conditions-loops-transfers
batch: JAVA-FLOW-B02
status: published
card_count: 20
java_versions:
  - 17
  - 21
objectives:
  - JAVA21-2.1
tags:
  - java
  - control-flow
  - loops
  - labels
  - instanceof-patterns
  - active-recall
---

# JAVA-B02A — Conditions, Loops and Transfers Cards

## Route navigation

- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]
- [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]]
- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills]]
- [[50_LABS/Java/JAVA-B02/README]]

---

## JAVA-FLOW-B02-C001 — What type must an `if` or loop condition have?

### Russian Translation

Какой тип должно иметь условие `if` или цикла?

> [!answer]- Answer
> It must evaluate to `boolean`. A `Boolean` reference is allowed only through unboxing.

### Explanation

`if (flag)` is valid when `flag` is `Boolean`, but evaluation unboxes the reference.

### Exam Trap

A `null` `Boolean` condition compiles and then throws `NullPointerException`; Java does not treat integers as booleans.

---

## JAVA-FLOW-B02-C002 — Which `if` owns an `else` when braces are omitted?

### Russian Translation

К какому `if` относится `else`, если фигурные скобки опущены?

> [!answer]- Answer
> An `else` is associated with the nearest preceding unmatched `if`.

### Explanation

Indentation has no grammatical effect. Braces are the reliable way to express the intended nesting.

### Exam Trap

Visually aligning `else` with an outer `if` does not change the dangling-`else` rule.

---

## JAVA-FLOW-B02-C003 — How many statements does an unbraced `if`, loop, or `else` control?

### Russian Translation

Сколько операторов контролирует `if`, цикл или `else` без фигурных скобок?

> [!answer]- Answer
> Exactly one statement, which may itself be a block statement.

### Explanation

A declaration plus a following method call are two statements unless enclosed in `{ ... }`.

### Exam Trap

Misleading indentation can make a second statement look conditional even though it always executes.

---

## JAVA-FLOW-B02-C004 — What does definite assignment require for a local variable?

### Russian Translation

Что требует definite assignment для локальной переменной?

> [!answer]- Answer
> A local variable must be definitely assigned on every reachable path before it is read.

### Explanation

The compiler performs flow analysis rather than assuming a branch will happen at runtime.

### Exam Trap

Assigning a variable in only one branch is insufficient unless the other branch cannot reach the read.

---

## JAVA-FLOW-B02-C005 — When is the body of a `while` loop tested?

### Russian Translation

Когда проверяется условие тела цикла `while`?

> [!answer]- Answer
> The condition is evaluated before every iteration, so the body may execute zero times.

### Explanation

After the body completes normally or through `continue`, control returns to the condition.

### Exam Trap

Do not infer one execution from the presence of the loop body; `while` is pre-test.

---

## JAVA-FLOW-B02-C006 — What is the execution guarantee of a `do-while` loop?

### Russian Translation

Какую гарантию выполнения даёт цикл `do-while`?

> [!answer]- Answer
> Its body executes at least once because the condition is evaluated after the body.

### Explanation

A `continue` in a `do-while` transfers control to the condition expression.

### Exam Trap

The semicolon after `while (condition);` is part of the `do-while` syntax.

---

## JAVA-FLOW-B02-C007 — Which parts of a basic `for` statement may be omitted?

### Russian Translation

Какие части обычного оператора `for` можно опустить?

> [!answer]- Answer
> The initializer, condition, and update expressions may all be omitted.

### Explanation

An omitted condition is treated as `true`, so `for (;;) { ... }` is an infinite loop unless control exits.

### Exam Trap

The two semicolons remain mandatory even when all three parts are empty.

---

## JAVA-FLOW-B02-C008 — What is the scope of a variable declared in a `for` initializer?

### Russian Translation

Какова область видимости переменной, объявленной в инициализаторе `for`?

> [!answer]- Answer
> It extends through the condition, update expressions, and loop body, but not beyond the `for` statement.

### Explanation

Each iteration reuses the same loop variable unless another variable is created inside the body.

### Exam Trap

Referencing the initializer variable after the loop is a compile-time error.

---

## JAVA-FLOW-B02-C009 — What does `continue` do in a basic `for` loop?

### Russian Translation

Что делает `continue` в обычном цикле `for`?

> [!answer]- Answer
> It skips the rest of the body, then executes the update expressions, and then re-evaluates the condition.

### Explanation

This differs from `while`, where `continue` transfers directly to the condition.

### Exam Trap

Assuming the update is skipped can produce an incorrect output trace or hide an infinite loop.

---

## JAVA-FLOW-B02-C010 — What does assigning to an enhanced-for loop variable change?

### Russian Translation

Что изменяет присваивание переменной расширенного цикла `for`?

> [!answer]- Answer
> It changes only the loop variable, not the corresponding array element or collection slot.

### Explanation

For reference elements, mutating the referenced object can still be visible; replacing the loop variable is not.

### Exam Trap

`for (int x : values) x++;` does not increment the elements of `values`.

---

## JAVA-FLOW-B02-C011 — What inputs support the enhanced `for` statement?

### Russian Translation

Какие источники поддерживает расширенный оператор `for`?

> [!answer]- Answer
> Arrays and expressions whose type is `Iterable` can be used.

### Explanation

The compiler obtains array elements by index or obtains an iterator from an `Iterable`.

### Exam Trap

An `Iterator` by itself is not an `Iterable` and cannot be used directly after the colon.

---

## JAVA-FLOW-B02-C012 — What does an unlabeled `break` exit?

### Russian Translation

Из чего выходит `break` без метки?

> [!answer]- Answer
> It exits the innermost enclosing loop or `switch` statement.

### Explanation

Execution continues with the first statement after that construct.

### Exam Trap

A `break` does not automatically exit an enclosing method or every nested loop.

---

## JAVA-FLOW-B02-C013 — What may a labeled `break` target?

### Russian Translation

На какую конструкцию может ссылаться `break` с меткой?

> [!answer]- Answer
> It may target any enclosing labeled statement.

### Explanation

This allows one jump out of nested loops or out of a labeled block without using flags.

### Exam Trap

The target label must lexically enclose the `break`; labels are not general `goto` destinations.

---

## JAVA-FLOW-B02-C014 — What may a labeled `continue` target?

### Russian Translation

На какую конструкцию может ссылаться `continue` с меткой?

> [!answer]- Answer
> It must target an enclosing labeled loop statement.

### Explanation

Control transfers to that loop's continuation point: its update/condition as defined by the loop kind.

### Exam Trap

A label on a plain block can be targeted by `break` but not by `continue`.

---

## JAVA-FLOW-B02-C015 — Can two local variables with overlapping scopes have the same name?

### Russian Translation

Могут ли две локальные переменные с пересекающимися областями видимости иметь одинаковое имя?

> [!answer]- Answer
> No. A local variable or parameter cannot be redeclared in an overlapping local scope.

### Explanation

A nested block does not permit redeclaring an active local variable from the enclosing method body.

### Exam Trap

Field shadowing by a local variable is allowed, but local-on-local redeclaration is not.

---

## JAVA-FLOW-B02-C016 — How do `&&` and `||` affect evaluation?

### Russian Translation

Как `&&` и `||` влияют на вычисление?

> [!answer]- Answer
> `&&` skips its right operand when the left operand is false; `||` skips it when the left operand is true.

### Explanation

This short-circuiting can prevent side effects or exceptions in the right operand.

### Exam Trap

Exact-output questions often depend on whether an increment or method call is skipped.

---

## JAVA-FLOW-B02-C017 — What does pattern matching for `instanceof` do in Java 17?

### Russian Translation

Что делает pattern matching для `instanceof` в Java 17?

> [!answer]- Answer
> It combines a type test with a conditional cast and pattern-variable binding.

### Explanation

`if (value instanceof String text)` makes `text` available where the compiler knows the match succeeded.

### Exam Trap

The pattern variable is not a normal declaration that is available on paths where the test may be false.

---

## JAVA-FLOW-B02-C018 — Why is `while (false) { ... }` rejected while `if (false) { ... }` is allowed?

### Russian Translation

Почему `while (false) { ... }` запрещён, а `if (false) { ... }` разрешён?

> [!answer]- Answer
> The loop body is unreachable under Java's reachability rules, while `if` has a special allowance for conditional compilation patterns.

### Explanation

The compiler reports the statement inside a constant-false `while` as unreachable.

### Exam Trap

Do not generalize the `if (false)` exception to loops.

---

## JAVA-FLOW-B02-C019 — When can a statement after `while (true)` be reachable?

### Russian Translation

Когда оператор после `while (true)` может быть достижимым?

> [!answer]- Answer
> It is reachable only if the loop contains a reachable `break` that can exit that loop.

### Explanation

Reachability is a compile-time structural analysis, not proof that a runtime condition will become true.

### Exam Trap

A `return` or `throw` inside the loop does not make the following statement reachable.

---

## JAVA-FLOW-B02-C020 — Where can an `instanceof` pattern variable be in scope?

### Russian Translation

Где может находиться в области видимости pattern variable из `instanceof`?

> [!answer]- Answer
> It is in scope only where flow analysis proves the pattern matched, such as the right operand of `&&` and the true branch.

### Explanation

Negated tests can extend scope after an early exit: `if (!(x instanceof String s)) return;` makes `s` available afterward.

### Exam Trap

It is not in scope in the right operand of `||`, because that operand may run when the pattern did not match.

---
