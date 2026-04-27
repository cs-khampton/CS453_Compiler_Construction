import java.io.PrintWriter;

public class VMTranslator {
    private PrintWriter out;

    VMTranslator() {
        this.out = new PrintWriter(System.out);
    }

    public void addToOut(String line) {
        out.println(line);
        out.flush();
    }
}