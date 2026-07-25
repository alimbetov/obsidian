---
type: atomic-lesson
route: JAVA-B05
level: A-C
java_versions: [17, 21]
tags: [list, set, map, queue, deque]
---

# Java List, Set, Map, Queue and Deque

## Простая ситуация

Интернет-магазину нужны: товары корзины, уникальные промокоды, товар по SKU и очередь заказов.

## Mental model

| Interface | Вопрос |
|---|---|
| `List<E>` | какой элемент находится на позиции `i`? |
| `Set<E>` | встречался ли такой элемент? |
| `Map<K,V>` | какое значение связано с ключом? |
| `Queue<E>` | какой элемент обработать следующим? |
| `Deque<E>` | что находится на первом или последнем конце? |

## Минимальный код

```java
List<String> cart = new ArrayList<>();
Set<String> coupons = new HashSet<>();
Map<String, Integer> stock = new HashMap<>();
Queue<Long> jobs = new ArrayDeque<>();
Deque<String> history = new ArrayDeque<>();
```

## Пошаговая трассировка

```java
jobs.offer(10L);
jobs.offer(20L);
System.out.println(jobs.poll());
```

1. `offer(10)` добавляет tail.
2. `offer(20)` добавляет новый tail.
3. `poll()` читает и удаляет head: `10`.

## Точный механизм

- `add`, `remove`, `element` могут бросать exception при невозможности операции.
- `offer`, `poll`, `peek` используют special value (`false`/`null`) для нормального отсутствия результата.
- `Deque` предпочтительнее legacy `Stack`; `push/pop/peek` дают LIFO, `offer/poll` — FIFO.
- `List` допускает duplicates; `Set` определяет uniqueness через equality/order contract implementation.
- `Map.put` заменяет старое value для equal key и возвращает прежнее значение.

## Контраст

```java
Queue<String> q = new ArrayDeque<>();
q.poll();   // null
q.remove(); // NoSuchElementException
```

## Самостоятельный прогноз

```java
var map = new HashMap<String, Integer>();
System.out.println(map.put("A", 1));
System.out.println(map.put("A", 2));
System.out.println(map.get("A"));
```

Предскажите три строки до запуска.

## Executable proof

См. `CollectionProof.interfaceContracts()`.
