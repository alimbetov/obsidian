---
type: concept
domain: spring
subdomain: spring-boot
difficulty: advanced
status: learning
confidence: 0
interview: true
certification:
  - spring-2V0-72.22
exam_baseline:
  - spring-boot-2.5
current_delta:
  - spring-boot-current
production_relevance: critical
prerequisites:
  - "[[Spring Core Foundations]]"
  - "[[Container Extension Points]]"
related:
  - "[[Spring Boot Auto-configuration Visual Deep Dive]]"
tags:
  - spring-boot
  - bootstrap
  - auto-configuration
  - conditions
---

# Spring Boot Bootstrap and Auto-configuration

> [!summary] За 30 секунд
> Spring Boot не заменяет Spring container. `SpringApplication` подготавливает environment и context, primary configuration запускает configuration-class parsing, а `@EnableAutoConfiguration` импортирует candidate auto-configurations. Каждая candidate configuration проходит condition checks и обычно отступает, если пользователь уже определил собственный bean. Для диагностики нужно различать: candidate discovery, condition evaluation, bean-definition registration и bean creation.

# 1. What Spring Boot adds

Spring Framework уже предоставляет:

```text
ApplicationContext
BeanDefinition registry
configuration classes
component scanning
conditions
BeanFactoryPostProcessor / BeanPostProcessor
web framework
transaction/testing infrastructure
```

Spring Boot добавляет opinionated orchestration:

```text
application bootstrap
managed dependency versions
starter dependencies
auto-configuration
externalized configuration
embedded-server defaults
production-ready diagnostics
packaging support
```

Boot не генерирует application code и не отменяет обычные Spring rules.

# 2. `@SpringBootApplication` composition

Exam-baseline mental model:

```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
public @interface SpringBootApplication {
}
```

Responsibilities:

| Annotation | Responsibility |
|---|---|
| `@SpringBootConfiguration` | Marks the primary Boot configuration; based on `@Configuration` semantics. |
| `@EnableAutoConfiguration` | Imports Boot auto-configuration candidates. |
| `@ComponentScan` | Scans from the application class package downward unless configured otherwise. |

Exam trap: auto-configuration and component scanning are separate mechanisms.

# 3. Main application class placement

Recommended layout:

```text
com.example.application.Application
com.example.application.orders.*
com.example.application.payments.*
```

Placing the application class in a root package gives component scanning and several auto-configured scans a stable default base.

The Java default package should be avoided because broad classpath scanning can become unpredictable and expensive.

# 4. `SpringApplication.run`

Typical entry point:

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

Conceptual stages:

```text
create/configure SpringApplication
identify application type
prepare bootstrap listeners and initializers
prepare Environment
load configuration data and command-line properties
create ApplicationContext
apply initializers
load primary sources
refresh context
publish startup events
invoke runners
return ready context
```

Exact internal class ordering is version-sensitive. The stable model is environment → context → definitions → refresh → runners.

# 5. Primary sources

A source supplied to `SpringApplication` can lead to:

```text
annotated configuration classes
XML resources
package/class sources recognized by loaders
```

The usual primary source is the application class annotated with `@SpringBootApplication`.

# 6. Application type

Boot chooses an application-context strategy based on classpath and explicit configuration:

```text
NONE
SERVLET
REACTIVE
```

Classpath presence influences the default, but `WebApplicationType` can be configured explicitly.

Exam trap: adding both servlet and reactive libraries does not mean two independent web contexts are automatically created.

# 7. Environment preparation

Before ordinary bean creation, Boot prepares the `Environment`.

Common sources include:

```text
configuration files
profile-specific configuration
environment variables
system properties
command-line arguments
test overrides
```

Exact precedence belongs to the externalized-configuration route. Here the key fact is that conditions and bean definitions can read the prepared environment during context creation.

# 8. Configuration data boundary

Spring Boot 2.4+ introduced the Config Data system. For Boot 2.5 exam baseline:

```text
application.properties / application.yml
profile-specific files
spring.config.import
optional locations
config trees where supported
```

Do not mix old bootstrap-context behavior from legacy Spring Cloud with Boot Config Data rules without marking the version boundary.

# 9. What auto-configuration means

Auto-configuration attempts to register sensible beans when evidence supports them.

Typical evidence:

```text
a library class exists
an application type matches
a property is enabled
a resource exists
a bean is missing
a required bean already exists
```

Auto-configuration is non-invasive by design: user-defined configuration can replace specific defaults.

# 10. Candidate-discovery pipeline

Stable model:

```text
@EnableAutoConfiguration
    ↓
select candidate auto-configuration classes
    ↓
apply exclusions and import filters
    ↓
evaluate conditions
    ↓
import matching configuration classes
    ↓
register their BeanDefinitions
```

In Boot 2.x, custom auto-configuration candidates are commonly listed under `EnableAutoConfiguration` in `META-INF/spring.factories`.

Current Boot uses:

