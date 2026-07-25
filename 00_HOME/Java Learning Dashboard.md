---
type: dashboard
domain: java
status: active
verified_at: 2026-07-25
current_next_route: JAVA-B05
published_routes:
  - JAVA-LTS-B01
  - JAVA-B01
  - JAVA-B02
  - JAVA-B03
  - JAVA-B04
lab_proven_routes:
  - JAVA-B01
  - JAVA-B02
  - JAVA-B03
  - JAVA-B04
published_base_cards: 286
published_drills: 90
atomic_notes: 35
cssclasses:
  - learning-dashboard
  - wide-page
tags:
  - java
  - navigation
  - learning-dashboard
  - active-recall
  - progress
---

# Java Learning Dashboard

> [!start]
> **Первый раз изучаешь Java или обучаешь школьника/студента:** открой [[00_HOME/Java Beginner Learning Path]].  
> **Уже умеешь читать Java-код:** для ежедневной работы используй [[00_HOME/Java Learning Cockpit]].

## Два режима входа

| Режим | Для кого | Начать |
|---|---|---|
| Beginner path | школьник, студент, начинающий | [[00_HOME/Java Beginner Learning Path]] |
| Route catalog | разработчик, certification candidate | эта страница |

## Опубликованные маршруты

| Status | Route | Beginner entry | Atomic notes | Cards | Drills | Roadmap |
|---|---|---|---:|---:|---:|---|
| `lab-proven` | JAVA-B01 — Values, Text and Date-Time | [[10_CONCEPTS/Java/Foundations/Java B01 Beginner Bridge|bridge]] | 9 | 75 | 15 | [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]] |
| `lab-proven` | JAVA-B02 — Control Flow and Pattern Switch | [[10_CONCEPTS/Java/Foundations/Java B02 Beginner Bridge|bridge]] | 8 | 60 | 20 | [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]] |
| `lab-proven` | JAVA-B03 — Object Model and Record Patterns | [[10_CONCEPTS/Java/Foundations/Java B03 Beginner Bridge|bridge]] | 12 | 115 | 35 | [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]] |
| `lab-proven` | JAVA-B04 — Exceptions and Resource Safety | roadmap starts with beginner model | 6 | 36 | 20 | [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Roadmap]] |
| `next` | JAVA-B05 — Collections, Generics and Sequenced Collections | — | — | — | — | planned |

## Нулевая ступень

Перед B01 начинающий должен понять:

- [[10_CONCEPTS/Java/Foundations/Java Mental Model for Beginners]]

Она объясняет:

```text
значение → переменная → выражение → statement
class → object → reference
source → compilation → execution
```

## Выбрать маршрут по задаче

> [!learn]
> **Значения, строки, даты:** [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time|JAVA-B01 hub]]

> [!learn]
> **Условия, циклы, switch, patterns:** [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch|JAVA-B02 hub]]

> [!learn]
> **Объекты, initialization, inheritance, records:** [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns|JAVA-B03 hub]]

> [!learn]
> **Exceptions, stack traces и resource cleanup:** [[10_CONCEPTS/Java/Exceptions/Java Exceptions and Resource Safety|JAVA-B04 hub]]

## JAVA-B01 concept map

| # | Atomic concept | Practice batch |
|---:|---|---|
| 1 | [[10_CONCEPTS/Java/Core/Java Primitive Values and Literals]] | `JAVA-VALUES-B01` |
| 2 | [[10_CONCEPTS/Java/Core/Java Numeric Promotion and Casting]] | `JAVA-VALUES-B01` |
| 3 | [[10_CONCEPTS/Java/Core/Java Wrappers Boxing and Math]] | `JAVA-VALUES-B01` |
| 4 | [[10_CONCEPTS/Java/Core/Java String Identity and Operations]] | `JAVA-TEXT-B01` |
| 5 | [[10_CONCEPTS/Java/Core/Java StringBuilder Mutation]] | `JAVA-TEXT-B01` |
| 6 | [[10_CONCEPTS/Java/Core/Java Text Blocks]] | `JAVA-TEXT-B01` |
| 7 | [[10_CONCEPTS/Java/Core/Java Local Date-Time Types]] | `JAVA-TIME-B01` |
| 8 | [[10_CONCEPTS/Java/Core/Java Period Duration and Instant]] | `JAVA-TIME-B01` |
| 9 | [[10_CONCEPTS/Java/Core/Java Zones DST and Formatting]] | `JAVA-TIME-B01` |

## JAVA-B02 concept map

| # | Atomic concept | Practice batch |
|---:|---|---|
| 1 | [[10_CONCEPTS/Java/Core/Java Conditions and Definite Assignment]] | `JAVA-FLOW-B02` |
| 2 | [[10_CONCEPTS/Java/Core/Java Loops Transfers and Labels]] | `JAVA-FLOW-B02` |
| 3 | [[10_CONCEPTS/Java/Core/Java Reachability Rules]] | `JAVA-FLOW-B02` |
| 4 | [[10_CONCEPTS/Java/Core/Java Classic Switch]] | `JAVA-SWITCH-B02` |
| 5 | [[10_CONCEPTS/Java/Core/Java Switch Expressions]] | `JAVA-SWITCH-B02` |
| 6 | [[10_CONCEPTS/Java/Core/Java Pattern Matching for instanceof]] | `JAVA-FLOW-B02` |
| 7 | [[10_CONCEPTS/Java/Core/Java 21 Pattern Switch]] | `JAVA-PATTERN-B02` |
| 8 | [[10_CONCEPTS/Java/Core/Java Switch Dominance and Exhaustiveness]] | `JAVA-PATTERN-B02` |

