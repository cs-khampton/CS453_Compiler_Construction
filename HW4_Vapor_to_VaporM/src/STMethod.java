import java.util.HashMap;
import java.util.LinkedHashMap;

public class STMethod {
    String name;
    String returnType;
    HashMap<String, String> params = new LinkedHashMap<>();
    HashMap<String, String> locals = new LinkedHashMap<>();

    STMethod(String name, String returnType) {
        this.name = name;
        this.returnType = returnType;
    }
}
