# Java Backend Knowledge System

Структурированная Obsidian-база для Java backend: canonical mechanisms, atomic concepts, visual maps, stable cards, spaced repetition, production cases, executable labs и certification traceability.

## Start here

1. [[00_HOME/Java Learning Dashboard]] — текущий Java-маршрут, atomic concepts, cards, drills и labs.
2. [[00_HOME/Certification 99 Percent Readiness Dashboard]] — material readiness и оставшиеся gaps.
3. [[00_HOME/Card Review Dashboard]] — per-card review queue и spaced repetition.
4. [[00_HOME/Knowledge Route Registry]] — реестр опубликованных routes.
5. [[30_CERTIFICATIONS/Certification MOC]] — навигация по сертификациям.
6. [[01_MAPS/Java Certification Routes.canvas]] — Java 17/21 spatial navigation.
7. [[01_MAPS/Certification 99 Percent Map.canvas]] — общая certification map.

## How the vault works

```text
Official objective
    ↓
Route roadmap
    ↓
Canonical hub
    ↓
Atomic concept notes
    ↓
Stable card IDs
    ↓
Compile/output drills
    ↓
Production transfer and executable lab
    ↓
Per-card progress and delayed review
    ↓
Timed mocks
```

The canonical hub preserves route-level reading order. Atomic notes improve backlinks, Graph View, focused review and reuse by later routes.

## Current Java delivery

### JAVA-LTS-B01 — Java 11, 17 and 21 evolution

- [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Roadmap]]
- JDK 11/17/21 runtime matrix.

### JAVA-B01 — Values, Text and Date-Time

```text
9 atomic concepts
75 base cards
15 drills
3 proof classes
JDK 17 and 21 PASS
status: lab-proven
```

- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]]

### JAVA-B02 — Control Flow and Pattern Switch

```text
8 atomic concepts
60 base cards
20 drills
2 positive proof classes
11 expected compile failures
JDK 17 and 21 PASS
status: lab-proven
```

- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]
- [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]]

### JAVA-B03 — Object Model, Records and Record Patterns

```text
12 atomic concepts
115 base cards
35 drills
4 positive proof classes
17 expected compile failures
JDK 17 and 21 proof lanes
status: lab-proven
```

- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- [[01_MAPS/Java Object Model and Record Patterns Map.canvas]]

### Navigation inventory

```text
canonical route hubs             3
atomic Java concept notes       29
Java learning dashboard          1
Java certification Canvas        1
machine-registered Java routes   3
```

### Next Java route

```text
JAVA-B05 — Collections, Generics and Sequenced Collections
```

## Java certification lanes

| Track | Exact baseline | Roadmap |
|---|---|---|
| Oracle Java SE 17 `1Z0-829` | Java 17 | [[30_CERTIFICATIONS/Java/1Z0-829/Java SE 17 99 Percent Master Roadmap]] |
| Oracle Java SE 21 `1Z0-830` | Java 21 | [[30_CERTIFICATIONS/Java/1Z0-830/Java SE 21 99 Percent Master Roadmap]] |
| Java 11/17/21 platform | version migration and production | [[00_HOME/Java 11 17 21 Complete Knowledge Program]] |

Shared knowledge is reused, but compile/API answers remain version-bound. See [[30_CERTIFICATIONS/Java/Java 17 and 21 Exam Delta Matrix]].

## Repository structure

```text
00_HOME              dashboards and entry points
01_MAPS              Obsidian Canvas maps
10_CONCEPTS          canonical hubs and atomic notes
20_QUESTIONS         interview recall
30_CERTIFICATIONS    roadmaps, cards, assessments and mocks
40_PRODUCTION_CASES  symptoms, root causes, repairs and proofs
50_LABS              executable evidence
70_PROGRESS          per-card learning state
90_TEMPLATES         content and cross-link standards
98_SOURCES           official and primary sources
99_AUDITS            audit reports
```

## Daily learning workflow

```text
1. Open Java Learning Dashboard.
2. Continue from the next atomic concept.
3. Answer Active recall without notes.
4. Review the matching card batch.
5. Attempt compile/output drills before execution.
6. Predict lab results.
7. Run the correct JDK lane.
8. Record per-card outcome and confidence.
```

Progress commands:

```bash
python .github/scripts/card_progress.py audit
python .github/scripts/card_progress.py sync
python .github/scripts/card_progress.py due --limit 30
python .github/scripts/card_progress.py record \
  --card-id JAVA-FLOW-B02-C001 \
  --outcome correct-confident \
  --confidence 4
```

## Quality controls

```text
structural Markdown and card audit
cross-link graph audit
per-card progress tests and catalog audit
objective traceability
certification readiness calculation
Java version coverage
Canvas validation
Mermaid rendering
route-specific executable proof workflows
```

Dedicated Java B01, B02 and B03 workflows pass on JDK 17 and JDK 21. The aggregate vault workflow may also report unrelated Spring route regressions; route-specific proof remains separately visible.

## Published non-Java areas

The vault also contains:

- Spring Core, AOP, Cache, Transactions, Data JPA and Testing;
- Spring Boot B01/B02;
- Spring MVC B01/B02;
- Java Concurrency foundation;
- PostgreSQL indexes and query-plan route.

Use [[00_HOME/Knowledge Route Registry]] and [[30_CERTIFICATIONS/Certification MOC]] for their current artifact lists.

## Navigation standard

Every published route should be reachable through:

```text
README
→ Dashboard / MOC
→ Route roadmap
→ Canonical hub
→ Atomic concepts
→ Cards and drills
→ Lab and sources
→ Progress review
```

Cross-link rules: [[90_TEMPLATES/Cross-Linking Standard]].
