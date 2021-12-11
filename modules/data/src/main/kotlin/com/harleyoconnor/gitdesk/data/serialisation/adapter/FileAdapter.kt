package com.harleyoconnor.gitdesk.data.serialisation.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.io.File

/**
 * @author Harley O'Connor
 */
object FileAdapter {

    @ToJson
    fun toJson(file: File): String {
        return file.absolutePath
    }

    @FromJson
    fun fromJson(file: String): File {
        return File(file)
    }

}