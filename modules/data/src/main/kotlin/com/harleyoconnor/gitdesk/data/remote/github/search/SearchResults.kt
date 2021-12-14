package com.harleyoconnor.gitdesk.data.remote.github.search

import com.squareup.moshi.Json

/**
 *
 * @author Harley O'Connor
 */
class SearchResults<E>(
    @Json(name = "total_count") val count: Int,
    @Json(name = "incomplete_results") val incompleteResults: Boolean,
    val items: Array<E>
)