package com.harleyoconnor.gitdesk.data.remote.github

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.remote.Comment
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import java.util.Date

/**
 * @author Harley O'Connor
 */
class GitHubComment(
    override val id: Int?,
    override val body: String?,
    @Json(name = "user") override val commenter: GitHubUser,
    @Json(name = "created_at") override val createdAt: Date,
    @Json(name = "updated_at") override val updatedAt: Date
): Comment {
    companion object {
        val ADAPTER: JsonAdapter<GitHubComment> by lazy { MOSHI.adapter(GitHubComment::class.java) }
    }
}