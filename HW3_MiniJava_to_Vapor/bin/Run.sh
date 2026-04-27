#!/bin/bash

RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m'

# Clear all .class files first
find . -name "*.class" -delete
echo "Cleared all .class files"

javac J2V.java || exit 1

TEST_DIR="tests"

if [ ! -d "$TEST_DIR" ]; then
  echo "Error: Directory '$TEST_DIR' does not exist."
  exit 1
fi

mkdir -p "$TEST_DIR/out"

count=0
echo "----------------------------------------------"
echo "Starting Translation Tests"
echo "----------------------------------------------"

for file in "$TEST_DIR"/*.java; do
  if [ ! -e "$file" ]; then
    echo "No .java files found in $TEST_DIR"
    exit 1
  fi

  count=$((count + 1))
  base=$(basename "$file" .java)
  outfile="$TEST_DIR/out/$base.vapor"

  echo "Running test: $file"

  java J2V < "$file" > "$outfile"

  givenfile="$TEST_DIR/out_given/$base.vapor"
  if [ -f "$givenfile" ]; then
    if diff -q "$outfile" "$givenfile" > /dev/null 2>&1; then
      echo "PASS: $base matches expected output"
    else
      echo "FAIL: $base does not match expected output"
      echo "--- differences ---"
      diff "$outfile" "$givenfile" | while IFS= read -r line; do
        case "$line" in
          ">"*) echo -e "${YELLOW}$line${NC}" ;;  # in expected but not in yours
          "<"*) echo -e "${RED}$line${NC}" ;;     # in yours but not in expected
          *)    ;;                                 # skip context lines
        esac
      done
    fi
  else
    echo "No expected output found for $base — skipping comparison"
  fi

  echo "Finished test: $file"
  echo "----------------------------------------------"
done

echo "Total tests run: $count"
# javac J2V.java || exit 1

# TEST_DIR="tests"

# if [ ! -d "$TEST_DIR" ]; then
#   echo "Error: Directory '$TEST_DIR' does not exist."
#   exit 1
# fi

# count=0

# for file in "$TEST_DIR"/*.java; do
#   if [ ! -e "$file" ]; then
#     echo "No .java files found in $TEST_DIR"
#     exit 1
#   fi

#   count=$((count + 1))
#   echo "Running test: $file"

#   # java J2V < "$file" > "${file%.java}.vapor"
#   java J2V < "$file" > "tests/out/$(basename "${file%.java}").vapor"

#   echo "Finished test: $file"
#   echo "----------------------------------------------"
# done

# echo "Total tests run: $count"
