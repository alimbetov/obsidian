# JAVA-B05 Executable Proofs

## Coverage

- JDK 17 semantic proofs for List/Set/Map/Queue/Deque;
- equality/hash and mutable-key failure demonstration;
- Comparable/Comparator ordering;
- PECS and generic methods;
- immutable snapshot versus unmodifiable view;
- Java 21 SequencedCollection and SequencedMap;
- negative Java 17 version-boundary proof for Java 21 APIs.

## Run

```bash
bash 50_LABS/Java/JAVA-B05/run.sh
```

A JDK 21 runtime executes both lanes. On JDK 17, the base lane runs and the Java 21 source is required to fail under `--release 17`.

## Expected output

```text
JAVA-B05 JDK17 semantic proofs PASS
JAVA-B05 JDK17 negative version-boundary proof PASS
JAVA-B05 JDK21 sequenced proofs PASS
```

## Learning loop

Before running each method:

1. predict output or compilation result;
2. name the governing contract;
3. trace the internal mechanism;
4. run the proof;
5. repair the explanation if prediction differed.
