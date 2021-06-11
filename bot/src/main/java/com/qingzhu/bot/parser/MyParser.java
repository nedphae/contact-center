// Generated from C:/Users/ned_g/Documents/project/kefu/contact-center/im-access/src/main/java/com/qingzhu/imaccess/parser\MyParser.g4 by ANTLR 4.8
package com.qingzhu.bot.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MyParser extends Parser {
    public static final int
            KW_AND = 1, KW_OR = 2, KW_NOT = 3, LPAREN = 4, RPAREN = 5, IDENTIFIER = 6, WS = 7, LINE_COMMENT = 8;
    public static final int
            RULE_expression = 0;
    public static final String[] ruleNames = makeRuleNames();
    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN =
            "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\n\27\4\2\t\2\3\2" +
                    "\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2\r\n\2\3\2\3\2\3\2\7\2\22\n\2\f\2\16\2" +
                    "\25\13\2\3\2\2\3\2\3\2\2\3\3\2\3\4\2\30\2\f\3\2\2\2\4\5\b\2\1\2\5\6\7" +
                    "\6\2\2\6\7\5\2\2\2\7\b\7\7\2\2\b\r\3\2\2\2\t\n\7\5\2\2\n\r\5\2\2\5\13" +
                    "\r\7\b\2\2\f\4\3\2\2\2\f\t\3\2\2\2\f\13\3\2\2\2\r\23\3\2\2\2\16\17\f\4" +
                    "\2\2\17\20\t\2\2\2\20\22\5\2\2\5\21\16\3\2\2\2\22\25\3\2\2\2\23\21\3\2" +
                    "\2\2\23\24\3\2\2\2\24\3\3\2\2\2\25\23\3\2\2\2\4\f\23";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    private static final String[] _LITERAL_NAMES = makeLiteralNames();
    private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
    public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

    static {
        RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION);
    }

    static {
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (int i = 0; i < tokenNames.length; i++) {
            tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }

            if (tokenNames[i] == null) {
                tokenNames[i] = "<INVALID>";
            }
        }
    }

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }

    public MyParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    private static String[] makeRuleNames() {
        return new String[]{
                "expression"
        };
    }

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, null, "'('", "')'"
        };
    }

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "KW_AND", "KW_OR", "KW_NOT", "LPAREN", "RPAREN", "IDENTIFIER",
                "WS", "LINE_COMMENT"
        };
    }

    @Override
    @Deprecated
    public String[] getTokenNames() {
        return tokenNames;
    }

    @Override

    public Vocabulary getVocabulary() {
        return VOCABULARY;
    }

    @Override
    public String getGrammarFileName() {
        return "MyParser.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public final ExpressionContext expression() throws RecognitionException {
        return expression(0);
    }

    private ExpressionContext expression(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
        ExpressionContext _prevctx = _localctx;
        int _startState = 0;
        enterRecursionRule(_localctx, 0, RULE_expression, _p);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(10);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case LPAREN: {
                        setState(3);
                        match(LPAREN);
                        setState(4);
                        expression(0);
                        setState(5);
                        match(RPAREN);
                    }
                    break;
                    case KW_NOT: {
                        setState(7);
                        match(KW_NOT);
                        setState(8);
                        expression(3);
                    }
                    break;
                    case IDENTIFIER: {
                        setState(9);
                        match(IDENTIFIER);
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
                _ctx.stop = _input.LT(-1);
                setState(17);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
                while (_alt != 2 && _alt != ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new ExpressionContext(_parentctx, _parentState);
                                _localctx.leftExpr = _prevctx;
                                _localctx.leftExpr = _prevctx;
                                pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                setState(12);
                                if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                                setState(13);
                                ((ExpressionContext) _localctx).operator = _input.LT(1);
                                _la = _input.LA(1);
                                if (!(_la == KW_AND || _la == KW_OR)) {
                                    ((ExpressionContext) _localctx).operator = (Token) _errHandler.recoverInline(this);
                                } else {
                                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                                    _errHandler.reportMatch(this);
                                    consume();
                                }
                                setState(14);
                                ((ExpressionContext) _localctx).rightExpr = expression(3);
                            }
                        }
                    }
                    setState(19);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 0:
                return expression_sempred((ExpressionContext) _localctx, predIndex);
        }
        return true;
    }

    private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
        switch (predIndex) {
            case 0:
                return precpred(_ctx, 2);
        }
        return true;
    }

    public static class ExpressionContext extends ParserRuleContext {
        public ExpressionContext leftExpr;
        public Token operator;
        public ExpressionContext rightExpr;

        public ExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode LPAREN() {
            return getToken(MyParser.LPAREN, 0);
        }

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode RPAREN() {
            return getToken(MyParser.RPAREN, 0);
        }

        public TerminalNode KW_NOT() {
            return getToken(MyParser.KW_NOT, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(MyParser.IDENTIFIER, 0);
        }

        public TerminalNode KW_AND() {
            return getToken(MyParser.KW_AND, 0);
        }

        public TerminalNode KW_OR() {
            return getToken(MyParser.KW_OR, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_expression;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MyParserListener) ((MyParserListener) listener).enterExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MyParserListener) ((MyParserListener) listener).exitExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MyParserVisitor)
                return ((MyParserVisitor<? extends T>) visitor).visitExpression(this);
            else return visitor.visitChildren(this);
        }
    }
}