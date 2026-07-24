---
type: certification-batch
domain: java
subdomain: initialization-scope-var
batch: JAVA-INIT-B03
status: published
card_count: 30
java_versions:
  - 17
  - 21
objectives:
  - JAVA-B03
  - JAVA21-3.2
  - JAVA21-3.4
tags:
  - java
  - object-model
  - active-recall
---

# JAVA-B03B — Initialization, Scope and Immutability Cards

## Route navigation

- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- [[10_CONCEPTS/Java/Object Model/Java Fields Initializers and Constructor Order]]
- [[10_CONCEPTS/Java/Object Model/Java Scope Encapsulation Immutability and var]]
- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- [[50_LABS/Java/JAVA-B03/README]]

---

## JAVA-INIT-B03-C001 — What default value does an `int` field receive?

### Russian Translation

Какое значение по умолчанию получает поле `int`?

> [!answer]- Answer
> `0`.

### Explanation

Fields are default-initialized before explicit initializers run.

### Exam Trap

Local variables do not receive default values.

---

## JAVA-INIT-B03-C002 — What default value does a reference field receive?

### Russian Translation

Какое значение по умолчанию получает ссылочное поле?

> [!answer]- Answer
> `null`.

### Explanation

Default initialization precedes constructor execution.

### Exam Trap

Reading the field is legal; dereferencing null can fail.

---

## JAVA-INIT-B03-C003 — Do local variables receive default values?

### Russian Translation

Получают ли локальные переменные значения по умолчанию?

> [!answer]- Answer
> No.

### Explanation

They must be definitely assigned before use.

### Exam Trap

A declaration alone does not make a local readable.

---

## JAVA-INIT-B03-C004 — In what order do static field initializers and static blocks run?

### Russian Translation

В каком порядке выполняются static-поля и static-блоки?

> [!answer]- Answer
> In textual order during class initialization.

### Explanation

Each initializer observes earlier completed static initialization and default values of later fields.

### Exam Trap

Reordering declarations can change output.

---

## JAVA-INIT-B03-C005 — When do instance field initializers run relative to the superclass constructor?

### Russian Translation

Когда выполняются инициализаторы полей экземпляра относительно конструктора суперкласса?

> [!answer]- Answer
> After superclass initialization completes and before the current constructor body.

### Explanation

The current object's storage already has default values during the superclass constructor.

### Exam Trap

Subclass fields may still contain defaults when an overridden method is called from `super`.

---

## JAVA-INIT-B03-C006 — What is the superclass-first instance initialization order?

### Russian Translation

Каков порядок инициализации экземпляра с учётом суперкласса?

> [!answer]- Answer
> Superclass fields/blocks/constructor, then subclass fields/blocks, then subclass constructor body.

### Explanation

Each class's fields and instance blocks run in textual order.

### Exam Trap

Static initialization is a separate once-per-class phase.

---

## JAVA-INIT-B03-C007 — What invocation is inserted when a constructor has no explicit `this` or `super` call?

### Russian Translation

Какой вызов вставляется, если конструктор не содержит явного `this` или `super`?

> [!answer]- Answer
> `super()`.

### Explanation

Compilation succeeds only if the superclass has an accessible no-arg constructor.

### Exam Trap

The compiler does not invent superclass arguments.

---

## JAVA-INIT-B03-C008 — Does declaring any constructor suppress the default constructor?

### Russian Translation

Подавляет ли объявление любого конструктора генерацию default-конструктора?

> [!answer]- Answer
> Yes.

### Explanation

The compiler generates a default no-arg constructor only when no constructor is declared.

### Exam Trap

A parameterized constructor removes the implicit no-arg option.

---

## JAVA-INIT-B03-C009 — Can one constructor invoke both `this(...)` and `super(...)` directly?

### Russian Translation

Может ли один конструктор напрямую вызвать и `this(...)`, и `super(...)`?

> [!answer]- Answer
> No.

### Explanation

At most one explicit constructor invocation begins the constructor body.

### Exam Trap

A `this(...)` chain eventually reaches a constructor that invokes `super(...)`.

---

## JAVA-INIT-B03-C010 — Where must an explicit constructor invocation appear?

### Russian Translation

Где должен находиться явный вызов конструктора?

> [!answer]- Answer
> As the first constructor statement under Java 17 and Java 21 baseline rules.

### Explanation

Ordinary statements cannot precede `this(...)` or `super(...)` in these releases.

### Exam Trap

Do not apply later flexible-constructor-body rules to these exam baselines.

---

## JAVA-INIT-B03-C011 — What must every blank final instance field satisfy?

### Russian Translation

Что требуется для каждого blank final поля экземпляра?

> [!answer]- Answer
> Definite assignment exactly once on every constructor path.

### Explanation

Assignment may occur in an instance initializer or constructor path.

### Exam Trap

A path that omits assignment does not compile.

---

## JAVA-INIT-B03-C012 — Where is a blank static final field assigned?

### Russian Translation

Где присваивается blank static final поле?

> [!answer]- Answer
> In a static initializer path.

### Explanation

It belongs to class initialization, not object construction.

### Exam Trap

An instance constructor cannot initialize it.

---

## JAVA-INIT-B03-C013 — What happens when a constructor calls an overridable method?

### Russian Translation

Что происходит, когда конструктор вызывает переопределяемый метод?

> [!answer]- Answer
> Runtime dispatch can invoke the subclass override before subclass initialization completes.

### Explanation

Subclass fields may still contain default values.

### Exam Trap

This is legal but hazardous.

---

## JAVA-INIT-B03-C014 — Do instance initializer blocks run for every constructor path?

### Russian Translation

Выполняются ли instance initializer blocks для каждого пути конструктора?

> [!answer]- Answer
> Yes, once after superclass construction and before the selected constructor body, excluding delegated duplicates.

