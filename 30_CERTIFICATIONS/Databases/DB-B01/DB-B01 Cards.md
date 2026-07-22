---
type: certification-batch
domain: databases
subdomain: postgresql-indexes
batch: DB-B01
status: published
card_count: 30
postgresql_versions:
  - 18
tags:
  - postgresql
  - indexes
  - explain
  - active-recall
---

# DB-B01 — Indexes and Query Plans

---

## DB-B01-C001 — What does a PostgreSQL B-tree index leaf entry point to?

### Russian Translation

На что указывает leaf entry B-tree индекса PostgreSQL?

> [!answer]- Answer
> Обычно на tuple identifier — heap block и offset соответствующей table tuple.

### Explanation

PostgreSQL index хранится отдельно от heap. Обычный Index Scan сначала находит index entry, затем идёт в heap для visibility check и получения columns, которых нет в index.

### Exam Trap

Не утверждай, что любой index lookup возвращает row без table access.

### Memory Hook

**Index finds TID; heap owns row visibility.**

---

## DB-B01-C002 — Why can a sequential scan be faster even when an index exists?

### Russian Translation

Почему sequential scan может быть быстрее при наличии индекса?

> [!answer]- Answer
> Если query возвращает большую долю table, linear heap scan может быть дешевле чтения index плюс множества scattered heap fetches.

### Explanation

Index path имеет минимум два слоя работы: index pages и heap pages. Для large result fraction planner часто рационально выбирает Seq Scan.

### Exam Trap

«Planner не использует индекс» не равно «индекс сломан».

---

## DB-B01-C003 — What is selectivity in index reasoning?

### Russian Translation

Что такое selectivity при анализе индекса?

> [!answer]- Answer
> Это оценка доли строк, которые пройдут predicate; важно называть конкретную fraction, потому что термин часто используют неоднозначно.

### Explanation

Predicate, возвращающий 1 row из миллиона, обычно лучше подходит для precise index lookup, чем predicate, возвращающий 700,000 rows.

### Exam Trap

Не оценивай usefulness index только по cardinality column без distribution и query shape.

---

## DB-B01-C004 — What most directly limits a multicolumn B-tree scan range?

### Russian Translation

Что наиболее непосредственно ограничивает диапазон multicolumn B-tree scan?

> [!answer]- Answer
> Equality constraints на leading columns и inequality constraint на первой следующей column без equality.

### Explanation

Для index `(a,b,c)` predicate `a=? AND b=? AND c>?` формирует узкий navigable range. Condition только на `c` обычно намного слабее, хотя PostgreSQL 18 может cost-based использовать skip scan.

### Exam Trap

Skip scan не отменяет необходимость осознанного порядка columns.

---

## DB-B01-C005 — How should equality, range and ordering influence composite index order?

### Russian Translation

Как equality, range и ordering влияют на порядок composite index?

> [!answer]- Answer
> Практическая отправная точка: leading equality columns, затем first range/order column, после чего remaining filtering/payload needs.

### Explanation

Это heuristic, а не универсальная формула. Финальный порядок подтверждается representative queries и plans.

### Exam Trap

Не копируй порядок columns из `WHERE` текста автоматически.

---

## DB-B01-C006 — Why can an index help an ORDER BY LIMIT query with moderate selectivity?

### Russian Translation

Почему индекс помогает `ORDER BY ... LIMIT` даже при умеренной selectivity?

> [!answer]- Answer
> Он может вернуть rows уже в требуемом порядке и остановиться после первых N без полного Sort и чтения всех matches.

### Explanation

Index `(tenant_id, created_at DESC)` хорошо соответствует `WHERE tenant_id=? ORDER BY created_at DESC LIMIT 50`.

### Exam Trap

Не оценивай index только по filtering; ordering и early stop тоже часть query shape.

---

## DB-B01-C007 — What is the difference between key and INCLUDE columns?

### Russian Translation

Чем key columns отличаются от `INCLUDE` columns?

> [!answer]- Answer
> Key columns участвуют в B-tree navigation/order; `INCLUDE` columns являются leaf payload для covering/index-only queries.

### Explanation

Payload не помогает искать range и увеличивает index size. Его добавляют только для конкретного high-value read path.

### Exam Trap

`INCLUDE` не превращает payload column в дополнительный search key.

---

## DB-B01-C008 — What conditions make an index-only scan truly avoid heap access?

### Russian Translation

Когда Index Only Scan действительно не обращается к heap?

> [!answer]- Answer
> Все нужные values должны быть доступны в index, а visibility map должна показывать all-visible для соответствующих heap pages.

### Explanation

Даже plan node `Index Only Scan` может иметь `Heap Fetches > 0`, если tuples недавно изменялись.

### Exam Trap

Название node не гарантирует нулевые heap reads.

---

## DB-B01-C009 — Why can frequent updates reduce covering-index benefit?

### Russian Translation

Почему частые updates уменьшают пользу covering index?

> [!answer]- Answer
> Updates сбрасывают all-visible bits, поэтому index-only scan снова вынужден проверять visibility в heap.

### Explanation

