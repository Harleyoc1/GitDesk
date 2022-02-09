package com.harleyoconnor.gitdesk.ui.repository

import com.harleyoconnor.gitdesk.ui.menubar.ViewMenu
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.util.process.logFailure
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Menu

/**
 * @author Harley O'Connor
 */
class RepositoryMenu : Menu() {

    private lateinit var window: RepositoryWindow

    init {
        val loader = FXMLLoader(
            ViewMenu::class.java.getResource("/ui/nodes/RepositoryMenu.fxml"),
            TRANSLATIONS_BUNDLE
        )
        loader.setRoot(this)
        loader.setController(this)
        loader.load<Menu>()
    }

    fun setWindow(window: RepositoryWindow) {
        this.window = window
    }

    @FXML
    private fun fetch(event: ActionEvent) {
        window.repository.gitRepository.fetch()
            .ifFailure(::logFailure)
            .begin()
    }

    @FXML
    private fun push(event: ActionEvent) {
        // not implemented
    }

    @FXML
    private fun commit(event: ActionEvent) {
        window.promptCommit()
    }

    @FXML
    private fun pull(event: ActionEvent) {
        window.repository.gitRepository.pull()
            .ifSuccessful {
                // Pull will change files, so we refresh the view.
                window.refreshView()
            }
            .ifFailure(::logFailure)
            .begin()
    }

    @FXML
    private fun openBranchesWindow(event: ActionEvent) {
        window.openBranchesWindow()
    }

    @FXML
    private fun openNewBranchWindow(event: ActionEvent) {
        window.openNewBranchWindow()
    }

}