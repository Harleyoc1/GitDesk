package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.account.Session
import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.util.getCloseIcon
import com.harleyoconnor.gitdesk.ui.util.getOpenIcon
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import java.util.concurrent.CompletableFuture

/**
 * @author Harley O'Connor
 */
class IssueToolbarController : ViewController<IssueToolbarController.Context> {

    // TODO: Refresh button, open in browser button.

    object Loader : ResourceViewLoader<Context, IssueToolbarController, VBox>(
        UIResource("/ui/layouts/repository/issues/IssueToolbar.fxml")
    )

    class Context(val repository: RemoteRepository, val issue: Issue) : ViewController.Context

    private lateinit var issue: Issue

    @FXML
    private lateinit var root: HBox

    @FXML
    private lateinit var toggleStateButton: Button

    @FXML
    private lateinit var pinButton: Button

    @FXML
    private lateinit var deleteButton: Button

    override fun setup(context: Context) {
        issue = context.issue

        root.children.remove(1, root.children.size)
        Session.getOrLoad()?.getGitHubAccount()?.username?.let { username ->
            showButtonsIfUserIsCollaborator(context, username)
        }
    }

    private fun showButtonsIfUserIsCollaborator(
        context: Context,
        username: String
    ) = CompletableFuture.supplyAsync({
        context.repository.isCollaborator(username)
    }, Application.getInstance().backgroundExecutor)
        .thenApplyAsync({ collaborator: Boolean? ->
            // Show edit issue buttons if we could check the user is a collaborator, and they are.
            if (collaborator == true) {
                showButtons()
            }
        }, Application.getInstance().mainThreadExecutor)

    private fun showButtons() {
        root.children.addAll(
            toggleStateButton, pinButton, deleteButton
        )
        loadUI()
    }

    fun loadUI() {
        if (issue.state == Issue.State.OPEN) {
            loadUIForOpenIssue()
        } else if (issue.state == Issue.State.CLOSED) {
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

    fun toggleState(event: ActionEvent) {

    }

    fun pin(event: ActionEvent) {

    }

    fun delete(event: ActionEvent) {

    }

}