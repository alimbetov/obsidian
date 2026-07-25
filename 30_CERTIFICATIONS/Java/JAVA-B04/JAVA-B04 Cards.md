---
type: certification-batch
domain: java
route: JAVA-B04
batch: JAVA-EXCEPT-B04
status: published
card_count: 36
java_versions: [17, 21]
objectives:
  - JAVA-B04
  - JAVA21-4.1
tags: [java, exceptions, active-recall, beginner]
---

# JAVA-B04 — Exceptions and Resource Safety Cards

## Navigation

- [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Roadmap]]
- [[10_CONCEPTS/Java/Exceptions/Java Exceptions and Resource Safety]]
- [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Drills]]
- [[50_LABS/Java/JAVA-B04/README]]

## Level 1 — понять основную модель

## JAVA-EXCEPT-B04-C001 — What is an exception?

### Russian Translation

Что такое exception?

> [!answer]- Answer
> An object indicating that normal execution cannot continue as planned.

### Explanation

Throwing starts exceptional control flow and handler search.

### Exam Trap

Exception не является compile error или обычным return value.

---

## JAVA-EXCEPT-B04-C002 — What happens after an uncaught throw in the same method?

### Russian Translation

Что происходит после необработанного throw в том же методе?

> [!answer]- Answer
> Later statements on that path are not executed; the method completes abruptly.

### Explanation

Runtime begins unwinding the method frame.

### Exam Trap

После обработки выше по stack выполнение не возвращается к строке после throw.

---

## JAVA-EXCEPT-B04-C003 — What is stack unwinding?

### Russian Translation

Что такое stack unwinding?

> [!answer]- Answer
> Ending method frames while searching upward for a compatible handler.

### Explanation

Cleanup can run while each frame is left.

### Exam Trap

Это не возврат к месту throw.

---

## JAVA-EXCEPT-B04-C004 — How should a stack trace be read first?

### Russian Translation

С чего начинать чтение stack trace?

> [!answer]- Answer
> Read the exception type/message, then the first relevant application frame and any `Caused by` chain.

### Explanation

The trace reconstructs where failure appeared and how calls reached it.

### Exam Trap

Первый frame не всегда является root cause.

---

## JAVA-EXCEPT-B04-C005 — Are compile errors exceptions?

### Russian Translation

Являются ли ошибки компиляции exceptions?

> [!answer]- Answer
> No.

### Explanation

Compile errors prevent creation of executable bytecode; exceptions happen during execution.

### Exam Trap

`try/catch` не может обработать исходный код, который не компилируется.

---

## JAVA-EXCEPT-B04-C006 — Can one invocation return normally and throw on the same path?

### Russian Translation

Может ли один вызов одновременно нормально вернуть значение и выбросить exception?

> [!answer]- Answer
> No; one path completes normally or abruptly.

### Explanation

Return and throw are different completion modes.

### Exam Trap

`finally` может заменить ранний outcome, поэтому return в finally опасен.

---

## Level 2 — hierarchy and compiler contract

## JAVA-EXCEPT-B04-C007 — What makes an exception checked?

### Russian Translation

Что делает exception checked?

> [!answer]- Answer
> It is an `Exception` subtype that is not a `RuntimeException` subtype.

### Explanation

Compiler requires catch or declaration.

### Exam Trap

Checked не означает «более серьёзный».

---

## JAVA-EXCEPT-B04-C008 — Must RuntimeException be declared?

### Russian Translation

Нужно ли объявлять RuntimeException через throws?

> [!answer]- Answer
> No.

### Explanation

Unchecked exceptions are exempt from catch-or-declare.

### Exam Trap

Их всё равно можно документировать и ловить.

---

## JAVA-EXCEPT-B04-C009 — Does catch(Exception) catch Error?

### Russian Translation

Ловит ли `catch (Exception)` тип Error?

> [!answer]- Answer
> No.

### Explanation

`Error` is another branch under `Throwable`.

### Exam Trap

`catch (Throwable)` ловит оба, но редко является правильной policy.

---

## JAVA-EXCEPT-B04-C010 — What is the main checked/unchecked difference?

### Russian Translation

Каково главное различие checked и unchecked?

> [!answer]- Answer
> The compile-time catch-or-declare contract.

### Explanation

Hierarchy determines the rule.

### Exam Trap

Не классифицируй по частоте или тяжести.

---

