package com.harleyoconnor.gitdesk.ui.repository.editor

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.util.load
import javafx.fxml.FXML
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class EditorTabController {

    companion object {
        fun load(stage: Stage, repository: LocalRepository): SplitPane {
            val fxml = load<SplitPane, EditorTabController>("repository/editor/Root")
            fxml.controller.setup(stage, repository)
            return fxml.root
        }
    }

    private lateinit var stage: Stage
    private lateinit var repository: LocalRepository

    @FXML
    private lateinit var fileList: ScrollPane

    @FXML
    private lateinit var fileEditorTabs: TabPane

    @FXML
    private fun initialize() {
        fileEditorTabs.tabDragPolicy = TabPane.TabDragPolicy.REORDER
    }

    fun setup(stage: Stage, repository: LocalRepository) {
        this.stage = stage
        this.repository = repository
        fileList.content = FileListController.load(repository, this)
    }

    fun open(fileCell: FileCellController) {
        if (fileEditorTabs.tabs.firstOrNull() !is FileTab) {
            fileEditorTabs.tabs.removeFirst()
        }
        fileEditorTabs.selectionModel.select(
            getOrCreateTab(fileCell)
        )
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

}