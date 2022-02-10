package com.harleyoconnor.gitdesk.ui.repository.editor

import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.util.Directory
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.ButtonType
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class DirectoryCellController : FileCellController<DirectoryCellController.Context>() {

    object Loader : ResourceViewLoader<Context, DirectoryCellController, VBox>(
        UIResource("/ui/layouts/repository/editor/DirectoryCell.fxml")
    )

    class Context(parent: FileListController, directory: Directory, insetIndex: Int) : FileCellController.Context(
        parent, directory, insetIndex
    )

    private lateinit var directory: Directory

    private var insetIndex: Int = 0

    private var open: Boolean = false

    @FXML
    private lateinit var root: VBox

    @FXML
    private lateinit var expandIcon: SVGIcon

    private lateinit var cells: Array<Node>

    override fun setup(context: Context) {
        assert(context.file is Directory)
        directory = Directory(context.file)
        this.insetIndex = context.insetIndex
        this.cells = context.parent.buildCells(directory, insetIndex + 1)
        super.setup(context)
    }

    override fun setupOpenInExternalEditorMenuItem() {
        // Do nothing, as you shouldn't be able to open a folder in an editor.
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
        if (open) {
            return
        }
        root.children.addAll(cells)
        expandIcon.rotate = 90.0
        open = true
    }

    private fun closeDirectory() {
        if (!open) {
            return
        }
        root.children.remove(1, root.children.size)
        expandIcon.rotate = 0.0
        open = false
        parent.onDirectoryClosed(directory)
    }

    @FXML
    private fun createNewFile(event: ActionEvent) {
        CreateFileWindow(directory) {
            reloadCells()
        }.open()
    }

    @FXML
    private fun createNewDirectory(event: ActionEvent) {
        CreateDirectoryWindow(directory) {
            reloadCells()
        }.open()
    }

    fun reloadCells() {
        cells = parent.buildCells(directory, insetIndex + 1)
        if (open) {
            root.children.remove(1, root.children.size)
            root.children.addAll(cells)
            parent.showLastOpenDirectories()
        }
    }

    @FXML
    private fun delete(event: ActionEvent) {
        createDeleteFileDialogue()
            .showAndWait()
            .filter(ButtonType.OK::equals)
            .ifPresent {
                directory.deleteRecursively()
                parent.onDirectoryDeleted(directory)
            }
    }

}