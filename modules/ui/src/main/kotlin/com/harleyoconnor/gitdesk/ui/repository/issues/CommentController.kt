package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Comment
import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
import com.harleyoconnor.gitdesk.ui.util.formatByDate
import com.harleyoconnor.gitdesk.ui.util.formatByTime
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.VBox
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Circle
import java.text.SimpleDateFormat

/**
 * @author Harley O'Connor
 */
open class CommentController : ViewController<CommentController.Context> {

    object Loader : ResourceViewLoader<Context, CommentController, VBox>(
        UIResource("/ui/layouts/repository/issues/Comment.fxml")
    )

    class Context(val parent: IssueController<out Issue>, val issue: IssueAccessor<out Issue>, val comment: Comment) :
        ViewController.Context

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
        commenterAvatar.fill = ImagePattern(Image(comment.commenter.avatarUrl.toExternalForm()))
        usernameLabel.text = comment.commenter.username
        createdLabel.text = TRANSLATIONS_BUNDLE.getString(
            "ui.repository.tab.issues.view.comment.header.created_at",
            comment.createdAt.formatByTime(),
            comment.createdAt.formatByDate()
        )
        bodyLabel.text = comment.body ?: TRANSLATIONS_BUNDLE.getString("ui.empty")
    }

}