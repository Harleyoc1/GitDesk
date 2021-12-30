package com.harleyoconnor.gitdesk.ui.repository.editor

import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.util.getIcon
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.util.getUserHome
import com.harleyoconnor.gitdesk.util.system.SystemManager
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.Tooltip

/**
 * @author Harley O'Connor
 */
class FileTabController {

    companion object {
        fun load(parent: TabPane, fileCell: FileCellController): Tab {
            val fxml = load<Tab, FileTabController>("repository/editor/FileTab")
            fxml.controller.setup(parent, fileCell)
            return fxml.root
        }
    }

    private lateinit var parent: TabPane

    private lateinit var fileCell: FileCellController

    @FXML
    private lateinit var root: FileTab

    @FXML
    private lateinit var tooltip: Tooltip

    @FXML
    private lateinit var icon: SVGIcon

    @FXML
    private lateinit var label: Label

    private fun setup(parent: TabPane, fileCell: FileCellController) {
        this.parent = parent
        this.fileCell = fileCell

        val file = fileCell.file
        val fileEditorFxml = FileEditorController.load(file)
        this.root.setFile(file)
        this.root.setSaveCallback {
            fileEditorFxml.controller.saveToFile()
        }
        icon.setupFromSvg(file.getIcon())
        label.text = file.name
        tooltip.text = file.absolutePath.replace(getUserHome(), "~")
        root.content = fileEditorFxml.root

        root.setOnSelectionChanged {
            if (root.isSelected) {
                fileCell.open(it)
            } else {
                fileCell.displayAsClosed()
            }
        }
    }

    @FXML
    private fun open(event: ActionEvent) {
        parent.selectionModel.select(this.root)
    }

    @FXML
    private fun close(event: ActionEvent) {
        parent.tabs.remove(root)
    }

    @FXML
    private fun openInFileBrowser(event: ActionEvent) {
        SystemManager.get().openInFileBrowser(fileCell.file).begin()
    }
}
