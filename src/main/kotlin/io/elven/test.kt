package io.elven

fun main() {
    class foo2 (val bar2 : Int)
    class foo (val bar: Int, val f : foo2 = foo2(bar))

    val obj = arrayOf(foo(3), foo(6), foo(9))
        .asSequence()
        .filter { it.f.bar2 > 4 }
        .filter { it.f.bar2 < 10}
        .toList()
}