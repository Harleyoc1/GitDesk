package com.harleyoconnor.gitdesk.ui.menu

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.menu.clone.CloneTab
import com.harleyoconnor.gitdesk.ui.menu.create.CreateTabController
import com.harleyoconnor.gitdesk.ui.menu.open.OpenTabController
import com.harleyoconnor.gitdesk.ui.repository.RepositoryWindow
import com.harleyoconnor.gitdesk.ui.util.load
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
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
    private lateinit var openExistingButton: RadioButton

    @FXML
    private lateinit var navigationGroup: ToggleGroup

    @FXML
    private fun initialize() {
        openExistingButton.fire()
    }

    @FXML
    private fun openOpenExistingTab(event: ActionEvent) {
        root.center = this.openTab.node
    }

    @FXML
    private fun openCloneExistingTab(event: ActionEvent) {
        root.center = this.cloneTab.node
    }

    @FXML
    private fun openCreateTab(event: ActionEvent) {
        root.center = this.createTab.node
    }

    fun openRepository(repository: LocalRepository) {
        RepositoryWindow.focusOrOpen(repository)
        this.parent.close()
    }

    open class Tab(
        node: Node,
        protected val appender: (Node) -> Unit
    ) {
        var node: Node = node
            protected set(value) {
                field = value
                appender(value)
            }
    }

}