package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Comment
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.util.createConfirmationDialogue
import com.harleyoconnor.gitdesk.ui.util.createErrorDialogue
import com.harleyoconnor.gitdesk.ui.util.exceptionallyOnMainThread
import com.harleyoconnor.gitdesk.ui.util.thenAcceptOnMainThread
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.TextArea
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.apache.logging.log4j.LogManager

/**
 * @author Harley O'Connor
 */
class ModifiableCommentController : CommentController() {

    object Loader : ResourceViewLoader<Context, ModifiableCommentController, VBox>(
        UIResource("/ui/layouts/repository/issues/ModifiableComment.fxml")
    )

    private lateinit var parent: TimelineController
    private lateinit var issue: IssueAccessor
    private lateinit var comment: Comment

    @FXML
    private lateinit var root: VBox

    @FXML
    private lateinit var header: HBox

    @FXML
    private lateinit var editButton: Button

    @FXML
    private lateinit var deleteButton: Button

    private val editCommentField: TextArea by lazy {
        val textArea = TextArea(comment.body)
        textArea.isWrapText = true
        textArea
    }

    private val cancelEditButton: Button by lazy {
        val button = Button(TRANSLATIONS_BUNDLE.getString("ui.button.cancel"))
        button.setOnAction {
            editCommentField.text = comment.body // reset field to comment body
            finishedEditing()
        }
        button
    }

    private val saveEditButton: Button by lazy {
        val button = Button(TRANSLATIONS_BUNDLE.getString("ui.button.save"))
        button.setOnAction {
            saveEditedComment()
        }
        button
    }

    override fun setup(context: Context) {
        this.parent = context.parent
        this.issue = context.issue
        this.comment = context.comment
        super.setup(context)
        if (comment.isIssueBody()) {
            this.header.children.remove(this.deleteButton)
        }
    }

    private fun saveEditedComment() {
        val newBody = editCommentField.text
        if (newBody == bodyLabel.text) {
            finishedEditing()
            return
        }
        if (comment.isIssueBody()) {
            editIssueBody(newBody)
        } else {
            editComment(comment.id!!, newBody)
        }
    }

    private fun editComment(id: Int, newBody: String) {
        issue.get().editComment(id, newBody)
            .thenAcceptOnMainThread {
                bodyLabel.text = newBody
                finishedEditing()
            }
            .exceptionallyOnMainThread {
                createErrorDialogue(
                    TRANSLATIONS_BUNDLE.getString("dialogue.error.editing_issue_comment"),
                    it
                ).show()
                LogManager.getLogger().error("Editing issue comment.", it)
            }
    }

    /**
     * Sends a request to edit the issue body. Should only be called if this 'comment' represents the issue body.
     */
    private fun editIssueBody(newBody: String) {
        issue.get().editBody(newBody)
            .thenAcceptOnMainThread {
                parent.issueUpdated(it)
                bodyLabel.text = newBody
                finishedEditing()
            }
            .exceptionallyOnMainThread {
                createErrorDialogue(
                    TRANSLATIONS_BUNDLE.getString("dialogue.error.editing_issue_body"),
                    it
                ).show()
                LogManager.getLogger().error("Editing issue body.", it)
            }
    }

    @FXML
    private fun edit(event: ActionEvent) {
        root.children.remove(bodyLabel)
        root.children.add(editCommentField)
        header.children.removeAll(
            editButton, deleteButton
        )
        header.children.addAll(
            cancelEditButton, saveEditButton
        )
    }

    private fun finishedEditing() {
        root.children.remove(editCommentField)
        root.children.add(bodyLabel)
        header.children.removeAll(
            cancelEditButton, saveEditButton
        )
        header.children.add(editButton)
        if (!comment.isIssueBody()) {
            header.children.add(deleteButton)
        }
    }

    @FXML
    private fun delete(event: ActionEvent) {
        comment.id?.let { id ->
            createConfirmationDialogue("comment_deletion")
                .showAndWait()
                .filter(ButtonType.OK::equals)
                .ifPresent {
                    deleteComment(id)
                }
        }
    }

    private fun deleteComment(id: Int) = issue.get().deleteComment(id)
        .thenAcceptOnMainThread {
            parent.remove(root)
        }
        .exceptionallyOnMainThread {
            createErrorDialogue(TRANSLATIONS_BUNDLE.getString("dialogue.error.deleting_issue_comment"), it).show()
            LogManager.getLogger().error("Deleting issue comment.", it)
        }

}