## JAVA-EXCEPT-B04-C011 — Should Error represent business failure?

### Russian Translation

Следует ли использовать Error для бизнес-ошибки?

> [!answer]- Answer
> Normally no.

### Explanation

`Error` represents serious runtime/JVM/linkage conditions.

### Exam Trap

Business exceptions обычно наследуются от Exception или RuntimeException.

---

## JAVA-EXCEPT-B04-C012 — Can unchecked exceptions preserve a cause?

### Russian Translation

Может ли unchecked exception сохранять cause?

> [!answer]- Answer
> Yes.

### Explanation

RuntimeException supports cause chaining.

### Exam Trap

Не теряй исходную причину при translation.

---

## Level 3 — try, catch and finally

## JAVA-EXCEPT-B04-C013 — How is a catch selected?

### Russian Translation

Как выбирается catch?

> [!answer]- Answer
> The first source-order handler compatible with the runtime exception type.

### Explanation

Specific handlers must precede general handlers.

### Exam Trap

Выбор не основан на имени переменной.

---

## JAVA-EXCEPT-B04-C014 — Why must FileNotFoundException precede IOException?

### Russian Translation

Почему FileNotFoundException должен быть раньше IOException?

> [!answer]- Answer
> Because it is an IOException subtype.

### Explanation

The broad handler would make the narrow one unreachable.

### Exam Trap

Compiler проверяет этот порядок.

---

## JAVA-EXCEPT-B04-C015 — Does finally run after success?

### Russian Translation

Выполняется ли finally после успешного try?

> [!answer]- Answer
> Yes, under normal JVM execution.

### Explanation

Finally runs before control leaves the statement.

### Exam Trap

Не говори «абсолютно всегда»: process termination/crash are boundaries.

---

## JAVA-EXCEPT-B04-C016 — What if no catch matches but finally exists?

### Russian Translation

Что если catch не подошёл, но есть finally?

> [!answer]- Answer
> Finally runs, then the exception continues to the caller.

### Explanation

Cleanup does not mean handling.

### Exam Trap

Finally не является скрытым catch.

---

## JAVA-EXCEPT-B04-C017 — Why is return in finally dangerous?

### Russian Translation

Почему return в finally опасен?

> [!answer]- Answer
> It can replace a previous return or suppress a pending exception.

### Explanation

Finally completion can override the earlier outcome.

### Exam Trap

Это скрывает failures.

---

## JAVA-EXCEPT-B04-C018 — Can execution continue after catch?

### Russian Translation

Может ли выполнение продолжиться после catch?

> [!answer]- Answer
> Yes, if catch and finally complete normally.

### Explanation

Execution resumes after the whole try statement.

### Exam Trap

Пропущенные строки failed try не выполняются заново.

---

## Level 4 — throw, throws and custom exceptions

## JAVA-EXCEPT-B04-C019 — What is the difference between throw and throws?

### Russian Translation

В чём разница `throw` и `throws`?

> [!answer]- Answer
> `throw` throws one object; `throws` declares possible checked failures.

### Explanation

One affects runtime flow; the other defines signature contract.

### Exam Trap

`throws` не создаёт exception.

---

## JAVA-EXCEPT-B04-C020 — Must a declared exception always occur?

### Russian Translation

Обязан ли declared exception всегда возникнуть?

> [!answer]- Answer
> No.

### Explanation

Declaration expresses possibility.

### Exam Trap

Это не обязательный outcome.

---

## JAVA-EXCEPT-B04-C021 — Why preserve the cause?

### Russian Translation

Почему нужно сохранять cause?

> [!answer]- Answer
> To retain the original type, message and stack trace.

### Explanation

Pass the original exception to a cause constructor.

### Exam Trap

Новый generic exception без cause уничтожает evidence.

---

## JAVA-EXCEPT-B04-C022 — Can override broaden checked exceptions?

### Russian Translation

Может ли override расширить checked exceptions?

> [!answer]- Answer
> No.

### Explanation

It may declare the same, narrower, or none.

### Exam Trap

Rule concerns checked exceptions, not arbitrary unchecked ones.

---

## JAVA-EXCEPT-B04-C023 — What belongs in an exception message?

### Russian Translation

Что должно быть в exception message?

> [!answer]- Answer
> The failed operation and safe identifying context.

### Explanation

A message should help diagnosis.

### Exam Trap

Не помещай passwords, tokens или sensitive personal data.

---

