package com.harleyoconnor.gitdesk.data.remote

import com.harleyoconnor.gitdesk.git.repository.Remote
import java.net.URL
import java.util.Date

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

    // TODO: Issues and PRs

    class Name(
        val ownerName: String,
        val repositoryName: String
    ) {
        fun getFullName(): String = "$ownerName/$repositoryName"
    }
}