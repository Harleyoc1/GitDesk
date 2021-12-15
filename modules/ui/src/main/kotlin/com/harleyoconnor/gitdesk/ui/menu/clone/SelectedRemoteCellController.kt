package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.util.load
import javafx.event.Event
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class SelectedRemoteCellController: RemoteCellController() {

    companion object {
        fun loadCell(parent: CloneController, remote: Remote): HBox {
            val fxml = load<HBox, SelectedRemoteCellController>("menu/tabs/clone/SelectedRemoteCell")
            fxml.controller.initializeWithRemote(remote)
            fxml.controller.parent = parent
            return fxml.root
        }
    }

    private lateinit var parent: CloneController

    // TODO: Platform icon

    @FXML
    override fun initialize() {
        VBox.setMargin(root, Insets(0.0, 10.0, 0.0, 10.0))
        super.initialize()
    }

    fun editRemote(event: Event) {
        parent.editRemoteSelection()
    }

}