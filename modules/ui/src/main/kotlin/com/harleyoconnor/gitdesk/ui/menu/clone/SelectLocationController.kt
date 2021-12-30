package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.node.DirectoryField
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.ui.validation.FieldValidator
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.getUserHome
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import java.io.File

/**
 * @author Harley O'Connor
 */
class SelectLocationController {

    companion object {
        fun load(remote: Remote, parent: CloneTab): VBox {
            val fxml = load<VBox, SelectLocationController>("menu/tabs/clone/SelectLocation")
            fxml.controller.setup(parent, remote)
            return fxml.root
        }
    }

    private lateinit var parent: CloneTab

    @FXML
    private lateinit var root: VBox

    @FXML
    private lateinit var locationField: DirectoryField

    @FXML
    private lateinit var cloneButton: Button

    private lateinit var selectedRemote: Remote

    fun setup(parent: CloneTab, remote: Remote) {
        this.parent = parent
        this.selectedRemote = remote
        this.locationField.setStage(parent.window.stage)
        displayRemoteCell(remote)
        if (remote is RemoteRepository) {
            locationField.setText(getUserHome() + File.separator + remote.name.repositoryName)
        }
    }

    private fun displayRemoteCell(remote: Remote) {
        this.root.children.add(1, SelectedRemoteCellController.loadCell(this, remote))
    }

    @FXML
    private fun cancel() {
        returnToRemoteSelection()
    }

    @FXML
    private fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            cloneButton.fire()
        }
    }

    fun clone() {
        try {
            val destination = File(locationField.getText())
            destination.mkdirs()
            CloneWindow(selectedRemote, Directory(destination)).open()
            this.parent.window.close()
        } catch (ignored: FieldValidator.InvalidException) { }
    }

    fun returnToRemoteSelection() {
        parent.returnToRemoteSelection()
    }

}