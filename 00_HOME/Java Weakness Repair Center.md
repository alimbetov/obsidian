---
type: diagnostic-dashboard
domain: java
status: active
verified_at: 2026-07-25
cssclasses:
  - repair-center
  - wide-page
tags:
  - java
  - diagnosis
  - misconceptions
  - repair
  - pedagogy
---

# Java Weakness Repair Center

> [!repair]
> Ошибка не означает «ученик неспособен». Она показывает, какой механизм нужно восстановить. Не перечитывай весь раздел: найди одну ошибочную модель, один contrast и один новый прогноз.

## Сначала проверь нулевую модель

Если ученик не различает перечисленные слова, начни не с route notes, а с:

- [[10_CONCEPTS/Java/Foundations/Java Mental Model for Beginners]]

| Симптом | Что восстановить |
|---|---|
| путаю значение и переменную | value → variable → assignment |
| думаю, что ссылка содержит весь объект | object vs reference |
| не понимаю, какая строка выполняется | execution pointer / flow |
| смешиваю compile error и runtime exception | compilation vs execution |
| не могу пройти код по шагам | таблица трассировки |

## Repair loop: 12–20 минут

```text
1. Назови тип ошибки: attention / retrieval / discrimination / concept / transfer.
2. Запиши ошибочную мысль одним предложением.
3. Если тема новая — открой Beginner Bridge.
4. Открой одну atomic note.
5. Сравни правильное правило с ближайшим похожим.
6. Предскажи новый пример до ответа или запуска.
7. Повтори после интервала.
```

## B01 — Values, Text and Date-Time

Beginner entry: [[10_CONCEPTS/Java/Foundations/Java B01 Beginner Bridge]]

| Симптом | Focused concept |
|---|---|
| неверно определяю literal type, octal/hex/binary или underscores | [[10_CONCEPTS/Java/Core/Java Primitive Values and Literals]] |
| забываю binary numeric promotion, narrowing cast или overflow | [[10_CONCEPTS/Java/Core/Java Numeric Promotion and Casting]] |
| смешиваю boxing, unboxing, cache, `==`, `equals`, null | [[10_CONCEPTS/Java/Core/Java Wrappers Boxing and Math]] |
| ошибаюсь в pool, identity, index, substring, split/replace | [[10_CONCEPTS/Java/Core/Java String Identity and Operations]] |
| не вижу alias и mutation `StringBuilder` | [[10_CONCEPTS/Java/Core/Java StringBuilder Mutation]] |
| неверно считаю indentation или final newline text block | [[10_CONCEPTS/Java/Core/Java Text Blocks]] |
| смешиваю `LocalDate`, `LocalTime`, `LocalDateTime` | [[10_CONCEPTS/Java/Core/Java Local Date-Time Types]] |
| смешиваю calendar amount и elapsed amount | [[10_CONCEPTS/Java/Core/Java Period Duration and Instant]] |
| ошибаюсь в zone, offset, DST или formatter | [[10_CONCEPTS/Java/Core/Java Zones DST and Formatting]] |

## B02 — Control Flow and Pattern Switch

Beginner entry: [[10_CONCEPTS/Java/Foundations/Java B02 Beginner Bridge]]

| Симптом | Focused concept |
|---|---|
| не понимаю, какая ветка `if` выполнится | [[10_CONCEPTS/Java/Core/Java Conditions and Definite Assignment]] |
| теряю порядок `for`, `continue`, labels | [[10_CONCEPTS/Java/Core/Java Loops Transfers and Labels]] |
| путаю runtime impossibility и compile-time reachability | [[10_CONCEPTS/Java/Core/Java Reachability Rules]] |
| selector types, fall-through, arrow/colon rules | [[10_CONCEPTS/Java/Core/Java Classic Switch]] |
| `yield`, target typing, exhaustiveness expression | [[10_CONCEPTS/Java/Core/Java Switch Expressions]] |
| flow scope pattern variable | [[10_CONCEPTS/Java/Core/Java Pattern Matching for instanceof]] |
| `case null`, `when`, Java 21 boundary | [[10_CONCEPTS/Java/Core/Java 21 Pattern Switch]] |
| dominance, match-all, sealed exhaustiveness | [[10_CONCEPTS/Java/Core/Java Switch Dominance and Exhaustiveness]] |

## B03 — Object Model and Record Patterns

Beginner entry: [[10_CONCEPTS/Java/Foundations/Java B03 Beginner Bridge]]

