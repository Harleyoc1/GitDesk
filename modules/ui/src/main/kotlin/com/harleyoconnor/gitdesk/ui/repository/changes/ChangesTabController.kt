package com.harleyoconnor.gitdesk.ui.repository.changes

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.util.load
import javafx.fxml.FXML
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.control.TabPane
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class ChangesTabController {

    companion object {
        fun load(stage: Stage, repository: LocalRepository): SplitPane {
            val fxml = load<SplitPane, ChangesTabController>("repository/changes/Root")
            fxml.controller.setup(stage, repository)
            return fxml.root
        }
    }

    private lateinit var stage: Stage
    private lateinit var repository: LocalRepository

    @FXML
    private lateinit var fileList: ScrollPane

    @FXML
    private lateinit var commitArea: ScrollPane

    @FXML
    private lateinit var fileTabs: TabPane

    @FXML
    private fun initialize() {
        fileTabs.tabDragPolicy = TabPane.TabDragPolicy.REORDER
    }

    fun setup(stage: Stage, repository: LocalRepository) {
        this.stage = stage
        this.repository = repository
        fileList.content = ChangedFileListController.load(repository, this)
    }


}