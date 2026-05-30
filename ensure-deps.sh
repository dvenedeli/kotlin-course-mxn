#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

if [[ -f lib/junit-platform-console-standalone-1.10.2.jar && -f lib/kotlinx-serialization-compiler-plugin-1.9.24.jar ]]; then
  exit 0
fi

echo "Fetching dependencies (first run may take a minute)..."
./download-deps.sh
