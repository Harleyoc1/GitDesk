package com.harleyoconnor.gitdesk.data.remote

import com.harleyoconnor.gitdesk.git.repository.Remote
import java.net.URL
import java.util.Date
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

/**
 * A [Remote] Git repository containing additional context from the server.
 *
 * @author Harley O'Connor
 */
interface RemoteRepository : Remote {

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

    fun getIssues(query: String, sort: String, order: Order, executor: Executor): CompletableFuture<Array<Issue>>

    // TODO: PRs

    data class Name(
        val ownerName: String,
        val repositoryName: String
    ) {
        fun getFullName(): String = "$ownerName/$repositoryName"
    }

    enum class Order {
        ASCENDING, DESCENDING
    }
}