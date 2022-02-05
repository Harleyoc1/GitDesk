package com.harleyoconnor.gitdesk.data.remote.github.search

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.PullRequest
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.User
import com.harleyoconnor.gitdesk.data.remote.github.GitHubIssue
import com.harleyoconnor.gitdesk.data.remote.github.GitHubLabel
import com.harleyoconnor.gitdesk.data.remote.github.GitHubRemoteBranch
import com.harleyoconnor.gitdesk.data.remote.github.GitHubUser
import com.harleyoconnor.gitdesk.data.remote.github.search.PullRequestSearch.PullRequestSearchResult.MergedAtAdapter
import com.harleyoconnor.gitdesk.data.serialisation.qualifier.GitHubRepositoryNameFromUrl
import com.harleyoconnor.gitdesk.data.serialisation.util.findName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Types
import org.apache.logging.log4j.LogManager
import java.net.URL
import java.util.Date
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Returns search results for [PullRequest] data from GitHub.
 *
 * Consumers should note that the data returned by GitHub's search API does not include the `head`, `base`, `mergable`,
 * and `rebaseable` [PullRequest] properties, meaning they will all be `null` for all returned [PullRequest] objects.
 *
 * @author Harley O'Connor
 */
class PullRequestSearch(
    remote: RemoteRepository,
    query: String,
    sort: String = "best match",
    order: String = "desc",
    perPage: Int = 5,
    page: Int = 1,
    executor: Executor = Executors.newSingleThreadExecutor()
) : Search<PullRequestSearch.PullRequestSearchResult>(
    processQuery(remote, query),
    sort,
    order,
    perPage,
    page,
    executor
) {

    companion object {
        fun processQuery(remote: RemoteRepository, query: String): String =
            "repo:${remote.name.getFullName()} is:pull-request $query"
    }

    override val id: String = "issues"

    override fun decodeResults(body: String): SearchResults<PullRequestSearchResult>? {
        try {
            return MOSHI.adapter<SearchResults<PullRequestSearchResult>>(
                Types.newParameterizedType(SearchResults::class.java, PullRequestSearchResult::class.java)
            ).fromJson(body)
        } catch (e: Exception) {
            LogManager.getLogger().error(e)
            throw RuntimeException(e)
        }
    }

    /**
     * A custom [PullRequest] class is required because the GitHub Search API returns the merged data in a nested
     * object. This means to work out [mergedAt] a custom [MergedAtAdapter] is used.
     */
    class PullRequestSearchResult(
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
        @Json(name = "merged_by") override val mergedBy: GitHubUser?,
        @MergedAtFromPullRequest @Json(name = "pull_request") override val mergedAt: Date?
    ) : GitHubIssue(
        parentName,
        number,
        title,
        author,
        url,
        labels.toMutableList(),
        state,
        assignees.toMutableList(),
        createdAt,
        updatedAt,
        closedAt,
        body,
        comments,
        locked
    ), PullRequest {

        companion object {
            val ADAPTER: JsonAdapter<PullRequestSearchResult> by lazy { MOSHI.adapter(PullRequestSearchResult::class.java) }
        }

        @Retention(AnnotationRetention.RUNTIME)
        @JsonQualifier
        annotation class MergedAtFromPullRequest

        /**
         * Reads the [mergedAt] field from the nested `pull_request` object in each pull request item returned by
         * GitHub's search API.
         */
        object MergedAtAdapter : JsonAdapter<Date>() {

            override fun fromJson(reader: JsonReader): Date? {
                var mergedAt: Date? = null
                reader.beginObject()
                reader.findName("merged_at")
                val merged = reader.peek() == JsonReader.Token.STRING
                if (merged) {
                    mergedAt = MOSHI.adapter(Date::class.java).fromJson(reader)
                }
                while (reader.hasNext()) {
                    if (reader.peek() == JsonReader.Token.NAME) {
                        reader.skipName()
                    } else {
                        reader.skipValue()
                    }
                }
                reader.endObject()
                return mergedAt
            }

            override fun toJson(writer: JsonWriter, value: Date?) {
                assert(false) // Should never be used.
            }
        }

        override val merged: Boolean = this.mergedAt != null

        override fun merge(mergedBy: User): CompletableFuture<PullRequest.MergeResponse> {
            return CompletableFuture.completedFuture(null)
        }

    }

}