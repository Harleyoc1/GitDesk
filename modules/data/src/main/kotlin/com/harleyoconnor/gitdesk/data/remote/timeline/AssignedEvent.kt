package com.harleyoconnor.gitdesk.data.remote.timeline

import com.harleyoconnor.gitdesk.data.remote.User

/**
 *
 * @author Harley O'Connor
 */
interface AssignedEvent : Event {

    val assignee: User
}