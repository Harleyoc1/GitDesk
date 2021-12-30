package com.harleyoconnor.gitdesk.ui.clone

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.git.clone
import com.harleyoconnor.gitdesk.ui.menu.clone.CloneWindow
import com.harleyoconnor.gitdesk.ui.repository.RepositoryWindow
import com.harleyoconnor.gitdesk.ui.util.load
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.layout.VBox

/**
 *
 * @author Harley O'Connor
 */
class CloneController {

    companion object {
        fun setup(parent: CloneWindow): VBox {
            val fxml = load<VBox, CloneController>("clone/Root")
            fxml.controller.setup(parent)
            return fxml.root
        }
    }

    private lateinit var parent: CloneWindow

    @FXML
    private lateinit var root: VBox

    private fun setup(parent: CloneWindow) {
        this.parent = parent
        this.root.children.add(1, RemoteCellController.loadCell(parent.remote))
        clone(parent.remote.url, parent.destination)
            .ifSuccessful {
                queueOpenRepository()
            }
            .ifFailure {
                // TODO: Display error
            }
            .begin()
    }

    private fun queueOpenRepository() {
        Platform.runLater {
            RepositoryWindow.focusOrOpen(
                LocalRepository(parent.destination.name, parent.destination)
            )
            parent.close()
        }
    }

}