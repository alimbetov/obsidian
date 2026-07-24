---
type: concept
domain: spring
subdomain: spring-mvc
route: SPRING-MVC-B02
status: published
objectives:
  - SPRING-3.2.1
  - SPRING-3.2.2
exam_baseline:
  - spring-framework-5.3
  - spring-boot-2.5
current_delta:
  - spring-framework-current
prerequisites:
  - "[[DispatcherServlet and Annotated Controller Pipeline]]"
related:
  - "[[Spring MVC REST Visual Deep Dive]]"
  - "[[Spring MVC REST and RestTemplate Sources]]"
tags:
  - spring-mvc
  - rest
  - response-entity
  - rest-template
---

# REST Endpoints, ResponseEntity and RestTemplate

> [!summary]
> `SPRING-MVC-B02` extends the DispatcherServlet pipeline into REST design. It covers how HTTP verbs map to controller methods, how `ResponseEntity` controls status/headers/body, how content negotiation selects message converters, how error contracts are expressed, and how `RestTemplate` performs client-side HTTP operations in the Spring 5.3 / Boot 2.5 exam baseline.

# Learning outcomes

After completing this route, the learner can:

1. Design a REST controller with GET, POST, PUT, PATCH and DELETE mappings.
2. Explain when to use `@RequestBody`, `@PathVariable`, `@RequestParam` and `@RequestHeader`.
3. Choose correct status codes for create, read, update, delete and validation failures.
4. Build `ResponseEntity` with status, headers and body deliberately.
5. Explain content negotiation and message-converter selection.
6. Distinguish 400, 404, 405, 406 and 415 from the MVC pipeline.
7. Implement `@ControllerAdvice` REST error responses.
8. Use `RestTemplate` methods and `exchange` with `HttpEntity`/`ResponseEntity`.
9. Test client operations with `MockRestServiceServer`.
10. Separate Spring 5.3 exam answers from current `RestClient`/`ProblemDetail` deltas.

# 1. Route boundary

This route does not re-teach `DispatcherServlet`, `HandlerMapping`, `HandlerAdapter`, argument resolvers or return-value handlers from scratch. Those are covered in [[DispatcherServlet and Annotated Controller Pipeline]].

This route focuses on the REST contract that is built on top of that pipeline:

```text
HTTP request semantics
REST endpoint annotations
request entity/body handling
response status/header/body design
message conversion
REST error response
RestTemplate client calls
```

# 2. REST controller model

`@RestController` is a composed annotation that combines controller semantics with response-body semantics.

Practical meaning:

```text
controller return values are normally written to the response body
rather than interpreted as logical view names
```

A typical resource controller groups operations by resource path:

```java
@RestController
@RequestMapping(path = "/catalog", produces = MediaType.APPLICATION_JSON_VALUE)
class CatalogController {

    @GetMapping("/{id}")
    CatalogItemDto get(@PathVariable Long id) { ... }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CatalogItemDto> create(@Valid @RequestBody CreateItemRequest request) { ... }
}
```

# 3. HTTP verb semantics

| Verb | Typical role | Body in request | Response pattern |
|---|---|---|---|
| `GET` | read resource or collection | no body in normal design | `200 OK`, body |
| `POST` | create or command | request body common | `201 Created` + `Location` or command result |
| `PUT` | replace resource | full representation | `200 OK` or `204 No Content` |
| `PATCH` | partial update | patch/partial body | `200 OK` or `204 No Content` |
| `DELETE` | delete resource | usually no body | `204 No Content` |

Exam trap: HTTP verbs are mapping conditions, not method names. `@PostMapping` narrows a request mapping to POST.

# 4. Mapping annotations

Shortcut annotations:

```text
@GetMapping
@PostMapping
@PutMapping
@PatchMapping
@DeleteMapping
```

They are composed variants of `@RequestMapping` with a fixed HTTP method condition.

You can still express the same thing with:

```java
@RequestMapping(path = "/{id}", method = RequestMethod.GET)
```

Use shortcuts for readability; understand the underlying method condition for exam questions.

# 5. Request input sources

| Annotation | Source | Common failure |
|---|---|---|
| `@PathVariable` | URI template variable | missing or conversion failure |
| `@RequestParam` | query/form parameter | required parameter absent, type mismatch |
| `@RequestHeader` | HTTP header | missing required header |
| `@RequestBody` | request body converted by `HttpMessageConverter` | 400 invalid body, 415 unsupported content type |

