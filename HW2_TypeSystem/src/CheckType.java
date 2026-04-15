import java.util.HashMap;

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

public class CheckType<R, A> extends GJDepthFirst<MyType, HashMap<String, String>> {

    private String currClass = null;
    private String currMethod = null;
    public boolean typeError = false;

    /*
     * Goal
     * f0: MainClass()
     * f1: TypeDeclaration()*
     * f2: <EOF>
     */
    public MyType visit(Goal n, HashMap<String, String> arg) {
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
        String retType = mc.f5.toString();

        currClass = className;
        currMethod = methodName;
        // Check class name in symbol table
        String key = retTypeKey(className, methodName, retType);
        if (!arg.containsKey(key)) {
            errorMessage("MainClass", key);
            typeError = true;
        }

        mc.f14.accept(this, arg);
        mc.f15.accept(this, arg);
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
        if (!arg.containsKey(key) || arg.get(key) == "TypeError") {
            errorMessage("ClassDeclaration", key);
            typeError = true;
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
                || arg.get(key) == "TypeError") {
            errorMessage("ClassExtendsDeclaration", key);
            typeError = true;
        }

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
        String idName = vd.f1.f0.toString(); // Identifier()
        String type = getTypeChoice(vd.f0);
        String key = "";
        if (currMethod == null && currClass != null) {
            key = instVarKey(currClass, idName);
        } else if (currMethod != null && currClass != null) {
            key = localKey(currClass, currMethod, idName);
        }

        if (!arg.containsKey(key)) {
            errorMessage("VarDeclaration", key);
            typeError = true;
        } else {
            if (!arg.get(key).equals(type)) {
                errorMessage("VarDeclaration", key);
                typeError = true;
            }
        }
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
        String returnType = getTypeChoice(md.f1);

        currMethod = methodName;
        String key = methodKey(currClass, methodName);
        String retKey = retTypeKey(currClass, methodName, returnType);
        if (!arg.containsKey(key) || arg.get(key) == "TypeError" || arg.get(retKey) == "TypeError") {
            errorMessage("MethodDeclaration", key);
            typeError = true;
        }
        md.f4.accept(this, arg); // FormalParameterList()?
        md.f7.accept(this, arg); // VarDeclaration()*
        md.f8.accept(this, arg); // Statement()*
        md.f10.f0.choice.accept(this, arg); // Expression()
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
            errorMessage("FormalParameter", key);
            typeError = true;
        } else {
            if (arg.get(key) != fpType) {
                typeError = true;
            }
        }
        return null;
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
        if (currMethod != null && currClass != null) {
            key = localKey(currClass, currMethod, id);
        }
        if (currMethod == null && currClass != null) {
            key = instVarKey(currClass, id);
        }
        MyType idType = new MyType(arg.get(key));

        String exprType = getExprChoice(as.f2);
        System.out.println(idType.type + "  " + exprType);
        if (!idType.toString().equals(exprType)) {
            typeError = true;
            errorMessage("AssignmentStatement", key);
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
        if (currClass != null && currMethod != null) {
            key += localKey(currClass, currMethod, id);
        }
        if (currClass != null && currMethod == null) {
            key += instVarKey(currClass, id);
        }
        // String[] to account for main method args
        if (arg.get(key) != "int[]" && arg.get(key) != "String[]" || currClass == null) {
            typeError = true;
            errorMessage("ArrayAssignmentStatement", arg.get(key));
        }
        aas.f2.f0.choice.accept(this, arg);
        aas.f5.f0.choice.accept(this, arg);
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
        // TODO: Check that Expression() is a boolean
        is.f2.f0.choice.accept(this, arg);
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
        ws.f2.f0.choice.accept(this, arg);
        // TODO: check that Expression() is a boolean
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
        ps.f2.f0.choice.accept(this, arg);
        return null;
    }

    /*
     * Expression
     * f0: AndExpression() | CompareExpression() | PlusExpression() |
     * MinusExpression() | TimesExpression() | ArrayLookup() | ArrayLength() |
     * MessageSend() | PrimaryExpression()
     */
    public MyType visit(Expression e, HashMap<String, String> arg) {
        e.f0.choice.accept(this, arg);
        return null;
    }

    /*
     * AndExpression
     * f0: PrimaryExpression()
     * f1: "&&"
     * f2: PrimaryExpression()
     */
    public MyType visit(AndExpression ae, HashMap<String, String> arg) {
        // TODO: Check that left and right operands are compatible
        MyType t0 = new MyType(getPrimeExpChoice(ae.f0));
        MyType t2 = new MyType(getPrimeExpChoice(ae.f2));

        // ae.f0.f0.choice.accept(this, arg);
        // ae.f2.f0.choice.accept(this, arg);
        return null;
    }

    /*
     * CompareExpression
     * f0: PrimaryExpression()
     * f1: "<"
     * f2: PrimaryExpression()
     */
    public MyType visit(CompareExpression ce, HashMap<String, String> arg) {
        // TODO: Check that left and right operands are compatible
        ce.f0.f0.choice.accept(this, arg);
        ce.f2.f0.choice.accept(this, arg);
        return null;
    }

