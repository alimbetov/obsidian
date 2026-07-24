---
type: lab
lab: JAVA-B01
domain: java
status: lab-proven
runtime_status: jdk17-jdk21-ci-pass
verified_at: 2026-07-24
ci_run_id: 30065610629
java_versions:
  - 17
  - 21
objectives:
  - JAVA21-1.1
  - JAVA21-1.2
  - JAVA21-1.3
tags:
  - java
  - lab
  - javac
  - values
  - text-blocks
  - dst
---

# JAVA-B01 Lab — Values, Text and Date-Time

> [!summary]
> This lab converts the highest-risk B01 rules into deterministic executable assertions. It contains no dependency on the machine's default locale, zone or current clock. The same sources compile and pass on independent JDK 17 and JDK 21 GitHub Actions lanes.

# Source files

```text
src/main/java/lab/b01/ValuesProof.java
src/main/java/lab/b01/TextProof.java
src/main/java/lab/b01/DateTimeProof.java
```

# What is proved

## ValuesProof

```text
compound-assignment narrowing and overflow
char numeric promotion
integer division and remainder
NaN comparison
required Integer cache identity
unboxing null failure
radix parsing
Math exact-overflow detection
```

## TextProof

```text
compile-time versus runtime String concatenation
String immutability
split trailing-empty behavior
text-block incidental indentation
text-block final newline
StringBuilder aliasing and identity-based equals
```

## DateTimeProof

```text
month-end adjustment and path dependence
Period normalization
unsupported Duration on LocalDate
Instant epoch
same-instant zone conversion
Europe/Berlin 2026 DST gap
Europe/Berlin 2026 DST overlap
Period day versus Duration 24 hours
SMART versus STRICT parsing
next versus nextOrSame adjusters
```

# Compile with Java 17 API baseline

From the repository root:

```bash
rm -rf build/java-b01-17
mkdir -p build/java-b01-17
javac --release 17 \
  -d build/java-b01-17 \
  50_LABS/Java/JAVA-B01/src/main/java/lab/b01/*.java

java -cp build/java-b01-17 lab.b01.ValuesProof
java -cp build/java-b01-17 lab.b01.TextProof
java -cp build/java-b01-17 lab.b01.DateTimeProof
```

Expected:

```text
JAVA-B01 ValuesProof PASS
JAVA-B01 TextProof PASS
JAVA-B01 DateTimeProof PASS
```

# Compile with Java 21 API baseline

```bash
rm -rf build/java-b01-21
mkdir -p build/java-b01-21
javac --release 21 \
  -d build/java-b01-21 \
  50_LABS/Java/JAVA-B01/src/main/java/lab/b01/*.java

java -cp build/java-b01-21 lab.b01.ValuesProof
java -cp build/java-b01-21 lab.b01.TextProof
java -cp build/java-b01-21 lab.b01.DateTimeProof
```

Expected output is identical.

# Confirmed evidence

Dedicated GitHub Actions run `30065610629` executed the `JAVA-B01 Proof` workflow:

```text
JDK 17 compile with --release 17  PASS
JDK 17 ValuesProof                 PASS
JDK 17 TextProof                   PASS
JDK 17 DateTimeProof               PASS

JDK 21 compile with --release 21  PASS
JDK 21 ValuesProof                 PASS
JDK 21 TextProof                   PASS
JDK 21 DateTimeProof               PASS
```

Additional validation:

```text
OpenJDK 21.0.10 local compile/run  PASS
all 15 documented drill results    VERIFIED
objective traceability contract    PASS
card catalog recognition           75/75
```

The lab and objectives `JAVA21-1.1`, `JAVA21-1.2` and `JAVA21-1.3` are classified as `lab-proven`.

# Determinism contract

The lab explicitly uses:

```text
ZoneId.of("Europe/Berlin")
ZoneId.of("UTC")
fixed dates and instants
explicit formatter patterns
no Locale.getDefault()
no ZoneId.systemDefault()
no LocalDate.now()
no Instant.now()
```

# CI matrix

The repository workflow is:

```text
.github/workflows/java-b01-proof.yml
```

Its matrix contract is:

```yaml
strategy:
  matrix:
    java: [17, 21]
```

Each lane compiles with its own `--release` value and executes all three proof classes.

# Related

- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]]
- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills]]
- [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
