---
type: atomic-concept
domain: java
route: JAVA-B01
concept_id: JAVA-B01-N04
status: published
verified_at: 2026-07-24
java_versions:
  - 17
  - 21
prerequisites:
  - JAVA-B01-N03
previous_note: Java Wrappers Boxing and Math
next_note: Java StringBuilder Mutation
tags:
  - java
  - atomic-note
  - string
  - identity
  - operations
---

# Java String Identity and Operations

> [!summary]
> `String` is immutable. Exam questions often combine pool identity, left-to-right concatenation, index contracts and regex-sensitive methods.

## Immutability

```java
String s = "java";
s.toUpperCase();
System.out.println(s); // java

s = s.toUpperCase();
System.out.println(s); // JAVA
```

Methods return values; they do not mutate the existing instance.

## Pooling and compile-time constants

```java
String a = "java";
String b = "ja" + "va";
System.out.println(a == b); // true
```

Runtime concatenation usually produces another object:

```java
String part = "ja";
String c = part + "va";

a == c;       // false
a.equals(c);  // true
```

`intern()` returns the canonical pooled representation.

## Concatenation order

```java
1 + 2 + "3"    // "33"
"1" + 2 + 3    // "123"
"1" + (2 + 3)  // "15"
```

## Index contracts

Indexes are zero-based. End indexes are usually exclusive.

```java
String s = "certify";
s.length();          // 7
s.charAt(0);         // 'c'
s.substring(1, 4);  // "ert"
s.indexOf('t');      // 3
s.lastIndexOf('i');  // 5
```

`charAt(length())` throws `StringIndexOutOfBoundsException`.

For `substring(begin, end)`:

```text
0 <= begin <= end <= length
```

## Equality and ordering

```java
"Java".equals("java");            // false
"Java".equalsIgnoreCase("java");  // true
"abc".compareTo("abd");           // negative
```

`compareTo` promises negative, zero or positive, not specifically `-1`, `0`, `1`.

## Empty, blank and whitespace

```java
"".isEmpty();      // true
"   ".isEmpty();   // false
"   ".isBlank();   // true
```

`trim()` uses the older U+0020 boundary rule. `strip()` uses Unicode-aware whitespace classification.

## Replacement and splitting

`replace` is literal:

```java
"a.b".replace(".", "-"); // "a-b"
```

`replaceAll` and `replaceFirst` use regex:

```java
"a.b".replaceAll(".", "-"); // "---"
```

`split` uses regex. The one-argument overload removes trailing empty results:

```java
"a,b,".split(",").length;     // 2
"a,b,".split(",", -1).length; // 3
```

## Formatting

```java
"%s:%02d".formatted("A", 7);      // "A:07"
String.format("%s:%02d", "A", 7); // "A:07"
```

Use an explicit locale when output depends on localized formatting.

## Exam traps

- `==` compares identity.
- compile-time and runtime concatenation can differ in identity.
- `charAt(length())` is out of range.
- `replaceAll(".", "-")` uses regex `.`.
- trailing empty elements disappear from one-argument `split`.
- `compareTo` does not promise exactly `-1` or `1`.

## Route navigation

- **Java dashboard:** [[00_HOME/Java Learning Dashboard]]
- **Route roadmap:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Roadmap]]
- **Canonical hub:** [[10_CONCEPTS/Java/Core/Java Values Text and Date-Time]]
- **Previous concept:** [[10_CONCEPTS/Java/Core/Java Wrappers Boxing and Math]]
- **Next concept:** [[10_CONCEPTS/Java/Core/Java StringBuilder Mutation]]
- **Practice cards:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01B Text Cards]]
- **Compile/output drills:** [[30_CERTIFICATIONS/Java/JAVA-B01/JAVA-B01 Drills]]
- **Executable lab:** [[50_LABS/Java/JAVA-B01/README]]
- **Primary sources:** [[98_SOURCES/Java SE 21 1Z0-830 Sources]]
