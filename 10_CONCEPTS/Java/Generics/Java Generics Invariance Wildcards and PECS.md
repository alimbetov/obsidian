---
type: atomic-lesson
route: JAVA-B05
level: A-C
java_versions: [17, 21]
tags: [generics, invariance, wildcard, pecs]
---

# Java Generics, Invariance, Wildcards and PECS

## Простая ситуация

`Dog` является `Animal`, но `List<Dog>` не является `List<Animal>`.

## Mental model

Если бы такое присваивание было разрешено, через ссылку `List<Animal>` можно было бы добавить `Cat` в реальный `List<Dog>`.

## Минимальный код

```java
static double total(List<? extends Number> source) {
    double result = 0;
    for (Number n : source) result += n.doubleValue();
    return result;
}

static void addDefaults(List<? super Integer> target) {
    target.add(0);
    target.add(1);
}
```

## Пошаговая трассировка

- `List<? extends Number>` означает неизвестный конкретный subtype `CAP#1 extends Number`.
- Читать можно как `Number`.
- Добавлять `Integer` нельзя: фактическим типом может быть `List<Double>`.
- `List<? super Integer>` принимает `Integer`.
- Читать из неё безопасно только как `Object`.

## PECS

```text
Producer Extends
Consumer Super
```

PECS применяют к роли параметра в конкретном методе. Коллекция может одновременно читаться и изменяться; тогда часто нужен exact type parameter вместо wildcard.

## Контраст

```java
List<?> unknown = List.of("x");
Object value = unknown.get(0);
// unknown.add("y"); // compile error
unknown.add(null); // единственное universally safe значение
```

## Самостоятельный прогноз

Какие вызовы компилируются?

```java
List<Integer> ints = new ArrayList<>();
List<Number> nums = new ArrayList<>();
total(ints);
addDefaults(nums);
```

## Executable proof

Positive cases находятся в `CollectionProof`; illegal writes — в negative compile bank.
