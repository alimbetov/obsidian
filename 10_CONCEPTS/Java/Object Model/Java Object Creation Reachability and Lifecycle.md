---
type: atomic-concept
concept_id: JAVA-B03-C01
domain: java
route: JAVA-B03
subdomain: object-lifecycle
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

# Java Object Creation, Reachability and Lifecycle

> [!summary]
> Object creation has distinct compile-time, allocation, initialization and reachability phases. A reference variable is not the object, and losing one reference does not necessarily make the object unreachable.

## Creation path

```text
validate constructor invocation
→ allocate storage and apply default field values
→ execute superclass initialization
→ execute current-class field initializers and instance blocks
→ execute constructor body
→ return the reference
```

`new Type(args)` both creates an object and invokes a constructor. Constructors initialize already allocated storage; they do not have a return type.

```java
Account a = new Account(10);
Account b = a;
```

`a` and `b` refer to the same object. The assignment does not copy the object.

## Identity, equality and aliases

Object identity answers whether two references designate the same object. `==` compares reference identity. `equals` may define value equality.

```java
StringBuilder a = new StringBuilder("x");
StringBuilder b = a;
b.append("y");
System.out.println(a); // xy
```

## Reachability

A local variable can leave scope while the object remains reachable through a field, collection, static variable or another object. Conversely, assigning `null` to one alias does not make the object collectible when another alias still exists.

```java
Object a = new Object();
Object b = a;
a = null;        // object remains reachable through b
```

Garbage-collection eligibility is not a promise of immediate collection. Exact timing must never be used in deterministic exam output.

## `null`

`null` is a reference value that denotes no object. Member access through `null` compiles when the static type permits the member, then throws `NullPointerException` at runtime.

```java
String text = null;
System.out.println(text.length()); // NPE
```

Static members should be accessed through the type. Accessing a static member through a null reference may not dereference an object, but it is misleading and should not be used as normal style.

## Active recall

1. Does assigning one reference to another copy the object?
2. Can an object remain reachable after a local variable leaves scope?
3. Does garbage-collection eligibility guarantee collection before program exit?

## Route navigation

- **Previous:** [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]]
- **Next:** [[10_CONCEPTS/Java/Object Model/Java Nested Local and Anonymous Classes]]
- **Roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- **Cards:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03A Object Lifecycle Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03B Initialization and Scope Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03C Inheritance and Interfaces Cards]], [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03D Records Sealed and Patterns Cards]]
- **Drills:** [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- **Lab:** [[50_LABS/Java/JAVA-B03/README]]
- **Sources:** [[98_SOURCES/Java SE 17 1Z0-829 Sources]], [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
- **Dashboard:** [[00_HOME/Java Learning Dashboard]]
