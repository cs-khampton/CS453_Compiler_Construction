#!/bin/bash

# clean the .class files out first
./src/clean.sh

zip -r Kai_Hampton.zip src/tests src/MyType.java src/VMethod.java src/VTranslator.java src/VVisitor.java src/VClass.java src/STMethod.java src/STClass.java src/SymbolTable.java src/J2V.java