---
type: certification-batch
domain: java
route: JAVA-B05
batch: JAVA-B05
status: published
card_count: 48
java_versions: [17, 21]
objectives:
  - JAVA-B05
  - JAVA21-5.1
tags: [java, collections, generics, sequenced-collections, active-recall]
---

# JAVA-B05 — Collections, Generics and Sequenced Collections Cards

## Navigation

- [[30_CERTIFICATIONS/Java/JAVA-B05/JAVA-B05 Roadmap]]
- [[10_CONCEPTS/Java/Collections/Java Collections Generics and Sequenced Collections]]
- [[30_CERTIFICATIONS/Java/JAVA-B05/JAVA-B05 Drills]]
- [[50_LABS/Java/JAVA-B05/README]]

## JAVA-B05-C001 — Why is an array fixed-size?

> [!answer]- Answer
> Its length is fixed when the array object is created and cannot change.

## JAVA-B05-C002 — What does List add over an array mental model?

> [!answer]- Answer
> A dynamic logical size and the Collection API.

## JAVA-B05-C003 — What is the defining property of Set?

> [!answer]- Answer
> At most one element may occupy a given logical equality position.

## JAVA-B05-C004 — What is the defining property of Map?

> [!answer]- Answer
> One equal key maps to at most one current value.

## JAVA-B05-C005 — Which abstraction models FIFO?

> [!answer]- Answer
> Queue, commonly implemented with ArrayDeque.

## JAVA-B05-C006 — Which abstraction models LIFO without legacy Stack?

> [!answer]- Answer
> Deque with push, pop and peek.

## JAVA-B05-C007 — What does poll return on an empty queue?

> [!answer]- Answer
> null.

## JAVA-B05-C008 — What does remove do on an empty queue?

> [!answer]- Answer
> It throws NoSuchElementException.

## JAVA-B05-C009 — What does Map.put return?

> [!answer]- Answer
> The previous value associated with the key, or null.

## JAVA-B05-C010 — Why does Map not extend Collection?

> [!answer]- Answer
> Its key-value entry model is not a single-element collection contract.

## JAVA-B05-C011 — Does List permit duplicates?

> [!answer]- Answer
> Yes.

## JAVA-B05-C012 — Can Set.add return false?

> [!answer]- Answer
> Yes, when an equal element is already present.

## JAVA-B05-C013 — What is the average complexity of ArrayList.get?

> [!answer]- Answer
> O(1).

## JAVA-B05-C014 — What is the complexity of inserting into the middle of ArrayList?

> [!answer]- Answer
> O(n) because later elements must shift.

## JAVA-B05-C015 — What is the average complexity of HashMap.get?

> [!answer]- Answer
> O(1), assuming an effective hash distribution.

## JAVA-B05-C016 — What is the complexity of TreeMap.get?

> [!answer]- Answer
> O(log n).

## JAVA-B05-C017 — Why is LinkedList.get(i) O(n)?

> [!answer]- Answer
> Nodes must be traversed to reach the requested position.

## JAVA-B05-C018 — Why is Big-O alone insufficient?

> [!answer]- Answer
> It omits allocation cost, locality, constants and hash or comparator cost.

## JAVA-B05-C019 — What is the default queue or deque choice?

> [!answer]- Answer
> ArrayDeque.

## JAVA-B05-C020 — When is LinkedHashMap appropriate?

> [!answer]- Answer
> When hash lookup and predictable encounter or access order are both required.

## JAVA-B05-C021 — What hash rule must equal objects satisfy?

> [!answer]- Answer
> Equal objects must produce equal hash codes.

## JAVA-B05-C022 — May unequal objects have the same hash code?

> [!answer]- Answer
> Yes. Collisions are permitted.

## JAVA-B05-C023 — Why is mutating a hash key dangerous?

> [!answer]- Answer
> A later lookup may search a different bucket from the one containing the entry.

## JAVA-B05-C024 — What is a robust key type?

> [!answer]- Answer
> An immutable value object or record with stable equality and hashing.

## JAVA-B05-C025 — Does IdentityHashMap repair a broken equality contract?

> [!answer]- Answer
> No. It deliberately uses reference identity instead of logical equality.

## JAVA-B05-C026 — When does HashMap use equals?

> [!answer]- Answer
> After hash and bucket narrowing identifies candidate entries.

## JAVA-B05-C027 — What does Comparable define?

> [!answer]- Answer
> The natural order of a type.

## JAVA-B05-C028 — What does Comparator define?

> [!answer]- Answer
> An external ordering strategy.

## JAVA-B05-C029 — Why is a - b unsafe in a comparator?

> [!answer]- Answer
> Integer overflow can violate the comparator contract.

## JAVA-B05-C030 — What does compare result zero mean in TreeSet?

> [!answer]- Answer
> The elements occupy the same sorted position, so the second is not added.

## JAVA-B05-C031 — How do you avoid element collapse in a price comparator?

> [!answer]- Answer
> Add a deterministic tie-breaker such as id.

## JAVA-B05-C032 — How do you define null ordering?

> [!answer]- Answer
> Use Comparator.nullsFirst or Comparator.nullsLast.

## JAVA-B05-C033 — Is List<Integer> a subtype of List<Number>?

> [!answer]- Answer
> No. Generic types are invariant.

## JAVA-B05-C034 — Why is generic invariance safe?

> [!answer]- Answer
> It prevents inserting an incompatible subtype through a widened reference.

## JAVA-B05-C035 — What can be safely read from ? extends Number?

> [!answer]- Answer
> Number.

## JAVA-B05-C036 — Can Integer be added to List<? extends Number>?

> [!answer]- Answer
> No, because the exact captured subtype is unknown.

## JAVA-B05-C037 — What can be added to List<? super Integer>?

> [!answer]- Answer
> Integer values and values of its subtypes.

## JAVA-B05-C038 — What can be safely read from ? super Integer?

> [!answer]- Answer
> Object.

## JAVA-B05-C039 — What does PECS mean?

> [!answer]- Answer
> Producer Extends, Consumer Super.

## JAVA-B05-C040 — What may be added to List<?>?

> [!answer]- Answer
> Only null is universally safe.

## JAVA-B05-C041 — Where is a generic method type parameter declared?

> [!answer]- Answer
> Before the return type.

## JAVA-B05-C042 — Is List<String> a distinct runtime class?

> [!answer]- Answer
> No. Type arguments are erased.

## JAVA-B05-C043 — Why is new T() forbidden?

> [!answer]- Answer
> Erasure removes the runtime constructor type information required to instantiate T.

## JAVA-B05-C044 — Why are raw types dangerous?

> [!answer]- Answer
> They disable generic checks and introduce unchecked compatibility paths.

## JAVA-B05-C045 — What is heap pollution?

> [!answer]- Answer
> A parameterized reference points to data incompatible with its declared type argument.

## JAVA-B05-C046 — Why are generic arrays forbidden?

> [!answer]- Answer
> Arrays are reified and covariant, while generics are erased and invariant.

## JAVA-B05-C047 — Unmodifiable view versus immutable snapshot?

> [!answer]- Answer
> A view may reflect source changes; a snapshot is independent of later source mutation.

## JAVA-B05-C048 — What does SequencedCollection.reversed return?

> [!answer]- Answer
> A reverse-ordered view that preserves first and last semantics.
