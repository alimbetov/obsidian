---
type: card-bank
route: JAVA-B05
status: published
cards: 48
java_versions: [17, 21]
---

# JAVA-B05 Cards

## Beginner bridge

1. **Почему array fixed-size?** Его length задаётся при создании объекта массива и не меняется.
2. **Что добавляет `List` поверх array mental model?** Dynamic logical size и collection API.
3. **Главное свойство `Set`?** Не более одного элемента для одной logical equality position.
4. **Главное свойство `Map`?** Equal key связан максимум с одним current value.
5. **FIFO structure?** `Queue`, обычно `ArrayDeque`.
6. **LIFO без legacy `Stack`?** `Deque.push/pop/peek`.

## Interfaces and contracts

7. **`poll()` на пустой queue?** `null`.
8. **`remove()` на пустой queue?** `NoSuchElementException`.
9. **Что возвращает `Map.put`?** Previous value или `null`.
10. **Почему `Map` не extends `Collection`?** Entry key/value model не является single-element collection contract.
11. **Допускает ли `List` duplicates?** Да.
12. **Может ли `Set.add` вернуть `false`?** Да, если equal element уже присутствует.

## Implementations and Big-O

13. **`ArrayList.get(i)` average complexity?** O(1).
14. **`ArrayList` insertion in middle?** O(n) из-за shifting.
15. **`HashMap.get` average complexity?** O(1), при корректном hashing.
16. **`TreeMap.get` complexity?** O(log n).
17. **Почему `LinkedList.get(i)` O(n)?** Нужно пройти nodes.
18. **Почему Big-O недостаточно?** Не учитывает allocations, locality, constants, hash/comparator cost.
19. **Default queue/deque choice?** `ArrayDeque`.
20. **Когда нужен `LinkedHashMap`?** Нужны hash lookup и predictable encounter/access order.

## Equality and hashing

21. **Equal objects и hash codes?** Equal обязаны иметь одинаковый hash.
22. **Unequal objects могут иметь одинаковый hash?** Да, collision допустим.
23. **Что происходит после mutation hash key?** Lookup может искать в другом bucket.
24. **Лучший key type?** Immutable value object/record со stable identity.
25. **Исправляет ли `IdentityHashMap` плохой equality contract?** Нет.
26. **Когда HashMap вызывает equals?** После bucket/hash candidate narrowing.

## Ordering

27. **`Comparable`?** Natural order типа.
28. **`Comparator`?** Внешняя стратегия order.
29. **Почему нельзя `a-b` в comparator?** Integer overflow нарушает ordering.
30. **Что значит compare result zero в `TreeSet`?** Та же sorted position; второй элемент не добавится.
31. **Как избежать collapse по price?** Добавить tie-breaker.
32. **Как задать null policy?** `nullsFirst`/`nullsLast`.

## Generics

33. **Является ли `List<Integer>` subtype `List<Number>`?** Нет, generics invariant.
34. **Почему invariance безопасна?** Запрещает добавить иной subtype через widened reference.
35. **`? extends Number`: что безопасно читать?** `Number`.
36. **Можно ли добавить `Integer` в `List<? extends Number>`?** Нет.
37. **`? super Integer`: что можно добавить?** `Integer` и его subtypes.
38. **Что безопасно читать из `? super Integer`?** `Object`.
39. **PECS?** Producer Extends, Consumer Super.
40. **`List<?>` и add?** Только `null` universally safe.

## Erasure and pollution

41. **Где объявляется type parameter generic method?** Перед return type.
42. **Есть ли runtime class `List<String>`?** Нет отдельной reified class.
43. **Почему запрещён `new T()`?** Erasure не даёт runtime constructor type.
44. **Почему raw types опасны?** Отключают generic checks и создают unchecked paths.
45. **Heap pollution?** Parameterized reference указывает на incompatible object/content.
46. **Почему generic arrays запрещены?** Arrays reified/covariant, generics erased/invariant.

## Immutability and Java 21

47. **Unmodifiable view vs immutable snapshot?** View отражает source changes; snapshot independent.
48. **Что возвращает `SequencedCollection.reversed()`?** Reverse-ordered view с first/last semantics.
