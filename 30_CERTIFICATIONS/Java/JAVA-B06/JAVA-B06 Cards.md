---
type: certification-batch
domain: java
route: JAVA-B06
batch: JAVA-B06
status: published
card_count: 48
java_versions: [17, 21]
objectives:
  - JAVA-B06
  - JAVA21-6.1
tags: [java, lambdas, streams, active-recall]
---

# JAVA-B06 — Lambdas and Streams Cards

## Navigation

- [[30_CERTIFICATIONS/Java/JAVA-B06/JAVA-B06 Roadmap]]
- [[10_CONCEPTS/Java/Functional/Java Lambdas and Streams]]
- [[30_CERTIFICATIONS/Java/JAVA-B06/JAVA-B06 Drills]]
- [[50_LABS/Java/JAVA-B06/README]]

## JAVA-B06-C001 — What makes an interface functional?

### Russian Translation

Что делает interface functional?

> [!answer]- Answer
> Exactly one abstract method after Object-method rules; default and static methods do not count.

### Explanation

The single abstract method is the SAM implemented by a lambda.

### Exam Trap

The annotation validates the contract but does not create it.

## JAVA-B06-C002 — Why use FunctionalInterface?

### Russian Translation

Зачем `@FunctionalInterface`?

> [!answer]- Answer
> It asks the compiler to enforce the SAM contract.

### Explanation

A compatible interface remains functional without the annotation.

### Exam Trap

The annotation is optional; the SAM rule is not.

## JAVA-B06-C003 — Why can a lambda not initialize var directly?

### Russian Translation

Почему `var x = a -> a + 1` не компилируется?

> [!answer]- Answer
> A lambda has no standalone type and needs a target functional-interface type.

### Explanation

Parameter and return types come from target context.

### Exam Trap

`var` cannot supply the missing target type.

## JAVA-B06-C004 — What is target typing?

### Russian Translation

Что такое target typing?

> [!answer]- Answer
> Inferring lambda compatibility from the expected functional interface.

### Explanation

The same syntax may fit different interfaces in different contexts.

### Exam Trap

Overloads can make the target ambiguous.

## JAVA-B06-C005 — What is effectively final?

### Russian Translation

Что значит effectively final?

> [!answer]- Answer
> A local variable assigned once even when not declared final.

### Explanation

Such a variable may be captured by a lambda.

### Exam Trap

Any later reassignment breaks effective finality.

## JAVA-B06-C006 — Why can a captured local not be reassigned?

### Russian Translation

Почему captured local нельзя переназначать?

> [!answer]- Answer
> Lambda capture uses the local value or reference, not a mutable stack slot.

### Explanation

Java enforces stable local capture semantics.

### Exam Trap

Referenced object state may still mutate.

## JAVA-B06-C007 — Can a lambda mutate captured object state?

### Russian Translation

Можно ли менять состояние captured object?

> [!answer]- Answer
> Yes, if the local reference remains effectively final.

### Explanation

This is legal but can introduce side effects and races.

### Exam Trap

Legal capture does not imply thread safety.

## JAVA-B06-C008 — What are the four method-reference forms?

### Russian Translation

Какие четыре формы method reference?

> [!answer]- Answer
> Static, bound instance, unbound instance, and constructor reference.

### Explanation

Each form must match the target SAM parameter list.

### Exam Trap

A reference is deferred behavior, not an immediate call.

## JAVA-B06-C009 — What kind of reference is String trim?

### Russian Translation

Какая форма у `String::trim`?

> [!answer]- Answer
> Unbound instance method reference.

### Explanation

The receiver becomes the first SAM argument.

### Exam Trap

It is not a zero-argument Supplier.

## JAVA-B06-C010 — What kind of reference is value trim?

### Russian Translation

Какая форма у `value::trim`?

> [!answer]- Answer
> Bound instance method reference.

### Explanation

The receiver is fixed, so the SAM supplies only method arguments.

### Exam Trap

Bound and unbound forms can have different arity.

