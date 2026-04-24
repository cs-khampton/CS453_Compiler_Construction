import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import syntaxtree.AllocationExpression;
import syntaxtree.AndExpression;
import syntaxtree.ArrayAllocationExpression;
import syntaxtree.ArrayAssignmentStatement;
import syntaxtree.ArrayLength;
import syntaxtree.ArrayLookup;
import syntaxtree.ArrayType;
import syntaxtree.AssignmentStatement;
import syntaxtree.Block;
import syntaxtree.BooleanType;
import syntaxtree.BracketExpression;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.CompareExpression;
import syntaxtree.Expression;
import syntaxtree.ExpressionList;
import syntaxtree.ExpressionRest;
import syntaxtree.FalseLiteral;
import syntaxtree.FormalParameter;
import syntaxtree.FormalParameterList;
import syntaxtree.FormalParameterRest;
import syntaxtree.Goal;
import syntaxtree.Identifier;
import syntaxtree.IfStatement;
import syntaxtree.IntegerLiteral;
import syntaxtree.IntegerType;
import syntaxtree.MainClass;
import syntaxtree.MessageSend;
import syntaxtree.MethodDeclaration;
import syntaxtree.MinusExpression;
import syntaxtree.Node;
import syntaxtree.NotExpression;
import syntaxtree.PlusExpression;
import syntaxtree.PrimaryExpression;
import syntaxtree.PrintStatement;
import syntaxtree.Statement;
import syntaxtree.ThisExpression;
import syntaxtree.TimesExpression;
import syntaxtree.TrueLiteral;
import syntaxtree.Type;
import syntaxtree.TypeDeclaration;
import syntaxtree.VarDeclaration;
import syntaxtree.WhileStatement;
import visitor.GJDepthFirst;

public class VVisitor extends GJDepthFirst<MyType, HashMap<String, String>> {

    VTranslator translate;
    private String currClass;
    private String currMethod;
    private int tempCount;
    private int labelCount;
    private int nullCount;
    private int indent;

    public VVisitor(VTranslator translator) {
        this.translate = translator;
        currMethod = null;
        currClass = null;

        indent = 0;

        tempCount = 0;
        labelCount = 0;
        nullCount = 0;
    }

    /*
     * Goal
     * f0: MainClass()
     * f1: TypeDeclaration()*
     * f2: <EOF>
     */
    public MyType visit(Goal n, HashMap<String, String> arg) {

        if (n.f1.present()) {
            // print all present methods and classes
            for (Node no : n.f1.nodes) {
                printMT((TypeDeclaration) no, arg);
            }
        }
        n.f0.accept(this, arg); // MainClass()
        n.f1.accept(this, arg); // TypeDeclaration()*
        // <EOF>
        return null;
    }

    /*
     * MainClass
     * f0: "class"
     * f1: Identifier()
     * f2: "{"
     * f3: "public"
     * f4: "static"
     * f5: "void"
     * f6: "main"
     * f7: "("
     * f8: "String"
     * f9: "["
     * f10: "]"
     * f11: Identifier()
     * f12: ")"
     * f13: "{"
     * f14: VarDeclaration()*
     * f15: Statement()*
     * f16: "}"
     * f17: "}"
     */
    public MyType visit(MainClass mc, HashMap<String, String> arg) {
        currClass = mc.f1.f0.toString();
        currMethod = mc.f6.toString();

        tempCount = 0;

        // "Main" per given Factorial.vapor
        translate.addToOut(formatIndent("func Main()"));
        indent();
        mc.f14.accept(this, arg);
        mc.f15.accept(this, arg);

        translate.addToOut(formatIndent("ret"));
        deIndent();

        translate.addToOut("");
        currClass = null;
        currMethod = null;
        return null;
    }

    /*
     * TypeDeclaration
     * f0: ClassDeclaration() | ClassExtendsDeclaration()
     */
    public MyType visit(TypeDeclaration d, HashMap<String, String> arg) {
        d.f0.choice.accept(this, arg);
        return null;
    }

