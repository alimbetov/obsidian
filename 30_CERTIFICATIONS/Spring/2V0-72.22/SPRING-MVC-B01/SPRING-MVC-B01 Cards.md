---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain: spring-mvc
batch_id: SPRING-MVC-B01
status: published
normalization_status: complete
card_count: 35
first_card: SPRING-MVC-B01-C001
last_card: SPRING-MVC-B01-C035
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
  - annotated-controller
---

# SPRING-MVC-B01 — DispatcherServlet and Controller Pipeline Cards

## Route navigation

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Roadmap]]
- [[10_CONCEPTS/Spring/MVC/DispatcherServlet and Annotated Controller Pipeline]]
- [[10_CONCEPTS/Spring/MVC/Spring MVC DispatcherServlet Visual Deep Dive]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Assessment]]
- [[40_PRODUCTION_CASES/Spring/Spring MVC DispatcherServlet Production Cases]]
- [[50_LABS/Spring/SPRING-MVC-B01/README]]
- [[98_SOURCES/Spring MVC DispatcherServlet Sources]]

---

## SPRING-MVC-B01-C001 — What architectural pattern does `DispatcherServlet` implement?

### Russian Translation

Какой архитектурный паттерн реализует `DispatcherServlet`?

> [!answer]- Answer
> The front-controller pattern: one central servlet coordinates request processing through configurable delegate strategies.

### Explanation

The servlet owns the shared algorithm but delegates mapping, invocation, exception handling, and rendering.

### Exam Trap

It is not a controller superclass and does not contain endpoint-specific business logic.

---

## SPRING-MVC-B01-C002 — Where does a Servlet `Filter` run relative to `DispatcherServlet`?

### Russian Translation

Где выполняется Servlet `Filter` относительно `DispatcherServlet`?

> [!answer]- Answer
> In the Servlet filter chain before and after the servlet invocation.

### Explanation

Filters belong to the container-level request pipeline and can wrap requests and responses before Spring MVC selects a handler.

### Exam Trap

A filter is not a `HandlerInterceptor` and does not require a selected Spring handler.

---

## SPRING-MVC-B01-C003 — What is the main responsibility of `HandlerMapping`?

### Russian Translation

Какова главная ответственность `HandlerMapping`?

> [!answer]- Answer
> To map the current request to a handler, usually together with interceptors in a `HandlerExecutionChain`.

### Explanation

Mappings are consulted in order and the first non-null result is used.

### Exam Trap

`HandlerMapping` selects a handler; it does not invoke the controller method.

---

## SPRING-MVC-B01-C004 — What is the main responsibility of `HandlerAdapter`?

### Russian Translation

Какова главная ответственность `HandlerAdapter`?

> [!answer]- Answer
> To invoke a selected handler through the invocation model appropriate for that handler type.

### Explanation

For annotated methods, `RequestMappingHandlerAdapter` resolves arguments, invokes the method, and processes the return value.

### Exam Trap

The adapter does not choose the request mapping; that occurred earlier.

---

## SPRING-MVC-B01-C005 — Why does `DispatcherServlet` use both `HandlerMapping` and `HandlerAdapter`?

### Russian Translation

Почему `DispatcherServlet` использует и `HandlerMapping`, и `HandlerAdapter`?

> [!answer]- Answer
> Mapping and invocation are separate extension points: one selects a handler, the other knows how to execute it.

### Explanation

This separation lets the servlet support multiple handler styles without handler-specific code.

### Exam Trap

Finding a `HandlerMethod` does not mean its Java parameters are already resolved.

---

## SPRING-MVC-B01-C006 — What does `RequestMappingHandlerMapping` register at startup?

### Russian Translation

Что `RequestMappingHandlerMapping` регистрирует при старте?

> [!answer]- Answer
> Request conditions represented by `RequestMappingInfo` mapped to controller `HandlerMethod` objects.

### Explanation

It inspects controller annotations before requests arrive and builds a mapping registry.

### Exam Trap

It does not scan the entire classpath for every request.

---

## SPRING-MVC-B01-C007 — What conditions can participate in request mapping besides the path?

### Russian Translation

Какие условия участвуют в request mapping кроме path?

