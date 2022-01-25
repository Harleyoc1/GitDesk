package com.harleyoconnor.gitdesk.ui.repository.editor

import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.util.getIcon
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
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
class FileTabController : ViewController<FileTabController.Context> {

    object Loader: ResourceViewLoader<Context, FileTabController, Tab>(
        UIResource("/ui/layouts/repository/editor/FileTab.fxml")
    )

    class Context(val parent: TabPane, val fileCell: FileCellController<*>): ViewController.Context

    private lateinit var parent: TabPane
    private lateinit var fileCell: FileCellController<*>

    @FXML
    private lateinit var root: FileTab

    @FXML
    private lateinit var tooltip: Tooltip

    @FXML
    private lateinit var icon: SVGIcon

    @FXML
    private lateinit var label: Label

    override fun setup(context: Context) {
        this.parent = context.parent
        this.fileCell = context.fileCell

        val file = fileCell.file
        val fileEditorFxml = FileEditorController.Loader.load(FileEditorController.Context(file))
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
