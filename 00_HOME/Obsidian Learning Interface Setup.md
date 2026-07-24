---
type: setup-guide
domain: knowledge-system
status: active
verified_at: 2026-07-24
cssclasses:
  - setup-guide
tags:
  - obsidian
  - interface
  - css
  - navigation
---

# Obsidian Learning Interface Setup

> [!summary]
> Vault работает без community plugins. Визуальный слой усиливается одним CSS snippet, а learning state остаётся в version-controlled JSON и Markdown.

## 1. Открой правильную стартовую страницу

Главная learner-facing страница:

- [[00_HOME/Java Learning Cockpit]]

Каталог материалов:

- [[00_HOME/Java Learning Dashboard]]

## 2. Включи CSS snippet

В репозитории находится:

```text
.obsidian/snippets/learning-experience.css
```

В Obsidian открой раздел CSS snippets, обнови список и включи `learning-experience`.

Snippet изменяет только представление:

- делает dashboards шире;
- выделяет режимы `learn`, `recall`, `practice`, `repair`, `checkpoint`;
- улучшает читаемость tables и callouts;
- не изменяет содержимое и learning state.

## 3. Рекомендуемая раскладка

```text
LEFT
  File explorer
  Search

CENTER
  Java Learning Cockpit
  текущая atomic note

RIGHT
  Outline
  Backlinks
  Properties
```

Canvas открывай отдельной вкладкой:

- [[01_MAPS/Java Learning Journey.canvas]]
- [[01_MAPS/Java Certification Routes.canvas]]

## 4. Визуальная легенда

> [!learn]
> **Learn** — построить новый mental model.

> [!recall]
> **Recall** — извлечь знание без подсказки.

> [!practice]
> **Practice** — применить правило к новому input.

> [!prove]
> **Prove** — сделать прогноз и проверить executable evidence.

> [!repair]
> **Repair** — исправить ошибочную модель.

> [!checkpoint]
> **Checkpoint** — остановиться и проверить качество понимания.

## 5. Что не коммитится

Не сохраняй в Git личное состояние интерфейса:

```text
.obsidian/workspace.json
.obsidian/workspace-mobile.json
.obsidian/cache/
```

Так repository остаётся переносимым, а личная раскладка окон не конфликтует между устройствами.

## 6. Learning state

```text
70_PROGRESS/card-progress.json
```

Команды и outcome model:

- [[00_HOME/Card Review Dashboard]]
- [[70_PROGRESS/Java Learning Progress Dashboard]]

## 7. Авторские стандарты

- [[90_TEMPLATES/Route Learning UX Standard]]
- [[90_TEMPLATES/Atomic Lesson UX Template]]
- [[90_TEMPLATES/Learning Session Template]]
- [[90_TEMPLATES/Pedagogical Visual Standard]]
- [[90_TEMPLATES/Cross-Linking Standard]]
