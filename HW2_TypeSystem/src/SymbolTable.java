import java.util.HashMap;
import java.util.LinkedHashMap;
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

    HashMap<String, String> st = new HashMap<>();

    /*
     * Goal
     * f0: MainClass()
     * f1: TypeDeclaration()*
     * f2: <EOF>
     */
    public R visit(Goal n, A arg) {
        n.f0.accept(this, arg); // MainClass()
        n.f1.accept(this, arg); // TypeDeclaration()*
        st.put(n.f2.toString(), "<EOF>");
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
        String className = mc.f0.toString();
        st.put(mc.f0.toString(), "class");
        mc.f1.accept(this, arg); // Identifier()
        st.put(mc.f2.toString(), "{");
        st.put(mc.f3.toString(), "public");
        st.put(mc.f4.toString(), "static");
        st.put(mc.f5.toString(), "void");
        st.put(mc.f6.toString(), "main");
        st.put(mc.f7.toString(), "(");
        st.put(mc.f8.toString(), "String");
        st.put(mc.f9.toString(), "[");
        st.put(mc.f10.toString(), "]");
        mc.f11.accept(this, arg); // Identifier()
        st.put(mc.f12.toString(), ")");
        st.put(mc.f3.toString(), "{");
        mc.f14.accept(this, arg); // Vardeclaration()*
        mc.f15.accept(this, arg); // Statement()*
        st.put(mc.f16.toString(), "}");
        st.put(mc.f17.toString(), "}");
        return null;
    }

    // TypeDeclaration => ClassDeclaration() | ClassExtendsDeclaration()
    public R visit(TypeDeclaration d, A arg) {

        return null;
    }

    // ClassDeclaration => "class" Identifier() "{" VarDeclaration()*
    // MethodDeclaration()* "}"
    public R visit(ClassDeclaration cd, A arg) {

        return null;
    }

    // ClassExtendsDeclaration => "class" Identifier() "extends" Identifier() "{"
    // VarDeclaration()* MethodDeclaration()* "}"
    public R visit(ClassExtendsDeclaration ced, A arg) {

        return null;
    }

    // VarDeclaration => Type() Identifier() ";"
    public R visit(VarDeclaration vd, A arg) {

        return null;
    }

    // MethodDeclaration => "public" Type() Identifier() "(" FormalParameterList()?
    // ")" "{" VarDeclaration()* Statement()* "return" Expression() ";" "}"
    public R visit(MethodDeclaration md, A arg) {

        return null;
    }

    // FormalParameterList => FormalParameter() FormalParameterRest()*
    public R visit(FormalParameterList fpl, A arg) {

        return null;
    }

    // FormalParameter => Type() Identifier()
    public R visit(FormalParameter fp, A arg) {

        return null;
    }

    // FormalParameterRest => "," FormalParameter()
    public R visit(FormalParameterRest fpr, A arg) {

        return null;
    }

    // Type => ArrayType() | BooleanType() | Identifier()
    public R visit(Type t, A arg) {

        return null;
    }

    // ArrayType => "int" "[" "]"
    public R visit(ArrayType at, A arg) {

        return null;
    }

    // BooleanType => "boolean"
    public R visit(BooleanType bt, A arg) {

        return null;
    }

    // IntegerType => "int"
    public R visit(IntegerType it, A arg) {

        return null;
    }

    // Statement => Block() | AssignmentStatement() | ArrayAssignmentStatement() |
    // IfStatement() | WhileStatement() | PrintStatement()
    public R visit(Statement s, A arg) {

        return null;
    }

    // Block => "{" Statement()* "}"
    public R visit(Block b, A arg) {

        return null;
    }

    // AssignmentStatement => Identifier() "=" Expression() ";"
    public R visit(AssignmentStatement as, A arg) {

        return null;
    }

    // ArrayAssignmentStatement => Identifier() "[" Expression() "]" "="
    // Expression() ";"
    public R visit(ArrayAssignmentStatement aas, A arg) {

        return null;
    }

    // IfStatement => "if" "(" Expression() ")" Statement() "else" Statement()
    public R visit(IfStatement is, A arg) {

        return null;
    }

    // WhileStatement => "while" "(" Expression() ")" Statement()
    public R visit(WhileStatement ws, A arg) {

        return null;
    }

    // PrintStatement => "System.out.println" "(" Expression() ")" ";"
    public R visit(PrintStatement ps, A arg) {

        return null;
    }

    // Expression => AndExpression() | CompareExpression() | PlusExpression() |
    // MinusExpression() | TimesExpression() | ArrayLookup() | ArrayLength() |
    // MessageSend() | PrimaryExpression()
    public R visit(Expression e, A arg) {

        return null;
    }

    // AndExpression => PrimaryExpression() "&&" PrimaryExpression()
    public R visit(AndExpression ae, A arg) {

        return null;
    }

    // ComapreExpression => PrimaryExpression() "<" PrimaryExpression()
    public R visit(CompareExpression ce, A arg) {

        return null;
    }

    // PlusExpression => PrimaryExpression() "+" PrimaryExpression()
    public R visit(PlusExpression pe, A arg) {

        return null;
    }

    // MinusExpression => PrimaryExpression() "-" PrimaryExpression()
    public R visit(MinusExpression me, A arg) {

        return null;
    }

    // TimesExpression => PrimaryExpression() "*" PrimaryExpression()
    public R visit(TimesExpression te, A arg) {

        return null;
    }

    // ArrayLookup => PrimaryExpression() "[" PrimaryExpression() "]"
    public R visit(ArrayLookup al, A arg) {

        return null;
    }

    // ArrayLength => PrimaryExpression() "." "length"
    public R visit(ArrayLength al, A arg) {

        return null;
    }

    // MessageSend => PrimaryExpression() "." Identifier() "(" ExpresionList()? ")"
    public R visit(MessageSend ms, A arg) {

        return null;
    }

    // ExpressionList => Expression() ExpressionRest()
    public R visit(ExpressionList el, A arg) {

        return null;
    }

    // ExpressionRest => "," Expression()
    public R visit(ExpressionRest er, A arg) {

        return null;
    }

    // PrimaryExpression => IntegerLiteral() | TrueLiteral() | FalseLiteral() |
    // Identifier() | ThisExpression() | ArrayAllocationExpression() |
    // AllocationExpression() | NotExpression() | BracketExpression()
    public R visit(PrimaryExpression pe, A arg) {

        return null;
    }

    // IntegerLiteral => <INTEGER_LITERAL>
    public R visit(IntegerLiteral il, A arg) {

        return null;
    }

    // TrueLiteral => "true"
    public R visit(TrueLiteral tl, A arg) {

        return null;
    }

    // FalseLiteral => "false"
    public R visit(FalseLiteral fl, A arg) {

        return null;
    }

    // Identifier => <IDENTIFIER>
    public R visit(Identifier id, A arg) {

        return null;
    }

    // ThisExpression => "this"
    public R visit(ThisExpression te, A arg) {

        return null;
    }

    // ArrayAllocationExpression => "new" "int" "[" Expression() "]"
    public R visit(ArrayAllocationExpression aae, A arg) {

        return null;
    }

    // AllocationExpression => "new" Identifier() "(" ")"
    public R visit(AllocationExpression ae, A arg) {

        return null;
    }

    // NotExpression => "!" Expression()
    public R visit(NotExpression ne, A arg) {

        return null;
    }

    // BracketExpression => "(" Expression() ")"
    public R visit(BracketExpression be, A arg) {

        return null;
    }

    // Keep track of global vs. local variables
    class Class {
        String parent;
        String name;
        Map<String, Class> instvars = new LinkedHashMap<>();
        Map<String, Method> methods = new LinkedHashMap<>();

        Class(String name, String parent) {
            this.name = name;
            this.parent = parent;
        }
    }

    class Method {
        String name;
        String retType;
        HashMap<String, String> params = new LinkedHashMap<>();
        HashMap<String, String> locals = new LinkedHashMap<>();

        Method(String name, String retType) {
            this.name = name;
            this.retType = retType;
        }
    }

}