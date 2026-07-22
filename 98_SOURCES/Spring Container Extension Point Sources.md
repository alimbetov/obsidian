---
type: sources
domain: spring
subdomain: container-extension-points
status: active
spring_versions:
  - 5.3.39
tags:
  - spring
  - sources
  - bean-post-processor
---

# Spring Container Extension Point Sources

## Primary Spring 5.3 sources

1. Spring Framework 5.3 Core Technologies — Container Extension Points  
   https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/core.html#beans-factory-extension

2. `BeanPostProcessor` — Spring Framework 5.3.39 Javadoc  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/beans/factory/config/BeanPostProcessor.html

3. `BeanFactoryPostProcessor` — Spring Framework 5.3.39 Javadoc  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/beans/factory/config/BeanFactoryPostProcessor.html

4. `BeanDefinitionRegistryPostProcessor` — Spring Framework 5.3.39 Javadoc  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/beans/factory/support/BeanDefinitionRegistryPostProcessor.html

5. `InstantiationAwareBeanPostProcessor` — Spring Framework 5.3.39 Javadoc  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/beans/factory/config/InstantiationAwareBeanPostProcessor.html

6. `SmartInstantiationAwareBeanPostProcessor` — Spring Framework 5.3.39 Javadoc  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/beans/factory/config/SmartInstantiationAwareBeanPostProcessor.html

7. `DestructionAwareBeanPostProcessor` — Spring Framework 5.3.39 Javadoc  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/beans/factory/config/DestructionAwareBeanPostProcessor.html

8. `@Bean` — Spring Framework 5.3.39 Javadoc  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/context/annotation/Bean.html

9. `Ordered` — Spring Framework 5.3.39 Javadoc  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/core/Ordered.html

10. `PriorityOrdered` — Spring Framework 5.3.39 Javadoc  
    https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/core/PriorityOrdered.html

11. `AutowiredAnnotationBeanPostProcessor` — Spring Framework 5.3.39 Javadoc  
    https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/beans/factory/annotation/AutowiredAnnotationBeanPostProcessor.html

12. `AbstractAutoProxyCreator` — Spring Framework 5.3.39 Javadoc  
    https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/aop/framework/autoproxy/AbstractAutoProxyCreator.html

## Stable public contracts used in the module

- BFPP operates on configuration metadata before ordinary bean instantiation.
- BPP operates on bean instances around initialization callbacks.
- BDRPP can register additional BeanDefinitions before regular BFPP execution.
- Programmatically registered BPPs use registration order and ignore Ordered semantics.
- Auto-detected processors are sorted using PriorityOrdered/Ordered categories.
- BPPs and their direct dependencies are instantiated early and can be ineligible for complete auto-proxy processing.
- `postProcessBeforeInstantiation()` can short-circuit normal target creation.
- `postProcessAfterInstantiation(false)` can veto normal property population.
- `postProcessProperties()` participates before property values are applied.
- `SmartInstantiationAwareBeanPostProcessor` supports type prediction, constructor candidates and early references.
- `DestructionAwareBeanPostProcessor` runs before bean destruction callbacks when the container manages destruction.
- BFPP-returning `@Bean` methods should normally be static to avoid early configuration-class instantiation.

## Version-sensitive implementation details

Do not teach these as universal contracts without checking the target Spring version:

- exact internal processor class list;
- exact numeric order values of infrastructure processors;
- circular-reference cache internals;
- early proxy exposure implementation;
- merged-definition cache behavior;
- AOT-specific processor interfaces in newer Spring versions;
- differences between Spring 5.3, 6.x and 7.x;
- Boot-specific auto-proxy defaults.

## Source policy

- Public lifecycle guarantees come from Spring reference documentation and Javadoc.
- Source-code internals are supplementary and version-sensitive.
- A successful runtime trace demonstrates one configuration, not every legal processor ordering.
- Custom framework code should document its supported Spring version explicitly.
