---
type: certification-batch
domain: java
subdomain: java-versions
batch: JAVA-LTS-B01
status: published
card_count: 30
java_versions:
  - 11
  - 17
  - 21
tags:
  - java
  - java-11
  - java-17
  - java-21
  - active-recall
---

# JAVA-LTS-B01 — Java 11, 17 and 21 Cards

## Route navigation

- [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Roadmap]]
- [[10_CONCEPTS/Java/Versions/Java 11 17 21 LTS Evolution]]
- [[40_PRODUCTION_CASES/Java/Java 11 17 21 Migration Cases]]
- [[50_LABS/Java/JAVA-LTS-B01/README]]
- [[98_SOURCES/Java 11 17 21 Official Sources]]

---

## JAVA-LTS-B01-C001 — What role does Java 11 play in this knowledge program?

### Russian Translation

Какую роль Java 11 играет в этой программе знаний?

> [!answer]- Answer
> Java 11 is the enterprise compatibility and migration baseline, especially for systems moving from Java 8-era assumptions.

### Explanation

It combines a stable LTS runtime with the post-module-system packaging, reflection and removed-module realities that mature enterprise applications must handle.

### Exam Trap

Do not treat Java 11 merely as “Java 8 plus a few APIs”; the 8→11 migration boundary includes major platform and packaging changes.

---

## JAVA-LTS-B01-C002 — What role does Java 17 play?

### Russian Translation

Какую роль Java 17 играет в программе?

> [!answer]- Answer
> Java 17 is the `1Z0-829` certification baseline and the reference version for Java 17 language and API questions.

### Explanation

Exam answers must follow Java 17 JLS and API behavior even if Java 21 offers newer syntax or APIs.

### Exam Trap

Virtual threads and sequenced collections must not appear in a Java 17 exam answer unless the question explicitly compares versions.

---

## JAVA-LTS-B01-C003 — What role does Java 21 play?

### Russian Translation

Какую роль Java 21 играет в программе?

> [!answer]- Answer
> Java 21 is the modern production LTS baseline used for current language, concurrency and runtime decisions.

### Explanation

It includes permanent features such as virtual threads, record patterns, pattern switch and sequenced collections, plus some preview/incubator features that require separate classification.

### Exam Trap

“Present in JDK 21” does not always mean “permanent Java 21 feature.”

---

## JAVA-LTS-B01-C004 — What is a preview feature?

### Russian Translation

Что такое preview feature?

> [!answer]- Answer
> A preview feature is a fully specified but non-permanent language or VM feature that requires preview flags and may change or disappear.

### Explanation

Both compilation and execution require preview enablement for preview bytecode.

### Exam Trap

Preview is not the same as incubator: preview usually concerns language/VM features, while incubator commonly concerns APIs/modules.

---

## JAVA-LTS-B01-C005 — What is an incubator API?

### Russian Translation

Что такое incubator API?

> [!answer]- Answer
> An incubator API is a non-final API delivered in an incubator module so developers can test it before standardization.

### Explanation

Its package/module surface may change incompatibly between releases.

### Exam Trap

Do not build a long-term compatibility promise around an incubator API.

---

## JAVA-LTS-B01-C006 — Which release standardized the HTTP Client?

### Russian Translation

В какой версии был стандартизирован HTTP Client?

> [!answer]- Answer
> Java 11 standardized `java.net.http.HttpClient`.

### Explanation

It supports synchronous and asynchronous requests and WebSocket client APIs.

### Exam Trap

`sendAsync` returns a `CompletableFuture`; it does not imply virtual-thread execution.

---

## JAVA-LTS-B01-C007 — Which Java 11 removals often break Java 8 applications?

### Russian Translation

Какие удаления Java 11 часто ломают приложения Java 8?

> [!answer]- Answer
> Removed Java EE/CORBA modules, including bundled JAXB and JAX-WS, and the removed deployment stack are common breakpoints.

### Explanation

Applications must add explicit dependencies or replace the removed technologies.

### Exam Trap

A `ClassNotFoundException` after migration may be a removed-module issue, not an application classpath typo.

---

## JAVA-LTS-B01-C008 — What does `javac --release 11` guarantee?

### Russian Translation

Что гарантирует `javac --release 11`?

> [!answer]- Answer
> It constrains the language level, target class-file version and documented API surface to Java 11.

### Explanation

This prevents accidental compilation against newer APIs when using a newer compiler.

### Exam Trap

`-source 11 -target 11` alone does not fully prevent newer API leakage.

---

## JAVA-LTS-B01-C009 — Are switch expressions available in Java 11?

