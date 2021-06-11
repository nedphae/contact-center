// Generated from C:/Users/ned_g/Documents/project/kefu/contact-center/im-access/src/main/java/com/qingzhu/imaccess/parser\MyLexer.g4 by ANTLR 4.8
package com.qingzhu.bot.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MyLexer extends Lexer {
    public static final int
            KW_AND = 1, KW_OR = 2, KW_NOT = 3, LPAREN = 4, RPAREN = 5, IDENTIFIER = 6, WS = 7, LINE_COMMENT = 8;
    public static final String[] ruleNames = makeRuleNames();
    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN =
            "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\n\u00c7\b\1\4\2\t" +
                    "\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13" +
                    "\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" +
                    "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31" +
                    "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!" +
                    "\t!\4\"\t\"\4#\t#\3\2\3\2\3\2\3\2\3\2\5\2M\n\2\3\3\3\3\3\3\3\3\5\3S\n" +
                    "\3\3\4\3\4\3\4\3\4\3\4\5\4Z\n\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7\7\7d\n" +
                    "\7\f\7\16\7g\13\7\3\7\3\7\3\7\3\7\3\7\7\7n\n\7\f\7\16\7q\13\7\3\7\3\7" +
                    "\3\7\7\7v\n\7\f\7\16\7y\13\7\3\7\3\7\3\7\7\7~\n\7\f\7\16\7\u0081\13\7" +
                    "\5\7\u0083\n\7\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\7\t\u008d\n\t\f\t\16\t" +
                    "\u0090\13\t\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3" +
                    "\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3" +
                    "\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3" +
                    "\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\2\2$\3\3\5\4\7\5\t\6" +
                    "\13\7\r\b\17\t\21\n\23\2\25\2\27\2\31\2\33\2\35\2\37\2!\2#\2%\2\'\2)\2" +
                    "+\2-\2/\2\61\2\63\2\65\2\67\29\2;\2=\2?\2A\2C\2E\2\3\2#\3\2$$\3\2bb\3" +
                    "\2__\5\2C\\aac|\6\2\62;C\\aac|\5\2\13\f\17\17\"\"\4\2\f\f\17\17\4\2CC" +
                    "cc\4\2DDdd\4\2EEee\4\2FFff\4\2GGgg\4\2HHhh\4\2IIii\4\2JJjj\4\2KKkk\4\2" +
                    "LLll\4\2MMmm\4\2NNnn\4\2OOoo\4\2PPpp\4\2QQqq\4\2RRrr\4\2SSss\4\2TTtt\4" +
                    "\2UUuu\4\2VVvv\4\2WWww\4\2XXxx\4\2YYyy\4\2ZZzz\4\2[[{{\4\2\\\\||\2\u00b9" +
                    "\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2" +
                    "\2\2\2\17\3\2\2\2\2\21\3\2\2\2\3L\3\2\2\2\5R\3\2\2\2\7Y\3\2\2\2\t[\3\2" +
                    "\2\2\13]\3\2\2\2\r\u0082\3\2\2\2\17\u0084\3\2\2\2\21\u0088\3\2\2\2\23" +
                    "\u0093\3\2\2\2\25\u0095\3\2\2\2\27\u0097\3\2\2\2\31\u0099\3\2\2\2\33\u009b" +
                    "\3\2\2\2\35\u009d\3\2\2\2\37\u009f\3\2\2\2!\u00a1\3\2\2\2#\u00a3\3\2\2" +
                    "\2%\u00a5\3\2\2\2\'\u00a7\3\2\2\2)\u00a9\3\2\2\2+\u00ab\3\2\2\2-\u00ad" +
                    "\3\2\2\2/\u00af\3\2\2\2\61\u00b1\3\2\2\2\63\u00b3\3\2\2\2\65\u00b5\3\2" +
                    "\2\2\67\u00b7\3\2\2\29\u00b9\3\2\2\2;\u00bb\3\2\2\2=\u00bd\3\2\2\2?\u00bf" +
                    "\3\2\2\2A\u00c1\3\2\2\2C\u00c3\3\2\2\2E\u00c5\3\2\2\2GH\5\23\n\2HI\5-" +
                    "\27\2IJ\5\31\r\2JM\3\2\2\2KM\7(\2\2LG\3\2\2\2LK\3\2\2\2M\4\3\2\2\2NO\5" +
                    "/\30\2OP\5\65\33\2PS\3\2\2\2QS\7~\2\2RN\3\2\2\2RQ\3\2\2\2S\6\3\2\2\2T" +
                    "U\5-\27\2UV\5/\30\2VW\59\35\2WZ\3\2\2\2XZ\7#\2\2YT\3\2\2\2YX\3\2\2\2Z" +
                    "\b\3\2\2\2[\\\7*\2\2\\\n\3\2\2\2]^\7+\2\2^\f\3\2\2\2_e\7$\2\2`d\n\2\2" +
                    "\2ab\7$\2\2bd\7$\2\2c`\3\2\2\2ca\3\2\2\2dg\3\2\2\2ec\3\2\2\2ef\3\2\2\2" +
                    "fh\3\2\2\2ge\3\2\2\2h\u0083\7$\2\2io\7b\2\2jn\n\3\2\2kl\7b\2\2ln\7b\2" +
                    "\2mj\3\2\2\2mk\3\2\2\2nq\3\2\2\2om\3\2\2\2op\3\2\2\2pr\3\2\2\2qo\3\2\2" +
                    "\2r\u0083\7b\2\2sw\7]\2\2tv\n\4\2\2ut\3\2\2\2vy\3\2\2\2wu\3\2\2\2wx\3" +
                    "\2\2\2xz\3\2\2\2yw\3\2\2\2z\u0083\7_\2\2{\177\t\5\2\2|~\t\6\2\2}|\3\2" +
                    "\2\2~\u0081\3\2\2\2\177}\3\2\2\2\177\u0080\3\2\2\2\u0080\u0083\3\2\2\2" +
                    "\u0081\177\3\2\2\2\u0082_\3\2\2\2\u0082i\3\2\2\2\u0082s\3\2\2\2\u0082" +
                    "{\3\2\2\2\u0083\16\3\2\2\2\u0084\u0085\t\7\2\2\u0085\u0086\3\2\2\2\u0086" +
                    "\u0087\b\b\2\2\u0087\20\3\2\2\2\u0088\u0089\7/\2\2\u0089\u008a\7/\2\2" +
                    "\u008a\u008e\3\2\2\2\u008b\u008d\n\b\2\2\u008c\u008b\3\2\2\2\u008d\u0090" +
                    "\3\2\2\2\u008e\u008c\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u0091\3\2\2\2\u0090" +
                    "\u008e\3\2\2\2\u0091\u0092\b\t\2\2\u0092\22\3\2\2\2\u0093\u0094\t\t\2" +
                    "\2\u0094\24\3\2\2\2\u0095\u0096\t\n\2\2\u0096\26\3\2\2\2\u0097\u0098\t" +
                    "\13\2\2\u0098\30\3\2\2\2\u0099\u009a\t\f\2\2\u009a\32\3\2\2\2\u009b\u009c" +
                    "\t\r\2\2\u009c\34\3\2\2\2\u009d\u009e\t\16\2\2\u009e\36\3\2\2\2\u009f" +
                    "\u00a0\t\17\2\2\u00a0 \3\2\2\2\u00a1\u00a2\t\20\2\2\u00a2\"\3\2\2\2\u00a3" +
                    "\u00a4\t\21\2\2\u00a4$\3\2\2\2\u00a5\u00a6\t\22\2\2\u00a6&\3\2\2\2\u00a7" +
                    "\u00a8\t\23\2\2\u00a8(\3\2\2\2\u00a9\u00aa\t\24\2\2\u00aa*\3\2\2\2\u00ab" +
                    "\u00ac\t\25\2\2\u00ac,\3\2\2\2\u00ad\u00ae\t\26\2\2\u00ae.\3\2\2\2\u00af" +
                    "\u00b0\t\27\2\2\u00b0\60\3\2\2\2\u00b1\u00b2\t\30\2\2\u00b2\62\3\2\2\2" +
                    "\u00b3\u00b4\t\31\2\2\u00b4\64\3\2\2\2\u00b5\u00b6\t\32\2\2\u00b6\66\3" +
                    "\2\2\2\u00b7\u00b8\t\33\2\2\u00b88\3\2\2\2\u00b9\u00ba\t\34\2\2\u00ba" +
                    ":\3\2\2\2\u00bb\u00bc\t\35\2\2\u00bc<\3\2\2\2\u00bd\u00be\t\36\2\2\u00be" +
                    ">\3\2\2\2\u00bf\u00c0\t\37\2\2\u00c0@\3\2\2\2\u00c1\u00c2\t \2\2\u00c2" +
                    "B\3\2\2\2\u00c3\u00c4\t!\2\2\u00c4D\3\2\2\2\u00c5\u00c6\t\"\2\2\u00c6" +
                    "F\3\2\2\2\16\2LRYcemow\177\u0082\u008e\3\2\3\2";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    private static final String[] _LITERAL_NAMES = makeLiteralNames();
    private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
    public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };
    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

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

    public MyLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    private static String[] makeRuleNames() {
        return new String[]{
                "KW_AND", "KW_OR", "KW_NOT", "LPAREN", "RPAREN", "IDENTIFIER", "WS",
                "LINE_COMMENT", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
                "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y",
                "Z"
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
        return "MyLexer.g4";
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
    public String[] getChannelNames() {
        return channelNames;
    }

    @Override
    public String[] getModeNames() {
        return modeNames;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }
}