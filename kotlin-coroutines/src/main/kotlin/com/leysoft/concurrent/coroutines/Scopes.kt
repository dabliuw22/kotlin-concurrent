package com.leysoft.concurrent.coroutines

import kotlinx.coroutines.*

/**
 * # 5
 * The scope creates relationships between coroutines inside it
 * and allows us to manage the lifecycles of these coroutines
 */
object Scopes {
    fun runWithCoroutineScope(): Unit =
        runBlocking {
            /**
             * coroutineScope creates a new coroutine,
             * it suspends the previous coroutine until
             * the new one is finished. It doesn't start
             * a concurrent process.
             */
            val x =
                coroutineScope {
                    delay(1000)
                    10
                }
            println("x is calculated")
            val y =
                coroutineScope {
                    delay(1000)
                    20
                }
            println("y is calculated")
            println(x)
            println(y)
        }

    /**
     * The produced scope respects its parental responsibilities:
     * 1. inherits a context from its parent.
     * 2. waits for all its children before it can finish itself.
     * 3. cancels all its children when the parent is cancelled.
     * But it overrides the context's Job.
     */
    suspend fun longTask(): Unit =
        //  coroutineScope will not finish until all its children are finished.
        coroutineScope {
            launch {
                delay(1000)
                val name = coroutineContext[CoroutineName]?.name
                println("[$name] finished task1")
            }

            launch {
                delay(2000)
                val name = coroutineContext[CoroutineName]?.name
                println("[$name] finished task2")
            }
        }

    /**
     * "After" will be printed at the end because coroutineScope
     *  will not finish until all its children are finished
     */
    fun waitChildren(): Unit =
        runBlocking(CoroutineName("Parent")) {
            println("Before")
            longTask()
            println("After")
        }

    /**
     * A cancelled parent leads to the cancellation of unfinished children.
     */
    fun cancelParent(): Unit =
        runBlocking {
            val job =
                launch(CoroutineName("Parent")) {
                    longTask()
                }
            delay(1500)
            job.cancelAndJoin()
        }

    data class Profile(
        val userName: String,
        val followers: Long,
    )

    data class Tweet(val text: String)

    class ApiException(
        val code: Int,
        override val message: String,
    ) : RuntimeException(message)

    class TwitterService() {
        suspend fun getTweets(userName: String): List<Tweet> {
            delay(1200)
            return listOf(Tweet("Test"))
        }

        /**
         * coroutineScope is a perfect candidate to start a few concurrent calls
         */
        suspend fun getProfile(): Profile =
            coroutineScope {
                val userName = async { getUserName() }
                val followers = async { getFollowers() }
                Profile(
                    userName.await(),
                    followers.await(),
                )
            }

        private suspend fun getUserName(): String {
            delay(1000)
            return "test"
        }

        private fun getFollowers(): Long {
            throw ApiException(500, "Error...")
        }
    }

    fun getTwitterData(): Unit =
        runBlocking {
            val service = TwitterService()
            val profile =
                try {
                    service.getProfile()
                } catch (e: ApiException) {
                    null
                }
            val tweets = async { service.getTweets(profile?.userName ?: "") }
            println("Profile: $profile, Tweets: ${tweets.await()}")
        }

    /**
     * it overrides the context’s Job with SupervisorJob,
     * so it is not cancelled when a child raises an exception.
     */
    suspend fun scopeWithErrorWithSupervisorScope(): Unit =
        supervisorScope {
            launch {
                delay(1000)
                throw Error()
            }
            launch {
                delay(2000)
                println("Done")
            }
        }

    /**
     * supervisorScope is mainly used in functions that start
     * multiple independent tasks.
     */
    suspend fun multiTaskWithSupervisorScope(params: List<String>): Unit =
        supervisorScope {
            params.forEach {
                launch {
                    delay(100)
                    println("Send $it")
                }
            }
        }

    /**
     * How to use this function
     * fun main(): Unit = runBlocking {
     *     val notificationScope = CoroutineScope(SupervisorJob())
     *     val job = launch {
     *         Scopes.multipleCoroutineScopes(notificationScope)
     *     }
     *     delay(2000)
     *     job.join()
     * }
     */
    suspend fun multipleCoroutineScopes(notificationScope: CoroutineScope): Unit =
        coroutineScope {
            val name =
                async {
                    delay(100)
                    "Test"
                }
            val email =
                async {
                    delay(200)
                    "email@test.com"
                }
            val user = email.await() to name.await()
            println(user)

            // This process is not mandatory, so we are using another scope
            notificationScope.launch {
                println("Send notification to $user")
                delay(1000)
                throw RuntimeException("Error")
                // println("Notification was sent")
            }
        }
}

fun main(): Unit =
    runBlocking {
        val notificationScope = CoroutineScope(SupervisorJob())
        val job =
            launch {
                Scopes.multipleCoroutineScopes(notificationScope)
            }
        delay(2000)
        job.join()
    }
