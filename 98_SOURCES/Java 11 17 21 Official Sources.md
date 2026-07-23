---
type: source-index
domain: java
subdomain: java-lts-evolution
status: active
verified_at: 2026-07-23
java_versions:
  - 11
  - 17
  - 21
tags:
  - java
  - java-11
  - java-17
  - java-21
  - sources
  - migration
---

# Java 11, 17 and 21 Official Sources

> [!summary]
> Primary-source index for the cumulative Java LTS knowledge program. Java 11 is the compatibility baseline, Java 17 is the `1Z0-829` certification baseline, and Java 21 is the modern production baseline. A feature must be marked as permanent, preview, incubator, deprecated or removed according to its exact release state.

# Release projects and complete JEP catalogs

## JDK 11

- https://openjdk.org/projects/jdk/11/
- https://docs.oracle.com/en/java/javase/11/
- https://docs.oracle.com/en/java/javase/11/migrate/

Use for:

```text
HTTP Client
single-file source launch
var in lambda parameters
Flight Recorder
TLS 1.3
Nest-Based Access Control
Epsilon GC
experimental ZGC
removed Java EE/CORBA modules
removed deployment stack
```

## JDK 17

- https://openjdk.org/projects/jdk/17/
- https://openjdk.org/projects/jdk/17/jeps-since-jdk-11
- https://docs.oracle.com/en/java/javase/17/
- https://docs.oracle.com/en/java/javase/17/migrate/

Use for the complete delta from JDK 11 to JDK 17 and for:

```text
switch expressions
text blocks
records
pattern matching for instanceof
sealed classes
helpful NullPointerException messages
strong encapsulation of JDK internals
enhanced pseudo-random number generators
context-specific deserialization filters
removed/deprecated components
```

## JDK 21

- https://openjdk.org/projects/jdk/21/
- https://openjdk.org/projects/jdk/21/jeps-since-jdk-17
- https://docs.oracle.com/en/java/javase/21/
- https://docs.oracle.com/en/java/javase/21/migrate/

Use for the complete delta from JDK 17 to JDK 21 and for:

```text
virtual threads
record patterns
pattern matching for switch
sequenced collections
generational ZGC
KEM API
UTF-8 default charset change inherited from JDK 18
Simple Web Server inherited from JDK 18
preview and incubator APIs present in JDK 21
migration and removed API guidance
```

# Language specifications

- Java 11 JLS: https://docs.oracle.com/javase/specs/jls/se11/html/index.html
- Java 17 JLS: https://docs.oracle.com/javase/specs/jls/se17/html/index.html
- Java 21 JLS: https://docs.oracle.com/javase/specs/jls/se21/html/index.html

Use the JLS for:

```text
compile or does-not-compile questions
scope and definite assignment
type conversions
method invocation and overload resolution
inheritance and overriding
generics and type inference
lambda expressions
switch and pattern semantics
records and sealed types
thread and memory-model rules
```

# JVM specifications

- Java 11 JVMS: https://docs.oracle.com/javase/specs/jvms/se11/html/index.html
- Java 17 JVMS: https://docs.oracle.com/javase/specs/jvms/se17/html/index.html
- Java 21 JVMS: https://docs.oracle.com/javase/specs/jvms/se21/html/index.html

Use for:

```text
class-file format
runtime data areas
class loading and initialization
bytecode execution
verification
linking
invokedynamic
monitor instructions
```

# API documentation

- Java 11 API: https://docs.oracle.com/en/java/javase/11/docs/api/index.html
- Java 17 API: https://docs.oracle.com/en/java/javase/17/docs/api/index.html
- Java 21 API: https://docs.oracle.com/en/java/javase/21/docs/api/index.html

API documentation is the source for exact library behavior. Do not infer an API contract from an implementation accident.

# Migration guides

## 8 → 11

Review:

```text
module system migration
JDK-internal API access
removed Java EE and CORBA modules
JAXB/JAX-WS dependencies
class-loader changes
JRE image changes
GC option changes
jdeps analysis
```

## 11 → 17

Review:

```text
strong encapsulation
illegal reflective access
removed Nashorn and Pack200
Security Manager deprecation
RMI Activation removal
serialization filtering
compiler and runtime compatibility
```

## 17 → 21

Review:

```text
UTF-8 default charset
virtual-thread adoption
ThreadLocal and pinning considerations
sequenced collection API changes
record/switch pattern migration
GC and observability changes
preview-feature lifecycle
```

# Feature-status policy

Every Java-version note and card must classify features as one of:

```text
permanent
preview
incubator
experimental VM feature
deprecated
deprecated-for-removal
removed
```

Rules:

1. A preview feature is not a permanent language guarantee.
2. Incubator modules require explicit module flags and can change incompatibly.
3. A feature present in JDK 21 is not automatically part of the Java 17 exam.
4. Removed JDK modules must be replaced with explicit dependencies where appropriate.
5. Migration behavior must be tested under the target runtime, not assumed from source compatibility.

# Related routes

- [[00_HOME/Java 11 17 21 Complete Knowledge Program]]
- [[10_CONCEPTS/Java/Versions/Java 11 17 21 LTS Evolution]]
- [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Roadmap]]
- [[30_CERTIFICATIONS/Java/1Z0-829/Java SE 17 99 Percent Master Roadmap]]
- [[01_MAPS/Java Map]]
