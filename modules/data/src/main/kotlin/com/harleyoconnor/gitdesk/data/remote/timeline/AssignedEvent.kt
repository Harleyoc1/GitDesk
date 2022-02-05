package com.harleyoconnor.gitdesk.data.remote.timeline

import com.harleyoconnor.gitdesk.data.remote.User
import java.util.*

/**
 *
 * @author Harley O'Connor
 */
interface AssignedEvent : Event {

    val assignee: User

    class Raw(
        override val type: EventType,
        override val actor: User,
        override val createdAt: Date,
        override val assignee: User
    ) : AssignedEvent

}