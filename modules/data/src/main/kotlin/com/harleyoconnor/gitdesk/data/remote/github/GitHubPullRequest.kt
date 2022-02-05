package com.harleyoconnor.gitdesk.data.remote.github

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.PullRequest
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.serialisation.qualifier.GitHubRepositoryNameFromUrl
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import java.net.URL
import java.util.Date

/**
 *
 * @author Harley O'Connor
 */
class GitHubPullRequest(
    @GitHubRepositoryNameFromUrl @Json(name = "repository_url") parentName: RemoteRepository.Name,
    number: Int,
    title: String,
    @Json(name = "user") author: GitHubUser,
    @Json(name = "html_url") url: URL,
    labels: Array<GitHubLabel>,
    state: Issue.State,
    assignees: Array<GitHubUser>,
    @Json(name = "created_at") createdAt: Date,
    @Json(name = "updated_at") updatedAt: Date,
    @Json(name = "closed_at") closedAt: Date?,
    body: String?,
    comments: Int,
    locked: Boolean,
    override val head: GitHubRemoteBranch?,
    override val base: GitHubRemoteBranch?,
    override val draft: Boolean,
    override val mergeable: Boolean?,
    override val rebaseable: Boolean?,
    override val merged: Boolean,
    @Json(name = "merged_by") override val mergedBy: GitHubUser?,
    @Json(name = "merged_at") override val mergedAt: Date?
) : GitHubIssue(
    parentName,
    number,
    title,
    author,
    url,
    labels,
    state,
    assignees,
    createdAt,
    updatedAt,
    closedAt,
    body,
    comments,
    locked
), PullRequest {

    companion object {
        val ADAPTER: JsonAdapter<GitHubPullRequest> by lazy { MOSHI.adapter(GitHubPullRequest::class.java) }
    }

}