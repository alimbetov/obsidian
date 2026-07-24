---
type: atomic-concept
concept_id: JAVA-B03-C03
domain: java
route: JAVA-B03
subdomain: initialization-order
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
certifications:
  - java-1Z0-829
  - java-1Z0-830
prerequisites:
  - JAVA-B01
  - JAVA-B02
tags:
  - java
  - object-model
  - active-recall
---

# Java Fields, Initializers and Constructor Order

> [!summary]
> Initialization is a deterministic superclass-first pipeline with default values, textual initializer order and constructor chaining.

## Default values versus local variables

Fields receive default values before explicit initialization. Local variables do not.

```text
numeric fields  0 / 0.0
char field      \u0000
boolean field   false
reference field null
```

```java
int field;
void run() {
    int local;
    System.out.println(field); // 0
    // System.out.println(local); // not definitely assigned
}
```

## Class initialization

Static fields and static initializer blocks execute once in textual order when the class is initialized.

```java
static int a = mark("a");
static { mark("block"); }
static int b = mark("b");
```

## Instance initialization

For each constructed object:

```text
superclass instance initialization
→ current-class instance fields and initializer blocks in textual order
→ current constructor body
```

Constructor delegation with `this(...)` eventually reaches a constructor that invokes `super(...)`.

## Constructor invocation

When no explicit constructor invocation is written, the compiler attempts to insert `super()`.

```java
class Parent { Parent(int x) {} }
class Child extends Parent {
    // Child() {} // fails: Parent has no no-arg constructor
    Child() { super(1); }
}
```

Declaring any constructor suppresses compiler generation of the default no-argument constructor.

## Blank final fields

A blank final instance field must be definitely assigned exactly once by every constructor path. A blank static final field must be assigned in a static initializer path.

## Dynamic dispatch hazard

Calling an overridable method from a constructor dispatches to the runtime subclass before the subclass's fields have completed initialization.

```java
class Parent {
    Parent() { print(); }
    void print() {}
}
class Child extends Parent {
    int value = 10;
    @Override void print() { System.out.println(value); } // prints 0 during super construction
}
```

## Active recall

1. Do fields receive their explicit initializer before or after `super()` completes?
2. What constructor is inserted when no explicit `this` or `super` appears?
3. Why is invoking an overridable method from a constructor risky?

## Route navigation

- **Previous:** [[10_CONCEPTS/Java/Object Model/Java Nested Local and Anonymous Classes]]
- **Next:** [[10_CONCEPTS/Java/Object Model/Java Overloading Varargs and Method Selection]]
- **Roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- **Cards:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03A Object Lifecycle Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03B Initialization and Scope Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03C Inheritance and Interfaces Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03D Records Sealed and Patterns Cards]]
- **Drills:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- **Lab:** [[50_LABS/Java/JAVA-B03/README]]
- **Sources:** [[98_SOURCES/Java SE 17 1Z0-829 Sources]], [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
- **Dashboard:** [[00_HOME/Java Learning Dashboard]]
