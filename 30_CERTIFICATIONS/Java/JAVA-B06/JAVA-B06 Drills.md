---
type: drill-bank
route: JAVA-B06
status: published
drills: 24
---
# JAVA-B06 Drills

Для каждого задания сначала дай прогноз: compile / output / exception / semantic defect.

1. `Predicate<String> p = s -> s.isBlank();` — назови SAM и тип `s`.
2. `var p = s -> s.length();` — объясни compile result.
3. После `int n=2; n++;` захвати `n` в lambda — объясни ошибку.
4. Сравни `String::trim` и `text::trim` по SAM parameters.
5. Определи подходящий interface для `() -> UUID.randomUUID()`.
6. Определи interface для `(a,b) -> Math.max(a,b)`.
7. Предскажи, выполнится ли `peek` без terminal operation.
8. Предскажи visited elements для `peek().filter(even).findFirst()` на `[1,2,3,4]`.
9. Повторно вызови terminal operation на одном stream.
10. Получи `Stream<Stream<String>>` через `map`; исправь через `flatMap`.
11. Поставь `filter` до дорогого `map`, сохранив семантику.
12. Предскажи `List.of("b","a","b").stream().distinct().sorted().toList()`.
13. Объясни requirement `Comparable` для `sorted()`.
14. Предскажи `IntStream.range(2,5).sum()`.
15. Предскажи `IntStream.rangeClosed(2,5).count()`.
16. Безопасно обработай `IntStream.empty().max()`.
17. Исправь `toMap(String::length, identity())` для duplicate lengths.
18. Сгруппируй строки по первой букве и посчитай количество в группе.
19. Раздели числа на even/odd через `partitioningBy`.
20. Объясни, почему `reduce(10, Integer::sum)` опасен в parallel stream.
21. Найди algebra defect у `parallel().reduce(0, (a,b)->a-b)`.
22. Исправь parallel `forEach(sharedArrayList::add)`.
23. Сравни `findFirst` и `findAny` в unordered parallel stream.
24. Для blocking HTTP calls объясни, почему parallel stream — не default choice.

## Answer checkpoints

- 2: compile error — отсутствует target type.
- 3: compile error — переменная не effectively final.
- 7: ничего не выполняется.
- 8: посещаются 1 и 2; затем short-circuit.
- 9: `IllegalStateException`.
- 12: `[a, b]`.
- 14: `9`.
- 15: `4`.
- 20: identity может примениться к каждому partition.
- 21: subtraction не associative.
- 22: использовать `map(...).toList()` или корректный collector.
