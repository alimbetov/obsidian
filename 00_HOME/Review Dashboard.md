---
type: dashboard
domain: learning-system
status: active
last_reviewed: 2026-07-20
tags:
  - dashboard
  - review
  - active-recall
---

# Review Dashboard

> [!summary]
> Главная рабочая страница для повторения. Она отделяет **прочитано** от **воспроизведено**, правильный уверенный ответ от угадывания и знание определения от способности применить механизм к production-case.

## Сегодняшний цикл

```mermaid
flowchart LR
    A[Choose weak concept] --> B[Recall without notes]
    B --> C[Answer cards]
    C --> D[Run or inspect example]
    D --> E[Explain production case]
    E --> F[Record outcome]
    F -->|weak| G[Schedule soon]
    F -->|strong| H[Increase interval]
```

## Current Learning Routes

### Java Concurrency

1. [[10_CONCEPTS/Java/Concurrency/Concurrency Learning Path]]
2. [[01_MAPS/Java Concurrency Map.canvas]]
3. [[01_MAPS/Java Advanced Concurrency Map.canvas]]
4. [[20_QUESTIONS/Interview/Java/Concurrency/Advanced Concurrency Recall]]
5. [[50_LABS/Java/Concurrency/README]]

### Spring Certification

1. [[10_CONCEPTS/Spring/Core/Spring Core Foundations]]
2. [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B01/CORE-B01 Cards]]
3. [[10_CONCEPTS/Spring/Core/Dependency Resolution and Optional Injection]]
4. [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B02/CORE-B02 Cards]]
5. [[10_CONCEPTS/Spring/Core/Bean Lifecycle from Definition to Destruction]]
6. [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B03/CORE-B03 Cards]]
7. [[10_CONCEPTS/Spring/Core/Container Extension Points]]
8. [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B04/CORE-B04 Cards]]
9. [[30_CERTIFICATIONS/Spring/2V0-72.22/Spring Core Card Roadmap]]

```text
CORE-B01  20 cards
CORE-B02  24 cards
CORE-B03  24 cards
CORE-B04  24 cards
TOTAL     92 cards
```

## Confidence Scale

| confidence | Реальное значение |
|---:|---|
| 0 | тема не изучена или не проверена |
| 1 | узнаю термин, но не воспроизвожу |
| 2 | отвечаю с подсказкой |
| 3 | объясняю самостоятельно |
| 4 | решаю новый code/production case |
| 5 | защищаю trade-offs на Senior-интервью |

> [!danger]
> Confidence повышается не после чтения, а после самостоятельного воспроизведения и успешного transfer task.

## Outcome Taxonomy

| outcome | Что произошло | Следующее действие |
|---|---|---|
| `correct-confident` | ответ точный и объяснён | увеличить interval |
| `correct-guessed` | вариант выбран без механизма | повторить как ошибку |
| `wrong-concept` | неверна модель | перечитать concept + lab |
| `wrong-attention` | пропущено NOT/select N/phase | attention drill |
| `wrong-confusion` | перепутаны похожие механизмы | comparison card |

## Dynamic Search — Unverified Concepts

```query
[confidence:0]
```

## Dynamic Search — Learning Status

```query
[status:learning]
```

## Dynamic Search — Certification Questions

```query
[type:certification-question]
```

Batch routes:

- [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B01/CORE-B01 Cards]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B02/CORE-B02 Cards]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B03/CORE-B03 Cards]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B04/CORE-B04 Cards]]

## Spring contrast drills

### CORE-B01

- `@Bean` vs `@Component`;
- BeanFactory vs ApplicationContext;
- constructor vs setter vs field injection.

### CORE-B02

- `@Primary` vs `@Qualifier`;
- `Optional<T>` vs `ObjectProvider<T>`;
- collection ordering vs startup ordering.

### CORE-B03

- instantiation vs initialization;
- raw target vs published proxy;
- `@PostConstruct` vs `afterPropertiesSet()` vs custom init;
- singleton destruction vs prototype ownership.

### CORE-B04

- BFPP vs BPP;
- BDRPP vs BFPP;
- before-instantiation vs before-initialization;
- auto-detected ordering vs programmatic registration order;
- metadata mutation vs instance wrapping;
- normal reference vs early reference;
- BPP dependency vs auto-proxy eligibility.

CORE-B04 memory model:

```text
Registry adds recipes.
Factory processor edits recipes.
Bean processor edits or replaces objects.
Instantiation-aware hooks surround creation and population.
Smart hooks predict type, constructors and early references.
Destruction-aware hooks clean before destruction.
```

Practice:

- [[01_MAPS/Spring Container Extension Points Map.canvas]]
- [[40_PRODUCTION_CASES/Spring/Container Extension Point Production Cases]]
- [[50_LABS/Spring/Core-B04/README]]

## Active Weakness Register

| Confusion pair | Проверка |
|---|---|
| `@Primary` vs `@Qualifier` | default preference против semantic filter |
| `Optional<T>` vs `ObjectProvider<T>` | construction-time absence против lazy lookup |
| List ordering vs bean startup order | `@Order` против dependency lifecycle |
| instantiation vs initialization | constructor против init pipeline |
| BFPP vs BPP | metadata против instance |
| before-instantiation vs before-initialization | target ещё не существует против target уже создан |
| programmatic vs auto-detected BPP | registration order против Ordered semantics |
| early bean creation vs full proxying | infrastructure timing против annotations |
| visibility vs atomicity | `volatile` против compound operation |
| deadlock vs contention | permanent cycle против long wait |
| `thenApply` vs `thenCompose` | transform против async flattening |

## Ten-Minute Review Session

1. Выбрать одну строку Active Weakness Register.
2. Не открывая notes, проговорить различие.
3. Ответить на 3 связанные карточки.
4. Нарисовать mechanism diagram от руки.
5. Открыть concept и исправить пропуски.
6. Зафиксировать outcome.

## Thirty-Minute Deep Session

```text
5 min   recall map
10 min  certification cards
10 min  production case or lab
5 min   summary from memory
```

## Weekly Review Protocol

1. Найти все `correct-guessed` outcomes.
2. Найти recurring confusion pairs.
3. Выбрать одну тему confidence 2 и довести до 3.
4. Выбрать одну тему confidence 3 и решить новый production case.
5. Проверить, какие labs ещё не запускались в реальном environment.
6. Не добавлять новый batch, если предыдущий не получил хотя бы первый review cycle.

## Rule of Completion

Тема считается готовой не когда заметка заполнена, а когда выполнены проверки:

- [ ] Definition recall.
- [ ] Mechanism explanation.
- [ ] Lifecycle phase identification.
- [ ] Trap discrimination.
- [ ] Production transfer.
- [ ] Lab trace prediction.

## Next Planned Modules

- Spring `CORE-B05`: configuration and profiles.
- Java: ForkJoinPool and parallel streams.
- Databases: transactions, isolation and locks.
- Messaging: delivery semantics and idempotency.
