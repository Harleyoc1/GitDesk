package com.harleyoconnor.gitdesk.data.remote

import java.util.*

/**
 *
 * @author Harley O'Connor
 */
interface Issue {

    val number: Int

    val title: String

    val author: User

    val labels: Array<Label>

    val state: State

    val assignees: List<User>

    val createdAt: Date

    val updatedAt: Date

    val closedAt: Date?

    val body: String

    enum class State {
        OPEN, CLOSED, ALL
    }

}