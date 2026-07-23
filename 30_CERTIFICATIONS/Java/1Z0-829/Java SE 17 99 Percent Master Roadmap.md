---
type: certification-master-roadmap
certification: java-1Z0-829
domain: java
status: active
target_material_readiness: 99
base_card_target: 720
drill_card_target: 180
full_mock_target: 6
java_version: 17
java_11_baseline: required
java_21_delta: required
tags:
  - java
  - java-11
  - java-17
  - java-21
  - ocp
  - 1Z0-829
  - readiness
  - roadmap
---

# Java SE 17 1Z0-829 — 99 Percent Master Roadmap

> [!summary]
> Java 17 remains the exact exam baseline, but the knowledge route belongs to the cumulative [[00_HOME/Java 11 17 21 Complete Knowledge Program]]. Every exam domain must explain its Java 11 compatibility baseline and Java 21 production delta without contaminating Java 17 answers.

# Version contract

```text
Java 11  compatibility and migration baseline
Java 17  exact 1Z0-829 language/API baseline
Java 21  modern production delta
```

Rules:

1. Compile/no-compile answers use Java 17 JLS rules.
2. API questions use Java SE 17 API contracts.
3. Java 21-only syntax/APIs are explicit traps, not valid Java 17 answers.
4. Java 11 differences are included where they explain migration or minimum version.
5. Every example declares its minimum Java version.
6. Preview/incubator features are identified by exact release status.

Required preliminary route:

- [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Roadmap]].

# Official capability baseline

Oracle's Java SE 17 learning path identifies these broad capabilities:

```text
object-oriented programming
Java syntax and constructs
Collections and Streams
I/O and Concurrency
deployment
JDK 17 features
```

The repository decomposes them into 11 exam-oriented domains.

# Card target

```text
Base cards    720
Drill cards   180
-----------------
Total         900
Full mocks      6
```

## Base-card allocation

| Domain | Batch | Target |
|---|---|---:|
| Data types, operators, text and date/time | JAVA-B01 | 70 |
| Control flow and switch | JAVA-B02 | 45 |
| Object model, inheritance, records and sealed classes | JAVA-B03 | 90 |
| Exceptions and resource management | JAVA-B04 | 50 |
| Arrays, collections and generics | JAVA-B05 | 90 |
| Lambdas, functional interfaces and streams | JAVA-B06 | 100 |
| Modules, services and deployment | JAVA-B07 | 60 |
| Concurrency exam objectives | JAVA-B08 | 75 |
| I/O, NIO.2 and serialization | JAVA-B09 | 65 |
| JDBC | JAVA-B10 | 40 |
| Localization | JAVA-B11 | 35 |
| **Total** |  | **720** |

## Drill allocation

| Drill type | Cards |
|---|---:|
| Compile / does-not-compile | 55 |
| Exact output and initialization order | 35 |
| Multiple-select API semantics | 25 |
| Streams/collectors pipelines | 20 |
| Generics and overload resolution | 15 |
| Modules/I/O/JDBC/localization traps | 20 |
| Concurrency/JMM execution traps | 10 |
| **Total** | **180** |

# Required answer order

```text
1. Which Java version is targeted?
2. Does it compile under Java 17?
3. If not, which line and JLS rule fail?
4. If yes, what is the exact output?
5. Does execution throw an exception?
6. Which answer options are selected?
7. Is any tempting option Java 11-only history or Java 21-only behavior?
```

# JAVA-B01 — Data, Text and Date-Time

Target: 70 base cards + 15 drills.

```text
primitive types and literals
numeric promotion and casts
wrappers and boxing
operators and precedence
String, text blocks and StringBuilder
equality and immutability
Math and utility APIs
LocalDate/Time/DateTime
ZonedDateTime and Instant
Period versus Duration
DateTimeFormatter
DST and unsupported operations
```

Version requirements:

```text
Java 11  String.isBlank/lines/repeat and Files text helpers
Java 17  text blocks and exam API baseline
Java 21  no accidental use of newer collection/pattern APIs
```

# JAVA-B02 — Control Flow and Pattern Matching

Target: 45 base cards + 10 drills.

```text
if/else
classic switch
switch expressions and yield
loops and labels
break and continue
unreachable code
pattern matching for instanceof
pattern-variable scope
```

Version trap: record patterns and permanent pattern switch belong to Java 21, not Java 17.

# JAVA-B03 — Object Model

Target: 90 base cards + 25 drills.

```text
declarations and access
constructors and initialization order
overloading and overriding
static hiding
covariant returns
abstract classes and interfaces
default/private/static interface methods
polymorphism and casts
enums and nested classes
records
sealed classes
annotations
```

# JAVA-B04 — Exceptions and Resources

Target: 50 base cards + 15 drills.

```text
checked versus unchecked
catch ordering and multi-catch
finally
try-with-resources
AutoCloseable
close order
suppressed exceptions
propagation
overriding throws rules
assertions and custom exceptions
```

# JAVA-B05 — Arrays, Collections and Generics

Target: 90 base cards + 25 drills.

```text
arrays and multidimensional initialization
List Set Map Queue Deque
immutable collection factories
Comparable and Comparator
equals/hashCode
generic classes and methods
invariance and bounded wildcards
PECS
type erasure and raw types
overload resolution
```

