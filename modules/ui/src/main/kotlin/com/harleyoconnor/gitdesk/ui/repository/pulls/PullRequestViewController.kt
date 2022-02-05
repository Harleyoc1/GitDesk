package com.harleyoconnor.gitdesk.ui.repository.pulls

import com.harleyoconnor.gitdesk.data.remote.PullRequest
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.repository.issues.AbstractIssueViewController
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
import com.harleyoconnor.gitdesk.ui.util.formatByDate
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewLoader
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.layout.VBox

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
    private fun merge(event: ActionEvent) {

    }

    override fun loadStateLabel() {
        super.loadStateLabel()
    }

    override fun loadSubHeading() {
        val pullRequest = issue.get()
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