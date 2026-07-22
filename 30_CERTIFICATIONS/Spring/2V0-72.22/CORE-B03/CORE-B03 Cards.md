---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain: bean-lifecycle
batch_id: CORE-B03
status: published
card_count: 24
language:
  question: en
  translation: ru
  explanation: ru
prerequisites:
  - "[[10_CONCEPTS/Spring/Core/Spring Core Foundations]]"
  - "[[10_CONCEPTS/Spring/Core/Dependency Resolution and Optional Injection]]"
related:
  - "[[10_CONCEPTS/Spring/Core/Bean Lifecycle from Definition to Destruction]]"
tags:
  - spring
  - certification
  - lifecycle
  - flashcards
---

# CORE-B03 — Bean Lifecycle Cards

> [!summary]
> Партия тренирует полный lifecycle Spring bean: metadata, instantiation, dependency population, aware callbacks, BeanPostProcessor phases, initialization order, proxy creation, singleton publication и destruction.

## Как проходить

1. Прочитать English Question.
2. Ответить вслух до раскрытия Answer.
3. Назвать lifecycle phase.
4. Объяснить, raw bean это или final published reference.
5. Для callback назвать порядок и ограничения.
6. Зафиксировать outcome.

---

## CORE-B03-C045

### Question

> [!question]
> What is the role of a BeanDefinition in the Spring bean lifecycle?

### Russian Translation

> Какую роль выполняет BeanDefinition в жизненном цикле Spring bean?

> [!answer]- Answer
> It is metadata describing how the container should create, configure, scope, initialize, and destroy a bean. It is not the bean instance itself.

### Explanation

BeanDefinition хранит class/factory method, scope, constructor arguments, property values, qualifiers, primary flag, lazy status, init method и destroy method. Instance создаётся позже согласно этому recipe.

### Exam Trap

> [!warning]
> Do not confuse registered metadata with an already-created singleton object.

### Memory Hook

> BeanDefinition is the recipe; the bean is the cooked object.

---

## CORE-B03-C046

### Question

> [!question]
> What is the difference between bean instantiation and bean initialization?

### Russian Translation

> Чем отличается создание экземпляра bean от его инициализации?

> [!answer]- Answer
> Instantiation creates the raw Java object. Initialization happens later, after dependency population and aware callbacks, and includes lifecycle callbacks and post-processing.

### Explanation

Constructor execution only proves that the raw instance exists. It does not prove that setter/field dependencies, @PostConstruct, afterPropertiesSet, custom init methods, or proxy creation have completed.

### Exam Trap

> [!warning]
> Constructor completion does not mean that the Spring bean is fully ready or proxied.

### Memory Hook

> Instantiation gives an object; initialization gives a managed bean.

---

## CORE-B03-C047

### Question

> [!question]
> When are normal bean properties and dependencies populated relative to initialization callbacks?

### Russian Translation

> Когда Spring устанавливает обычные свойства и зависимости относительно initialization callbacks?

> [!answer]- Answer
> They are populated before aware callbacks and before ordinary initialization callbacks such as @PostConstruct, afterPropertiesSet, and a custom init method.

### Explanation

Именно поэтому initialization callback может валидировать уже внедрённые dependencies. Constructor-injected dependencies передаются при instantiation, но остальные lifecycle phases всё равно следуют позже.

### Exam Trap

> [!warning]
> Do not place field or setter injection after @PostConstruct in the lifecycle sequence.

### Memory Hook

> Assemble dependencies before starting the machine.

---

## CORE-B03-C048

### Question

> [!question]
> When are Aware callbacks invoked in relation to property population and initialization?

### Russian Translation

> Когда вызываются Aware callbacks относительно заполнения свойств и initialization?

> [!answer]- Answer
> They are invoked after normal property population and before initialization callbacks.

### Explanation

BeanNameAware, BeanFactoryAware and context-specific aware callbacks provide container infrastructure information before afterPropertiesSet or custom init methods run.

### Exam Trap

> [!warning]
> Aware callbacks are not destruction callbacks and are not normally delayed until after proxy creation.

### Memory Hook

> Dependencies first; the container introduces itself next; init follows.

---

## CORE-B03-C049

### Question

> [!question]
> What information does BeanNameAware provide to a bean?

### Russian Translation

> Какую информацию BeanNameAware передаёт bean?

> [!answer]- Answer
> It provides the name under which the bean is defined in the containing BeanFactory.

### Explanation

`setBeanName(String name)` может быть полезен infrastructure component для logging, metadata и diagnostics. Business logic обычно не должна без необходимости зависеть от container bean name.

