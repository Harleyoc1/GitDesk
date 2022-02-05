package com.harleyoconnor.gitdesk.data.remote.github.timeline

import com.harleyoconnor.gitdesk.data.remote.github.GitHubUser
import com.harleyoconnor.gitdesk.data.remote.timeline.EventType
import com.harleyoconnor.gitdesk.data.remote.timeline.ReviewedEvent
import com.harleyoconnor.gitdesk.data.serialisation.qualifier.GitHubEventType
import com.squareup.moshi.Json
import java.util.Date

data class GitHubReviewedEvent(
    @GitHubEventType @Json(name = "event") override val type: EventType,
    @Json(name = "user") override val actor: GitHubUser,
    @Json(name = "submitted_at") override val createdAt: Date,
    override val body: String,
    override val state: ReviewedEvent.State
) : ReviewedEvent