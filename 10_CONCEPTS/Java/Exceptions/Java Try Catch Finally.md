---
type: atomic-concept
domain: java
route: JAVA-B04
concept_id: JAVA-B04-E03
status: published
difficulty: beginner-to-intermediate
java_versions: [17, 21]
previous_note: Java Checked Unchecked Exceptions and Errors
next_note: Java Throw Throws and Custom Exceptions
tags: [java, exceptions, try, catch, finally]
---

# Java Try, Catch and Finally

> [!summary]
> `try` содержит operation, которая может завершиться failure. `catch` обрабатывает совместимый exception. `finally` выполняет cleanup-путь после try/catch независимо от normal или exceptional completion, кроме особых завершений JVM.

## Минимальный пример

```java
try {
    int result = 10 / 0;
    System.out.println(result);
} catch (ArithmeticException error) {
    System.out.println("division failed");
} finally {
    System.out.println("finished");
}
```

Результат:

```text
division failed
finished
```

## Пошаговый выбор catch

```java
try {
    operation();
} catch (FileNotFoundException error) {
    handleMissingFile(error);
} catch (IOException error) {
    handleOtherIo(error);
}
```

Java проверяет catch сверху вниз. Поэтому subtype должен находиться раньше parent type.

Неверно:

```java
catch (IOException error) {}
catch (FileNotFoundException error) {} // unreachable
```

## Что происходит при success

```text
try completes normally
→ catch blocks skipped
→ finally runs
→ next statement runs
```

## Что происходит при handled exception

```text
try throws
→ matching catch runs
→ finally runs
→ next statement runs if catch completed normally
```

## Что происходит при unhandled exception

```text
try throws
→ no compatible catch
→ finally runs
→ exception continues to caller
```

## `finally` и return

```java
static int value() {
    try {
        return 1;
    } finally {
        System.out.print("F");
    }
}
```

Сначала вычисляется return value, затем выполняется `finally`, затем method возвращает `1`.

Опасный код:

```java
static int value() {
    try {
        return 1;
    } finally {
        return 2;
    }
}
```

Вернётся `2`. Return из finally подавляет предыдущий return или exception. Так писать почти всегда нельзя.

## Когда `finally` может не завершиться нормально

- JVM process принудительно завершён;
- `System.exit` завершает process;
- machine/process crash;
- finally сам зациклился;
- finally выбросил новый exception.

Поэтому формулировка «finally выполняется всегда» слишком грубая.

## Catch parameter

```java
catch (IOException error) {
    System.out.println(error.getMessage());
    throw new ImportException("Cannot load input", error);
}
```

Не теряй cause.

## Правдоподобная ошибка

> Catch выбирается по типу переменной, которой exception присвоили.

Handler выбирается по runtime type реально thrown object и совместимости catch parameter.

## Самостоятельный прогноз

```java
try {
    System.out.print("A");
    throw new IllegalStateException();
} catch (IllegalArgumentException error) {
    System.out.print("B");
} finally {
    System.out.print("C");
}
System.out.print("D");
```

> [!answer]- Ответ
> Печатается `AC`, затем IllegalStateException уходит caller. `D` не выполняется.

## Продвинутый слой

- Catch block должен либо восстановить корректное состояние, либо перевести/передать failure.
- Logging и повторный throw на каждом layer создают duplicate logs.
- Cleanup внешних ресурсов лучше выражать try-with-resources, а не ручным finally.
- Catch broad `Exception` на низком уровне скрывает реальные failure contracts.

## Navigation

- **Previous:** [[10_CONCEPTS/Java/Exceptions/Java Checked Unchecked Exceptions and Errors]]
- **Next:** [[10_CONCEPTS/Java/Exceptions/Java Throw Throws and Custom Exceptions]]
- [[10_CONCEPTS/Java/Exceptions/Java Exceptions and Resource Safety]]
- [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Drills]]
