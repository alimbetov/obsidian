---
type: lab
domain: spring
subdomain: spring-mvc
route: SPRING-MVC-B01
status: published
runtime: spring-boot-2.5.15
java_source: 8
test_count: 11
objectives:
  - SPRING-3.1.1
  - SPRING-3.1.2
  - SPRING-3.1.3
  - SPRING-3.1.4
tags:
  - spring-mvc
  - mockmvc
  - dispatcher-servlet
---

# SPRING-MVC-B01 Lab — DispatcherServlet and Controller Pipeline

> [!summary]
> This lab runs Spring Boot 2.5.15 with Java 8 source compatibility and sends requests through the real `DispatcherServlet` using `@SpringBootTest`, `@AutoConfigureMockMvc`, and `MockMvc`.

# Route navigation

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Roadmap]]
- [[10_CONCEPTS/Spring/MVC/DispatcherServlet and Annotated Controller Pipeline]]
- [[10_CONCEPTS/Spring/MVC/Spring MVC DispatcherServlet Visual Deep Dive]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Cards]]
- [[40_PRODUCTION_CASES/Spring/Spring MVC DispatcherServlet Production Cases]]

# Run

```bash
cd 50_LABS/Spring/SPRING-MVC-B01
mvn clean test
```

PowerShell:

```powershell
Set-Location 50_LABS/Spring/SPRING-MVC-B01
mvn clean test
```

# Evidence model

```text
MockHttpServletRequest
→ Servlet filter infrastructure
→ DispatcherServlet
→ RequestMappingHandlerMapping
→ HandlerExecutionChain
→ RequestMappingHandlerAdapter
→ HandlerMethodArgumentResolver chain
→ conversion / WebDataBinder / validation
→ CatalogController
→ HandlerMethodReturnValueHandler
→ HttpMessageConverter
→ MockHttpServletResponse
```

# Lab components

## `MvcPipelineLabApplication`

Provides the Boot application and Servlet MVC auto-configuration baseline.

## `CatalogController`

Demonstrates:

- literal and path-variable GET mappings;
- `@RequestParam` default values;
- `@ModelAttribute` binding and validation;
- a minimal JSON `@RequestBody` path to prove converter behavior;
- `ResponseEntity` status/header/body processing;
- a domain exception handled by advice.

## `TenantIdArgumentResolver`

Injects a `TenantId` method parameter from the `X-Tenant-Id` header. It proves that custom controller arguments belong to the ordered `HandlerMethodArgumentResolver` chain.

## `RequestTraceInterceptor`

Adds `X-MVC-Interceptor: applied` in `preHandle`. It provides observable evidence that handler-aware interception occurs after mapping and before controller invocation.

## `MvcLabExceptionAdvice`

Translates `CatalogItemNotFoundException` into a stable 404 JSON response and `Cache-Control: no-store`.

# Executable tests

| Test evidence | Expected result |
|---|---|
| mapping + custom resolver + interceptor + body writer | 200 JSON with handler/interceptor headers |
| default request parameter | `detail=summary` |
| missing tenant header | 400 before controller completion |
| path-variable conversion failure | 400 |
| valid model-attribute binding | typed query object and 200 JSON |
| invalid model-attribute validation | 400 |
| valid JSON request body | 201 and JSON response |
| malformed JSON | 400 |
| invalid request-body bean validation | 400 |
| controller advice | 404 stable error contract |
| unknown path | 404 |
| mapping-registry inspection | `getItem` `HandlerMethod` registered |

The Java test class currently contains 11 JUnit methods; the first row combines several pipeline assertions in one test.

# Prediction worksheet

Before running Maven, predict:

1. Which resolver owns `@CurrentTenant TenantId`?
2. Does missing `X-Tenant-Id` fail before or after controller invocation?
3. Does `id=not-a-number` fail conversion or validation?
4. Which component writes `CatalogItemResponse` as JSON?
5. Which resolver invokes the domain `@ExceptionHandler`?
6. Why does the unknown path not invoke `CatalogController`?

# Diagnostic exercises

## Exercise A — Disable the custom resolver

Comment out `addArgumentResolvers`. Predict which default resolver sees `TenantId` and what failure occurs.

## Exercise B — Return false from the interceptor

Change `preHandle` to return `false` without setting a status. Observe why short-circuiting requires explicit response ownership.

## Exercise C — Remove Jackson

Exclude or remove JSON converter support. Predict request-body and response-body failures.

## Exercise D — Add `@EnableWebMvc`

Record which Boot MVC defaults change and explain why additive `WebMvcConfigurer` customization is normally safer.

## Exercise E — Move the domain exception into a filter

Prove that `@RestControllerAdvice` is not a universal outer error boundary.

# Runtime claim policy

A local run can claim PASS only when Maven exits with code 0. The repository CI executes this module on every route commit. Documentation and source review alone do not count as runtime proof.

# Exam baseline and current delta

```text
Exam baseline
  Spring Framework 5.3
  Spring Boot 2.5.15
  javax.servlet
  Java 8 source level
  MockMvc result matchers

Current delta
  jakarta.servlet
  current path matching and error abstractions
  MockMvcTester may be available
  core DispatcherServlet strategy model remains
```

# Related material

- [[98_SOURCES/Spring MVC DispatcherServlet Sources]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Assessment]]
- [[70_PROGRESS/README]]
