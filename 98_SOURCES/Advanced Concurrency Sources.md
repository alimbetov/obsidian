---
type: sources
domain: java
subdomain: concurrency
status: active
tags: [java, concurrency, sources]
---

# Advanced Concurrency Sources

## Atomic variables

- Atomic package  
  https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/package-summary.html
- AtomicInteger  
  https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/AtomicInteger.html
- AtomicReference  
  https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/AtomicReference.html
- AtomicStampedReference  
  https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/AtomicStampedReference.html
- LongAdder  
  https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/LongAdder.html

## Locks and diagnostics

- Locks package  
  https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/package-summary.html
- ReentrantLock  
  https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/ReentrantLock.html
- ThreadMXBean deadlock detection APIs  
  https://docs.oracle.com/en/java/javase/21/docs/api/java.management/java/lang/management/ThreadMXBean.html

## Concurrent collections

- ConcurrentHashMap  
  https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html
- CopyOnWriteArrayList  
  https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CopyOnWriteArrayList.html
- BlockingQueue  
  https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/BlockingQueue.html
- ArrayBlockingQueue  
  https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ArrayBlockingQueue.html
- ConcurrentLinkedQueue  
  https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ConcurrentLinkedQueue.html

## Interpretation policy

- Public API contracts are stable learning targets.
- Internal implementation details may differ between JDK versions.
- Runtime examples demonstrate behavior but do not prove the absence of races.
- Performance claims must be measured against the actual workload and contention profile.
