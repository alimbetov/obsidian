---
type: production-cases
route: JAVA-B05
status: published
cases: 10
java_versions: [17, 21]
---

# Java Collections and Generics Production Cases

## 1. Mutable JPA entity as HashMap key

**Failure:** id/hash fields change after persistence. Lookup becomes unstable.

**Policy:** use immutable business key/value object; avoid mutable entity keys.

## 2. Returning internal mutable list

**Failure:** caller changes service state without invariant checks.

**Policy:** return `List.copyOf` or domain-specific immutable projection.

## 3. Unmodifiable wrapper mistaken for snapshot

**Failure:** background mutation of source changes supposedly stable response.

**Policy:** distinguish view from copy explicitly in API contract.

## 4. Comparator inconsistent with business identity

**Failure:** `TreeSet` silently drops different orders sharing same timestamp/price.

**Policy:** add deterministic tie-breaker such as immutable id.

## 5. LinkedList selected for “fast insertion”

**Failure:** application still traverses O(n), allocates nodes and loses locality.

**Policy:** benchmark realistic workload; default to `ArrayList`/`ArrayDeque`.

## 6. Raw collection at integration boundary

**Failure:** unchecked data enters typed code and fails later with `ClassCastException`.

**Policy:** validate and convert once at boundary; eliminate raw types internally.

## 7. Cache key has weak hash distribution

**Failure:** collisions increase CPU and latency variance.

**Policy:** use stable composite value objects and tested equality/hash generation.

## 8. Queue API hides overload

**Failure:** unbounded queue grows until memory pressure.

**Policy:** choose bounded queue where backpressure matters; handle failed `offer` deliberately.

## 9. Generic repository API overuses wildcards

**Failure:** callers cannot express mutation safely and casts spread.

**Policy:** use exact type parameters for invariant read/write contracts; wildcards only for variance at boundaries.

## 10. Java 21 reversed view crosses thread boundary

**Failure:** caller assumes copied reverse snapshot while original collection continues changing.

**Policy:** document view semantics or materialize `List.copyOf(sequence.reversed())`.

## Review checklist

- dominant operation and Big-O justified;
- order/uniqueness contract explicit;
- equality stable;
- no mutable hash keys;
- comparator has tie-breakers;
- ownership and mutability documented;
- raw/unchecked warnings rejected;
- Java 17/21 API boundary tested.
