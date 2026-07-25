#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
OUT="$ROOT/out"
NEG="$OUT/negative"
rm -rf "$OUT"
mkdir -p "$OUT/classes" "$NEG"

javac --release 17 -d "$OUT/classes" \
  "$ROOT/src/main/java/lab/b06/LambdaStreamProof.java" \
  "$ROOT/src/main/java/lab/b06/ParallelReductionProof.java"

java -cp "$OUT/classes" lab.b06.LambdaStreamProof
java -cp "$OUT/classes" lab.b06.ParallelReductionProof

expect_compile_failure() {
  local name="$1"
  local source="$2"
  printf '%s\n' "$source" > "$NEG/$name.java"
  if javac --release 17 -d "$OUT/classes" "$NEG/$name.java" >"$NEG/$name.log" 2>&1; then
    echo "Expected compilation failure: $name" >&2
    exit 1
  fi
}

expect_compile_failure NoTargetType 'class NoTargetType { void test() { var f = x -> x; } }'
expect_compile_failure NotEffectivelyFinal 'import java.util.function.*; class NotEffectivelyFinal { void test() { int n=1; n++; IntSupplier s=()->n; } }'
expect_compile_failure WrongPredicateReturn 'import java.util.function.*; class WrongPredicateReturn { Predicate<String> p = s -> s.length(); }'
expect_compile_failure ReassignCaptured 'import java.util.function.*; class ReassignCaptured { void test(){ int n=1; Runnable r=()->System.out.println(n); n=2; } }'
expect_compile_failure BadMethodReference 'import java.util.function.*; class BadMethodReference { Supplier<String> s = String::trim; }'
expect_compile_failure SortedNonComparable 'import java.util.stream.*; class SortedNonComparable { record X(int n){} void t(){ Stream.of(new X(1)).sorted(); } }'

rm -rf "$OUT"
echo "JAVA-B06 expected compile-failure bank PASS"
