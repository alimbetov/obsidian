---
type: concept
domain: spring
subdomain: spring-boot
difficulty: advanced
status: learning
confidence: 0
interview: true
certification:
  - spring-2V0-72.22
exam_baseline:
  - spring-boot-2.5
current_delta:
  - spring-boot-current
production_relevance: critical
prerequisites:
  - "[[Spring Boot Bootstrap and Auto-configuration]]"
tags:
  - spring-boot
  - auto-configuration
  - visual-learning
---

# Spring Boot Auto-configuration Visual Deep Dive

> [!summary]
> Visual route: application launch → environment → context → configuration parsing → candidate discovery → condition evaluation → bean-definition contribution → refresh → runners/readiness.

# 1. Full bootstrap pipeline

```mermaid
flowchart LR
    MAIN["main(args)"] --> APP["SpringApplication"]
    APP --> TYPE["Determine WebApplicationType"]
    TYPE --> ENV["Prepare Environment"]
    ENV --> CTX["Create ApplicationContext"]
    CTX --> LOAD["Load primary sources"]
    LOAD --> REFRESH["refresh()"]
    REFRESH --> EVENTS["Started events"]
    EVENTS --> RUNNERS["ApplicationRunner / CommandLineRunner"]
    RUNNERS --> READY["Application ready"]
```

# 2. `@SpringBootApplication` composition

```mermaid
flowchart TB
    SBA["@SpringBootApplication"] --> SBC["@SpringBootConfiguration"]
    SBA --> EAC["@EnableAutoConfiguration"]
    SBA --> CS["@ComponentScan"]
    SBC --> USER["Primary user configuration"]
    EAC --> AUTO["Boot candidate imports"]
    CS --> COMPONENTS["Application components"]
```

# 3. Three different discovery paths

```mermaid
flowchart LR
    APP["Application class"] --> SCAN["Component scan"]
    APP --> IMPORT["Explicit @Import"]
    APP --> AUTO["Auto-configuration import selector"]
    SCAN --> USERBEANS["User component definitions"]
    IMPORT --> EXPLICIT["Explicit configuration definitions"]
    AUTO --> DEFAULTS["Conditional Boot definitions"]
```

# 4. Main-class placement

```mermaid
flowchart TB
    ROOT["com.example.Application"] --> ORD["com.example.orders"]
    ROOT --> PAY["com.example.payments"]
    ROOT --> CFG["com.example.config"]
    BAD["default package Application"] --> BROAD["Broad/unpredictable scanning"]
```

# 5. Environment before ordinary beans

```mermaid
sequenceDiagram
    participant SA as SpringApplication
    participant E as Environment
    participant C as ApplicationContext
    participant B as Ordinary beans

    SA->>E: create and configure
    SA->>E: load configuration data
    SA->>C: create context with Environment
    C->>B: evaluate conditions and create beans
```

# 6. Application type decision

```mermaid
flowchart TD
    CP["Classpath + explicit setting"] --> TYPE{"WebApplicationType"}
    TYPE --> NONE["NONE"]
    TYPE --> SERVLET["SERVLET"]
    TYPE --> REACTIVE["REACTIVE"]
    SERVLET --> SCTX["Servlet web context"]
    REACTIVE --> RCTX["Reactive web context"]
    NONE --> GCTX["Generic context"]
```

# 7. Auto-configuration candidate pipeline

```mermaid
flowchart LR
    ENABLE["@EnableAutoConfiguration"] --> SELECTOR["AutoConfigurationImportSelector"]
    SELECTOR --> LOAD["Load candidates"]
    LOAD --> DEDUP["Remove duplicates"]
    DEDUP --> EXCL["Apply exclusions"]
    EXCL --> FILTER["Apply filters/metadata"]
    FILTER --> IMPORT["Import selected configurations"]
```

# 8. Boot 2.x versus current registration

```mermaid
flowchart TB
    B2["Boot 2.x"] --> SF["META-INF/spring.factories"]
    SF --> EKEY["EnableAutoConfiguration key"]
    CURRENT["Current Boot"] --> IMP["META-INF/spring/...AutoConfiguration.imports"]
    IMP --> LINES["One auto-configuration class per line"]
```

# 9. Deferred import effect

```mermaid
sequenceDiagram
    participant P as Configuration parser
    participant U as User configuration
    participant A as Auto-configuration selector
    participant R as BeanDefinition registry

    P->>U: parse user definitions
    P->>A: process deferred imports
    A->>R: inspect visible definitions/metadata
    A->>R: contribute matching auto-configurations
```

