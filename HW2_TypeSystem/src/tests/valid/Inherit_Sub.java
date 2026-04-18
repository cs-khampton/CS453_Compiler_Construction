class Inherit_Sub {
    public static void main(String[] args) {
        Animal a;
        a = new Dog();
        System.out.println(a.speak());
    }
}

class Animal {
    public int speak() {
        return 0;
    }
}

class Dog extends Animal {
    public int speak() {
        return 1;
    }
}