# Java Backend Knowledge System

Структурированная база знаний для Obsidian по Java backend-разработке.

## Цели

- вспомнить тему за 30 секунд перед собеседованием;
- изучить концепцию достаточно глубоко, чтобы объяснить mechanism и trade-offs;
- готовиться к сертификационным экзаменам Java и Spring;
- тренировать interview questions, code-output questions и production scenarios;
- связать Java, Spring, базы данных, messaging и distributed systems в едином графе;
- изучать runtime paths и failure boundaries через diagrams, Canvas и executable labs.

## С чего начать

1. Открыть [[00_HOME/Java Backend Knowledge System]].
2. Для ежедневного повторения использовать [[00_HOME/Review Dashboard]].
3. Для пространственной навигации открыть [[01_MAPS/Java Backend Map.canvas]].
4. Для углублённого визуального изучения Spring открыть [[01_MAPS/Spring Visual Learning Atlas.canvas]].

## Модель знаний

```text
Concept
   ├── Visual deep dive
   ├── Interview questions
   ├── Certification questions
   ├── Production cases
   ├── Comparisons
   └── Labs
```

Каноническая теория описывается один раз. Карточки, визуальные deep dives, кейсы и лаборатории ссылаются на неё.

## Основные карты

- [[01_MAPS/Java Map]]
- [[01_MAPS/Spring Map]]
- [[01_MAPS/Databases Map]]
- [[01_MAPS/Messaging Map]]
- [[01_MAPS/Distributed Systems Map]]
- [[01_MAPS/Spring Visual Learning Atlas.canvas]]
- [[01_MAPS/Spring AOP and Cache Visual Atlas.canvas]]
- [[01_MAPS/Spring AOP and Caching Map.canvas]]
- [[01_MAPS/Spring Transaction Management Map.canvas]]
- [[01_MAPS/Spring Data JPA Map.canvas]]
- [[01_MAPS/Spring Testing Map.canvas]]
- [[20_QUESTIONS/Interview/Interview Questions MOC]]
- [[30_CERTIFICATIONS/Certification MOC]]

# Visual learning layer

Повторный педагогический audit показал, что line count и наличие одной mental-model diagram недостаточны. Для зрелых Spring routes добавлены отдельные visual deep dives.

```text
AOP Visual Deep Dive          20 diagrams
Cache Visual Deep Dive        27 diagrams
Transactions Visual Deep Dive 20 diagrams
Data JPA Visual Deep Dive     31 diagrams
Testing Visual Deep Dive      24 diagrams
Standard example               1 diagram
Canvas atlases                 2 maps
-----------------------------------------
Total new visual elements    125
```

## Visual deep dives

- [[10_CONCEPTS/Spring/AOP/Spring AOP Visual Deep Dive]]
- [[10_CONCEPTS/Spring/Cache/Spring Cache Visual Deep Dive]]
- [[10_CONCEPTS/Spring/Transactions/Spring Transaction Management Visual Deep Dive]]
- [[10_CONCEPTS/Spring/Data/Spring Data JPA Visual Deep Dive]]
- [[10_CONCEPTS/Spring/Testing/Spring Testing Visual Deep Dive]]
- [[90_TEMPLATES/Pedagogical Visual Standard]]
- [[99_AUDITS/Pedagogical Visual Enrichment Pass]]

Visual notes используют разные типы моделей:

```text
Topology          → какие компоненты существуют
Sequence          → как идёт runtime interaction
State/data flow   → как меняется состояние
Decision tree     → как выбирать и диагностировать
Class model       → contracts и proxy/inheritance boundaries
```

# Текущие опубликованные маршруты

## Java Concurrency

- Java Memory Model и happens-before;
- visibility, atomicity и ordering;
- `volatile`, `synchronized`, locks и ThreadLocal;
- executors, CompletableFuture и virtual threads;
- atomic/CAS, deadlock и concurrent collections;
- interview recall и Java 8/21 labs.

## Spring Core Certification

- `CORE-B01`: 20 карточек — IoC, beans, registration и injection styles;
- `CORE-B02`: 24 карточки — candidate resolution, qualifiers и optionality;
- `CORE-B03`: 24 карточки — bean lifecycle, initialization и destruction;
- `CORE-B04`: 24 карточки — extension points и early references;
- `CORE-B05`: 24 карточки — configuration, profiles и properties;
- `CORE-B06`: 24 карточки — scopes, FactoryBean, lazy и hierarchy;
- всего **140 Spring Core cards**.

## Spring AOP, Caffeine and Redis

- `AOP-B01`: 24 нормализованные карточки;
- `CACHE-B01`: 20 нормализованных карточек;
- AOP lab показывает transaction/async proxy boundaries;
- Cache lab запускается с Caffeine локально и Redis через Docker Compose;
- 12 production cases связывают proxy/cache mechanics с отказами;
- AOP visual deep dive содержит 20 diagrams;
- Cache visual deep dive содержит 27 diagrams.

## Spring Transaction Management

- `TX-B01`: 32 карточки;
- logical scope против physical database transaction;
- все propagation modes;
- rollback-only и `UnexpectedRollbackException`;
- isolation and locking boundaries;
- checked/runtime rollback rules;
- `TransactionTemplate` и multiple managers;
- synchronization callbacks и transactional events;
- database/cache ordering;
- async/thread boundaries;
- отдельный deep dive по Transactional Outbox;
- 15 production incidents;
- H2 lab с наблюдаемыми commit/rollback outcomes;
- visual deep dive содержит 20 diagrams.

## Spring Data and JPA