    /*
     * PlusExpression
     * f0: PrimaryExpression()
     * f1: "+"
     * f2: PrimaryExpression()
     */
    public MyType visit(PlusExpression pe, HashMap<String, String> arg) {
        // TODO: Check that left and right operands are compatible
        pe.f0.f0.choice.accept(this, arg);
        pe.f2.f0.choice.accept(this, arg);
        return null;
    }

    /*
     * MinusExpression
     * f0: PrimaryExpression()
     * f1: "-"
     * f2: PrimaryExpression()
     */
    public MyType visit(MinusExpression me, HashMap<String, String> arg) {
        // TODO: Check that left and right operands are compatible
        me.f0.f0.choice.accept(this, arg);
        me.f2.f0.choice.accept(this, arg);
        return null;
    }

    /*
     * TimesExpression
     * f0: PrimaryExpression()
     * f1: "*"
     * f2: PrimaryExpression()
     */
    public MyType visit(TimesExpression te, HashMap<String, String> arg) {
        MyType t0 = new MyType(getPrimeExpChoice(te.f0));
        MyType t2 = new MyType(getPrimeExpChoice(te.f2));
        if (!t0.toString().equals(t2.toString())) {
            te.f0.f0.choice.accept(this, arg);
            te.f2.f0.choice.accept(this, arg);
        }
        return null;
    }

    /*
     * ArrayLookup
     * f0: PrimaryExpression()
     * f1: "["
     * f2: PrimaryExpression()
     * f3: "]"
     */
    public MyType visit(ArrayLookup al, HashMap<String, String> arg) {
        // TODO: Check that PrimaryExpression is of type []
        al.f0.f0.choice.accept(this, arg);
        al.f2.f0.choice.accept(this, arg);
        return null;
    }

    /*
     * ArrayLength
     * f0: PrimaryExpression()
     * f1: "."
     * f2: "length"
     */
    public MyType visit(ArrayLength al, HashMap<String, String> arg) {
        // FIXME: Check that PrimaryExpression is of type []
        String primChoice = getPrimeExpChoice(al.f0);
        System.out.println(primChoice);
        al.f0.f0.choice.accept(this, arg);
        return null;
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
        ms.f0.f0.choice.accept(this, arg);
        ms.f2.f0.toString();
        ms.f4.accept(this, arg);
        return null;
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
        return new MyType("true");
    }

    /*
     * FalseLiteral
     * f0: "false"
     */ public MyType visit(FalseLiteral fl, HashMap<String, String> arg) {
        return new MyType("false");
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
        }

        if (currClass != null && currMethod != null) {
            key = mParamKey(currClass, currMethod, name);
            if (arg.containsKey(key)) {
                return new MyType(arg.get(key));
            }
        }

        if (currClass != null) {
            key = instVarKey(currClass, name);
            if (arg.containsKey(key)) {
                return new MyType(arg.get(key));
            }
        }

        typeError = true;
        errorMessage("Identifier", key);
        return null;
    }

    /*
     * ThisExpression
     * f0: "this"
     */
    public MyType visit(ThisExpression te, HashMap<String, String> arg) {
        String key = classKey(currClass);
        if (!arg.containsKey(key) || currClass == null) {
            typeError = true;
            errorMessage("ThisExpression", currClass);
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
        // f3 must be an IntegerLiteral()
        String type = getExprChoice(aae.f3);
        if (type != "int") {
            typeError = true;
            errorMessage("ArrayAllocationExpression", type);
        }
        aae.f3.f0.choice.accept(this, arg);
        return null;
    }

    /*
     * AllocationExpression
     * f0: "new"
     * f1: Identifier()
     * f2: "("
     * f3: ")"
     */
    public MyType visit(AllocationExpression ae, HashMap<String, String> arg) {
        String key = classKey(ae.f1.f0.toString());
        if (!arg.containsKey(key)) {
            typeError = true;
            errorMessage("AllocationExpression", key);
        }
        ae.f1.f0.accept(this, arg);
        return null;
    }

    /*
     * NotExpression
     * f0: "!"
     * f1: Expression()
     */
    public MyType visit(NotExpression ne, HashMap<String, String> arg) {
        String exprType = getExprChoice(ne.f1);
        if (!exprType.equals("true") || !exprType.equals("false")) {
            ne.f1.f0.choice.accept(this, arg);
        }
        return null;
    }

    /*
     * BracketExpression
     * f0: "("
     * f1: Expression()
     * f2: ")"
     */
    public MyType visit(BracketExpression be, HashMap<String, String> arg) {
        be.f1.f0.choice.accept(this, arg);
        return null;
    }

    /************ Helper Methods **********/

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
        if (choice instanceof IntegerLiteral) {
            return "int";
        } else if (choice instanceof TrueLiteral) {
            return "true";
        } else if (choice instanceof FalseLiteral) {
            return "false";
        } else if (choice instanceof Identifier i) {
            return i.f0.toString();
        } else if (choice instanceof ThisExpression) {
            return "this";
        } else if (choice instanceof ArrayAllocationExpression) {
            return "int[]";
        } else if (choice instanceof AllocationExpression) {
            return "int[]";
        } else if (choice instanceof NotExpression) {
            return "NotExpression";
        } else if (choice instanceof BracketExpression) {
            return "BracketExpression";
        }
        return "invalid primary expression choice";
    }

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

    public void errorMessage(String errorLocation, String key) {
        System.out.println(errorLocation + " - Type Error Key: " + key);
    }
}
