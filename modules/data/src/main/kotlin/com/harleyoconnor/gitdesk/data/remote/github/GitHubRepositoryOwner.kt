package com.harleyoconnor.gitdesk.data.remote.github

import com.harleyoconnor.gitdesk.data.remote.RepositoryOwner
import com.squareup.moshi.Json

/**
 * @author Harley O'Connor
 */
class GitHubRepositoryOwner(
    @Json(name = "login") override val name: String,
    val type: Type,
): RepositoryOwner {

    enum class Type {
        @Json(name = "User") USER, @Json(name = "Organization") ORGANISATION
    }

}