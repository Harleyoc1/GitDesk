package com.harleyoconnor.gitdesk.data.account

import com.harleyoconnor.gitdesk.data.MOSHI
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter

/**
 * @author Harley O'Connor
 */
class GitHubAccount(
    val username: String,
    @Json(name = "access_token") val accessToken: String
) {
    companion object {
        val ADAPTER: JsonAdapter<GitHubAccount> = MOSHI.adapter(GitHubAccount::class.java)
    }
}