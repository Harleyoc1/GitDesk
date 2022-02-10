package com.harleyoconnor.gitdesk.ui.menu.create

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.git.initRepository
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.menu.MenuController
import com.harleyoconnor.gitdesk.ui.node.DirectoryField
import com.harleyoconnor.gitdesk.ui.node.TextField
import com.harleyoconnor.gitdesk.ui.settings.AppSettings
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
class CreateTabController : ViewController<CreateTabController.Context> {

    object Loader : ResourceViewLoader<Context, CreateTabController, VBox>(
        UIResource("/ui/layouts/menu/create/CreateTab.fxml")
    )

    class Context(val parent: MenuController) : ViewController.Context

    private lateinit var parent: MenuController

    @FXML
    private lateinit var nameField: TextField

    @FXML
    private lateinit var locationField: DirectoryField

    @FXML
    private lateinit var createButton: Button

    @FXML
    private fun initialize() {
        clear()
    }

    override fun setup(context: Context) {
        parent = context.parent

        locationField.setStage(parent.stage)
    }

    @FXML
    private fun clear() {
        nameField.setText("Project")
        locationField.setText(getUserHome() + File.separator + "Project")
    }

    @FXML
    private fun create() {
        val name = nameField.getTextOrNull() ?: return
        val location = File(locationField.getTextOrNull() ?: return)
        location.mkdirs()
        val directory = Directory(location)
        initRepository(directory)
        parent.openRepository(
            LocalRepository(
                name,
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