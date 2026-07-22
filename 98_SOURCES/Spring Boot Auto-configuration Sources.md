---
type: source-index
domain: spring
subdomain: spring-boot
status: active
verified_at: 2026-07-21
exam_baseline:
  - spring-boot-2.5
current_delta:
  - spring-boot-current
tags:
  - spring-boot
  - auto-configuration
  - sources
---

# Spring Boot Auto-configuration Sources

> [!summary]
> Primary sources for `SPRING-BOOT-B01`. Exam-facing explanations use Spring Boot 2.5-era behavior where relevant; current production deltas are explicitly separated.

# Official exam status

- https://www.broadcom.com/support/education/software/certification/exams/spring-pro-develop-exam

Confirmed on 2026-07-21:

```text
Exam 2V0-72.22
60 questions
130 minutes
single and multiple choice
passing score 300 scaled
```

The public page exposes an Exam Study Guide link. The detailed downloadable objective guide must be re-verified before final exam freeze because Broadcom can change portal delivery and certification paths.

# Spring Boot 2.5 exam-baseline documentation

## Developing with Spring Boot

- https://docs.spring.io/spring-boot/docs/2.5.x/reference/html/using.html
- https://docs.spring.io/spring-boot/docs/2.5.9/reference/html/using.html

Use for:

- build systems and dependency management;
- starters;
- main application class placement;
- configuration classes;
- auto-configuration opt-in;
- replacing and excluding auto-configuration;
- `@SpringBootApplication` composition;
- `--debug` condition report.

## Auto-configuration API package

- https://docs.spring.io/spring-boot/docs/2.5.15/api/org/springframework/boot/autoconfigure/package-summary.html

Use for:

- `AutoConfigurationImportSelector` ecosystem;
- import filters and listeners;
- auto-configuration metadata;
- `spring.factories` extension model.

## SpringApplication and core features

- https://docs.spring.io/spring-boot/docs/2.5.x/reference/html/features.html

Use for:

- startup lifecycle;
- application events;
- runners;
- externalized configuration;
- profiles;
- lazy initialization;
- failure analysis.

## Creating auto-configuration in Boot 2.x

- https://docs.spring.io/spring-boot/docs/2.5.x/reference/html/features.html#features.developing-auto-configuration

Exam-baseline rules:

```text
@Configuration auto-configuration classes
@ConditionalOnClass
@ConditionalOnMissingBean
META-INF/spring.factories registration
ordering annotations
ApplicationContextRunner testing
```

# Spring Boot 2.7 bridge

- https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/

Use for a stable late-Boot-2 comparison and for labs that need a maintained Java 8-compatible baseline.

# Current production delta

## Using auto-configuration

- https://docs.spring.io/spring-boot/reference/using/auto-configuration.html

## Creating custom auto-configuration and starters

- https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html

Current differences:

```text
@AutoConfiguration
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
before/after attributes and replacement metadata
current starter naming and metadata guidance
```

Do not project these Boot 3+/current details backward into a Boot 2.5 exam question unless the question explicitly asks for the modern delta.

# Test utilities

- https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/context/runner/package-summary.html
- https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/context/runner/AbstractApplicationContextRunner.html

Use for:

- `ApplicationContextRunner`;
- `WebApplicationContextRunner`;
- context assertions;
- filtered classloader tests;
- property and user-configuration tests.

# Related route

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B01/SPRING-BOOT-B01 Roadmap]]
- [[10_CONCEPTS/Spring/Boot/Spring Boot Bootstrap and Auto-configuration]]
- [[10_CONCEPTS/Spring/Boot/Spring Boot Auto-configuration Visual Deep Dive]]
