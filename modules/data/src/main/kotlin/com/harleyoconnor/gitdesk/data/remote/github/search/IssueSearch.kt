package com.harleyoconnor.gitdesk.data.remote.github.search

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.remote.github.GitHubIssue
import com.squareup.moshi.Types

/**
 *
 * @author Harley O'Connor
 */
class IssueSearch(
    query: String,
    sort: String = "best match",
    order: String = "desc",
    perPage: Int = 5,
    page: Int = 1
) : Search<GitHubIssue>(query, sort, order, perPage, page) {

    override val id: String = "issues"

    override fun decodeResults(body: String): SearchResults<GitHubIssue>? {
        return MOSHI.adapter<SearchResults<GitHubIssue>>(
            Types.newParameterizedType(SearchResults::class.java, GitHubIssue::class.java)
        ).fromJson(body)
    }

}