> [!answer]- Answer
> HTTP method, parameters, headers, consumed media types, produced media types, and custom conditions.

### Explanation

All declared conditions must match before the mapping becomes a candidate.

### Exam Trap

A correct URL alone does not guarantee handler selection.

---

## SPRING-MVC-B01-C008 — What happens when two request mappings are equally specific?

### Russian Translation

Что происходит, если два request mappings одинаково специфичны?

> [!answer]- Answer
> Spring reports an ambiguous mapping rather than selecting one arbitrarily.

### Explanation

Ambiguity is a configuration defect because request behavior would otherwise be unstable.

### Exam Trap

Do not assume controller declaration order resolves ambiguity.

---

## SPRING-MVC-B01-C009 — What is a `HandlerMethod`?

### Russian Translation

Что такое `HandlerMethod`?

> [!answer]- Answer
> Framework metadata that identifies the target bean and Java method plus method and annotation information.

### Explanation

It is used by adapters, resolvers, advice, and diagnostics.

### Exam Trap

It is not the result returned by the controller method.

---

## SPRING-MVC-B01-C010 — What is contained in a `HandlerExecutionChain`?

### Russian Translation

Что содержится в `HandlerExecutionChain`?

> [!answer]- Answer
> The selected handler plus the ordered `HandlerInterceptor` instances that apply to it.

### Explanation

The chain coordinates pre-processing, post-processing, and completion callbacks.

### Exam Trap

Servlet filters are not stored in this chain.

---

## SPRING-MVC-B01-C011 — What does `HandlerInterceptor.preHandle` returning `false` mean?

### Russian Translation

Что означает `false`, возвращённый из `HandlerInterceptor.preHandle`?

> [!answer]- Answer
> Processing stops before the handler is invoked; the interceptor must handle any intended response itself.

### Explanation

Later interceptors and the controller method are skipped.

### Exam Trap

Returning `false` does not automatically create a standard error body.

---

## SPRING-MVC-B01-C012 — When does `postHandle` execute?

### Russian Translation

Когда выполняется `postHandle`?

> [!answer]- Answer
> After successful handler invocation and before view rendering, when the processing path supports a model and view.

### Explanation

It can adjust model data before rendering.

### Exam Trap

It is not guaranteed after every exception and is less useful when a response body has already been written.

---

## SPRING-MVC-B01-C013 — What is the purpose of `afterCompletion`?

### Russian Translation

Какова цель `afterCompletion`?

> [!answer]- Answer
> To perform completion work after the request finishes, with access to a processing exception when available.

### Explanation

It is commonly used for cleanup and final diagnostic recording.

### Exam Trap

It is not a replacement for `finally` around failures that occur before the interceptor chain is entered.

---

## SPRING-MVC-B01-C014 — How is a controller method parameter resolved?

### Russian Translation

Как разрешается параметр controller method?

> [!answer]- Answer
> Spring walks an ordered `HandlerMethodArgumentResolver` list and lets the first supporting resolver create the argument value.

### Explanation

Different resolvers own path variables, request parameters, bodies, models, principals, and custom parameters.

### Exam Trap

Reflection alone cannot construct HTTP-aware arguments.

---

## SPRING-MVC-B01-C015 — What resolves `@PathVariable`?

### Russian Translation

Что разрешает `@PathVariable`?

> [!answer]- Answer
> A method-argument resolver reads URI template variables and converts the selected value to the declared Java type.

### Explanation

The path variable exists because the winning mapping extracted it from the request path.

### Exam Trap

A path variable is not a query parameter.

---

## SPRING-MVC-B01-C016 — What resolves `@RequestParam`?

### Russian Translation

Что разрешает `@RequestParam`?

> [!answer]- Answer
> A resolver reads Servlet request parameters, applies required/default rules, and converts the value to the declared type.

### Explanation

Request parameters can come from query parameters and form-style request parameters.

### Exam Trap

JSON object fields are not Servlet request parameters.

---

## SPRING-MVC-B01-C017 — What is the difference between conversion and validation?

### Russian Translation

В чём разница между conversion и validation?

> [!answer]- Answer
> Conversion creates the target type; validation checks whether the converted object satisfies constraints.

