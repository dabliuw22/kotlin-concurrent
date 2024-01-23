package com.leysoft.concurrent.coroutines

import kotlinx.coroutines.*

object ExceptionHandling {
    /**
     * We can use a SupervisorJob as parent
     */
    fun nonBlockingException(): Unit =
        runBlocking {
            val job = SupervisorJob() // never use withContext(SupervisorJob())
            launch(job) {
                delay(1000)
                throw Error("Boom...")
            }

            launch(job) {
                delay(2000)
                println("Will not be printed")
            }
            delay(3000)
        }

    /**
     * We can use a CoroutineScope with SupervisorJob as parent
     */
    fun nonBlockingExceptionWithScope(): Unit =
        runBlocking {
            val scope = CoroutineScope(SupervisorJob())
            scope.launch {
                delay(1000)
                throw Error("Boom...")
            }

            scope.launch {
                delay(2000)
                println("Will not be printed")
            }
            delay(3000)
        }

    /**
     * We can use a CoroutineScope with SupervisorJob as parent
     */
    suspend fun nonBlockingExceptionWithSupervisorScope(): Unit =
        supervisorScope {
            launch {
                delay(1000)
                throw Error("Boom...")
            }

            launch {
                delay(2000)
                println("Will not be printed")
            }
            delay(3000)
        }
}

suspend fun main(): Unit = ExceptionHandling.nonBlockingExceptionWithSupervisorScope()
