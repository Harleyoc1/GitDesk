package com.harleyoconnor.gitdesk.ui.repository.editor

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.repository.RepositoryWindow
import com.harleyoconnor.gitdesk.ui.repository.branch.BranchesWindow
import com.harleyoconnor.gitdesk.ui.util.LoadedFXML
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.ui.window.Window
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class EditorTabController {

    companion object {
        fun load(window: RepositoryWindow, repository: LocalRepository): LoadedFXML<SplitPane, EditorTabController> {
            val fxml = load<SplitPane, EditorTabController>("repository/editor/Root")
            fxml.controller.setup(window, repository)
            return fxml
        }
    }

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

    fun setup(window: RepositoryWindow, repository: LocalRepository) {
        this.window = window
        this.repository = repository
        titleLabel.text = repository.id
        branchNameLabel.text = repository.gitRepository.getCurrentBranch().name
        fileList.content = FileListController.load(repository, this)
    }

    fun open(fileCell: FileCellController) {
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

    private fun getOrCreateTab(fileCell: FileCellController) = fileEditorTabs.tabs.stream()
        .filter { it is FileTab && it.getFile() == fileCell.file }
        .findFirst()
        .orElseGet {
            createTab(fileCell)
        }

    private fun createTab(file: FileCellController): Tab {
        val tab = FileTabController.load(fileEditorTabs, file)
        fileEditorTabs.tabs.add(tab)
        return tab
    }

    @FXML
    private fun openBranchesWindow(event: ActionEvent) {
        window.openBranchesWindow()
    }

}