---
type: certification-batch
certification: spring-2V0-72.22
domain: spring
subdomain: container-extension-points
batch_id: CORE-B04
status: published
card_count: 24
language:
  question: en
  translation: ru
  explanation: ru
prerequisites:
  - "[[10_CONCEPTS/Spring/Core/Bean Lifecycle from Definition to Destruction]]"
related:
  - "[[10_CONCEPTS/Spring/Core/Container Extension Points]]"
tags:
  - spring
  - certification
  - flashcards
---

# CORE-B04 — Container Extension Point Cards

> [!summary]
> Партия тренирует различие metadata-phase и instance-phase, ordering processors, advanced instantiation hooks, early references и destruction processing.

## Как проходить

1. Ответить по English Question без подсказки.
2. Назвать lifecycle phase.
3. Объяснить, с чем работает extension point: definition или instance.
4. Раскрыть Answer и Explanation.
5. Применить правило к production symptom.
6. Зафиксировать outcome в Review Dashboard.

---

## CORE-B04-C069

### Question

> [!question]
> What is the fundamental difference between BeanFactoryPostProcessor and BeanPostProcessor?

### Russian Translation

> В чём фундаментальное различие BeanFactoryPostProcessor и BeanPostProcessor?

> [!answer]- Answer
> A BeanFactoryPostProcessor works with bean configuration metadata before ordinary beans are instantiated. A BeanPostProcessor works with actual bean instances during their creation lifecycle.

### Explanation

Первый изменяет recipe — `BeanDefinition`. Второй получает уже создаваемый object и может проверить, модифицировать или заменить exposed reference.

### Exam Trap

> [!warning]
> BeanFactoryPostProcessor не post-process-ит экземпляр BeanFactory как business object.

### Memory Hook

> Factory processor edits recipes; bean processor edits objects.

---

## CORE-B04-C070

### Question

> [!question]
> What additional capability does BeanDefinitionRegistryPostProcessor provide?

### Russian Translation

> Какую дополнительную возможность даёт BeanDefinitionRegistryPostProcessor?

> [!answer]- Answer
> It can register, remove, or otherwise modify BeanDefinitions through BeanDefinitionRegistry before regular BeanFactoryPostProcessor processing.

### Explanation

Он расширяет BFPP и получает ранний registry callback, поэтому может изменить сам набор definitions.

### Exam Trap

> [!warning]
> Обычный BeanFactoryPostProcessor меняет существующую metadata, но registry-specific API даёт именно BDRPP.

### Memory Hook

> Registry processor changes the catalog; factory processor edits catalog entries.

---

## CORE-B04-C071

### Question

> [!question]
> Which callback runs first in a BeanDefinitionRegistryPostProcessor?

### Russian Translation

> Какой callback BeanDefinitionRegistryPostProcessor выполняется первым?

> [!answer]- Answer
> postProcessBeanDefinitionRegistry() runs before the inherited postProcessBeanFactory().

### Explanation

Сначала container завершает registry-level extension, затем выполняет factory metadata processing.

### Exam Trap

> [!warning]
> Interface имеет две фазы, а не только registry callback.

### Memory Hook

> Register first, refine second.

---

## CORE-B04-C072

### Question

> [!question]
> Why should a BeanFactoryPostProcessor normally avoid calling getBean()?

### Russian Translation

> Почему BeanFactoryPostProcessor обычно не должен вызывать getBean()?

> [!answer]- Answer
> Because getBean() can instantiate application beans prematurely, before the normal BeanPostProcessor and auto-proxy infrastructure is fully active.

### Explanation

BFPP предназначен для metadata. Переход к instance creation нарушает стандартную фазность container.

### Exam Trap

> [!warning]
> Техническая возможность вызвать getBean() не означает lifecycle correctness.

### Memory Hook

> Metadata phase: inspect definitions, not instances.

---

## CORE-B04-C073

### Question

> [!question]
> Why is a BeanFactoryPostProcessor-returning @Bean method commonly declared static?

### Russian Translation

> Почему @Bean-метод, возвращающий BeanFactoryPostProcessor, обычно объявляют static?

> [!answer]- Answer
> A static factory method can be invoked without prematurely instantiating its declaring @Configuration class and its dependencies.

