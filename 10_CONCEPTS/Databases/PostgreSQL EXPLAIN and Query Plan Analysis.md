---
type: concept
domain: databases
subdomain: postgresql-planner
difficulty: advanced
status: learning
confidence: 0
interview: true
postgresql_versions:
  - 18
  - current
production_relevance: critical
prerequisites:
  - "[[PostgreSQL Index Mechanics]]"
tags:
  - postgresql
  - explain
  - query-plan
  - performance
  - visual-learning
---

# PostgreSQL EXPLAIN and Query Plan Analysis

> [!summary] За 30 секунд
> Query plan — дерево iterators. Каждый parent запрашивает rows у children. `EXPLAIN` показывает estimates, `EXPLAIN ANALYZE` реально выполняет query и показывает actual time/rows/loops. Диагностика начинается не с поиска слова `Index`, а с расхождения estimated/actual rows, multiplicative `loops`, buffer I/O, removed rows, sort/hash spill и доминирующего subtree.

# 1. Planner pipeline

```mermaid
flowchart LR
    SQL["SQL text"] --> PARSE["Parse tree"]
    PARSE --> REWRITE["Rewrite rules / views"]
    REWRITE --> REL["Relational alternatives"]
    REL --> COST["Cost model + statistics"]
    COST --> PLAN["Chosen plan tree"]
    PLAN --> EXEC["Executor"]
```

Planner выбирает cheapest estimated plan, а не objectively fastest plan. Ошибочные statistics → ошибочная cardinality → ошибочный join/access choice.

# 2. Plan as tree

```mermaid
flowchart TB
    LIMIT["Limit"] --> SORT["Sort"]
    SORT --> JOIN["Hash Join"]
    JOIN --> SCAN1["Seq Scan orders"]
    JOIN --> HASH["Hash"]
    HASH --> SCAN2["Seq Scan clients"]
```

Читать план удобно снизу вверх: children производят rows, parent их преобразует.

# 3. Cost fields

```text
(cost=startup_cost..total_cost rows=estimated_rows width=average_row_bytes)
```

```mermaid
flowchart LR
    START["startup cost"] --> FIRST["work before first row"]
    TOTAL["total cost"] --> ALL["estimated work for all rows"]
    ROWS["rows"] --> CARD["estimated cardinality per execution"]
    WIDTH["width"] --> BYTES["estimated row width"]
```

Cost units не milliseconds. Они служат для сравнения alternatives внутри configured cost model.

# 4. Actual fields

```text
(actual time=first_row..last_row rows=actual_rows loops=executions)
```

```mermaid
flowchart LR
    TIME["actual time"] --> LOOP["reported per loop average"]
    ROWS["actual rows"] --> PER["rows per loop"]
    LOOPS["loops"] --> TOTAL["Total work ≈ per-loop values × loops"]
```

Очень частая ошибка — увидеть `actual time=0.05` и проигнорировать `loops=100000`.

# 5. EXPLAIN versus EXPLAIN ANALYZE

```mermaid
flowchart TD
    E["EXPLAIN"] --> SAFE["Does not execute normal query"]
    E --> EST["Estimates only"]
    EA["EXPLAIN ANALYZE"] --> RUN["Executes query"]
    EA --> ACT["Actual timing/rows/loops"]
    RUN --> RISK["DML changes data unless wrapped and rolled back"]
```

Для `UPDATE/DELETE/INSERT` используй transaction + rollback при диагностике.

# 6. BUFFERS interpretation

```mermaid
flowchart TD
    BUF["Buffers"] --> HIT["shared hit: found in shared buffers"]
    BUF --> READ["shared read: loaded from storage"]
    BUF --> DIRT["dirtied"]
    BUF --> WR["written"]
    HIT --> NOTE["Hit is not free: CPU and memory access remain"]
```

Buffers считаются page accesses, не distinct pages для каждого node aggregation. Сравнивай plans на сходном cache state.

# 7. Sequential scan

```mermaid
flowchart LR
    HEAP["Heap page 0"] --> P1["Heap page 1"]
    P1 --> P2["Heap page 2"]
    P2 --> PN["..."]
    PN --> FILTER["Apply filter to tuples"]
```

