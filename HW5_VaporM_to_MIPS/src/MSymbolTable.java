import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs132.vapor.ast.VCodeLabel;
import cs132.vapor.ast.VFunction;

public class MSymbolTable {

    public Map<String, String> locals; // varname, local[n]
    public Map<Integer, List<String>> labels; // instrIndex, labelname
    public int localCount;

    public MSymbolTable() {
        locals = new HashMap<>();
        labels = new HashMap<Integer, List<String>>();
        localCount = 0;
    }

    public void build(VFunction func) {
        assignVars(func);
        getLabels(func);
    }

    private void assignVars(VFunction func) {
        // params are in $a regs. Need to be allocated first
        for (int i = 0; i < func.params.length; i++) {
            String name = func.params[i].ident;
            if (!locals.containsKey(name)) {
                locals.put(name, "local[" + localCount++ + "]");
            }
        }

        for (String var : func.vars) {
            if (!locals.containsKey(var)) {
                locals.put(var, "local[" + localCount++ + "]");
            }
        }
    }

    private void getLabels(VFunction func) {
        for (VCodeLabel label : func.labels) {
            if (!labels.containsKey(label.instrIndex)) {
                labels.put(label.instrIndex, new ArrayList<String>());
            }
            labels.get(label.instrIndex).add(label.ident);
        }
    }
}