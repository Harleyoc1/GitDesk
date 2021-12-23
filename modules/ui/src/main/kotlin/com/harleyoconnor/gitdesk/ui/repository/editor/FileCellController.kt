package com.harleyoconnor.gitdesk.ui.repository.editor

import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.style.OPEN_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.style.SELECTED_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.util.getIcon
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.util.system.SystemManager
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import java.io.File

/**
 * @author Harley O'Connor
 */
open class FileCellController {

    companion object {
        fun load(file: File, insetIndex: Int, parent: FileListController): HBox {
            val fxml = load<HBox, FileCellController>("repository/editor/FileCell")
            fxml.controller.setup(file, insetIndex, parent)
            return fxml.root
        }
    }

    lateinit var file: File

    protected lateinit var parent: FileListController

    @FXML
    private lateinit var cell: HBox

    @FXML
    private lateinit var contextMenu: ContextMenu

    @FXML
    private lateinit var icon: SVGIcon

    @FXML
    private lateinit var nameLabel: Label

    internal open fun setup(file: File, insetIndex: Int, parent: FileListController) {
        this.file = file
        this.parent = parent
        cell.padding = getPadding(insetIndex)
        icon.setupFromSvg(file.getIcon())
        nameLabel.text = file.name
    }

    protected open fun getPadding(insetIndex: Int): Insets =
        Insets(2.0, 8.0, 2.0, 23.0 + insetIndex * 15.0)

    @FXML
    private fun select(event: MouseEvent) {
        if (event.button == MouseButton.SECONDARY || event.isControlDown) {
            contextMenu.show(cell, event.screenX, event.screenY)
        } else if (cell.pseudoClassStates.contains(SELECTED_PSEUDO_CLASS)) {
            open(event)
            return
        }
        parent.selected = this
        cell.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, true)
    }

    fun deselect() {
        cell.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, false)
    }

    open fun open(event: Event) {
        parent.open = this
        cell.pseudoClassStateChanged(OPEN_PSEUDO_CLASS, true)
    }

    fun displayAsClosed() {
        cell.pseudoClassStateChanged(OPEN_PSEUDO_CLASS, false)
    }

    @FXML
    private fun openInFileBrowser(event: ActionEvent) {
        SystemManager.get().openInFileBrowser(file).begin()
    }

}