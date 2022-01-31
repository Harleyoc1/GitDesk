package com.harleyoconnor.gitdesk.data.remote.github.timeline

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.remote.timeline.Event
import com.harleyoconnor.gitdesk.data.remote.timeline.Timeline
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

object GitHubTimelineAdapter : JsonAdapter<Timeline>() {
    private val CUSTOM_EVENT_TYPES = mapOf(
        "commented" to GitHubCommentedEvent::class.java,
        "labeled" to GitHubLabeledEvent::class.java
    )

    override fun fromJson(reader: JsonReader): Timeline? {
        val events = mutableListOf<Event>()
        reader.beginArray()
        while (reader.hasNext()) {
            val json = reader.readJsonValue() as Map<String, Any?>
            val eventId = json["event"] as String
            MOSHI.adapter(getEventType(eventId)).fromJsonValue(json)?.let {
                events.add(it)
            }
        }
        reader.endArray()
        return Timeline(events.toTypedArray())
    }

    private fun getEventType(eventId: String): Class<out Event> = CUSTOM_EVENT_TYPES[eventId] ?: GitHubEvent::class.java

    override fun toJson(writer: JsonWriter, value: Timeline?) {
        writer.nullValue() // Serialisation is never needed for this.
    }
}
