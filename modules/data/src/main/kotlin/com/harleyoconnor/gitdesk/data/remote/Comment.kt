package com.harleyoconnor.gitdesk.data.remote

import java.util.Date

/**
 * Represents a comment made on an issue or pull request.
 *
 * @author Harley O'Connor
 */
interface Comment {

    /**
     * The comment's ID, or `null` if this 'comment' is the issue body.
     */
    val id: Int?

    val body: String?

    val commenter: User

    val createdAt: Date

    val updatedAt: Date

    fun isIssueBody(): Boolean = this.id == null

    class Raw(
        override val id: Int?,
        override val body: String?,
        override val commenter: User,
        override val createdAt: Date,
        override val updatedAt: Date
    ) : Comment
}