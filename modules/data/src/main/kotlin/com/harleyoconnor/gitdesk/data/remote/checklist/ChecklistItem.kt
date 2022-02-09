package com.harleyoconnor.gitdesk.data.remote.checklist

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.serialisation.adapter.ISO8601_FORMAT
import com.squareup.moshi.Json
import java.util.Date

/**
 * @author Harley O'Connor
 */
class ChecklistItem(
    val id: Int,
    var title: String,
    var body: String,
    var completed: Boolean,
    val created: Date,
    @Json(name = "updated") private var _updated: Date?,
    var deadline: Date?,
    @Json(name = "assignees") private val _assignees: MutableList<ChecklistItemAssignee>
) {

    val updated: Date?
        get() = _updated
    val assignees: Array<ChecklistItemAssignee>
        get() = _assignees.toTypedArray()

    fun addComment(remote: RemoteRepository, parent: Checklist, body: String) =
        postChecklistItemComment(remote, parent, this, body)

    fun addAssignee(remote: RemoteRepository, parent: Checklist, username: String) =
        postChecklistItemAssignee(remote, parent, this, username)
            .thenApply {
                _assignees.add(it)
            }

    fun deleteAssignee(remote: RemoteRepository, parent: Checklist, username: String) =
        deleteChecklistItemAssignee(remote, parent, this, username)
            .thenApply {
                _assignees.removeIf { it.username == username }
            }

    fun getComments(remote: RemoteRepository, parent: Checklist) =
        getChecklistItemComments(remote, parent, this)

    fun patch(remote: RemoteRepository, parent: Checklist) = patchChecklistItem(remote, parent, this)
        .thenApply {
            this._updated = Date(); it
        }

    fun delete(remote: RemoteRepository, parent: Checklist) = deleteChecklistItem(remote, parent, this)

    fun createPatchMap(): Map<String, String> = mutableMapOf(
        "item_id" to id.toString(),
        "title" to title,
        "body" to body,
        "completed" to completed.toString()
    ).also {
        deadline?.let { deadline ->
            it["deadline"] = ISO8601_FORMAT.format(deadline)
        }
    }

}