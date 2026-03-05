
import java.util.ArrayList;
import java.util.Scanner;

public class Lex {
    private int linenum = 1;
    private boolean hasError = false;
    private ArrayList<Token> tokens = new ArrayList<Token>();

    public Lex(Scanner sc) {
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            hasError = false;

            int comment = line.indexOf('#');
            if (comment != -1) {
                line = line.substring(0, comment);
            }

            if (line.trim().isEmpty()) {
                continue;
            }
            line = line.replaceAll("\\s+", "");
            lex(line);
            linenum++;
        }
    }

    public void lex(String in) {
        // System.out.println("Lex in = " + in);
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if (c == '#') {
                break;
            }

            if (i + 1 < in.length()) {
                String two = in.substring(i, i + 2);
                if (two.equals("++") || two.equals("--")) {
                    tokens.add(new Token(two, linenum));
                    i++;
                    continue;
                }
            }

            if (c == '$' || c == '+' || c == '-' || c == '(' || c == ')') {
                tokens.add(new Token(c, linenum));
            } else if (Character.isDigit(c)) {
                tokens.add(new Token(c, linenum));
            } else {
                hasError = true;
            }
        }
    }

    public boolean getErrorStatus() {
        return this.hasError;
    }

    public int getLineNumber() {
        return linenum;
    }

    public ArrayList<Token> getTokens() {
        return this.tokens;
    }

    public void setLineNumber(int linenum) {
        this.linenum = linenum;
    }

    public void setErrorStatus(boolean hasError) {
        this.hasError = hasError;
    }
}