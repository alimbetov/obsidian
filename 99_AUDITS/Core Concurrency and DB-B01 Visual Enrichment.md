---
type: quality-audit
domain: knowledge-system
status: active
audited_at: 2026-07-21
audited_branch: agent/knowledge-map-foundation
tags:
  - audit
  - visual-learning
  - spring-core
  - concurrency
  - databases
---

# Core, Concurrency and DB-B01 Visual Enrichment

> [!summary]
> Этот pass расширяет visual standard за пределы AOP/Cache/Transactions/Data/Testing. Spring Core и Java Concurrency получили отдельные mechanism-oriented deep dives и Canvas. DB-B01 построен сразу как полный visual vertical slice, а не как длинная note с одной общей схемой.

# Added visual routes

## Spring Core

- [[10_CONCEPTS/Spring/Core/Spring Core Visual Deep Dive]];
- [[01_MAPS/Spring Core Visual Atlas.canvas]].

```text
26 Mermaid models
1 Canvas atlas
```

Coverage:

- BeanDefinition sources;
- IoC pipeline;
- candidate resolution;
- lifecycle callbacks;
- BFPP/BPP boundary;
- proxy creation;
- scopes;
- FactoryBean;
- profile/condition/configuration;
- cycles and early references;
- parent/child contexts;
- startup diagnostics.

## Java Concurrency

- [[10_CONCEPTS/Java/Concurrency/Java Concurrency Visual Deep Dive]];
- [[01_MAPS/Java Concurrency Visual Atlas.canvas]].

```text
35 Mermaid models
1 Canvas atlas
```

Coverage:

- thread states;
- Java Memory Model and happens-before;
- volatile, monitor and lock paths;
- CAS and atomic state;
- executor lifecycle and saturation;
- backpressure and rejection;
- Future/CompletableFuture;
- ForkJoinPool;
- ThreadLocal leaks;
- virtual threads and downstream limits;
- concurrent collections;
- deadlock/livelock/starvation;
- diagnostic trees.

# DB-B01 — complete route

```text
Canonical notes          2
Mermaid models          62
Cards                   30
Production incidents    14
PostgreSQL experiments  10
Canvas atlas             1
Official source index    1
```

Materials:

- [[10_CONCEPTS/Databases/PostgreSQL Index Mechanics]];
- [[10_CONCEPTS/Databases/PostgreSQL EXPLAIN and Query Plan Analysis]];
- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Roadmap]];
- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Cards]];
- [[40_PRODUCTION_CASES/Databases/Indexes and Query Plans Production Cases]];
- [[50_LABS/Databases/DB-B01/README]];
- [[01_MAPS/Database Indexes and Query Plans Map.canvas]];
- [[98_SOURCES/PostgreSQL Indexes and Query Plans Sources]].

# Net visual growth

```text
Previous visual elements       125
Spring Core additions           27
Java Concurrency additions      36
DB-B01 additions                63
-----------------------------------
Current visual elements        251
```

`DB-B01 additions` counts 62 Mermaid diagrams + 1 Canvas. Lab SQL is executable evidence and is not counted as a diagram.

# Accuracy controls

- PostgreSQL baseline: version 18/current official docs.
- B-tree skip scan explicitly marked as PostgreSQL 18 cost-based behavior.
- Index-only scan explanation includes visibility map and possible heap fetches.
- `INCLUDE` is described as payload, not search key.
- Seq Scan is treated as potentially correct, not automatically defective.
- Extended statistics are separated from index access paths.
- `EXPLAIN ANALYZE` DML risk is documented.
- Rows and times are interpreted with loops.
- Lab plans are not claimed deterministic across hardware/cache states.

# Runtime status

```text
Markdown/Canvas structure     pending current CI run
Mermaid renderer              pending current CI run
PostgreSQL Docker execution   not executed in assistant environment
SQL source review             completed
Keyset EXPLAIN syntax         corrected before final gate
```

# Remaining debt

- Normalize TX-B01, DATA-B01 and TEST-B01 cards.
- Normalize four remaining Core cards.
- Execute DB-B01 lab and retain representative plans.
- Build DB-B02 — Transactions, MVCC and Locks.
- Build complete Java language, collections and JVM routes.
- Build Messaging and Distributed Systems visual routes.
