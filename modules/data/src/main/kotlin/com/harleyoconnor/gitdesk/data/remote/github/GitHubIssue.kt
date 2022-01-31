package com.harleyoconnor.gitdesk.data.remote.github

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.timeline.Timeline
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import java.net.URL
import java.util.*

/**
 *
 * @author Harley O'Connor
 */
class GitHubIssue(
    override val number: Int,
    override val title: String,
    @Json(name = "user") override val author: GitHubUser,
    @Json(name = "html_url") override val url: URL,
    override val labels: Array<GitHubLabel>,
    override val state: Issue.State,
    override val assignees: Array<GitHubUser>,
    @Json(name = "created_at") override val createdAt: Date,
    @Json(name = "updated_at") override val updatedAt: Date,
    @Json(name = "closed_at") override val closedAt: Date?,
    override val body: String?,
    override val comments: Int
) : Issue {

    companion object {
        val ADAPTER: JsonAdapter<GitHubIssue> by lazy { MOSHI.adapter(GitHubIssue::class.java) }
    }

    override fun getTimeline(name: RemoteRepository.Name, page: Int): Timeline? {
        return GitHubNetworking.getIssueTimeline(name, number, page)
    }
}