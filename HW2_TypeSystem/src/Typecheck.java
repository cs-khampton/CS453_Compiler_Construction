
import java.util.Map;

import syntaxtree.Node;

public class Typecheck {
    public static <R, A> void main(String[] args) {
        Node root = null;
        try {
            root = new MiniJavaParser(System.in).Goal();
            // FIXME: PPrinter finished - Need indentation
            // PPrinter<R, A> pp = new PPrinter<R, A>();
            // root.accept(pp, null);

            // TODO: Build the symbol table. Top-down visitor, inherits from
            // GJDepthFirst<R,A>. R=Void, A=Integer.

            SymbolTable<Void, Integer> pv = new SymbolTable<Void, Integer>();
            root.accept(pv, 0);
            Map<String, STClass> symt = pv.classes;
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
                    System.out.println("    ReturnType: " + m.returnType);
                    System.out.println("    MethodParams: ");
                    for (String paramName : m.params.keySet()) {
                        System.out.println("        " + paramName + " = " + m.params.get(paramName));
                    }
                    System.out.println("      Local inst vars:");
                    for (String localName : m.locals.keySet()) {
                        System.out.println("        " + localName + " = " + m.locals.get(localName));
                    }
                }
            }
            // TODO: Do type checking. Bottom-up visitor, also inherits from
            // GJDepthFirst. Visit functions return MyTpe (=R), and
            // take a symbol table (HashMap<String,String>) as
            // argument (=A). You may implement things differently of
            // course!

            // TypeCheckSimp ts = new TypeCheckSimp();
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
}