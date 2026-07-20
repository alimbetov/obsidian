---
type: lab
domain: spring
subdomain: advanced-core
status: active
java_versions:
  - 8
spring_versions:
  - 5.3.39
related:
  - "[[10_CONCEPTS/Spring/Core/Advanced Core Scopes FactoryBean and Context Hierarchy]]"
  - "[[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B06/CORE-B06 Cards]]"
  - "[[40_PRODUCTION_CASES/Spring/Advanced Core Production Cases]]"
tags:
  - spring
  - lab
  - scopes
  - factorybean
---

# CORE-B06 Lab — Advanced Core

> [!summary]
> Лаборатория показывает identity, runtime lookup и ownership: singleton/prototype, `ObjectProvider`, custom thread scope со scoped proxy, `FactoryBean`, lazy creation, constructor-cycle failure, parent/child contexts, `Resource` и `MessageSource`.

## Структура

```text
Core-B06/
├── pom.xml
├── README.md
└── src/main/
    ├── java/lab/AdvancedCoreLab.java
    └── resources/
        ├── core-b06.txt
        ├── messages.properties
        └── messages_ru.properties
```

## Запуск

```bash
cd 50_LABS/Spring/Core-B06
mvn clean compile exec:java
```

Требования:

- JDK 8 или новее;
- Maven;
- доступ к Maven Central при первом dependency resolution.

---

# Experiment 1. Prototype captured by singleton

Config defines:

```java
@Bean
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
PrototypeToken prototypeToken()
```

`DirectPrototypeConsumer` receives the token directly.

Expected shape:

```text
direct token #1 = 1
direct token #2 = 1
```

The same field is used twice because injection happened once during singleton creation.

## Provider lookup

`ProviderPrototypeConsumer` stores `ObjectProvider<PrototypeToken>`.

Expected shape:

```text
provider token #1 = 2
provider token #2 = 3
```

Every `getObject()` performs a new container lookup, and prototype scope creates a new target.

## Destruction ownership

Two provider-created tokens are closed explicitly. The direct token remains unclosed when the context closes.

Expected relation:

```text
prototype created > prototype explicitly closed
```

This demonstrates that prototype destruction callbacks are not automatically managed through context shutdown.

> [!warning]
> Exact numeric IDs are not the contract. Identity equality and explicit-close count are the contract.

---

# Experiment 2. Custom thread scope and scoped proxy

The lab registers:

```java
scopes.put("thread", new SimpleThreadScope());
```

The bean is declared as:

```java
@Bean
@Scope(value = "thread", proxyMode = ScopedProxyMode.INTERFACES)
ThreadState threadState()
```

The application receives a stable JDK proxy.

Expected behavior:

```text
main thread targets = main-target-1 / main-target-1
worker thread targets = scope-worker-target-2 / scope-worker-target-2
same target inside one thread = true
different target between threads = true
```

Mental model:

```text
same injected proxy
    ↓
lookup current thread scope
    ↓
main thread → target A
worker thread → target B
```

`SimpleThreadScope` is used only as a compact experiment. Its destruction-callback behavior is limited and it should not be adopted blindly as production request-context replacement.

---

# Experiment 3. FactoryBean product and factory identity

The configuration exposes:

```java
@Bean
DemoProductFactoryBean demoProduct()
```

Normal lookup:

```java
context.getBean("demoProduct")
```

returns `DemoProduct`.

Factory dereference:

```java
context.getBean("&demoProduct")
```

returns `DemoProductFactoryBean`.

Expected output shape:

```text
normal lookup type = ...DemoProduct
factory lookup type = ...DemoProductFactoryBean
singleton product identity = true
```

Questions:

1. What scope does the FactoryBean instance have?
2. What identity policy does `isSingleton()` describe?
3. Who would close an external resource owned by the product?

---

# Experiment 4. Lazy creation

`LazyProbe` is declared:

```java
@Bean
@Lazy
LazyProbe lazyProbe()
```

It is not injected into an eager bean.

Expected sequence:

