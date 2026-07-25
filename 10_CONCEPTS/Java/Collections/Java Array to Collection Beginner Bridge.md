---
type: atomic-lesson
route: JAVA-B05
level: A-B
java_versions: [17, 21]
tags: [array, list, set, map, beginner]
---

# Java Array to Collection Beginner Bridge

## Простая ситуация

Нужно хранить имена участников. Сначала известно, что их ровно три:

```java
String[] names = {"Aida", "Boris", "Chen"};
```

Позже число участников меняется, появляются дубликаты и поиск по идентификатору.

## Mental model

```text
array = ряд ячеек фиксированной длины
List  = растущий ряд элементов
Set   = коробка уникальных элементов
Map   = шкаф: ключ открывает нужное значение
```

## Минимальный код

```java
var list = new ArrayList<String>();
list.add("Aida");
list.add("Boris");

var set = new HashSet<String>();
set.add("Aida");
set.add("Aida");

var map = new HashMap<Integer, String>();
map.put(101, "Aida");
```

## Пошаговая трассировка

1. `ArrayList` начинает с внутреннего массива.
2. `add` помещает ссылку в следующую свободную позицию.
3. Когда capacity недостаточна, создаётся больший массив и ссылки копируются.
4. `HashSet` вычисляет hash и проверяет, есть ли equal element.
5. `HashMap` вычисляет bucket по key и связывает key с value.

## Точный механизм

Размер массива неизменяем: `array.length` задаётся при создании. Размер коллекции меняется, но это не означает отсутствие массива внутри implementation. Например, `ArrayList` скрывает resize за API.

`Set` не «удаляет дубликат после добавления»: операция `add` проверяет membership и возвращает `false`, если equal element уже существует.

`Map` не является `Collection`; она имеет отдельный key/value API и views `keySet()`, `values()`, `entrySet()`.

## Контраст

```java
String[] a = new String[2]; // fixed length
List<String> b = new ArrayList<>(); // variable size

b.add("x");
b.add("x"); // allowed

Set<String> c = new HashSet<>();
c.add("x");
c.add("x"); // size remains 1
```

## Самостоятельный прогноз

Что выведет код?

```java
var names = new HashSet<>(List.of("A", "B", "A"));
System.out.println(names.size());
```

Ответ зафиксируйте до запуска.

## Executable proof

См. `CollectionProof.beginnerBridge()` в [[50_LABS/Java/JAVA-B05/README]].
