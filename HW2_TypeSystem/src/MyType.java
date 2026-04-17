import java.util.HashMap;

import visitor.GJDepthFirst;

public class MyType extends GJDepthFirst<MyType, HashMap<String, String>> {

    String type;

    public MyType(String type) {
        setType(type);
    }

    private void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof MyType t) {
            if (t.type == null) {
                return false;
            }
            return this.type.equals(t.type);
        }
        return false;
    }
}