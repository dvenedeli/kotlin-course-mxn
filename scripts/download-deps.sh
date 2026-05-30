#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$ROOT"

LIB="lib"
DEPS="deps.list"
MAVEN_BASE="https://repo1.maven.org/maven2"
KOTLIN_VERSION="1.9.24"
KOTLIN_ZIP_URL="https://github.com/JetBrains/kotlin/releases/download/v${KOTLIN_VERSION}/kotlin-compiler-${KOTLIN_VERSION}.zip"
TOOLS_ZIP="tools/kotlin-compiler-${KOTLIN_VERSION}.zip"

mkdir -p "$LIB"

download_maven() {
  local path="$1"
  local name
  name="$(basename "$path")"
  local dest="$LIB/$name"
  if [[ -f "$dest" ]]; then
    echo "OK $name"
    return
  fi
  echo "Downloading $name..."
  curl -fsSL "$MAVEN_BASE/$path" -o "$dest"
}

ensure_kotlin_zip() {
  mkdir -p tools
  if [[ ! -f "$TOOLS_ZIP" ]]; then
    echo "Downloading Kotlin compiler ${KOTLIN_VERSION}..."
    curl -fsSL "$KOTLIN_ZIP_URL" -o "$TOOLS_ZIP"
  fi
}

extract_from_kotlin_dist() {
  local entry="$1"
  local dest_name="$2"
  local dest="$LIB/$dest_name"
  if [[ -f "$dest" ]]; then
    echo "OK $dest_name"
    return
  fi
  ensure_kotlin_zip
  echo "Extracting $dest_name from Kotlin compiler..."
  unzip -p "$TOOLS_ZIP" "$entry" > "$dest"
}

while IFS= read -r line || [[ -n "$line" ]]; do
  line="${line%%#*}"
  line="$(echo "$line" | xargs)"
  [[ -z "$line" ]] && continue
  if [[ "$line" == KOTLIN_DIST:* ]]; then
    rest="${line#KOTLIN_DIST:}"
    entry="${rest%%:*}"
    dest_name="${rest#*:}"
    extract_from_kotlin_dist "$entry" "$dest_name"
  else
    download_maven "$line"
  fi
done < "$DEPS"

KOTLIN_HOME="tools/kotlin-${KOTLIN_VERSION}"
if [[ ! -x "${KOTLIN_HOME}/bin/kotlinc" && ! -f "${KOTLIN_HOME}/bin/kotlinc.bat" ]]; then
  echo "Extracting Kotlin compiler to ${KOTLIN_HOME}..."
  ensure_kotlin_zip
  rm -rf tools/kotlinc "${KOTLIN_HOME}"
  unzip -q "$TOOLS_ZIP" -d tools
  if [[ -d tools/kotlinc ]]; then
    mv tools/kotlinc "$KOTLIN_HOME"
  fi
fi

echo "Dependencies ready in lib/"
