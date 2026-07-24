---
type: certification-batch
domain: java
subdomain: object-lifecycle-nested
batch: JAVA-OBJECT-B03
status: published
card_count: 25
java_versions:
  - 17
  - 21
objectives:
  - JAVA-B03
  - JAVA21-3.1
tags:
  - java
  - object-model
  - active-recall
---

# JAVA-B03A — Object Lifecycle and Nested Types Cards

## Route navigation

- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- [[10_CONCEPTS/Java/Object Model/Java Object Creation Reachability and Lifecycle]]
- [[10_CONCEPTS/Java/Object Model/Java Nested Local and Anonymous Classes]]
- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- [[50_LABS/Java/JAVA-B03/README]]

---

## JAVA-OBJECT-B03-C001 — What does a Java reference variable store?

### Russian Translation

Что хранит ссылочная переменная Java?

> [!answer]- Answer
> A reference value identifying an object or `null`, not the object contents themselves.

### Explanation

Assigning the reference copies the reference value.

### Exam Trap

Two variables can alias one mutable object.

---

## JAVA-OBJECT-B03-C002 — Does assigning one object reference to another copy the object?

### Russian Translation

Копирует ли присваивание одной ссылки другой сам объект?

> [!answer]- Answer
> No. Both variables refer to the same object unless an explicit copy is created.

### Explanation

Java reference assignment is aliasing, not cloning.

### Exam Trap

Mutating through either alias is visible through the other.

---

## JAVA-OBJECT-B03-C003 — What does `new` do before a constructor body runs?

### Russian Translation

Что делает `new` до выполнения тела конструктора?

> [!answer]- Answer
> It allocates storage, applies default field values and begins superclass-first initialization.

### Explanation

The constructor body is the last part of the object initialization path.

### Exam Trap

A constructor does not allocate by itself and has no return type.

---

## JAVA-OBJECT-B03-C004 — Does `null` refer to an object?

### Russian Translation

Ссылается ли `null` на объект?

> [!answer]- Answer
> No. It is the null reference value and designates no object.

### Explanation

Member access through null normally throws `NullPointerException`.

### Exam Trap

The declaration can compile even though dereference fails at runtime.

---

## JAVA-OBJECT-B03-C005 — Is object reachability the same as local-variable scope?

### Russian Translation

Совпадает ли достижимость объекта с областью видимости локальной переменной?

> [!answer]- Answer
> No. An object may remain reachable through other references after one variable leaves scope.

### Explanation

Reachability follows the object graph, not source-level scope alone.

### Exam Trap

Leaving a block does not guarantee collection.

---

## JAVA-OBJECT-B03-C006 — Does garbage-collection eligibility guarantee immediate collection?

### Russian Translation

Гарантирует ли доступность для GC немедленную сборку?

> [!answer]- Answer
> No. Eligibility only means no live reachability path remains.

### Explanation

The collector and timing are implementation decisions.

### Exam Trap

Never predict exact GC timing in deterministic output.

---

## JAVA-OBJECT-B03-C007 — Which members belong to the class rather than each instance?

### Russian Translation

Какие члены принадлежат классу, а не каждому экземпляру?

> [!answer]- Answer
> Static members.

### Explanation

One class-level member is shared per class loader context, while instance fields belong to objects.

### Exam Trap

Access static members through the type to avoid misleading code.

---

## JAVA-OBJECT-B03-C008 — Does a static nested class require an enclosing object?

### Russian Translation

Требует ли статический вложенный класс внешний объект?

> [!answer]- Answer
> No.

### Explanation

It is a static member type and is created with `new Outer.Nested()`.

### Exam Trap

It cannot directly use an outer instance field without an explicit instance.

---

## JAVA-OBJECT-B03-C009 — Does an inner member class carry an enclosing instance?

### Russian Translation

Хранит ли нестатический внутренний класс ссылку на внешний экземпляр?

> [!answer]- Answer
> Yes.

### Explanation

Create it with `outer.new Inner()` outside the enclosing class.

### Exam Trap

`new Outer.Inner()` is not valid for a non-static inner class.

---

## JAVA-OBJECT-B03-C010 — How can an inner class name its enclosing object?

### Russian Translation

Как внутренний класс обращается к внешнему объекту?

> [!answer]- Answer
> With `Outer.this`.

### Explanation

This disambiguates current-object and enclosing-object members.

### Exam Trap

Plain `this` denotes the inner object.

---

## JAVA-OBJECT-B03-C011 — Can an inner class access private members of its enclosing class?

### Russian Translation

Может ли внутренний класс обращаться к private-членам внешнего класса?

> [!answer]- Answer
> Yes.

### Explanation

Nested declarations in the same top-level nest can access each other's private members under Java language rules.

### Exam Trap

Private does not block enclosing/nested access within the same nest.

---

## JAVA-OBJECT-B03-C012 — Can a static nested class directly read an outer instance field?

