---
type: atomic-lesson
route: JAVA-B06
status: published
---
# Java Parallel Streams Safety and Performance

## Простая ситуация
Есть CPU-bound обработка большого независимого набора данных.

```java
long count = values.parallelStream()
        .map(this::expensivePureTransform)
        .filter(this::accepted)
        .count();
```

## Mental model
Parallel stream делит source на partitions, обрабатывает их в ForkJoinPool и объединяет partial results.

## Безопасный контракт
- функции stateless и non-interfering;
- source не изменяется во время traversal;
- reduction associative;
- combiner совместим с accumulator;
- порядок либо не важен, либо явно сохраняется ценой координации;
- workload достаточно крупный и CPU-bound.

## Опасный контраст
```java
List<Integer> target = new ArrayList<>();
values.parallelStream().forEach(target::add); // race и structural corruption
```

Используй collector:

```java
List<Integer> target = values.parallelStream().map(this::convert).toList();
```

## Точный механизм
Parallelism добавляет splitting, scheduling, coordination и merging overhead. Маленький input, blocking I/O, ordered operations, shared mutable state и плохой spliterator часто делают parallel pipeline медленнее или неверным.

`forEachOrdered` сохраняет encounter order, но уменьшает свободу выполнения. Common pool разделяется с другими задачами процесса.

## Решение для production
Сначала измерь sequential baseline через JMH/production metrics. Для I/O и контролируемой конкуренции чаще выбирай executor, virtual threads или reactive pipeline, а не `parallelStream()`.

## Прогноз
Почему `ArrayList` обычно делится лучше, чем `LinkedList`, и почему это влияет на speedup?

## Evidence
[[50_LABS/Java/JAVA-B06/README]]
