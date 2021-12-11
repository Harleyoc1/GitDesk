package com.harleyoconnor.gitdesk.data.serialisation.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.Date

/**
 * A Moshi adapter for encoding and decoding dates formatted using the
 * [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601) standard.
 *
 * @author Harley O'Connor
 */
object ISO8601DateAdapter {

    private val FORMAT by lazy { SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX") }

    @ToJson
    fun toJson(date: Date): String {
        return FORMAT.format(date)
    }

    @FromJson
    fun fromJson(date: String): Date {
        return FORMAT.parse(date)
    }

}