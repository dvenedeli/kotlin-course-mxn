#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

./compile-main.sh

source ./find-kotlinc.sh
source ./lib-test-classpath.sh

PLUGIN="lib/kotlinx-serialization-compiler-plugin-1.9.24.jar"
OUT="build/test-classes"

mkdir -p "$OUT"
find test/org/example -name '*.kt' > build/sources-test.txt

kotlinc -jvm-target 11 -cp "build/classes:$TEST_CP" -Xplugin="$PLUGIN" -d "$OUT" @"build/sources-test.txt"

java -jar lib/junit-platform-console-standalone-1.10.2.jar \
  --class-path "build/classes:$OUT:$TEST_CP" \
  --scan-class-path
