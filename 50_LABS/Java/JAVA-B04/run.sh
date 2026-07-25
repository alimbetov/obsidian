#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
BUILD="$ROOT/build"
OUT="$BUILD/out"
NEGATIVE="$BUILD/negative"

rm -rf "$BUILD"
mkdir -p "$OUT" "$NEGATIVE"

javac --release "${JAVA_RELEASE:-17}" \
  -d "$OUT" \
  "$ROOT/src/main/java/lab/b04/ExceptionProof.java"

java -cp "$OUT" lab.b04.ExceptionProof

cat > "$NEGATIVE/CheckedNotDeclared.java" <<'JAVA'
import java.io.IOException;
class CheckedNotDeclared {
    static void run() {
        throw new IOException();
    }
}
JAVA

cat > "$NEGATIVE/UnreachableCatch.java" <<'JAVA'
import java.io.*;
class UnreachableCatch {
    static void run() {
        try { throw new FileNotFoundException(); }
        catch (IOException e) { }
        catch (FileNotFoundException e) { }
    }
}
JAVA

cat > "$NEGATIVE/OverlappingMultiCatch.java" <<'JAVA'
import java.io.*;
class OverlappingMultiCatch {
    static void run() {
        try { throw new FileNotFoundException(); }
        catch (IOException | FileNotFoundException e) { }
    }
}
JAVA

cat > "$NEGATIVE/ReassignMultiCatch.java" <<'JAVA'
import java.io.*;
import java.sql.*;
class ReassignMultiCatch {
    static void run() {
        try { if (System.nanoTime() > 0) throw new IOException(); else throw new SQLException(); }
        catch (IOException | SQLException e) { e = new IOException(); }
    }
}
JAVA

cat > "$NEGATIVE/BroadOverride.java" <<'JAVA'
import java.io.*;
class BroadOverride {
    static class Parent { void run() throws IOException { } }
    static class Child extends Parent { @Override void run() throws Exception { } }
}
JAVA

cat > "$NEGATIVE/ReassignedResource.java" <<'JAVA'
import java.io.*;
class ReassignedResource {
    static void run() {
        StringReader reader = new StringReader("A");
        reader = new StringReader("B");
        try (reader) { }
    }
}
JAVA

failures=0
for source in "$NEGATIVE"/*.java; do
  log="$source.log"
  if javac --release "${JAVA_RELEASE:-17}" -d "$OUT" "$source" >"$log" 2>&1; then
    echo "ERROR: expected compilation failure: $(basename "$source")"
    failures=$((failures + 1))
  else
    echo "Expected compile failure PASS: $(basename "$source")"
  fi
done

if [[ "$failures" -ne 0 ]]; then
  exit 1
fi

echo "JAVA-B04 negative compile bank PASS"
