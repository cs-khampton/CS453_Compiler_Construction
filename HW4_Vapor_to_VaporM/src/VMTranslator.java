import java.io.PrintWriter;

import cs132.vapor.ast.VDataSegment;
import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VOperand;
import cs132.vapor.ast.VaporProgram;

public class VMTranslator {
    private PrintWriter out;
    private VaporProgram program;
    private VMVisitor visitor;

    public VMTranslator(VaporProgram program) {
        this.out = new PrintWriter(System.out);
        this.program = program;
        this.visitor = new VMVisitor(this);
    }

    public void addToOut(String line) {
        out.println(line);
        out.flush();
    }

    public void translate() throws Throwable {
        getDataSegs();
        getFunctions();

    }

    public void getDataSegs() {
        for (VDataSegment ds : this.program.dataSegments) {
            addToOut("const " + ds.ident);
            for (VOperand.Static s : ds.values) {
                addToOut("  " + s.toString());
            }
            addToOut("");
        }
    }

    public void getFunctions() throws Throwable {
        for (VFunction func : program.functions) {
            VMSymbolTable symt = new VMSymbolTable();
            symt.build(func);
            visitor.setSymt(symt);
            addToOut("func " + func.ident + " [in 0, out 0, local " + symt.localCount + "]");
            getBody(func, symt);
            addToOut("");
        }
    }

    public void getBody(VFunction func, VMSymbolTable symt) throws Throwable {
        // store params
        for (int i = 0; i < func.body.length; i++) {
            // String stackIndex = func.params[i].ident;
            if (symt.labels.containsKey(i)) {
                for (String label : symt.labels.get(i)) {
                    addToOut(label);
                }
            }
            func.body[i].accept(visitor);
        }
        if (symt.labels.containsKey(func.body.length)) {
            for (String label : symt.labels.get(func.body.length)) {
                addToOut(label);
            }
        }
    }
}