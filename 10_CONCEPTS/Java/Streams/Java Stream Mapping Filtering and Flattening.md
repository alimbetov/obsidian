---
type: atomic-lesson
route: JAVA-B06
status: published
---
# Java Stream Mapping Filtering and Flattening

## Простая ситуация
Из заказов нужно получить уникальные названия товаров длиннее трёх символов.

```java
List<String> result = orders.stream()
        .flatMap(order -> order.items().stream())
        .map(Item::name)
        .filter(name -> name.length() > 3)
        .distinct()
        .sorted()
        .toList();
```

## Mental model
- `filter`: элемент остаётся или исчезает;
- `map`: один элемент превращается в один другой;
- `flatMap`: один элемент раскрывается в 0..N элементов;
- `distinct`: сохраняет первое появление по `equals/hashCode`;
- `sorted`: stateful operation, которой нужно учитывать несколько элементов;
- `limit/skip`: ограничивают позиционный диапазон.

## Контраст
```java
Stream<Stream<String>> nested = groups.stream().map(Collection::stream);
Stream<String> flat = groups.stream().flatMap(Collection::stream);
```

## Точный механизм
Порядок операций влияет и на смысл, и на стоимость. Обычно выгоднее отфильтровать данные до дорогого `map`/`sorted`, если семантика сохраняется.

```java
// сортирует меньше элементов
stream.filter(this::accepted).sorted().toList();
```

`distinct()` зависит от корректного equality contract. `sorted()` без comparator требует `Comparable`.

## Прогноз
Одинаковы ли результаты `map(...).filter(...)` и `filter(...).map(...)` для любой функции? Обоснуй через тип и predicate.

## Evidence
[[50_LABS/Java/JAVA-B06/README]]