Version trap: sequenced collection interfaces and `getFirst/getLast/reversed` are Java 21 APIs.

# JAVA-B06 — Lambdas and Streams

Target: 100 base cards + 30 drills.

```text
lambda syntax and capture
functional interfaces
method references
Optional
stream laziness
map flatMap filter reduce
primitive streams
collectors
grouping partitioning mapping flatMapping teeing
parallel streams
ordering associativity and side effects
```

# JAVA-B07 — Modules and Deployment

Target: 60 base cards + 15 drills.

```text
module-info.java
requires/transitive/static
exports and opens
qualified exports/opens
uses and provides
service loading
classpath versus module path
named automatic and unnamed modules
modular and multi-release JARs
jar jdeps jlink
migration and split packages
```

Java 11 migration and Java 17 strong encapsulation are required context.

# JAVA-B08 — Concurrency Exam Objectives

Target: 75 base cards + 20 drills.

```text
Runnable versus Callable
execute versus submit
Future outcomes
executor lifecycle
synchronization and locks
atomics
concurrent collections
parallel streams
thread safety
happens-before exact code paths
```

Parent route:

- [[30_CERTIFICATIONS/Java/Concurrency/Java Concurrency 99 Percent Roadmap]].

Version trap: Java 21 virtual threads are production delta, not part of Java 17 exam APIs.

# JAVA-B09 — I/O, NIO.2 and Serialization

Target: 65 base cards + 15 drills.

```text
byte versus character streams
buffering
Reader/Writer
serialization and transient
serialVersionUID
Path and Files
attributes and directory traversal
resolve relativize normalize
watch service
try-with-resources
serialization filtering
```

# JAVA-B10 — JDBC

Target: 40 base cards + 10 drills.

```text
Connection
Statement PreparedStatement CallableStatement
parameter indexing
execute executeQuery executeUpdate
ResultSet navigation
transactions and savepoints
batching
resource lifecycle
SQL exception semantics
```

# JAVA-B11 — Localization

Target: 35 base cards + 10 drills.

```text
Locale
ResourceBundle lookup/fallback
properties versus class bundles
NumberFormat
currency and percentages
localized date/time
MessageFormat
missing-resource behavior
```

# Vertical-slice contract

Every `JAVA-Bxx` route includes:

```text
learning outcomes
pre-test and post-test
canonical explanation
visual deep dive
Java 11 baseline/delta
Java 17 exact exam semantics
Java 21 production delta
base cards
version-comparison and exam drills
production/migration cases
executable labs under applicable JDKs
Canvas
official JLS/API/JEP/tool sources
```

# Mock system

## Domain mini-mocks

```text
22 mini-mocks
25 questions each
2 per domain
```

## Full timed mocks

```text
6 full mixed Java 17 mocks
format/time verified before publication
```

Each question stores:

```text
objective ID
target Java version
question kind
correct-answer count
compile status
runtime outcome
source evidence
confidence
elapsed time
error taxonomy
```

# Current baseline

```text
JAVA-LTS-B01 version route            published
Java Concurrency canonical/visual     strong
Java 17 general exam cards            not published
10 of 11 exam domains                 unmapped
full Java mocks                       absent
```

Machine truth remains in:

```text
.github/objectives/java-1Z0-829.json
.github/java-version-coverage.json
.audit/certification-readiness.json
.audit/java-version-coverage.json
```

Do not use an old narrative percentage when machine reports are available.

# 99% gate

```text
[ ] 18/18 Java shared domains complete
[ ] Java 11/17/21 delta section for every domain
[ ] 11/11 Java 17 exam domains complete
[ ] 720 base exam cards
[ ] 180 exam drills
[ ] 120 cross-version cards
[ ] compile/output validation
[ ] JDK 11/17/21 labs pass
[ ] 6 Java 17 full mocks
[ ] 6 migration mini-mocks
[ ] JLS/API/JEP/tool sources version-pinned
[ ] no domain below target
[ ] all structural/graph/card/objective/version audits pass
```

# Delivery sequence

```mermaid
flowchart LR
    LTS["JAVA-LTS-B01 done"] --> B1["JAVA-B01 data/date"]
    B1 --> B2["JAVA-B02 control/patterns"]
    B2 --> B3["JAVA-B03 object model"]
    B3 --> B4["JAVA-B04 exceptions"]
    B4 --> B5["JAVA-B05 collections/generics"]
    B5 --> B6["JAVA-B06 lambdas/streams"]
    B6 --> B7["JAVA-B07 modules"]
    B7 --> B8["JAVA-B08 concurrency"]
    B8 --> B9["JAVA-B09 I/O"]
    B9 --> B10["JAVA-B10 JDBC"]
    B10 --> B11["JAVA-B11 localization"]
    B11 --> JVM["JVM/GC/JIT/diagnostics"]
    JVM --> DRILL["exam + version drills"]
    DRILL --> MOCK["full mocks"]
```

# Related navigation

- [[00_HOME/Java 11 17 21 Complete Knowledge Program]]
- [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Roadmap]]
- [[30_CERTIFICATIONS/Java/Concurrency/Java Concurrency 99 Percent Roadmap]]
- [[01_MAPS/Java Map]]
- [[30_CERTIFICATIONS/Certification MOC]]
- [[98_SOURCES/Java 11 17 21 Official Sources]]
