
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
            Map<String, String> symt = SymbolTable.flatten(pv.classes);
            for (String key : symt.keySet()) {
                System.out.println(key + " = " + symt.get(key));
            }
            // printSymbolTable(symt);

            // TODO: Do type checking. Bottom-up visitor, also inherits from
            // GJDepthFirst. Visit functions return MyTpe (=R), and
            // take a symbol table (HashMap<String,String>) as
            // argument (=A). You may implement things differently of
            // course!

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

}