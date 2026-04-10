import java.util.LinkedHashMap;
import java.util.Map;

public class STClass {
    String parent;
    String name;
    Map<String, String> instvars = new LinkedHashMap<>();
    Map<String, STMethod> methods = new LinkedHashMap<>();

    STClass(String name, String parent) {
        this.name = name;
        this.parent = parent;
    }
}