package com.harleyoconnor.gitdesk.data.serialisation.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.regex.Pattern

/**
 * @author Harley O'Connor
 */
object PatternAdapter {

    @ToJson
    fun toJson(pattern: Pattern): String {
        return pattern.toString()
    }

    @FromJson
    fun fromJson(regex: String): Pattern {
        return Pattern.compile(regex)
    }

}