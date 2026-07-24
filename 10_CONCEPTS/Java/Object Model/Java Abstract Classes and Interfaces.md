---
type: atomic-concept
concept_id: JAVA-B03-C07
domain: java
route: JAVA-B03
subdomain: abstract-interfaces
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

# Java Abstract Classes and Interfaces

> [!summary]
> Abstract classes share state and implementation; interfaces define contracts with abstract, default, static and private methods under specific inheritance rules.

## Abstract classes

An abstract class cannot be instantiated directly. It may contain constructors, fields, concrete methods and abstract methods. A concrete subclass must implement every inherited abstract method.

An abstract method has no body and cannot be `private`, `static` or `final` because those modifiers conflict with overriding.

## Interface members

Interface fields are implicitly `public static final`. A field declaration therefore needs an initializer.

Ordinary interface methods without a body are implicitly `public abstract`.

```java
interface Service {
    void run();
    default String name() { return "service"; }
    static Service noop() { return () -> {}; }
    private String prefix() { return "S:"; }
}
```

Private interface methods must have a body and exist to share implementation among default or other private methods. Static interface methods are called through the interface type and are not inherited as instance methods.

## Default-method resolution

A concrete class method wins over an interface default. A more specific subinterface default wins over an inherited parent-interface default. Unrelated conflicting defaults require an explicit override.

```java
interface A { default String id() { return "A"; } }
interface B { default String id() { return "B"; } }
class C implements A, B {
    public String id() { return A.super.id() + B.super.id(); }
}
```

## Functional interfaces

A functional interface has one abstract method after excluding methods matching public `Object` methods and accounting for inherited overrides. Default, static and private methods do not add abstract-method count.

`@FunctionalInterface` asks the compiler to enforce the rule.

## Active recall

1. Are interface static methods inherited by implementing objects?
2. What happens when two unrelated interfaces provide the same default signature?
3. Do default methods count against functional-interface status?

## Route navigation

- **Previous:** [[10_CONCEPTS/Java/Object Model/Java Inheritance Overriding Hiding and Polymorphism]]
- **Next:** [[10_CONCEPTS/Java/Object Model/Java Records]]
- **Roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- **Cards:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03A Object Lifecycle Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03B Initialization and Scope Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03C Inheritance and Interfaces Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03D Records Sealed and Patterns Cards]]
- **Drills:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- **Lab:** [[50_LABS/Java/JAVA-B03/README]]
- **Sources:** [[98_SOURCES/Java SE 17 1Z0-829 Sources]], [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
- **Dashboard:** [[00_HOME/Java Learning Dashboard]]
