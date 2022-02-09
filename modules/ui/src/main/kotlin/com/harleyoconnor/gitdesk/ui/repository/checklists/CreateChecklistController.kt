package com.harleyoconnor.gitdesk.ui.repository.checklists

import com.harleyoconnor.gitdesk.data.remote.checklist.Checklist
import com.harleyoconnor.gitdesk.data.remote.checklist.postChecklist
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.form.validation.ChecklistNameAvailableValidator
import com.harleyoconnor.gitdesk.ui.form.validation.FieldValidator
import com.harleyoconnor.gitdesk.ui.node.TextArea
import com.harleyoconnor.gitdesk.ui.node.TextField
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.util.exceptionallyOnMainThread
import com.harleyoconnor.gitdesk.ui.util.logErrorAndCreateDialogue
import com.harleyoconnor.gitdesk.ui.util.thenAcceptOnMainThread
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class CreateChecklistController : ViewController<CreateChecklistController.Context> {

    object Loader : ResourceViewLoader<Context, CreateChecklistController, VBox>(
        UIResource("/ui/layouts/repository/checklists/CreateChecklist.fxml")
    )

    class Context(
        val remoteContext: RemoteContext,
        val createdCallback: (Checklist) -> Unit,
        val canceledCallback: () -> Unit
    ) : ViewController.Context

    private lateinit var remoteContext: RemoteContext
    private lateinit var createdCallback: (Checklist) -> Unit
    private lateinit var canceledCallback: () -> Unit

    @FXML
    private lateinit var nameField: TextField

    @FXML
    private lateinit var descriptionField: TextArea

    @FXML
    private lateinit var createButton: Button

    override fun setup(context: Context) {
        remoteContext = context.remoteContext
        createdCallback = context.createdCallback
        canceledCallback = context.canceledCallback
        nameField.setOrAppendValidator(
            ChecklistNameAvailableValidator(remoteContext.remote)
        )
    }

    @FXML
    private fun cancel(event: ActionEvent) {
        canceledCallback()
    }

    @FXML
    private fun create(event: ActionEvent) {
        try {
            addChecklist(
                nameField.getText(), descriptionField.getText().ifEmpty { null }
            )
        } catch (ignored: FieldValidator.InvalidException) {
        }
    }

    private fun addChecklist(name: String, description: String?) {
        postChecklist(remoteContext.remote, name, description)
            .thenAcceptOnMainThread {
                createdCallback(it)
            }
            .exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.adding_checklist", it).show()
            }
    }

    @FXML
    private fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            createButton.fire()
        }
    }
}