| Симптом | Focused concept |
|---|---|
| не различаю class, object и reference | [[10_CONCEPTS/Java/Object Model/Java Object Creation Reachability and Lifecycle]] |
| путаю static nested, inner, local, anonymous | [[10_CONCEPTS/Java/Object Model/Java Nested Local and Anonymous Classes]] |
| неверно трассирую initialization и constructors | [[10_CONCEPTS/Java/Object Model/Java Fields Initializers and Constructor Order]] |
| смешиваю overload, boxing, widening и varargs | [[10_CONCEPTS/Java/Object Model/Java Overloading Varargs and Method Selection]] |
| scope, shadowing, effectively final, `var` | [[10_CONCEPTS/Java/Object Model/Java Scope Encapsulation Immutability and var]] |
| overload vs override, hiding, dispatch, casts | [[10_CONCEPTS/Java/Object Model/Java Inheritance Overriding Hiding and Polymorphism]] |
| abstract/interface/default/private/static contracts | [[10_CONCEPTS/Java/Object Model/Java Abstract Classes and Interfaces]] |
| record components и compact constructor | [[10_CONCEPTS/Java/Object Model/Java Records]] |
| enum constructor, fields, constant bodies | [[10_CONCEPTS/Java/Object Model/Java Enums]] |
| `sealed`, `final`, `non-sealed`, permits | [[10_CONCEPTS/Java/Object Model/Java Sealed Types]] |
| record-pattern arity/type/Java 21 syntax | [[10_CONCEPTS/Java/Object Model/Java Record Patterns]] |
| nested patterns и sealed switch coverage | [[10_CONCEPTS/Java/Object Model/Java Nested Patterns and Exhaustiveness]] |

## B04 — Exceptions and Resource Safety

Start: [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Roadmap]]

| Симптом | Focused concept |
|---|---|
| думаю, что после throw method продолжится | [[10_CONCEPTS/Java/Exceptions/Java Why Programs Fail and Exception Flow]] |
| путаю compile error, checked, unchecked и Error | [[10_CONCEPTS/Java/Exceptions/Java Checked Unchecked Exceptions and Errors]] |
| неверно выбираю catch или порядок finally | [[10_CONCEPTS/Java/Exceptions/Java Try Catch Finally]] |
| путаю `throw` и `throws`, теряю cause | [[10_CONCEPTS/Java/Exceptions/Java Throw Throws and Custom Exceptions]] |
| ошибаюсь в multi-catch или broad rethrow | [[10_CONCEPTS/Java/Exceptions/Java Multi-catch and Precise Rethrow]] |
| не понимаю close order, primary/suppressed | [[10_CONCEPTS/Java/Exceptions/Java Try-with-resources and Suppressed Exceptions]] |
| exception используется как обычный frequent branch | [[40_PRODUCTION_CASES/Java/Java Exceptions Production Cases#Case 3 — exception используется как обычный цикл]] |
| resource закрывается до async worker | [[40_PRODUCTION_CASES/Java/Java Exceptions Production Cases#Case 7 — async resource закрыт до выполнения worker]] |

## Контрастные пары высокого риска

| Не смешивать | Диагностический вопрос |
|---|---|
| primitive value vs object reference | копируется значение или reference на mutable object? |
| overload vs override | compiler выбирает signature или runtime выбирает implementation? |
| widening vs boxing vs varargs | какая invocation phase применима раньше? |
| field hiding vs method overriding | static/reference type или runtime object? |
| `Period` vs `Duration` | календарное количество или elapsed time? |
| classic switch vs switch expression | обязан ли construct произвести value? |
| checked vs unchecked | требует ли compiler catch-or-declare? |
| `throw` vs `throws` | object выбрасывается или contract объявляется? |
| cause vs suppressed | исходная причина или competing cleanup failure? |
| catch vs finally | failure обработан или только выполнен cleanup? |
| correct vs correct-guessed | могу ли объяснить mechanism до ответа? |

## Мини-протокол проверки repair

> [!checkpoint]
> Repair завершён после трёх действий:
>
> 1. объяснение простыми словами и точными терминами;
> 2. решение контрастного примера;
> 3. повторная проверка после интервала.

## Возврат к обучению

- [[00_HOME/Java Beginner Learning Path]]
- [[00_HOME/Java Learning Cockpit]]
- [[00_HOME/Card Review Dashboard]]
- [[70_PROGRESS/Java Learning Progress Dashboard]]
- [[90_TEMPLATES/Learning Session Template]]
