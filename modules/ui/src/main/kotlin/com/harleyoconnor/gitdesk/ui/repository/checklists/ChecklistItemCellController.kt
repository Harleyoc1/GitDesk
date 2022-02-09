package com.harleyoconnor.gitdesk.ui.repository.checklists

import com.harleyoconnor.gitdesk.data.remote.checklist.ChecklistItem
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
import com.harleyoconnor.gitdesk.ui.util.exceptionallyOnMainThread
import com.harleyoconnor.gitdesk.ui.util.formatByDate
import com.harleyoconnor.gitdesk.ui.util.logErrorAndCreateDialogue
import com.harleyoconnor.gitdesk.ui.util.setOnAction
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Circle

/**
 *
 * @author Harley O'Connor
 */
class ChecklistItemCellController : ViewController<ChecklistItemCellController.Context> {

    object Loader : ResourceViewLoader<Context, ChecklistItemCellController, HBox>(
        UIResource("/ui/layouts/repository/checklists/ChecklistItemCell.fxml")
    )

    class Context(val selectCallback: (ChecklistItem) -> Unit, val itemContext: ChecklistItemContext) :
        ViewController.Context

    private lateinit var selectCallback: (ChecklistItem) -> Unit
    private lateinit var checklistContext: ChecklistContext
    private lateinit var item: ChecklistItem

    @FXML
    private lateinit var root: HBox

    @FXML
    private lateinit var contextMenu: ContextMenu

    @FXML
    private lateinit var completedCheckbox: CheckBox

    @FXML
    private lateinit var titleLabel: Label

    @FXML
    private lateinit var otherInfoLabel: Label

    @FXML
    private lateinit var assigneeAvatar: Circle

    @FXML
    private fun initialize() {
        root.setOnContextMenuRequested {
            contextMenu.show(root, it.screenX, it.screenY)
        }
    }

    override fun setup(context: Context) {
        selectCallback = context.selectCallback
        checklistContext = context.itemContext.checklistContext
        item = context.itemContext.checklistItem
        setupCheckbox()
        titleLabel.text = item.title
        otherInfoLabel.text = TRANSLATIONS_BUNDLE.getString(
            "cell.checklist_item.info",
            item.id.toString(),
            item.created.formatByDate()
        )
        item.assignees.firstOrNull()?.let {
            assigneeAvatar.fill = ImagePattern(Image(
                it.gitHubUsername?.let { username -> getAvatarUrl(username).toExternalForm() } ?: NO_PROFILE_ICON
            ))
        } ?: run { root.children.remove(assigneeAvatar) }
    }

    private fun setupCheckbox() {
        completedCheckbox.isSelected = item.completed
        completedCheckbox.isDisable = !checklistContext.remoteContext.loggedInUserIsCollaborator
        completedCheckbox.setOnAction { new: Boolean ->
            item.completed = new
            item.patch(
                checklistContext.remoteContext.remote,
                checklistContext.checklist
            ).exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.toggle_checklist_item", it)
            }
        }
    }

    @FXML
    private fun select(event: MouseEvent) {
        if (event.button == MouseButton.PRIMARY) {
            selectCallback(item)
        }
    }

    @FXML
    private fun open(event: ActionEvent) {
        selectCallback(item)
    }

}