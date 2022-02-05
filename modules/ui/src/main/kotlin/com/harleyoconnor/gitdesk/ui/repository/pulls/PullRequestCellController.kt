package com.harleyoconnor.gitdesk.ui.repository.pulls

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.PullRequest
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
class PullRequestCellController : ViewController<PullRequestCellController.Context> {

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("dd MMM yyyy")
    }

    object Loader : ResourceViewLoader<Context, PullRequestCellController, HBox>(
        UIResource("/ui/layouts/repository/pulls/PullRequestsCell.fxml")
    )

    class Context(val selectCallback: (PullRequest) -> Unit, val pullRequest: PullRequest) : ViewController.Context

    private lateinit var selectCallback: (PullRequest) -> Unit
    private lateinit var pullRequest: PullRequest

    @FXML
    private lateinit var root: HBox

    @FXML
    private lateinit var contextMenu: ContextMenu

    @FXML
    private lateinit var pullRequestIcon: SVGIcon

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
        pullRequest = context.pullRequest
        if (pullRequest.state == Issue.State.CLOSED) {
            if (pullRequest.merged) {
                setupForMergedPullRequest()
            } else {
                setupForClosedPullRequest()
            }
        } else {
            if (pullRequest.draft) {
                setupForDraftPullRequest()
            }
            setupForOpenPullRequest()
        }
        titleLabel.text = pullRequest.title
        pullRequest.assignees.firstOrNull()?.let {
            assigneeAvatar.fill = ImagePattern(Image(it.avatarUrl.toExternalForm()))
        } ?: root.children.remove(assigneeAvatar)
    }

    private fun setupForMergedPullRequest() {
        pullRequestIcon.setPath("/ui/icons/branch.svg")
        otherInfoLabel.text = TRANSLATIONS_BUNDLE.getString(
            "cell.pull_request.merged.info",
            pullRequest.number.toString(),
            DATE_FORMAT.format(pullRequest.createdAt),
            pullRequest.author.username,
            DATE_FORMAT.format(pullRequest.mergedAt)
        )
    }

    private fun setupForClosedPullRequest() {
        pullRequestIcon.setPath("/ui/icons/closed_pull_request.svg")
        otherInfoLabel.text = TRANSLATIONS_BUNDLE.getString(
            "cell.issue.closed.info",
            pullRequest.number.toString(),
            DATE_FORMAT.format(pullRequest.createdAt),
            pullRequest.author.username,
            DATE_FORMAT.format(pullRequest.closedAt)
        )
    }

    private fun setupForDraftPullRequest() {
        pullRequestIcon.setPath("/ui/icons/draft_pull_request.svg")
    }

    private fun setupForOpenPullRequest() {
        otherInfoLabel.text = TRANSLATIONS_BUNDLE.getString(
            "cell.issue.open.info",
            pullRequest.number.toString(),
            DATE_FORMAT.format(pullRequest.createdAt),
            pullRequest.author.username
        )
    }

    @FXML
    private fun select(event: MouseEvent) {
        if (event.button == MouseButton.PRIMARY) {
            selectCallback(pullRequest)
        }
    }

    @FXML
    private fun open(event: ActionEvent) {
        selectCallback(pullRequest)
    }

    @FXML
    private fun openInBrowser(event: ActionEvent) {
        Application.getInstance().hostServices.showDocument(pullRequest.url.toExternalForm())
    }

}