    /*
     * ClassDeclaration
     * f0: "class"
     * f1: Identifier()
     * f2: "{"
     * f3: VarDeclaration()*
     * f4: MethodDeclaration()*
     * f5: "}"
     */
    public MyType visit(ClassDeclaration cd, HashMap<String, String> arg) {
        currClass = cd.f1.f0.toString();

        cd.f3.accept(this, arg);
        cd.f4.accept(this, arg);
        currMethod = null;
        currClass = null;
        return null;
    }

    /*
     * ClassExtendsDeclaration
     * f0: "class"
     * f1: Identifier()
     * f2: "extends"
     * f3: Identifier()
     * f4: "{"
     * f5: VarDeclarataion()*
     * f6: MethodDeclaration()*
     * f7: "}"
     */
    public MyType visit(ClassExtendsDeclaration ced, HashMap<String, String> arg) {
        currClass = ced.f1.f0.toString();

        ced.f5.accept(this, arg);
        ced.f6.accept(this, arg);
        currClass = null;
        currMethod = null;
        return null;
    }

    /*
     * VarDeclaration
     * f0: Type()
     * f1: Identifier()
     * f2: ";"
     */
    public MyType visit(VarDeclaration vd, HashMap<String, String> arg) {
        // No VarDeclaration in Vapor. No need to print
        return null;
    }

    /*
     * MethodDeclaration
     * f0: "public"
     * f1: Type()
     * f2: Identifier()
     * f3: "("
     * f4: FormalParameterList()?
     * f5: ")"
     * f6: "{"
     * f7: VarDeclaration()*
     * f8: Statement()*
     * f9: "return"
     * f10: Expression()
     * f11: ";"
     * f12: "}"
     */
    public MyType visit(MethodDeclaration md, HashMap<String, String> arg) {
        String methodName = md.f2.f0.toString();
        currMethod = methodName;
        String params = "this";
        tempCount = 0;

        // Check if FPL exists, grab all params
        if (md.f4.present()) {
            FormalParameterList fp = (FormalParameterList) md.f4.node;
            params += " " + fp.f0.f1.f0.toString();
            for (Node n : fp.f1.nodes) {
                FormalParameterRest fr = (FormalParameterRest) n;
                params += " " + fr.f1.f1.f0.toString();
            }
        }
        // add params
        translate.addToOut(formatIndent("func " + currClass + "." + methodName + "(" + params + ")"));
        indent();

        md.f7.accept(this, arg); // VarDeclaration()
        md.f8.accept(this, arg); // Statement()*

        MyType retVal = md.f10.f0.choice.accept(this, arg);
        translate.addToOut(formatIndent("ret " + (retVal == null ? "0" : retVal.type)));
        deIndent();
        translate.addToOut("");

        currMethod = null;
        return null;
    }

    /*
     * FormalParameterList
     * f0: FormalParameter()
     * f1: FormalParameterRest()*
     */
    public MyType visit(FormalParameterList fpl, HashMap<String, String> arg) {
        // Grabbing these in MethodDeclaration
        return null;
    }

    /*
     * FormalParameter
     * f0: Type()
     * f1: Identifier()
     */
    public MyType visit(FormalParameter fp, HashMap<String, String> arg) {
        // Grabbing these in MethodDeclaration
        return null;
    }

    /*
     * FormalParameterRest
     * f0: ","
     * f1: FormalParameter()
     */
    public MyType visit(FormalParameterRest fpr, HashMap<String, String> arg) {
        // Grabbing these in MethodDeclaration
        return null;
    }

    /*
     * Type
     * f0: ArrayType() | BooleanType() | Identifier()
     */
    public MyType visit(Type t, HashMap<String, String> arg) {
        t.f0.accept(this, arg);
        return null;
    }

    /*
     * ArrayType
     * f0: "int"
     * f1: "["
     * f2: "]"
     */
    public MyType visit(ArrayType at, HashMap<String, String> arg) {
        return null;
    }

