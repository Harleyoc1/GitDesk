package com.harleyoconnor.gitdesk.ui.menu

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.menu.clone.CloneTab
import com.harleyoconnor.gitdesk.ui.menu.create.CreateTabController
import com.harleyoconnor.gitdesk.ui.menu.open.OpenTabController
import com.harleyoconnor.gitdesk.ui.menubar.EditMenu
import com.harleyoconnor.gitdesk.ui.menubar.FileMenu
import com.harleyoconnor.gitdesk.ui.menubar.ViewMenu
import com.harleyoconnor.gitdesk.ui.menubar.WindowMenu
import com.harleyoconnor.gitdesk.ui.repository.RepositoryWindow
import com.harleyoconnor.gitdesk.ui.util.Tab
import com.harleyoconnor.gitdesk.ui.util.setOnSelected
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
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
class MenuController : ViewController<MenuController.Context> {

    object Loader: ResourceViewLoader<Context, MenuController, BorderPane>(
        UIResource("/ui/layouts/menu/Root.fxml")
    )

    class Context(val parent: MenuWindow): ViewController.Context

    private val openTabFxml by lazy { OpenTabController.Loader.load(OpenTabController.Context(this)) }

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
        Tab(CreateTabController.Loader.load(CreateTabController.Context(this)).root) {
            root.center = it
        }
    }

    private lateinit var parent: MenuWindow

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

    override fun setup(context: Context) {
        this.parent = context.parent
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
