package com.harleyoconnor.gitdesk.ui.repository

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.ui.node.CodeEditor
import com.harleyoconnor.gitdesk.ui.util.load
import javafx.fxml.FXML
import javafx.scene.Node
import java.io.File

/**
 * @author Harley O'Connor
 */
class FileEditorController {

    companion object {
        fun load(file: File): Node {
            val fxml = load<Node, FileEditorController>("repository/FileEditor")
            fxml.controller.setFile(file)
            return fxml.root
        }
    }

    private lateinit var file: File

    @FXML
    private lateinit var editor: CodeEditor

    private fun setFile(file: File) {
        this.file = file
        loadFromFile(file)
    }

    private fun loadFromFile(file: File) {
        Data.syntaxHighlighterAccess.getForFile(file.name)?.let {
            this.editor.setupSyntaxHighlighting(it)
        }
        this.editor.replaceText(file.readLines().joinToString(separator = "\n"))
    }

    private fun saveToFile() {
        this.file.writeText(this.editor.text)
    }

}