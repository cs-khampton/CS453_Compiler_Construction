import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;

import cs132.vapor.ast.VInstr;

public class VMVisitor extends VInstr.VisitorP<Context, Throwable> {
    private VMTranslator translate; // can reuse your existing VTranslator

    public static class Context {
        public Map<String, String> localHier;
        public Map<String, String> paramHier;
        public int localCount;
        public int outCount;

        public Context() {
            localHier = new HashMap<>();
            paramHier = new HashMap<>();
            localCount = 0;
            outCount = 0;
        }
    }

    public VMVisitor(VMTranslator vmt) {
        this.translate = vmt;
    }
}