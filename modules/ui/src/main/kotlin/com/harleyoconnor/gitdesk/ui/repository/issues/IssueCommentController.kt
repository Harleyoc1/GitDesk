package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.IssueComment
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
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
class IssueCommentController : ViewController<IssueCommentController.Context> {

    companion object {
        private val TIME_FORMAT = SimpleDateFormat("HH:mm")
        private val DATE_FORMAT = SimpleDateFormat("dd MMM yyyy")
    }

    object Loader : ResourceViewLoader<Context, IssueCommentController, VBox>(
        UIResource("/ui/layouts/repository/issues/IssueComment.fxml")
    )

    class Context(val issue: Issue, val comment: IssueComment) : ViewController.Context

    private lateinit var issue: Issue
    private lateinit var comment: IssueComment

    @FXML
    private lateinit var commenterAvatar: Circle

    @FXML
    private lateinit var usernameLabel: Label

    @FXML
    private lateinit var createdLabel: Label

    @FXML
    private lateinit var bodyLabel: Label

    override fun setup(context: Context) {
        issue = context.issue
        comment = context.comment

        commenterAvatar.fill = ImagePattern(Image(comment.commenter.avatarUrl.toExternalForm()))
        usernameLabel.text = comment.commenter.username
        createdLabel.text = TRANSLATIONS_BUNDLE.getString(
            "ui.repository.tab.issues.view.comment.header.created_at",
            TIME_FORMAT.format(issue.createdAt),
            DATE_FORMAT.format(comment.createdAt)
        )
        bodyLabel.text = comment.body ?: TRANSLATIONS_BUNDLE.getString("ui.empty")
    }

}