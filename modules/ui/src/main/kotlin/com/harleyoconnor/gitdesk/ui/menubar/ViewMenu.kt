package com.harleyoconnor.gitdesk.ui.menubar

import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.stage.Stage

/**
 *
 * @author Harley O'Connor
 */
class ViewMenu : Menu() {

    private lateinit var stage: Stage

    @FXML
    private lateinit var toggleFullScreenItem: MenuItem

    init {
        val loader = FXMLLoader(
            ViewMenu::class.java.getResource("/ui/nodes/ViewMenu.fxml"),
            TRANSLATIONS_BUNDLE
        )
        loader.setRoot(this)
        loader.setController(this)
        loader.load<Menu>()
    }

    fun setStage(stage: Stage) {
        this.stage = stage

        updateToggleFullScreenText(stage.isFullScreen)
        this.stage.fullScreenProperty().addListener { _, _, new ->
            updateToggleFullScreenText(new)
        }
    }

    private fun updateToggleFullScreenText(fullScreen: Boolean) {
        toggleFullScreenItem.text = TRANSLATIONS_BUNDLE.getString(
            "ui.menu.view.toggle_full_screen." + if (fullScreen) "disable" else "enable"
        )
    }

    @FXML
    private fun toggleFullScreen() {
        stage.isFullScreen = !stage.isFullScreen
    }

}