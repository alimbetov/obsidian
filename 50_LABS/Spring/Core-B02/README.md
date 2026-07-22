---
type: lab
domain: spring
subdomain: dependency-resolution
status: active
certification:
  - spring-2V0-72.22
spring_version: 5.3.39
java_version: 8+
related:
  - "[[10_CONCEPTS/Spring/Core/Dependency Resolution and Optional Injection]]"
  - "[[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B02/CORE-B02 Cards]]"
tags:
  - spring
  - lab
  - dependency-injection
---

# CORE-B02 Dependency Resolution Lab

> [!summary]
> Минимальный Spring Framework 5.3 project показывает успешный candidate-resolution path и отдельно воспроизводит ожидаемый startup failure при двух неразрешённых beans одного типа.

## Что демонстрирует

### Успешный context

- `@Primary` выбирает Stripe как default single-valued dependency;
- `@Qualifier("online") List<PaymentGateway>` получает Stripe и PayPal;
- `Map<String, PaymentGateway>` получает все gateways, где keys — bean names;
- `Optional<AuditSink>` становится empty, потому что audit bean отсутствует;
- `ObjectProvider<DiscountPolicy>` возвращает supplied fallback;
- один context показывает одновременно one-of, filtered-all, all-by-name и zero-candidate patterns.

### Failure context

- зарегистрированы два `PaymentGateway`;
- нет `@Primary`;
- нет qualifier;
- consumer требует один `PaymentGateway`;
- startup завершается ожидаемой ошибкой non-unique candidate.

## Запуск

Из каталога `50_LABS/Spring/Core-B02`:

```bash
mvn clean compile exec:java
```

Ожидаемый смысл результата:

```text
=== SUCCESSFUL RESOLUTION ===
Default gateway (@Primary): stripe
Online gateways (@Qualifier + List): [stripe, paypal]
All gateway bean names (Map): [stripeGateway, paypalGateway, cashGateway]
Audit available (Optional): false
Discount policy (ObjectProvider fallback): no-discount

=== EXPECTED AMBIGUITY ===
Context failed as expected
Root cause: NoUniqueBeanDefinitionException
```

Точный порядок отображения map keys не следует использовать как business contract.

## Эксперименты

### Experiment 1. Удалить `@Primary`

Предскажи результат до запуска.

Ожидание: даже GoodConfiguration становится ambiguous для `defaultGateway`.

### Experiment 2. Добавить второй `@Primary`

Ожидание: ambiguity возвращается. Два primary beans не формируют ranking.

### Experiment 3. Изменить qualifier списка

```java
@Qualifier("offline") List<PaymentGateway> offlineGateways
```

Ожидание: список содержит только cash gateway.

### Experiment 4. Зарегистрировать AuditSink

```java
@Bean
AuditSink auditSink() {
    return event -> System.out.println("AUDIT " + event);
}
```

Ожидание: `Optional.isPresent()` становится `true`.

### Experiment 5. Заменить Optional на required parameter

```java
CheckoutService(..., AuditSink auditSink, ...)
```

Без bean context должен завершиться unsatisfied-dependency failure.

### Experiment 6. Добавить DiscountPolicy bean

Ожидание: ObjectProvider возвращает bean вместо fallback supplier.

### Experiment 7. Заменить List на single qualified value

```java
@Qualifier("online") PaymentGateway onlineGateway
```

Сейчас qualifier `online` совпадает с двумя beans. Нужно добавить primary внутри online group либо более узкий qualifier.

## Questions Before Running

1. Почему `@Primary` влияет на defaultGateway, но не сокращает onlineGateways до одного элемента?
2. Почему Optional решает отсутствие AuditSink, но не ambiguity gateway?
3. Почему `Map<String,T>` подходит для диагностики, но bean name не всегда хороший business routing key?
4. Что изменится, если qualifier используется только на injection point, но не на beans?
5. Какой contract выразительнее для optional audit: Optional, provider или no-op implementation?

## Source Layout

```text
Core-B02/
├── pom.xml
├── README.md
└── src/main/java/lab/DependencyResolutionLab.java
```

## Validation Note

POM использует Spring Framework `5.3.39` и Java release 8. В текущем исполнительном окружении Maven отсутствовал, поэтому dependency-based lab не был запущен здесь. Код и configuration model построены по официальным Spring 5.3 contracts; запуск должен быть выполнен локально командой выше.