# 10. Condition composition

```mermaid
flowchart TD
    CAND["Candidate auto-configuration"] --> CLASS{"Required class present?"}
    CLASS -->|"No"| SKIP["Do not import/register"]
    CLASS -->|"Yes"| WEB{"Application type matches?"}
    WEB -->|"No"| SKIP
    WEB -->|"Yes"| PROP{"Property condition matches?"}
    PROP -->|"No"| SKIP
    PROP -->|"Yes"| BEAN{"Required/missing bean condition matches?"}
    BEAN -->|"No"| SKIP
    BEAN -->|"Yes"| APPLY["Contribute BeanDefinitions"]
```

# 11. Classpath condition

```mermaid
flowchart LR
    DEP["Starter/library dependency"] --> CP["Class appears on classpath"]
    CP --> ONCLASS["@ConditionalOnClass matches"]
    ONCLASS --> CONFIG["Optional configuration becomes eligible"]
```

# 12. Missing-bean back-off

```mermaid
flowchart TD
    USER{"User bean exists?"}
    USER -->|"Yes"| BACK["Auto-configuration backs off"]
    USER -->|"No"| DEFAULT["Register default bean"]
```

# 13. Property condition

```mermaid
flowchart TD
    ENV["Environment"] --> KEY["acme.enabled"]
    KEY --> VAL{"Value / havingValue / matchIfMissing"}
    VAL -->|"Match"| APPLY["Enable configuration"]
    VAL -->|"No match"| SKIP["Skip configuration"]
```

# 14. Match does not guarantee successful creation

```mermaid
flowchart LR
    MATCH["Conditions match"] --> DEF["BeanDefinition registered"]
    DEF --> CREATE["Bean creation during refresh"]
    CREATE --> FAIL["Constructor/binding/network validation can fail"]
```

# 15. Positive versus negative condition report

```mermaid
flowchart TB
    REPORT["Condition Evaluation Report"] --> POS["Positive matches"]
    REPORT --> NEG["Negative matches"]
    REPORT --> EXCL["Exclusions"]
    REPORT --> UNCOND["Unconditional classes"]
```

# 16. Diagnostic decision tree

```mermaid
flowchart TD
    MISS["Expected bean missing"] --> DISC{"Candidate discovered?"}
    DISC -->|"No"| REG["Check spring.factories/imports and version"]
    DISC -->|"Yes"| EX{"Excluded?"}
    EX -->|"Yes"| REMOVE["Check annotation/property exclusion"]
    EX -->|"No"| COND{"Which condition failed?"}
    COND --> CLASS["Classpath"]
    COND --> PROP["Property"]
    COND --> BEAN["Existing/missing bean"]
    COND --> WEB["Application type"]
```

# 17. User override path

```mermaid
sequenceDiagram
    participant U as User configuration
    participant R as Registry
    participant A as Auto-configuration

    U->>R: register custom Client bean definition
    A->>R: evaluate @ConditionalOnMissingBean(Client)
    R-->>A: matching bean already present
    A-->>R: do not register default Client
```

# 18. Exclusion paths

```mermaid
flowchart LR
    ANN["@SpringBootApplication exclude"] --> DISABLED["Auto-config disabled"]
    NAME["excludeName"] --> DISABLED
    PROP["spring.autoconfigure.exclude"] --> DISABLED
```

# 19. Ordering boundary

```mermaid
flowchart LR
    A["Auto-config A definitions"] --> B["Auto-config B definitions"]
    ORDER["@AutoConfigureBefore/After"] --> A
    ORDER --> B
    BEANS["Bean instantiation order"] --> DEPS["Dependencies / @DependsOn"]
```

# 20. Starter versus auto-configuration

```mermaid
flowchart LR
    STARTER["Starter POM/module"] --> DEPS["Dependency set"]
    DEPS --> CLASSES["Classpath evidence"]
    CLASSES --> AUTO["Auto-configuration conditions"]
    AUTO --> BEANS["Default beans"]
```

# 21. Dependency-management boundary

```mermaid
flowchart TD
    BOM["Boot dependency management"] --> VERSION["Chooses compatible versions"]
    DECL["Declared dependency"] --> PRESENT["Adds dependency"]
    VERSION --> BUILD["Resolved build graph"]
    PRESENT --> BUILD
```

# 22. Custom Boot 2.x auto-configuration

