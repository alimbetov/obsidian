---
type: sources
domain: java
subdomain: concurrency
status: active
tags:
  - java
  - concurrency
  - sources
---

# Java Concurrency Sources

## Primary specifications and documentation

1. Java Language Specification, Chapter 17 — Threads and Locks  
   https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html

2. JLS 17.4 — Memory Model  
   https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html#jls-17.4

3. `java.util.concurrent` package specification  
   https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/package-summary.html

4. `ExecutorService` API  
   https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ExecutorService.html

5. `CompletableFuture` API  
   https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html

6. `ThreadLocal` API  
   https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ThreadLocal.html

7. JEP 444 — Virtual Threads, delivered in JDK 21  
   https://openjdk.org/jeps/444

8. `Executors.newVirtualThreadPerTaskExecutor()` API  
   https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Executors.html#newVirtualThreadPerTaskExecutor()

## Source policy

- Formal guarantees are derived from JLS and official API documentation.
- Analogies explain intuition but never override specification wording.
- Runtime experiments demonstrate symptoms, not proofs.
- Version-specific statements must identify the target Java version.
- Secondary articles may be used for additional explanation, but not as the only source for memory-model guarantees.
