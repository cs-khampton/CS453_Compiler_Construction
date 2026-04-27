class Overload {
    public static void main(String[] a) {
        System.out.println(0);
    }
}

// Parent class with a valid method
class Parent {
    public int compute(int n, boolean flag) {
        return n;
    }
}

// CASE 1: Violation via Return Type
// Error: Child returns boolean, Parent returns int
// class ChildTypeMismatch extends Parent {
// public boolean compute(int n, boolean flag) {
// return flag;
// }
// }

// CASE 2: Violation via Parameter Count
// Error: Child expects 1 param, Parent expects 2
class ChildCountMismatch extends Parent {
    public int compute(int n) {
        return n;
    }
}

/*
 * // CASE 3: Violation via Parameter Type
 * // Error: Second param is int instead of boolean
 * class ChildParamMismatch extends Parent {
 * public int compute(int n, int flag) {
 * return n;
 * }
 * }
 */

/*
 * // CASE 4: Nested Overriding Violation
 * // A -> B -> C (C violates A's signature)
 * class GrandChild extends ChildTypeMismatch {
 * public int compute(int n, boolean flag) {
 * return n;
 * }
 * }
 */