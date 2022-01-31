package com.harleyoconnor.gitdesk.data.remote.github.timeline

import com.harleyoconnor.gitdesk.data.remote.github.GitHubUser
import com.harleyoconnor.gitdesk.data.remote.timeline.CommentedEvent
import com.squareup.moshi.Json
import java.util.*

data class GitHubCommentedEvent(
    @Json(name = "event") override val id: String,
    override val actor: GitHubUser,
    override val body: String,
    @Json(name = "created_at") override val createdAt: Date,
    @Json(name = "updated_at") override val updatedAt: Date
) : CommentedEvent {
}