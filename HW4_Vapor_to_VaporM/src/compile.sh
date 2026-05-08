#!/bin/bash
javac -classpath vapor-parser.jar:. V2VM.java
java -jar vapor.jar run tests/valid/Factorial.vapor

java -classpath vapor-parser.jar:. V2VM.java < tests/valid/Factorial.vapor > tests/out/Factorial.vaporm
java -jar vapor.jar run -mips tests/out/Factorial.vaporm