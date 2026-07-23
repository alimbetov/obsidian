---
type: assessment
domain: java
subdomain: java-versions
route: JAVA-LTS-B01
status: published
pre_test_questions: 10
post_test_questions: 15
java_versions:
  - 11
  - 17
  - 21
tags:
  - java
  - assessment
  - java-11
  - java-17
  - java-21
---

# JAVA-LTS-B01 Assessment

> [!summary]
> The pre-test identifies version confusion without changing confidence. The post-test checks release boundaries, migration reasoning and runtime evidence after study.

# Pre-test — 10 questions

Do not open notes and do not update card confidence.

1. Which version is the exact baseline for `1Z0-829`?
2. Are records available in Java 11, 17 or 21?
3. Are record patterns available in Java 17?
4. Which release standardized `java.net.http.HttpClient`?
5. What is the main difference between `--release 17` and `-source 17 -target 17`?
6. Name two modules/APIs commonly missing after an 8→11 migration.
7. What strong-encapsulation problem commonly appears during 11→17 migration?
8. Do virtual threads remove database connection-pool limits?
9. Does an LTS release contain only permanent features?
10. Name five compatibility dimensions that must be verified during migration.

# Pre-test interpretation

```text
0–3 correct   version model missing
4–6 correct   partial recognition, high contamination risk
7–8 correct   good baseline, review migration boundaries
9–10 correct  ready for detailed route and drills
```

# Post-test — 15 questions

## Version identification

1. Classify each as Java 11, Java 17 or Java 21 baseline: HTTP Client, records, virtual threads, sequenced collections, text blocks.
2. Explain why records in Java 17 do not imply record patterns in Java 17.
3. Explain why pattern switch answers differ between Java 17 preview history and Java 21 permanent semantics.
4. Classify structured concurrency, scoped values and the Vector API by their Java 21 feature status.

## Migration reasoning

5. A Java 8 service fails on Java 11 with missing JAXB classes. State root cause, repair and proof.
6. A Java 11 library fails on Java 17 with an inaccessible-object exception. State root cause and long-term repair.
7. A build uses JDK 21, targets Java 17 and calls a Java 21 API. Explain how `--release 17` prevents this.
8. A text-processing test changes after 17→21. Which default-behavior change should be investigated?
9. A virtual-thread migration increases database latency. Explain why and propose a capacity control.
10. Explain why running old bytecode on a new JDK before recompiling is diagnostically useful.

## Exact boundaries

11. Can Java 11 compile a record declaration? Explain.
12. Can Java 17 call `List.getFirst()`? Explain.
13. Can Java 21 run Java 11 class files? Which compatibility dimension does that primarily test?
14. Why is `--add-opens` considered a migration bridge rather than a final design?
15. What evidence is required before declaring a production LTS migration complete?

# Post-test gate

```text
[ ] at least 13/15 correct
[ ] no confusion about Java 17 exam versus Java 21 production
[ ] preview/incubator/permanent classification correct
[ ] all three migration paths can be reconstructed
[ ] virtual-thread resource limits explained
[ ] --release behavior explained precisely
```

# Error taxonomy

```text
wrong-version
wrong-feature-status
wrong-migration-dimension
wrong-api-minimum-version
wrong-runtime-assumption
wrong-preview-assumption
wrong-capacity-model
correct-guessed
```

# Related material

- [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Roadmap]]
- [[10_CONCEPTS/Java/Versions/Java 11 17 21 LTS Evolution]]
- [[10_CONCEPTS/Java/Versions/Java 11 17 21 Visual Deep Dive]]
- [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Cards]]
- [[50_LABS/Java/JAVA-LTS-B01/README]]
