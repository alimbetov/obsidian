---
type: production-cases
domain: databases
subdomain: postgresql-indexes
status: published
case_count: 14
postgresql_versions:
  - 18
tags:
  - postgresql
  - indexes
  - explain
  - incidents
---

# Indexes and Query Plans Production Cases

# Case 1 — Index exists, but PostgreSQL chooses Seq Scan

## Symptom

```sql
SELECT * FROM events WHERE status = 'PROCESSED';
```

`events_status_idx` exists, but plan uses `Seq Scan`.

## Evidence

```text
Rows: 10,000,000
PROCESSED: 9,200,000
Seq Scan buffers: most heap pages once
Forced Index Scan: index pages + most heap pages
```

## Root cause

Predicate returns 92% of table. Index does not reduce required heap work and adds secondary-structure traversal.

## Correct repair

- Accept Seq Scan for full result.
- Avoid `SELECT *` if caller needs aggregate or small projection.
- Reconsider retention/partitioning for operational query.
- Add partial index only for rare operational statuses, not common processed rows.

## Proof

Compare execution time and buffers with representative data; do not judge by node name.

---

# Case 2 — Composite index has wrong column order

## Symptom

Query remains slow:

```sql
SELECT *
FROM operations
WHERE tenant_id = 77
  AND status = 'SUCCESS'
ORDER BY created_at DESC
LIMIT 50;
```

Existing index:

```sql
CREATE INDEX operations_created_status_tenant_idx
ON operations(created_at, status, tenant_id);
```

## Evidence

Plan reads a wide time range and filters many rows.

## Root cause

Leading `created_at` does not narrow by tenant/status equality first.

## Repair

Test index:

```sql
CREATE INDEX operations_tenant_status_created_idx
ON operations(tenant_id, status, created_at DESC);
```

## Proof

Expect narrow `Index Cond`, no Sort, early stop at 50, lower buffers.

---

# Case 3 — Three single-column indexes do not replace workload-aligned composite index

## Symptom

Indexes on `tenant_id`, `status`, `created_at` exist. Planner uses BitmapAnd plus Sort.

## Root cause

Bitmap combination can filter, but loses ordering and adds multiple index scans.

## Repair

For dominant query shape, test one composite index matching equality + order. Keep separate indexes only if independent query workload justifies them.

## Proof

Compare:

```text
Bitmap Index Scans count
Bitmap Heap buffers
Sort method/time
Composite Index Scan buffers/time
write overhead
```

---

# Case 4 — Index Only Scan still performs many heap fetches

## Symptom

Plan says `Index Only Scan`, but latency remains high.

## Evidence

```text
Heap Fetches: 450000
```

## Root cause

Heap pages are not all-visible due to frequent updates or insufficient vacuum progress.

## Repair

- Inspect autovacuum and update rate.
- Verify covering index columns.
- Avoid assuming read-only optimization on hot table.
- Consider read model/table design if workload is mixed and critical.

## Proof

Observe `Heap Fetches`, visibility map-related behavior, vacuum metrics and stable-table comparison.

---

# Case 5 — Wide INCLUDE index increases write latency

## Symptom

Read query improves 10%, but INSERT latency rises 40% and index size doubles.

## Root cause

Large payload columns duplicate data in every leaf tuple, increase page splits, WAL and cache footprint.

## Repair

- Keep only columns required by high-value query.
- Remove wide JSON/text payload.
- Measure index-only hit conditions.
- Compare narrower projection.

## Proof

`pg_relation_size`, WAL volume, insert benchmark, buffer residency and query improvement.

---

# Case 6 — Partial index is ignored

## Symptom

```sql
CREATE INDEX orders_pending_idx
ON orders(created_at)
WHERE status = 'PENDING';
```

Application query does not use it.

## Evidence

Prepared/generalized query has broad predicate or incompatible expression.

## Root cause

Planner cannot prove query predicate implies partial-index predicate for all parameter values.

## Repair

- Align exact query predicate.
- Separate endpoint/query for pending workload.
- Inspect prepared statement generic/custom plan behavior.
- Do not force index blindly.

## Proof

Compare literal/custom plan and actual application prepared execution.

---

# Case 7 — Function on indexed column moves condition into Filter

## Symptom

Index on `email` exists, query uses:

```sql
WHERE lower(email) = lower(:email)
```

Plan uses Seq Scan or reads many candidates.

## Root cause

Plain index on `email` does not index `lower(email)` expression.

## Repair

```sql
CREATE INDEX users_email_lower_idx ON users(lower(email));
```

