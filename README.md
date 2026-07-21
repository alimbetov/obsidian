# Java Backend Knowledge System

Структурированная Obsidian-база для Java backend: mechanism-oriented explanations, active recall, production incidents, visual models и executable labs.

## Цели

- вспомнить тему за 30 секунд перед собеседованием;
- объяснить runtime mechanism и trade-offs;
- готовиться к Java/Spring certification;
- разбирать production failures через evidence;
- связывать Java, Spring, databases, messaging и distributed systems;
- изучать topology, sequence, state и decision models, а не только линейный текст.

# С чего начать

1. [[00_HOME/Java Backend Knowledge System]] — главный маршрут.
2. [[00_HOME/Knowledge Route Registry]] — реестр всех опубликованных routes и их artifacts.
3. [[00_HOME/Review Dashboard]] — ежедневное повторение.
4. [[01_MAPS/Java Backend Map.canvas]] — общая spatial map.
5. [[01_MAPS/Spring Visual Learning Atlas.canvas]] — зрелые Spring routes.
6. [[01_MAPS/Spring Core Visual Atlas.canvas]] — container и lifecycle.
7. [[01_MAPS/Java Concurrency Visual Atlas.canvas]] — JMM, execution и diagnostics.
8. [[01_MAPS/Database Indexes and Query Plans Map.canvas]] — DB-B01.

Cross-linking rules: [[90_TEMPLATES/Cross-Linking Standard]].

# Модель знаний

```text
Canonical concept
   ├── Visual deep dive
   ├── Active-recall cards
   ├── Production cases
   ├── Comparisons
   ├── Executable lab
   ├── Canvas map
   └── Official sources
```

Visual deep dive дополняет canonical note, но не создаёт второй conflicting source of truth.

# Основные карты

- [[00_HOME/Knowledge Route Registry]]
- [[01_MAPS/Java Map]]
- [[01_MAPS/Spring Map]]
- [[01_MAPS/Databases Map]]
- [[01_MAPS/Messaging Map]]
- [[01_MAPS/Distributed Systems Map]]
- [[01_MAPS/Spring Visual Learning Atlas.canvas]]
- [[01_MAPS/Spring Core Visual Atlas.canvas]]
- [[01_MAPS/Spring AOP and Cache Visual Atlas.canvas]]
- [[01_MAPS/Java Concurrency Visual Atlas.canvas]]
- [[01_MAPS/Database Indexes and Query Plans Map.canvas]]
- [[20_QUESTIONS/Interview/Interview Questions MOC]]
- [[30_CERTIFICATIONS/Certification MOC]]

# Visual learning layer

Повторный педагогический audit показал: line count и одна общая схема не гарантируют понимание. Advanced route должен показывать разные функции visual model.

```text
Topology          → какие компоненты существуют
Sequence          → как идёт runtime interaction
State/data flow   → как меняется состояние
Decision tree     → как выбирать и диагностировать
Class model       → contracts/inheritance/proxy boundaries
```

## Current visual enrichment

```text
Spring Core Visual Deep Dive    26 diagrams
AOP Visual Deep Dive            20 diagrams
Cache Visual Deep Dive          27 diagrams
Transactions Visual Deep Dive   20 diagrams
Data JPA Visual Deep Dive       31 diagrams
Testing Visual Deep Dive        24 diagrams
Java Concurrency Visual         35 diagrams
DB-B01 visual models            62 diagrams
Standard example                 1 diagram
Canvas atlases                   5 maps
-------------------------------------------
Visual elements                251
```

## Visual deep dives

- [[10_CONCEPTS/Spring/Core/Spring Core Visual Deep Dive]]
- [[10_CONCEPTS/Spring/AOP/Spring AOP Visual Deep Dive]]
- [[10_CONCEPTS/Spring/Cache/Spring Cache Visual Deep Dive]]
- [[10_CONCEPTS/Spring/Transactions/Spring Transaction Management Visual Deep Dive]]
- [[10_CONCEPTS/Spring/Data/Spring Data JPA Visual Deep Dive]]
- [[10_CONCEPTS/Spring/Testing/Spring Testing Visual Deep Dive]]
- [[10_CONCEPTS/Java/Concurrency/Java Concurrency Visual Deep Dive]]
- [[10_CONCEPTS/Databases/PostgreSQL Index Mechanics]]
- [[10_CONCEPTS/Databases/PostgreSQL EXPLAIN and Query Plan Analysis]]
- [[90_TEMPLATES/Pedagogical Visual Standard]]

# Published routes

## Java Concurrency — visually enriched

- thread lifecycle;
- Java Memory Model and happens-before;
- visibility, atomicity and ordering;
- `volatile`, monitors, locks and atomics;
- ExecutorService, saturation and backpressure;
- Future, CompletableFuture and ForkJoinPool;
- ThreadLocal lifecycle and leaks;
- virtual threads and downstream capacity;
- concurrent collections;
- deadlock/livelock/starvation;
- 35 visual models;
- Java 8/21 labs.

Entry points:

- [[10_CONCEPTS/Java/Concurrency/Concurrency Learning Path]]
- [[10_CONCEPTS/Java/Concurrency/Java Concurrency Visual Deep Dive]]
- [[01_MAPS/Java Concurrency Visual Atlas.canvas]]
- [[50_LABS/Java/Concurrency/README]]

## Spring Core — 140 cards, visually enriched

```text
CORE-B01  20 — IoC, beans, registration, injection
CORE-B02  24 — candidate resolution and optionality
CORE-B03  24 — lifecycle
CORE-B04  24 — extension points and early references
CORE-B05  24 — configuration, profiles and properties
CORE-B06  24 — scopes, FactoryBean, cycles and hierarchy
```

