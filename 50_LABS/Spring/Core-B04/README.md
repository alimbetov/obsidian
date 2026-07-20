---
type: lab
domain: spring
subdomain: container-extension-points
status: active
java_versions:
  - 8
spring_versions:
  - 5.3.39
related:
  - "[[10_CONCEPTS/Spring/Core/Container Extension Points]]"
  - "[[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B04/CORE-B04 Cards]]"
  - "[[40_PRODUCTION_CASES/Spring/Container Extension Point Production Cases]]"
tags:
  - spring
  - lab
  - bean-post-processor
---

# CORE-B04 Lab — Container Extension Points

> [!summary]
> Лаборатория показывает registry metadata processing, BeanDefinition mutation, programmatic и auto-detected BPP ordering, instantiation-aware callbacks, proxy creation и destruction-aware cleanup в одном timeline.

## Что демонстрирует проект

### Metadata plane

- `BeanDefinitionRegistryPostProcessor` динамически добавляет `dynamicPlugin`;
- inherited `postProcessBeanFactory()` выполняется после registry callback;
- отдельный `BeanFactoryPostProcessor` меняет property `timeoutMs` в definition `configurableClient`;
- application bean не создаётся через `getBean()` в metadata phase;
- processor factory methods объявлены `static`.

### Instance plane

- два BPP добавляются programmatically до `refresh()`;
- `InstantiationAwareBeanPostProcessor` логирует:
  - `postProcessBeforeInstantiation`;
  - `postProcessAfterInstantiation`;
  - `postProcessProperties`;
- custom BPP создаёт JDK proxy для class с `@Audited`;
- exposed `WorkService` является proxy;
- target method выполняется внутри audit wrapper.

### Destruction plane

- `DestructionAwareBeanPostProcessor` фильтрует beans через `requiresDestruction()`;
- cleanup callback выполняется при `context.close()` для выбранных beans.

## Структура

```text
Core-B04/
├── pom.xml
├── README.md
└── src/main/java/lab/
    └── ContainerExtensionPointLab.java
```

## Запуск

```bash
cd 50_LABS/Spring/Core-B04
mvn clean compile exec:java
```

Требования:

- JDK 8 или новее;
- Maven;
- доступ к Maven Central для первого dependency resolution.

## Ожидаемая логика timeline

Точные номера событий могут включать infrastructure details, но устойчивые причинные связи должны быть такими:

```text
context created without refresh
    ↓
programmatic BPP registration
    ↓
context refresh
    ↓
BDRPP registry callback adds dynamicPlugin
    ↓
BDRPP inherited factory callback
    ↓
BFPP modifies configurableClient definition
    ↓
regular beans begin creation
    ↓
IABPP before instantiation
    ↓
constructor or @Bean factory method
    ↓
IABPP after instantiation
    ↓
IABPP property processing
    ↓
BeanDefinition property applied
    ↓
programmatic before-init callbacks
    ↓
programmatic after-init callbacks
    ↓
auto-detected proxy BPP may replace reference
    ↓
consumer obtains proxy
    ↓
context close triggers destruction-aware callbacks
```

## Что нужно увидеть

### 1. Dynamic definition

```text
BDRPP: register dynamicPlugin definition
...
dynamicPlugin: constructor registry-plugin
...
main: dynamic plugin = registry-plugin
```

Это доказывает различие:

```text
registration of metadata
    before
instantiation of resulting bean
```

### 2. Definition mutation

```text
BFPP: modify configurableClient BeanDefinition
...
configurableClient: setTimeoutMs(2500)
...
main: client timeout = 2500
```

BFPP не вызывает setter напрямую. Он меняет property metadata, а setter вызывается позже в population phase.

### 3. Programmatic ordering

В коде processors добавляются так:

```java
addBeanPostProcessor(programmatic-1);
addBeanPostProcessor(programmatic-2);
```

Несмотря на `PriorityOrdered`, их взаимный порядок определяется registration calls:

```text
programmatic-1
programmatic-2
```

### 4. Instantiation-aware callbacks

Для application beans должны быть видны:

```text
IABPP: beforeInstantiation
constructor / factory method
IABPP: afterInstantiation
IABPP: postProcessProperties
```

### 5. Proxy boundary

```text
AuditBPP: create JDK proxy for workService
main: service is JDK proxy = true
audit proxy: before workService.execute
workService target: execute invoice-42
audit proxy: after workService.execute
```

Raw `AuditedWorkService` создаётся раньше, но consumer получает `WorkService` proxy.

### 6. Destruction-aware cleanup

После:

```java
context.close();
```

должны появиться события вида:

```text
DestructionBPP: beforeDestruction dynamicPlugin
DestructionBPP: beforeDestruction configurableClient
```

Порядок destruction отдельных singleton beans не следует превращать в business contract без явных dependencies.

## Эксперименты

### Experiment 1. Programmatic order ignores Ordered

Поменяй registration calls местами:

```java
addBeanPostProcessor(programmatic-2);
addBeanPostProcessor(programmatic-1);
```

Предскажи новый timeline до запуска.

### Experiment 2. Remove static from BFPP method

Измени:

```java
@Bean
static TimeoutMetadataPostProcessor timeoutMetadataPostProcessor()
```

на non-static method.

Наблюдай startup logs и сравни timing configuration-class creation.

> [!warning]
> Это учебный эксперимент, не рекомендуемый production pattern.

### Experiment 3. Call getBean inside BFPP

Добавь в BFPP:

```java
beanFactory.getBean("workService");
```

Проверь:

- когда создаётся service;
- получает ли он Audit proxy;
- появляются ли eligibility warnings.

После эксперимента обязательно удалить вызов.

### Experiment 4. Short-circuit instantiation

В `postProcessBeforeInstantiation()` для `workService` верни custom proxy.

Ответь:

- выполняется ли original `@Bean` method;
- вызывается ли target constructor;
- выполняется ли normal property population;
- какие after-initialization processors всё ещё получают reference.

### Experiment 5. Veto population

Для `configurableClient` верни `false` из `postProcessAfterInstantiation()`.

Ожидаемый вопрос:

> Почему `timeoutMs` остаётся default value, хотя BFPP добавил property metadata?

Потому что definition была изменена, но instance processor запретил normal population.

### Experiment 6. Remove proxy processor

Удалить `AuditProxyPostProcessor` и сравнить:

- runtime class;
- `Proxy.isProxyClass()`;
- business invocation timeline.

### Experiment 7. Processor dependency trap

Внедрить `WorkService` в constructor custom BPP.

Наблюдать:

- early creation;
- processor eligibility warnings;
- наличие или отсутствие proxy.

## Вопросы после лаборатории

1. Почему BDRPP имеет два callbacks?
2. Почему property setter выполняется позже BFPP?
3. Почему programmatic BPP не подчиняется `@Order`?
4. В какой момент target уже существует, но properties ещё не заполнены?
5. Как `postProcessBeforeInstantiation()` может short-circuit creation?
6. Почему processor dependency может потерять `@Transactional` proxy?
7. Почему manual JDK proxy может конфликтовать с Spring AOP?
8. Когда `requiresDestruction()` возвращать false?

## Validation status

Проведена локальная проверка Java source shape:

```text
javac --release 8 — PASS
```

Проверка выполнена против минимальных Spring API stubs, чтобы подтвердить:

- Java 8 syntax;
- method signatures used by the lab;
- generic/type consistency;
- absence of accidental Java 9+ language features.

Полный Spring runtime запуск в текущем исполнительном окружении не выполнялся, потому что Maven отсутствует и external dependency resolution недоступен. Полный запуск должен быть выполнен командой `mvn clean compile exec:java` в Maven-enabled environment.

## Sources

- [[98_SOURCES/Spring Container Extension Point Sources]]
