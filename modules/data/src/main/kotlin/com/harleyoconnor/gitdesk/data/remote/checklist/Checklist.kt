package com.harleyoconnor.gitdesk.data.remote.checklist

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository

/**
 * @author Harley O'Connor
 */
class Checklist(
    val name: String,
    val description: String?
) {

    fun addItem(remote: RemoteRepository, title: String, body: String) =
        postChecklistItem(remote, this, title, body)

    fun getItems(remote: RemoteRepository) = getChecklistItems(remote, this)

    fun delete(remote: RemoteRepository) = deleteChecklist(remote, this)

}