package com.harleyoconnor.gitdesk.ui.repository.checklists

import com.harleyoconnor.gitdesk.data.remote.checklist.ChecklistItemComment
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
import com.harleyoconnor.gitdesk.ui.util.formatByDate
import com.harleyoconnor.gitdesk.ui.util.formatByTime
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.VBox
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Circle

/**
 * @author Harley O'Connor
 */
open class ChecklistItemCommentController : ViewController<ChecklistItemCommentController.Context> {

    object Loader : ResourceViewLoader<Context, ChecklistItemCommentController, VBox>(
        UIResource("/ui/layouts/repository/checklists/ChecklistItemComment.fxml")
    )

    class Context(
        val checklistItemContext: ChecklistItemContext,
        val comment: ChecklistItemComment,
        val removeCommentCallback: (Node) -> Unit
    ) : ViewController.Context

    @FXML
    private lateinit var commenterAvatar: Circle

    @FXML
    private lateinit var usernameLabel: Label

    @FXML
    private lateinit var createdLabel: Label

    @FXML
    protected lateinit var bodyLabel: Label

    override fun setup(context: Context) {
        val comment = context.comment
        commenterAvatar.fill = ImagePattern(Image(
            comment.gitHubUsername?.let { username -> getAvatarUrl(username).toExternalForm() } ?: NO_PROFILE_ICON
        ))
        usernameLabel.text = comment.username
        createdLabel.text = TRANSLATIONS_BUNDLE.getString(
            "view.checklist_item.comment.heading",
            comment.created.formatByTime(),
            comment.created.formatByDate()
        )
        bodyLabel.text = comment.body
    }

}