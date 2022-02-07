package com.harleyoconnor.gitdesk.ui.menubar

import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.account.AccountWindow
import com.harleyoconnor.gitdesk.ui.settings.SettingsWindow
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Menu
import javafx.stage.Stage

/**
 *
 * @author Harley O'Connor
 */
class WindowMenu : Menu() {

    private lateinit var stage: Stage

    init {
        val loader = FXMLLoader(
            WindowMenu::class.java.getResource("/ui/nodes/WindowMenu.fxml"),
            TRANSLATIONS_BUNDLE
        )
        loader.setRoot(this)
        loader.setController(this)
        loader.load<Menu>()
    }

    fun setStage(stage: Stage) {
        this.stage = stage
    }

    @FXML
    private fun minimise() {
        stage.isIconified = true
    }

    @FXML
    private fun openSettingsWindow() {
        val window = (Application.getInstance().windowManager.get("Settings") ?: SettingsWindow())
        window.open()
        window.focus() // Focus (bring to front) in case window was already open.
    }

    @FXML
    private fun openAccountWindow() {
        val window = (Application.getInstance().windowManager.get("Account") ?: AccountWindow())
        window.open()
        window.focus() // Focus (bring to front) in case window was already open.
    }

}