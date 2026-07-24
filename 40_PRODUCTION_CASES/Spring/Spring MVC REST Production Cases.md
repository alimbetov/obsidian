---
type: production-cases
domain: spring
subdomain: spring-mvc
route: SPRING-MVC-B02
status: published
case_count: 12
objectives:
  - SPRING-3.2.1
  - SPRING-3.2.2
tags:
  - spring-mvc
  - rest
  - response-entity
  - rest-template
  - incidents
---

# Spring MVC REST Production Cases

## Route navigation

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Roadmap]]
- [[10_CONCEPTS/Spring/MVC/REST Endpoints ResponseEntity and RestTemplate]]
- [[10_CONCEPTS/Spring/MVC/Spring MVC REST Visual Deep Dive]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Cards]]
- [[50_LABS/Spring/SPRING-MVC-B02/README]]
- [[98_SOURCES/Spring MVC REST and RestTemplate Sources]]

# Case 1 — Resource creation returns 200 without Location

## Symptom

A mobile client creates an item but cannot reliably navigate to the created resource.

## Evidence

```text
POST /catalog
status 200
body contains id
no Location header
```

## Root cause

The endpoint uses a plain DTO return instead of an explicit creation response contract.

## Repair

Return `ResponseEntity.created(location).body(dto)`.

## Proof

A `MockMvc` test asserts `201 Created`, `Location` and JSON body.

---

# Case 2 — DELETE returns a JSON body by accident

## Symptom

Some clients expect no content, but the endpoint returns an empty JSON object.

## Root cause

The handler returns a DTO/object instead of `ResponseEntity.noContent().build()`.

## Repair

Use `204 No Content` for successful no-body delete.

## Proof

Assert status 204 and empty response body.

---

# Case 3 — 415 mistaken for validation error

## Symptom

Client posts text to a JSON endpoint and expects field validation details.

## Evidence

```text
Content-Type: text/plain
Endpoint consumes application/json
status 415
controller method not invoked
```

## Root cause

The request body media type is unsupported; no DTO exists yet for validation.

## Repair

Fix client `Content-Type` or support additional media type deliberately.

## Proof

`MockMvc` test posts `text/plain` and asserts 415 before service call.

---

# Case 4 — 406 caused by strict Accept header

## Symptom

The API works in browser/manual tests but fails for a client requesting `text/plain` only.

## Root cause

The endpoint produces JSON; the client `Accept` header excludes JSON.

## Repair

Fix the client Accept header or provide a supported representation.

## Proof

A test with `Accept: text/plain` returns 406.

---

# Case 5 — Validation exception leaks default error shape

## Symptom

Validation errors return an inconsistent default body.

## Root cause

No `@RestControllerAdvice` maps validation exceptions into the API's error contract.

## Repair

Add an exception handler that returns `ResponseEntity<ApiError>` with status 400.

## Proof

Post invalid DTO and assert stable `code/message` fields.

---

# Case 6 — Domain not-found appears as 500

## Symptom

Missing catalog item returns 500.

## Root cause

A domain exception is thrown but not translated into a 404 response.

## Repair

Handle the domain exception through `@RestControllerAdvice` or throw a suitable `ResponseStatusException`.

## Proof

`GET /catalog/999` returns 404 with API error body.

---

# Case 7 — PUT and PATCH have identical accidental semantics

## Symptom

PATCH clears fields that were omitted by the client.

## Root cause

PATCH delegates to full-replacement logic intended for PUT.

## Repair

Define PATCH as partial update and update only supplied fields.

## Proof

A test patches one field and asserts the other fields remain unchanged.

---

# Case 8 — RestTemplate client cannot inspect Location header

## Symptom

Client uses `postForObject` and cannot access response headers.

## Root cause

`postForObject` returns only the body.

## Repair

Use `postForEntity` or `exchange` when status/headers are part of the contract.

## Proof

`MockRestServiceServer` returns `201 Location`; test asserts `postForEntity` exposes it.

---

# Case 9 — getForObject hides status/header needs

## Symptom

Client needs ETag but uses `getForObject`.

## Root cause

The chosen API discards status and headers.

## Repair

Use `getForEntity` or `exchange`.

## Proof

A client test asserts the `ETag` header from `ResponseEntity`.

---

# Case 10 — RestTemplate error handler surprises caller

## Symptom

Client code expects a normal `ResponseEntity` for 404, but an exception is thrown.

## Root cause

Default `ResponseErrorHandler` treats 4xx/5xx as errors.

## Repair

Catch `HttpClientErrorException.NotFound`, customize error handler or model absence explicitly.

## Proof

`MockRestServiceServer` returns 404 and the test asserts domain-specific handling.

---

# Case 11 — RestClient used in exam-baseline answer

## Symptom

A study answer replaces all `RestTemplate` details with `RestClient`.

## Root cause

Current production delta leaked into Spring 5.3 exam baseline.

## Repair

Keep `RestTemplate` as the baseline answer; mention `RestClient` only as current API comparison.

## Proof

Card review explicitly asks which client belongs to the Spring 5.3 objective.

---

# Case 12 — ProblemDetail leaked into Spring 5.3 baseline

## Symptom

A Spring 5.3 exam answer claims `ProblemDetail` is required for REST errors.

## Root cause

Spring 6+ error abstractions were applied to a Spring 5.3 baseline question.

## Repair

Use custom DTO/`ResponseEntity`/exception handler patterns for baseline; mark `ProblemDetail` as current delta.

## Proof

Version-boundary card classifies `ProblemDetail` correctly.

# Diagnostic checklist

```text
1. Which mapping condition failed: path, method, consumes or produces?
2. Did the request body convert before validation?
3. Did the controller body execute?
4. Who owns status, headers and body?
5. Is the error response produced by advice or default resolver?
6. Is the client API body-only, entity-returning or fully flexible?
7. Is the answer Spring 5.3 baseline or current production delta?
```
