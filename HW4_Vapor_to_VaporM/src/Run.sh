#!/bin/bash

RED='\033[0;31m'
YELLOW='\033[0;33m'
GREEN='\033[0;32m'
NC='\033[0m'

javac -classpath vapor-parser.jar:. V2VM.java || exit 1

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

for file in "$TEST_DIR"/valid/*.vapor; do
  if [ ! -e "$file" ]; then
    echo "No .vapor files found in $TEST_DIR"
    exit 1
  fi

  count=$((count + 1))
  base=$(basename "$file" .vapor)
  outfile="$TEST_DIR/out/$base.vaporm"

  echo "Running test: $file"

  java -classpath vapor-parser.jar:. V2VM < "$file" > "$outfile"

  givenfile="$TEST_DIR/out_given/$base.vaporm"
  if [ -f "$givenfile" ]; then
    if diff <(grep -v '^$' "$outfile") <(grep -v '^$' "$givenfile") > /dev/null 2>&1; then
      echo -e "${GREEN}PASS: $base matches expected output${NC}"
      echo "Running vapor program..."
      actual_output=$(java -jar vapor.jar run "$outfile" 2>/dev/null)
      expected_output=$(java -jar vapor.jar run "$givenfile" 2>/dev/null)
      echo "$actual_output"

      if [ "$actual_output" = "$expected_output" ]; then
        echo -e "${GREEN}RUNTIME PASS: output matches expected${NC}"
      else
        echo -e "${RED}RUNTIME FAIL: output does not match expected${NC}"
        echo "--- runtime differences ---"
        diff <(echo "$actual_output") <(echo "$expected_output") | while IFS= read -r line; do
          case "$line" in
            ">"*) echo -e "${YELLOW}$line${NC}" ;;
            "<"*) echo -e "${RED}$line${NC}" ;;
            *)    ;;
          esac
        done
      fi
    else
      echo -e "${RED}FAIL: $base does not match expected output${NC}"
      echo "--- differences ---"
      diff <(grep -v '^$' "$outfile") <(grep -v '^$' "$givenfile") | while IFS= read -r line; do
        case "$line" in
          ">"*) echo -e "${YELLOW}$line${NC}" ;;
          "<"*) echo -e "${RED}$line${NC}" ;;
          *)    ;;
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