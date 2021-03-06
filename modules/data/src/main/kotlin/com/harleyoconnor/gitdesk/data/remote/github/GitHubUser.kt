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
    @Json(name = "html_url") override val url: URL,
    @Json(name = "avatar_url") override val avatarUrl: URL
) : User {

    companion object {
        val ADAPTER: JsonAdapter<GitHubUser> by lazy { MOSHI.adapter(GitHubUser::class.java) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GitHubUser

        if (username != other.username) return false

        return true
    }

    override fun hashCode(): Int {
        return username.hashCode()
    }


}