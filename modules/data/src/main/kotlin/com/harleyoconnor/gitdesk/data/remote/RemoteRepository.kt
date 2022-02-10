package com.harleyoconnor.gitdesk.data.remote

import com.harleyoconnor.gitdesk.git.repository.Remote
import java.net.URL
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

/**
 * A [Remote] Git repository containing additional context from the server.
 *
 * @author Harley O'Connor
 */
interface RemoteRepository : Remote {

    val platform: Platform

    val name: Name

    val private: Boolean

    val owner: RepositoryOwner

    override val url: URL

    val description: String?

    val parent: RemoteRepository?
    val forks: Int

    val createdAt: Date
    val updatedAt: Date

    val size: Int

    val language: String?

    val license: License?

    val hasIssues: Boolean

    val labels: Array<Label>

    fun getBranchUrl(ref: String): URL

    fun getCommitUrl(commitId: String): URL

    fun isCollaborator(username: String): Boolean?

    fun getLabel(name: String): Label?

    fun getIssues(
        query: String,
        sort: Issue.Sort,
        sortOrder: SortOrder,
        page: Int,
        executor: Executor
    ): CompletableFuture<Array<out Issue>>

    fun addIssue(title: String, body: String): CompletableFuture<Issue>

    fun getPullRequests(
        query: String,
        sort: Issue.Sort,
        sortOrder: SortOrder,
        page: Int,
        executor: Executor
    ): CompletableFuture<Array<out PullRequest>>

    data class Name(
        val ownerName: String,
        val repositoryName: String
    ) {
        fun getFullName(): String = "$ownerName/$repositoryName"
    }

    enum class Sort(
        val gitHubId: String
    ) {
        BEST_MATCH("best match"),
        STARS("stars"),
        FORKS("forks"),
        HELP_WANTED_ISSUES("help-wanted-issues"),
        UPDATED("updated")
    }

    enum class SortOrder(
        val gitHubId: String
    ) {
        ASCENDING("asc"),
        DESCENDING("desc");

        fun other(): SortOrder {
            return values()[(ordinal + 1) % 2]
        }
    }
}