---
type: moc
domain: distributed-systems
status: planned
tags:
  - map
  - distributed-systems
  - navigation
---

# Distributed Systems Map

> [!summary]
> Distributed Systems vertical slice ещё не опубликован. Эта MOC связывает planned scope с готовыми материалами по cache topology, transaction/outbox boundaries, backpressure и database scaling.

## Navigation

- [[00_HOME/Knowledge Route Registry|Knowledge Route Registry]]
- [[00_HOME/Java Backend Knowledge System|Home]]
- [[01_MAPS/Messaging Map|Messaging Map]]
- [[01_MAPS/Databases Map|Databases Map]]
- [[10_CONCEPTS/Spring/Cache/Spring Cache Visual Deep Dive|Cache topology and failure paths]]
- [[10_CONCEPTS/Spring/Transactions/Transactional Outbox and Commit Boundaries|Outbox consistency boundary]]
- [[10_CONCEPTS/Java/Concurrency/Concurrent Collections and Backpressure|Backpressure foundation]]

Next planned route:

```text
DS-B01 — Resilience, Consistency and Distributed Coordination
```

## Коммуникация

- synchronous versus asynchronous communication;
- REST, gRPC and messaging;
- API Gateway;
- service discovery;
- load balancing.

## Надёжность

- timeout;
- retry;
- circuit breaker;
- bulkhead;
- rate limiting;
- idempotency;
- graceful degradation.

Published bridges:

- [[10_CONCEPTS/Spring/Cache/Spring Cache Visual Deep Dive]];
- [[10_CONCEPTS/Java/Concurrency/Java Concurrency Visual Deep Dive]].

## Согласованность данных

- CAP theorem;
- strong and eventual consistency;
- distributed transactions;
- Saga;
- Transactional Outbox;
- consensus basics.

Published bridge:

- [[10_CONCEPTS/Spring/Transactions/Transactional Outbox and Commit Boundaries]].

## Observability

- logs;
- metrics;
- traces;
- correlation IDs;
- SLI, SLO and SLA.

## System design

- scaling;
- caching;
- partitioning;
- replication;
- failure domains;
- capacity estimation.

Database bridge:

- [[30_CERTIFICATIONS/Databases/DB-B01/DB-B01 Roadmap|DB-B01 — Indexes and Query Plans]].

## Publication definition

Distributed Systems route becomes published only when it contains:

```text
canonical concepts
visual deep dive
cards
production cases
executable resilience lab
Canvas
primary sources
route manifest entry
```

Cross-link contract: [[90_TEMPLATES/Cross-Linking Standard]].
