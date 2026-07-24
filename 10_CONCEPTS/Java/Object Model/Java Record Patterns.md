---
type: atomic-concept
concept_id: JAVA-B03-C11
domain: java
route: JAVA-B03
subdomain: record-patterns
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

# Java Record Patterns

> [!summary]
> Java 21 record patterns test a record type and deconstruct its components into nested component patterns.

## Version boundary

Record patterns are final in Java 21 under JEP 440. They are not part of the normal Java 17 compilation baseline.

## Deconstruction

```java
record Point(int x, int y) {}

if (value instanceof Point(int x, int y)) {
    System.out.println(x + y);
}
```

The pattern first checks that the value is a non-null compatible `Point`, then invokes component accessors conceptually and matches each component pattern.

## Component types and arity

The number of component patterns must match the record header. Component patterns must be type-compatible with the declared component types.

```java
record Name(String value) {}
// case Name(Integer x) -> ... // incompatible
```

## `var` component patterns

`var` infers the component pattern's static type:

```java
case Point(var x, var y) -> x + y
```

This is pattern inference, not dynamic typing.

## Null behavior

A record pattern does not match `null`. Null must be handled separately in a switch when required.

## Record patterns in switch

```java
return switch (value) {
    case Point(int x, int y) -> x + y;
    case null -> 0;
    default -> -1;
};
```

Dominance and exhaustiveness rules from pattern switch still apply.

## Active recall

1. Do record patterns match null?
2. Must pattern arity equal record component count?
3. Which Java release is the normal final baseline for record patterns?

## Route navigation

- **Previous:** [[10_CONCEPTS/Java/Object Model/Java Sealed Types]]
- **Next:** [[10_CONCEPTS/Java/Object Model/Java Nested Patterns and Exhaustiveness]]
- **Roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- **Cards:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03A Object Lifecycle Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03B Initialization and Scope Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03C Inheritance and Interfaces Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03D Records Sealed and Patterns Cards]]
- **Drills:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- **Lab:** [[50_LABS/Java/JAVA-B03/README]]
- **Sources:** [[98_SOURCES/Java SE 17 1Z0-829 Sources]], [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
- **Dashboard:** [[00_HOME/Java Learning Dashboard]]
