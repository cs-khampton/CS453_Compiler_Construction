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
import syntaxtree.NodeToken;
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

public class PPrinter<R, A> extends GJDepthFirst<R, A> {
    private int indent = 0;

    public void printClassName(Node n) {
        if (n instanceof NodeToken nt) {
            indent++;
        }
        for (int i = 0; i < indent; i++) {
            System.out.print("    ");
        }
        if (n instanceof NodeToken nt) {
            System.out.println("NodeToken => " + nt.toString());
            indent--;
        } else {
            System.out.println(n.getClass().getSimpleName());
        }
    }

    // G => MainClass() TypeDeclaration()* <EOF>
    public R visit(Goal n, A arg) {
        printClassName(n);
        visit(n.f0, arg); // MainClass()
        visit(n.f1, arg); // TypeDeclaration()
        printClassName(n.f2); // EOF
        return null;
    }

    // mc => "class" Identifier() "{" "public" "static" "void" "main" "(" "String"
    // "[" "]" Identifier() ")" " {" VarDeclaration()* Statement()* "}" "}"
    public R visit(MainClass mc, A arg) {
        indent++;
        printClassName(mc);
        printClassName(mc.f0); // class
        indent++;
        visit(mc.f1, arg); // Identifier()
        indent--;
        printClassName(mc.f2); // {
        printClassName(mc.f3); // public
        printClassName(mc.f4); // static
        printClassName(mc.f5); // void
        printClassName(mc.f6); // main
        printClassName(mc.f7); // (
        printClassName(mc.f8); // String
        printClassName(mc.f9); // [
        printClassName(mc.f10); // ]
        indent++;
        visit(mc.f11, arg); // Identifier()
        indent--;
        printClassName(mc.f12); // )
        printClassName(mc.f13); // {
        indent++;
        visit(mc.f14, arg); // VarDeclaration()
        visit(mc.f15, arg); // Statement()
        indent--;
        printClassName(mc.f16); // }
        printClassName(mc.f17); // }
        return null;
    }

    // TypeDeclaration => ClassDeclaration() | ClassExtendsDeclaration()
    public R visit(TypeDeclaration d, A arg) {
        Node n = d.f0.choice;
        indent++;
        printClassName(d);
        if (n instanceof ClassDeclaration cd) {
            visit(cd, arg); // ClassDeclaration()
        } else if (n instanceof ClassExtendsDeclaration ced) {
            visit(ced, arg); // ClassExtendsDeclaration()
        }
        indent--;
        return null;
    }

    // ClassDeclaration => "class" Identifier() "{" VarDeclaration()*
    // MethodDeclaration()* "}"
    public R visit(ClassDeclaration cd, A arg) {
        indent++;
        printClassName(cd);
        printClassName(cd.f0); // class
        visit(cd.f1, arg); // Identifier()
        printClassName(cd.f2); // {
        visit(cd.f3, arg); // VarDeclaration()
        visit(cd.f4, arg); // MethodDeclaration()
        printClassName(cd.f5); // }
        indent--;
        return null;
    }

    // ClassExtendsDeclaration => "class" Identifier() "extends" Identifier() "{"
    // VarDeclaration()* MethodDeclaration()* "}"
    public R visit(ClassExtendsDeclaration ced, A arg) {
        printClassName(ced);
        indent++;
        printClassName(ced.f0); // class
        visit(ced.f1, arg); // Identifier()
        printClassName(ced.f2); // extends
        visit(ced.f3, arg); // Identifier()
        printClassName(ced.f4); // {
        indent++;
        visit(ced.f5, arg); // VarDeclaration()
        visit(ced.f6, arg); // MethodDeclaration()
        indent--;
        printClassName(ced.f7); // }
        return null;
    }

    // VarDeclaration => Type() Identifier() ";"
    public R visit(VarDeclaration vd, A arg) {
        indent++;
        printClassName(vd);
        visit(vd.f0, arg); // Type()
        visit(vd.f1, arg); // Identifier()
        indent--;
        printClassName(vd.f2); // ;
        indent--;
        return null;
    }

    // MethodDeclaration => "public" Type() Identifier() "(" FormalParameterList()?
    // ")" "{" VarDeclaration()* Statement()* "return" Expression() ";" "}"
    public R visit(MethodDeclaration md, A arg) {
        printClassName(md);
        printClassName(md.f0); // public
        visit(md.f1, arg); // Type()
        visit(md.f2, arg); // Identifier()
        printClassName(md.f3); // (
        visit(md.f4, arg); // FormalParameterList()
        printClassName(md.f5); // )
        printClassName(md.f6); // {
        visit(md.f7, arg); // VarDeclaration()
        visit(md.f8, arg); // Statement()
        printClassName(md.f9); // return
        visit(md.f10, arg); // Expression()
        printClassName(md.f11); // ;
        indent--;
        printClassName(md.f12); // }
        return null;
    }