### Russian Translation

Доступны ли switch expressions в Java 11?

> [!answer]- Answer
> No. They became permanent before Java 17 and are part of the Java 17 baseline.

### Explanation

Classic switch statements remain available in all three releases.

### Exam Trap

Do not confuse classic switch with expression form using `yield` and a produced value.

---

## JAVA-LTS-B01-C010 — Are text blocks available in Java 17?

### Russian Translation

Доступны ли text blocks в Java 17?

> [!answer]- Answer
> Yes. Text blocks are a permanent language feature by Java 17.

### Explanation

They simplify multiline string literals while preserving defined indentation and escape rules.

### Exam Trap

A text block is still a `String`; it is not a template or automatic JSON object.

---

## JAVA-LTS-B01-C011 — Which release baseline includes records?

### Russian Translation

Какая версия включает records как постоянную возможность?

> [!answer]- Answer
> Records are permanent by Java 17.

### Explanation

A record provides a restricted data-carrier class form with generated state access, constructor, equality, hash code and string representation behavior.

### Exam Trap

A record is not automatically deeply immutable; referenced mutable objects can still change.

---

## JAVA-LTS-B01-C012 — Which release baseline includes sealed classes?

### Russian Translation

Какая версия включает sealed classes?

> [!answer]- Answer
> Sealed classes and interfaces are permanent in Java 17.

### Explanation

They restrict permitted direct subtypes and enable more controlled hierarchies.

### Exam Trap

Each permitted subtype must follow the sealed hierarchy rules by being final, sealed or non-sealed where required.

---

## JAVA-LTS-B01-C013 — What changed around JDK internals by Java 17?

### Russian Translation

Что изменилось с внутренними API JDK к Java 17?

> [!answer]- Answer
> Strong encapsulation makes unsupported reflective access to JDK internals substantially more restricted.

### Explanation

Libraries relying on deep reflection often require upgrades or explicit temporary module-opening flags.

### Exam Trap

`--add-opens` is a migration bridge, not a clean permanent module design.

---

## JAVA-LTS-B01-C014 — Which release provides permanent pattern matching for `instanceof`?

### Russian Translation

Какая версия предоставляет постоянный pattern matching для `instanceof`?

> [!answer]- Answer
> It is permanent by Java 17.

### Explanation

The pattern variable exists only where the compiler can prove the match succeeded.

### Exam Trap

Pattern-variable scope follows flow analysis, not simply the enclosing block.

---

## JAVA-LTS-B01-C015 — Which release provides permanent record patterns?

### Russian Translation

Какая версия предоставляет постоянные record patterns?

> [!answer]- Answer
> Java 21.

### Explanation

Record patterns destructure record components within pattern-matching constructs.

### Exam Trap

Records exist in Java 17, but record patterns do not.

---

## JAVA-LTS-B01-C016 — Which release provides permanent pattern matching for switch?

### Russian Translation

Какая версия предоставляет постоянный pattern matching для switch?

> [!answer]- Answer
> Java 21.

### Explanation

Java 17 included a preview stage, while Java 21 provides the permanent form.

### Exam Trap

Do not answer a Java 17 question using the final Java 21 switch-pattern rules.

---

## JAVA-LTS-B01-C017 — Which release provides virtual threads as a permanent feature?

### Russian Translation

В какой версии virtual threads стали постоянной возможностью?

> [!answer]- Answer
> Java 21.

### Explanation

Virtual threads support a thread-per-task model for large numbers of blocking operations without allocating one platform thread per task.

### Exam Trap

Virtual threads do not remove database-pool, socket, rate-limit or memory constraints.

---

## JAVA-LTS-B01-C018 — Should virtual threads be pooled?

### Russian Translation

Нужно ли создавать пул virtual threads?

> [!answer]- Answer
> Generally no. They are designed to be created per task; scarce downstream resources should be limited separately.

### Explanation

Pooling virtual threads recreates platform-thread scarcity without solving resource governance.

### Exam Trap

Use semaphores, connection pools or rate limiters for scarce resources rather than limiting the number of virtual thread objects.

---

## JAVA-LTS-B01-C019 — Which release introduces sequenced collections?

### Russian Translation

Какая версия вводит sequenced collections?

> [!answer]- Answer
> Java 21.

### Explanation

Sequenced collection interfaces provide uniform first, last and reversed encounter-order operations.

### Exam Trap

Methods such as `getFirst()` on `List` are not available when targeting Java 17.

---

## JAVA-LTS-B01-C020 — What charset behavior changed between Java 17 and 21?

