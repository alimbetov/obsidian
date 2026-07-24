---
type: assessment
certification: spring-2V0-72.22
domain: spring
subdomain: spring-mvc
route: SPRING-MVC-B02
status: published
pre_test_questions: 10
post_test_questions: 15
objectives:
  - SPRING-3.2.1
  - SPRING-3.2.2
tags:
  - spring-mvc
  - rest
  - rest-template
  - assessment
---

# SPRING-MVC-B02 Assessment

## Route navigation

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Roadmap]]
- [[10_CONCEPTS/Spring/MVC/REST Endpoints ResponseEntity and RestTemplate]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Cards]]
- [[50_LABS/Spring/SPRING-MVC-B02/README]]

# Pre-test — do not update confidence

1. What does `@RestController` imply for return values?
2. Which annotation reads a JSON request body into a DTO?
3. Which status should a newly created resource normally return?
4. What is the purpose of the `Location` header?
5. Which mismatch usually causes 415?
6. Which mismatch usually causes 406?
7. What does `ResponseEntity` contain that a plain DTO return does not explicitly contain?
8. What does `RestTemplate.getForObject` return?
9. When would you choose `RestTemplate.exchange`?
10. Which test utility mocks outbound `RestTemplate` calls?

# Post-test — update progress after review

1. Select all true statements about `@RestController` and `ResponseEntity`.
2. Given POST `/catalog` with valid JSON, choose the best response contract for creation.
3. Given `Content-Type: text/plain` for a JSON-only endpoint, predict status and boundary.
4. Given `Accept: text/plain` for a JSON-only endpoint, predict status and boundary.
5. Given invalid DTO fields, identify whether the controller body runs.
6. Given a handler returning a DTO directly, explain the default status/body behavior.
7. Given a handler returning `ResponseEntity.noContent()`, identify status and body.
8. Compare `getForObject`, `getForEntity` and `exchange`.
9. Given a `RestTemplate` call with custom `Accept` header, choose the correct API shape.
10. Explain how message converters are used both server-side and client-side.
11. Distinguish `MockMvc` from `MockRestServiceServer`.
12. Explain why `RestClient` is not the Spring 5.3 exam-baseline answer.
13. Explain why `ProblemDetail` is a current delta, not a Spring 5.3 baseline requirement.
14. Trace a 404 from domain exception through `@RestControllerAdvice` to JSON error response.
15. Write a small `RestTemplate.postForEntity` call and identify request/response conversion points.

# Transfer tasks

## Task 1 — Endpoint contract

Design a catalog resource with:

```text
GET /catalog/{id}
POST /catalog
PUT /catalog/{id}
PATCH /catalog/{id}
DELETE /catalog/{id}
```

For each endpoint, specify:

```text
request body required?
success status
response body?
headers
validation failure status
not-found behavior
```

## Task 2 — Client contract

Write a `RestTemplate` client method that:

```text
sends Accept: application/json
uses URI variable id
returns ResponseEntity<CatalogItemDto>
handles 404 as Optional.empty
```

## Task 3 — Boundary diagnosis

Classify each failure:

```text
invalid JSON syntax
valid JSON but invalid field
unsupported request Content-Type
unsupported Accept header
resource missing in service
remote server returns 500 to RestTemplate
```

# Progress policy

- Pre-test answers do not change card confidence.
- Post-test answers map to `SPRING-MVC-B02-C001` through `C025`.
- `correct-guessed` must schedule early review.
- `wrong-confusion` should link back to the relevant visual model.

# Related cards

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Cards]]