```text
META-INF/spring/
org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

Do not answer a Boot 2.5 registration question with only `AutoConfiguration.imports`.

# 11. `AutoConfigurationImportSelector`

Conceptually it is a deferred-import selector that:

1. Confirms auto-configuration is enabled.
2. Loads candidate class names.
3. Removes duplicates.
4. Applies exclusions.
5. Applies import filters/metadata optimizations.
6. Produces selected imports.
7. Emits import events/listener information where supported.

Why deferred matters: ordinary user configuration is parsed before auto-configuration selection is finalized, supporting back-off conditions such as missing-bean checks.

# 12. Core condition annotations

## `@ConditionalOnClass`

Matches when required classes are available.

```java
@Configuration
@ConditionalOnClass(SomeClient.class)
class SomeClientAutoConfiguration {
}
```

Use annotation metadata carefully so the JVM does not load an absent type too early.

## `@ConditionalOnMissingClass`

Matches when named classes are absent.

## `@ConditionalOnBean`

Matches when required beans already exist.

## `@ConditionalOnMissingBean`

Backs off when the application provides its own matching bean.

## `@ConditionalOnProperty`

Matches based on environment properties and options such as `havingValue` or `matchIfMissing`.

## `@ConditionalOnWebApplication`

Restricts configuration to servlet/reactive web application types.

## `@ConditionalOnNotWebApplication`

Restricts configuration to non-web applications.

## `@ConditionalOnResource`

Matches when required resources exist.

## `@ConditionalOnExpression`

Evaluates a SpEL expression, but can create early-initialization complexity and should not replace clearer conditions casually.

# 13. Condition phase matters

Conditions can be evaluated while parsing configuration or while registering bean definitions, depending on condition implementation.

Important consequence:

```text
classpath/property conditions
    often decide whether configuration participates

missing-bean conditions
    depend on definitions visible at registration time
```

A condition is not a recurring runtime check around every method invocation.

# 14. Back-off behavior

Example mental model:

```text
JDBC API present
DataSource implementation present
no user DataSource bean
required properties/default embedded DB available
    ↓
DataSource auto-configuration contributes a bean
```

If the user defines a `DataSource`, missing-bean conditions usually prevent the default from being registered.

Exam trap: auto-configuration does not generally override a deliberate user bean with the same semantic role.

# 15. Search strategy in bean conditions

Some bean conditions can search:

```text
current context
ancestors
all contexts
```

Context hierarchy can therefore affect condition outcomes. When diagnosing unexpected matching, inspect both local and parent beans.

# 16. Ordering auto-configurations

Boot 2.x annotations:

```text
@AutoConfigureBefore
@AutoConfigureAfter
@AutoConfigureOrder
```

Current Boot also supports ordering attributes on `@AutoConfiguration`.

Ordering affects when bean definitions are contributed. It does not directly force ordinary bean instantiation order; dependencies and `@DependsOn` govern creation relationships.

# 17. Excluding auto-configuration

Supported approaches include:

```java
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
```

```java
@SpringBootApplication(excludeName =
    "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration")
```

```properties
spring.autoconfigure.exclude=\
org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

Use exclusion when a whole auto-configuration is inappropriate. Prefer user beans/properties when replacing only one default behavior.

# 18. Condition Evaluation Report

Run with debug enabled:

```bash
java -jar app.jar --debug
```

The report groups:

```text
positive matches
negative matches
exclusions
unconditional classes
```

Correct diagnostic question:

```text
Was the auto-configuration not discovered?
Was it excluded?
Which condition did not match?
Did it match but bean registration fail?
Did bean creation fail later?
```

# 19. Starters

A starter is primarily an opinionated dependency descriptor.

It usually:

```text
pulls a coherent set of dependencies
works with Boot dependency management
activates relevant auto-configuration through classpath evidence
reduces manual version selection
```

A starter is not itself necessarily the class that configures beans.

# 20. Dependency management

Spring Boot's parent/BOM supplies tested dependency versions.

Maven patterns:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.5.x</version>
</parent>
```

or importing the Boot dependency-management BOM.

Exam trap: dependency management chooses versions; it does not add a dependency unless the dependency is declared or brought transitively.

# 21. Custom auto-configuration — Boot 2.x

Typical class:

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(AcmeClient.class)
@EnableConfigurationProperties(AcmeProperties.class)
public class AcmeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    AcmeClient acmeClient(AcmeProperties properties) {
        return new AcmeClient(properties.getEndpoint());
    }
}
```

Boot 2.x registration:

```properties
# META-INF/spring.factories
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.acme.AcmeAutoConfiguration
```

# 22. Current custom auto-configuration delta

Current style:

```java
@AutoConfiguration
@ConditionalOnClass(AcmeClient.class)
@EnableConfigurationProperties(AcmeProperties.class)
public class AcmeAutoConfiguration {
}
```

Registration:

```text
META-INF/spring/
org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

The conceptual condition/back-off model remains similar; the registration/annotation surface changed.

# 23. Auto-configuration package rules

Auto-configuration classes should live in a dedicated package and should not be found accidentally by the consumer's component scan.

Prefer explicit imports for supporting configuration rather than broad component scans inside auto-configuration.

Why:

```text
condition boundaries remain visible
optional dependencies remain optional
consumer package structure does not affect library internals
```

# 24. `ApplicationContextRunner`

Purpose: create a small context repeatedly with controlled configuration, properties and classpath.

```java
private final ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
                .withUserConfiguration(AcmeAutoConfiguration.class);

@Test
void backsOffWhenUserProvidesClient() {
    contextRunner
        .withBean(AcmeClient.class, () -> new AcmeClient("custom"))
        .run(context -> {
            assertThat(context).hasSingleBean(AcmeClient.class);
            assertThat(context.getBean(AcmeClient.class).endpoint())
                    .isEqualTo("custom");
        });
}
```

High-value tests:

```text
default condition matches
missing class causes back-off
property disables feature
user bean causes back-off
invalid properties fail clearly
web/non-web context distinction
```

# 25. Filtered classpath testing

A filtered classloader can simulate an optional dependency being absent.

This is stronger than merely not declaring a bean because `@ConditionalOnClass` is about classpath evidence.

# 26. Failure analyzers

Boot can turn specific startup exceptions into structured failure analysis:

```text
description
action
root cause context
```

A failure analyzer does not fix the error. It improves startup diagnostics.

# 27. Startup events

Stable sequence contains phases such as:

```text
starting
environment prepared
context initialized/prepared
context refreshed
application started
runners invoked
application ready
failed
```

Exact event names/order can vary by Boot version. Do not perform arbitrary bean access before the context phase supports it.

# 28. `ApplicationRunner` and `CommandLineRunner`

Both run after the context has been refreshed.

```text
CommandLineRunner → raw String[] args
ApplicationRunner → parsed ApplicationArguments
```

Multiple runners can be ordered with `Ordered`/`@Order`.

Failure from a runner can fail application startup.

# 29. Lazy initialization

Global lazy initialization can reduce startup work but shifts failures and latency to first use.

Trade-off:

```text
faster startup
versus
later configuration errors
first-request latency
less deterministic warm-up
```

Do not use lazy mode to conceal an invalid bean graph.

# 30. Embedded server boundary

For a servlet web application, Boot usually:

1. Creates a servlet-capable application context.
2. Finds an embedded servlet-container factory from auto-configuration.
3. Creates/starts the server during context refresh.
4. Registers `DispatcherServlet` and web infrastructure when conditions match.

The detailed request pipeline belongs to `SPRING-MVC-B01`.

# 31. Production diagnostic sequence

```text
1. Confirm the intended Boot version and dependencies.
2. Confirm primary application class/package.
3. Inspect startup exception's deepest cause.
4. Enable condition report/debug.
5. Locate candidate auto-configuration.
6. Check exclusions.
7. Check each condition and property value.
8. Check user bean that may cause back-off.
9. Distinguish definition registration from instance creation failure.
10. Reproduce with ApplicationContextRunner when possible.
```

# 32. Interview explanation

> `@SpringBootApplication` combines Boot configuration, component scanning and auto-configuration. `SpringApplication` prepares the environment and context, then configuration parsing reaches `@EnableAutoConfiguration`. Boot discovers candidate configuration classes, applies exclusions and conditions, and imports matching definitions. User-defined beans normally make missing-bean conditions back off. I diagnose failures through the condition report and a small `ApplicationContextRunner` test rather than assuming Boot ignored a property.

# 33. Exam traps

```text
@SpringBootApplication is not only @Configuration.
Component scanning is not auto-configuration discovery.
Dependency management does not add dependencies.
A starter is not the same thing as an auto-configuration class.
@ConditionalOnMissingBean is evaluated against visible bean definitions, not arbitrary runtime objects.
Ordering auto-configurations does not guarantee bean instantiation order.
--debug explains condition matches; it does not automatically repair configuration.
Boot 2.x spring.factories and current AutoConfiguration.imports are version-specific answers.
```

# 34. Exercises

1. Expand `@SpringBootApplication` into its three core annotations.
2. Trace a missing-bean back-off example.
3. Explain why an auto-configuration can match but bean creation still fail.
4. Compare Boot 2.x and current candidate registration.
5. Write `ApplicationContextRunner` tests for classpath, property and user-bean conditions.
6. Diagnose an unwanted `DataSourceAutoConfiguration` activation.
7. Explain starter versus dependency management versus auto-configuration.

## Route navigation

- **Roadmap:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B01/SPRING-BOOT-B01 Roadmap]]
- **Visual model:** [[10_CONCEPTS/Spring/Boot/Spring Boot Auto-configuration Visual Deep Dive]]
- **Cards:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B01/SPRING-BOOT-B01 Cards]]
- **Production cases:** [[40_PRODUCTION_CASES/Spring/Spring Boot Auto-configuration Production Cases]]
- **Lab:** [[50_LABS/Spring/SPRING-BOOT-B01/README]]
- **Sources:** [[98_SOURCES/Spring Boot Auto-configuration Sources]]
- **Domain map:** [[01_MAPS/Spring Map]]
