---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain: dependency-resolution
batch_id: CORE-B02
status: published
card_count: 24
language:
  question: en
  translation: ru
  explanation: ru
prerequisites:
  - "[[10_CONCEPTS/Spring/Core/Spring Core Foundations]]"
related:
  - "[[10_CONCEPTS/Spring/Core/Dependency Resolution and Optional Injection]]"
tags:
  - spring
  - certification
  - flashcards
---

# CORE-B02 — Dependency Resolution Cards

> [!summary]
> Партия тренирует выбор кандидата после type matching: `@Primary`, `@Qualifier`, bean-name fallback, collection/map injection и способы моделирования optional/lazy dependencies.

## Как проходить

1. Прочитать только English Question.
2. Ответить вслух на английском или русском.
3. Сверить Russian Translation.
4. Раскрыть Answer.
5. Объяснить механизм без чтения Explanation.
6. Зафиксировать outcome: `correct-confident`, `correct-guessed`, `wrong-concept`, `wrong-attention`, `wrong-confusion`.

---

## CORE-B02-C021

### Question

> [!question]
> What does @Primary do when multiple beans match a single-valued dependency?

### Russian Translation

> Что делает @Primary, когда несколько beans подходят для одной зависимости?

> [!answer]- Answer
> It marks one candidate as preferred. If exactly one primary bean remains among the candidates, Spring injects it.

### Explanation

Spring first finds type-compatible candidates. @Primary does not create a candidate and does not bypass type matching; it gives preference inside the matching set.

### Exam Trap

> [!warning]
> Do not say that @Primary injects a bean by name or that it excludes the other beans from the container.

### Mini Example

```java
@Bean
@Primary
PaymentGateway stripeGateway() { return new StripeGateway(); }

@Bean
PaymentGateway paypalGateway() { return new PaypalGateway(); }
```

### Memory Hook

> Primary means preferred default, not unique identity.

---

## CORE-B02-C022

### Question

> [!question]
> Does @Primary remove non-primary beans from List<T>, Map<String,T>, or array injection?

### Russian Translation

> Удаляет ли @Primary остальные beans при инъекции List<T>, Map<String,T> или массива?

> [!answer]- Answer
> No. Multi-element injection receives all matching beans; @Primary is relevant mainly to a single-valued dependency.

### Explanation

A list or map asks for the candidate set rather than one winner. Ordering and qualifier filters can shape that set, but one primary bean does not erase the others.

### Exam Trap

> [!warning]
> A common trap is applying single-candidate resolution rules to collection injection.

### Mini Example

```java
List<PaymentGateway> gateways;
Map<String, PaymentGateway> gatewaysByName;
```

### Memory Hook

> Primary chooses one; collections collect many.

---

## CORE-B02-C023

### Question

> [!question]
> What happens if two matching beans are both marked @Primary for one injection point?

### Russian Translation

> Что произойдёт, если два подходящих bean одновременно помечены @Primary?

> [!answer]- Answer
> The dependency is still ambiguous and context creation fails unless another narrowing rule resolves it.

### Explanation

The contract requires exactly one preferred candidate among the remaining matches. Two primaries do not form a priority order.

### Exam Trap

> [!warning]
> The word 'primary' is not a numeric rank. Multiple primaries do not select the first registered bean.

### Memory Hook

> One primary resolves; two primaries re-create ambiguity.

---

## CORE-B02-C024

### Question

> [!question]
> What is the semantic purpose of @Qualifier?

### Russian Translation

> Какова семантическая цель @Qualifier?

> [!answer]- Answer
> It narrows the type-matched candidate set by a semantic label.

### Explanation

@Autowired remains type-driven. A qualifier is an additional filtering criterion such as 'online', 'persistent', or 'emea'.

### Exam Trap

> [!warning]
> Do not reduce @Qualifier to a direct getBean(name) lookup. Bean-name matching can be a fallback, but qualifier semantics are filtering semantics.

### Mini Example

```java
CheckoutService(@Qualifier("online") PaymentGateway gateway) { ... }
```

### Memory Hook

> Type first, meaning second.

---

## CORE-B02-C025

### Question

> [!question]
> Should qualifier values be treated as unique bean identifiers?

### Russian Translation

> Следует ли считать значения qualifier уникальными идентификаторами bean?

