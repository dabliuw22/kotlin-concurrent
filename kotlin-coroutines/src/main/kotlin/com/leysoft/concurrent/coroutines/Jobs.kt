package com.leysoft.concurrent.coroutines

import kotlinx.coroutines.*

/**
 * # 3
 **/
object Jobs {
    /**
     * Jobs states:
     * 1. NEW: When the job is created with start = CoroutineStart.LAZY,
     * to start this job you will use .start() function. The next state is ACTIVE.
     * 2. ACTIVE: When the job is created with a coroutine builder,
     * the next state could be CANCELLING or COMPLETING.
     * 3. COMPLETING: When the job is done, where it waits for its children,
     * the next state could be COMPLETED or CANCELLING.
     * 4. CANCELLING: When the job fails, or it is cancelled. The next state is CANCELLED.
     * In this state we have the last chance to do some clean-up, like resources.
     * 5. CANCELLED: When the previous step is done.
     * 6. COMPLETED: When the job children are done.
     */

    /**
     * Every coroutine builder create its own JOB. Deferred<T> implement a Job interface.
     * Job is the only coroutine context that is not inherited by a coroutine from a coroutine
     */
    fun inherited(): Unit =
        runBlocking {
            val name = CoroutineName("Test")
            val parentJob = coroutineContext.job

            val coroutine =
                launch(name + parentJob) {
                    /**
                     * if you replace the parent job, the structured concurrency will not work. launch(Job()),
                     * because the parent does not wait for its children, it does have relation with them.
                     */
                    val childName = coroutineContext[CoroutineName]
                    println(childName == name) // true
                    val childJob = coroutineContext[Job]
                    println(childJob == parentJob) // false
                    println(childJob == parentJob.children.first()) // true
                }

            coroutine.join()
        }

    fun getChildren(): Unit =
        runBlocking {
            val parentJob: Job = coroutineContext.job
            val job1: Job =
                launch {
                    delay(1000)
                }
            println(job1 == parentJob) // false
            val job2: Job =
                launch {
                    delay(1000)
                }
            println(job2 == parentJob) // false
            val parentChildren: Sequence<Job> = parentJob.children
            println(parentChildren.first() == job1)

            // we can use the join() method to wait the children
            parentJob.children.forEach { it.join() } // job1.join(); job2.join();
        }

    suspend fun jobWithoutACoroutine(): Unit =
        coroutineScope {
            val parent = Job()
            launch(parent) { // the new job replaces one from parent
                delay(1000)
                println("Text 1")
            }
            launch(parent) { // the new job replaces one from parent
                delay(2000)
                println("Text 2")
            }
            // parent.join(); println("Will not be printed") // Here we will await forever, Don't use it without children.join
            parent.children.forEach { it.join() }
        }

    fun completeParentJob(): Unit =
        runBlocking {
            val coroutineParent = coroutineContext.job
            val myParentJob = Job()

            launch(myParentJob) {
                repeat(5) {
                    delay(200)
                    println("Rep$it")
                }
            }

            launch {
                delay(500)
                myParentJob.complete() // We completed the myParentJob job, it doesn't allow new children
            }

            myParentJob.join()

            // this coroutine will not run, because the parent was completed.
            launch(myParentJob) {
                println("Will not be printed")
            }

            println("Done")
        }

    fun completeParentJobWithError(): Unit =
        runBlocking {
            val coroutineParent = coroutineContext.job
            val myParentJob = Job()

            launch(myParentJob) {
                repeat(5) { // they ran partially, because the parent cancels it when the error is thrown.
                    delay(200)
                    println("Rep$it")
                }
            }

            launch {
                delay(500)
                myParentJob.completeExceptionally(
                    RuntimeException("Error"),
                ) // We completed the myParentJob job, all the children will be cancelled immediately
            }

            myParentJob.join()

            // this coroutine will not run, because the parent was completed with error.
            launch(myParentJob) {
                println("Will not be printed")
            }

            println("Done")
        }

    suspend fun complete(): Unit =
        coroutineScope {
            val parentJob = coroutineContext.job
            val job = Job(parentJob) // we can create a relationship between parent and children.

            launch(job) {
                delay(1000)
                println("Text 1")
            }

            launch(job) {
                delay(1000)
                println("Text 2")
            }

            job.complete() // It is often used after we start the last coroutine on a job.
            job.join() // We can just wait for the job completion using the join function.
        }
}
