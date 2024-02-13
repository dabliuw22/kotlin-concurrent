package com.leysoft.concurrent.coroutines

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BasicStructuredConcurrencySpec : FunSpec() {
    init {
        coroutineTestScope = true

        test("BasicStructuredConcurrency.getAmountAndScore() should return (100, 0.5)") {
            val (amount, score) = BasicStructuredConcurrency.getAmountAndScore()
            amount shouldBe 100.00.toBigDecimal()
            score shouldBe 0.50
        }
    }
}
