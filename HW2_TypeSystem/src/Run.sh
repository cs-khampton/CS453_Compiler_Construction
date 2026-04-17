#!/bin/bash

javac Typecheck.java || exit 1

TEST_DIR="tests"

if [ ! -d "$TEST_DIR" ]; then
  echo "Error: Directory '$TEST_DIR' does not exist."
  exit 1
fi

count=0
echo "----------------------------------------------"
echo "Starting Valid Tests"
echo "----------------------------------------------"

for file in "$TEST_DIR"/valid/*.java; do
  if [ ! -e "$file" ]; then
    echo "No .jj files found in $TEST_DIR"
    exit 1
  fi

  count=$((count + 1))
  echo "Running test: $file"

  java Typecheck < "$file"

  echo "Finished test: $file"
  echo "----------------------------------------------"
done
echo "----------------------------------------------"
echo "Starting Invalid Tests"
echo "----------------------------------------------"

for file in "$TEST_DIR"/invalid/*.java; do
  if [ ! -e "$file" ]; then
    echo "No .jj files found in $TEST_DIR"
    exit 0;
  fi

  count=$((count + 1))
  echo "Running test: $file"

  java Typecheck < "$file"

  echo "Finished test: $file"
  echo "----------------------------------------------"
done

echo "Total tests run: $count"