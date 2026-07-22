---
type: sources
domain: spring
subdomain: bean-lifecycle
spring_versions:
  - 5.3
status: active
tags:
  - spring
  - lifecycle
  - sources
---

# Spring Bean Lifecycle Sources

## Primary Spring 5.3 documentation

1. Spring Framework 5.3 Reference — Lifecycle Callbacks  
   https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/core.html#beans-factory-lifecycle

2. Spring Framework 5.3 Reference — ApplicationContextAware and BeanNameAware  
   https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/core.html#beans-factory-aware

3. Spring Framework 5.3 Reference — BeanPostProcessor extension point  
   https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/core.html#beans-factory-extension-bpp

4. BeanPostProcessor API, Spring Framework 5.3  
   https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/beans/factory/config/BeanPostProcessor.html

5. InstantiationAwareBeanPostProcessor API, Spring Framework 5.3  
   https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/beans/factory/config/InstantiationAwareBeanPostProcessor.html

6. DestructionAwareBeanPostProcessor API, Spring Framework 5.3  
   https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/beans/factory/config/DestructionAwareBeanPostProcessor.html

7. InitializingBean API, Spring Framework 5.3  
   https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/beans/factory/InitializingBean.html

8. DisposableBean API, Spring Framework 5.3  
   https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/beans/factory/DisposableBean.html

9. BeanNameAware API, Spring Framework 5.3  
   https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/beans/factory/BeanNameAware.html

10. ApplicationContextAware API, Spring Framework 5.3  
    https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/context/ApplicationContextAware.html

11. SmartInitializingSingleton API, Spring Framework 5.3  
    https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/beans/factory/SmartInitializingSingleton.html

12. Lifecycle and SmartLifecycle APIs  
    https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/context/Lifecycle.html  
    https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/context/SmartLifecycle.html

13. @Bean API — initMethod and destroyMethod  
    https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/context/annotation/Bean.html

14. CommonAnnotationBeanPostProcessor API  
    https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/context/annotation/CommonAnnotationBeanPostProcessor.html

## Source interpretation policy

### Stable public contracts

- dependency population precedes normal initialization callbacks;
- BeanPostProcessor before/after initialization positions;
- initialization callback order for different mechanisms;
- destruction callback order for different mechanisms;
- Aware callback timing documented by APIs;
- prototype destruction is not managed automatically;
- processor may return a wrapper or proxy.

### Version-sensitive implementation details

- exact set and relative order of internal BeanPostProcessor implementations;
- early proxy exposure during circular dependency resolution;
- internal singleton cache transitions;
- exact framework classes used for annotation processing;
- behavior changed between Spring 5.3, 6.x and 7.x.

### Pedagogical rule

Explain the stable lifecycle route first. Add internal implementation only when it clarifies a production failure and label it as version-sensitive.
