package com.harleyoconnor.gitdesk.ui.menubar

import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Menu
import javafx.stage.Stage

/**
 *
 * @author Harley O'Connor
 */
class ViewMenu : Menu() {

    private lateinit var stage: Stage

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
    }

    @FXML
    private fun enterFullScreen() {
        stage.isFullScreen = !stage.isFullScreen
    }


}