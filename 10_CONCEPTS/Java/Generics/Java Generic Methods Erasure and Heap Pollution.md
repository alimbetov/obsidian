---
type: atomic-lesson
route: JAVA-B05
level: B-C
java_versions: [17, 21]
tags: [generic-methods, raw-types, erasure, heap-pollution]
---

# Java Generic Methods, Erasure and Heap Pollution

## Простая ситуация

Нужен reusable метод, который возвращает первый элемент списка независимо от element type.

## Generic method

```java
static <T> T first(List<T> values) {
    if (values.isEmpty()) throw new NoSuchElementException();
    return values.get(0);
}
```

`<T>` объявляется перед return type. Type inference обычно выводит `T` из arguments и target type.

## Erasure mental model

Generics проверяются compiler, но большинство type arguments не существует как отдельные runtime classes.

```text
List<String> и List<Integer>
→ один runtime raw class List
→ compiler вставляет casts и bridge methods там, где требуется
```

Поэтому запрещено:

```java
// new T();
// new List<String>[10];
// if (x instanceof List<String>)
```

## Raw types

```java
List raw = new ArrayList<String>();
raw.add(42); // unchecked path
```

Raw type отключает часть generic type safety ради legacy compatibility. Warning должен рассматриваться как defect boundary, а не косметика.

## Heap pollution

Heap pollution возникает, когда переменная parameterized type ссылается на объект, несовместимый с её declared parameterization.

```java
static void corrupt(List<String> strings) {
    List raw = strings;
    raw.add(42);
}
```

Ошибка часто проявляется позже как `ClassCastException` при чтении.

## Generic varargs

Varargs создаёт array, а arrays reified и covariant, generics erased и invariant. Комбинация может быть unsafe.

`@SafeVarargs` допустим только когда implementation действительно не записывает несовместимые значения и не раскрывает unsafe array reference.

## Самостоятельный прогноз

Почему `List<?>[]` разрешён, а `List<String>[]` запрещён?

## Executable proof

Negative bank содержит raw assignment, generic array creation, illegal `instanceof`, unsafe wildcard write и varargs pollution cases.
