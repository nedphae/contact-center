parser grammar MyParser;

options
{
  tokenVocab=MyLexer;
}

expression:
    // #后面是子表达式命名，可有可无
    LPAREN expression RPAREN #lrExpr
    | KW_NOT expression #notExpr
    | leftExpr = expression operator = (KW_AND | KW_OR ) rightExpr = expression #boolExpr
    | IDENTIFIER    #idExpr
    ;

