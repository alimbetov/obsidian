---
type: production-cases
domain: spring
subdomain: spring-boot-configuration
status: published
case_count: 12
objectives:
  - SPRING-1.3.1
  - SPRING-1.3.2
  - SPRING-6.2.1
exam_baseline:
  - spring-boot-2.5.15
tags:
  - spring-boot
  - externalized-configuration
  - production-incidents
---

# Spring Boot Configuration Production Cases

## Route navigation

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Roadmap]]
- [[10_CONCEPTS/Spring/Boot/Spring Boot Externalized Configuration and Type-safe Binding]]
- [[10_CONCEPTS/Spring/Boot/Spring Boot Configuration Visual Deep Dive]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Cards]]
- [[50_LABS/Spring/SPRING-BOOT-B02/README]]
- [[98_SOURCES/Spring Boot Externalized Configuration Sources]]

# Case 1 — Stale environment variable overrides YAML

## Symptom

HTTP timeout is 30 seconds although `application.yml` contains `3s`.

## Evidence

```text
application.yml          client.timeout=3s
container environment   CLIENT_TIMEOUT=30s
resolved properties      client.timeout=30s
```

## Runtime path

Environment-variable property source has higher precedence than packaged Config Data.

## Root cause

An obsolete deployment override survived a previous release.

## Repair

Remove the override, document the canonical key and add a deployment-level assertion for the resolved non-secret value.

## Proof

An `ApplicationContextRunner` or deployment smoke test asserts `Duration.ofSeconds(3)` and records the winning source.

---

# Case 2 — `spring.config.location` removes packaged defaults

## Symptom

Several default properties disappear after adding a custom external directory.

## Evidence

Launch uses:

```text
--spring.config.location=file:/etc/myapp/
```

Packaged `application.properties` is no longer loaded.

## Root cause

`spring.config.location` replaced the default search locations.

## Repair

Use `spring.config.additional-location` when packaged defaults must remain, or explicitly list all required locations.

## Proof

A startup test compares the final Environment with replace and extend modes.

---

# Case 3 — Required secret was marked optional

## Symptom

Application starts with an empty/default credential and fails only on the first external request.

## Evidence

```properties
spring.config.import=optional:configtree:/run/secrets/acme/
```

The directory is absent.

## Root cause

`optional:` converted a deployment contract into silent fallback behavior.

## Repair

Make the import mandatory and validate required credential properties at startup.

## Proof

A missing-secret integration test must fail context startup with a clear action message.

---

# Case 4 — Profile file exists but is never active

## Symptom

Production database URL remains the packaged default.

## Evidence

`application-prod.properties` exists, but active profiles contain only `production`; no profile group maps it to `prod`.

## Root cause

Filename presence was mistaken for profile activation.

## Repair

Activate the correct profile or define a documented profile group.

## Proof

Startup diagnostics and a context test assert the final active profiles and bound URL.

---

# Case 5 — Recursive/invalid profile activation arrangement

## Symptom

Profile configuration is ignored or startup reports invalid profile placement.

## Evidence

A profile-activated document also attempts to redefine `spring.profiles.active`.

## Root cause

Activation metadata was placed in a document whose activation already depends on profile resolution.

## Repair

Move global active-profile selection to an allowed external source and keep profile documents declarative.

## Proof

Config Data tests cover the intended activation graph without recursive dependence.

---

# Case 6 — `@ConfigurationProperties` type is never registered

## Symptom

No binding or validation occurs; injection fails with missing bean or code instantiates defaults manually.

## Evidence

The class has `@ConfigurationProperties("client")` but no scan, enable annotation or bean declaration.

## Root cause

Binding metadata was confused with bean registration.

## Repair

Use `@EnableConfigurationProperties(ClientProperties.class)` or `@ConfigurationPropertiesScan`.

## Proof

A context test asserts a single bound properties bean.

---

# Case 7 — Unitless duration uses the wrong assumption

## Symptom

`client.timeout=5000` produces a duration different from the operator's expectation.

## Root cause

The configuration relied on an implicit default unit.

## Repair

Use explicit units such as `5000ms` or `5s` and document the canonical form.

## Proof

Binding tests assert exact `Duration` values for supported input forms.

---

# Case 8 — Validation annotation exists but validation never runs

## Symptom

A null endpoint reaches a client constructor despite `@NotNull` on the property.

## Evidence

The properties class lacks the validation trigger or the validation provider is absent.

## Root cause

Constraint metadata alone did not activate binding validation.

## Repair

Register the properties bean, add `@Validated` and include the appropriate validation dependency for the Boot baseline.

## Proof

A context test with missing endpoint must fail before the client bean is created.

---

# Case 9 — List override removes packaged entries

## Symptom

Only one server remains after a deployment override, while operators expected the external item to be appended.

## Evidence

Packaged list has two elements; a higher-priority source defines index zero.

## Root cause

Collection binding behavior was assumed to merge like a map or scalar override.

## Repair

Define the complete intended list in the winning source or model additive configuration explicitly.

## Proof

A multi-source binding test asserts the final collection.

---

# Case 10 — Misspelled property silently has no effect

## Symptom

`client.timout=2s` is present, but the default timeout remains active.

## Evidence

The unknown key exists in Environment but no binding target consumes it.

## Root cause

No metadata-assisted authoring, strict binding or objective-level startup test caught the typo.

## Repair

Publish configuration metadata, use canonical names and add strict/startup assertions for critical namespaces.

## Proof

The corrected key changes the bound value; the typo is detected by tooling or test policy.

---

# Case 11 — Secret exposed by diagnostics

## Symptom

A database password appears in support logs.

## Evidence

A properties object's generated `toString()` includes all fields, or a management endpoint is exposed without sufficient sanitization/security.

## Root cause

Secret delivery was secured, but secret use and observability were not.

## Repair

Exclude secret fields from string rendering, protect management endpoints, configure sanitization and restrict file/process access.

## Proof

Security tests verify endpoint authorization and log scans verify secret values are absent.

---

# Case 12 — Test override fragments context cache

## Symptom

The Spring test suite becomes much slower after many classes add unique inline properties.

## Evidence

Context-cache metrics show many distinct merged configurations.

## Root cause

Each property variation created a different cache key.

## Repair

Consolidate shared test profiles/configuration, use focused `ApplicationContextRunner` tests for property matrices, and avoid unnecessary full-context variants.

## Proof

Context startup count and suite duration fall while assertions remain equivalent.

# Diagnostic checklist

```text
1 canonical property key
2 all source forms and locations
3 winning PropertySource and origin
4 active/default profiles and document activation
5 Config Data imports and optionality
6 registration of binding target
7 relaxed-name match
8 conversion/unit handling
9 validation outcome
10 secret exposure and test-cache side effects
```
