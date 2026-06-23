#!/bin/bash

RED='\033[0;31m'
YELLOW='\033[0;33m'
GREEN='\033[0;32m'
NC='\033[0m'

javac -classpath vapor-parser.jar:. VM2M.java MTranslator.java MVisitor.java || exit 1

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

for file in "$TEST_DIR"/valid/*.vaporm; do
  if [ ! -e "$file" ]; then
    echo "No .vaporm files found in $TEST_DIR/valid"
    exit 1
  fi

  count=$((count + 1))
  base=$(basename "$file" .vaporm)
  outfile="$TEST_DIR/out/$base.s"

  echo "Running test: $file"

  java -classpath vapor-parser.jar:. VM2M < "$file" > "$outfile"

  # run original vaporm
  vaporm_output=$(java -jar vapor.jar run -mips "$file" 2>&1)

  # run mips output with mars
  mips_output=$(java -jar mars.jar nc "$outfile" 2>&1)

  if [ "$vaporm_output" = "$mips_output" ]; then
    echo -e "${GREEN}PASS: $base outputs match${NC}"
    echo "$mips_output"
  else
    echo -e "${RED}FAIL: $base outputs differ${NC}"
    echo "--- differences ---"
    diff <(echo "$vaporm_output") <(echo "$mips_output") | while read -r line; do
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