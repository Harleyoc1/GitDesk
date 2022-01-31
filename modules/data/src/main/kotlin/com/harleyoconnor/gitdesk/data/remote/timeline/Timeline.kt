package com.harleyoconnor.gitdesk.data.remote.timeline

/**
 * The timeline for an issue or PR. Made up of an array of [Event]s.
 */
class Timeline(
    private val events: Array<Event>
) {
    override fun toString(): String {
        return events.contentToString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Timeline

        if (!events.contentEquals(other.events)) return false

        return true
    }

    override fun hashCode(): Int {
        return events.contentHashCode()
    }
}