package com.harleyoconnor.gitdesk.ui.settings

import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.FileField
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.io.File

/**
 * @author Harley O'Connor
 */
class IntegrationsTabController : ViewController<IntegrationsTabController.Context> {

    object Loader : ResourceViewLoader<Context, IntegrationsTabController, VBox>(
        UIResource("/ui/layouts/settings/IntegrationsTab.fxml")
    )

    class Context(val settings: AppSettings, val stage: Stage, val closeCallback: () -> Unit) : ViewController.Context

    private lateinit var settings: AppSettings
    private lateinit var closeCallback: () -> Unit

    @FXML
    private lateinit var defaultExternalEditorField: FileField

    @FXML
    private lateinit var saveButton: Button

    @FXML
    private fun initialize() {
        defaultExternalEditorField.onTextChanged { _, _ ->
            settings.integrations.defaultExternalEditor = File(
                defaultExternalEditorField.getTextOrNull() ?: return@onTextChanged
            )
        }
    }

    override fun setup(context: Context) {
        settings = context.settings
        closeCallback = context.closeCallback
        settings.integrations.defaultExternalEditor?.let {
            defaultExternalEditorField.setText(it.canonicalPath)
        }
        defaultExternalEditorField.setStage(context.stage)
    }

    @FXML
    private fun cancel(event: ActionEvent) {
        closeCallback()
    }

    @FXML
    private fun save(event: ActionEvent) {
        defaultExternalEditorField.getTextOrNull() ?: return // Don't save and exit if field is invalid.
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