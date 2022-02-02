package com.harleyoconnor.gitdesk.data.remote.timeline

import com.harleyoconnor.gitdesk.data.remote.Label
import com.harleyoconnor.gitdesk.data.remote.User
import java.util.Date

interface LabeledEvent : Event {

    val label: Label

    class Raw(
        override val type: EventType,
        override val actor: User,
        override val createdAt: Date,
        override val label: Label
    ) : LabeledEvent

}