    // FormalParameterList => FormalParameter() FormalParameterRest()*
    public R visit(FormalParameterList fpl, A arg) {
        indent++;
        printClassName(fpl);
        visit(fpl.f0, arg); // FormalParameter()
        indent--;
        visit(fpl.f1, arg); // FormalParameterRest()
        indent--;
        return null;
    }

    // FormalParameter => Type() Identifier()
    public R visit(FormalParameter fp, A arg) {
        indent++;
        printClassName(fp);
        visit(fp.f0, arg); // Type()
        visit(fp.f1, arg); // Identifier()
        indent--;
        return null;
    }

    // FormalParameterRest => "," FormalParameter()
    public R visit(FormalParameterRest fpr, A arg) {
        printClassName(fpr);
        printClassName(fpr.f0); // ,
        visit(fpr.f1, arg); // FormalParameter()
        return null;
    }

    // Type => ArrayType() | BooleanType() | Identifier()
    public R visit(Type t, A arg) {
        Node n = t.f0.choice;
        indent++;
        printClassName(t);
        if (n instanceof ArrayType at) {
            visit(at.f0, arg); // ArrayType()
        } else if (n instanceof BooleanType bt) {
            visit(bt.f0, arg); // BooleanType()
        } else if (n instanceof IntegerType it) {
            visit(it.f0, arg); // IntegerType()
        } else if (n instanceof Identifier bt) {
            visit(bt.f0, arg); // Identifier()
        }
        return null;
    }

    // ArrayType => "int" "[" "]"
    public R visit(ArrayType at, A arg) {
        printClassName(at);
        printClassName(at.f0); // int
        printClassName(at.f1); // [
        printClassName(at.f2); // ]
        return null;
    }

    // BooleanType => "boolean"
    public R visit(BooleanType bt, A arg) {
        printClassName(bt);
        printClassName(bt.f0); // boolean
        return null;
    }

    // IntegerType => "int"
    public R visit(IntegerType it, A arg) {
        printClassName(it);
        printClassName(it.f0); // int
        return null;
    }

    // Statement => Block() | AssignmentStatement() | ArrayAssignmentStatement() |
    // IfStatement() | WhileStatement() | PrintStatement()
    public R visit(Statement s, A arg) {
        Node n = s.f0.choice;
        printClassName(s);
        if (n instanceof Block b) {
            indent++;
            visit(b, arg); // Block()
        } else if (n instanceof AssignmentStatement as) {
            indent++;
            visit(as, arg); // AssignStatement()
        } else if (n instanceof ArrayAssignmentStatement as) {
            indent++;
            visit(as, arg); // ArrayAssignStatement()
        } else if (n instanceof IfStatement i) {
            indent++;
            visit(i, arg); // IfStatement()
        } else if (n instanceof WhileStatement ws) {
            indent++;
            visit(ws, arg); // WhileStatement()
        } else if (n instanceof PrintStatement ps) {
            indent++;
            visit(ps, arg); // PrintStatement()
        }
        indent--;
        return null;
    }

    // Block => "{" Statement()* "}"
    public R visit(Block b, A arg) {
        printClassName(b);
        printClassName(b.f0); // {
        visit(b.f1, arg); // Statement()
        printClassName(b.f2); // }
        return null;
    }

    // AssignmentStatement => Identifier() "=" Expression() ";"
    public R visit(AssignmentStatement as, A arg) {
        printClassName(as);
        // indent++;
        visit(as.f0, arg); // Identifier()
        // indent--;
        printClassName(as.f1); // =
        visit(as.f2, arg); // Expression()
        // indent--;
        printClassName(as.f3); // ;
        return null;
    }

    // ArrayAssignmentStatement => Identifier() "[" Expression() "]" "="
    // Expression() ";"
    public R visit(ArrayAssignmentStatement aas, A arg) {
        printClassName(aas);
        visit(aas.f0, arg); // Identifier()
        printClassName(aas.f1); // [
        visit(aas.f2, arg); // Expression()
        printClassName(aas.f3); // ]
        printClassName(aas.f4); // =
        visit(aas.f5, arg); // Expression()
        printClassName(aas.f6); // ;
        return null;
    }

