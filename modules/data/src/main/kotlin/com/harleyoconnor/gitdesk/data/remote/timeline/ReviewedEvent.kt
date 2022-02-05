package com.harleyoconnor.gitdesk.data.remote.timeline

import com.squareup.moshi.Json

interface ReviewedEvent : Event {

    val body: String

    val state: State

    enum class State {
        @Json(name = "commented") COMMENTED,
        @Json(name = "changes_requested") CHANGES_REQUESTED,
        @Json(name = "approved") APPROVED
    }

}