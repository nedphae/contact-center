package com.qingzhu.imaccess.util

import com.qingzhu.common.domain.shared.NotSpecification
import com.qingzhu.common.domain.shared.Specification
import com.qingzhu.imaccess.parser.MyLexer
import com.qingzhu.imaccess.parser.MyParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

/**
 * eg: filter1 AND filter2 OR (NOT filter3 AND filter4)
 * filter* 是要调用的过滤器名称(需要转换成小写)
 * 过滤器返回 Boolean
 */
class ParserUtils<T>(
    input: String,
    private val block: (lowerCaseString: String) -> Specification<T>
) {
    private val expression: MyParser.ExpressionContext

    init {
        val lexer = MyLexer(CharStreams.fromString(input))
        val parser = MyParser(CommonTokenStream(lexer))
        // 生成表达式
        expression = parser.expression()
    }

    fun calExpression(): Specification<T> {
        return getLeft(expression)
    }

    /**
     * 深度优先 (从左到右)遍历
     */
    private fun getLeft(expr: MyParser.ExpressionContext): Specification<T> {
        if (expr.leftExpr != null) {
            val left = getLeft(expr.leftExpr)
            val right = getLeft(expr.rightExpr)
            return when {
                expr.KW_AND() != null -> left.and(right)
                expr.KW_OR() != null -> left.or(right)
                else -> throw RuntimeException()
            }
        } else {
            // TODO 后续把文法命名吗？
            return when {
                // 括号
                expr.LPAREN() != null -> getLeft(expr.getChild(1) as MyParser.ExpressionContext)
                // 非
                expr.KW_NOT() != null -> NotSpecification(getLeft(expr.getChild(1) as MyParser.ExpressionContext))
                // 调用过滤方法
                expr.IDENTIFIER() != null -> block(expr.IDENTIFIER().text.toLowerCase())
                else -> throw RuntimeException()
            }
        }
    }
}