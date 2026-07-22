---
type: concept
domain: databases
subdomain: postgresql-indexes
difficulty: advanced
status: learning
confidence: 0
interview: true
postgresql_versions:
  - 18
  - current
production_relevance: critical
related:
  - "[[PostgreSQL EXPLAIN and Query Plan Analysis]]"
tags:
  - postgresql
  - indexes
  - btree
  - performance
  - visual-learning
---

# PostgreSQL Index Mechanics

> [!summary] За 30 секунд
> PostgreSQL index — отдельная структура рядом с heap table. B-tree помогает быстро сузить диапазон candidate TIDs, но обычный Index Scan всё равно часто читает heap pages. Польза индекса определяется не фактом его существования, а selectivity, correlation, number of heap visits, ordering requirements, visibility map, write cost и соответствием predicates operator class-у.

# 1. Heap и secondary index

```mermaid
flowchart LR
    Q["Query predicate"] --> IDX["B-tree index pages"]
    IDX --> TID["Tuple IDs: heap block + offset"]
    TID --> HEAP["Heap pages"]
    HEAP --> ROW["Visible rows"]
```

PostgreSQL indexes являются secondary: leaf entry обычно ведёт к heap tuple location. Поэтому «нашёл key в index» не всегда означает «получил всю row без table access».

# 2. B-tree page hierarchy

```mermaid
flowchart TB
    ROOT["Root page"] --> I1["Internal page: lower keys"]
    ROOT --> I2["Internal page: higher keys"]
    I1 --> L1["Leaf page 1"]
    I1 --> L2["Leaf page 2"]
    I2 --> L3["Leaf page 3"]
    I2 --> L4["Leaf page 4"]
    L1 --> T1["TIDs"]
    L2 --> T2["TIDs"]
    L3 --> T3["TIDs"]
    L4 --> T4["TIDs"]
```

Upper levels направляют поиск; leaf pages содержат ordered keys и tuple references. Tree height обычно невелик, но итоговая стоимость может определяться heap random access.

# 3. Equality lookup path

```mermaid
sequenceDiagram
    participant E as Executor
    participant R as Root page
    participant I as Internal page
    participant L as Leaf page
    participant H as Heap page

    E->>R: search key = 42
    R-->>E: choose child range
    E->>I: descend
    I-->>E: choose leaf
    E->>L: locate key 42
    L-->>E: return TID
    E->>H: fetch tuple and check visibility
    H-->>E: visible row
```

# 4. Range scan

```mermaid
flowchart LR
    SEEK["Seek first key >= 100"] --> L1["Leaf page"]
    L1 --> L2["Next leaf page"]
    L2 --> L3["Next leaf page"]
    L3 --> STOP["Stop after key > 200"]
```

B-tree особенно полезен для ordered ranges и `ORDER BY`, если query order соответствует index order.

# 5. Selectivity

```mermaid
flowchart TD
    PRED["Predicate"] --> EST["Estimated matching rows"]
    EST --> FEW{"Small fraction of table?"}
    FEW -->|"Yes"| INDEX["Index path may win"]
    FEW -->|"No"| SEQ["Sequential scan may be cheaper"]
```

Selectivity ≈ доля rows, прошедших predicate. Низкая selectivity в разговорной практике часто означает «predicate возвращает много rows», хотя термины иногда используют неоднозначно; лучше говорить exact fraction.

# 6. Why index may lose on many rows

```mermaid
flowchart LR
    INDEX["Read index pages"] --> MANY["Many TIDs"]
    MANY --> RANDOM["Many heap page visits"]
    RANDOM --> COST["Random I/O + visibility checks"]
    SEQ["Sequential scan"] --> LINEAR["Read heap pages linearly"]
    LINEAR --> COST2["Lower per-page overhead"]
```

Если query возвращает значительную часть table, sequential access может быть дешевле двухступенчатого index→heap path.

# 7. Cardinality and distinct values