    // IfStatement => "if" "(" Expression() ")" Statement() "else" Statement()
    public R visit(IfStatement is, A arg) {
        printClassName(is);
        printClassName(is.f0); // if
        printClassName(is.f1); // (
        visit(is.f2, arg); // Expression()
        printClassName(is.f3); // )
        visit(is.f4, arg); // Statement()
        printClassName(is.f5); // else
        visit(is.f6, arg); // Statement()
        return null;
    }

    // WhileStatement => "while" "(" Expression() ")" Statement()
    public R visit(WhileStatement ws, A arg) {
        printClassName(ws);
        printClassName(ws.f0); // while
        printClassName(ws.f1); // (
        visit(ws.f2, arg); // Expression()
        printClassName(ws.f3); // )
        visit(ws.f4, arg); // Statement()
        return null;
    }

    // PrintStatement => "System.out.println" "(" Expression() ")" ";"
    public R visit(PrintStatement ps, A arg) {
        printClassName(ps);
        printClassName(ps.f0); // System.out.println
        printClassName(ps.f1); // (
        visit(ps.f2, arg); // Expression()
        printClassName(ps.f3); // )
        printClassName(ps.f4); // ;
        return null;
    }

    // Expression => AndExpression() | CompareExpression() | PlusExpression() |
    // MinusExpression() | TimesExpression() | ArrayLookup() | ArrayLength() |
    // MessageSend() | PrimaryExpression()
    public R visit(Expression e, A arg) {
        printClassName(e);
        Node n = e.f0.choice;
        if (n instanceof AndExpression ae) {
            indent++;
            visit(ae, arg); // AndExpression()
        } else if (n instanceof CompareExpression ce) {
            indent++;
            visit(ce, arg); // CompareExpression()
        } else if (n instanceof PlusExpression pe) {
            indent++;
            visit(pe, arg); // PlusExpression()
        } else if (n instanceof MinusExpression me) {
            indent++;
            visit(me, arg); // MinusExpression()
        } else if (n instanceof TimesExpression te) {
            indent++;
            visit(te, arg); // TimesExpression()
        } else if (n instanceof ArrayLookup alook) {
            indent++;
            visit(alook, arg); // ArrayLookup()
        } else if (n instanceof ArrayLength alen) {
            indent++;
            visit(alen, arg); // ArrayLength()
        } else if (n instanceof MessageSend ms) {
            indent++;
            visit(ms, arg); // MessageSend()
        } else if (n instanceof PrimaryExpression primexp) {
            indent++;
            visit(primexp, arg); // CompareExpression()
        }
        indent--;
        return null;
    }

    // AndExpression => PrimaryExpression() "&&" PrimaryExpression()
    public R visit(AndExpression ae, A arg) {
        printClassName(ae);
        visit(ae.f0, arg); // PrimaryExpression()
        printClassName(ae.f1); // &&
        visit(ae.f2, arg); // PrimaryExpression()
        return null;
    }

    // ComapreExpression => PrimaryExpression() "<" PrimaryExpression()
    public R visit(CompareExpression ce, A arg) {
        printClassName(ce);
        visit(ce.f0, arg); // PrimaryExpression()
        printClassName(ce.f1); // <
        visit(ce.f2, arg); // PrimaryExpression()
        return null;
    }

    // PlusExpression => PrimaryExpression() "+" PrimaryExpression()
    public R visit(PlusExpression pe, A arg) {
        printClassName(pe);
        visit(pe.f0, arg); // PrimaryExpression()
        printClassName(pe.f1); // +
        visit(pe.f2, arg); // PrimaryExpression()
        return null;
    }

    // MinusExpression => PrimaryExpression() "-" PrimaryExpression()
    public R visit(MinusExpression me, A arg) {
        printClassName(me);
        visit(me.f0, arg); // PrimaryExpression()
        printClassName(me.f1); // -
        visit(me.f2, arg); // PrimaryExpression()
        return null;
    }

    // TimesExpression => PrimaryExpression() "*" PrimaryExpression()
    public R visit(TimesExpression te, A arg) {
        printClassName(te);
        visit(te.f0, arg); // PrimaryExpression()
        printClassName(te.f1); // *
        visit(te.f2, arg); // PrimaryExpression()
        return null;
    }

    // ArrayLookup => PrimaryExpression() "[" PrimaryExpression() "]"
    public R visit(ArrayLookup al, A arg) {
        printClassName(al);
        visit(al.f0, arg); // PrimaryExpression()
        printClassName(al.f1); // [
        visit(al.f2, arg); // PrimaryExpression()
        printClassName(al.f3); // ]
        return null;
    }

