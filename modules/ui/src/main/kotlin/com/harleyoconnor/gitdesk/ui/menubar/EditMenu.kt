package com.harleyoconnor.gitdesk.ui.menubar

import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Menu

/**
 *
 * @author Harley O'Connor
 */
class EditMenu : Menu() {

    private lateinit var behaviour: EditMenuBehaviour

    init {
        val loader = FXMLLoader(
            EditMenu::class.java.getResource("/ui/nodes/EditMenu.fxml"),
            TRANSLATIONS_BUNDLE
        )
        loader.setRoot(this)
        loader.setController(this)
        loader.load<Menu>()
    }

    fun useSelectableAccess(selectableAccess: SelectableAccess) {
        this.behaviour = EditMenuBehaviour(selectableAccess)
    }

    @FXML
    private fun undo(event: ActionEvent) {

    }

    @FXML
    private fun redo(event: ActionEvent) {

    }

    @FXML
    private fun cut(event: ActionEvent) {
        behaviour.cut()
    }

    @FXML
    private fun copy(event: ActionEvent) {
        behaviour.copy()
    }

    @FXML
    private fun paste(event: ActionEvent) {
        behaviour.paste()
    }

    @FXML
    private fun selectAll(event: ActionEvent) {

    }


}