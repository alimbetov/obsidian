---
type: certification-batch
domain: java
subdomain: overloading-inheritance-interfaces
batch: JAVA-INHERIT-B03
status: published
card_count: 35
java_versions:
  - 17
  - 21
objectives:
  - JAVA-B03
  - JAVA21-3.3
  - JAVA21-3.5
  - JAVA21-3.6
tags:
  - java
  - object-model
  - active-recall
---

# JAVA-B03C — Overloading, Inheritance and Interfaces Cards

## Route navigation

- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- [[10_CONCEPTS/Java/Object Model/Java Overloading Varargs and Method Selection]]
- [[10_CONCEPTS/Java/Object Model/Java Inheritance Overriding Hiding and Polymorphism]]
- [[10_CONCEPTS/Java/Object Model/Java Abstract Classes and Interfaces]]
- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- [[50_LABS/Java/JAVA-B03/README]]

---

## JAVA-INHERIT-B03-C001 — When is an overloaded method selected?

### Russian Translation

Когда выбирается перегруженный метод?

> [!answer]- Answer
> At compile time from the argument expressions and available declarations.

### Explanation

Runtime object type does not redo overload selection.

### Exam Trap

Do not confuse overloading with overriding.

---

## JAVA-INHERIT-B03-C002 — When is an overridden instance method selected?

### Russian Translation

Когда выбирается переопределённый instance-метод?

> [!answer]- Answer
> At runtime from the actual receiver object after compile-time signature selection.

### Explanation

Virtual dispatch applies to eligible instance methods.

### Exam Trap

Static, private and final methods do not participate as ordinary overrides.

---

## JAVA-INHERIT-B03-C003 — Which invocation phase is considered before boxing?

### Russian Translation

Какая фаза вызова рассматривается до boxing?

> [!answer]- Answer
> Exact matches and primitive/reference widening.

### Explanation

A widening primitive overload can beat a wrapper overload.

### Exam Trap

Do not assume boxing is always preferred because it looks exact by value.

---

## JAVA-INHERIT-B03-C004 — Which is considered before varargs: boxing or variable arity?

### Russian Translation

Что рассматривается раньше: boxing или varargs?

> [!answer]- Answer
> Boxing/unboxing applicability.

### Explanation

Varargs is a later fallback phase.

### Exam Trap

A fixed-arity wrapper overload can beat a varargs overload.

---

## JAVA-INHERIT-B03-C005 — Can an `int` argument be widened to `long` and then boxed to `Long` for invocation?

### Russian Translation

Можно ли расширить `int` до `long`, затем упаковать в `Long` при вызове?

> [!answer]- Answer
> No.

### Explanation

Method invocation does not permit arbitrary widening-then-boxing chains.

### Exam Trap

An `Integer` may unbox to int and then widen to long.

---

## JAVA-INHERIT-B03-C006 — Can an `Integer` argument invoke a `long` parameter?

### Russian Translation

Может ли `Integer` вызвать метод с параметром `long`?

> [!answer]- Answer
> Yes, by unboxing to `int` and widening to `long`.

### Explanation

Unboxing followed by primitive widening is permitted.

### Exam Trap

A null Integer would throw during unboxing at runtime.

---

## JAVA-INHERIT-B03-C007 — Why can overloads `m(String)` and `m(Integer)` make `m(null)` ambiguous?

### Russian Translation

Почему перегрузки `m(String)` и `m(Integer)` делают `m(null)` неоднозначным?

> [!answer]- Answer
> Both are applicable and neither parameter type is more specific than the other.

### Explanation

The null literal converts to either unrelated reference type.

### Exam Trap

Casting null resolves the target overload.

---

## JAVA-INHERIT-B03-C008 — What runtime object backs a varargs parameter?

### Russian Translation

Какой runtime-объект представляет varargs-параметр?

> [!answer]- Answer
> An array.

### Explanation

The compiler can package separate arguments into an array.

### Exam Trap

Passing an existing array uses that array directly.

---

## JAVA-INHERIT-B03-C009 — Where must a varargs parameter appear?

### Russian Translation

Где должен находиться varargs-параметр?

> [!answer]- Answer
> Last in the parameter list.

### Explanation

A method can have at most one variable-arity parameter.

### Exam Trap

Parameters cannot follow it.

---

## JAVA-INHERIT-B03-C010 — Can return type alone overload a method?

