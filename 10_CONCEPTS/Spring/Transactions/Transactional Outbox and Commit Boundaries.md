---
type: concept
domain: distributed-systems
subdomain: transactional-outbox
difficulty: advanced
status: learning
confidence: 0
interview: true
production_relevance: critical
prerequisites:
  - "[[Spring Transaction Management Deep Dive]]"
related:
  - "[[Spring Cache with Caffeine and Redis]]"
tags:
  - outbox
  - transaction
  - kafka
  - rabbitmq
  - idempotency
---

# Transactional Outbox and Commit Boundaries

> [!summary] За 30 секунд
> Transactional Outbox решает dual-write problem: business change и запись события сохраняются в одной local database transaction. Отдельный relay читает outbox rows, публикует их в broker и помечает доставленными. Паттерн гарантирует, что committed business change имеет durable publication intent, но обычно обеспечивает **at-least-once delivery**, поэтому consumer и relay должны быть idempotent. Outbox не делает database и broker одной physical transaction и не устраняет ordering, retry, cleanup и observability design.

---

# 1. Dual-write problem

```java
@Transactional
public Order createOrder(CreateOrderCommand command) {
    Order order = orderRepository.save(command.toOrder());
    kafkaTemplate.send("order-created", order.id());
    return order;
}
```

Две разные systems:

```text
Database transaction
Kafka/RabbitMQ publish
```

Нет общей atomic commit boundary.

## Failure A — database commit, publish lost

```text
insert order
    ↓
DB commit succeeds
    ↓
process crashes
    ↓
message never published
```

## Failure B — publish succeeds, DB rolls back

```text
message published
    ↓
DB commit fails
    ↓
consumer sees event for non-existent order
```

## Failure C — uncertain broker acknowledgment

```text
publish sent
    ↓
network timeout
    ↓
producer does not know whether broker accepted
    ↓
retry can create duplicate
```

> [!danger]
> `@Transactional` around a method containing DB and broker calls does not automatically create distributed atomicity.

---

# 2. Outbox transaction

```text
one local DB transaction
    ├── business table update
    └── outbox row insert
```

```java
@Transactional
public Order createOrder(CreateOrderCommand command) {
    Order order = orderRepository.save(command.toOrder());

    outboxRepository.insert(
            OutboxMessage.orderCreated(order)
    );

    return order;
}
```

If transaction commits:

```text
business row exists
outbox intent exists
```

If transaction rolls back:

```text
neither exists
```

This is the atomic guarantee.

---

# 3. Minimal schema

```sql
create table outbox_message (
    id              uuid primary key,
    aggregate_type  varchar(100) not null,
    aggregate_id    varchar(100) not null,
    event_type      varchar(150) not null,
    event_version   integer not null,
    payload         text not null,
    headers_json    text,
    status          varchar(30) not null,
    attempts        integer not null default 0,
    next_attempt_at timestamp,
    created_at      timestamp not null,
    published_at    timestamp,
    locked_by       varchar(100),
    locked_at       timestamp
);
```

Recommended indexes:

```sql
create index ix_outbox_pending
    on outbox_message(status, next_attempt_at, created_at);

create index ix_outbox_aggregate
    on outbox_message(aggregate_type, aggregate_id, created_at);
```

## Why fields exist

| Field | Purpose |
|---|---|
| `id` | event identity/idempotency key |
| `aggregate_id` | ordering/routing key |
| `event_type` | consumer contract |
| `event_version` | schema evolution |
| `payload` | immutable event data |
| `status` | relay state |
| `attempts` | retry policy |
| `next_attempt_at` | backoff scheduling |
| `locked_by/locked_at` | competing relay workers |
| `published_at` | audit and cleanup |

---

# 4. Event payload design

Bad:

```json
{
  "entity": "serialized JPA proxy with all fields"
}
```

Better:

```json
{
  "eventId": "d6b0...",
  "eventType": "OrderCreated",
  "eventVersion": 2,
  "occurredAt": "2026-07-21T10:15:00Z",
  "aggregateId": "ORD-9812",
  "tenantId": "tenant-a",
  "data": {
    "customerId": "CUS-42",
    "total": 15000.00,
    "currency": "KZT"
  }
}
```

Properties:

- immutable fact, not command;
- stable event ID;
- explicit schema version;
- tenant/security boundary;
- enough data for intended consumers;
- no lazy persistence proxy;
- no secrets unless required and protected.

---

# 5. Relay algorithm

## Polling relay

```text
poll pending rows
    ↓
claim batch
    ↓
publish each message
    ↓
mark published or schedule retry
```

Pseudo-code:

```java
public void relayBatch() {
    List<OutboxMessage> batch = claimBatch(100);

    for (OutboxMessage message : batch) {
        try {
            broker.publish(message);
            markPublished(message.id());
        } catch (Exception error) {
            scheduleRetry(message.id(), error);
        }
    }
}
```

