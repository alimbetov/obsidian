---
type: source-index
domain: spring
subdomain: spring-boot-configuration
status: active
verified_at: 2026-07-22
exam_baseline:
  - spring-boot-2.5.15
current_delta:
  - spring-boot-current
objectives:
  - SPRING-1.3.1
  - SPRING-1.3.2
  - SPRING-6.2.1
tags:
  - spring-boot
  - externalized-configuration
  - configuration-properties
  - sources
---

# Spring Boot Externalized Configuration Sources

> [!summary]
> Primary source index for `SPRING-BOOT-B02`. Exam-facing explanations use Boot 2.5 Config Data, property ordering and `@ConfigurationProperties`; current documentation is used only for explicit deltas.

# Official exam baseline

- Broadcom exam guide: https://docs.broadcom.com/doc/vmw-spring-professional-develop-exam-guide

Mapped objectives:

```text
SPRING-1.3.1  Use external properties to control configuration
SPRING-1.3.2  Explain and use profiles
SPRING-6.2.1  Describe options for defining and loading properties
```

The guide pins the reference baseline to Spring Framework 5.3 and Spring Boot 2.5.

# Spring Boot 2.5 reference

## Externalized configuration, Config Data and profiles

- https://docs.spring.io/spring-boot/docs/2.5.x/reference/html/features.html
- https://docs.spring.io/spring-boot/docs/2.5.15/reference/pdf/spring-boot-reference.pdf

Use for:

```text
property-source ordering
application.properties / application.yaml
profile-specific files
spring.config.import
optional: locations
config trees
multi-document files
spring.config.activate.on-profile
spring.profiles.active/include/group
command-line properties
JSON application properties
```

## Config Data API package

- https://docs.spring.io/spring-boot/docs/2.5.15/api/org/springframework/boot/context/config/package-summary.html

Use for:

```text
ConfigDataEnvironmentPostProcessor
ConfigDataLocationResolver
ConfigDataLoader
ConfigDataResource
location-not-found behavior
```

## Configuration properties API

- https://docs.spring.io/spring-boot/docs/2.5.15/api/org/springframework/boot/context/properties/ConfigurationProperties.html
- https://docs.spring.io/spring-boot/docs/2.5.15/api/org/springframework/boot/context/properties/EnableConfigurationProperties.html
- https://docs.spring.io/spring-boot/docs/2.5.15/api/org/springframework/boot/context/properties/ConfigurationPropertiesScan.html
- https://docs.spring.io/spring-boot/docs/2.5.15/api/org/springframework/boot/context/properties/bind/package-summary.html

Use for:

```text
prefix-based binding
relaxed names
nested objects
collections and maps
constructor binding
conversion
binding validation
programmatic Binder
```

# Current production delta

- https://docs.spring.io/spring-boot/reference/features/external-config.html
- https://docs.spring.io/spring-boot/reference/features/profiles.html
- https://docs.spring.io/spring-boot/how-to/properties-and-configuration.html

Current documentation remains useful for the conceptual model, but constructor binding, records, profile validation and registration details must be marked by version.

# Spring Framework sources

- https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/core.html#beans-environment
- https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/core/env/Environment.html
- https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/core/env/PropertySource.html

Use for the underlying `Environment`, profiles and ordered `PropertySource` abstraction.

# Evidence policy

When documenting a configuration value, record:

```text
property key
resolved value
winning property source
active profiles
config-data location/document
binding target
validation outcome
```

Do not infer precedence from file names alone; inspect the prepared Environment or controlled tests.

# Related route

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Roadmap]]
- [[10_CONCEPTS/Spring/Boot/Spring Boot Externalized Configuration and Type-safe Binding]]
- [[10_CONCEPTS/Spring/Boot/Spring Boot Configuration Visual Deep Dive]]
- [[50_LABS/Spring/SPRING-BOOT-B02/README]]
