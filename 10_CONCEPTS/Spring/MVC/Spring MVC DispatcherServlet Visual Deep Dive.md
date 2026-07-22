---
type: visual-deep-dive
domain: spring
subdomain: spring-mvc
route: SPRING-MVC-B01
status: published
visual_diagrams: 30
objectives:
  - SPRING-3.1.1
  - SPRING-3.1.2
  - SPRING-3.1.3
  - SPRING-3.1.4
exam_baseline:
  - spring-framework-5.3
  - spring-boot-2.5
current_delta:
  - spring-framework-current
tags:
  - spring-mvc
  - dispatcher-servlet
  - visual-learning
---

# Spring MVC DispatcherServlet Visual Deep Dive

> [!summary]
> Thirty distinct models for reconstructing the Servlet request path, strategy selection, annotated-controller invocation, body/view rendering, exception translation, and diagnostic boundaries.

# Route navigation

- [[10_CONCEPTS/Spring/MVC/DispatcherServlet and Annotated Controller Pipeline]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Roadmap]]
- [[01_MAPS/Spring MVC DispatcherServlet Map.canvas]]

# 1. End-to-end topology

```mermaid
flowchart LR
    C[Client] --> CT[Servlet container]
    CT --> F[Filter chain]
    F --> DS[DispatcherServlet]
    DS --> HM[HandlerMapping]
    HM --> HA[HandlerAdapter]
    HA --> CTRL[Controller method]
    CTRL --> RV[Return-value handler]
    RV --> RESP[HTTP response]
```

# 2. Front-controller responsibility split

```mermaid
flowchart TB
    DS[DispatcherServlet] --> MAP[Find handler]
    DS --> INV[Invoke handler]
    DS --> ERR[Resolve exception]
    DS --> REN[Render result]
    MAP --> HM[HandlerMapping]
    INV --> HA[HandlerAdapter]
    ERR --> HER[HandlerExceptionResolver]
    REN --> VR[ViewResolver or message converter]
```

# 3. Servlet versus MVC boundaries

```mermaid
flowchart LR
    NET[Network] --> CONT[Container]
    CONT --> FILTER[Servlet Filter]
    FILTER --> SERVLET[DispatcherServlet]
    SERVLET --> INT[HandlerInterceptor]
    INT --> METHOD[Controller method]
```

# 4. Traditional context hierarchy

```mermaid
flowchart TB
    ROOT[Root WebApplicationContext] --> SVC[Services]
    ROOT --> REPO[Repositories]
    ROOT --> INFRA[Infrastructure]
    CHILD[DispatcherServlet child context] --> CTRL[Controllers]
    CHILD --> MVC[MVC strategies]
    CHILD -. can see .-> ROOT
    ROOT -. cannot see child-only beans .-> CHILD
```

# 5. DispatcherServlet initialization

```mermaid
sequenceDiagram
    participant Container
    participant DispatcherServlet
    participant Context
    participant Strategies
    Container->>DispatcherServlet: init
    DispatcherServlet->>Context: create or obtain WebApplicationContext
    Context-->>DispatcherServlet: refreshed context
    DispatcherServlet->>Strategies: detect HandlerMappings, Adapters, Resolvers
    Strategies-->>DispatcherServlet: ordered strategy lists
```

# 6. Special strategy beans

```mermaid
mindmap
  root((DispatcherServlet))
    HandlerMapping
    HandlerAdapter
    HandlerExceptionResolver
    ViewResolver
    LocaleResolver
    MultipartResolver
    FlashMapManager
    RequestToViewNameTranslator
```

# 7. Request-processing sequence

```mermaid
sequenceDiagram
    participant DS as DispatcherServlet
    participant HM as HandlerMapping
    participant HA as HandlerAdapter
    participant HR as HandlerExceptionResolver
    participant VR as View or Body path
    DS->>HM: getHandler(request)
    HM-->>DS: HandlerExecutionChain
    DS->>HA: handle(request, response, handler)
    alt successful invocation
        HA-->>DS: ModelAndView or handled response
        DS->>VR: render if needed
    else exception
        DS->>HR: resolveException
        HR-->>DS: resolved result or null
    end
```

# 8. Ordered HandlerMapping selection

```mermaid
flowchart TD
    R[Request] --> M1[HandlerMapping order 0]
    M1 -->|match| USE1[Use handler chain]
    M1 -->|null| M2[HandlerMapping order 1]
    M2 -->|match| USE2[Use handler chain]
    M2 -->|null| NONE[No handler]
```

