#!/bin/bash

RED='\033[0;31m'
YELLOW='\033[0;33m'
GREEN='\033[0;32m'
NC='\033[0m'

javac -classpath vapor-parser.jar:. V2VM.java VMTranslator.java VMVisitor.java VMSymbolTable.java || exit 1

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

  # run original vapor
  vapor_output=$(java -jar vapor.jar run "$file" 2>&1)

  # translate to vaporm
  java -classpath vapor-parser.jar:. V2VM < "$file" > "$outfile"

  # run vaporm
  vaporm_output=$(java -jar vapor.jar run -mips "$outfile" 2>&1)

  # compare outputs
  if [ "$vapor_output" = "$vaporm_output" ]; then
    echo -e "${GREEN}PASS: $base outputs match${NC}"
  else
    echo -e "${RED}FAIL: $base outputs differ${NC}"
    echo "--- differences ---"
    diff <(echo "$vapor_output") <(echo "$vaporm_output") | while read -r line; do
      case "${line:0:1}" in
        "<") echo -e "${RED}$line${NC}" ;;
        ">") echo -e "${YELLOW}$line${NC}" ;;
        *) ;;
      esac
    done
  fi

  echo "Finished test: $file"
  echo "----------------------------------------------"
done

echo "Total tests run: $count"