class AndExpr {
    public static void main(String[] args) {
        System.out.println(new Checker().check());
    }
}

class Checker {
    public int check() {
        boolean a;
        boolean b;
        int result;
        a = true;
        b = true;
        if (a && b)
            result = 1;
        else
            result = 0;
        return result;
    }
}