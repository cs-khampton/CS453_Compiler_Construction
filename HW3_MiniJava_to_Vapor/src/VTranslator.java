import java.io.PrintWriter;

public class VTranslator {
    private PrintWriter out;

    VTranslator() {
        this.out = new PrintWriter(System.out);
    }

    public void addToOut(String line) {
        out.println(line);
        out.flush();
    }
}
