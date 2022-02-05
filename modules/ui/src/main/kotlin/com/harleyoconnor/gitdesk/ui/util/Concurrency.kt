package com.harleyoconnor.gitdesk.ui.util

import com.harleyoconnor.gitdesk.ui.Application
import java.util.concurrent.CompletableFuture

fun <U> supplyInBackground(supplier: () -> U): CompletableFuture<U> {
    return CompletableFuture.supplyAsync(supplier, Application.getInstance().backgroundExecutor)
}

fun <T> CompletableFuture<T>.thenAcceptOnMainThread(action: (T) -> Unit): CompletableFuture<Void?> {
    return this.thenAcceptAsync(action, Application.getInstance().mainThreadExecutor)
}

fun CompletableFuture<Void?>.exceptionallyOnMainThread(action: (Throwable) -> Unit): CompletableFuture<Void?> {
    return this.exceptionallyAsync({ action(it); null }, Application.getInstance().mainThreadExecutor)
}