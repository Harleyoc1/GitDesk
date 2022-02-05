package com.harleyoconnor.gitdesk.ui.repository.pulls

import com.harleyoconnor.gitdesk.data.remote.PullRequest
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.repository.issues.AbstractIssuesListController
import com.harleyoconnor.gitdesk.ui.util.exceptionallyOnMainThread
import com.harleyoconnor.gitdesk.ui.util.logErrorAndCreateDialogue
import com.harleyoconnor.gitdesk.ui.util.thenAcceptOnMainThread
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import java.util.concurrent.CompletableFuture

/**
 * @author Harley O'Connor
 */
class PullRequestsListController : AbstractIssuesListController<PullRequest, PullRequestsListController.Context>() {

    object Loader : ResourceViewLoader<Context, PullRequestsListController, VBox>(
        UIResource("/ui/layouts/repository/pulls/PullRequestsList.fxml")
    )

    open class Context(
        openIssueCallback: (PullRequest) -> Unit,
        remote: RemoteContext
    ) : AbstractIssuesListController.Context<PullRequest>(openIssueCallback, remote)

    override fun loadPage(page: Int) {
        remoteContext.remote.getPullRequests(
            searchQuery,
            sort,
            sortOrder,
            page,
            Application.getInstance().backgroundExecutor
        ).thenApply {
            loadCells(it)
        }.thenAcceptOnMainThread { cells ->
            displayCells(cells)
        }.exceptionallyOnMainThread {
            logErrorAndCreateDialogue("dialogue.error.searching_pull_requests", it).show()
        }
    }

    private fun loadCells(issues: Array<out PullRequest>): Map<PullRequest, HBox> {
        return issues.associateWith {
            PullRequestCellController.Loader.load(PullRequestCellController.Context(this::select, it)).root
        }
    }

}