package com.harleyoconnor.gitdesk.data.remote.github.timeline

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.remote.timeline.Event
import com.harleyoconnor.gitdesk.data.remote.timeline.EventType
import com.harleyoconnor.gitdesk.data.remote.timeline.Timeline
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

object GitHubTimelineAdapter : JsonAdapter<Timeline>() {
    private val customEventTypes = mapOf(
        EventType.COMMENTED to GitHubCommentedEvent::class.java,
        EventType.LABELED to GitHubLabeledEvent::class.java,
        EventType.UNLABELED to GitHubLabeledEvent::class.java,
        EventType.ASSIGNED to GitHubAssignedEvent::class.java,
        EventType.UNASSIGNED to GitHubAssignedEvent::class.java,
        EventType.COMMITTED to GitHubCommittedEvent::class.java,
        EventType.REVIEWED to GitHubReviewedEvent::class.java,
        EventType.MERGED to GitHubMergedEvent::class.java
    )

    override fun fromJson(reader: JsonReader): Timeline {
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

    private fun getEventType(eventId: String): Class<out Event> =
        customEventTypes[EventType.fromGitHubId(eventId)] ?: GitHubEvent::class.java

    override fun toJson(writer: JsonWriter, value: Timeline?) {
        writer.nullValue() // Serialisation is never needed for this.
    }
}