> [!answer]- Answer
> No. Qualifier values may describe a characteristic and do not have to be unique.

### Explanation

Several beans can share the same qualifier. This is especially useful when injecting a filtered collection of strategies.

### Exam Trap

> [!warning]
> A qualifier can match multiple beans. That is valid for List<T> but still ambiguous for a single T unless another rule selects one.

### Mini Example

```java
@Qualifier("online") List<PaymentGateway> onlineGateways;
```

### Memory Hook

> Qualifier is a tag, not necessarily an ID.

---

## CORE-B02-C026

### Question

> [!question]
> Can a bean name act as a fallback qualifier value?

### Russian Translation

> Может ли имя bean использоваться как fallback-значение qualifier?

> [!answer]- Answer
> Yes. Within type-matching candidates, a bean name can satisfy a qualifier value as a fallback.

### Explanation

This convenience does not change the fundamental model: @Autowired selects by type and then narrows candidates.

### Exam Trap

> [!warning]
> Do not infer that @Autowired has become pure by-name injection.

### Mini Example

```java
@Bean("stripe")
PaymentGateway stripeGateway() { ... }

CheckoutService(@Qualifier("stripe") PaymentGateway gateway) { ... }
```

### Memory Hook

> Name may satisfy a qualifier; type still opens the door.

---

## CORE-B02-C027

### Question

> [!question]
> How do @Qualifier and @Primary interact?

### Russian Translation

> Как взаимодействуют @Qualifier и @Primary?

> [!answer]- Answer
> The qualifier narrows the candidate set. @Primary can then prefer one bean among the remaining candidates when a single value is required.

### Explanation

A specific qualifier usually expresses the injection point's intended category. Primary is a default preference, not a way to ignore an explicit qualifier.

### Exam Trap

> [!warning]
> Do not claim that @Primary always wins over @Qualifier.

### Memory Hook

> Qualifier filters; Primary prefers within the filter.

---

## CORE-B02-C028

### Question

> [!question]
> Why create a custom qualifier annotation?

### Russian Translation

> Зачем создавать собственную qualifier-аннотацию?

> [!answer]- Answer
> To replace fragile strings with a reusable, type-safe semantic annotation.

### Explanation

A custom annotation meta-annotated with @Qualifier can carry domain meaning and optional attributes.

### Exam Trap

> [!warning]
> The custom annotation still participates in qualifier matching; it does not create beans by itself.

### Mini Example

```java
@Target({FIELD, PARAMETER, METHOD, TYPE})
@Retention(RUNTIME)
@Qualifier
@interface Channel {
    ChannelType value();
}
```

### Memory Hook

> Promote repeated strings into domain vocabulary.

---

## CORE-B02-C029

### Question

> [!question]
> What is injected into @Qualifier("online") List<PaymentGateway>?

### Russian Translation

> Что будет внедрено в @Qualifier("online") List<PaymentGateway>?

> [!answer]- Answer
> All PaymentGateway beans that match the 'online' qualifier.

### Explanation

Qualifier values are filtering criteria and may intentionally match several candidates for a collection injection point.

### Exam Trap

> [!warning]
> Do not expect exactly one element merely because @Qualifier is present.

### Memory Hook

> Single T needs one winner; List<T> keeps every filtered match.

---

## CORE-B02-C030

### Question

> [!question]
> Can an injection-point name resolve an otherwise ambiguous dependency?

### Russian Translation

> Может ли имя параметра или поля разрешить неоднозначную зависимость?

> [!answer]- Answer
> Yes, Spring 5.3 can consider the injection-point name against bean names as a fallback when no stronger resolution indicator selects a candidate.

### Explanation

This is a convenience fallback after type matching. It is more fragile than an explicit semantic qualifier because refactoring a parameter name can change wiring.

### Exam Trap

> [!warning]
> Do not present name matching as the first or preferred resolution rule.

### Mini Example

```java
CheckoutService(PaymentGateway stripeGateway) { ... }
```

### Memory Hook

> Name fallback is convenient, explicit qualifier is intentional.

---

## CORE-B02-C031

### Question

> [!question]
> When is @Resource semantically clearer than @Autowired?

### Russian Translation

> Когда @Resource семантически яснее, чем @Autowired?

> [!answer]- Answer
> When the dependency is intended to be selected primarily by its unique name.

