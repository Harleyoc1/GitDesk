package com.harleyoconnor.gitdesk.ui.menu

import com.harleyoconnor.gitdesk.ui.util.loadLayout
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class MenuController {

    private val openExistingTab: VBox by lazy {
        loadLayout("menu/OpenExistingTab")
    }

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
        root.center = this.openExistingTab
    }

}