package com.harleyoconnor.gitdesk.ui.menu.open

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.data.local.LocalRepository
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class OpenTabController {

    @FXML
    private lateinit var content: VBox

    @FXML
    private lateinit var searchBar: TextField
    private var lastSearchQuery = ""

    fun initialize() {
        displayRepositories("")
    }

    fun onAddPressed(event: ActionEvent) {

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
        this.content.children.clear()
    }

    private fun displayRepositories(searchQuery: String) {
        Data.repositoryAccess.forEach {
            if (matchesQuery(it.id, searchQuery)) {
                displayRepository(it)
            }
        }
    }

    private fun displayRepository(repository: LocalRepository) {
        val children = content.children
        children.lastOrNull()?.styleClass?.remove("bottom")
        children.add(RepositoryCellController.loadCell(repository))
        children.firstOrNull()?.styleClass?.add("top")
        children.lastOrNull()?.styleClass?.add("bottom")
    }

    private fun matchesQuery(name: String, query: String) = name.lowercase().contains(query.lowercase())

}