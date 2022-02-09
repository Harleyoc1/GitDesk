package com.harleyoconnor.gitdesk.ui.repository.checklists

import com.harleyoconnor.gitdesk.data.remote.checklist.ChecklistItem
import com.harleyoconnor.gitdesk.data.remote.checklist.ChecklistItemComment
import com.harleyoconnor.gitdesk.data.remote.checklist.getChecklistItemComments
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
import com.harleyoconnor.gitdesk.ui.util.createAvatarNode
import com.harleyoconnor.gitdesk.ui.util.exceptionallyOnMainThread
import com.harleyoconnor.gitdesk.ui.util.formatByDate
import com.harleyoconnor.gitdesk.ui.util.logErrorAndCreateDialogue
import com.harleyoconnor.gitdesk.ui.util.setOnAction
import com.harleyoconnor.gitdesk.ui.util.thenAcceptOnMainThread
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.toTypedArray
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.shape.Circle

/**
 * @author Harley O'Connor
 */
class ChecklistItemViewController : ViewController<ChecklistItemViewController.Context> {

    object Loader : ResourceViewLoader<Context, ChecklistItemViewController, VBox>(
        UIResource("/ui/layouts/repository/checklists/ChecklistItemView.fxml")
    )

    class Context(val itemContext: ChecklistItemContext, val deletedCallback: () -> Unit) : ViewController.Context

    private lateinit var checklistContext: ChecklistContext
    private lateinit var item: ChecklistItem
    private lateinit var deletedCallback: () -> Unit

    @FXML
    private lateinit var root: VBox

    @FXML
    private lateinit var toolbar: HBox

    @FXML
    private lateinit var deleteButton: Button

    @FXML
    private lateinit var titleLabel: Label

    @FXML
    private lateinit var idLabel: Label

    @FXML
    private lateinit var assigneesBox: HBox

    @FXML
    private lateinit var addAssigneeButton: Button

    @FXML
    private lateinit var completedCheckbox: CheckBox

    @FXML
    private lateinit var subHeadingLabel: Label

    @FXML
    private lateinit var bodyLabel: Label

    @FXML
    private lateinit var commentsScrollPane: ScrollPane

    @FXML
    private lateinit var commentsBox: VBox

    @FXML
    private lateinit var composeCommentBox: VBox

    @FXML
    private lateinit var commentField: TextArea

    @FXML
    private lateinit var commentButtonsBox: HBox

    @FXML
    private lateinit var commentAndToggleStateButton: Button

    @FXML
    private lateinit var commentButton: Button

    @FXML
    private fun initialize() {
        commentField.textProperty().addListener { _, old, new ->
            if (new.isEmpty()) {
                updateForEmptyCommentField()
            } else if (old.isEmpty()) {
                updateForNonEmptyCommentField()
            }
        }
    }

