package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.util.getCloseIcon
import com.harleyoconnor.gitdesk.ui.util.getOpenIcon
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.xml.SVGCache
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class IssueToolbarController : ViewController<IssueToolbarController.Context> {

    companion object {
        val LOCK_ICON = SVGCache.getOrLoad(UIResource("/ui/icons/lock.svg"))
        val UNLOCK_ICON = SVGCache.getOrLoad(UIResource("/ui/icons/unlock.svg"))
    }

    // TODO: Refresh button, open in browser button.

    object Loader : ResourceViewLoader<Context, IssueToolbarController, VBox>(
        UIResource("/ui/layouts/repository/issues/IssueToolbar.fxml")
    )

    class Context(val parent: IssueViewController, val remoteContext: RemoteContext, val issue: IssueAccessor) :
        ViewController.Context

    private lateinit var parent: IssueViewController
    private lateinit var remoteContext: RemoteContext
    private lateinit var issue: IssueAccessor

    @FXML
    private lateinit var root: HBox

    @FXML
    private lateinit var toggleStateButton: Button

    @FXML
    private lateinit var toggleLockedButton: Button

    override fun setup(context: Context) {
        parent = context.parent
        remoteContext = context.remoteContext
        issue = context.issue
        reloadUI()
    }

    fun reloadUI() {
        root.children.remove(1, root.children.size)
        if (remoteContext.loggedInUserIsCollaborator) {
            showButtons()
        }
    }

    private fun showButtons() {
        root.children.addAll(
            toggleStateButton, toggleLockedButton
        )
        loadUIForState()
        loadUIForLockedState()
    }

    private fun loadUIForState() {
        if (issue.get().state == Issue.State.OPEN) {
            loadUIForOpenIssue()
        } else {
            loadUIForClosedIssue()
        }
    }

    private fun loadUIForOpenIssue() {
        toggleStateButton.graphic = getCloseIcon()
        toggleStateButton.tooltip = Tooltip(TRANSLATIONS_BUNDLE.getString("ui.button.close"))
    }

    private fun loadUIForClosedIssue() {
        toggleStateButton.graphic = getOpenIcon()
        toggleStateButton.tooltip = Tooltip(TRANSLATIONS_BUNDLE.getString("ui.button.open"))
    }

    private fun loadUIForLockedState() {
        if (issue.get().locked) {
            loadUIForLockedIssue()
        } else {
            loadUIForUnlockedIssue()
        }
    }

    private fun loadUIForLockedIssue() {
        (toggleLockedButton.graphic as SVGIcon).setupFromSvg(UNLOCK_ICON)
        toggleLockedButton.tooltip.text = TRANSLATIONS_BUNDLE.getString("ui.button.unlock")
    }

    private fun loadUIForUnlockedIssue() {
        (toggleLockedButton.graphic as SVGIcon).setupFromSvg(LOCK_ICON)
        toggleLockedButton.tooltip.text = TRANSLATIONS_BUNDLE.getString("ui.button.lock")
    }

    @FXML
    private fun toggleState(event: ActionEvent) {
        parent.toggleState()
    }

    @FXML
    private fun toggleLocked(event: ActionEvent) {

    }

}