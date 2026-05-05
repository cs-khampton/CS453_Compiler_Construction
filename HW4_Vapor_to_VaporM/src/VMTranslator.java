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
        for (VFunction fun : program.functions) {
            VMSymbolTable symt = new VMSymbolTable();
            symt.buildTable(fun);
            visitor.setSymt(symt);
            addToOut("func " + fun.ident + " [in 0, out 0, local " + symt.localCount + "]");
            getBody(fun, symt);
            addToOut("");
        }
    }

    public void getBody(VFunction fun, VMSymbolTable symt) throws Throwable {
        for (int i = 0; i < fun.body.length; i++) {
            if (symt.labels.containsKey(i)) {
                addToOut(symt.labels.get(i) + ":");
            }
            fun.body[i].accept(visitor);
        }
    }
}