---
type: production-cases
domain: java
subdomain: java-versions
route: JAVA-LTS-B01
status: published
case_count: 10
java_versions:
  - 11
  - 17
  - 21
tags:
  - java
  - migration
  - production
---

# Java 11, 17 and 21 Migration Cases

## Route navigation

- [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Roadmap]]
- [[10_CONCEPTS/Java/Versions/Java 11 17 21 LTS Evolution]]
- [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Cards]]
- [[50_LABS/Java/JAVA-LTS-B01/README]]
- [[98_SOURCES/Java 11 17 21 Official Sources]]

# Case 1 — JAXB disappears after Java 8 → 11

## Symptom

Compilation or startup fails with missing `javax.xml.bind` classes.

## Root cause

JAXB was no longer bundled after the Java EE/CORBA modules were removed from the JDK.

## Repair

Declare an explicit JAXB API and implementation compatible with the application's namespace and framework generation.

## Proof

Compile with `--release 11`, run XML integration tests and inspect the runtime dependency graph.

---

# Case 2 — Library fails on Java 17 because of deep reflection

## Symptom

Startup fails with an inaccessible-object or illegal-access exception.

## Root cause

The library reaches into a strongly encapsulated JDK package.

## Repair

Upgrade or replace the library. Use `--add-opens` only as a temporary migration bridge with an owner and removal date.

## Proof

Run without module-opening flags and verify all reflective paths under integration tests.

---

# Case 3 — Newer compiler leaks a Java 21 API into a Java 17 artifact

## Symptom

The project compiles on the developer machine but fails at runtime on Java 17 with `NoSuchMethodError` or missing API behavior.

## Root cause

The build used a Java 21 compiler with `-source/-target` but without `--release 17`.

## Repair

Use toolchains and `javac --release 17`; run tests on an actual Java 17 runtime.

## Proof

Inspect class-file target and compile the same source in the Java 17 CI lane.

---

# Case 4 — UTF-8 default changes file behavior after 17 → 21

## Symptom

Text files, CSV exports or tests behave differently after migration.

## Root cause

The application relied on the platform default charset; Java 21 inherits the UTF-8 default introduced in JDK 18.

## Repair

Specify the charset explicitly at persistent and network boundaries.

## Proof

Run tests with non-ASCII data and compare explicit UTF-8 reads/writes across JDK 17 and 21.

---

# Case 5 — Virtual threads overload the database

## Symptom

Thread creation scales, but database timeouts and latency increase sharply.

## Root cause

Cheap virtual threads allowed far more concurrent blocking calls than the database pool and server could support.

## Repair

Preserve connection-pool limits, add admission control and size concurrency from downstream capacity.

## Proof

Measure queueing, pool saturation, database sessions, latency percentiles and rejection behavior.

---

# Case 6 — ThreadLocal context leaks during virtual-thread adoption

## Symptom

Security, tracing or tenant context becomes inconsistent after changing execution architecture.

## Root cause

The application assumed a specific platform-thread pool lifecycle or copied ThreadLocal state manually.

## Repair

Define explicit context propagation, clean up ThreadLocal values and evaluate scoped-value alternatives only with correct feature-status handling.

## Proof

Run concurrent isolation tests with unique request/tenant identifiers.

---

# Case 7 — Preview feature used as a permanent API contract

## Symptom

Code written for one preview release fails to compile or behaves differently on a later JDK.

## Root cause

The team treated preview syntax/API as stable.

## Repair

Isolate preview experiments, record the exact JDK release and flags, and avoid exposing preview types in long-lived public interfaces.

## Proof

Compile with the exact preview release and maintain a migration test for the next target JDK.

---

# Case 8 — Java 17 certification answer contaminated by Java 21 knowledge

## Symptom

A learner selects an option using virtual threads, record patterns or sequenced collection methods in a Java 17 question.

## Root cause

The knowledge base lacks a strict exam-baseline boundary.

## Repair

Mark every card with minimum/maximum applicable Java version and include a version-trap explanation.

## Proof

Use mixed version drills where the first step is identifying the target release.

---

# Case 9 — Multi-release JAR ships inconsistent implementations

## Symptom

Behavior differs between Java 11, 17 and 21 even though the artifact version is identical.

## Root cause

Version-specific classes under `META-INF/versions` diverged semantically or were not covered by all runtime lanes.

## Repair

Keep one behavioral contract, minimize version-specific code and test the final packaged JAR on every supported JDK.

## Proof

Inspect the JAR and run the same black-box contract tests in the 11/17/21 matrix.

---

# Case 10 — Migration is declared complete because compilation passed

## Symptom

Production later exposes TLS, locale, GC, agent or performance regressions.

## Root cause

The migration gate measured only source compatibility.

## Repair

Require evidence for source, binary, behavioral, tooling and operational compatibility.

## Proof

Maintain a migration report containing integration tests, dependency scans, security tests, JFR/GC evidence and rollback criteria.

# Migration evidence checklist

```text
[ ] target JDK compile with --release
[ ] actual target-runtime execution
[ ] dependency and jdeps review
[ ] removed/deprecated API review
[ ] reflection/module-access review
[ ] charset/locale/time-zone tests
[ ] TLS/security tests
[ ] GC/JFR/performance comparison
[ ] framework/plugin/agent compatibility
[ ] container/runtime image validation
[ ] rollback plan
```
