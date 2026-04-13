import java.io.PrintWriter;
import java.util.HashMap;

import visitor.GJDepthFirst;

public class VTranslator extends GJDepthFirst<MyType, HashMap<String, String>> {
    private PrintWriter out;

    VTranslator() {
        this.out = new PrintWriter(System.out);
    }

    public void addToOut(String line) {
        out.println(line);
        out.flush();
    }
}