Seq Scan рационален для small table, large result fraction, poor index match или когда sequential I/O дешевле many random heap fetches.

# 8. Index Scan

```mermaid
flowchart LR
    INDEX["Index traversal"] --> TIDS["Matching TIDs"]
    TIDS --> HEAP["Heap fetch per candidate"]
    HEAP --> OUTPUT["Rows"]
```

Plan properties:

```text
Index Cond    → condition used to navigate index
Filter        → condition checked after candidate row retrieval
Rows Removed by Filter → wasted candidate work
```

# 9. Index Only Scan

```mermaid
flowchart TD
    IOS["Index Only Scan"] --> COVER["Columns covered"]
    COVER --> VM["Visibility map"]
    VM --> ZERO{"Heap Fetches = 0?"}
    ZERO -->|"Yes"| PURE["No heap tuple visits"]
    ZERO -->|"No"| MIXED["Some visibility checks hit heap"]
```

# 10. Bitmap path

```mermaid
flowchart TB
    BI["Bitmap Index Scan"] --> BM["Bitmap of heap locations"]
    BM --> BH["Bitmap Heap Scan"]
    BH --> EXACT["Exact pages"]
    BH --> LOSSY["Lossy pages under memory pressure"]
    LOSSY --> RECHECK["Recheck Cond"]
```

Bitmap path часто выигрывает для moderate result set: он группирует heap accesses по physical pages.

# 11. Filter placement

```mermaid
flowchart TD
    PRED["Predicate"] --> COND{"Can access method use it?"}
    COND -->|"Yes"| INDEXCOND["Index Cond"]
    COND -->|"No"| FILTER["Filter after row retrieval"]
    FILTER --> REMOVED["Rows Removed by Filter"]
```

Expression/operator/cast mismatch может переместить условие из `Index Cond` в `Filter`.

# 12. Estimate error ratio

```mermaid
flowchart LR
    EST["estimated rows = 10"] --> RATIO["actual rows = 100000"]
    RATIO --> BAD["10,000× underestimation"]
    BAD --> JOIN["Planner may choose nested loop incorrectly"]
```

Диагностируй estimate error на earliest node, где divergence становится большой; upstream errors часто являются следствием.

# 13. Single-column statistics

```mermaid
flowchart LR
    ANALYZE["ANALYZE sample"] --> MCV["Most common values"]
    ANALYZE --> HIST["Histogram bounds"]
    ANALYZE --> ND["n_distinct"]
    ANALYZE --> NULL["null fraction"]
    ANALYZE --> CORR["correlation"]
    MCV --> EST["Selectivity estimate"]
    HIST --> EST
    ND --> EST
```

# 14. Correlated columns problem

```mermaid
flowchart LR
    CITY["city='Almaty'"] --> EST1["estimate independently"]
    COUNTRY["country='KZ'"] --> EST2["estimate independently"]
    EST1 --> MULT["multiply selectivities"]
    EST2 --> MULT
    MULT --> WRONG["Underestimate because columns are correlated"]
```

Extended statistics can model dependencies, ndistinct combinations или common combinations.

# 15. Extended statistics

```mermaid
flowchart TD
    COLS["Related columns"] --> DEP["dependencies"]
    COLS --> NDIS["ndistinct"]
    COLS --> MCV["multivariate MCV"]
    DEP --> BETTER["Better cardinality estimates"]
    NDIS --> BETTER
    MCV --> BETTER
```

```sql
CREATE STATISTICS clients_city_country_stats
    (dependencies, ndistinct, mcv)
ON city, country
FROM clients;
ANALYZE clients;
```

# 16. Nested Loop

```mermaid
sequenceDiagram
    participant O as Outer node
    participant I as Inner node

    O-->>I: outer row 1 → execute inner
    I-->>O: matches
    O-->>I: outer row 2 → execute inner
    I-->>O: matches
    Note over O,I: inner loops equals outer rows in common case
```

Nested loop excellent when outer small and inner indexed. Catastrophic when outer unexpectedly huge.

# 17. Hash Join

```mermaid
flowchart LR
    BUILD["Build-side scan"] --> HASH["Hash table"]
    PROBE["Probe-side scan"] --> LOOKUP["Hash lookup"]
    HASH --> LOOKUP
    LOOKUP --> OUT["Joined rows"]
```

