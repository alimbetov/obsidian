---
type: sources
domain: spring
subdomain: configuration-profiles
status: active
spring_versions:
  - 5.3.39
  - 6.x
  - 7.x
boot_versions:
  - 2.7.x
  - 3.5.x
tags:
  - spring
  - configuration
  - profiles
  - sources
---

# Spring Configuration and Profiles Sources

## Spring Framework primary sources

1. Java-based Container Configuration  
   https://docs.spring.io/spring-framework/reference/core/beans/java.html

2. Basic Concepts: `@Bean` and `@Configuration`  
   https://docs.spring.io/spring-framework/reference/core/beans/java/basic-concepts.html

3. Using the `@Configuration` annotation  
   https://docs.spring.io/spring-framework/reference/core/beans/java/configuration-annotation.html

4. Composing Java-based Configurations and `@Import`  
   https://docs.spring.io/spring-framework/reference/core/beans/java/composing-configuration-classes.html

5. Environment abstraction, profiles and PropertySource  
   https://docs.spring.io/spring-framework/reference/core/beans/environment.html

6. `@Value` and placeholder resolution  
   https://docs.spring.io/spring-framework/reference/core/beans/annotation-config/value-annotations.html

7. `@Configuration` API  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/context/annotation/Configuration.html

8. `@Bean` API and lite-mode semantics  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/context/annotation/Bean.html

9. `@Import` API  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/context/annotation/Import.html

10. `@Profile` API  
    https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/context/annotation/Profile.html

11. `Environment` API  
    https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/core/env/Environment.html

12. `PropertySource` API  
    https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/core/env/PropertySource.html

13. TestContext environment profiles  
    https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/ctx-management/env-profiles.html

## Spring Boot primary sources

14. Externalized Configuration  
    https://docs.spring.io/spring-boot/reference/features/external-config.html

15. Type-safe Configuration Properties  
    https://docs.spring.io/spring-boot/reference/features/external-config.html#features.external-config.typesafe-configuration-properties

16. Testing Spring Boot applications  
    https://docs.spring.io/spring-boot/reference/testing/index.html

## Version-sensitivity policy

### Stable ideas

- full `@Configuration` supports managed inter-bean calls;
- lite-mode `@Bean` calls are ordinary Java calls;
- `@Import` composes configuration and may import configuration classes, selectors, registrars and regular components;
- profiles conditionally register bean definitions;
- Environment exposes profiles and property sources;
- later/higher-precedence property sources override lower-precedence values according to the active framework/Boot model;
- `@ConfigurationProperties` binds groups of related values into typed objects in Spring Boot.

### Version-sensitive details

- exact Spring Boot PropertySource precedence;
- Config Data processing rules introduced in Spring Boot 2.4;
- constructor-binding defaults and annotation requirements across Boot 2.x and 3.x;
- support for profile expressions and annotation placement details;
- native-image/AOT constraints around configuration classes;
- current behavior of `proxyBeanMethods` optimizations.

## Source policy

- Spring Framework guarantees and Spring Boot conveniences are documented separately.
- Exact property precedence must always be tied to a specific Boot version.
- A successful local test with one active profile is evidence for that setup, not proof of universal precedence.
- Environment variables and secret stores should be treated as deployment inputs, not copied into the knowledge base as real credentials.
