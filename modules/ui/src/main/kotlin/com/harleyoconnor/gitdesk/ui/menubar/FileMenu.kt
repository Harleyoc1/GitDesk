package com.harleyoconnor.gitdesk.ui.menubar

import com.harleyoconnor.gitdesk.ui.menu.MenuWindow
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.window.Window
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Menu

/**
 *
 * @author Harley O'Connor
 */
class FileMenu : Menu() {

    private lateinit var window: Window

    init {
        val loader = FXMLLoader(
            FileMenu::class.java.getResource("/ui/nodes/FileMenu.fxml"),
            TRANSLATIONS_BUNDLE
        )
        loader.setRoot(this)
        loader.setController(this)
        loader.load<Menu>()
    }

    fun setWindow(window: Window) {
        this.window = window
    }

    @FXML
    private fun selectCreateTab(event: ActionEvent) {
        MenuWindow.showOrFocus().selectCreateTab()
    }

    @FXML
    private fun importLocal(event: ActionEvent) {
        MenuWindow.showOrFocus().selectImportLocal()
    }

    @FXML
    private fun selectCloneTab(event: ActionEvent) {
        MenuWindow.showOrFocus().selectCloneTab()
    }

    @FXML
    private fun closeWindow(event: ActionEvent) {
        window.close()
    }

}