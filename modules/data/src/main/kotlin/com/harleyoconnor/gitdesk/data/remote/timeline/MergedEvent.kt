package com.harleyoconnor.gitdesk.data.remote.timeline

import com.harleyoconnor.gitdesk.data.remote.User
import java.util.Date

/**
 *
 * @author Harley O'Connor
 */
interface MergedEvent : Event {

    val commitId: String

    class Raw(
        override val type: EventType,
        override val actor: User,
        override val createdAt: Date,
        override val commitId: String
    ) : MergedEvent

}