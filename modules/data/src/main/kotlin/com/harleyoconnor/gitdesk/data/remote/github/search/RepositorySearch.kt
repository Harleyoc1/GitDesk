package com.harleyoconnor.gitdesk.data.remote.github.search

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.data.remote.github.GitHubRemoteRepository
import com.squareup.moshi.Types

/**
 *
 * @author Harley O'Connor
 */
class RepositorySearch(
    query: String,
    sort: String = "best match",
    order: String = "desc",
    perPage: Int = 5,
    page: Int = 1
) : Search<GitHubRemoteRepository>(query, sort, order, perPage, page) {

    override val id: String = "repositories"

    override fun decodeResults(body: String): SearchResults<GitHubRemoteRepository>? {
        return Data.moshi.adapter<SearchResults<GitHubRemoteRepository>>(
            Types.newParameterizedType(SearchResults::class.java, GitHubRemoteRepository::class.java)
        ).fromJson(body)
    }

}