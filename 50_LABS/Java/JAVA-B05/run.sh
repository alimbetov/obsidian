#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
OUT="$ROOT/out"
rm -rf "$OUT" && mkdir -p "$OUT/jdk17" "$OUT/jdk21"

javac --release 17 -d "$OUT/jdk17" "$ROOT/src/main/java/lab/b05/CollectionProof.java"
java -cp "$OUT/jdk17" lab.b05.CollectionProof

if javac --release 17 -d "$OUT/jdk17" "$ROOT/src/java21/java/lab/b05/SequencedCollectionProof.java" 2>/dev/null; then
  echo "Expected Java 21 source to fail with --release 17" >&2
  exit 1
fi

echo "JAVA-B05 JDK17 negative version-boundary proof PASS"

if java -version 2>&1 | grep -Eq 'version "(21|2[2-9]|[3-9][0-9])'; then
  javac --release 21 -d "$OUT/jdk21" "$ROOT/src/java21/java/lab/b05/SequencedCollectionProof.java"
  java -cp "$OUT/jdk21" lab.b05.SequencedCollectionProof
else
  echo "JDK21 lane skipped locally: run GitHub Actions matrix for full proof"
fi
