---
type: lab
domain: spring
subdomain: spring-boot
status: active
spring_boot_version: 2.5.15
java_version: 8
certification:
  - spring-2V0-72.22
tags:
  - spring-boot
  - auto-configuration
  - application-context-runner
  - lab
---

# SPRING-BOOT-B01 Lab — Auto-configuration with `ApplicationContextRunner`

## Purpose

Prove the Boot 2.5 auto-configuration contract with a small context:

```text
spring.factories candidate registration
@ConditionalOnClass
@ConditionalOnProperty
@ConditionalOnMissingBean
@ConfigurationProperties binding
user-bean back-off
filtered classpath
ApplicationContextRunner assertions
```

This lab deliberately avoids a web server and database. The subject is auto-configuration selection and back-off, not endpoint behavior.

# Baseline

```text
Spring Boot 2.5.15
Java 8 source/bytecode target
JUnit Jupiter
ApplicationContextRunner
```

The route separately documents current Boot's `@AutoConfiguration` and `AutoConfiguration.imports` delta.

# Source structure

```text
SPRING-BOOT-B01/
├── pom.xml
└── src
    ├── main
    │   ├── java/lab/acme
    │   │   ├── AcmeLibraryMarker.java
    │   │   ├── AcmeClient.java
    │   │   ├── AcmeProperties.java
    │   │   └── AcmeAutoConfiguration.java
    │   └── resources/META-INF/spring.factories
    └── test/java/lab/acme
        └── AcmeAutoConfigurationTest.java
```

# Run

```bash
cd 50_LABS/Spring/SPRING-BOOT-B01
mvn clean test
```

Requirements:

```text
JDK 8+
Maven
Maven Central access for initial dependency resolution
```

# Auto-configuration under test

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(AcmeLibraryMarker.class)
@ConditionalOnProperty(
        prefix = "acme",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(AcmeProperties.class)
public class AcmeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AcmeClient acmeClient(AcmeProperties properties) {
        return new AcmeClient(properties.getEndpoint());
    }
}
```

Boot 2.x candidate registration:

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
lab.acme.AcmeAutoConfiguration
```

# Experiment 1 — Default conditions match

Expected:

```text
AcmeLibraryMarker is visible
acme.enabled is missing
matchIfMissing = true
no user AcmeClient exists
--------------------------------
one default AcmeClient is created
endpoint = https://default.example.test
```

Test:

```java
contextRunner.run(context -> {
    assertThat(context).hasSingleBean(AcmeClient.class);
});
```

# Experiment 2 — Property binding

Input:

```text
acme.endpoint=https://configured.example.test
```

Expected:

```text
AcmeProperties.endpoint is bound
AcmeClient receives configured endpoint
```

This distinguishes environment preparation/binding from hard-coded bean construction.

# Experiment 3 — Property condition disables feature

Input:

```text
acme.enabled=false
```

Expected:

```text
@ConditionalOnProperty does not match
AcmeAutoConfiguration contributes no AcmeClient
context still starts successfully
```

# Experiment 4 — User bean causes back-off

The runner contributes a custom bean before the auto-configuration's missing-bean condition is evaluated.

Expected:

```text
one AcmeClient
same object as custom instance
no default duplicate
```

# Experiment 5 — Optional class is absent

```java
.withClassLoader(new FilteredClassLoader(AcmeLibraryMarker.class))
```

Expected:

```text
@ConditionalOnClass does not match
AcmeClient is absent
context starts without NoClassDefFoundError
```

This proves classpath condition behavior rather than merely removing a Spring bean.

# Experiment 6 — Boot 2.x registration metadata

The test reads `META-INF/spring.factories` and asserts that the `EnableAutoConfiguration` key contains `AcmeAutoConfiguration`.

This is a metadata test, not a substitute for condition tests.

# Controlled modifications

## A. Make opt-in explicit

Change:

```java
matchIfMissing = false
```

Prediction:

```text
Default context no longer creates AcmeClient.
The test must supply acme.enabled=true.
```

## B. Remove `@ConditionalOnMissingBean`

Prediction:

```text
User-bean test creates two candidates or a bean-name/type conflict depending on definitions.
Non-invasive back-off is lost.
```

## C. Remove `spring.factories`

Prediction:

```text
Direct AutoConfigurations.of(...) tests still import the class explicitly.
A real Boot 2.x application relying on candidate discovery will not discover it.
```

This demonstrates why direct runner import and registration-metadata tests prove different things.

## D. Filter `AcmeClient` instead of marker

Observe which types are loaded while parsing configuration and creating definitions. Keep optional API references isolated to condition-protected configuration.

## E. Add invalid property validation

Add Jakarta/Javax validation appropriate to the selected Boot baseline and verify that conditions can match while property binding fails later.

# Evidence worksheet

| Experiment | Candidate available | Condition result | User bean | AcmeClient count | Startup failure |
|---|---|---|---|---:|---|
| Default | yes | match | no | | |
| Configured endpoint | yes | match | no | | |
| Disabled property | yes | no match | no | | |
| User bean | yes | missing-bean no match | yes | | |
| Missing class | no | class no match | no | | |

# Diagnostic questions

1. Was the auto-configuration candidate discovered or imported?
2. Which classpath condition matched?
3. What property value was resolved?
4. Did a user bean make the default back off?
5. Did conditions match but binding/creation fail later?
6. Is the registration metadata correct for the Boot version?

# Runtime status

```text
Source review       completed
Maven execution     not executed in assistant environment
Runtime PASS        not claimed
```

# Route navigation

- **Roadmap:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B01/SPRING-BOOT-B01 Roadmap]]
- **Canonical:** [[10_CONCEPTS/Spring/Boot/Spring Boot Bootstrap and Auto-configuration]]
- **Visual:** [[10_CONCEPTS/Spring/Boot/Spring Boot Auto-configuration Visual Deep Dive]]
- **Cards:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B01/SPRING-BOOT-B01 Cards]]
- **Cases:** [[40_PRODUCTION_CASES/Spring/Spring Boot Auto-configuration Production Cases]]
- **Sources:** [[98_SOURCES/Spring Boot Auto-configuration Sources]]
