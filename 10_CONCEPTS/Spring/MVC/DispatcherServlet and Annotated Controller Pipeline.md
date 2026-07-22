---
type: concept
domain: spring
subdomain: spring-mvc
route: SPRING-MVC-B01
status: published
objectives:
  - SPRING-3.1.1
  - SPRING-3.1.2
  - SPRING-3.1.3
  - SPRING-3.1.4
exam_baseline:
  - spring-framework-5.3
  - spring-boot-2.5
current_delta:
  - spring-framework-current
  - spring-boot-current
tags:
  - spring-mvc
  - dispatcher-servlet
  - controller-pipeline
  - servlet
---

# DispatcherServlet and Annotated Controller Pipeline

> [!summary]
> Spring MVC is a Servlet-stack framework organized around the front-controller pattern. `DispatcherServlet` owns the shared request algorithm, while ordered strategy components map the request, invoke the selected handler, resolve controller arguments and return values, handle failures, and render either a response body or a view.

# Route navigation

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Roadmap]]
- [[10_CONCEPTS/Spring/MVC/Spring MVC DispatcherServlet Visual Deep Dive]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Cards]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Assessment]]
- [[40_PRODUCTION_CASES/Spring/Spring MVC DispatcherServlet Production Cases]]
- [[50_LABS/Spring/SPRING-MVC-B01/README]]
- [[98_SOURCES/Spring MVC DispatcherServlet Sources]]

# 1. The mental model

A request does not jump directly from the Servlet container into a controller method.

```text
container
→ servlet filters
→ DispatcherServlet
→ HandlerMapping
→ HandlerExecutionChain
→ HandlerAdapter
→ argument resolvers / binding / conversion / validation
→ controller method
→ return-value handlers
→ body conversion or view resolution
→ exception resolvers when a stage fails
→ servlet response
```

The essential exam and production question is always:

> Which component owns the current stage, and what contract does it expose?

# 2. Spring MVC versus WebFlux

Spring MVC is built on the Servlet API and normally executes request handling on container-managed threads. Spring WebFlux uses a reactive server abstraction and a different runtime pipeline.

Do not infer WebFlux semantics from classes with similar names. This route concerns `spring-webmvc`, `DispatcherServlet`, `HttpServletRequest`, `HttpServletResponse`, `HandlerMapping`, and `HandlerAdapter`.

# 3. Front Controller pattern

A front controller centralizes cross-cutting request orchestration. Instead of every controller implementing its own routing, conversion, error handling, and rendering protocol, `DispatcherServlet` coordinates configurable strategies.

The servlet does not need controller-specific code. It asks generic collaborators:

- Which handler matches?
- Which adapter can invoke that handler?
- How should parameters be created?
- How should the return value be processed?
- Can an exception be translated?
- Should the result become bytes or a view?

# 4. Servlet container boundary

The Servlet container accepts the network request, creates Servlet request/response objects, applies the filter chain, and invokes the servlet mapped to the request path.

Spring MVC begins after the request reaches `DispatcherServlet`. Filters execute outside the MVC handler chain; interceptors execute inside it.

# 5. DispatcherServlet registration

In traditional Servlet deployment, `DispatcherServlet` can be registered in `web.xml` or through a `WebApplicationInitializer`.

Spring Boot normally registers and configures it automatically when a Servlet web application and MVC infrastructure are present. The exam baseline is Boot 2.5 with `javax.servlet`; current Spring uses `jakarta.servlet`.

# 6. WebApplicationContext

`DispatcherServlet` works with a `WebApplicationContext`. Traditional applications may have:

```text
root context
  services, repositories, infrastructure

servlet child context
  controllers, HandlerMappings, HandlerAdapters, ViewResolvers
```

A child context can see beans in its parent; the parent cannot see child-only MVC beans.

Spring Boot applications often use a simpler single application-context arrangement, but the hierarchy remains an important framework model.

# 7. DispatcherServlet strategy discovery

During initialization, the servlet discovers strategy beans from its context. Important strategy contracts include:

- `HandlerMapping`;
- `HandlerAdapter`;
- `HandlerExceptionResolver`;
- `ViewResolver`;
- `LocaleResolver` or `LocaleContextResolver`;
- `MultipartResolver`;
- `FlashMapManager`;
- `RequestToViewNameTranslator`.

Most are discovered by type. Some optional strategies use conventional bean names.

# 8. Request attributes prepared by DispatcherServlet

