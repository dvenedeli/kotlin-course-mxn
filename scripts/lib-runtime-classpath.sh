#!/usr/bin/env bash
RUN_CP=""
for jar in lib/*.jar; do
  base="$(basename "$jar")"
  if [[ "$base" == *serialization-compiler-plugin* || "$base" == junit-platform-console-standalone* ]]; then
    continue
  fi
  if [[ -z "$RUN_CP" ]]; then
    RUN_CP="$jar"
  else
    RUN_CP="$RUN_CP:$jar"
  fi
done

if [[ -z "$RUN_CP" ]]; then
  echo "No runtime classpath jars found in lib/" >&2
  exit 1
fi

export RUN_CP
