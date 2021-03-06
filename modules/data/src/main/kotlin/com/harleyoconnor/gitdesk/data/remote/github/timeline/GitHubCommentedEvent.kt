package com.harleyoconnor.gitdesk.data.remote.github.timeline

import com.harleyoconnor.gitdesk.data.remote.github.GitHubUser
import com.harleyoconnor.gitdesk.data.remote.timeline.CommentedEvent
import com.harleyoconnor.gitdesk.data.remote.timeline.EventType
import com.harleyoconnor.gitdesk.data.serialisation.qualifier.GitHubEventType
import com.squareup.moshi.Json
import java.util.*

data class GitHubCommentedEvent(
    @GitHubEventType @Json(name = "event") override val type: EventType,
    @Json(name = "id") override val commentId: Int,
    override val actor: GitHubUser,
    override val body: String,
    @Json(name = "created_at") override val createdAt: Date,
    @Json(name = "updated_at") override val updatedAt: Date
) : CommentedEvent