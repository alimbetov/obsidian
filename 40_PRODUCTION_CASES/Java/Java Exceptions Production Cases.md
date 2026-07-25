---
type: production-case-set
domain: java
route: JAVA-B04
status: published
case_count: 8
java_versions: [17, 21]
tags: [java, exceptions, production, diagnostics]
---

# Java Exceptions Production Cases

> [!summary]
> Восемь случаев показывают переход от симптома к exception path, причине и исправлению. Для начинающего сначала приведено простое объяснение, затем production-граница.

## Case 1 — причина потеряна при wrapping

### Симптом

Log содержит только:

```text
ImportException: import failed
```

Невозможно понять, был ли файл недоступен, повреждён или имел неверный формат.

### Плохой код

```java
try {
    parser.parse(path);
} catch (IOException error) {
    throw new ImportException("import failed");
}
```

### Причина

Исходный exception не передан как cause.

### Исправление

```java
throw new ImportException("Cannot import " + path, error);
```

### Урок

> Переводи abstraction, но сохраняй evidence.

---

## Case 2 — broad catch превратил failure в SUCCESS

### Симптом

API возвращает успешный ответ, хотя данные не сохранены.

```java
try {
    repository.save(command);
    return Result.success();
} catch (Exception error) {
    log.warn("ignored", error);
    return Result.success();
}
```

### Причина

Catch не восстановил корректное состояние и скрыл failure contract.

### Исправление

- вернуть failure result для ожидаемой validation problem;
- перевести infrastructure exception и передать выше;
- не ловить exception, если текущий layer не умеет восстановиться.

### Урок

Catch должен иметь понятную recovery policy.

---

## Case 3 — exception используется как обычный цикл

### Симптом

Обработка миллиона строк медленная, profiler показывает создание stack traces.

```java
for (String value : values) {
    try {
        numbers.add(Integer.parseInt(value));
    } catch (NumberFormatException error) {
        invalid++;
    }
}
```

### Причина

Ожидаемый частый вариант моделируется через expensive exceptional path.

### Исправление

Зависит от требований:

- предварительная дешёвая validation;
- parser, возвращающий result object;
- batch validation с отдельным списком ошибок.

### Урок

Exception подходит для невозможности выполнить contract, а не для каждого ожидаемого branch.

---

## Case 4 — resource закрывается слишком поздно

### Симптом

Connection pool исчерпан, хотя каждый method когда-нибудь вызывает close.

```java
Connection connection = dataSource.getConnection();
List<Row> rows = query(connection);
doLongCpuWork(rows);
connection.close();
```

### Причина

Scope ресурса шире реальной database operation.

### Исправление

```java
List<Row> rows;
try (Connection connection = dataSource.getConnection()) {
    rows = query(connection);
}
doLongCpuWork(rows);
```

### Урок

Закрывай resource в минимальном корректном scope.

---

## Case 5 — close exception скрыл body exception

### Симптом

Manual finally сообщает `close failed`, но исходная ошибка обработки потеряна.

### Плохой шаблон

```java
Resource resource = open();
try {
    process(resource);
} finally {
    resource.close();
}
```

Если `process` и `close` выбросят exceptions, finally failure может заменить body failure.

### Исправление

Использовать try-with-resources, который сохраняет body exception primary, а close failure — suppressed.

### Проверка

```java
for (Throwable suppressed : error.getSuppressed()) {
    log.debug("suppressed", suppressed);
}
```

---

## Case 6 — секрет попал в exception message

### Симптом

Access token оказался в logs и tracing system.

```java
throw new AuthenticationException("Invalid token: " + token);
```

### Причина

Exception message рассматривается как безопасное хранилище context, хотя оно распространяется по logs, metrics labels и error responses.

### Исправление

```java
throw new AuthenticationException(
        "Authentication failed for request " + requestId
);
```

Token хранить только там, где это строго необходимо и защищено.

### Урок

Diagnostic context не должен раскрывать secrets.

---

## Case 7 — async resource закрыт до выполнения worker

### Симптом

Worker получает `Stream closed`.

```java
try (InputStream input = Files.newInputStream(path)) {
    executor.submit(() -> parse(input));
}
```

### Timeline

```text
submit task
→ try scope ends
→ input closes
→ worker starts later
→ parse reads closed stream
```

### Исправления

- читать immutable data до submit;
- передавать path и открывать resource внутри worker;
- возвращать/await future внутри resource scope, если это соответствует design.

### Урок

Resource lifetime должен охватывать фактическое asynchronous использование.

---

## Case 8 — duplicate logging на каждом layer

### Симптом

Один failure создаёт пять одинаковых ERROR entries.

```text
repository logs and throws
service logs and throws
controller logs and throws
global handler logs and returns response
```

### Причина

Каждый layer считает logging обязательной частью catch/rethrow.

### Исправление

- нижний layer добавляет context через exception type/message/cause;
- boundary layer логирует один раз с request/correlation context;
- ожидаемые client errors могут логироваться на INFO/WARN без stack trace;
- metrics учитываются отдельно от duplicate logs.

### Урок

> Лови для recovery или translation; логируй там, где есть полный operational context.

## Navigation

- [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Roadmap]]
- [[10_CONCEPTS/Java/Exceptions/Java Exceptions and Resource Safety]]
- [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Drills]]
- [[50_LABS/Java/JAVA-B04/README]]
