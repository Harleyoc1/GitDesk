package com.harleyoconnor.gitdesk.data.serialisation.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.net.URL

/**
 * @author Harley O'Connor
 */
object URLAdapter {

    @ToJson
    fun toJson(url: URL): String {
        return url.toExternalForm()
    }

    @FromJson
    fun fromJson(url: String): URL {
        return URL(url)
    }

}