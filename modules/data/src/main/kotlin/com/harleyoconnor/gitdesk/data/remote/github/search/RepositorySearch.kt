package com.harleyoconnor.gitdesk.data.remote.github.search

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.remote.github.GitHubRemoteRepository
import com.squareup.moshi.Types
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 *
 * @author Harley O'Connor
 */
class RepositorySearch(
    query: String,
    sort: String = "best match",
    order: String = "desc",
    perPage: Int = 5,
    page: Int = 1,
    executor: Executor = Executors.newSingleThreadExecutor()
) : Search<GitHubRemoteRepository>(query, sort, order, perPage, page, executor) {

    override val id: String = "repositories"

    override fun decodeResults(body: String): SearchResults<GitHubRemoteRepository>? {
        return MOSHI.adapter<SearchResults<GitHubRemoteRepository>>(
            Types.newParameterizedType(SearchResults::class.java, GitHubRemoteRepository::class.java)
        ).fromJson(body)
    }

}