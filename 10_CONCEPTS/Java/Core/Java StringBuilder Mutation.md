---
type: atomic-concept
domain: java
route: JAVA-B01
concept_id: JAVA-B01-N05
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
prerequisites:
  - JAVA-B01-N04
previous_note: Java String Identity and Operations
next_note: Java Text Blocks
tags:
  - java
  - atomic-note
  - stringbuilder
  - mutation
  - aliasing
---

# Java StringBuilder Mutation

> [!summary]
> `StringBuilder` is mutable. The central reasoning task is tracing one object through chained mutators and aliases.

## Mutation and chaining

Most mutator methods return the same builder.

```java
StringBuilder b = new StringBuilder("ab");
b.append("cd").insert(1, "X");
System.out.println(b); // aXbcd
```

## High-value methods

```text
append(value)
insert(offset, value)
delete(start, end)
deleteCharAt(index)
replace(start, end, text)
reverse()
setCharAt(index, char)
setLength(newLength)
substring(start, end)
```

`substring` returns a `String`; it does not mutate the builder.

End indexes for `delete`, `replace` and `substring` are exclusive.

## Aliasing

```java
StringBuilder a = new StringBuilder("x");
StringBuilder b = a;

b.append("y");
System.out.println(a); // xy
```

Both variables point to one mutable object.

## Equality

`StringBuilder` does not override `Object.equals` for content equality.

```java
StringBuilder a = new StringBuilder("java");
StringBuilder b = new StringBuilder("java");

a.equals(b);                       // false
a.toString().equals(b.toString()); // true
```

## `setLength`

Shrinking truncates content:

```java
StringBuilder b = new StringBuilder("abcdef");
b.setLength(3); // "abc"
```

Growing fills new positions with `\u0000` null characters.

## Capacity is not content

```text
length   → characters currently stored
capacity → internal storage size
```

Capacity changes do not directly change text content.

## Mutation trace protocol

1. Identify which object each variable references.
2. Apply the mutation to that object.
3. Note the returned value type.
4. Separate builder-returning methods from `String` results.
5. Treat `equals` as identity unless converted to `String`.

## Exam traps

- chained methods generally mutate the same object.
- aliases expose the same mutation.
- `substring` returns `String`.
- builder `equals` is identity-based.
- exclusive end indexes still apply.
- increasing length introduces null characters.

## Route navigation

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Route roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]]
- **Previous concept:** [[10_CONCEPTS/Java/Core/Java String Identity and Operations]]
- **Next concept:** [[10_CONCEPTS/Java/Core/Java Text Blocks]]
- **Practice cards:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01B Text Cards]]
- **Compile/output drills:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills]]
- **Executable lab:** [[50_LABS/Java/JAVA-B01/README]]
- **Primary sources:** [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