Visual route covers metadata → BeanDefinition → candidate resolution → lifecycle → post-processing/proxy → scopes → configuration → startup diagnostics.

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Core Card Roadmap]]
- [[10_CONCEPTS/Spring/Core/Spring Core Visual Deep Dive]]
- [[01_MAPS/Spring Core Visual Atlas.canvas]]

## Spring AOP, Caffeine and Redis

- `AOP-B01`: 24 normalized cards;
- `CACHE-B01`: 20 normalized cards;
- 47 dedicated visual diagrams;
- AOP and Cache labs;
- 12 production cases.

Route: [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring AOP and Cache Roadmap]].

## Spring Transaction Management

- `TX-B01`: 32 cards;
- logical versus physical transactions;
- propagation, rollback-only and savepoints;
- isolation, callbacks, async boundaries;
- Transactional Outbox;
- 20 visual diagrams;
- 15 incidents and H2 lab.

Route: [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Transaction Management Roadmap]].

## Spring Data and JPA

- `DATA-B01`: 36 cards;
- persistence context, entity states, dirty checking and flush;
- repositories, Specifications, projections and pagination;
- N+1, fetch plans and locking;
- 31 visual diagrams;
- 16 incidents and H2/Hibernate lab.

Route: [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Data JPA Roadmap]].

## Spring Testing

- `TEST-B01`: 36 cards;
- TestContext, slices and context cache;
- test-managed transactions, commit boundaries and `TestTransaction`;
- H2 versus PostgreSQL/Testcontainers;
- N+1 regression;
- 24 visual diagrams;
- 16 incidents and JUnit suite.

Route: [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Testing Roadmap]].

```text
Spring Core               140
AOP and Cache               44
Transaction Management      32
Spring Data and JPA          36
Spring Testing               36
-------------------------------
Published Spring total     288 cards
```

## DB-B01 — Indexes and Query Plans

Published as a complete visual vertical slice:

```text
Canonical notes         2
Visual diagrams        62
Cards                  30
Production cases       14
PostgreSQL experiments 10
Canvas maps             1
Official source index   1
```

Coverage:

- B-tree hierarchy, leaf traversal and heap TIDs;
- selectivity, cardinality and skew;
- composite indexes and leading-prefix reasoning;
- PostgreSQL 18 skip-scan boundary;
- `ORDER BY LIMIT` and early stop;
- covering/`INCLUDE`, visibility map and `Heap Fetches`;
- partial and expression indexes;
- bitmap scans;
- write amplification and HOT;
- `EXPLAIN (ANALYZE, BUFFERS)`;
- estimates, actual rows and loops;
- planner statistics and extended statistics;
- nested-loop/hash/merge joins;
- sort/hash spill;
- OFFSET versus keyset;
- cases where indexes cannot remove required large-data work.

Entry points:

- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Roadmap]]
- [[10_CONCEPTS/Databases/PostgreSQL Index Mechanics]]
- [[10_CONCEPTS/Databases/PostgreSQL EXPLAIN and Query Plan Analysis]]
- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Cards]]
- [[50_LABS/Databases/DB-B01/README]]
- [[01_MAPS/Database Indexes and Query Plans Map.canvas]]

# Repository structure

```text
00_HOME/              entry points and dashboards
01_MAPS/              Canvas and text maps
10_CONCEPTS/          canonical and visual deep-dive notes
20_QUESTIONS/         interview recall and troubleshooting
30_CERTIFICATIONS/    card batches and roadmaps
40_PRODUCTION_CASES/  incidents with evidence and repair
50_LABS/              executable experiments
90_TEMPLATES/         note templates and visual/cross-link standards
98_SOURCES/           official primary sources
99_AUDITS/            quality and enrichment reports
```

# Running DB-B01

```bash
cd 50_LABS/Databases/DB-B01
docker compose up -d

docker compose exec -T postgres psql -U lab -d index_lab < sql/01_schema.sql
docker compose exec -T postgres psql -U lab -d index_lab < sql/02_seed.sql
docker compose exec -T postgres psql -U lab -d index_lab < sql/03_experiments.sql | tee db-b01-output.txt
```

PowerShell commands are documented in [[50_LABS/Databases/DB-B01/README]].

# Running Spring labs

```bash
cd 50_LABS/Spring/AOP-B01 && mvn clean compile exec:java
cd 50_LABS/Spring/CACHE-B01 && mvn clean compile exec:java
cd 50_LABS/Spring/TX-B01 && mvn clean compile exec:java
cd 50_LABS/Spring/DATA-B01 && mvn clean compile exec:java
cd 50_LABS/Spring/TEST-B01 && mvn clean test
```

# Quality policy

- One concept — one canonical note.
- Advanced route includes topology, sequence, state/data flow and decision tree.
- Diagram is followed by interpretation and evidence path.
- Cards contain Question, Russian Translation, Answer, Explanation and Exam Trap.
- Production case starts with observable symptom and ends with measured proof.
- Version-sensitive details are explicitly marked.
- Mermaid passes real `mermaid-cli` rendering.
- Canvas passes JSON, reference, geometry and connectivity checks.
- Published routes obey [[90_TEMPLATES/Cross-Linking Standard]].
- Route graph is machine-checked by `.github/scripts/audit_cross_links.py`.

# Current next routes

```text
DB-B02 — Transactions, MVCC and Locks
Spring Boot Internals and Auto-configuration
Java language / collections / JVM vertical slices
Messaging and Distributed Systems
```