Hash spill indicators: multiple batches, temp read/write, memory limits.

# 18. Merge Join

```mermaid
flowchart LR
    LEFT["Sorted left input"] --> MERGE["Merge Join"]
    RIGHT["Sorted right input"] --> MERGE
    MERGE --> OUT["Joined rows"]
```

Если inputs уже ordered по indexes, merge join может избежать explicit sort.

# 19. Join-choice diagnostic

```mermaid
flowchart TD
    JOIN["Slow join"] --> EST{"Cardinality estimates accurate?"}
    EST -->|"No"| STATS["Fix statistics/predicates first"]
    EST -->|"Yes"| OUTER{"Outer rows small?"}
    OUTER -->|"Yes"| NL["Nested loop may fit"]
    OUTER -->|"No"| EQ{"Equality join and memory adequate?"}
    EQ -->|"Yes"| HASH["Hash join candidate"]
    EQ -->|"No / sorted"| MERGE["Merge join candidate"]
```

# 20. Sort node

```mermaid
flowchart TD
    INPUT["Input rows"] --> SORT["Sort"]
    SORT --> MEM{"Fits work_mem?"}
    MEM -->|"Yes"| QUICK["quicksort / top-N heapsort"]
    MEM -->|"No"| DISK["external merge + temp I/O"]
```

`ORDER BY ... LIMIT` может использовать top-N strategy. Подходящий ordered index иногда устраняет Sort полностью.

# 21. Hash aggregation

```mermaid
flowchart TD
    ROWS["Input rows"] --> HASH["HashAggregate"]
    HASH --> GROUPS["Groups"]
    HASH --> BATCH{"Batches > 1?"}
    BATCH -->|"Yes"| SPILL["Disk spill / temp I/O"]
```

# 22. Parallel plan

```mermaid
flowchart TB
    G["Gather / Gather Merge"] --> W1["Worker partial plan"]
    G --> W2["Worker partial plan"]
    G --> LEADER["Leader participation"]
```

Parallelism имеет startup/coordination cost и не лечит плохую selectivity или неправильный join order.

# 23. LIMIT and early stop

```mermaid
flowchart LR
    IDX["Ordered index scan"] --> ROW1["Row 1"]
    ROW1 --> ROW50["Row 50"]
    ROW50 --> STOP["Limit stops child"]
```

Parent may stop child before it produces its estimated full row count. Поэтому actual rows child может отражать demand parent-а.

# 24. Loops multiplication example

```text
Index Scan on order_lines
(actual time=0.010..0.012 rows=3 loops=50000)
```

```mermaid
flowchart LR
    PER["≈3 rows per loop"] --> MUL["× 50,000 loops"]
    MUL --> TOTAL["≈150,000 rows produced"]
```

# 25. Rows Removed by Filter

```mermaid
flowchart LR
    SCAN["Read 1,000,000 candidates"] --> KEEP["Return 100"]
    SCAN --> REMOVE["Rows Removed by Filter: 999,900"]
    REMOVE --> QUESTION["Can predicate move into index/join condition or earlier node?"]
```

# 26. Planning versus execution time

```mermaid
flowchart TD
    TOTAL["Observed latency"] --> PLAN["Planning time"]
    TOTAL --> EXEC["Execution time"]
    TOTAL --> CLIENT["Network, serialization, pool wait, app code"]
```

`EXPLAIN ANALYZE` execution time не включает всю application latency.

# 27. Cache-state comparison

```mermaid
flowchart LR
    COLD["Cold-ish cache run"] --> READ["shared read high"]
    WARM["Warm cache run"] --> HIT["shared hit high"]
    READ --> COMPARE["Do not compare blindly"]
    HIT --> COMPARE
```

Записывай cache state и повторяй measurements. Не очищай OS cache на production ради теста.

# 28. Safe DML analysis

```sql
BEGIN;
EXPLAIN (ANALYZE, BUFFERS, WAL)
UPDATE accounts
SET status = 'REVIEW'
WHERE risk_score > 900;
ROLLBACK;
```

