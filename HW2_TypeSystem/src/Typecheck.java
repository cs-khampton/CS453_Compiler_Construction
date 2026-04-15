
import java.util.HashMap;

import syntaxtree.Node;

public class Typecheck {
    public static <R, A> void main(String[] args) {
        Node root = null;
        try {
            root = new MiniJavaParser(System.in).Goal();
            // FIXME: Review indentation further
            // PPrinter<R, A> pp = new PPrinter<R, A>();
            // root.accept(pp, null);

            // Build the symbol table. Top-down visitor, inherits from
            // GJDepthFirst<R,A>. R=Void, A=Integer.

            SymbolTable<Void, Integer> pv = new SymbolTable<Void, Integer>();
            root.accept(pv, 0);
            HashMap<String, String> symt = pv.classes;
            pv.printSymt();

            // FIXME: Typechecking at 53% currently

            CheckType<Object, Object> tc = new CheckType<>();
            root.accept(tc, symt);

            if (tc.typeError) {
                System.out.println("Type error");
            } else {
                System.out.println("Program type checked successfully");
            }

        } catch (ParseException e) {
            System.out.println(e.toString());
            System.exit(1);
        }
    }

    /*********** THESE ARE HELPER METHODS ***********/

}