Also enforce consistent normalization/uniqueness contract if required.

## Proof

Condition should appear as `Index Cond` on expression index.

---

# Case 8 — Stale statistics cause catastrophic Nested Loop

## Symptom

Plan estimated 20 outer rows, actual 300,000. Inner index scan loops 300,000 times.

## Root cause

Stale/skewed statistics or correlated predicates caused severe underestimation.

## Repair

- Run `ANALYZE`.
- Increase statistics target for skewed columns.
- Add extended statistics for correlated columns.
- Verify bind values and data drift.

## Proof

Estimated/actual rows converge; join choice and buffers improve.

---

# Case 9 — Correlated columns estimated independently

## Symptom

```sql
WHERE country = 'KZ' AND city = 'Almaty'
```

Planner assumes independence and badly misestimates rows.

## Repair

```sql
CREATE STATISTICS customer_geo_stats
    (dependencies, ndistinct, mcv)
ON country, city
FROM customer;
ANALYZE customer;
```

## Proof

Compare estimate ratio before/after. Extended statistics improve cardinality; they do not create an access path by themselves.

---

# Case 10 — Sort spills after data growth

## Symptom

Query was stable, then latency jumped. Plan shows external merge and temp writes.

## Root cause

Input cardinality or row width grew beyond effective memory for Sort.

## Repair

- Reduce rows before sort.
- Add workload-aligned ordered index for `ORDER BY LIMIT`.
- Reduce selected width.
- Tune memory carefully with concurrency model.

## Proof

Sort method, temp read/write, execution time and server memory pressure.

---

# Case 11 — Bitmap becomes lossy

## Symptom

Plan shows `lossy` heap blocks and high `Rows Removed by Index Recheck`.

## Root cause

Bitmap memory pressure forced page-level representation; executor rechecks many tuples.

## Repair

- Reduce result set earlier.
- Improve index/query selectivity.
- Review memory cautiously.
- Consider composite index for dominant conjunction.

## Proof

Exact/lossy block counts and recheck removals decline.

---

# Case 12 — Too many indexes break HOT and vacuum behavior

## Symptom

Update-heavy table has high WAL, bloat and latency. Many overlapping indexes exist.

## Root cause

Updates touch indexed columns and require new index entries; HOT opportunities shrink, vacuum work grows.

## Repair

- Inventory real index usage.
- Drop redundant indexes after safe observation.
- Avoid indexing volatile columns without critical workload.
- Consolidate overlapping indexes when justified.

## Proof

Update benchmark, WAL, index sizes, HOT statistics, vacuum duration and query regressions.

---

# Case 13 — Pagination becomes slower on high offsets

## Symptom

```sql
ORDER BY created_at DESC, id DESC
OFFSET 500000 LIMIT 50
```

Index exists, but query scans/skips hundreds of thousands of entries.

## Root cause

Offset pagination must consume preceding rows.

## Repair

Use keyset cursor:

```sql
WHERE (created_at, id) < (:lastCreatedAt, :lastId)
ORDER BY created_at DESC, id DESC
LIMIT 50;
```

Supporting index:

```sql
CREATE INDEX events_created_id_desc_idx
ON events(created_at DESC, id DESC);
```

## Proof

Buffers and execution time remain approximately stable across pages.

---

# Case 14 — “Indexes no longer help” because query must aggregate most data

## Symptom

Daily dashboard scans billions of historical rows despite indexes.

## Root cause

Query semantically requires large fraction aggregation. Index cannot erase required computation and I/O.

## Repair options

- Partition pruning by time/tenant.
- Incremental/materialized aggregates.
- Separate analytical store/read model.
- Archival/retention.
- BRIN for physically correlated ranges.
- Distributed processing only after workload model is clear.

## Proof

Measure rows/pages that must be processed after filtering. Demonstrate reduced required work, not merely different plan node.

# Production diagnostic checklist

```text
1 exact SQL and bind values
2 actual data distribution
3 EXPLAIN ANALYZE BUFFERS
4 dominant subtree
5 estimate/actual ratio
6 rows × loops
7 Index Cond versus Filter
8 heap fetches and removed rows
9 sort/hash spills
10 write/storage cost of proposed index
11 repeat on representative cache state
12 verify application-level latency after DB fix
```

## Related materials

- [[PostgreSQL Index Mechanics]]
- [[PostgreSQL EXPLAIN and Query Plan Analysis]]
- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Cards]]
- [[50_LABS/Databases/DB-B01/README]]
