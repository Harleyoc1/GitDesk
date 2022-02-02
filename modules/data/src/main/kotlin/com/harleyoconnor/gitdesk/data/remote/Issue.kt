package com.harleyoconnor.gitdesk.data.remote

import com.harleyoconnor.gitdesk.data.remote.timeline.Timeline
import com.squareup.moshi.Json
import java.net.URL
import java.util.Date
import java.util.concurrent.CompletableFuture

/**
 *
 * @author Harley O'Connor
 */
interface Issue {

    val number: Int

    val title: String

    val author: User

    val url: URL

    val labels: Array<out Label>

    val state: State

    val assignees: Array<out User>

    val createdAt: Date

    val updatedAt: Date

    val closedAt: Date?

    val body: String?

    val comments: Int

    val locked: Boolean

    fun getTimeline(page: Int): Timeline?

    fun addComment(body: String): CompletableFuture<Comment>

    fun editComment(id: Int, body: String): CompletableFuture<Comment>

    fun deleteComment(id: Int): CompletableFuture<Boolean>

    fun editBody(body: String): CompletableFuture<Issue>

    fun close(): CompletableFuture<Issue>

    fun open(): CompletableFuture<Issue>

    enum class State {
        @Json(name = "open") OPEN,
        @Json(name = "closed") CLOSED
    }

}