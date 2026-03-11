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
import syntaxtree.Node;
import syntaxtree.NodeToken;
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

public class PPrinter<R, A> extends GJDepthFirst<R, A> {
    private int indent = 0;

    public void printClassName(Node n) {
        if (n instanceof NodeToken nt) {
            indent++;
        }
        for (int i = 0; i < indent; i++) {
            System.out.print("  ");
        }
        if (n instanceof NodeToken nt) {
            System.out.println("NodeToken => " + nt.toString());
            indent--;
        } else {
            System.out.println(n.getClass().getSimpleName());
        }
    }

    // G => MainClass(TypeDeclaration)* <EOF>
    public R visit(Goal n, A arg) {
        R ret = null;
        printClassName(n);
        visit(n.f0, arg);
        printClassName(n.f2);
        return ret;
    }

    // mc => "class" Identifier() "{" "public" "static" "void" "main" "(" "String"
    // "[" "]" Identifier() ")" " {" VarDeclaration()* Statement()* "}" "}"
    public R visit(MainClass mc, A arg) {
        R ret = null;
        indent++;
        printClassName(mc);
        printClassName(mc.f0); // class
        visit(mc.f1, arg); // Identifier()
        printClassName(mc.f2); // {
        indent++;
        printClassName(mc.f3); // public
        printClassName(mc.f4); // static
        printClassName(mc.f5); // void
        printClassName(mc.f6); // main
        printClassName(mc.f7); // (
        printClassName(mc.f8); // String
        printClassName(mc.f9); // [
        printClassName(mc.f10); // ]
        visit(mc.f11, arg); // Identifier()
        printClassName(mc.f12); // )
        indent++;
        printClassName(mc.f13); // {
        visit(mc.f14, arg); // VarDeclaration()
        visit(mc.f15, arg); // Statement()
        indent--;
        printClassName(mc.f16); // }
        indent--;
        printClassName(mc.f17); // }
        return ret;
    }

    public R visit(TypeDeclaration d, A arg) {
        R ret = null;
        printClassName(d);
        return ret;
    }

    public R visit(ClassDeclaration cd, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(ClassExtendsDeclaration ced, A arg) {
        R ret = null;

        return ret;
    }

    // VarDeclaration => Type() Identifier() ";"
    public R visit(VarDeclaration vd, A arg) {
        R ret = null;
        printClassName(vd);
        visit(vd.f0, arg); // Type()
        visit(vd.f1, arg); // Identifier()
        indent--;
        printClassName(vd.f2); // ;
        return ret;
    }

    public R visit(MethodDeclaration md, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(FormalParameterList fpl, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(FormalParameter fp, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(FormalParameterRest fpr, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(Type t, A arg) {
        R ret = null;
        indent++;
        printClassName(t.f0.choice);
        return ret;
    }

    public R visit(ArrayType at, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(BooleanType bt, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(IntegerType it, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(Statement s, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(Block b, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(AssignmentStatement as, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(ArrayAssignmentStatement aas, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(IfStatement is, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(WhileStatement ws, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(PrintStatement ps, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(Expression e, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(AndExpression ae, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(CompareExpression ce, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(PlusExpression pe, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(MinusExpression me, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(TimesExpression te, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(ArrayLookup al, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(ArrayLength al, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(MessageSend ms, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(ExpressionList el, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(ExpressionRest er, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(PrimaryExpression pe, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(IntegerLiteral il, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(TrueLiteral tl, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(FalseLiteral fl, A arg) {
        R ret = null;
        return ret;
    }

    public R visit(Identifier id, A arg) {
        R ret = null;
        indent++;
        printClassName(id);
        printClassName(id.f0);
        indent--;
        return ret;
    }

    public R visit(ThisExpression te, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(ArrayAllocationExpression aae, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(AllocationExpression ae, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(NotExpression ne, A arg) {
        R ret = null;

        return ret;
    }

    public R visit(BracketExpression be, A arg) {
        R ret = null;

        return ret;
    }
}