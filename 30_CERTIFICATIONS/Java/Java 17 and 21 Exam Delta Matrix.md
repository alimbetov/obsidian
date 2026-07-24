---
type: certification-delta-matrix
domain: java
status: active
certification:
  - java-1Z0-829
  - java-1Z0-830
java_versions:
  - 17
  - 21
tags:
  - java
  - java-17
  - java-21
  - 1Z0-829
  - 1Z0-830
  - version-delta
---

# Java 17 and 21 Exam Delta Matrix

> [!summary]
> Use this matrix before answering any certification question. Shared language and API areas can reuse one canonical explanation, but compile status, exact API availability and required exam scope remain version-bound.

# Navigation

- [[00_HOME/Oracle Java 17 and 21 Certification Program]]
- [[30_CERTIFICATIONS/Java/1Z0-829/Java SE 17 99 Percent Master Roadmap]]
- [[30_CERTIFICATIONS/Java/1Z0-830/Java SE 21 99 Percent Master Roadmap]]
- [[98_SOURCES/Java SE 17 1Z0-829 Sources]]
- [[98_SOURCES/Java SE 21 1Z0-830 Sources]]

# First decision

```text
What is the target exam?

1Z0-829 → compile and reason as Java 17
1Z0-830 → compile and reason as Java 21
```

Do not answer from “latest Java” memory. Choose the exact language and API baseline first.

# Objective-level comparison

| Domain | 1Z0-829 Java 17 | 1Z0-830 Java 21 | Knowledge-base action |
|---|---|---|---|
| primitives, operators, text, date/time | direct | direct | shared canonical; separate version traps |
| program flow | direct | direct | Java 21 adds final pattern-switch rules |
| objects, records, sealed types | direct | direct | Java 21 adds record patterns and richer switch matching |
| exceptions and resources | direct | direct | mostly shared; compile under both JDKs |
| arrays and collections | direct | direct | Java 21 adds sequenced collections |
| streams and lambdas | direct | direct | shared core; preserve encounter-order differences |
| modules and deployment | direct | direct | shared model; exact JDK tool baseline differs |
| concurrency | platform-thread/executor baseline | platform and virtual threads | separate virtual-thread card and lab lane |
| I/O, NIO.2, serialization | direct | direct | shared canonical; exact API verification per release |
| JDBC | direct objective | not in main objective list | count only toward 1Z0-829 readiness |
| localization | direct | direct | shared canonical and version-specific API checks |
| logging and standard annotations | supplementary | supplementary | one shared supplementary route |
| generics/wildcards | direct through collections + supplementary | direct through collections + supplementary | one strong shared route |

# Java 21 additions that invalidate Java 17 answers

## Pattern matching for `switch`

Java 21 finalizes pattern matching for `switch`.

```java
static String describe(Object value) {
    return switch (value) {
        case null -> "null";
        case String s when s.isBlank() -> "blank";
        case String s -> "text:" + s;
        case Integer i -> "int:" + i;
        default -> "other";
    };
}
```

For 1Z0-829, this is not valid final Java 17 syntax. Java 17 exam questions use classic switch, switch expressions and final pattern matching for `instanceof`; preview switch-pattern knowledge must not leak into ordinary compile answers.

Required 1Z0-830 rules:

```text
pattern compatibility
case dominance
exhaustiveness
null handling
guards
sealed hierarchy coverage
```

## Record patterns

```java
record Point(int x, int y) {}

static int sum(Object value) {
    if (value instanceof Point(int x, int y)) {
        return x + y;
    }
    return 0;
}
```

Record patterns are a Java 21 final feature. They are invalid in normal Java 17 compilation.

Required rules:

```text
component pattern compatibility
nested record patterns
scope of pattern variables
matching null
interaction with instanceof and switch
```

## Sequenced collections

Java 21 introduces common encounter-order contracts:

```text
SequencedCollection
SequencedSet
SequencedMap
```

High-value methods:

```text
getFirst / getLast
addFirst / addLast
removeFirst / removeLast
reversed
firstEntry / lastEntry
pollFirstEntry / pollLastEntry
putFirst / putLast
sequencedKeySet / sequencedValues / sequencedEntrySet
```

