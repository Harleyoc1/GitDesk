package com.harleyoconnor.gitdesk.ui.node

import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.stage.FileChooser
import javafx.stage.Stage

/**
 *
 * @author Harley O'Connor
 */
class FileField : ValidatedField<TextField>() {

    private lateinit var stage: Stage

    init {
        load("/ui/nodes/FileField.fxml", this)
    }

    fun setStage(stage: Stage) {
        this.stage = stage
    }

    @FXML
    private fun openChooser() {
        val fileChooser = FileChooser()
        fileChooser.showOpenDialog(stage)?.let {
            setText(it.canonicalPath)
        }
    }

}