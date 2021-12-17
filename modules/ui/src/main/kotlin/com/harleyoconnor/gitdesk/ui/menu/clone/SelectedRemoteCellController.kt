package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.util.getIcon
import com.harleyoconnor.gitdesk.ui.util.load
import javafx.fxml.FXML
import javafx.scene.layout.HBox

/**
 * @author Harley O'Connor
 */
class SelectedRemoteCellController: RemoteCellController() {

    companion object {
        fun loadCell(parent: SelectLocationController, remote: Remote): HBox {
            val fxml = load<HBox, SelectedRemoteCellController>("menu/tabs/clone/SelectedRemoteCell")
            fxml.controller.initializeWithRemote(remote)
            fxml.controller.parent = parent
            return fxml.root
        }
    }

    private lateinit var parent: SelectLocationController

    @FXML
    private lateinit var platformIcon: SVGIcon

    override fun initializeWithRemote(remote: Remote) {
        super.initializeWithRemote(remote)
        platformIcon.setupFromSvg(remote.getIcon())
    }

    @FXML
    private fun editRemote() {
        parent.returnToRemoteSelection()
    }

}