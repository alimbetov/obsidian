---
type: certification-drill-bank
domain: java
route: JAVA-B04
status: published
drill_count: 20
java_versions: [17, 21]
objectives: [JAVA-B04, JAVA21-4.1]
tags: [java, exceptions, compile-output, beginner]
---

# JAVA-B04 — Compile and Output Drills

> [!instruction]
> Для каждой задачи сначала запиши: `compile / no compile`, затем output или первый uncaught exception, затем механизм. Не запускай код до прогноза.

## Navigation

- [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Roadmap]]
- [[10_CONCEPTS/Java/Exceptions/Java Exceptions and Resource Safety]]
- [[30_CERTIFICATIONS/Java/JAVA-B04/JAVA-B04 Cards]]
- [[50_LABS/Java/JAVA-B04/README]]

## JAVA-B04-D001 — Handled arithmetic failure

```java
try {
    System.out.print("A");
    System.out.print(10 / 0);
} catch (ArithmeticException error) {
    System.out.print("B");
}
System.out.print("C");
```

> [!answer]- Answer
> Compiles and prints `ABC`.

### Mechanism

Division throws before the second print completes; catch handles it, then execution continues after try/catch.

### Beginner Hint

Отмечай последнюю полностью выполненную operation.

---

## JAVA-B04-D002 — Unmatched catch

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

> [!answer]- Answer
> Compiles, prints `AC`, then throws `IllegalStateException`. `D` is not printed.

### Mechanism

Catch type is incompatible; finally runs before propagation.

---

## JAVA-B04-D003 — Catch order

```java
try {
    throw new java.io.FileNotFoundException();
} catch (java.io.IOException error) {
} catch (java.io.FileNotFoundException error) {
}
```

> [!answer]- Answer
> Does not compile.

### Mechanism

The broader IOException catch makes the subtype catch unreachable.

---

## JAVA-B04-D004 — Checked exception declaration

```java
static void load() {
    throw new java.io.IOException();
}
```

> [!answer]- Answer
> Does not compile.

### Mechanism

IOException must be caught or declared.

---

## JAVA-B04-D005 — Unchecked exception declaration

```java
static void validate() {
    throw new IllegalArgumentException("bad");
}
```

> [!answer]- Answer
> Compiles.

### Mechanism

IllegalArgumentException is unchecked.

---

## JAVA-B04-D006 — Finally after return

```java
static int value() {
    try {
        return 1;
    } finally {
        System.out.print("F");
    }
}
System.out.print(value());
```

> [!answer]- Answer
> Compiles and prints `F1`.

### Mechanism

Return value is prepared, finally runs, then the value is returned.

---

## JAVA-B04-D007 — Return in finally

```java
static int value() {
    try {
        return 1;
    } finally {
        return 2;
    }
}
System.out.print(value());
```

> [!answer]- Answer
> Compiles and prints `2`.

### Mechanism

Finally return replaces the pending return.

---

## JAVA-B04-D008 — Finally throws

```java
static void run() {
    try {
        throw new IllegalArgumentException("body");
    } finally {
        throw new IllegalStateException("finally");
    }
}
```

> [!answer]- Answer
> Compiles; `IllegalStateException("finally")` propagates.

### Mechanism

A new exception from finally replaces the earlier pending exception in ordinary try/finally.

---

## JAVA-B04-D009 — Multi-catch overlap

```java
try {
    throw new java.io.FileNotFoundException();
} catch (java.io.IOException | java.io.FileNotFoundException error) {
}
```

> [!answer]- Answer
> Does not compile.

### Mechanism

Multi-catch alternatives cannot have a subtype relationship.

---

## JAVA-B04-D010 — Multi-catch reassignment

```java
try {
    throw new java.io.IOException();
} catch (java.io.IOException | java.sql.SQLException error) {
    error = new java.io.IOException();
}
```

> [!answer]- Answer
> Does not compile.

### Mechanism

Multi-catch parameter cannot be reassigned.

