#!/usr/bin/env bash
set -euo pipefail

RELEASE="${1:-21}"
ROOT="$(cd "$(dirname "$0")" && pwd)"
BUILD="$ROOT/build/$RELEASE"
rm -rf "$BUILD"
mkdir -p "$BUILD"

mapfile -t SHARED < <(find "$ROOT/src/shared/java" -name '*.java' | sort)
mapfile -t NEG_SHARED < <(find "$ROOT/src/negative/shared" -name '*.java' | sort)

expect_fail() {
  local file="$1"
  if javac --release "$RELEASE" -d "$BUILD" "$file" >"$BUILD/negative.log" 2>&1; then
    echo "Expected compilation failure: $file" >&2
    exit 1
  fi
}

if [[ "$RELEASE" == "17" ]]; then
  javac --release 17 -d "$BUILD" "${SHARED[@]}"
  java -cp "$BUILD" lab.b03.InitializationProof
  java -cp "$BUILD" lab.b03.ObjectModelProof
  java -cp "$BUILD" lab.b03.RecordsSealedProof
  for f in "${NEG_SHARED[@]}"; do expect_fail "$f"; done
  while IFS= read -r f; do expect_fail "$f"; done < <(find "$ROOT/src/negative/java17" -name '*.java' | sort)
elif [[ "$RELEASE" == "21" ]]; then
  mapfile -t J21 < <(find "$ROOT/src/java21/java" -name '*.java' | sort)
  javac --release 21 -d "$BUILD" "${SHARED[@]}" "${J21[@]}"
  java -cp "$BUILD" lab.b03.InitializationProof
  java -cp "$BUILD" lab.b03.ObjectModelProof
  java -cp "$BUILD" lab.b03.RecordsSealedProof
  java -cp "$BUILD" lab.b03.RecordPatternProof
  for f in "${NEG_SHARED[@]}"; do expect_fail "$f"; done
  while IFS= read -r f; do expect_fail "$f"; done < <(find "$ROOT/src/negative/java21" -name '*.java' | sort)
else
  echo "Supported releases: 17, 21" >&2
  exit 2
fi

echo "JAVA-B03 release $RELEASE PASS"
