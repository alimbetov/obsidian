---
type: certification-roadmap
certification: spring-2V0-72.22
domain: spring
subdomain:
  - aop
  - caching
status: active
published_cards: 44
normalized_cards: 44
normalized_at: 2026-07-21
visual_enrichment: complete
batches:
  - AOP-B01
  - CACHE-B01
tags:
  - spring
  - certification
  - aop
  - caching
  - visual-learning
---

# Spring AOP and Cache Roadmap

> [!summary]
> Маршрут продолжает Spring Core. `@Transactional`, `@Async`, method security и Spring Cache используют proxy/interceptor boundaries, поэтому их типовые failures имеют общий diagnostic model. Все 44 карточки приведены к единому pedagogical contract. Дополнительно создан визуальный слой с 37 Mermaid-моделями, production traces и отдельным Canvas-atlas.

## Progress

```text
AOP-B01    24 cards  PUBLISHED + NORMALIZED + VISUAL
CACHE-B01  20 cards  PUBLISHED + NORMALIZED + VISUAL
----------------------------------------------------
TOTAL      44 cards
```

## Learning sequence

```mermaid
flowchart LR
    C[Spring Core] --> A[AOP terminology]
    A --> P[Proxy mechanics]
    P --> AV[AOP Visual Deep Dive]
    AV --> S[Self-invocation and advisor chains]
    S --> K[Spring Cache]
    K --> KV[Cache Visual Deep Dive]
    KV --> CF[Caffeine topology]
    KV --> R[Redis topology]
    CF --> L2[L1 and L2 consistency]
    R --> L2
    L2 --> TX[Transaction Management]
    TX --> DATA[Spring Data and JPA]
    DATA --> TEST[Spring Testing]
    TEST --> DB[DB-B01 Indexes and Query Plans]
```

# Visual learning layer

## Visual artifacts

- [[10_CONCEPTS/Spring/AOP/Spring AOP Visual Deep Dive]];
- [[10_CONCEPTS/Spring/Cache/Spring Cache Visual Deep Dive]];
- [[01_MAPS/Spring AOP and Cache Visual Atlas.canvas]];
- [[90_TEMPLATES/Pedagogical Visual Standard]].

## Visual coverage

```text
AOP visual models      16 Mermaid diagrams
Cache visual models    21 Mermaid diagrams
Canvas atlas            1 connected learning map
------------------------------------------------
New visual elements    38
```

Diagrams include:

- bean auto-proxy creation;
- caller → proxy → interceptor → target sequences;
- JDK and CGLIB class models;
- external invocation versus self-invocation;
- advisor ordering and nested exits;
- around advice with missing or duplicate `proceed()`;
- exception and rollback paths;
- async executor/thread boundary;
- method-security bypass;
- AOP diagnostic decision tree;
- cache hit/miss sequence;
- `condition`/`unless` decision path;
- tenant-key collision;
- transaction/cache timing;
- Caffeine multi-node topology;
- Redis shared topology;
- cache stampede;
- local `sync=true` boundary;
- L1/L2 promotion and invalidation;
- Redis outage cascade;
- cache diagnostic decision tree.

# AOP-B01 — published, normalized and visually enriched

Materials:

- [[10_CONCEPTS/Spring/AOP/Spring AOP Proxy Mechanics]];
- [[10_CONCEPTS/Spring/AOP/Spring AOP Visual Deep Dive]];
- [[10_CONCEPTS/Spring/AOP/Spring AOP Proxies and Cache Interception]];
- [[30_CERTIFICATIONS/Spring/2V0-72.22/AOP-B01/AOP-B01 Cards]];
- [[50_LABS/Spring/AOP-B01/README]];
- [[40_PRODUCTION_CASES/Spring/AOP and Cache Production Cases]];
- [[98_SOURCES/Spring AOP and Cache Sources]].

Coverage:

- aspect, join point, pointcut, advice and advisor;
- around advice and `ProceedingJoinPoint.proceed()`;
- JDK dynamic proxy and CGLIB execution paths;
- final/private method limitations;
- self-invocation and collaborator refactoring;
- `AopContext` requirements and trade-offs;
- advisor ordering and nested execution;
- exception propagation and false-success risk;
- runtime proxy and advisor-chain diagnostics;
- `@Transactional(REQUIRES_NEW)` self-invocation;
- `@Async` thread/transaction boundary;
- method-security internal-call bypass.

Normalization result:

```text
24 / 24 cards contain:
Question
Russian Translation
Answer
Explanation
Exam Trap
```

Visual walkthroughs include:

- annotation metadata → infrastructure → advisor → proxy;
- bean creation and auto-proxy sequence;
- proxy/target object graph;
- JDK versus CGLIB class diagrams;
- external versus internal call traces;
- nested advisor-chain sequence;
- missing/double `proceed()` paths;
- swallowed-exception path;
- async executor and transaction boundary;
- security bypass path;
- end-to-end payment execution with security, tracing, retry and transaction.

Real examples include:

- security → transaction advisor order;
- final and private method interception failures;
- same-class `REQUIRES_NEW` failure;
- collaborator refactoring;
- swallowed exception changing rollback semantics;
- `AopUtils` and `Advised#getAdvisors()` diagnostics;
- synchronous execution of self-invoked `@Async`;
- method-security bypass through `this`.

# CACHE-B01 — published, normalized and visually enriched

Materials:

