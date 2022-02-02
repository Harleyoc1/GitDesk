package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
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
        private val DATE_FORMAT = SimpleDateFormat("dd MMM yyyy")
    }

    object Loader : ResourceViewLoader<Context, IssueCellController, HBox>(
        UIResource("/ui/layouts/repository/issues/IssueCell.fxml")
    )

    class Context(val parent: IssuesListController, val issue: Issue) : ViewController.Context

    private lateinit var parent: IssuesListController
    private lateinit var issue: Issue

    @FXML
    private lateinit var root: HBox

    @FXML
    private lateinit var contextMenu: ContextMenu

    @FXML
    private lateinit var issueIcon: SVGIcon

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
        parent = context.parent
        issue = context.issue
        if (issue.state == Issue.State.CLOSED) {
            setupForClosedIssue()
        } else {
            setupForOpenIssue()
        }
        titleLabel.text = issue.title
        issue.assignees.firstOrNull()?.let {
            assigneeAvatar.fill = ImagePattern(Image(it.avatarUrl.toExternalForm()))
        } ?: root.children.remove(assigneeAvatar)
    }

    private fun setupForClosedIssue() {
        issueIcon.setPath("/ui/icons/closed_issue.svg")
        otherInfoLabel.text = TRANSLATIONS_BUNDLE.getString(
            "ui.repository.tab.issues.cell.closed.info",
            issue.number.toString(),
            DATE_FORMAT.format(issue.createdAt),
            issue.author.username,
            DATE_FORMAT.format(issue.closedAt)
        )
    }

    private fun setupForOpenIssue() {
        otherInfoLabel.text = TRANSLATIONS_BUNDLE.getString(
            "ui.repository.tab.issues.cell.open.info",
            issue.number.toString(),
            DATE_FORMAT.format(issue.createdAt),
            issue.author.username
        )
    }

    @FXML
    private fun select(event: MouseEvent) {
        if (event.button == MouseButton.PRIMARY) {
            parent.select(issue)
        }
    }

    @FXML
    private fun open(event: ActionEvent) {
        parent.select(issue)
    }

    @FXML
    private fun openInBrowser(event: ActionEvent) {
        Application.getInstance().hostServices.showDocument(issue.url.toExternalForm())
    }

}