# 6. `@RequestBody` path

`@RequestBody` delegates the raw body to `HttpMessageConverter` selection.

Selection uses evidence such as:

```text
Content-Type
controller parameter type
registered converters
converter canRead/canWrite checks
```

If the request body is JSON and Jackson is available, the Jackson converter can deserialize it to a DTO.

# 7. Validation boundary

Validation normally happens after deserialization and binding:

```text
HTTP body bytes
→ message converter creates Java DTO
→ validation checks constraints
→ controller invoked only if validation passes or BindingResult captures errors
```

For a REST controller without adjacent `BindingResult`, invalid `@Valid @RequestBody` typically becomes a validation exception handled by the MVC exception-resolver chain.

# 8. `ResponseEntity`

`ResponseEntity<T>` represents the full HTTP response:

```text
status
headers
body
```

Examples:

```java
return ResponseEntity.ok(dto);
```

```java
return ResponseEntity
        .created(URI.create("/catalog/42"))
        .body(dto);
```

```java
return ResponseEntity.noContent().build();
```

Use `ResponseEntity` when the method needs explicit status or headers. If only a JSON body and default `200 OK` are required, returning the DTO can be enough.

# 9. Status-code discipline

Typical REST status choices:

| Scenario | Status |
|---|---:|
| resource found | 200 |
| resource created | 201 |
| update succeeds with body | 200 |
| update/delete succeeds without body | 204 |
| invalid request body/validation | 400 |
| authentication missing | 401 |
| authenticated but forbidden | 403 |
| resource not found | 404 |
| unsupported HTTP method for mapping | 405 |
| not acceptable for requested `Accept` | 406 |
| unsupported request `Content-Type` | 415 |

Exam trap: `404` means no matching resource or application-level not-found. It is not the same as malformed JSON or validation failure.

# 10. Headers

Common response headers:

```text
Location
Content-Type
Cache-Control
ETag
Last-Modified
```

`ResponseEntity` makes header construction explicit:

```java
return ResponseEntity
        .ok()
        .eTag("\"v1\"")
        .cacheControl(CacheControl.noCache())
        .body(dto);
```

# 11. Content negotiation

Content negotiation determines the response media type.

Inputs include:

```text
Accept header
mapping produces condition
registered message converters
return type
```

`produces = application/json` narrows the mapping and response type. If the client requests only an unsupported `Accept`, MVC can return 406.

# 12. Consumes condition

`consumes = application/json` narrows a mapping to requests with the expected `Content-Type`.

If a client posts XML/text to a JSON-only endpoint, MVC can respond with 415 Unsupported Media Type before the controller method body runs.

# 13. Message converters

Server-side converters are used for:

```text
reading @RequestBody
writing @ResponseBody / @RestController return values
writing ResponseEntity body
```

REST client-side converters are used by `RestTemplate` for request and response bodies.

# 14. REST error handling

A REST error contract should be deliberate:

```json
{
  "code": "CATALOG_NOT_FOUND",
  "message": "Catalog item was not found"
}
```

Common Spring mechanisms:

```text
@ResponseStatus
ResponseStatusException
@ExceptionHandler
@ControllerAdvice / @RestControllerAdvice
ResponseEntity from exception handler
```

`@RestControllerAdvice` combines advice with response-body semantics for exception handler return values.

# 15. `@ResponseStatus` versus `ResponseEntity`

`@ResponseStatus` attaches a fixed status to an exception or handler method.

`ResponseEntity` is better when the status, headers or body must be computed dynamically.

Exam trap: annotating an exception with `@ResponseStatus` does not create a detailed JSON error body by itself.

# 16. `RestTemplate` role

`RestTemplate` is a synchronous blocking HTTP client used heavily in the Spring 5.3/Boot 2.5 exam baseline.

It uses:

```text
URI template expansion
HttpEntity for request headers/body
HttpMessageConverter for bodies
ResponseErrorHandler for error status handling
ResponseExtractor for response conversion
```

Current production code may prefer `RestClient` or `WebClient`, but exam questions still require `RestTemplate` fluency.

# 17. Common `RestTemplate` methods

| Method | Purpose |
|---|---|
| `getForObject` | GET and return converted body |
| `getForEntity` | GET and return status/headers/body |
| `postForObject` | POST and return converted body |
| `postForEntity` | POST and return `ResponseEntity` |
| `put` | PUT with no response body return |
| `delete` | DELETE with no response body return |
| `exchange` | arbitrary method, headers/body, typed response |
| `execute` | low-level request/response callbacks |

