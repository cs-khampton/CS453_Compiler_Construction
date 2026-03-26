import java.util.HashMap;
import java.util.Map;

import syntaxtree.AllocationExpression;
import syntaxtree.AndExpression;
import syntaxtree.ArrayAllocationExpression;
import syntaxtree.ArrayAssignmentStatement;
import syntaxtree.ArrayLength;
import syntaxtree.ArrayLookup;
import syntaxtree.ArrayType;
import syntaxtree.AssignmentStatement;
import syntaxtree.Block;
import syntaxtree.BooleanType;
import syntaxtree.BracketExpression;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.CompareExpression;
import syntaxtree.Expression;
import syntaxtree.ExpressionList;
import syntaxtree.ExpressionRest;
import syntaxtree.FalseLiteral;
import syntaxtree.FormalParameter;
import syntaxtree.FormalParameterList;
import syntaxtree.FormalParameterRest;
import syntaxtree.Goal;
import syntaxtree.Identifier;
import syntaxtree.IfStatement;
import syntaxtree.IntegerLiteral;
import syntaxtree.IntegerType;
import syntaxtree.MainClass;
import syntaxtree.MessageSend;
import syntaxtree.MethodDeclaration;
import syntaxtree.MinusExpression;
import syntaxtree.NotExpression;
import syntaxtree.PlusExpression;
import syntaxtree.PrimaryExpression;
import syntaxtree.PrintStatement;
import syntaxtree.Statement;
import syntaxtree.ThisExpression;
import syntaxtree.TimesExpression;
import syntaxtree.TrueLiteral;
import syntaxtree.Type;
import syntaxtree.TypeDeclaration;
import syntaxtree.VarDeclaration;
import syntaxtree.WhileStatement;
import visitor.GJDepthFirst;

public class SymbolTable<R, A> extends GJDepthFirst<R, A> {

    Map<String, STClass> classes = new HashMap<>();
    private STClass currClass = null;
    private STMethod currMethod = null;

    /*
     * Goal
     * f0: MainClass()
     * f1: TypeDeclaration()*
     * f2: <EOF>
     */
    public R visit(Goal n, A arg) {
        n.f0.accept(this, arg); // MainClass()
        n.f1.accept(this, arg); // TypeDeclaration()*
        // <EOF> -- doesn't need to be added to symbol table
        return null;
    }

    /*
     * MainClass
     * f0: "class"
     * f1: Identifier()
     * f2: "{"
     * f3: "public"
     * f4: "static"
     * f5: "void"
     * f6: "main"
     * f7: "("
     * f8: "String"
     * f9: "["
     * f10: "]"
     * f11: Identifier()
     * f12: ")"
     * f13: "{"
     * f14: VarDeclaration()*
     * f15: Statement()*
     * f16: "}"
     * f17: "}"
     */
    public R visit(MainClass mc, A arg) {
        String className = mc.f1.f0.toString();
        STClass c = new STClass(className, null);
        classes.put(className, c);
        currClass = c;

        String returnType = mc.f5.toString();
        String name = mc.f6.toString();
        STMethod m = new STMethod(name, returnType);
        return null;
    }

    /*
     * TypeDeclaration
     * f0: ClassDeclaration() | ClassExtendsDeclaration()
     */
    public R visit(TypeDeclaration d, A arg) {
        return null;
    }

    /*
     * ClassDeclaration
     * f0: "class"
     * f1: Identifier()
     * f2: "{"
     * f3: VarDeclaration()*
     * f4: MethodDeclaration()*
     * f5: "}"
     */
    public R visit(ClassDeclaration cd, A arg) {

        return null;
    }

    /*
     * ClassExtendsDeclaration
     * f0: "class"
     * f1: Identifier()
     * f2: "extends"
     * f3: Identifier()
     * f4: "{"
     * f5: VarDeclarataion()*
     * f6: MethodDeclaration()*
     * f7: "}"
     */
    public R visit(ClassExtendsDeclaration ced, A arg) {

        return null;
    }

