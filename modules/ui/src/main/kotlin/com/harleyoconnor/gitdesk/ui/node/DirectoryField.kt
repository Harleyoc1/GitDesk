package com.harleyoconnor.gitdesk.ui.node

import com.harleyoconnor.gitdesk.util.Directory
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File

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
    private fun openChooser() {
        val directoryChooser = DirectoryChooser()
        val file = File(this.getTextUnvalidated())
        // Start in currently selected folder if it exists (and is not a file).
        if (file.exists() && file.isDirectory) {
            directoryChooser.initialDirectory = file
        }
        directoryChooser.showDialog(stage)?.let {
            setText(Directory(it).path)
        }
    }

}