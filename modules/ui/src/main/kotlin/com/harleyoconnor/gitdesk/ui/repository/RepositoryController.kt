package com.harleyoconnor.gitdesk.ui.repository

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.repository.changes.ChangesTabController
import com.harleyoconnor.gitdesk.ui.repository.editor.EditorTabController
import com.harleyoconnor.gitdesk.ui.util.Tab
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.ui.util.setOnSelected
import javafx.fxml.FXML
import javafx.scene.control.RadioButton
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

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
    private lateinit var editorTabButton: RadioButton

    @FXML
    private lateinit var changesTabButton: RadioButton

    @FXML
    private lateinit var issuesTabButton: RadioButton

    @FXML
    private lateinit var pullRequestsTabButton: RadioButton

    @FXML
    private lateinit var checklistsTabButton: RadioButton

    private val editorTab: Tab by lazy {
        Tab(EditorTabController.load(stage, repository)) {
            root.center = it
        }
    }

    private val changesTab: Tab by lazy {
        Tab(ChangesTabController.load(stage, repository)) {
            root.center = it
        }
    }

    fun setup(stage: Stage, repository: LocalRepository) {
        this.stage = stage
        this.repository = repository

        editorTabButton.setOnSelected {
            editorTab.open()
        }
        changesTabButton.setOnSelected {
            changesTab.open()
        }
        editorTabButton.fire()
    }
}