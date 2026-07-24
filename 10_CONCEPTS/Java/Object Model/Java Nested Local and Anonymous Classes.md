---
type: atomic-concept
concept_id: JAVA-B03-C02
domain: java
route: JAVA-B03
subdomain: nested-classes
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

# Java Nested, Local and Anonymous Classes

> [!summary]
> Nested type categories differ in outer-instance requirements, captured state, member restrictions and creation syntax.

## Static nested class

A static nested class is a static member of the enclosing type. It does not carry an implicit enclosing instance.

```java
class Outer {
    static int shared = 1;
    int value = 2;
    static class Nested {
        int readShared() { return shared; }
        // int readValue() { return value; } // no outer instance
    }
}
Outer.Nested n = new Outer.Nested();
```

## Inner class

A non-static member class is associated with an enclosing instance.

```java
class Outer {
    private int value = 7;
    class Inner {
        int read() { return value; }
    }
}
Outer outer = new Outer();
Outer.Inner inner = outer.new Inner();
```

Inside the inner class, `Outer.this` names the enclosing instance. Java permits enclosing and nested classes to access each other's private members.

## Local class

A local class is declared inside a block and is visible only after its declaration within that block. It may capture local variables only when they are final or effectively final.

```java
int limit = 10;
class Checker { boolean ok(int x) { return x < limit; } }
```

Reassigning `limit` would make the capture invalid.

## Anonymous class

An anonymous class combines declaration and instantiation in one expression.

```java
Runnable r = new Runnable() {
    @Override public void run() { System.out.println("run"); }
};
```

It has no declared constructor name, but it may use instance initializer blocks. Its static type is the declared superclass or interface type unless `var` preserves the anonymous type locally.

## Shadowing

A nested class can shadow outer names. Qualify deliberately:

```java
this.value        // current object
Outer.this.value  // enclosing object
Outer.value       // static member
```

## Active recall

1. Which nested type requires `outer.new Inner()`?
2. What makes a captured local variable effectively final?
3. Can an anonymous class declare a constructor?

## Route navigation

- **Previous:** [[10_CONCEPTS/Java/Object Model/Java Object Creation Reachability and Lifecycle]]
- **Next:** [[10_CONCEPTS/Java/Object Model/Java Fields Initializers and Constructor Order]]
- **Roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- **Cards:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03A Object Lifecycle Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03B Initialization and Scope Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03C Inheritance and Interfaces Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03D Records Sealed and Patterns Cards]]
- **Drills:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- **Lab:** [[50_LABS/Java/JAVA-B03/README]]
- **Sources:** [[98_SOURCES/Java SE 17 1Z0-829 Sources]], [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
- **Dashboard:** [[00_HOME/Java Learning Dashboard]]
