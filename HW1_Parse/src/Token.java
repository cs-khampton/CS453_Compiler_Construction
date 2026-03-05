
public class Token {
    String text;
    int linenum;

    Token(String text, int linenum) {
        this.text = text;
        this.linenum = linenum;
    }

    Token(char text, int linenum) {
        this.text = String.valueOf(text);
        this.linenum = linenum;
    }

    public int getLineNum() {
        return this.linenum;
    }
}