### Exam Trap

> [!warning]
> BeanNameAware does not rename the bean and does not select an autowiring candidate.

### Memory Hook

> BeanNameAware tells identity; it does not choose dependencies.

---

## CORE-B03-C050

### Question

> [!question]
> Why should ApplicationContextAware generally be used cautiously in application code?

### Russian Translation

> Почему ApplicationContextAware следует осторожно использовать в прикладном коде?

> [!answer]- Answer
> Because it couples the bean to Spring and often turns explicit dependency injection into service-locator-style lookups.

### Explanation

Иногда infrastructure lookup оправдан, но обычные collaborators лучше передавать constructor injection. Тогда dependency graph остаётся видимым и тестируемым.

### Exam Trap

> [!warning]
> The fact that a bean can access the ApplicationContext does not make context lookup the preferred DI style.

### Memory Hook

> Aware is an escape hatch, not the main entrance.

---

## CORE-B03-C051

### Question

> [!question]
> At what lifecycle point is BeanPostProcessor.postProcessBeforeInitialization invoked?

### Russian Translation

> На каком этапе вызывается BeanPostProcessor.postProcessBeforeInitialization?

> [!answer]- Answer
> After the bean has been instantiated and populated, but before InitializingBean.afterPropertiesSet and a custom init method.

### Explanation

Before-initialization processors form a chain. Infrastructure processors can use this phase to invoke annotation-driven lifecycle methods such as @PostConstruct.

### Exam Trap

> [!warning]
> The method name means before initialization callbacks, not before object instantiation or dependency injection.

### Memory Hook

> Before-init sees an assembled raw bean.

---

## CORE-B03-C052

### Question

> [!question]
> How is @PostConstruct integrated into the Spring lifecycle?

### Russian Translation

> Как @PostConstruct встроен в жизненный цикл Spring?

> [!answer]- Answer
> It is detected and invoked by an infrastructure BeanPostProcessor during the before-initialization phase.

### Explanation

Это важно для ordering: `@PostConstruct` не находится «вне BPP». Он вызывается одним из processors, поэтому relative order с custom BPP зависит от processor ordering.

### Exam Trap

> [!warning]
> Do not claim that every custom before-initialization processor always runs before @PostConstruct.

### Memory Hook

> @PostConstruct rides inside the before-init processor train.

---

## CORE-B03-C053

### Question

> [!question]
> In what order are different Spring initialization mechanisms invoked for one bean?

### Russian Translation

> В каком порядке вызываются разные initialization mechanisms одного bean?

> [!answer]- Answer
> @PostConstruct first, then InitializingBean.afterPropertiesSet(), then the configured custom init method.

### Explanation

Порядок относится к разным callback methods. Он позволяет framework-neutral annotation callback выполниться до Spring-specific interface callback и configured init method.

### Exam Trap

> [!warning]
> Do not put the custom init method before afterPropertiesSet.

### Memory Hook

> Annotation → interface → configured method.

---

## CORE-B03-C054

### Question

> [!question]
> What is the main trade-off of implementing InitializingBean?

### Russian Translation

> Каков главный недостаток реализации InitializingBean?

> [!answer]- Answer
> It gives an explicit lifecycle interface but couples the class to the Spring Framework API.

### Explanation

`afterPropertiesSet()` удобен для infrastructure beans. Для application POJO обычно предпочтительнее `@PostConstruct` или custom init method.

### Exam Trap

> [!warning]
> InitializingBean is supported, but “supported” does not mean “preferred for every business class.”

### Memory Hook

> Strong contract, stronger framework coupling.

---

## CORE-B03-C055

### Question

> [!question]
> How can Java configuration declare a custom initialization method?

### Russian Translation

> Как в Java configuration указать custom initialization method?

> [!answer]- Answer
> By setting the initMethod attribute of @Bean, for example @Bean(initMethod = "open").

### Explanation

Custom init method позволяет оставить managed class независимым от Spring lifecycle interfaces.

### Mini Example

```java
@Bean(initMethod = "open")
ClientPool clientPool() {
    return new ClientPool();
}
```

### Exam Trap

> [!warning]
> @Bean does not call every method named init automatically unless configuration or convention declares it.

### Memory Hook

> The factory method creates; initMethod prepares.

---

## CORE-B03-C056

### Question

> [!question]
> What happens if the same method is configured through more than one lifecycle mechanism?

### Russian Translation

> Что произойдёт, если один method настроен через несколько lifecycle mechanisms?

> [!answer]- Answer
> Spring avoids invoking the same method repeatedly when the same method name represents multiple configured lifecycle mechanisms.