### Explanation

A `this(...)` chain does not repeat the same class's instance initializers for each constructor in the chain.

### Exam Trap

Trace the actual object initialization, not each source constructor separately.

---

## JAVA-INIT-B03-C015 — Can a constructor return a value?

### Russian Translation

Может ли конструктор возвращать значение?

> [!answer]- Answer
> No.

### Explanation

Constructors have no return type, not even `void`.

### Exam Trap

A declaration with `void` is a method, not a constructor.

---

## JAVA-INIT-B03-C016 — What does `this(...)` do?

### Russian Translation

Что делает `this(...)` в конструкторе?

> [!answer]- Answer
> Delegates to another constructor in the same class.

### Explanation

It centralizes initialization and must not create a recursive constructor cycle.

### Exam Trap

Direct or indirect recursive delegation is a compile-time error.

---

## JAVA-INIT-B03-C017 — What is the scope of a block local variable?

### Russian Translation

Какова область видимости локальной переменной блока?

> [!answer]- Answer
> From its declaration to the end of the declaring block, subject to nested shadowing rules.

### Explanation

The variable is not visible before its declaration.

### Exam Trap

Lifetime and scope are related but not identical concepts.

---

## JAVA-INIT-B03-C018 — How does a parameter access a shadowed field with the same name?

### Russian Translation

Как параметр обращается к скрытому полю с тем же именем?

> [!answer]- Answer
> Use `this.field` for the instance field.

### Explanation

The simple name resolves to the nearest local or parameter declaration.

### Exam Trap

For a static field, prefer `Type.field`.

---

## JAVA-INIT-B03-C019 — What does `private` primarily enforce?

### Russian Translation

Что в первую очередь обеспечивает `private`?

> [!answer]- Answer
> Access limited to the declaring top-level nest under Java language access rules.

### Explanation

It supports encapsulation by preventing arbitrary external member access.

### Exam Trap

Reflection and serialization are separate runtime mechanisms.

---

## JAVA-INIT-B03-C020 — Does returning a mutable internal list preserve immutability?

### Russian Translation

Сохраняется ли неизменяемость при возврате внутреннего mutable-списка?

> [!answer]- Answer
> Usually no.

### Explanation

The caller can mutate the object's representation unless a defensive copy or safe view is returned.

### Exam Trap

An unmodifiable view may still reflect internal mutations.

---

## JAVA-INIT-B03-C021 — Does a final reference make its object immutable?

### Russian Translation

Делает ли final-ссылка объект неизменяемым?

> [!answer]- Answer
> No.

### Explanation

`final` prevents reassigning the variable, not mutating the object.

### Exam Trap

The referenced collection may still change.

---

## JAVA-INIT-B03-C022 — Why copy mutable constructor arguments in an immutable class?

### Russian Translation

Зачем копировать mutable-аргументы конструктора immutable-класса?

> [!answer]- Answer
> To prevent callers from retaining an alias that can mutate internal state.

### Explanation

Input defensive copying closes an ownership leak.

### Exam Trap

Output copying may also be required.

---

## JAVA-INIT-B03-C023 — Where may `var` be used?

### Russian Translation

Где можно использовать `var`?

> [!answer]- Answer
> For local variables with an initializer, including supported loop variables.

### Explanation

It cannot replace field, parameter or return-type declarations.

### Exam Trap

`var` is a reserved type name in this context, not dynamic typing.

---

## JAVA-INIT-B03-C024 — Does `var` require an initializer?

### Russian Translation

Требует ли `var` инициализатор?

> [!answer]- Answer
> Yes.

### Explanation

The compiler needs the initializer to infer the local variable's static type.

### Exam Trap

`var x;` does not compile.

---

## JAVA-INIT-B03-C025 — Can `var` be initialized with plain `null`?

### Russian Translation

Можно ли инициализировать `var` значением `null`?

> [!answer]- Answer
> No.

### Explanation

There is no standalone static type to infer from the null literal.

### Exam Trap

A cast such as `(String) null` supplies a type.

---

## JAVA-INIT-B03-C026 — Can `var` declare a method parameter?

### Russian Translation

Можно ли использовать `var` для параметра метода?

> [!answer]- Answer
> No for ordinary method parameter type inference.

### Explanation

Local-variable `var` does not generalize to method signatures.

### Exam Trap

Lambda parameters may use `var` under separate lambda syntax rules.

---

## JAVA-INIT-B03-C027 — What type does `var number = 1` infer?

### Russian Translation

Какой тип выводится для `var number = 1`?

> [!answer]- Answer
> Primitive `int`.

### Explanation

Inference uses the initializer's compile-time type.

### Exam Trap

The variable does not later accept arbitrary unrelated types.

---

## JAVA-INIT-B03-C028 — Can a bare lambda initialize `var`?

### Russian Translation

Может ли lambda без target type инициализировать `var`?

> [!answer]- Answer
> No.

### Explanation

A lambda requires a target functional-interface type.

### Exam Trap

Use an explicit interface type or cast.

---

## JAVA-INIT-B03-C029 — Can one `var` declaration contain multiple declarators?

### Russian Translation

Может ли одно объявление `var` содержать несколько переменных?

> [!answer]- Answer
> No.

### Explanation

`var a = 1, b = 2;` is invalid.

### Exam Trap

Declare each inferred local separately.

---

## JAVA-INIT-B03-C030 — What happens to later static fields before their explicit initializers run?

### Russian Translation

Какое состояние имеют последующие static-поля до своих инициализаторов?

> [!answer]- Answer
> They hold default values.

### Explanation

Earlier static initialization code may observe those defaults, subject to forward-reference rules.

### Exam Trap

Method calls can expose default-before-explicit initialization effects.

---
