---
type: concept
domain: java
subdomain: concurrency
difficulty: advanced
status: learning
confidence: 0
interview: true
certification:
  - java
java_versions:
  - 21
production_relevance: high
prerequisites:
  - "[[ExecutorService]]"
  - "[[Java Memory Model]]"
related:
  - "[[ThreadLocal]]"
  - "[[CompletableFuture]]"
tags:
  - java
  - concurrency
  - virtual-threads
---

# Virtual Threads

> [!summary] За 30 секунд
> Virtual threads в Java 21 делают thread-per-task/thread-per-request масштабируемым для большого количества blocking I/O. Они уменьшают стоимость ожидания, но не ускоряют CPU-bound вычисления и не отменяют ограничения БД, сети или внешних сервисов.

## Интуиция: официанты и кухня

Platform thread похож на отдельного официанта, который занимает физическое рабочее место даже пока ждёт кухню.

Virtual thread похож на заказ-карточку:

- когда операция ждёт I/O, runtime может освободить carrier thread;
- carrier обслуживает другую runnable virtual thread;
- после готовности ожидание продолжается.

> [!warning] Аналогия не означает «virtual thread выполняется без platform thread».
> Когда virtual thread реально выполняет Java-код, её исполняет carrier platform thread.

## Platform vs virtual

| Свойство | Platform thread | Virtual thread |
|---|---|---|
| Стоимость создания | сравнительно высокая | значительно ниже |
| Количество | обычно ограничено | может быть очень большим |
| Хорош для | CPU work, bounded pools | blocking I/O, request-per-thread |
| Нужно pooling | часто да | обычно нет |
| Ускоряет CPU | нет | нет |
| ThreadLocal | есть | есть, но массовое состояние дорого |

## Минимальный пример Java 21

```java
try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
    Future<String> future = executor.submit(() -> {
        Thread.sleep(100);
        return "done by " + Thread.currentThread();
    });

    System.out.println(future.get());
}
```

Каждая submitted task получает новую virtual thread.

## Главная польза

Традиционный server design часто ограничивал concurrency размером platform-thread pool, потому что каждый blocking request удерживал worker.

С virtual threads можно снова писать прямолинейный blocking code:

```java
Customer customer = customerClient.load(id);
List<Offer> offers = offerClient.load(customer);
return buildResponse(customer, offers);
```

без обязательного перехода к callback/reactive style только ради экономии threads.

## Они не делают downstream бесконечным

100 000 virtual threads не создают 100 000 database connections.

Ограничивать нужно дефицитный ресурс:

```java
Semaphore databasePermits = new Semaphore(50);

Response executeQuery() throws InterruptedException {
    databasePermits.acquire();
    try {
        return repository.query();
    } finally {
        databasePermits.release();
    }
}
```

На практике основной limiter часто уже существует как connection pool.

## CPU-bound workload

```java
for (int i = 0; i < 1_000_000; i++) {
    executor.submit(this::calculateHash);
}
```

Virtual threads не создают дополнительные processor cores. Для CPU-bound work excessive concurrency добавляет scheduling overhead.

## Не pool virtual threads

Идея virtual threads — создавать thread на task. Ограничение concurrency через маленький pool virtual threads возвращает старую модель и обычно скрывает настоящий scarce resource.

## ThreadLocal cost

Каждая virtual thread может иметь ThreadLocal values. При очень большом количестве tasks тяжёлый per-thread context становится существенным по памяти.

Правила:

- хранить минимальный контекст;
- всегда очищать lifecycle-sensitive values;
- не использовать ThreadLocal как dependency container;
- отдельно оценивать framework context propagation.

## Pinning в Java 21

В Java 21 virtual thread может удерживать carrier во время некоторых blocking operations, если находится внутри `synchronized` section или выполняет определённые native/foreign calls.

Проблемный shape:

```java
synchronized (lock) {
    blockingRemoteCall();
}
```

Практическая рекомендация для Java 21:

- не держать monitor во время долгого blocking I/O;
- сокращать critical sections;
- профилировать реальные contention/pinning scenarios;
- не заменять `synchronized` механически без доказанной проблемы.

## Virtual threads vs CompletableFuture

Это не прямые конкуренты.

- Virtual threads упрощают масштабируемый blocking style.
- CompletableFuture выражает dependency graph, parallel branches и non-blocking composition.

Можно использовать virtual threads внутри executor и при этом строить orchestration на futures, но сложность должна быть оправдана.

## Interview Answer

> Virtual threads в Java 21 снижают стоимость большого количества blocking tasks и делают thread-per-request практичным. Они не ускоряют CPU-bound work и не увеличивают capacity downstream systems. Их обычно не pool-ят; вместо этого ограничивают scarce resource. Для Java 21 также важно избегать долгого blocking внутри synchronized regions и контролировать ThreadLocal footprint.

## Exam Trap

> [!question] Будет ли миллион virtual threads вычислять CPU-задачу в миллион раз быстрее?

> [!answer]- Ответ
> Нет. Реальное выполнение ограничено carrier threads и количеством CPU cores. Virtual threads выигрывают главным образом на ожидании blocking I/O.

## Memory Hook

> **Virtual threads make waiting cheap, not computing free.**

## Sources

- [[98_SOURCES/Java Concurrency Sources|Primary Java Concurrency Sources]]
