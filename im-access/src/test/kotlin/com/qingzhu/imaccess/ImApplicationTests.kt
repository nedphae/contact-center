package com.qingzhu.imaccess

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

/**
 * Kotlin Y 组合实现
 *
 * lambda f. (lambda x. (f(x x)) lambda x. (f(x x)))
 *
 * FP YCombinator
 */

// 为了方便易懂，使用 X 用做函数 (X)->Int 的别名
typealias X = (Int) -> Int

typealias F = Function1<X, X>

// G 递归引用 G 自己
interface G : Function1<G, X>

// create a fun G from lazy blocking
fun G(block: (G) -> X) = object : G {
    // 调用函数自身 `block(g)` like as `g(g)`
    override fun invoke(g: G) = block(g)
}

fun Y(f: F): X {
    val p1 = G { g -> f { x -> g(g)(x) } }
    return p1(p1)
}

val fact: (Int) -> Int = Y { rec ->
    { n ->
        if (n == 0) 1 else n * rec(n - 1)
    }
}
val fib: (Int) -> Int = Y { f ->
    { n ->
        if (n == 1 || n == 2) 1 else f(n - 1) + f(n - 2)
    }
}

@SpringBootTest(classes = [ImAccessApplication::class])
class ImApplicationTests {

    @Autowired
    private lateinit var appContext: ApplicationContext

    @Test
    fun contextLoads() {
        // (lambda fn:
        // (lambda f: f(f))(lambda f:
        //         fn(lambda n: f(f)(n))
        // )
        //         )(lambda g:
        //         lambda n: 1 if n in [1, 2] else g(n-1) + g(n-2)
        // )(10)

        // (function (callable $fn): callable {
        //     return (function (callable $f): callable {
        //     return $f($f);
        // })(function (callable $f) use ($fn): callable {
        //     return $fn(function (int $n) use ($f): int {
        //     return $f($f)($n);
        // });
        // });
        // })(function (callable $g): callable {
        //     return function (int $n) use ($g): int {
        //     return in_array($n, [1, 2]) ? 1 : $g($n - 1) + $g($n - 2);
        // };
        // })(10);

        // (fn =>
        // (f => f(f))(f => fn(n => f(f)(n)))
        // )(g =>
        // n => [1, 2].indexOf(n) > -1 ? 1 : g(n - 1) + g(n - 2)
        // )(10);

        println(fib)
        println(fib(10))
        var lambda2: (Int) -> Int = { it }
        lambda2 = { if (it <= 2) 1 else lambda2(it - 1) + lambda2(it - 2) }
        // 通过 typealias 定义一些类型可以写成 Y 组合的 lambda 递归
        // 太麻烦了
    }

    fun efg(a: String, b: String, c: String) {
        println("$a + $b + $c")
    }

    fun efg1(a: String) = fun(b: String) = fun(c: String) {
        efg(a, b, c)
    }
}
