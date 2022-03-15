// Generated from C:/Users/ned_g/Documents/project/kefu/contact-center/im-access/src/main/java/com/qingzhu/imaccess/parser\MyParser.g4 by ANTLR 4.8
package com.qingzhu.bot.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MyParser}.
 */
public interface MyParserListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link MyParser#expression}.
     *
     * @param ctx the parse tree
     */
    void enterExpression(MyParser.ExpressionContext ctx);

    /**
     * Exit a parse tree produced by {@link MyParser#expression}.
     *
     * @param ctx the parse tree
     */
    void exitExpression(MyParser.ExpressionContext ctx);
}