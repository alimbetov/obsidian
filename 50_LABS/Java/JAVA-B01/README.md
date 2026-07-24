---
type: lab
lab: JAVA-B01
domain: java
status: source-validated
runtime_status: jdk21-local-pass
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
> This lab converts the highest-risk B01 rules into deterministic executable assertions. It contains no dependency on the machine's default locale, zone or current clock.

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

# Current evidence status

```text
OpenJDK 21.0.10 local compile/run  PASS
javac --release 17 source check    PASS
JDK 17 independent runtime job     pending
JDK 21 CI runtime job              pending
```

The route remains `source-and-drill-backed`, not `lab-proven`, until independent JDK 17 and JDK 21 CI jobs are attached.

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

# Recommended CI matrix

```yaml
strategy:
  matrix:
    java: [17, 21]
```

Each lane should compile with its own `--release` value and execute all three proof classes.

# Related

- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]]
- [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills]]
- [[98_SOURCES/Java SE 21 1Z0-830 Sources]]