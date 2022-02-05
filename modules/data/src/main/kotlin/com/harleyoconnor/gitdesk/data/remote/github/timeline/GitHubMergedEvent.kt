package com.harleyoconnor.gitdesk.data.remote.github.timeline

import com.harleyoconnor.gitdesk.data.remote.github.GitHubUser
import com.harleyoconnor.gitdesk.data.remote.timeline.EventType
import com.harleyoconnor.gitdesk.data.remote.timeline.MergedEvent
import com.harleyoconnor.gitdesk.data.serialisation.qualifier.GitHubEventType
import com.squareup.moshi.Json
import java.util.Date

data class GitHubMergedEvent(
    @GitHubEventType @Json(name = "event") override val type: EventType,
    override val actor: GitHubUser,
    @Json(name = "created_at") override val createdAt: Date,
    @Json(name = "commit_id") override val commitId: String
) : MergedEvent