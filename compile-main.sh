#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

./ensure-deps.sh

source ./find-kotlinc.sh
source ./lib-classpath.sh

PLUGIN="lib/kotlinx-serialization-compiler-plugin-1.9.24.jar"
OUT="build/classes"

mkdir -p "$OUT"
find src/org/example -name '*.kt' > build/sources-main.txt

kotlinc -jvm-target 11 -cp "$LIB_CP" -Xplugin="$PLUGIN" -d "$OUT" @"build/sources-main.txt"
