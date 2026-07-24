---
type: guided-lab
domain: spring
subdomain: spring-mvc
route: SPRING-MVC-B02
status: published
java_baseline: 8
spring_framework: 5.3
spring_boot: 2.5.15
scenario_count: 9
objectives:
  - SPRING-3.2.1
  - SPRING-3.2.2
tags:
  - lab
  - mockmvc
  - rest-template
  - mock-rest-service-server
---

# SPRING-MVC-B02 Guided Lab

> [!summary]
> Nine implementation scenarios for converting the reading route into Java 8 / Spring Boot 2.5.15 executable evidence. Each scenario defines the contract, expected result and test boundary before code is written.

# Target module

```text
50_LABS/Spring/SPRING-MVC-B02
├── pom.xml
├── src/main/java/.../OrderController.java
├── src/main/java/.../GlobalExceptionHandler.java
├── src/main/java/.../CatalogClient.java
├── src/test/java/.../OrderControllerTest.java
└── src/test/java/.../CatalogClientTest.java
```

# Scenario 1 — POST creation contract

Implement:

```java
@PostMapping
ResponseEntity<OrderResponse> create(
        @Valid @RequestBody OrderRequest request,
        UriComponentsBuilder uriBuilder) {

    OrderResponse created = service.create(request);
    URI location = uriBuilder
            .path("/api/orders/{id}")
            .buildAndExpand(created.getId())
            .toUri();

    return ResponseEntity
            .created(location)
            .eTag("\"v" + created.getVersion() + "\"")
            .body(created);
}
```

Expected MockMvc evidence:

```text
status = 201
Location = canonical order URI
ETag exists
body contains created resource
```

# Scenario 2 — Validation failure

Send a valid JSON document with a blank required field.

Expected:

```text
HttpMessageConverter succeeds
Bean Validation fails
controller method is not invoked
status = 400
```

# Scenario 3 — Unsupported media type

Send `text/plain` to an endpoint that consumes JSON.

Expected:

```text
mapping/body conversion boundary rejects request
status = 415
```

# Scenario 4 — Stable not-found schema

Create a `@RestControllerAdvice` mapping:

```java
@ExceptionHandler(OrderNotFoundException.class)
ResponseEntity<ApiError> handle(OrderNotFoundException ex) {
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ApiError("ORDER_NOT_FOUND", ex.getMessage()));
}
```

Expected:

```text
status = 404
Content-Type = application/json
body.code = ORDER_NOT_FOUND
```

# Scenario 5 — PUT replacement and creation

Test both branches:

```text
existing target URI  → 200 or 204
missing target URI   → 201 + Location
repeating same PUT   → equivalent target state
```

Do not implement PUT as an undocumented partial merge.

# Scenario 6 — PATCH partial update

Implement a narrow `/api/orders/{id}/status` patch. Verify that the status changes and unrelated fields remain unchanged.

Then redesign the patch as `{"increment": 1}` and explain why automatic retry is unsafe.

# Scenario 7 — Repeated DELETE

Call DELETE twice.

Accept either of these deliberate contracts:

```text
204 then 204
204 then 404
```

Explain why both can satisfy target-state idempotence.

# Scenario 8 — Generic RestTemplate extraction

Implement:

```java
ResponseEntity<List<ProductDto>> response = restTemplate.exchange(
        "/products",
        HttpMethod.GET,
        HttpEntity.EMPTY,
        new ParameterizedTypeReference<List<ProductDto>>() {});
```

Bind `MockRestServiceServer`, return a JSON array, and prove that the first element is `ProductDto`, not `LinkedHashMap`.

# Scenario 9 — Default downstream error handling

Return 404 from `MockRestServiceServer`.

Expected:

```text
RestTemplate default ResponseErrorHandler
→ HttpClientErrorException.NotFound
→ status, headers and error body remain inspectable
```

# Required client configuration exercise

Build a shared client:

```java
@Bean
RestTemplate catalogRestTemplate(RestTemplateBuilder builder) {
    return builder
            .rootUri("https://catalog.internal")
            .setConnectTimeout(Duration.ofSeconds(2))
            .setReadTimeout(Duration.ofSeconds(3))
            .additionalInterceptors(correlationInterceptor())
            .build();
}
```

Explain separately:

- connection establishment timeout;
- response read timeout;
- why a timeout does not prove the server did not process a POST;
- why retry requires operation semantics and idempotency controls.

# Completion gate

```text
[ ] Maven module created
[ ] nine tests implemented
[ ] Java 8 compilation confirmed
[ ] Spring Boot 2.5.15 baseline preserved
[ ] MockMvc tests pass
[ ] MockRestServiceServer tests pass
[ ] GitHub Actions runtime PASS recorded
```

# Related material

- [[10_CONCEPTS/Spring/MVC/REST Endpoints ResponseEntity and RestTemplate]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Assessment]]
- [[40_PRODUCTION_CASES/Spring/Spring MVC REST Contract Production Cases]]
- [[98_SOURCES/Spring MVC REST and RestTemplate Sources]]