# 9. Startup mapping registration

```mermaid
flowchart LR
    BEANS[Controller beans] --> SCAN[Inspect request-mapping annotations]
    SCAN --> INFO[Create RequestMappingInfo]
    INFO --> METHOD[Create HandlerMethod]
    METHOD --> REG[Mapping registry]
```

# 10. RequestMappingInfo condition set

```mermaid
flowchart TB
    REQ[Incoming request] --> PATH[Path condition]
    PATH --> VERB[HTTP method condition]
    VERB --> PARAMS[Parameter condition]
    PARAMS --> HEADERS[Header condition]
    HEADERS --> CONS[Consumes condition]
    CONS --> PROD[Produces condition]
    PROD --> MATCH[Candidate mapping]
```

# 11. Best-match decision

```mermaid
flowchart TD
    CANDS[Matching candidates] --> SPEC[Compare specificity]
    SPEC --> ONE{Single best match?}
    ONE -->|yes| SELECT[Select HandlerMethod]
    ONE -->|no| AMB[Ambiguous mapping failure]
```

# 12. HandlerExecutionChain

```mermaid
flowchart LR
    CHAIN[HandlerExecutionChain] --> I1[Interceptor 1]
    I1 --> I2[Interceptor 2]
    I2 --> H[Handler]
    H --> P2[postHandle 2]
    P2 --> P1[postHandle 1]
    P1 --> AC[afterCompletion reverse order]
```

# 13. Interceptor short-circuit

```mermaid
flowchart TD
    PRE1[preHandle 1] -->|true| PRE2[preHandle 2]
    PRE2 -->|true| H[Invoke handler]
    PRE2 -->|false| STOP[Stop chain]
    STOP --> RESP[Interceptor owns response]
```

# 14. HandlerAdapter selection

```mermaid
flowchart TD
    H[Selected handler] --> A1{Adapter 1 supports?}
    A1 -->|yes| INV1[Adapter 1 invokes]
    A1 -->|no| A2{Adapter 2 supports?}
    A2 -->|yes| INV2[Adapter 2 invokes]
    A2 -->|no| FAIL[No adapter for handler]
```

# 15. Annotated method adapter pipeline

```mermaid
flowchart LR
    HM[HandlerMethod] --> MODEL[Initialize model]
    MODEL --> BINDER[Run InitBinder methods]
    BINDER --> ARGS[Resolve arguments]
    ARGS --> CALL[Invoke Java method]
    CALL --> RETURN[Handle return value]
```

# 16. Argument resolver chain

```mermaid
flowchart TD
    P[Method parameter] --> R1{Resolver 1 supports?}
    R1 -->|no| R2{Resolver 2 supports?}
    R2 -->|no| RN[Continue ordered resolvers]
    R1 -->|yes| V1[Resolver creates value]
    R2 -->|yes| V2[Resolver creates value]
    RN -->|none| UNSUP[Unsupported parameter]
```

# 17. PathVariable path

```mermaid
flowchart LR
    URI[/catalog/42] --> PAT[/catalog/{id}]
    PAT --> VAR[id equals 42]
    VAR --> CONV[String to Long]
    CONV --> ARG[Controller argument]
```

# 18. RequestParam path

```mermaid
flowchart LR
    Q[Query or form parameter] --> LOOKUP[Name lookup]
    LOOKUP --> REQUIRED{Present or default?}
    REQUIRED -->|missing required| BAD[Binding exception]
    REQUIRED -->|value available| CONV[Type conversion]
    CONV --> ARG[Controller argument]
```

# 19. ModelAttribute binding

```mermaid
flowchart LR
    PARAMS[Request parameters] --> OBJ[Create or obtain object]
    OBJ --> BIND[WebDataBinder]
    BIND --> CONVERT[ConversionService]
    CONVERT --> VALIDATE[Validator]
    VALIDATE --> MODEL[Object plus BindingResult in model]
```

# 20. RequestBody conversion

```mermaid
flowchart LR
    BODY[HTTP body bytes] --> CT[Content-Type]
    CT --> SELECT[Select HttpMessageConverter]
    SELECT --> JAVA[Deserialize Java object]
    JAVA --> VALID[Optional validation]
    VALID --> ARG[Controller argument]
```

# 21. Conversion versus validation

```mermaid
flowchart TD
    RAW[Raw string] --> CONV{Can convert to target type?}
    CONV -->|no| TYPEERR[Type mismatch]
    CONV -->|yes| VALUE[Typed value]
    VALUE --> VALID{Satisfies constraints?}
    VALID -->|no| VALERR[Validation error]
    VALID -->|yes| OK[Invoke controller]
```

