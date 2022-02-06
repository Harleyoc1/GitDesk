package com.harleyoconnor.gitdesk.data.account

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.remote.Platform
import com.harleyoconnor.gitdesk.data.remote.PlatformAccount
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter

/**
 * @author Harley O'Connor
 */
class GitHubAccount(
    override val username: String,
    @Json(name = "access_token") override val accessToken: String
) : PlatformAccount {
    companion object {
        val ADAPTER: JsonAdapter<GitHubAccount> = MOSHI.adapter(GitHubAccount::class.java)

        fun getForActiveSession(): GitHubAccount? {
            return Session.getOrLoad()?.getGitHubAccount()
        }
    }

    override val platform: Platform = Platform.GITHUB

}