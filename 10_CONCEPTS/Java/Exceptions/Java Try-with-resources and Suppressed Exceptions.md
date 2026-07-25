---
type: atomic-concept
domain: java
route: JAVA-B04
concept_id: JAVA-B04-E06
status: published
difficulty: intermediate
java_versions: [17, 21]
previous_note: Java Multi-catch and Precise Rethrow
tags: [java, exceptions, try-with-resources, autocloseable, suppressed]
---

# Java Try-with-resources and Suppressed Exceptions

> [!summary]
> Try-with-resources закрывает `AutoCloseable` resources автоматически. Resources закрываются в обратном порядке создания. Если body и close одновременно выбрасывают exceptions, body exception остаётся primary, а close failures становятся suppressed.

## Зачем это нужно

Файлы, sockets и database connections ограничены. Garbage Collector управляет Java objects, но не является надёжным механизмом своевременного закрытия внешнего ресурса.

## Минимальный пример

```java
try (BufferedReader reader = Files.newBufferedReader(path)) {
    System.out.println(reader.readLine());
}
```

Compiler создаёт cleanup logic, который вызывает `reader.close()`.

## Требование

Resource type должен реализовывать `AutoCloseable`.

```java
class LessonResource implements AutoCloseable {
    @Override
    public void close() {
        System.out.println("closed");
    }
}
```

## Close order

```java
try (
    Resource first = new Resource("first");
    Resource second = new Resource("second")
) {
    System.out.println("body");
}
```

Порядок:

```text
create first
create second
body
close second
close first
```

Это stack/LIFO order.

## Почему обратный порядок логичен

Поздний resource может зависеть от раннего:

```java
InputStream input = ...;
BufferedInputStream buffered = new BufferedInputStream(input);
```

Сначала закрывается wrapper, затем underlying resource.

## Java 9+ effectively-final resource

```java
BufferedReader reader = Files.newBufferedReader(path);
try (reader) {
    System.out.println(reader.readLine());
}
```

Variable должна быть final или effectively final.

## Primary и suppressed exceptions

```java
class BrokenResource implements AutoCloseable {
    @Override
    public void close() {
        throw new IllegalStateException("close failed");
    }
}

try (BrokenResource resource = new BrokenResource()) {
    throw new IllegalArgumentException("body failed");
}
```

Результат:

```text
primary:    IllegalArgumentException("body failed")
suppressed: IllegalStateException("close failed")
```

Проверка:

```java
catch (Exception error) {
    System.out.println(error.getMessage());
    for (Throwable suppressed : error.getSuppressed()) {
        System.out.println(suppressed.getMessage());
    }
}
```

## Если body успешен, а close падает

Тогда close exception становится обычным propagated exception, потому что primary failure из body нет.

## Resource initializer failure

```java
try (
    Resource first = openFirst();
    Resource second = openSecond()
) {
    use();
}
```

Если `openSecond()` выбросит exception, уже созданный `first` будет закрыт. Body не начнётся.

## Правдоподобная ошибка

> Try-with-resources закрывает resources в порядке объявления.

Нет. Закрытие идёт в обратном порядке.

## Самостоятельный прогноз

```java
class R implements AutoCloseable {
    private final String name;
    R(String name) { this.name = name; System.out.print("+" + name); }
    public void close() { System.out.print("-" + name); }
}

try (R a = new R("A"); R b = new R("B")) {
    System.out.print("X");
}
```

> [!answer]- Ответ
> `+A+BX-B-A`.

## Production layer

- Не проглатывай close exception без диагностики.
- Connection pool возвращает connection через `close`; это не обязательно физическое закрытие socket.
- Не сохраняй resource reference и не используй после close.
- Слишком широкий scope удерживает resource дольше необходимого.
- Для asynchronous/reactive API обычный try-with-resources может закрыть resource до фактического завершения asynchronous work; lifecycle должен соответствовать execution model.

## Navigation

- **Previous:** [[10_CONCEPTS/Java/Exceptions/Java Multi-catch and Precise Rethrow]]
- [[10_CONCEPTS/Java/Exceptions/Java Exceptions and Resource Safety]]
- [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Roadmap]]
- [[40_PRODUCTION_CASES/Java/Java Exceptions Production Cases]]
- [[50_LABS/Java/JAVA-B04/README]]
