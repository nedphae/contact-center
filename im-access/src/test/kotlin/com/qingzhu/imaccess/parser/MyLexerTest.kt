package com.qingzhu.imaccess.parser

import com.qingzhu.common.domain.shared.AbstractSpecification
import com.qingzhu.common.domain.shared.Specification
import com.qingzhu.imaccess.util.ParserUtils
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * 服务器的语法分析
 */
internal class MyLexerTest {

    @Test
    fun testLexer() {
        val test = """not (false and true or false) and true"""

        val map = mapOf("true" to NotFilter(), "false" to FalseFilter())

        val parserUtils = ParserUtils(test) {
            map[it] as Specification<Boolean>
        }
        assertTrue(parserUtils.calExpression().isSatisfiedBy(false))
    }

    class NotFilter : AbstractSpecification<Boolean>() {
        override fun isSatisfiedBy(t: Boolean): Boolean {
            return !t
        }
    }

    class FalseFilter : AbstractSpecification<Boolean>() {
        override fun isSatisfiedBy(t: Boolean): Boolean {
            return false
        }
    }

}