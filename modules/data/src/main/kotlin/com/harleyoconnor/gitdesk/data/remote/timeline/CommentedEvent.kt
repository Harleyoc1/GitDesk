package com.harleyoconnor.gitdesk.data.remote.timeline

import java.util.*

interface CommentedEvent : Event {

    val commentId: Int

    val body: String

    val updatedAt: Date

}