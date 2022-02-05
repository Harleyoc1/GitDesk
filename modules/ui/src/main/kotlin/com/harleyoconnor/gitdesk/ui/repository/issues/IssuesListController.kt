package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.util.exceptionallyOnMainThread
import com.harleyoconnor.gitdesk.ui.util.logErrorAndCreateDialogue
import com.harleyoconnor.gitdesk.ui.util.thenAcceptOnMainThread
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class IssuesListController : AbstractIssuesListController<Issue, IssuesListController.Context>() {

    object Loader : ResourceViewLoader<Context, IssuesListController, VBox>(
        UIResource("/ui/layouts/repository/issues/IssueList.fxml")
    )

    open class Context(
        openIssueCallback: (Issue) -> Unit,
        remote: RemoteContext
    ) : AbstractIssuesListController.Context<Issue>(openIssueCallback, remote)

    override fun loadPage(page: Int) {
        remoteContext.remote.getIssues(
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
            logErrorAndCreateDialogue("dialogue.error.searching_issues", it).show()
        }
    }

    private fun loadCells(issues: Array<out Issue>): Map<Issue, HBox> {
        return issues.associateWith {
            IssueCellController.Loader.load(IssueCellController.Context(this::select, it)).root
        }
    }

}