### Russian Translation

Можно ли перегрузить метод только по возвращаемому типу?

> [!answer]- Answer
> No.

### Explanation

The invocation argument list would not distinguish the declarations.

### Exam Trap

Parameter signature must differ.

---

## JAVA-INHERIT-B03-C011 — What return types are allowed in an override?

### Russian Translation

Какие возвращаемые типы разрешены при override?

> [!answer]- Answer
> The same type or a covariant reference subtype.

### Explanation

Primitive return types must match exactly.

### Exam Trap

Covariance does not apply to unrelated reference types.

---

## JAVA-INHERIT-B03-C012 — May an override reduce method visibility?

### Russian Translation

Может ли override уменьшить видимость метода?

> [!answer]- Answer
> No.

### Explanation

It may preserve or widen access.

### Exam Trap

A protected method cannot become private in a subclass override.

---

## JAVA-INHERIT-B03-C013 — May an override declare a broader checked exception?

### Russian Translation

Может ли override объявить более широкий checked exception?

> [!answer]- Answer
> No.

### Explanation

It may declare the same, narrower or no checked exceptions.

### Exam Trap

Unchecked exceptions are not restricted by this rule.

---

## JAVA-INHERIT-B03-C014 — Are private superclass methods overridden?

### Russian Translation

Переопределяются ли private-методы суперкласса?

> [!answer]- Answer
> No.

### Explanation

They are not inherited; a same-signature subclass method is a new declaration.

### Exam Trap

`@Override` would fail for that private method.

---

## JAVA-INHERIT-B03-C015 — Are static methods overridden?

### Russian Translation

Переопределяются ли static-методы?

> [!answer]- Answer
> No, they are hidden.

### Explanation

Selection follows the compile-time type used for the call.

### Exam Trap

Static/instance signature conflicts are compile-time errors.

---

## JAVA-INHERIT-B03-C016 — Are fields polymorphically dispatched?

### Russian Translation

Выбираются ли поля полиморфно?

> [!answer]- Answer
> No.

### Explanation

Field selection uses the reference expression's compile-time type.

### Exam Trap

A subclass field hides rather than overrides a parent field.

---

## JAVA-INHERIT-B03-C017 — Can a final instance method be overridden?

### Russian Translation

Можно ли переопределить final instance-метод?

> [!answer]- Answer
> No.

### Explanation

`final` closes that method implementation to overriding.

### Exam Trap

It may still be overloaded with a different signature.

---

## JAVA-INHERIT-B03-C018 — Can an abstract class be instantiated directly?

### Russian Translation

Можно ли напрямую создать экземпляр abstract-класса?

> [!answer]- Answer
> No.

### Explanation

It may provide state, constructors and concrete behavior for subclasses.

### Exam Trap

An anonymous concrete subclass can be instantiated.

---

## JAVA-INHERIT-B03-C019 — What must a concrete subclass do with inherited abstract methods?

### Russian Translation

Что должен сделать concrete-подкласс с inherited abstract methods?

> [!answer]- Answer
> Implement all of them with compatible methods.

### Explanation

Otherwise the subclass must also be abstract.

### Exam Trap

Default interface methods may satisfy some inherited contracts.

---

## JAVA-INHERIT-B03-C020 — What access do ordinary abstract interface methods have?

### Russian Translation

Какой доступ имеют обычные abstract-методы интерфейса?

> [!answer]- Answer
> They are implicitly public.

### Explanation

An implementing method cannot reduce that visibility.

### Exam Trap

Package-private implementation does not satisfy the interface method.

---

## JAVA-INHERIT-B03-C021 — What happens with conflicting defaults from unrelated interfaces?

### Russian Translation

Что происходит при конфликте default-методов несвязанных интерфейсов?

> [!answer]- Answer
> The implementing class must override and resolve the conflict.

### Explanation

It may call `A.super.method()` and `B.super.method()` explicitly.

### Exam Trap

The compiler does not choose arbitrarily.

---

## JAVA-INHERIT-B03-C022 — Which wins: a concrete superclass method or an interface default?

### Russian Translation

Что имеет приоритет: concrete-метод класса или default интерфейса?

> [!answer]- Answer
> The class method.

### Explanation

Class inheritance takes precedence over interface defaults.

### Exam Trap

Even an inherited class method can suppress the default.

---

## JAVA-INHERIT-B03-C023 — Are private interface methods inherited?

