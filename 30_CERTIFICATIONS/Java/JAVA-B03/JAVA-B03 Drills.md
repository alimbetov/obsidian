---
type: certification-drill-bank
domain: java
route: JAVA-B03
status: published
drill_count: 35
java_versions:
  - 17
  - 21
objectives:
  - JAVA-B03
  - JAVA21-3.1
  - JAVA21-3.2
  - JAVA21-3.3
  - JAVA21-3.4
  - JAVA21-3.5
  - JAVA21-3.6
  - JAVA21-3.7
tags:
  - java
  - compile-output
  - object-model
  - records
  - record-patterns
---

# JAVA-B03 — Compile and Output Drills

> [!summary]
> Predict compilation first, then initialization, overload selection, runtime dispatch and output. Java 21-only record-pattern drills are explicitly marked.

## Route navigation

- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- [[50_LABS/Java/JAVA-B03/README]]

# Drill bank

## JAVA-B03-D001 — Reference aliasing

**Target:** Java 17 and 21

```java
var a = new StringBuilder("A");
var b = a;
b.append("B");
System.out.println(a);
```

> [!answer]- Answer
> Compiles and prints `AB`.

### Reasoning

Both variables refer to one mutable builder.

### Error Taxonomy

`aliasing / output`

---

## JAVA-B03-D002 — Static nested construction

**Target:** Java 17 and 21

```java
class Outer { static class Nested {} }
var n = new Outer.Nested();
System.out.println(n != null);
```

> [!answer]- Answer
> Compiles and prints `true`.

### Reasoning

A static nested class needs no enclosing object.

### Error Taxonomy

`nested-class / construction`

---

## JAVA-B03-D003 — Inner construction syntax

**Target:** Java 17 and 21

```java
class Outer { class Inner {} }
var outer = new Outer();
var inner = outer.new Inner();
System.out.println(inner != null);
```

> [!answer]- Answer
> Compiles and prints `true`.

### Reasoning

A member inner class is created from an enclosing instance.

### Error Taxonomy

`inner-class / construction`

---

## JAVA-B03-D004 — Captured local reassignment

**Target:** Java 17 and 21

```java
int limit = 10;
class Check { boolean ok(int x) { return x < limit; } }
limit = 20;
```

> [!answer]- Answer
> Does not compile.

### Reasoning

The local class captures `limit`, which is not effectively final after reassignment.

### Error Taxonomy

`capture / effectively-final`

---

## JAVA-B03-D005 — Anonymous `this`

**Target:** Java 17 and 21

```java
class Demo {
  String name = "outer";
  void run() {
    Object x = new Object() { String name = "inner"; public String toString(){ return this.name; } };
    System.out.println(x);
  }
}
```

> [!answer]- Answer
> Compiles; calling `new Demo().run()` prints `inner`.

### Reasoning

`this` in the anonymous class denotes the anonymous object.

### Error Taxonomy

`anonymous-class / this`

---

## JAVA-B03-D006 — Default field and local

**Target:** Java 17 and 21

```java
class A {
  int field;
  void run() {
    int local;
    System.out.print(field);
    System.out.print(local);
  }
}
```

> [!answer]- Answer
> Does not compile because `local` is not definitely assigned.

### Reasoning

The field has default `0`; the local variable has no default.

### Error Taxonomy

`definite-assignment`

---

## JAVA-B03-D007 — Initialization trace

**Target:** Java 17 and 21

```java
class A {
  static { System.out.print("S"); }
  int x = print("F");
  { System.out.print("I"); }
  A(){ System.out.print("C"); }
  static int print(String s){ System.out.print(s); return 1; }
  public static void main(String[] x){ new A(); }
}
```

> [!answer]- Answer
> Compiles and prints `SFIC`.

### Reasoning

Static initialization occurs once, then instance field, instance block and constructor body.

### Error Taxonomy

`initialization-order / output`

---

## JAVA-B03-D008 — Implicit super failure

**Target:** Java 17 and 21

```java
class Parent { Parent(int x) {} }
class Child extends Parent { Child() {} }
```

> [!answer]- Answer
> Does not compile.

### Reasoning

`Child()` implicitly invokes inaccessible/nonexistent `Parent()`.

### Error Taxonomy

`constructor / implicit-super`

---

## JAVA-B03-D009 — Overridable call from constructor

**Target:** Java 17 and 21

```java
class P { P(){ print(); } void print(){} }
class C extends P { int x = 7; void print(){ System.out.print(x); } }
new C();
```

> [!answer]- Answer
> Compiles and prints `0`.

### Reasoning

Runtime dispatch reaches `C.print` before `C.x` receives its explicit initializer.

### Error Taxonomy

`constructor-dispatch / output`

---

## JAVA-B03-D010 — Constructor delegation order

**Target:** Java 17 and 21

