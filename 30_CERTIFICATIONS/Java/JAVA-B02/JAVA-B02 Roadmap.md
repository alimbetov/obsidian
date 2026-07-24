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
atomic_note_count: 8
base_card_target: 60
drill_card_target: 20
published_base_cards: 60
published_drills: 20
card_batches:
  - JAVA-FLOW-B02
  - JAVA-SWITCH-B02
  - JAVA-PATTERN-B02
previous_route: JAVA-B01
next_route: JAVA-B03
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
> Second lab-proven certification route. Shared Java 17/21 flow semantics and final Java 21 pattern switch are separated into eight atomic concepts, three card batches and version-specific executable evidence.

## Fast entry

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]]
- **Visual map:** [[01_MAPS/Java Certification Routes.canvas]]
- **Start concept:** [[10_CONCEPTS/Java/Core/Java Conditions and Definite Assignment]]
- **Cards:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02A Control Flow Cards]], [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02B Switch Cards]], [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02C Pattern Switch Cards]]
- **Drills:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills]]
- **Lab:** [[50_LABS/Java/JAVA-B02/README]]

## Objective mapping

| Objective | Exam lane | Scope | Evidence |
|---|---|---|---|
| `JAVA-B02` | Java 17 / 1Z0-829 | conditions, loops, labels, reachability, classic switch, switch expressions, `instanceof` patterns | 6 shared atomic notes, shared cards/drills, JDK 17 proof lane |
| `JAVA21-2.1` | Java 21 / 1Z0-830 | shared flow plus final pattern switch, guards, null, dominance and exhaustiveness | all 8 atomic notes, 60 cards, 20 drills, JDK 21 proof lane |

## Atomic knowledge sequence

| Order | Concept ID | Note | Card batch |
|---:|---|---|---|
| 1 | `JAVA-B02-N01` | [[10_CONCEPTS/Java/Core/Java Conditions and Definite Assignment]] | `JAVA-FLOW-B02` |
| 2 | `JAVA-B02-N02` | [[10_CONCEPTS/Java/Core/Java Loops Transfers and Labels]] | `JAVA-FLOW-B02` |
| 3 | `JAVA-B02-N03` | [[10_CONCEPTS/Java/Core/Java Reachability Rules]] | `JAVA-FLOW-B02` |
| 4 | `JAVA-B02-N04` | [[10_CONCEPTS/Java/Core/Java Classic Switch]] | `JAVA-SWITCH-B02` |
| 5 | `JAVA-B02-N05` | [[10_CONCEPTS/Java/Core/Java Switch Expressions]] | `JAVA-SWITCH-B02` |
| 6 | `JAVA-B02-N06` | [[10_CONCEPTS/Java/Core/Java Pattern Matching for instanceof]] | `JAVA-FLOW-B02` |
| 7 | `JAVA-B02-N07` | [[10_CONCEPTS/Java/Core/Java 21 Pattern Switch]] | `JAVA-PATTERN-B02` |
| 8 | `JAVA-B02-N08` | [[10_CONCEPTS/Java/Core/Java Switch Dominance and Exhaustiveness]] | `JAVA-PATTERN-B02` |

## Version contract

```text
Java 17 lane
  conditions, loops, labels and reachability
  classic switch statements
  final switch expressions
  final pattern matching for instanceof
  pattern switch excluded from ordinary exam assumptions

Java 21 lane
  all shared Java 17 rules
  final pattern switch
  case null
  when guards
  broader reference selectors
  dominance and exhaustive enhanced statements
  qualified enum constants
```

## Practice inventory

```text
JAVA-FLOW-B02 cards       20
JAVA-SWITCH-B02 cards     20
JAVA-PATTERN-B02 cards    20
compile/output drills     20
positive proof classes     2
expected compile failures 11
```

## Confirmed CI evidence

Dedicated run `30089425851`:

```text
JDK 17 shared compile/runtime                 PASS
JDK 17 shared expected-failure bank          PASS
JDK 17 qualified-enum version trap           PASS
JDK 21 shared compile/runtime                 PASS
JDK 21 final pattern-switch runtime proof    PASS
JDK 21 dominance/exhaustiveness failure bank PASS
```

## Reliable exam algorithm

1. Fix the Java version.
2. Validate grammar and condition or selector compatibility.
3. Check definite assignment and reachability.
4. Classify switch as classic statement, expression or enhanced statement.
5. Check duplicate labels and dominance.
6. Prove exhaustiveness.
7. Resolve null independently.
8. Trace selected rules and transfer statements.
9. Compute exact output last.

## Learner workflow

1. Complete the first six shared atomic notes.
2. Answer shared control-flow and switch cards.
3. Attempt Java 17-compatible drills.
4. Complete the two Java 21-only notes.
5. Answer pattern-switch cards and drills.
6. Predict positive and negative lab outcomes.
7. Execute both JDK lanes.
8. Record review outcomes through [[00_HOME/Card Review Dashboard]].

## Completion status

```text
[x] 8 atomic concepts
[x] 60 stable base-card IDs
[x] 20 drills
[x] exact Java 17/21 boundary
[x] positive shared proof
[x] Java 21 pattern proof
[x] 11 expected compile-fail sources
[x] JDK 17/21 CI evidence
[x] objective status lab-proven
[ ] learner delayed-review history
[ ] timed mixed mocks
```

## Sources and navigation

- [[98_SOURCES/Java SE 17 1Z0-829 Sources]]
- [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
- [[30_CERTIFICATIONS/Java/Java 17 and 21 Exam Delta Matrix]]
- **Previous route:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- **Next planned route:** `JAVA-B03 — Object Model, Records and Record Patterns`
