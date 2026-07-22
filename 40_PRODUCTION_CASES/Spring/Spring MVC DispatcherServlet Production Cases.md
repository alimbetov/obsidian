---
type: production-cases
domain: spring
subdomain: spring-mvc
route: SPRING-MVC-B01
status: published
case_count: 12
objectives:
  - SPRING-3.1.1
  - SPRING-3.1.2
  - SPRING-3.1.3
  - SPRING-3.1.4
tags:
  - spring-mvc
  - incidents
  - dispatcher-servlet
---

# Spring MVC DispatcherServlet Production Cases

# Case 1 — The endpoint exists, but every request returns 404

## Symptom

The controller bean is present and the path appears correct, but no request reaches the method.

## Likely boundary

`HandlerMapping` selection, not controller execution.

## Diagnostic path

1. Confirm the request reached the expected application and `DispatcherServlet` mapping.
2. Inspect context path and servlet path.
3. Inspect controller discovery and package scanning.
4. Inspect path, HTTP method, params, headers, `consumes`, and `produces` conditions.
5. Inspect whether a different mapping or static-resource handler owns the path.

## Root causes

- controller outside component scan;
- wrong context/servlet path;
- method-condition mismatch;
- path-matching configuration drift;
- request handled by another servlet or application.

## Prevention

Use `MockMvc` mapping tests and log selected `HandlerMethod` evidence.

---

# Case 2 — Ambiguous mapping after a harmless refactor

## Symptom

Application startup fails or a request reports ambiguous handler methods after two controllers are reorganized.

## Mechanism

Two `RequestMappingInfo` definitions match with equal specificity.

## Wrong reaction

Rely on source-file order or bean registration order.

## Correct fix

Make the HTTP contract distinct through path, method, parameters, headers, `consumes`, or `produces` conditions. Ambiguity should be removed, not hidden.

---

# Case 3 — Interceptor returns false and clients see an empty 200

## Symptom

Unauthorized requests never reach the controller, but the response has no stable error body and may retain an incorrect status.

## Mechanism

`preHandle` returned `false`, which stops the chain. Spring does not synthesize a domain error response automatically.

## Correct fix

The interceptor must set status/body deliberately, or the concern should move to an appropriate security/filter exception mechanism.

## Production lesson

Short-circuiting is response ownership.

---

# Case 4 — `@ControllerAdvice` does not catch authentication failure

## Symptom

Controller exceptions are translated correctly, but exceptions thrown by an upstream security filter bypass the advice.

## Mechanism

`@ControllerAdvice` participates in MVC exception resolution after the request enters the `DispatcherServlet` pipeline. A failure in an earlier filter belongs to the filter/security error path.

## Correct fix

Use security entry points, access-denied handlers, or a filter-level error strategy for failures outside MVC.

---

# Case 5 — JSON request returns 415

## Symptom

The handler path and method are correct, but a JSON request is rejected before controller invocation.

## Diagnostic path

1. Inspect request `Content-Type`.
2. Inspect mapping `consumes`.
3. Inspect target `@RequestBody` type.
4. Inspect registered `HttpMessageConverter` instances.
5. Confirm Jackson or another required codec is present.

## Common root causes

- missing or wrong `Content-Type`;
- unsupported vendor media type;
- converter removed by custom MVC configuration;
- request body sent as form data instead of JSON.

---

# Case 6 — Successful controller result becomes 406

## Symptom

The controller method executes and returns a valid object, but the client receives HTTP 406.

## Mechanism

No response representation is compatible with the request `Accept` header, mapping `produces`, return type, and available writers.

## Correct fix

Align the response contract and converter set. Do not change request `Content-Type` as a substitute for fixing response negotiation.

---

# Case 7 — Binding a JPA entity exposes administrative fields

## Symptom

A browser form can unexpectedly update fields such as `role`, `status`, or ownership identifiers.

## Mechanism

`WebDataBinder` applies request parameters to writable object properties. Binding a persistence entity creates a mass-assignment boundary.

## Correct fix

Bind a purpose-specific web DTO and map explicitly to the domain model. Restrict allowed fields when necessary.

## Production lesson

Data binding is a security mechanism, not only a convenience API.

---

# Case 8 — Validation never runs because conversion fails first

## Symptom

A request with `limit=abc` does not produce the expected `@Min` validation message.

## Mechanism

The string cannot be converted to an integer, so validation never receives a typed value.

## Correct fix

Handle conversion failures and validation failures as distinct error categories with distinct diagnostics.

---

# Case 9 — BindingResult is declared in the wrong position

## Symptom

The developer expects controller code to inspect validation errors, but Spring throws an exception instead.

## Mechanism

`BindingResult` must immediately follow the model attribute or validated argument it describes.

## Correct fix

Move `BindingResult` next to the intended argument and add a focused MVC test.

---

# Case 10 — Custom argument resolver is never called

## Symptom

A custom tenant or identity parameter remains unresolved even though a resolver bean exists.

## Mechanism

Possible causes include:

- resolver not registered through MVC configuration;
- `supportsParameter` returns false;
- another earlier resolver claims the parameter;
- `@EnableWebMvc` or custom configuration replaced expected Boot registration.

## Correct fix

Verify resolver registration and ordering, then test the actual `DispatcherServlet` path rather than calling the controller directly.

---

# Case 11 — Adding `@EnableWebMvc` removes Boot behavior

## Symptom

After adding `@EnableWebMvc`, JSON conversion, static resources, formatters, or other Boot defaults change unexpectedly.

## Mechanism

The annotation signals stronger application ownership of MVC configuration and can cause Boot MVC auto-configuration to back off.

## Correct fix

Prefer implementing `WebMvcConfigurer` for additive customization. Use `@EnableWebMvc` only when full control is intentional and tested.

---

# Case 12 — Standalone controller test passes, production mapping fails

## Symptom

`standaloneSetup` tests pass, but the deployed application uses different converters, advice, interceptors, or mapping configuration.

## Mechanism

Standalone MockMvc creates programmatic MVC infrastructure around selected controllers; it does not automatically prove the application's real `WebApplicationContext` configuration.

## Correct fix

Keep focused standalone tests where useful, but add `webAppContextSetup`, `@WebMvcTest`, or `@SpringBootTest + @AutoConfigureMockMvc` evidence for configuration-sensitive behavior.

# Incident checklist

```text
[ ] request reached expected application
[ ] filter chain completed or intentionally stopped
[ ] DispatcherServlet mapping is correct
[ ] HandlerMapping selected expected HandlerMethod
[ ] mapping conditions all matched
[ ] interceptors completed in expected order
[ ] HandlerAdapter supports selected handler
[ ] argument resolver ownership is known
[ ] conversion and validation are distinguished
[ ] return-value handler is known
[ ] converter or ViewResolver is known
[ ] exception resolver ownership is known
[ ] final status, headers and body are asserted
```

# Related material

- [[10_CONCEPTS/Spring/MVC/DispatcherServlet and Annotated Controller Pipeline]]
- [[10_CONCEPTS/Spring/MVC/Spring MVC DispatcherServlet Visual Deep Dive]]
- [[50_LABS/Spring/SPRING-MVC-B01/README]]
