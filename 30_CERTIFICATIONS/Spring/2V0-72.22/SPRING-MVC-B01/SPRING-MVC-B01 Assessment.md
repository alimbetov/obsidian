---
type: assessment
certification: spring-2V0-72.22
domain: spring
subdomain: spring-mvc
batch: SPRING-MVC-B01
status: published
pre_test_questions: 10
post_test_questions: 15
objectives:
  - SPRING-3.1.1
  - SPRING-3.1.2
  - SPRING-3.1.3
  - SPRING-3.1.4
tags:
  - assessment
  - spring-mvc
  - dispatcher-servlet
---

# SPRING-MVC-B01 Assessment

# Assessment protocol

1. Complete the pre-test before opening the canonical note.
2. Record confidence from 1 to 5 for every answer.
3. Study the visual and canonical routes.
4. Complete cards and predict lab outcomes.
5. Run the lab.
6. Complete the post-test without notes.
7. Record weak card IDs through [[70_PROGRESS/README]].

# Pre-test — 10 questions

## P01

Put these stages in order: controller method, `DispatcherServlet`, filter, `HandlerMapping`, `HandlerAdapter`.

## P02

Explain the difference between `HandlerMapping` and `HandlerAdapter` in one sentence each.

## P03

What object usually combines a selected handler with interceptors?

## P04

What happens when `preHandle` returns `false`?

## P05

Which mechanism creates a controller argument annotated with `@PathVariable`?

## P06

Why are conversion and validation separate failure stages?

## P07

How does Spring decide whether a returned `String` is a view name or response text?

## P08

Which default exception-resolver family invokes `@ExceptionHandler` methods?

## P09

Distinguish HTTP 415 and HTTP 406.

## P10

What changes between executable JAR and WAR deployment, and what remains the same?

# Pre-test answer key

1. filter → `DispatcherServlet` → `HandlerMapping` → `HandlerAdapter` → controller method.
2. Mapping selects the handler; adapter invokes the selected handler.
3. `HandlerExecutionChain`.
4. The interceptor chain stops before controller invocation; the interceptor owns any response it intends to produce.
5. An ordered `HandlerMethodArgumentResolver`.
6. Conversion creates the Java type; validation checks constraints after conversion.
7. Through controller/return-value semantics: regular `@Controller` usually treats it as a view name, while response-body semantics write it as content.
8. `ExceptionHandlerExceptionResolver`.
9. 415 concerns unsupported request media type; 406 concerns unavailable acceptable response representation.
10. Bootstrap and container ownership differ; the MVC controller pipeline still routes through `DispatcherServlet`.

# Post-test — 15 questions

## T01 — Pipeline reconstruction

Reconstruct the complete request path from Servlet container to committed response, naming at least eight Spring MVC stages.

## T02 — Strategy ownership

For each concern, name the primary strategy: handler lookup, handler invocation, exception translation, logical view resolution.

## T03 — Mapping conditions

A path matches, but the request receives 415 before controller invocation. Name three mapping/conversion checks to inspect.

## T04 — Ambiguity

Two controller methods match the same path and method with equal specificity. Predict startup/runtime behavior and explain why registration order is not a valid design.

## T05 — Interceptor sequence

Given interceptors A then B, write the order of `preHandle`, handler invocation, `postHandle`, and `afterCompletion`.

## T06 — Custom argument

Design the extension point for injecting a `TenantId` parameter from `X-Tenant-Id`. State the two core resolver methods.

## T07 — Model binding

Trace a query string into a validated `@ModelAttribute` object through creation, binding, conversion, validation, and `BindingResult`.

## T08 — Body conversion

Trace JSON into a `@RequestBody` argument and identify where HTTP 400 or 415 can arise.

## T09 — Return paths

Compare the processing of `ModelAndView`, a view-name `String`, a body object from `@RestController`, and `ResponseEntity`.

## T10 — Error chain

Explain the order and distinct responsibilities of `ExceptionHandlerExceptionResolver`, `ResponseStatusExceptionResolver`, and `DefaultHandlerExceptionResolver`.

## T11 — Boundary diagnosis

Why can `@ControllerAdvice` handle a controller exception but fail to handle an exception thrown in an upstream security or Servlet filter?

## T12 — 404 taxonomy

Distinguish no handler, method-condition mismatch, controller domain not-found, and static-resource not-found.

## T13 — Boot ownership

What does Boot normally configure for Servlet MVC, and why can adding `@EnableWebMvc` materially change that behavior?

## T14 — Deployment

Explain the bootstrap difference between executable JAR and WAR deployment in the Spring Boot 2.5 baseline.

## T15 — Production incident

A request reaches the controller, but the client receives 406. Provide a diagnostic sequence covering `Accept`, mapping `produces`, return type, converters, and committed response.

# Post-test expected evidence

A complete answer should demonstrate:

- exact stage ownership rather than annotation lists;
- correct ordering of mapping, adaptation, argument resolution, invocation, and return handling;
- distinction between request parameters and request bodies;
- distinction between type conversion and validation;
- distinction between MVC exception handling and outer Servlet/filter failures;
- explicit exam-baseline versus current-version boundaries;
- ability to predict `MockMvc` evidence before running tests.

# Scoring

| Level | Result |
|---|---|
| 0 | Cannot reconstruct pipeline |
| 1 | Names annotations but confuses strategy ownership |
| 2 | Correct main sequence with gaps in conversion/error paths |
| 3 | Correct sequence and most diagnostic boundaries |
| 4 | Explains extension points, media negotiation, and failure ownership |
| 5 | Predicts lab results and transfers the model to new incidents |

# Progress recording

Record uncertain or incorrect cards, for example:

```bash
python .github/scripts/card_progress.py record \
  --card-id SPRING-MVC-B01-C014 \
  --outcome wrong-confusion \
  --confidence 2 \
  --note "Confused HandlerMethodArgumentResolver with HttpMessageConverter"
```

# Related material

- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Cards]]
- [[10_CONCEPTS/Spring/MVC/Spring MVC DispatcherServlet Visual Deep Dive]]
- [[50_LABS/Spring/SPRING-MVC-B01/README]]
