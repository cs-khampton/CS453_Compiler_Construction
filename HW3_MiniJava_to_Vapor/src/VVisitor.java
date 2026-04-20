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

    private String currClass = null;
    private String currMethod = null;
    VTranslator translate;
    private int tempNum;
    private int labelNum;
    private int indent = 0;

    public VVisitor(VTranslator translator) {
        this.translate = translator;
        currMethod = null;
        currClass = null;
        tempNum = 0;
        labelNum = 0;
    }

    /*
     * Goal
     * f0: MainClass()
     * f1: TypeDeclaration()*
     * f2: <EOF>
     */
    public MyType visit(Goal n, HashMap<String, String> arg) {
        for (String k : arg.keySet()) {
            if (k.matches("class:\\[.*\\]") && !k.contains(":")) {
                if (arg.get(k).equals("TypeError")) {
                    // errorMessage("Goal", k);
                    return null;
                }
            }
        }
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
        String className = mc.f1.f0.toString();
        String methodName = mc.f6.toString();

        currClass = className;
        currMethod = methodName;

        tempNum = 0;

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
        String className = cd.f1.f0.toString();
        currClass = className;
        String key;
        key = classKey(className);
        if (!arg.containsKey(key) || arg.get(key).equals("TypeError")) {
            // errorMessage("ClassDeclaration", key);
            currMethod = null;
            currClass = null;
            return null;
        }
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
        String className = ced.f1.f0.toString();
        String extendName = ced.f3.f0.toString();
        String classExtKey = classKey(extendName);

        currClass = className;

        String key = parentClassKey(extendName);
        // check key exists, className != extendName, extendName key exists
        if (!(arg.containsKey(key)) || className.equals(extendName) || !arg.containsKey(classExtKey)
                || arg.get(key).equals("TypeError")) {
            // errorMessage("ClassExtendsDeclaration", key);
            return null;
        }
        ced.f5.accept(this, arg);
        ced.f6.accept(this, arg);
        currClass = null;
        currMethod = null;

        // check parent method signature if duplicate method
        for (String k : arg.keySet()) {
            if (k.startsWith("class:[" + className + "]:method:[") && k.endsWith("]")
                    && arg.get(k).equals("method")) {
                String mName = stripKey(k);
                String pk = resmKey(extendName, mName, arg);
                if (pk != null) {
                    // check the signatures are the same return type
                    String c = arg.get(methodKey(className, mName) + ":returnType");
                    String p = arg.get(pk + ":returnType");
                    if (c != null && !c.equals(p)) {
                        // errorMessage("ClassExtendsDeclaration", mName);
                        currClass = null;
                        return null;
                    }
                }
            }
        }
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
        currMethod = methodName;
        tempNum = 0;

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
        translate.addToOut("func " + currClass + "." + methodName + "(" + params + ")");
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
        fpl.f0.accept(this, arg);
        fpl.f1.accept(this, arg);
        return null;
    }

    /*
     * FormalParameter
     * f0: Type()
     * f1: Identifier()
     */
    public MyType visit(FormalParameter fp, HashMap<String, String> arg) {
        String fpName = fp.f1.f0.toString();
        String fpType = getTypeChoice(fp.f0);
        String key = "";
        if (currClass != null && currMethod != null) {
            key += mParamKey(currClass, currMethod, fpName);
        }

        if (!arg.containsKey(key)) {
            // errorMessage("FormalParameter", key);
            return null;
        }
        if (!arg.get(key).equals(fpType)) {
            // errorMessage("FormalParameter", key);
            return null;
        }
        return new MyType(fpType);
    }

    /*
     * FormalParameterRest
     * f0: ","
     * f1: FormalParameter()
     */
    public MyType visit(FormalParameterRest fpr, HashMap<String, String> arg) {
        fpr.f1.accept(this, arg);
        return null;
    }

    /*
     * Type
     * f0: ArrayType() | BooleanType() | Identifier()
     */
    public MyType visit(Type t, HashMap<String, String> arg) {
        t.f0.choice.accept(this, arg);
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
        String id = as.f0.f0.toString();
        String key = "";
        if (currClass != null && currMethod != null) {
            key = localKey(currClass, currMethod, id);
            if (!arg.containsKey(key)) {
                key = mParamKey(currClass, currMethod, id);
            }
        }

        if (!arg.containsKey(key) && currClass != null) {
            String temp = resiKey(currClass, id, arg);
            if (temp != null) {
                key = temp;
            }
        }
        if (!arg.containsKey(key)) {
            // errorMessage("AssignmentStatement", key);
            return null;
        }

        MyType idType = new MyType(arg.get(key));
        MyType exprType = as.f2.f0.choice.accept(this, arg);

        if (exprType == null || !isSub(exprType.type, idType.type, arg)) {
            // errorMessage("AssignmentStatement", key);
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
        String key = "";
        String id = aas.f0.f0.toString();
        if (currMethod != null && currClass != null) {
            key = localKey(currClass, currMethod, id);
            if (!arg.containsKey(key)) {
                key = mParamKey(currClass, currMethod, id);
            }
        }
        if (!arg.containsKey(key) && currClass != null) {
            String temp = resiKey(currClass, id, arg);
            if (temp != null) {
                key = temp;
            }
        }
        if (!arg.containsKey(key)) {
            // errorMessage("ArrayAssignmentStatement", key);
            return null;
        }
        String idType = arg.get(key);
        // check for int[] or String[] -- String[] from MainClassDeclaration
        if (idType == null || (!idType.equals("int[]") && !idType.equals("String[]"))) {
            // errorMessage("ArrayAssignmentStatement", key);
            return null;
        }
        // Check f2 and f5 are both type "int"
        MyType idx = aas.f2.f0.choice.accept(this, arg);
        if (idx == null || !(idx.type.equals("int"))) {
            // errorMessage("ArrayAssignmentStatement", key);
        }

        MyType exp = aas.f5.f0.choice.accept(this, arg);
        if (exp == null || !(exp.type.equals("int"))) {
            // errorMessage("ArrayAssignmentStatement", key);
        }
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
        MyType c = is.f2.f0.choice.accept(this, arg);
        if (c == null || !c.type.equals("boolean")) {
            // errorMessage("IfStatement", c == null ? "null" : c.type + " != boolean");
        }
        is.f4.f0.choice.accept(this, arg);
        is.f6.f0.choice.accept(this, arg);
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
        MyType c = ws.f2.f0.choice.accept(this, arg);
        if (c == null || !c.type.equals("boolean")) {
            // errorMessage("WhileStatement", c == null ? "null" : c.type + " != boolean");
            return null;
        }
        ws.f4.f0.choice.accept(this, arg);
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
        if (t2 == null || !t2.type.equals("int")) {
            // errorMessage("PrintStatement", t2 == null ? "null" : t2.type);
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

        if (t0 == null || t2 == null || !t0.type.equals("boolean") || !t2.type.equals("boolean")) {

            String l = t0 == null ? "null" : t0.type;
            String r = t2 == null ? "null" : t2.type;
            // errorMessage("AndExpression", l + " && " + r);
            return null;
        }
        return new MyType("boolean");
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
        if (t0 == null || t2 == null || !t0.type.equals("int") || !t2.type.equals("int")) {

            String l = t0 == null ? "null" : t0.type;
            String r = t2 == null ? "null" : t2.type;
            // errorMessage("CompareExpression", l + " < " + r);
            return null;
        }
        return new MyType("boolean");
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
        if (t0 == null || t2 == null || !t0.type.equals("int") || !t2.type.equals("int")) {

            String l = t0 == null ? "null" : t0.type;
            String r = t2 == null ? "null" : t2.type;
            // errorMessage("PlusExpression", l + " + " + r);
            return null;
        }
        return new MyType("int");
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
        if (t0 == null || t2 == null || !t0.type.equals("int") || !t2.type.equals("int")) {

            String l = t0 == null ? "null" : t0.type;
            String r = t2 == null ? "null" : t2.type;
            // errorMessage("PlusExpression", l + " - " + r);
            return null;
        }
        return new MyType("int");
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
        if (t0 == null || t2 == null || !t0.type.equals("int") || !t2.type.equals("int")) {

            String l = t0 == null ? "null" : t0.type;
            String r = t2 == null ? "null" : t2.type;
            // errorMessage("TimesExpression", l + " * " + r);
            return null;
        }
        return new MyType("int");
    }

    /*
     * ArrayLookup
     * f0: PrimaryExpression()
     * f1: "["
     * f2: PrimaryExpression()
     * f3: "]"
     */
    public MyType visit(ArrayLookup al, HashMap<String, String> arg) {
        MyType t0 = getOp(al.f0, arg);
        if (t0 == null || !t0.type.equals("int[]")) {

            // errorMessage("ArrayLookup", t0 == null ? "null" : t0.type);
            return null;
        }
        MyType t2 = al.f2.f0.choice.accept(this, arg);
        if (t2 == null || !t2.type.equals("int")) {

            // errorMessage("ArrayLookup", t2 == null ? "null" : t2.type);
            return null;
        }

        return new MyType("int");
    }

    /*
     * ArrayLength
     * f0: PrimaryExpression()
     * f1: "."
     * f2: "length"
     */
    public MyType visit(ArrayLength al, HashMap<String, String> arg) {
        MyType t0 = al.f0.f0.choice.accept(this, arg);
        if (t0 == null || !t0.type.equals("int[]")) {

            // errorMessage("ArrayLength", t0 == null ? "null" : t0.type + ".length");
            return null;
        }
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
        MyType rec = ms.f0.f0.choice.accept(this, arg);
        String methodName = ms.f2.f0.toString();
        if (rec == null) {

            // errorMessage("MessageSend", "null." + methodName);
            return null;
        }

        String mk = resmKey(rec.type, methodName, arg);
        if (mk == null) {

            // errorMessage("MessageSend", rec.type + "." + methodName);
            return null;
        }

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
        el.f0.f0.choice.accept(this, arg);
        el.f1.accept(this, arg);
        return null;
    }

    /*
     * ExpressionRest
     * f0: ","
     * f1: Expression()
     */
    public MyType visit(ExpressionRest er, HashMap<String, String> arg) {
        return er.f1.f0.choice.accept(this, arg);
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
        return new MyType("int");
    }

    /*
     * TrueLiteral
     * f0: "true"
     */
    public MyType visit(TrueLiteral tl, HashMap<String, String> arg) {
        return new MyType("boolean");
    }

    /*
     * FalseLiteral
     * f0: "false"
     */ public MyType visit(FalseLiteral fl, HashMap<String, String> arg) {
        return new MyType("boolean");
    }

    /*
     * Identifier
     * f0: <IDENTIFIER>
     */

    public MyType visit(Identifier id, HashMap<String, String> arg) {
        String name = id.f0.toString();
        String key = "";
        if (currClass != null && currMethod != null) {
            key = localKey(currClass, currMethod, name);
            if (arg.containsKey(key)) {
                return new MyType(arg.get(key));
            }
            key = mParamKey(currClass, currMethod, name);
            if (arg.containsKey(key)) {
                return new MyType(arg.get(key));
            }
        }
        if (currClass != null) {
            key = resiKey(currClass, name, arg);
            if (key != null) {
                return new MyType(arg.get(key));
            }
        }

        // errorMessage("Identifier", key);
        return null;
    }

    /*
     * ThisExpression
     * f0: "this"
     */
    public MyType visit(ThisExpression te, HashMap<String, String> arg) {
        if (currClass == null || !arg.containsKey(classKey(currClass))) {

            // errorMessage("ThisExpression", currClass);
            return null;
        }
        return new MyType(currClass);
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
        MyType type = getOp(aae.f3, arg);

        if (type == null || !type.type.equals("int")) {

            // errorMessage("ArrayAllocationExpression", type == null ? "null" : type.type);
            return null;
        }
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
        String className = ae.f1.f0.toString();
        String key = classKey(className);

        if (!arg.containsKey(key)) {
            return null;
        }
        int allocSize = getAllocationSize(className, arg);
        String temp = newTemp();
        translate.addToOut(formatIndent(temp + " = HeapAllocZ(" + allocSize + ")"));

        return new MyType(temp);
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

    /*
     * NotExpression
     * f0: "!"
     * f1: Expression()
     */
    public MyType visit(NotExpression ne, HashMap<String, String> arg) {
        MyType exprType = ne.f1.f0.choice.accept(this, arg);
        if (exprType == null || !exprType.type.equals("boolean")) {

            // errorMessage("NotExpression", exprType == null ? "null" : exprType.type + "
            // != boolean");
            return null;
        }
        return exprType;
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
        return "t." + tempNum++;
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

    /************** CHOICES ************/

    public String getExprChoice(Expression e) {
        Node choice = e.f0.choice;
        if (choice instanceof AndExpression) {
            return "AndExpression";
        } else if (choice instanceof CompareExpression) {
            return "CompareExpression";
        } else if (choice instanceof PlusExpression) {
            return "PlusExpression";
        } else if (choice instanceof MinusExpression) {
            return "MinusExpression";
        } else if (choice instanceof TimesExpression) {
            return "TimesExpression";
        } else if (choice instanceof ArrayLookup) {
            return "ArrayLookup";
        } else if (choice instanceof ArrayLength) {
            return "ArrayLength";
        } else if (choice instanceof MessageSend) {
            return "MessageSend";
        } else if (choice instanceof PrimaryExpression p) {
            String pec = getPrimeExpChoice(p);
            return pec;
        }
        return "invalid expression choice";
    }

    public String getPrimeExpChoice(PrimaryExpression p) {
        Node choice = p.f0.choice;
        if (choice instanceof IntegerLiteral i) {
            return i.f0.toString();
        } else if (choice instanceof TrueLiteral) {
            return "1";
        } else if (choice instanceof FalseLiteral) {
            return "0";
        } else if (choice instanceof Identifier i) {
            return newTemp();
        } else if (choice instanceof ThisExpression) {
            return "this";
        } else if (choice instanceof ArrayAllocationExpression) {
            return "ArrayAllocationExpression";
        } else if (choice instanceof AllocationExpression) {
            return "AllocationExpression";
        } else if (choice instanceof NotExpression) {
            return "NotExpression";
        } else if (choice instanceof BracketExpression) {
            return "BracketExpression";
        }
        return "invalid primary expression choice";
    }

    public MyType getOp(Expression e, HashMap<String, String> arg) {
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

    private boolean isSub(String t0, String t2, HashMap<String, String> arg) {
        if (t0.equals(t2)) {
            return true;
        }
        String curr = t0;
        while (curr != null && !curr.equals("none")) {
            String pk = parentClassKey(curr);
            if (!arg.containsKey(pk)) {
                return false;
            }
            curr = arg.get(pk);
            if (curr.equals(t2)) {
                return true;
            }
        }
        return false;
    }

    public String getTypeChoice(Type t) {
        Node choice = t.f0.choice;
        if (choice instanceof ArrayType) {
            return "int[]";
        } else if (choice instanceof BooleanType) {
            return "boolean";
        } else if (choice instanceof IntegerType) {
            return "int";
        } else if (choice instanceof Identifier i) {
            return i.f0.toString();
        }
        return "invalid type choice";
    }

    /************** KEY FORMATTING ************/

    public String classKey(String className) {
        return "class:[" + className + "]";
    }

    public String parentClassKey(String pcName) {
        return classKey(pcName) + ":parent";
    }

    public String methodKey(String className, String methodName) {
        return classKey(className) + ":method:[" + methodName + "]";
    }

    public String retTypeKey(String className, String methodName, String retType) {
        return methodKey(className, methodName) + ":returnType";
    }

    public String mParamKey(String className, String methodName, String paramName) {
        return methodKey(className, methodName) + ":methodParam:[" + paramName + "]";
    }

    public String localKey(String className, String methodName, String paramName) {
        return methodKey(className, methodName) + ":localParams:[" + paramName + "]";
    }

    public String instVarKey(String className, String varName) {
        return classKey(className) + ":instVariable:[" + varName + "]";
    }
}