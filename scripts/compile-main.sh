#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$ROOT"

"$SCRIPT_DIR/ensure-deps.sh"

source "$SCRIPT_DIR/find-kotlinc.sh"
source "$SCRIPT_DIR/lib-classpath.sh"

PLUGIN="lib/kotlinx-serialization-compiler-plugin-1.9.24.jar"
OUT="build/classes"

mkdir -p "$OUT"
find src/org/example -name '*.kt' > build/sources-main.txt

kotlinc -jvm-target 11 -cp "$LIB_CP" -Xplugin="$PLUGIN" -d "$OUT" @"build/sources-main.txt"
