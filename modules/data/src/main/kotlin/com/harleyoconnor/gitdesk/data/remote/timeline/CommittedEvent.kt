package com.harleyoconnor.gitdesk.data.remote.timeline

import com.harleyoconnor.gitdesk.data.remote.User
import java.net.URL
import java.util.Date

/**
 *
 * @author Harley O'Connor
 */
interface CommittedEvent : Event {

    val hash: String

    val url: URL

    val author: GitUser

    val committer: GitUser

    val message: String

    override val actor: User
        get() = throw IllegalStateException("Committed event does not contain an actor.")

    override val createdAt: Date
        get() = throw IllegalStateException("Committed event does not contain created at date.")

    class GitUser(
        val name: String,
        val email: String,
        val date: Date
    )

}