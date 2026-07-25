---
type: concept-hub
domain: java
route: JAVA-B05
status: published
java_versions: [17, 21]
tags: [collections, generics, sequencing]
---

# Java Collections, Generics and Sequenced Collections

## Карта темы

```text
данные
├─ фиксированный размер → array
├─ последовательность → List
├─ уникальность → Set
├─ поиск по ключу → Map
└─ обработка с концов → Queue / Deque
```

## Главный принцип

Коллекцию выбирают не по привычке, а по доминирующей операции:

| Требование | Базовый выбор | Причина |
|---|---|---|
| быстрый доступ по индексу | `ArrayList` | contiguous backing array |
| уникальность и быстрый membership check | `HashSet` | hash lookup |
| сохранение insertion order | `LinkedHashSet` / `LinkedHashMap` | linked encounter order |
| sorted order | `TreeSet` / `TreeMap` | balanced search tree |
| FIFO/LIFO | `ArrayDeque` | efficient operations at both ends |
| key → value lookup | `HashMap` | bucket-based lookup |

## Внутренние механизмы

- `ArrayList`: backing array, capacity, growth, element shifting.
- `HashMap`: hash spreading, buckets, equality check, collision handling, resize.
- `TreeMap`: comparison-based ordering; equality is defined by comparison result for key placement.
- `ArrayDeque`: circular array; no indexed random access contract.
- `LinkedList`: doubly linked nodes; cheap end insertion but expensive traversal and poor locality.

## Generics model

```text
List<Dog> НЕ является List<Animal>
```

Иначе в `List<Dog>` можно было бы добавить `Cat`. Поэтому Java generics invariant.

```java
static double sum(List<? extends Number> source) { ... }
static void addDefaults(List<? super Integer> target) { ... }
```

`extends` — читаем producer. `super` — передаём consumer. Это PECS, а не правило «extends всегда для чтения» вне конкретного API-контекста.

## Equality boundary

Hash-based structures сначала используют `hashCode()`, затем `equals()`. Если поля ключа, участвующие в этих методах, изменились после вставки, объект может остаться физически в старом bucket и перестать находиться логически.

## Java 21 boundary

Java 21 вводит:

- `SequencedCollection<E>`;
- `SequencedSet<E>`;
- `SequencedMap<K,V>`;
- `getFirst()`, `getLast()`, `addFirst()`, `addLast()`, `reversed()`;
- first/last entry operations для maps.

`reversed()` обычно возвращает view: изменение исходной структуры отражается во view и наоборот, если операция поддерживается.

## Связанные уроки

- [[10_CONCEPTS/Java/Collections/Java Array to Collection Beginner Bridge]]
- [[10_CONCEPTS/Java/Collections/Java List Set Map Queue and Deque]]
- [[10_CONCEPTS/Java/Collections/Java Collection Implementations and Internals]]
- [[10_CONCEPTS/Java/Collections/Java Equality Hashing and Mutable Keys]]
- [[10_CONCEPTS/Java/Collections/Java Comparable Comparator and Ordering]]
- [[10_CONCEPTS/Java/Generics/Java Generics Invariance Wildcards and PECS]]
- [[10_CONCEPTS/Java/Generics/Java Generic Methods Erasure and Heap Pollution]]
- [[10_CONCEPTS/Java/Collections/Java Immutable Unmodifiable and Sequenced Collections]]
