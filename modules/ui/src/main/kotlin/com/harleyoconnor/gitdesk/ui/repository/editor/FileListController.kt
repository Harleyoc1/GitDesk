package com.harleyoconnor.gitdesk.ui.repository.editor

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.repository.RepositoryController
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.forFirst
import com.harleyoconnor.gitdesk.util.forSecond
import com.harleyoconnor.gitdesk.util.sort
import com.harleyoconnor.gitdesk.util.splitToPair
import com.harleyoconnor.gitdesk.util.stream
import com.harleyoconnor.gitdesk.util.toTypedArray
import com.harleyoconnor.gitdesk.util.tree.traversal.PreOrderTraverser
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.layout.VBox
import java.io.File

/**
 * @author Harley O'Connor
 */
class FileListController {

    companion object {
        fun load(repository: LocalRepository, parent: EditorTabController): VBox {
            val fxml = load<VBox, FileListController>("repository/editor/FileList")
            fxml.controller.setup(repository, parent)
            return fxml.root
        }
    }

    private lateinit var repository: LocalRepository

    private lateinit var parent: EditorTabController

    @FXML
    private lateinit var root: VBox

    var selected: FileCellController? = null
        set(value) {
            selected?.deselect()
            field = value
        }
    var open: FileCellController? = null
        set(value) {
            open?.displayAsClosed()
            field = value
            if (value?.file?.isDirectory == false) {
                parent.open(value)
            }
        }

    private val directoryCells: MutableMap<Directory, DirectoryCellController> = mutableMapOf()

    private fun setup(repository: LocalRepository, parent: EditorTabController) {
        this.repository = repository
        this.parent = parent
        root.children.addAll(buildCells(repository.directory))
        showLastOpenDirectories()
    }

    private fun showLastOpenDirectories() {
        repository.openDirectories.traverse(PreOrderTraverser({ directory ->
            directoryCells[directory]?.setOpen()
        }))
    }

    fun buildCells(directory: Directory, insetIndex: Int = 0): Array<Node> {
        val cells: MutableList<Node> = mutableListOf()
        directory.stream()
            .filter { this.shouldShowFile(it) }
            .splitToPair { it.isDirectory }
            .forFirst { directories ->
                cells.addAll(
                    buildDirectoryCells(sort(directories.map { Directory(it) }.toTypedArray()), insetIndex)
                )
            }
            .forSecond { files ->
                cells.addAll(
                    buildFileCells(sort(files.toTypedArray()), insetIndex)
                )
            }
        return cells.toTypedArray()
    }

    private fun buildDirectoryCells(directories: Array<Directory>, insetIndex: Int): Array<Node> {
        return directories.map {
            this.createDirectoryCell(it, insetIndex)
        }.toTypedArray()
    }

    private fun createDirectoryCell(directory: Directory, insetIndex: Int): VBox {
        val cell = DirectoryCellController.load(directory, insetIndex, this)
        directoryCells[directory] = cell.controller
        return cell.root
    }

    private fun buildFileCells(files: Array<File>, insetIndex: Int): Array<Node> {
        return files.map {
            this.createFileCell(it, insetIndex)
        }.toTypedArray()
    }

    private fun createFileCell(file: File, insetIndex: Int) = FileCellController.load(file, insetIndex, this)

    private fun shouldShowFile(file: File): Boolean = !file.name.startsWith(".")

    fun onDirectoryOpened(directory: Directory) {
        val parent = Directory(directory.parentFile)
        this.repository.openDirectories.addLeaf(parent, directory)
    }

    fun onDirectoryClosed(directory: Directory) {
        this.repository.openDirectories.remove(directory)
    }

}