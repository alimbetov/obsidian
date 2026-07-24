---
type: atomic-concept
concept_id: JAVA-B03-C06
domain: java
route: JAVA-B03
subdomain: inheritance-polymorphism
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

# Java Inheritance, Overriding, Hiding and Polymorphism

> [!summary]
> Instance methods use runtime dispatch; fields, static methods and overload selection are determined by compile-time types and declarations.

## Upcasting and runtime dispatch

```java
Parent ref = new Child();
ref.run();
```

The compiler validates members against `Parent`; an overridden instance method executes from `Child` at runtime.

## Overriding contract

An overriding method has the same signature and a compatible return type. It may widen access, return a covariant subtype and declare fewer or narrower checked exceptions. It may not reduce access or override a final method.

Private methods are not inherited and therefore are not overridden.

## Static method and field hiding

Static methods are hidden, not overridden. Fields are also selected from the reference expression's compile-time type.

```java
Parent ref = new Child();
System.out.println(ref.field); // Parent field
ref.staticMethod();            // Parent static method
ref.instanceMethod();          // Child override
```

## Casts

Upcasts are implicit. Downcasts require an explicit cast and may throw `ClassCastException` when the runtime object is incompatible.

```java
Parent p = new Child();
Child c = (Child) p;
```

The compiler checks whether the types could be related; the runtime checks the actual object.

## `instanceof`

`null instanceof Type` is false. A type pattern combines the test and cast:

```java
if (value instanceof String text) {
    System.out.println(text.length());
}
```

Flow analysis determines where the pattern variable is definitely matched.

## `super`

`super.method()` invokes the superclass implementation from within a subclass and bypasses normal virtual selection for that invocation. It cannot be used from a static context.

## Active recall

1. Which members use runtime dispatch?
2. Can an overriding method throw a broader checked exception?
3. What two checks occur for a downcast?

## Route navigation

- **Previous:** [[10_CONCEPTS/Java/Object Model/Java Scope Encapsulation Immutability and var]]
- **Next:** [[10_CONCEPTS/Java/Object Model/Java Abstract Classes and Interfaces]]
- **Roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- **Cards:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03A Object Lifecycle Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03B Initialization and Scope Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03C Inheritance and Interfaces Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03D Records Sealed and Patterns Cards]]
- **Drills:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- **Lab:** [[50_LABS/Java/JAVA-B03/README]]
- **Sources:** [[98_SOURCES/Java SE 17 1Z0-829 Sources]], [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
- **Dashboard:** [[00_HOME/Java Learning Dashboard]]
