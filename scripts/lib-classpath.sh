#!/usr/bin/env bash
LIB_CP=""
for jar in lib/*.jar; do
  base="$(basename "$jar")"
  if [[ "$base" == *serialization-compiler-plugin* || "$base" == junit-platform-console-standalone* || "$base" == kotlin-test* ]]; then
    continue
  fi
  if [[ -z "$LIB_CP" ]]; then
    LIB_CP="$jar"
  else
    LIB_CP="$LIB_CP:$jar"
  fi
done

if [[ -z "$LIB_CP" ]]; then
  echo "No compile classpath jars found in lib/" >&2
  exit 1
fi

export LIB_CP
