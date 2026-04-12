import java.util.LinkedHashMap;
import java.util.Map;

public class VpClass {
    String parent;
    String name;
    Map<String, String> instvars = new LinkedHashMap<>();
    Map<String, VpMethod> method = new LinkedHashMap<>();

    VpClass(String name, String parent) {
        this.name = name;
        this.parent = parent;
    }
}
