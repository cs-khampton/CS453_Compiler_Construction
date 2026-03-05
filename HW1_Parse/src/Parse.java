
import java.util.ArrayList;
import java.util.Scanner;

/*
    Grammar:
    expr ::= num expr' | lvalue expr' | incrop expr' | (expr) expr'
    expr' ::= incrop expr' | binop expr expr'
    lvalue ::= $expr
    incrop ::= ++ | --
    binop ::= + | -
    num ::= digit num'
    num' ::= digit num' | ϵ
    digit ::= 0 | 1 | 2 | 3 | 4 | 5 | 7 | 8 | 9
    
	Example:
		$1 +
		(1 - ++$2) $# (a confusing comment)
		3
	post-fix should be...
		1 $ 1 2 $ ++_ - + 3 $ _
*/

public class Parse {

    static int cursor;
    static ArrayList<Token> tokens;
    static String postfix;
    static boolean hasError;

    public static void main(String[] args) {
        cursor = 0;
        // Scanner sc = new Scanner(System.in);
        Lex lex = new Lex(new Scanner(System.in));
        tokens = lex.getTokens();
        expr();
        printPostFix();
    }

    /* non-terminal methods */
    public static void expr() {
        String peek = peek();
        if (peek.isEmpty()) {
            hasError = true;
            return;
        }
        switch (peek) {
            case "0":
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
                num();
                exprp();
                break;
            case "$":
                lvalue();
                setPostFixVal("$");
                exprp();
                break;
            // prefix incrops
            case "++":
            case "--":
                incrop();
                expr();
                setPostFixVal(peek + "_");
                break;
            case "(":
                match("(");
                expr();
                match(")");
                exprp();
                break;
            default:
                hasError = true;
                break;
        }
    }

    public static void exprp() {
        String peek = peek();
        // postfix incrops
        if (isIncrop(peek)) {
            incrop();
            expr();
            // setPostFixVal("_" + peek);
            exprp();
            return;
        } else if (isBinop(peek)) {
            binop();
            expr();
            setPostFixVal(peek);
            exprp();
            return;
        } else {
        }
    }

    public static void lvalue() {
        match("$");
        expr();
    }

    public static void incrop() {
        String peek = peek();
        switch (peek) {
            case "++":
                match("++");
                break;
            case "--":
                match("--");
                break;
            default:
                hasError = true;
                break;
        }
    }

    public static void binop() {
        String peek = peek();
        switch (peek) {
            case "+":
                match("+");
                break;
            case "-":
                match("-");
                break;
            default:
                hasError = true;
                break;
        }
    }

    public static void num() {
        String peek = peek();
        if (isDigit(peek)) {
            digit();
            nump();
        } else {
            hasError = true;
        }
    }

    public static void nump() {
        String peek = peek();
        if (isDigit(peek)) {
            digit();
            nump();
        }
    }

    public static void digit() {
        String peek = peek();
        if (isDigit(peek)) {
            match(peek);
            setPostFixVal(peek);
        } else {
            hasError = true;
        }
    }

    /* Helper methods */

    public static String peek() {
        if (cursor > tokens.size()) {
            System.out.println("Last token read " + tokens.get(cursor - 1));
            hasError = true;
            return "";
        }
        if (cursor == tokens.size()) {
            return "";
        }
        return tokens.get(cursor).text;
    }

    public static void match(String expect) {
        if (peek().equals(expect)) {
            cursor++;
        } else {
            hasError = true;
        }
    }

    public static void success() {
        System.out.println("Expression parsed successfully");
    }

    // TODO: add the error line
    public static void error() {
        System.out.println("Parse error in line " + "");
    }

    public static boolean isIncrop(String s) {
        return (s.equals("++") || s.equals("--"));
    }

    public static boolean isBinop(String s) {
        return (s.equals("+") || s.equals("-"));
    }

    public static boolean isDigit(String s) {
        return (s.length() == 1 && Character.isDigit(s.charAt(0)));
    }

    public static void setPostFixVal(String in) {
        System.out.println("Cursor at " + cursor + " In value: " + in);
        if (in == null) {
            return;
        } else {
            postfix += in + " ";
        }
    }

    // public static boolean isStart(String in) {
    // return (in.equals("$") || in.equals("(") || in.equals("++") ||
    // in.equals("--") || isDigit(in));
    // }

    public static void printPostFix() {
        System.out.println(postfix);
    }
}