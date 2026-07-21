---
type: concept
domain: spring
subdomain: spring-boot-configuration
status: learning
difficulty: advanced
certification:
  - spring-2V0-72.22
objectives:
  - SPRING-1.3.1
  - SPRING-1.3.2
  - SPRING-6.2.1
exam_baseline:
  - spring-boot-2.5.15
current_delta:
  - spring-boot-current
tags:
  - spring-boot
  - configuration-properties
  - visual-learning
---

# Spring Boot Configuration Visual Deep Dive

> [!summary]
> Visual route: raw configuration sources → Config Data → ordered Environment → profiles/documents → resolved key → typed binding → conversion/validation → bean behavior → diagnostics.

# 1. Complete configuration pipeline

```mermaid
flowchart LR
    RAW["Files / env / system / CLI / tests"] --> CONFIG["Config Data processing"]
    CONFIG --> SOURCES["Ordered PropertySources"]
    SOURCES --> RESOLVE["Resolve canonical key"]
    RESOLVE --> BIND["Placeholder or Binder"]
    BIND --> CONVERT["Type conversion"]
    CONVERT --> VALIDATE["Validation"]
    VALIDATE --> BEAN["Configured bean"]
```

**How to read:** a wrong runtime value can originate at discovery, source ordering, key normalization, conversion or validation. Reading only one YAML file inspects one stage.

# 2. Ordered PropertySource chain

```mermaid
flowchart TB
    HIGH["Higher precedence"] --> CLI["Command-line properties"]
    CLI --> TEST["Test overrides"]
    TEST --> SYS["System properties / environment"]
    SYS --> EXTERNAL["External Config Data"]
    EXTERNAL --> PACKAGED["Packaged Config Data"]
    PACKAGED --> DEFAULTS["Code defaults"]
    DEFAULTS --> LOW["Lower precedence"]
```

The exact category order is version-sensitive; the invariant is that the first/highest source containing the key wins.

# 3. Resolution decision

```mermaid
flowchart TD
    KEY["client.timeout"] --> S1{"Present in CLI?"}
    S1 -->|"Yes"| V1["Use CLI value"]
    S1 -->|"No"| S2{"Present in env/system?"}
    S2 -->|"Yes"| V2["Use env/system value"]
    S2 -->|"No"| S3{"Present in external config?"}
    S3 -->|"Yes"| V3["Use external value"]
    S3 -->|"No"| V4["Use packaged/default value or missing"]
```

# 4. Config Data bootstrap timing

```mermaid
sequenceDiagram
    participant SA as SpringApplication
    participant CD as ConfigDataEnvironmentPostProcessor
    participant ENV as Environment
    participant CTX as ApplicationContext
    participant BEAN as Beans

    SA->>ENV: create initial environment
    SA->>CD: process config data
    CD->>CD: locate and load documents/imports
    CD->>ENV: add ordered property sources
    SA->>CTX: create context with prepared environment
    CTX->>BEAN: bind/create beans
```

# 5. Default and external locations

```mermaid
flowchart LR
    JAR["Packaged application config"] --> DEFAULT["Safe defaults"]
    EXT["External working-dir/config locations"] --> OVERRIDE["Deployment overrides"]
    DEFAULT --> ENV["Environment"]
    OVERRIDE --> ENV
```

# 6. Replace versus extend locations

```mermaid
flowchart TD
    CHOICE{"Configuration option"}
    CHOICE --> LOCATION["spring.config.location"]
    CHOICE --> ADDITIONAL["spring.config.additional-location"]
    LOCATION --> REPLACE["Replace default search locations"]
    ADDITIONAL --> EXTEND["Keep defaults and add locations"]
```

# 7. Import graph

```mermaid
flowchart LR
    APP["application.properties"] --> IMPORT["spring.config.import"]
    IMPORT --> DEV["optional:file:developer.properties"]
    IMPORT --> TREE["configtree:/run/secrets/"]
    DEV --> ENV["Environment"]
    TREE --> ENV
```

# 8. Mandatory versus optional import

```mermaid
flowchart TD
    LOCATION["Imported location"] --> EXISTS{"Exists?"}
    EXISTS -->|"Yes"| LOAD["Load property source"]
    EXISTS -->|"No + optional:"| CONTINUE["Continue without source"]
    EXISTS -->|"No + mandatory"| FAIL["Fail startup"]
```

# 9. Config tree mapping

