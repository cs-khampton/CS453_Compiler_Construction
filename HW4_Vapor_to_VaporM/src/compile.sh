#!/bin/bash
javac -classpath vapor-parser.jar:. V2VM.java
java -classpath vapor-parser.jar:. V2VM.java < tests/valid/Factorial.vapor