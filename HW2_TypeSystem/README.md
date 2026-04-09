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

## [BNF for MiniJava](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html)

## [MiniJava TypeSystem](https://cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/doc/miniJava-typesystem.pdf)

| Rule# | Non-Terminal | Production(s) |
| --- | --- | --- |
| 0 | Goal | [MainClass](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod2) ( [TypeDeclaration](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod3) )* \<EOF> |
| 1 | MainClass | "class" [Identifier](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod4) "{" "public" "static" "void" "main" "(" "String" "[" "]" [Identifier](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod4) ")" "{" ( [VarDeclaration](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod5) )* ( [Statement](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod6) )* "}" "}" |
| 2 | TypeDeclaration | ClassDeclaration |
| 3 |  | ClassExtendsDeclaration |
| 4 | ClassDeclaration | "class" [Identifier](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod4) "{" ( [VarDeclaration](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod5) )* ( [MethodDeclaration](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod9) )* "}" |
| 5 | ClassExtendsDeclaration | "class" [Identifier](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod4) "extends" [Identifier](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod4) "{" ( [VarDeclaration](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod5) )* ( [MethodDeclaration](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod9) )* "}" |
| 6 | VarDeclaration | [Type](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod10) [Identifier](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod4) ";" |
| 7 | MethodDeclaration | "public" [Type](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod10) [Identifier](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod4) "(" ( [FormalParameterList](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod11) )? ")" "{" ( [VarDeclaration](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod5) )* ( [Statement](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod6) )* "return" [Expression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod12) ";" "}" |
| 8 | FormalParameterList | [FormalParameter](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod13) ( [FormalParameterRest](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod14) )* |
| 9 | FormalParameter | [Type](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod10) [Identifier](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod4) |
| 10 | FormalParameterRest | "," [FormalParameter](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod13) |
| 11 | Type | [ArrayType](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod15) |
| 12 |  | [BooleanType](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod16) |
| 13 |  | [IntegerType](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod17) |
| 14 |  | [Identifier](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod4) |
| 15 | ArrayType | “int” “[” “]” |
| 16 | BooleanType | “boolean” |
| 17 | IntegerType | "int” |
| 18 | Statement | [Block](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod18) |
| 19 |  | [AssignmentStatement](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod19) |
| 20 |  | [ArrayAssignmentStatement](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod20) |
| 21 |  | [IfStatement](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod21) |
| 22 |  | [WhileStatement](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod22) |
| 23 |  | [PrintStatement](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod23) |
| 24 | Block | "{" ( [Statement](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod6) )* "}" |
| 25 | AssignmentStatement | [Identifier](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod4) "=" [Expression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod12) ";" |
| 26 | ArrayAssignmentStatement | [Identifier](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod4) "[" [Expression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod12) "]" "=" [Expression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod12) ";" |
| 27 | IfStatement | "if" "(" [Expression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod12) ")" [Statement](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod6) "else" [Statement](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod6) |
| 28 | WhileStatement | "while" "(" [Expression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod12) ")" [Statement](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod6) |
| 29 | PrintStatement | "System.out.println" "(" [Expression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod12) ")" ";" |
| 30 | Expression | [AndExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod24) |
| 31 |  | [CompareExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod25) |
| 32 |  | [PlusExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod26) |
| 33 |  | [MinusExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod27) |
| 34 |  | [TimesExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod28) |
| 35 |  | [ArrayLookup](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod29) |
| 36 |  | [ArrayLength](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod30) |
| 37 |  | [MessageSend](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod31) |
| 38 |  | [PrimaryExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod32) |
| 39 | AndExpression | [PrimaryExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod32) "&&" [PrimaryExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod32) |
| 40 | CompareExpression | [PrimaryExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod32) "<" [PrimaryExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod32) |
| 41 | PlusExpression | [PrimaryExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod32) "+" [PrimaryExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod32) |
| 42 | MinusExpression | [PrimaryExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod32) "-" [PrimaryExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod32) |
| 43 | TimesExpression | [PrimaryExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod32) "*" [PrimaryExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod32) |
| 44 | ArrayLookup | [PrimaryExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod32) "[" [PrimaryExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod32) “]” |
| 45 | ArrayLength | [PrimaryExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod32) "." "length" |
| 46 | MessageSend | [PrimaryExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod32) "." [Identifier](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod4) "(" ( [ExpressionList](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod33) )? ")" |
| 47 | ExpressionList | [Expression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod12) ( [ExpressionRest](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod34) )* |
| 48 | ExpressionRest | "," [Expression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod12) |
| 49 | PrimaryExpression | [IntegerLiteral](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod35) |
| 50 |  | [TrueLiteral](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod36) |
| 51 |  | [FalseLiteral](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod37) |
| 52 |  | [Identifier](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod4) |
| 53 |  | [ThisExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod38) |
| 54 |  | [ArrayAllocationExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod39) |
| 55 |  | [AllocationExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod40) |
| 56 |  | [NotExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod41) |
| 57 |  | [BracketExpression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod42) |
| 58 | IntegerLiteral | \<INTEGER_LITERAL> |
| 59 | TrueLiteral | “true” |
| 60 | FalseLiteral | “false” |
| 61 | Identifier | \<IDENTIFIER> |
| 62 | ThisExpression | "this” |
| 63 | ArrayAllocationExpression | "new" "int" "[" [Expression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod12) "]" |
| 64 | AllocationExpression | "new" [Identifier](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod4) "(" ")" |
| 65 | NotExpression | "!" [Expression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod12) |
| 66 | BracketExpression | "(" [Expression](https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html#prod12) ")" |


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