    /*
     * VarDeclaration
     * f0: Type()
     * f1: Identifier()
     * f2: ";"
     */
    public R visit(VarDeclaration vd, A arg) {

        return null;
    }

    /*
     * MethodDeclaration
     * f0: "public"
     * f1: Type()
     * f2: Identifier()
     * f3: "("
     * f4: FormalParameterList()?
     * f5: ")"
     * f6: "{"
     * f7: VarDeclaration()*
     * f8: Statement()*
     * f9: "return"
     * f10: Expression()
     * f11: ";"
     * f12: "}"
     */
    public R visit(MethodDeclaration md, A arg) {

        return null;
    }

    /*
     * FormalParameterList
     * f0: FormalParameter()
     * f1: FormalParameterRest()*
     */
    public R visit(FormalParameterList fpl, A arg) {

        return null;
    }

    /*
     * FormalParameter
     * f0: Type()
     * f1: Identifier()
     */
    public R visit(FormalParameter fp, A arg) {

        return null;
    }

    /*
     * FormalParameterRest
     * f0: ","
     * f1: FormalParameter()
     */
    public R visit(FormalParameterRest fpr, A arg) {

        return null;
    }

    /*
     * Type
     * f0: ArrayType() | BooleanType() | Identifier()
     */
    public R visit(Type t, A arg) {

        return null;
    }

    /*
     * ArrayType
     * f0: "int"
     * f1: "["
     * f2: "]"
     */
    public R visit(ArrayType at, A arg) {

        return null;
    }

    /*
     * BooleanType
     * f0: "boolean"
     */
    public R visit(BooleanType bt, A arg) {

        return null;
    }

    /*
     * IntegerType
     * f0: "int"
     */
    public R visit(IntegerType it, A arg) {

        return null;
    }

    /*
     * Statement
     * f0: Block() | AssignmentStatement() | ArrayAssignmentStatement() |
     * IfStatement() | WhileStatement() | PrintStatement()
     */
    public R visit(Statement s, A arg) {

        return null;
    }

    /*
     * Block
     * f0: "{"
     * f1: Statement()*
     * f2: "}"
     */
    public R visit(Block b, A arg) {

        return null;
    }

    /*
     * AssignmentStatement
     * f0: Identifier()
     * f1: "="
     * f2: Expression()
     * f3: ";"
     */
    public R visit(AssignmentStatement as, A arg) {

        return null;
    }

    /*
     * ArrayAssignmentStatement
     * f0: Idenfiier()
     * f1: "["
     * f2: Expression()
     * f3: "]"
     * f4: "="
     * f5: Expression()
     * f6: ";"
     */
    public R visit(ArrayAssignmentStatement aas, A arg) {

        return null;
    }

    /*
     * IfStatement
     * f0: "if"
     * f1: "("
     * f2: Expression()
     * f3: ")"
     * f4: Statement()
     * f5: "else"
     * f6: Statement()
     */
    public R visit(IfStatement is, A arg) {

        return null;
    }

    /*
     * WhileStatement
     * f0: "while"
     * f1: "("
     * f2: Expression()
     * f3: ")"
     * f4: Statement()
     */
    public R visit(WhileStatement ws, A arg) {

        return null;
    }

    /*
     * PrintStatement
     * f0: "System.out.println"
     * f1: "("
     * f2: Expression()
     * f3: ")"
     * f4: ";"
     */
    public R visit(PrintStatement ps, A arg) {

        return null;
    }

    /*
     * Expression
     * f0: AndExpression() | CompareExpression() | PlusExpression() |
     * MinusExpression() | TimesExpression() | ArrayLookup() | ArrayLength() |
     * MessageSend() | PrimaryExpression()
     */
    public R visit(Expression e, A arg) {

        return null;
    }

