
import java.util.Map;

import syntaxtree.Node;

public class Typecheck {
    @SuppressWarnings("static-access")
    public static <R, A> void main(String[] args) {
        Node root = null;
        try {
            root = new MiniJavaParser(System.in).Goal();
            // XXX: Review indentation further
            // PPrinter<R, A> pp = new PPrinter<R, A>();
            // root.accept(pp, null);

            // XXX: Review this method
            // Build the symbol table. Top-down visitor, inherits from
            // GJDepthFirst<R,A>. R=Void, A=Integer.

            SymbolTable<Void, Integer> pv = new SymbolTable<Void, Integer>();
            root.accept(pv, 0);
            Map<String, STClass> symt = pv.classes;
            printSymbolTable(symt);

            /*
             * TODO: Do type checking. Bottom-up visitor, also inherits from
             * GJDepthFirst. Visit functions return MyTpe (=R), and
             * take a symbol table (HashMap<String,String>) as
             * argument (=A). You may implement things differently of
             * course!
             */

            // CheckType ts = new CheckType();
            // MyType res = root.accept(ts, symt);

            // Ugly code not to be inspired from: "my" way of storing
            // type info / typecheck property: if some of my internal
            // structure is empty, then things don't typecheck for
            // me. This is specific to my own implementation.
            // if (res != null && res.type_array.size() > 0)
            // System.out.println("Code typechecks");
            // else
            // System.out.println("Type error");
        } catch (ParseException e) {
            System.out.println(e.toString());
            System.exit(1);
        }
    }

    /*********** THESE ARE HELPER METHODS FOR PRINTING THE SYMBOL TABLE *********/
    public static void printSymbolTable(Map<String, STClass> symt) {
        for (String c : symt.keySet()) {
            STClass sc = symt.get(c);
            System.out.println("\n---------------------------------------\n");

            System.out.println("Class: " + c);
            System.out.println(" Parent: " + sc.parent);
            System.out.println(" Class inst vars: " + sc.instvars);
            System.out.println("  Methods");

            for (String methodName : sc.methods.keySet()) {
                STMethod m = sc.methods.get(methodName);
                System.out.println("    MethodName: " + m.name);
                System.out.println("     ReturnType: " + m.returnType);
                if (m.params.size() != 0) {
                    System.out.println("     MethodParams: ");
                    for (String paramName : m.params.keySet()) {
                        System.out.println("          " + paramName + " = " + m.params.get(paramName));
                    }
                }
                if (m.locals.size() != 0) {
                    System.out.println("      Local inst vars:");
                    for (String localName : m.locals.keySet()) {
                        System.out.println("        " + localName + " = " + m.locals.get(localName));
                    }
                }
            }
        }
    }

}