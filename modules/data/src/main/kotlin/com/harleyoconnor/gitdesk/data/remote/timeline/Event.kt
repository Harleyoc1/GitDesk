package com.harleyoconnor.gitdesk.data.remote.timeline

import com.harleyoconnor.gitdesk.data.remote.User
import java.util.Date

/**
 * An event that has occurred along the timeline of an issue or PR.
 */
interface Event {

    val type: EventType

    /**
     * The user who caused the event to occur.
     */
    val actor: User

    val createdAt: Date

    class Raw(
        override val type: EventType,
        override val actor: User,
        override val createdAt: Date
    ) : Event

}