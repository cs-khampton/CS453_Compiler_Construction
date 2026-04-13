import java.util.LinkedHashMap;
import java.util.Map;

public class VClass {
    String parent;
    String name;
    Map<String, String> instvars = new LinkedHashMap<>();
    Map<String, VMethod> method = new LinkedHashMap<>();

    VClass(String name, String parent) {
        this.name = name;
        this.parent = parent;
    }
}
