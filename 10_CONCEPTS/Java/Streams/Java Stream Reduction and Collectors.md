---
type: atomic-lesson
route: JAVA-B06
status: published
---
# Java Stream Reduction and Collectors

## Простая ситуация
Нужно свернуть много элементов в один итог или mutable result container.

```java
int sum = List.of(2, 3, 4).stream().reduce(0, Integer::sum);
```

## Mental model
Reduction объединяет элементы по одному правилу.

```text
identity + element → partial result
partial + element  → next partial
partial + partial  → combined result (parallel)
```

## Точный механизм
Identity должна быть нейтральной, accumulator и combiner — совместимыми и ассоциативными. Иначе sequential и parallel результаты могут отличаться.

```java
int wrong = numbers.parallelStream().reduce(10, Integer::sum); // 10 добавится в каждый partition
```

`collect()` применяется для mutable accumulation:

```java
Map<String, List<Person>> byCity = people.stream()
        .collect(Collectors.groupingBy(Person::city));

Map<Boolean, List<Person>> adults = people.stream()
        .collect(Collectors.partitioningBy(p -> p.age() >= 18));
```

Ключевые collectors: `toList`, `toSet`, `joining`, `mapping`, `filtering`, `groupingBy`, `partitioningBy`, `counting`, `summingInt`, `averagingInt`, `toMap`.

## Контраст
`reduce` создаёт immutable-style value из values. `collect` заполняет mutable container через supplier/accumulator/combiner.

```java
Map<String,Integer> lengths = names.stream().collect(Collectors.toMap(
        Function.identity(), String::length,
        (left, right) -> left));
```

Merge function обязательна, если возможны duplicate keys.

## Прогноз
Почему вычитание не подходит для parallel reduction без специальной модели?

## Evidence
[[50_LABS/Java/JAVA-B06/README]]
