package com.harleyoconnor.gitdesk.data.serialisation.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.DateFormat
import java.util.Date

/**
 * A Moshi adapter for encoding and decoding [date][Date] objects.
 *
 * @author Harley O'Connor
 */
class DateAdapter(
    private val format: DateFormat
) {

    @ToJson
    fun toJson(date: Date): String {
        return format.format(date)
    }

    @FromJson
    fun fromJson(date: String): Date {
        return format.parse(date)
    }

}