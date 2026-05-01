import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs132.vapor.ast.*;

public class VMVisitor extends VInstr.Visitor<Throwable> {
    VMTranslator translate;
    VMSymbolTable symt;

    public VMVisitor(VMTranslator vmt) {
        this.translate = vmt;
    }

    public void setSymt(VMSymbolTable symt) {
        this.symt = symt;
    }

    public void visit(VAssign a) throws Throwable {
        String d = resolveVar(a.dest);
        String s = resolveOp(a.source);
        System.out.println("  " + d + " = " + s);
    }

    public void visit(VCall c) throws Throwable {
        // TODO: Implement
        for (int i = 0; i < c.args.length; i++) {
            String arg = resolveOp(c.args[i]);
            translate.addToOut("  $a" + i + " = " + arg);
        }
        String ad = resolveAddr(c.addr);

        if (c.dest != null) {
            String dest = resolveVar(c.dest);
            translate.addToOut("  " + dest + " = $v0");
        }
    }

    private String resolveAddr(VAddr ad) {
        if (ad instanceof VAddr.Label) {
            return ":" + ((VAddr.Label<?>) ad).label;
        }
        return resolveVar(((VAddr.Var<?>) ad).var);
    }

    public void visit(VBuiltIn b) throws Throwable {
        // TODO: Implement
    }

    public void visit(VMemWrite w) throws Throwable {
        // TODO: Implement
    }

    public void visit(VMemRead r) throws Throwable {
        // TODO: Implement
    }

    public void visit(VBranch b) throws Throwable {
        // TODO: Implement
    }

    public void visit(VGoto g) throws Throwable {
        // TODO: Implement
    }

    public void visit(VReturn r) throws Throwable {
        // TODO: Implement
    }

    /******************* HELPER METHODS *******************/

    private String resolveVar(VVarRef v) {
        if (v instanceof VVarRef.Local) {
            String varName = v.toString();
            if (symt.params.containsKey(varName)) {
                return symt.params.get(varName);
            }
            if (symt.locals.containsKey(varName)) {
                return symt.locals.get(varName);
            }
            return varName; // catch all
        } else {
            return v.toString();
        }
    }

    private String resolveOp(VOperand op) {
        if (op instanceof VVarRef v) {
            return resolveVar(v);
        }
        return op.toString();
    }
}