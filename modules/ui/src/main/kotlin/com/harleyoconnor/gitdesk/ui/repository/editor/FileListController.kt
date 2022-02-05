package com.harleyoconnor.gitdesk.ui.repository.editor

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.forFirst
import com.harleyoconnor.gitdesk.util.forSecond
import com.harleyoconnor.gitdesk.util.sortComparable
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
class FileListController : ViewController<FileListController.Context> {

    object Loader: ResourceViewLoader<Context, FileListController, VBox>(
        UIResource("/ui/layouts/repository/editor/FileList.fxml")
    )

    class Context(val parent: EditorTabController, val repository: LocalRepository): ViewController.Context

    private lateinit var parent: EditorTabController
    private lateinit var repository: LocalRepository

    @FXML
    private lateinit var root: VBox

    var selected: FileCellController<*>? = null
        set(value) {
            selected?.deselect()
            field = value
        }
    var open: FileCellController<*>? = null
        set(value) {
            open?.displayAsClosed()
            field = value
            if (value?.file?.isDirectory == false) {
                parent.open(value)
            }
        }

    private val directoryCells: MutableMap<Directory, DirectoryCellController> = mutableMapOf()

    override fun setup(context: Context) {
        this.parent = context.parent
        this.repository = context.repository
        loadRootCells()
        showLastOpenDirectories()
    }

    private fun loadRootCells() {
        root.children.clear()
        root.children.addAll(buildCells(repository.directory))
    }

    fun showLastOpenDirectories() {
        repository.openDirectories.traverse(PreOrderTraverser.traverse { directory ->
            directoryCells[directory]?.setOpen()
        })
    }

    fun buildCells(directory: Directory, insetIndex: Int = 0): Array<Node> {
        val cells: MutableList<Node> = mutableListOf()
        directory.stream()
            .filter { this.shouldShowFile(it) }
            .splitToPair { it.isDirectory }
            .forFirst { directories ->
                cells.addAll(
                    buildDirectoryCells(sortComparable(directories.map { Directory(it) }.toTypedArray()), insetIndex)
                )
            }
            .forSecond { files ->
                cells.addAll(
                    buildFileCells(sortComparable(files.toTypedArray()), insetIndex)
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
        val cell = DirectoryCellController.Loader.load(
            DirectoryCellController.Context(this, directory, insetIndex)
        )
        directoryCells[directory] = cell.controller
        return cell.root
    }

    private fun buildFileCells(files: Array<File>, insetIndex: Int): Array<Node> {
        return files.map {
            this.createFileCell(it, insetIndex)
        }.toTypedArray()
    }

    private fun createFileCell(file: File, insetIndex: Int) = FileCellController.Loader.load(
        FileCellController.Context(this, file, insetIndex)
    ).root

    private fun shouldShowFile(file: File): Boolean = !file.name.startsWith(".")

    fun onDirectoryOpened(directory: Directory) {
        val parent = Directory(directory.parentFile)
        this.repository.openDirectories.addLeaf(parent, directory)
    }

    fun onDirectoryClosed(directory: Directory) {
        this.repository.openDirectories.remove(directory)
    }

    fun onDirectoryDeleted(directory: Directory) {
        val parent = Directory(directory.parentFile)
        directoryCells[parent]?.reloadCells() ?: run {
            loadRootCells()
        }
        this.repository.openDirectories.remove(directory)
        showLastOpenDirectories()
    }

    fun onFileDeleted(file: File) {
        val parent = Directory(file.parentFile)
        directoryCells[parent]?.reloadCells() ?: run {
            loadRootCells()
        }
        showLastOpenDirectories()
    }

    fun reloadCellsFor(directory: Directory) {
        directoryCells[directory]?.reloadCells() ?: run {
            loadRootCells()
        }
        showLastOpenDirectories()
    }

}