    /*
     * BooleanType
     * f0: "boolean"
     */
    public MyType visit(BooleanType bt, HashMap<String, String> arg) {
        return null;
    }

    /*
     * IntegerType
     * f0: "int"
     */
    public MyType visit(IntegerType it, HashMap<String, String> arg) {
        return null;
    }

    /*
     * Statement
     * f0: Block() | AssignmentStatement() | ArrayAssignmentStatement() |
     * IfStatement() | WhileStatement() | PrintStatement()
     */
    public MyType visit(Statement s, HashMap<String, String> arg) {
        s.f0.choice.accept(this, arg);
        return null;
    }

    /*
     * Block
     * f0: "{"
     * f1: Statement()*
     * f2: "}"
     */
    public MyType visit(Block b, HashMap<String, String> arg) {
        b.f1.accept(this, arg);
        return null;
    }

    /*
     * AssignmentStatement
     * f0: Identifier()
     * f1: "="
     * f2: Expression()
     * f3: ";"
     */
    public MyType visit(AssignmentStatement as, HashMap<String, String> arg) {
        // XXX: WORKING ON THIS ONE
        String id = as.f0.f0.toString();

        MyType exprType = as.f2.f0.choice.accept(this, arg);
        if (exprType == null) {
            return null;
        }

        String instKey = resiKey(currClass, id, arg);
        if (instKey != null && instKey.contains(":instVariable:")) {
            int offset = getOffset(currClass, id, arg);
            translate.addToOut(formatIndent("[this+" + offset + "] = " + exprType.type));
        } else {
            translate.addToOut(formatIndent(id + " = " + exprType.type));
        }
        return null;
    }

    /*
     * ArrayAssignmentStatement
     * f0: Identifier()
     * f1: "["
     * f2: Expression()
     * f3: "]"
     * f4: "="
     * f5: Expression()
     * f6: ";"
     */
    public MyType visit(ArrayAssignmentStatement aas, HashMap<String, String> arg) {
        String id = aas.f0.f0.toString();
        // XXX: Working on this
        // check for int[] or String[] -- String[] from MainClass declaration

        // Check f2 and f5 are both type "int"
        MyType index = aas.f2.f0.choice.accept(this, arg);
        MyType exp = aas.f5.f0.choice.accept(this, arg);
        if (index == null || exp == null) {
            return null;
        }
        String t0 = newTemp();
        String t1 = newTemp();
        String boundLabel = "bounds" + nullCount++;
        translate.addToOut(formatIndent(t0 + " = [" + id + "]"));
        translate.addToOut(formatIndent(t1 + " = LtS(" + index.type + " " + t0 + ")"));
        translate.addToOut(formatIndent("if " + t1 + " goto :" + boundLabel + "_ok"));
        indent();
        translate.addToOut(formatIndent("Error(\"array index out of bounds\")"));
        deIndent();
        translate.addToOut(formatIndent(boundLabel + "_ok:"));

        String address = newTemp();
        translate.addToOut(formatIndent(address + " = MulS(" + index.type + " 4)"));
        translate.addToOut(formatIndent(address + " = Add(" + address + " " + id + ")"));
        translate.addToOut(formatIndent("[" + address + "+4] = " + exp.type));
        return null;
    }

    /*
     * IfStatement
     * f0: "if"
     * f1: "("
     * f2: Expression()
     * f3: ")"
     * f4: Statement()
     * f5: "else"
     * f6: Statement()
     */
    public MyType visit(IfStatement is, HashMap<String, String> arg) {
        String label = "if" + labelCount++;
        String ieLabel = label + "_else";
        String endLabel = label + "_end";

        MyType con = is.f2.f0.choice.accept(this, arg);
        if (con == null) {
            return null;
        }
        translate.addToOut(formatIndent("if0 " + con.type + " goto :" + ieLabel));
        indent();
        is.f4.f0.choice.accept(this, arg);
        translate.addToOut(formatIndent("goto :" + endLabel));
        deIndent();
        translate.addToOut(formatIndent(ieLabel + ":"));
        indent();
        is.f6.f0.choice.accept(this, arg);
        deIndent();
        translate.addToOut(formatIndent(endLabel + ":"));
        return null;
    }