Before invoking application code, Spring exposes framework state such as the web application context, locale information, flash maps, and theme-related state through request attributes.

These attributes support downstream components without forcing controllers to locate framework infrastructure manually.

# 9. Multipart preprocessing

When a `MultipartResolver` is configured, `DispatcherServlet` checks whether the request is multipart. A matching request is wrapped so later argument resolvers can expose files and parts.

Multipart parsing occurs before handler invocation. A multipart failure can therefore happen before a controller has been selected.

# 10. HandlerMapping contract

A `HandlerMapping` maps the current request to a handler and may also provide interceptors. Its result is normally a `HandlerExecutionChain`.

`DispatcherServlet` asks mappings in order and uses the first non-null match.

A `HandlerMapping` does not invoke the controller.

# 11. RequestMappingHandlerMapping

`RequestMappingHandlerMapping` supports annotated `@RequestMapping` methods.

At startup it scans controller beans, creates `RequestMappingInfo` instances, and registers mappings from request conditions to `HandlerMethod` objects.

At request time it compares candidate mappings and selects the best match.

# 12. HandlerMethod

A `HandlerMethod` is framework metadata describing the selected bean and Java method. It is not the HTTP response and not the method result.

It carries information used later for:

- annotations;
- parameter types;
- return type;
- bean resolution;
- method invocation;
- exception-handler discovery.

# 13. RequestMappingInfo conditions

A mapping can constrain more than the path:

- HTTP method;
- request parameters;
- headers;
- consumed media types;
- produced media types;
- custom request conditions.

A path match alone is insufficient when another declared condition fails.

# 14. Best-match selection

If multiple mappings match, Spring compares their specificity. More specific path and condition combinations normally win.

Ambiguous mappings are configuration defects. They should fail clearly rather than depend on registration order.

# 15. HandlerExecutionChain

The execution chain contains:

```text
selected handler
+ zero or more HandlerInterceptor instances
```

The chain preserves interceptor order and ensures completion callbacks can be invoked consistently.

# 16. Filter versus interceptor

A Servlet `Filter` surrounds the servlet and can run for non-Spring requests. A `HandlerInterceptor` runs after Spring has selected a handler.

Use filters for Servlet-level concerns such as request wrapping, security-chain integration, or generic correlation. Use interceptors when handler metadata matters.

# 17. Interceptor phases

A `HandlerInterceptor` can participate through:

- `preHandle` before handler invocation;
- `postHandle` after successful handler invocation but before view rendering;
- `afterCompletion` after request completion, including many failure paths.

If `preHandle` returns `false`, the chain stops. The interceptor is then responsible for any response it intends to return.

# 18. HandlerAdapter contract

`HandlerAdapter` shields `DispatcherServlet` from handler-specific invocation details.

The servlet finds the first adapter that supports the chosen handler. For annotated controller methods the important implementation is `RequestMappingHandlerAdapter`.

`HandlerMapping` chooses; `HandlerAdapter` invokes.

# 19. Why an adapter is necessary

Spring MVC supports different handler styles. A handler can be an annotated method, a legacy controller, an `HttpRequestHandler`, or another supported abstraction.

The adapter pattern lets the central servlet remain stable while invocation mechanisms vary.

# 20. RequestMappingHandlerAdapter pipeline

For an annotated method, `RequestMappingHandlerAdapter` coordinates:

1. model initialization;
2. `@InitBinder` methods;
3. argument resolution;
4. data binding and validation;
5. reflective method invocation;
6. return-value handling;
7. async state when applicable.

# 21. Model initialization

Before the selected request method runs, Spring can invoke `@ModelAttribute` methods declared in the controller or applicable `@ControllerAdvice` classes.

These methods populate the model and may depend on supported request arguments.

# 22. InitBinder lifecycle

`@InitBinder` methods customize `WebDataBinder` instances for a controller or advice scope.

Typical uses:

- allowed or disallowed fields;
- custom editors;
- validators;
- formatter/conversion customization.

`@InitBinder` does not globally replace the application `ConversionService` unless explicitly configured to do so.

# 23. HandlerMethodArgumentResolver

Each controller parameter is delegated to an ordered list of `HandlerMethodArgumentResolver` implementations.

A resolver answers two questions:

```text
supportsParameter(parameter)?
resolveArgument(parameter, request, binderFactory, model)
```

The first supporting resolver owns the parameter.

# 24. Common argument-resolution families

Examples include:

- `@PathVariable` from URI variables;
- `@RequestParam` from Servlet request parameters;
- `@RequestHeader` and `@CookieValue`;
- `@ModelAttribute` through creation, binding, and validation;
- `@RequestBody` through `HttpMessageConverter`;
- `HttpEntity` and `RequestEntity`;
- `Model`, `Principal`, locale, session, request, and response objects;
- custom application-specific parameters.

# 25. Default argument rules

For unmatched parameters, simple types can be treated like request parameters and complex types like model attributes.

Relying on implicit rules can reduce clarity. Explicit annotations make request contracts and review reasoning easier.

# 26. ConversionService

Request data begins mainly as strings. Type conversion turns values into target Java types such as numbers, enums, dates, and domain value objects.

Conversion is distinct from validation:

```text
conversion asks: can this value become the target type?
validation asks: is the converted value acceptable?
```

# 27. WebDataBinder

`WebDataBinder` binds request parameters onto an object graph and coordinates conversion and validation.

It is commonly used for `@ModelAttribute` parameters. Its security boundary matters: writable fields should reflect an intentional web-binding model, not an unrestricted persistence entity.

# 28. BindingResult ordering

A `BindingResult` or `Errors` parameter must immediately follow the model attribute or validated argument whose errors it represents.

If it is placed elsewhere, it does not automatically capture those errors.

# 29. ModelAttribute binding

For `@ModelAttribute`, Spring obtains or creates an object, binds request parameters, converts values, invokes validation when requested, and exposes the object in the model.

This is natural for form and query-object workflows.

# 30. RequestBody conversion

For `@RequestBody`, Spring reads the HTTP body through an `HttpMessageConverter` selected from the request `Content-Type` and target Java type.

Binding form parameters and converting a request body are different pipelines.

# 31. HttpMessageConverter selection

A converter participates when it can read or write the Java type and media type.

Common examples include:

- byte-array conversion;
- string conversion;
- form conversion;
- Jackson JSON conversion.

A converter mismatch often appears as unsupported media type, unreadable body, or unacceptable representation.

# 32. Validation

Validation can apply after binding or body conversion. Typical triggers include `@Valid` or `@Validated`.

Failure handling depends on the argument type and whether an adjacent `BindingResult` captures errors. Otherwise Spring usually raises an exception handled by the exception-resolution chain.

# 33. Controller method invocation

After all parameters are resolved, the adapter invokes the Java method. The controller should focus on application orchestration and HTTP semantics, not reimplementing mapping or conversion infrastructure.

A method can still fail because application code throws or a return value cannot be processed.

# 34. HandlerMethodReturnValueHandler

The raw Java return value is passed through an ordered list of `HandlerMethodReturnValueHandler` implementations.

Examples process:

- `ModelAndView`;
- view-name `String` values;
- model attributes;
- `@ResponseBody` values;
- `ResponseEntity` and `HttpEntity`;
- `void` results;
- asynchronous return types.

# 35. ResponseBody path

`@ResponseBody` or `@RestController` instructs Spring to write the return value to the HTTP response through message converters.

A returned `String` from a response-body method is response content. A returned `String` from a normal `@Controller` method is usually a logical view name.

# 36. ResponseEntity path

`ResponseEntity` carries status, headers, and body. The body is still written through an `HttpMessageConverter` when present.

It does not bypass the return-value-handler pipeline.

# 37. View-name path

For an HTML controller, a logical view name is resolved through configured `ViewResolver` instances. The selected `View` renders the model into the response.

A resolver may return `null`, allowing another resolver to try. Resolvers therefore have meaningful order.

# 38. ModelAndView

`ModelAndView` explicitly combines model data and view information. It represents an MVC rendering result, not a serialized REST body.

Annotated methods can build the same outcome through a `Model` argument and a returned view-name string.

# 39. Content negotiation

Content negotiation determines acceptable and producible media types. It can affect mapping selection, converter selection, and view selection.

Remember the direction:

```text
Content-Type describes the request body or response body being sent.
Accept describes representations the client can receive.
```

# 40. HandlerExceptionResolver chain

Exceptions from mapping, argument resolution, invocation, or rendering can be offered to ordered `HandlerExceptionResolver` instances.

Important default families include:

- `ExceptionHandlerExceptionResolver` for `@ExceptionHandler` and `@ControllerAdvice`;
- `ResponseStatusExceptionResolver` for response-status semantics;
- `DefaultHandlerExceptionResolver` for standard MVC exceptions.

The first resolver that successfully handles the exception ends the chain.

