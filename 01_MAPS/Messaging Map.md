---
type: moc
domain: messaging
status: planned
tags:
  - map
  - messaging
  - navigation
---

# Messaging Map

> [!summary]
> Messaging vertical slices ещё не опубликованы. Эта карта фиксирует planned scope и связывает будущий маршрут с уже готовыми материалами по Outbox, transaction boundaries, idempotency и backpressure.

## Navigation

- [[00_HOME/Knowledge Route Registry|Knowledge Route Registry]]
- [[00_HOME/Java Backend Knowledge System|Home]]
- [[01_MAPS/Distributed Systems Map|Distributed Systems Map]]
- [[10_CONCEPTS/Spring/Transactions/Transactional Outbox and Commit Boundaries|Transactional Outbox]]
- [[10_CONCEPTS/Java/Concurrency/Concurrent Collections and Backpressure|Backpressure foundation]]

Next planned route:

```text
MSG-B01 — Kafka Delivery, Ordering and Consumer Groups
```

## Основы

- Queue versus log;
- producer, broker and consumer;
- push versus pull;
- message ordering;
- backpressure.

## Delivery semantics

- at-most-once;
- at-least-once;
- exactly-once claims and boundaries;
- acknowledgements and offsets;
- duplicate delivery;
- idempotent consumer.

Relevant published bridge:

- [[10_CONCEPTS/Spring/Transactions/Transactional Outbox and Commit Boundaries]];
- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Transaction Management Roadmap]].

## Обработка сбоев

- retry and exponential backoff;
- dead letter queue;
- poison message;
- replay;
- consumer lag.

## Kafka

- topics and partitions;
- consumer groups;
- rebalancing;
- message keys;
- offset management;
- producer idempotence and transactions.

## RabbitMQ

- exchanges;
- queues and bindings;
- routing keys;
- acknowledgements;
- prefetch;
- dead-letter exchanges.

## Integration patterns

- Transactional Outbox;
- Change Data Capture;
- Saga;
- Event sourcing.

## Publication definition

Messaging route becomes published only when it contains:

```text
canonical concepts
visual deep dive
cards
production cases
Kafka/RabbitMQ lab
Canvas
primary sources
route manifest entry
```

Cross-link contract: [[90_TEMPLATES/Cross-Linking Standard]].
