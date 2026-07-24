---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain: spring-mvc
batch_id: SPRING-MVC-B02
status: published
normalization_status: complete
card_count: 25
first_card: SPRING-MVC-B02-C001
last_card: SPRING-MVC-B02-C025
objectives:
  - SPRING-3.2.1
  - SPRING-3.2.2
exam_baseline:
  - spring-framework-5.3
  - spring-boot-2.5
current_delta:
  - spring-framework-current
  - spring-boot-current
tags:
  - spring-mvc
  - rest
  - response-entity
  - rest-template
---

# SPRING-MVC-B02 — REST Verbs, ResponseEntity and RestTemplate Cards

## Route navigation

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Roadmap]]
- [[10_CONCEPTS/Spring/MVC/REST Endpoints ResponseEntity and RestTemplate]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Drills]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Assessment]]
- [[40_PRODUCTION_CASES/Spring/Spring MVC REST Contract Production Cases]]
- [[50_LABS/Spring/SPRING-MVC-B02/README]]
- [[98_SOURCES/Spring MVC REST and RestTemplate Sources]]

---

## SPRING-MVC-B02-C001 — What does `@RestController` add to `@Controller`?

### Russian Translation

Что `@RestController` добавляет к `@Controller`?

> [!answer]- Answer
> Class-level response-body semantics through `@ResponseBody`, so handler return values are written through message converters instead of being treated as view names.

### Explanation

`@RestController` is a composed annotation: component/controller detection plus response-body handling.

### Exam Trap

It does not automatically make every method return `ResponseEntity`.

---

## SPRING-MVC-B02-C002 — Why should endpoint methods normally use method-specific mapping annotations?

### Russian Translation

Почему endpoint-методы обычно должны использовать аннотации mapping для конкретного HTTP-метода?

> [!answer]- Answer
> They make the accepted HTTP method an explicit part of the contract and avoid accidentally matching every method.

### Explanation

`@GetMapping`, `@PostMapping`, `@PutMapping`, `@PatchMapping`, and `@DeleteMapping` are composed `@RequestMapping` variants.

### Exam Trap

A path-only `@RequestMapping` is not implicitly GET-only.

---

## SPRING-MVC-B02-C003 — Which HTTP methods are safe?

### Russian Translation

Какие HTTP-методы являются safe?

> [!answer]- Answer
> GET, HEAD, and OPTIONS are defined as safe because their intended semantics are read-only.

### Explanation

Safety concerns intended semantics, not whether logs, metrics, or caches change internally.

### Exam Trap

PUT and DELETE are idempotent but not safe.

---

## SPRING-MVC-B02-C004 — What does idempotent mean for an HTTP method?

### Russian Translation

Что означает idempotent для HTTP-метода?

> [!answer]- Answer
> Repeating the same intended request has the same target-state effect as performing it once.

### Explanation

The status or response body may differ between repetitions while the state effect remains equivalent.

### Exam Trap

Idempotent does not mean every implementation automatically deduplicates all side effects.

---

## SPRING-MVC-B02-C005 — What is the normal semantic difference between POST and PUT?

### Russian Translation

В чем обычная семантическая разница между POST и PUT?

> [!answer]- Answer
> POST submits to a resource or collection for server-defined processing, while PUT creates or replaces the state of a resource at a known target URI.

### Explanation

POST is not idempotent by default; PUT is defined as idempotent.

### Exam Trap

Using PUT does not automatically implement replacement semantics; the application must honor the contract.

---

## SPRING-MVC-B02-C006 — Why is PATCH not automatically idempotent?

### Russian Translation

Почему PATCH не является автоматически idempotent?

> [!answer]- Answer
> A patch document may describe an operation whose effect accumulates, such as incrementing a value, so repeating it can change state again.

### Explanation

Idempotence depends on the patch format and operation design.

### Exam Trap

Partial update does not imply idempotence.

---

## SPRING-MVC-B02-C007 — Can DELETE be idempotent if the second request returns 404?

### Russian Translation

Может ли DELETE быть idempotent, если второй запрос возвращает 404?

