import syntaxtree.Goal;
import syntaxtree.Identifier;
import syntaxtree.IntegerLiteral;
import syntaxtree.MainClass;
import syntaxtree.Node;
import syntaxtree.TypeDeclaration;
import visitor.GJDepthFirst;

public class PPrinter<R, A> extends GJDepthFirst<R, A> {
    private int indent = 0;

    public void printClassName(Node n) {
        for (int i = 0; i < indent; i++) {
            System.out.print("  ");
        }
        System.out.println(n.getClass().getSimpleName());
    }

    public void printNodeTokenName(String s) {
        for (int i = 0; i < indent; i++) {
            System.out.print("  ");
        }
        System.out.println("NodeToken => " + s);
    }

    // Goal -> MainClass() TypeDeclaration()*
    public R visit(Goal n, A arg) {
        R ret = null;
        printClassName(n);
        n.f0.accept(this, arg); // MainClass()
        n.f1.accept(this, arg); // TypeDeclaration()*
        n.f2.accept(this, arg); // <EOF>
        indent--;
        return ret;
    }

    // MainClass -> class Identifier() { public static void main (String []
    // Identifier()){ (Type()_1 Identifier()_1)* ; Statement()* }}
    public R visit(MainClass mc, A arg) {
        R ret = null;
        indent++;
        printClassName(mc);
        mc.f0.accept(this, arg); // class
        printNodeTokenName(mc.f0.toString());
        mc.f1.accept(this, arg); // Identifier()
        printClassName(mc.f1);
        printNodeTokenName(mc.f1.f0.toString());
        mc.f2.accept(this, arg); // {
        mc.f3.accept(this, arg); // public
        printNodeTokenName(mc.f3.toString());
        mc.f4.accept(this, arg); // static
        printNodeTokenName(mc.f4.toString());
        mc.f5.accept(this, arg); // void
        printNodeTokenName(mc.f5.toString());
        mc.f6.accept(this, arg); // main
        printNodeTokenName(mc.f6.toString());
        mc.f7.accept(this, arg); // (
        printNodeTokenName(mc.f7.toString());
        mc.f8.accept(this, arg); // String
        printNodeTokenName(mc.f8.toString());
        mc.f9.accept(this, arg); // [
        printNodeTokenName(mc.f9.toString());
        mc.f10.accept(this, arg); // ]
        printNodeTokenName(mc.f10.toString());
        mc.f11.accept(this, arg); // Identifier()
        printClassName(mc.f11);
        printNodeTokenName(mc.f11.f0.toString());
        indent--;
        mc.f12.accept(this, arg); // )
        printNodeTokenName(mc.f12.toString());
        mc.f13.accept(this, arg); // {
        indent++;
        printNodeTokenName(mc.f13.toString());
        mc.f14.accept(this, arg); // Type()
        mc.f15.accept(this, arg); // Identifier()
        mc.f15.accept(this, arg); // Statement()
        mc.f16.accept(this, arg); // }
        indent--;
        mc.f17.accept(this, arg); // }
        indent--;
        return ret;
    }

    // TypeDeclaration -> ClassDeclaration() | ClassExtendsDeclaration()
    public R visit(TypeDeclaration d, A arg) {
        R ret = null;
        d.f0.accept(this, arg); // ClassDeclaration() | ClassExtendsDeclaration()
        return ret;
    }

    public R visit(Identifier id, A arg) {
        R ret = null;
        id.f0.accept(this, arg); // IDENTIFIER
        return ret;
    }

    public R visit(IntegerLiteral c, A arg) {
        R ret = null;
        c.f0.accept(this, arg); // INTEGER_LITERAL
        return ret;
    }
}
