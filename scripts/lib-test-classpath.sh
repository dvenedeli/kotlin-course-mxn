#!/usr/bin/env bash
TEST_CP=""
for jar in lib/*.jar; do
  base="$(basename "$jar")"
  if [[ "$base" == *serialization-compiler-plugin* ]]; then
    continue
  fi
  if [[ -z "$TEST_CP" ]]; then
    TEST_CP="$jar"
  else
    TEST_CP="$TEST_CP:$jar"
  fi
done

if [[ -z "$TEST_CP" ]]; then
  echo "No test compile classpath jars found in lib/" >&2
  exit 1
fi

export TEST_CP
