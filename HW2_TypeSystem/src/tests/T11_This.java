
class T11_This {
    public static void main(String[] args) {
        System.out.println(new Count().getDoubleVal());
    }
}

class Count {
    int count;

    public int getDoubleVal() {
        count = 5;
        return this.doubled();
    }

    public int doubled() {
        return count * 2;
    }
}