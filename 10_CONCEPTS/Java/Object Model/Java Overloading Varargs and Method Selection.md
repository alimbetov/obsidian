---
type: atomic-concept
concept_id: JAVA-B03-C04
domain: java
route: JAVA-B03
subdomain: overloading-varargs
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

# Java Overloading, Varargs and Method Selection

> [!summary]
> Overload selection is compile-time resolution based on the argument expressions and an ordered set of applicable-conversion phases.

## Overload versus override

Overloading selects among different signatures at compile time. Overriding selects a runtime implementation for one inherited instance-method signature.

## Applicability phases

A reliable exam approximation is:

```text
1. exact match and primitive/reference widening
2. boxing or unboxing, with allowed widening after unboxing
3. variable arity
```

Earlier phases win; Java does not freely chain arbitrary conversions.

```java
static String pick(long x)    { return "long"; }
static String pick(Integer x) { return "Integer"; }
static String pick(int... x)  { return "varargs"; }

pick(1); // long: primitive widening is considered before boxing
```

`Integer` can unbox to `int` and widen to `long`. An `int` cannot widen to `long` and then box to `Long` for method invocation.

## Most specific method

When multiple overloads are applicable in the same phase, the compiler selects the most specific. Unrelated reference overloads can make `null` ambiguous.

```java
void call(String x) {}
void call(Integer x) {}
// call(null); // ambiguous
```

## Varargs

A varargs parameter is an array parameter at runtime.

```java
void log(String prefix, int... values) {}
```

Only the final parameter may be variable arity, and a method may have at most one varargs parameter. A caller can pass separate arguments or an existing array. Passing a typed `null` array differs from passing one null element.

## Signature rules

Return type alone cannot distinguish overloads. Parameter types, order or arity must differ. Generic erasure can also make two source-level declarations clash.

## Active recall

1. Which is considered first: primitive widening or boxing?
2. Why can `call(null)` be ambiguous?
3. What runtime type backs a varargs parameter?

## Route navigation

- **Previous:** [[10_CONCEPTS/Java/Object Model/Java Fields Initializers and Constructor Order]]
- **Next:** [[10_CONCEPTS/Java/Object Model/Java Scope Encapsulation Immutability and var]]
- **Roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- **Cards:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03A Object Lifecycle Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03B Initialization and Scope Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03C Inheritance and Interfaces Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03D Records Sealed and Patterns Cards]]
- **Drills:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- **Lab:** [[50_LABS/Java/JAVA-B03/README]]
- **Sources:** [[98_SOURCES/Java SE 17 1Z0-829 Sources]], [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
- **Dashboard:** [[00_HOME/Java Learning Dashboard]]
