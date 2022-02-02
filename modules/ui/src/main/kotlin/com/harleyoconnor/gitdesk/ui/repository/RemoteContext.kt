package com.harleyoconnor.gitdesk.ui.repository

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.data.remote.PlatformAccount
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.User

/**
 * @author Harley O'Connor
 */
class RemoteContext(
    val repository: LocalRepository,
    val remote: RemoteRepository,
    val loggedInUser: User?
) {

    val loggedInUserIsCollaborator: Boolean by lazy {
        loggedInUser?.let { remote.isCollaborator(it.username) } ?: false
    }

}