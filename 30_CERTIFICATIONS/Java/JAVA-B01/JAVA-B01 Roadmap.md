---
type: learning-route
route: JAVA-B01
domain: java
subdomain: values-text-date-time
status: published
evidence_status: lab-proven
verified_at: 2026-07-24
ci_run_id: 30065610629
java_versions:
  - 17
  - 21
certifications:
  - java-1Z0-829
  - java-1Z0-830
objectives:
  - JAVA21-1.1
  - JAVA21-1.2
  - JAVA21-1.3
atomic_note_count: 9
base_card_target: 75
drill_card_target: 15
published_base_cards: 75
published_drills: 15
card_batches:
  - JAVA-VALUES-B01
  - JAVA-TEXT-B01
  - JAVA-TIME-B01
next_route: JAVA-B02
tags:
  - java
  - java-17
  - java-21
  - ocp
  - values
  - strings
  - text-blocks
  - date-time
---

# JAVA-B01 — Values, Strings, Text Blocks and Date-Time

> [!summary]
> First lab-proven Java certification route. It establishes expression, text and temporal rules reused by later compile/output questions. The route is now navigated through nine atomic concept notes instead of one 800-line chapter.

## Fast entry

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]]
- **Visual map:** [[01_MAPS/Java Certification Routes.canvas]]
- **Start concept:** [[10_CONCEPTS/Java/Core/Java Primitive Values and Literals]]
- **Cards:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01A Values Cards]], [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01B Text Cards]], [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01C Date-Time Cards]]
- **Drills:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills]]
- **Lab:** [[50_LABS/Java/JAVA-B01/README]]

## Objective mapping

| Objective | Scope | Evidence |
|---|---|---|
| `JAVA21-1.1` | primitives, wrappers, Math, promotions, casts, boolean expressions | 3 atomic notes, 25 cards, drills, `ValuesProof` |
| `JAVA21-1.2` | String, StringBuilder, text blocks | 3 atomic notes, 25 cards, drills, `TextProof` |
| `JAVA21-1.3` | local temporal types, period, duration, instant, zones, DST, formatting | 3 atomic notes, 25 cards, drills, `DateTimeProof` |

## Atomic knowledge sequence

| Order | Concept ID | Note | Card batch |
|---:|---|---|---|
| 1 | `JAVA-B01-N01` | [[10_CONCEPTS/Java/Core/Java Primitive Values and Literals]] | `JAVA-VALUES-B01` |
| 2 | `JAVA-B01-N02` | [[10_CONCEPTS/Java/Core/Java Numeric Promotion and Casting]] | `JAVA-VALUES-B01` |
| 3 | `JAVA-B01-N03` | [[10_CONCEPTS/Java/Core/Java Wrappers Boxing and Math]] | `JAVA-VALUES-B01` |
| 4 | `JAVA-B01-N04` | [[10_CONCEPTS/Java/Core/Java String Identity and Operations]] | `JAVA-TEXT-B01` |
| 5 | `JAVA-B01-N05` | [[10_CONCEPTS/Java/Core/Java StringBuilder Mutation]] | `JAVA-TEXT-B01` |
| 6 | `JAVA-B01-N06` | [[10_CONCEPTS/Java/Core/Java Text Blocks]] | `JAVA-TEXT-B01` |
| 7 | `JAVA-B01-N07` | [[10_CONCEPTS/Java/Core/Java Local Date-Time Types]] | `JAVA-TIME-B01` |
| 8 | `JAVA-B01-N08` | [[10_CONCEPTS/Java/Core/Java Period Duration and Instant]] | `JAVA-TIME-B01` |
| 9 | `JAVA-B01-N09` | [[10_CONCEPTS/Java/Core/Java Zones DST and Formatting]] | `JAVA-TIME-B01` |

## Version contract

```text
1Z0-829 → compile and API reasoning against Java SE 17
1Z0-830 → compile and API reasoning against Java SE 21
```

The B01 mechanisms are shared in both lanes unless a question explicitly says otherwise. Runtime examples execute in both JDK lanes to prevent accidental API leakage.

## Practice inventory

```text
JAVA-VALUES-B01 cards    25
JAVA-TEXT-B01 cards      25
JAVA-TIME-B01 cards      25
compile/output drills    15
positive proof classes    3
```

The drills require compile/no-compile, exact output, exception type, identity/equality, mutation tracing, DST transition reasoning or formatter analysis.

## Confirmed executable evidence

GitHub Actions run `30065610629`:

```text
JDK 17 compile and runtime assertions  PASS
JDK 21 compile and runtime assertions  PASS
```

Executed classes:

```text
lab.b01.ValuesProof
lab.b01.TextProof
lab.b01.DateTimeProof
```

## Determinism contract

Exact questions avoid environment-dependent defaults:

```text
no system default locale
no system default time zone
no current clock
no unspecified charset
```

Use explicit `Locale`, `ZoneId`, `Clock` or fixed values.

## Learner workflow

1. Open the first atomic note.
2. Follow **Next concept** links.
3. Answer active recall before opening cards.
4. Complete the related card batch.
5. Attempt drills without executing code.
6. Write predictions for proof classes.
7. Run the JDK 17/21 lab.
8. Record card outcomes through [[00_HOME/Card Review Dashboard]].

## Completion status

```text
[x] 9 atomic concepts
[x] 75 stable base-card IDs
[x] 15 drills
[x] Java 17/21 version contract
[x] deterministic temporal examples
[x] JDK 17 and JDK 21 CI evidence
[x] objective status lab-proven
[ ] learner delayed-review history
[ ] timed mixed mocks
```

## Sources and navigation

- [[98_SOURCES/Java SE 17 1Z0-829 Sources]]
- [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
- [[30_CERTIFICATIONS/Java/Java 17 and 21 Exam Delta Matrix]]
- [[00_HOME/Oracle Java 17 and 21 Certification Program]]
- **Next route:** [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]
