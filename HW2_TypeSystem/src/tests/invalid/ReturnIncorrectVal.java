class ReturnIncorrectVal {
    public static void main(String[] args) {
        System.out.println(new Foo().getVal());
    }
}

class Foo {
    public int getVal() {
        return true;
    }
}