### Explanation

BFPP создаётся очень рано. Non-static factory method требует configuration instance и может вызвать lifecycle conflicts.

### Mini Example

```java
@Bean
static BeanFactoryPostProcessor metadataProcessor() {
    return new MetadataProcessor();
}
```

### Memory Hook

> Static lets infrastructure arrive before configuration instances.

---

## CORE-B04-C074

### Question

> [!question]
> What kind of state should a BeanFactoryPostProcessor modify?

### Russian Translation

> Какое состояние должен изменять BeanFactoryPostProcessor?

> [!answer]- Answer
> BeanDefinition metadata such as property values, scope, lazy-init, qualifiers, class or factory metadata.

### Explanation

Он не предназначен для вызова business methods уже созданного объекта.

### Exam Trap

> [!warning]
> Изменение поля raw instance — не metadata post-processing.

### Memory Hook

> Change the blueprint before construction.

---

## CORE-B04-C075

### Question

> [!question]
> At what points does a basic BeanPostProcessor receive callbacks?

### Russian Translation

> В какие моменты базовый BeanPostProcessor получает callbacks?

> [!answer]- Answer
> Before initialization callbacks and after initialization callbacks for each eligible bean instance.

### Explanation

Target уже создан и populated. Between callbacks container invokes init mechanisms.

### Exam Trap

> [!warning]
> postProcessBeforeInitialization не выполняется до constructor.

### Memory Hook

> BPP surrounds init, not instantiation.

---

## CORE-B04-C076

### Question

> [!question]
> May postProcessAfterInitialization return a different object reference?

### Russian Translation

> Может ли postProcessAfterInitialization вернуть другой object reference?

> [!answer]- Answer
> Yes. It may return a wrapper, decorator, or proxy that becomes the exposed bean reference.

### Explanation

Это основа многих Spring proxy-based features.

### Exam Trap

> [!warning]
> Consumer не обязан получить тот же object identity, который создал constructor.

### Memory Hook

> After init decides what the container publishes.

---

## CORE-B04-C077

### Question

> [!question]
> Is a BeanPostProcessor automatically applied to beans in parent and child contexts?

### Russian Translation

> Применяется ли BeanPostProcessor автоматически к beans parent и child contexts?

> [!answer]- Answer
> No. BeanPostProcessors are scoped to the container in which they are registered.

### Explanation

Context hierarchy не превращает processors в global cross-container interceptors.

### Exam Trap

> [!warning]
> Bean visibility across hierarchy и processor scope — разные правила.

### Memory Hook

> Processor belongs to its own container.

---

## CORE-B04-C078

### Question

> [!question]
> Why should an @Bean method that creates a BeanPostProcessor expose a processor-compatible return type?

### Russian Translation

> Почему @Bean-метод BeanPostProcessor должен иметь совместимый declared return type?

> [!answer]- Answer
> So the ApplicationContext can detect its post-processor nature early enough from factory-method metadata.

### Explanation

Возврат `Object` может скрыть infrastructure type до полного создания bean, а processor нужен раньше regular beans.

### Mini Example

```java
@Bean
BeanPostProcessor auditProcessor() {
    return new AuditProcessor();
}
```

### Memory Hook

> Declare infrastructure as infrastructure.

---

## CORE-B04-C079

### Question

> [!question]
> How are programmatically registered BeanPostProcessors ordered?

### Russian Translation

> Как упорядочиваются programmatically registered BeanPostProcessors?

> [!answer]- Answer
> They execute in registration order. Their PriorityOrdered or Ordered contracts are ignored.

### Explanation

`addBeanPostProcessor()` создаёт explicit sequence; такие processors выполняются до auto-detected processors.

### Exam Trap

> [!warning]
> @Order не переупорядочит processors, добавленные вручную.

### Memory Hook

> Programmatic means call order is execution order.

---

## CORE-B04-C080

### Question

> [!question]
> How are auto-detected processors generally grouped for ordering?

### Russian Translation

> Как в общем случае группируются auto-detected processors по order?

> [!answer]- Answer
> PriorityOrdered processors first, then Ordered processors, then unordered processors.

### Explanation