```mermaid
flowchart LR
    ROWS["1,000,000 rows"] --> STATUS["status has 4 distinct values"]
    STATUS --> ONE["status='ACTIVE' may match 600,000"]
    ROWS --> ID["id has 1,000,000 distinct values"]
    ID --> UNIQUE["id=42 matches 1"]
```

Index на low-cardinality column не бесполезен всегда, но isolated equality predicate часто возвращает слишком большую fraction. Partial index или composite index может изменить economics.

# 8. Composite B-tree ordering

```mermaid
flowchart TB
    IDX["Index (tenant_id, status, created_at)"] --> T1["tenant A"]
    IDX --> T2["tenant B"]
    T1 --> S1["ACTIVE"]
    T1 --> S2["CLOSED"]
    S1 --> D1["created_at ordered"]
```

Index order группирует сначала leading key, затем следующий внутри equal prefix.

# 9. Leftmost-prefix reasoning

```mermaid
flowchart TD
    IDX["Index (a,b,c)"] --> Q1["a = ?"]
    IDX --> Q2["a = ? AND b = ?"]
    IDX --> Q3["a = ? AND b BETWEEN ? AND ?"]
    IDX --> Q4["b = ? only"]
    Q1 --> GOOD["Efficient prefix navigation"]
    Q2 --> GOOD
    Q3 --> RANGE["Range limits later navigation"]
    Q4 --> MAYBE["Usually weak; current PostgreSQL may consider skip scan when profitable"]
```

Equality constraints on leading columns plus inequality on first non-equality column most directly limit scan range. PostgreSQL 18 documents B-tree skip scan, but it is cost-based and not a reason to ignore index order design.

# 10. Equality, range, order rule

```mermaid
flowchart LR
    E["Equality columns"] --> R["First range column"]
    R --> O["Remaining ordering / filtering columns"]
```

Практическое начало дизайна composite index: leading equality predicates, затем range/order requirements. Реальное решение подтверждается workload и plan-ом.

# 11. Index and ORDER BY

```mermaid
flowchart TD
    Q["WHERE tenant_id=? ORDER BY created_at DESC LIMIT 50"] --> IDX["Index (tenant_id, created_at DESC)"]
    IDX --> SEEK["Seek tenant range"]
    SEEK --> FIRST["Read first 50 in required order"]
    FIRST --> NOSORT["No explicit Sort node"]
```

Index может быть полезен даже когда predicate не супер-selective, если позволяет early stop для `LIMIT` и устраняет sort.

# 12. Ordinary Index Scan

```mermaid
sequenceDiagram
    participant I as Index
    participant H as Heap
    participant E as Executor

    E->>I: scan matching entries
    loop each candidate
        I-->>E: TID
        E->>H: fetch heap tuple
        H-->>E: visibility + requested columns
    end
```

# 13. Index-only scan prerequisites

```mermaid
flowchart TD
    Q["Query"] --> COVER{"All needed columns available in index?"}
    COVER -->|"No"| NORMAL["Heap visit required"]
    COVER -->|"Yes"| VM{"Heap page all-visible in visibility map?"}
    VM -->|"Yes"| ONLY["Return from index"]
    VM -->|"No"| FETCH["Heap fetch for visibility"]
```

`Index Only Scan` node может показывать `Heap Fetches > 0`; название plan node не гарантирует нулевой heap access.

# 14. INCLUDE covering index

```mermaid
flowchart TB
    ROOT["Upper B-tree levels: key columns"] --> LEAF["Leaf tuple"]
    LEAF --> KEY["tenant_id, created_at"]
    LEAF --> PAYLOAD["INCLUDE amount, status"]
```

`INCLUDE` columns — payload: они доступны для index-only result, но не участвуют в navigation/search ordering. Они увеличивают index size и write cost.

```sql
CREATE INDEX orders_tenant_created_cover_idx
    ON orders (tenant_id, created_at DESC)
    INCLUDE (amount, status);
```