## JAVA-B03 concept map

| # | Atomic concept | Practice batch |
|---:|---|---|
| 1 | [[10_CONCEPTS/Java/Object Model/Java Object Creation Reachability and Lifecycle]] | `JAVA-OBJECT-B03` |
| 2 | [[10_CONCEPTS/Java/Object Model/Java Nested Local and Anonymous Classes]] | `JAVA-OBJECT-B03` |
| 3 | [[10_CONCEPTS/Java/Object Model/Java Fields Initializers and Constructor Order]] | `JAVA-INIT-B03` |
| 4 | [[10_CONCEPTS/Java/Object Model/Java Overloading Varargs and Method Selection]] | `JAVA-INIT-B03` |
| 5 | [[10_CONCEPTS/Java/Object Model/Java Scope Encapsulation Immutability and var]] | `JAVA-INIT-B03` |
| 6 | [[10_CONCEPTS/Java/Object Model/Java Inheritance Overriding Hiding and Polymorphism]] | `JAVA-INHERIT-B03` |
| 7 | [[10_CONCEPTS/Java/Object Model/Java Abstract Classes and Interfaces]] | `JAVA-INHERIT-B03` |
| 8 | [[10_CONCEPTS/Java/Object Model/Java Records]] | `JAVA-TYPES-B03` |
| 9 | [[10_CONCEPTS/Java/Object Model/Java Enums]] | `JAVA-TYPES-B03` |
| 10 | [[10_CONCEPTS/Java/Object Model/Java Sealed Types]] | `JAVA-TYPES-B03` |
| 11 | [[10_CONCEPTS/Java/Object Model/Java Record Patterns]] | `JAVA-TYPES-B03` |
| 12 | [[10_CONCEPTS/Java/Object Model/Java Nested Patterns and Exhaustiveness]] | `JAVA-TYPES-B03` |

## JAVA-B04 concept map

| # | Atomic concept | Учебный фокус |
|---:|---|---|
| 1 | [[10_CONCEPTS/Java/Exceptions/Java Why Programs Fail and Exception Flow]] | normal vs exceptional path |
| 2 | [[10_CONCEPTS/Java/Exceptions/Java Checked Unchecked Exceptions and Errors]] | hierarchy и compiler contract |
| 3 | [[10_CONCEPTS/Java/Exceptions/Java Try Catch Finally]] | handler и cleanup |
| 4 | [[10_CONCEPTS/Java/Exceptions/Java Throw Throws and Custom Exceptions]] | failure API |
| 5 | [[10_CONCEPTS/Java/Exceptions/Java Multi-catch and Precise Rethrow]] | exact type handling |
| 6 | [[10_CONCEPTS/Java/Exceptions/Java Try-with-resources and Suppressed Exceptions]] | deterministic resource lifecycle |

## Практика и доказательства

| Route | Cards | Drills | Cases | Lab |
|---|---|---|---|---|
| B01 | [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01A Values Cards|values]] · [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01B Text Cards|text]] · [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01C Date-Time Cards|time]] | [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills|15]] | — | [[50_LABS/Java/JAVA-B01/README]] |
| B02 | [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02A Control Flow Cards|flow]] · [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02B Switch Cards|switch]] · [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02C Pattern Switch Cards|patterns]] | [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills|20]] | — | [[50_LABS/Java/JAVA-B02/README]] |
| B03 | [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03A Object Lifecycle Cards|objects]] · [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03B Initialization and Scope Cards|init]] · [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03C Inheritance and Interfaces Cards|inheritance]] · [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03D Records Sealed and Patterns Cards|types]] | [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills|35]] | — | [[50_LABS/Java/JAVA-B03/README]] |
| B04 | [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Cards|36]] | [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Drills|20]] | [[40_PRODUCTION_CASES/Java/Java Exceptions Production Cases|8]] | [[50_LABS/Java/JAVA-B04/README|JDK 17/21 PASS]] |

## Управление обучением

- [[00_HOME/Knowledge Route Registry]] — machine-governed registry.
- [[00_HOME/Card Review Dashboard]] — due cards и outcome recording.
- [[00_HOME/Java Weakness Repair Center]] — ошибка → mechanism → repair.
- [[70_PROGRESS/Java Learning Progress Dashboard]] — weekly review.
- [[01_MAPS/Java Learning Journey.canvas]] — visual journey.
- [[30_CERTIFICATIONS/Java/Java 17 and 21 Exam Delta Matrix]] — version boundary.

## Что будет дальше

```text
JAVA-B05 — Collections, Generics and Sequenced Collections
```
