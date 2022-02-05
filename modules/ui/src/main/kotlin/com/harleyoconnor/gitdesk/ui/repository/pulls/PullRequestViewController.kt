package com.harleyoconnor.gitdesk.ui.repository.pulls

import com.harleyoconnor.gitdesk.data.remote.PullRequest
import com.harleyoconnor.gitdesk.data.remote.timeline.EventType
import com.harleyoconnor.gitdesk.data.remote.timeline.MergedEvent
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.repository.issues.AbstractIssueViewController
import com.harleyoconnor.gitdesk.ui.style.DRAFT_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.style.MERGED_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
import com.harleyoconnor.gitdesk.ui.util.CLOSED_PULL_REQUEST_ICON
import com.harleyoconnor.gitdesk.ui.util.DRAFT_PULL_REQUEST_ICON
import com.harleyoconnor.gitdesk.ui.util.MERGED_PULL_REQUEST_ICON
import com.harleyoconnor.gitdesk.ui.util.OPEN_PULL_REQUEST_ICON
import com.harleyoconnor.gitdesk.ui.util.exceptionallyOnMainThread
import com.harleyoconnor.gitdesk.ui.util.formatByDate
import com.harleyoconnor.gitdesk.ui.util.logErrorAndCreateDialogue
import com.harleyoconnor.gitdesk.ui.util.thenAcceptOnMainThread
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewLoader
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import java.util.Date

/**
 * @author Harley O'Connor
 */
class PullRequestViewController : AbstractIssueViewController<PullRequest, PullRequestViewController.Context>() {

    object Loader : ResourceViewLoader<Context, PullRequestViewController, VBox>(
        UIResource("/ui/layouts/repository/pulls/PullRequestView.fxml")
    )

    class Context(remoteContext: RemoteContext, pullRequest: PullRequest, refreshCallback: (Int) -> Unit) :
        AbstractIssueViewController.Context<PullRequest>(remoteContext, pullRequest, refreshCallback)

    override val toolbarView: ViewLoader.View<PullRequestToolbarController, out Node> by lazy {
        PullRequestToolbarController.Loader.load(PullRequestToolbarController.Context(this, remoteContext, issue))
    }

    @FXML
    private lateinit var mergeButton: Button

    override fun setup(context: Context) {
        super.setup(context)
        mergeButton.isDisable = issue.draft
        if (!remoteContext.loggedInUserIsCollaborator || issue.merged) {
            commentBox.children.remove(mergeButton)
        }
    }

    @FXML
    private fun merge(event: ActionEvent) {
        issue.merge(remoteContext.loggedInUser!!)
            .thenAcceptOnMainThread {
                val createdAt = Date()
                issueUpdated()
                addMergedEventToTimeline(createdAt, it)
                addStateChangeEventToTimeline(createdAt)
            }
            .exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.merging_pull_request", it).show()
            }
    }

    private fun addMergedEventToTimeline(
        createdAt: Date,
        it: PullRequest.MergeResponse
    ) {
        addEventToTimeline(
            MergedEvent.Raw(
                EventType.MERGED, remoteContext.loggedInUser!!, createdAt, it.commitId
            )
        )
    }

    override fun loadStateBox() {
        super.loadStateBox()
        stateBox.pseudoClassStateChanged(DRAFT_PSEUDO_CLASS, issue.draft)
        stateBox.pseudoClassStateChanged(MERGED_PSEUDO_CLASS, issue.merged)
        if (issue.draft) {
            loadStateBoxForDraft()
            stateLabel.text = TRANSLATIONS_BUNDLE.getString("pull_request.draft")
        } else if (issue.merged) {
            loadStateBoxForMerged()
            stateLabel.text = TRANSLATIONS_BUNDLE.getString("pull_request.merged")
        }
    }

    override fun loadStateBoxForOpen() {
        stateIcon.setupFromSvg(OPEN_PULL_REQUEST_ICON)
    }

    private fun loadStateBoxForDraft() {
        stateIcon.setupFromSvg(DRAFT_PULL_REQUEST_ICON)
    }

    override fun loadStateBoxForClosed() {
        stateIcon.setupFromSvg(CLOSED_PULL_REQUEST_ICON)
    }

    private fun loadStateBoxForMerged() {
        stateIcon.setupFromSvg(MERGED_PULL_REQUEST_ICON)
    }

    override fun loadSubHeading() {
        val pullRequest = issue
        if (pullRequest.merged) {
            loadMergedSubheading(pullRequest)
        } else {
            loadOpenSubheading(pullRequest)
        }
    }

    private fun loadMergedSubheading(pullRequest: PullRequest) {
        subHeadingLabel.text = TRANSLATIONS_BUNDLE.getString(
            "ui.pull_request.merged.subheading",
            pullRequest.mergedBy!!.username,
            pullRequest.base!!.label,
            pullRequest.head!!.label,
            pullRequest.mergedAt!!.formatByDate()
        )
    }

    private fun loadOpenSubheading(pullRequest: PullRequest) {
        subHeadingLabel.text = TRANSLATIONS_BUNDLE.getString(
            "ui.pull_request.open.subheading",
            pullRequest.author.username,
            pullRequest.base!!.label,
            pullRequest.head!!.label
        )
    }

}