---
type: certification-drill-bank
domain: java
route: JAVA-B02
status: published
drill_count: 20
java_versions:
  - 17
  - 21
objectives:
  - JAVA-B02
  - JAVA21-2.1
tags:
  - java
  - compile-output
  - control-flow
  - switch
  - pattern-switch
---

# JAVA-B02 — Compile and Output Drills

> [!summary]
> These drills separate Java 17 shared flow semantics from final Java 21 pattern-switch semantics. Predict compile status before tracing output.

## Route navigation

- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]
- [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch]]
- [[50_LABS/Java/JAVA-B02/README]]

# Drill bank

## JAVA-B02-D001 — Dangling `else`

**Target:** Java 17 and Java 21

```java
int x = 0;
if (true)
    if (false)
        x = 1;
    else
        x = 2;
System.out.println(x);
```

> [!answer]- Answer
> Compiles and prints `2`.

### Reasoning

The `else` belongs to the nearest unmatched `if`, which is `if (false)`.

### Error Taxonomy

`dangling-else / exact-output`

---

## JAVA-B02-D002 — `continue` executes the `for` update

**Target:** Java 17 and Java 21

```java
for (int i = 0; i < 4; i++) {
    if (i == 1) continue;
    System.out.print(i);
}
```

> [!answer]- Answer
> Compiles and prints `023`.

### Reasoning

For `i == 1`, the body remainder is skipped, but `i++` still runs before the condition is checked again.

### Error Taxonomy

`loop-transfer / exact-output`

---

## JAVA-B02-D003 — Labeled break exits two loops

**Target:** Java 17 and Java 21

```java
int count = 0;
outer:
for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (i == 1 && j == 1) break outer;
        count++;
    }
}
System.out.println(count);
```

> [!answer]- Answer
> Compiles and prints `4`.

### Reasoning

The completed pairs are `(0,0)`, `(0,1)`, `(0,2)`, and `(1,0)`; the labeled break exits both loops.

### Error Taxonomy

`labeled-break / exact-output`

---

## JAVA-B02-D004 — Enhanced-for reassignment

**Target:** Java 17 and Java 21

```java
int[] values = {1, 2, 3};
for (int value : values) {
    value *= 10;
}
for (int value : values) {
    System.out.print(value);
}
```

> [!answer]- Answer
> Compiles and prints `123`.

### Reasoning

The enhanced-for variable receives a copy of each primitive element; reassigning that variable does not write back to the array.

### Error Taxonomy

`enhanced-for / aliasing`

---

## JAVA-B02-D005 — Flow-scoped `instanceof` pattern

**Target:** Java 17 and Java 21

```java
Object value = "java";
if (value instanceof String text && text.length() > 3) {
    System.out.println(text.toUpperCase());
}
```

> [!answer]- Answer
> Compiles and prints `JAVA`.

### Reasoning

The pattern variable is in scope in the right operand of `&&` and in the true branch because both are reached only after a successful match.

### Error Taxonomy

`instanceof-pattern / flow-scope`

---

## JAVA-B02-D006 — Constant-false `while`

**Target:** Java 17 and Java 21

```java
while (false) {
    System.out.println("never");
}
```

> [!answer]- Answer
> Does not compile: the loop body is an unreachable statement.

### Reasoning

Unlike `if (false)`, a constant-false while body is rejected by reachability analysis.

### Error Taxonomy

`compile-fail / unreachable-statement`

---

## JAVA-B02-D007 — Colon-style fall-through

**Target:** Java 17 and Java 21

```java
int n = 2;
switch (n) {
    case 1:
        System.out.print("A");
    case 2:
        System.out.print("B");
    default:
        System.out.print("C");
}
```

> [!answer]- Answer
> Compiles and prints `BC`.

### Reasoning

Selection starts at `case 2`; without `break`, execution falls through into `default`.

### Error Taxonomy

`switch-fallthrough / exact-output`

---

## JAVA-B02-D008 — Block arm with `yield`

**Target:** Java 17 and Java 21

```java
int value = switch (2) {
    case 1 -> 10;
    case 2 -> {
        int base = 20;
        yield base + 2;
    }
    default -> 0;
};
System.out.println(value);
```

> [!answer]- Answer
> Compiles and prints `22`.

### Reasoning

The selected block arm produces the switch-expression value with `yield`.

### Error Taxonomy

`switch-expression / yield`

---

## JAVA-B02-D009 — Exhaustive enum expression

**Target:** Java 17 and Java 21

```java
enum State { NEW, RUNNING, DONE }
State state = State.RUNNING;
int code = switch (state) {
    case NEW -> 0;
    case RUNNING -> 1;
    case DONE -> 2;
};
System.out.println(code);
```

> [!answer]- Answer
> Compiles and prints `1`.

### Reasoning

All enum constants are covered, so no `default` is required.

### Error Taxonomy

`switch-expression / enum-exhaustiveness`

---

## JAVA-B02-D010 — Non-exhaustive switch expression

**Target:** Java 17 and Java 21

```java
int value = switch (2) {
    case 1 -> 10;
    case 2 -> 20;
};
```

> [!answer]- Answer
> Does not compile because the switch expression is not exhaustive.

### Reasoning

The selector is `int`; values other than 1 and 2 are uncovered.

