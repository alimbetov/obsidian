---
type: source-index
domain: spring
subdomain: spring-mvc
route: SPRING-MVC-B02
status: published
verified_at: 2026-07-24
exam_baseline:
  - spring-framework-5.3.39
  - spring-boot-2.5.15
current_delta:
  - spring-framework-current
  - spring-boot-current
tags:
  - sources
  - spring-mvc
  - rest
  - response-entity
  - rest-template
---

# Spring MVC REST and RestTemplate Sources

> [!summary]
> Primary-source index for `SPRING-MVC-B02`. The route focuses on REST HTTP verbs, response contracts, content negotiation, error responses and `RestTemplate` client operations under the Spring Framework 5.3 / Boot 2.5 exam baseline.

# Official exam baseline

- Broadcom Spring Professional Develop Exam Guide: https://docs.broadcom.com/doc/vmw-spring-professional-develop-exam-guide
- Spring Framework 5.3 Web on Servlet Stack: https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/web.html
- Spring Framework 5.3 Integration REST clients: https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/integration.html#rest-client-access
- Spring Boot 2.5 Reference: https://docs.spring.io/spring-boot/docs/2.5.15/reference/html/

# Framework 5.3 API contracts

- `@RestController`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/bind/annotation/RestController.html
- `@RequestMapping`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/bind/annotation/RequestMapping.html
- `@GetMapping`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/bind/annotation/GetMapping.html
- `@PostMapping`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/bind/annotation/PostMapping.html
- `@PutMapping`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/bind/annotation/PutMapping.html
- `@PatchMapping`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/bind/annotation/PatchMapping.html
- `@DeleteMapping`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/bind/annotation/DeleteMapping.html
- `@RequestBody`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/bind/annotation/RequestBody.html
- `ResponseEntity`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/http/ResponseEntity.html
- `HttpEntity`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/http/HttpEntity.html
- `RequestEntity`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/http/RequestEntity.html
- `HttpHeaders`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/http/HttpHeaders.html
- `HttpStatus`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/http/HttpStatus.html
- `MediaType`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/http/MediaType.html
- `ParameterizedTypeReference`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/core/ParameterizedTypeReference.html
- `RestTemplate`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/client/RestTemplate.html
- `ResponseErrorHandler`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/client/ResponseErrorHandler.html
- `RestTemplateBuilder`: https://docs.spring.io/spring-boot/docs/2.5.x/api/org/springframework/boot/web/client/RestTemplateBuilder.html
- `MockRestServiceServer`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/test/web/client/MockRestServiceServer.html

# Current documentation for production delta

- REST controllers and annotated methods: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html
- Message converters: https://docs.spring.io/spring-framework/reference/web/webmvc/message-converters.html
- REST clients: https://docs.spring.io/spring-framework/reference/integration/rest-clients.html
- REST error responses: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html
- Spring Boot REST clients: https://docs.spring.io/spring-boot/reference/io/rest-client.html

# Stable architectural conclusions

```text
REST endpoint method
→ request mapping condition match
→ request body/path/query/header resolution
→ conversion and validation
→ controller method
→ ResponseEntity or return-value handler
→ HttpMessageConverter
→ HTTP status, headers and body
```

For clients:

```text
RestTemplate method
→ URI expansion and request entity
→ ClientHttpRequestFactory
→ message conversion
→ status/error handling
→ response extraction
```

# Version-boundary matrix

| Concern | Spring 5.3 / Boot 2.5 exam baseline | Current production delta |
|---|---|---|
| REST server | `@RestController`, mapping annotations, `ResponseEntity` | same core model |
| Error body | custom DTO, `@ExceptionHandler`, `ResponseEntityExceptionHandler` | `ProblemDetail` and `ErrorResponse` abstractions available |
| REST client | `RestTemplate`, `RestTemplateBuilder` | `RestClient` for modern synchronous code; `WebClient` for reactive flows |
| Servlet namespace | `javax.servlet` | `jakarta.servlet` |
| HTTP status type | `HttpStatus` | newer APIs also expose `HttpStatusCode` |

# Evidence policy

The route is **reading-ready**. Runtime proof remains a separate gate. After converting the guided scenarios into an executable module, run:

```bash
mvn --batch-mode --no-transfer-progress \
  -f 50_LABS/Spring/SPRING-MVC-B02/pom.xml test
```

Do not mark the route runtime-ready until the module exists and the command passes in CI.

# Related material

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Roadmap]]
- [[10_CONCEPTS/Spring/MVC/REST Endpoints ResponseEntity and RestTemplate]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Cards]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Drills]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Assessment]]
- [[40_PRODUCTION_CASES/Spring/Spring MVC REST Contract Production Cases]]
- [[50_LABS/Spring/SPRING-MVC-B02/README]]
