#!/bin/bash

RED='\033[0;31m'
YELLOW='\033[0;33m'
GREEN='\033[0;32m'
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
    if diff <(grep -v '^$' "$outfile") <(grep -v '^$' "$givenfile") > /dev/null 2>&1; then
      echo -e "${GREEN}PASS: $base matches expected output${NC}"

      # run vapor output and compare
      actual_output=$(java -jar vapor.jar run "$outfile" 2>&1)
      expected_output=$(java -jar vapor.jar run "$givenfile" 2>&1)

      if [ "$actual_output" = "$expected_output" ]; then
        echo -e "${GREEN}RUNTIME PASS: $base runtime output matches${NC}"
      else
        echo -e "${RED}RUNTIME FAIL: $base runtime output differs${NC}"
        echo "--- runtime differences ---"
        diff <(echo "$actual_output") <(echo "$expected_output") | while read -r line; do
          case "${line:0:1}" in
            "<") echo -e "${RED}$line${NC}" ;;
            ">") echo -e "${YELLOW}$line${NC}" ;;
            *) ;;
          esac
        done
      fi
    else
      echo -e "${RED}FAIL: $base does not match expected output${NC}"
      echo "--- differences ---"
      diff <(grep -v '^$' "$outfile") <(grep -v '^$' "$givenfile") | while read -r line; do
        case "${line:0:1}" in
          "<") echo -e "${RED}$line${NC}" ;;
          ">") echo -e "${YELLOW}$line${NC}" ;;
          *) ;;
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