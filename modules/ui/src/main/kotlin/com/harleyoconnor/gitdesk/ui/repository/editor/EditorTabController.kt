package com.harleyoconnor.gitdesk.ui.repository.editor

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.repository.RepositoryWindow
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane

/**
 * @author Harley O'Connor
 */
class EditorTabController : ViewController<EditorTabController.Context> {

    object Loader: ResourceViewLoader<Context, EditorTabController, SplitPane>(
        UIResource("/ui/layouts/repository/editor/Root.fxml")
    )

    class Context(val window: RepositoryWindow, val repository: LocalRepository): ViewController.Context

    private lateinit var window: RepositoryWindow
    private lateinit var repository: LocalRepository

    @FXML
    private lateinit var titleLabel: Label

    @FXML
    private lateinit var branchNameLabel: Label

    @FXML
    private lateinit var fileList: ScrollPane

    @FXML
    private lateinit var fileEditorTabs: TabPane

    @FXML
    private fun initialize() {
        fileEditorTabs.tabDragPolicy = TabPane.TabDragPolicy.REORDER
    }

    override fun setup(context: Context) {
        this.window = context.window
        this.repository = context.repository
        titleLabel.text = repository.id
        branchNameLabel.text = repository.gitRepository.getCurrentBranch().name
        fileList.content = FileListController.Loader.load(FileListController.Context(this, repository)).root
    }

    fun open(fileCell: FileCellController<*>) {
        if (fileEditorTabs.tabs.firstOrNull() !is FileTab) {
            fileEditorTabs.tabs.removeFirstOrNull()
        }
        fileEditorTabs.selectionModel.select(
            getOrCreateTab(fileCell)
        )
    }

    fun save() {
        val selectedTab = fileEditorTabs.selectionModel.selectedItem
        if (selectedTab is FileTab) {
            selectedTab.getSaveCallback().invoke()
        }
    }

    private fun getOrCreateTab(fileCell: FileCellController<*>) = fileEditorTabs.tabs.stream()
        .filter { it is FileTab && it.getFile() == fileCell.file }
        .findFirst()
        .orElseGet {
            createTab(fileCell)
        }

    private fun createTab(file: FileCellController<*>): Tab {
        val tab = FileTabController.Loader.load(FileTabController.Context(fileEditorTabs, file)).root
        fileEditorTabs.tabs.add(tab)
        return tab
    }

    @FXML
    private fun openIgnoredWindow(event: ActionEvent) {
        window.openIgnoredWindow()
    }

    @FXML
    private fun openBranchesWindow(event: ActionEvent) {
        window.openBranchesWindow()
    }

}