    /*
     * WhileStatement
     * f0: "while"
     * f1: "("
     * f2: Expression()
     * f3: ")"
     * f4: Statement()
     */
    public MyType visit(WhileStatement ws, HashMap<String, String> arg) {
        String label = "while" + labelCount++;
        String topLabel = label + "_top";
        String endLabel = label + "_end";

        translate.addToOut(formatIndent(topLabel + ":"));
        indent();

        MyType con = ws.f2.f0.choice.accept(this, arg);
        if (con == null) {
            return null;
        }
        translate.addToOut(formatIndent("if0 " + con.type + " goto :" + endLabel));
        ws.f4.f0.choice.accept(this, arg);
        translate.addToOut(formatIndent("goto :" + topLabel));
        deIndent();
        translate.addToOut(formatIndent(endLabel + ":"));
        return null;
    }

    /*
     * PrintStatement
     * f0: "System.out.println"
     * f1: "("
     * f2: Expression()
     * f3: ")"
     * f4: ";"
     */
    public MyType visit(PrintStatement ps, HashMap<String, String> arg) {
        MyType t2 = ps.f2.f0.choice.accept(this, arg);
        if (t2 != null) {
            translate.addToOut(formatIndent("PrintIntS(" + t2.type + ")"));
        }
        return null;
    }

    /*
     * Expression
     * f0: AndExpression() | CompareExpression() | PlusExpression() |
     * MinusExpression() | TimesExpression() | ArrayLookup() | ArrayLength() |
     * MessageSend() | PrimaryExpression()
     */
    public MyType visit(Expression e, HashMap<String, String> arg) {
        return e.f0.choice.accept(this, arg);
    }

    /*
     * AndExpression
     * f0: PrimaryExpression()
     * f1: "&&"
     * f2: PrimaryExpression()
     */
    public MyType visit(AndExpression ae, HashMap<String, String> arg) {
        MyType t0 = getOp(ae.f0, arg);
        MyType t2 = getOp(ae.f2, arg);
        if (t0 == null || t2 == null) {
            return null;
        }

        String temp = newTemp();
        String label = "ss" + labelCount++;
        String elseLabel = "ss" + labelCount + "_else";
        String endLabel = "ss" + labelCount + "_end";

        translate.addToOut(formatIndent("if0 " + t0.type + " goto :" + elseLabel));
        indent();
        translate.addToOut(formatIndent("if0 " + t2.type + " goto :" + elseLabel));
        indent();
        translate.addToOut(formatIndent(temp + " = 1"));
        translate.addToOut(formatIndent("goto :" + endLabel));
        deIndent();
        deIndent();
        translate.addToOut(formatIndent(elseLabel + ":"));
        indent();
        translate.addToOut(formatIndent(temp + " = 0"));
        deIndent();
        translate.addToOut(formatIndent(endLabel + ":"));
        return new MyType(temp);
    }

    /*
     * CompareExpression
     * f0: PrimaryExpression()
     * f1: "<"
     * f2: PrimaryExpression()
     */
    public MyType visit(CompareExpression ce, HashMap<String, String> arg) {
        MyType t0 = getOp(ce.f0, arg);
        MyType t2 = getOp(ce.f2, arg);
        if (t0 == null || t2 == null) {
            return null;
        }
        String temp = newTemp();
        translate.addToOut(formatIndent(temp + " = LtS(" + t0.type + " " + t2.type + ")"));
        return new MyType(temp);
    }

