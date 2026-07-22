---
type: lab
domain: spring
subdomain: bean-lifecycle
status: active
spring_versions:
  - 5.3.39
java_versions:
  - 8
related:
  - "[[10_CONCEPTS/Spring/Core/Bean Lifecycle from Definition to Destruction]]"
  - "[[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B03/CORE-B03 Cards]]"
tags:
  - spring
  - lifecycle
  - lab
---

# Spring CORE-B03 Bean Lifecycle Lab

## Цель

Увидеть один lifecycle как последовательность observable events:

```text
BeanDefinition / @Bean metadata
        ↓
constructor or factory method
        ↓
Aware callbacks
        ↓
BeanPostProcessor before initialization
        ↓
@PostConstruct
        ↓
afterPropertiesSet
        ↓
custom init method
        ↓
BeanPostProcessor after initialization
        ↓
proxy publication
        ↓
SmartInitializingSingleton
        ↓
business invocation through proxy
        ↓
context close and destruction callbacks
```

> [!warning]
> Exact relative order of `@PostConstruct` and a custom before-initialization processor depends on BeanPostProcessor ordering. The lab deliberately registers its tracing processor with `LOWEST_PRECEDENCE`, so annotation processing may appear earlier inside the same before-initialization phase.

## Что демонстрирует LifecycleProbe

`LifecycleProbe` реализует:

- `BeanNameAware`;
- `BeanFactoryAware`;
- `ApplicationContextAware`;
- `InitializingBean`;
- `SmartInitializingSingleton`;
- `DisposableBean`.

Также используются:

- `@PostConstruct`;
- custom `initMethod`;
- `@PreDestroy`;
- custom `destroyMethod`;
- `DestructionAwareBeanPostProcessor`.

Ожидаемый устойчивый порядок callback-групп:

```text
constructor
aware callbacks
@PostConstruct
afterPropertiesSet
custom init
SmartInitializingSingleton after all regular singletons
...
@PreDestroy
destroy
custom destroy
```

## Что демонстрирует BusinessService

`BusinessServiceImpl` создаётся как raw target. Custom BeanPostProcessor после initialization возвращает JDK dynamic proxy.

Проверки:

```text
businessService runtime class contains $Proxy
Proxy.isProxyClass(...) = true
business call logs proxy-before
business target executes
business call logs proxy-after
```

Это показывает различие:

```text
raw target initialized by container
        ≠
published reference received by consumer
```

## Запуск

```bash
cd 50_LABS/Spring/Core-B03
mvn clean compile exec:java
```

## Что наблюдать

### 1. Constructor is not full initialization

После constructor появляются aware callbacks и initialization callbacks.

### 2. @PostConstruct belongs to processor infrastructure

В output tracing BPP может печататься до или после annotation callback в зависимости от order. Не запоминайте случайный порядок processors как универсальный contract.

### 3. Initialization callback order

Для разных methods:

```text
@PostConstruct
InitializingBean.afterPropertiesSet
custom init-method
```

### 4. Proxy is created after target initialization

Target `@PostConstruct` выполняется до сообщения `custom BPP creates JDK proxy`.

### 5. Final invocation crosses proxy boundary

```text
proxy: before execute
raw target: execute
proxy: after execute
```

### 6. Destruction requires context close

Try-with-resources закрывает `AnnotationConfigApplicationContext`, после чего выполняются destruction callbacks.

## Эксперименты

### Experiment A — remove context close

Замените try-with-resources на context без `close()`.

Наблюдение: destruction callbacks не являются результатом обычного garbage collection.

### Experiment B — change processor order

Измените:

```java
return Ordered.LOWEST_PRECEDENCE;
```

на более высокий priority и сравните расположение custom before-init log относительно `@PostConstruct`.

Вывод: lifecycle phase стабилен, relative processor order настраивается.

### Experiment C — return raw target instead of proxy

Удалите proxy creation в `postProcessAfterInitialization`.

Наблюдение:

```text
Proxy.isProxyClass(...) = false
proxy before/after logs disappear
```

### Experiment D — fail in customInit

Добавьте exception:

```java
void customInit() {
    throw new IllegalStateException("init failed");
}
```

Наблюдение: bean не публикуется как successfully initialized singleton; context refresh fails.

### Experiment E — prototype destruction

Создайте prototype bean с `@PreDestroy`, запросите несколько instances и закройте context.

Наблюдение: ordinary prototype destruction callbacks не вызываются автоматически.

## Педагогический отчёт

После запуска ответьте:

1. Где raw instance впервые появляется?
2. Какие callbacks видят уже injected dependencies?
3. Где создаётся final proxy?
4. Какой object получает consumer?
5. Что происходит после `SmartInitializingSingleton`?
6. Какие destruction callbacks вызваны при close?
7. Почему output одного BPP ordering не является спецификацией всех Spring applications?

## Проверка проекта

POM рассчитан на:

```text
Java release: 8
Spring Framework: 5.3.39
JSR-250 annotations: javax.annotation-api 1.3.2
```

Полная runtime-проверка требует Maven и доступа к зависимостям. Java source должен компилироваться через Maven-команду выше.

## Sources

- [[98_SOURCES/Spring Bean Lifecycle Sources]]
