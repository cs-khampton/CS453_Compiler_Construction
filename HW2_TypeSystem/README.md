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

## BNF for MiniJava

## MiniJava TypeSystem

| Rule# | Non-Terminal | Production(s) |
| --- | --- | --- |
| 0 | Goal |  MainClass ( TypeDeclaration )* \<EOF> |
| 1 | MainClass | "class" Identifier "{" "public" "static" "void" "main" "(" "String" "[" "]" Identifier ")" "{" ( VarDeclaration() )* ( Statement )* "}" "}" |
| 2 | TypeDeclaration | ClassDeclaration |
| 3 |  | ClassExtendsDeclaration |
| 4 | ClassDeclaration | "class" Identifier "{" ( VarDeclaration )* ( MethodDeclaration )* "}" |
| 5 | ClassExtendsDeclaration | "class" Identifier "extends" Identifier "{" ( VarDeclaration )* ( MethodDeclaration )* "}" |
| 6 | VarDeclaration | Type Identifier ";" |
| 7 | MethodDeclaration | "public" Type Identifier "(" ( FormalParameterList )? ")" "{" ( VarDeclaration )* ( Statement )* "return" Expression ";" "}" |
| 8 | FormalParameterList | FormalParameter ( FormalParameterRest )* |
| 9 | FormalParameter | Type Identifier |
| 10 | FormalParameterRest | "," FormalParameter |
| 11 | Type | ArrayType |
| 12 |  | BooleanType |
| 13 |  | IntegerType |
| 14 |  | Identifier |
| 15 | ArrayType | “int” “[” “]” |
| 16 | BooleanType | “boolean” |
| 17 | IntegerType | "int” |
| 18 | Statement | Block |
| 19 |  | AssignmentStatement |
| 20 |  | ArrayAssignmentStatement |
| 21 |  | [IfStatement |
| 22 |  | WhileStatement |
| 23 |  | PrintStatement |
| 24 | Block | "{" ( Statement )* "}" |
| 25 | AssignmentStatement | Identifier "=" Expression ";" |
| 26 | ArrayAssignmentStatement | Identifier "[" Expression "]" "=" Expression ";" |
| 27 | IfStatement | "if" "(" Expression ")" Statement "else" Statement |
| 28 | WhileStatement | "while" "(" Expression ")" Statement |
| 29 | PrintStatement | "System.out.println" "(" Expression ")" ";" |
| 30 | Expression | AndExpression |
| 31 |  | CompareExpression |
| 32 |  | PlusExpression |
| 33 |  | MinusExpression |
| 34 |  | TimesExpression |
| 35 |  | ArrayLookup |
| 36 |  | ArrayLength |
| 37 |  | MessageSend |
| 38 |  | PrimaryExpression |
| 39 | AndExpression | PrimaryExpression "&&" PrimaryExpression |
| 40 | CompareExpression | PrimaryExpression "<" PrimaryExpression |
| 41 | PlusExpression | PrimaryExpression "+" PrimaryExpression |
| 42 | MinusExpression | PrimaryExpression "-" PrimaryExpression |
| 43 | TimesExpression | PrimaryExpression "*" PrimaryExpression |
| 44 | ArrayLookup | PrimaryExpression "[" PrimaryExpression “]” |
| 45 | ArrayLength | PrimaryExpression "." "length" |
| 46 | MessageSend | PrimaryExpression "." Identifier "(" ( ExpressionList )? ")" |
| 47 | ExpressionList | Expression ( ExpressionRest )* |
| 48 | ExpressionRest | "," Expression |
| 49 | PrimaryExpression | IntegerLiteral |
| 50 |  | TrueLiteral |
| 51 |  | FalseLiteral |
| 52 |  | Identifier |
| 53 |  | ThisExpression |
| 54 |  | ArrayAllocationExpression |
| 55 |  | AllocationExpression |
| 56 |  | NotExpression |
| 57 |  | BracketExpression |
| 58 | IntegerLiteral | \<INTEGER_LITERAL> |
| 59 | TrueLiteral | “true” |
| 60 | FalseLiteral | “false” |
| 61 | Identifier | \<IDENTIFIER> |
| 62 | ThisExpression | "this” |
| 63 | ArrayAllocationExpression | "new" "int" "[" Expression "]" |
| 64 | AllocationExpression | "new" Identifier "(" ")" |
| 65 | NotExpression | "!" Expression |
| 66 | BracketExpression | "(" Expression ")" |


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
| Statement |  | ["{”, \<IDENTIFIER>, “if” “while”, “System.out.println”] | ["{”, \<IDENTIFIER>, “if” “while”, “System.out.println”, “}”, “return”, “else”] |
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