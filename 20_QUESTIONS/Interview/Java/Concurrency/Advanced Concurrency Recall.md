---
type: question-set
domain: java
subdomain: concurrency
difficulty: advanced
status: learning
confidence: 0
interview: true
java_versions: [8, 11, 17, 21]
tags: [java, concurrency, interview, recall]
---

# Advanced Concurrency Recall

> [!summary]
> Сначала ответь вслух. Затем раскрой answer и сравни не только итог, но и механизм рассуждения.

## 1. How does compareAndSet work?

> [!question]
> Как работает `compareAndSet`, и почему вокруг него часто нужен retry loop?

> [!answer]- Answer
> CAS атомарно записывает update только если текущее значение равно expected. Между первоначальным read и CAS другой поток мог изменить state, поэтому CAS возвращает false, а алгоритм перечитывает состояние и повторяет вычисление.

### Follow-up

- Lock-free означает wait-free?
- Может ли CAS callback иметь side effect?
- Как contention влияет на retry cost?

---

## 2. AtomicInteger or synchronized?

> [!question]
> Когда выбрать `AtomicInteger`, а когда `synchronized`?

> [!answer]- Answer
> AtomicInteger подходит для независимой операции над одной numeric variable. `synchronized` обычно проще, когда invariant охватывает несколько полей, объектов или последовательность шагов.

> [!warning] Trap
> Несколько AtomicInteger не образуют общей транзакции.

---

## 3. LongAdder or AtomicLong?

> [!question]
> Почему `LongAdder` масштабируется под contention и когда его нельзя использовать?

> [!answer]- Answer
> Он распределяет updates по нескольким cells и агрегирует их при `sum()`. Это хорошо для statistics, но sum не является линейризуемой coordination operation; для sequence и CAS нужен AtomicLong.

---

## 4. What is ABA?

> [!question]
> Что такое ABA problem?

> [!answer]- Answer
> State меняется `A → B → A`. CAS видит ожидаемое A и не замечает промежуточное изменение. Если history важна, используют version/stamp или другой algorithm.

---

## 5. Four deadlock conditions

> [!question]
> Какие четыре условия необходимы для классического deadlock?

> [!answer]- Answer
> Mutual exclusion, hold and wait, no preemption, circular wait.

### Senior extension

Назови, какое условие разрушает каждое решение:

- global lock ordering;
- tryLock timeout;
- один coarse-grained lock;
- message passing.

---

## 6. How does lock ordering help?

> [!question]
> Почему единый lock ordering предотвращает deadlock?

> [!answer]- Answer
> Все потоки захватывают ресурсы в одном направлении, поэтому circular wait не может сформироваться. Order key должен быть стабильным и использоваться во всех code paths.

---

## 7. Deadlock, livelock, starvation

> [!question]
> Чем отличаются deadlock, livelock и starvation?

> [!answer]- Answer
> Deadlock: движения нет из-за цикла ожидания. Livelock: потоки активны, но полезного прогресса нет. Starvation: система работает, но конкретный участник постоянно не получает ресурс.

---

## 8. ConcurrentHashMap compound action

> [!question]
> Почему `containsKey` затем `put` остаётся race condition на ConcurrentHashMap?

> [!answer]- Answer
> Individual calls thread-safe, но последовательность не атомарна. Между check и act может вмешаться другой поток. Используй `computeIfAbsent`, `putIfAbsent`, `compute` или `merge`.

---

## 9. CopyOnWriteArrayList workload

> [!question]
> Когда CopyOnWriteArrayList — правильный выбор?

> [!answer]- Answer
> Для небольших read-mostly lists с редкими writes и snapshot iteration. Частые mutations дороги, потому что копируется весь внутренний массив.

---

## 10. BlockingQueue backpressure

> [!question]
> Как bounded BlockingQueue создаёт backpressure?

> [!answer]- Answer
> При заполнении producer блокируется, получает false или timeout. Разрыв скоростей становится управляемым решением, а не бесконечным backlog в памяти.

---

## 11. Production scenario: slow consumer

> [!question]
> Producer создаёт 20 000 jobs/s, consumer обрабатывает 5 000 jobs/s. Что произойдёт с unbounded queue?

> [!answer]- Answer
> Backlog растёт примерно на 15 000 jobs/s, увеличивая память и latency до отказа. Нужны bounded capacity, admission/rejection policy, масштабирование consumer или снижение входного rate.

---

## 12. Production scenario: thread dump

> [!question]
> В thread dump два потока `BLOCKED`, каждый ждёт monitor, которым владеет другой. Каков следующий шаг?

> [!answer]- Answer
> Построить wait-for cycle, найти все code paths захвата этих locks и ввести единый ordering или redesign. Restart только временно устраняет симптом.

## Answer quality rubric

Хороший ответ содержит:

1. механизм;
2. гарантию;
3. counterexample;
4. подходящий alternative;
5. production diagnostic.

## Related concepts

- [[10_CONCEPTS/Java/Concurrency/Atomic CAS and Counters]]
- [[10_CONCEPTS/Java/Concurrency/Deadlock Livelock and Lock Ordering]]
- [[10_CONCEPTS/Java/Concurrency/Concurrent Collections and Backpressure]]
