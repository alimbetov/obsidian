---
type: atomic-concept
domain: java
route: JAVA-B04
concept_id: JAVA-B04-E04
status: published
difficulty: beginner-to-intermediate
java_versions: [17, 21]
previous_note: Java Try Catch Finally
next_note: Java Multi-catch and Precise Rethrow
tags: [java, exceptions, throw, throws, custom-exception]
---

# Java `throw`, `throws` and Custom Exceptions

> [!summary]
> `throw` запускает exceptional path конкретным object. `throws` объявляет, какие checked exceptions method может передать caller. Custom exception делает failure понятной частью domain contract.

## `throw`

```java
if (age < 0) {
    throw new IllegalArgumentException("age must be non-negative");
}
```

`throw` принимает expression типа `Throwable`.

После unconditional throw normal execution этого блока не продолжается.

## `throws`

```java
static String load(Path path) throws IOException {
    return Files.readString(path);
}
```

Declaration не создаёт exception и не гарантирует его возникновение. Оно сообщает compiler/caller о возможном checked failure.

## Сравнение

| Конструкция | Где используется | Что делает |
|---|---|---|
| `throw` | внутри body | выбрасывает object |
| `throws` | в signature | объявляет checked failure contract |

## Custom checked exception

```java
class InvalidReportException extends Exception {
    InvalidReportException(String message) {
        super(message);
    }
}
}
```

Caller обязан catch или declare.

## Custom unchecked exception

```java
class AccountInvariantException extends RuntimeException {
    AccountInvariantException(String message) {
        super(message);
    }
}
}
```

Compiler не требует declaration.

## Сохраняй cause

```java
try {
    parser.parse(file);
} catch (IOException error) {
    throw new ReportImportException(
            "Cannot import report: " + file,
            error
    );
}
```

Без cause теряется исходный stack trace и техническая причина.

## Полезное сообщение

Плохо:

```java
throw new RuntimeException("error");
```

Лучше:

```java
throw new OrderNotFoundException(
        "Order " + orderId + " was not found for tenant " + tenantId
);
```

Не помещай в message пароль, token, полный personal data или secret.

## Constructor patterns

Полезный minimum:

```java
class ImportException extends RuntimeException {
    ImportException(String message) {
        super(message);
    }

    ImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

## Override rule

Overriding method не может объявить более широкий checked exception, чем parent contract.

```java
class Parent {
    void run() throws IOException {}
}

class Child extends Parent {
    @Override
    void run() throws FileNotFoundException {}
}
```

Narrower checked exception разрешён.

## Правдоподобная ошибка

> Если method объявил `throws IOException`, он обязан всегда выбрасывать IOException.

Нет. Declaration описывает возможность, а не обязательный outcome.

## Самопроверка

```java
static void check(int score) {
    if (score < 0) {
        throw new IllegalArgumentException("negative");
    }
}
```

Нужно ли писать `throws IllegalArgumentException`?

> [!answer]- Ответ
> Нет. Это unchecked exception. Declaration возможно, но compiler его не требует и оно редко добавляет ценность.

## Продвинутый слой

- Exception type должен соответствовать abstraction layer.
- Infrastructure exception можно перевести в domain/application exception, сохранив cause.
- Не создавай отдельный type для каждого message без различимой recovery semantics.
- Public checked exceptions увеличивают compile-time coupling caller-а к API.

## Navigation

- **Previous:** [[10_CONCEPTS/Java/Exceptions/Java Try Catch Finally]]
- **Next:** [[10_CONCEPTS/Java/Exceptions/Java Multi-catch and Precise Rethrow]]
- [[40_PRODUCTION_CASES/Java/Java Exceptions Production Cases]]
- [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Cards]]
