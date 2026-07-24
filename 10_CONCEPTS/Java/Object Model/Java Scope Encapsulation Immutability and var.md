---
type: atomic-concept
concept_id: JAVA-B03-C05
domain: java
route: JAVA-B03
subdomain: scope-encapsulation-var
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

# Java Scope, Encapsulation, Immutability and var

> [!summary]
> Scope determines name visibility, access control protects type boundaries, immutability requires object-graph discipline, and `var` changes only local static-type spelling.

## Scope and shadowing

A local variable exists only within its declaring block after the declaration. Parameters are in scope throughout the method body. A local variable may shadow a field.

```java
class Account {
    private int balance;
    void setBalance(int balance) {
        this.balance = balance;
    }
}
```

## Access control

```text
private     declaring top-level nest
package     same package
protected   same package plus subclass rules
public      accessible where the type itself is accessible
```

A top-level type may be `public` or package-private, not `private` or `protected`.

## Encapsulation

Expose invariants through behavior, not writable representation. Returning a mutable internal collection leaks encapsulation unless a defensive copy or unmodifiable view is appropriate.

## Immutability

A robust immutable class usually has:

```text
final or effectively closed type
private final fields
validated construction
no mutators
no escaping mutable internals
defensive copies at input and output boundaries
```

`final` prevents reassignment of a reference; it does not freeze the referenced object.

## Local-variable type inference

`var` is allowed for local variables with an initializer, including loop variables. It is not allowed for fields, method parameters or return types.

```java
var count = 1;          // int
var names = new StringBuilder();
```

`var value = null` has no inferable type. A lambda or method reference also requires a target type and cannot initialize a bare `var` directly.

The inferred type is a normal static type. `var` does not make Java dynamically typed.

## Anonymous-type inference

A local `var` can retain an anonymous class's inferred type, allowing access to members declared only by that anonymous class within the local scope.

## Active recall

1. Does `final List<?>` make the list immutable?
2. Where may `var` appear?
3. Why are defensive copies needed for immutable classes containing mutable values?

## Route navigation

- **Previous:** [[10_CONCEPTS/Java/Object Model/Java Overloading Varargs and Method Selection]]
- **Next:** [[10_CONCEPTS/Java/Object Model/Java Inheritance Overriding Hiding and Polymorphism]]
- **Roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- **Cards:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03A Object Lifecycle Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03B Initialization and Scope Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03C Inheritance and Interfaces Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03D Records Sealed and Patterns Cards]]
- **Drills:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- **Lab:** [[50_LABS/Java/JAVA-B03/README]]
- **Sources:** [[98_SOURCES/Java SE 17 1Z0-829 Sources]], [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
- **Dashboard:** [[00_HOME/Java Learning Dashboard]]