### Russian Translation

Наследуются ли private-методы интерфейса?

> [!answer]- Answer
> No.

### Explanation

They are implementation helpers inside the interface.

### Exam Trap

They must have a body.

---

## JAVA-INHERIT-B03-C024 — Are static interface methods inherited as instance methods?

### Russian Translation

Наследуются ли static-методы интерфейса как instance-методы?

> [!answer]- Answer
> No.

### Explanation

Call them through the interface type.

### Exam Trap

An implementing class may declare an unrelated static method with the same name.

---

## JAVA-INHERIT-B03-C025 — What is a functional interface?

### Russian Translation

Что такое functional interface?

> [!answer]- Answer
> An interface with one abstract method after applying inheritance and Object-method rules.

### Explanation

Default, static and private methods do not count as additional abstract methods.

### Exam Trap

`@FunctionalInterface` enables compiler verification.

---

## JAVA-INHERIT-B03-C026 — Why does a lambda require a target type?

### Russian Translation

Почему lambda требует target type?

> [!answer]- Answer
> Its parameter and return compatibility are interpreted against a functional-interface descriptor.

### Explanation

A lambda has no standalone class type in source.

### Exam Trap

Overloaded targets can become ambiguous.

---

## JAVA-INHERIT-B03-C027 — Is an upcast implicit?

### Russian Translation

Является ли upcast неявным?

> [!answer]- Answer
> Yes, from a subtype reference to a supertype reference.

### Explanation

The object is unchanged; only the accessible static view changes.

### Exam Trap

Subclass-only members are unavailable through the supertype reference.

---

## JAVA-INHERIT-B03-C028 — Does a downcast require an explicit cast?

### Russian Translation

Требует ли downcast явного приведения?

> [!answer]- Answer
> Yes.

### Explanation

The compiler checks possible type relatedness and runtime checks the object.

### Exam Trap

An incompatible runtime object throws `ClassCastException`.

---

## JAVA-INHERIT-B03-C029 — What is the result of `null instanceof Type`?

### Russian Translation

Каков результат `null instanceof Type`?

> [!answer]- Answer
> `false`.

### Explanation

No pattern variable is bound.

### Exam Trap

This test does not throw `NullPointerException`.

---

## JAVA-INHERIT-B03-C030 — Where is an `instanceof` pattern variable in scope?

### Russian Translation

Где доступна pattern-переменная `instanceof`?

> [!answer]- Answer
> Where flow analysis proves the pattern matched.

### Explanation

It is valid on the right of `&&` after a successful match and after a negated early exit.

### Exam Trap

It is not generally valid on the right of `||`.

---

## JAVA-INHERIT-B03-C031 — What does `super.method()` select?

### Russian Translation

Что выбирает `super.method()`?

> [!answer]- Answer
> The superclass implementation named from the subclass context.

### Explanation

That invocation bypasses virtual dispatch to the current override.

### Exam Trap

`super` is not available in static context.

---

## JAVA-INHERIT-B03-C032 — Can an override return `Integer` when the parent returns `Number`?

### Russian Translation

Может ли override возвращать `Integer`, если parent возвращает `Number`?

> [!answer]- Answer
> Yes.

### Explanation

`Integer` is a covariant reference return subtype of `Number`.

### Exam Trap

The method parameter signature must still match.

---

## JAVA-INHERIT-B03-C033 — Can an override add an unchecked exception?

### Russian Translation

Может ли override добавить unchecked exception?

> [!answer]- Answer
> Yes.

### Explanation

The checked-exception narrowing rule does not restrict runtime exceptions.

### Exam Trap

API design may still make this undesirable.

---

## JAVA-INHERIT-B03-C034 — Does runtime receiver type affect an already selected overload signature?

### Russian Translation

Влияет ли runtime-тип receiver на уже выбранную overload-сигнатуру?

> [!answer]- Answer
> No.

### Explanation

Overload resolution is complete at compile time; runtime dispatch may choose an override of that signature.

### Exam Trap

A child-only overload is not considered through a parent-typed reference.

---

## JAVA-INHERIT-B03-C035 — Can a method be both abstract and final?

### Russian Translation

Может ли метод быть одновременно abstract и final?

> [!answer]- Answer
> No.

### Explanation

Abstract requires implementation by overriding; final forbids overriding.

### Exam Trap

The modifiers are semantically incompatible.

---
