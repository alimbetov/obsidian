---
type: sources
domain: spring
subdomain: dependency-resolution
status: active
spring_version_focus: 5.3.39
tags:
  - spring
  - sources
  - dependency-injection
---

# Spring Dependency Resolution Sources

## Version focus

Основной учебный target — Spring Framework 5.3.x, соответствующий линии подготовки `Spring Certified Professional (2V0-72.22)`. Для runnable lab используется `5.3.39`.

## Primary Spring documentation

1. Spring Framework 5.3.39 Reference Documentation  
   https://docs.spring.io/spring-framework/docs/5.3.39/reference/html/

2. Core Technologies — Beans, Dependencies and Annotation-based Configuration  
   https://docs.spring.io/spring-framework/docs/5.3.39/reference/html/core.html

3. `@Primary` API — Spring Framework 5.3  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/context/annotation/Primary.html

4. `@Qualifier` API — Spring Framework 5.3  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/beans/factory/annotation/Qualifier.html

5. `@Autowired` API — Spring Framework 5.3  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/beans/factory/annotation/Autowired.html

6. `ObjectProvider` API — Spring Framework 5.3  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/beans/factory/ObjectProvider.html

7. `@Order` API — Spring Framework 5.3  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/core/annotation/Order.html

8. Spring Framework 5.3.39 release announcement  
   https://spring.io/blog/2024/08/14/spring-framework-6-1-12-6-0-23-and-5-3-39-available-now

## Version-sensitivity policy

- `@Fallback` не включается в экзаменационный маршрут Spring 5.3, поскольку это более новая возможность Spring 6.2.
- Современная документация может описывать optimizations и name-shortcut behavior, появившиеся после 5.3; такие детали нельзя автоматически переносить в старый certification target.
- Formal card answers должны соответствовать Spring 5.3 public contracts.
- Implementation details candidate resolver не должны подаваться как стабильный API, если это не зафиксировано reference documentation.
- Code examples должны явно отмечать, когда используется Spring Boot-specific annotation вместо чистого Spring Framework.

## Pedagogical source rule

Каждая сложная карточка должна различать:

1. public contract;
2. convenient resolution convention;
3. version-specific implementation detail;
4. production design recommendation.

Эти категории нельзя смешивать в одном недифференцированном утверждении.