- [[10_CONCEPTS/Spring/Cache/Spring Cache with Caffeine and Redis]];
- [[10_CONCEPTS/Spring/Cache/Spring Cache Visual Deep Dive]];
- [[10_CONCEPTS/Spring/AOP/Spring AOP Proxies and Cache Interception]];
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CACHE-B01/CACHE-B01 Cards]];
- [[50_LABS/Spring/CACHE-B01/README]];
- [[50_LABS/Spring/CACHE-B01/compose.yaml]];
- [[40_PRODUCTION_CASES/Spring/AOP and Cache Production Cases]];
- [[98_SOURCES/Spring AOP and Cache Sources]].

Coverage:

- Spring Cache abstraction and provider boundary;
- `CacheManager` selection;
- `@Cacheable`, `@CachePut`, `@CacheEvict`;
- keys, tenant isolation, `condition` and `unless`;
- self-invocation;
- `sync=true` and provider-specific single-flight limits;
- cache stampede mitigation;
- Caffeine locality, size, weight and expiration;
- Redis shared state, TTL, prefixes and serialization;
- transaction-aware timing versus distributed atomicity;
- Redis outage and database overload;
- L1 Caffeine + L2 Redis invalidation;
- runtime metrics and diagnostic sequence.

Normalization result:

```text
20 / 20 cards contain:
Question
Russian Translation
Answer
Explanation
Exam Trap
```

Visual walkthroughs include:

- full cache interceptor sequence;
- hit and miss as different execution paths;
- `condition` before and `unless` after target invocation;
- tenant-key collision sequence;
- `@CachePut` and `@CacheEvict` timing;
- database transaction versus cache update timeline;
- Caffeine per-node topology;
- Redis shared-store topology;
- serialization compatibility across deployments;
- TTL versus business invalidation;
- hot-key stampede;
- local single-flight versus multi-node loads;
- L1/L2 promotion and invalidation protocol;
- Redis outage cascade;
- end-to-end product-catalogue topology.

Real examples include:

- multi-tenant cache-key collision;
- `condition` versus `unless`;
- after-invocation and before-invocation eviction;
- same-key concurrent miss and `sync=true` boundary;
- hot-key stampede;
- Caffeine per-JVM stale copies;
- `maximumSize` versus `maximumWeight`;
- Redis DTO/schema-version contract;
- DB commit followed by failed Redis eviction;
- two-level cache cross-node staleness;
- Redis outage causing database overload.

# Vertical-slice quality gate

- [x] 24 AOP cards.
- [x] 20 caching cards.
- [x] English questions and Russian translations.
- [x] 44/44 direct answers.
- [x] 44/44 mechanism explanations.
- [x] 44/44 concrete exam traps.
- [x] AOP visual deep dive with multiple diagram types.
- [x] Cache visual deep dive with topology and failure diagrams.
- [x] Connected visual Canvas atlas.
- [x] Pedagogical visual standard documented.
- [x] Real transaction, async and security proxy examples.
- [x] Real Caffeine, Redis and L1/L2 failure examples.
- [x] Caffeine local cache lab.
- [x] Redis Docker Compose lab.
- [x] 12 production cases.
- [x] Primary source index.
- [x] Repository structural and Mermaid quality gate.
- [ ] Full Maven runtime executed in connected environment.
- [ ] Redis lab executed against Docker Redis.
- [ ] Real review outcomes collected.

# Review questions

1. Через какой object reference входит caller?
2. JDK или CGLIB proxy?
3. Какие advisors применяются и в каком порядке?
4. Есть ли self-invocation?
5. Может ли method быть переопределён proxy?
6. Кто вычисляет cache key и включает ли он tenant/locale/version?
7. Какой `CacheManager` выбран?
8. Caffeine entry локален какому node?
9. Какой TTL и serialization contract у Redis?
10. Что произойдёт при Redis outage?
11. Как инвалидируется L1 на других nodes?
12. Как доказать behavior через metrics и advisor inspection?
13. Какая diagram объясняет runtime path, а какая — topology?
14. Показан ли failure path, а не только happy path?

# Published continuations

## Transaction Management

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Transaction Management Roadmap]];
- [[30_CERTIFICATIONS/Spring/2V0-72.22/TX-B01/TX-B01 Cards]];
- [[50_LABS/Spring/TX-B01/README]].

## Spring Data and JPA

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Data JPA Roadmap]];
- [[30_CERTIFICATIONS/Spring/2V0-72.22/DATA-B01/DATA-B01 Cards]];
- [[50_LABS/Spring/DATA-B01/README]].

## Spring Testing

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Testing Roadmap]];
- [[30_CERTIFICATIONS/Spring/2V0-72.22/TEST-B01/TEST-B01 Cards]];
- [[50_LABS/Spring/TEST-B01/README]].

# Next enrichment targets

Before expanding breadth, the same visual standard should be applied to:

```text
1. Spring Transaction Management
2. Spring Data JPA
3. Spring Testing
4. Java Concurrency
```

# Next implementation route

```text
DB-B01 — Indexes and Query Plans
```

Planned vertical slice:

1. B-tree mechanics and selectivity.
2. Composite-index ordering and leftmost-prefix reasoning.
3. Covering/index-only scans.
4. Partial and expression indexes.
5. PostgreSQL `EXPLAIN (ANALYZE, BUFFERS)`.
6. Sequential scan versus index scan.
7. Statistics, cardinality estimation and stale statistics.
8. Cases where indexes no longer solve large-data workloads.
9. Production cases and reproducible PostgreSQL lab.
10. Visual standard: tree structure, page access, plan tree, selectivity curves and diagnostic decision paths.
