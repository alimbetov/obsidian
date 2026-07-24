---
type: learning-route
route: JAVA-B02
domain: java
subdomain: control-flow-pattern-switch
status: lab-proven
evidence_status: jdk17-jdk21-ci-pass
verified_at: 2026-07-24
ci_run_id: 30089425851
java_versions:
  - 17
  - 21
certifications:
  - java-1Z0-829
  - java-1Z0-830
objectives:
  - JAVA-B02
  - JAVA21-2.1
base_card_target: 60
drill_card_target: 20
published_base_cards: 60
published_drills: 20
tags:
  - java
  - java-17
  - java-21
  - control-flow
  - switch
  - pattern-matching
---

# JAVA-B02 — Control Flow and Java 21 Pattern Switch

> [!summary]
> `JAVA-B02` establishes deterministic reasoning about conditions, loops, transfers, classic switch, switch expressions and the final Java 21 pattern-switch model. Shared mechanisms are compiled against Java 17 and Java 21; pattern labels, guards, null labels, broadened reference selectors and enhanced-switch exhaustiveness are isolated to the Java 21 lane. Dedicated CI run `30089425851` passed both version lanes.

# Why this route follows JAVA-B01

Every B02 compile/output question assumes the learner can already resolve:

```text
boolean evaluation and unboxing
numeric literal and promotion rules
String equality and null behavior
operator precedence and short-circuiting
exact expression output
```

B02 then provides the control-flow substrate reused by object initialization, exception propagation, stream pipelines and concurrency code.

# Objective mapping

| Objective | Exam lane | Scope | Evidence |
|---|---|---|---|
| `JAVA-B02` | 1Z0-829 / Java 17 | conditions, loops, labels, classic switch, switch expressions, Java 17 pattern baseline | canonical, 40 shared cards, 12 shared drills, Java 17 proof lane |
| `JAVA21-2.1` | 1Z0-830 / Java 21 | all shared flow rules plus final pattern switch, guards, null, dominance and exhaustive enhanced statements | canonical, 60 cards, 20 drills, Java 21 proof lane |

# Route artifacts

| Role | Artifact |
|---|---|
| Canonical knowledge | [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]] |
| Conditions and loops cards | [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02A Control Flow Cards]] |
| Switch cards | [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02B Switch Cards]] |
| Pattern-switch cards | [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02C Pattern Switch Cards]] |
| Compile/output drills | [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills]] |
| Executable lab | [[50_LABS/Java/JAVA-B02/README]] |
| Java 17 sources | [[98_SOURCES/Java SE 17 1Z0-829 Sources]] |
| Java 21 sources | [[98_SOURCES/Java SE 21 1Z0-830 Sources]] |

# Version contract

```text
Java 17 lane
  conditions, loops and transfers
  classic switch statements
  final switch expressions
  pattern matching for instanceof
  no normal final pattern-switch assumptions

Java 21 lane
  all Java 17 shared rules
  final JEP 441 pattern switch
  case null
  when guards
  dominance checking
  exhaustive enhanced switch statements
  qualified enum constants
```

Java 17 preview syntax for pattern switch is historical context only. It is not mixed into ordinary 1Z0-829 compile questions.

# Knowledge slices

## JAVA-B02A — Conditions, loops and transfers

```text
boolean conditions and Boolean unboxing
dangling else and block boundaries
definite assignment
while and do-while
basic and enhanced for
loop-variable scope
break and continue
labels
short-circuit evaluation
reachability and unreachable statements
return versus break
```

Target: **20 base cards**.

## JAVA-B02B — Classic switch and switch expressions

```text
legacy selector types
compile-time case constants
duplicate labels
colon fall-through
arrow rules
break and yield
switch expression exhaustiveness
enum coverage
target typing
normal versus abrupt completion
Java 17 enum-label qualification rule
```

Target: **20 base cards**.

## JAVA-B02C — Java 21 pattern switch

```text
reference-type selectors
type patterns and binding variables
source-order selection
dominance
when guards
case null
case null, default
enhanced-statement exhaustiveness
sealed hierarchy coverage
match-all labels
pattern-variable scope
qualified enum constants
```

Target: **20 base cards**.

# Drill allocation

| Family | Count |
|---|---:|
| Conditions, loops and labels | 6 |
| Classic switch and switch expressions | 6 |
| Java 21 pattern switch | 8 |
| **Total** | **20** |

Each drill records:

```text
target Java version
compile status
exact output or diagnostic class
reasoning path
error taxonomy
```

# Executable evidence model

```text
shared positive source       compile/run with --release 17 and --release 21
shared negative source       javac must reject in both lanes
Java 17 version trap         qualified enum label must fail under --release 17
Java 21 positive source      compile/run only in Java 21 lane
Java 21 negative source      dominance/exhaustiveness/guard failures must be rejected
```

The lab has no external dependencies and no environment-dependent output.

# Confirmed CI evidence

Dedicated GitHub Actions run `30089425851` produced:

```text
JDK 17 shared compile/runtime                 PASS
JDK 17 shared expected-failure bank          PASS
JDK 17 qualified-enum version trap           PASS
JDK 21 shared compile/runtime                 PASS
JDK 21 final pattern-switch runtime proof    PASS
JDK 21 dominance/exhaustiveness failure bank PASS
```

# Study sequence

```mermaid
flowchart LR
    A[Boolean conditions] --> B[Loops]
    B --> C[Transfers and labels]
    C --> D[Reachability]
    D --> E[Classic switch]
    E --> F[Switch expressions]
    F --> G[Pattern labels]
    G --> H[Guards and null]
    H --> I[Dominance]
    I --> J[Exhaustiveness]
    J --> K[Sealed and enum coverage]
    K --> L[Mixed drills]
```

# Completion gate

```text
[x] 60 stable base-card IDs
[x] 20 stable drill IDs
[x] exact Java 17 and Java 21 boundaries
[x] positive shared proof source
[x] positive Java 21 pattern source
[x] expected compile-fail source bank
[x] execute shared proof on JDK 17
[x] execute shared and pattern proof on JDK 21
[x] confirm all negative cases fail in their intended lanes
[x] mark JAVA-B02 and JAVA21-2.1 lab-proven
[x] attach workflow run ID 30089425851
```

# Next route

```text
JAVA-B03 — Object Model, Records and Record Patterns
```

B03 may assume all B01 expression rules and all B02 flow, switch, dominance and exhaustiveness rules.
