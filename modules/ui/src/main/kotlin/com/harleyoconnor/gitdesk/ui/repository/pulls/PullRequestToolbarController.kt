package com.harleyoconnor.gitdesk.ui.repository.pulls

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.PullRequest
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.repository.issues.AbstractIssueToolbarController
import com.harleyoconnor.gitdesk.ui.repository.issues.IssueAccessor
import com.harleyoconnor.gitdesk.ui.repository.issues.IssueController
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.util.createClosedPullRequestIcon
import com.harleyoconnor.gitdesk.ui.util.createOpenPullRequestIcon
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import javafx.scene.control.Tooltip
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class PullRequestToolbarController :
    AbstractIssueToolbarController<PullRequest, PullRequestToolbarController.Context>() {

    object Loader : ResourceViewLoader<Context, PullRequestToolbarController, HBox>(
        UIResource("/ui/layouts/repository/pulls/PullRequestToolbar.fxml")
    )

    class Context(
        parent: IssueController<PullRequest>,
        remoteContext: RemoteContext,
        pullRequest: IssueAccessor<PullRequest>
    ) : AbstractIssueToolbarController.Context<PullRequest>(parent, remoteContext, pullRequest)

    override fun shouldShowButtons(): Boolean {
        return super.shouldShowButtons() && !issue.get().merged
    }

    override fun loadUIForState() {
        if (issue.get().state == Issue.State.OPEN) {
            loadUIForOpenState()
        } else {
            loadUIForClosedState()
        }
    }

    private fun loadUIForOpenState() {
        toggleStateButton.graphic = createClosedPullRequestIcon()
        toggleStateButton.tooltip = Tooltip(TRANSLATIONS_BUNDLE.getString("ui.button.close"))
    }

    private fun loadUIForClosedState() {
        toggleStateButton.graphic = createOpenPullRequestIcon()
        toggleStateButton.tooltip = Tooltip(TRANSLATIONS_BUNDLE.getString("ui.button.open"))
    }

}