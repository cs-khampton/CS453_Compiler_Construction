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
        for (int i = 0; i < func.params.length; i++) {
            String stackIndex = symt.locals.get(func.params[i].ident);
            if (stackIndex != null) {
                addToOut("  " + stackIndex + " = $a" + i);
            }
        }

        // emit instructions
        for (int i = 0; i < func.body.length; i++) {
            if (symt.labels.containsKey(i)) {
                addToOut(symt.labels.get(i) + ":");
            }
            func.body[i].accept(visitor);
        }
    }

}