Read-mostly tables обычно лучше подходят для index-only optimization.

### Exam Trap

Не добавляй wide covering index на write-heavy table без измерений.

---

## DB-B01-C010 — What problem does a partial index solve?

### Russian Translation

Какую проблему решает partial index?

> [!answer]- Answer
> Он индексирует только rows, удовлетворяющие predicate, уменьшая размер и write/read cost для targeted workload.

### Explanation

Operational queue часто выигрывает от index только по `status='PENDING'`.

### Exam Trap

Planner должен доказать, что query predicate подразумевает partial-index predicate.

---

## DB-B01-C011 — When is an expression index usable?

### Russian Translation

Когда можно использовать expression index?

> [!answer]- Answer
> Когда query содержит semantically matching expression и operator, поддерживаемый index operator class.

### Explanation

Index `lower(email)` предназначен для predicates с `lower(email)`, а не для произвольного case-insensitive operation.

### Exam Trap

Скрытый cast или другая function может оставить condition в Filter, а не Index Cond.

---

## DB-B01-C012 — Why use a bitmap heap scan?

### Russian Translation

Зачем PostgreSQL использует Bitmap Heap Scan?

> [!answer]- Answer
> Чтобы собрать matching tuple locations в bitmap и посещать heap pages более упорядоченно, особенно для moderate result sets или combined indexes.

### Explanation

BitmapAnd/BitmapOr позволяет комбинировать несколько indexes. Index ordering при этом теряется.

### Exam Trap

Bitmap path не удовлетворяет `ORDER BY` автоматически.

---

## DB-B01-C013 — What does Recheck Cond mean in a bitmap plan?

### Russian Translation

Что означает `Recheck Cond` в bitmap plan?

> [!answer]- Answer
> Executor повторно проверяет condition на heap tuples, особенно для lossy bitmap pages или lossy access methods.

### Explanation

При memory pressure bitmap может хранить page-level, а не exact tuple-level information.

### Exam Trap

Не считай все rows bitmap-а exact matches.

---

## DB-B01-C014 — How do extra indexes affect writes?

### Russian Translation

Как дополнительные индексы влияют на записи?

> [!answer]- Answer
> Они увеличивают storage, WAL, INSERT/UPDATE/DELETE work, vacuum effort и могут уменьшать HOT-update opportunities.

### Explanation

Index design — trade-off read acceleration против ongoing write amplification.

### Exam Trap

Не создавай index «на всякий случай» без workload evidence.

---

## DB-B01-C015 — What is a HOT update boundary?

### Russian Translation

От чего зависит возможность HOT update?

> [!answer]- Answer
> Indexed columns не должны изменяться, и новая tuple version должна удовлетворять physical page/implementation conditions.

### Explanation

HOT может избежать новых index entries, если index keys остаются прежними.

### Exam Trap

Index на frequently updated column может превратить дешёвый update в multiple index writes.

---

## DB-B01-C016 — What does EXPLAIN show versus EXPLAIN ANALYZE?

### Russian Translation

Что показывает `EXPLAIN` в сравнении с `EXPLAIN ANALYZE`?

> [!answer]- Answer
> `EXPLAIN` показывает estimates выбранного plan; `EXPLAIN ANALYZE` выполняет query и добавляет actual time, rows и loops.

### Explanation

Для DML `ANALYZE` действительно меняет data, если execution не обёрнут rollback-able transaction.

### Exam Trap

Не запускай `EXPLAIN ANALYZE DELETE` на production как безопасный read-only command.

---

## DB-B01-C017 — Are PostgreSQL cost values milliseconds?

### Russian Translation

Являются ли cost values миллисекундами?

> [!answer]- Answer
> Нет. Это abstract planner cost units для сравнения alternatives.

### Explanation

Actual time появляется только при execution. Cost зависит от configured cost constants и estimates.

### Exam Trap

Нельзя сравнивать `cost=100` с «100 ms».

---

## DB-B01-C018 — How should actual rows and loops be read?

### Russian Translation

Как читать `actual rows` и `loops`?

> [!answer]- Answer
> Rows и timing обычно показаны per-loop averages; total work оценивается с учётом multiplication на loops.

### Explanation

Inner index scan с 3 rows и 50,000 loops произвёл около 150,000 rows.

### Exam Trap

Не игнорируй loops при nested-loop diagnosis.

---

## DB-B01-C019 — What is the first major signal of a planner-statistics problem?

### Russian Translation

Какой главный признак проблемы planner statistics?

> [!answer]- Answer
> Большое расхождение estimated rows и actual rows на раннем node plan-а.

### Explanation

Cardinality error распространяется вверх и может вызвать неверный join type/order и access path.

### Exam Trap

Не лечи wrong estimate случайным index до проверки statistics.

---

## DB-B01-C020 — What statistics does ANALYZE collect conceptually?

### Russian Translation

Какие statistics концептуально собирает `ANALYZE`?

> [!answer]- Answer
> Samples для most-common values, histograms, null fraction, distinct estimates, correlation и related planner metadata.

### Explanation

Statistics являются estimates, а не exact full-table counts.