```mermaid
flowchart LR
    CLASS["@Configuration auto-config class"] --> CONDS["Conditional annotations"]
    CONDS --> BEANS["@Bean defaults"]
    SF["spring.factories"] --> CLASS
    USER["User bean"] --> BACKOFF["Missing-bean condition backs off"]
```

# 23. Current custom auto-configuration

```mermaid
flowchart LR
    AC["@AutoConfiguration"] --> CONDS["Conditions"]
    IMPORTS["AutoConfiguration.imports"] --> AC
    AC --> BEANS["Conditional default beans"]
```

# 24. `ApplicationContextRunner` test matrix

```mermaid
flowchart TB
    RUNNER["ApplicationContextRunner"] --> DEFAULT["Default context"]
    RUNNER --> PROP["withPropertyValues"]
    RUNNER --> USER["withBean / user configuration"]
    RUNNER --> FILTER["FilteredClassLoader"]
    DEFAULT --> ASSERT["Assert bean presence/absence"]
    PROP --> ASSERT
    USER --> ASSERT
    FILTER --> ASSERT
```

# 25. Failure analyzer path

```mermaid
flowchart LR
    EX["Startup exception"] --> ANALYZER["Matching FailureAnalyzer"]
    ANALYZER --> DESC["Description"]
    ANALYZER --> ACTION["Suggested action"]
    DESC --> LOG["Failure report"]
    ACTION --> LOG
```

# 26. Startup events and runners

```mermaid
flowchart LR
    START["Starting"] --> ENV["Environment prepared"]
    ENV --> CTX["Context prepared/loaded"]
    CTX --> REFRESH["Context refreshed"]
    REFRESH --> STARTED["Application started"]
    STARTED --> RUNNER["Runners"]
    RUNNER --> READY["Ready"]
    START --> FAIL["Failed event on startup error"]
```

# 27. Runner comparison

```mermaid
flowchart TD
    ARGS["Startup arguments"] --> CLR["CommandLineRunner: String[]"]
    ARGS --> AR["ApplicationRunner: ApplicationArguments"]
    CLR --> ORDER["Ordered execution"]
    AR --> ORDER
```

# 28. Lazy initialization trade-off

```mermaid
flowchart LR
    LAZY["Global lazy initialization"] --> FAST["Less startup work"]
    LAZY --> LATE["Failures move to first use"]
    LAZY --> FIRST["First-request latency"]
```

# 29. Embedded servlet startup

```mermaid
flowchart LR
    SERVLETCTX["Servlet application context"] --> FACTORY["ServletWebServerFactory bean"]
    FACTORY --> SERVER["Embedded server"]
    SERVER --> DISPATCH["DispatcherServlet infrastructure"]
```

# 30. Worked example — custom client

```mermaid
flowchart TD
    LIB["Acme library on classpath"] --> ONCLASS["@ConditionalOnClass"]
    PROP["acme.enabled=true"] --> ONPROP["@ConditionalOnProperty"]
    USER{"Custom AcmeClient bean?"} -->|"No"| DEFAULT["Create default AcmeClient"]
    USER -->|"Yes"| KEEP["Keep user bean"]
    ONCLASS --> USER
    ONPROP --> USER
```

Evidence plan:

```text
ApplicationContextRunner default case
property-disabled case
FilteredClassLoader missing-library case
user-bean back-off case
invalid-properties failure case
```

# 31. Interview explanation map

```mermaid
flowchart LR
    Q["How does Boot auto-configure?"] --> E["EnableAutoConfiguration"]
    E --> C["Discover candidates"]
    C --> F["Apply exclusions/filters"]
    F --> COND["Evaluate conditions"]
    COND --> DEF["Register definitions"]
    DEF --> REF["Refresh and create beans"]
    REF --> DIAG["Condition report + runner tests"]
```

## Route navigation

- **Canonical:** [[10_CONCEPTS/Spring/Boot/Spring Boot Bootstrap and Auto-configuration]]
- **Roadmap:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B01/SPRING-BOOT-B01 Roadmap]]
- **Cards:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B01/SPRING-BOOT-B01 Cards]]
- **Cases:** [[40_PRODUCTION_CASES/Spring/Spring Boot Auto-configuration Production Cases]]
- **Lab:** [[50_LABS/Spring/SPRING-BOOT-B01/README]]
- **Sources:** [[98_SOURCES/Spring Boot Auto-configuration Sources]]
