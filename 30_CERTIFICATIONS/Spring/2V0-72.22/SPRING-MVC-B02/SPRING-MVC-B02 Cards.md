---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain: spring-mvc
route: SPRING-MVC-B02
batch: SPRING-MVC-B02
status: published
card_count: 25
objectives:
  - SPRING-3.2.1
  - SPRING-3.2.2
exam_baseline:
  - spring-framework-5.3
  - spring-boot-2.5
tags:
  - spring-mvc
  - rest
  - response-entity
  - rest-template
  - active-recall
---

# SPRING-MVC-B02 — REST Verbs, ResponseEntity and RestTemplate Cards

## Route navigation

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Roadmap]]
- [[10_CONCEPTS/Spring/MVC/REST Endpoints ResponseEntity and RestTemplate]]
- [[10_CONCEPTS/Spring/MVC/Spring MVC REST Visual Deep Dive]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Assessment]]
- [[40_PRODUCTION_CASES/Spring/Spring MVC REST Production Cases]]
- [[50_LABS/Spring/SPRING-MVC-B02/README]]
- [[98_SOURCES/Spring MVC REST and RestTemplate Sources]]

---

## SPRING-MVC-B02-C001 — What does `@RestController` change compared with a view-oriented controller?

### Russian Translation

Что меняет `@RestController` по сравнению с controller, который возвращает view name?

> [!answer]- Answer
> `@RestController` implies response-body semantics for handler return values, so returned objects are normally written to the HTTP response body instead of being resolved as view names.

### Explanation

It combines controller semantics with `@ResponseBody` behavior for methods in the class.

### Exam Trap

Do not say that it bypasses `DispatcherServlet`; it still runs through the MVC pipeline.

---

## SPRING-MVC-B02-C002 — What does `@GetMapping` primarily add to a request mapping?

### Russian Translation

Что в первую очередь добавляет `@GetMapping` к request mapping?

> [!answer]- Answer
> It narrows the mapping to HTTP GET requests.

### Explanation

It is a composed shortcut for `@RequestMapping(method = RequestMethod.GET)` with optional path, produces and other conditions.

### Exam Trap

The Java method name is irrelevant; the HTTP method condition selects the handler.

---

## SPRING-MVC-B02-C003 — Which HTTP verb is normally used to create a resource?

### Russian Translation

Какой HTTP verb обычно используется для создания ресурса?

> [!answer]- Answer
> `POST` is commonly used to create a subordinate resource or submit a command that creates state.

### Explanation

A successful creation often returns `201 Created` and a `Location` header pointing to the new resource.

### Exam Trap

Do not always return `200 OK` after creation when the contract says a new resource was created.

---

## SPRING-MVC-B02-C004 — What is the usual difference between PUT and PATCH?

### Russian Translation

В чём обычная разница между PUT и PATCH?

> [!answer]- Answer
> `PUT` usually represents full replacement of a resource, while `PATCH` represents partial modification.

### Explanation

The exact semantics are an API contract decision, but this distinction is the normal REST design baseline.

### Exam Trap

Do not use `PATCH` as a synonym for every update operation without defining partial-update behavior.

---

## SPRING-MVC-B02-C005 — What does `@RequestBody` use to create a Java object?

### Russian Translation

Что использует `@RequestBody`, чтобы создать Java-объект из HTTP body?

> [!answer]- Answer
> It uses an `HttpMessageConverter` selected by request `Content-Type`, target type and registered converters.

### Explanation

For JSON, Jackson-based converters are commonly used when Jackson is on the classpath.

### Exam Trap

`@RequestBody` is not the same as `@RequestParam`; it reads the HTTP body, not the query string.

---

## SPRING-MVC-B02-C006 — What status is appropriate for successful DELETE with no response body?

### Russian Translation

Какой статус подходит для успешного DELETE без response body?

> [!answer]- Answer
> `204 No Content`.

### Explanation

The server successfully processed the request and deliberately returns no body.

### Exam Trap

Returning `200 OK` with an empty body is possible but is not the cleanest no-body delete contract.

---

## SPRING-MVC-B02-C007 — What does `ResponseEntity<T>` control?

### Russian Translation

Чем управляет `ResponseEntity<T>`?

> [!answer]- Answer
> HTTP status, headers and body.

### Explanation

It is useful when a handler must explicitly set status codes such as 201/204, headers such as `Location` or `ETag`, or a typed response body.

