package com.harleyoconnor.gitdesk.data.remote.github

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.remote.User
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import java.net.URL

/**
 * @author Harley O'Connor
 */
class GitHubUser(
    @Json(name = "login") override val username: String,
    override val name: String,
    @Json(name = "html_url") override val url: URL,
    @Json(name = "avatar_url") override val avatarUrl: URL,
    @Json(name = "node_id") override val nodeId: String
) : User, GitHubNode {

    companion object {
        val ADAPTER: JsonAdapter<GitHubUser> by lazy { MOSHI.adapter(GitHubUser::class.java) }
    }

}