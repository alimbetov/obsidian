# Java Backend Knowledge System

Структурированная база знаний для Obsidian по Java backend-разработке.

## Цели

- вспомнить тему за 30 секунд перед собеседованием;
- изучить концепцию достаточно глубоко, чтобы объяснить механизм и trade-offs;
- готовиться к сертификационным экзаменам Java и Spring;
- тренировать interview questions, code-output questions и production scenarios;
- связать Java, Spring, базы данных, messaging и distributed systems в едином графе.

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

Каноническая теория описывается один раз. Карточки, кейсы и лаборатории ссылаются на неё.

## Основные карты

- [[01_MAPS/Java Map]]
- [[01_MAPS/Spring Map]]
- [[01_MAPS/Databases Map]]
- [[01_MAPS/Messaging Map]]
- [[01_MAPS/Distributed Systems Map]]
- [[01_MAPS/Spring AOP and Caching Map.canvas]]
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

### Spring Core Certification — completed foundation

- `CORE-B01`: 20 карточек — IoC, beans, registration и injection styles;
- `CORE-B02`: 24 карточки — candidate resolution, qualifiers, collections и optionality;
- `CORE-B03`: 24 карточки — bean lifecycle, initialization, proxy и destruction;
- `CORE-B04`: 24 карточки — extension points, processor ordering и early references;
- `CORE-B05`: 24 карточки — configuration, imports, profiles и externalized properties;
- `CORE-B06`: 24 карточки — scopes, providers, FactoryBean, lazy, cycles и context hierarchy;
- всего опубликовано **140 Spring Core cards**.

### Spring AOP, Caffeine and Redis

- `AOP-B01`: 24 карточки — AOP terminology, JDK/CGLIB, self-invocation, advisor order, transaction/async/security proxy boundaries;
- `CACHE-B01`: 20 карточек — Spring Cache, keys, stampede, Caffeine, Redis, TTL, serialization и L1/L2 invalidation;
- всего опубликовано **44 AOP/Cache cards**;
- `AOP-B01` lab показывает настоящий transaction interceptor, async executor, final/private boundaries и advisor inspection;
- `CACHE-B01` lab запускается с Caffeine локально и Redis через Docker Compose;
- 12 production cases связывают proxy и cache mechanics с реальными отказами.

```text
Published Spring cards total: 184
```

## Структура репозитория

```text
00_HOME/              точки входа и dashboards
01_MAPS/              Canvas и текстовые карты знаний
10_CONCEPTS/          канонические заметки по концепциям
20_QUESTIONS/         интервью, сертификация и troubleshooting
30_CERTIFICATIONS/    exam objectives и маршруты подготовки
40_PRODUCTION_CASES/  реальные инженерные ситуации
50_LABS/              запускаемые примеры
60_BASES/             будущие динамические представления
90_TEMPLATES/         шаблоны заметок
98_SOURCES/           первичные официальные источники
99_ATTACHMENTS/       изображения и вложения
```

## Открытие в Obsidian

1. Клонировать репозиторий.
2. В Obsidian выбрать **Open folder as vault**.
3. Включить Canvas, Backlinks, Graph view, Properties, Templates и Bases.
4. Указать каталог шаблонов `90_TEMPLATES`.
5. Включить автоматическое обновление внутренних ссылок.

Community plugins не обязательны: база остаётся переносимым набором Markdown/Canvas-файлов.

## Запуск новых Spring labs

### AOP

```bash
cd 50_LABS/Spring/AOP-B01
mvn clean compile exec:java
```

### Caffeine

```bash
cd 50_LABS/Spring/CACHE-B01
mvn clean compile exec:java
```

### Redis

```bash
cd 50_LABS/Spring/CACHE-B01
docker compose up -d redis
RUN_REDIS=true mvn clean compile exec:java
```

## Языковая стратегия

- Java/Spring API и технические термины сохраняются на английском;
- объяснения пишутся на русском;
- certification questions создаются на английском с русским переводом;
- aliases используются для альтернативных названий.

## Шкала уверенности

| Значение | Состояние |
|---:|---|
| 0 | тема не изучена или не проверена |
| 1 | узнаю термин |
| 2 | понимаю с подсказкой |
| 3 | объясняю самостоятельно |
| 4 | решаю новый практический кейс |
| 5 | защищаю решение на Senior-интервью |

Confidence повышается только после active recall и transfer practice.

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
- завершённый Spring Core route `CORE-B01`–`CORE-B06`;
- Spring AOP and Proxy route;
- Spring Cache route с Caffeine и Redis;
- certification-card standard;
- production cases;
- Java и Spring labs;
- source indexes и templates.

Следующий Spring-маршрут: **Transaction Management Deep Dive**.
