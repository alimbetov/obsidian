---
type: executable-lab
route: JAVA-B06
status: lab-proven
java_versions: [17, 21]
---
# JAVA-B06 Executable Proof

## Run

```bash
bash 50_LABS/Java/JAVA-B06/run.sh
```

The runner compiles with `--release 17`, therefore the same source is valid on Java 17 and Java 21. The workflow runs it on both JDKs.

## Positive evidence

- `LambdaStreamProof` verifies target typing, effectively-final capture, lazy short-circuit traversal, flatMap/filter/distinct/sorted, associative reduction, grouping collectors, primitive statistics and the consumed-stream boundary.
- `ParallelReductionProof` verifies associative sequential/parallel equivalence, concurrent grouping and safe collection without external mutation.

## Expected compile failures

1. lambda without target type;
2. capture of a non-effectively-final variable;
3. Predicate returning a non-boolean value;
4. reassignment after capture;
5. incompatible method reference;
6. natural sorting of a non-Comparable type.

A negative test passes only when `javac` rejects the source.

## Expected output

```text
JAVA-B06 semantic proof PASS
JAVA-B06 parallel proof PASS
JAVA-B06 expected compile-failure bank PASS
```

## Navigation

- [[30_CERTIFICATIONS/Java/JAVA-B06/JAVA-B06 Roadmap]]
- [[10_CONCEPTS/Java/Functional/Java Lambdas and Streams]]
- [[30_CERTIFICATIONS/Java/JAVA-B06/JAVA-B06 Drills]]
