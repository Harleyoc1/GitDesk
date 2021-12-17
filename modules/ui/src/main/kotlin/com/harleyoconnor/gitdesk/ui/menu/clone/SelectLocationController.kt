package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.getUserHome
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import javafx.stage.DirectoryChooser
import java.io.File

/**
 * @author Harley O'Connor
 */
class SelectLocationController {

    companion object {
        fun load(remote: Remote, parent: CloneTab): VBox {
            val fxml = load<VBox, SelectLocationController>("menu/tabs/clone/SelectLocation")
            fxml.controller.setRemote(remote)
            fxml.controller.parent = parent
            return fxml.root
        }
    }

    private lateinit var parent: CloneTab

    @FXML
    private lateinit var root: VBox

    // TODO: Warn about existing directories.
    @FXML
    private lateinit var locationField: TextField

    @FXML
    private lateinit var selectedRemote: Remote

    fun setRemote(remote: Remote) {
        this.selectedRemote = remote
        displayRemoteCell(remote)
        if (remote is RemoteRepository) {
            locationField.text = getUserHome() + File.separator + remote.name.repositoryName
        }
    }

    private fun displayRemoteCell(remote: Remote) {
        this.root.children.add(1, SelectedRemoteCellController.loadCell(this, remote))
    }

    fun openDirectoryChooser(actionEvent: ActionEvent) {
        val directoryChooser = DirectoryChooser()
        directoryChooser.showDialog(parent.stage)?.let {
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
        parent.returnToRemoteSelectionView()
    }

}