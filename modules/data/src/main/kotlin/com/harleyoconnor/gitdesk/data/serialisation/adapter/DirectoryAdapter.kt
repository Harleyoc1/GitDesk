package com.harleyoconnor.gitdesk.data.serialisation.adapter

import com.harleyoconnor.gitdesk.util.Directory
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.io.File

/**
 * @author Harley O'Connor
 */
object DirectoryAdapter {

    @ToJson
    fun toJson(directory: Directory): String {
        return directory.absolutePath
    }

    @FromJson
    fun fromJson(directory: String): Directory {
        return Directory(File(directory))
    }

}