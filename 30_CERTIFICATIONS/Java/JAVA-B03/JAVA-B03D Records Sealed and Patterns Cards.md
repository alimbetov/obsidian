---
type: certification-batch
domain: java
subdomain: records-enums-sealed-patterns
batch: JAVA-TYPES-B03
status: published
card_count: 25
java_versions:
  - 17
  - 21
objectives:
  - JAVA-B03
  - JAVA21-3.5
  - JAVA21-3.7
tags:
  - java
  - object-model
  - active-recall
---

# JAVA-B03D — Records, Enums, Sealed Types and Record Patterns Cards

## Route navigation

- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- [[10_CONCEPTS/Java/Object Model/Java Records]]
- [[10_CONCEPTS/Java/Object Model/Java Enums]]
- [[10_CONCEPTS/Java/Object Model/Java Sealed Types]]
- [[10_CONCEPTS/Java/Object Model/Java Record Patterns]]
- [[10_CONCEPTS/Java/Object Model/Java Nested Patterns and Exhaustiveness]]
- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- [[50_LABS/Java/JAVA-B03/README]]

---

## JAVA-TYPES-B03-C001 — Are record classes implicitly final?

### Russian Translation

Являются ли record-классы неявно final?

> [!answer]- Answer
> Yes.

### Explanation

A record cannot be extended by another class.

### Exam Trap

It may implement interfaces.

---

## JAVA-TYPES-B03-C002 — What members are generated for each record component?

### Russian Translation

Какие члены генерируются для каждого record component?

> [!answer]- Answer
> A private final field and a public accessor named like the component.

### Explanation

The state description also contributes to canonical construction and Object methods.

### Exam Trap

The accessor is not named `getX()` automatically.

---

## JAVA-TYPES-B03-C003 — What is a record canonical constructor?

### Russian Translation

Что такое canonical-конструктор record?

> [!answer]- Answer
> A constructor whose parameter list corresponds to the record components in order and type.

### Explanation

It initializes the complete record state.

### Exam Trap

Its accessibility cannot be more restrictive than the record requires.

---

## JAVA-TYPES-B03-C004 — When are fields assigned in a compact record constructor?

### Russian Translation

Когда присваиваются поля в compact-конструкторе record?

> [!answer]- Answer
> After the compact constructor body.

### Explanation

The compiler inserts component-field assignments from the possibly reassigned parameters.

### Exam Trap

Direct field assignment in the compact body is forbidden.

---

## JAVA-TYPES-B03-C005 — Can a compact constructor normalize a component parameter?

### Russian Translation

Может ли compact-конструктор нормализовать параметр компонента?

> [!answer]- Answer
> Yes, by reassigning the parameter.

### Explanation

The normalized parameter value is assigned to the component field after the body.

### Exam Trap

This differs from assigning `this.component` directly.

---

## JAVA-TYPES-B03-C006 — Can a record declare extra instance fields?

### Russian Translation

Может ли record объявлять дополнительные instance-поля?

> [!answer]- Answer
> No.

### Explanation

Its instance state is declared by the record components.

### Exam Trap

Static fields are allowed.

---

## JAVA-TYPES-B03-C007 — Can a record declare instance methods?

### Russian Translation

Может ли record объявлять instance-методы?

> [!answer]- Answer
> Yes.

### Explanation

Methods can derive behavior from components or implement interfaces.

### Exam Trap

The record remains final.

---

## JAVA-TYPES-B03-C008 — Can a record implement interfaces?

### Russian Translation

Может ли record реализовывать интерфейсы?

> [!answer]- Answer
> Yes.

### Explanation

Records cannot extend a custom class but may implement one or more interfaces.

### Exam Trap

Generated accessors may satisfy interface methods when signatures match.

---

## JAVA-TYPES-B03-C009 — How does record `equals` work?

### Russian Translation

Как работает `equals` у record?

> [!answer]- Answer
> It compares record class and component values structurally.

### Explanation

Two different record classes are not equal merely because their components match.

### Exam Trap

Mutable component contents can still change their own equality behavior.

---

## JAVA-TYPES-B03-C010 — Does a record guarantee deep immutability?

### Russian Translation

Гарантирует ли record глубокую неизменяемость?

> [!answer]- Answer
> No.

### Explanation

Component fields are final, but referenced component objects may be mutable.

### Exam Trap

Defensive copies may still be necessary.

---

## JAVA-TYPES-B03-C011 — What are enum constants at runtime?

### Russian Translation

Чем являются enum-константы во время выполнения?

> [!answer]- Answer
> The fixed instances of the enum class.

### Explanation

Each constant is initialized once during enum class initialization.

### Exam Trap

Do not instantiate enum values with `new`.

---

## JAVA-TYPES-B03-C012 — Can an enum constructor be public or protected?

### Russian Translation

