---
type: learning-route
route: JAVA-B03
domain: java
subdomain: object-model-record-patterns
status: lab-proven
evidence_status: jdk17-jdk21-ci-pass
verified_at: 2026-07-24
ci_workflow: JAVA-B03 Proof
ci_run_id: 30097320887
java_versions:
  - 17
  - 21
certifications:
  - java-1Z0-829
  - java-1Z0-830
objectives:
  - JAVA-B03
  - JAVA21-3.1
  - JAVA21-3.2
  - JAVA21-3.3
  - JAVA21-3.4
  - JAVA21-3.5
  - JAVA21-3.6
  - JAVA21-3.7
base_card_target: 115
drill_card_target: 35
published_base_cards: 115
published_drills: 35
atomic_note_count: 12
tags:
  - java
  - object-model
  - records
  - sealed-types
  - record-patterns
---

# JAVA-B03 — Object Model, Records and Record Patterns

> [!summary]
> `JAVA-B03` builds the object model required by exceptions, collections, streams and concurrency. Shared Java 17/21 semantics cover construction, overloads, inheritance, interfaces, records, enums and sealed types; Java 21 adds final record patterns and nested deconstruction. Dedicated CI run `30097320887` passed both release lanes and every intended negative source.

## Prerequisites

- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]

## Objective mapping

| Objective | Scope | Evidence |
|---|---|---|
| `JAVA-B03` | Java 17 object model | shared concepts, 90+ shared cards, shared proof lane |
| `JAVA21-3.1` | objects, nested types, lifecycle | object cards + lifecycle concepts |
| `JAVA21-3.2` | classes, records, fields, constructors, initialization | initialization cards + proof |
| `JAVA21-3.3` | overloading and varargs | inheritance cards + overload proof |
| `JAVA21-3.4` | scope, encapsulation, immutability, `var` | initialization/scope cards |
| `JAVA21-3.5` | inheritance, sealed types, records and patterns | inheritance/types cards + both lanes |
| `JAVA21-3.6` | interfaces and functional interfaces | inheritance/interfaces cards |
| `JAVA21-3.7` | enums | types cards + shared proof |

## Route artifacts

| Role | Artifact |
|---|---|
| Canonical hub | [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]] |
| Object lifecycle cards | [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03A Object Lifecycle Cards]] |
| Initialization/scope cards | [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03B Initialization and Scope Cards]] |
| Inheritance/interfaces cards | [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03C Inheritance and Interfaces Cards]] |
| Records/sealed/pattern cards | [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03D Records Sealed and Patterns Cards]] |
| Drills | [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]] |
| Executable lab | [[50_LABS/Java/JAVA-B03/README]] |
| Canvas | [[01_MAPS/Java Object Model and Record Patterns Map.canvas]] |
| Sources | [[98_SOURCES/Java SE 17 1Z0-829 Sources]], [[98_SOURCES/Java SE 21 1Z0-830 Sources]] |

## Atomic concept sequence

1. [[10_CONCEPTS/Java/Object Model/Java Object Creation Reachability and Lifecycle]]
2. [[10_CONCEPTS/Java/Object Model/Java Nested Local and Anonymous Classes]]
3. [[10_CONCEPTS/Java/Object Model/Java Fields Initializers and Constructor Order]]
4. [[10_CONCEPTS/Java/Object Model/Java Overloading Varargs and Method Selection]]
5. [[10_CONCEPTS/Java/Object Model/Java Scope Encapsulation Immutability and var]]
6. [[10_CONCEPTS/Java/Object Model/Java Inheritance Overriding Hiding and Polymorphism]]
7. [[10_CONCEPTS/Java/Object Model/Java Abstract Classes and Interfaces]]
8. [[10_CONCEPTS/Java/Object Model/Java Records]]
9. [[10_CONCEPTS/Java/Object Model/Java Enums]]
10. [[10_CONCEPTS/Java/Object Model/Java Sealed Types]]
11. [[10_CONCEPTS/Java/Object Model/Java Record Patterns]]
12. [[10_CONCEPTS/Java/Object Model/Java Nested Patterns and Exhaustiveness]]

## Card allocation

| Batch | Cards |
|---|---:|
| `JAVA-OBJECT-B03` | 25 |
| `JAVA-INIT-B03` | 30 |
| `JAVA-INHERIT-B03` | 35 |
| `JAVA-TYPES-B03` | 25 |
| **Total** | **115** |

## Drill allocation

```text
object creation and nested types       5
initialization and constructors        5
overloading and varargs                6
inheritance, interfaces and casts     10
records, enums and sealed types        6
Java 21 record patterns                3
-----------------------------------------
total                                  35
```

## Executable evidence model

```text
shared positive sources      compile/run with --release 17 and 21
shared negative bank         javac must reject in both lanes
Java 17 version trap         record-pattern syntax must fail
Java 21 positive source      record/nested pattern runtime proof
Java 21 negative bank        arity, component type, dominance, exhaustiveness
```

## Confirmed CI evidence

`JAVA-B03 Proof` run `30097320887` produced:

```text
JDK 17 shared compile/runtime                    PASS
JDK 17 shared expected-failure bank              PASS
JDK 17 record-pattern version trap               PASS
JDK 21 shared compile/runtime                    PASS
JDK 21 record and nested-pattern runtime proof   PASS
JDK 21 arity/type/dominance/exhaustiveness bank  PASS
```

## Completion gate

```text
[x] 12 atomic concept notes
[x] 115 stable base-card IDs
[x] 35 drill IDs
[x] Java 17/21 shared positive proof
[x] Java 21 record-pattern positive proof
[x] 17 expected compile-fail sources
[x] GitHub JDK 17 lane PASS
[x] GitHub JDK 21 lane PASS
[x] objective manifests classified lab-proven
[x] attach workflow run ID 30097320887
```

## Next route

```text
JAVA-B05 — Collections, Generics and Sequenced Collections
```