### Explanation

A non-numeric string for an integer fails conversion before a minimum-value constraint can run.

### Exam Trap

A validation annotation cannot repair a type-conversion failure.

---

## SPRING-MVC-B01-C018 — What is `WebDataBinder` used for?

### Russian Translation

Для чего используется `WebDataBinder`?

> [!answer]- Answer
> To bind request parameters to object properties while coordinating conversion, field restrictions, and validation.

### Explanation

It is central to `@ModelAttribute` form and query-object binding.

### Exam Trap

It should not be used to expose every persistence-entity field for mass assignment.

---

## SPRING-MVC-B01-C019 — What is the purpose of `@InitBinder`?

### Russian Translation

Какова цель `@InitBinder`?

> [!answer]- Answer
> To customize controller-scoped `WebDataBinder` behavior such as allowed fields, formatters, editors, or validators.

### Explanation

Controller advice can share binder customization across selected controllers.

### Exam Trap

An `@InitBinder` method does not itself handle an HTTP endpoint.

---

## SPRING-MVC-B01-C020 — Where must a `BindingResult` parameter be declared?

### Russian Translation

Где должен быть объявлен параметр `BindingResult`?

> [!answer]- Answer
> Immediately after the model attribute or validated argument whose binding errors it should represent.

### Explanation

Position determines which argument's errors are exposed to the controller.

### Exam Trap

Placing it later after unrelated parameters does not capture the intended errors.

---

## SPRING-MVC-B01-C021 — How is `@ModelAttribute` different from `@RequestBody`?

### Russian Translation

Чем `@ModelAttribute` отличается от `@RequestBody`?

> [!answer]- Answer
> `@ModelAttribute` uses request-parameter binding; `@RequestBody` reads and converts the HTTP body through an `HttpMessageConverter`.

### Explanation

They use different resolver and data-input paths.

### Exam Trap

A JSON body is not automatically bound as form parameters.

---

## SPRING-MVC-B01-C022 — How does Spring choose an `HttpMessageConverter` for a request body?

### Russian Translation

Как Spring выбирает `HttpMessageConverter` для request body?

> [!answer]- Answer
> It chooses a converter that can read the declared Java type and the request `Content-Type`.

### Explanation

For JSON, a Jackson-based converter normally participates when Jackson is available.

### Exam Trap

The client's `Accept` header describes the desired response, not the request-body format.

---

## SPRING-MVC-B01-C023 — What is the role of `HandlerMethodReturnValueHandler`?

### Russian Translation

Какова роль `HandlerMethodReturnValueHandler`?

> [!answer]- Answer
> To interpret the controller method's return value and complete model/view, response-body, entity, async, or other response processing.

### Explanation

The raw return value is not written directly without framework interpretation.

### Exam Trap

Return-value handling is separate from argument resolution.

---

## SPRING-MVC-B01-C024 — How is a `String` return value interpreted in a regular `@Controller`?

### Russian Translation

Как интерпретируется `String`, возвращённый обычным `@Controller`?

> [!answer]- Answer
> Normally as a logical view name unless response-body semantics are explicitly active.

### Explanation

The view name is later resolved by a `ViewResolver`.

### Exam Trap

Do not assume every returned string becomes response text.

---

## SPRING-MVC-B01-C025 — How is a return value interpreted in `@RestController`?

### Russian Translation

Как интерпретируется return value в `@RestController`?

> [!answer]- Answer
> It is treated as a response body and written through an appropriate `HttpMessageConverter`.

### Explanation

`@RestController` combines controller detection with class-level response-body semantics.

### Exam Trap

Returning a string from `@RestController` does not normally select a template view.

---

## SPRING-MVC-B01-C026 — Does `ResponseEntity` bypass message conversion?

### Russian Translation

Обходит ли `ResponseEntity` message conversion?

> [!answer]- Answer
> No. It supplies status and headers, while its body is still written through a selected message converter.

### Explanation

A return-value handler unpacks the entity and coordinates the response.

### Exam Trap

`ResponseEntity` is not the raw Servlet response object.

---

## SPRING-MVC-B01-C027 — What is the responsibility of `ViewResolver`?

### Russian Translation