```mermaid
flowchart LR
    DIR["/run/secrets/"] --> USERFILE["username file"]
    DIR --> PASSFILE["password file"]
    USERFILE --> USERKEY["username property"]
    PASSFILE --> PASSKEY["password property"]
    USERKEY --> BINDER["Typed binding"]
    PASSKEY --> BINDER
```

# 10. Multi-document activation

```mermaid
flowchart TD
    FILE["application.yaml"] --> D1["Document 1: defaults"]
    FILE --> D2["Document 2: on-profile=prod"]
    PROFILE{"prod active?"}
    PROFILE -->|"No"| ONLY["Use defaults"]
    PROFILE -->|"Yes"| BOTH["Use defaults + prod overrides"]
    D1 --> ONLY
    D1 --> BOTH
    D2 --> BOTH
```

# 11. Profile-specific file composition

```mermaid
flowchart LR
    BASE["application.properties"] --> MERGE["Resolved configuration"]
    PROD["application-prod.properties"] --> MERGE
    ACTIVE["spring.profiles.active=prod"] --> PROD
```

# 12. Default profile path

```mermaid
flowchart TD
    START["Environment preparation"] --> ACTIVE{"Any active profiles?"}
    ACTIVE -->|"Yes"| USEACTIVE["Use active profiles"]
    ACTIVE -->|"No"| DEFAULT["Use default profile"]
```

# 13. Profile group

```mermaid
flowchart LR
    PROD["production profile"] --> DB["proddb"]
    PROD --> MQ["prodmq"]
    PROD --> OBS["prod-observability"]
```

# 14. Flat key model

```mermaid
flowchart LR
    YAML["client.retry.max-attempts: 3"] --> KEY["client.retry.max-attempts"]
    PROPS["client.retry.max-attempts=3"] --> KEY
    KEY --> ENV["Environment"]
```

# 15. `@Value` path

```mermaid
flowchart LR
    KEY["${client.timeout:5s}"] --> PLACEHOLDER["Placeholder resolution"]
    PLACEHOLDER --> CONVERT["Convert to Duration"]
    CONVERT --> PARAM["Single injection point"]
```

# 16. `@ConfigurationProperties` path

```mermaid
flowchart LR
    NS["client.* namespace"] --> BINDER["Binder"]
    BINDER --> ROOT["ClientProperties"]
    ROOT --> ENDPOINT["URI endpoint"]
    ROOT --> TIMEOUT["Duration timeout"]
    ROOT --> RETRY["Nested Retry"]
    ROOT --> SERVERS["List<URI>"]
```

# 17. Choosing binding style

```mermaid
flowchart TD
    NEED{"Configuration shape"}
    NEED -->|"One unrelated value / expression"| VALUE["@Value"]
    NEED -->|"Coherent namespace"| PROPS["@ConfigurationProperties"]
    PROPS --> TYPE["Typed conversion"]
    PROPS --> VALID["Validation"]
    PROPS --> META["Metadata"]
```

# 18. Registration paths

```mermaid
flowchart TD
    TYPE["@ConfigurationProperties type"] --> ENABLE["@EnableConfigurationProperties"]
    TYPE --> SCAN["@ConfigurationPropertiesScan"]
    TYPE --> BEAN["Explicit @Bean"]
    ENABLE --> REGISTERED["Bindable bean"]
    SCAN --> REGISTERED
    BEAN --> REGISTERED
```

# 19. Relaxed binding normalization

```mermaid
flowchart LR
    KEBAB["client.retry.max-attempts"] --> CANON["Canonical property"]
    CAMEL["client.retry.maxAttempts"] --> CANON
    ENVVAR["CLIENT_RETRY_MAXATTEMPTS"] --> CANON
    CANON --> FIELD["maxAttempts"]
```

# 20. Nested binding

```mermaid
flowchart TB
    PREFIX["client"] --> RETRY["retry"]
    RETRY --> MAX["max-attempts"]
    RETRY --> BACKOFF["backoff"]
    PREFIX --> TLS["tls"]
    TLS --> ENABLED["enabled"]
```

# 21. Collection binding

```mermaid
flowchart LR
    INDEX0["client.servers[0]"] --> LIST["List<URI>"]
    INDEX1["client.servers[1]"] --> LIST
    HEADER["client.headers.X-Tenant"] --> MAP["Map<String,String>"]
```

# 22. Conversion pipeline

