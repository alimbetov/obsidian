# DB-B01 — PostgreSQL Indexes and Query Plans Lab

## Purpose

Лаборатория показывает не только наличие index node, а фактическую работу planner/executor:

```text
B-tree traversal
index → heap path
Seq Scan economics
composite index ordering
ORDER BY + LIMIT early stop
index-only scan and Heap Fetches
partial and expression indexes
bitmap combination
extended statistics
OFFSET versus keyset pagination
estimated versus actual rows
buffers, loops and filter removals
```

## Baseline

```text
PostgreSQL 18
Docker Compose
100,000 clients
1,000,000 operation events
skewed status distribution
correlated country/city data
```

PostgreSQL 18 chosen because it is the current stable documentation baseline used by the route. Plans and costs remain environment-dependent.

## Start

```bash
cd 50_LABS/Databases/DB-B01
docker compose up -d
```

Check readiness:

```bash
docker compose exec postgres pg_isready -U lab -d index_lab
```

## Create schema

```bash
docker compose exec -T postgres \
  psql -U lab -d index_lab \
  < sql/01_schema.sql
```

## Seed representative data

```bash
docker compose exec -T postgres \
  psql -U lab -d index_lab \
  < sql/02_seed.sql
```

The seed deliberately creates:

```text
PROCESSED ≈ 80%
SUCCESS   ≈ 10%
PENDING   ≈ 5%
FAILED    ≈ 5%
```

It also correlates `country` and `city`, so single-column selectivity independence can be observed.

## Run all experiments

```bash
docker compose exec -T postgres \
  psql -U lab -d index_lab \
  < sql/03_experiments.sql \
  | tee db-b01-output.txt
```

PowerShell:

```powershell
Get-Content sql/01_schema.sql | docker compose exec -T postgres psql -U lab -d index_lab
Get-Content sql/02_seed.sql | docker compose exec -T postgres psql -U lab -d index_lab
Get-Content sql/03_experiments.sql | docker compose exec -T postgres psql -U lab -d index_lab | Tee-Object db-b01-output.txt
```

# Experiments

## 1. Common versus rare status

Compare the same `status` index for:

```sql
status = 'PROCESSED' -- common
status = 'FAILED'    -- rare
```

Record:

```text
plan node
actual rows
shared hit/read buffers
execution time
```

Expected lesson: same index can be rational for one value and rejected for another due to distribution.

## 2. Composite equality + order + limit

Before index:

```sql
WHERE tenant_id = 42
  AND status = 'SUCCESS'
ORDER BY created_at DESC
LIMIT 50
```

After:

```sql
CREATE INDEX operation_event_tenant_status_created_idx
ON operation_event(tenant_id, status, created_at DESC)
INCLUDE (amount);
```

Check:

- `Index Cond` includes equality prefix;
- explicit `Sort` disappears or becomes unnecessary;
- executor stops after 50 rows;
- buffers decline;
- `Heap Fetches` depends on visibility.

## 3. Index-only scan

Run after `VACUUM (ANALYZE)`, then update a subset of rows and repeat.

Observe:

```text
Index Only Scan
Heap Fetches before update
Heap Fetches after update
```

Expected lesson: covering is necessary but visibility map determines heap avoidance.

## 4. Partial index

```sql
CREATE INDEX operation_event_pending_created_idx
ON operation_event(created_at)
WHERE status = 'PENDING';
```

Explain why this index is smaller and targeted to operational queue queries.

## 5. Expression index

Compare:

```sql
WHERE lower(city) = 'almaty'
```

before and after:

```sql
CREATE INDEX client_city_lower_idx ON client(lower(city));
```

Check whether expression appears in `Index Cond`.

## 6. Bitmap versus composite

First allow separate indexes on `status` and `category`, then add:

```sql
CREATE INDEX operation_event_status_category_idx
ON operation_event(status, category);
```

Compare:

```text
number of index scans
BitmapAnd presence
heap buffers
sort/order behavior
write/storage cost
```

## 7. Extended statistics

Before and after:

```sql
CREATE STATISTICS client_country_city_stats
    (dependencies, ndistinct, mcv)
ON country, city
FROM client;
ANALYZE client;
```

Compare estimated rows with actual rows for correlated `country/city` predicate.

## 8. OFFSET versus keyset

High offset:

```sql
OFFSET 500000 LIMIT 50
```

Keyset:

```sql
WHERE (created_at, id) < (:cursorCreatedAt, :cursorId)
ORDER BY created_at DESC, id DESC
LIMIT 50
```

Expected lesson: index cannot avoid consuming skipped offset entries; keyset seeks from cursor.

## 9. Join estimates and loops

Read join plan bottom-up. Capture:

```text
outer actual rows
inner loops
estimated rows
actual rows
buffer count per subtree
join algorithm
```

# Plan-reading worksheet

For every experiment fill:

| Field | Observation |
|---|---|
| Exact SQL and values | |
| Plan root | |
| Dominant subtree | |
| Estimated rows | |
| Actual rows | |
| Error ratio | |
| Loops | |
| Index Cond | |
| Filter | |
| Rows Removed | |
| Heap Fetches | |
| Shared hit/read | |
| Sort/hash spill | |
| Proposed change | |
| Measured result | |
| Write/storage trade-off | |

# Controlled failure exercises

## A. Make statistics stale

```sql
INSERT INTO operation_event
SELECT ...;
-- intentionally do not ANALYZE yet
```

Compare estimates before and after `ANALYZE`.

## B. Force a sort spill

In isolated session:

```sql
SET work_mem = '1MB';
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM operation_event ORDER BY payload;
```

Do not treat session-level experiment as production tuning recommendation.

## C. Diagnose alternative plans

Temporarily:

```sql
SET enable_seqscan = off;
```

Use only to inspect alternative economics. Reset afterwards:

```sql
RESET enable_seqscan;
```

## D. Measure write amplification

1. Time bulk insert with base indexes.
2. Add several wide/overlapping indexes.
3. Repeat with fresh table/data.
4. Compare WAL, time and index sizes.

# Cleanup

```bash
docker compose down -v
```

# Limitations

- Plan times depend on CPU, storage, Docker and cache state.
- Dataset is synthetic and deterministic enough for learning, not a production benchmark.
- No replication, partitioning or concurrent write workload is included.
- The route does not claim runtime PASS in the assistant environment; execute the commands locally/CI with Docker.
- PostgreSQL minor releases may change cost choices while preserving the underlying reasoning.

# Evidence expected before marking lab complete

```text
[ ] schema and seed completed
[ ] all 10 experiments executed
[ ] output saved
[ ] at least three before/after plans explained
[ ] Heap Fetches interpreted correctly
[ ] one estimate error corrected
[ ] OFFSET/keyset compared
[ ] write cost discussed
```

## Related materials

- [[PostgreSQL Index Mechanics]]
- [[PostgreSQL EXPLAIN and Query Plan Analysis]]
- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Cards]]
- [[40_PRODUCTION_CASES/Databases/Indexes and Query Plans Production Cases]]