## Claim transaction should be short

```java
@Transactional
public List<OutboxMessage> claimBatch(int size) {
    List<OutboxMessage> rows = repository.lockPending(size);
    rows.forEach(row -> row.markProcessing(workerId));
    return rows;
}
```

Do not hold database locks during network publish if avoidable.

Safer sequence:

```text
short TX: claim rows
    ↓
commit claim
    ↓
publish outside DB transaction
    ↓
short TX: mark result
```

This creates duplicate possibility after publish-before-mark crash, which is why idempotency is mandatory.

---

# 6. Why delivery is usually at least once

Failure window:

```text
broker accepts message
    ↓
process crashes before markPublished
    ↓
row remains retryable
    ↓
message published again
```

Therefore:

```text
at-least-once publication
    +
idempotent consumer
```

Not:

```text
exactly once end-to-end by assumption
```

---

# 7. Consumer idempotency

## Inbox table

```sql
create table consumed_message (
    consumer_name varchar(100) not null,
    message_id    uuid not null,
    consumed_at   timestamp not null,
    primary key (consumer_name, message_id)
);
```

Consumer:

```java
@Transactional
public void handle(OrderCreated event) {
    boolean firstTime = inboxRepository.tryInsert(
            "billing-service",
            event.eventId()
    );

    if (!firstTime) {
        return;
    }

    invoiceRepository.createFrom(event);
}
```

Unique constraint protects against concurrent duplicate delivery.

## Natural idempotency

```sql
insert into invoice(order_id, ...)
values (:orderId, ...)
on conflict (order_id) do nothing;
```

Use only when domain identity truly defines idempotency.

---

# 8. Ordering

Global order across all events is expensive and often unnecessary.

Usually required:

```text
order events for same aggregate
```

Example:

```text
OrderCreated v1
OrderConfirmed v2
OrderCancelled v3
```

Techniques:

- broker partition key = `aggregateId`;
- sequence number per aggregate;
- monotonic aggregate version;
- relay query ordered by aggregate/version;
- consumer rejects or buffers gaps.

## Ordering trap with parallel relay workers

```text
worker A claims version 2
worker B claims version 3
worker B publishes first
```

Possible solutions:

- partition claims by aggregate hash;
- serialize per aggregate;
- rely on broker key only if send order from producer is preserved;
- include version and make consumer resilient.

---

# 9. Claiming rows safely

PostgreSQL-style example:

```sql
select *
from outbox_message
where status = 'PENDING'
  and (next_attempt_at is null or next_attempt_at <= now())
order by created_at
for update skip locked
limit 100;
```

Benefits:

- multiple workers;
- no duplicate claim of currently locked rows;
- bounded batch.

But verify database support and execution plan.

## Lease recovery

Worker may die after marking `PROCESSING`.

Recovery rule:

```text
status = PROCESSING
and locked_at < now - leaseTimeout
    ↓
return to retryable state
```

Use fencing/version checks to avoid two workers finalizing the same row inconsistently.

---

# 10. Retry and dead-letter policy

```text
attempt 1 → 1 second
attempt 2 → 5 seconds
attempt 3 → 30 seconds
attempt 4 → 5 minutes
```

Add jitter to avoid synchronized retries.

Classify errors:

| Error | Action |
|---|---|
| network timeout | retry |
| broker unavailable | retry with backoff |
| invalid payload | dead-letter/manual repair |
| authorization failure | alert, limited retry |
| message too large | dead-letter and fix producer |

Do not retry forever without visibility.

Useful fields:

```text
last_error_code
last_error_message
attempts
next_attempt_at
dead_lettered_at
```

---

# 11. Cleanup and retention

Outbox grows continuously.

Policies:

- delete published rows after retention period;
- archive for audit;
- partition by month/day;
- batch deletes;
- avoid massive single delete transaction;
- monitor table/index bloat;
- retain failed/dead-letter rows longer.

Example:

```sql
delete from outbox_message
where status = 'PUBLISHED'
  and published_at < now() - interval '7 days'
limit 10000;
```

Actual syntax and batching strategy depend on database.

---

# 12. `@TransactionalEventListener` vs Outbox

## Transactional listener

```java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void publish(OrderCreatedEvent event) {
    kafkaTemplate.send("orders", event);
}
```

Advantages:

- simple;
- executes after successful commit;
- good for non-critical local side effects.

Failure:

```text
commit succeeds
process dies before send
message lost
```

## Outbox

```text
commit business row + durable outbox intent
relay can retry after process restart
```

Use outbox when publication is business-critical.

---

# 13. Direct broker transaction is not automatically outbox replacement

Kafka transaction can make multiple Kafka operations atomic within Kafka, and some frameworks can coordinate producer and database work in specific ways.

But evaluate:

- crash windows;
- commit ordering;
- broker/database coordination semantics;
- operational coupling;
- recovery behavior;
- whether strict outbox guarantees can be relaxed.