```java
class A {
 A(){ this(1); System.out.print("A"); }
 A(int x){ System.out.print(x); }
 public static void main(String[] x){ new A(); }
}
```

> [!answer]- Answer
> Compiles and prints `1A`.

### Reasoning

The delegated constructor completes before the delegating constructor body.

### Error Taxonomy

`constructor-chain / output`

---

## JAVA-B03-D011 — Widening before boxing

**Target:** Java 17 and 21

```java
static void m(long x){ System.out.print("L"); }
static void m(Integer x){ System.out.print("I"); }
m(1);
```

> [!answer]- Answer
> Compiles and prints `L`.

### Reasoning

Primitive widening is considered before boxing.

### Error Taxonomy

`overload / conversion-phase`

---

## JAVA-B03-D012 — Unboxing and widening

**Target:** Java 17 and 21

```java
static void m(long x){ System.out.print("L"); }
Integer x = 1;
m(x);
```

> [!answer]- Answer
> Compiles and prints `L`.

### Reasoning

`Integer` unboxes to int and widens to long.

### Error Taxonomy

`overload / unboxing-widening`

---

## JAVA-B03-D013 — Widen then box

**Target:** Java 17 and 21

```java
static void m(Long x){}
m(1);
```

> [!answer]- Answer
> Does not compile.

### Reasoning

Invocation does not widen int to long and then box to Long.

### Error Taxonomy

`overload / invalid-conversion`

---

## JAVA-B03-D014 — Ambiguous null overload

**Target:** Java 17 and 21

```java
static void m(String x){}
static void m(Integer x){}
m(null);
```

> [!answer]- Answer
> Does not compile because the call is ambiguous.

### Reasoning

The unrelated reference parameters are both applicable and neither is more specific.

### Error Taxonomy

`overload / ambiguity`

---

## JAVA-B03-D015 — Varargs fallback

**Target:** Java 17 and 21

```java
static void m(int x){ System.out.print("F"); }
static void m(int... x){ System.out.print("V"); }
m();
```

> [!answer]- Answer
> Compiles and prints `V`.

### Reasoning

Only the variable-arity overload is applicable to zero arguments.

### Error Taxonomy

`varargs / output`

---

## JAVA-B03-D016 — Return type overload

**Target:** Java 17 and 21

```java
int value(){ return 1; }
long value(){ return 2; }
```

> [!answer]- Answer
> Does not compile.

### Reasoning

Return type alone does not create a distinct overload signature.

### Error Taxonomy

`overload / duplicate-signature`

---

## JAVA-B03-D017 — Field versus method polymorphism

**Target:** Java 17 and 21

```java
class P { String x="P"; String m(){return "P";} }
class C extends P { String x="C"; String m(){return "C";} }
P p = new C();
System.out.println(p.x + p.m());
```

> [!answer]- Answer
> Compiles and prints `PC`.

### Reasoning

Field selection uses static type P; overridden method uses runtime type C.

### Error Taxonomy

`polymorphism / field-hiding`

---

## JAVA-B03-D018 — Static hiding

**Target:** Java 17 and 21

```java
class P { static String m(){return "P";} }
class C extends P { static String m(){return "C";} }
P p = new C();
System.out.println(p.m());
```

> [!answer]- Answer
> Compiles and prints `P`.

### Reasoning

Static method selection follows the reference expression compile-time type.

### Error Taxonomy

`static-hiding / output`

---

## JAVA-B03-D019 — Covariant return

**Target:** Java 17 and 21

```java
class P { Number m(){ return 1; } }
class C extends P { Integer m(){ return 2; } }
```

> [!answer]- Answer
> Compiles.

### Reasoning

Integer is a covariant subtype of Number.

### Error Taxonomy

`override / covariant-return`

---

## JAVA-B03-D020 — Reduced access override

**Target:** Java 17 and 21

```java
class P { protected void m(){} }
class C extends P { private void m(){} }
```

> [!answer]- Answer
> Does not compile.

### Reasoning

An override cannot reduce accessibility.

### Error Taxonomy

`override / access`

---

## JAVA-B03-D021 — Broader checked exception

**Target:** Java 17 and 21

```java
class P { void m() throws java.io.IOException{} }
class C extends P { void m() throws Exception{} }
```

> [!answer]- Answer
> Does not compile.

### Reasoning

The override declares a broader checked exception.

### Error Taxonomy

`override / checked-exception`

---

## JAVA-B03-D022 — Default conflict

**Target:** Java 17 and 21

```java
interface A { default String id(){return "A";} }
interface B { default String id(){return "B";} }
class C implements A,B {}
```

> [!answer]- Answer
> Does not compile.

### Reasoning

C must explicitly resolve unrelated default methods with the same signature.

### Error Taxonomy

`interface-default / conflict`

---

## JAVA-B03-D023 — Class wins over default

**Target:** Java 17 and 21