    // ArrayLength => PrimaryExpression() "." "length"
    public R visit(ArrayLength al, A arg) {
        printClassName(al);
        visit(al.f0, arg); // PrimaryExpression()
        printClassName(al.f1); // .
        printClassName(al.f2); // length
        return null;
    }

    // MessageSend => PrimaryExpression() "." Identifier() "(" ExpresionList()? ")"
    public R visit(MessageSend ms, A arg) {
        printClassName(ms);
        visit(ms.f0, arg); // PrimaryExpression
        printClassName(ms.f1); // .
        visit(ms.f2, arg); // Identifier()
        printClassName(ms.f3); // (
        visit(ms.f4, arg); // ExpressionList()?
        printClassName(ms.f5); // )
        return null;
    }

    // ExpressionList => Expression() ExpressionRest()
    public R visit(ExpressionList el, A arg) {
        printClassName(el);
        visit(el.f0, arg); // Expression()
        visit(el.f1, arg); // ExpressionRest()
        return null;
    }

    // ExpressionRest => "," Expression()
    public R visit(ExpressionRest er, A arg) {
        printClassName(er);
        printClassName(er.f0); // ,
        visit(er.f1, arg); // Expression()
        return null;
    }

    // PrimaryExpression => IntegerLiteral() | TrueLiteral() | FalseLiteral() |
    // Identifier() | ThisExpression() | ArrayAllocationExpression() |
    // AllocationExpression() | NotExpression() | BracketExpression()
    public R visit(PrimaryExpression pe, A arg) {
        Node n = pe.f0.choice;
        if (n instanceof IntegerLiteral il) {
            visit(il, arg); // IntegerLiteral()
        } else if (n instanceof TrueLiteral tl) {
            visit(tl, arg); // TrueLiteral()
        } else if (n instanceof FalseLiteral fl) {
            visit(fl, arg); // FalseLiteral()
        } else if (n instanceof Identifier id) {
            visit(id, arg); // Identifier()
        } else if (n instanceof ThisExpression te) {
            visit(te, arg); // ThisExpression()
        } else if (n instanceof ArrayAllocationExpression aae) {
            visit(aae, arg); // ArrayAllocationExpression()
        } else if (n instanceof AllocationExpression ae) {
            visit(ae, arg); // AllocationExpression(()
        } else if (n instanceof NotExpression ne) {
            visit(ne, arg); // NotExpression()
        } else if (n instanceof BracketExpression be) {
            visit(be, arg); // CompareExpression()
        }
        return null;
    }

    // IntegerLiteral => <INTEGER_LITERAL>
    public R visit(IntegerLiteral il, A arg) {
        printClassName(il);
        printClassName(il.f0);
        return null;
    }

    // TrueLiteral => "true"
    public R visit(TrueLiteral tl, A arg) {
        printClassName(tl);
        printClassName(tl.f0); // true
        return null;
    }

    // FalseLiteral => "false"
    public R visit(FalseLiteral fl, A arg) {
        printClassName(fl);
        printClassName(fl.f0);
        return null;
    }

    // Identifier => <IDENTIFIER>
    public R visit(Identifier id, A arg) {
        indent++;
        printClassName(id);
        printClassName(id.f0);
        indent--;
        return null;
    }

    // ThisExpression => "this"
    public R visit(ThisExpression te, A arg) {
        printClassName(te);
        printClassName(te.f0);
        return null;
    }

    // ArrayAllocationExpression => "new" "int" "[" Expression() "]"
    public R visit(ArrayAllocationExpression aae, A arg) {
        printClassName(aae);
        printClassName(aae.f0); // new
        printClassName(aae.f1); // int
        printClassName(aae.f2); // [
        visit(aae.f3, arg); // Expression()
        indent--;
        printClassName(aae.f4); // ]
        return null;
    }

    // AllocationExpression => "new" Identifier() "(" ")"
    public R visit(AllocationExpression ae, A arg) {
        printClassName(ae);
        printClassName(ae.f0); // new
        visit(ae.f1, arg); // Identifier()
        indent--;
        printClassName(ae.f2); // (
        printClassName(ae.f3); // )
        return null;
    }

    // NotExpression => "!" Expression()
    public R visit(NotExpression ne, A arg) {
        printClassName(ne);
        printClassName(ne.f0); // !
        visit(ne.f1, arg); // Expression()
        return null;
    }

    // BracketExpression => "(" Expression() ")"
    public R visit(BracketExpression be, A arg) {
        printClassName(be);
        printClassName(be.f0); // (
        visit(be.f1, arg); // Expression()
        printClassName(be.f2); // )
        return null;
    }
}