---
type: lab
lab: JAVA-B03
domain: java
status: lab-proven
runtime_status: jdk17-and-jdk21-ci-pass
verified_at: 2026-07-24
ci_workflow: JAVA-B03 Proof
ci_run_id: 30097320887
java_versions:
  - 17
  - 21
objectives:
  - JAVA-B03
  - JAVA21-3.1
  - JAVA21-3.2
  - JAVA21-3.3
  - JAVA21-3.4
  - JAVA21-3.5
  - JAVA21-3.6
  - JAVA21-3.7
tags:
  - java
  - lab
  - javac
  - records
  - sealed-types
  - record-patterns
---

# JAVA-B03 Executable Proof

> [!summary]
> Dependency-free compile/runtime evidence for object initialization, overload and dispatch rules, records, enums, sealed types and Java 21 record patterns. Dedicated run `30097320887` passed on JDK 17 and JDK 21.

## Positive sources

```text
shared/java/lab/b03/InitializationProof.java
shared/java/lab/b03/ObjectModelProof.java
shared/java/lab/b03/RecordsSealedProof.java
java21/java/lab/b03/RecordPatternProof.java
```

## Negative bank

Shared failures verify ambiguous overloads, invalid overrides, static/instance conflicts, final overrides, record restrictions, sealed restrictions, functional-interface validation, private interface methods and enum constructor access.

Version failures verify:

```text
Java 17 rejects final record-pattern syntax
Java 21 rejects wrong record-pattern arity/type
dominance and non-exhaustive sealed pattern switches
```

## Local execution

```bash
bash 50_LABS/Java/JAVA-B03/run.sh 17
bash 50_LABS/Java/JAVA-B03/run.sh 21
```

## CI execution

`JAVA-B03 Proof` run `30097320887`:

```text
JDK 17 positive and expected-failure evidence  PASS
JDK 21 positive and expected-failure evidence  PASS
```

## Evidence contract

A lane passes only when every positive assertion succeeds and every intended negative source is rejected by `javac`.

## Navigation

- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills]]
- [[01_MAPS/Java Object Model and Record Patterns Map.canvas]]
- [[98_SOURCES/Java SE 17 1Z0-829 Sources]]
- [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
