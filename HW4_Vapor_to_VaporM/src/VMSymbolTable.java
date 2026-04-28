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

    public VMSymbolTable(String[] reg) {
        locals = new HashMap<>();
        params = new HashMap<>();
        labels = new HashMap<>();
        this.reg = reg;
        localCount = 0;
        outCount = 0;
    }

    public void buildTable(VFunction f) {
        getParams(f);
        getLocals(f);
        getOut(f);
        getLabels(f);
    }

    public void getParams(VFunction f) {
        for (int i = 0; i < f.params.length; i++) {
            params.put(f.params[i].ident, "in[" + i + "]");
        }
    }

    public void getLocals(VFunction f) {
        int regCount = 0;
        for (String var : f.vars) {
            if (params.containsKey(var)) {
                if (regCount < this.reg.length) {
                    locals.put(var, "$" + reg[regCount++]);
                } else {
                    locals.put(var, "local[" + localCount++ + "]");
                }
            }
        }
    }

    public void getOut(VFunction f) {
        for (VInstr instr : f.body) {
            if (instr instanceof VCall c) {
                int argNum = c.args.length;
                if (argNum > 4) {
                    outCount = Math.max(outCount, c.args.length - 4);
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