## JAVA-EXCEPT-B04-C024 — When is a custom exception useful?

### Russian Translation

Когда полезен custom exception?

> [!answer]- Answer
> When callers need a meaningful failure category or recovery contract.

### Explanation

Type communicates semantics across a boundary.

### Exam Trap

Не создавай type для каждого варианта текста.

---

## Level 5 — multi-catch

## JAVA-EXCEPT-B04-C025 — When should multi-catch be used?

### Russian Translation

Когда использовать multi-catch?

> [!answer]- Answer
> When unrelated exception types share the same handling policy.

### Explanation

It removes duplicate handler code.

### Exam Trap

Разные recovery paths не следует объединять.

---

## JAVA-EXCEPT-B04-C026 — Why is IOException | FileNotFoundException illegal?

### Russian Translation

Почему `IOException | FileNotFoundException` недопустим?

> [!answer]- Answer
> One alternative is a subtype of the other.

### Explanation

Multi-catch alternatives must not overlap by inheritance.

### Exam Trap

Parent уже покрывает child.

---

## JAVA-EXCEPT-B04-C027 — Can a multi-catch parameter be reassigned?

### Russian Translation

Можно ли переназначить multi-catch parameter?

> [!answer]- Answer
> No.

### Explanation

It is implicitly final for precise type analysis.

### Exam Trap

Assignment causes compilation failure.

---

## JAVA-EXCEPT-B04-C028 — Does one object have both multi-catch types?

### Russian Translation

Имеет ли объект оба типа multi-catch?

> [!answer]- Answer
> No.

### Explanation

The handler accepts any listed compatible runtime type.

### Exam Trap

Object still has one runtime class.

---

## JAVA-EXCEPT-B04-C029 — What is precise rethrow?

### Russian Translation

Что такое precise rethrow?

> [!answer]- Answer
> Compiler analysis preserving the specific checked types that can reach a broad catch.

### Explanation

It can avoid declaring broad `Exception`.

### Exam Trap

Это не делает broad catch хорошим по умолчанию.

---

## JAVA-EXCEPT-B04-C030 — When are separate catches better?

### Russian Translation

Когда отдельные catch лучше multi-catch?

> [!answer]- Answer
> When recovery, logging, metrics or user response differs.

### Explanation

Different semantics deserve different handlers.

### Exam Trap

Устранение duplication не должно стирать смысл.

---

## Level 6 — resources and suppressed failures

## JAVA-EXCEPT-B04-C031 — Which interface enables try-with-resources?

### Russian Translation

Какой interface нужен для try-with-resources?

> [!answer]- Answer
> `AutoCloseable`.

### Explanation

Declared resources are closed automatically.

### Exam Trap

GC не заменяет deterministic cleanup.

---

## JAVA-EXCEPT-B04-C032 — In what order are resources closed?

### Russian Translation

В каком порядке закрываются resources?

> [!answer]- Answer
> Reverse order of successful creation.

### Explanation

The latest resource closes first.

### Exam Trap

Не declaration order.

---

## JAVA-EXCEPT-B04-C033 — What if a later initializer fails?

### Russian Translation

Что если поздний resource initializer падает?

> [!answer]- Answer
> Earlier successfully created resources are closed; the body is not entered.

### Explanation

Try-with-resources protects partial initialization.

### Exam Trap

Не созданный resource нельзя закрыть.

---

## JAVA-EXCEPT-B04-C034 — What is a suppressed exception?

### Russian Translation

Что такое suppressed exception?

> [!answer]- Answer
> A secondary failure attached to a primary exception, often from `close()`.

### Explanation

Inspect it through `getSuppressed()`.

### Exam Trap

Suppressed chain отличается от cause chain.

---

## JAVA-EXCEPT-B04-C035 — What propagates if body succeeds but close fails?

### Russian Translation

Что уйдёт наружу, если body успешен, но close падает?

> [!answer]- Answer
> The close exception.

### Explanation

There is no body failure to remain primary.

### Exam Trap

Close failure нельзя считать автоматически проигнорированным.

---

## JAVA-EXCEPT-B04-C036 — Can an existing variable be used as a resource?

### Russian Translation

Можно ли использовать существующую переменную как resource?

> [!answer]- Answer
> Yes in Java 9+, when it is final or effectively final.

### Explanation

The variable may be referenced directly in the resource specification.

### Exam Trap

Reassignment makes this form illegal.
