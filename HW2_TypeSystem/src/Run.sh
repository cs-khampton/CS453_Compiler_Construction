#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

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
    echo "No .java files found in $TEST_DIR"
    exit 1
  fi

  count=$((count + 1))
  echo "Running test: $file"

  output=$(java Typecheck < "$file")

  if echo "$output" | grep -q "Type error"; then
    echo -e "${RED}$output${NC}"
  else
    echo -e "${GREEN}$output${NC}"
  fi

  echo "Finished test: $file"
  echo "----------------------------------------------"
done

echo "----------------------------------------------"
echo "Starting Invalid Tests"
echo "----------------------------------------------"

for file in "$TEST_DIR"/invalid/*.java; do
  if [ ! -e "$file" ]; then
    echo "No .java files found in $TEST_DIR"
    exit 0
  fi

  count=$((count + 1))
  echo "Running test: $file"

  output=$(java Typecheck < "$file")

  if echo "$output" | grep -q "Program type checked successfully"; then
    echo -e "${RED}$output${NC}"
  else
    echo -e "${GREEN}$output${NC}"
  fi

  echo "Finished test: $file"
  echo "----------------------------------------------"
done

echo "Total tests run: $count"