### Explanation

Но смешивание одного method name для annotation, interface delegation и custom configuration делает design труднее для чтения. Разные callbacks лучше выражать явно.

### Exam Trap

> [!warning]
> The documented three-step order applies to different callback methods, not three guaranteed duplicate calls to one method.

### Memory Hook

> Different callbacks are ordered; the same callback is not multiplied.

---

## CORE-B03-C057

### Question

> [!question]
> At what lifecycle phase does postProcessAfterInitialization run?

### Russian Translation

> На каком этапе выполняется postProcessAfterInitialization?

> [!answer]- Answer
> After the bean's initialization callbacks have completed.

### Explanation

К этому моменту прошли @PostConstruct, afterPropertiesSet и custom init method. Processor может вернуть raw bean, wrapper или proxy.

### Exam Trap

> [!warning]
> “After initialization” does not mean after context shutdown or after destruction.

### Memory Hook

> Init finishes; final wrapping begins.

---

## CORE-B03-C058

### Question

> [!question]
> Where are Spring AOP proxies commonly created in the bean lifecycle?

### Russian Translation

> Где обычно создаются Spring AOP proxies в жизненном цикле bean?

> [!answer]- Answer
> Commonly in a BeanPostProcessor during postProcessAfterInitialization, after the target has completed its initialization callbacks.

### Explanation

Auto-proxy creator анализирует initialized target и возвращает proxy as the published bean reference. Advanced early-proxy cases exist, but they are not the default mental model.

### Exam Trap

> [!warning]
> Do not assume that the constructor or @PostConstruct normally executes through the final AOP proxy.

### Memory Hook

> Train the target first; dress it in proxy uniform afterward.

---

## CORE-B03-C059

### Question

> [!question]
> Why may @Transactional not apply to a method called from @PostConstruct?

### Russian Translation

> Почему @Transactional может не примениться к методу, вызванному из @PostConstruct?

> [!answer]- Answer
> Because the call is normally a direct self-invocation on the raw target during initialization, before normal external invocation through the final proxy.

### Explanation

Даже после proxy creation self-invocation bypasses proxy interception. Для startup transaction лучше использовать отдельный bean или post-refresh orchestration.

### Exam Trap

> [!warning]
> Adding @Transactional to the called method does not force this.method() to pass through the proxy.

### Memory Hook

> No proxy boundary, no proxy advice.

---

## CORE-B03-C060

### Question

> [!question]
> Can a BeanPostProcessor replace the original bean instance?

### Russian Translation

> Может ли BeanPostProcessor заменить исходный экземпляр bean?

> [!answer]- Answer
> Yes. It can return a wrapper or proxy, and the container continues with the returned object as the bean reference.

### Explanation

Именно этот contract лежит в основе многих Spring infrastructure features. Consumer может получить proxy, хотя target class был instantiated separately.

### Exam Trap

> [!warning]
> BeanPostProcessor callbacks are not required to return the exact same object instance.

### Memory Hook

> The processor receives a bean and may return a new public face.

---

## CORE-B03-C061

### Question

> [!question]
> When is a singleton bean considered fully initialized and ready for publication?

### Russian Translation

> Когда singleton bean считается полностью инициализированным и готовым к публикации?

> [!answer]- Answer
> After successful initialization callbacks and after-initialization post-processing have completed and the final bean reference is available.

### Explanation

До этого initialization может fail, processor может заменить reference, а final proxy ещё может отсутствовать.

### Exam Trap

> [!warning]
> A non-null constructor result is not equivalent to a successfully published singleton.

### Memory Hook

> Ready means initialized, processed, and publishable.

---

## CORE-B03-C062

### Question

> [!question]
> Why is heavy external work risky inside @PostConstruct for a singleton bean?

### Russian Translation

> Почему тяжёлая внешняя работа опасна внутри @PostConstruct singleton bean?

> [!answer]- Answer
> Because initialization occurs while the singleton is still being created; long external calls or cross-bean waits can slow startup or contribute to initialization deadlocks.

### Explanation

Init callback лучше использовать для local validation и bounded preparation. Координацию после всех singleton beans можно вынести в SmartInitializingSingleton или ContextRefreshedEvent.

### Exam Trap

> [!warning]
> @PostConstruct is not automatically an asynchronous or post-startup hook.

### Memory Hook

> Initialize locally; orchestrate globally later.

---

## CORE-B03-C063

### Question

> [!question]
> What does SmartInitializingSingleton provide?

### Russian Translation

> Что предоставляет SmartInitializingSingleton?

