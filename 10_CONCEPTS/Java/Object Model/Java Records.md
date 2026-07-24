---
type: atomic-concept
concept_id: JAVA-B03-C08
domain: java
route: JAVA-B03
subdomain: records
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

# Java Records

> [!summary]
> A record is a final nominal class whose state description generates private final fields, accessors, a canonical constructor and value-oriented Object methods.

## State description

```java
record Point(int x, int y) {}
```

For each component, the record has a private final field and a public accessor named exactly like the component. The compiler also provides a canonical constructor plus `equals`, `hashCode` and `toString` based on the record components.

Records are implicitly final and extend `java.lang.Record`. They may implement interfaces but cannot extend another class explicitly.

## Canonical and compact constructors

An explicit canonical constructor has the component parameter list. A compact constructor omits the parameter list:

```java
record Money(int amount, String currency) {
    Money {
        if (amount < 0) throw new IllegalArgumentException();
        currency = currency.toUpperCase();
    }
}
```

In a compact constructor, assignments to component fields are inserted after the body. Reassigning a parameter changes the value later assigned. Direct assignment to a component field in the compact body is forbidden.

## Members

Records may declare static fields, static methods, instance methods and nested types. They may not declare additional instance fields outside the component state.

An accessor may be explicitly declared but must preserve the component's return type and accessibility contract.

## Equality

Record equality is structural for instances of the same record class and component values. It is not automatically interchangeable with another record having identical component types.

## Shallow immutability

Record component fields are final, but referenced objects may be mutable. Defensive copies are still required when deep immutability is needed.

## Active recall

1. Can a record declare an additional instance field?
2. When are compact-constructor component fields assigned?
3. Does a record make a mutable component deeply immutable?

## Route navigation

- **Previous:** [[10_CONCEPTS/Java/Object Model/Java Abstract Classes and Interfaces]]
- **Next:** [[10_CONCEPTS/Java/Object Model/Java Enums]]
- **Roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- **Cards:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03A Object Lifecycle Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03B Initialization and Scope Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03C Inheritance and Interfaces Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03D Records Sealed and Patterns Cards]]
- **Drills:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- **Lab:** [[50_LABS/Java/JAVA-B03/README]]
- **Sources:** [[98_SOURCES/Java SE 17 1Z0-829 Sources]], [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
- **Dashboard:** [[00_HOME/Java Learning Dashboard]]
