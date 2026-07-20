---
type: lab
domain: spring
subdomain: configuration-profiles
status: active
java_versions:
  - 8
spring_versions:
  - 5.3.39
related:
  - "[[10_CONCEPTS/Spring/Core/Configuration Profiles and Externalized Properties]]"
  - "[[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B05/CORE-B05 Cards]]"
  - "[[40_PRODUCTION_CASES/Spring/Configuration and Profiles Production Cases]]"
tags:
  - spring
  - lab
  - configuration
  - profiles
  - properties
---

# CORE-B05 Lab — Configuration and Profiles

> [!summary]
> Лаборатория показывает три наблюдаемых механизма: managed inter-bean calls в full configuration, обычные Java calls при `proxyBeanMethods=false`, а также выбор profile-dependent bean и победившего PropertySource.

## Структура

```text
Core-B05/
├── pom.xml
├── README.md
└── src/main/
    ├── java/lab/ConfigurationProfilesLab.java
    └── resources/core-b05.properties
```

## Запуск

```bash
cd 50_LABS/Spring/Core-B05
mvn clean compile exec:java
```

Требования:

- JDK 8 или новее;
- Maven;
- доступ к Maven Central при первом разрешении dependencies.

## Experiment 1. Full configuration

Configuration:

```java
@Configuration
class FullConfig {
    @Bean Repository repository() {
        return new Repository();
    }

    @Bean OrderService orderService() {
        return new OrderService(repository());
    }
}
```

Ожидаемая причинная цепочка:

```text
orderService() calls repository()
        ↓
enhanced configuration intercepts call
        ↓
BeanFactory returns managed repository
        ↓
context repository == service repository
```

Ожидаемый вывод:

```text
=== FULL CONFIGURATION ===
managed repository id = 1
service repository id = 1
same instance = true
```

Точные numeric IDs могут отличаться, но identity должна совпасть.

## Experiment 2. `proxyBeanMethods=false`

Configuration:

```java
@Configuration(proxyBeanMethods = false)
class LiteConfig {
    @Bean Repository repository() {
        return new Repository();
    }

    @Bean OrderService orderService() {
        return new OrderService(repository());
    }
}
```

Ожидаемая цепочка:

```text
container creates managed repository
        ↓
container invokes orderService()
        ↓
orderService() directly invokes repository()
        ↓
ordinary Java method creates second object
```

Ожидаемый результат:

```text
same instance = false
```

> [!important]
> Scope bean definition остался singleton. Второй object возник не из-за prototype scope, а из-за direct Java call.

## Experiment 3. Profiles

Lab создаёт два configuration modules:

```java
@Profile("dev")
class DevTransportConfig { ... }

@Profile("prod")
class ProdTransportConfig { ... }
```

Для каждого profile создаётся отдельный context.

Ожидаемо:

```text
dev  -> logging-transport
prod -> remote-transport
```

Profile устанавливается до `refresh()`:

```java
context.getEnvironment().setActiveProfiles(profile);
context.register(RootConfig.class);
context.refresh();
```

## Experiment 4. PropertySource precedence

Classpath file содержит:

```properties
client.timeout-ms=1200
```

До refresh lab добавляет source с более высоким precedence:

```java
environment.getPropertySources().addFirst(
    new MapPropertySource(
        "runtimeOverride",
        singletonMap("client.timeout-ms", "2500")
    )
);
```

Ожидаемый resolved value:

```text
client timeout = 2500
```

Lab также печатает все sources, которые содержат key:

```text
runtimeOverride -> 2500
class path resource [core-b05.properties] -> 1200
```

Первый matching source выигрывает.

## Experiment 5. Placeholder resolution

```java
@Value("${client.timeout-ms:1000}") int timeoutMs
```

Проверяются:

- lookup key;
- precedence;
- fallback syntax;
- conversion String → int;
- fail-fast validation в constructor `ClientSettings`.

`PropertySourcesPlaceholderConfigurer` объявлен как static bean, потому что это BeanFactoryPostProcessor:

```java
@Bean
static PropertySourcesPlaceholderConfigurer placeholders() {
    return new PropertySourcesPlaceholderConfigurer();
}
```

## Управляемые изменения

### Change 1. Исправить lite configuration

Заменить:

```java
@Bean
OrderService orderService() {
    return new OrderService(repository());
}
```

на:

```java
@Bean
OrderService orderService(Repository repository) {
    return new OrderService(repository);
}
```

Предсказать новый identity result до запуска.

### Change 2. Удалить runtime override

Удалить `addFirst(MapPropertySource...)`.

Ожидаемый timeout:

```text
1200
```

### Change 3. Поменять `addFirst` на `addLast`

Сравнить winner property source.

### Change 4. Не активировать profile

Удалить `setActiveProfiles(profile)`.

Проверить:

- существует ли `Transport` bean;
- какой exception возникает;
- какую роль мог бы сыграть default profile.

### Change 5. Активировать оба profiles

```java
setActiveProfiles("dev", "prod");
```

Проверить ambiguity для `Transport`.

### Change 6. Сделать invalid timeout

```text
client.timeout-ms=0
```

Убедиться, что context creation завершается ошибкой до business invocation.

### Change 7. Удалить placeholder default

Сравнить:

```java
${client.timeout-ms:1000}
```

и:

```java
${client.timeout-ms}
```

при отсутствии key.

## Вопросы после лаборатории

1. Почему full configuration возвращает один Repository?
2. Почему `proxyBeanMethods=false` не превращает definition в prototype?
3. Почему method parameter исправляет обе configuration modes?
4. Когда profile condition проверяется?
5. Почему изменение profile после refresh не пересобирает graph?
6. Какой source побеждает при `addFirst()`?
7. Чем `${...}` отличается от `#{...}`?
8. Почему placeholder configurer зарегистрирован static method?
9. Почему production diagnosis должна учитывать actual PropertySource chain?
10. Где проходит граница между Framework Environment и Boot Config Data?

## Validation status

Java source рассчитан на Java 8 и Spring Framework 5.3.39.

Проверка должна включать:

```text
javac --release 8
mvn clean compile exec:java
```

В текущем исполнительном окружении full Maven runtime может быть недоступен при отсутствии Maven или external dependency resolution. В таком случае необходимо выполнить Maven-команду в обычной development environment и сравнить timeline с ожидаемыми инвариантами, а не только с номерами объектов.

## Sources

- [[98_SOURCES/Spring Configuration and Profiles Sources]]
