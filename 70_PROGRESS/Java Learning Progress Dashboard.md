---
type: learner-progress-dashboard
domain: java
status: active
verified_at: 2026-07-24
cssclasses:
  - progress-dashboard
  - wide-page
tags:
  - java
  - progress
  - reflection
  - metacognition
---

# Java Learning Progress Dashboard

> [!summary]
> Эта страница разделяет **готовность материалов** и **состояние ученика**. `lab-proven` означает, что repository evidence существует; mastery появляется только после retrieval, repair, transfer и delayed review.

## Material state

| Route | Repository status | Atomic notes | Cards | Drills |
|---|---|---:|---:|---:|
| JAVA-B01 | lab-proven | 9 | 75 | 15 |
| JAVA-B02 | lab-proven | 8 | 60 | 20 |
| JAVA-B03 | lab-proven | 12 | 115 | 35 |
| JAVA-B05 | planned next | — | — | — |

## Learner state: weekly check

Заполняй раз в неделю, а не после каждой карточки.

| Показатель | Значение |
|---|---|
| Самый устойчивый route | |
| Самый слабый mechanism | |
| Ошибки `attention` | |
| Ошибки `retrieval` | |
| Ошибки `discrimination` | |
| Ошибки `concept` | |
| Ошибки `transfer` | |
| Средняя confidence calibration | |
| Следующий focused repair | |
| Один новый concept следующей недели | |

## Команды состояния

```bash
python .github/scripts/card_progress.py sync \
  --root . \
  --progress 70_PROGRESS/card-progress.json

python .github/scripts/card_progress.py audit \
  --root . \
  --progress 70_PROGRESS/card-progress.json \
  --catalog-output .audit/card-catalog.json \
  --queue-output .audit/card-review-queue.md

python .github/scripts/card_progress.py due \
  --root . \
  --progress 70_PROGRESS/card-progress.json \
  --limit 30
```

## Route mastery checklist

### JAVA-B01

```text
[ ] могу определять expression type без запуска
[ ] различаю identity/value equality/mutation
[ ] считаю text-block output
[ ] различаю local/offset/zoned/instant
[ ] объясняю Period vs Duration и DST boundary
```

### JAVA-B02

```text
[ ] трассирую loop transfer points
[ ] различаю reachability и runtime path
[ ] отличаю switch statement/expression/enhanced statement
[ ] проверяю dominance, null и exhaustiveness
[ ] фиксирую Java 17/21 boundary
```

### JAVA-B03

```text
[ ] трассирую initialization order
[ ] разделяю overload, override и hiding
[ ] объясняю interface method contracts
[ ] знаю ограничения records/enums/sealed types
[ ] применяю record patterns и sealed exhaustiveness
```

## Confidence calibration

> [!checkpoint]
> Высокая уверенность при неправильном ответе важнее для repair, чем низкая уверенность. Она показывает, что ошибочная модель закреплена и требует контрастного примера.

| Результат | Интерпретация |
|---|---|
| correct + high confidence | устойчивое извлечение |
| correct + low confidence | знание есть, но retrieval нестабилен |
| wrong + low confidence | gap осознаётся; focused review |
| wrong + high confidence | misconception; обязательный repair loop |

## Weekly learning design

```text
60% delayed review and repair
25% new atomic concepts
15% mixed drills / executable proof
```

Это ориентир, а не жёсткая квота. При росте conceptual errors доля нового материала уменьшается.

## Следующее решение

> [!start]
> - Есть due cards → [[00_HOME/Card Review Dashboard]]
> - Есть повторяющаяся ошибка → [[00_HOME/Java Weakness Repair Center]]
> - Нужен новый mechanism → [[00_HOME/Java Learning Dashboard]]
> - Нужна готовая сессия → [[90_TEMPLATES/Learning Session Template]]