### Exam Trap

A plain DTO return usually implies a successful body response, commonly `200 OK`, not explicit status/header control.

---

## SPRING-MVC-B02-C008 — What should `ResponseEntity.created(location)` normally produce?

### Russian Translation

Что обычно создаёт `ResponseEntity.created(location)`?

> [!answer]- Answer
> A `201 Created` response with a `Location` header.

### Explanation

The body can be added with `.body(dto)` if the API returns the created representation.

### Exam Trap

Do not confuse the response body URL field with the HTTP `Location` header.

---

## SPRING-MVC-B02-C009 — What is the difference between `consumes` and `produces`?

### Russian Translation

В чём разница между `consumes` и `produces`?

> [!answer]- Answer
> `consumes` constrains the request `Content-Type`; `produces` constrains the response media type and client `Accept` negotiation.

### Explanation

A mismatch in `consumes` can lead to 415; a mismatch in `produces`/`Accept` can lead to 406.

### Exam Trap

Do not swap 406 and 415.

---

## SPRING-MVC-B02-C010 — When can Spring MVC return 415 Unsupported Media Type?

### Russian Translation

Когда Spring MVC может вернуть 415 Unsupported Media Type?

> [!answer]- Answer
> When the request `Content-Type` does not match the mapping or available message converter requirements.

### Explanation

For example, posting `text/plain` to a JSON-only `@PostMapping(consumes = application/json)` endpoint can fail before the controller method body runs.

### Exam Trap

415 is not a validation error after DTO creation; it is a media-type compatibility failure.

---

## SPRING-MVC-B02-C011 — When can Spring MVC return 406 Not Acceptable?

### Russian Translation

Когда Spring MVC может вернуть 406 Not Acceptable?

> [!answer]- Answer
> When the selected endpoint cannot produce a response compatible with the client's `Accept` header.

### Explanation

The response side uses `produces`, `Accept` and message-converter capabilities.

### Exam Trap

406 is response negotiation; 415 is request body media type negotiation.

---

## SPRING-MVC-B02-C012 — What happens when `@Valid @RequestBody` fails validation without adjacent BindingResult?

### Russian Translation

Что происходит, если `@Valid @RequestBody` не проходит validation и рядом нет BindingResult?

> [!answer]- Answer
> MVC raises a validation exception that can be handled by the exception-resolver chain, commonly through `@RestControllerAdvice`.

### Explanation

The controller method body is not normally invoked in this failure path.

### Exam Trap

Do not treat validation failure as a normal method call with a partially valid DTO unless `BindingResult` is deliberately used.

---

## SPRING-MVC-B02-C013 — What is the purpose of `@RestControllerAdvice`?

### Russian Translation

Для чего нужен `@RestControllerAdvice`?

> [!answer]- Answer
> To apply controller advice with response-body semantics, often for REST exception handling.

### Explanation

It can define `@ExceptionHandler` methods returning DTOs or `ResponseEntity` error bodies.

### Exam Trap

It handles MVC controller pipeline exceptions, not arbitrary failures in an upstream servlet filter before MVC is reached.

---

## SPRING-MVC-B02-C014 — What does `getForObject` return?

### Russian Translation

Что возвращает `RestTemplate.getForObject`?

> [!answer]- Answer
> The response body converted to the requested type.

### Explanation

It does not expose response status and headers directly.

### Exam Trap

Use `getForEntity` or `exchange` when status and headers matter.

---

## SPRING-MVC-B02-C015 — What does `getForEntity` return?

### Russian Translation

Что возвращает `RestTemplate.getForEntity`?

> [!answer]- Answer
> A `ResponseEntity<T>` containing status, headers and converted body.

### Explanation

This is useful when the client must inspect headers or status code.

### Exam Trap

Do not confuse it with `getForObject`, which returns only the body.

---

## SPRING-MVC-B02-C016 — What does `postForEntity` return?

### Russian Translation

Что возвращает `RestTemplate.postForEntity`?

> [!answer]- Answer
> A `ResponseEntity<T>` for the POST response.

### Explanation

The request object is written with message converters, and the response is converted to the specified response type.

### Exam Trap

`postForEntity` is not just fire-and-forget; it returns the response entity.

---

## SPRING-MVC-B02-C017 — When is `RestTemplate.exchange` the best choice?

