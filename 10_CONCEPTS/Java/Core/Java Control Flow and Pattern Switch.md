---
type: canonical-concept
domain: java
subdomain: control-flow-pattern-switch
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
objectives:
  - JAVA-B02
  - JAVA21-2.1
tags:
  - java
  - control-flow
  - switch-expression
  - pattern-switch
  - dominance
  - exhaustiveness
---

# Java Control Flow and Pattern Switch

> [!summary]
> Java control flow is governed by grammar, definite-assignment and reachability analysis, not by indentation or informal intent. Java 21 extends `switch` with patterns, `null`, guards and broader reference selectors, and compensates for that expressiveness with compile-time dominance and exhaustiveness checks.

# 1. Conditions are boolean computations

`if`, `while`, `do-while` and the middle expression of a basic `for` require a boolean result.

```java
Boolean enabled = null;
if (enabled) {             // compiles; unboxing throws NullPointerException
    System.out.println("on");
}
```

Java has no truthy/falsy conversion from numbers or references.

```java
if (1) { }                 // does not compile
if (new Object()) { }      // does not compile
```

## Short-circuit boundary

```java
left && right
left || right
```

The right operand may be skipped.

```java
boolean result = false && dangerous();
```

`dangerous()` is not invoked.

With boolean operands, `&` and `|` evaluate both sides:

```java
boolean result = false & dangerous(); // dangerous() runs
```

This difference is observable in side effects, exceptions and exact output.

# 2. `if`, `else` and definite assignment

## Dangling else

Without braces, `else` belongs to the nearest unmatched `if`.

```java
if (outer)
    if (inner)
        actionA();
    else
        actionB();
```

The `else` belongs to `if (inner)`.

## Definite assignment

Local variables have no default value. The compiler must prove assignment on every reachable path before a read.

```java
int value;
if (condition) {
    value = 10;
}
System.out.println(value);  // does not compile
```

An exhaustive `if/else` can establish assignment:

```java
int value;
if (condition) {
    value = 10;
} else {
    value = 20;
}
System.out.println(value);
```

# 3. Loop execution points

## `while`

```text
condition → body → condition → ...
```

The body can execute zero times.

## `do-while`

```text
body → condition → body → ...
```

The body executes at least once. The trailing semicolon is mandatory.

## Basic `for`

```text
initializer → condition → body → update → condition → ...
```

All three header components may be empty:

```java
for (;;) {
    // infinite unless control exits
}
```

A `continue` transfers to the update expressions before the next condition check.

## Enhanced `for`

```java
for (Element element : source) {
    ...
}
```

The source must be an array or `Iterable`.

Assigning a new value to the loop variable does not replace the underlying element:

```java
int[] values = {1, 2, 3};
for (int value : values) {
    value++;
}
// values is still {1, 2, 3}
```

For references, mutation of the referenced object can remain visible even though reassignment of the loop variable is not.

# 4. Transfers and labels

## `break`

Unlabeled `break` exits the innermost loop or switch statement.

A labeled `break` may exit any enclosing labeled statement:

```java
search: {
    if (found()) {
        break search;
    }
    continueWork();
}
```

## `continue`

Unlabeled `continue` targets the innermost loop.

A labeled `continue` must target an enclosing loop:

```java
outer:
for (int row = 0; row < 3; row++) {
    for (int col = 0; col < 3; col++) {
        if (skipRow(row, col)) {
            continue outer;
        }
    }
}
```

A plain labeled block is a valid `break` target but not a valid `continue` target.

## `return`

`return` completes the method invocation. It is not a multi-level `break`. A `finally` block can still execute before method completion.

# 5. Java 17 pattern matching for `instanceof`

Java 17 includes final type patterns for `instanceof`:

```java
if (value instanceof String text) {
    System.out.println(text.length());
}
```

The pattern variable exists only where flow analysis proves the match succeeded.

```java
if (value instanceof String text && text.length() > 3) {
    use(text);
}
```

The right operand of `&&` is evaluated only after a successful match, so `text` is in scope.

This is invalid:

```java
if (value instanceof String text || text.isEmpty()) {
    // text may be needed when the match was false
}
```

A negated early exit can extend scope afterward:

```java
if (!(value instanceof String text)) {
    return;
}
System.out.println(text.length());
```

# 6. Reachability

Java rejects statements that cannot be reached under its structural rules.

```java
while (false) {
    System.out.println("never"); // compile-time error
}
```

`if (false)` is specially permitted to support conditional-compilation idioms:

```java
if (false) {
    debugOnly();
}
```

A statement after `while (true)` is reachable only when a reachable `break` can exit that loop.

# 7. Legacy switch statements

Legacy selector types are:

```text
byte, short, char, int
Byte, Short, Character, Integer
String
enum
```

Primitive `boolean`, `long`, `float` and `double` are not switch selector types.

A constant label must be a compatible compile-time constant:

```java
final int ONE = 1;
switch (value) {
    case ONE:
        break;
}
```

A merely final runtime value is insufficient.

```java
final int one = Integer.parseInt("1");
case one:                   // not a constant expression
```

## Colon fall-through

```java
switch (n) {
    case 1:
        print("A");
    case 2:
        print("B");
    default:
        print("C");
}
```

For `n == 2`, output is `BC`.

A label chooses an entry point; execution continues until `break`, `return`, `throw`, or the end of the switch.

## Arrow rules

```java
switch (n) {
    case 1, 2 -> print("small");
    default -> print("other");
}
```

Arrow rules do not fall through.

# 8. Switch expressions

