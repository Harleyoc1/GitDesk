package com.harleyoconnor.gitdesk.data.remote

import com.squareup.moshi.Json

/**
 * @author Harley O'Connor
 */
interface RepositoryOwner {

    val name: String

    val type: Type

    enum class Type {
        @Json(name = "User") USER, @Json(name = "Organization") ORGANISATION
    }

}