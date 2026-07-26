---
type: atomic-lesson
route: JAVA-B06
status: published
---
# Java Functional Interfaces and Lambda Target Typing

## Простая ситуация
Нужно передать правило сортировки или проверки, а не создавать отдельный класс.

## Mental model
Functional interface имеет один abstract method. Lambda — реализация этого метода без имени класса.

```java
Predicate<String> longName = s -> s.length() >= 5;
System.out.println(longName.test("stream")); // true
```

## Пошаговая трассировка
1. Target type — `Predicate<String>`.
2. Его SAM — `boolean test(String value)`.
3. `s` получает тип `String`.
4. Выражение возвращает `boolean`.

## Точный механизм
`@FunctionalInterface` проверяет SAM-контракт, но не создаёт его. Методы `Object`, default и static не увеличивают число abstract methods. Одна lambda может быть совместима с разными interfaces только через конкретный target context.

## Контраст
```java
var p = s -> s.isEmpty(); // compile error: нет target type
Predicate<String> p = s -> s.isEmpty(); // OK
```

## Прогноз
Почему `(a, b) -> a + b` может быть `BinaryOperator<Integer>`, но не имеет самостоятельного типа?

## Evidence
[[50_LABS/Java/JAVA-B06/README]]
