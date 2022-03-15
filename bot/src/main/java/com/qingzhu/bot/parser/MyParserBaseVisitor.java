// Generated from C:/Users/ned_g/Documents/project/kefu/contact-center/im-access/src/main/java/com/qingzhu/imaccess/parser\MyParser.g4 by ANTLR 4.8
package com.qingzhu.bot.parser;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link MyParserVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public class MyParserBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements MyParserVisitor<T> {
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public T visitExpression(MyParser.ExpressionContext ctx) {
        return visitChildren(ctx);
    }
}