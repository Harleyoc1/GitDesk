package com.harleyoconnor.gitdesk.data.remote.timeline

import java.util.*

interface CommentedEvent : Event {

    val body: String

    val updatedAt: Date

}