package com.harleyoconnor.gitdesk.ui.menu.open

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.git.initRepository
import com.harleyoconnor.gitdesk.git.repositoryExistsAt
import com.harleyoconnor.gitdesk.ui.menu.MenuController
import com.harleyoconnor.gitdesk.ui.util.*
import com.harleyoconnor.gitdesk.util.Directory
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.DirectoryChooser

/**
 * @author Harley O'Connor
 */
class OpenTabController {

    companion object {
        fun load(parent: MenuController): VBox {
            val fxml = load<VBox, OpenTabController>("menu/tabs/open/Root")
            fxml.controller.setup(parent)
            return fxml.root
        }
    }

    private lateinit var parent: MenuController

    @FXML
    private lateinit var content: VBox

    @FXML
    private lateinit var searchBar: TextField
    private var lastSearchQuery = ""

    private val cells: MutableMap<LocalRepository, HBox> = mutableMapOf()

    fun setup(parent: MenuController) {
        this.parent = parent
        displayRepositories("")
    }

    fun onAddPressed(event: ActionEvent) {
        val directoryChooser = DirectoryChooser()
        directoryChooser.showDialog(parent.stage)?.let {
            addDirectory(Directory(it))
        }
    }

    private fun addDirectory(directory: Directory) {
        if (!repositoryExistsAt(directory)) {
            initRepository(directory)
        }
        parent.openRepository(LocalRepository(directory.name, directory))
    }

    fun onSearchQueryUpdated(keyEvent: KeyEvent) {
        val query = searchBar.text
        if (lastSearchQuery == query) {
            return
        }
        lastSearchQuery = query
        clearDisplayedRepositories()
        displayRepositories(query)
    }

    private fun clearDisplayedRepositories() {
        this.content.children.forEach {
            it.removeTopClass()
            it.removeBottomClass()
        }
        this.content.children.clear()
    }

    private fun displayRepositories(searchQuery: String) {
        Data.repositoryAccess.getAll().forEach { (directory, id) ->
            if (matchesQuery(id, searchQuery)) {
                displayRepository(Data.repositoryAccess.get(directory))
            }
        }
    }

    @Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")
    private fun displayRepository(repository: LocalRepository) {
        val children = content.children
        children.lastOrNull()?.removeBottomClass()
        children.add(cells.computeIfAbsent(repository) {
            RepositoryCellController.loadCell(this.parent, it)
        })
        children.firstOrNull()?.addTopClass()
        children.lastOrNull()?.addBottomClass()
    }

    private fun matchesQuery(name: String, query: String) = name.lowercase().contains(query.lowercase())

}