package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.util.Directory
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import javafx.stage.DirectoryChooser

/**
 * @author Harley O'Connor
 */
class CloneController {

    companion object {
        fun load(remote: Remote, sibling: SelectRemoteTabController): VBox {
            val fxml = load<VBox, CloneController>("menu/tabs/clone/Clone")
            fxml.controller.setRemote(remote)
            fxml.controller.sibling = sibling
            return fxml.root
        }
    }

    private lateinit var sibling: SelectRemoteTabController

    @FXML
    private lateinit var root: VBox

    @FXML
    private lateinit var locationField: TextField

    @FXML
    private lateinit var selectedRemote: Remote

    fun setRemote(remote: Remote) {
        this.selectedRemote = remote
        this.root.children.add(1, SelectedRemoteCellController.loadCell(this, remote))
    }

    fun openDirectoryChooser(actionEvent: ActionEvent) {
        val directoryChooser = DirectoryChooser()
        directoryChooser.showDialog(sibling.stage)?.let {
            this.locationField.text = Directory(it).path
        }
    }

    fun cancel(event: ActionEvent) {
        editRemoteSelection()
    }

    fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            clone(event)
        }
    }

    fun clone(event: Event) {

    }

    fun editRemoteSelection() {
        sibling.editSelection()
    }

}