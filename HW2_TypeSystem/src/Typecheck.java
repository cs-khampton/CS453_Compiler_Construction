
import java.util.HashMap;

import syntaxtree.Node;

public class Typecheck {
    @SuppressWarnings("static-access")
    public static <R, A> void main(String[] args) {
        Node root = null;
        try {
            root = new MiniJavaParser(System.in).Goal();
            // FIXME: Review indentation further
            // PPrinter<R, A> pp = new PPrinter<R, A>();
            // root.accept(pp, null);

            // XXX: Review this method
            // Build the symbol table. Top-down visitor, inherits from
            // GJDepthFirst<R,A>. R=Void, A=Integer.

            SymbolTable<Void, Integer> pv = new SymbolTable<Void, Integer>();
            root.accept(pv, 0);
            HashMap<String, String> symt = pv.classes;

            // XXX: To be removed before submission
            for (String key : symt.keySet()) {
                System.out.println(key + " ==== " + symt.get(key));
            }

            // TODO: Do type checking. Bottom-up visitor, also inherits from
            // GJDepthFirst. Visit functions return MyType (=R), and
            // take a symbol table (HashMap<String,String>) as
            // argument (=A). You may implement things differently of
            // course!

            CheckType tc = new CheckType();
            MyType res = (MyType) root.accept(tc, symt);
            if (tc.typeError) {
                System.out.println("Type error");
            } else {
                System.out.println("Program type checked successfully");
            }

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

    /*********** THESE ARE HELPER METHODS ***********/

}