#!/bin/bash

javac J2V.java || exit 1

TEST_DIR="tests"

if [ ! -d "$TEST_DIR" ]; then
  echo "Error: Directory '$TEST_DIR' does not exist."
  exit 1
fi

count=0

for file in "$TEST_DIR"/*.jj; do
  if [ ! -e "$file" ]; then
    echo "No .jj files found in $TEST_DIR"
    exit 1
  fi

  count=$((count + 1))
  echo "Running test: $file"

  # java J2V < "$file" > "${file%.jj}.vapor"
  java J2V < "$file" > "tests/out/$(basename "${file%.jj}").vapor"

  echo "Finished test: $file"
  echo "----------------------------------------------"
done

echo "Total tests run: $count"