A switch expression produces a value and must be exhaustive.

```java
int size = switch (token) {
    case "S" -> 1;
    case "M" -> 2;
    case "L" -> 3;
    default -> throw new IllegalArgumentException(token);
};
```

A block arm uses `yield`:

```java
int size = switch (token) {
    case "S" -> {
        log(token);
        yield 1;
    }
    default -> 0;
};
```

`break value` is not final Java syntax.

Every selected arm must:

```text
produce a value
or complete abruptly with throw
```

A block that reaches its closing brace without `yield` is invalid.

## Enum exhaustiveness

```java
enum State { NEW, RUNNING, DONE }

int code = switch (state) {
    case NEW -> 0;
    case RUNNING -> 1;
    case DONE -> 2;
};
```

No `default` is needed because all known enum constants are covered. Omitting a broad default preserves compile-time feedback when the enum evolves and the switch is recompiled.

# 9. Java 17 versus Java 21 switch boundary

## Java 17

Java 17 includes final switch expressions. When switching directly over an enum, case constants use unqualified names:

```java
case RED -> ...
```

This is rejected under the Java 17 baseline:

```java
case Color.RED -> ...
```

Pattern matching for switch exists only as an earlier preview feature and is excluded from ordinary Java 17 exam assumptions.

## Java 21

JEP 441 finalizes pattern matching for switch and allows:

```text
type patterns
case null
when guards
any reference selector type
qualified enum constants
```

# 10. Type patterns

```java
static String describe(Object value) {
    return switch (value) {
        case Integer i -> "int " + i;
        case String s -> "text " + s;
        default -> "other";
    };
}
```

A pattern label:

```text
tests runtime type compatibility
binds a local pattern variable
does not match null
```

Selection is source ordered: the first applicable label wins.

# 11. Dominance

Because several patterns can apply to one value, Java rejects unreachable later labels.

Valid:

```java
switch (value) {
    case String s -> useString(s);
    case CharSequence cs -> useSequence(cs);
    default -> useOther(value);
}
```

Invalid:

```java
switch (value) {
    case CharSequence cs -> useSequence(cs);
    case String s -> useString(s);      // dominated
    default -> useOther(value);
}
```

Constants should also precede broad compatible patterns:

```java
switch (value) {
    case "YES" -> accept();
    case String s -> inspect(s);
    default -> reject();
}
```

Placing `case String s` first dominates the later `"YES"` constant.

# 12. Guards

Final Java 21 syntax uses `when`:

```java
switch (value) {
    case String s when s.isBlank() -> "blank";
    case String s -> "text";
    default -> "other";
}
```

Only pattern labels may have guards.

The pattern variable is in scope:

```text
inside the guard
inside the selected arrow expression/block/throw
```

For dominance between pattern labels, arbitrary guards are not solved logically. A guard that is the constant expression `true` is treated specially.

# 13. Null handling

A Java 21 switch can handle null explicitly:

```java
switch (value) {
    case null -> "missing";
    case String s -> s;
    default -> value.toString();
}
```

Without `case null`, a null selector throws `NullPointerException`.

`default` does not match null.

A combined label is available:

```java
case null, default -> "rest";
```

It handles both null and every unmatched non-null value.

# 14. Enhanced switch statements

A switch statement is enhanced when it:

```text
uses a pattern label
uses case null
or switches on a non-legacy selector reference type
```

Enhanced switch statements must be exhaustive.

```java
static void print(Object value) {
    switch (value) {                 // does not compile: not exhaustive
        case String s -> System.out.println(s);
    }
}
```

Add a match-all label or complete sealed coverage.

# 15. Sealed exhaustiveness

```java
sealed interface Shape permits Circle, Rectangle {}
record Circle(double radius) implements Shape {}
record Rectangle(double width, double height) implements Shape {}

double area(Shape shape) {
    return switch (shape) {
        case Circle c -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.width() * r.height();
    };
}
```

The compiler uses the `permits` structure to prove coverage.

A broad `default` is often undesirable here because it can hide a newly added permitted alternative from compile-time checking.

# 16. Match-all labels

The following can act as match-all labels:

```text
default
case null, default
an unconditional type pattern for the selector type
```

At most one match-all label is allowed.

For an `Object` selector, this is invalid:

```java
case Object o -> ...
default -> ...             // second match-all label
```

# 17. Pattern-variable scope and fall-through

For an arrow rule, the pattern variable is scoped to that rule and its guard.

Colon-style fall-through cannot enter a statement group that expects a pattern variable, because execution might arrive without matching and initializing that variable.

# 18. Qualified enum constants in Java 21

Java 21 permits qualified enum constants:

```java
case Color.RED -> ...
```

This also supports switches over broader selector types where constants from distinct enum classes must be named unambiguously.

This is a version-sensitive compile rule: the same direct enum switch uses unqualified names in the Java 17 lane.

# 19. Reliable exam algorithm

For every control-flow question:

```text
1. Fix the Java version.
2. Validate grammar and selector/label compatibility.
3. Check definite assignment and reachability.
4. For switch, classify legacy statement, expression, or enhanced statement.
5. Check duplicate labels and dominance.
6. Check exhaustiveness.
7. Resolve null behavior.
8. Trace the selected label and transfer statements.
9. Only then compute output.
```

# Related

- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Roadmap]]
- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02A Control Flow Cards]]
- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02B Switch Cards]]
- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02C Pattern Switch Cards]]
- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills]]
- [[50_LABS/Java/JAVA-B02/README]]
- [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