- `DATA-B01`: 36 карточек;
- persistence context и repository/query canonical notes;
- identity map, entity states и dirty checking;
- flush vs commit;
- `persist()` vs `merge()`;
- derived queries, `@Query`, `@Modifying`;
- Specifications и dynamic query;
- projections, Page/Slice;
- N+1, fetch join и `@EntityGraph`;
- locking;
- 16 production incidents;
- H2/Hibernate lab со statement counters;
- visual deep dive содержит 31 diagram.

## Spring Testing

- `TEST-B01`: 36 карточек;
- Spring TestContext lifecycle и listeners;
- unit/slice/full-context decision model;
- `@DataJpaTest` и `TestEntityManager`;
- test-managed transaction и rollback by default;
- `@Commit`, `@Rollback`, `TestTransaction`;
- flush/clear database proof;
- context cache и `@DirtiesContext`;
- H2 slice tests;
- full-context service transaction tests;
- PostgreSQL Testcontainers;
- N+1 statement-count regression;
- 16 testing production incidents;
- visual deep dive содержит 24 diagrams.

```text
Spring Core               140
AOP and Cache               44
Transaction Management      32
Spring Data and JPA          36
Spring Testing               36
-------------------------------
Published Spring total     288
```

# Структура репозитория

```text
00_HOME/              точки входа и dashboards
01_MAPS/              Canvas и текстовые карты знаний
10_CONCEPTS/          канонические и visual deep-dive заметки
20_QUESTIONS/         интервью, сертификация и troubleshooting
30_CERTIFICATIONS/    exam objectives и маршруты подготовки
40_PRODUCTION_CASES/  реальные инженерные ситуации
50_LABS/              запускаемые примеры
60_BASES/             будущие динамические представления
90_TEMPLATES/         шаблоны и pedagogical standards
98_SOURCES/           первичные официальные источники
99_AUDITS/            quality и enrichment reports
99_ATTACHMENTS/       изображения и вложения
```

# Открытие в Obsidian

1. Клонировать репозиторий.
2. В Obsidian выбрать **Open folder as vault**.
3. Включить Canvas, Backlinks, Graph view, Properties, Templates и Bases.
4. Указать каталог шаблонов `90_TEMPLATES`.
5. Включить автоматическое обновление внутренних ссылок.

Community plugins не обязательны: база остаётся переносимым набором Markdown/Canvas-файлов.

# Запуск Spring labs

## AOP

```bash
cd 50_LABS/Spring/AOP-B01
mvn clean compile exec:java
```

## Caffeine / Redis

```bash
cd 50_LABS/Spring/CACHE-B01
mvn clean compile exec:java

docker compose up -d redis
RUN_REDIS=true mvn clean compile exec:java
```

## Transaction Management

```bash
cd 50_LABS/Spring/TX-B01
mvn clean compile exec:java
```

## Spring Data and JPA

```bash
cd 50_LABS/Spring/DATA-B01
mvn clean compile exec:java
```

## Spring Testing

```bash
cd 50_LABS/Spring/TEST-B01
mvn clean test
```

Без Docker можно запустить H2/full-context tests:

```bash
mvn -Dtest=PurchaseOrderRepositorySliceTest test
mvn -Dtest=PurchaseOrderServiceTransactionTest test
mvn -Dtest=PurchaseOrderCommitBoundaryTest test
```

PostgreSQL Testcontainers:

```bash
mvn -Dtest=PostgreSqlPurchaseOrderRepositoryTest test
```

TEST-B01 демонстрирует:

```text
@DataJpaTest slice boundary
flush + clear round trip
constraint failure at flush
dirty checking without save
N+1 SQL-count regression
Page content + count queries
service transaction rollback without test transaction
explicit TestTransaction commit/rollback
PostgreSQL native ILIKE
PostgreSQL unique constraint
```

# Языковая стратегия

- Java/Spring API и технические термины сохраняются на английском;
- объяснения пишутся на русском;
- certification questions создаются на английском с русским переводом;
- aliases используются для альтернативных названий.

# Шкала уверенности

| Значение | Состояние |
|---:|---|
| 0 | тема не изучена или не проверена |
| 1 | узнаю термин |
| 2 | понимаю с подсказкой |
| 3 | объясняю самостоятельно |
| 4 | решаю новый практический кейс |
| 5 | защищаю решение на Senior-интервью |

Confidence повышается только после active recall и transfer practice.

# Правила именования и качества

- Одна концепция — одна каноническая заметка.
- Visual deep dive дополняет canonical note, но не создаёт второй conflicting source of truth.
- Версии Java, Spring и Spring Boot хранить в properties.
- Использовать точные названия механизмов.
- Название вопроса формулировать как вопрос.
- Production case называть по наблюдаемой проблеме.
- Advanced route должен содержать topology, sequence, failure path и diagnostic tree.
- Mermaid должен проходить реальный renderer.

# Текущий фундамент

Репозиторий содержит:

- главный dashboard и Review Dashboard;
- Java и Spring Canvas maps;
- глубокий Java Concurrency route;
- Spring Core `CORE-B01`–`CORE-B06`;
- Spring AOP and Cache;
- Spring Transaction Management с Outbox;
- Spring Data JPA;
- Spring Testing с H2 и PostgreSQL Testcontainers;
- 125 новых visual elements по зрелым Spring routes;
- certification-card standard;
- production cases;
- Java и Spring labs;
- official source indexes и templates.

Следующий backend-маршрут: **DB-B01 — Indexes and Query Plans**.

Следующий Spring-маршрут: **Spring Boot Internals and Auto-configuration**.
