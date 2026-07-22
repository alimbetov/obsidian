---
type: assessment
certification: spring-2V0-72.22
domain: spring
subdomain: spring-boot-configuration
batch_id: SPRING-BOOT-B02
status: active
objectives:
  - SPRING-1.3.1
  - SPRING-1.3.2
  - SPRING-6.2.1
pre_test_questions: 10
post_test_questions: 15
tags:
  - assessment
  - spring-boot
  - externalized-configuration
---

# SPRING-BOOT-B02 Assessment

> [!summary]
> Pre-test identifies misconceptions before study. Post-test checks recall, discrimination, configuration-result reasoning and transfer. Do not update card confidence from the pre-test.

# Pre-test — 10 questions

Answer without opening the route. Record only misconceptions, not a readiness score.

## P01

Three sources define `client.timeout`: packaged YAML `3s`, environment variable `CLIENT_TIMEOUT=20s`, command line `--client.timeout=1s`. Which value normally wins, and why?

## P02

What is the difference between `spring.config.location` and `spring.config.additional-location`?

## P03

Does adding `@ConfigurationProperties("client")` automatically register the class as a Spring bean?

## P04

Why can an application start even when `client.timout=5s` is misspelled?

## P05

What does `optional:` change for a Config Data import?

## P06

Which is a better fit for twelve related client properties: twelve `@Value` fields or one validated `@ConfigurationProperties` object? Explain the mechanism, not only preference.

## P07

What is the difference between an active profile and a default profile?

## P08

Can a property be present in `Environment` but fail to bind into `Duration`?

## P09

What is the Boot 2.5 constructor-binding boundary that differs from current Boot?

## P10

A password is mounted through a config tree. Name two remaining exposure risks.

# Pre-test diagnostic key

> [!answer]- Open after answering
> 1. Command line normally wins because source precedence is higher.
> 2. `location` replaces defaults; `additional-location` extends them.
> 3. No; enable, scan or declare it.
> 4. Unknown keys can remain unused without strict binding/validation.
> 5. Missing location no longer fails startup.
> 6. Typed properties object: namespace ownership, conversion, validation and metadata.
> 7. Active is explicitly selected; default applies only when none is active.
> 8. Yes; source resolution and conversion are separate stages.
> 9. Explicit `@ConstructorBinding` is a common Boot 2.5 answer; current Boot can infer a single constructor.
> 10. Logs/toString, Actuator env/configprops exposure, process/file permissions or repository history.

# Study sequence

```text
pre-test
→ canonical note
→ visual deep dive
→ cards C001–C035
→ production cases
→ lab predictions and execution
→ post-test
→ record per-card outcomes
```

# Post-test — 15 mixed questions

## Q01 — Multiple select, choose 3

Which statements are true?

A. `spring.config.location` normally replaces default locations.  
B. `spring.config.additional-location` normally replaces default locations.  
C. `spring.config.import` participates in Config Data processing.  
D. `optional:` suppresses validation errors in a present configuration file.  
E. A missing mandatory import can fail startup.

> [!answer]- Answer
> A, C and E.

## Q02 — Configuration result

```properties
# packaged application.properties
client.timeout=3s
```

Deployment:

```text
CLIENT_TIMEOUT=20s
```

Launch:

```bash
java -jar app.jar --client.timeout=1s
```

What value binds?

> [!answer]- Answer
> `1s`, because the command-line property normally has higher precedence than the environment variable and file.

## Q03 — Distinction

Explain why `@PropertySource` is not a complete replacement for Config Data.

> [!answer]- Answer
> It is added during configuration-class processing and lacks Boot Config Data discovery/import/profile semantics required before ordinary bean creation.

## Q04 — Multiple select, choose 2

Which approaches can register a `@ConfigurationProperties` type?

A. `@EnableConfigurationProperties`  
B. `@ConfigurationPropertiesScan`  
C. `@ConditionalOnProperty`  
D. `@PropertySource`

> [!answer]- Answer
> A and B.

## Q05 — Failure analysis

`client.timeout=abc` exists in the winning source. The application fails while creating configuration properties. Which stage failed?

> [!answer]- Answer
> Type conversion/binding, not property-source discovery.

## Q06 — Profile result

`application.properties` defines `mode=base`; `application-prod.properties` defines `mode=prod`; no active profile exists and default profile is `local`. What value is expected?

> [!answer]- Answer
> `base`; the prod file is inactive.

## Q07 — Multiple select, choose 3

Benefits of `@ConfigurationProperties` over many scattered `@Value` injections include:

A. relaxed binding  
B. nested object binding  
C. guaranteed secret encryption  
D. validation  
E. automatic remote refresh

> [!answer]- Answer
> A, B and D.

## Q08 — Version trap

A question explicitly targets Boot 2.5 immutable binding. Is “a single constructor is always inferred without any marker” the safest answer?

> [!answer]- Answer
> No. Boot 2.5 commonly uses explicit `@ConstructorBinding`; current inference rules must be treated as a version delta.

## Q09 — Incident

The YAML says `feature.enabled=false`, but the feature is enabled only in Kubernetes. What is the first evidence to collect?

> [!answer]- Answer
> The resolved value and winning `PropertySource`, including environment variables/config maps/command-line overrides and active profiles.

## Q10 — Config tree

What does `configtree:/run/secrets/` map into the environment?

> [!answer]- Answer
> Each filename becomes a property key and each file's content becomes its value.

## Q11 — Validation

Why is `@NotNull` on a properties field insufficient if the class is never registered as a configuration-properties bean?

> [!answer]- Answer
> Binding and validation never run for that unregistered target.

## Q12 — Unknown property

Name two controls that help detect a misspelled property.

> [!answer]- Answer
> Configuration metadata/IDE completion, strict binding or startup validation/tests, and controlled environment inspection.

## Q13 — Collection binding

Why should a list override be tested when several property sources contribute values?

> [!answer]- Answer
> Collection replacement/merge behavior can differ from scalar overriding, so the intended final list must be proven under the actual source combination.

## Q14 — Security transfer

Why can a config-tree secret still leak?

> [!answer]- Answer
> It enters the Environment and bean graph; logs, `toString`, Actuator endpoints, permissions or diagnostics can expose it unless protected and sanitized.

## Q15 — Diagnostic sequence

Put these in order: inspect conversion, identify canonical key, inspect active profile/document, list property sources and winner.

> [!answer]- Answer
> Identify canonical key → list sources and winner → inspect active profiles/documents → inspect conversion/binding/validation.

# Post-test scoring

```text
13–15 correct  route recall is strong; proceed to delayed review
10–12 correct  review wrong/guessed cards and rerun selected lab cases
7–9 correct    revisit canonical decision models and all production cases
0–6 correct    rebuild the route from Environment → source order → Config Data → Binder
```

A correct guessed answer is recorded as `correct-guessed`, not mastery.

# Related materials

- [[00_HOME/Card Review Dashboard]]
- [[70_PROGRESS/README]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Cards]]
- [[50_LABS/Spring/SPRING-BOOT-B02/README]]
