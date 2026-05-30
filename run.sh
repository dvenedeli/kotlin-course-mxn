#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

./scripts/compile-main.sh
source ./scripts/lib-runtime-classpath.sh
export LANG="${LANG:-ru_RU.UTF-8}"
export LC_ALL="${LC_ALL:-ru_RU.UTF-8}"
exec java -Dfile.encoding=UTF-8 -cp "build/classes:$RUN_CP" org.example.MainKt