    /*
     * PlusExpression
     * f0: PrimaryExpression()
     * f1: "+"
     * f2: PrimaryExpression()
     */
    public MyType visit(PlusExpression pe, HashMap<String, String> arg) {
        MyType t0 = getOp(pe.f0, arg);
        MyType t2 = getOp(pe.f2, arg);
        if (t0 == null || t2 == null) {
            return null;
        }
        String temp = newTemp();
        translate.addToOut(formatIndent(temp + " = Add(" + t0.type + " " + t2.type + ")"));
        return new MyType(temp);
    }

    /*
     * MinusExpression
     * f0: PrimaryExpression()
     * f1: "-"
     * f2: PrimaryExpression()
     */
    public MyType visit(MinusExpression me, HashMap<String, String> arg) {
        MyType t0 = getOp(me.f0, arg);
        MyType t2 = getOp(me.f2, arg);
        if (t0 == null || t2 == null) {
            return null;
        }
        String temp = newTemp();
        translate.addToOut(formatIndent(temp + " = Sub(" + t0.type + " " + t2.type + ")"));
        return new MyType(temp);
    }

    /*
     * TimesExpression
     * f0: PrimaryExpression()
     * f1: "*"
     * f2: PrimaryExpression()
     */
    public MyType visit(TimesExpression te, HashMap<String, String> arg) {
        MyType t0 = getOp(te.f0, arg);
        MyType t2 = getOp(te.f2, arg);
        if (t0 == null || t2 == null) {
            return null;
        }
        String temp = newTemp();
        translate.addToOut(formatIndent(temp + " = MulS(" + t0.type + " " + t2.type + ")"));
        return new MyType(temp);
    }

    /*
     * ArrayLookup
     * f0: PrimaryExpression()
     * f1: "["
     * f2: PrimaryExpression()
     * f3: "]"
     */
    public MyType visit(ArrayLookup al, HashMap<String, String> arg) {
        // FIXME: Working on
        MyType t0 = getOp(al.f0, arg);
        MyType t2 = al.f2.f0.choice.accept(this, arg);

        if (t0 == null) {
            return null;
        }
        String nullLabel = "null" + nullCount++;
        translate.addToOut(formatIndent("if " + t2.type + " goto :" + nullLabel));
        indent();
        translate.addToOut(formatIndent(nullLabel + ":"));
        return new MyType("int");
    }

    /*
     * ArrayLength
     * f0: PrimaryExpression()
     * f1: "."
     * f2: "length"
     */
    public MyType visit(ArrayLength al, HashMap<String, String> arg) {
        // TODO:
        MyType t0 = al.f0.f0.choice.accept(this, arg);
        return new MyType("int");
    }

    /*
     * MessageSend
     * f0: PrimaryExpression()
     * f1: "."
     * f2: Identifier()
     * f3: "("
     * f4: ExpressionList()?
     * f5: ")"
     */
    public MyType visit(MessageSend ms, HashMap<String, String> arg) {
        // TODO:
        MyType rec = ms.f0.f0.choice.accept(this, arg);
        String methodName = ms.f2.f0.toString();

        String mk = resmKey(rec.type, methodName, arg);

        // count number of params
        int count = 0;
        for (String k : arg.keySet()) {
            if (k.startsWith(mk + ":methodParam:[")) {
                count++;
            }
        }
        int act = 0;

        if (ms.f4.present()) {
            ExpressionList el = (ExpressionList) ms.f4.node;
            act = 1 + el.f1.nodes.size();
            ms.f4.accept(this, arg);
        }

        if (act != count) {

            // errorMessage("MessageSend", mk);
        }
        return new MyType(arg.get(mk + ":returnType"));
    }

    /*
     * ExpressionList
     * f0: Expression()
     * f1: ExpressionRest()
     */
    public MyType visit(ExpressionList el, HashMap<String, String> arg) {
        return null;
    }

    /*
     * ExpressionRest
     * f0: ","
     * f1: Expression()
     */
    public MyType visit(ExpressionRest er, HashMap<String, String> arg) {
        // TODO: Handle in MessageSend
        return null;
    }