# 41. ControllerAdvice

`@ControllerAdvice` centralizes applicable model attributes, binder methods, and exception handlers across controllers.

It is not a Servlet filter and does not wrap every failure outside MVC. Failures before `DispatcherServlet` or outside supported resolver paths require other mechanisms.

# 42. No-handler and 404 boundaries

If no mapping returns a handler, Spring produces the configured no-handler behavior. This is distinct from a selected controller throwing a domain "not found" exception.

Operational diagnosis should distinguish:

```text
no mapping
mapping found but method condition failed
argument resolution failed
controller returned domain not-found
resource handler returned not-found
```

# 43. Async request processing

Spring MVC can release the container thread for supported asynchronous return types and later dispatch the request again for completion.

This is still Servlet-stack MVC. It is not equivalent to a fully reactive WebFlux pipeline.

# 44. Filters, interceptors, advice, and resolvers

Choose by lifecycle position:

| Concern | Typical extension point |
|---|---|
| Raw request/response wrapping | Servlet Filter |
| Handler-aware pre/post logic | HandlerInterceptor |
| Custom controller parameter | HandlerMethodArgumentResolver |
| Custom return contract | HandlerMethodReturnValueHandler |
| Controller binding rules | `@InitBinder` / WebBindingInitializer |
| Controller exception policy | `@ExceptionHandler` / ControllerAdvice |
| Global MVC exception strategy | HandlerExceptionResolver |

# 45. Spring Boot MVC application

With `spring-boot-starter-web`, Boot normally supplies:

- embedded Servlet container dependencies;
- MVC auto-configuration;
- `DispatcherServlet` registration;
- default message converters;
- JSON support when Jackson is present;
- static-resource and error infrastructure;
- MVC configuration extension points.

Adding `@EnableWebMvc` takes stronger control and can disable parts of Boot's MVC auto-configuration. Use it only when that ownership transfer is intentional.

# 46. Deployment models

The exam baseline recognizes both:

```text
executable JAR
  embedded Servlet container

WAR deployment
  external compatible Servlet container
  SpringBootServletInitializer when required
```

Controller code is usually independent of the packaging choice, but bootstrap and container ownership differ.

# 47. Exam baseline versus current delta

## Spring Framework 5.3 / Boot 2.5

- `javax.servlet` namespace;
- `PathPatternParser` available but legacy path matching remains a relevant baseline distinction;
- Boot 2.5 MVC auto-configuration and embedded container model;
- `MockMvc` with Hamcrest-style result matchers;
- Java 8 compatible route lab.

## Current Spring

- `jakarta.servlet` namespace;
- current path-matching defaults and Servlet compatibility;
- newer testing APIs such as `MockMvcTester` may be available;
- `ProblemDetail` and newer error-response abstractions exist;
- the core DispatcherServlet strategy architecture remains recognizable.

Do not answer a 5.3-era certification question only from current convenience APIs.

# 48. Diagnostic sequence

For a failing MVC request, inspect in this order:

1. Did the request reach the expected application and filter chain?
2. Which servlet mapping handled it?
3. Which `HandlerMapping` candidates were considered?
4. Which mapping condition failed or won?
5. Which interceptor stopped or modified the request?
6. Which `HandlerAdapter` supports the handler?
7. Which argument resolver owns each parameter?
8. Did conversion fail before validation?
9. Which return-value handler was selected?
10. Which message converter or view resolver was selected?
11. Which exception resolver handled the failure?
12. What status, headers, and body were finally committed?

# 49. Production invariants

A robust MVC application preserves these invariants:

- mappings are explicit and non-ambiguous;
- controllers do not bind persistence entities indiscriminately;
- converter and validation errors are observable;
- interceptors do not silently swallow requests;
- global advice has controlled applicability;
- response media types match client contracts;
- exception translation preserves stable API semantics;
- MVC tests execute the real DispatcherServlet path.

# 50. Route boundary

This B01 route proves the controller pipeline and a simple GET endpoint. The next route owns broader REST design:

```text
SPRING-MVC-B02
REST endpoints across HTTP verbs
ResponseEntity and error contracts in depth
RestTemplate exam baseline
RestClient/WebClient current comparison
```

# Related material

- [[10_CONCEPTS/Spring/Testing/Spring TestContext and Test Slices]]
- [[10_CONCEPTS/Spring/Boot/Spring Boot Bootstrap and Auto-configuration]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Roadmap]]
- [[70_PROGRESS/README]]
