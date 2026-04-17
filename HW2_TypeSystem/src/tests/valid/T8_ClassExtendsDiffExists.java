/* Rules used: r0, r1, r6, r25, r32 r61 */
// r0
class T8_ClassExtendsDiffExists {
    public static void main(String[] arg) { // r1, r61
        int i; // r6, r61
        i = i + 2; // r25, r32
    }
}
class F extends G {
    public int test(){
        return 2;
    }
}

class G {
    public int test(){
        return 1;
    }
}