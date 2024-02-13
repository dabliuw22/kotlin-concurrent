package com.leysoft.concurrent.coroutines

import kotlinx.coroutines.*
import java.math.BigDecimal

/**
 * # 3
 **/

typealias Score = Float

typealias Amount = BigDecimal

/**
 * # 2
 **/
object BasicStructuredConcurrency {
    /**
     * How do you can use this function?
     * fun main() = BasicStructuredConcurrency.printHelloWorld()
     */
    fun printHelloWorld() {
        // runBlocking can't be a child. it can only be used as a root.
        runBlocking { // Parent
            /**
             * launch use the previous CoroutineScope, so runBlocking is a parent of this,
             * it is a relationship that is called structured concurrency.
             * If we use GlobalScope.launch(), we don't have structured concurrency, because
             * the runBlocking doesn't wait for the launch()
             **/
            launch { // Child 1
                // coroutineContext[CoroutineName]?.name
                delay(1000L)
                print("World")
            }
            launch { // Child 2
                delay(2000L)
                print("!")
            }
            print("Hello ")
        }
    }

    private suspend fun printCurrentCoroutineContext() {
        val ctx = currentCoroutineContext()[CoroutineName]?.name
        println(ctx)
    }

    /**
     * How do you can use this function?
     * fun main() =
     *     runBlocking {
     *         val (amount, score) = BasicStructuredConcurrency.getAmountAndScore()
     *         println(amount)
     *         println(score)
     *     }
     */
    suspend fun getAmountAndScore(): Pair<Amount, Score> =
        /**
         * coroutineScope create a scope to run suspending function, is a standard
         * function we use when we need a scope inside a suspending function.
         * It can be call
         **/
        coroutineScope {
            /**
             * Rules:
             * 1. Children inherit context from their parent. It is a basic part of a coroutine builder’s behavior.
             * 2. A parent suspends until all the children are finished.
             * 3. When the parent is cancelled, its child coroutines are cancelled too.
             * 4. When a child raises an error, it destroys the parent as well.
             **/
            val amount: Deferred<Amount> =
                // We can not run builders (async, launch) on suspending functions,
                // so we use coroutine scope functions
                async {
                    delay(1500L)
                    100.00.toBigDecimal()
                }
            val score: Deferred<Score> =
                async {
                    delay(1000L)
                    0.5F
                }
            // We can use Deferred<?>.await() function to get the value, it is a suspend function.
            amount.await() to score.await()
        }
}
