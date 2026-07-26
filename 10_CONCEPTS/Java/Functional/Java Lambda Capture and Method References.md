---
type: atomic-lesson
route: JAVA-B06
status: published
---
# Java Lambda Capture and Method References

## Простая ситуация
Lambda использует значение из окружающего метода.

```java
int minimum = 5;
Predicate<String> accepted = s -> s.length() >= minimum;
```

## Mental model
Lambda может читать локальный snapshot ссылки/значения, но локальная переменная должна оставаться final или effectively final.

```java
int limit = 3;
limit++;
Predicate<String> p = s -> s.length() > limit; // compile error
```

Поля объекта под это ограничение не попадают, потому что lambda захватывает `this`, а состояние объекта может меняться.

## Method references

| Lambda | Reference |
|---|---|
| `s -> s.trim()` | `String::trim` |
| `s -> Integer.parseInt(s)` | `Integer::parseInt` |
| `(a,b) -> a.compareTo(b)` | `String::compareTo` |
| `() -> new ArrayList<>()` | `ArrayList::new` |

Reference не «вызывает метод заранее» — он создаёт реализацию SAM, вызов произойдёт позже.

## Прогноз
Чем отличаются `instance::method` и `Type::instanceMethod` по числу параметров SAM?

## Evidence
[[50_LABS/Java/JAVA-B06/README]]
