package com.leysoft.concurrent.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce

/**
 * # 8
 * Channels are used to send and receive elements
 * interface Channel<E> : SendChannel<E>, ReceiveChannel<E>
 * send and receive are suspending functions.
 *
 * receive: When we try to receive and there are no elements in the
 * channel, the coroutine is suspended.
 *
 * send: It will be suspended when the channel reaches the capacity.
 **/
object Channels {
    /**
     * This is a bad approach because if one coroutine stops
     * to produce messages, another will wait for messages forever.
     */
    suspend fun oneToOne(numberOfMessages: Int): Unit =
        coroutineScope {
            val channel = Channel<Int>()
            launch {
                repeat(numberOfMessages) { index ->
                    delay(1000)
                    println("Producing next one")
                    channel.send(index * 2)
                }
            }

            launch {
                repeat(numberOfMessages) {
                    val message = channel.receive()
                    println(message)
                }
            }
        }

    /**
     * This is a bad approach because if one coroutine stops
     * to produce messages, another will wait for messages forever.
     *
     * consumeEach function uses a for-loop under the hood,
     * but it also cancels the channel once it has consumed all its elements
     *
     * The best approach is the produce function.
     */
    suspend fun oneToOneWithConsumeEach(): Unit =
        coroutineScope {
            val channel = Channel<Int>()
            launch {
                repeat(5) { index ->
                    delay(1000)
                    println("Producing next one")
                    channel.send(index * 2)
                }
                channel.close()
            }

            launch {
                for (message in channel) {
                    println(message)
                }
                // channel.consumeEach { println(it) }
            }
        }

    /**
     * This function produces a channel with next positive
     * integers from 0 to `max`.
     *
     * The produce function closes the channel whenever the builder
     * coroutine ends in any way (finished, stopped, cancelled)
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun oneToOneWithProduce(): Unit =
        coroutineScope {
            val channel =
                produce {
                    repeat(5) { index ->
                        delay(1000)
                        println("Producing next one")
                        send(index * 2)
                    }
                }

            channel.consumeEach { println(it) }
        }
}
