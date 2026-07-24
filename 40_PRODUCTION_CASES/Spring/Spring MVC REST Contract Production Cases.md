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
  - incidents
  - rest-template
---

# Spring MVC REST Contract Production Cases

# Case 1 — POST creates a resource but returns only `200 OK`

## Symptom

The client receives the created DTO but cannot determine the canonical resource URI without reconstructing routing rules.

## Mechanism

The endpoint returned an application object but omitted the creation-specific status and `Location` header.

## Correct fix

Return `201 Created` with `Location`. Include the representation when useful.

## Production lesson

Resource creation is a protocol contract, not only a successful service call.

---

# Case 2 — PUT silently performs a partial merge

## Symptom

Omitted fields retain previous values even though clients believe PUT replaces the resource.

## Mechanism

Implementation semantics behave like PATCH while the public contract says PUT.

## Risks

- clients cannot reason about replacement;
- stale fields survive unexpectedly;
- retry and synchronization logic becomes ambiguous.

## Correct fix

Either implement replacement semantics or expose a clearly defined PATCH operation.

---

# Case 3 — PATCH increments twice after a retry

## Symptom

A network timeout causes the client to retry `PATCH {"increment": 1}` and the value increases twice.

## Mechanism

The patch operation is not idempotent. The timeout did not prove that the first request failed before server processing.

## Correct fix

Use an idempotency key, a version precondition, or a state-setting patch such as `{"value": 11}`.

---

# Case 4 — DELETE is idempotent, but audit events duplicate

## Symptom

Repeating DELETE leaves the resource absent but emits duplicate billing or audit side effects.

## Mechanism

HTTP target-state semantics are idempotent, while application side effects are not deduplicated.

## Correct fix

Make side-effect publication idempotent through event keys, outbox uniqueness, or state-transition checks.

## Production lesson

Method semantics do not automatically make every implementation detail idempotent.

---

# Case 5 — Team changes `Content-Type` to fix HTTP 406

## Symptom

The response is still 406, and request conversion behavior also becomes unstable.

## Mechanism

406 is response negotiation. The relevant dimensions are `Accept`, `produces`, return type and available converters.

## Correct fix

Restore the correct request `Content-Type`; fix the response representation contract separately.

---

# Case 6 — `204 No Content` includes an error-like JSON body

## Symptom

Some clients ignore the body while others fail parsing or report inconsistent behavior.

## Mechanism

The endpoint contradicts the 204 contract by attaching a representation.

## Correct fix

Use 200/202 with a body, or preserve 204 and remove the body.

---

# Case 7 — Missing entity becomes `200 OK` with `null`

## Symptom

A GET endpoint returns `ResponseEntity.ok(service.find(id).orElse(null))`.

## Mechanism

`ok(null)` still explicitly selects status 200.

## Correct fix

Map absence to `ResponseEntity.notFound().build()` or throw a domain exception translated centrally.

---

# Case 8 — Broad advice hides validation details

## Symptom

Every error becomes a generic 500 response, including malformed JSON and Bean Validation failures.

## Mechanism

A broad `@ExceptionHandler(Exception.class)` captures exceptions that should have precise status and field-level detail.

## Correct fix

Use specific mappings, extend `ResponseEntityExceptionHandler` where appropriate, and preserve framework error categories.

---

# Case 9 — No RestTemplate timeouts exhaust request threads

## Symptom

A downstream outage causes a growing number of blocked servlet threads and cascading latency.

## Mechanism

The synchronous client waits without a bounded connect/read policy.

## Correct fix

Configure connect and read timeouts with `RestTemplateBuilder`, then combine them with bulkheads, circuit breaking and controlled retries at the service boundary.

---

# Case 10 — `List.class` produces `LinkedHashMap` elements

## Symptom

The call succeeds, but code later fails with `ClassCastException`.

## Mechanism

The generic element type was erased, so Jackson converted each object to a generic map.

## Correct fix

Use `exchange` with `ParameterizedTypeReference<List<T>>`.

---

# Case 11 — PATCH fails only in production

## Symptom

Mock tests pass, but the production request factory rejects or mishandles PATCH.

## Mechanism

`RestTemplate` behavior depends partly on the underlying `ClientHttpRequestFactory` and HTTP library capabilities.

## Diagnostic path

1. Inspect the actual request factory.
2. Reproduce against the same runtime HTTP stack.
3. Inspect proxy/gateway method support.
4. Prefer `exchange(HttpMethod.PATCH, ...)` when full control is required.

---

# Case 12 — Blind POST retry creates duplicate orders

## Symptom

A read timeout triggers a retry and two orders are created.

## Mechanism

The first request may have committed before the response was lost. POST is not idempotent by default.

## Correct fix

Use a client-generated idempotency key with server-side deduplication, or redesign around a known resource URI and idempotent state transition.

## Production lesson

Retry policy must be derived from operation semantics, not only exception type.

# Incident checklist

```text
server:
method → path → headers/params → consumes → converter → validation
→ domain rule → status → response headers → produces → converter

client:
URI → method → request entity → request factory → timeout
→ downstream status → error handler → response extraction → retry policy
```

# Related material

- [[10_CONCEPTS/Spring/MVC/REST Endpoints ResponseEntity and RestTemplate]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B02/SPRING-MVC-B02 Cards]]
- [[50_LABS/Spring/SPRING-MVC-B02/README]]
