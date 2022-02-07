package com.harleyoconnor.gitdesk.ui.repository.pulls

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.data.remote.PullRequest
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.util.exceptionallyOnMainThread
import com.harleyoconnor.gitdesk.ui.util.logErrorAndCreateDialogue
import com.harleyoconnor.gitdesk.ui.util.thenAcceptOnMainThread
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
class PullRequestsTabController : ViewController<PullRequestsTabController.Context> {

    object Loader : ResourceViewLoader<Context, PullRequestsTabController, SplitPane>(
        UIResource("/ui/layouts/repository/pulls/PullRequestsTab.fxml")
    )

    class Context(val stage: Stage, val repository: LocalRepository, val remoteContext: RemoteContext) :
        ViewController.Context

    private lateinit var stage: Stage
    private lateinit var repository: LocalRepository
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
        repository = context.repository
        remoteContext = context.remoteContext
        titleLabel.text = repository.id
        sideBar.children.add(
            loadPullRequestsList()
        )
        // If user not logged in and linked to platform, do not show create button.
        if (remoteContext.loggedInUser == null) {
            toolBarBox.children.remove(createButton)
        }
    }

    private fun loadPullRequestsList(): VBox {
        return PullRequestsListController.Loader.load(
            PullRequestsListController.Context(
                // Full PR data not loaded by search, so refresh data.
                { pullRequest -> setShownPullRequest(pullRequest.number) },
                remoteContext
            )
        ).root
    }

    private fun setShownPullRequest(issueNumber: Int) {
        remoteContext.remote.platform.networking!!.getPullRequest(remoteContext.remote.name, issueNumber)
            .thenAcceptOnMainThread {
                setShownPullRequest(it)
            }
            .exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.retrieving_pull_request", it).show()
            }
    }

    private fun setShownPullRequest(pullRequest: PullRequest) {
        root.items[1] = PullRequestViewController.Loader.load(
            PullRequestViewController.Context(remoteContext, pullRequest, this::setShownPullRequest)
        ).root
    }

    @FXML
    private fun create(event: ActionEvent) {
        TODO("PR Creation")
//        CreateIssueWindow(remoteContext, this::setShownIssue).open()
    }

}