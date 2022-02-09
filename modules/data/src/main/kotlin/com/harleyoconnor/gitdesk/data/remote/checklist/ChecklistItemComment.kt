package com.harleyoconnor.gitdesk.data.remote.checklist

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.squareup.moshi.Json
import java.util.Date

/**
 * @author Harley O'Connor
 */
class ChecklistItemComment(
    val id: Int,
    val username: String,
    @Json(name = "github_username") val gitHubUsername: String?,
    var body: String,
    val created: Date,
    @Json(name = "updated") private var _updated: Date?,
) {

    val updated: Date?
        get() = _updated

    fun patch(remote: RemoteRepository, checklist: Checklist, parent: ChecklistItem) =
        patchChecklistItemComment(remote, checklist, parent, this)
            .thenApply {
                this._updated = Date()
            }

    fun delete(remote: RemoteRepository, checklist: Checklist, parent: ChecklistItem) =
        deleteChecklistItemComment(remote, checklist, parent, this)

    fun createPatchMap(): Map<String, String> = mapOf(
        "comment_id" to id.toString(),
        "body" to body
    )

}