import cs132.vapor.ast.VAddr;
import cs132.vapor.ast.VAssign;
import cs132.vapor.ast.VBranch;
import cs132.vapor.ast.VBuiltIn;
import cs132.vapor.ast.VCall;
import cs132.vapor.ast.VGoto;
import cs132.vapor.ast.VInstr;
import cs132.vapor.ast.VLabelRef;
import cs132.vapor.ast.VLitInt;
import cs132.vapor.ast.VLitStr;
import cs132.vapor.ast.VMemRead;
import cs132.vapor.ast.VMemRef;
import cs132.vapor.ast.VMemWrite;
import cs132.vapor.ast.VOperand;
import cs132.vapor.ast.VReturn;
import cs132.vapor.ast.VVarRef;

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
        String src = load(a.source, "$v0");
        String dest = getIndex(a.dest);
        if (!src.equals("$v0")) {
            translate.addToOut("  $v0 = " + src);
        }
        translate.addToOut("  " + dest + " = $v0");
    }

    public void visit(VCall c) throws Throwable {
        String[] reg = { "$v0", "$v1", "$t0" };
        for (int i = 0; i < c.args.length; i++) {
            String arg = forceLoad(c.args[i], reg[i % reg.length]);
            translate.addToOut("  $a" + i + " = " + arg);
        }
        String ad = load(c.addr);
        translate.addToOut("  call " + ad);
        if (c.dest != null) {
            translate.addToOut("  " + getIndex(c.dest) + " = $v0");
        }
    }

    public void visit(VBuiltIn b) throws Throwable {
        String[] reg = { "$v0", "$v1", "$t0" };
        String[] argArr = new String[b.args.length];
        for (int i = 0; i < b.args.length; i++) {
            // load declared inputs into scratch registers
            argArr[i] = load(b.args[i], reg[i]);
        }

        // join into a string for pretty printing
        String args = String.join(" ", argArr);

        if (b.dest != null) {
            // result goes into $v0
            translate.addToOut("  $v0 = " + b.op.name + "(" + args + ")");
            // store $v0 back to the stack location
            String destIndex = getIndex(b.dest);
            translate.addToOut("  " + destIndex + " = $v0");
        } else {
            // no dest === PrintIntS(), Add(), Sub(), MulS()
            translate.addToOut("  " + b.op.name + "(" + args + ")");
        }
    }

    public void visit(VMemWrite w) throws Throwable {
        String dest = loadMemRefBase(w.dest, "$v0");
        String src;
        if (w.source instanceof VLabelRef || w.source instanceof VLitStr) {
            src = w.source.toString();
        } else {
            src = load(w.source, "$v1");
        }
        translate.addToOut("  " + dest + " = " + src);
    }

    public void visit(VMemRead r) throws Throwable {
        String dest = getIndex(r.dest);
        String src = loadMemRefBase(r.source, "$v0");

        translate.addToOut("  $v0 = " + src);
        translate.addToOut("  " + dest + " = $v0");
    }

    public void visit(VBranch b) throws Throwable {
        String cond = load(b.value, "$v0");
        String label = b.target.toString();
        if (b.positive) {
            translate.addToOut("  if " + cond + " goto " + label);
        } else {
            translate.addToOut("  if0 " + cond + " goto " + label);
        }
    }

    public void visit(VGoto g) throws Throwable {
        translate.addToOut("  goto " + load(g.target));
    }

    public void visit(VReturn r) throws Throwable {
        if (r.value != null) {
            String val = load(r.value, "$v0");
            if (!val.equals("$v0")) {
                translate.addToOut("  $v0 = " + val);
            }
        }
        translate.addToOut("  ret");
    }

    /******************* HELPER METHODS *******************/

    private String load(VOperand op, String temp) {
        if (op instanceof VVarRef.Local) {
            String stackIndex = symt.locals.get(op.toString());

            if (stackIndex != null) {
                translate.addToOut("  " + temp + " = " + stackIndex);
                return temp;
            }
        }
        if (op instanceof VLitStr) {
            return op.toString();
        }
        if (op instanceof VLabelRef) {
            return op.toString();
        }
        if (op instanceof VLitInt) {
            return op.toString();
        }

        // everything else into a temp
        translate.addToOut("  " + temp + " = " + op.toString());
        return temp;
    }

    private String forceLoad(VOperand op, String temp) {
        if (op instanceof VLitStr)
            return op.toString();
        if (op instanceof VVarRef.Local) {
            String stackIndex = symt.locals.get(op.toString());
            if (stackIndex != null) {
                translate.addToOut("  " + temp + " = " + stackIndex);
                return temp;
            }
        }
        // force literals and labelRefs through reg
        translate.addToOut("  " + temp + " = " + op.toString());
        return temp;
    }

    private String getIndex(VVarRef var) {
        if (var instanceof VVarRef.Local) {
            String stackIndex = symt.locals.get(var.toString());
            if (stackIndex != null) {
                return stackIndex;
            }
        }
        return var.toString();
    }

    private String load(VAddr<?> addr) {
        if (addr instanceof VAddr.Label vl) {
            return vl.label.toString();
        }
        VVarRef var = ((VAddr.Var<?>) addr).var;
        if (var instanceof VVarRef.Local) {
            String stackIndex = symt.locals.get(var.toString());
            if (stackIndex != null) {
                translate.addToOut("  $v0 = " + stackIndex);
                return "$v0";
            }
        }
        return var.toString();
    }

    private String loadMemRefBase(VMemRef mem, String temp) {
        if (mem instanceof VMemRef.Global g) {
            String base;
            if (g.base instanceof VAddr.Var) {
                VVarRef var = ((VAddr.Var<?>) g.base).var;
                if (var instanceof VVarRef.Local) {
                    String stackIndex = symt.locals.get(var.toString());
                    if (stackIndex != null) {
                        translate.addToOut("  " + temp + " = " + stackIndex);
                        base = temp;
                    } else {
                        base = var.toString();
                    }
                } else {
                    base = var.toString();
                }
            } else {
                // VAddr.Label — ds reference like :vmt_Fac
                base = ((VAddr.Label<?>) g.base).label.ident;
            }
            if (g.byteOffset == 0) {
                return "[" + base + "]";
            }
            return "[" + base + "+" + Integer.toString(g.byteOffset) + "]";
        }
        if (mem instanceof VMemRef.Stack s)
            // region is an enum
            switch (s.region) {
                case In:
                    return "in[" + s.index + "]";
                case Out:
                    return "out[" + s.index + "]";
                case Local:
                    return "local[" + s.index + "]";
            }
        return mem.toString();
    }
}