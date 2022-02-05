package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.repository.issues.timeline.IssueController
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.HBox

/**
 * @author Harley O'Connor
 */
abstract class AbstractIssueToolbarController<I : Issue, C : AbstractIssueToolbarController.Context<I>> :
    ViewController<C> {

    open class Context<I : Issue>(
        val parent: IssueController<I>,
        val remoteContext: RemoteContext,
        val issue: IssueAccessor<I>
    ) : ViewController.Context

    private lateinit var parent: IssueController<I>
    private lateinit var remoteContext: RemoteContext
    protected lateinit var issue: IssueAccessor<I>

    @FXML
    protected lateinit var root: HBox

    @FXML
    protected lateinit var toggleStateButton: Button

    override fun setup(context: C) {
        parent = context.parent
        remoteContext = context.remoteContext
        issue = context.issue
        reloadUI()
    }

    fun reloadUI() {
        root.children.remove(3, root.children.size)
        if (shouldShowButtons()) {
            showButtons()
        }
    }

    protected open fun shouldShowButtons() = remoteContext.loggedInUserIsCollaborator

    private fun showButtons() {
        root.children.addAll(
            toggleStateButton
        )
        loadUIForState()
    }

    protected abstract fun loadUIForState()

    @FXML
    private fun toggleState(event: ActionEvent) {
        parent.toggleState()
    }

    @FXML
    private fun refresh(event: ActionEvent) {
        parent.refresh()
    }

    @FXML
    private fun openInBrowser(event: ActionEvent) {
        Application.getInstance().hostServices.showDocument(issue.get().url.toExternalForm())
    }

}