---

## JAVA-B04-D011 — Custom cause

```java
RuntimeException source = new RuntimeException("source");
RuntimeException wrapper = new RuntimeException("wrapper", source);
System.out.print(wrapper.getCause().getMessage());
```

> [!answer]- Answer
> Compiles and prints `source`.

### Mechanism

The original exception is preserved as cause.

---

## JAVA-B04-D012 — Override checked exception

```java
class Parent {
    void run() throws java.io.IOException {}
}
class Child extends Parent {
    @Override
    void run() throws Exception {}
}
```

> [!answer]- Answer
> Does not compile.

### Mechanism

Override cannot broaden the checked exception contract.

---

## JAVA-B04-D013 — Narrow checked exception

```java
class Parent {
    void run() throws java.io.IOException {}
}
class Child extends Parent {
    @Override
    void run() throws java.io.FileNotFoundException {}
}
```

> [!answer]- Answer
> Compiles.

### Mechanism

A narrower checked exception is compatible.

---

## JAVA-B04-D014 — Reverse close order

```java
class R implements AutoCloseable {
    String n;
    R(String n) { this.n = n; System.out.print("+" + n); }
    public void close() { System.out.print("-" + n); }
}
try (R a = new R("A"); R b = new R("B")) {
    System.out.print("X");
}
```

> [!answer]- Answer
> Compiles and prints `+A+BX-B-A`.

### Mechanism

Resources close in reverse creation order.

---

## JAVA-B04-D015 — Body and close both fail

```java
class R implements AutoCloseable {
    public void close() { throw new IllegalStateException("close"); }
}
try (R r = new R()) {
    throw new IllegalArgumentException("body");
} catch (Exception error) {
    System.out.print(error.getMessage() + ":" + error.getSuppressed()[0].getMessage());
}
```

> [!answer]- Answer
> Compiles and prints `body:close`.

### Mechanism

Body exception is primary; close exception is suppressed.

---

## JAVA-B04-D016 — Only close fails

```java
class R implements AutoCloseable {
    public void close() { throw new IllegalStateException("close"); }
}
try (R r = new R()) {
    System.out.print("body");
}
```

> [!answer]- Answer
> Compiles, prints `body`, then throws `IllegalStateException("close")`.

### Mechanism

No body exception exists, so close failure propagates normally.

---

## JAVA-B04-D017 — Initializer failure closes earlier resource

```java
class R implements AutoCloseable {
    String n;
    R(String n) { this.n = n; System.out.print("+" + n); }
    public void close() { System.out.print("-" + n); }
}
static R fail() { throw new IllegalStateException("open"); }
try (R a = new R("A"); R b = fail()) {
    System.out.print("X");
}
```

> [!answer]- Answer
> Compiles, prints `+A-A`, then throws `IllegalStateException("open")`.

### Mechanism

The body never starts; already-created resource `a` is closed.

---

## JAVA-B04-D018 — Existing effectively-final resource

```java
var reader = new java.io.StringReader("A");
try (reader) {
    System.out.print((char) reader.read());
}
```

> [!answer]- Answer
> Compiles on Java 17 and 21 and prints `A`.

### Mechanism

Java 9+ allows a final/effectively-final existing variable in resource specification.

---

## JAVA-B04-D019 — Reassigned existing resource

```java
var reader = new java.io.StringReader("A");
reader = new java.io.StringReader("B");
try (reader) {
    System.out.print(reader.read());
}
```

> [!answer]- Answer
> Does not compile.

### Mechanism

`reader` is not effectively final.

---

## JAVA-B04-D020 — Catch then rethrow

```java
try {
    throw new IllegalArgumentException("A");
} catch (RuntimeException error) {
    System.out.print("B");
    throw error;
} finally {
    System.out.print("C");
}
```

> [!answer]- Answer
> Compiles, prints `BC`, then propagates the original `IllegalArgumentException("A")`.

### Mechanism

Catch executes and rethrows; finally still runs before propagation.