# 15. Visibility map effect

```mermaid
flowchart LR
    STABLE["Mostly unchanged heap pages"] --> VAC["VACUUM marks all-visible"]
    VAC --> VM["Visibility map bit set"]
    VM --> FAST["Index-only scan avoids heap"]
    UPDATE["Recent updates"] --> CLEAR["All-visible bit cleared"]
    CLEAR --> HEAP["Heap Fetches increase"]
```

Read-mostly tables обычно получают больше пользы от covering indexes, чем frequently updated tables.

# 16. Partial index

```mermaid
flowchart LR
    TABLE["orders: all statuses"] --> PART["Index only WHERE status='PENDING'"]
    PART --> SMALL["Smaller index"]
    SMALL --> HOT["Fast operational queue query"]
```

```sql
CREATE INDEX orders_pending_created_idx
    ON orders (created_at)
    WHERE status = 'PENDING';
```

Planner должен доказать, что query predicate подразумевает partial-index predicate. Параметризованные/generalized predicates могут мешать такому доказательству.

# 17. Expression index

```mermaid
flowchart LR
    INPUT["email"] --> EXPR["lower(email)"]
    EXPR --> IDX["B-tree expression index"]
    QUERY["WHERE lower(email)=lower(?)"] --> IDX
```

```sql
CREATE INDEX users_email_lower_idx ON users (lower(email));
```

Query expression должен семантически совпадать с indexed expression и подходящим operator class.

# 18. Bitmap scan composition

```mermaid
flowchart TD
    I1["Bitmap Index Scan on status"] --> AND["BitmapAnd"]
    I2["Bitmap Index Scan on region"] --> AND
    AND --> HEAP["Bitmap Heap Scan"]
    HEAP --> ORDER["Heap pages visited in physical order"]
```

Bitmap strategy amortizes heap access and может комбинировать indexes, но теряет index ordering, поэтому `ORDER BY` может потребовать Sort.

# 19. Bitmap versus plain index scan

```mermaid
flowchart TD
    MATCH["Expected matches"] --> FEW{"Very few scattered rows?"}
    FEW -->|"Yes"| PLAIN["Plain Index Scan"]
    FEW -->|"No, moderate set"| BITMAP["Bitmap Index + Heap Scan"]
    BITMAP --> LOSSY{"Bitmap memory pressure?"}
    LOSSY -->|"Yes"| RECHECK["Lossy pages + Recheck Cond"]
```

# 20. Unique index

```mermaid
sequenceDiagram
    participant T as Transaction
    participant U as Unique index

    T->>U: insert key K
    U->>U: check conflicting visible/in-progress entries
    alt conflict
        U-->>T: unique violation or wait
    else free
        U-->>T: index entry accepted
    end
```

Unique constraint — correctness mechanism, не только performance feature.

# 21. Write amplification

```mermaid
flowchart LR
    INSERT["INSERT row"] --> HEAP["Heap write"]
    INSERT --> I1["Index 1 write"]
    INSERT --> I2["Index 2 write"]
    INSERT --> I3["Index 3 write"]
    UPDATE["UPDATE indexed column"] --> NEW["New heap tuple + index updates"]
```

Каждый дополнительный index увеличивает storage, WAL, vacuum work и modification latency.

# 22. HOT update boundary

```mermaid
flowchart TD
    UPDATE["UPDATE row"] --> IDXCOL{"Indexed column changed?"}
    IDXCOL -->|"Yes"| INDEXWRITE["New index entries required"]
    IDXCOL -->|"No"| SPACE{"Space on same heap page and conditions allow?"}
    SPACE -->|"Yes"| HOT["HOT chain may avoid index update"]
    SPACE -->|"No"| INDEXWRITE
```

Наличие лишних indexes на frequently changed columns уменьшает HOT opportunities.

# 23. Correlation and heap locality

