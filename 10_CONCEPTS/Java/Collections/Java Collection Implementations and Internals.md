---
type: atomic-lesson
route: JAVA-B05
level: B-C
java_versions: [17, 21]
tags: [arraylist, hashmap, trees, deque, big-o]
---

# Java Collection Implementations and Internals

## Простая ситуация

Одинаковый interface может иметь implementations с разной стоимостью операций.

## Mental model

```text
ArrayList   = массив + logical size
LinkedList  = цепочка nodes
HashMap     = buckets + hash + equals
TreeMap     = comparison tree
ArrayDeque  = circular array
```

## Минимальный код

```java
List<Integer> indexed = new ArrayList<>();
Set<Integer> unique = new HashSet<>();
Map<Integer, String> lookup = new HashMap<>();
Deque<Integer> ends = new ArrayDeque<>();
```

## Пошаговая трассировка HashMap

Для `map.get(key)`:

1. вызывается `key.hashCode()`;
2. hash преобразуется implementation-specific spreading function;
3. определяется bucket;
4. кандидаты сравниваются сначала по hash, затем через `equals()`;
5. найденное entry возвращает value.

При большом числе collisions bucket может перейти от linked structure к tree structure при выполнении implementation thresholds; нельзя строить бизнес-логику на конкретных внутренних порогах.

## Big-O карта

| Structure | get(index/key) | add end | add/remove middle | membership |
|---|---:|---:|---:|---:|
| `ArrayList` | O(1) index | amortized O(1) | O(n) | O(n) |
| `LinkedList` | O(n) | O(1) at known end | O(1) after node found | O(n) |
| `HashSet` | — | average O(1) | — | average O(1) |
| `TreeSet` | — | O(log n) | — | O(log n) |
| `HashMap` | average O(1) | average O(1) | — | average O(1) key |
| `TreeMap` | O(log n) | O(log n) | — | O(log n) key |
| `ArrayDeque` | no index API | amortized O(1) ends | unsupported middle model | O(n) |

Big-O не описывает memory locality, allocation pressure, comparator cost и hash quality.

## Контраст

`LinkedList` редко выигрывает у `ArrayList` в production: поиск позиции остаётся O(n), каждый node требует отдельной allocation, cache locality хуже.

## Самостоятельный прогноз

Какую структуру выбрать для:

- LRU-like encounter order;
- millions of key lookups;
- sorted range queries;
- BFS queue;
- frequent indexed reads?

## Executable proof

Proof проверяет semantic contracts, а не ненадёжные microbenchmarks.
