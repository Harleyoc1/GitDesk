package com.harleyoconnor.gitdesk.data.remote.github

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.remote.Comment
import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.timeline.Timeline
import com.harleyoconnor.gitdesk.data.serialisation.qualifier.GitHubRepositoryNameFromUrl
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import java.net.URL
import java.util.Date
import java.util.concurrent.CompletableFuture

/**
 *
 * @author Harley O'Connor
 */
class GitHubIssue(
    @GitHubRepositoryNameFromUrl @Json(name = "repository_url") val parentName: RemoteRepository.Name,
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
    override val comments: Int,
    override val locked: Boolean
) : Issue {

    companion object {
        val ADAPTER: JsonAdapter<GitHubIssue> by lazy { MOSHI.adapter(GitHubIssue::class.java) }
    }

    override fun getTimeline(page: Int): Timeline? {
        return GitHubNetworking.getIssueTimeline(parentName, number, page)
    }

    override fun addComment(body: String): CompletableFuture<Comment> {
        return GitHubNetworking.postIssueComment(parentName, number, body)
    }

    override fun editComment(id: Int, body: String): CompletableFuture<Comment> {
        return GitHubNetworking.editIssueComment(parentName, id, body)
    }

    override fun deleteComment(id: Int): CompletableFuture<Boolean> {
        return GitHubNetworking.deleteIssueComment(parentName, id)
    }

    override fun editBody(body: String): CompletableFuture<Issue> {
        return GitHubNetworking.editIssueBody(parentName, number, body)
    }

    override fun close(): CompletableFuture<Issue> {
        return GitHubNetworking.closeIssue(parentName, number)
    }

    override fun open(): CompletableFuture<Issue> {
        return GitHubNetworking.openIssue(parentName, number)
    }
}