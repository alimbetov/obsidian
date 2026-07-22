---
type: source-index
domain: spring
subdomain: transactions
status: active
spring_versions:
  - 5.3.39
  - current
tags:
  - spring
  - transactions
  - sources
---

# Spring Transaction Management Sources

## Primary baseline — Spring Framework 5.3.39

The certification route uses Spring Framework 5.3.39 as the executable and semantic baseline.

- Spring Framework 5.3.39 Data Access reference:
  - https://docs.spring.io/spring-framework/docs/5.3.39/reference/html/data-access.html
- `@Transactional` 5.3.39 Javadoc:
  - https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/transaction/annotation/Transactional.html
- `Propagation` 5.3.39 Javadoc:
  - https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/transaction/annotation/Propagation.html
- `PlatformTransactionManager` 5.3.39 Javadoc:
  - https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/transaction/PlatformTransactionManager.html
- `TransactionDefinition` 5.3.39 Javadoc:
  - https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/transaction/TransactionDefinition.html
- `TransactionStatus` 5.3.39 Javadoc:
  - https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/transaction/TransactionStatus.html
- `TransactionTemplate` 5.3.39 Javadoc:
  - https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/transaction/support/TransactionTemplate.html
- `TransactionSynchronizationManager` 5.3.39 Javadoc:
  - https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/transaction/support/TransactionSynchronizationManager.html
- `TransactionSynchronization` 5.3.39 Javadoc:
  - https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/transaction/support/TransactionSynchronization.html
- `@TransactionalEventListener` 5.3.39 Javadoc:
  - https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/transaction/event/TransactionalEventListener.html

## Current Spring reference

Use current documentation to detect evolution, but do not silently project new behavior back onto the 5.3 exam baseline.

- Transaction management overview:
  - https://docs.spring.io/spring-framework/reference/data-access/transaction.html
- Transaction abstraction:
  - https://docs.spring.io/spring-framework/reference/data-access/transaction/strategies.html
- Declarative implementation:
  - https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-decl-explained.html
- Propagation:
  - https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-propagation.html
- Rollback rules:
  - https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/rolling-back.html
- Transaction-bound events:
  - https://docs.spring.io/spring-framework/reference/data-access/transaction/event.html
- Programmatic transaction management:
  - https://docs.spring.io/spring-framework/reference/data-access/transaction/programmatic.html

## Transactional Outbox references

- Spring blog — outbox pattern strategies:
  - https://spring.io/blog/2023/10/24/a-use-case-for-transactions-adapting-to-transactional-outbox-pattern/
- Spring Cloud Stream transaction series introduction:
  - https://spring.io/blog/2023/09/27/introduction-to-transactions-in-spring-cloud-stream-kafka-applications/

## Database references for isolation experiments

Transaction isolation is implemented by the database and driver. Spring requests a level; actual phenomena and locking behavior remain database-specific.

- PostgreSQL transaction isolation:
  - https://www.postgresql.org/docs/current/transaction-iso.html
- PostgreSQL explicit locking:
  - https://www.postgresql.org/docs/current/explicit-locking.html
- H2 commands and transaction support:
  - https://www.h2database.com/html/commands.html

## Stable public contracts

Treat these as stable concepts:

- `PlatformTransactionManager` starts, commits and rolls back transactions;
- declarative transactions are normally applied through an interceptor/proxy boundary;
- imperative transactions are normally thread-bound;
- `REQUIRED` can map multiple logical scopes to one physical transaction;
- `REQUIRES_NEW` creates an independent physical transaction;
- `NESTED` generally maps to savepoints for JDBC transactions;
- default rollback is for `RuntimeException` and `Error`, not checked exceptions;
- transaction synchronization callbacks have explicit completion phases;
- `@TransactionalEventListener` can bind a listener to a transaction phase.

## Version-sensitive or implementation-sensitive areas

Verify before relying on:

- exact advisor ordering when several infrastructure advisors coexist;
- reactive transaction behavior and Reactor context propagation;
- nested transaction support of a particular transaction manager;
- savepoint behavior of a driver/database;
- isolation semantics of a particular database;
- multiple-manager auto-selection in Spring Boot;
- transaction labels and newer annotation attributes;
- event listener behavior added after the Spring 5.3 baseline;
- cache transaction-awareness and cross-resource guarantees.

## Source discipline

1. Explain Spring contract from Spring documentation.
2. Explain database phenomena from database documentation.
3. Separate logical Spring scopes from physical database transactions.
4. State when behavior depends on transaction manager, driver or database.
5. Never describe `REQUIRES_NEW` as a savepoint.
6. Never describe `NESTED` as a second independent physical transaction.
7. Never describe `transactionAware()` cache behavior as XA atomicity.
8. Never describe `@TransactionalEventListener(AFTER_COMMIT)` as a durable message-delivery guarantee.