## JAVA-B06-C011 — What are the core java util function interfaces?

### Russian Translation

Какие основные interfaces в `java.util.function`?

> [!answer]- Answer
> Predicate, Function, Consumer, Supplier, UnaryOperator, BinaryOperator and variants.

### Explanation

Choose by inputs, output and whether a value is returned.

### Exam Trap

Consumer returns void; Predicate returns boolean.

## JAVA-B06-C012 — Function versus UnaryOperator?

### Russian Translation

Чем Function отличается от UnaryOperator?

> [!answer]- Answer
> Function may map T to R; UnaryOperator maps T to T.

### Explanation

UnaryOperator is a specialization of Function.

### Exam Trap

Do not use it when the result type changes.

## JAVA-B06-C013 — What does a Stream store?

### Russian Translation

Что хранит Stream?

> [!answer]- Answer
> A lazy pipeline description and traversal source, not an element container.

### Explanation

Elements flow when a terminal operation starts traversal.

### Exam Trap

A Stream is not a reusable Collection.

## JAVA-B06-C014 — What starts a stream pipeline?

### Russian Translation

Что запускает stream pipeline?

> [!answer]- Answer
> A terminal operation.

### Explanation

Intermediate operations only extend the lazy plan.

### Exam Trap

Building a pipeline alone performs no traversal.

## JAVA-B06-C015 — Why may peek execute nothing?

### Russian Translation

Почему `peek` может ничего не выполнить?

> [!answer]- Answer
> Without a terminal operation traversal never starts; short-circuiting may skip elements.

### Explanation

`peek` follows pipeline demand.

### Exam Trap

Do not use it for required business effects.

## JAVA-B06-C016 — Can a Stream be reused?

### Russian Translation

Можно ли повторно использовать Stream?

> [!answer]- Answer
> No, it is consumed after a terminal operation.

### Explanation

A second use normally throws IllegalStateException.

### Exam Trap

Store data or a Supplier, not one stream instance.

## JAVA-B06-C017 — Does filter change element type?

### Russian Translation

Меняет ли `filter` тип элемента?

> [!answer]- Answer
> No; it keeps or discards elements of the same type.

### Explanation

Its predicate returns boolean.

### Exam Trap

Use `map` to transform type.

## JAVA-B06-C018 — What does map do to cardinality?

### Russian Translation

Что делает `map` с cardinality?

> [!answer]- Answer
> It normally maps one input element to one output element.

### Explanation

The output type may differ.

### Exam Trap

Use `flatMap` for zero-to-many expansion.

## JAVA-B06-C019 — Why use flatMap?

### Russian Translation

Для чего нужен `flatMap`?

> [!answer]- Answer
> To expand each input into zero or more elements and flatten one nesting level.

### Explanation

It avoids Stream of Stream when a flat stream is required.

### Exam Trap

`map(Collection::stream)` remains nested.

## JAVA-B06-C020 — What contract powers distinct?

### Russian Translation

От какого контракта зависит `distinct`?

> [!answer]- Answer
> Equality and hashing semantics, normally equals and hashCode.

### Explanation

Logical duplicates are removed according to equality.

### Exam Trap

Broken equality produces surprising uniqueness.

## JAVA-B06-C021 — Why is sorted stateful?

### Russian Translation

Почему `sorted` stateful?

> [!answer]- Answer
> Ordering generally requires observing multiple elements before emitting final order.

### Explanation

It may buffer the whole stream.

### Exam Trap

`sorted().limit(n)` can still be expensive.

## JAVA-B06-C022 — What is encounter order?

### Russian Translation

Что такое encounter order?

> [!answer]- Answer
> The defined order in which a source and pipeline encounter elements.

### Explanation

Some sources are ordered and operations may preserve it.

### Exam Trap

Parallel execution does not automatically discard it.

## JAVA-B06-C023 — Which terminal operations short-circuit?

### Russian Translation

Какие terminal operations short-circuit?

> [!answer]- Answer
> findFirst, findAny, anyMatch, allMatch and noneMatch.

