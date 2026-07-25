---
type: production-cases
route: JAVA-B06
status: published
production_cases: 10
---
# Java Lambdas and Streams Production Cases

## 1. Duplicate-key failure in toMap
Customer records share an external identifier. `toMap` without merge function fails. Define business precedence explicitly or reject duplicates before collection.

## 2. N+1 hidden inside map
A stream `map(repository::findById)` performs one database call per element. Replace with batch query; stream syntax does not remove I/O complexity.

## 3. Side effects in peek
Audit publishing in `peek` disappears when terminal operation changes or short-circuits. Keep `peek` diagnostic; perform required effects explicitly.

## 4. Parallel mutation corruption
`parallelStream().forEach(arrayList::add)` races. Return values and collect them rather than sharing a mutable target.

## 5. Wrong monetary reduction
Floating-point `double` and non-neutral identity distort totals. Use `BigDecimal`, neutral zero, associative addition and defined rounding boundaries.

## 6. Large sorted pipeline
`sorted().limit(10)` may sort the full dataset. Push top-N work to the database or use bounded selection when appropriate.

## 7. Nulls in mapping
`map(User::email).map(String::trim)` fails for null email. Model absence, filter null at boundary, or enforce invariant before pipeline.

## 8. Accidental repeated traversal
A service stores a Stream field and consumers invoke it twice. Store immutable data or a `Supplier<Stream<T>>`, not a consumed stream instance.

## 9. Blocking I/O in parallel stream
HTTP calls occupy common-pool workers and interfere with unrelated tasks. Use bounded executor, virtual threads, async client or reactive composition.

## 10. Collector memory pressure
`groupingBy` materializes every element in lists. For large datasets aggregate counts/sums downstream, paginate, stream to storage or move aggregation to DB.

## Review questions
For each case identify: source size, statefulness, side effects, ordering need, failure mode, observability and measurable alternative.
