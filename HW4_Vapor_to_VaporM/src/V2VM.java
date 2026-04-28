import cs132.util.*;
import cs132.vapor.ast.*;
import cs132.vapor.ast.VBuiltIn.Op;
import cs132.vapor.parser.*;

import java.io.InputStreamReader;
import static java.lang.System.in;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedList;

public class V2VM {
    public static void main(String[] args) throws IOException {
        Op[] ops = {
                Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS,
                Op.PrintIntS, Op.HeapAllocZ, Op.Error,
        };
        boolean allowLocals = true;
        String[] registers = null;
        boolean allowStack = false;

        VaporProgram program;
        try {
            program = VaporParser.run(new InputStreamReader(in), 1, 1,
                    java.util.Arrays.asList(ops),
                    allowLocals, registers, allowStack);
            VMTranslator vmt = new VMTranslator();
            VMVisitor vmv = new VMVisitor(vmt);
            System.out.println("REGISTER NUMBER: " + program.registers.length);
            for (VFunction fun : program.functions) {
                VMSymbolTable symt = new VMSymbolTable(program.registers);

                symt.buildTable(fun);
                vmv.setSymt(symt);

                System.out.println("func " + fun.ident + " [in " + fun.params.length + ", out" + symt.outCount
                        + ", local " + symt.locals.size() + "]");
            }

        } catch (ProblemException ex) {
            System.err.println(ex.getMessage());
        }

    }
}