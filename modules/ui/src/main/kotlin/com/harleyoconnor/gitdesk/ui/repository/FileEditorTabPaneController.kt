package com.harleyoconnor.gitdesk.ui.repository

import javafx.fxml.FXML
import javafx.geometry.Side
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import java.io.File

/**
 * @author Harley O'Connor
 */
class FileEditorTabPaneController {

    @FXML
    private lateinit var root: TabPane

    @FXML
    private fun initialize() {
        root.tabDragPolicy = TabPane.TabDragPolicy.REORDER
    }

    fun open(fileCell: FileCellController) {
        root.selectionModel.select(
            getOrCreateTab(fileCell)
        )
    }

    private fun getOrCreateTab(fileCell: FileCellController) = root.tabs.stream()
        .filter { it is FileTab && it.getFile() == fileCell.file }
        .findFirst()
        .orElseGet {
            createTab(fileCell)
        }

    private fun createTab(file: FileCellController): Tab {
        val tab = FileTabController.load(root, file)
        root.tabs.add(tab)
        return tab
    }

}