    /*
     * PrimaryExpression()
     * f0: IntegerLiteral() | TrueLiteral() | FalseLiteral() | Identifier() |
     * ThisExpression() | ArrayAllocationExpression() | AllocationExpression() |
     * NotExpression() | BracketExpression()
     */
    public MyType visit(PrimaryExpression pe, HashMap<String, String> arg) {
        return pe.f0.choice.accept(this, arg);
    }

    /*
     * IntegerLiteral
     * f0: <INTEGER_LITERAL>
     */
    public MyType visit(IntegerLiteral il, HashMap<String, String> arg) {
        return new MyType(il.f0.toString());
    }

    /*
     * TrueLiteral
     * f0: "true"
     */
    public MyType visit(TrueLiteral tl, HashMap<String, String> arg) {
        return new MyType("1");
    }

    /*
     * FalseLiteral
     * f0: "false"
     */ public MyType visit(FalseLiteral fl, HashMap<String, String> arg) {
        return new MyType("0");
    }

    /*
     * Identifier
     * f0: <IDENTIFIER>
     */

    public MyType visit(Identifier id, HashMap<String, String> arg) {
        String name = id.f0.toString();
        String instKey = resiKey(currClass, name, arg);
        if (instKey != null && instKey.contains(":instVariable:")) {
            int offset = getOffset(currClass, name, arg);
            String t = newTemp();
            translate.addToOut(formatIndent(t + " = [this+" + offset + "]"));
            return new MyType(t);
        }
        // errorMessage("Identifier", key);
        return new MyType(name);
    }

    /*
     * ThisExpression
     * f0: "this"
     */
    public MyType visit(ThisExpression te, HashMap<String, String> arg) {
        return new MyType("this");
    }

    /*
     * ArrayAllocationExpression
     * f0: "new"
     * f1: "int"
     * f2: "["
     * f3: Expression()
     * f4: "]"
     */
    public MyType visit(ArrayAllocationExpression aae, HashMap<String, String> arg) {
        // TODO:
        MyType type = getOp(aae.f3, arg);

        return new MyType("int[]");
    }

    /*
     * AllocationExpression
     * f0: "new"
     * f1: Identifier()
     * f2: "("
     * f3: ")"
     */
    public MyType visit(AllocationExpression ae, HashMap<String, String> arg) {
        // FIXME:
        String className = ae.f1.f0.toString();
        String key = classKey(className);

        if (!arg.containsKey(key)) {
            return null;
        }
        int allocSize = getAllocationSize(className, arg);
        String temp = newTemp();
        translate.addToOut(formatIndent(temp + " = HeapAllocZ(" + allocSize + ")"));

        translate.addToOut(formatIndent("[" + temp + "] = :vmt_" + className));
        return new MyType(temp);
    }

    /*
     * NotExpression
     * f0: "!"
     * f1: Expression()
     */
    public MyType visit(NotExpression ne, HashMap<String, String> arg) {
        MyType exprType = ne.f1.f0.choice.accept(this, arg);
        if (exprType == null) {
            return null;
        }
        String temp = newTemp();
        translate.addToOut(formatIndent(temp + " = Sub(1 " + exprType.type + ")"));
        return new MyType(temp);
    }

    /*
     * BracketExpression
     * f0: "("
     * f1: Expression()
     * f2: ")"
     */
    public MyType visit(BracketExpression be, HashMap<String, String> arg) {
        return be.f1.f0.choice.accept(this, arg);
    }

    /********************* HELPER METHODS *****************/

    /************** FORMATTING ************/

    private String formatIndent(String message) {
        String ret = "";
        for (int i = 0; i < indent; i++) {
            ret += "  ";
        }
        ret += message;
        return ret;
    }

    private String stripKey(String k) {
        return k.substring(k.lastIndexOf('[') + 1, k.lastIndexOf(']'));
    }

    private String newTemp() {
        return "t." + tempCount++;
    }

    private void indent() {
        indent++;
    }

    private void deIndent() {
        indent--;
    }

