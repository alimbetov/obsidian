---
type: card-bank
route: JAVA-B06
status: published
base_cards: 48
---
# JAVA-B06 Cards

## Functional interfaces and lambdas

### JAVA-LAMBDA-B06-C001
**Q:** Что делает interface functional?  
**A:** Ровно один abstract method после учёта методов `Object`; default/static не считаются.

### JAVA-LAMBDA-B06-C002
**Q:** Зачем `@FunctionalInterface`?  
**A:** Даёт compile-time проверку SAM-контракта; сама функциональность существует и без annotation.

### JAVA-LAMBDA-B06-C003
**Q:** Почему `var x = a -> a + 1` не компилируется?  
**A:** Lambda не имеет самостоятельного типа; отсутствует target type.

### JAVA-LAMBDA-B06-C004
**Q:** Что такое target typing?  
**A:** Определение типов параметров и return lambda из ожидаемого functional interface.

### JAVA-LAMBDA-B06-C005
**Q:** Что значит effectively final?  
**A:** Локальная переменная не объявлена `final`, но после инициализации не переназначается.

### JAVA-LAMBDA-B06-C006
**Q:** Почему локальную captured variable нельзя менять?  
**A:** Lambda захватывает значение/ссылку локальной переменной, а не общую mutable stack-cell.

### JAVA-LAMBDA-B06-C007
**Q:** Можно ли менять состояние объекта, ссылка на который captured?  
**A:** Да; ограничение касается переназначения локальной переменной, но side effects требуют осторожности.

### JAVA-LAMBDA-B06-C008
**Q:** Четыре формы method reference?  
**A:** static, bound instance, unbound instance, constructor.

### JAVA-LAMBDA-B06-C009
**Q:** `String::trim` — какая форма?  
**A:** Unbound instance; receiver становится первым SAM-параметром.

### JAVA-LAMBDA-B06-C010
**Q:** `value::trim` — какая форма?  
**A:** Bound instance; receiver уже зафиксирован.

### JAVA-LAMBDA-B06-C011
**Q:** Основные `java.util.function` interfaces?  
**A:** Predicate, Function, Consumer, Supplier, UnaryOperator, BinaryOperator и bi/primitive variants.

### JAVA-LAMBDA-B06-C012
**Q:** Разница Function и UnaryOperator?  
**A:** Function может менять тип T→R; UnaryOperator сохраняет T→T.

## Pipeline semantics

### JAVA-STREAM-B06-C013
**Q:** Что хранит Stream?  
**A:** Ленивое описание pipeline и source traversal, а не коллекцию элементов.

### JAVA-STREAM-B06-C014
**Q:** Что запускает pipeline?  
**A:** Terminal operation.

### JAVA-STREAM-B06-C015
**Q:** Почему `peek` может ничего не выполнить?  
**A:** Без terminal operation traversal не начинается; short-circuit может не посетить все элементы.

### JAVA-STREAM-B06-C016
**Q:** Можно ли повторно использовать Stream?  
**A:** Нет; после terminal operation он consumed.

### JAVA-STREAM-B06-C017
**Q:** `filter` меняет тип?  
**A:** Нет, оставляет 0 или 1 элемент того же типа.

### JAVA-STREAM-B06-C018
**Q:** `map` меняет cardinality?  
**A:** Обычно один вход → один выход; тип может измениться.

### JAVA-STREAM-B06-C019
**Q:** Для чего `flatMap`?  
**A:** Раскрывает 0..N элементов на каждый вход и убирает вложенный stream/container layer.

### JAVA-STREAM-B06-C020
**Q:** От чего зависит `distinct`?  
**A:** От корректных `equals/hashCode`.

### JAVA-STREAM-B06-C021
**Q:** Почему `sorted` stateful?  
**A:** Для выдачи первого результата обычно нужно увидеть и упорядочить множество элементов.

### JAVA-STREAM-B06-C022
**Q:** Что такое encounter order?  
**A:** Определённый source/pipeline порядок элементов, который некоторые operations сохраняют.

