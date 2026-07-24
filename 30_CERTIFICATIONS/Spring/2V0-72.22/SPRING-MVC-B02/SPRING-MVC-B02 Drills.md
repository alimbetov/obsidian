---
type: certification-drills
certification: spring-2V0-72.22
domain: spring
subdomain: spring-mvc
batch_id: SPRING-MVC-B02
status: published
drill_count: 7
first_card: SPRING-MVC-B02-D001
last_card: SPRING-MVC-B02-D007
objectives:
  - SPRING-3.2.1
  - SPRING-3.2.2
tags:
  - spring-mvc
  - drills
  - exam-traps
  - rest-template
---

# SPRING-MVC-B02 — Exam Drills

> [!tip]
> Answer each drill before opening the answer callout. Record both correctness and confidence.

## SPRING-MVC-B02-D001 — HTTP semantic classification

Select all true statements:

A. GET is safe and idempotent.  
B. DELETE must return the same status on every repetition to be idempotent.  
C. PUT is defined as idempotent.  
D. PATCH is always idempotent because it changes only part of a resource.  
E. POST is not idempotent by default.

> [!answer]- Answer
> A, C, E. Idempotence concerns target-state effect, so repeated DELETE responses may differ. PATCH idempotence depends on the patch operation.

---

## SPRING-MVC-B02-D002 — Creation contract

```java
@PostMapping
public ResponseEntity<OrderDto> create(@RequestBody OrderDto request) {
    OrderDto created = service.create(request);
    return ResponseEntity.ok(created);
}
```

What is missing from the strongest resource-creation contract?

> [!answer]- Answer
> Normally `201 Created` and a `Location` header identifying the created resource. A response body is optional but useful.

---

## SPRING-MVC-B02-D003 — 415 versus 406

A JSON POST request uses:

```http
Content-Type: text/plain
Accept: application/xml
```

The endpoint consumes and produces only JSON. Which failure concerns request consumption, and which concerns response production?

> [!answer]- Answer
> `415 Unsupported Media Type` concerns the unsupported request `Content-Type`. If request consumption succeeds but XML cannot be produced for the declared `Accept`, the response-side result is `406 Not Acceptable`.

---

## SPRING-MVC-B02-D004 — Generic collection extraction

Why can this produce a `List<LinkedHashMap>`?

```java
List<OrderDto> result =
    (List<OrderDto>) restTemplate.getForObject(url, List.class);
```

> [!answer]- Answer
> `List.class` loses the element type. Use `exchange` with `new ParameterizedTypeReference<List<OrderDto>>() {}` so the converter receives the full generic type.

---

## SPRING-MVC-B02-D005 — Timeout diagnosis

A service uses a shared `RestTemplate` with no explicit timeouts. During a downstream incident, request threads accumulate.

Which configuration is required first?

A. Only a connection timeout  
B. Only a read timeout  
C. Both connection and read timeouts  
D. A retry on every POST

> [!answer]- Answer
> C. Connection and read waits are distinct failure modes. Blind POST retries can duplicate side effects.

---

## SPRING-MVC-B02-D006 — Default error behavior

What normally happens when `RestTemplate` receives a 404 under its default error handler?

> [!answer]- Answer
> It throws an `HttpClientErrorException.NotFound` (within the `RestClientResponseException` hierarchy) rather than returning a normal `ResponseEntity` from the convenience operation.

---

## SPRING-MVC-B02-D007 — Baseline versus current delta

Choose the best answer for a Spring 5.3 certification question asking how to invoke a REST service synchronously:

A. Replace the question with `WebClient`.  
B. Explain `RestTemplate`; optionally add that current Spring also offers `RestClient` and `WebClient`.  
C. State that `RestTemplate` no longer works.  
D. Use `ProblemDetail`.

> [!answer]- Answer
> B. Answer the declared exam baseline first. Current alternatives are a version delta, not a substitute for the required API.

# Progress recording

```bash
python .github/scripts/card_progress.py record \
  --card-id SPRING-MVC-B02-D004 \
  --outcome wrong-confusion \
  --confidence 2 \
  --note "Forgot ParameterizedTypeReference"
```
