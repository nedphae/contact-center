lexer grammar MyLexer;

//关键词
KW_AND : A N D | '&';
KW_OR : O R | '|';
KW_NOT : N O T | '!';

LPAREN : '(' ;
RPAREN : ')' ;

IDENTIFIER:
	'"' (~'"' | '""')* '"'
	| '`' (~'`' | '``')* '`'
	| '[' ~']'* ']'
	| [a-zA-Z_] [a-zA-Z_0-9]*;

WS  : (' '|'\r'|'\t'|'\n') -> channel(HIDDEN)
    ;

LINE_COMMENT
    : '--' (~('\n'|'\r'))* -> channel(HIDDEN)
    ;

// 必须这样才能忽略大小写
// 参考的 mysql parser 就会区分大小写
fragment A: [aA];
fragment B: [bB];
fragment C: [cC];
fragment D: [dD];
fragment E: [eE];
fragment F: [fF];
fragment G: [gG];
fragment H: [hH];
fragment I: [iI];
fragment J: [jJ];
fragment K: [kK];
fragment L: [lL];
fragment M: [mM];
fragment N: [nN];
fragment O: [oO];
fragment P: [pP];
fragment Q: [qQ];
fragment R: [rR];
fragment S: [sS];
fragment T: [tT];
fragment U: [uU];
fragment V: [vV];
fragment W: [wW];
fragment X: [xX];
fragment Y: [yY];
fragment Z: [zZ];