#!/usr/bin/env bash
set -euo pipefail

JAVA_VERSION="${1:-}"
if [[ ! "${JAVA_VERSION}" =~ ^(11|17|21)$ ]]; then
  echo "Usage: $0 <11|17|21>" >&2
  exit 2
fi

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SOURCE_DIR="${ROOT_DIR}/src/main/java"
TARGET_DIR="${ROOT_DIR}/target/classes-${JAVA_VERSION}"

rm -rf "${TARGET_DIR}"
mkdir -p "${TARGET_DIR}"

javac --release 11 \
  -d "${TARGET_DIR}" \
  "${SOURCE_DIR}/lab/lts/Java11Baseline.java"
java -cp "${TARGET_DIR}" lab.lts.Java11Baseline

if [[ "${JAVA_VERSION}" -ge 17 ]]; then
  javac --release 17 \
    -d "${TARGET_DIR}" \
    "${SOURCE_DIR}/lab/lts/Java17Features.java"
  java -cp "${TARGET_DIR}" lab.lts.Java17Features
fi

if [[ "${JAVA_VERSION}" -ge 21 ]]; then
  javac --release 21 \
    -d "${TARGET_DIR}" \
    "${SOURCE_DIR}/lab/lts/Java21Features.java"
  java -cp "${TARGET_DIR}" lab.lts.Java21Features
fi

echo "JAVA-LTS-B01 PASS on JDK ${JAVA_VERSION}"
