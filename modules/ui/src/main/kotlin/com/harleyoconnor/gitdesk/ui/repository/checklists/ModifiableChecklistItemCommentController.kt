package com.harleyoconnor.gitdesk.ui.repository.checklists

import com.harleyoconnor.gitdesk.data.remote.checklist.ChecklistItemComment
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.util.createConfirmationDialogue
import com.harleyoconnor.gitdesk.ui.util.exceptionallyOnMainThread
import com.harleyoconnor.gitdesk.ui.util.logErrorAndCreateDialogue
import com.harleyoconnor.gitdesk.ui.util.thenAcceptOnMainThread
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.TextArea
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class ModifiableChecklistItemCommentController : ChecklistItemCommentController() {

    object Loader : ResourceViewLoader<Context, ModifiableChecklistItemCommentController, VBox>(
        UIResource("/ui/layouts/repository/checklists/ModifiableChecklistItemComment.fxml")
    )

    private lateinit var removeCommentCallback: (Node) -> Unit
    private lateinit var checklistItemContext: ChecklistItemContext
    private lateinit var comment: ChecklistItemComment

    @FXML
    private lateinit var root: VBox

    @FXML
    private lateinit var header: HBox

    @FXML
    private lateinit var editButton: Button

    @FXML
    private lateinit var deleteButton: Button

    private val editCommentField: TextArea by lazy {
        TextArea(comment.body).also {
            it.isWrapText = true
        }
    }

    private val cancelEditButton: Button by lazy {
        Button(TRANSLATIONS_BUNDLE.getString("ui.button.cancel")).also {
            it.setOnAction {
                editCommentField.text = comment.body // reset field to comment body
                finishedEditing()
            }
        }
    }

    private val saveEditButton: Button by lazy {
        Button(TRANSLATIONS_BUNDLE.getString("ui.button.save")).also {
            it.setOnAction {
                saveEditedComment()
            }
        }
    }

    override fun setup(context: Context) {
        this.removeCommentCallback = context.removeCommentCallback
        this.checklistItemContext = context.checklistItemContext
        this.comment = context.comment
        super.setup(context)
    }

    private fun saveEditedComment() {
        val newBody = editCommentField.text
        if (newBody == bodyLabel.text) {
            finishedEditing()
            return
        }
        editComment(newBody)
    }

    private fun editComment(newBody: String) {
        comment.body = newBody
        comment.patch(
            checklistItemContext.checklistContext.remoteContext.remote,
            checklistItemContext.checklistContext.checklist,
            checklistItemContext.checklistItem,
        )
            .thenAcceptOnMainThread {
                bodyLabel.text = newBody
                finishedEditing()
            }
            .exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.editing_issue_comment", it).show()
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
        header.children.addAll(
            editButton, deleteButton
        )
    }

    @FXML
    private fun delete(event: ActionEvent) {
        createConfirmationDialogue("comment_deletion")
            .showAndWait()
            .filter(ButtonType.OK::equals)
            .ifPresent {
                deleteComment()
            }
    }

    private fun deleteComment() = comment.delete(
        checklistItemContext.checklistContext.remoteContext.remote,
        checklistItemContext.checklistContext.checklist,
        checklistItemContext.checklistItem
    )
        .thenAcceptOnMainThread {
            removeCommentCallback(root)
        }
        .exceptionallyOnMainThread {
            logErrorAndCreateDialogue("dialogue.error.deleting_issue_comment", it).show()
        }

}