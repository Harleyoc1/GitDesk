package com.harleyoconnor.gitdesk.data.remote

import com.squareup.moshi.Json
import java.net.URL
import java.util.*

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

    enum class State {
        @Json(name = "open") OPEN,
        @Json(name = "closed") CLOSED,
        @Json(name = "all") ALL
    }

}