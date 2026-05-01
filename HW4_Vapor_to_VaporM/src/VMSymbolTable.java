import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VInstr;
import cs132.vapor.ast.VCall;
import cs132.vapor.ast.VCodeLabel;

public class VMSymbolTable {

    public Map<String, String> locals;
    public Map<String, String> params;
    public Map<Integer, String> labels;
    String[] reg;
    public int localCount;
    public int outCount;

    public VMSymbolTable() {
        locals = new HashMap<>();
        params = new HashMap<>();
        labels = new HashMap<>();
        this.reg = new String[] { "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", // general use callee-saved
                "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", // general use caller-saved
                "a0", "a1", "a2", "a3", // reserved for argument passing
                "v0", // returning a result from a call
                "v1" // { v0, v1 } can be used as temporary register for loading values from the
                     // stack
        };
        localCount = 0;
        outCount = 0;
    }

    public void buildTable(VFunction f) {
        assign(f);
        getLabels(f);
    }

    public void assign(VFunction f) {
        int tCount = 0;
        int sCount = 0;

        for (int i = 0; i < f.params.length; i++) {
            params.put(f.params[i].ident, "$a" + i);
        }

        // assign temps to $t registers and locals to $s registers
        for (String var : f.vars) {
            if (!params.containsKey(var)) {
                if (var.startsWith("t.")) {
                    locals.put(var, "$t" + tCount++);
                } else {
                    locals.put(var, "$s" + sCount++);
                    localCount++;
                }
            }
        }
    }

    public void getOut(VFunction f) {
        for (VInstr instr : f.body) {
            if (instr instanceof VCall call) {
                int argNum = call.args.length;
                if (argNum > 4) {
                    outCount = Math.max(outCount, call.args.length - 4);
                }
            }
        }
    }

    public void getLabels(VFunction f) {
        for (VCodeLabel label : f.labels) {
            labels.put(label.instrIndex, label.ident);
        }
    }
}