### Explanation

They may finish without visiting every element.

### Exam Trap

Early stop depends on data and ordering.

## JAVA-B06-C024 — Which intermediate operations short-circuit?

### Russian Translation

Какие intermediate operations short-circuit?

> [!answer]- Answer
> Examples include limit and takeWhile.

### Explanation

They bound downstream demand.

### Exam Trap

Stateful upstream operations may still process many elements.

## JAVA-B06-C025 — What must identity satisfy?

### Russian Translation

Какое требование к identity?

> [!answer]- Answer
> It must be neutral for the accumulator and combiner.

### Explanation

Combining identity with a value must preserve the value.

### Exam Trap

A non-neutral identity may be applied per partition.

## JAVA-B06-C026 — Why must reduction be associative?

### Russian Translation

Почему reduction должна быть associative?

> [!answer]- Answer
> Parallel grouping may change while the result must remain stable.

### Explanation

Grouping parentheses must not change the result.

### Exam Trap

Subtraction is not associative.

## JAVA-B06-C027 — When use reduce versus collect?

### Russian Translation

Когда использовать `reduce`, а когда `collect`?

> [!answer]- Answer
> Reduce values; collect into a mutable result container.

### Explanation

Collectors use supplier, accumulator and combiner roles.

### Exam Trap

Do not mutate one external object inside reduce.

## JAVA-B06-C028 — Why is a combiner needed?

### Russian Translation

Зачем нужен combiner?

> [!answer]- Answer
> It merges partial results, especially in parallel execution.

### Explanation

Its contract must match the accumulator result model.

### Exam Trap

A combiner ignored in sequential tests may fail in parallel.

## JAVA-B06-C029 — What does groupingBy do?

### Russian Translation

Что делает `groupingBy`?

> [!answer]- Answer
> It classifies elements into a Map keyed by classifier result.

### Explanation

A downstream collector controls aggregation inside groups.

### Exam Trap

Default grouping may materialize large lists.

## JAVA-B06-C030 — What does partitioningBy do?

### Russian Translation

Что делает `partitioningBy`?

> [!answer]- Answer
> It divides elements into true and false groups using a predicate.

### Explanation

The result is keyed by Boolean.

### Exam Trap

It is specifically a two-way split.

## JAVA-B06-C031 — Why can toMap throw on duplicate keys?

### Russian Translation

Почему `toMap` может упасть на duplicate keys?

> [!answer]- Answer
> The two-argument collector has no merge policy for equal keys.

### Explanation

Provide an explicit merge function when duplicates are valid.

### Exam Trap

Duplicate-key policy is a business rule.

## JAVA-B06-C032 — What is a downstream collector?

### Russian Translation

Что такое downstream collector?

> [!answer]- Answer
> The collector used to aggregate values inside each group.

### Explanation

Examples include counting, mapping and summingInt.

### Exam Trap

Classifier and downstream collector solve different roles.

## JAVA-B06-C033 — Does Collectors toList guarantee implementation?

### Russian Translation

Гарантирует ли `Collectors.toList()` implementation?

> [!answer]- Answer
> No.

### Explanation

Its specification does not promise ArrayList or mutability.

### Exam Trap

Do not cast the result to a concrete type.

## JAVA-B06-C034 — Is Stream toList modifiable?

### Russian Translation

Можно ли менять результат `Stream.toList()`?

> [!answer]- Answer
> No; it returns an unmodifiable list.

### Explanation

Create a mutable copy when mutation is required.

### Exam Trap

Do not assume it behaves like Collectors.toList.

## JAVA-B06-C035 — What is the collector supplier role?

### Russian Translation

Какова роль supplier в collector?

> [!answer]- Answer
> It creates a fresh mutable result container.

### Explanation

Parallel partitions may receive separate containers.

### Exam Trap

Returning one shared container breaks isolation.

## JAVA-B06-C036 — Why is an external shared list unsafe?

### Russian Translation

Почему внешний shared list опасен?

> [!answer]- Answer
> It introduces side effects, races and non-repeatable behavior.

