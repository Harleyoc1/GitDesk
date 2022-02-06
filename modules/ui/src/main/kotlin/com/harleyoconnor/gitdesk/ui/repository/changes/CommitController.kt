package com.harleyoconnor.gitdesk.ui.repository.changes

import com.harleyoconnor.gitdesk.git.repository.Repository
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.process.logFailure
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox

/**
 *
 * @author Harley O'Connor
 */
class CommitController : ViewController<CommitController.Context> {

    object Loader : ResourceViewLoader<Context, CommitController, VBox>(
        UIResource("/ui/layouts/repository/changes/Commit.fxml")
    )

    class Context(val parent: ChangesTabController, val repository: Repository) : ViewController.Context

    private lateinit var parent: ChangesTabController
    private lateinit var repository: Repository

    @FXML
    private lateinit var summaryField: TextField

    @FXML
    private lateinit var descriptionField: TextArea

    @FXML
    private lateinit var commitButton: Button

    override fun setup(context: Context) {
        this.parent = context.parent
        this.repository = context.repository
    }

    fun promptCommit() {
        summaryField.requestFocus()
    }

    @FXML
    private fun commitAndPush(event: ActionEvent) {
        commit(event)
        // TODO: Pushing
    }

    @FXML
    private fun commit(event: ActionEvent) {
        repository.commit(summaryField.text, descriptionField.text)
            .ifSuccessful {
                Platform.runLater {
                    summaryField.clear()
                    descriptionField.clear()
                    parent.refresh()
                }
            }
            .ifFailure(::logFailure)
            .begin()
    }

    @FXML
    private fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            commitButton.fire()
        }
    }

}