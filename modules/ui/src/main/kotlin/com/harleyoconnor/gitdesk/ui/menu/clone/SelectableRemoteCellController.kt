package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.util.load
import javafx.event.Event
import javafx.scene.layout.HBox

/**
 * @author Harley O'Connor
 */
class SelectableRemoteCellController: RemoteCellController() {

    companion object {
        fun loadCell(parent: CloneTab, remote: Remote): HBox {
            val fxml = load<HBox, SelectableRemoteCellController>("menu/tabs/clone/SelectableRemoteCell")
            fxml.controller.initializeWithRemote(remote)
            fxml.controller.parent = parent
            return fxml.root
        }
    }

    private lateinit var parent: CloneTab

    fun selectRemote(event: Event) {
        parent.toLocationSelection(remote)
    }

}