---
type: moc
domain: interview
status: active
tags:
  - interview
  - questions
---

# Interview Questions MOC

## Java

- Language and OOP
- Collections
- Streams
- JVM
- Java 8, 11, 17 and 21

### Concurrency

- [[10_CONCEPTS/Java/Concurrency/Concurrency Learning Path|Learning Path]]
- [[20_QUESTIONS/Interview/Java/Concurrency/Why volatile does not make increment atomic]]
- [[20_QUESTIONS/Interview/Java/Concurrency/What does happens-before actually guarantee]]
- [[20_QUESTIONS/Interview/Java/Concurrency/How synchronized provides visibility]]
- [[20_QUESTIONS/Interview/Java/Concurrency/execute vs submit]]
- [[20_QUESTIONS/Interview/Java/Concurrency/thenApply vs thenCompose]]
- [[20_QUESTIONS/Interview/Java/Concurrency/Are virtual threads faster]]
- [[20_QUESTIONS/Interview/Java/Why can ThreadLocal leak in a thread pool]]

## Spring

- IoC and dependency injection
- Bean lifecycle
- AOP and proxies
- Transactions
- Spring Data
- Spring Boot
- Spring Security
- Testing

## Databases

- Transactions and isolation
- Locks and deadlocks
- Indexes
- Query plans
- Partitioning and sharding
- Physical and logical replication

## Messaging

- Delivery guarantees
- Kafka partitions and consumer groups
- Offset management
- Idempotency
- Retry and dead-letter processing
- Transactional Outbox

## Distributed systems

- Circuit breaker
- Retry and timeout
- Service discovery
- Consistency
- Distributed transactions
- System design scenarios

## Уровни качественного ответа

Сильный ответ обычно содержит:

1. прямое определение;
2. внутренний механизм;
3. практический пример;
4. ограничения и trade-offs;
5. production failure mode;
6. вероятные дополнительные вопросы.

## Правило самопроверки

> [!question]
> Можешь ли ты объяснить тему без слов «просто», «магически», «JVM сама» и «обычно работает»?

> [!answer]- Критерий
> Ответ должен ссылаться на конкретную гарантию: happens-before, atomic operation, monitor, queue policy, executor lifecycle или resource limit.
