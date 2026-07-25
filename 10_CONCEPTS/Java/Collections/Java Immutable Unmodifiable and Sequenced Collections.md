---
type: atomic-lesson
route: JAVA-B05
level: B-C
java_versions: [17, 21]
tags: [immutable, unmodifiable, sequenced-collections]
---

# Java Immutable, Unmodifiable and Sequenced Collections

## Простая ситуация

Метод должен вернуть список настроек так, чтобы caller не мог нарушить состояние сервиса.

## Mental model

```text
immutable snapshot  = отдельное неизменяемое значение
unmodifiable view   = окно без write API, но источник может измениться
```

## Минимальный код

```java
var source = new ArrayList<>(List.of("A", "B"));
var view = Collections.unmodifiableList(source);
var snapshot = List.copyOf(source);
source.add("C");
```

После изменения source:

- `view` видит `C`;
- `snapshot` не видит `C`;
- обе ссылки запрещают mutation через свой API.

`List.of`, `Set.of`, `Map.of` не принимают `null`; factory methods sets/maps также отвергают duplicates.

## Defensive copying

```java
final class Catalog {
    private final List<String> items;
    Catalog(Collection<String> source) {
        this.items = List.copyOf(source);
    }
    List<String> items() { return items; }
}
```

Это защищает container structure, но не делает mutable elements глубоко immutable.

## Java 21 Sequenced Collections

```java
SequencedCollection<String> values = new ArrayList<>();
values.addFirst("B");
values.addFirst("A");
values.addLast("C");
System.out.println(values.getFirst());
System.out.println(values.getLast());
System.out.println(values.reversed());
```

Interfaces:

- `SequencedCollection<E>` — defined encounter order;
- `SequencedSet<E>` — encounter order + uniqueness;
- `SequencedMap<K,V>` — ordered entries and first/last entry operations.

`reversed()` предоставляет reverse-ordered view, а не обязательную копию. Capability зависит от underlying collection: unmodifiable source даёт unmodifiable reversed view.

## Контраст Java 17 / Java 21

Java 17 требует implementation-specific first/last idioms. Java 21 унифицирует операции через sequenced interfaces. Код, использующий эти interfaces, не компилируется `--release 17`.

## Самостоятельный прогноз

Изменится ли `reversed()` view после `addFirst` в original collection? Ответ подтвердите JDK 21 proof.
