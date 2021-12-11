package com.harleyoconnor.gitdesk.data.serialisation.util

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import okio.buffer
import okio.source
import java.io.File

@Throws(JsonDataException::class)
fun JsonReader.findName(name: String) {
    while (this.hasNext()) {
        if (this.peek() == JsonReader.Token.NAME && this.nextName() == name) {
            return
        }
    }
    throw JsonDataException("No such name \"$name\".")
}

inline fun JsonReader.findNameOrElse(name: String, action: () -> Unit) {
    while (this.hasNext()) {
        if (this.peek() == JsonReader.Token.NAME && this.nextName() == name) {
            return
        }
    }
    action()
}

@Throws(JsonDataException::class)
fun JsonReader.findNextName() {
    while (this.hasNext()) {
        if (this.peek() == JsonReader.Token.NAME) {
            return
        }
    }
    throw JsonDataException("No name.")
}

fun <T> File.tryReadJson(adapter: JsonAdapter<T>): T? {
    return try {
        adapter.fromJson(this.source().buffer())
    } catch (t: Exception) {
        null
    }
}