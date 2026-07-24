# Java Backend Knowledge System

Obsidian-база знаний по Java backend и сертификационным трекам Java 17/21. Система объединяет atomic concepts, visual maps, stable cards, compile/output drills, executable labs, objective traceability и персональный learning state.

## Start here

1. [[00_HOME/Java Learning Cockpit]] — главный учебный интерфейс: выбрать длительность, режим и следующее действие.
2. [[00_HOME/Java Learning Dashboard]] — каталог опубликованных Java routes и atomic concepts.
3. [[00_HOME/Card Review Dashboard]] — due cards, confidence и outcome recording.
4. [[00_HOME/Java Weakness Repair Center]] — ошибка → focused concept → contrast → proof.
5. [[70_PROGRESS/Java Learning Progress Dashboard]] — weekly learner review.
6. [[01_MAPS/Java Learning Journey.canvas]] — визуальная карта учебного процесса.
7. [[00_HOME/Obsidian Learning Interface Setup]] — включение CSS и рекомендуемая раскладка.
8. [[00_HOME/Certification 99 Percent Readiness Dashboard]] — readiness материалов и gaps.
9. [[00_HOME/Knowledge Route Registry]] — machine-governed реестр routes.
10. [[30_CERTIFICATIONS/Certification MOC]] — сертификационная навигация.

## Learner-facing model

```text
Cockpit
→ choose one goal
→ retrieve before reading
→ one atomic concept
→ explain and contrast
→ cards / drills
→ predict and run lab
→ classify outcome
→ repair or delayed review
```

Материал не считается освоенным только потому, что он опубликован или `lab-proven`. Learner mastery требует устойчивого retrieval, confidence calibration, transfer и mixed timed performance.

## Current Java delivery

| Route | Scope | Atomic notes | Cards | Drills | Status |
|---|---|---:|---:|---:|---|
| JAVA-B01 | Values, Text and Date-Time | 9 | 75 | 15 | lab-proven |
| JAVA-B02 | Control Flow and Pattern Switch | 8 | 60 | 20 | lab-proven |
| JAVA-B03 | Object Model and Record Patterns | 12 | 115 | 35 | lab-proven |
| JAVA-B05 | Collections, Generics and Sequenced Collections | — | — | — | next |

```text
published exam routes       3
atomic concept notes       29
base cards                250
compile/output drills      70
positive proof classes      9
expected compile failures  28
JDK lanes               17, 21
```

## Study modes

- **Learn:** [[00_HOME/Java Learning Dashboard]]
- **Recall:** [[00_HOME/Card Review Dashboard]]
- **Practice/Prove:** route drills and [[50_LABS/Java/JAVA-B03/README|executable labs]]
- **Repair:** [[00_HOME/Java Weakness Repair Center]]
- **Reflect:** [[70_PROGRESS/Java Learning Progress Dashboard]]

## Java certification lanes

| Track | Exact baseline | Roadmap |
|---|---|---|
| Oracle Java SE 17 `1Z0-829` | Java 17 | [[30_CERTIFICATIONS/Java/1Z0-829/Java SE 17 99 Percent Master Roadmap]] |
| Oracle Java SE 21 `1Z0-830` | Java 21 | [[30_CERTIFICATIONS/Java/1Z0-830/Java SE 21 99 Percent Master Roadmap]] |
| Java 11/17/21 platform | migration and production | [[00_HOME/Java 11 17 21 Complete Knowledge Program]] |

Shared mechanisms are reused only when compile/API/runtime behavior is identical. Version-sensitive answers remain bound to the corresponding JLS/API lane.

## Repository structure

```text
00_HOME              learner dashboards and entry points
01_MAPS              Obsidian Canvas maps
10_CONCEPTS          canonical hubs and atomic concepts
20_QUESTIONS         interview recall
30_CERTIFICATIONS    roadmaps, cards, drills and mocks
40_PRODUCTION_CASES  symptoms, causes, repairs and proofs
50_LABS              executable evidence
70_PROGRESS          learner state and review dashboards
90_TEMPLATES         pedagogical, visual and authoring standards
98_SOURCES           official and primary sources
99_AUDITS            generated audit evidence
```

## Visual and pedagogical standards

- [[90_TEMPLATES/Route Learning UX Standard]]
- [[90_TEMPLATES/Atomic Lesson UX Template]]
- [[90_TEMPLATES/Learning Session Template]]
- [[90_TEMPLATES/Pedagogical Visual Standard]]
- [[90_TEMPLATES/Cross-Linking Standard]]

Optional visual snippet:

```text
.obsidian/snippets/learning-experience.css
```

## Progress commands

```bash
python .github/scripts/card_progress.py sync
python .github/scripts/card_progress.py audit
python .github/scripts/card_progress.py due --limit 30
python .github/scripts/card_progress.py record \
  --card-id JAVA-FLOW-B02-C001 \
  --outcome correct-confident \
  --confidence 4
```

Details: [[00_HOME/Card Review Dashboard]].

## Quality controls

```text
structural Markdown and card audit
cross-link graph audit
per-card scheduler and catalog audit
objective traceability
certification readiness calculation
Java version coverage
Canvas validation
Mermaid rendering
route-specific JDK proof workflows
```

B01, B02 and B03 have dedicated JDK 17/21 proof workflows. Aggregate vault failures from an unrelated route do not erase route-specific evidence.

## Published non-Java areas

The vault also includes Spring Core/Boot/MVC/AOP/Cache/Transactions/Data JPA/Testing, Java Concurrency foundations and PostgreSQL indexes/query-plan material.

Use [[00_HOME/Knowledge Route Registry]] and [[30_CERTIFICATIONS/Certification MOC]] for the full artifact registry.