    private void printMT(TypeDeclaration td, HashMap<String, String> arg) {
        String className;
        List<MethodDeclaration> methods = new ArrayList<>();
        if (td.f0.choice instanceof ClassDeclaration cd) {
            className = cd.f1.f0.toString();
            for (Node n : cd.f4.nodes) {
                methods.add((MethodDeclaration) n);
            }
        } else {
            ClassExtendsDeclaration ced = (ClassExtendsDeclaration) td.f0.choice;
            className = ced.f1.f0.toString();
            for (Node n : ced.f6.nodes) {
                methods.add((MethodDeclaration) n);
            }
        }

        translate.addToOut("const vmt_" + className);
        indent();
        for (MethodDeclaration m : methods) {
            String mName = m.f2.f0.toString();
            translate.addToOut("  :" + className + "." + mName);
        }

        deIndent();
        translate.addToOut("");
    }

    private int getAllocationSize(String name, HashMap<String, String> arg) {
        int count = 0;
        String curr = name;
        while (curr != null && !curr.equals("none")) {
            for (String k : arg.keySet()) {
                if (k.startsWith("class:[" + curr + "]:instVariable:[")) {
                    count++;
                }
            }
            String pk = parentClassKey(curr);
            curr = arg.containsKey(pk) ? arg.get(pk) : null; // make sure parent key exists before setting
        }
        return (count + 1) * 4;
    }

    private int getOffset(String className, String varName, HashMap<String, String> arg) {
        int offset = 4;
        String curr = className;
        while (curr != null && !curr.equals("none")) {
            for (String k : arg.keySet()) {
                if (k.startsWith("class:[" + curr + "]:instVariable:[")) {
                    // System.out.println(offset);
                    if (stripKey(k).equals(varName)) {
                        return offset;
                    }
                    offset += 4;
                }
            }
            String pk = parentClassKey(curr);
            curr = arg.containsKey(pk) ? arg.get(pk) : null;
        }
        return -1;
    }

    /************** CHOICES ************/

    private MyType getOp(Expression e, HashMap<String, String> arg) {
        return e.f0.choice.accept(this, arg);
    }

    private MyType getOp(PrimaryExpression p, HashMap<String, String> arg) {
        return p.f0.choice.accept(this, arg);
    }

    private String resiKey(String className, String var, HashMap<String, String> arg) {
        String curr = className;
        while (curr != null && !curr.equals("none")) {
            String key = instVarKey(curr, var);
            if (arg.containsKey(key))
                return key;
            String pk = parentClassKey(curr);
            if (arg.containsKey(pk)) {
                curr = arg.get(pk);
            } else {
                curr = null;
            }
        }
        return null;
    }

    private String resmKey(String className, String methodName, HashMap<String, String> arg) {
        String curr = className;
        while (curr != null && !curr.equals("none")) {
            String key = methodKey(curr, methodName);
            if (arg.containsKey(key)) {
                return key;
            }
            String pk = parentClassKey(curr);
            if (arg.containsKey(pk)) {
                curr = arg.get(pk);
            } else {
                curr = null;
            }
        }
        return null;
    }

    /************** KEY FORMATTING ************/

    private String classKey(String className) {
        return "class:[" + className + "]";
    }

    private String parentClassKey(String pcName) {
        return classKey(pcName) + ":parent";
    }

    private String methodKey(String className, String methodName) {
        return classKey(className) + ":method:[" + methodName + "]";
    }

    private String retTypeKey(String className, String methodName, String retType) {
        return methodKey(className, methodName) + ":returnType";
    }

    private String mParamKey(String className, String methodName, String paramName) {
        return methodKey(className, methodName) + ":methodParam:[" + paramName + "]";
    }

    private String localKey(String className, String methodName, String paramName) {
        return methodKey(className, methodName) + ":localParams:[" + paramName + "]";
    }

    private String instVarKey(String className, String varName) {
        return classKey(className) + ":instVariable:[" + varName + "]";
    }
}