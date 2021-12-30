package com.harleyoconnor.gitdesk.ui.node

import com.harleyoconnor.gitdesk.util.Directory
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.stage.DirectoryChooser
import javafx.stage.Stage

/**
 *
 * @author Harley O'Connor
 */
class DirectoryField : ValidatedField<TextField>() {

    private lateinit var stage: Stage

    init {
        load("/ui/nodes/DirectoryField.fxml", this)
    }

    fun setStage(stage: Stage) {
        this.stage = stage
    }

    @FXML
    private fun openDirectoryChooser() {
        val directoryChooser = DirectoryChooser()
        directoryChooser.showDialog(stage)?.let {
            setText(Directory(it).path)
        }
    }

}