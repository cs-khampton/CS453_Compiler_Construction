
class T2 {
    public static void main(String[] args) {
        A a;
        B b;
        int result;

        a = new A();
        b = new B();

        result = a.foo(3, 4);
        System.out.println(result);

        result = b.foo(5, 6);
        System.out.println(result);
    }
}

class A {
    int x;
    int y;

    public int foo(int a, int b) {
        int c;

        c = a + b;

        if (a < b && true) {
            c = c * 2;
        } else {
            c = c - 1;
        }

        return c;
    }
}

class B extends A {
    int z;

    public int foo(int a, int b) {
        int[] arr;
        int i;

        arr = new int[10];
        i = 0;

        while (i < 5) {
            arr[i] = a + b;
            i = i + 1;
        }

        System.out.println(arr[2]);

        return this.bar(arr[1]);
    }

    public int bar(int x) {
        boolean cond;
        int result;

        cond = !false && (x < 10);

        if (cond) {
            result = x;
        } else {
            result = x * 2;
        }

        return result;
    }
}