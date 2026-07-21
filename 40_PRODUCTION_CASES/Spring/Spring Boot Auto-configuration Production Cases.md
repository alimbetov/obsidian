---
type: production-cases
domain: spring
subdomain: spring-boot
status: published
case_count: 15
exam_baseline:
  - spring-boot-2.5
current_delta:
  - spring-boot-current
tags:
  - spring-boot
  - auto-configuration
  - incidents
---

# Spring Boot Auto-configuration Production Cases

## Route navigation

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B01/SPRING-BOOT-B01 Roadmap]]
- [[10_CONCEPTS/Spring/Boot/Spring Boot Bootstrap and Auto-configuration]]
- [[10_CONCEPTS/Spring/Boot/Spring Boot Auto-configuration Visual Deep Dive]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B01/SPRING-BOOT-B01 Cards]]
- [[50_LABS/Spring/SPRING-BOOT-B01/README]]
- [[98_SOURCES/Spring Boot Auto-configuration Sources]]

# Case 1 — Main class is below application packages

## Symptom

Controllers and services in sibling packages are missing.

## Evidence

```text
Application class: com.example.bootstrap.Application
Service class:     com.example.orders.OrderService
NoSuchBeanDefinitionException for OrderService
```

## Runtime path

`@ComponentScan` starts from the application class package and scans descendants. `com.example.orders` is not below `com.example.bootstrap`.

## Root cause

The primary application class was placed too deep in the package tree.

## Repair

Move it to `com.example.Application` or configure scan base packages explicitly.

## Proof

Inspect bean definitions and add a context test that asserts the service is present.

---

# Case 2 — Library auto-configuration works only when component-scanned

## Symptom

A custom starter works in one application but not another.

## Evidence

The working application accidentally scans `com.vendor.autoconfigure`; the failing one does not.

## Root cause

The library relied on consumer component scanning instead of Boot auto-configuration registration.

## Repair

For Boot 2.x, register the auto-configuration under the `EnableAutoConfiguration` key in `META-INF/spring.factories`. For current Boot, use `AutoConfiguration.imports` and mark the version boundary.

## Proof

Run an `ApplicationContextRunner` test without scanning the library package.

---

# Case 3 — Boot 3 registration copied into Boot 2.5 application

## Symptom

Custom auto-configuration is never considered.

## Evidence

The JAR contains only:

```text
META-INF/spring/
org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

The application uses Boot 2.5.

## Root cause

Registration metadata belongs to a newer Boot generation.

## Repair

Add Boot 2.x `META-INF/spring.factories` registration or publish version-specific artifacts.

## Proof

Condition report lists the candidate after correction.

---

# Case 4 — Auto-configuration discovered but excluded

## Symptom

Required `DataSource` is absent despite JDBC dependencies and properties.

## Evidence

Condition report lists `DataSourceAutoConfiguration` under exclusions.

## Root cause

An annotation or `spring.autoconfigure.exclude` property disables it.

## Repair

Remove the stale exclusion or document the replacement configuration.

## Proof

Condition report no longer lists the exclusion; the expected definition appears.

---

# Case 5 — Property name is correct in YAML but environment override disables feature

## Symptom

`acme.enabled: true` is present in `application.yml`, but the bean is absent in production.

## Evidence

Container environment contains `ACME_ENABLED=false`.

## Runtime path

The prepared `Environment` resolves a higher-priority source before `@ConditionalOnProperty` is evaluated.

## Repair

Remove/fix the deployment override and add startup logging for resolved non-sensitive feature flags.

## Proof

Actuator environment/config diagnostics or a startup assertion shows the resolved value.

---

# Case 6 — `matchIfMissing` silently enables an optional integration

## Symptom

A client bean is created even though no enabling property is configured.

## Root cause

`@ConditionalOnProperty(matchIfMissing = true)` treats absence as a match.

## Repair

Use explicit opt-in for integrations that allocate resources or contact external systems.

## Proof

An `ApplicationContextRunner` case with no property must assert absence after the change.

---

# Case 7 — User bean causes expected default to back off

## Symptom

The application does not use the Boot default client.

## Evidence

Condition report shows `@ConditionalOnMissingBean` did not match. A test configuration or imported library already defines the same type.

## Root cause

Back-off is working as designed.

## Repair

Inspect the existing bean. Remove it if accidental; otherwise configure it deliberately.

## Proof

Assert one bean and its source/configuration owner.

---

# Case 8 — Matching configuration still fails during bean creation

## Symptom

Condition report shows positive matches, but startup fails.

## Evidence

Deepest cause is property binding validation or constructor failure.

## Runtime path

```text
conditions match
→ BeanDefinition registered
→ context refresh creates bean
→ constructor/binding fails
```

## Repair

Diagnose the creation failure rather than changing condition annotations.

## Proof

A runner test asserts the exact startup failure and action message.

---

# Case 9 — `@ConditionalOnClass` references an optional type too early

## Symptom

Application throws `NoClassDefFoundError` instead of cleanly backing off.

## Root cause

An optional class is referenced in a way that causes JVM loading before condition metadata can protect the configuration.

## Repair

Isolate the condition on a configuration class and avoid optional types in signatures that are loaded before matching.

## Proof

Use `FilteredClassLoader` and verify context starts without the optional feature.

---

# Case 10 — Auto-configuration order mistaken for bean creation order

## Symptom

`@AutoConfigureAfter` was added, but a bean still initializes before another expected side effect.

## Root cause

Auto-configuration order controls definition contribution, not arbitrary bean instantiation order.

## Repair

Express the real dependency through constructor injection or, for exceptional lifecycle coupling, `@DependsOn`.

## Proof

A context test verifies dependency graph and initialization trace.

---

# Case 11 — Starter imported, but no bean appears

## Symptom

A starter dependency is present, but expected infrastructure is absent.

## Root causes to distinguish

```text
starter does not contain/register auto-configuration
required optional dependency is absent
property condition is disabled
user bean caused back-off
auto-configuration was excluded
bean creation failed later
```

## Repair

Inspect dependency graph and condition report; do not assume starter name guarantees a bean.

## Proof

A minimal context test reproduces the same classpath.

---

# Case 12 — Dependency management expected to add a library

## Symptom

Code does not compile because a library class is absent even though Boot BOM manages its version.

## Root cause

Dependency management selects versions; it does not declare dependencies.

## Repair

Add the dependency or appropriate starter explicitly.

## Proof

Inspect the resolved dependency tree.

---

# Case 13 — Global lazy initialization hides broken configuration until traffic

## Symptom

Deployment reports ready, but the first request fails with a bean-creation exception.

## Root cause

Global lazy initialization deferred creation of an invalid bean.

## Repair

Fix configuration and explicitly warm critical paths. Do not use lazy mode as a correctness workaround.

## Proof

A startup/warm-up test requests critical beans before accepting traffic.

---

# Case 14 — Runner failure prevents readiness

## Symptom

Context refresh succeeds, then application exits or reports startup failure.

## Evidence

`ApplicationRunner` throws while loading reference data.

## Root cause

Runners execute after refresh but remain part of startup success.

## Repair

Make the task idempotent, bounded and failure-aware; move non-critical work behind readiness when appropriate.

## Proof

Test successful and failing runner outcomes and observe ready/failed events.

---

# Case 15 — Debug report read at the wrong layer

## Symptom

Engineer sees a positive match and concludes Boot is correct, despite no usable bean.

## Root cause

The report proves condition matching, not complete instantiation or successful external connectivity.

## Repair

Trace four stages separately:

```text
candidate discovery
condition evaluation
definition registration
bean creation/readiness
```

## Proof

Combine condition report, bean-definition inspection, startup stack trace and a focused runner test.

# Production diagnostic checklist

```text
1 Boot version
2 exact dependency graph
3 primary application class/package
4 application type
5 candidate registration metadata
6 exclusions
7 resolved properties
8 condition report
9 existing beans causing back-off
10 deepest bean-creation cause
11 runner/startup event failures
12 minimal ApplicationContextRunner reproduction
```
