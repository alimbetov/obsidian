---
type: visual-deep-dive
domain: spring
subdomain: spring-mvc
route: SPRING-MVC-B02
status: published
visual_diagrams: 25
objectives:
  - SPRING-3.2.1
  - SPRING-3.2.2
exam_baseline:
  - spring-framework-5.3
  - spring-boot-2.5
current_delta:
  - spring-framework-current
tags:
  - spring-mvc
  - rest
  - response-entity
  - rest-template
  - visual-learning
---

# Spring MVC REST Visual Deep Dive

> [!summary]
> Twenty-five models for reconstructing REST endpoint selection, request-body conversion, response contracts, error mapping and `RestTemplate` client execution.

# Route navigation

- [[10_CONCEPTS/Spring/MVC/REST Endpoints ResponseEntity and RestTemplate]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Roadmap]]
- [[01_MAPS/Spring MVC REST Map.canvas]]

# 1. REST route boundary

```mermaid
flowchart LR
    B01["B01 DispatcherServlet pipeline"] --> B02["B02 REST contract"]
    B02 --> VERB["HTTP verbs"]
    B02 --> ENTITY["Request/Response entity"]
    B02 --> CONV["Message conversion"]
    B02 --> CLIENT["RestTemplate client"]
```

# 2. REST server topology

```mermaid
flowchart LR
    REQ["HTTP request"] --> MAP["Verb/path/media conditions"]
    MAP --> ARGS["Path/query/header/body args"]
    ARGS --> VALID["Conversion + validation"]
    VALID --> CTRL["Controller method"]
    CTRL --> RESP["ResponseEntity or body"]
    RESP --> BYTES["HTTP status, headers, bytes"]
```

# 3. Verb-to-method mapping

```mermaid
flowchart TB
    RES["/catalog"] --> GET["GET read"]
    RES --> POST["POST create"]
    RESID["/catalog/{id}"] --> GETID["GET read one"]
    RESID --> PUT["PUT replace"]
    RESID --> PATCH["PATCH partial update"]
    RESID --> DEL["DELETE remove"]
```

# 4. Mapping condition set

```mermaid
flowchart LR
    R["Request"] --> PATH["path"]
    PATH --> METHOD["HTTP method"]
    METHOD --> PARAMS["params"]
    PARAMS --> HEADERS["headers"]
    HEADERS --> CONS["consumes"]
    CONS --> PROD["produces"]
    PROD --> HANDLER["selected handler"]
```

# 5. Input-source split

```mermaid
flowchart TB
    REQ["Request"] --> PATHVAR["@PathVariable"]
    REQ --> PARAM["@RequestParam"]
    REQ --> HEADER["@RequestHeader"]
    REQ --> BODY["@RequestBody"]
    BODY --> CONVERTER["HttpMessageConverter"]
```

# 6. Request body path

```mermaid
flowchart LR
    BYTES["Body bytes"] --> CT["Content-Type"]
    CT --> SELECT["Select converter"]
    SELECT --> DTO["Java DTO"]
    DTO --> VALID["@Valid"]
    VALID --> ARG["Controller argument"]
```

# 7. Validation failure path

```mermaid
flowchart TD
    DTO["Deserialized DTO"] --> VALID{"Constraints valid?"}
    VALID -->|"yes"| CALL["Invoke method"]
    VALID -->|"no"| EX["Validation exception"]
    EX --> ADVICE["@RestControllerAdvice"]
    ADVICE --> BAD["400 error body"]
```

# 8. ResponseEntity contract

```mermaid
flowchart TB
    RE["ResponseEntity<T>"] --> STATUS["status"]
    RE --> HEADERS["headers"]
    RE --> BODY["body"]
    STATUS --> HTTP["HTTP response"]
    HEADERS --> HTTP
    BODY --> HTTP
```

# 9. Create response

```mermaid
flowchart LR
    POST["POST /catalog"] --> SAVE["create id"]
    SAVE --> URI["Location: /catalog/{id}"]
    URI --> CREATED["201 Created + body"]
```

# 10. Delete response

```mermaid
flowchart LR
    DEL["DELETE /catalog/{id}"] --> REMOVE["remove resource"]
    REMOVE --> NOCONTENT["204 No Content"]
```

# 11. Content negotiation

```mermaid
flowchart TD
    ACCEPT["Accept header"] --> PROD["produces condition"]
    PROD --> CONV{"Can converter write?"}
    CONV -->|"yes"| OK["response body"]
    CONV -->|"no"| NA["406 Not Acceptable"]
```

# 12. Consumes negotiation

```mermaid
flowchart TD
    CT["Content-Type"] --> CONS["consumes condition"]
    CONS -->|"match"| READ["read body"]
    CONS -->|"no match"| UMT["415 Unsupported Media Type"]
```

# 13. Status-code decision tree

