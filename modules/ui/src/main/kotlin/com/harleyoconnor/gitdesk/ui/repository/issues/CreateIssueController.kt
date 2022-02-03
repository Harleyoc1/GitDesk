package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.form.validation.FieldValidator
import com.harleyoconnor.gitdesk.ui.node.TextArea
import com.harleyoconnor.gitdesk.ui.node.TextField
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.util.exceptionallyOnMainThread
import com.harleyoconnor.gitdesk.ui.util.logAndCreateDialogue
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
class CreateIssueController : ViewController<CreateIssueController.Context> {

    object Loader : ResourceViewLoader<Context, CreateIssueController, VBox>(
        UIResource("/ui/layouts/repository/issues/CreateIssue.fxml")
    )

    class Context(
        val remoteContext: RemoteContext,
        val createdCallback: (Issue) -> Unit,
        val canceledCallback: () -> Unit
    ) : ViewController.Context

    private lateinit var remoteContext: RemoteContext
    private lateinit var createdCallback: (Issue) -> Unit
    private lateinit var canceledCallback: () -> Unit

    @FXML
    private lateinit var titleField: TextField

    @FXML
    private lateinit var bodyField: TextArea

    @FXML
    private lateinit var createButton: Button

    override fun setup(context: Context) {
        remoteContext = context.remoteContext
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
            addIssue(
                titleField.getText(), bodyField.getText()
            )
        } catch (ignored: FieldValidator.InvalidException) {
        }
    }

    private fun addIssue(title: String, body: String) {
        remoteContext.remote.addIssue(title, body)
            .thenAcceptOnMainThread {
                createdCallback(it)
            }
            .exceptionallyOnMainThread {
                logAndCreateDialogue("dialogue.error.adding_issue", it).show()
            }
    }

    @FXML
    private fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            createButton.fire()
        }
    }
}