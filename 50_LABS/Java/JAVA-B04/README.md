---
type: lab-index
domain: java
route: JAVA-B04
status: published
java_versions: [17, 21]
positive_proofs: 5
negative_cases: 6
tags: [java, exceptions, lab, proof]
---

# JAVA-B04 — Exceptions and Resource Safety Lab

> [!summary]
> Лаборатория превращает правила exception flow в наблюдаемые assertions. Сначала ученик делает прогноз, затем запускает proof и объясняет каждую гарантию.

## Что доказывается

### Runtime proof

1. catch/finally ordering;
2. cause preservation;
3. reverse resource close order;
4. primary и suppressed exception;
5. cleanup ранее открытого resource при failure следующего initializer.

### Expected compile failures

1. checked exception не caught/declared;
2. subtype catch после parent catch;
3. overlapping multi-catch alternatives;
4. reassignment multi-catch parameter;
5. broader checked exception в override;
6. reassigned variable в Java 9+ resource specification.

## Перед запуском

Запиши прогноз:

```text
catch/finally trace:
resource close order:
primary exception:
suppressed exception:
какие 6 snippets должны не скомпилироваться:
```

## Запуск на Java 17

```bash
cd 50_LABS/Java/JAVA-B04
JAVA_RELEASE=17 bash run.sh
```

## Запуск на Java 21

```bash
cd 50_LABS/Java/JAVA-B04
JAVA_RELEASE=21 bash run.sh
```

Ожидаемый финал:

```text
JAVA-B04 runtime proof PASS
JAVA-B04 negative compile bank PASS
```

## Как объяснить proof

Недостаточно сказать «tests green». Для каждого assertion ответь:

1. Какой normal path ожидался?
2. Где начался exceptional path?
3. Какой handler был выбран?
4. Какие cleanup actions выполнились?
5. Какое правило compiler проверил в negative case?

## Почему lab не заменяет design

Код может корректно ловить exception и при этом иметь плохую policy:

- скрывать failure;
- логировать один incident пять раз;
- раскрывать secrets;
- удерживать resource слишком долго;
- использовать exception для частого обычного branch.

Для design-level практики используй:

- [[40_PRODUCTION_CASES/Java/Java Exceptions Production Cases]]

## Navigation

- [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Roadmap]]
- [[10_CONCEPTS/Java/Exceptions/Java Exceptions and Resource Safety]]
- [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Cards]]
- [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Drills]]
