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

## Memory model and execution

1. Java Language Specification, Chapter 17 — Threads and Locks  
   https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html
2. JLS 17.4 — Memory Model  
   https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html#jls-17.4
3. `java.util.concurrent` package  
   https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/package-summary.html
4. `ExecutorService`  
   https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ExecutorService.html
5. `CompletableFuture`  
   https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html
6. `ThreadLocal`  
   https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ThreadLocal.html
7. JEP 444 — Virtual Threads  
   https://openjdk.org/jeps/444

## Atomic variables

8. `java.util.concurrent.atomic` package  
   https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/package-summary.html
9. `AtomicInteger`  
   https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/AtomicInteger.html
10. `AtomicReference`  
    https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/AtomicReference.html
11. `AtomicStampedReference`  
    https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/AtomicStampedReference.html
12. `LongAdder`  
    https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/LongAdder.html

## Locks and concurrent collections

13. `ReentrantLock`  
    https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/ReentrantLock.html
14. `ConcurrentHashMap`  
    https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html
15. `CopyOnWriteArrayList`  
    https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CopyOnWriteArrayList.html
16. `BlockingQueue`  
    https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/BlockingQueue.html
17. `ArrayBlockingQueue`  
    https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ArrayBlockingQueue.html
18. `ConcurrentLinkedQueue`  
    https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ConcurrentLinkedQueue.html

## Diagnostics

19. `ThreadMXBean` deadlock detection APIs  
    https://docs.oracle.com/en/java/javase/21/docs/api/java.management/java/lang/management/ThreadMXBean.html

## Source policy

- Formal guarantees come from JLS and official API contracts.
- Analogies explain intuition but never replace specification reasoning.
- Runtime experiments demonstrate symptoms, not proofs.
- Internal implementation details are version-sensitive unless documented as public contract.
- Performance claims require measurement against real workload and contention.
