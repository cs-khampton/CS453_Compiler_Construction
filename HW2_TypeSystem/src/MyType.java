import java.util.HashMap;

import visitor.GJDepthFirst;

public class MyType extends GJDepthFirst<MyType, HashMap<String, String>> {

    String type;

    public MyType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}