package com.harleyoconnor.gitdesk.data.remote

import com.harleyoconnor.gitdesk.data.remote.timeline.Timeline
import com.squareup.moshi.Json
import java.net.URL
import java.util.*
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

    fun addLabel(label: Label): CompletableFuture<Void?>

    fun deleteLabel(label: Label): CompletableFuture<Void?>

    fun addAssignee(user: User): CompletableFuture<Void?>

    fun removeAssignee(user: User): CompletableFuture<Void?>

    fun getTimeline(page: Int): Timeline?

    fun addComment(body: String): CompletableFuture<Comment>

    fun editComment(id: Int, body: String): CompletableFuture<Comment>

    fun deleteComment(id: Int): CompletableFuture<Boolean>

    fun editBody(body: String): CompletableFuture<Void?>

    fun close(): CompletableFuture<Void?>

    fun open(): CompletableFuture<Void?>

    enum class State {
        @Json(name = "open") OPEN,
        @Json(name = "closed") CLOSED;

        fun other(): State {
            return values()[(ordinal + 1) % 2]
        }
    }

}