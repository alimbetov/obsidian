#!/usr/bin/env bash
set -euo pipefail

JAVA_VERSION="${1:-}"
if [[ "${JAVA_VERSION}" != "17" && "${JAVA_VERSION}" != "21" ]]; then
  echo "Usage: $0 17|21" >&2
  exit 2
fi

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD="${ROOT}/build/java-${JAVA_VERSION}"
rm -rf "${BUILD}"
mkdir -p "${BUILD}/classes" "${BUILD}/negative"

javac --release "${JAVA_VERSION}" \
  -d "${BUILD}/classes" \
  "${ROOT}/src/shared/java/lab/b02/ClassicFlowProof.java"

java -cp "${BUILD}/classes" lab.b02.ClassicFlowProof

expect_failure() {
  local source="$1"
  local name
  name="$(basename "${source}" .java)"
  local log="${BUILD}/negative/${name}.log"
  if javac --release "${JAVA_VERSION}" -d "${BUILD}/negative" "${source}" >"${log}" 2>&1; then
    echo "ERROR: expected javac failure for ${source}" >&2
    exit 1
  fi
  echo "EXPECTED-FAIL ${name}"
}

for source in "${ROOT}"/src/negative/shared/*.java; do
  expect_failure "${source}"
done

if [[ "${JAVA_VERSION}" == "17" ]]; then
  expect_failure "${ROOT}/src/negative/java17/QualifiedEnumJava17.java"
else
  javac --release 21 \
    -cp "${BUILD}/classes" \
    -d "${BUILD}/classes" \
    "${ROOT}/src/java21/java/lab/b02/PatternSwitchProof.java"

  java -cp "${BUILD}/classes" lab.b02.PatternSwitchProof

  for source in "${ROOT}"/src/negative/java21/*.java; do
    expect_failure "${source}"
  done
fi

echo "JAVA-B02 JDK ${JAVA_VERSION} proof PASS"
