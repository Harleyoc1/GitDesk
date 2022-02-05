package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.repository.issues.timeline.IssueController
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.util.createClosedIssueIcon
import com.harleyoconnor.gitdesk.ui.util.createOpenIssueIcon
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import javafx.scene.control.Tooltip
import javafx.scene.layout.HBox

/**
 * @author Harley O'Connor
 */
class IssueToolbarController : AbstractIssueToolbarController<Issue, IssueToolbarController.Context>() {

    object Loader : ResourceViewLoader<Context, IssueToolbarController, HBox>(
        UIResource("/ui/layouts/repository/issues/IssueToolbar.fxml")
    )

    class Context(
        parent: IssueController<Issue>,
        remoteContext: RemoteContext,
        issue: IssueAccessor<Issue>
    ) : AbstractIssueToolbarController.Context<Issue>(parent, remoteContext, issue)

    override fun loadUIForState() {
        if (issue.get().state == Issue.State.OPEN) {
            loadUIForOpenIssue()
        } else {
            loadUIForClosedIssue()
        }
    }

    private fun loadUIForOpenIssue() {
        toggleStateButton.graphic = createClosedIssueIcon()
        toggleStateButton.tooltip = Tooltip(TRANSLATIONS_BUNDLE.getString("ui.button.close"))
    }

    private fun loadUIForClosedIssue() {
        toggleStateButton.graphic = createOpenIssueIcon()
        toggleStateButton.tooltip = Tooltip(TRANSLATIONS_BUNDLE.getString("ui.button.open"))
    }

}