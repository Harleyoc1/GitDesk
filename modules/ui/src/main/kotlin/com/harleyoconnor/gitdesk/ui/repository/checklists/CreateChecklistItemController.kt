package com.harleyoconnor.gitdesk.ui.repository.checklists

import com.harleyoconnor.gitdesk.data.remote.checklist.ChecklistItem
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.form.validation.FieldValidator
import com.harleyoconnor.gitdesk.ui.node.TextArea
import com.harleyoconnor.gitdesk.ui.node.TextField
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
class CreateChecklistItemController : ViewController<CreateChecklistItemController.Context> {

    object Loader : ResourceViewLoader<Context, CreateChecklistItemController, VBox>(
        UIResource("/ui/layouts/repository/checklists/CreateChecklistItem.fxml")
    )

    class Context(
        val checklistContext: ChecklistContext,
        val createdCallback: (ChecklistItem) -> Unit,
        val canceledCallback: () -> Unit
    ) : ViewController.Context

    private lateinit var checklistContext: ChecklistContext
    private lateinit var createdCallback: (ChecklistItem) -> Unit
    private lateinit var canceledCallback: () -> Unit

    @FXML
    private lateinit var titleField: TextField

    @FXML
    private lateinit var bodyField: TextArea

    @FXML
    private lateinit var createButton: Button

    override fun setup(context: Context) {
        checklistContext = context.checklistContext
        createdCallback = context.createdCallback
        canceledCallback = context.canceledCallback
    }

    @FXML
    private fun cancel(event: ActionEvent) {
        canceledCallback()
    }

    @FXML
    private fun create(event: ActionEvent) {
        try {
            addChecklist(
                titleField.getText(), bodyField.getText()
            )
        } catch (ignored: FieldValidator.InvalidException) {
        }
    }

    private fun addChecklist(title: String, body: String) {
        checklistContext.checklist.addItem(checklistContext.remoteContext.remote, title, body)
            .thenAcceptOnMainThread {
                createdCallback(it)
            }
            .exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.adding_checklist_item", it).show()
            }
    }

    @FXML
    private fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            createButton.fire()
        }
    }
}