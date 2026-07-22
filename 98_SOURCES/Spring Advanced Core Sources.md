---
type: sources
domain: spring
subdomain: advanced-core
status: active
spring_versions:
  - 5.3.39
  - current
tags:
  - spring
  - sources
  - scopes
  - factorybean
---

# Spring Advanced Core Sources

## Primary Spring Framework sources

1. Spring Framework 5.3 Core reference — bean scopes, scoped proxies, custom scopes, lazy initialization, circular dependencies, FactoryBean, Resources, MessageSource and ApplicationContext hierarchy  
   https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/core.html

2. Bean Scopes — current reference  
   https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html

3. Lazy-initialized Beans — current reference  
   https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-lazy-init.html

4. Dependency Injection and circular dependencies — current reference  
   https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html

5. `FactoryBean` API, Spring Framework 5.3.39  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/beans/factory/FactoryBean.html

6. `ObjectProvider` API, Spring Framework 5.3.39  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/beans/factory/ObjectProvider.html

7. `ApplicationContext` API and parent-context semantics  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/context/ApplicationContext.html

8. `Resource` API  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/core/io/Resource.html

9. `MessageSource` API  
   https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/context/MessageSource.html

10. `SimpleThreadScope` API  
    https://docs.spring.io/spring-framework/docs/5.3.39/javadoc-api/org/springframework/context/support/SimpleThreadScope.html

## Stable contracts used in the module

- Spring singleton means one instance per bean definition per container, not one JVM-global instance.
- Prototype creates a new instance for each container request, but configured destruction callbacks are not automatically invoked by the container.
- Injecting a prototype directly into a singleton resolves the prototype once during singleton construction.
- A scoped proxy is a stable reference that resolves the actual scoped target when a method is invoked.
- `ObjectProvider<T>` performs container-backed lookup on demand.
- A `FactoryBean<T>` is a bean whose normal lookup returns its product; `&beanName` dereferences the factory itself.
- Lazy initialization delays creation, but an eager singleton dependency can force a lazy bean to be created at startup.
- Constructor circular dependencies are not resolvable through early property injection.
- A child `ApplicationContext` can resolve beans from its parent; the parent cannot resolve child beans. A child definition can shadow a parent definition by name.
- `ApplicationContext` also acts as a `ResourceLoader` and `MessageSource`.

## Version-sensitive areas

- Spring Boot circular-reference defaults are Boot policy, not the core Spring Framework contract.
- Proxy implementation and generated class details must not be treated as business API.
- Web-scope activation depends on the type of web application context and infrastructure.
- Native-image/AOT restrictions can change which dynamic proxy or reflection patterns are practical.
- Current Spring versions may add convenience APIs while preserving the underlying scope and hierarchy model.

## Source policy

- Formal statements use Spring reference documentation and official Javadoc.
- Spring Framework behavior is separated from Spring Boot defaults.
- A successful experiment illustrates a mechanism but does not replace lifecycle and ownership reasoning.
- Every scope discussion must answer both: **who creates the object?** and **who owns destruction?**
