package com.harleyoconnor.gitdesk.data.remote.github.search

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.github.GitHubIssue
import com.squareup.moshi.Types
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 *
 * @author Harley O'Connor
 */
class IssueSearch(
    remote: RemoteRepository,
    query: String,
    sort: String = "best match",
    order: String = "desc",
    perPage: Int = 5,
    page: Int = 1,
    executor: Executor = Executors.newSingleThreadExecutor()
) : Search<GitHubIssue>(processQuery(remote, query), sort, order, perPage, page, executor) {

    companion object {
        fun processQuery(remote: RemoteRepository, query: String): String =
            "repo:${remote.name.getFullName()} is:issue $query"
    }

    override val id: String = "issues"

    override fun decodeResults(body: String): SearchResults<GitHubIssue>? {
        return MOSHI.adapter<SearchResults<GitHubIssue>>(
            Types.newParameterizedType(SearchResults::class.java, GitHubIssue::class.java)
        ).fromJson(body)
    }

}