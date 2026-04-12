import java.util.HashMap;

import syntaxtree.Node;
import visitor.GJDepthFirst;

public class J2V extends GJDepthFirst<Object, Object> {
    public static void main(String[] args) {
        Node root = null;
        try {
            new MiniJavaParser(System.in);
            root = MiniJavaParser.Goal();

            // Build the symbol table
            SymbolTable<Void, Integer> pv = new SymbolTable<>();
            root.accept(pv, 0);

            HashMap<String, String> symt = pv.classes;
            pv.printSymt();
            VTranslator t = new VTranslator();
            VVisitor visitor = new VVisitor<>(t, symt);
            root.accept(visitor, symt);
        } catch (ParseException e) {
            System.out.println(e.toString());
            System.exit(1);
        }
    }

}