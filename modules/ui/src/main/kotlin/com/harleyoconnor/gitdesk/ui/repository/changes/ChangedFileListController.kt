package com.harleyoconnor.gitdesk.ui.repository.changes

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.util.stream
import com.harleyoconnor.gitdesk.util.toTypedArray
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import java.io.File

/**
 * @author Harley O'Connor
 */
class ChangedFileListController {

    companion object {
        fun load(repository: LocalRepository, parent: ChangesTabController): VBox {
            val fxml = load<VBox, ChangedFileListController>("repository/changes/ChangedFileList")
            fxml.controller.setup(repository, parent)
            return fxml.root
        }
    }

    lateinit var repository: LocalRepository
    private lateinit var parent: ChangesTabController

    @FXML
    private lateinit var root: VBox

    private fun setup(repository: LocalRepository, parent: ChangesTabController) {
        this.repository = repository
        this.parent = parent
        startRefresh()
    }

    private fun startRefresh() = this.repository.gitRepository.getChangedFiles()
        .ifSuccessful { response ->
            Platform.runLater {
                root.children.clear()
                root.children.addAll(buildCells(response.result!!))
            }
        }
        .begin()

    private fun buildCells(changedFiles: Array<File>): Array<out Node> = changedFiles.stream()
        .map { FileCellController.load(it, this) }
        .toTypedArray()

}