### Russian Translation

Какое поведение charset изменилось между Java 17 и 21?

> [!answer]- Answer
> Starting with JDK 18, UTF-8 became the default charset, so Java 21 inherits that behavior.

### Explanation

Applications that relied on operating-system default encodings can change behavior during 17→21 migration.

### Exam Trap

Explicit charset use remains the safest choice for persistent or network data.

---

## JAVA-LTS-B01-C021 — What is source compatibility?

### Russian Translation

Что такое source compatibility?

> [!answer]- Answer
> It asks whether source code compiles under the target compiler and language rules.

### Explanation

It is only one migration dimension.

### Exam Trap

Successful compilation does not prove binary, behavioral, tooling or operational compatibility.

---

## JAVA-LTS-B01-C022 — What is binary compatibility?

### Russian Translation

Что такое binary compatibility?

> [!answer]- Answer
> It asks whether already compiled classes can link and run against the target platform and dependencies.

### Explanation

A source rebuild may hide binary incompatibilities that existing deployed artifacts would encounter.

### Exam Trap

Do not equate binary compatibility with identical runtime behavior.

---

## JAVA-LTS-B01-C023 — Why run an application on the new JDK before recompiling?

### Russian Translation

Зачем сначала запускать приложение на новом JDK без перекомпиляции?

> [!answer]- Answer
> It separates runtime/binary compatibility problems from source/compiler migration problems.

### Explanation

This staged approach makes failures easier to classify.

### Exam Trap

A successful startup still requires functional, security and performance verification.

---

## JAVA-LTS-B01-C024 — What does `jdeps` help detect?

### Russian Translation

Что помогает обнаружить `jdeps`?

> [!answer]- Answer
> Static dependencies on modules, packages and JDK-internal APIs.

### Explanation

It is valuable during modularization and LTS migration.

### Exam Trap

Reflection and dynamically constructed class names may escape static dependency analysis.

---

## JAVA-LTS-B01-C025 — What is a multi-release JAR?

### Russian Translation

Что такое multi-release JAR?

> [!answer]- Answer
> A JAR that supplies version-specific class implementations under `META-INF/versions/<n>` while retaining a lower baseline.

### Explanation

The runtime selects the appropriate versioned entry for the current JDK.

### Exam Trap

It increases packaging and testing complexity and should not be used without a real compatibility requirement.

---

## JAVA-LTS-B01-C026 — What is the key 11→17 reflection risk?

### Russian Translation

Каков ключевой риск reflection при миграции 11→17?

> [!answer]- Answer
> Libraries using unsupported deep reflection into JDK internals can fail because of stronger encapsulation.

### Explanation

Upgrade the library or redesign access before relying on temporary module flags.

### Exam Trap

The failure can appear at runtime even if application source compiles.

---

## JAVA-LTS-B01-C027 — What is the key 17→21 concurrency risk?

### Russian Translation

Каков ключевой concurrency-риск миграции 17→21?

> [!answer]- Answer
> Treating virtual threads as unlimited capacity and overwhelming downstream resources.

### Explanation

Thread creation becomes cheap, but database sessions, sockets, remote quotas and memory remain bounded.

### Exam Trap

Higher concurrency without backpressure can reduce reliability and increase latency.

---

## JAVA-LTS-B01-C028 — Does LTS status make every feature permanent?

### Russian Translation

Делает ли LTS-статус все функции версии постоянными?

> [!answer]- Answer
> No. An LTS release can contain preview, incubator or experimental features.

### Explanation

Feature status is defined per JEP/API, not by the release support label.

### Exam Trap

Always check the exact feature status for the named JDK release.

---

## JAVA-LTS-B01-C029 — What is the safest rule for Java 17 exam answers?

### Russian Translation

Какое главное правило для ответов на экзамене Java 17?

> [!answer]- Answer
> Use Java 17 JLS and API behavior and explicitly exclude Java 21-only syntax and APIs.

### Explanation

A developer may know newer Java, but the certification question is version-bound.

### Exam Trap

Do not assume the newest behavior is accepted because it is backward compatible at runtime.

---

## JAVA-LTS-B01-C030 — What evidence proves an LTS migration?

### Russian Translation

Какие доказательства подтверждают миграцию между LTS-версиями?

> [!answer]- Answer
> Successful compile/run tests on the target JDK plus functional, integration, security, performance and operational evidence.

### Explanation

Migration correctness spans source, binary, behavioral, tooling and operational compatibility.

### Exam Trap

“Build is green” alone is not a complete migration proof.