```mermaid
flowchart LR
    IDX["Index key order"] --> CORR{"Correlated with heap physical order?"}
    CORR -->|"High"| LOCAL["Fewer random heap jumps"]
    CORR -->|"Low"| RANDOM["Scattered heap pages"]
```

Planner statistics учитывают correlation при cost estimation.

# 24. BRIN versus B-tree intuition

```mermaid
flowchart TD
    TABLE["Very large append-ordered table"] --> CORR{"Column correlated with physical order?"}
    CORR -->|"Yes"| BRIN["BRIN summarizes page ranges"]
    CORR -->|"No / precise lookup"| BTREE["B-tree"]
```

DB-B01 сфокусирован на B-tree, но на огромных time-series tables компактный BRIN иногда рациональнее ещё одного большого B-tree.

# 25. Index design decision tree

```mermaid
flowchart TD
    Q["Slow query"] --> FILTER["List predicates, joins, order, limit, selected columns"]
    FILTER --> PLAN["Run EXPLAIN ANALYZE BUFFERS"]
    PLAN --> EST{"Estimate error?"}
    EST -->|"Yes"| STATS["Fix statistics/data correlation first"]
    EST -->|"No"| ACCESS{"Dominant access cost?"}
    ACCESS -->|"Large table scan"| SELECT["Assess selectivity and useful prefix"]
    ACCESS -->|"Sort"| ORDER["Consider index order + LIMIT"]
    ACCESS -->|"Heap fetches"| COVER["Assess covering index and visibility"]
    SELECT --> WRITE["Check write/storage cost"]
    ORDER --> WRITE
    COVER --> WRITE
    WRITE --> TEST["Re-run plan with realistic data"]
```

# 26. Worked example — client operation feed

Query:

```sql
SELECT id, amount, status, created_at
FROM operations
WHERE client_id = :clientId
  AND status = 'SUCCESS'
ORDER BY created_at DESC
LIMIT 50;
```

Naive indexes:

```sql
CREATE INDEX operations_client_idx ON operations(client_id);
CREATE INDEX operations_status_idx ON operations(status);
CREATE INDEX operations_created_idx ON operations(created_at);
```

Potential workload-aligned index:

```sql
CREATE INDEX operations_client_status_created_idx
    ON operations(client_id, status, created_at DESC)
    INCLUDE (amount);
```

```mermaid
flowchart LR
    CLIENT["client_id equality"] --> STATUS["status equality"]
    STATUS --> DATE["created_at DESC order"]
    DATE --> LIMIT["Stop after 50"]
    LIMIT --> PAYLOAD["amount from INCLUDE when index-only conditions hold"]
```

Это не универсальная рекомендация. Нужно проверить:

```text
actual rows and loops
Buffers: shared hit/read
Sort node absence/presence
Heap Fetches
write rate and index size
alternative partial index for SUCCESS/PENDING workload
```

# 27. Interview explanation

> B-tree не делает query быстрым сам по себе. Я сначала определяю query shape: equality, range, order, limit и selected columns. Затем проверяю selectivity и composite prefix. После этого смотрю physical path: plain index scan, bitmap, heap visits или index-only scan. Решение подтверждаю `EXPLAIN (ANALYZE, BUFFERS)`, а затем оцениваю write amplification и production workload.

# 28. Exercises

1. Для index `(tenant_id, status, created_at)` оценить пять разных predicates.
2. Сравнить three single-column indexes с composite index.
3. Создать covering index и наблюдать `Heap Fetches` до/после `VACUUM`.
4. Создать partial index для operational queue.
5. Измерить INSERT throughput до и после добавления пяти indexes.

## Related materials

- [[PostgreSQL EXPLAIN and Query Plan Analysis]]
- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Roadmap]]
- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Cards]]
- [[40_PRODUCTION_CASES/Databases/Indexes and Query Plans Production Cases]]
- [[50_LABS/Databases/DB-B01/README]]
- [[98_SOURCES/PostgreSQL Indexes and Query Plans Sources]]
