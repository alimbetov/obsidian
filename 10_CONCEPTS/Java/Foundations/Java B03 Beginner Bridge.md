---
type: beginner-bridge
domain: java
route: JAVA-B03
status: published
difficulty: beginner
java_versions: [17, 21]
tags: [java, beginner, objects, classes, inheritance]
---

# Java B03 Beginner Bridge

> [!summary]
> B03 отвечает на вопрос: **как объединить данные и действия в понятную модель программы**.

## 1. Класс, объект и ссылка

```java
class Student {
    String name;
    int score;
}

Student student = new Student();
```

```text
Student          class/type
new Student()    создание объекта
student          reference variable
```

Не говори «переменная содержит весь объект». Ссылочная переменная хранит reference value, через которое программа обращается к объекту.

## 2. Конструктор создаёт корректное начальное состояние

```java
class Student {
    private final String name;

    Student(String name) {
        this.name = name;
    }
}
```

Простая цель конструктора: после завершения объект должен быть пригоден для использования.

## 3. Encapsulation

```java
class BankAccount {
    private int balance;

    void deposit(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        balance += amount;
    }
}
```

`private` не является целью само по себе. Оно помогает заставить изменения проходить через правила объекта.

## 4. Overload и override

### Overload

Одинаковое имя, разные параметры. Выбор делает compiler.

```java
void print(String text) {}
void print(int number) {}
```

### Override

Subclass заменяет inherited instance-method behavior. Выбор реализации происходит во время выполнения.

```java
class Animal {
    String sound() { return "?"; }
}
class Dog extends Animal {
    @Override String sound() { return "woof"; }
}
```

```java
Animal animal = new Dog();
System.out.println(animal.sound()); // woof
```

## 5. Наследование — не копирование класса

Неправильная модель:

> Child получает копию всех полей и методов parent.

Точнее:

> Subclass создаёт новый тип, связанный контрактом наследования. Объект subclass содержит parent-state и подчиняется правилам initialization и dynamic dispatch.

Private members нельзя напрямую использовать из subclass, хотя parent-part объекта может их хранить.

## 6. Interface

Interface задаёт роль или capability.

```java
interface Printable {
    void print();
}
```

Разные классы могут реализовать один контракт, не становясь разновидностями одного implementation class.

## 7. Record

Record удобен для прозрачного набора значений.

```java
record Point(int x, int y) {}
```

Compiler создаёт accessors, constructor, `equals`, `hashCode` и `toString`. Record не означает, что весь object graph глубоко immutable.

## 8. Enum

Enum описывает закрытый набор именованных значений.

```java
enum Status {
    NEW, ACTIVE, CLOSED
}
```

Используй enum, когда допустимые варианты заранее ограничены.

## 9. Sealed hierarchy

```java
sealed interface Shape permits Circle, Rectangle {}
record Circle(double radius) implements Shape {}
record Rectangle(double width, double height) implements Shape {}
```

Sealed type контролирует список прямых наследников. Это помогает compiler проверять exhaustiveness.

## Пошаговая трассировка dispatch

```java
class Parent {
    String name = "P";
    String value() { return "parent"; }
}
class Child extends Parent {
    String name = "C";
    @Override String value() { return "child"; }
}

Parent ref = new Child();
System.out.println(ref.name + ":" + ref.value());
```

| Выражение | Как выбирается | Результат |
|---|---|---|
| `ref.name` | compile-time type `Parent` | `P` |
| `ref.value()` | runtime object `Child` | `child` |

## Практика

### Задача 1

```java
StringBuilder a = new StringBuilder("A");
StringBuilder b = a;
b.append("B");
System.out.println(a);
```

> [!answer]- Ответ
> `AB`: две ссылки указывают на один mutable object.

### Задача 2

Почему опасно вызывать overridable method из constructor?

> [!answer]- Ответ
> Runtime dispatch может вызвать subclass method до завершения initialization subclass fields.

### Задача 3

Когда record лучше обычного class?

> [!answer]- Ответ
> Когда основной смысл типа — прозрачный фиксированный набор значений без сложной identity/lifecycle semantics.

## Дальше

1. [[10_CONCEPTS/Java/Object Model/Java Object Creation Reachability and Lifecycle]]
2. [[10_CONCEPTS/Java/Object Model/Java Fields Initializers and Constructor Order]]
3. [[10_CONCEPTS/Java/Object Model/Java Overloading Varargs and Method Selection]]
4. [[10_CONCEPTS/Java/Object Model/Java Inheritance Overriding Hiding and Polymorphism]]
5. [[10_CONCEPTS/Java/Object Model/Java Records]]
6. [[10_CONCEPTS/Java/Object Model/Java Sealed Types]]
7. [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]

## Navigation

- [[00_HOME/Java Beginner Learning Path]]
- [[10_CONCEPTS/Java/Foundations/Java B02 Beginner Bridge]]
- [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Roadmap]]