### JAVA-STREAM-B06-C023
**Q:** Примеры short-circuit terminal operations?  
**A:** findFirst, findAny, anyMatch, allMatch, noneMatch.

### JAVA-STREAM-B06-C024
**Q:** Примеры short-circuit intermediate operations?  
**A:** limit, takeWhile.

## Reduction and collectors

### JAVA-STREAM-B06-C025
**Q:** Требования к identity?  
**A:** Нейтральность для accumulator/combiner.

### JAVA-STREAM-B06-C026
**Q:** Почему reduction должна быть associative?  
**A:** Parallel grouping может меняться, но результат должен оставаться тем же.

### JAVA-STREAM-B06-C027
**Q:** Когда `reduce`, когда `collect`?  
**A:** Reduce для value reduction; collect для mutable result container.

### JAVA-STREAM-B06-C028
**Q:** Зачем combiner?  
**A:** Объединяет partial results, особенно в parallel execution.

### JAVA-STREAM-B06-C029
**Q:** Что делает `groupingBy`?  
**A:** Группирует элементы по classifier key в Map.

### JAVA-STREAM-B06-C030
**Q:** Что делает `partitioningBy`?  
**A:** Разделяет по boolean predicate на true/false groups.

### JAVA-STREAM-B06-C031
**Q:** Почему `toMap` может бросить exception?  
**A:** Duplicate keys без merge function.

### JAVA-STREAM-B06-C032
**Q:** Что делает downstream collector?  
**A:** Определяет aggregation внутри каждой группы.

### JAVA-STREAM-B06-C033
**Q:** `Collectors.toList()` гарантирует implementation?  
**A:** Нет.

### JAVA-STREAM-B06-C034
**Q:** `Stream.toList()` modifiable?  
**A:** Нет, возвращаемый list unmodifiable.

### JAVA-STREAM-B06-C035
**Q:** Роль supplier в custom collect?  
**A:** Создаёт новый mutable result container.

### JAVA-STREAM-B06-C036
**Q:** Почему shared external list — плохой collector?  
**A:** Нарушает isolation, thread safety и повторяемость pipeline.

## Primitive and optional streams

### JAVA-STREAM-B06-C037
**Q:** Зачем IntStream?  
**A:** Избегает boxing и предоставляет sum/range/statistics.

### JAVA-STREAM-B06-C038
**Q:** `range(1,4)`?  
**A:** 1,2,3.

### JAVA-STREAM-B06-C039
**Q:** `rangeClosed(1,4)`?  
**A:** 1,2,3,4.

### JAVA-STREAM-B06-C040
**Q:** Почему max возвращает OptionalInt?  
**A:** Пустой stream не имеет максимума.

### JAVA-STREAM-B06-C041
**Q:** Что возвращает empty IntStream sum?  
**A:** 0 — additive identity.

### JAVA-STREAM-B06-C042
**Q:** Что возвращает empty average?  
**A:** Empty OptionalDouble.

## Parallel streams

### JAVA-STREAM-B06-C043
**Q:** Автоматически ли parallel быстрее?  
**A:** Нет; нужны измерения, подходящий source и достаточно крупный CPU-bound workload.

### JAVA-STREAM-B06-C044
**Q:** Главный запрет для parallel pipeline?  
**A:** Shared mutable state и interfering functions.

### JAVA-STREAM-B06-C045
**Q:** Где выполняется parallel stream по умолчанию?  
**A:** Обычно в ForkJoinPool common pool.

### JAVA-STREAM-B06-C046
**Q:** Цена `forEachOrdered`?  
**A:** Координация для сохранения encounter order, потенциально меньше parallel benefit.

### JAVA-STREAM-B06-C047
**Q:** Почему ArrayList лучше split, чем LinkedList?  
**A:** Индексируемое contiguous storage позволяет дешевле и равномернее делить диапазоны.

### JAVA-STREAM-B06-C048
**Q:** Когда parallelStream особенно сомнителен?  
**A:** Малый input, blocking I/O, shared state, ordered/stateful operations, unknown common-pool contention.
