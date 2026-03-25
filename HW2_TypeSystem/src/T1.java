
class T1 {
    public static void main(String[] args) {
        int x;
        int y;
        int[] arr;

        x = 5;
        y = 10;

        arr = new int[5];
        arr[0] = x + y;
        arr[1] = x * y;

        if (x < y) {
            System.out.println(arr[0]);
        } else {
            System.out.println(arr[1]);
        }

        while (x < y) {
            x = x + 1;
            System.out.println(x);
        }

        System.out.println(arr.length);
    }
}