### Explanation

@Autowired is type-driven with optional qualifiers. @Resource is name-oriented and is supported on fields and single-argument setter methods.

### Exam Trap

> [!warning]
> Do not use @Resource as a drop-in constructor parameter annotation; its supported injection targets differ.

### Memory Hook

> Autowired asks 'which type?'; Resource asks 'which name?'.

---

## CORE-B02-C032

### Question

> [!question]
> What does Spring inject into List<Strategy>?

### Russian Translation

> Что Spring внедряет в List<Strategy>?

> [!answer]- Answer
> All autowire-candidate beans assignable to Strategy, subject to qualifier filtering.

### Explanation

Collection injection is useful for strategy chains, validators, converters, and handlers. The consumer should define whether ordering matters.

### Exam Trap

> [!warning]
> Do not assume the list contains only the @Primary bean.

### Memory Hook

> A collection injection point requests the whole matching team.

---

## CORE-B02-C033

### Question

> [!question]
> How can the order of an injected List<T> be controlled?

### Russian Translation

> Как управлять порядком внедряемого List<T>?

> [!answer]- Answer
> Use Ordered, @Order, or @Priority on the target beans.

### Explanation

Ordering of an injected collection is separate from bean initialization order. @Order influences sorting at the injection point, not startup sequencing.

### Exam Trap

> [!warning]
> Do not use @Order as a substitute for @DependsOn or an actual dependency relationship.

### Memory Hook

> List order is not lifecycle order.

---

## CORE-B02-C034

### Question

> [!question]
> What are the keys in an injected Map<String, PaymentGateway>?

### Russian Translation

> Что является ключами в Map<String, PaymentGateway> при инъекции?

> [!answer]- Answer
> The corresponding bean names.

### Explanation

Spring collects all matching PaymentGateway beans as values. The map key type must be String for this standard bean-name mapping.

### Exam Trap

> [!warning]
> Do not expect qualifier values or class names to become map keys automatically.

### Memory Hook

> Map key = bean name; map value = matching bean.

---

## CORE-B02-C035

### Question

> [!question]
> What does @Autowired(required=false) mean on a field?

### Russian Translation

> Что означает @Autowired(required=false) на поле?

> [!answer]- Answer
> If no candidate is available, Spring leaves the field at its existing default value instead of failing.

### Explanation

The field is not populated when the dependency cannot be resolved. The code must therefore handle the absent value safely.

### Exam Trap

> [!warning]
> Optional injection does not make the dependency magically non-null.

### Memory Hook

> Not required means injection may be skipped.

---

## CORE-B02-C036

### Question

> [!question]
> What happens to a non-required @Autowired method when one of its arguments is unavailable?

### Russian Translation

> Что происходит с необязательным @Autowired-методом, если один аргумент недоступен?

> [!answer]- Answer
> The method is not invoked at all.

### Explanation

This differs from calling the method with null. Existing object state remains unchanged unless another mechanism sets it.

### Exam Trap

> [!warning]
> Do not assume Spring calls the method with null for missing collaborators.

### Memory Hook

> Optional method missing one input means no method call.

---

## CORE-B02-C037

### Question

> [!question]
> How does Optional<T> express an optional dependency?

### Russian Translation

> Как Optional<T> выражает необязательную зависимость?

> [!answer]- Answer
> Spring injects Optional.empty() when no matching bean exists and Optional.of(bean) when one resolvable bean exists.

### Explanation

Optional makes absence explicit in the constructor contract and avoids a raw nullable field.

### Exam Trap

> [!warning]
> Optional does not solve ambiguity among multiple matching beans.

### Mini Example

```java
Service(Optional<AuditSink> auditSink) {
    this.auditSink = auditSink;
}
```

### Memory Hook

> Optional models absence, not ambiguity.

---

## CORE-B02-C038

### Question

> [!question]
> What does @Nullable mean on an injection parameter?

### Russian Translation

> Что означает @Nullable на параметре инъекции?

> [!answer]- Answer
> It allows Spring to supply null when no candidate is available.

### Explanation

It overrides the default required semantics for that parameter. The consuming code must still handle null correctly.

### Exam Trap

> [!warning]
> Do not confuse nullable with lazy resolution or multiple-candidate resolution.

### Memory Hook

> Nullable permits null; it does not defer lookup.

