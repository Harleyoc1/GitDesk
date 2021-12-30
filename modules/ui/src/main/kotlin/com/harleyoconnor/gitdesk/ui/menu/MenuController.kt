package com.harleyoconnor.gitdesk.ui.menu

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.menu.clone.CloneTab
import com.harleyoconnor.gitdesk.ui.menu.create.CreateTabController
import com.harleyoconnor.gitdesk.ui.menu.open.OpenTabController
import com.harleyoconnor.gitdesk.ui.menubar.EditMenu
import com.harleyoconnor.gitdesk.ui.menubar.FileMenu
import com.harleyoconnor.gitdesk.ui.menubar.ViewMenu
import com.harleyoconnor.gitdesk.ui.menubar.WindowMenu
import com.harleyoconnor.gitdesk.ui.repository.RepositoryWindow
import com.harleyoconnor.gitdesk.ui.util.Tab
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.ui.util.setOnSelected
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Menu
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class MenuController {

    companion object {
        fun load(parent: MenuWindow): com.harleyoconnor.gitdesk.ui.util.LoadedFXML<BorderPane, MenuController> {
            val fxml = load<BorderPane, MenuController>("menu/Root")
            fxml.controller.setup(parent)
            return fxml
        }
    }

    private val openTabFxml by lazy { OpenTabController.load(this) }

    @FXML
    private lateinit var openRecentItem: Menu

    @FXML
    private lateinit var fileMenu: FileMenu

    @FXML
    private lateinit var editMenu: EditMenu

    @FXML
    private lateinit var viewMenu: ViewMenu

    @FXML
    private lateinit var windowMenu: WindowMenu

    private val openTab: Tab by lazy {
        Tab(openTabFxml.root) {
            root.center = it
        }
    }

    private val cloneTab: Tab by lazy {
        CloneTab(parent) {
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

    fun setup(parent: MenuWindow) {
        this.parent = parent
        this.fileMenu.setWindow(parent)
        this.editMenu.useSelectableAccess(parent.selectableAccess)
        this.viewMenu.setStage(parent.stage)
        this.windowMenu.setStage(parent.stage)
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

    fun selectCloneTab() {
        cloneTabButton.fire()
    }

    fun selectCreateTab() {
        createTabButton.fire()
    }

    fun selectImportLocal() {
        openTabFxml.controller.selectLocalRepository()
    }

    @FXML
    private fun closeWindow(event: ActionEvent) {
        parent.close()
    }

    fun openRepository(repository: LocalRepository) {
        RepositoryWindow.focusOrOpen(repository)
        this.parent.close()
    }

}