> [!answer]- Answer
> Yes. The resource is absent after both requests, so the target-state effect is the same even though the responses differ.

### Explanation

Idempotence is about state effect, not identical response bytes.

### Exam Trap

Do not redefine idempotence as “same status every time”.

---

## SPRING-MVC-B02-C008 — When should `@PathVariable` be preferred over `@RequestParam`?

### Russian Translation

Когда следует предпочесть `@PathVariable` вместо `@RequestParam`?

> [!answer]- Answer
> Use a path variable for resource identity or hierarchy; use query parameters for filtering, sorting, pagination, and optional controls.

### Explanation

The URI structure should express stable identity, while query parameters modify selection or presentation.

### Exam Trap

There is no rule that every required value must be a path variable.

---

## SPRING-MVC-B02-C009 — What is the role of `@RequestHeader` in a REST contract?

### Russian Translation

Какова роль `@RequestHeader` в REST-контракте?

> [!answer]- Answer
> It binds protocol metadata such as conditional headers, correlation IDs, locale, or custom control headers to a controller argument.

### Explanation

Headers are part of the HTTP contract and can participate in mapping or method arguments.

### Exam Trap

Core business data should not be hidden in arbitrary headers without a clear protocol reason.

---

## SPRING-MVC-B02-C010 — How is an `@RequestBody` argument created?

### Russian Translation

Как создается аргумент `@RequestBody`?

> [!answer]- Answer
> A compatible `HttpMessageConverter` reads the request bytes according to the request media type and deserializes them into the declared Java type.

### Explanation

Body conversion is separate from query/form binding.

### Exam Trap

`@RequestBody` does not use `WebDataBinder` in the same way as `@ModelAttribute`.

---

## SPRING-MVC-B02-C011 — What is the difference between `Content-Type` and `Accept`?

### Russian Translation

В чем разница между `Content-Type` и `Accept`?

> [!answer]- Answer
> `Content-Type` describes the representation being sent; `Accept` lists response representations the client can accept.

### Explanation

They govern opposite directions of the exchange.

### Exam Trap

Changing `Content-Type` is not the correct fix for a response-side 406.

---

## SPRING-MVC-B02-C012 — What usually causes HTTP 415 in a Spring MVC REST endpoint?

### Russian Translation

Что обычно вызывает HTTP 415 в Spring MVC REST endpoint?

> [!answer]- Answer
> The request representation is unsupported because `Content-Type`, mapping `consumes`, target body type, and available message converters are incompatible.

### Explanation

The request can be rejected before controller invocation.

### Exam Trap

A correct path and method do not guarantee body conversion.

---

## SPRING-MVC-B02-C013 — What usually causes HTTP 406 after a controller returns a value?

### Russian Translation

Что обычно вызывает HTTP 406 после возврата значения контроллером?

> [!answer]- Answer
> No response representation satisfies the request `Accept` header, mapping `produces`, return type, and available message converters.

### Explanation

This is a response content-negotiation failure.

### Exam Trap

It is not the same as malformed request JSON.

---

## SPRING-MVC-B02-C014 — What three parts can `ResponseEntity<T>` control?

### Russian Translation

Какие три части может контролировать `ResponseEntity<T>`?

> [!answer]- Answer
> HTTP status, response headers, and an optional body.

### Explanation

The body is still written through an `HttpMessageConverter`.

### Exam Trap

`ResponseEntity` is not merely a wrapper around a DTO.

---

## SPRING-MVC-B02-C015 — When is returning a body object preferable to returning `ResponseEntity`?

### Russian Translation

Когда лучше вернуть объект body, а не `ResponseEntity`?

> [!answer]- Answer
> When the success contract is the default status with a body and no special headers or status decisions are required.

### Explanation

Use the simpler return type when no protocol customization is needed.

### Exam Trap

Do not use `ResponseEntity` mechanically on every endpoint.

---

## SPRING-MVC-B02-C016 — What should a successful resource-creation response normally contain?

### Russian Translation

Что обычно должен содержать успешный ответ создания ресурса?

> [!answer]- Answer
> `201 Created` and a `Location` header pointing to the created resource; a representation may also be included.

### Explanation

