package com.harleyoconnor.gitdesk.ui.repository.changes

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.process.logFailure
import com.harleyoconnor.gitdesk.util.stream
import com.harleyoconnor.gitdesk.util.toTypedArray
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.layout.VBox
import java.io.File

/**
 * @author Harley O'Connor
 */
class ChangedFileListController : ViewController<ChangedFileListController.Context> {

    object Loader: ResourceViewLoader<Context, ChangedFileListController, VBox>(
        UIResource("/ui/layouts/repository/changes/ChangedFileList.fxml")
    )

    class Context(val parent: ChangesTabController, val repository: LocalRepository): ViewController.Context

    private lateinit var parent: ChangesTabController
    lateinit var repository: LocalRepository

    @FXML
    private lateinit var root: VBox

    override fun setup(context: Context) {
        this.repository = context.repository
        this.parent = context.parent
        refresh()
    }

    fun refresh() = this.repository.gitRepository.getChangedFiles()
        .ifSuccessful { response ->
            Platform.runLater {
                root.children.clear()
                root.children.addAll(buildCells(response.result!!))
            }
        }
        .ifFailure(::logFailure)
        .begin()

    private fun buildCells(changedFiles: Array<File>): Array<out Node> = changedFiles.stream()
        .map { FileCellController.Loader.load(FileCellController.Context(this.repository.gitRepository, it)).root }
        .toTypedArray()

}