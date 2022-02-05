package com.harleyoconnor.gitdesk.data.remote.github

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.remote.Comment
import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.Label
import com.harleyoconnor.gitdesk.data.remote.PullRequest
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.User
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
open class GitHubIssue(
    @GitHubRepositoryNameFromUrl @Json(name = "repository_url") parentName: RemoteRepository.Name,
    override val number: Int,
    override val title: String,
    @Json(name = "user") override val author: GitHubUser,
    @Json(name = "html_url") override val url: URL,
    @Json(name = "labels") private val _labels: MutableList<GitHubLabel>,
    @Json(name = "state") private var _state: Issue.State,
    @Json(name = "assignees") private val _assignees: MutableList<GitHubUser>,
    @Json(name = "created_at") override val createdAt: Date,
    @Json(name = "updated_at") override val updatedAt: Date,
    @Json(name = "closed_at") override val closedAt: Date?,
    @Json(name = "body") private var _body: String?,
    @Json(name = "comments") private var _comments: Int,
    override val locked: Boolean
) : Issue {

    companion object {
        val ADAPTER: JsonAdapter<GitHubIssue> by lazy { MOSHI.adapter(GitHubIssue::class.java) }
    }

    override val labels: Array<GitHubLabel>
        get() = _labels.toTypedArray()

    override val state: Issue.State
        get() = _state

    override val assignees: Array<GitHubUser>
        get() = _assignees.toTypedArray()

    override val body: String?
        get() = _body

    override val comments: Int
        get() = _comments

    protected open val parentName: RemoteRepository.Name = parentName

    override fun addLabel(label: Label): CompletableFuture<Void?> {
        return GitHubNetworking.addLabel(parentName, number, label.name)
            .thenApply {
                _labels.add(label as GitHubLabel); it
            }
    }

    override fun deleteLabel(label: Label): CompletableFuture<Void?> {
        return GitHubNetworking.deleteLabel(parentName, number, label.name)
            .thenApply {
                _labels.remove(label as GitHubLabel); it
            }
    }

    override fun addAssignee(user: User): CompletableFuture<Void?> {
        return GitHubNetworking.addAssignee(parentName, number, this is PullRequest, user.username)
            .thenApply {
                _assignees.add(user as GitHubUser); it
            }
    }

    override fun removeAssignee(user: User): CompletableFuture<Void?> {
        return GitHubNetworking.removeAssignee(parentName, number, this is PullRequest, user.username)
            .thenApply {
                _assignees.remove(user as GitHubUser); it
            }
    }

    override fun getTimeline(page: Int): Timeline? {
        return GitHubNetworking.getIssueTimeline(parentName, number, page)
    }

    override fun addComment(body: String): CompletableFuture<Comment> {
        return GitHubNetworking.addIssueComment(parentName, number, body)
            .thenApply {
                this._comments++; it
            }
    }

    override fun editComment(id: Int, body: String): CompletableFuture<Comment> {
        return GitHubNetworking.editIssueComment(parentName, id, body)
    }

    override fun deleteComment(id: Int): CompletableFuture<Boolean> {
        return GitHubNetworking.deleteIssueComment(parentName, id)
            .thenApply {
                this._comments--; it
            }
    }

    override fun editBody(body: String): CompletableFuture<Void?> {
        return GitHubNetworking.editIssueBody(parentName, number, body)
            .thenApply {
                this._body = body; it
            }
    }

    override fun close(): CompletableFuture<Void?> {
        return GitHubNetworking.closeIssue(parentName, number)
            .thenApply {
                this._state = Issue.State.CLOSED; it
            }
    }

    override fun open(): CompletableFuture<Void?> {
        return GitHubNetworking.openIssue(parentName, number)
            .thenApply {
                this._state = Issue.State.OPEN
                it
            }
    }
}