### Russian Translation

Когда лучше использовать `RestTemplate.exchange`?

> [!answer]- Answer
> When the client needs explicit HTTP method, request headers/body through `HttpEntity`, URI variables and typed `ResponseEntity` handling.

### Explanation

It is the flexible general-purpose method for methods like PATCH, custom headers or generic request/response contracts.

### Exam Trap

Do not force a limited convenience method when headers or non-trivial method control are required.

---

## SPRING-MVC-B02-C018 — What does `HttpEntity<T>` represent?

### Russian Translation

Что представляет `HttpEntity<T>`?

> [!answer]- Answer
> HTTP headers plus an optional body.

### Explanation

It can be used as a request entity for `RestTemplate` or as a base concept for response entities.

### Exam Trap

`HttpEntity` itself does not include a response status; `ResponseEntity` does.

---

## SPRING-MVC-B02-C019 — What does `RestTemplateBuilder` add in Spring Boot?

### Russian Translation

Что добавляет `RestTemplateBuilder` в Spring Boot?

> [!answer]- Answer
> A configurable builder for creating `RestTemplate` instances with shared timeouts, root URI, message converters, interceptors, customizers and error handling.

### Explanation

It centralizes client configuration better than direct scattered `new RestTemplate()` calls.

### Exam Trap

The builder creates/configures a client; it is not a server-side MVC endpoint component.

---

## SPRING-MVC-B02-C020 — What handles non-2xx responses in RestTemplate by default?

### Russian Translation

Что обрабатывает non-2xx ответы в RestTemplate по умолчанию?

> [!answer]- Answer
> The configured `ResponseErrorHandler`.

### Explanation

Default behavior commonly throws HTTP status exceptions for error responses.

### Exam Trap

Do not assume every non-2xx response is returned as a normal `ResponseEntity` unless error handling is configured accordingly.

---

## SPRING-MVC-B02-C021 — What is `MockRestServiceServer` used for?

### Russian Translation

Для чего используется `MockRestServiceServer`?

> [!answer]- Answer
> To test `RestTemplate` client code by asserting expected requests and providing mock HTTP responses without a real remote server.

### Explanation

It verifies URI, method, headers/body and response handling at the client boundary.

### Exam Trap

It is not a server-side `DispatcherServlet` test; that is `MockMvc` territory.

---

## SPRING-MVC-B02-C022 — What is the difference between server-side MockMvc and client-side MockRestServiceServer?

### Russian Translation

В чём разница между server-side MockMvc и client-side MockRestServiceServer?

> [!answer]- Answer
> `MockMvc` exercises the MVC server pipeline; `MockRestServiceServer` exercises outbound `RestTemplate` client requests.

### Explanation

They test opposite sides of HTTP communication.

### Exam Trap

Do not use `MockRestServiceServer` to prove controller request mapping.

---

## SPRING-MVC-B02-C023 — Is `RestClient` the Spring 5.3 exam baseline client?

### Russian Translation

Является ли `RestClient` baseline-клиентом для экзамена Spring 5.3?

> [!answer]- Answer
> No. `RestTemplate` is the relevant exam-baseline client for Spring Framework 5.3 / Boot 2.5.

### Explanation

`RestClient` is a current production delta and should be mentioned only as a version comparison.

### Exam Trap

Do not answer a `RestTemplate` exam objective with only `RestClient`.

---

## SPRING-MVC-B02-C024 — Is `ProblemDetail` the Spring 5.3 baseline error model?

### Russian Translation

Является ли `ProblemDetail` baseline error model для Spring 5.3?

> [!answer]- Answer
> No. Spring 5.3 exam-baseline REST error bodies are commonly custom DTOs or `ResponseEntity` results from exception handlers.

### Explanation

`ProblemDetail` is part of newer Spring production APIs.

### Exam Trap

Do not import current Spring 6+ error abstractions into baseline Spring 5.3 answers.

---

## SPRING-MVC-B02-C025 — What is the full REST response path to remember?

### Russian Translation

Какой полный путь REST response нужно помнить?

> [!answer]- Answer
> Controller result → return-value handler → status/header/body decision → content negotiation → message converter → HTTP response.

### Explanation

`ResponseEntity` participates in return-value handling and supplies explicit response metadata before converter writing.

### Exam Trap

Do not think returning a DTO directly and returning `ResponseEntity` have identical status/header control.
