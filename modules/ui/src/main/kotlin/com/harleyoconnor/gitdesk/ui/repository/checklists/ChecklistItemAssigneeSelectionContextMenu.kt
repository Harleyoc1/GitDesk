package com.harleyoconnor.gitdesk.ui.repository.checklists

import com.harleyoconnor.gitdesk.data.remote.checklist.AssigneeData
import com.harleyoconnor.gitdesk.data.remote.checklist.ChecklistItem
import com.harleyoconnor.gitdesk.data.remote.checklist.getAssignees
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.util.createAvatarNode
import com.harleyoconnor.gitdesk.ui.util.exceptionallyOnMainThread
import com.harleyoconnor.gitdesk.ui.util.logErrorAndCreateDialogue
import com.harleyoconnor.gitdesk.ui.util.thenAcceptOnMainThread
import com.harleyoconnor.gitdesk.util.stream
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import java.util.concurrent.CompletableFuture

/**
 * @author Harley O'Connor
 */
class ChecklistItemAssigneeSelectionContextMenu(
    private val checklistContext: ChecklistContext,
    private val item: ChecklistItem,
    private val callback: (String) -> Unit
) : ContextMenu() {

    init {
        loadAssignees()
    }

    private fun loadAssignees(): CompletableFuture<Void?> {
        addLoadingItem()
        return getAssignees(checklistContext.remoteContext.remote)
            .thenAcceptOnMainThread { assignees ->
                removeLoadingItem()
                displayAssignees(assignees)
            }
            .exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.retrieving_assignees", it).show()
            }
    }

    private fun displayAssignees(assignees: List<AssigneeData>) {
        assignees.stream()
            .filter { user ->
                item.assignees.stream().noneMatch { user.username == it.username }
            }
            .map {
                createUserMenuItem(it)
            }
            .forEach(this.items::add)
    }

    private fun addLoadingItem() {
        this.items.add(MenuItem(TRANSLATIONS_BUNDLE.getString("ui.assignees.loading")))
    }

    private fun removeLoadingItem() {
        this.items.removeLast()
    }

    private fun createUserMenuItem(assigneeData: AssigneeData): MenuItem {
        val username = assigneeData.username
        return MenuItem(username, createAvatarNode(getAvatarUrl(username))).also {
            it.setOnAction {
                callback(username)
            }
        }
    }

}