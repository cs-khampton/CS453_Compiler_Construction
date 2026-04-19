class ArrOps {
    public static void main(String[] args) {
        System.out.println(new Sorter().getElement(3));
    }
}

class Sorter {
    public int getElement(int idx) {
        int[] arr;
        int result;
        int val;
        arr = new int[5];
        arr[0] = 10;
        arr[1] = 20;
        val = arr[idx];
        result = val + idx;
        return result;
    }
}