Может ли enum-конструктор быть public или protected?

> [!answer]- Answer
> No.

### Explanation

Enum construction is restricted to the declared constants.

### Exam Trap

The constructor is private in effect.

---

## JAVA-TYPES-B03-C013 — Can enum constants have constant-specific class bodies?

### Russian Translation

Могут ли enum-константы иметь собственные class bodies?

> [!answer]- Answer
> Yes.

### Explanation

They can override enum methods for constant-specific behavior.

### Exam Trap

A constant body is not a separately instantiable public class.

---

## JAVA-TYPES-B03-C014 — Does `values()` return the enum's internal mutable array?

### Russian Translation

Возвращает ли `values()` внутренний массив enum?

> [!answer]- Answer
> No, it returns a new array.

### Explanation

Mutating the returned array does not change the enum constants.

### Exam Trap

Repeated calls allocate separate arrays.

---

## JAVA-TYPES-B03-C015 — What does a sealed type restrict?

### Russian Translation

Что ограничивает sealed-тип?

> [!answer]- Answer
> Its permitted direct subclasses or implementors.

### Explanation

The restriction forms an explicit inheritance boundary.

### Exam Trap

Indirect descendants depend on whether permitted children are final, sealed or non-sealed.

---

## JAVA-TYPES-B03-C016 — Which modifiers may a permitted direct subclass use?

### Russian Translation

Какие модификаторы может использовать разрешённый прямой подкласс?

> [!answer]- Answer
> `final`, `sealed` or `non-sealed`.

### Explanation

A record is already final and can be a permitted implementation.

### Exam Trap

An ordinary unmodified direct subclass is invalid.

---

## JAVA-TYPES-B03-C017 — What does `non-sealed` mean?

### Russian Translation

Что означает `non-sealed`?

> [!answer]- Answer
> It reopens inheritance below that permitted subtype.

### Explanation

Further subclasses are not constrained by the original sealed parent.

### Exam Trap

The modifier is legal only in the required sealed-parent context.

---

## JAVA-TYPES-B03-C018 — Can any unrelated class use `non-sealed`?

### Russian Translation

Может ли любой класс быть объявлен `non-sealed`?

> [!answer]- Answer
> No.

### Explanation

It must directly extend or implement a sealed type where the modifier is required.

### Exam Trap

Using it without a sealed direct parent is a compile-time error.

---

## JAVA-TYPES-B03-C019 — Can a record be a permitted subtype of a sealed interface?

### Russian Translation

Может ли record быть разрешённым подтипом sealed-интерфейса?

> [!answer]- Answer
> Yes.

### Explanation

The record's implicit finality satisfies the permitted-subtype requirement.

### Exam Trap

Its declaration must still satisfy module/package locality rules.

---

## JAVA-TYPES-B03-C020 — Why do sealed types support exhaustive switches?

### Russian Translation

Почему sealed-типы помогают exhaustive switch?

> [!answer]- Answer
> The compiler knows the closed set of permitted direct alternatives.

### Explanation

Covering each alternative can make an enhanced switch exhaustive without default.

### Exam Trap

A non-sealed branch may require broader handling.

---

## JAVA-TYPES-B03-C021 — What does a Java 21 record pattern do?

### Russian Translation

Что делает record pattern в Java 21?

> [!answer]- Answer
> It type-tests a record and deconstructs its components into component patterns.

### Explanation

The record must be non-null and compatible.

### Exam Trap

This is not ordinary Java 17 syntax.

---

## JAVA-TYPES-B03-C022 — Do record patterns match null?

### Russian Translation

Совпадает ли record pattern с null?

> [!answer]- Answer
> No.

### Explanation

Handle null with a separate `case null` or prior check.

### Exam Trap

A record pattern is not a null-tolerant deconstructor.

---

## JAVA-TYPES-B03-C023 — Must record-pattern arity match component count?

### Russian Translation

Должно ли число элементов record pattern совпадать с числом компонентов?

> [!answer]- Answer
> Yes.

### Explanation

Each record component needs a corresponding component pattern.

### Exam Trap

Wrong arity is a compile-time error.

---

## JAVA-TYPES-B03-C024 — What does `var` mean inside a record pattern?

### Russian Translation

Что означает `var` внутри record pattern?

> [!answer]- Answer
> Infer the component pattern's static type.

### Explanation

It does not create dynamic typing.

### Exam Trap

The inferred variable remains scoped to the matched rule.

---

## JAVA-TYPES-B03-C025 — How should specific nested patterns be ordered relative to broad patterns?

### Russian Translation

Как располагать specific nested patterns относительно широких patterns?

> [!answer]- Answer
> Place specific patterns first.

### Explanation

A broad earlier deconstruction can dominate and make a later specific pattern unreachable.

### Exam Trap

Source order and dominance are compile-time concerns.

---
