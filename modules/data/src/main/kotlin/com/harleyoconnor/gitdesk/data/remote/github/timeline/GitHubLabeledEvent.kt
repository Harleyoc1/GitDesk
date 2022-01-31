package com.harleyoconnor.gitdesk.data.remote.github.timeline

import com.harleyoconnor.gitdesk.data.remote.github.GitHubLabel
import com.harleyoconnor.gitdesk.data.remote.github.GitHubUser
import com.harleyoconnor.gitdesk.data.remote.timeline.LabeledEvent
import com.squareup.moshi.Json
import java.util.*

data class GitHubLabeledEvent(
    @Json(name = "event") override val id: String,
    override val actor: GitHubUser,
    @Json(name = "created_at") override val createdAt: Date,
    override val label: GitHubLabel
) : LabeledEvent