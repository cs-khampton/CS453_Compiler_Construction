import cs132.vapor.ast.VAddr;
import cs132.vapor.ast.VAssign;
import cs132.vapor.ast.VBranch;
import cs132.vapor.ast.VBuiltIn;
import cs132.vapor.ast.VCall;
import cs132.vapor.ast.VFunction;
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

public class MVisitor extends VInstr.Visitor<Throwable> {
    MTranslator translate;
    VFunction currFunc;

    public MVisitor(MTranslator t) {
        this.translate = t;
    }

    public void setFunc(VFunction func) {
        this.currFunc = func;
    }

    public void visit(VAssign a) throws Throwable {
        String dest = reg(a.dest);
        if (a.source instanceof VLitInt) {
            translate.addToOut("  li " + dest + " " + a.source.toString());
        } else if (a.source instanceof VLabelRef) {
            String label = stripColon(a.source.toString());
            translate.addToOut("  la " + dest + " " + label);
        } else {
            translate.addToOut("  move " + dest + " " + reg((VVarRef) a.source));
        }
    }

    public void visit(VCall c) throws Throwable {
        if (c.addr instanceof VAddr.Label) {
            String label = stripColon(((VAddr.Label<?>) c.addr).label.toString());
            translate.addToOut("  jal " + label);
        } else {
            String r = reg(((VAddr.Var<?>) c.addr).var);
            translate.addToOut("  jalr " + r);
        }
        if (c.dest != null) {
            translate.addToOut("  move " + reg(c.dest) + " $v0");
        }
    }

    public void visit(VBuiltIn b) throws Throwable {
        String op = b.op.name;
        String dest = b.dest != null ? reg(b.dest) : null;
        switch (op) {
            case "Add":
                String lhs = load(b.args[0], "$t9");
                String rhs = load(b.args[1], "$t8");
                translate.addToOut("  add " + dest + " " + lhs + " " + rhs);
                break;
            case "Sub":
                translate.addToOut("  subu " + dest + " "
                        + load(b.args[0], "$t9") + " "
                        + load(b.args[1], "$t8"));
                break;
            case "MulS":
                translate.addToOut("  mul " + dest + " "
                        + load(b.args[0], "$t9") + " "
                        + load(b.args[1], "$v1"));
                break;
            case "Eq":
                translate.addToOut("  subu " + dest + " "
                        + load(b.args[0], "$t9") + " "
                        + load(b.args[1], "$t8"));
                translate.addToOut("  sltiu " + dest + " " + dest + " 1");
                break;
            case "Lt":
                translate.addToOut("  sltu " + dest + " "
                        + load(b.args[0], "$t9") + " "
                        + load(b.args[1], "$t8"));
                break;
            case "LtS":
                if (b.args[1] instanceof VLitInt) {
                    int val = Integer.parseInt(b.args[1].toString());
                    if (val >= -32768 && val <= 32767) {
                        translate.addToOut("  slti " + dest + " "
                                + load(b.args[0], "$t9") + " "
                                + b.args[1].toString());
                    } else {
                        translate.addToOut("  li $t8 " + b.args[1].toString());
                        translate.addToOut("  slt " + dest + " "
                                + load(b.args[0], "$t9") + " $t8");
                    }
                } else {
                    translate.addToOut("  slt " + dest + " "
                            + load(b.args[0], "$t9") + " "
                            + load(b.args[1], "$t8"));
                }
                break;
            case "PrintIntS":
                if (b.args[0] instanceof VLitInt) {
                    translate.addToOut("  li $a0 " + operand(b.args[0]));
                } else {
                    translate.addToOut("  move $a0 " + operand(b.args[0]));
                }
                translate.addToOut("  jal _print");
                break;
            case "HeapAllocZ":
                if (b.args[0] instanceof VLitInt) {
                    translate.addToOut("  li $a0 " + operand(b.args[0]));
                } else {
                    translate.addToOut("  move $a0 " + operand(b.args[0]));
                }
                translate.addToOut("  jal _heapAlloc");
                if (dest != null) {
                    translate.addToOut("  move " + dest + " $v0");
                }
                break;
            case "Error":
                String msg = b.args[0].toString();
                if (msg.contains("null pointer")) {
                    translate.addToOut("  la $a0 _str0");
                } else {
                    translate.addToOut("  la $a0 _str1");
                }
                translate.addToOut("  j _error");
                break;
        }
    }

