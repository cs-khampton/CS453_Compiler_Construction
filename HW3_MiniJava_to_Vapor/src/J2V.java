import java.util.LinkedHashMap;

import syntaxtree.Node;

public class J2V {
    public static void main(String[] args) {
        Node root = null;
        try {
            root = new MiniJavaParser(System.in).Goal();

            // Build the symbol table
            SymbolTable<Void, Integer> pv = new SymbolTable<>();
            root.accept(pv, 0);

            LinkedHashMap<String, String> symt = pv.classes;

            VTranslator translator = new VTranslator();
            // set the translator for output
            VVisitor v = new VVisitor(translator);
            root.accept(v, symt);
        } catch (ParseException e) {
            System.out.println(e.toString());
            System.exit(1);
        }
    }

}