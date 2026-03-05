## Starting Grammar
    expr ::= num | lvalue | incrop expr | expr incrop | expr binop expr | (expr)
    lvalue ::= $expr
    incrop ::= ++ | --
    binop ::= + | - | $\epsilon$
    num ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9


## Input Tests

#### In: 1
    G -> E
    E -> N
    N -> 1 // recognize 1
    Successfully parsed

#### In: 12
    G -> E
    E -> N
    N -> 1 // recognize 1
    Finished parsing -- can't reach 2!

#### In: 1 2
    G      -> E
    E      -> E B E
    E B E  -> 1 B E
    1 B E  -> 1 E     // string concat
    1 E    -> 1 2     // recognize 1
    Successfully parsed

#### In: \$1
    G   -> L
    L   -> $E       // recognize $
    $E  -> $N
    $N  -> $1       // recognize 1
    Successfully parsed

#### In: \$\$1
    G    -> L
    L    -> $E
    $E   -> $L
    $L   -> $$E       // recognize 2nd $
    $$E  -> $$1       // recognize 1
    Succesfully parsed

#### In: 1+2
    G      -> E B E
    E B E  -> 1 B E   // recognize 1
    1 B E  -> 1+ E    // recognize +
    1+ E   -> 1+2     // recognize 2
    Successfully parsed

#### In: <br>$1 <br>+ (1 - ++$2) $# (a confusing comment)<br>3
    G              -> L
    L              -> $E B E
    $E B E         -> $1 B E          // recognize 1
    $1 B E         -> $1+ E           // recognize +
    $1+ E          -> $1+(E)          // recognize (
    $1+(E)         -> $1+(E B E)
    $1+(E B E)     -> $1+(1 B E)      // recognize inner 1
    $1(1 B E)      -> $1+(1- E)       // recognize -
    $1(1- E)       -> $1+(1- I E)
    $1(1- I E)     -> $1+(1-++ E)     // recognize ++
    $1(1-++ E)     -> $1+(1-++ L)
    $1(1-++ L)     -> $1+(1-++$E)     // recognize $
    $1(1-++$E)     -> $1+(1-++$2)     // recognize 2 and )
    $1+(1-++$2)    -> $1+(1-++$2) E   // recognize epsilon
    $1+(1-++$2) E  -> $1+(1-++$2) L
    $1+(1-++$2) L  -> $1+(1-++$2) $E  // recognize $
    $1+(1-++$2) $E -> $1+(1-++$2) $3  // recognize 3
    Successfully parsed
<!-- post-fix should be: 1 $ 1 2 $ ++_ - + 3 $ _ -->

#### In: 