    public void visit(VMemRead r) throws Throwable {
        String dest = reg(r.dest);
        if (r.source instanceof VMemRef.Global) {
            VMemRef.Global g = (VMemRef.Global) r.source;
            String base = memBase(g.base);
            int offset = g.byteOffset;
            translate.addToOut("  lw " + dest + " " + offset + "(" + base + ")");
        } else if (r.source instanceof VMemRef.Stack) {
            VMemRef.Stack s = (VMemRef.Stack) r.source;
            switch (s.region) {
                case In:
                    translate.addToOut("  lw " + dest + " " + (s.index * 4) + "($fp)");
                    break;
                case Out:
                    translate.addToOut("  lw " + s + " " + (s.index * 4) + "($sp)");
                    break;
                case Local:
                    translate.addToOut("  lw " + dest + " -" + ((s.index + 1) * 4 + 8) + "($fp)");
                    break;
            }
        }
    }

    public void visit(VMemWrite w) throws Throwable {
        String src;
        if (w.dest instanceof VMemRef.Global) {
            VMemRef.Global g = (VMemRef.Global) w.dest;
            String base = memBase(g.base);
            int offset = g.byteOffset;
            src = operand(w.source);
            if (w.source instanceof VLabelRef) {
                translate.addToOut("  la $t9 " + stripColon(src));
                translate.addToOut("  sw $t9 " + offset + "(" + base + ")");
            } else if (w.source instanceof VLitInt) {
                translate.addToOut("  li $t9 " + src);
                translate.addToOut("  sw $t9 " + offset + "(" + base + ")");
            } else {
                translate.addToOut("  sw " + src + " " + offset + "(" + base + ")");
            }
        } else if (w.dest instanceof VMemRef.Stack) {
            VMemRef.Stack s = (VMemRef.Stack) w.dest;
            src = operand(w.source);
            if (w.source instanceof VLitInt) {
                translate.addToOut("  li $t9 " + src);
                src = "$t9";
            } else if (w.source instanceof VLabelRef) {
                translate.addToOut("  la $t9 " + stripColon(src));
                src = "$t9";
            }
            switch (s.region) {
                case Out:
                    translate.addToOut("  sw " + src + " " + (s.index * 4) + "($sp)");
                    break;
                case In:
                    translate.addToOut("  lw " + src + " " + (s.index * 4) + "($fp)");
                    break;
                case Local:
                    translate.addToOut("  sw " + src + " -" + ((s.index + 1) * 4 + 8) + "($fp)");
                    break;
            }
        }
    }

    public void visit(VBranch b) throws Throwable {
        String cond = operand(b.value);
        String label = b.target.ident;
        if (b.positive) {
            translate.addToOut("  bnez " + cond + " " + label);
        } else {
            translate.addToOut("  beqz " + cond + " " + label);
        }
    }

    public void visit(VGoto g) throws Throwable {
        if (g.target instanceof VAddr.Label) {
            String label = stripColon(((VAddr.Label<?>) g.target).label.toString());
            translate.addToOut("  j " + label);
        } else {
            translate.addToOut("  jr " + reg(((VAddr.Var<?>) g.target).var));
        }
    }

    public void visit(VReturn r) throws Throwable {
        if (r.value != null) {
            translate.addToOut("  move $v0 " + operand(r.value));
        }
        translate.getEpi(currFunc);
    }

    /******************* HELPER METHODS *******************/

    private String reg(VVarRef v) {
        return v.toString();
    }

    private String operand(VOperand op) {
        if (op instanceof VVarRef)
            return op.toString();
        return op.toString();
    }

    private String load(VOperand op, String temp) {
        if (op instanceof VLitInt) {
            translate.addToOut("  li " + temp + " " + op.toString());
            return temp;
        }

        return op.toString();
    }

    private String memBase(VAddr<?> base) {
        if (base instanceof VAddr.Var) {
            return ((VAddr.Var<?>) base).var.toString();
        }
        String label = stripColon(((VAddr.Label<?>) base).label.toString());
        translate.addToOut("  la $t9 " + label);
        return "$t9";
    }

    private String stripColon(String s) {
        if (s.startsWith(":"))
            return s.substring(1);
        return s;
    }
}