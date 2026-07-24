---
type: lab
lab: JAVA-B02
domain: java
status: source-validated
runtime_status: local-jdk21-release17-release21-pass
java_versions:
  - 17
  - 21
objectives:
  - JAVA-B02
  - JAVA21-2.1
tags:
  - java
  - lab
  - javac
  - control-flow
  - switch
  - pattern-switch
---

# JAVA-B02 Lab — Control Flow and Pattern Switch

> [!summary]
> This lab proves shared Java 17/21 control-flow semantics, final Java 21 pattern-switch behavior, and expected compiler rejection for reachability, selector, yield, dominance, guard and exhaustiveness violations.

# Layout

```text
src/shared/java/lab/b02/ClassicFlowProof.java
src/java21/java/lab/b02/PatternSwitchProof.java

src/negative/shared/
src/negative/java17/
src/negative/java21/

run.sh
```

# Positive shared proof

`ClassicFlowProof` is compiled and executed in both lanes.

It asserts:

```text
dangling-else binding
for-loop continue/update order
labeled break across nested loops
enhanced-for reassignment
Java 17 instanceof pattern flow scope
classic switch fall-through
switch-expression yield
exhaustive enum expression
non-exhaustive legacy switch statement behavior
```

# Positive Java 21 proof

`PatternSwitchProof` is compiled only in the Java 21 lane.

It asserts:

```text
type-pattern dispatch
when guards
explicit case null
default for unmatched non-null values
sealed hierarchy exhaustiveness
Long reference selector
qualified enum constants
case null, default
```

# Negative compile bank

## Shared rejection cases

```text
ContinueToBlock              labeled continue targeting a non-loop
DuplicateCase                duplicate case value after folding
LongPrimitiveSwitch          unsupported primitive long selector
MissingYield                 value-producing block completes without yield
NonExhaustiveExpression      switch expression is not exhaustive
UnreachableWhile             constant-false while body is unreachable
```

## Java 17 version trap

```text
QualifiedEnumJava17          qualified enum constants rejected under --release 17
```

## Java 21 rejection cases

```text
ConstantGuard                when guard attached to a constant label
DominatedPattern             subtype pattern dominated by earlier supertype
DoubleMatchAll               unconditional Object pattern plus default
NonExhaustivePatternStatement enhanced pattern statement lacks coverage
```

# Run Java 17 lane

```bash
bash 50_LABS/Java/JAVA-B02/run.sh 17
```

Expected final line:

```text
JAVA-B02 JDK 17 proof PASS
```

# Run Java 21 lane

```bash
bash 50_LABS/Java/JAVA-B02/run.sh 21
```

Expected final lines include:

```text
JAVA-B02 PatternSwitchProof PASS
JAVA-B02 JDK 21 proof PASS
```

# Validation contract

The runner fails when:

```text
a positive source does not compile
a positive assertion fails
an expected-negative source compiles
a Java 21 source leaks into the Java 17 lane
```

All compilation uses the lane's explicit `--release` value.

# Current evidence

```text
OpenJDK 21.0.10 with --release 17  PASS
OpenJDK 21.0.10 with --release 21  PASS
shared negative sources              PASS as expected failures
Java 17 version trap                 PASS as expected failure
Java 21 negative sources             PASS as expected failures
independent GitHub JDK 17 lane       pending
independent GitHub JDK 21 lane       pending
```

The route becomes `lab-proven` after both GitHub Actions lanes complete successfully.

# Related

- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]
- [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]]
- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills]]
- [[98_SOURCES/Java SE 17 1Z0-829 Sources]]
- [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
