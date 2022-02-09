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
import com.harleyoconnor.gitdesk.ui.repository.checklists.ChecklistsTabController
import com.harleyoconnor.gitdesk.ui.repository.editor.EditorTabController
import com.harleyoconnor.gitdesk.ui.repository.issues.IssuesTabController
import com.harleyoconnor.gitdesk.ui.repository.pulls.PullRequestsTabController
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.util.Tab
import com.harleyoconnor.gitdesk.ui.util.setOnSelected
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.MenuItem
import javafx.scene.control.RadioButton
import javafx.scene.control.SplitPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class RepositoryController : ViewController<RepositoryController.Context> {

    object Loader : ResourceViewLoader<Context, RepositoryController, BorderPane>(
        UIResource("/ui/layouts/repository/Repository.fxml")
    )

    class Context(val parent: RepositoryWindow, val repository: LocalRepository) : ViewController.Context

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
    private lateinit var toggleTerminalMenuItem: MenuItem

    @FXML
    private lateinit var repositoryMenu: RepositoryMenu

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

    @FXML
    private lateinit var centreSplitPane: SplitPane

    val remoteContext by lazy {
        val remote = repository.gitRepository.getCurrentBranch().getUpstream()?.remote?.remote?.withFullData()
            ?: return@lazy null
        RemoteContext(
            repository,
            remote,
            Session.getOrLoad()?.getAccount(),
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
        Tab(editorTabView.root, this::openTab)
    }

    private val changesTab by lazy {
        ChangesTab(parent.stage, repository, this::openTab)
    }

    private val issuesTabView by lazy {
        IssuesTabController.Loader.load(
            IssuesTabController.Context(parent.stage, remoteContext!!)
        )
    }

    private val issuesTab by lazy {
        Tab(issuesTabView.root, this::openTab)
    }

    private val pullRequestsTabView by lazy {
        PullRequestsTabController.Loader.load(
            PullRequestsTabController.Context(parent.stage, repository, remoteContext!!)
        )
    }

    private val pullRequestsTab by lazy {
        Tab(pullRequestsTabView.root, this::openTab)
    }

    private val checklistsTabView by lazy {
        ChecklistsTabController.Loader.load(
            ChecklistsTabController.Context(remoteContext!!)
        )
    }

    private val checklistsTab by lazy {
        Tab(checklistsTabView.root, this::openTab)
    }

    private val terminalView by lazy {
        TerminalController.Loader.load(TerminalController.Context(repository))
    }

    override fun setup(context: Context) {
        this.parent = context.parent
        this.repository = context.repository

        fileMenu.setWindow(parent)
        editMenu.useSelectableAccess(parent.selectableAccess)
        viewMenu.setStage(parent.stage)
        repositoryMenu.setWindow(parent)
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
        checklistsTabButton.setOnSelected {
            checklistsTab.open()
        }
        editorTabButton.fire()

        disableTabsIfNotNeeded()
    }

    private fun openTab(node: Node) {
        if (centreSplitPane.items.size < 1) {
            centreSplitPane.items.add(node)
        } else {
            centreSplitPane.items[0] = node
        }
    }

    private fun disableTabsIfNotNeeded() {
        if (remoteContext == null) {
            tabs.children.removeAll(issuesTabButton, pullRequestsTabButton, checklistsTabButton)
        } else {
            if (!remoteContext!!.remote.hasIssues) {
                tabs.children.remove(issuesTabButton)
            }
        }
    }

    @FXML
    private fun save(event: ActionEvent) {
        if (editorTabButton.isSelected) {
            editorTabView.controller.save()
        }
    }

    fun promptCommit() {
        changesTabButton.fire()
        changesTab.promptCommit()
    }

    @FXML
    private fun toggleTerminalView(event: ActionEvent) {
        if (centreSplitPane.items.size < 2) {
            centreSplitPane.items.add(terminalView.root)
            toggleTerminalMenuItem.text = TRANSLATIONS_BUNDLE.getString(
                "ui.menu.view.toggle_terminal_view.disable"
            )
        } else {
            centreSplitPane.items.remove(terminalView.root)
            toggleTerminalMenuItem.text = TRANSLATIONS_BUNDLE.getString(
                "ui.menu.view.toggle_terminal_view.enable"
            )
        }
    }

}