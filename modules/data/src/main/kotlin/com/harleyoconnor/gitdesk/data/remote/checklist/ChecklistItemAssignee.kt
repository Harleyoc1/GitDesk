package com.harleyoconnor.gitdesk.data.remote.checklist

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.squareup.moshi.Json

/**
 * @author Harley O'Connor
 */
class ChecklistItemAssignee(
    val username: String,
    @Json(name = "github_username") val gitHubUsername: String?
) {

    fun delete(remote: RemoteRepository, checklist: Checklist, parent: ChecklistItem) =
        deleteChecklistItemAssignee(remote, checklist, parent, this.username)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChecklistItemAssignee

        if (username != other.username) return false

        return true
    }

    override fun hashCode(): Int {
        return username.hashCode()
    }


}