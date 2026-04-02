# Homework 2: Type checking of MiniJava

Use JTB and JavaCC and write in Java one or more visitors that type check a MiniJava program. Follow the Minijava type system for your homework implementation.

Your main file should be called Typecheck.java, and if P.java contains a program to be type checked, then:

`java Typecheck < P.java`

should print either "Program type checked successfully" or "Type error".


## Setup
```unzip javacc-6.0.zip
java -jar jtb132.jar minijava.jj
java -cp javacc-6.0/bin/lib/javacc.jar javacc jtb.out.jj
```

### Test Cases to Check for
- [ ] Cycles in class (class A extends B, class B extends A)

- [ ] Cycles in method

- [ ] Multiple Class Declaration

- [ ] Multiple Method Declaration

- [ ] Forward Declaration (class A extends B, class B extends C)


## [BNF for MiniJava](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html)

## [MiniJava TypeSystem](https://cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/doc/miniJava-typesystem.pdf)

## Tests
T0_Fac.jj -- Should pass
T1_Factorial.jj -- Should pass
T2_AddInts.jj
T3_.jj
T4_.jj
T5_EmptyClass.jj -- Should Pass?
T6_Disc.jj -- Should Pass

- Use FIRST/FOLLOW sets to type check

|  | NULL | FIRST | FOLLOW |
| --- | --- | --- | --- |
| Goal | FALSE | [“class”] | \<EOF> |
| MainClass | FALSE | [“class”] | [“class”, \<EOF>] |
| TypeDeclaration | TRUE | [“class”] | [”class”, \<EOF>] |
| ClassDeclaration |  | [”class”] | [”class”, \<EOF>] |
| ClassExtendsDeclaration |  | [”class”] | [”class”, \<EOF>] |
| VarDeclaration |  | [“boolean”, “int”, \<IDENTIFIER] | [”boolean”, “int”, "{”, \<IDENTIFIER, “if” “while”, “System.out.println”, “public”, “}”] |
| MethodDeclaration |  | [“public”] | [”public”, ”}”] |
| FormalParameterList |  | [”“boolean”, “int”, \<IDENTIFIER] | [”)”] |
| FormalParameter |  | [”boolean”, “int”, \<IDENTIFIER] | [”,”, “)”] |
| FormalParameterRest |  | [”,”] | [”,”, “)”] |
| Type |  | [”“boolean”, “int”, \<IDENTIFIER] | [\<IDENTIFIER>] |
| ArrayType |  | [”int”] | [\<IDENTIFIER>] |
| BooleanType |  | [”boolean”] | [\<IDENTIFIER>] |
| IntegerType |  | [”int”] | [\<IDENTIFIER>] |
| Statement |  | ["{”, <IDENTIFIER>, “if” “while”, “System.out.println”] | ["{”, <IDENTIFIER>, “if” “while”, “System.out.println”, “}”, “return”, “else”] |
| Block |  | [“{”] | ["{”, \<IDENTIFIER>, “if” “while”, “System.out.println”, “}”, “return”, “else”] |
| AssignmentStatement |  | [\<IDENTIFIER>] | ["{”, \<IDENTIFIER>, “if” “while”, “System.out.println”, “}”, “return”, “else”] |
| ArrayAssignmentStatement |  | [\<IDENTIFIER>] | ["{”, \<IDENTIFIER>, “if” “while”, “System.out.println”, “}”, “return”, “else”] |
| IfStatement |  | [“if”] | ["{”, \<IDENTIFIER>, “if” “while”, “System.out.println”, “}”, “return”, “else”] |
| WhileStatement |  | [“while”] | ["{”, \<IDENTIFIER>, “if” “while”, “System.out.println”, “}”, “return”, “else”] |
| PrintStatement |  | "System.out.println” | ["{”, \<IDENTIFIER, “if” “while”, “System.out.println”, “}”, “return”, “else”] |
| Expression |  | [\<INTEGER_LITERAL>, “true”, “false”, \<IDENTIFIER, “this”, “new”, “!”, “(”] | [”;”, “]”, “)”, “,”] |
| AndExpression |  | [\<INTEGER_LITERAL>, “true”, “false”, \<IDENTIFIER, “this”, “new”, “!”, “(”] | [”;”, “]”, “)”, “,”] |
| CompareExpression |  | [\<INTEGER_LITERAL>, “true”, “false”, \<IDENTIFIER, “this”, “new”, “!”, “(”] | [”;”, “]”, “)”, “,”] |
| PlusExpression |  | [\<INTEGER_LITERAL>, “true”, “false”, \<IDENTIFIER, “this”, “new”, “!”, “(”] | [”;”, “]”, “)”, “,”] |
| MinusExpression |  | [\<INTEGER_LITERAL>, “true”, “false”, \<IDENTIFIER, “this”, “new”, “!”, “(”] | [”;”, “]”, “)”, “,”] |
| TimesExpression |  | [\<INTEGER_LITERAL>, “true”, “false”, \<IDENTIFIER, “this”, “new”, “!”, “(”] | [”;”, “]”, “)”, “,”] |
| ArrayLookup |  | [\<INTEGER_LITERAL>, “true”, “false”, \<IDENTIFIER, “this”, “new”, “!”, “(”] | [”;”, “]”, “)”, “,”] |
| ArrayLength |  | [\<INTEGER_LITERAL>, “true”, “false”, \<IDENTIFIER, “this”, “new”, “!”, “(”] | [”;”, “]”, “)”, “,”] |
| MessageSend |  | [\<INTEGER_LITERAL>, “true”, “false”, \<IDENTIFIER, “this”, “new”, “!”, “(”] | [”;”, “]”, “)”, “,”] |
| ExpressionList |  | [\<INTEGER_LITERAL>, “true”, “false”, \<IDENTIFIER, “this”, “new”, “!”, “(”] | [”;”, “]”, “)”, “,”] |
| ExpressionRest |  | [",”] | [”)”] |
| PrimaryExpression |  | [\<INTEGER_LITERAL>, “true”, “false”, \<IDENTIFIER, “this”, “new”, “!”, “(”] | [”;”, “]”, “)”, “,”, “&&”, “<”, “+”, “-”, “*”, “.”, “[” ] |
| IntegerLiteral |  | [\<INTEGER_LITERAL>] | [”;”, “]”, “)”, “,”, “&&”, “<”, “+”, “-”, “*”, “.”, “[” ] |
| TrueLiteral |  | [“true”] | [”;”, “]”, “)”, “,”, “&&”, “<”, “+”, “-”, “*”, “.”, “[” ] |
| FalseLiteral |  | ["false”] | [”;”, “]”, “)”, “,”, “&&”, “<”, “+”, “-”, “*”, “.”, “[” ] |
| Identifier |  | [\<IDENTIFIER] | [ “{”, “)”, “extends”, “;”, “,”, “(”, “=”, “[”, “&&”, “<”, “+”, “-”, “*”, “]”, “.”] |
| ThisExpression |  | [“this”] | [”;”, “]”, “)”, “,”, “&&”, “<”, “+”, “-”, “*”, “.”, “[” ] |
| ArrayAllocationExpression |  | [”new”] | [”;”, “]”, “)”, “,”, “&&”, “<”, “+”, “-”, “*”, “.”, “[” ] |
| AllocationExpression |  | [“new”] | [”;”, “]”, “)”, “,”, “&&”, “<”, “+”, “-”, “*”, “.”, “[” ] |
| NotExpression |  | [”!”] | [”;”, “]”, “)”, “,”, “&&”, “<”, “+”, “-”, “*”, “.”, “[” ] |
| BracketExpression |  | ["(”] | [”;”, “]”, “)”, “,”, “&&”, “<”, “+”, “-”, “*”, “.”, “[” ] |