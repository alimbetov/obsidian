---
type: moc
domain: certification
status: active
tags:
  - certification
---

# Certification MOC

## Общая модель

Каноническая теория хранится в `10_CONCEPTS`. Сертификационные карточки используют её для active recall, discrimination, mechanism explanation и production transfer.

```mermaid
flowchart LR
    C[Concept] --> Q[Base card]
    Q --> T[Exam trap]
    T --> D[Drill question]
    D --> P[Production transfer]
    P --> L[Executable lab]
    L --> R[Review outcome]
    R -->|weak| C
    R -->|confident| N[Next objective]
```

## Review entry point

- [[00_HOME/Review Dashboard]]

## Java certification route

### Java concurrency foundation

- [[10_CONCEPTS/Java/Concurrency/Java Memory Model]]
- [[10_CONCEPTS/Java/Concurrency/Happens-Before]]
- [[10_CONCEPTS/Java/Concurrency/volatile]]
- [[10_CONCEPTS/Java/Concurrency/synchronized]]
- [[10_CONCEPTS/Java/Concurrency/ExecutorService]]
- [[10_CONCEPTS/Java/Concurrency/CompletableFuture]]
- [[10_CONCEPTS/Java/Concurrency/Virtual Threads]]
- [[10_CONCEPTS/Java/Concurrency/Atomic CAS and Counters]]
- [[10_CONCEPTS/Java/Concurrency/Deadlock Livelock and Lock Ordering]]
- [[10_CONCEPTS/Java/Concurrency/Concurrent Collections and Backpressure]]

## Spring certification route

- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Certification Card System|Card System]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Core Card Roadmap|Spring Core Roadmap]]

### Published Spring Core batches

| Batch | Cards | Concept | Status |
|---|---:|---|---|
| [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B01/CORE-B01 Cards|CORE-B01]] | 20 | [[10_CONCEPTS/Spring/Core/Spring Core Foundations]] | published |
| [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B02/CORE-B02 Cards|CORE-B02]] | 24 | [[10_CONCEPTS/Spring/Core/Dependency Resolution and Optional Injection]] | published |
| [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B03/CORE-B03 Cards|CORE-B03]] | 24 | [[10_CONCEPTS/Spring/Core/Bean Lifecycle from Definition to Destruction]] | published |
| [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B04/CORE-B04 Cards|CORE-B04]] | 24 | [[10_CONCEPTS/Spring/Core/Container Extension Points]] | published |
| [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B05/CORE-B05 Cards|CORE-B05]] | 24 | [[10_CONCEPTS/Spring/Core/Configuration Profiles and Externalized Properties]] | published |
| [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B06/CORE-B06 Cards|CORE-B06]] | 24 | [[10_CONCEPTS/Spring/Core/Advanced Core Scopes FactoryBean and Context Hierarchy]] | published |

Total published Spring Core cards:

```text
140
```

### Supporting practice

- [[01_MAPS/Spring Core Foundation Map.canvas]]
- [[01_MAPS/Spring Dependency Resolution Map.canvas]]
- [[01_MAPS/Spring Bean Lifecycle Map.canvas]]
- [[01_MAPS/Spring Container Extension Points Map.canvas]]
- [[01_MAPS/Spring Configuration and Profiles Map.canvas]]
- [[01_MAPS/Spring Advanced Core Map.canvas]]
- [[40_PRODUCTION_CASES/Spring/Dependency Resolution Production Cases]]
- [[40_PRODUCTION_CASES/Spring/Bean Lifecycle Production Cases]]
- [[40_PRODUCTION_CASES/Spring/Container Extension Point Production Cases]]
- [[40_PRODUCTION_CASES/Spring/Configuration and Profiles Production Cases]]
- [[40_PRODUCTION_CASES/Spring/Advanced Core Production Cases]]
- [[50_LABS/Spring/Core-B02/README]]
- [[50_LABS/Spring/Core-B03/README]]
- [[50_LABS/Spring/Core-B04/README]]
- [[50_LABS/Spring/Core-B05/README]]
- [[50_LABS/Spring/Core-B06/README]]

## Card format

1. `Question` на английском;
2. `Russian Translation`;
3. `Answer`;
4. `Explanation`;
5. `Exam Trap`;
6. `Mini Example` для сложной темы;
7. `Memory Hook` для легко путаемой темы;
8. production transfer для mechanism-heavy темы.

Целевая модель всей Spring certification base:

```text
750 base cards + 150 exam drill questions = 900 items
```

Spring Core foundation now contributes:

```text
140 reviewed-base candidates
```

## Testing process

1. Ответить, не открывая explanation.
2. Зафиксировать confident или guessed.
3. Связать вопрос с канонической concept note.
4. Разобрать неправильные варианты.
5. Применить правило к новому production scenario.
6. Предсказать lab trace до запуска.
7. Повышать confidence только после повторного успешного recall.

## Outcome taxonomy

- `correct-confident`;
- `correct-guessed`;
- `wrong-concept`;
- `wrong-attention`;
- `wrong-confusion`.

## Next Spring certification route

`AOP — Proxies and Advice`:

- AOP terminology;
- JDK proxy vs CGLIB;
- proxy selection;
- pointcut matching;
- interceptor chain;
- self-invocation;
- method visibility/finality;
- aspect ordering;
- relationship to transactions, async, caching and security.
