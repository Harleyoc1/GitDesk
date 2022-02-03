package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.User
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
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
class AssigneeSelectionContextMenu(
    private val remoteContext: RemoteContext,
    private val issue: IssueAccessor,
    private val callback: (User) -> Unit
) : ContextMenu() {

    private var assigneePage: Int = 1

    init {
        loadNextPageOfAssignees()
    }

    private fun loadNextPageOfAssignees(): CompletableFuture<Void?> {
        addLoadingItem()
        return remoteContext.remote.platform.networking!!.getAssignees(
            remoteContext.remote.name, assigneePage++
        )
            .thenAcceptOnMainThread { assignees ->
                removeLoadingItem()
                displayAssignees(assignees)
                if (shouldLoadAnotherPage(assignees)) {
                    loadNextPageOfAssignees()
                }
            }
            .exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.retrieving_assignees", it).show()
            }
    }

    private fun displayAssignees(assignees: Array<User>) {
        assignees.stream()
            .filter { user ->
                issue.get().assignees.stream().noneMatch { user.username == it.username }
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

    /**
     * Checks if another page should be loaded based on how many assignees were loaded by the last request.
     *
     * This is done by checking `100` assignees were loaded. Since a full page would return `100` assignees this means
     * we stop sending requests once we land on a non-full page (as all assignees have been loaded).
     *
     * In reality, there will almost never be this volume of available assignees.
     */
    private fun shouldLoadAnotherPage(assignees: Array<User>) =
        assignees.size >= 100

    private fun createUserMenuItem(user: User): MenuItem {
        return MenuItem(user.username, user.createAvatarNode()).also {
            it.setOnAction {
                callback(user)
            }
        }
    }

}