package com.harleyoconnor.gitdesk.ui.repository

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.forFirst
import com.harleyoconnor.gitdesk.util.forSecond
import com.harleyoconnor.gitdesk.util.sort
import com.harleyoconnor.gitdesk.util.split
import com.harleyoconnor.gitdesk.util.stream
import com.harleyoconnor.gitdesk.util.toTypedArray
import com.harleyoconnor.gitdesk.util.tree.traversal.PreOrderTraverser
import javafx.fxml.FXML
import javafx.scene.layout.VBox
import java.io.File

/**
 * @author Harley O'Connor
 */
class FileListController {

    companion object {
        fun load(repository: LocalRepository, parent: RepositoryController): VBox {
            val fxml = load<VBox, FileListController>("repository/FileList")
            fxml.controller.setup(repository, parent)
            return fxml.root
        }
    }

    private lateinit var repository: LocalRepository

    private lateinit var parent: RepositoryController

    @FXML
    private lateinit var root: VBox

    var selected: FileCellController? = null
        set(value) {
            selected?.deselect()
            field = value
        }
    var open: FileCellController? = null
        set(value) {
            open?.close()
            field = value
            if (value?.file?.isDirectory == false) {
                parent.setFileInEditor(value.file)
            }
        }

    private val directoryCells: MutableMap<Directory, DirectoryCellController> = mutableMapOf()

    private fun setup(repository: LocalRepository, parent: RepositoryController) {
        this.repository = repository
        this.parent = parent
        appendCells(repository.directory, root)
        showLastOpenDirectories()
    }

    private fun showLastOpenDirectories() {
        repository.openDirectories.traverse(PreOrderTraverser({ directory ->
            directoryCells[directory]?.setOpen()
        }))
    }

    fun appendCells(directory: Directory, box: VBox, insetIndex: Int = 0) {
        directory.stream()
            .filter { this.shouldShowFile(it) }
            .split { it.isDirectory }
            .forFirst { directories ->
                appendDirectoryCells(
                    box, sort(directories.map { Directory(it) }.toTypedArray()), insetIndex
                )
            }
            .forSecond { files ->
                appendFileCells(box, sort(files.toTypedArray()), insetIndex)
            }
    }

    private fun appendDirectoryCells(box: VBox, directories: Array<Directory>, insetIndex: Int) {
        directories.forEach {
            box.children.add(this.createDirectoryCell(it, insetIndex))
        }
    }

    private fun createDirectoryCell(directory: Directory, insetIndex: Int): VBox {
        val cell = DirectoryCellController.load(directory, insetIndex, this)
        directoryCells[directory] = cell.controller
        return cell.root
    }

    private fun appendFileCells(box: VBox, files: Array<File>, insetIndex: Int) {
        files.forEach {
            box.children.add(this.createFileCell(it, insetIndex))
        }
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