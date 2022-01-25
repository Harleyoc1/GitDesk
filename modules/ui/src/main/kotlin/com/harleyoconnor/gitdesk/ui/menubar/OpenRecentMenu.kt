package com.harleyoconnor.gitdesk.ui.menubar

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.ui.repository.RepositoryWindow
import com.harleyoconnor.gitdesk.util.Directory
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem

/**
 *
 * @author Harley O'Connor
 */
class OpenRecentMenu: Menu() {

    init {
        Data.repositoryAccess.getAllNotOpen().entries
            .forEach {
                addItemFor(it.value, it.key)
            }
    }

    private fun addItemFor(id: String, directory: Directory) {
        val item = MenuItem(id)
        item.setOnAction {
            openRepository(directory)
        }
        this.items.add(item)
    }

    private fun openRepository(directory: Directory) {
        val repository = Data.repositoryAccess.get(directory)
        RepositoryWindow.focusOrOpen(repository)
    }

}