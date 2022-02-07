package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.form.validation.FieldValidator
import com.harleyoconnor.gitdesk.ui.node.DirectoryField
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
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
class SelectLocationController : ViewController<SelectLocationController.Context> {

    object Loader : ResourceViewLoader<Context, SelectLocationController, VBox>(
        UIResource("/ui/layouts/menu/clone/SelectLocation.fxml")
    )

    class Context(val parent: CloneTab, val remote: Remote) : ViewController.Context

    private lateinit var parent: CloneTab

    private lateinit var remote: Remote

    @FXML
    private lateinit var root: VBox

    @FXML
    private lateinit var locationField: DirectoryField

    @FXML
    private lateinit var cloneButton: Button

    override fun setup(context: Context) {
        this.parent = context.parent
        this.remote = context.remote
        this.locationField.setStage(parent.window.stage)
        displayRemoteCell(remote)
        if (remote is RemoteRepository) {
            locationField.setText(getUserHome() + File.separator + (remote as RemoteRepository).name.repositoryName)
        }
    }

    private fun displayRemoteCell(remote: Remote) {
        this.root.children.add(
            1,
            SelectedRemoteCellController.Loader.load(SelectedRemoteCellController.Context(this, remote)).root
        )
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
            CloneWindow(remote, Directory(destination)).open()
            this.parent.window.close()
        } catch (ignored: FieldValidator.InvalidException) {
        }
    }

    fun returnToRemoteSelection() {
        parent.returnToRemoteSelection()
    }

}