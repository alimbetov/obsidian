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
production_relevance: critical
tags:
  - spring-boot
  - externalized-configuration
  - configuration-properties
  - config-data
---

# Spring Boot Externalized Configuration and Type-safe Binding

> [!summary] За 30 секунд
> Spring Boot собирает упорядоченный `Environment` из нескольких property sources. Значение определяется не местом, где его впервые увидели, а источником с более высоким приоритетом. Config Data загружает файлы, imports, profile-specific documents и config trees до создания обычных beans. `@ConfigurationProperties` связывает namespace с типизированным объектом, поддерживает relaxed binding, nested structures, conversion и validation. Для диагностики нужно назвать key, resolved value, winning source, active profiles, binding target и validation outcome.

# Learning outcomes

После маршрута learner может:

1. Объяснить `Environment` и ordered `PropertySource` model.
2. Предсказать победившее значение при нескольких источниках.
3. Различить `spring.config.location`, `additional-location` и `spring.config.import`.
4. Объяснить profile activation, profile-specific documents и groups.
5. Выбрать `@Value` или `@ConfigurationProperties` по задаче.
6. Объяснить relaxed binding и type conversion.
7. Настроить immutable/constructor binding для Boot 2.5 baseline.
8. Добавить validation и доказать startup failure тестом.
9. Диагностировать неизвестный или переопределённый property.
10. Не раскрывать secrets через logs, Actuator или repository history.

# 1. Externalized configuration model

Application code should not hard-code environment-specific values such as:

```text
service endpoints
connection pools
timeouts
feature flags
credentials
batch sizes
management exposure
```

Spring Boot externalizes them into an `Environment` assembled before ordinary bean creation.

Stable mental model:

```text
raw sources
    ↓
ordered PropertySource chain
    ↓
resolved key/value
    ↓
placeholder or typed binding
    ↓
bean definition / bean instance behavior
```

# 2. `Environment` and `PropertySource`

`Environment` exposes:

```text
property lookup
active profiles
default profiles
required properties
conversion through configured services
```

A `PropertySource` is a named source of keys and values. The environment keeps sources in an ordered chain. When several sources contain the same key, the highest-precedence source wins.

```java
String timeout = environment.getProperty("client.timeout");
```

The resolved value is a projection of the whole source chain, not a field owned by one file.

# 3. Precedence: the rule, not a memorized partial list

The exam may ask about common precedence categories. The robust rule is:

> Later/higher-priority sources override lower-priority sources for the same key.

Common high-priority categories include command-line arguments, test overrides, system properties and environment variables; packaged configuration files are usually lower. Exact ordering is version-sensitive and must be checked against the Boot 2.5 reference for exam questions.

Diagnostic sequence:

```text
1. identify the key
2. list every source containing it
3. inspect source order
4. identify the winner
5. check conversion/binding after resolution
```

# 4. Command-line properties

Boot converts command-line options into environment properties:

```bash
java -jar app.jar --client.timeout=3s
```

Command-line properties commonly override file configuration. They can be disabled through `SpringApplication` configuration when a product must reject this override surface.

Exam trap: positional arguments without `--key=value` are application arguments, not automatically property entries.

# 5. System properties and environment variables

Examples:

```bash
java -Dclient.timeout=5s -jar app.jar
export CLIENT_TIMEOUT=7s
```

Environment-variable names are adapted to canonical property names. For typical relaxed binding:

```text
CLIENT_TIMEOUT
client.timeout
client-timeout
client_timeout
```

can refer to the same logical property where the binding rules permit it.

Operational caution: environment variables are process-visible in many platforms and are not automatically a secure secret store.

# 6. `application.properties` and YAML

Properties:

```properties
client.endpoint=https://api.example.test
client.timeout=5s
client.retry.max-attempts=3
```

YAML:

```yaml
client:
  endpoint: https://api.example.test
  timeout: 5s
  retry:
    max-attempts: 3
```

Both become flat property keys. YAML provides hierarchical authoring, not a different runtime namespace model.

Exam trap: loading both `.properties` and `.yml` with the same base name can create version-specific precedence surprises; prefer one format per location.

# 7. Config Data in Boot 2.5

Boot 2.4+ introduced Config Data processing. It runs early enough to influence profiles, auto-configuration conditions and bean definitions.

Conceptual pipeline:

```text
initial environment
    ↓
locate Config Data
    ↓
load files/imports/config trees
    ↓
activate documents/profiles
    ↓
publish property sources
    ↓
create and refresh context
```

Key infrastructure includes `ConfigDataEnvironmentPostProcessor`, location resolvers and loaders.

# 8. Default search locations

Boot searches standard locations for `application.properties` or `application.yaml`, including classpath and external working-directory locations. External locations generally override packaged defaults.

Use packaged configuration for safe defaults and external configuration for deployment-specific values.

Do not depend on an implicit working directory in containers without documenting it.

# 9. `spring.config.name`

Changes the base configuration name:

```bash
java -jar app.jar --spring.config.name=myservice
```

Boot then searches for `myservice.properties` or YAML equivalents in applicable locations.

This setting must be supplied early enough to affect Config Data discovery.

# 10. `spring.config.location`

`spring.config.location` replaces the default search locations with explicitly supplied locations.

```bash
--spring.config.location=optional:file:./custom/
```

Consequences:

```text
default locations are not searched unless explicitly included
location order affects override order
directories and files have different semantics
```

Exam trap: replacing locations is different from adding another location.

# 11. `spring.config.additional-location`

Adds locations while retaining default search locations.

```bash
--spring.config.additional-location=optional:file:/etc/myapp/
```

Use it when packaged/default configuration should remain a fallback.

Memory rule:

```text
location            → replace defaults
additional-location → extend defaults
```

# 12. `spring.config.import`

Imports additional Config Data from a configuration document:

```properties
spring.config.import=optional:file:./developer.properties
```

Imports are processed as Config Data and can participate in profile activation and property ordering.

Imports are not ordinary `@PropertySource` declarations and occur before bean creation.

# 13. `optional:` locations

Without `optional:`, a missing mandatory Config Data location can fail startup.

```properties
spring.config.import=optional:file:./local.properties
```

Use optional imports only when absence is genuinely valid. A required secret/configuration should fail fast rather than silently use an unsafe default.

# 14. Config trees

A config tree maps files in a directory to property keys, commonly for mounted secrets:

```text
/run/secrets/
├── username
└── password
```

Imported as:

```properties
spring.config.import=optional:configtree:/run/secrets/
```

Each filename becomes a property key and file content becomes its value.

Security boundary: a config tree avoids placing secret text in the main YAML, but access control and endpoint exposure still matter.

# 15. Multi-document files

A single properties or YAML resource can contain several logical documents. Documents can be activated under different conditions.

YAML separator:

```yaml
---
```

Properties multi-document separator in Boot Config Data uses the supported document marker syntax for that version.

Documents are processed in order, and later active documents can override earlier values.

# 16. Profile-specific files

Examples:

```text
application.properties
application-prod.properties
application-test.yaml
```

When a profile is active, profile-specific configuration augments/overrides non-profile configuration according to Config Data ordering.

Profile-specific files should not become a substitute for secret management or uncontrolled configuration duplication.

# 17. Active and default profiles

```properties
spring.profiles.active=prod
spring.profiles.default=local
```

- Active profiles are explicitly enabled.
- Default profiles apply when no active profile has been selected.

Programmatic and test-level profile activation can also contribute.

Exam trap: `spring.profiles.active` is not valid inside a document that is itself activated by `spring.config.activate.on-profile` in the same unrestricted way; profile activation rules are deliberately constrained to avoid recursive ambiguity.

# 18. Profile groups

A profile group lets one logical profile activate several members:

```properties
spring.profiles.group.production[0]=proddb
spring.profiles.group.production[1]=prodmq
```

Activating `production` activates its group members.

Use groups to express deployment compositions without duplicating all property files.

# 19. `spring.profiles.include`

Includes additional profiles in addition to active ones. It is different from replacing the active list.

Profile groups are often clearer for named deployment bundles; include is useful for additive activation.

Always verify final active profiles in the prepared environment.

# 20. Document activation

Config Data documents can activate under profile or cloud-platform conditions:

```yaml
spring:
  config:
    activate:
      on-profile: prod
```

This is the Boot 2.5 replacement for older profile-document patterns.

Do not mix legacy and Config Data activation rules without marking version behavior.

# 21. `@Value`

Example:

```java
@Component
class Client {
    Client(@Value("${client.timeout:5s}") Duration timeout) {
    }
}
```

Strengths:

```text
small number of unrelated values
simple placeholder or SpEL expression
local dependency visible at injection point
```

