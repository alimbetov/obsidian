\timing on
\pset pager off

\echo 'EXPERIMENT 1 — common status: index exists, Seq Scan may be rational'
EXPLAIN (ANALYZE, BUFFERS, SETTINGS)
SELECT count(*)
FROM operation_event
WHERE status = 'PROCESSED';

\echo 'EXPERIMENT 2 — rare status: same index, different selectivity'
EXPLAIN (ANALYZE, BUFFERS, SETTINGS)
SELECT count(*)
FROM operation_event
WHERE status = 'FAILED';

\echo 'EXPERIMENT 3 — three single-column concerns: filter + order + limit'
EXPLAIN (ANALYZE, BUFFERS)
SELECT id, amount, status, created_at
FROM operation_event
WHERE tenant_id = 42
  AND status = 'SUCCESS'
ORDER BY created_at DESC
LIMIT 50;

\echo 'Create workload-aligned composite covering index'
CREATE INDEX operation_event_tenant_status_created_idx
    ON operation_event(tenant_id, status, created_at DESC)
    INCLUDE (amount);
ANALYZE operation_event;

EXPLAIN (ANALYZE, BUFFERS)
SELECT id, amount, status, created_at
FROM operation_event
WHERE tenant_id = 42
  AND status = 'SUCCESS'
ORDER BY created_at DESC
LIMIT 50;

\echo 'EXPERIMENT 4 — index-only behavior and heap fetches'
VACUUM (ANALYZE) operation_event;
EXPLAIN (ANALYZE, BUFFERS)
SELECT tenant_id, status, created_at, amount
FROM operation_event
WHERE tenant_id = 42
  AND status = 'SUCCESS'
ORDER BY created_at DESC
LIMIT 200;

\echo 'Dirty visibility map on some pages'
UPDATE operation_event
SET amount = amount + 0.01
WHERE id % 1000 = 0;
ANALYZE operation_event;

EXPLAIN (ANALYZE, BUFFERS)
SELECT tenant_id, status, created_at, amount
FROM operation_event
WHERE tenant_id = 42
  AND status = 'SUCCESS'
ORDER BY created_at DESC
LIMIT 200;

\echo 'EXPERIMENT 5 — partial index for operational queue'
CREATE INDEX operation_event_pending_created_idx
    ON operation_event(created_at)
    WHERE status = 'PENDING';
ANALYZE operation_event;

EXPLAIN (ANALYZE, BUFFERS)
SELECT id, created_at
FROM operation_event
WHERE status = 'PENDING'
ORDER BY created_at
LIMIT 100;

\echo 'EXPERIMENT 6 — expression index'
EXPLAIN (ANALYZE, BUFFERS)
SELECT count(*)
FROM client
WHERE lower(city) = 'almaty';

CREATE INDEX client_city_lower_idx ON client(lower(city));
ANALYZE client;

EXPLAIN (ANALYZE, BUFFERS)
SELECT count(*)
FROM client
WHERE lower(city) = 'almaty';

\echo 'EXPERIMENT 7 — bitmap combination versus composite index'
CREATE INDEX operation_event_category_idx ON operation_event(category);
ANALYZE operation_event;

EXPLAIN (ANALYZE, BUFFERS)
SELECT id
FROM operation_event
WHERE status = 'FAILED'
  AND category = 'TRANSFER';

CREATE INDEX operation_event_status_category_idx
    ON operation_event(status, category);
ANALYZE operation_event;

EXPLAIN (ANALYZE, BUFFERS)
SELECT id
FROM operation_event
WHERE status = 'FAILED'
  AND category = 'TRANSFER';

\echo 'EXPERIMENT 8 — correlated columns before extended statistics'
EXPLAIN (ANALYZE, BUFFERS)
SELECT count(*)
FROM client
WHERE country = 'KZ'
  AND city = 'Almaty';

CREATE STATISTICS client_country_city_stats
    (dependencies, ndistinct, mcv)
ON country, city
FROM client;
ANALYZE client;

EXPLAIN (ANALYZE, BUFFERS)
SELECT count(*)
FROM client
WHERE country = 'KZ'
  AND city = 'Almaty';

\echo 'EXPERIMENT 9 — high OFFSET versus keyset pagination'
CREATE INDEX operation_event_created_id_desc_idx
    ON operation_event(created_at DESC, id DESC);
ANALYZE operation_event;

EXPLAIN (ANALYZE, BUFFERS)
SELECT id, created_at
FROM operation_event
ORDER BY created_at DESC, id DESC
OFFSET 500000 LIMIT 50;

SELECT
    created_at AS cursor_created_at,
    id AS cursor_id
FROM operation_event
ORDER BY created_at DESC, id DESC
OFFSET 500000 LIMIT 1
\gset

EXPLAIN (ANALYZE, BUFFERS)
SELECT id, created_at
FROM operation_event
WHERE (created_at, id) < (
    :'cursor_created_at'::timestamptz,
    :cursor_id::bigint
)
ORDER BY created_at DESC, id DESC
LIMIT 50;

\echo 'EXPERIMENT 10 — estimate and loops in a join'
EXPLAIN (ANALYZE, BUFFERS)
SELECT e.id, c.segment
FROM operation_event e
JOIN client c ON c.id = e.client_id
WHERE e.status = 'FAILED'
  AND c.country = 'KZ'
  AND c.city = 'Almaty';

\echo 'Index inventory'
SELECT
    schemaname,
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size,
    idx_scan,
    idx_tup_read,
    idx_tup_fetch
FROM pg_stat_user_indexes
ORDER BY pg_relation_size(indexrelid) DESC;
