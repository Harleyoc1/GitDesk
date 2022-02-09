package com.harleyoconnor.gitdesk.ui.repository.checklists

import com.harleyoconnor.gitdesk.data.remote.checklist.Checklist
import com.harleyoconnor.gitdesk.data.remote.checklist.ChecklistItem
import com.harleyoconnor.gitdesk.data.remote.github.GitHubNetworking
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import java.net.URL

const val NO_PROFILE_ICON = "/ui/images/profile.png"

/**
 * A map of GitHub usernames to their user's avatar URL. These are cached to save sending excess requests to the GitHub
 * API.
 */
private val avatarCache: MutableMap<String, URL> = mutableMapOf()

@Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")
fun getAvatarUrl(username: String): URL {
    return avatarCache.computeIfAbsent(username) {
        val user = GitHubNetworking.getUser(it)
            ?: throw RuntimeException("Tried to get avatar for user on unsupported platform.")
        user.avatarUrl
    }
}

class ChecklistContext(
    val remoteContext: RemoteContext,
    val checklist: Checklist
)

class ChecklistItemContext(
    val checklistContext: ChecklistContext,
    val checklistItem: ChecklistItem
)

