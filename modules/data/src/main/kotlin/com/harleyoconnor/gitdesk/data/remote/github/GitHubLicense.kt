package com.harleyoconnor.gitdesk.data.remote.github

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.remote.License
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter

/**
 * @author Harley O'Connor
 */
class GitHubLicense(
    override val key: String,
    override val name: String,
    @Json(name = "spdx_id") override val spdxId: String,
    @Json(name = "node_id") override val nodeId: String
): License, GitHubNode {

    companion object {
        val ADAPTER: JsonAdapter<GitHubLicense> by lazy { MOSHI.adapter(GitHubLicense::class.java) }
    }

}