Exam traps:

- `reversed()` normally returns a view rather than an independent copy;
- not every sequenced implementation supports every mutation operation;
- Java 17 interfaces do not declare these methods;
- encounter order is not the same as sorted order.

## Virtual threads

Java 21 adds final virtual-thread APIs:

```java
Thread thread = Thread.startVirtualThread(task);

try (ExecutorService executor =
         Executors.newVirtualThreadPerTaskExecutor()) {
    Future<String> future = executor.submit(callable);
}
```

Required distinctions:

| Concern | Platform thread | Virtual thread |
|---|---|---|
| scheduling resource | OS thread closely associated | scheduled by JVM over carrier threads |
| expected quantity | relatively limited | very large task-per-thread counts possible |
| best workload | CPU and general workloads | blocking I/O concurrency |
| pooling | often pooled | generally create per task rather than pool virtual threads |
| synchronization semantics | standard Java thread rules | same Java thread/JMM rules |

Exam traps:

- a virtual thread is still a `Thread`;
- virtual threads do not make CPU-bound code faster by themselves;
- `ThreadLocal` works but can be costly at very large thread counts;
- Structured Concurrency and Scoped Values remain preview in Java 21 and require explicit preview assumptions.

# Java 17-specific direct objective

## JDBC

1Z0-829 directly requires:

```text
Connection creation
Statement
PreparedStatement
CallableStatement
ResultSet
execute / executeQuery / executeUpdate
transaction control
commit and rollback
```

The broader Java backend program must keep JDBC, but 1Z0-830 readiness calculations must not count JDBC cards as evidence for missing Java 21 objectives.

# Shared-domain reuse policy

One canonical note may support both exams when all of the following are true:

```text
language rule is unchanged
API contract exists in both versions
the code sample compiles under both JDKs
no newer overload changes the selected answer
no version-specific exam objective adds extra depth
```

Every shared card must declare:

```yaml
applies_to:
  - java-17
  - java-21
```

Every version-specific card must declare exactly one baseline.

# Card ID strategy

```text
shared base cards      JAVA-Bxx-Cxxx
Java 17 version traps  JAVA17-Bxx-Dxxx
Java 21 version traps  JAVA21-Bxx-Dxxx
1Z0-829 JDBC cards     JAVA-B10-Cxxx
supplementary cards    JAVA-SUP-B01-Cxxx
```

Do not duplicate a shared question under two IDs. Store one stable card with two objective mappings.

# Lab strategy

```text
src/shared/java       compiles on JDK 17 and 21
src/java17/java       exact Java 17 examples and negative traps
src/java21/java       pattern switch, record patterns, sequenced collections, virtual threads
```

CI matrix:

```yaml
java: [17, 21]
```

Negative compilation tests should assert the expected JDK boundary:

```text
Java 17 rejects record-pattern syntax
Java 17 rejects SequencedCollection APIs
Java 17 rejects virtual-thread APIs
Java 21 accepts final feature syntax without --enable-preview
```

# Readiness accounting

```text
shared evidence can satisfy both tracks
version-specific evidence satisfies only its declared track
JDBC cannot hide missing 1Z0-830 pattern/collection/thread coverage
Java 21 deltas cannot hide missing 1Z0-829 JDBC coverage
```

# Recommended implementation order

1. `JAVA-B01` shared values/text/date-time.
2. `JAVA-B02` shared flow plus Java 21 pattern-switch delta.
3. `JAVA-B03` shared object model plus record-pattern delta.
4. `JAVA-B05` shared collections/generics plus sequenced collections.
5. `JAVA-B06` streams and lambdas.
6. `JAVA-B04` exceptions and resources.
7. `JAVA-B07` modules and deployment.
8. `JAVA-B08` concurrency plus virtual threads.
9. `JAVA-B09` I/O and NIO.2.
10. `JAVA-B10` JDBC for 1Z0-829 only.
11. `JAVA-B11` localization.
12. `JAVA-SUP-B01` supplementary topics.
