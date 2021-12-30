package com.harleyoconnor.gitdesk.ui.repository

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.menubar.EditMenu
import com.harleyoconnor.gitdesk.ui.menubar.FileMenu
import com.harleyoconnor.gitdesk.ui.menubar.ViewMenu
import com.harleyoconnor.gitdesk.ui.menubar.WindowMenu
import com.harleyoconnor.gitdesk.ui.repository.changes.ChangesTabController
import com.harleyoconnor.gitdesk.ui.repository.editor.EditorTabController
import com.harleyoconnor.gitdesk.ui.util.Tab
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.ui.util.setOnSelected
import com.harleyoconnor.gitdesk.ui.window.Window
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.RadioButton
import javafx.scene.layout.BorderPane

/**
 * @author Harley O'Connor
 */
class RepositoryController {

    companion object {
        fun load(parent: Window, repository: LocalRepository): BorderPane {
            val fxml = load<BorderPane, RepositoryController>("repository/Root")
            fxml.controller.setup(parent, repository)
            return fxml.root
        }
    }

    private lateinit var parent: Window
    private lateinit var repository: LocalRepository

    @FXML
    private lateinit var root: BorderPane

    @FXML
    private lateinit var fileMenu: FileMenu

    @FXML
    private lateinit var editMenu: EditMenu

    @FXML
    private lateinit var viewMenu: ViewMenu

    @FXML
    private lateinit var windowMenu: WindowMenu

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

    private val editorTabFxml by lazy { EditorTabController.load(parent.stage, repository) }

    private val editorTab: Tab by lazy {
        Tab(editorTabFxml.root) {
            root.center = it
        }
    }

    private val changesTab: Tab by lazy {
        Tab(ChangesTabController.load(parent.stage, repository)) {
            root.center = it
        }
    }

    fun setup(parent: Window, repository: LocalRepository) {
        this.parent = parent
        this.repository = repository

        fileMenu.setWindow(parent)
        editMenu.useSelectableAccess(parent.selectableAccess)
        viewMenu.setStage(parent.stage)
        windowMenu.setStage(parent.stage)

        editorTabButton.setOnSelected {
            editorTab.open()
        }
        changesTabButton.setOnSelected {
            changesTab.open()
        }
        editorTabButton.fire()
    }

    @FXML
    private fun save(event: ActionEvent) {
        if (editorTabButton.isSelected) {
            editorTabFxml.controller.save()
        }
    }
}