```mermaid
flowchart LR
    RAW["String source value"] --> SERVICE["ConversionService / Binder converters"]
    SERVICE --> DURATION["Duration"]
    SERVICE --> SIZE["DataSize"]
    SERVICE --> URI["URI"]
    SERVICE --> ENUM["Enum"]
    SERVICE --> CUSTOM["Custom domain type"]
```

# 23. Validation failure path

```mermaid
sequenceDiagram
    participant ENV as Environment
    participant B as Binder
    participant V as Validator
    participant CTX as Context refresh

    ENV->>B: resolved client.* values
    B->>V: typed ClientProperties
    V-->>B: constraint violations
    B-->>CTX: bind/validation exception
    CTX-->>CTX: fail startup with report
```

# 24. Constructor binding version boundary

```mermaid
flowchart TB
    B25["Boot 2.5 exam baseline"] --> ANNO["Explicit @ConstructorBinding common"]
    CURRENT["Current Boot"] --> INFER["Single constructor can be inferred"]
    CURRENT --> RECORD["Records supported naturally"]
```

# 25. Unknown property diagnostic

```mermaid
flowchart TD
    SUPPLIED["client.timout=5s typo"] --> ENV["Environment contains key"]
    ENV --> BIND{"Any binding target consumes it?"}
    BIND -->|"No"| UNUSED["Application may still start"]
    BIND -->|"Strict/validated contract"| FAIL["Detect mismatch"]
```

# 26. Wrong value diagnostic tree

```mermaid
flowchart TD
    WRONG["Resolved value is wrong"] --> KEY{"Canonical key correct?"}
    KEY -->|"No"| FIXKEY["Fix spelling/relaxed form"]
    KEY -->|"Yes"| SOURCES{"Which sources contain key?"}
    SOURCES --> ORDER["Inspect precedence and origin"]
    ORDER --> PROFILE{"Expected profile/document active?"}
    PROFILE -->|"No"| ACTIVATE["Fix profile activation"]
    PROFILE -->|"Yes"| BIND{"Conversion/binding succeeds?"}
    BIND -->|"No"| CONVERT["Fix type/unit/converter/validation"]
    BIND -->|"Yes"| WINNER["Remove or document winning override"]
```

# 27. Secret exposure path

```mermaid
flowchart LR
    SECRET["Secret source"] --> ENV["Environment"]
    ENV --> BEAN["Credentials bean"]
    ENV --> ACTUATOR["env/configprops endpoints"]
    BEAN --> LOG["toString/logging"]
    ACTUATOR --> RISK["Exposure risk"]
    LOG --> RISK
    RISK --> CONTROLS["Sanitization + security + no Git secrets"]
```

# 28. Test override path

```mermaid
flowchart LR
    BASE["application-test.properties"] --> ENV["Test Environment"]
    TESTPROP["@TestPropertySource"] --> ENV
    BOOTPROP["@SpringBootTest properties"] --> ENV
    DYNAMIC["@DynamicPropertySource"] --> ENV
    ENV --> CACHE["Merged context cache key"]
```

# 29. Worked incident

```mermaid
flowchart TD
    SYMPTOM["Timeout is 30s, YAML says 3s"] --> ASSUME["Wrong: Boot ignored YAML"]
    SYMPTOM --> INSPECT["Inspect PropertySources"]
    INSPECT --> ENVVAR["CLIENT_TIMEOUT=30s"]
    ENVVAR --> WIN["Higher-priority value wins"]
    WIN --> REPAIR["Remove stale deployment override"]
    REPAIR --> PROOF["Context/deployment test asserts value and origin"]
```

# 30. Learning verification path

```mermaid
flowchart LR
    PRE["10-question pre-test"] --> CONCEPT["Canonical + visual route"]
    CONCEPT --> CARDS["35 stable card IDs"]
    CARDS --> CASES["Production incidents"]
    CASES --> LAB["Binding and validation tests"]
    LAB --> POST["15-question post-test"]
    POST --> PROGRESS["Per-card progress registry"]
```

## Route navigation

- **Canonical:** [[10_CONCEPTS/Spring/Boot/Spring Boot Externalized Configuration and Type-safe Binding]]
- **Roadmap:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Roadmap]]
- **Cards:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Cards]]
- **Assessment:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Assessment]]
- **Cases:** [[40_PRODUCTION_CASES/Spring/Spring Boot Configuration Production Cases]]
- **Lab:** [[50_LABS/Spring/SPRING-BOOT-B02/README]]
- **Sources:** [[98_SOURCES/Spring Boot Externalized Configuration Sources]]
