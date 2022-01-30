package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.RemoteRepositoryReference
import com.harleyoconnor.gitdesk.data.remote.withFullData
import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
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

    @FXML
    private lateinit var titleLabel: Label

    @FXML
    private lateinit var sideBar: VBox

    @FXML
    private lateinit var issueBox: VBox

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
            IssuesListController.Context(this, getCurrentRemote())
        ).root
    }

    private fun getCurrentRemote(): RemoteRepository {
        return repository.gitRepository.getCurrentBranch().getUpstream()!!.remote.remote.withFullData()!!
    }

    fun setShownIssue(issue: Issue) {
        issueBox.children.clear()
        issueBox.children.add(
            IssueViewController.Loader.load(IssueViewController.Context(issue)).root
        )
    }

    @FXML
    private fun create(event: ActionEvent) {

    }

}