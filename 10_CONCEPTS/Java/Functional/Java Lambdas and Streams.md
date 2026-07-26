---
type: canonical-concept
domain: java
route: JAVA-B06
status: published
java_versions: [17, 21]
tags: [java, lambdas, streams, functional-programming]
---

# Java Lambdas and Streams

> [!summary]
> Lambda описывает поведение как значение, functional interface задаёт его тип, а Stream строит ленивый pipeline обработки данных без изменения источника.

## Mental model

```text
данные → source → intermediate operations → terminal operation → результат
                     lazy descriptions          запускает обход
```

## Route

1. [[10_CONCEPTS/Java/Functional/Java Functional Interfaces and Lambda Target Typing]]
2. [[10_CONCEPTS/Java/Functional/Java Lambda Capture and Method References]]
3. [[10_CONCEPTS/Java/Streams/Java Stream Pipeline Laziness and Single Use]]
4. [[10_CONCEPTS/Java/Streams/Java Stream Mapping Filtering and Flattening]]
5. [[10_CONCEPTS/Java/Streams/Java Stream Reduction and Collectors]]
6. [[10_CONCEPTS/Java/Streams/Java Primitive Streams Optional and Statistics]]
7. [[10_CONCEPTS/Java/Streams/Java Parallel Streams Safety and Performance]]

## Core invariants

- Lambda получает тип только из target context.
- Захваченная локальная переменная должна быть final или effectively final.
- Intermediate operations ленивы.
- Stream одноразовый.
- Reduction требует identity, accumulator и combiner с согласованной алгеброй.
- Side effects делают pipeline труднее проверять и опасны в parallel execution.
- Parallel stream не является автоматическим ускорением.

## Practice

- [[30_CERTIFICATIONS/Java/JAVA-B06/JAVA-B06 Cards]]
- [[30_CERTIFICATIONS/Java/JAVA-B06/JAVA-B06 Drills]]
- [[40_PRODUCTION_CASES/Java/Java Lambdas and Streams Production Cases]]
- [[50_LABS/Java/JAVA-B06/README]]
