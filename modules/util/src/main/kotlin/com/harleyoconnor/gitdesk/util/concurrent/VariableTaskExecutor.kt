package com.harleyoconnor.gitdesk.util.concurrent

import java.time.Duration
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

/**
 * Executor for a variable number of tasks, with a set cooldown between each.
 *
 * @author Harley O'Connor
 */
class VariableTaskExecutor(
    preCoolDown: Duration = Duration.ofMillis(0),
    postCoolDown: Duration = Duration.ofMillis(0)
) {

    private val preCoolDown: Long = preCoolDown.toMillis()
    private val postCoolDown: Long = postCoolDown.toMillis()

    private val executor = Executors.newSingleThreadExecutor()
    private val enqueuedFutures: Queue<CompletableFuture<*>> = LinkedList()

    fun submit(task: Runnable) {
        val future = CompletableFuture.runAsync({ Thread.sleep(preCoolDown) }, executor)
            .thenRun(task)
        future.thenRun { Thread.sleep(postCoolDown) }
        enqueuedFutures.add(future)
    }

    fun cancelAll() {
        this.enqueuedFutures.forEach { it.cancel(true) }
        this.enqueuedFutures.clear()
    }

}