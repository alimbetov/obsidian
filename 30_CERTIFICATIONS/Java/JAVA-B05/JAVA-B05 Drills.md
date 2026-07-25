---
type: drill-bank
route: JAVA-B05
status: published
drills: 24
java_versions: [17, 21]
---

# JAVA-B05 Drills

Для каждого задания сначала укажите: `COMPILES`, `DOES NOT COMPILE` или точный output.

## Output drills

1. `new HashSet<>(List.of("A", "A", "B")).size()` → `2`.
2. Два `put` с equal key: второй возвращает previous value.
3. `ArrayDeque.offer(1); offer(2); poll()` → `1`.
4. `push(1); push(2); pop()` → `2`.
5. `Collections.unmodifiableList(source)` отражает последующий `source.add`.
6. `List.copyOf(source)` не отражает последующий `source.add`.
7. `TreeSet` с comparator по длине схлопывает строки одинаковой длины.
8. Mutable hash key после изменения может перестать находиться.
9. `List.of("A", null)` throws `NullPointerException`.
10. `Set.of("A", "A")` throws `IllegalArgumentException`.
11. Java 21 `reversed().getFirst()` равен original `getLast()`.
12. `Map.putIfAbsent` не заменяет non-null existing mapping.

## Compile drills

13.
```java
List<Number> x = new ArrayList<Integer>();
```
`DOES NOT COMPILE`: invariance.

14.
```java
List<? extends Number> x = new ArrayList<Integer>();
x.add(1);
```
`DOES NOT COMPILE`: captured subtype unknown.

15.
```java
List<? super Integer> x = new ArrayList<Number>();
x.add(1);
```
`COMPILES`.

16.
```java
Number n = new ArrayList<? extends Number>().get(0);
```
`DOES NOT COMPILE`: wildcard нельзя использовать в constructor type argument.

17.
```java
List<String>[] x = new List<String>[2];
```
`DOES NOT COMPILE`: generic array creation.

18.
```java
if (value instanceof List<String>) {}
```
`DOES NOT COMPILE`: non-reifiable parameterized type.

19.
```java
static T first<T>(List<T> x) { return x.get(0); }
```
`DOES NOT COMPILE`: `<T>` должно стоять перед return type.

20.
```java
static <T> T first(List<T> x) { return x.get(0); }
```
`COMPILES`.

21.
```java
List<?> x = new ArrayList<String>();
x.add(null);
```
`COMPILES`.

22.
```java
List<?> x = new ArrayList<String>();
x.add("A");
```
`DOES NOT COMPILE`.

23. Использование `SequencedCollection` с `javac --release 17` → `DOES NOT COMPILE`.

24. Comparator lambda, возвращающая boolean вместо int → `DOES NOT COMPILE`.

## Repair questions

Для каждого compile-fail:

1. Назовите нарушенный type-system rule.
2. Исправьте код минимально.
3. Объясните, какую runtime ошибку предотвращает compiler.
4. Найдите соответствующий файл в negative proof bank.
