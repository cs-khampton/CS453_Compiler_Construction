class Overload {
    public static void main(String[] args) {
        System.out.println(new Child().compute(5));
    }
}

class Base {
    public int compute(int x) {
        return x;
    }
}

class Child extends Base {
    public boolean compute(int x) {
        return true;
    }
}