### Exam Trap

Недостаточный sample может плохо описывать skewed distribution.

---

## DB-B01-C021 — Why are extended statistics needed?

### Russian Translation

Зачем нужны extended statistics?

> [!answer]- Answer
> Чтобы моделировать relationships между columns, которые single-column statistics считают независимыми.

### Explanation

`city` и `country` часто correlated; независимое перемножение selectivities даёт ошибку.

### Exam Trap

Composite index и extended statistics решают разные задачи: access path против cardinality model.

---

## DB-B01-C022 — When is a nested loop efficient?

### Russian Translation

Когда Nested Loop эффективен?

> [!answer]- Answer
> Когда outer input мал, а inner lookup дешёв, часто через selective index.

### Explanation

Если outer actual rows намного больше estimate, inner node выполняется огромным числом loops.

### Exam Trap

Nested Loop не плох сам по себе; плох mismatch между assumptions и actual cardinality.

---

## DB-B01-C023 — What is the basic hash join trade-off?

### Russian Translation

Каков основной trade-off Hash Join?

> [!answer]- Answer
> Он строит hash table по одному input и probe-ит другим, обычно хорошо подходит для large equality joins, но требует memory и может spill в batches.

### Explanation

Смотри batches, temp I/O и build-side size.

### Exam Trap

Увеличение `work_mem` глобально может умножить memory usage на many concurrent nodes/sessions.

---

## DB-B01-C024 — When can a merge join be attractive?

### Russian Translation

Когда Merge Join может быть выгоден?

> [!answer]- Answer
> Когда оба inputs доступны в compatible sorted order или сортировка оправдана, особенно для large ordered joins.

### Explanation

Indexes иногда поставляют required order и устраняют explicit Sort.

### Exam Trap

Merge Join требует order; он не является просто «ещё одним equality join».

---

## DB-B01-C025 — What do Rows Removed by Filter reveal?

### Russian Translation

Что показывает `Rows Removed by Filter`?

> [!answer]- Answer
> Сколько candidate rows было прочитано node-ом, но отброшено post-access filter-ом.

### Explanation

Большое значение может указывать, что predicate не вошёл в Index Cond или должен применяться раньше.

### Exam Trap

Нулевое значение не гарантирует быстрый plan; node мог дорого получить exact candidates.

---

## DB-B01-C026 — What does BUFFERS add to EXPLAIN ANALYZE?

### Russian Translation

Что добавляет `BUFFERS`?

> [!answer]- Answer
> Счётчики shared/local/temp page hits, reads, dirtied и written по nodes.

### Explanation

`shared hit` означает page уже в shared buffers, но CPU и memory processing всё равно существуют.

### Exam Trap

Не сравнивай buffer counts разных cache states без контекста.

---

## DB-B01-C027 — Why can a sort become slow suddenly?

### Russian Translation

Почему Sort может внезапно замедлиться?

> [!answer]- Answer
> Input вырос или перестал помещаться в available memory, вызвав external sort и temp I/O.

### Explanation

Plan показывает sort method и disk/memory details. Ordered index или query redesign иногда лучше простого увеличения memory.

### Exam Trap

`work_mem` применяется per operation, не как единый budget на server.

---

## DB-B01-C028 — Why are planner enable_* switches diagnostic tools rather than fixes?

### Russian Translation

Почему `enable_seqscan`, `enable_nestloop` и подобные switches — диагностика, а не исправление?

> [!answer]- Answer
> Они помогают увидеть alternative plan и проверить cost-model hypothesis, но не устраняют statistics, query или schema root cause.

### Explanation

Если forced index plan быстрее, исследуй estimate/cost mismatch.

### Exam Trap

Не оставляй global disabled plan type вместо корректной tuning work.

---

## DB-B01-C029 — What is the correct first sequence for a slow indexed query?

### Russian Translation

Какова правильная первая последовательность анализа медленного query?

> [!answer]- Answer
> Зафиксировать exact SQL и binds, выполнить `EXPLAIN (ANALYZE, BUFFERS)`, найти dominant subtree, проверить estimates, а затем менять index/statistics/query по одной причине.

### Explanation

Index creation до plan evidence часто создаёт write cost без устранения bottleneck.

### Exam Trap

Не анализируй sanitized query с другими bind values и data distribution.

---

## DB-B01-C030 — What should a Senior answer include when asked “indexes no longer help on large data”?

### Russian Translation

Что должен включать Senior-ответ на вопрос «индексы перестали помогать на больших данных»?

> [!answer]- Answer
> Проверку result fraction, heap fetches, cache/I/O, statistics, partition pruning, query shape, aggregation/join cost, index bloat/write amplification и необходимость data architecture changes.

### Explanation

Когда query действительно обрабатывает огромную fraction dataset, другой B-tree не уменьшит required work. Возможны partitioning, pre-aggregation, archival, BRIN, denormalized read model, distributed processing или workload redesign.

### Exam Trap

Шардирование не является первым автоматическим ответом на любой медленный query.

### Memory Hook

**Indexes reduce search work; they cannot erase required result work.**
