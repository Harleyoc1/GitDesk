package com.harleyoconnor.gitdesk.ui.repository

import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.util.Directory
import javafx.event.Event
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.layout.VBox
import java.io.File

/**
 * @author Harley O'Connor
 */
class DirectoryCellController : FileCellController() {

    companion object {
        fun load(
            directory: Directory,
            insetIndex: Int,
            parent: FileListController
        ): com.harleyoconnor.gitdesk.ui.util.FXML<VBox, DirectoryCellController> {
            val fxml = load<VBox, DirectoryCellController>("repository/DirectoryCell")
            fxml.controller.setup(directory, insetIndex, parent)
            return fxml
        }
    }

    private lateinit var directory: Directory

    private var insetIndex: Int = 0

    private var open: Boolean = false

    @FXML
    private lateinit var root: VBox

    @FXML
    private lateinit var expandIcon: SVGIcon

    private lateinit var cells: Array<Node>

    override fun setup(file: File, insetIndex: Int, parent: FileListController) {
        assert(file is Directory)
        directory = Directory(file)
        this.insetIndex = insetIndex
        this.cells = parent.buildCells(directory, insetIndex + 1)
        super.setup(file, insetIndex, parent)
    }

    override fun getPadding(insetIndex: Int): Insets =
        Insets(2.0, 8.0, 2.0, 8.0 + insetIndex * 15.0)

    override fun open(event: Event) {
        if (!open) {
            openDirectory()
        } else {
            closeDirectory()
        }
        parent.selected = null
    }

    @FXML
    private fun expandPressed(event: Event) {
        toggleDirectoryOpen()
        event.consume() // Consume event so current selection is not interrupted.
    }

    private fun toggleDirectoryOpen() {
        if (!open) {
            openDirectory()
        } else {
            closeDirectory()
        }
    }

    private fun openDirectory() {
        setOpen()
        parent.onDirectoryOpened(directory)
    }

    fun setOpen() {
        root.children.addAll(cells)
        expandIcon.rotate = 90.0
        open = true
    }

    private fun closeDirectory() {
        root.children.remove(1, root.children.size)
        expandIcon.rotate = 0.0
        open = false
        parent.onDirectoryClosed(directory)
    }
}