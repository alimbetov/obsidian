---
type: atomic-concept
domain: java
route: JAVA-B04
concept_id: JAVA-B04-E01
status: published
difficulty: beginner
java_versions: [17, 21]
next_note: Java Checked Unchecked Exceptions and Errors
tags: [java, exceptions, flow, beginner]
---

# Java Why Programs Fail and Exception Flow

> [!summary]
> Метод имеет обычный путь с результатом и exceptional path, который начинается, когда method contract нельзя выполнить.

## Зачем это нужно

Программа читает файл. Файл может существовать, отсутствовать или быть недоступным. Нельзя притворяться, что все варианты являются одинаковым успешным результатом.

## Обычный путь

```java
static int divide(int a, int b) {
    return a / b;
}
```

Для `divide(10, 2)`:

```text
enter method
→ calculate 10 / 2
→ return 5
→ caller continues
```

## Exceptional path

Для `divide(10, 0)` вычисление невозможно для integer arithmetic.

```text
enter method
→ attempt 10 / 0
→ ArithmeticException created/thrown by runtime
→ method does not return normally
→ search for handler
```

## Пошаговый пример

```java
static void level2() {
    int value = 10 / 0;
    System.out.println(value);
}

static void level1() {
    level2();
    System.out.println("after level2");
}

public static void main(String[] args) {
    try {
        level1();
    } catch (ArithmeticException error) {
        System.out.println("cannot divide");
    }
}
```

| Шаг | Активный метод | Что происходит |
|---:|---|---|
| 1 | `main` | входит в try |
| 2 | `level1` | вызывает `level2` |
| 3 | `level2` | возникает ArithmeticException |
| 4 | `level2` | frame завершается без normal return |
| 5 | `level1` | frame завершается; строка after не выполняется |
| 6 | `main` | найден совместимый catch |
| 7 | `main` | печатается `cannot divide` |

Этот процесс называют **stack unwinding**.

## Stack trace

Не читай stack trace как случайный текст.

```text
Exception type and message
at method where failure appeared
at caller
at caller of caller
```

Первый application frame часто показывает место проявления, но root cause может находиться глубже или быть сохранён в `Caused by`.

## Правдоподобная ошибка

> После exception Java вернётся к следующей строке того же метода.

Нет. Если exception не обработан внутри этого метода, его normal path прекращается.

## Exception object

Exception хранит диагностическую информацию:

- runtime type;
- message;
- stack trace;
- optional cause;
- optional suppressed exceptions.

## Самостоятельный прогноз

```java
static void run() {
    System.out.print("A");
    throw new IllegalStateException("stop");
    // System.out.print("B");
}

public static void main(String[] args) {
    try {
        run();
    } catch (RuntimeException error) {
        System.out.print("C");
    }
    System.out.print("D");
}
```

> [!answer]- Ответ
> Напечатается `ACD`. `B` недостижима после unconditional throw, `catch` обрабатывает exception, затем выполнение продолжается после try/catch.

## Продвинутый слой

- Handler выбирается по runtime type thrown object.
- Java не возвращается в место throw после обработки выше по stack.
- Stack trace показывает call chain, но asynchronous boundaries могут разрывать привычную картину.
- Не ловите `Throwable` как универсальный способ «не падать».

## Navigation

- [[10_CONCEPTS/Java/Exceptions/Java Exceptions and Resource Safety]]
- **Next:** [[10_CONCEPTS/Java/Exceptions/Java Checked Unchecked Exceptions and Errors]]
- [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Cards]]
- [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Drills]]
- [[50_LABS/Java/JAVA-B04/README]]