    /*
     * AndExpression
     * f0: PrimaryExpression()
     * f1: "&&"
     * f2: PrimaryExpression()
     */
    public R visit(AndExpression ae, A arg) {

        return null;
    }

    /*
     * CompareExpression
     * f0: PrimaryExpression()
     * f1: "<"
     * f2: PrimaryExpression()
     */
    public R visit(CompareExpression ce, A arg) {

        return null;
    }

    /*
     * PlusExpression
     * f0: PrimaryExpression()
     * f1: "+"
     * f2: PrimaryExpression()
     */
    public R visit(PlusExpression pe, A arg) {

        return null;
    }

    /*
     * MinusExpression
     * f0: PrimaryExpression()
     * f1: "-"
     * f2: PrimaryExpression()
     */
    public R visit(MinusExpression me, A arg) {

        return null;
    }

    /*
     * TimesExpression
     * f0: PrimaryExpression()
     * f1: "*"
     * f2: PrimaryExpression()
     */
    public R visit(TimesExpression te, A arg) {

        return null;
    }

    /*
     * ArrayLookup
     * f0: PrimaryExpression()
     * f1: "["
     * f2: PrimaryExpression()
     * f3: "]"
     */
    public R visit(ArrayLookup al, A arg) {

        return null;
    }

    /*
     * ArrayLength
     * f0: PrimaryExpression()
     * f1: "."
     * f2: "length"
     */
    public R visit(ArrayLength al, A arg) {

        return null;
    }

    /*
     * MessageSend
     * f0: PrimaryExpression()
     * f1: "."
     * f2: Identifier()
     * f3: "("
     * f4: ExpressionList()?
     * f5: ")"
     */
    public R visit(MessageSend ms, A arg) {

        return null;
    }

    /*
     * ExpressionList
     * f0: Expression()
     * f1: ExpressionRest()
     */
    public R visit(ExpressionList el, A arg) {

        return null;
    }

    /*
     * ExpressionRest
     * f0: ","
     * f1: Expression()
     */
    public R visit(ExpressionRest er, A arg) {

        return null;
    }

    /*
     * PrimaryExpression()
     * f0: IntegerLiteral() | TrueLiteral() | FalseLiteral() | Identifier() |
     * ThisExpression() | ArrayAllocationExpression() | AllocationExpression() |
     * NotExpression() | BracketExpression()
     */
    public R visit(PrimaryExpression pe, A arg) {

        return null;
    }

    /*
     * IntegerLiteral
     * f0: <INTEGER_LITERAL>
     */
    public R visit(IntegerLiteral il, A arg) {

        return null;
    }

    /*
     * TrueLiteral
     * f0: "true"
     */
    public R visit(TrueLiteral tl, A arg) {

        return null;
    }

    /*
     * FalseLiteral
     * f0: "false"
     */ public R visit(FalseLiteral fl, A arg) {

        return null;
    }

    /*
     * Identifier
     * f0: <IDENTIFIER>
     */
    public R visit(Identifier id, A arg) {

        return null;
    }

    /*
     * ThisExpression
     * f0: "this"
     */
    public R visit(ThisExpression te, A arg) {

        return null;
    }

    /*
     * ArrayAllocationExpression
     * f0: "new"
     * f1: "int"
     * f2: "["
     * f3: Expression()
     * f4: "]"
     */
    public R visit(ArrayAllocationExpression aae, A arg) {

        return null;
    }

    /*
     * AllocationExpression
     * f0: "new"
     * f1: Identifier()
     * f2: "("
     * f3: ")"
     */
    public R visit(AllocationExpression ae, A arg) {

        return null;
    }

    /*
     * NotExpression
     * f0: "!"
     * f1: Expression()
     */
    public R visit(NotExpression ne, A arg) {

        return null;
    }

    /*
     * BracketExpression
     * f0: "("
     * f1: Expression()
     * f2: ")"
     */
    public R visit(BracketExpression be, A arg) {

        return null;
    }

}