```java
class P { public String id(){return "P";} }
interface A { default String id(){return "A";} }
class C extends P implements A {}
System.out.println(new C().id());
```

> [!answer]- Answer
> Compiles and prints `P`.

### Reasoning

A concrete class method takes precedence over an interface default.

### Error Taxonomy

`interface-default / class-wins`

---

## JAVA-B03-D024 — Functional interface Object method

**Target:** Java 17 and 21

```java
@FunctionalInterface
interface F { void run(); boolean equals(Object x); }
```

> [!answer]- Answer
> Compiles.

### Reasoning

A declaration matching a public Object method does not add a second functional abstract method.

### Error Taxonomy

`functional-interface / Object-method`

---

## JAVA-B03-D025 — Downcast runtime check

**Target:** Java 17 and 21

```java
class P {}
class C extends P {}
P p = new P();
C c = (C) p;
```

> [!answer]- Answer
> Compiles and throws `ClassCastException` at runtime.

### Reasoning

The types are related enough for the cast, but the actual object is not a C.

### Error Taxonomy

`cast / runtime-type`

---

## JAVA-B03-D026 — Pattern scope after early exit

**Target:** Java 17 and 21

```java
static int size(Object x){
 if (!(x instanceof String s)) return -1;
 return s.length();
}
```

> [!answer]- Answer
> Compiles.

### Reasoning

After the negated pattern exits, flow analysis proves `s` is matched.

### Error Taxonomy

`instanceof-pattern / scope`

---

## JAVA-B03-D027 — Record structural equality

**Target:** Java 17 and 21

```java
record P(int x,int y){}
System.out.println(new P(1,2).equals(new P(1,2)));
```

> [!answer]- Answer
> Compiles and prints `true`.

### Reasoning

Generated record equality compares same record class and components.

### Error Taxonomy

`record / equality`

---

## JAVA-B03-D028 — Record extra field

**Target:** Java 17 and 21

```java
record P(int x){ int y = 2; }
```

> [!answer]- Answer
> Does not compile.

### Reasoning

A record cannot declare an additional instance field.

### Error Taxonomy

`record / state-description`

---

## JAVA-B03-D029 — Compact constructor normalization

**Target:** Java 17 and 21

```java
record Name(String value){ Name { value = value.trim(); } }
System.out.println(new Name(" A ").value());
```

> [!answer]- Answer
> Compiles and prints `A`.

### Reasoning

The reassigned parameter is assigned to the component field after the compact body.

### Error Taxonomy

`record / compact-constructor`

---

## JAVA-B03-D030 — Enum constructor access

**Target:** Java 17 and 21

```java
enum E { A; public E(){} }
```

> [!answer]- Answer
> Does not compile.

### Reasoning

An enum constructor cannot be public or protected.

### Error Taxonomy

`enum / constructor`

---

## JAVA-B03-D031 — Sealed unauthorized child

**Target:** Java 17 and 21

```java
sealed class P permits A {}
final class A extends P {}
final class B extends P {}
```

> [!answer]- Answer
> Does not compile.

### Reasoning

B is not a permitted direct subtype.

### Error Taxonomy

`sealed / permits`

---

## JAVA-B03-D032 — Non-sealed context

**Target:** Java 17 and 21

```java
non-sealed class Open {}
```

> [!answer]- Answer
> Does not compile.

### Reasoning

`non-sealed` is valid only for the required direct child of a sealed type.

### Error Taxonomy

`sealed / non-sealed`

---

## JAVA-B03-D033 — Record pattern version trap

**Target:** Java 17

```java
record Point(int x,int y){}
boolean ok = value instanceof Point(int x,int y);
```

> [!answer]- Answer
> Does not compile under `--release 17`.

### Reasoning

Record patterns are a final Java 21 feature, not ordinary Java 17 syntax.

### Error Taxonomy

`version / record-pattern`

---

## JAVA-B03-D034 — Nested record pattern

**Target:** Java 21

```java
record Point(int x,int y){}
record Box(Point p){}
int result = switch(value){
 case Box(Point(int x,int y)) -> x+y;
 default -> 0;
};
```

> [!answer]- Answer
> Compiles under Java 21.

### Reasoning

The Box record pattern nests a Point record pattern and binds both components.

### Error Taxonomy

`record-pattern / nested`

---

## JAVA-B03-D035 — Sealed record-pattern exhaustiveness

**Target:** Java 21

```java
sealed interface E permits N,A{}
record N(int v) implements E{}
record A(E l,E r) implements E{}
int eval(E e){ return switch(e){
 case N(int v) -> v;
 case A(var l,var r) -> eval(l)+eval(r);
};}
```

> [!answer]- Answer
> Compiles under Java 21 without `default`.

### Reasoning

The two cases cover all permitted direct implementations of E.

### Error Taxonomy

`sealed / exhaustive-pattern-switch`

---
