---
type: atomic-concept
domain: java
route: JAVA-B04
concept_id: JAVA-B04-E02
status: published
difficulty: beginner-to-intermediate
java_versions: [17, 21]
previous_note: Java Why Programs Fail and Exception Flow
next_note: Java Try Catch Finally
tags: [java, exceptions, checked, unchecked]
---

# Java Checked, Unchecked Exceptions and Errors

> [!summary]
> Checked и unchecked — это прежде всего разные compiler contracts. Checked exception нужно обработать или объявить; RuntimeException и Error compiler не заставляет перечислять.

## Иерархия

```text
Throwable
├── Error
└── Exception
    ├── RuntimeException
    └── другие Exception = checked
```

## Checked exception

```java
static String readFirstLine(Path path) throws IOException {
    return Files.readAllLines(path).get(0);
}
```

`IOException` checked. Caller должен:

```java
try {
    String line = readFirstLine(path);
} catch (IOException error) {
    // handle
}
```

или объявить:

```java
static void run() throws IOException {
    readFirstLine(path);
}
```

## Unchecked exception

```java
int result = 10 / 0;               // ArithmeticException
String text = null;
text.length();                      // NullPointerException
```

Это subclasses `RuntimeException`. Compiler разрешает не писать catch/throws.

## Error

`Error` обычно сообщает о серьёзной проблеме JVM/runtime environment:

```text
OutOfMemoryError
StackOverflowError
NoClassDefFoundError
```

Application обычно не должна использовать Error как обычный business signal.

## Не путай серьёзность и compiler rule

Checked exception может описывать ожидаемую внешнюю проблему: файл не найден. Unchecked exception может привести к серьёзному production incident.

```text
checked/unchecked ≠ harmless/serious
```

## Пошаговый compile example

```java
static void load() {
    Files.readString(Path.of("data.txt"));
}
```

Не компилируется, потому что `Files.readString` объявляет `IOException`.

Исправление:

```java
static void load() throws IOException {
    Files.readString(Path.of("data.txt"));
}
```

или catch.

## Custom hierarchy design

```java
class OrderImportException extends RuntimeException {
    OrderImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

Unchecked custom exception уместно, когда caller обычно не может локально восстановиться и ошибка должна пересечь несколько application layers. Но это design choice, а не универсальное правило.

## Правдоподобная ошибка

> RuntimeException нельзя ловить.

Можно. Отличие в том, что compiler не требует catch/throws.

## Самопроверка

Какие строки компилируются без дополнительных catch/throws?

```java
throw new IllegalArgumentException();
throw new IOException();
throw new AssertionError();
```

> [!answer]- Ответ
> `IllegalArgumentException` и `AssertionError` компилируются без declaration. `IOException` требует catch или `throws`.

## Продвинутый слой

- Catch `Exception` ловит RuntimeException и checked exceptions, но не Error.
- Catch `Throwable` ловит и Error, что редко является правильной policy.
- Public API exception hierarchy является частью контракта.
- Перевод exception между layers должен сохранять cause.

## Navigation

- **Previous:** [[10_CONCEPTS/Java/Exceptions/Java Why Programs Fail and Exception Flow]]
- **Next:** [[10_CONCEPTS/Java/Exceptions/Java Try Catch Finally]]
- [[10_CONCEPTS/Java/Exceptions/Java Exceptions and Resource Safety]]
- [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Roadmap]]
