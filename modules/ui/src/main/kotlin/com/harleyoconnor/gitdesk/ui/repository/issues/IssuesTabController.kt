package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.account.Session
import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.withFullData
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

/**
 *
 * @author Harley O'Connor
 */
class IssuesTabController : ViewController<IssuesTabController.Context> {

    object Loader : ResourceViewLoader<Context, IssuesTabController, SplitPane>(
        UIResource("/ui/layouts/repository/issues/Root.fxml")
    )

    class Context(val stage: Stage, val repository: LocalRepository) : ViewController.Context

    private lateinit var stage: Stage
    private lateinit var repository: LocalRepository

    private val remoteContext: RemoteContext by lazy {
        val remote = repository.gitRepository.getCurrentBranch().getUpstream()!!.remote.remote.withFullData()!!
        RemoteContext(
            repository,
            remote,
            Session.getOrLoad()?.getUserFor(remote.platform)
        )
    }

    @FXML
    private lateinit var root: SplitPane

    @FXML
    private lateinit var titleLabel: Label

    @FXML
    private lateinit var sideBar: VBox

    override fun setup(context: Context) {
        stage = context.stage
        repository = context.repository
        titleLabel.text = repository.id
        sideBar.children.add(
            loadIssuesList()
        )
    }

    private fun loadIssuesList(): VBox {
        return IssuesListController.Loader.load(
            IssuesListController.Context(this, remoteContext)
        ).root
    }

    private fun setShownIssue(issueNumber: Int) {
        remoteContext.remote.platform.networking!!.getIssue(remoteContext.remote.name, issueNumber)
            ?.let { setShownIssue(it) }
    }

    fun setShownIssue(issue: Issue) {
        root.items[1] = IssueViewController.Loader.load(
            IssueViewController.Context(remoteContext, issue, this::setShownIssue)
        ).root
    }

    @FXML
    private fun create(event: ActionEvent) {

    }

}