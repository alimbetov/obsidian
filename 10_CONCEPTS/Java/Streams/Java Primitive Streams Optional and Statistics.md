---
type: atomic-lesson
route: JAVA-B06
status: published
---
# Java Primitive Streams Optional and Statistics

## Простая ситуация
Нужно посчитать сумму, среднее и максимум без постоянного boxing.

```java
IntSummaryStatistics stats = people.stream()
        .mapToInt(Person::age)
        .summaryStatistics();
```

## Mental model
`IntStream`, `LongStream`, `DoubleStream` специализируют pipeline для primitives.

```java
int total = orders.stream().mapToInt(Order::amount).sum();
OptionalInt max = scores.stream().mapToInt(Integer::intValue).max();
```

## Точный механизм
- `mapToInt` переводит object stream в primitive stream.
- `boxed` возвращает object stream.
- `range(a,b)` исключает `b`; `rangeClosed(a,b)` включает.
- `average`, `min`, `max`, `findFirst` возвращают Optional-like result, потому что stream может быть пустым.

```java
int max = values.max().orElseThrow();
double average = values.average().orElse(0.0);
```

Не вызывай `getAsInt()` без доказательства наличия значения.

## Контраст
`Stream<Integer>` допускает `null` и boxing. `IntStream` работает с `int`, имеет `sum()` и summary statistics.

## Прогноз
Что вернут `IntStream.empty().sum()` и `IntStream.empty().average()` и почему API различается?

## Evidence
[[50_LABS/Java/JAVA-B06/README]]
