package com.harleyoconnor.gitdesk.data.remote.timeline

import com.harleyoconnor.gitdesk.data.remote.Comment
import com.harleyoconnor.gitdesk.data.remote.User
import java.util.*

/**
 * An event that has occurred along the timeline of an issue or PR.
 */
interface Event {

    /**
     * The identifier of this event.
     */
    val id: String

    /**
     * The user who caused the event to occur.
     */
    val actor: User

    val createdAt: Date

    class Raw(
        override val id: String,
        override val actor: User,
        override val createdAt: Date
    ) : Event

}