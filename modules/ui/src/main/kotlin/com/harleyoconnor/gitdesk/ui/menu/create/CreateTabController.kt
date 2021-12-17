package com.harleyoconnor.gitdesk.ui.menu.create

import com.harleyoconnor.gitdesk.ui.menu.MenuController
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.getUserHome
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.stage.DirectoryChooser
import java.io.File

/**
 * @author Harley O'Connor
 */
class CreateTabController {

    companion object {
        fun load(parent: MenuController): VBox {
            val fxml = load<VBox, CreateTabController>("menu/tabs/create/Root")
            fxml.controller.parent = parent
            return fxml.root
        }
    }

    private lateinit var parent: MenuController

    @FXML
    private lateinit var nameField: TextField

    @FXML
    lateinit var locationField: TextField

    @FXML
    fun initialize() {
        clear(Event(null))
    }

    fun openDirectoryChooser(event: ActionEvent) {
        val directoryChooser = DirectoryChooser()
        directoryChooser.showDialog(parent.stage)?.let {
            this.locationField.text = Directory(it).path
        }
    }

    fun clear(event: Event) {
        nameField.text = "Project"
        locationField.text = getUserHome() + File.separator + "Project"
    }

    fun create(event: ActionEvent) {

    }

}