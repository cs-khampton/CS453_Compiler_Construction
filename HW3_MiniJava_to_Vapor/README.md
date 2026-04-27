## Tokens
```
// Whitespace and Comments
<DEFAULT> SKIP : {
" "
| "\t"
| <LineComment: "//" (~["\n","\r"])*>
| <BlockComment: "/*" (~["*"])* "*" ("*" | ~["*","/"] (~["*"])* "*")* "/">
}

   
<DEFAULT> TOKEN : {
<#Digit: ["0"-"9"]>
| <Digits: (<Digit>)+>
| <NegDigits: "-" <Digits>> : {
| <#IdentHead: ["a"-"z","A"-"Z","_"]>
| <#IdentRest: <IdentHead> | <Digit> | ".">
| <Eol: (("\r")* "\n" ([" ","\t"])*)+>
}

   
<DEFAULT> MORE : {
<LitStrStart: "\""> : WITHIN_STRING
}

   
<WITHIN_STRING> TOKEN : {
<LitStr: "\""> : DEFAULT
}

   
<WITHIN_STRING> MORE : {
<EscapeQuote: "\\\""> : {
| <EscapeBackslash: "\\\\"> : {
| <NormalStringContent: [" "-"~"]>
}

   
// -------------------------------------------------------------
<DEFAULT> TOKEN : {
<#Ident: <IdentHead> (<IdentRest>)*>
| <RegIdent: "$" <Ident>> : {
| <CodeLabelIdent: <Ident> ":"> : {
| <LabelRefIdent: ":" <Ident>> : {
| <PlainIdent: <Ident>>
}

   
// Catch-all token.
<*> TOKEN : {
<Anything: ~[]>
}
```
   
NON-TERMINALS
-------------------------------------------------------------
## Parser
-------------------------------------------------------------

| Rule#| Non-Terminal | Production(s) |
| ---- | ------------ | ------------- |
| 1 | Program | ( Eol )? ( Function )* \<EOF> | 
| 2 |         | ( Eol )? ( DataSegment )* \<EOF> | 
| 3 | DataSegment | "const" Ident Eol ( (DataValue) + Eol)* | 
| 4 |             | "var" Ident Eol ( (DataValue ) + Eol)* |
| 5 | Function | "func" Ident ("(" ( VarRefNoReg )* ")" )? ("[" "in" \<Digits> "," "out" \<Digits> "," "local" \<Digits> "]" )? Eol ((CodeLabel Eol))* |
| 6 | | "func" Ident ("(" ( VarRefNoReg )* ")" )? ("[" "in" \<Digits> "," "out" \<Digits> "," "local" \<Digits> "]" )? Eol ((CodeLabel(Instr)))* |
| 7 |  | "func" Ident ( "(" ( VarRefNoReg )* ")" )? ("[" "in" \<Digits> "," "out" \<Digits> "," "local" \<Digits> "]" )? Eol (( Instr ))* |
| 8 | Instr | Return |
| 9 | | MemRead |
| 10 | | MemWrite |
| 11 | | Assign | 
| 12 | | Branch | 
| 13 | | GoTo | 
| 14 | | Call | 
| 15 | | BuiltIn | 
| 16 | MemRead | VarRef "=" MemRef Eol |
| 17 | MemWrite | MemRef "=" Operand Eol
| 18 | MemRef | StackMemRef |
| 19 | | GlobalMemRef | 
| 20 | StackMemRef | "in" "[" \<Digits>"]"|
| 21 | | "out" "[" \<Digits>"]"|
| 22 | | "local" "[" \<Digits>"]"|
| 23 | GlobalMemRef | "[" DataAddr ( ("+") \<Digits>)? "]"| 
| 24 |  | "[" DataAddr ( ("-") \<Digits>)? "]" | 
| 25 | Assign | VarRef "=" Operand Eol |
| 26 | Branch | "if" Operand "goto" CodeLabelRef Eol |
| 27 | | "if0" Operand "goto" CodeLabelRef Eol |
| 28 | Goto | "goto" CodeAddr Eol | 
| 29 | Call | ( VarRefNoReg "=" )? "call" FuncAddr ( "(" ( OperandNoReg )* ")" )? Eol |
| 30 | BuiltIn | (VarRef "=" )? Ident "(" ( Operand )* ")" Eol |
| 31 | Return | "ret" ( OperandNoReg )? Eol |
| 32 | LabelRef | \<LabelRefIdent> |
| 33 | CodeAddr | CodeLabelRef |
| 34 | | VarRef |
| 35 | DataAddr | DataSegmentRef | 
| 36 | | VarRef |
| 37 | FuncAddr | FuncRef |
| 38 | | VarRef |
| 39 | CodeLabelRef | \<LabelRefIdent>
| 40 | DataSegmentRef | \<LabelRefIdent> | 
| 41 | FuncRef | \<LabelRefIdent> |
| 42 | VarRef | Ident|
| 43 | | \<RegIdent> |
| 44 | VarRefNoReg | VarRef |
| 45 | OperandNoReg | Operand | 
| 46 | LitInt | \<Digits> | 
| 47 | | \<NegDigits> | 
| 48 | LitStr | \<Litstr> |
| 49 | Ident | \<PlainIdent> |
| 50 | | "func" |
| 51 | | "const" |
| 52 | | "var" |
| 53 | | "in" |
| 54 | | "out" |
| 55 | | "local" |
| 56 | | "if" |
| 57 | | "if0" |
| 58 | | "goto" |
| 59 | | "ret" |
| 60 | | "call" |
| 61 | CodeLabel | \<CodeLabelIdent> |
| 62 | Eol | (\<Eol>)+


- [ ] Create test that checks equivalence