> [!answer]- Answer
> A callback after the BeanFactory has instantiated all regular singleton beans during singleton pre-instantiation.

### Explanation

Он полезен для coordination, которой нужны fully created peer singletons. Это не общий callback prototype beans и не destruction mechanism.

### Exam Trap

> [!warning]
> Do not confuse afterSingletonsInstantiated with afterPropertiesSet on one bean.

### Memory Hook

> afterPropertiesSet: this bean is ready; afterSingletonsInstantiated: the singleton neighborhood is built.

---

## CORE-B03-C064

### Question

> [!question]
> In what order are different destruction mechanisms invoked for one bean?

### Russian Translation

> В каком порядке вызываются разные destruction mechanisms одного bean?

> [!answer]- Answer
> @PreDestroy first, then DisposableBean.destroy(), then the configured custom destroy method.

### Explanation

Это destruction mirror initialization order: annotation, Spring interface, configured method.

### Exam Trap

> [!warning]
> Do not reverse DisposableBean.destroy and the custom destroy method.

### Memory Hook

> Annotation → interface → configured method, both at birth and retirement.

---

## CORE-B03-C065

### Question

> [!question]
> What must happen for singleton destruction callbacks to run in a standalone ApplicationContext?

### Russian Translation

> Что должно произойти, чтобы destruction callbacks singleton bean выполнились в standalone ApplicationContext?

> [!answer]- Answer
> The context must be closed, for example explicitly or through try-with-resources.

### Explanation

Container вызывает destruction callbacks для managed singleton beans during shutdown. Если context не закрыт, custom resources могут остаться открыты.

### Mini Example

```java
try (AnnotationConfigApplicationContext context =
         new AnnotationConfigApplicationContext(AppConfig.class)) {
    context.getBean(Service.class).work();
}
```

### Exam Trap

> [!warning]
> Garbage collection of the bean is not the Spring lifecycle signal that invokes @PreDestroy.

### Memory Hook

> Close the context to retire its singletons.

---

## CORE-B03-C066

### Question

> [!question]
> Does Spring automatically invoke destruction callbacks for ordinary prototype beans?

### Russian Translation

> Вызывает ли Spring автоматически destruction callbacks обычных prototype beans?

> [!answer]- Answer
> No. Spring creates and initializes a prototype bean, then hands it to the client without managing its complete destruction lifecycle.

### Explanation

Consumer или отдельный lifecycle manager должен закрыть prototype-owned resources.

### Exam Trap

> [!warning]
> Prototype changes creation frequency and ownership; it does not mean “destroy on context close.”

### Memory Hook

> Spring births the prototype; the caller handles retirement.

---

## CORE-B03-C067

### Question

> [!question]
> What is the main trade-off of implementing DisposableBean?

### Russian Translation

> Каков главный недостаток реализации DisposableBean?

> [!answer]- Answer
> It provides a precise destroy callback but couples the class to Spring.

### Explanation

Для application POJO чаще выбирают @PreDestroy или custom destroy method. Infrastructure component может сознательно использовать Spring interface.

### Exam Trap

> [!warning]
> DisposableBean.destroy is a container lifecycle callback, not Java finalization.

### Memory Hook

> Explicit shutdown contract, explicit framework coupling.

---

## CORE-B03-C068

### Question

> [!question]
> What is the difference between bean initialization callbacks and the Lifecycle or SmartLifecycle interfaces?

### Russian Translation

> Чем initialization callbacks отличаются от Lifecycle или SmartLifecycle?

> [!answer]- Answer
> Initialization callbacks prepare an individual bean during creation. Lifecycle and SmartLifecycle model runtime start/stop behavior coordinated by the container.

### Explanation

`@PostConstruct` не является replacement для phased runtime startup. `SmartLifecycle` поддерживает auto-startup, phase ordering и stop semantics.

### Exam Trap

> [!warning]
> A bean can be initialized but not yet started in the Lifecycle sense.

### Memory Hook

> Init creates readiness; Lifecycle controls running state.

---

# Batch Review Checklist

- [ ] Я могу воспроизвести полный lifecycle без подсказки.
- [ ] Я различаю raw target и published proxy.
- [ ] Я знаю порядок трёх initialization callbacks.
- [ ] Я знаю порядок трёх destruction callbacks.
- [ ] Я могу объяснить, почему @PostConstruct связан с BeanPostProcessor.
- [ ] Я не обещаю prototype automatic destruction.
- [ ] Я различаю @PostConstruct, SmartInitializingSingleton и SmartLifecycle.
- [ ] Я могу диагностировать отсутствие @Transactional в init callback.
