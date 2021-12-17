package com.harleyoconnor.gitdesk.data.remote.github

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import java.net.URL
import java.util.*

/**
 * @author Harley O'Connor
 */
class GitHubRemoteRepository(
    val id: Int,
    @Json(name = "node_id") override val nodeId: String,
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
    @Json(name = "parent") private val parentData: ParentRepository?
) : GitHubNode, RemoteRepository {

    companion object {
        val ADAPTER: JsonAdapter<GitHubRemoteRepository> by lazy { MOSHI.adapter(GitHubRemoteRepository::class.java) }
    }

    override val parent: RemoteRepository?
        get() = if (fork) GitHubNetworking.getRemoteRepository(
            parentData!!.name.ownerName,
            parentData.name.repositoryName
        ) else null

    class ParentRepository(val name: RemoteRepository.Name)
}