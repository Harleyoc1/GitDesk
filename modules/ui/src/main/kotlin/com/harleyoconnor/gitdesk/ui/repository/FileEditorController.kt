package com.harleyoconnor.gitdesk.ui.repository

import com.harleyoconnor.gitdesk.ui.node.CodeEditor
import javafx.fxml.FXML
import java.io.File

/**
 * @author Harley O'Connor
 */
class FileEditorController {

    private lateinit var file: File

    @FXML
    private lateinit var editor: CodeEditor

    fun setFile(file: File) {
        this.file = file
        loadFromFile(file)
    }

    private fun loadFromFile(file: File) {
        this.editor.replaceText(file.readLines().joinToString(separator = "\n"))
    }

    private fun saveToFile() {
        this.file.writeText(this.editor.text)
    }

}