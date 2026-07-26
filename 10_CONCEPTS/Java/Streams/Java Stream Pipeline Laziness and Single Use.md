---
type: atomic-lesson
route: JAVA-B06
status: published
---
# Java Stream Pipeline Laziness and Single Use

## Простая ситуация
Нужно описать обработку списка как цепочку операций.

```java
List<String> names = List.of("Ann", "Robert", "Mia");
long count = names.stream()
        .filter(s -> s.length() >= 4)
        .count();
```

## Mental model
Stream не хранит элементы. Он хранит план обхода source. Intermediate operations дополняют план, terminal operation запускает traversal.

```text
source → filter → map → limit → terminal
```

## Пошаговая трассировка
Для каждого элемента pipeline обычно выполняется вертикально: элемент проходит `filter`, затем `map`, затем может быть остановлен short-circuit операцией.

```java
var result = List.of(1, 2, 3, 4).stream()
        .peek(x -> System.out.println("seen " + x))
        .filter(x -> x % 2 == 0)
        .findFirst();
```

Обход завершится после `2`.

## Точный механизм
- `filter`, `map`, `sorted`, `distinct` — intermediate.
- `count`, `collect`, `reduce`, `findFirst` — terminal.
- После terminal operation stream consumed.
- Source collection сама по себе не меняется.

```java
Stream<String> stream = names.stream();
stream.count();
stream.findFirst(); // IllegalStateException
```

## Контраст
Collection можно обходить повторно. Stream представляет один pipeline execution.

## Прогноз
Почему `peek()` без terminal operation обычно ничего не печатает?

## Evidence
[[50_LABS/Java/JAVA-B06/README]]