```mermaid
flowchart LR
    BEGIN["BEGIN"] --> RUN["Execute DML with ANALYZE"]
    RUN --> OBSERVE["Observe plan, buffers, WAL"]
    OBSERVE --> ROLLBACK["ROLLBACK"]
```

Triggers/external side effects могут не откатываться универсально; тестируй в isolated environment.

# 29. Planner toggles — diagnostic only

```mermaid
flowchart TD
    BAD["Suspect plan"] --> TOGGLE["Temporarily disable seqscan/nestloop/etc"]
    TOGGLE --> ALT["Observe alternative plan"]
    ALT --> FASTER{"Actually faster?"}
    FASTER -->|"Yes"| WHY["Investigate cost/statistics mismatch"]
    FASTER -->|"No"| PLANNER["Original plan likely rational"]
```

Не оставляй global planner toggles как permanent index hint replacement.

# 30. EXPLAIN diagnostic sequence

```mermaid
flowchart TD
    START["Slow query"] --> CAPTURE["Capture exact SQL, bind values, schema, data volume"]
    CAPTURE --> PLAN["EXPLAIN ANALYZE BUFFERS"]
    PLAN --> TOP["Find dominant subtree by time/buffers/loops"]
    TOP --> EST{"Large estimate error?"}
    EST -->|"Yes"| STATS["ANALYZE, statistics target, extended stats, predicate review"]
    EST -->|"No"| IO{"I/O or CPU dominant?"}
    IO -->|"I/O"| ACCESS["Access path, heap fetches, selectivity, index design"]
    IO -->|"CPU"| FILTER["Rows processed, expressions, joins, aggregation"]
    ACCESS --> RETEST["Change one variable and re-run"]
    FILTER --> RETEST
```

# 31. Worked example — wrong nested loop

Query joins 100,000 recent operations to clients.

Bad estimate:

```text
Nested Loop
  estimated outer rows: 50
  actual outer rows: 100,000
  inner Index Scan loops: 100,000
```

```mermaid
flowchart LR
    STALE["Stale/correlated statistics"] --> LOW["Outer estimate 50"]
    LOW --> NL["Choose Nested Loop"]
    NL --> LOOPS["100,000 inner index probes"]
    LOOPS --> SLOW["High buffer traffic and latency"]
```

Correction path:

```text
1. Check predicate and bind values.
2. ANALYZE relevant tables.
3. Increase statistics target for skewed column.
4. Add extended statistics for correlated predicates.
5. Re-run plan; do not immediately add random indexes.
```

# 32. Worked example — index exists but Seq Scan wins

```sql
SELECT * FROM events WHERE status = 'PROCESSED';
```

If 92% rows are processed:

```mermaid
flowchart LR
    IDX["status index"] --> MANY["920,000 TIDs"]
    MANY --> HEAP["Most heap pages visited"]
    HEAP --> TWO["Index + heap overhead"]
    SEQ["Seq Scan"] --> ONCE["Read heap once"]
    ONCE --> WIN["Cheaper plan"]
```

Planner not using index can be correct.

# 33. Interview explanation

> Я читаю plan снизу вверх. Сначала нахожу node с dominant actual time, buffers или loops. Затем сравниваю estimated и actual rows. Если estimate сильно ошибочен, исправляю statistics/predicate model до index tuning. Для access path смотрю `Index Cond`, `Filter`, heap fetches, rows removed и result fraction. После изменения повторяю `EXPLAIN (ANALYZE, BUFFERS)` на representative data.

# 34. Exercises

1. Найти estimate error в provided nested-loop plan.
2. Сравнить Seq Scan, Index Scan и Bitmap Heap Scan на разной selectivity.
3. Вызвать disk sort маленьким `work_mem`.
4. Создать extended statistics для correlated columns.
5. Сравнить warm/cold-ish buffer profile.
6. Проверить `Heap Fetches` index-only scan.

## Related materials

- [[PostgreSQL Index Mechanics]]
- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Roadmap]]
- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Cards]]
- [[40_PRODUCTION_CASES/Databases/Indexes and Query Plans Production Cases]]
- [[50_LABS/Databases/DB-B01/README]]
- [[98_SOURCES/PostgreSQL Indexes and Query Plans Sources]]