```text
lazy created before lookup = 0
LazyProbe constructor
LazyProbe touch
lazy created after lookup = 1
```

Experiment:

Inject `LazyProbe` directly into an eager singleton and predict whether its constructor still waits until explicit `getBean()`.

Then add `@Lazy` to the injection point and compare.

---

# Experiment 5. Constructor circular dependency

Separate context defines:

```java
@Bean
CycleA cycleA(CycleB b)

@Bean
CycleB cycleB(CycleA a)
```

Expected result:

```text
expected startup failure = BeanCurrentlyInCreationException
```

The exact outer exception wrapper may differ. The invariant is that normal constructor creation has no first complete object.

Design exercise:

Refactor:

```text
CycleA ↔ CycleB
```

into:

```text
Coordinator → CycleA
Coordinator → CycleB
```

without using `@Lazy`.

---

# Experiment 6. Parent and child contexts

Parent owns:

```text
labelService = parent-label
parentOnly
```

Child owns:

```text
labelService = child-label
childOnly
```

Expected behavior:

```text
parent label = parent-label
child label = child-label
child sees parentOnly = parent-visible-from-child
parent cannot see childOnly = true
```

This proves:

- child local definition shadows parent for child lookup;
- child can fall back to parent;
- parent cannot discover child-local bean;
- contexts have separate shutdown ownership.

The lab closes child before parent.

---

# Experiment 7. Resource abstraction

The lab loads:

```java
Resource resource =
        context.getResource("classpath:core-b06.txt");
```

and reads through:

```java
resource.getInputStream()
```

Expected output:

```text
resource text = Resource loaded through getInputStream, not getFile.
```

Package the application and explain why `resource.getFile()` would be a weaker contract.

---

# Experiment 8. MessageSource

The context contains a bean named:

```text
messageSource
```

with basename `messages`.

Expected output:

```text
Hello, Ruslan!
Здравствуйте, Руслан!
```

The same message code is rendered using two locales.

Architecture exercise:

Design an API error with:

- stable code;
- localized message;
- structured details;
- correlation ID.

Do not use localized text as the machine-readable code.

---

# Additional experiments

## A. FactoryBean product identity

Change:

```java
isSingleton() → false
```

and modify `getObject()` to return a new product each time. Predict two normal lookups.

## B. Wrong scope expectation

Replace provider injection with direct prototype injection and explain why both `tokenId()` calls stay equal.

## C. No scoped proxy

Set:

```java
proxyMode = ScopedProxyMode.NO
```

Predict what happens when singleton `ThreadStateConsumer` is created.

## D. Parent without child override

Remove child `labelService`. Child lookup should now return parent implementation.

## E. Lazy failure phase

Make `LazyProbe` constructor throw. Compare startup behavior before and after direct injection into eager singleton.

## F. Resource as File

Replace stream access with `getFile()` and test both IDE classes directory and packaged archive.

---

# Review questions

1. Why does prototype injection into singleton not refresh automatically?
2. What exactly does a scoped proxy cache?
3. How does provider behavior depend on target scope?
4. Why are FactoryBean scope and product identity separate?
5. Why can lazy dependency still initialize eagerly?
6. Why is constructor cycle different from setter cycle?
7. What is an early reference missing?
8. Which direction does context visibility flow?
9. Why is child shadowing local rather than a parent mutation?
10. Why is `Resource` not equivalent to `File`?
11. Why must message code and localized text be separated?
12. Who owns cleanup for every object created in the lab?

---

# Validation status

Local source-shape validation:

```text
javac --release 8 — PASS
```

The check used minimal Spring API stubs to validate:

- Java 8 syntax;
- imported API shapes;
- generics and lambda compatibility;
- absence of accidental newer-language constructs.

Full Spring runtime execution was not performed in the current environment because Maven/external dependency resolution was unavailable. Run:

```bash
mvn clean compile exec:java
```

in a Maven-enabled environment before treating expected output as runtime-confirmed.

## Sources

- [[98_SOURCES/Spring Advanced Core Sources]]
