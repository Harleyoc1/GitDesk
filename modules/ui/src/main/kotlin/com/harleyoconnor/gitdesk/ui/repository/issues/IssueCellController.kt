package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Circle
import java.text.SimpleDateFormat

/**
 *
 * @author Harley O'Connor
 */
class IssueCellController : ViewController<IssueCellController.Context> {

    companion object {
        private val CREATED_AT_FORMAT = SimpleDateFormat("dd MMM")
    }

    object Loader : ResourceViewLoader<Context, IssueCellController, HBox>(
        UIResource("/ui/layouts/repository/issues/IssueCell.fxml")
    )

    class Context(val parent: IssuesListController, val issue: Issue) : ViewController.Context

    private lateinit var parent: IssuesListController
    private lateinit var issue: Issue

    @FXML
    private lateinit var issueIcon: SVGIcon

    @FXML
    private lateinit var titleLabel: Label

    @FXML
    private lateinit var otherInfoLabel: Label

    @FXML
    private lateinit var assigneeAvatar: Circle

    override fun setup(context: Context) {
        parent = context.parent
        issue = context.issue
        if (issue.state == Issue.State.CLOSED) {
            issueIcon.setPath("/ui/icons/closed_issue.svg")
        }
        titleLabel.text = issue.title
        otherInfoLabel.text = TRANSLATIONS_BUNDLE.getString("ui.repository.tab.issues.cell.info")
            .replaceFirst("{}", issue.number.toString())
            .replaceFirst("{}", CREATED_AT_FORMAT.format(issue.createdAt))
            .replaceFirst("{}", issue.author.username)
        issue.assignees.firstOrNull()?.let {
            assigneeAvatar.fill = ImagePattern(Image(it.avatarUrl.toExternalForm()))
        }
    }

    @FXML
    private fun select(event: MouseEvent) {

    }

    @FXML
    private fun open(event: ActionEvent) {

    }

    @FXML
    private fun openInBrowser(event: ActionEvent) {
        Application.getInstance().hostServices.showDocument(issue.url.toExternalForm())
    }

    @FXML
    private fun close(event: ActionEvent) {

    }

}