Какова ответственность `ViewResolver`?

> [!answer]- Answer
> To map a logical view name and locale to a concrete `View` capable of rendering the model.

### Explanation

Multiple resolvers may be ordered, and a resolver may return `null` to let the chain continue.

### Exam Trap

A view resolver does not serialize `@ResponseBody` objects.

---

## SPRING-MVC-B01-C028 — Which resolver handles `@ExceptionHandler` methods?

### Russian Translation

Какой resolver обрабатывает методы `@ExceptionHandler`?

> [!answer]- Answer
> `ExceptionHandlerExceptionResolver`.

### Explanation

It searches local controller handlers and applicable `@ControllerAdvice` handlers.

### Exam Trap

It cannot catch a failure that occurs before the request enters supported MVC exception processing, such as some filter failures.

---

## SPRING-MVC-B01-C029 — What does `ResponseStatusExceptionResolver` do?

### Russian Translation

Что делает `ResponseStatusExceptionResolver`?

> [!answer]- Answer
> It translates response-status semantics declared through supported annotations or exceptions into an HTTP status response.

### Explanation

It participates after earlier exception resolvers return unresolved.

### Exam Trap

It does not invoke arbitrary `@ExceptionHandler` methods.

---

## SPRING-MVC-B01-C030 — What does `DefaultHandlerExceptionResolver` handle?

### Russian Translation

Что обрабатывает `DefaultHandlerExceptionResolver`?

> [!answer]- Answer
> Standard Spring MVC exceptions, translating many of them to appropriate HTTP status codes.

### Explanation

Examples include unsupported methods, unreadable bodies, missing parameters, and type mismatches.

### Exam Trap

It does not define the application's domain-error contract.

---

## SPRING-MVC-B01-C031 — What is the main boundary of `@ControllerAdvice`?

### Russian Translation

Какова главная граница `@ControllerAdvice`?

> [!answer]- Answer
> It contributes controller-oriented model, binder, and exception methods to applicable MVC handlers.

### Explanation

Advice can be restricted by annotations, packages, or assignable controller types.

### Exam Trap

It is not a generic replacement for filters, security handlers, or container error pages.

---

## SPRING-MVC-B01-C032 — What commonly causes HTTP 415 in Spring MVC?

### Russian Translation

Что обычно вызывает HTTP 415 в Spring MVC?

> [!answer]- Answer
> The request media type is unsupported by the selected mapping or no message converter can read it for the target argument.

### Explanation

Inspect mapping `consumes`, request `Content-Type`, converters, and body type.

### Exam Trap

HTTP 415 is not the same as HTTP 406, which concerns an unacceptable response representation.

---

## SPRING-MVC-B01-C033 — What commonly causes HTTP 406 in Spring MVC?

### Russian Translation

Что обычно вызывает HTTP 406 в Spring MVC?

> [!answer]- Answer
> Spring cannot produce a representation compatible with the client's `Accept` header and the handler's producible media types.

### Explanation

The failure occurs on the response representation path.

### Exam Trap

Changing request `Content-Type` alone does not solve an incompatible `Accept` header.

---

## SPRING-MVC-B01-C034 — What does Spring Boot normally provide for a Servlet MVC application?

### Russian Translation

Что Spring Boot обычно предоставляет для Servlet MVC application?

> [!answer]- Answer
> DispatcherServlet registration, MVC infrastructure, message converters, embedded-container support, and sensible web defaults when the required classes are present.

### Explanation

`spring-boot-starter-web` assembles the common Servlet MVC runtime.

### Exam Trap

Adding `@EnableWebMvc` can take stronger control and disable parts of Boot MVC auto-configuration.

---

## SPRING-MVC-B01-C035 — How do executable JAR and WAR MVC deployments differ?

### Russian Translation

Чем отличаются MVC deployments в executable JAR и WAR?

> [!answer]- Answer
> An executable JAR starts an embedded container; a WAR is initialized by an external compatible Servlet container, often through `SpringBootServletInitializer`.

### Explanation

Both ultimately route requests through `DispatcherServlet`, but bootstrap and container ownership differ.

### Exam Trap

Changing packaging does not convert MVC controllers into WebFlux handlers.