Внутри categories используются order values и registration details.

### Exam Trap

> [!warning]
> Не путать auto-detected ordering с programmatic registration.

### Memory Hook

> Priority, order, then ordinary.

---

## CORE-B04-C081

### Question

> [!question]
> Why can a business bean injected directly into a BeanPostProcessor miss auto-proxying?

### Russian Translation

> Почему business bean, напрямую внедрённый в BeanPostProcessor, может остаться без auto-proxy?

> [!answer]- Answer
> Because BeanPostProcessors and their direct dependencies are instantiated during an early startup phase before all post-processors, including auto-proxy creators, are available.

### Explanation

Bean может получить log `not eligible for getting processed by all BeanPostProcessor interfaces`.

### Exam Trap

> [!warning]
> Проблема не в annotation на service, а во времени его создания.

### Memory Hook

> A processor dependency may be created before processing is complete.

---

## CORE-B04-C082

### Question

> [!question]
> What can postProcessBeforeInstantiation() do?

### Russian Translation

> Что может сделать postProcessBeforeInstantiation()?

> [!answer]- Answer
> It may return a substitute object before normal target instantiation, short-circuiting the standard creation path for that bean.

### Explanation

Это callback `InstantiationAwareBeanPostProcessor`, а не обычного BPP.

### Exam Trap

> [!warning]
> Не путать с postProcessBeforeInitialization(), где target уже существует.

### Memory Hook

> Before instantiation can replace creation itself.

---

## CORE-B04-C083

### Question

> [!question]
> What does returning false from postProcessAfterInstantiation() mean?

### Russian Translation

> Что означает возврат false из postProcessAfterInstantiation()?

> [!answer]- Answer
> It tells Spring to skip normal property population for that bean.

### Explanation

Object уже создан, но automatic property/dependency population не продолжается стандартным путём.

### Exam Trap

> [!warning]
> false не уничтожает bean и не отменяет constructor invocation.

### Memory Hook

> Object exists; population is vetoed.

---

## CORE-B04-C084

### Question

> [!question]
> What is the purpose of postProcessProperties()?

### Russian Translation

> Для чего нужен postProcessProperties()?

> [!answer]- Answer
> It allows an InstantiationAwareBeanPostProcessor to inspect or modify property values and perform custom injection before Spring applies normal property population.

### Explanation

Annotation-driven field and method injection is implemented through this class of infrastructure.

### Exam Trap

> [!warning]
> Этот callback относится к population phase, не к after-initialization proxying.

### Memory Hook

> Properties hook prepares injection before init.

---

## CORE-B04-C085

### Question

> [!question]
> What is SmartInstantiationAwareBeanPostProcessor primarily intended for?

### Russian Translation

> Для чего прежде всего предназначен SmartInstantiationAwareBeanPostProcessor?

> [!answer]- Answer
> Special-purpose framework infrastructure that helps with bean type prediction, constructor selection, and early bean references.

### Explanation

Это advanced interface, обычно используемый container/AOP/injection infrastructure, а не everyday application code.

### Exam Trap

> [!warning]
> Название Smart не означает общий «улучшенный BPP для любого случая».

### Memory Hook

> Smart hooks help Spring decide before completion.

---

## CORE-B04-C086

### Question

> [!question]
> What does determineCandidateConstructors() influence?

### Russian Translation

> На что влияет determineCandidateConstructors()?

> [!answer]- Answer
> It can tell the container which constructors should be considered for autowiring.

### Explanation

Constructor annotation infrastructure может участвовать в выборе constructor до instantiation.

### Exam Trap

> [!warning]
> Callback не создаёт object сам по себе; он предлагает candidates.

### Memory Hook

> Candidate constructors guide creation.

---

## CORE-B04-C087

### Question

> [!question]
> What does predictBeanType() provide?

### Russian Translation

> Что предоставляет predictBeanType()?

> [!answer]- Answer
> An early prediction of the eventual bean type for type matching and infrastructure decisions.

### Explanation

Это особенно важно, если exposed object может быть proxy другого runtime class.

### Exam Trap

> [!warning]
> Prediction не обязана означать, что bean уже instantiated.

### Memory Hook

