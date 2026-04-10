import java.util.HashMap;

import syntaxtree.Node;

public class J2V {
    public static void main(String[] args) {
        Node root = null;
        try {
            root = new MiniJavaParser(System.in).Goal();

            // Build the symbol table
            SymbolTable<Void, Integer> pv = new SymbolTable<>();
            root.accept(pv, 0);

            HashMap<String, String> symt = pv.classes;

            for (String key : symt.keySet()) {
                System.out.println(key + " === " + symt.get(key));
            }

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

}