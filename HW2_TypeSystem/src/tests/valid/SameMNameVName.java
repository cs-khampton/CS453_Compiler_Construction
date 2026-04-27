class SameMNameVName {
    public static void main(String[] a) {
        int[] arr;
        int test;

        arr = new int[10];
        arr[3] = 7;
        test = (arr[3]) + (arr.length);
        System.out.println(test);
    }
}

// should type check but not currently
class Test {
    int first;

    public int first(A a) {
        return first;
    }
}