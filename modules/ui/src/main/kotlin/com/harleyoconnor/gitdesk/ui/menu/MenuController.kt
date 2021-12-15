package com.harleyoconnor.gitdesk.ui.menu

import com.harleyoconnor.gitdesk.ui.menu.clone.SelectRemoteTabController
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.ui.util.loadLayout
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
        fun load(stage: Stage): BorderPane {
            val fxml = load<BorderPane, MenuController>("menu/Root")
            fxml.controller.stage = stage
            return fxml.root
        }
    }

    private val openTab: Tab by lazy {
        Tab(loadLayout("menu/tabs/open/Root")) {
            root.center = it
        }
    }

    val cloneTab: Tab by lazy {
        Tab(SelectRemoteTabController.load(this)) {
            root.center = it
        }
    }

    lateinit var stage: Stage

    @FXML
    private lateinit var root: BorderPane

    @FXML
    private lateinit var openExistingButton: RadioButton

    @FXML
    private lateinit var navigationGroup: ToggleGroup

    @FXML
    fun initialize() {
        openExistingButton.fire()
    }

    @FXML
    fun openOpenExistingTab(event: ActionEvent) {
        root.center = this.openTab.node
    }

    @FXML
    fun openCloneExistingTab(event: ActionEvent) {
        root.center = this.cloneTab.node
    }

    class Tab(
        node: Node,
        private val appender: (Node) -> Unit
    ) {
        var node: Node = node
            set(value) {
                field = value
                appender(value)
            }
    }

}