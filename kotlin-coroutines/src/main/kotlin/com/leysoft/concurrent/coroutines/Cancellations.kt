package com.leysoft.concurrent.coroutines

import kotlinx.coroutines.*

/**
 * # 4
 **/
object Cancellations {
    /**
     * When the cancel() method is called:
     * 1. It finishes the job at the first suspension point.
     * 2. The children are cancelled.
     * 3. The job cancelled could not use like a parent for any new children.
     * Can use the CancellationException to pass it to the cancel() method.
     */
    suspend fun simpleCancellation() =
        coroutineScope {
            val job =
                launch {
                    repeat(1_000) {
                        println("Init $it")
                        delay(100)
                        Thread.sleep(100) // Long operation
                        println("Printing $it")
                    }
                }

            delay(1000)
            job.cancelAndJoin() // Or job.cancel(); job.join(), it is a better approach.
            println("Cancelled successfully")
        }

    data class InvalidOperationException(
        override val message: String,
    ) : CancellationException(message)

    data class Door(
        val id: String,
        var opened: Boolean = false,
    ) {
        suspend fun open(): Unit =
            if (opened) {
                throw InvalidOperationException("The door is opened") // this exception cancel a job
            } else {
                delay(100)
                this.opened = true
            }

        suspend fun close(): Unit =
            if (opened) {
                delay(200)
                this.opened = false
            } else {
                throw InvalidOperationException("The door is closed")
                // It will not be propagated to its parent. It will only cause cancellation of the current coroutine
            }
    }

    /**
     * suspend fun main() =
     *     Cancellations.openTheDoor(
     *         Cancellations.Door(
     *             "id",
     *             false,
     *         ),
     *         3
     *     )
     */
    suspend fun openTheDoor(
        door: Door,
        times: Int,
    ): Unit =
        coroutineScope {
            val job =
                launch {
                    try {
                        repeat(times) {
                            println("Open $door")
                            door.open()
                            println("$door opened")
                        }
                    } catch (e: CancellationException) {
                        println("Job was cancelled: ${e.message}")
                        ensureActive() // it is a good practice
                        throw e
                    } finally {
                        /**
                         * In this block I can release the resources.
                         * When the coroutine is canceled, we can't use
                         * suspend function in this block because the job is
                         * already in a Cancelling state.
                         * If you want to use suspend function you can use
                         * 1. a NonCancellable context
                         */
                        println("Release resources...")
                        withContext(NonCancellable) {
                            println("Close resources...")
                            delay(100)
                        }
                    }
                }

            println("isCancelled: ${job.isCancelled}")
            delay(2000)
            println("isActive: ${job.isActive}")
            println("isCancelled: ${job.isCancelled}")
            job.join()
        }

    suspend fun intensiveOperationWithYield(): Unit =
        coroutineScope {
            val job = Job()
            launch(job + CoroutineName("Intensive Operation")) {
                repeat(1000) {
                    Thread.sleep(200)
                    yield() // This function suspends and resume the coroutine
                    println("Printing $it")
                }
            }
            delay(1100)
            job.cancelAndJoin()
            println("Cancelled successfully")
            delay(1000)
        }

    suspend fun intensiveOperationWithIsActive(): Unit =
        coroutineScope {
            val job = Job()
            launch(job + CoroutineName("Intensive Operation")) {
                do {
                    Thread.sleep(200)
                    println("Printing")
                } while (isActive)
            }
            delay(1100)
            job.cancelAndJoin()
            println("Cancelled successfully")
            delay(1000)
        }

    suspend fun intensiveOperationWithEnsureActive(): Unit =
        coroutineScope {
            val job = Job()
            launch(job + CoroutineName("Intensive Operation")) {
                repeat(1000) {
                    Thread.sleep(200)
                    ensureActive() // throws CancellationException if Job is not active. This needs a CoroutineScope (or CoroutineContext, or Job)
                    println("Printing $it")
                }
            }
            delay(1100)
            job.cancelAndJoin()
            println("Cancelled successfully")
            delay(1000)
        }
}