`ResponseEntity.created(location)` expresses this contract directly.

### Exam Trap

Returning 200 with only a generated ID loses protocol-level location information.

---

## SPRING-MVC-B02-C017 — What does `204 No Content` promise?

### Russian Translation

Что обещает `204 No Content`?

> [!answer]- Answer
> The operation succeeded and the response has no representation body.

### Explanation

Use it for successful operations where no response document is needed.

### Exam Trap

A client must not depend on a JSON body from a 204 response.

---

## SPRING-MVC-B02-C018 — What is the purpose of an ETag?

### Russian Translation

Каково назначение ETag?

> [!answer]- Answer
> It is a representation validator used for caching and conditional requests such as `If-None-Match` or `If-Match`.

### Explanation

ETags can prevent unnecessary transfer and support optimistic HTTP concurrency.

### Exam Trap

An ETag is not automatically the database primary key.

---

## SPRING-MVC-B02-C019 — What does `@RestControllerAdvice` combine?

### Russian Translation

Что объединяет `@RestControllerAdvice`?

> [!answer]- Answer
> Global controller advice with response-body semantics.

### Explanation

It is a convenient place for centralized REST exception-to-response translation.

### Exam Trap

It does not catch failures that occur before the request enters the MVC exception-resolution path.

---

## SPRING-MVC-B02-C020 — Which exception handler normally wins: local controller or global advice?

### Russian Translation

Какой exception handler обычно имеет приоритет: локальный контроллера или глобальный advice?

> [!answer]- Answer
> A matching local `@ExceptionHandler` is considered before global advice for that controller.

### Explanation

Advice ordering also matters when several global handlers can match.

### Exam Trap

A broad global handler should not be assumed to override every local handler.

---

## SPRING-MVC-B02-C021 — How do `HttpEntity`, `RequestEntity`, and `ResponseEntity` differ?

### Russian Translation

Чем отличаются `HttpEntity`, `RequestEntity` и `ResponseEntity`?

> [!answer]- Answer
> `HttpEntity` carries headers and body, `RequestEntity` adds request method and URI, and `ResponseEntity` adds response status.

### Explanation

They model different sides and completeness levels of an HTTP exchange.

### Exam Trap

`HttpEntity` does not contain a response status.

---

## SPRING-MVC-B02-C022 — What is the difference between `getForObject` and `getForEntity`?

### Russian Translation

В чем разница между `getForObject` и `getForEntity`?

> [!answer]- Answer
> `getForObject` returns the converted body, while `getForEntity` returns status, headers, and body in a `ResponseEntity`.

### Explanation

Choose the entity form when headers or status are part of the client decision.

### Exam Trap

`getForObject` does not expose the full successful response contract.

---

## SPRING-MVC-B02-C023 — What are the three common POST convenience outcomes in `RestTemplate`?

### Russian Translation

Какие три распространенные формы результата POST предоставляет `RestTemplate`?

> [!answer]- Answer
> `postForObject` returns the body, `postForEntity` returns the full response, and `postForLocation` returns the `Location` URI.

### Explanation

The method should match which part of the response contract the caller needs.

### Exam Trap

`postForLocation` does not return the response body.

---

## SPRING-MVC-B02-C024 — When should `RestTemplate.exchange` be used?

### Russian Translation

Когда следует использовать `RestTemplate.exchange`?

> [!answer]- Answer
> Use it when the request needs an arbitrary method, explicit headers/body, full response access, or a generic response type.

### Explanation

`exchange` is the general-purpose operation built around `HttpEntity` or `RequestEntity`.

### Exam Trap

It is not limited to uncommon HTTP methods.

---

## SPRING-MVC-B02-C025 — Why is `ParameterizedTypeReference<List<OrderResponse>>` needed?

### Russian Translation

Зачем нужен `ParameterizedTypeReference<List<OrderResponse>>`?

> [!answer]- Answer
> It preserves the generic element type at runtime so message conversion can create `OrderResponse` elements instead of untyped maps.

### Explanation

An anonymous subclass captures the complete generic type token.

### Exam Trap

`List.class` cannot represent `List<OrderResponse>` because of type erasure.
