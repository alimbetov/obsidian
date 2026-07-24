---
type: assessment
certification: spring-2V0-72.22
domain: spring
subdomain: spring-mvc
batch: SPRING-MVC-B02
status: published
pre_test_questions: 10
post_test_questions: 15
objectives:
  - SPRING-3.2.1
  - SPRING-3.2.2
tags:
  - assessment
  - spring-mvc
  - rest
  - rest-template
---

# SPRING-MVC-B02 Assessment

# Assessment protocol

1. Complete the pre-test before opening the canonical note.
2. Record confidence from 1 to 5.
3. Study [[10_CONCEPTS/Spring/MVC/REST Endpoints ResponseEntity and RestTemplate]].
4. Complete base cards and drills.
5. Predict all lab outcomes.
6. Complete the post-test without notes.
7. Record weak card IDs through [[70_PROGRESS/README]].

# Pre-test — 10 questions

## P01

Classify GET, POST, PUT, PATCH and DELETE as safe and/or idempotent.

## P02

Explain the semantic difference between POST and PUT.

## P03

Distinguish `@PathVariable`, `@RequestParam`, `@RequestHeader` and `@RequestBody`.

## P04

Distinguish request `Content-Type` from response `Accept`.

## P05

What three parts of an HTTP response can `ResponseEntity` control?

## P06

What status and header should normally accompany successful resource creation?

## P07

Why can a repeated DELETE return a different status while remaining idempotent?

## P08

Compare `getForObject` and `getForEntity`.

## P09

When is `RestTemplate.exchange` preferable to convenience methods?

## P10

Why is `ParameterizedTypeReference` needed for `List<OrderDto>`?

# Pre-test answer key

1. GET is safe/idempotent; POST is neither by default; PUT is idempotent; PATCH is not guaranteed idempotent; DELETE is idempotent but not safe.
2. POST submits for server-defined processing or collection creation; PUT replaces/creates state at a known target URI.
3. Path identity, query selection/control, header protocol metadata, and message-converted body respectively.
4. `Content-Type` describes what is sent; `Accept` describes acceptable response representations.
5. Status, headers, and optional body.
6. `201 Created` with `Location`.
7. Idempotence concerns final target-state effect, not identical responses.
8. Body only versus full `ResponseEntity`.
9. For arbitrary method, headers/body, full response, or generic response type.
10. To preserve the generic element type after Java type erasure.

# Post-test — 15 questions

## T01 — Contract reconstruction

Define the complete HTTP contract for creating an order, including method, URI, request headers/body, validation, response status, headers and body.

## T02 — Method semantics

A team implements `PUT /accounts/{id}` as “increment balance by amount”. Explain the semantic defect and propose a better contract.

## T03 — Mapping diagnosis

A path exists but POST receives 405. Identify the stage and the mapping conditions to inspect.

## T04 — Body conversion

Trace JSON from bytes to a validated Java request object and identify where 400 and 415 arise.

## T05 — Response negotiation

A controller executes successfully but returns 406. Provide a diagnostic sequence covering `Accept`, `produces`, return type and converters.

## T06 — Creation

Compare `200 OK`, `201 Created`, and `202 Accepted` for a create request.

## T07 — Empty success

When should an update return 200 with a body versus 204 without a body?

## T08 — Conditional request

Design an ETag/`If-Match` update flow and explain when 412 should be returned.

## T09 — Error schema

Design a Spring 5.3 `@RestControllerAdvice` contract for not-found, validation and conflict failures.

## T10 — Entity types

Compare `HttpEntity`, `RequestEntity` and `ResponseEntity` with one server and one client example.

## T11 — Client method selection

Choose among `getForObject`, `getForEntity`, `postForLocation`, `put`, `exchange`, and `execute` for six different requirements.

## T12 — Generic extraction

Explain why `List.class` is insufficient and write the `exchange` signature for `List<OrderDto>`.

## T13 — Client resilience

Design `RestTemplateBuilder` configuration with root URI, connect timeout, read timeout, interceptor and error handler.

## T14 — Downstream errors

Explain the default 4xx/5xx exception hierarchy and how to preserve the downstream error body while translating it.

## T15 — Version boundary

Compare `RestTemplate`, `RestClient` and `WebClient` without confusing the Spring 5.3 exam baseline.

# Post-test expected evidence

A complete answer should demonstrate:

- method semantics rather than annotation memorization;
- explicit request and response contracts;
- correct status/header/body ownership;
- distinction between 400, 405, 406, 409, 412 and 415;
- safe generic deserialization;
- timeout and retry reasoning;
- baseline/current-version separation;
- ability to predict MockMvc and MockRestServiceServer outcomes.

# Scoring

| Level | Result |
|---|---|
| 0 | Lists annotations but cannot explain HTTP semantics |
| 1 | Knows verbs but confuses status and media contracts |
| 2 | Builds common endpoints but misses idempotence and client errors |
| 3 | Correct server and client contracts with minor gaps |
| 4 | Diagnoses negotiation, concurrency, generic types and timeouts |
| 5 | Transfers the model to production incidents and version migrations |

# Related material

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Cards]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Drills]]
- [[40_PRODUCTION_CASES/Spring/Spring MVC REST Contract Production Cases]]
- [[50_LABS/Spring/SPRING-MVC-B02/README]]
