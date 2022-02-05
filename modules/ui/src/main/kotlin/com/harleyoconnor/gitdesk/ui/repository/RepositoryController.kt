package com.harleyoconnor.gitdesk.ui.repository

import com.harleyoconnor.gitdesk.data.account.Session
import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.data.remote.withFullData
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.menubar.EditMenu
import com.harleyoconnor.gitdesk.ui.menubar.FileMenu
import com.harleyoconnor.gitdesk.ui.menubar.ViewMenu
import com.harleyoconnor.gitdesk.ui.menubar.WindowMenu
import com.harleyoconnor.gitdesk.ui.repository.changes.ChangesTab
import com.harleyoconnor.gitdesk.ui.repository.editor.EditorTabController
import com.harleyoconnor.gitdesk.ui.repository.issues.IssuesTabController
import com.harleyoconnor.gitdesk.ui.repository.pulls.PullRequestsTabController
import com.harleyoconnor.gitdesk.ui.util.Tab
import com.harleyoconnor.gitdesk.ui.util.setOnSelected
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.RadioButton
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class RepositoryController : ViewController<RepositoryController.Context> {

    object Loader: ResourceViewLoader<Context, RepositoryController, BorderPane>(
        UIResource("/ui/layouts/repository/Root.fxml")
    )

    class Context(val parent: RepositoryWindow, val repository: LocalRepository): ViewController.Context

    private lateinit var parent: RepositoryWindow
    private lateinit var repository: LocalRepository

    @FXML
    private lateinit var root: BorderPane

    @FXML
    private lateinit var fileMenu: FileMenu

    @FXML
    private lateinit var editMenu: EditMenu

    @FXML
    private lateinit var viewMenu: ViewMenu

    @FXML
    private lateinit var windowMenu: WindowMenu

    @FXML
    private lateinit var tabs: VBox

    @FXML
    private lateinit var editorTabButton: RadioButton

    @FXML
    private lateinit var changesTabButton: RadioButton

    @FXML
    private lateinit var issuesTabButton: RadioButton

    @FXML
    private lateinit var pullRequestsTabButton: RadioButton

    @FXML
    private lateinit var checklistsTabButton: RadioButton

    private val remoteContext by lazy {
        val remote = repository.gitRepository.getCurrentBranch().getUpstream()!!.remote.remote.withFullData()!!
        RemoteContext(
            repository,
            remote,
            Session.getOrLoad()?.getUserFor(remote.platform)
        )
    }

    private val editorTabView by lazy {
        EditorTabController.Loader.load(
            EditorTabController.Context(
                parent,
                repository
            )
        )
    }

    private val editorTab: Tab by lazy {
        Tab(editorTabView.root) {
            root.center = it
        }
    }

    private val changesTab by lazy {
        ChangesTab(parent.stage, repository) {
            root.center = it
        }
    }

    private val issuesTabView by lazy {
        IssuesTabController.Loader.load(
            IssuesTabController.Context(parent.stage, repository, remoteContext)
        )
    }

    private val issuesTab by lazy {
        Tab(issuesTabView.root) {
            root.center = it
        }
    }

    private val pullRequestsTabView by lazy {
        PullRequestsTabController.Loader.load(
            PullRequestsTabController.Context(parent.stage, repository, remoteContext)
        )
    }

    private val pullRequestsTab by lazy {
        Tab(pullRequestsTabView.root) {
            root.center = it
        }
    }

    override fun setup(context: Context) {
        this.parent = context.parent
        this.repository = context.repository

        fileMenu.setWindow(parent)
        editMenu.useSelectableAccess(parent.selectableAccess)
        viewMenu.setStage(parent.stage)
        windowMenu.setStage(parent.stage)

        editorTabButton.setOnSelected {
            editorTab.open()
        }
        changesTabButton.setOnSelected {
            changesTab.open()
        }
        issuesTabButton.setOnSelected {
            issuesTab.open()
        }
        pullRequestsTabButton.setOnSelected {
            pullRequestsTab.open()
        }
        editorTabButton.fire()

        removeIssuesTabIfDisabled()
    }

    private fun removeIssuesTabIfDisabled() {
        if (!remoteContext.remote.hasIssues) {
            removeIssuesTabButton()
        }
    }

    private fun removeIssuesTabButton() {
        tabs.children.remove(issuesTabButton)
    }

    @FXML
    private fun save(event: ActionEvent) {
        if (editorTabButton.isSelected) {
            editorTabView.controller.save()
        }
    }
}