### Error Taxonomy

`compile-fail / exhaustiveness`

---

## JAVA-B02-D011 — Duplicate constants after folding

**Target:** Java 17 and Java 21

```java
int n = 2;
switch (n) {
    case 1 + 1 -> System.out.println("A");
    case 2 -> System.out.println("B");
    default -> System.out.println("C");
}
```

> [!answer]- Answer
> Does not compile because both case constants have value `2`.

### Reasoning

Case uniqueness is checked after constant-expression evaluation.

### Error Taxonomy

`compile-fail / duplicate-case`

---

## JAVA-B02-D012 — Qualified enum label version boundary

**Target:** Java 17 versus Java 21

```java
enum Color { RED, BLUE }
Color color = Color.RED;
int code = switch (color) {
    case Color.RED -> 1;
    case Color.BLUE -> 2;
};
System.out.println(code);
```

> [!answer]- Answer
> Fails under `--release 17`; compiles under `--release 21` and prints `1`.

### Reasoning

Java 17 requires unqualified enum constants in a direct enum switch. Java 21 permits qualified enum constants.

### Error Taxonomy

`version-trap / enum-label`

---

## JAVA-B02-D013 — Type-pattern dispatch

**Target:** Java 21

```java
Object value = "abc";
String result = switch (value) {
    case String s -> "S" + s.length();
    case Integer i -> "I" + i;
    default -> "O";
};
System.out.println(result);
```

> [!answer]- Answer
> Compiles and prints `S3`.

### Reasoning

The first applicable type pattern is `String s`, which binds the selector value.

### Error Taxonomy

`pattern-switch / exact-output`

---

## JAVA-B02-D014 — Guarded pattern before fallback

**Target:** Java 21

```java
Object value = " ";
String result = switch (value) {
    case String s when s.isBlank() -> "blank";
    case String s -> "text";
    default -> "other";
};
System.out.println(result);
```

> [!answer]- Answer
> Compiles and prints `blank`.

### Reasoning

The selector first matches `String s`; its guard is true, so the first rule is selected.

### Error Taxonomy

`pattern-guard / exact-output`

---

## JAVA-B02-D015 — Explicit null label

**Target:** Java 21

```java
Object value = null;
String result = switch (value) {
    case null -> "N";
    case String s -> "S";
    default -> "O";
};
System.out.println(result);
```

> [!answer]- Answer
> Compiles and prints `N`.

### Reasoning

`case null` handles the null selector inside the switch.

### Error Taxonomy

`pattern-switch / null`

---

## JAVA-B02-D016 — `default` does not catch null

**Target:** Java 21

```java
Object value = null;
String result = switch (value) {
    case String s -> "S";
    default -> "D";
};
```

> [!answer]- Answer
> Compiles, but evaluation throws `NullPointerException`.

### Reasoning

Without `case null`, null retains the historical exceptional behavior; `default` is not selected.

### Error Taxonomy

`runtime-fail / null-selector`

---

## JAVA-B02-D017 — Dominated subtype pattern

**Target:** Java 21

```java
static int classify(Object value) {
    return switch (value) {
        case CharSequence cs -> 1;
        case String s -> 2;
        default -> 0;
    };
}
```

> [!answer]- Answer
> Does not compile because `case String s` is dominated by the preceding `CharSequence` pattern.

### Reasoning

Every String is a CharSequence, and source order chooses the first applicable label.

### Error Taxonomy

`compile-fail / dominance`

---

## JAVA-B02-D018 — Non-exhaustive enhanced switch statement

**Target:** Java 21

```java
static void print(Object value) {
    switch (value) {
        case String s -> System.out.println(s);
    }
}
```

> [!answer]- Answer
> Does not compile because a pattern switch statement is enhanced and must be exhaustive.

### Reasoning

An `Object` value that is not a String is uncovered.

### Error Taxonomy

`compile-fail / enhanced-exhaustiveness`

---

## JAVA-B02-D019 — Sealed hierarchy coverage

**Target:** Java 21

```java
sealed interface Token permits Word, NumberToken {}
record Word(String text) implements Token {}
record NumberToken(int value) implements Token {}

Token token = new NumberToken(7);
int size = switch (token) {
    case Word w -> w.text().length();
    case NumberToken n -> n.value();
};
System.out.println(size);
```

> [!answer]- Answer
> Compiles and prints `7`.

### Reasoning

The two patterns cover every permitted implementation of the sealed selector type.

### Error Taxonomy

`pattern-switch / sealed-exhaustiveness`

---

## JAVA-B02-D020 — Combined null/default label

**Target:** Java 21

```java
Object value = 42;
String result = switch (value) {
    case String s -> "text";
    case null, default -> "rest";
};
System.out.println(result);
```

> [!answer]- Answer
> Compiles and prints `rest`.

### Reasoning

The selector is non-null and does not match `String`, so the combined match-all label applies.

### Error Taxonomy

`pattern-switch / null-default`

---

# Coverage

| Family | Drills |
|---|---:|
| Conditions, loops and labels | 6 |
| Classic switch and switch expressions | 6 |
| Java 21 pattern switch | 8 |
| **Total** | **20** |

# Execution evidence

Positive and negative versions of these mechanisms are implemented in [[50_LABS/Java/JAVA-B02/README]].