# 18. `exchange`

Use `exchange` when you need explicit method, headers or `HttpEntity`:

```java
HttpHeaders headers = new HttpHeaders();
headers.setAccept(List.of(MediaType.APPLICATION_JSON));
HttpEntity<Void> request = new HttpEntity<>(headers);

ResponseEntity<CatalogItemDto> response = restTemplate.exchange(
        "/catalog/{id}",
        HttpMethod.GET,
        request,
        CatalogItemDto.class,
        id
);
```

# 19. `HttpEntity` and `RequestEntity`

`HttpEntity<T>` carries headers and body.

`RequestEntity<T>` extends the idea with an HTTP method and URI.

`ResponseEntity<T>` carries status, headers and body for responses.

# 20. Error handling in `RestTemplate`

By default, many non-2xx responses are handled by the configured `ResponseErrorHandler` and can throw exceptions such as client/server HTTP status exceptions.

Use `exchange` plus a custom error handler or catch status exceptions when the caller needs domain-specific handling.

# 21. `RestTemplateBuilder`

Spring Boot provides `RestTemplateBuilder` for consistent construction:

```text
root URI
timeouts
message converters
interceptors
error handler
basic authentication
customizers
```

It is preferable to scattering direct `new RestTemplate()` construction across services.

# 22. Server testing boundary

`MockMvc` tests the MVC server pipeline without a live Servlet container:

```text
request mapping
body conversion
validation
controller advice
status/headers/body assertions
```

This route uses `MockMvc` for server-side REST proof.

# 23. Client testing boundary

`MockRestServiceServer` verifies `RestTemplate` behavior without a real remote HTTP server:

```text
expected request method
expected URI
expected headers/body
mocked response status/body
error handling
```

# 24. Version boundary

Spring 5.3 / Boot 2.5 exam baseline:

```text
RestTemplate is central
javax servlet ecosystem
custom error DTOs and ResponseEntity patterns
```

Current production delta:

```text
RestClient / WebClient often preferred
ProblemDetail and ErrorResponse abstractions available
jakarta servlet ecosystem
```

Do not answer a Spring 5.3 exam question as if `RestClient` or `ProblemDetail` were the baseline.

# 25. Interview explanation

> A Spring REST endpoint is still invoked through the MVC pipeline. Mapping annotations add path, verb, consumes and produces conditions. `@RequestBody` uses `HttpMessageConverter` to deserialize the body; validation then runs before the controller body. The return value is handled by a return-value handler and usually written by another message converter. `ResponseEntity` gives explicit control over status, headers and body. On the client side, `RestTemplate` uses the same broad concepts—request entities, message converters, status handling and response extraction—but from the caller perspective.

# 26. Exam traps

```text
@RestController is not the same as @Controller returning a view name.
@PathVariable is not read from the query string.
@RequestBody needs a converter compatible with Content-Type and target type.
produces affects response negotiation; consumes affects request body media type.
ResponseEntity controls status/headers/body; plain DTO return usually means 200 with body.
201 Created should normally include Location for a newly created resource.
RestTemplate.getForObject returns a body, not ResponseEntity.
RestTemplate.exchange is the flexible method for method/header/entity control.
Current RestClient/ProblemDetail are production deltas, not Spring 5.3 baseline answers.
```

# 27. Exercises

1. Design GET/POST/PUT/PATCH/DELETE endpoints for a simple catalog resource.
2. Predict status codes for invalid JSON, validation failure, unsupported content type and unacceptable Accept header.
3. Convert a plain DTO return method into a `ResponseEntity` method with `Location` and `ETag`.
4. Implement `@RestControllerAdvice` for not-found and validation errors.
5. Write `MockMvc` tests for status, headers and JSON body.
6. Write `MockRestServiceServer` tests for `RestTemplate.exchange` and `postForEntity`.

# Route navigation

- **Roadmap:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Roadmap]]
- **Visual:** [[10_CONCEPTS/Spring/MVC/Spring MVC REST Visual Deep Dive]]
- **Cards:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Cards]]
- **Assessment:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Assessment]]
- **Cases:** [[40_PRODUCTION_CASES/Spring/Spring MVC REST Production Cases]]
- **Lab:** [[50_LABS/Spring/SPRING-MVC-B02/README]]
- **Sources:** [[98_SOURCES/Spring MVC REST and RestTemplate Sources]]
- **Previous:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Roadmap]]
