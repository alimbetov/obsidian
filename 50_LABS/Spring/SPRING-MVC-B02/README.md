---
type: lab
domain: spring
subdomain: spring-mvc
route: SPRING-MVC-B02
status: active
spring_boot_version: 2.5.15
java_version: 8
certification:
  - spring-2V0-72.22
runtime_tests: 15
tags:
  - spring-mvc
  - rest
  - response-entity
  - rest-template
  - mockmvc
  - mock-rest-service-server
---

# SPRING-MVC-B02 Lab — REST Endpoints and RestTemplate

## Purpose

Prove the REST server and client contracts from `SPRING-MVC-B02`:

```text
HTTP verb mappings
ResponseEntity status/header/body control
201 Created and Location
204 No Content
JSON request body conversion
validation and REST error body
406/415 media negotiation boundaries
RestTemplate getForObject/getForEntity/postForEntity/exchange
MockRestServiceServer client-side proof
```

# Baseline

```text
Spring Boot 2.5.15
Spring Framework 5.3.x
Java 8 source-level
MockMvc
MockRestServiceServer
```

# Run

```bash
cd 50_LABS/Spring/SPRING-MVC-B02
mvn clean test
```

# Source structure

```text
SPRING-MVC-B02/
├── pom.xml
└── src
    ├── main/java/lab/mvc/rest
    │   ├── RestLabApplication.java
    │   ├── CatalogController.java
    │   ├── CatalogService.java
    │   ├── CatalogRestClient.java
    │   ├── CatalogItemDto.java
    │   ├── CreateCatalogItemRequest.java
    │   ├── UpdateCatalogItemRequest.java
    │   ├── PatchCatalogItemRequest.java
    │   ├── ApiError.java
    │   ├── CatalogNotFoundException.java
    │   └── RestErrorAdvice.java
    └── test/java/lab/mvc/rest
        ├── CatalogControllerRestTest.java
        └── CatalogRestClientTest.java
```

# Server experiments — MockMvc

| Test | Contract |
|---|---|
| `getReturnsJsonBodyHeadersAndStatus` | `GET`, JSON body, ETag, status 200 |
| `postCreatesResourceWithLocationHeader` | `POST`, 201, `Location`, created body |
| `putReplacesFullResource` | `PUT` full replacement |
| `patchUpdatesOnlySuppliedFields` | `PATCH` partial update |
| `deleteReturnsNoContent` | `DELETE`, 204, empty body |
| `validationFailureReturnsRestErrorContract` | invalid DTO → 400 JSON error |
| `notFoundReturns404RestErrorContract` | domain not-found → 404 JSON error |
| `unsupportedContentTypeReturns415BeforeControllerBody` | `Content-Type` mismatch → 415 |
| `unacceptableAcceptHeaderReturns406` | `Accept` mismatch → 406 |
| `responseEntityCanExposeExplicitHeaders` | explicit `ResponseEntity` headers |

# Client experiments — MockRestServiceServer

| Test | Contract |
|---|---|
| `getForObjectReturnsOnlyConvertedBody` | body-only GET client method |
| `getForEntityExposesStatusHeadersAndBody` | status/header/body client method |
| `postForEntityExposesCreatedLocation` | POST response entity and `Location` |
| `exchangeSupportsExplicitAcceptHeader` | explicit method and headers |
| `restTemplate404CanBeMappedToOptionalEmpty` | default error handling mapped to domain Optional |

# Prediction worksheet

Before running the tests, predict:

| Scenario | Expected status | Does controller body run? | Boundary |
|---|---:|---|---|
| valid POST JSON | 201 | yes | controller + ResponseEntity |
| invalid DTO JSON | 400 | no | validation/advice |
| text/plain body to JSON POST | 415 | no | consumes/message converter |
| Accept text/plain to JSON GET | 406 | no/handler not used for response | produces/negotiation |
| missing id | 404 | service throws | advice |
| RestTemplate GET 404 | exception by default | client-side | error handler |

# Runtime status

```text
Source review       completed
Maven execution     pending until GitHub Actions run after route registration
Runtime PASS        not claimed until CI reports success
```

# Route navigation

- **Roadmap:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Roadmap]]
- **Canonical:** [[10_CONCEPTS/Spring/MVC/REST Endpoints ResponseEntity and RestTemplate]]
- **Visual:** [[10_CONCEPTS/Spring/MVC/Spring MVC REST Visual Deep Dive]]
- **Cards:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Cards]]
- **Assessment:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Assessment]]
- **Cases:** [[40_PRODUCTION_CASES/Spring/Spring MVC REST Production Cases]]
- **Sources:** [[98_SOURCES/Spring MVC REST and RestTemplate Sources]]
