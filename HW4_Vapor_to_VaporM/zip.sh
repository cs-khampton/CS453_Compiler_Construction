#!/bin/bash

# clean the .class files out first
./src/clean.sh

zip -r Kai_Hampton.zip src/tests src/V2VM.java src/VMSymbolTable.java src/VMTranslator.java src/VMVisitor.java