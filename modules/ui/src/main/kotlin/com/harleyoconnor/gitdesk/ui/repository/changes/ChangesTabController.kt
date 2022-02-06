package com.harleyoconnor.gitdesk.ui.repository.changes

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.process.logFailure
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.control.TabPane
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class ChangesTabController : ViewController<ChangesTabController.Context> {

    object Loader : ResourceViewLoader<Context, ChangesTabController, SplitPane>(
        UIResource("/ui/layouts/repository/changes/Root.fxml")
    )

    class Context(val stage: Stage, val repository: LocalRepository) : ViewController.Context

    private lateinit var stage: Stage
    private lateinit var repository: LocalRepository

    @FXML
    private lateinit var sideBar: SplitPane

    @FXML
    private lateinit var titleLabel: Label

    @FXML
    private lateinit var fileList: ScrollPane

    @FXML
    private lateinit var fileTabs: TabPane

    private val changedFilesListView by lazy {
        ChangedFileListController.Loader.load(
            ChangedFileListController.Context(this, repository)
        )
    }

    private val commitView by lazy {
        CommitController.Loader.load(CommitController.Context(this, repository.gitRepository))
    }

    @FXML
    private fun initialize() {
        fileTabs.tabDragPolicy = TabPane.TabDragPolicy.REORDER
    }

    override fun setup(context: Context) {
        this.stage = context.stage
        this.repository = context.repository
        titleLabel.text = repository.id
        fileList.content = changedFilesListView.root
        sideBar.items.add(commitView.root)
        sideBar.setDividerPositions(0.6)
    }

    fun refresh() {
        changedFilesListView.controller.refresh()
    }

    @FXML
    private fun rollbackAll(event: ActionEvent) {
        repository.gitRepository.rollbackAll()
            .ifSuccessful {
                Platform.runLater {
                    refresh()
                }
            }
            .ifFailure(::logFailure)
            .begin()
    }

    fun promptCommit() {
        commitView.controller.promptCommit()
    }


}