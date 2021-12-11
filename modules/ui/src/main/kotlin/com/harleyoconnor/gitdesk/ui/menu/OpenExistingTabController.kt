package com.harleyoconnor.gitdesk.ui.menu

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.data.repository.LocalRepository
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class OpenExistingTabController {

    @FXML
    private lateinit var root: VBox

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
        this.root.children.remove(1, this.root.children.size)
    }

    private fun displayRepositories(searchQuery: String) {
        Data.repositoryAccess.forEach {
            if (matchesQuery(it.id, searchQuery)) {
                displayRepository(it)
            }
        }
    }

    private fun displayRepository(repository: LocalRepository) {
        root.children.add(RepositoryCellController.loadCell(repository))
    }

    private fun matchesQuery(name: String, query: String) = name.lowercase().contains(query.lowercase())

}