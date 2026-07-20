# Java Backend Knowledge System

Структурированная база знаний для Obsidian по Java backend-разработке.

## Цели

- вспомнить тему за 30 секунд перед собеседованием;
- изучить концепцию достаточно глубоко, чтобы объяснить внутреннее устройство и trade-offs;
- готовиться к сертификационным экзаменам Java и Spring;
- тренировать interview questions, code-output questions и production scenarios;
- связать Java, Spring, базы данных, очереди, распределённые системы, тестирование и DevOps в едином графе знаний.

## С чего начать

1. Открыть [[00_HOME/Java Backend Knowledge System]].
2. Для ежедневного повторения использовать [[00_HOME/Review Dashboard]].
3. Для пространственной навигации открыть [[01_MAPS/Java Backend Map.canvas]].

## Модель знаний

```text
Concept
   ├── Interview questions
   ├── Certification questions
   ├── Production cases
   ├── Comparisons
   └── Labs
```

Концепция описывается один раз. Вопросы, тесты и кейсы ссылаются на неё, а не копируют теорию.

## Основные карты

- [[01_MAPS/Java Map]]
- [[01_MAPS/Spring Map]]
- [[01_MAPS/Databases Map]]
- [[01_MAPS/Messaging Map]]
- [[01_MAPS/Distributed Systems Map]]
- [[20_QUESTIONS/Interview/Interview Questions MOC]]
- [[30_CERTIFICATIONS/Certification MOC]]

## Текущие опубликованные маршруты

### Java Concurrency

- Java Memory Model и happens-before;
- visibility, atomicity и ordering;
- `volatile`, `synchronized`, locks и ThreadLocal;
- executors, CompletableFuture и virtual threads;
- atomic/CAS, deadlock и concurrent collections;
- interview recall и Java 8/21 labs.

### Spring Core Certification

- `CORE-B01`: 20 карточек по IoC, beans, registration и injection styles;
- `CORE-B02`: 24 карточки по candidate resolution, qualifiers, collections и optional dependencies;
- `CORE-B03`: 24 карточки по bean lifecycle, init/proxy/destruction boundaries;
- `CORE-B04`: 24 карточки по container extension points, processor ordering и early creation;
- `CORE-B05`: 24 карточки по full/lite configuration, imports, profiles, Environment и externalized properties;
- concept notes, Canvas maps, production cases, source indexes и Spring 5.3 labs;
- всего опубликовано **116 Spring Core cards**.

## Структура репозитория

```text
00_HOME/              точки входа и dashboards
01_MAPS/              Canvas и текстовые карты знаний
10_CONCEPTS/          канонические заметки по концепциям
20_QUESTIONS/         интервью, сертификация, code-output и troubleshooting
30_CERTIFICATIONS/    exam objectives и маршруты подготовки
40_PRODUCTION_CASES/  реальные инженерные ситуации
50_LABS/              запускаемые примеры
60_BASES/             будущие динамические представления Obsidian Bases
90_TEMPLATES/         шаблоны заметок
98_SOURCES/           первичные официальные источники
99_ATTACHMENTS/       изображения и вложения
```

## Открытие в Obsidian

1. Клонировать репозиторий.
2. В Obsidian выбрать **Open folder as vault**.
3. Включить core plugins: Canvas, Backlinks, Graph view, Properties view, Templates и Bases.
4. Указать каталог шаблонов `90_TEMPLATES`.
5. Включить автоматическое обновление внутренних ссылок при переименовании файлов.

На первом этапе обязательные community plugins не используются. База должна оставаться переносимым набором Markdown-файлов.

## Языковая стратегия

- названия Java/Spring API и основные технические термины сохраняются на английском;
- объяснения пишутся на русском;
- сертификационные questions создаются на английском с русским переводом;
- aliases используются для альтернативных русских и английских названий.

## Шкала уверенности

| Значение | Состояние |
|---:|---|
| 0 | тема не изучена или не проверена |
| 1 | узнаю термин |
| 2 | понимаю с подсказкой |
| 3 | могу объяснить самостоятельно |
| 4 | могу решить новый практический кейс |
| 5 | могу защитить решение на Senior-интервью |

Confidence повышается только после active recall и transfer practice, а не после чтения заметки.

## Правила именования

- Одна концепция — одна каноническая заметка.
- Версии Java, Spring и Spring Boot хранить в properties.
- Использовать точные названия механизмов.
- Название вопроса формулировать как вопрос.
- Production case называть по наблюдаемой проблеме.

## Текущий фундамент

Репозиторий содержит:

- главный dashboard и Review Dashboard;
- Java и Spring Canvas maps;
- глубокий Java Concurrency route;
- Spring Core routes `CORE-B01`–`CORE-B05`;
- certification-card standard;
- production cases;
- Java и Spring labs;
- source indexes и templates.

Следующий Spring-модуль: `CORE-B06 — Advanced Core`.
