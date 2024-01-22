package com.leysoft.concurrent.coroutines

/**
 * # 1
 **/
object InfinitySeq {
    /**
     * this generator build an infinite seq, it is a lazy seq.
     * Kotlin library
     **/
    private fun fibonacci(): Sequence<Long> =
        sequence {
            var first = 0L
            var second = 1L
            while (true) {
                yield(first) // it is a suspend function
                val temp = first
                first += second
                second = temp
            }
        }

    fun fibonacci(i: Int): List<Long> = fibonacci().get(i)

    private fun <A> Sequence<A>.get(i: Int): List<A> = take(i).toList()
}
