---
type: atomic-concept
domain: java
route: JAVA-B04
concept_id: JAVA-B04-E05
status: published
difficulty: intermediate
java_versions: [17, 21]
previous_note: Java Throw Throws and Custom Exceptions
next_note: Java Try-with-resources and Suppressed Exceptions
tags: [java, exceptions, multi-catch, rethrow]
---

# Java Multi-catch and Precise Rethrow

> [!summary]
> Multi-catch объединяет unrelated exception types с одинаковой обработкой. Precise rethrow позволяет compiler сохранить более узкий набор возможных checked exceptions.

## Multi-catch

```java
try {
    operation();
} catch (IOException | SQLException error) {
    log(error);
    throw new ServiceException("operation failed", error);
}
```

Используй multi-catch, когда handling действительно одинаковый.

## Почему нельзя parent и child вместе

Неверно:

```java
catch (IOException | FileNotFoundException error) {}
```

`FileNotFoundException` уже является IOException. Alternatives должны быть disjoint по наследованию.

## Catch parameter effectively final

В multi-catch нельзя присвоить parameter новый exception:

```java
catch (IOException | SQLException error) {
    // error = new IOException(); // does not compile
}
```

Compiler использует точный union типов для анализа.

## Отдельные catch нужны при разном recovery

```java
try {
    load();
} catch (FileNotFoundException error) {
    createDefaultFile();
} catch (AccessDeniedException error) {
    requestPermission();
}
```

Объединение ухудшило бы смысл.

## Precise rethrow

```java
static void work() throws IOException, SQLException {
    try {
        risky();
    } catch (Exception error) {
        log(error);
        throw error;
    }
}
```

Если compiler видит, что try может выбросить только IOException и SQLException, а catch parameter не переназначается, declaration может остаться точным.

## Почему это полезно

Без precise analysis broad catch мог бы заставить method объявить `throws Exception`, ухудшая API contract.

## Правдоподобная ошибка

> Multi-catch означает, что один exception object одновременно имеет оба типа.

Нет. Один thrown object соответствует одному runtime class. Catch принимает любой из перечисленных совместимых типов.

## Самостоятельный прогноз

```java
try {
    throw new FileNotFoundException();
} catch (FileNotFoundException | SQLException error) {
    System.out.println(error.getClass().getSimpleName());
}
```

> [!answer]- Ответ
> Если surrounding method корректно объявляет/обрабатывает checked types, напечатается `FileNotFoundException`. Runtime object не превращается в union type.

## Design checkpoint

Перед multi-catch спроси:

1. Одинаковая ли recovery policy?
2. Одинаковый ли user-facing result?
3. Нужны ли разные metrics/log levels?
4. Не скрывает ли объединение важную причину?

## Продвинутый слой

- Catch ordering всё равно должен идти от specific к general.
- Multi-catch уменьшает duplication, но не должен уничтожать semantic distinction.
- `catch (Exception)` + rethrow допустим в ограниченных случаях, но broad catch требует обоснования.

## Navigation

- **Previous:** [[10_CONCEPTS/Java/Exceptions/Java Throw Throws and Custom Exceptions]]
- **Next:** [[10_CONCEPTS/Java/Exceptions/Java Try-with-resources and Suppressed Exceptions]]
- [[10_CONCEPTS/Java/Exceptions/Java Exceptions and Resource Safety]]
- [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Drills]]
