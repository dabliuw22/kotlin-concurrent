package com.leysoft.concurrent.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

/**
 * # 7
 * Dispatchers.Default: Is used to run CPU-intensive operations.
 * It is the default dispatcher if you don't set a dispatcher.
 * The numbers of threads = core threads.
 * You can use Dispatchers.Default .limitedParallelism(n)
 *
 * Dispatchers.Main: Is used to UI operations, Android, JavaFx, Swing apps
 * could be use it.
 *
 * Dispatchers.IO: is designed to be used when we block threads with
 * I/O operations for instance, when we read/write files, or call
 * blocking functions.
 * The numbers of threads = unlimited pool of threads.
 *
 * Dispatchers.Default and Dispatchers.IO share the same pool of threads.
 *
 * The best practice for managing intensively blocking threads is
 * define your dispatchers that have their independent limits.
 */
object ExecutionContexts {
    /**
     * Since Dispatchers.Default and Dispatchers.IO share the same thread pool
     * when you shift the dispatcher the task will be executed in the same thread.
     */
    suspend fun multiDispatchersExecution(): Unit =
        supervisorScope {
            launch(Dispatchers.Default) {
                println(Thread.currentThread().name)
                withContext(Dispatchers.IO) {
                    println(Thread.currentThread().name)
                }
            }
        }
}

suspend fun main(): Unit = ExecutionContexts.multiDispatchersExecution()
