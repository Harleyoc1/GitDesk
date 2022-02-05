package com.harleyoconnor.gitdesk.data.remote.github.timeline

import com.harleyoconnor.gitdesk.data.remote.timeline.CommittedEvent
import com.harleyoconnor.gitdesk.data.remote.timeline.EventType
import com.harleyoconnor.gitdesk.data.serialisation.qualifier.GitHubEventType
import com.squareup.moshi.Json
import java.net.URL

/**
 *
 * @author Harley O'Connor
 */
class GitHubCommittedEvent(
    @GitHubEventType @Json(name = "event") override val type: EventType,
    @Json(name = "sha") override val hash: String,
    @Json(name = "html_url") override val url: URL,
    override val author: CommittedEvent.GitUser,
    override val committer: CommittedEvent.GitUser,
    override val message: String
) : CommittedEvent