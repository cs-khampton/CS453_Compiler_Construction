#!/bin/bash

javac J2V.java || exit 1

TEST_DIR="tests"

if [ ! -d "$TEST_DIR" ]; then
  echo "Error: Directory '$TEST_DIR' does not exist."
  exit 1
fi

count=0

for file in "$TEST_DIR"/*.java; do
  if [ ! -e "$file" ]; then
    echo "No .java files found in $TEST_DIR"
    exit 1
  fi

  count=$((count + 1))
  echo "Running test: $file"

  # java J2V < "$file" > "${file%.java}.vapor"
  java J2V < "$file" > "tests/out/$(basename "${file%.java}").vapor"

  echo "Finished test: $file"
  echo "----------------------------------------------"
done

echo "Total tests run: $count"