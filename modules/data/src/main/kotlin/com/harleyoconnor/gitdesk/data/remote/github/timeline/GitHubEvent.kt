package com.harleyoconnor.gitdesk.data.remote.github.timeline

import com.harleyoconnor.gitdesk.data.remote.github.GitHubUser
import com.harleyoconnor.gitdesk.data.remote.timeline.Event
import com.squareup.moshi.Json
import java.util.*

/**
 * Generic GitHub event. This is one in which we don't need additional context to display it.
 *
 * @author Harley O'Connor
 */
data class GitHubEvent(
    @Json(name = "event") override val id: String,
    override val actor: GitHubUser,
    @Json(name = "created_at") override val createdAt: Date
) : Event