#!/usr/bin/env bash
if command -v kotlinc >/dev/null 2>&1; then
  return 0 2>/dev/null || exit 0
fi

if [[ -n "${KOTLIN_HOME:-}" && -x "$KOTLIN_HOME/bin/kotlinc" ]]; then
  export PATH="$KOTLIN_HOME/bin:$PATH"
  return 0 2>/dev/null || exit 0
fi

if [[ -x "tools/kotlin-1.9.24/bin/kotlinc" ]]; then
  export PATH="$(pwd)/tools/kotlin-1.9.24/bin:$PATH"
  return 0 2>/dev/null || exit 0
fi

echo "kotlinc not found. Install Kotlin 1.9.x and add it to PATH, or set KOTLIN_HOME." >&2
echo "Optional: extract kotlin-compiler-1.9.24.zip to tools/kotlin-1.9.24/" >&2
return 1 2>/dev/null || exit 1