Weaknesses:

```text
scattered strings
weak namespace ownership
limited metadata
harder bulk validation/refactoring
```

# 22. `@ConfigurationProperties`

Example:

```java
@ConfigurationProperties(prefix = "client")
public class ClientProperties {
    private URI endpoint;
    private Duration timeout = Duration.ofSeconds(5);
}
```

It binds a coherent namespace into a typed object.

Benefits:

```text
type conversion
relaxed naming
nested structures
collections/maps
validation
metadata generation
single ownership boundary
```

# 23. Registering configuration-properties types

Common approaches:

```java
@EnableConfigurationProperties(ClientProperties.class)
```

or:

```java
@ConfigurationPropertiesScan
```

A class annotated only with `@ConfigurationProperties` is binding metadata; it still needs registration through scanning, enabling or bean declaration.

# 24. Relaxed binding

Canonical Java property:

```java
private int maxAttempts;
```

Common external forms:

```text
client.retry.max-attempts
client.retry.maxAttempts
CLIENT_RETRY_MAXATTEMPTS
```

Exact support depends on the source and Boot version. Use lowercase kebab-case as the canonical documented form.

Exam trap: relaxed binding applies to configuration-properties binding, not arbitrary lookup syntax in every API.

# 25. Nested objects

```java
@ConfigurationProperties("client")
public class ClientProperties {
    private final Retry retry = new Retry();

    public static class Retry {
        private int maxAttempts;
    }
}
```

Configuration:

```yaml
client:
  retry:
    max-attempts: 3
```

Nested structure makes domain ownership and validation clearer than many unrelated fields.

# 26. Collections and maps

```yaml
client:
  servers:
    - https://a.example.test
    - https://b.example.test
  headers:
    X-Tenant: retail
```

Binding can populate `List`, `Set` and `Map` properties. Override/merge behavior across property sources can differ by collection type and source ordering; test the intended deployment composition.

# 27. Type conversion

The Binder uses conversion infrastructure to convert strings into target types such as:

```text
boolean and numeric types
enums
URI / URL
Duration
DataSize
collections
custom domain types with converters
```

A resolved string can exist in the environment yet fail later during conversion.

# 28. `Duration`

Examples:

```properties
client.timeout=500ms
client.timeout=5s
client.timeout=2m
```

Without a unit suffix, default-unit behavior depends on metadata/annotation and version. Prefer explicit units in external configuration.

# 29. `DataSize`

Examples:

```properties
upload.max-size=10MB
cache.capacity=256KB
```

Use type-safe data-size binding rather than hand-parsing numeric strings.

# 30. Constructor binding in Boot 2.5

Immutable configuration can use constructor binding. In the Boot 2.5 baseline, `@ConstructorBinding` is an important explicit marker in common patterns.

```java
@ConstructorBinding
@ConfigurationProperties("client")
public class ClientProperties {
    private final URI endpoint;
    private final Duration timeout;
}
```

Registration is typically via `@EnableConfigurationProperties` or scanning rather than ordinary component scanning.

# 31. Current constructor-binding delta

Modern Boot can infer constructor binding when a configuration-properties type has a single parameterized constructor; records are natural immutable property carriers.

Do not answer a Boot 2.5-specific question only with current inference rules.

# 32. Validation

```java
@Validated
@ConfigurationProperties("client")
public class ClientProperties {
    @NotNull
    private URI endpoint;

    @DurationMin(seconds = 1)
    private Duration timeout;
}
```

If binding validation fails, startup should fail with a clear report. Validation is valuable because configuration is part of the application's executable contract.

Exam trap: validation annotations need a validation provider and the configuration-properties bean must actually be registered.

# 33. Custom converters

Use a configuration-properties binding converter when a domain-specific representation must be parsed:

```java
@ConfigurationPropertiesBinding
@Component
class RegionConverter implements Converter<String, Region> {
}
```

Keep converters deterministic and side-effect free. They run during configuration binding and should not perform network or database calls.

# 34. Programmatic `Binder`

`Binder` allows explicit binding from an `Environment`:

```java
ClientProperties properties = Binder.get(environment)
        .bind("client", Bindable.of(ClientProperties.class))
        .orElseThrow();
```

Prefer declarative registration for ordinary application configuration; use programmatic binding for infrastructure or framework extension points.

# 35. Configuration metadata

