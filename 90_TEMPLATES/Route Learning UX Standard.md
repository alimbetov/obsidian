---
type: quality-standard
domain: knowledge-system
status: active
verified_at: 2026-07-24
tags:
  - pedagogy
  - learning-interface
  - cognitive-load
  - quality
---

# Route Learning UX Standard

> [!summary]
> Route должен быть не только корректным набором файлов. Learner-facing interface обязан отвечать на четыре вопроса: **что делать сейчас, зачем это делать, сколько материала брать и что делать после ошибки**.

## 1. Progressive disclosure

Порядок раскрытия:

```text
Cockpit
→ route choice
→ canonical hub
→ one atomic concept
→ retrieval
→ cards/drill
→ executable proof
→ progress/repair
```

Не выводи на первую страницу полный список всех artifacts без приоритетов.

## 2. Four-mode contract

Каждый route должен поддерживать:

| Mode | Goal | Entry |
|---|---|---|
| Learn | построить mental model | atomic concept |
| Recall | извлечь без подсказки | stable cards |
| Practice | применить к новому input | drills/cases |
| Repair | исправить misconception | focused concept + contrast + proof |

Executable lab используется как **proof**, а не как замена предварительному reasoning.

## 3. Cognitive-load budget

```text
one session      one primary goal
one learn block  one new atomic concept
one diagram      one explanatory function
one repair       one misconception
```

Warnings:

- больше двух новых mechanisms за короткую сессию;
- несколько version boundaries в одной карточке без contrast;
- long note без 30-second model;
- drill без prediction step;
- dashboard без явного next action.

## 4. Retrieval before exposure

До повторного чтения learner должен сделать попытку ответа. Даже неполная попытка создаёт диагностический материал.

Каждый atomic lesson содержит:

```text
Before reading
Active recall
Worked prediction
Contrast
Delayed retry
```

## 5. Feedback language

Используй нейтральные диагностические формулировки.

Плохо:

```text
Ты не знаешь тему.
Слишком много ошибок.
```

Хорошо:

```text
Смешаны overload selection и runtime dispatch.
Нужен contrast example и повтор после интервала.
```

Ошибка классифицируется как `attention`, `retrieval`, `discrimination`, `concept` или `transfer`.

## 6. Visual language

Custom callouts:

```text
start       immediate next action
learn       build mental model
recall      retrieve without notes
practice    apply to new input
prove       predict and execute
repair      rebuild misconception
checkpoint  stop and assess
```

CSS implementation:

```text
.obsidian/snippets/learning-experience.css
```

Setup:

- [[00_HOME/Obsidian Learning Interface Setup]]

## 7. Route hub requirements

```text
[ ] 30-second summary
[ ] ordered atomic path
[ ] version boundary
[ ] Learn / Recall / Practice / Repair entrances
[ ] card and drill links
[ ] executable proof
[ ] weakest-concept routing
[ ] learner completion gate
[ ] previous/next route
```

## 8. Atomic lesson requirements

Use:

- [[90_TEMPLATES/Atomic Lesson UX Template]]

Minimum:

```text
mental model
worked example
plausible wrong model
contrast table
retrieval ladder
decision algorithm
cards/drills/lab links
```

## 9. Session requirements

Use:

- [[90_TEMPLATES/Learning Session Template]]

Session ends with:

```text
one learned mechanism
one classified outcome
one next action
one review date
```

## 10. Quality review questions

1. Learner видит следующее действие за 15 секунд?
2. Можно пройти route без просмотра file explorer?
3. Ошибка ведёт к одному focused concept?
4. Есть contrast для наиболее вероятной путаницы?
5. Proof запускается после prediction?
6. Interface не показывает ложный percentage mastery?
7. Цвет и diagram несут функцию, а не декор?
8. Материал доступен без обязательных community plugins?

## Related standards

- [[90_TEMPLATES/Pedagogical Visual Standard]]
- [[90_TEMPLATES/Cross-Linking Standard]]
- [[00_HOME/Java Learning Cockpit]]
- [[70_PROGRESS/Java Learning Progress Dashboard]]
