package com.harleyoconnor.gitdesk.data.serialisation.adapter

import com.harleyoconnor.gitdesk.data.remote.timeline.EventType
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

/**
 * @author Harley O'Connor
 */
object GitHubEventTypeAdapter : JsonAdapter<EventType>() {

    override fun fromJson(reader: JsonReader): EventType? {
        return if (reader.peek() == JsonReader.Token.NULL) {
            null
        } else if (reader.peek() == JsonReader.Token.STRING) {
            EventType.fromGitHubId(reader.nextString())
        } else {
            throw JsonDataException("Unsupported Json token for event type: ${reader.peek()}")
        }
    }

    override fun toJson(writer: JsonWriter, value: EventType?) {
        if (value == null) {
            writer.nullValue()
        } else {
            writer.value(value.gitHubId)
        }
    }
}