---

## CORE-B02-C039

### Question

> [!question]
> Why inject ObjectProvider<T>?

### Russian Translation

> Зачем внедрять ObjectProvider<T>?

> [!answer]- Answer
> To perform lazy, optional, or repeated programmatic lookup without directly depending on ApplicationContext.

### Explanation

ObjectProvider is useful when creation is expensive, the dependency may be absent, or a prototype/newly resolved instance is needed per use.

### Exam Trap

> [!warning]
> Do not call getObject() and expect absence to be tolerated; choose the appropriate provider method.

### Mini Example

```java
private final ObjectProvider<DiscountPolicy> policies;

DiscountPolicy current() {
    return policies.getIfAvailable(NoDiscount::new);
}
```

### Memory Hook

> Provider delays the decision until use.

---

## CORE-B02-C040

### Question

> [!question]
> What is the difference between ObjectProvider.getObject() and getIfAvailable()?

### Russian Translation

> Чем отличаются ObjectProvider.getObject() и getIfAvailable()?

> [!answer]- Answer
> getObject() requires a resolvable bean and fails otherwise; getIfAvailable() returns null or a supplied default when no candidate exists.

### Explanation

Both can still fail on non-unique candidates unless candidate resolution is otherwise unambiguous.

### Exam Trap

> [!warning]
> Optional availability and uniqueness are separate dimensions.

### Memory Hook

> Available handles zero; qualification handles many.

---

## CORE-B02-C041

### Question

> [!question]
> How does Spring choose among multiple constructors annotated @Autowired(required=false)?

### Russian Translation

> Как Spring выбирает между несколькими конструкторами @Autowired(required=false)?

> [!answer]- Answer
> It chooses the satisfiable candidate with the greatest number of resolvable dependencies, with fallback rules for a default constructor.

### Explanation

Only one constructor may normally be annotated as required=true. Multiple optional candidates participate in constructor resolution.

### Exam Trap

> [!warning]
> Do not say Spring always selects the constructor declared first.

### Memory Hook

> Choose the greediest satisfiable constructor, not the first one.

---

## CORE-B02-C042

### Question

> [!question]
> Does a bean class with one constructor need @Autowired in Spring 5.3?

### Russian Translation

> Нужна ли @Autowired классу bean с единственным конструктором в Spring 5.3?

> [!answer]- Answer
> No. Spring uses the sole constructor even without @Autowired.

### Explanation

This supports explicit constructor injection with less annotation noise.

### Exam Trap

> [!warning]
> The rule applies when the class has exactly one constructor and is itself created as a Spring bean.

### Memory Hook

> One constructor is already an unambiguous instruction.

---

## CORE-B02-C043

### Question

> [!question]
> Can generic type arguments act as autowiring qualifiers?

### Russian Translation

> Могут ли generic-параметры выступать autowiring qualifiers?

> [!answer]- Answer
> Yes. Spring can distinguish Store<String> from Store<Integer> using generic type information.

### Explanation

Generic qualification also applies to collections, such as List<Store<Integer>>.

### Exam Trap

> [!warning]
> Raw types discard the distinction and can reintroduce ambiguity.

### Mini Example

```java
Store<String> stringStore;
List<Store<Integer>> integerStores;
```

### Memory Hook

> Generic arguments carry selection meaning.

---

## CORE-B02-C044

### Question

> [!question]
> What does autowire-candidate=false do?

### Russian Translation

> Что делает autowire-candidate=false?

> [!answer]- Answer
> It excludes that bean definition from type-based autowiring candidate selection.

### Explanation

The bean still exists in the container and can be referenced explicitly. The setting controls candidacy, not bean registration.

### Exam Trap

> [!warning]
> Do not say the bean is disabled or cannot receive its own dependencies.

### Memory Hook

> Not an autowire candidate does not mean not a bean.

## Batch Review Checklist

- [ ] Все 24 карточки отвечены без раскрытия Answer.
- [ ] Карточки C021–C030: candidate resolution.
- [ ] Карточки C031–C034: name and multi-element injection.
- [ ] Карточки C035–C043: optional, lazy and generic resolution.
- [ ] Карточка C044: candidate exclusion.
- [ ] Все `correct-guessed` добавлены в ближайшее повторение.
- [ ] Для каждой ошибки указан confusion pair.
