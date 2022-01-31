package com.harleyoconnor.gitdesk.data.remote

import java.util.Date

/**
 * @author Harley O'Connor
 */
class IssueComment(
    val body: String?,
    val commenter: User,
    val createdAt: Date,
    val updatedAt: Date
)