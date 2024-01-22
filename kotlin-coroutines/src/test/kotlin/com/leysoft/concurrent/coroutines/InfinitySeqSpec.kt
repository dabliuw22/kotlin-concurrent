package com.leysoft.concurrent.coroutines

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class InfinitySeqSpec : DescribeSpec({
    describe("InfinitySeq") {
        it("fibonacci(1) shouldBe [0]") {
            InfinitySeq.fibonacci(1) shouldBe listOf(0L)
        }

        it("fibonacci(2) shouldBe [0, 1, 1]") {
            InfinitySeq.fibonacci(2) shouldBe
                listOf(
                    0L, 1L,
                )
        }

        it("fibonacci(10) shouldBe [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]") {
            InfinitySeq.fibonacci(10) shouldBe
                listOf(
                    0L, 1L, 1L, 2L, 3L, 5L, 8L, 13L, 21L, 34L,
                )
        }
    }
})
