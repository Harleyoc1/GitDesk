package com.harleyoconnor.gitdesk.data.remote.github

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.Label
import com.harleyoconnor.gitdesk.data.remote.Platform
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.asGitHubId
import com.harleyoconnor.gitdesk.data.remote.github.search.IssueSearch
import com.harleyoconnor.gitdesk.util.stream
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import java.net.URL
import java.util.Date
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

/**
 * @author Harley O'Connor
 */
class GitHubRemoteRepository(
    val id: Int,
    @Json(name = "full_name") override val name: RemoteRepository.Name,
    override val private: Boolean,
    override val owner: GitHubRepositoryOwner,
    override val description: String?,
    val fork: Boolean,
    @Json(name = "html_url") override val url: URL,
    @Json(name = "created_at") override val createdAt: Date,
    @Json(name = "updated_at") override val updatedAt: Date,
    @Json(name = "pushed_at") val pushedAt: Date,
    val homepage: String?,
    override val size: Int,
    override val language: String?,
    @Json(name = "forks_count") override val forks: Int,
    override val license: GitHubLicense?,
    @Json(name = "has_issues") override val hasIssues: Boolean,
    @Json(name = "parent") private val parentData: ParentRepository?
) : RemoteRepository {

    companion object {
        val ADAPTER: JsonAdapter<GitHubRemoteRepository> by lazy { MOSHI.adapter(GitHubRemoteRepository::class.java) }
    }

    override val platform: Platform = Platform.GITHUB

    override val parent: RemoteRepository?
        get() = if (fork) GitHubNetworking.getRemoteRepository(
            parentData!!.name.ownerName,
            parentData.name.repositoryName
        ) else null

    override val labels: Array<Label> by lazy {
        GitHubNetworking.getLabels(name) ?: arrayOf()
    }

    override fun isCollaborator(username: String): Boolean? {
        return GitHubNetworking.isCollaborator(username, name)
    }

    override fun getLabel(name: String): Label? {
        return labels.stream()
            .filter { it.name == name }
            .findFirst()
            .orElse(null)
    }

    override fun getIssues(
        query: String,
        sort: String,
        order: RemoteRepository.Order,
        executor: Executor
    ): CompletableFuture<Array<Issue>> {
        return CompletableFuture.supplyAsync({
            IssueSearch(
                "repo:${name.getFullName()} is:issue " + query,
                sort,
                order.asGitHubId(),
                20
            ).run()?.let {
                it.items as Array<Issue>
            }
        }, executor)
    }

    class ParentRepository(val name: RemoteRepository.Name)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GitHubRemoteRepository
        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }


}