Do not claim:

```text
@Transactional + KafkaTemplate = universal atomic DB/Kafka transaction
```

---

# 14. Outbox and cache invalidation

Business update:

```text
DB row updated
outbox CacheInvalidation event inserted
```

Relay publishes:

```json
{
  "eventId": "...",
  "cacheName": "productById",
  "key": "tenant-a:42",
  "version": 18
}
```

Every node evicts local Caffeine L1.

Redis L2 may be evicted by one designated consumer or by application update flow.

Need define:

- duplicate invalidation behavior;
- ordering by version;
- stale event protection;
- node restart behavior;
- missed event recovery.

---

# 15. Outbox state machine

```text
PENDING
   ↓ claim
PROCESSING
   ├── publish success → PUBLISHED
   ├── retryable error → PENDING with next_attempt_at
   └── permanent error → DEAD
```

Avoid ambiguous states such as `SENT` without defining whether it means attempted, broker-acknowledged or consumer-processed.

---

# 16. Production example — payment completed

## Transactional writer

```java
@Service
public class PaymentCompletionService {

    private final PaymentRepository payments;
    private final OutboxRepository outbox;

    @Transactional
    public void complete(long paymentId) {
        Payment payment = payments.getForUpdate(paymentId);
        payment.complete();
        payments.save(payment);

        outbox.insert(
                OutboxMessage.of(
                        UUID.randomUUID(),
                        "Payment",
                        String.valueOf(paymentId),
                        "PaymentCompleted",
                        1,
                        PaymentCompletedPayload.from(payment)
                )
        );
    }
}
```

## Relay

```java
@Component
public class PaymentOutboxRelay {

    public void runOnce() {
        List<OutboxMessage> messages = claimer.claim(100);

        for (OutboxMessage message : messages) {
            publishOne(message);
        }
    }

    private void publishOne(OutboxMessage message) {
        try {
            broker.send(
                    "payment-events",
                    message.aggregateId(),
                    message.payload()
            );
            marker.markPublished(message.id());
        } catch (Exception error) {
            marker.markRetry(message.id(), error);
        }
    }
}
```

`claimer`, `marker.markPublished` and `marker.markRetry` should normally use short explicit transactions.

---

# 17. Production diagnostics

Metrics:

```text
outbox.pending.count
outbox.oldest.pending.age
outbox.publish.success
outbox.publish.failure
outbox.retry.count
outbox.dead.count
outbox.batch.duration
outbox.claim.conflicts
broker.ack.latency
```

Critical alerts:

- oldest pending age above SLA;
- backlog growth rate;
- no successful publish for N minutes;
- high dead-letter count;
- processing leases expired;
- cleanup lag;
- database query slowdown on outbox indexes.

Tracing fields:

```text
eventId
aggregateId
correlationId
causationId
traceId
tenantId
```

---

# 18. Common mistakes

1. Publish to broker inside DB transaction and call it atomic.
2. Delete outbox row immediately after send without handling uncertain ack.
3. Assume exactly-once delivery.
4. No consumer idempotency.
5. No event version.
6. Serialize persistence entity.
7. Hold DB locks during network publish.
8. No retry backoff.
9. No dead-letter state.
10. No cleanup strategy.
11. No ordering policy.
12. Multiple relay workers without safe claiming.
13. Outbox table without indexes.
14. Monitoring only broker, not backlog age.
15. `AFTER_COMMIT` listener used where durable delivery is required.

---

# 19. Decision guide

Use simple after-commit listener when:

- side effect is non-critical;
- loss is acceptable;
- operation is local and recoverable;
- eventual retry is not required.

Use outbox when:

- committed state change must eventually produce an event;
- process restart must not lose publication intent;
- broker can be temporarily unavailable;
- duplicate delivery can be handled;
- operational backlog monitoring is available.

Consider CDC-based outbox when:

- database log capture infrastructure exists;
- polling overhead should be avoided;
- schema and connector operations are mature.

---

# 20. Senior interview answer

> Transactional Outbox solves the dual-write problem without XA. The service writes the business state and an immutable outbox record in the same local database transaction. A separate relay claims committed records, publishes them to the broker, and records success or retry state. The durable guarantee is that committed business state has a committed publication intent. Since a crash can happen after broker acceptance but before marking the row published, delivery is normally at least once, so event IDs, consumer inbox/idempotency, retry backoff and schema versioning are required. I would also define aggregate ordering, safe row claiming, lease recovery, dead-letter handling, cleanup and backlog-age monitoring. An `AFTER_COMMIT` listener is simpler but not durable across process failure, so it is not equivalent to outbox for business-critical delivery.

---

# Related materials

- [[Spring Transaction Management Deep Dive]]
- [[40_PRODUCTION_CASES/Spring/Transaction Management Production Cases]]
- [[50_LABS/Spring/TX-B01/README]]
- [[98_SOURCES/Spring Transaction Management Sources]]