The configuration processor can generate metadata for IDE completion and documentation.

Metadata can describe:

```text
property names
types
default values
deprecation/replacement
human-readable descriptions
```

Metadata improves authoring but does not validate runtime values by itself.

# 36. Unknown properties and typos

A typo can remain unnoticed if no consumer binds or validates it.

Controls:

```text
canonical property documentation
configuration metadata
strict binding where appropriate
startup assertions
objective-focused tests
Actuator/env inspection with sanitization
```

Do not treat “application started” as proof that every supplied key was consumed.

# 37. `@PropertySource` boundary

`@PropertySource` adds a Spring property source during configuration-class processing. It is not a complete replacement for Boot Config Data.

Limitations include:

```text
later processing phase
no native Config Data import semantics
limited YAML support without custom factory
less suitable for early logging/bootstrap properties
```

Use Config Data for application deployment configuration.

# 38. Test overrides

Useful test mechanisms include:

```text
@TestPropertySource
@SpringBootTest(properties = ...)
@DynamicPropertySource
ApplicationContextRunner.withPropertyValues(...)
```

Test overrides often have high precedence and can fragment the context cache when values differ across test classes.

# 39. Secrets and sensitive properties

Never assume a value is safe because it entered through an environment variable or config tree.

Controls:

```text
secret manager or mounted secret
least-privilege file/process access
log sanitization
Actuator endpoint protection
no secret values in Git
rotation strategy
short-lived credentials where possible
```

Avoid printing whole property objects in `toString()`.

# 40. Diagnostics

For a wrong value, collect:

```text
canonical key
all contributing names/forms
resolved value
property-source origin
active/default profiles
loaded config-data locations/documents
binding target and type
conversion/validation failure
```

Useful evidence:

```text
Condition Evaluation Report
startup binding failure report
Environment/PropertySource inspection
Actuator env/configprops with security and sanitization
ApplicationContextRunner
```

# 41. Production incident model

```text
Symptom: client timeout is 30s instead of 3s
Evidence: application.yml says 3s
Wrong conclusion: Boot ignored YAML
Correct investigation:
  CLIENT_TIMEOUT=30s exists in deployment
  environment source has higher priority
  binding succeeded into Duration
Root cause: stale deployment override
Repair: remove override, add startup diagnostic without exposing secrets
Proof: runner/deployment test asserts resolved property and origin
```

# 42. Interview explanation

> Spring Boot externalized configuration builds an ordered Environment before normal bean creation. Config Data loads standard files, profile documents, imports and config trees. A higher-priority source wins for the same key. For coherent settings I prefer `@ConfigurationProperties` because it supports relaxed binding, conversion, nested structures and validation. When a value is wrong, I inspect the winning PropertySource and active Config Data rather than only reading `application.yml`.

# 43. Exam traps

```text
spring.config.location replaces defaults; additional-location extends them
spring.config.import is Config Data, not @PropertySource
optional: suppresses missing-location failure, not binding validation
YAML hierarchy becomes flat keys
@Value and @ConfigurationProperties solve different scopes
@ConfigurationProperties still needs bean registration
relaxed binding is not arbitrary lookup equivalence everywhere
resolved string can fail conversion or validation
Boot 2.5 constructor-binding rules differ from current Boot
profiles do not secure secrets
```

# 44. Practice tasks

1. Predict the winner among file, environment and command-line values.
2. Explain replace versus additional search locations.
3. Model an imported optional developer file.
4. Bind nested retry and server-list configuration.
5. Add validation and predict startup failure.
6. Compare `@Value` and `@ConfigurationProperties` for ten related settings.
7. Diagnose a stale environment override.
8. Explain Boot 2.5 versus current constructor binding.

## Route navigation

- **Roadmap:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Roadmap]]
- **Visual:** [[10_CONCEPTS/Spring/Boot/Spring Boot Configuration Visual Deep Dive]]
- **Cards:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Cards]]
- **Assessment:** [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-BOOT-B02/SPRING-BOOT-B02 Assessment]]
- **Cases:** [[40_PRODUCTION_CASES/Spring/Spring Boot Configuration Production Cases]]
- **Lab:** [[50_LABS/Spring/SPRING-BOOT-B02/README]]
- **Sources:** [[98_SOURCES/Spring Boot Externalized Configuration Sources]]
- **Domain map:** [[01_MAPS/Spring Map]]