    private fun updateForEmptyCommentField() {
        commentButton.isDisable = true
        commentAndToggleStateButton.text =
            TRANSLATIONS_BUNDLE.getString(
                "ui.button.toggle_from_${
                    if (item.completed) "completed" else "not_completed"
                }"
            )
    }

    private fun updateForNonEmptyCommentField() {
        commentButton.isDisable = false
        commentAndToggleStateButton.text =
            TRANSLATIONS_BUNDLE.getString(
                "ui.button.comment_and_toggle_from_${
                    if (item.completed) "completed" else "not_completed"
                }"
            )
    }

    override fun setup(context: Context) {
        checklistContext = context.itemContext.checklistContext
        item = context.itemContext.checklistItem
        deletedCallback = context.deletedCallback
        loadAll()
    }

    private fun loadAll() {
        loadToolbar()
        loadTitle()
        loadAssignees()
        loadCheckbox()
        loadSubHeading()
        loadComments()
        loadCommentCompositionBox()
        updateForEmptyCommentField()
    }

    private fun loadToolbar() {
        // Non-collaborator cannot delete checklist item.
        if (!checklistContext.remoteContext.loggedInUserIsCollaborator) {
            toolbar.children.remove(deleteButton)
        }
    }

    private fun loadTitle() {
        idLabel.text = "#${item.id}"
        titleLabel.text = item.title
    }

    private fun loadAssignees() {
        assigneesBox.children.clear()
        val assignees = item.assignees
        if (checklistContext.remoteContext.loggedInUserIsCollaborator) {
            assigneesBox.children.add(0, addAssigneeButton)
        }
        assignees.forEach {
            assigneesBox.children.add(
                createAssigneeNode(
                    it.username,
                    it.gitHubUsername?.let { username -> getAvatarUrl(username).toExternalForm() }
                        ?: NO_PROFILE_ICON
                )
            )
        }
    }

    private fun createAssigneeNode(username: String, avatarUrl: String): Circle {
        return createAvatarNode(avatarUrl).also {
            val contextMenu = ContextMenu(
                createDeleteAssigneeItem(username)
            )
            it.setOnContextMenuRequested { event ->
                contextMenu.show(root, event.screenX, event.screenY)
            }
        }
    }

    private fun createDeleteAssigneeItem(username: String): MenuItem {
        return MenuItem(TRANSLATIONS_BUNDLE.getString("issue.assignee.remove")).also { item ->
            item.setOnAction { deleteAssignee(username) }
        }
    }

    private fun loadCheckbox() {
        completedCheckbox.isSelected = item.completed
        completedCheckbox.isDisable = !checklistContext.remoteContext.loggedInUserIsCollaborator
        completedCheckbox.setOnAction { new: Boolean ->
            updateCheckedState(new)
        }
    }

    private fun updateCheckedState(new: Boolean) {
        item.completed = new
        item.patch(
            checklistContext.remoteContext.remote,
            checklistContext.checklist
        ).thenAcceptOnMainThread {
            updateCommentButtons()
        }.exceptionallyOnMainThread {
            logErrorAndCreateDialogue("dialogue.error.toggle_checklist_item", it)
        }
    }

    private fun updateCommentButtons() {
        if (commentField.text.isEmpty()) {
            updateForEmptyCommentField()
        } else {
            updateForNonEmptyCommentField()
        }
    }

    private fun loadSubHeading() {
        subHeadingLabel.text = TRANSLATIONS_BUNDLE.getString(
            "view.checklist_item.subheading",
            item.created.formatByDate()
        )
    }

    private fun loadComments() {
        bodyLabel.text = item.body
        getChecklistItemComments(checklistContext.remoteContext.remote, checklistContext.checklist, item)
            .thenApply {
                buildCommentViews(it)
            }
            .thenAcceptOnMainThread {
                commentsBox.children.addAll(it)
            }
            .exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.loading_checklist_item_comments", it)
            }
    }

    private fun buildCommentViews(comments: List<ChecklistItemComment>): Array<Node> {
        return comments.stream()
            .map { buildCommentView(it) }
            .toTypedArray()
    }

    private fun buildCommentView(comment: ChecklistItemComment): Node {
        val remoteContext = checklistContext.remoteContext
        val context = ChecklistItemCommentController.Context(
            ChecklistItemContext(checklistContext, item),
            comment,
            this.commentsBox.children::remove
        )
        return if (canLoggedInUserModify(comment, remoteContext)) {
            ModifiableChecklistItemCommentController.Loader.load(context).root
        } else ChecklistItemCommentController.Loader.load(context).root
    }

    private fun canLoggedInUserModify(
        comment: ChecklistItemComment,
        remoteContext: RemoteContext
    ) = comment.username == remoteContext.loggedInAccount?.username ||
            remoteContext.loggedInUserIsCollaborator

    private fun loadCommentCompositionBox() {
        // User must be a collaborator to toggle the completed state.
        if (!checklistContext.remoteContext.loggedInUserIsCollaborator) {
            commentButtonsBox.children.remove(commentAndToggleStateButton)
        }
        // User must be logged in to GitDesk to add a comment.
        if (checklistContext.remoteContext.loggedInAccount == null) {
            root.children.remove(composeCommentBox)
        }
    }

    @FXML
    private fun comment(event: ActionEvent) {
        if (commentField.text.isEmpty()) {
            return
        }
        addComment()
    }

    private fun addComment() {
        item.addComment(checklistContext.remoteContext.remote, checklistContext.checklist, commentField.text)
            .thenApply {
                buildCommentView(it)
            }
            .thenAcceptOnMainThread {
                commentField.clear()
                commentsBox.children.add(it)
            }
            .exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.adding_checklist_item_comment", it).show()
            }
    }

    @FXML
    private fun commentAndToggleState(event: ActionEvent) {
        if (commentField.text.isNotEmpty()) {
            addComment()
        }
        // Toggle completed state. Listeners will update server-side.
        completedCheckbox.isSelected = !completedCheckbox.isSelected
    }

    @FXML
    private fun addAssignee(event: ActionEvent) {
        ChecklistItemAssigneeSelectionContextMenu(checklistContext, item) { username ->
            item.addAssignee(checklistContext.remoteContext.remote, checklistContext.checklist, username)
                .thenAcceptOnMainThread {
                    loadAssignees()
                }
                .exceptionallyOnMainThread {
                    logErrorAndCreateDialogue("dialogue.error.adding_assignee", it).show()
                }
        }.show(assigneesBox, Side.BOTTOM, 0.0, 0.0)
    }

    private fun deleteAssignee(username: String) {
        item.deleteAssignee(checklistContext.remoteContext.remote, checklistContext.checklist, username)
            .thenAcceptOnMainThread {
                loadAssignees()
            }
            .exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.removing_assignee", it).show()
            }
    }

    @FXML
    private fun delete(event: ActionEvent) {
        item.delete(checklistContext.remoteContext.remote, checklistContext.checklist)
            .thenAcceptOnMainThread {
                deletedCallback()
            }
            .exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.deleting_checklist_item", it).show()
            }
    }

}