### Russian Translation

Может ли статический вложенный класс напрямую читать поле внешнего экземпляра?

> [!answer]- Answer
> No, not without an explicit `Outer` reference.

### Explanation

There is no implicit enclosing instance.

### Exam Trap

Static nesting is not the same as inner-class association.

---

## JAVA-OBJECT-B03-C013 — Where is a local class visible?

### Russian Translation

Где виден локальный класс?

> [!answer]- Answer
> Within its declaring block after the declaration.

### Explanation

It follows local declaration scope.

### Exam Trap

Code before the declaration cannot use the local class name.

---

## JAVA-OBJECT-B03-C014 — Which local variables may a local or anonymous class capture?

### Russian Translation

Какие локальные переменные может захватывать локальный или анонимный класс?

> [!answer]- Answer
> Final or effectively final variables.

### Explanation

A variable is effectively final when it is assigned once and never reassigned.

### Exam Trap

Mutation of the referenced object is different from reassignment of the variable.

---

## JAVA-OBJECT-B03-C015 — Can an anonymous class declare a named constructor?

### Russian Translation

Может ли анонимный класс объявить именованный конструктор?

> [!answer]- Answer
> No.

### Explanation

It has no class name to use as a constructor name.

### Exam Trap

It may use instance initializer blocks.

---

## JAVA-OBJECT-B03-C016 — What is the static type of an anonymous-class expression assigned to an interface variable?

### Russian Translation

Каков статический тип анонимного класса при присваивании интерфейсной переменной?

> [!answer]- Answer
> The interface type of the variable.

### Explanation

Members declared only in the anonymous class are not accessible through that interface reference.

### Exam Trap

A local `var` can preserve the anonymous inferred type.

---

## JAVA-OBJECT-B03-C017 — What does `this` mean inside an anonymous class?

### Russian Translation

Что означает `this` внутри анонимного класса?

> [!answer]- Answer
> The anonymous-class instance.

### Explanation

It does not denote the enclosing object.

### Exam Trap

Use `Outer.this` when an enclosing instance must be named.

---

## JAVA-OBJECT-B03-C018 — Can a local class be declared `public`?

### Russian Translation

Можно ли объявить локальный класс `public`?

> [!answer]- Answer
> No.

### Explanation

Access modifiers do not apply to local class declarations.

### Exam Trap

Local visibility is controlled by block scope.

---

## JAVA-OBJECT-B03-C019 — Which access modifiers are allowed on a top-level class?

### Russian Translation

Какие модификаторы доступа разрешены для top-level класса?

> [!answer]- Answer
> `public` or package-private.

### Explanation

Top-level classes cannot be `private` or `protected`.

### Exam Trap

A nested member class may use all member access levels.

---

## JAVA-OBJECT-B03-C020 — How many public top-level classes may a normal source file declare?

### Russian Translation

Сколько public top-level классов обычно можно объявить в одном файле?

> [!answer]- Answer
> At most one.

### Explanation

Its name must match the source file name under ordinary compilation rules.

### Exam Trap

Additional top-level types may be package-private.

---

## JAVA-OBJECT-B03-C021 — Does a source file need to match a package-private top-level class name?

### Russian Translation

Должно ли имя файла совпадать с package-private top-level классом?

> [!answer]- Answer
> Not by the public-type filename rule.

### Explanation

The strict filename match applies to a public top-level type.

### Exam Trap

Build tools may impose additional conventions.

---

## JAVA-OBJECT-B03-C022 — Which class is the root superclass of ordinary Java classes?

### Russian Translation

Какой класс является корневым суперклассом обычных Java-классов?

> [!answer]- Answer
> `java.lang.Object`.

### Explanation

Classes without an explicit superclass extend `Object`, except `Object` itself.

### Exam Trap

Interfaces do not extend `Object`, though implementing objects have Object methods.

---

## JAVA-OBJECT-B03-C023 — What does reference `==` compare?

### Russian Translation

Что сравнивает `==` для ссылок?

> [!answer]- Answer
> Reference identity.

### Explanation

It checks whether two references designate the same object or are both null.

### Exam Trap

Use `equals` for value semantics when the type defines them.

---

## JAVA-OBJECT-B03-C024 — Can two distinct objects be equal according to `equals`?

### Russian Translation

Могут ли два разных объекта быть равны по `equals`?

> [!answer]- Answer
> Yes.

### Explanation

A type can define value equality independent of identity.

### Exam Trap

`==` can still be false for the same two objects.

---

## JAVA-OBJECT-B03-C025 — Does losing one alias make an object unreachable?

### Russian Translation

Делает ли потеря одной ссылки объект недостижимым?

> [!answer]- Answer
> Only if no other reachable references remain.

### Explanation

Reachability is determined across all roots and object paths.

### Exam Trap

Collections, fields and static variables may keep the object alive.

---
