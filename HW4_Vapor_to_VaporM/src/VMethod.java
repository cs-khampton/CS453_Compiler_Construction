import java.util.HashMap;
import java.util.LinkedHashMap;

public class VMethod {
    String name;
    String returnType;
    HashMap<String, String> params = new LinkedHashMap<>();
    HashMap<String, String> locals = new LinkedHashMap<>();

    VMethod(String methodName, String returnType) {
        this.name = methodName;
        this.returnType = returnType;
    }
}