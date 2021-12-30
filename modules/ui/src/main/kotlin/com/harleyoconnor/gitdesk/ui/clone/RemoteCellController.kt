package com.harleyoconnor.gitdesk.ui.clone

import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.menu.clone.RemoteCellController
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.util.getIcon
import com.harleyoconnor.gitdesk.ui.util.load
import javafx.fxml.FXML
import javafx.scene.layout.HBox

/**
 * @author Harley O'Connor
 */
class RemoteCellController: RemoteCellController() {

    companion object {
        fun loadCell(remote: Remote): HBox {
            val fxml = load<HBox, com.harleyoconnor.gitdesk.ui.clone.RemoteCellController>("clone/RemoteCell")
            fxml.controller.initializeWithRemote(remote)
            return fxml.root
        }
    }

    @FXML
    private lateinit var platformIcon: SVGIcon

    override fun initializeWithRemote(remote: Remote) {
        super.initializeWithRemote(remote)
        platformIcon.setupFromSvg(remote.getIcon())
    }

}