---
type: quality-standard
domain: knowledge-system
status: active
tags:
  - navigation
  - wikilinks
  - knowledge-graph
  - quality
---

# Cross-Linking Standard

> [!summary]
> Перекрёстные ссылки должны выражать смысловую связь, а не просто увеличивать число backlinks. Для каждого опубликованного route требуется понятный вход, каноническая теория, visual route, active recall, production transfer, executable evidence и primary sources.

# 1. Navigation contract

Каждый опубликованный маршрут должен иметь:

```text
Repository README
    ↓
Domain MOC
    ↓
Route roadmap / learning path
    ↓
Canonical concepts
    ↓
Visual deep dive / Canvas
    ↓
Cards or recall questions
    ↓
Production cases
    ↓
Lab
    ↓
Primary sources
```

## Required entry points

1. `README.md` — объясняет, где находится маршрут.
2. Domain MOC в `01_MAPS` — показывает маршрут внутри domain.
3. Route roadmap — перечисляет все опубликованные artifacts.
4. [[00_HOME/Knowledge Route Registry]] — единый реестр опубликованных маршрутов.

# 2. Link semantics

Используй links по роли.

| Relation | Meaning | Example |
|---|---|---|
| `Prerequisites` | что нужно знать раньше | JMM → happens-before |
| `Canonical concept` | основной источник теории | Card → concept note |
| `Visual model` | spatial/runtime explanation | Concept → visual deep dive |
| `Practice` | active recall | Concept → cards |
| `Production transfer` | failure and repair | Concept → production cases |
| `Executable evidence` | воспроизводимый experiment | Case → lab |
| `Primary sources` | официальная проверка | Concept → source index |
| `Previous / Next` | последовательность обучения | TX → Data JPA |
| `Domain hub` | возврат в MOC | Route → Spring Map |

# 3. Canonical-note footer

Advanced concept должен завершаться навигационным блоком:

```markdown
## Route navigation

- **Roadmap:** [[...]]
- **Visual model:** [[...]]
- **Cards:** [[...]]
- **Production cases:** [[...]]
- **Lab:** [[...]]
- **Sources:** [[...]]
- **Domain map:** [[...]]
```

Не нужно механически добавлять все links в каждую короткую заметку. Для supporting note достаточно:

```markdown
## Related materials

- **Canonical route:** [[...]]
- **Parent concept:** [[...]]
- **Practice:** [[...]]
```

# 4. Card-batch contract

Card batch должен иметь links:

```text
Roadmap
Canonical concept(s)
Visual model
Production cases
Lab
Sources
```

Individual card не обязана повторять одинаковые links. Batch-level navigation достаточно, если card headings находятся в одном файле.

# 5. Production-case contract

Production-case collection должна ссылаться на:

- canonical mechanism;
- relevant cards;
- executable lab;
- route roadmap;
- source index, если case содержит version-sensitive claim.

Каждый case должен иметь:

```text
Symptom
Evidence
Runtime/physical path
Root cause
Repair
Proof after change
```

# 6. Lab contract

Lab README должен ссылаться на:

- concept, который эксперимент доказывает;
- cards, перед которыми learner должен предсказать outcome;
- production case, который lab воспроизводит;
- roadmap;
- source index;
- ограничения среды и версии.

# 7. Canvas contract

Canvas не заменяет Markdown navigation.

Canvas должен:

- иметь root node;
- содержать file nodes на roadmap и главные artifacts;
- связывать nodes edges;
- не содержать missing file references;
- быть достижимым из Markdown MOC или roadmap;
- иметь обратную Markdown-ссылку из route index.

# 8. Link style

## Prefer explicit paths

Для route-level links используй полный path:

```markdown
[[10_CONCEPTS/Spring/Transactions/Spring Transaction Management Deep Dive]]
```

Короткое имя допустимо только если filename уникален во всём vault.

## Use aliases for readable labels

```markdown
[[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Cards|30 active-recall cards]]
```

## Avoid decorative links

Плохой вариант:

```markdown
See also: [[Java]], [[Spring]], [[Database]]
```

Хороший вариант:

```markdown
This failure is a proxy-boundary problem: [[10_CONCEPTS/Spring/AOP/Spring AOP Proxy Mechanics|Spring AOP proxy mechanics]].
```

# 9. Orphan and sink policy

## Orphan

Markdown/Canvas artifact без входящих links.

- Published artifact — error.
- Draft/planned artifact — warning.
- Template — allowed.

## Navigation sink

Advanced note имеет inbound links, но не даёт learner дальнейшего пути.

- Route hub, canonical concept, case collection и lab README должны иметь хотя бы один outbound wikilink.
- Leaf question может быть sink, если parent batch обеспечивает navigation.

# 10. Automated validation

Machine-readable registry:

```text
.github/knowledge-routes.json
```

Audit:

```text
.github/scripts/audit_cross_links.py
```

CI проверяет:

- existence всех route artifacts;
- broken и ambiguous wikilinks;
- route registry → roadmap edges;
- route index → artifacts coverage;
- inbound links для published artifacts;
- navigation sinks;
- global orphan warnings;
- Canvas file-node edges.

# 11. Review checklist

```text
[ ] Route reachable from README
[ ] Route reachable from domain MOC
[ ] Registry lists every artifact
[ ] Roadmap lists canonical/visual/cards/cases/lab/sources
[ ] Canonical notes link practice and evidence
[ ] Cases link concept and lab
[ ] Lab links concept, cases and sources
[ ] Canvas is linked from Markdown
[ ] No broken or ambiguous strict-route links
[ ] No published orphan artifact
[ ] Previous/next route is current, not stale
```

## Related materials

- [[00_HOME/Knowledge Route Registry]]
- [[90_TEMPLATES/Pedagogical Visual Standard]]
- [[99_AUDITS/Core Concurrency and DB-B01 Visual Enrichment]]
