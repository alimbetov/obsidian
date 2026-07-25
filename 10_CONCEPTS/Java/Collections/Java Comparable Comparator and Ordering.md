---
type: atomic-lesson
route: JAVA-B05
level: B-C
java_versions: [17, 21]
tags: [comparable, comparator, ordering]
---

# Java Comparable, Comparator and Ordering

## Простая ситуация

Товары нужно сортировать иногда по SKU, иногда по цене, иногда по рейтингу.

## Mental model

```text
Comparable = natural order, встроенный в тип
Comparator = внешний сценарий сортировки
```

## Минимальный код

```java
record Product(String sku, int price) implements Comparable<Product> {
    public int compareTo(Product other) {
        return sku.compareTo(other.sku);
    }
}

Comparator<Product> byPrice =
        Comparator.comparingInt(Product::price)
                  .thenComparing(Product::sku);
```

## Точный механизм

`TreeSet` и `TreeMap` используют результат comparison для определения logical uniqueness ключа. Если `compare(a,b) == 0`, sorted structure рассматривает элементы как одну позицию, даже когда `a.equals(b)` возвращает `false`.

Comparator contract требует sign symmetry, transitivity и consistency. Нельзя писать `return a.price() - b.price()` из-за overflow; используйте `Integer.compare` или comparing factory.

## Null policy

```java
Comparator<String> safe = Comparator.nullsLast(String::compareTo);
```

Natural-order structures обычно не принимают `null`, если comparison не может его обработать.

## Контраст

```java
Comparator<Product> byPriceOnly = Comparator.comparingInt(Product::price);
```

Два товара с одинаковой ценой могут схлопнуться в `TreeSet`. Добавьте stable tie-breaker, если business identity различается.

## Самостоятельный прогноз

Сколько элементов останется в `TreeSet`, если comparator сравнивает только длину строк и добавить `"aa"`, `"bb"`, `"c"`?

## Executable proof

См. `CollectionProof.ordering()`.