```mermaid
flowchart TD
    RESULT["Request outcome"] --> CREATED{"created new resource?"}
    CREATED -->|"yes"| S201["201"]
    CREATED -->|"no"| BODY{"response body?"}
    BODY -->|"yes"| S200["200"]
    BODY -->|"no"| S204["204"]
    RESULT --> INVALID["invalid input -> 400"]
    RESULT --> MISSING["missing resource -> 404"]
```

# 14. REST error contract

```mermaid
flowchart LR
    EX["Exception"] --> RESOLVER["Exception resolver"]
    RESOLVER --> HANDLER["@ExceptionHandler"]
    HANDLER --> ENTITY["ResponseEntity<ErrorDto>"]
    ENTITY --> JSON["status + JSON error"]
```

# 15. ResponseStatusException path

```mermaid
flowchart LR
    CODE["throw ResponseStatusException"] --> RSE["ResponseStatusExceptionResolver"]
    RSE --> HTTP["status/reason response"]
```

# 16. Server-side converter symmetry

```mermaid
flowchart TB
    IN["read @RequestBody"] --> CONV["HttpMessageConverter"]
    OUT["write @ResponseBody"] --> CONV
    RE["write ResponseEntity body"] --> CONV
```

# 17. RestTemplate topology

```mermaid
flowchart LR
    CALL["RestTemplate method"] --> URI["URI template expansion"]
    URI --> ENTITY["HttpEntity headers/body"]
    ENTITY --> FACTORY["ClientHttpRequestFactory"]
    FACTORY --> CONV["Message converters"]
    CONV --> RESP["Response extraction"]
```

# 18. RestTemplate method choice

```mermaid
flowchart TB
    NEED["Client need"] --> BODYONLY["body only"]
    NEED --> FULL["status/headers/body"]
    NEED --> CUSTOM["method + headers"]
    BODYONLY --> GFO["getForObject/postForObject"]
    FULL --> GFE["getForEntity/postForEntity"]
    CUSTOM --> EX["exchange"]
```

# 19. `exchange` path

```mermaid
flowchart LR
    METHOD["HttpMethod"] --> REQ["HttpEntity"]
    REQ --> URI["URI template"]
    URI --> TYPE["response type"]
    TYPE --> RESPONSE["ResponseEntity<T>"]
```

# 20. Client error handling

```mermaid
flowchart TD
    STATUS["HTTP status"] --> EH["ResponseErrorHandler"]
    EH -->|"error"| THROW["RestClientResponseException"]
    EH -->|"not error"| EXTRACT["extract body"]
```

# 21. RestTemplateBuilder

```mermaid
flowchart TB
    BUILDER["RestTemplateBuilder"] --> ROOT["root URI"]
    BUILDER --> TIMEOUTS["timeouts"]
    BUILDER --> CONVERTERS["converters"]
    BUILDER --> INTERCEPTORS["interceptors"]
    BUILDER --> ERR["error handler"]
    BUILDER --> TEMPLATE["RestTemplate"]
```

# 22. Server MockMvc proof

```mermaid
flowchart LR
    TEST["MockMvc request"] --> DS["DispatcherServlet"]
    DS --> CTRL["REST controller"]
    CTRL --> ADVICE["Controller advice"]
    ADVICE --> ASSERT["status/header/json assertions"]
```

# 23. Client MockRestServiceServer proof

```mermaid
flowchart LR
    CLIENT["CatalogRestClient"] --> RT["RestTemplate"]
    RT --> MOCK["MockRestServiceServer"]
    MOCK --> EXPECT["expect method/URI/body"]
    EXPECT --> RESP["mock response"]
```

# 24. Current production delta

```mermaid
flowchart TB
    BASE["Exam baseline"] --> RT["RestTemplate"]
    CURRENT["Current production"] --> RC["RestClient"]
    CURRENT --> WC["WebClient"]
    BASE --> CUSTOMERR["custom error DTO"]
    CURRENT --> PD["ProblemDetail"]
```

# 25. Full route mental model

```mermaid
flowchart LR
    SERVER["REST server contract"] --> STATUS["status/header/body"]
    SERVER --> NEG["content negotiation"]
    SERVER --> ERROR["error contract"]
    CLIENT["REST client contract"] --> RT["RestTemplate"]
    RT --> EXCHANGE["exchange"]
    RT --> TESTS["MockRestServiceServer"]
```

# Visual recall prompts

1. Reconstruct diagrams 2, 6, 8, 11, 17 and 20 without notes.
2. Explain why 406 and 415 occur at different sides of the message-converter boundary.
3. Explain why `getForObject` cannot inspect response headers.
4. Explain why `ResponseEntity.created(location)` is different from returning a DTO with `200 OK`.
5. Trace the same DTO through server-side response writing and `RestTemplate` client-side reading.

# Related material

- [[10_CONCEPTS/Spring/MVC/REST Endpoints ResponseEntity and RestTemplate]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Assessment]]
- [[50_LABS/Spring/SPRING-MVC-B02/README]]
