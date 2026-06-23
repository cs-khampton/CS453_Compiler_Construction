import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import cs132.vapor.ast.VCodeLabel;
import cs132.vapor.ast.VDataSegment;
import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VOperand;
import cs132.vapor.ast.VVarRef;
import cs132.vapor.ast.VaporProgram;

public class MTranslator {
    private PrintWriter out;
    private VaporProgram program;
    private MVisitor visitor;

    public MTranslator(VaporProgram program) {
        this.out = new PrintWriter(System.out);
        this.program = program;
        this.visitor = new MVisitor(this);
    }

    public void addToOut(String line) {
        out.println(line);
        out.flush();
    }

    public void translate() throws Throwable {
        getDataSegs();
        getTextPreamb();
        getHelp();
        getFunctions();
        getStrData();
    }

    public void getTextPreamb() {
        addToOut(".text");
        addToOut("");
        addToOut("  jal Main");
        addToOut("  li $v0 10");
        addToOut("  syscall");
        addToOut("");
    }

    public void getDataSegs() {
        addToOut(".data");
        addToOut("");
        for (VDataSegment ds : this.program.dataSegments) {
            addToOut(ds.ident + ":");
            for (VOperand.Static s : ds.values) {
                String val = s.toString();
                if (val.startsWith(":")) {
                    val = val.substring(1);
                    addToOut("  " + val);
                }
            }
            addToOut("");
        }
    }

    public void getFunctions() throws Throwable {
        for (VFunction func : program.functions) {
            visitor.setFunc(func);
            getFuncHeader(func);
            getBody(func);
        }
    }

    public void getFuncHeader(VFunction func) throws Throwable {
        String name = func.ident.equals("Main") ? "Main" : func.ident;
        int lSize = func.stack.local * 4;
        int oSize = func.stack.out * 4;
        int sRegs = countS(func);
        int fs = 8 + lSize + oSize + (sRegs * 4);

        addToOut(name + ":");
        addToOut("  sw $fp -8($sp)");
        addToOut("  move $fp $sp");
        addToOut("  subu $sp $sp " + fs);
        addToOut("  sw $ra -4($fp)");

        for (int i = 0; i < sRegs; i++) {
            addToOut("  sw $s" + i + " " + (i * 4) + "($sp)");
        }
    }

    public int calcFs(VFunction func) {
        return 8 + (func.stack.local * 4) + (func.stack.out * 4);
    }

    public void getBody(VFunction func) throws Throwable {
        java.util.Map<Integer, java.util.List<String>> labels = new java.util.HashMap<>();
        for (VCodeLabel vc : func.labels) {
            if (!labels.containsKey(vc.instrIndex)) {
                labels.put(vc.instrIndex, new java.util.ArrayList<String>());
            }
            labels.get(vc.instrIndex).add(vc.ident);
        }
        for (int i = 0; i < func.body.length; i++) {
            if (labels.containsKey(i)) {
                for (String label : labels.get(i)) {
                    addToOut(label + ":");
                }
            }
            func.body[i].accept(visitor);
        }
        if (labels.containsKey(func.body.length)) {
            for (String label : labels.get(func.body.length)) {
                addToOut(label + ":");
            }
        }
    }

    public void getHelp() {
        addToOut("_print:");
        addToOut("  li $v0 1");
        addToOut("  syscall");
        addToOut("  la $a0 _newline");
        addToOut("  li $v0 4");
        addToOut("  syscall");
        addToOut("  jr $ra");
        addToOut("");
        addToOut("_error:");
        addToOut("  li $v0 4");
        addToOut("  syscall");
        addToOut("  li $v0 10");
        addToOut("  syscall");
        addToOut("");
        addToOut("_heapAlloc:");
        addToOut("  li $v0 9");
        addToOut("  syscall");
        addToOut("  jr $ra");
        addToOut("");
    }

    public void getEpi(VFunction func) {
        int localSize = func.stack.local * 4;
        int outSize = func.stack.out * 4;
        int sRegs = countS(func);
        int frameSize = 8 + localSize + outSize + (sRegs * 4);

        // restore $s registers
        for (int i = 0; i < sRegs; i++) {
            addToOut("  lw $s" + i + " " + (-(frameSize - 8 - i * 4)) + "($fp)");
        }
        addToOut("  lw $ra -4($fp)");
        addToOut("  lw $fp -8($fp)");
        addToOut("  addu $sp $sp " + frameSize);
        addToOut("  jr $ra");
    }

    private void getStrData() {
        addToOut(".data");
        addToOut(".align 0");
        addToOut("_newline: .asciiz \"\\n\"");
        addToOut("_str0: .asciiz \"null pointer\\n\"");
        addToOut("_str1: .asciiz \"array index out of bounds\\n\"");
    }

    private int countS(VFunction func) {
        int max = -1;
        for (String var : func.vars) {
            if (var.startsWith("s")) {
                try {
                    int n = Integer.parseInt(var.substring(1));
                    if (n > max)
                        max = n;
                } catch (NumberFormatException e) {
                }
            }
        }
        return max + 1;
    }
}