### Explanation

Use a proper collector or return transformed values.

### Exam Trap

Synchronized mutation can still hurt scalability.

## JAVA-B06-C037 — Why use IntStream?

### Russian Translation

Зачем нужен IntStream?

> [!answer]- Answer
> It avoids boxing and provides sum, range and statistics.

### Explanation

Primitive specializations are IntStream, LongStream and DoubleStream.

### Exam Trap

Use boxed only when object semantics are needed.

## JAVA-B06-C038 — What does range 1 4 produce?

### Russian Translation

Что создаёт `IntStream.range(1,4)`?

> [!answer]- Answer
> 1, 2, 3.

### Explanation

The upper bound is exclusive.

### Exam Trap

Use rangeClosed to include 4.

## JAVA-B06-C039 — What does rangeClosed 1 4 produce?

### Russian Translation

Что создаёт `IntStream.rangeClosed(1,4)`?

> [!answer]- Answer
> 1, 2, 3, 4.

### Explanation

Both bounds are included.

### Exam Trap

The count is four, not three.

## JAVA-B06-C040 — Why does max return OptionalInt?

### Russian Translation

Почему `max` возвращает OptionalInt?

> [!answer]- Answer
> An empty stream has no maximum value.

### Explanation

Optional models possible absence without a sentinel.

### Exam Trap

Do not call getAsInt without proving presence.

## JAVA-B06-C041 — What is empty IntStream sum?

### Russian Translation

Что возвращает сумма empty IntStream?

> [!answer]- Answer
> Zero.

### Explanation

Zero is the additive identity.

### Exam Trap

This differs from min, max and average.

## JAVA-B06-C042 — What is empty IntStream average?

### Russian Translation

Что возвращает average empty IntStream?

> [!answer]- Answer
> An empty OptionalDouble.

### Explanation

There is no average without elements.

### Exam Trap

`orElse(0.0)` is caller policy.

## JAVA-B06-C043 — Is parallel always faster?

### Russian Translation

Всегда ли parallel stream быстрее?

> [!answer]- Answer
> No; measure a suitable CPU-bound workload and account for overhead.

### Explanation

Splitting, scheduling, merging and coordination have costs.

### Exam Trap

Small pipelines often become slower.

## JAVA-B06-C044 — What is the main parallel-stream safety rule?

### Russian Translation

Главное правило безопасности parallel stream?

> [!answer]- Answer
> Avoid shared mutable state and interfering functions.

### Explanation

Prefer pure transformations and collectors.

### Exam Trap

Thread-safe mutation may still harm semantics or speed.

## JAVA-B06-C045 — Which pool runs parallel streams by default?

### Russian Translation

Какой pool обычно выполняет parallel stream?

> [!answer]- Answer
> ForkJoinPool common pool.

### Explanation

The pool may be shared by unrelated process work.

### Exam Trap

Common-pool contention is an operational concern.

## JAVA-B06-C046 — What is the cost of forEachOrdered?

### Russian Translation

Какова цена `forEachOrdered`?

> [!answer]- Answer
> Coordination to preserve encounter order can reduce parallel freedom.

### Explanation

Ordering requirements constrain execution.

### Exam Trap

Use unordered behavior only when semantics allow it.

## JAVA-B06-C047 — Why does ArrayList split better than LinkedList?

### Russian Translation

Почему ArrayList обычно лучше делится, чем LinkedList?

> [!answer]- Answer
> Indexed contiguous ranges are cheaper and more balanced to partition.

### Explanation

Linked traversal makes splitting less efficient.

### Exam Trap

Spliterator quality affects parallel speedup.

## JAVA-B06-C048 — When is parallelStream especially doubtful?

### Russian Translation

Когда parallelStream особенно сомнителен?

> [!answer]- Answer
> Small input, blocking I/O, shared state, ordered operations or common-pool contention.

### Explanation

Choose concurrency from workload properties and measurements.

### Exam Trap

It is not the default replacement for loops or virtual threads.
