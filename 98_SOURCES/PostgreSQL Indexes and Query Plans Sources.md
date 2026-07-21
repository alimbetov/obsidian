---
type: source-index
domain: databases
subdomain: postgresql-indexes
status: active
verified_at: 2026-07-21
postgresql_versions:
  - 18
  - current
tags:
  - postgresql
  - sources
  - indexes
  - explain
---

# PostgreSQL Indexes and Query Plans Sources

> [!summary]
> Primary source index for `DB-B01`. Canonical explanation is based on PostgreSQL 18/current official documentation. Plan choices and exact output remain data-, statistics-, configuration- and hardware-dependent.

# Index architecture

## Chapter 11 — Indexes

- https://www.postgresql.org/docs/18/indexes.html
- https://www.postgresql.org/docs/current/indexes.html

Use for:

- index types;
- multicolumn indexes;
- combining indexes;
- expression and partial indexes;
- index-only scans;
- examining index usage.

## Multicolumn indexes

- https://www.postgresql.org/docs/current/indexes-multicolumn.html

Important PostgreSQL 18 detail:

- equality constraints on leading columns plus inequality on the first non-equality column directly limit scan range;
- B-tree skip scan is cost-based and useful only for suitable distributions.

## Combining multiple indexes and bitmap scans

- https://www.postgresql.org/docs/current/indexes-bitmap-scans.html

Use for:

- BitmapAnd and BitmapOr;
- physical-order heap visits;
- loss of index ordering;
- trade-off between multiple single-column and multicolumn indexes.

## Index-only scans and covering indexes

- https://www.postgresql.org/docs/current/indexes-index-only-scans.html
- https://www.postgresql.org/docs/current/sql-createindex.html

Use for:

- heap and secondary-index separation;
- visibility map;
- `Heap Fetches` interpretation;
- `INCLUDE` payload columns;
- index size/write trade-offs.

## Expression indexes

- https://www.postgresql.org/docs/current/indexes-expressional.html

## Partial indexes

- https://www.postgresql.org/docs/current/indexes-partial.html

## Indexes and ORDER BY

- https://www.postgresql.org/docs/current/indexes-ordering.html

## Examining index usage

- https://www.postgresql.org/docs/current/indexes-examine.html

Key official guidance:

- run `ANALYZE`;
- use `EXPLAIN`;
- compare alternative plans experimentally;
- planner toggles can help diagnosis but are not root-cause repairs.

# Query planner and EXPLAIN

## Using EXPLAIN

- https://www.postgresql.org/docs/current/using-explain.html

Use for:

- plan tree basics;
- startup and total cost;
- estimated rows and width;
- actual time, rows and loops;
- `BUFFERS`;
- sort/hash details;
- index-search count in PostgreSQL 18.

## EXPLAIN command reference

- https://www.postgresql.org/docs/current/sql-explain.html

Use for:

- `ANALYZE`;
- `BUFFERS`;
- `WAL`;
- `SETTINGS`;
- `SUMMARY`;
- output formats.

## Planner statistics

- https://www.postgresql.org/docs/current/planner-stats.html

Use for:

- `pg_class.reltuples/relpages`;
- most-common values;
- histogram bounds;
- distinct estimates;
- correlation;
- extended statistics.

## CREATE STATISTICS

- https://www.postgresql.org/docs/current/sql-createstatistics.html

Use for:

- dependencies;
- ndistinct;
- multivariate most-common values;
- correlated predicates.

## Planner method configuration

- https://www.postgresql.org/docs/current/runtime-config-query.html

Use cautiously for diagnostics:

- `enable_seqscan`;
- `enable_nestloop`;
- cost constants;
- statistics behavior.

# Runtime statistics and maintenance

## Cumulative statistics views

- https://www.postgresql.org/docs/current/monitoring-stats.html

Use for:

- `pg_stat_user_indexes`;
- table/index access counters;
- vacuum/analyze activity.

## VACUUM

- https://www.postgresql.org/docs/current/sql-vacuum.html

Use for:

- visibility map implications;
- `VACUUM (ANALYZE)`;
- index-only scan experiments.

# Version boundary

Current stable documentation baseline when verified: PostgreSQL 18.

Development/beta versions may expose additional planner behavior. The route avoids relying on PostgreSQL 19 beta behavior. Exact plans may change across minor/major releases even when the conceptual model remains valid.

# Related materials

- [[10_CONCEPTS/Databases/PostgreSQL Index Mechanics]]
- [[10_CONCEPTS/Databases/PostgreSQL EXPLAIN and Query Plan Analysis]]
- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Roadmap]]
- [[50_LABS/Databases/DB-B01/README]]
