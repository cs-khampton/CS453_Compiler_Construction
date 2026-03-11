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

## BNF For MiniJava
<iframe src="https://www.cs.colostate.edu/~pouchet/classes/CS453/hw25-minijava/cs453/mj/minijava.html" width="100%" height="500px" title="MiniJava BNF"></iframe>


## Pretty Printing
```
class Factorial{
    public static void main(String[] a){
        int x;
        x = 1+3;
    }
}
```
### Output
```
Goal
    MainClass
    NodeToken => class
    Identifier
        NodeToken => Factorial
    NodeToken => {
    NodeToken => public
    NodeToken => static
    NodeToken => void
    NodeToken => main
    NodeToken => (
    NodeToken => String
    NodeToken => [
    NodeToken => ]
    Identifier
        NodeToken => a
    NodeToken => )
    NodeToken => {
    VarDeclaration
        Type
            IntegerType
                NodeToken => int
        Identifier
            NodeToken => x
        NodeToken => ;
        Statement
            AssignmentStatement
                Identifier
                    NodeToken => x
                NodeToken => =
                Expression
                    PlusExpression
                        PrimaryExpression
                            IntegerLiteral
                                NodeToken => 1
                        NodeToken => +
                        PrimaryExpression
                            IntegerLiteral
                                NodeToken => 3
                NodeToken => ;
        NodeToken => }
        NodeToken => }
    NodeToken => 

```