> Predict the shape before building the object.

---

## CORE-B04-C088

### Question

> [!question]
> What is getEarlyBeanReference() used for?

### Russian Translation

> Для чего используется getEarlyBeanReference()?

> [!answer]- Answer
> It can expose an early, often proxy-compatible reference during certain circular-dependency resolution scenarios.

### Explanation

Цель — сохранить consistency между early reference и later published reference насколько позволяет infrastructure.

### Exam Trap

> [!warning]
> Early reference не является универсальным и безопасным решением всех circular dependencies.

### Memory Hook

> Early reference preserves identity under pressure—when possible.

---

## CORE-B04-C089

### Question

> [!question]
> What does DestructionAwareBeanPostProcessor add to the lifecycle?

### Russian Translation

> Что добавляет DestructionAwareBeanPostProcessor в lifecycle?

> [!answer]- Answer
> A callback before a bean's destruction callbacks, allowing processor-specific cleanup or deregistration.

### Explanation

Он расширяет BPP и участвует только там, где container выполняет destruction lifecycle.

### Exam Trap

> [!warning]
> Callback не превращает обычный prototype в fully managed destruction scope.

### Memory Hook

> Destruction-aware infrastructure leaves before the bean is destroyed.

---

## CORE-B04-C090

### Question

> [!question]
> What is the purpose of requiresDestruction(Object bean)?

### Russian Translation

> Для чего нужен requiresDestruction(Object bean)?

> [!answer]- Answer
> It lets a DestructionAwareBeanPostProcessor indicate whether a particular bean actually needs its destruction callback.

### Explanation

Это оптимизирует processing, когда cleanup относится только к annotated или special beans.

### Exam Trap

> [!warning]
> Метод не закрывает ресурс; он отвечает, нужен ли processor callback.

### Memory Hook

> Filter first, clean only relevant beans.

---

## CORE-B04-C091

### Question

> [!question]
> Why is AutowiredAnnotationBeanPostProcessor an important example?

### Russian Translation

> Почему AutowiredAnnotationBeanPostProcessor — важный пример?

> [!answer]- Answer
> It demonstrates that annotation-driven injection is implemented by container infrastructure that analyzes metadata and participates in constructor and property injection phases.

### Explanation

Annotation сама ничего не выполняет. Поведение возникает из annotation + processor + lifecycle phase.

### Exam Trap

> [!warning]
> JVM не знает семантику @Autowired.

### Memory Hook

> Annotation is metadata; processor makes it behavior.

---

## CORE-B04-C092

### Question

> [!question]
> Which extension point should be chosen for each task: add definitions, edit definitions, wrap initialized beans, customize property injection, or clean up before destruction?

### Russian Translation

> Какие extension points выбрать для добавления definitions, изменения definitions, wrapping beans, custom injection и cleanup перед destruction?

> [!answer]- Answer
> Add definitions: BeanDefinitionRegistryPostProcessor. Edit definitions: BeanFactoryPostProcessor. Wrap initialized beans: BeanPostProcessor. Customize pre-population injection: InstantiationAwareBeanPostProcessor. Cleanup before destruction: DestructionAwareBeanPostProcessor.

### Explanation

Выбор определяется lifecycle phase и видом изменяемого объекта.

### Exam Trap

> [!warning]
> Не выбирай processor только по похожему названию; сначала назови phase.

### Memory Hook

> Phase first, interface second.

---

# Batch Review Checklist

- [ ] Могу объяснить metadata vs instance без чтения.
- [ ] Не вызываю `getBean()` как обычную практику внутри BFPP.
- [ ] Помню `static @Bean` для ранних factory processors.
- [ ] Различаю programmatic и auto-detected ordering.
- [ ] Различаю before-instantiation и before-initialization.
- [ ] Объясняю early bean creation и loss of auto-proxy eligibility.
- [ ] Могу выбрать extension point для production requirement.

# Related

- [[10_CONCEPTS/Spring/Core/Container Extension Points]]
- [[01_MAPS/Spring Container Extension Points Map.canvas]]
- [[40_PRODUCTION_CASES/Spring/Container Extension Point Production Cases]]
- [[50_LABS/Spring/Core-B04/README]]
