package com.harleyoconnor.gitdesk.ui.menu

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.menu.clone.CloneTab
import com.harleyoconnor.gitdesk.ui.menu.create.CreateTabController
import com.harleyoconnor.gitdesk.ui.menu.open.OpenTabController
import com.harleyoconnor.gitdesk.ui.repository.RepositoryWindow
import com.harleyoconnor.gitdesk.ui.util.Tab
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.ui.util.setOnSelected
import javafx.fxml.FXML
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class MenuController {

    companion object {
        fun load(parent: MenuWindow): BorderPane {
            val fxml = load<BorderPane, MenuController>("menu/Root")
            fxml.controller.parent = parent
            return fxml.root
        }
    }

    private val openTab: Tab by lazy {
        Tab(OpenTabController.load(this)) {
            root.center = it
        }
    }

    val cloneTab: Tab by lazy {
        CloneTab(stage) {
            root.center = it
        }
    }

    private val createTab: Tab by lazy {
        Tab(CreateTabController.load(this)) {
            root.center = it
        }
    }

    lateinit var parent: MenuWindow

    val stage: Stage get() = parent.stage

    @FXML
    private lateinit var root: BorderPane

    @FXML
    private lateinit var navigationGroup: ToggleGroup

    @FXML
    private lateinit var openTabButton: RadioButton

    @FXML
    private lateinit var cloneTabButton: RadioButton

    @FXML
    private lateinit var createTabButton: RadioButton

    @FXML
    private fun initialize() {
        openTabButton.setOnSelected {
            openOpenTab()
        }
        cloneTabButton.setOnSelected {
            openCloneTab()
        }
        createTabButton.setOnSelected {
            openCreateTab()
        }
        openTabButton.fire()
    }

    private fun openOpenTab() {
        root.center = this.openTab.node
    }

    private fun openCloneTab() {
        root.center = this.cloneTab.node
    }

    private fun openCreateTab() {
        root.center = this.createTab.node
    }

    fun openRepository(repository: LocalRepository) {
        RepositoryWindow.focusOrOpen(repository)
        this.parent.close()
    }

}