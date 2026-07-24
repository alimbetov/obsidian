---
type: diagnostic-dashboard
domain: java
status: active
verified_at: 2026-07-24
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
> Здесь ошибка превращается в маршрут восстановления. Цель — не перечитать весь раздел, а найти минимальный механизм, который объясняет ошибку, построить контраст и снова применить правило.

## Repair loop: 12–20 минут

```text
1. Назови тип ошибки: attention / retrieval / discrimination / concept / transfer.
2. Сформулируй ошибочную интуицию одним предложением.
3. Открой одну atomic note.
4. Сравни правильное правило с ближайшим похожим.
5. Предскажи новый пример до ответа или запуска.
6. Повтори карточку не сразу, а после интервала.
```

## B01 — Values, Text and Date-Time

| Симптом | Focused concept |
|---|---|
| неверно определяю literal type, octal/hex/binary или underscores | [[10_CONCEPTS/Java/Core/Java Primitive Values and Literals]] |
| забываю binary numeric promotion, narrowing cast или overflow | [[10_CONCEPTS/Java/Core/Java Numeric Promotion and Casting]] |
| смешиваю boxing, unboxing, cache, `==`, `equals`, null | [[10_CONCEPTS/Java/Core/Java Wrappers Boxing and Math]] |
| ошибаюсь в pool, identity, index, substring, split/replace | [[10_CONCEPTS/Java/Core/Java String Identity and Operations]] |
| не вижу alias и mutation цепочки `StringBuilder` | [[10_CONCEPTS/Java/Core/Java StringBuilder Mutation]] |
| неверно считаю indentation, escapes или final newline text block | [[10_CONCEPTS/Java/Core/Java Text Blocks]] |
| смешиваю `LocalDate`, `LocalTime`, `LocalDateTime` и units | [[10_CONCEPTS/Java/Core/Java Local Date-Time Types]] |
| смешиваю calendar amount и elapsed amount | [[10_CONCEPTS/Java/Core/Java Period Duration and Instant]] |
| ошибаюсь в zone, offset, DST gap/overlap или formatter | [[10_CONCEPTS/Java/Core/Java Zones DST and Formatting]] |

## B02 — Control Flow and Pattern Switch

| Симптом | Focused concept |
|---|---|
| `Boolean`/null, dangling `else`, definite assignment | [[10_CONCEPTS/Java/Core/Java Conditions and Definite Assignment]] |
| порядок `for`, `continue`, labels или target transfer | [[10_CONCEPTS/Java/Core/Java Loops Transfers and Labels]] |
| путаю runtime impossibility и compile-time reachability | [[10_CONCEPTS/Java/Core/Java Reachability Rules]] |
| selector types, fall-through, arrow/colon rules | [[10_CONCEPTS/Java/Core/Java Classic Switch]] |
| `yield`, target typing, exhaustiveness expression | [[10_CONCEPTS/Java/Core/Java Switch Expressions]] |
| flow scope pattern variable | [[10_CONCEPTS/Java/Core/Java Pattern Matching for instanceof]] |
| `case null`, `when`, Java 21 boundary | [[10_CONCEPTS/Java/Core/Java 21 Pattern Switch]] |
| dominance, match-all, sealed exhaustiveness | [[10_CONCEPTS/Java/Core/Java Switch Dominance and Exhaustiveness]] |

## B03 — Object Model and Record Patterns

| Симптом | Focused concept |
|---|---|
| не различаю object creation, reference reachability и lifecycle | [[10_CONCEPTS/Java/Object Model/Java Object Creation Reachability and Lifecycle]] |
| путаю static nested, inner, local и anonymous classes | [[10_CONCEPTS/Java/Object Model/Java Nested Local and Anonymous Classes]] |
| неверно трассирую static/instance initialization и constructors | [[10_CONCEPTS/Java/Object Model/Java Fields Initializers and Constructor Order]] |
| смешиваю overload selection, boxing, widening и varargs | [[10_CONCEPTS/Java/Object Model/Java Overloading Varargs and Method Selection]] |
| scope, shadowing, effectively final, `var`, immutability | [[10_CONCEPTS/Java/Object Model/Java Scope Encapsulation Immutability and var]] |
| overload против override, field hiding, dispatch и casts | [[10_CONCEPTS/Java/Object Model/Java Inheritance Overriding Hiding and Polymorphism]] |
| abstract/interface/default/private/static method contracts | [[10_CONCEPTS/Java/Object Model/Java Abstract Classes and Interfaces]] |
| record components, compact constructor, accessor restrictions | [[10_CONCEPTS/Java/Object Model/Java Records]] |
| enum constructor, fields, constant-specific bodies | [[10_CONCEPTS/Java/Object Model/Java Enums]] |
| `sealed`, `final`, `non-sealed`, permits boundary | [[10_CONCEPTS/Java/Object Model/Java Sealed Types]] |
| record-pattern arity, component type и Java 21 syntax | [[10_CONCEPTS/Java/Object Model/Java Record Patterns]] |
| nested patterns, dominance и sealed switch coverage | [[10_CONCEPTS/Java/Object Model/Java Nested Patterns and Exhaustiveness]] |

## Контрастные пары высокого риска

| Не смешивать | Диагностический вопрос |
|---|---|
| overload vs override | решение принимается компилятором по declared types или runtime по object type? |
| widening vs boxing vs varargs | какая применимая фаза method invocation срабатывает раньше? |
| field hiding vs method overriding | обращение зависит от reference type или runtime object? |
| `Period` vs `Duration` | календарные единицы или измеренное elapsed time? |
| classic switch vs switch expression | значение обязательно производится? нужна exhaustiveness? |
| default vs `case null` | обрабатывает ли default null selector? |
| sealed declaration vs exhaustive switch | известна hierarchy или доказано покрытие patterns? |
| correct vs correct-guessed | могу ли объяснить механизм до просмотра ответа? |

## Мини-протокол проверки repair

> [!checkpoint]
> Repair считается выполненным не после перечитывания, а после трёх действий:
>
> 1. объяснение механизма без текста;
> 2. решение контрастного примера;
> 3. повторная проверка после интервала.

## Возврат к обучению

- [[00_HOME/Java Learning Cockpit]]
- [[00_HOME/Card Review Dashboard]]
- [[70_PROGRESS/Java Learning Progress Dashboard]]
- [[90_TEMPLATES/Learning Session Template]]
