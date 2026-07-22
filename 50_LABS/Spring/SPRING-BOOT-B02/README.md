---
type: lab
domain: spring
subdomain: spring-boot-configuration
status: active
spring_boot_version: 2.5.15
java_version: 8
certification:
  - spring-2V0-72.22
objectives:
  - SPRING-1.3.1
  - SPRING-1.3.2
  - SPRING-6.2.1
tags:
  - spring-boot
  - configuration-properties
  - config-data
  - lab
---

# SPRING-BOOT-B02 Lab — Config Data and Type-safe Binding

## Purpose

Prove the configuration pipeline rather than memorizing annotation names:

```text
property sources
→ resolved values
→ relaxed binding
→ nested/list/map binding
→ conversion
→ validation
→ immutable constructor binding
→ profile-specific Config Data
→ spring.config.import
```

# Baseline

```text
Spring Boot 2.5.15
Java 8 source/bytecode target
JUnit Jupiter
ApplicationContextRunner
SpringApplication Config Data test
```

# Run

```bash
cd 50_LABS/Spring/SPRING-BOOT-B02
mvn clean test
```

# Test inventory

| Test | Mechanism proved |
|---|---|
| `bindsRelaxedNamesAndTypedUnits` | relaxed names, `Duration`, `DataSize`, nested conversion |
| `bindsNestedCollectionsAndMaps` | indexed lists and map keys |
| `validatesMissingRequiredEndpoint` | startup failure for missing required property |
| `validatesNestedRetryConstraint` | nested Bean Validation |
| `bindsBoot25ConstructorProperties` | Boot 2.5 `@ConstructorBinding` |
| `programmaticBinderUsesThePreparedEnvironment` | explicit Binder over Environment |
| `configDataLoadsImportAndProfileSpecificOverride` | Config Data import, active profile and profile override |

# Source structure

```text
SPRING-BOOT-B02/
├── pom.xml
└── src
    ├── main/java/lab/config
    │   ├── ClientProperties.java
    │   ├── DeliveryProperties.java
    │   ├── ConfigurationBindingConfig.java
    │   └── ConfigurationSnapshot.java
    └── test
        ├── java/lab/config/ConfigurationBindingTest.java
        └── resources
            ├── application-b02.properties
            ├── application-b02-prod.properties
            └── b02-imported.properties
```

# Experiment 1 — Relaxed binding and typed units

Input:

```text
client.timeout=750ms
client.max-payload=4MB
client.retry.max_attempts=5
client.retry.backoff=2s
```

Prediction:

```text
Duration.ofMillis(750)
DataSize.ofMegabytes(4)
maxAttempts = 5
Duration.ofSeconds(2)
```

The underscore form is deliberately used for one nested property to exercise relaxed binding.

# Experiment 2 — Nested collections and maps

Input:

```text
client.servers[0]=https://a.example.test
client.servers[1]=https://b.example.test
client.headers[X-Tenant]=retail
```

Prediction:

```text
List<URI> contains two ordered values
Map contains X-Tenant=retail
```

# Experiment 3 — Missing required endpoint

`ClientProperties.endpoint` is `@NotNull` and the class is `@Validated`.

Prediction:

```text
binding target is registered
binding completes with null endpoint
validation rejects the object
context startup fails
client snapshot bean is never usable
```

# Experiment 4 — Nested validation

Input:

```text
client.retry.max-attempts=0
```

Prediction: `@Min(1)` on the nested object fails because the nested property is marked `@Valid`.

# Experiment 5 — Boot 2.5 constructor binding

`DeliveryProperties` uses:

```java
@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "delivery")
```

Prediction: final fields are initialized from external values, and invalid constructor parameters fail binding/validation.

# Experiment 6 — Programmatic Binder

```java
Binder.get(environment)
      .bind("client", Bindable.of(ClientProperties.class))
```

This proves that Binder consumes the same prepared Environment and conversion rules. Declarative bean registration remains the normal application choice.

# Experiment 7 — Profile-specific Config Data and import

Base resource:

```properties
spring.config.import=classpath:b02-imported.properties
client.endpoint=https://base.example.test
client.timeout=3s
```

Profile resource:

```properties
client.endpoint=https://prod.example.test
client.timeout=1s
```

Launch:

```text
--spring.config.name=application-b02
--spring.profiles.active=prod
```

Prediction:

```text
prod endpoint wins
prod timeout wins
imported header is present
active profile list contains prod
```

# Controlled modifications

## A. Remove `@EnableConfigurationProperties`

Prediction: property types are not registered; injection of `ConfigurationSnapshot` dependencies fails.

## B. Remove `@Validated`

Prediction: missing/invalid values can bind without constraint enforcement until later code fails.

## C. Change `application-b02-prod.properties` to an inactive profile

Prediction: base endpoint and timeout remain.

## D. Remove imported resource but keep a mandatory import

Prediction: Config Data processing fails before ordinary bean creation.

## E. Add `optional:` to the import

Prediction: missing import no longer fails, but any required values supplied only there must still be validated.

## F. Change `750ms` to `abc`

Prediction: property is found, but conversion fails during binding.

# Evidence worksheet

| Experiment | Winning source/document | Bound target | Conversion | Validation | Context result |
|---|---|---|---|---|---|
| Relaxed units | runner property source | ClientProperties | | | |
| Nested collection | runner property source | ClientProperties | | | |
| Missing endpoint | no endpoint | ClientProperties | | | |
| Invalid retry | runner property source | Retry | | | |
| Constructor binding | runner property source | DeliveryProperties | | | |
| Profile/import | base + prod + import | ClientProperties | | | |

# Runtime status

```text
Source review       completed
GitHub Actions      configured after route registration
Runtime PASS        must be taken from the workflow, not assumed
```

# Route navigation

- **Roadmap:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Roadmap]]
- **Canonical:** [[10_CONCEPTS/Spring/Boot/Spring Boot Externalized Configuration and Type-safe Binding]]
- **Visual:** [[10_CONCEPTS/Spring/Boot/Spring Boot Configuration Visual Deep Dive]]
- **Cards:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Cards]]
- **Assessment:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Assessment]]
- **Cases:** [[40_PRODUCTION_CASES/Spring/Spring Boot Configuration Production Cases]]
- **Sources:** [[98_SOURCES/Spring Boot Externalized Configuration Sources]]
