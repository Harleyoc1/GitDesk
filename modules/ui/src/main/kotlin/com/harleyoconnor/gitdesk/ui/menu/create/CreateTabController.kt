package com.harleyoconnor.gitdesk.ui.menu.create

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.git.initRepository
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.menu.MenuController
import com.harleyoconnor.gitdesk.ui.settings.AppSettings
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.getUserHome
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import javafx.stage.DirectoryChooser
import java.io.File

/**
 * @author Harley O'Connor
 */
class CreateTabController : ViewController<CreateTabController.Context> {

    object Loader : ResourceViewLoader<Context, CreateTabController, VBox>(
        UIResource("/ui/layouts/menu/create/CreateTab.fxml")
    )

    class Context(val parent: MenuController) : ViewController.Context

    private lateinit var parent: MenuController

    @FXML
    private lateinit var nameField: TextField

    @FXML
    lateinit var locationField: TextField

    @FXML
    private lateinit var createButton: Button

    @FXML
    private fun initialize() {
        clear()
    }

    override fun setup(context: Context) {
        this.parent = context.parent
    }

    @FXML
    private fun openDirectoryChooser() {
        val directoryChooser = DirectoryChooser()
        directoryChooser.showDialog(parent.stage)?.let {
            this.locationField.text = Directory(it).path
        }
    }

    @FXML
    private fun clear() {
        nameField.text = "Project"
        locationField.text = getUserHome() + File.separator + "Project"
    }

    @FXML
    private fun create() {
        val location = File(locationField.text)
        location.mkdirs()
        val directory = Directory(location)
        initRepository(directory)
        parent.openRepository(
            LocalRepository(
                nameField.text,
                directory,
                null,
                AppSettings.get().getOrLoad().repositories.showHiddenFilesByDefault
            )
        )
    }

    @FXML
    private fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            createButton.fire()
        }
    }

}