# 22. BindingResult adjacency

```mermaid
flowchart LR
    DTO[Validated DTO parameter] --> BR[Immediately following BindingResult]
    BR --> CAPTURE[Errors captured for controller]
    DTO --> OTHER[Unrelated parameter before BindingResult]
    OTHER --> EX[Errors raise exception instead]
```

# 23. Return-value handler chain

```mermaid
flowchart TD
    R[Raw method return value] --> H1{ModelAndView?}
    H1 -->|yes| MV[Model and view path]
    H1 -->|no| H2{ResponseBody or ResponseEntity?}
    H2 -->|yes| BODY[Message converter path]
    H2 -->|no| H3{View-name String?}
    H3 -->|yes| VIEW[ViewResolver path]
    H3 -->|no| NEXT[Other return-value handlers]
```

# 24. RestController body path

```mermaid
flowchart LR
    RET[Java return object] --> RVH[ResponseBody return-value handler]
    RVH --> NEG[Media-type negotiation]
    NEG --> CONV[HttpMessageConverter]
    CONV --> BYTES[Response bytes]
```

# 25. Controller view path

```mermaid
flowchart LR
    NAME[Logical view name] --> V1[ViewResolver order 0]
    V1 -->|null| V2[ViewResolver order 1]
    V1 -->|View| RENDER[Render model]
    V2 -->|View| RENDER
    RENDER --> RESPONSE[HTML response]
```

# 26. Exception resolution chain

```mermaid
flowchart TD
    EX[Exception] --> E1[ExceptionHandlerExceptionResolver]
    E1 -->|resolved| OUT[Error response or ModelAndView]
    E1 -->|null| E2[ResponseStatusExceptionResolver]
    E2 -->|resolved| OUT
    E2 -->|null| E3[DefaultHandlerExceptionResolver]
    E3 -->|resolved| OUT
    E3 -->|null| PROP[Propagate failure]
```

# 27. ControllerAdvice applicability

```mermaid
flowchart LR
    ADV[ControllerAdvice] --> SEL{Applies to controller?}
    SEL -->|yes| MODEL[ModelAttribute methods]
    SEL -->|yes| BIND[InitBinder methods]
    SEL -->|yes| ERR[ExceptionHandler methods]
    SEL -->|no| SKIP[Advice skipped]
```

# 28. No-handler diagnostic tree

```mermaid
flowchart TD
    R[404 symptom] --> REACH{Reached expected app?}
    REACH -->|no| ROUTE[Gateway or container routing]
    REACH -->|yes| MAP{HandlerMapping match?}
    MAP -->|no| COND[Inspect path, method, params, headers, consumes, produces]
    MAP -->|yes| DOMAIN{Controller returned domain not-found?}
    DOMAIN -->|yes| APP[Application exception policy]
    DOMAIN -->|no| RESOURCE[Static resource or later response path]
```

# 29. Embedded JAR versus WAR

```mermaid
flowchart TB
    CODE[Same controller code] --> JAR[Executable JAR]
    CODE --> WAR[WAR deployment]
    JAR --> EMB[Boot starts embedded container]
    WAR --> EXT[External container starts application]
    EMB --> DS1[DispatcherServlet]
    EXT --> DS2[DispatcherServlet]
```

# 30. Full production diagnostic path

```mermaid
flowchart LR
    A[Reach app] --> B[Filter chain]
    B --> C[Servlet mapping]
    C --> D[HandlerMapping]
    D --> E[Interceptor chain]
    E --> F[HandlerAdapter]
    F --> G[Argument resolvers]
    G --> H[Conversion and validation]
    H --> I[Controller]
    I --> J[Return-value handler]
    J --> K[Converter or ViewResolver]
    K --> L[ExceptionResolver if failed]
    L --> M[Committed status, headers, body]
```

# Visual recall prompts

1. Reconstruct diagrams 7, 15, 16, 23, and 26 without notes.
2. Explain why `HandlerMapping` cannot deserialize JSON.
3. Explain why `@ControllerAdvice` cannot catch a failure in an upstream Servlet filter.
4. Trace a `String` query parameter through conversion and validation.
5. Trace the same controller result through body rendering and view rendering.

# Related material

- [[10_CONCEPTS/Spring/MVC/DispatcherServlet and Annotated Controller Pipeline]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Assessment]]
- [[50_LABS/Spring/SPRING-MVC-B01/README]]
