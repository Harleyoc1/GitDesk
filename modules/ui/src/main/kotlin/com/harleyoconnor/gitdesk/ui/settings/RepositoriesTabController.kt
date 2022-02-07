package com.harleyoconnor.gitdesk.ui.settings

import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.util.setOnAction
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class RepositoriesTabController : ViewController<RepositoriesTabController.Context> {

    object Loader: ResourceViewLoader<Context, RepositoriesTabController, VBox>(
        UIResource("/ui/layouts/settings/RepositoriesTab.fxml")
    )

    class Context(val settings: AppSettings, val closeCallback: () -> Unit): ViewController.Context

    private lateinit var settings: AppSettings
    private lateinit var closeCallback: () -> Unit

    @FXML
    private lateinit var showHiddenFilesByDefaultCheckbox: CheckBox

    @FXML
    private lateinit var saveButton: Button

    override fun setup(context: Context) {
        settings = context.settings
        closeCallback = context.closeCallback

        showHiddenFilesByDefaultCheckbox.isSelected = settings.repositories.showHiddenFilesByDefault
        showHiddenFilesByDefaultCheckbox.setOnAction { selected: Boolean ->
            settings.repositories.showHiddenFilesByDefault = selected
        }
    }

    @FXML
    private fun cancel(event: ActionEvent) {
        closeCallback()
    }

    @FXML
    private fun save(event: ActionEvent) {
        AppSettings.get().save(settings)
        closeCallback()
    }

    @FXML
    private fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            saveButton.fire()
        }
    }

}