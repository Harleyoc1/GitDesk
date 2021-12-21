package com.harleyoconnor.gitdesk.ui.repository

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.util.load
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.io.File

/**
 * @author Harley O'Connor
 */
class RepositoryController {

    companion object {
        fun load(stage: Stage, repository: LocalRepository): BorderPane {
            val fxml = load<BorderPane, RepositoryController>("repository/Root")
            fxml.controller.setup(stage, repository)
            return fxml.root
        }
    }

    private lateinit var stage: Stage

    private lateinit var repository: LocalRepository

    @FXML
    private lateinit var root: BorderPane

    @FXML
    private lateinit var centre: SplitPane

    @FXML
    private lateinit var fileList: ScrollPane

    private val fileEditorTabPane = load<Node, FileEditorTabPaneController>("repository/FileEditorRoot")

    fun setup(stage: Stage, repository: LocalRepository) {
        this.stage = stage
        this.repository = repository
        fileList.content = FileListController.load(repository, this)
    }

    fun setFileInEditor(fileCell: FileCellController) {
        if (centre.items[1] is VBox) {
            centre.items[1] = fileEditorTabPane.root
        }
        fileEditorTabPane.controller.open(fileCell)
    }

}