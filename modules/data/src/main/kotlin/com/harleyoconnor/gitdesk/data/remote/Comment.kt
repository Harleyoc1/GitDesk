package com.harleyoconnor.gitdesk.data.remote

import java.util.Date

/**
 * Represents a comment made on an issue or pull request.
 *
 * @author Harley O'Connor
 */
interface Comment {

    val body: String?

    val commenter: User

    val createdAt: Date

    val updatedAt: Date

    class Raw(
        override val body: String?,
        override val commenter: User,
        override val createdAt: Date,
        override val updatedAt: Date
    ) : Comment
}