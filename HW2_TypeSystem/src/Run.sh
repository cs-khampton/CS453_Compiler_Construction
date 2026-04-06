#!/bin/bash
javac Typecheck.java
# Directory containing test files
TEST_DIR="tests"

# Check if directory exists
if [ ! -d "$TEST_DIR" ]; then
  echo "Error: Directory '$TEST_DIR' does not exist."
  exit 1
fi

# Loop through all .jj files in the directory
for file in "$TEST_DIR"/*.jj; do
  # Check if any .jj files exist
  if [ ! -e "$file" ]; then
    echo "No .jj files found in $TEST_DIR"
    exit 1
  fi

  echo "Running test: $file"
  java Typecheck < "$file"
  echo "----------------------------------------------"
done