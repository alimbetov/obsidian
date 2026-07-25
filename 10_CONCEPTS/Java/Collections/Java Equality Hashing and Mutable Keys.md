---
type: atomic-lesson
route: JAVA-B05
level: B-C
java_versions: [17, 21]
tags: [equals, hashcode, mutable-key]
---

# Java Equality, Hashing and Mutable Keys

## Простая ситуация

`HashMap` хранит сотрудника по табельному номеру. После вставки номер изменили, и `get` перестал находить запись.

## Mental model

```text
hashCode → выбирает район поиска
>equals  → подтверждает точного соседа
```

## Минимальный код

```java
record UserId(long value) {}
Map<UserId, String> users = new HashMap<>();
users.put(new UserId(7), "Aida");
System.out.println(users.get(new UserId(7)));
```

## Точный контракт

- equal objects обязаны иметь одинаковый hash code;
- unequal objects могут иметь одинаковый hash code;
- `equals` должен быть reflexive, symmetric, transitive, consistent и возвращать `false` для `null`;
- поля, участвующие в equality/hash, должны оставаться стабильными, пока объект является hash key.

## Mutable key failure

```java
final class Key {
    int id;
    Key(int id) { this.id = id; }
    public boolean equals(Object o) {
        return o instanceof Key k && id == k.id;
    }
    public int hashCode() { return Integer.hashCode(id); }
}

var key = new Key(1);
var map = new HashMap<Key, String>();
map.put(key, "value");
key.id = 2;
System.out.println(map.get(key)); // commonly null
```

Entry физически остаётся в bucket, выбранном старым hash. Новый lookup идёт в другой bucket.

## Контраст

`IdentityHashMap` использует reference identity и имеет специальное назначение; это не исправление плохого `equals/hashCode`.

## Самостоятельный прогноз

Что произойдёт, если переопределить `equals`, но оставить inherited `hashCode`?

## Production rule

Используйте immutable value objects/records для keys. Не включайте изменяемые display fields в identity.
