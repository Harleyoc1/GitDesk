package com.harleyoconnor.gitdesk.ui.repository

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.ui.node.CodeEditor
import javafx.fxml.FXML
import java.io.File

/**
 * @author Harley O'Connor
 */
class FileEditorController {

    private var file: File? = null

    @FXML
    private lateinit var editor: CodeEditor

    fun setFile(file: File) {
        if (this.file != file) {
            this.file = file
            loadFromFile(file)
        }
    }

    private fun loadFromFile(file: File) {
        Data.syntaxHighlighterAccess.getForFile(file.name)?.let {
            this.editor.setupSyntaxHighlighting(it)
        }
        this.editor.replaceText(file.readLines().joinToString(separator = "\n"))
    }

    private fun saveToFile() {
        this.file?.writeText(this.editor.text)
    }

}