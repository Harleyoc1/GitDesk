package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage

/**
 *
 * @author Harley O'Connor
 */
class IssuesTabController : ViewController<IssuesTabController.Context> {

    object Loader : ResourceViewLoader<Context, IssuesTabController, SplitPane>(
        UIResource("/ui/layouts/repository/issues/IssuesTab.fxml")
    )

    class Context(val stage: Stage, val remoteContext: RemoteContext) : ViewController.Context

    private lateinit var stage: Stage
    private lateinit var remoteContext: RemoteContext

    @FXML
    private lateinit var root: SplitPane

    @FXML
    private lateinit var titleLabel: Label

    @FXML
    private lateinit var sideBar: VBox

    @FXML
    private lateinit var toolBarBox: HBox

    @FXML
    private lateinit var createButton: Button

    override fun setup(context: Context) {
        stage = context.stage
        remoteContext = context.remoteContext
        titleLabel.text = context.remoteContext.remote.name.getFullName()
        sideBar.children.add(
            loadIssuesList()
        )
        // If user not logged in and linked to platform, do not show create button.
        if (remoteContext.loggedInUser == null) {
            toolBarBox.children.remove(createButton)
        }
    }

    private fun loadIssuesList(): VBox {
        return IssuesListController.Loader.load(
            IssuesListController.Context(this::setShownIssue, remoteContext)
        ).root
    }

    private fun setShownIssue(issueNumber: Int) {
        remoteContext.remote.platform.networking!!.getIssue(remoteContext.remote.name, issueNumber)
            ?.let { setShownIssue(it) }
    }

    private fun setShownIssue(issue: Issue) {
        root.items[1] = IssueViewController.Loader.load(
            IssueViewController.Context(remoteContext, issue, this::setShownIssue)
        ).root
    }

    @FXML
    private fun create(event: ActionEvent) {
        CreateIssueWindow(remoteContext, this::setShownIssue).open()
    }

}